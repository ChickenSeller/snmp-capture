package Capture; /**
 * Created by kaguya on 3/5/17.
 */
import Store.Store;
import redis.clients.jedis.Jedis;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Set;

public class CaptureWorker implements Runnable {
    private Thread t;
    private String thread_name;
    private DeviceConfig device_cfg;
    private String time_stamp;

    public CaptureWorker(String ac_host, DeviceConfig device_cfg, String time_stamp){
        this.thread_name = ac_host;
        this.device_cfg = device_cfg;
        this.time_stamp = time_stamp;
    }

    public void run(){
        SnmpCapture.ThreadCount+=1;
        Runtime run = Runtime.getRuntime();
        try{
            Jedis jedis = new Jedis(SnmpCapture.config.redis_host, SnmpCapture.config.redis_port);
            for (OidNodeConfig oid_info: SnmpCapture.config.oids
                    ) {
                String oid = oid_info.oid;
                String command = "snmpwalk -v "+this.device_cfg.snmp_version+" -c "+this.device_cfg.password+" -C c "+this.device_cfg.ip+" "+oid;
                System.out.println(command);
                Process p = run.exec(command);
                BufferedInputStream in = new BufferedInputStream(p.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line = "";
                while((line=reader.readLine())!=null){
                    String[] line_arr = line.split("=");
                    String tempRawString = line_arr[0].trim();
                    tempRawString = tempRawString.replaceAll("SNMPv2-SMI::enterprises",".1.3.6.1.4.1");
                    String key = this.time_stamp+"::"+this.device_cfg.ip+"::"+tempRawString;
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
                ModifyKey(jedis,this.device_cfg.ip);

            }
            SnmpCapture.ThreadCount-=1;
            if(SnmpCapture.ThreadCount<=0){
                Store store = new Store();
                store.ExecStore();
            }
        }catch (Exception e){
            SnmpCapture.ThreadCount -=1;
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
    private void ModifyKey(Jedis jedis,String ip){
        for (Map.Entry<String,String> entry:OidConfig.INSTANCE.Config.entrySet()
             ) {
            Set<String> keys = jedis.keys("*"+ip+"::"+entry.getKey()+"*");
            for (String key:keys
                 ) {
                String value = jedis.get(key);
                jedis.del(key);
                key = key.replaceAll(entry.getKey(),entry.getValue());
                System.out.println(key);
                jedis.set(key,value);
            }
        }
    }

}