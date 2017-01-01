import redis.clients.jedis.Jedis;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by kaguya on 16-12-31.
 */
public class CaptureExec implements Runnable {
    private Thread t;
    private String thread_name;
    private DeviceConfig device_cfg;
    private String time_stamp;
    public CaptureExec(String ac_host,DeviceConfig device_cfg,String time_stamp){
        this.thread_name = ac_host;
        this.device_cfg = device_cfg;
        this.time_stamp = time_stamp;
    }
    public void run(){
        Runtime run = Runtime.getRuntime();
        try{
            Jedis jedis = new Jedis(DataProccessor.config.redis_host,DataProccessor.config.redis_port);
            for (String oid:DataProccessor.config.oids
                    ) {
                String command = "snmpwalk -v "+this.device_cfg.snmp_version+" -c "+this.device_cfg.password+" -C c "+this.device_cfg.ip+" "+oid;
                System.out.println(command);
                Process p = run.exec(command);
                BufferedInputStream in = new BufferedInputStream(p.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line = "";
                while((line=reader.readLine())!=null){
                    String[] line_arr = line.split("=");
                    String key = this.time_stamp+"::"+this.device_cfg.ip+"::"+line_arr[0].trim();
                    String value = line_arr[1].trim();
                    jedis.set(key,value);
                }
                p.waitFor();
                    if(p.exitValue()==0){
                        System.out.println("Thread "+this.thread_name+"\tTask "+oid+"\tExec Successful");
                    }else{
                        System.out.println("Thread "+this.thread_name+"\tTask "+oid+"\tExec FAiled");
                    }
                    reader.close();
                    in.close();

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void start(){
        System.out.println("Starting\t"+this.thread_name);
        if(t==null){
            t = new Thread(this);
            t.start();
        }
    }
}
