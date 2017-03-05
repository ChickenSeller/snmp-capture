import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kaguya on 16-12-31.
 */
public class Config {
    public Path config_file;
    public DeviceConfig[] devices;
    public String[] oids;
    public String redis_host;
    public int redis_port;
    public Config(String  file){
        try{
            config_file= Paths.get(file);
            this.ParseConfig(Files.readAllLines(config_file));
        }catch(IOException io_e){
            System.out.println(io_e.getMessage());
            System.exit(1);
        }
    }
    private void ParseConfig(List<String> content){
        int segment = 0;
        ArrayList<String> device_string = new ArrayList<>();
        ArrayList<String> oid_string = new ArrayList<>();
        DeviceConfig temp_device;
        for (String line:content
                ) {
            line = line.trim();
            if(line.substring(0,1).equals("#")){
                continue;
            }
            switch (line){
                case "[device]":
                    segment = 1;
                    continue;
                case "[oid]":
                    segment = 2;
                    continue;
                case "[redis]":
                    segment = 3;
                    continue;
                default:
                    if(segment==1){
                        device_string.add(line);
                        //String[] device_info = line.split(" ");
                        //temp_device = new DeviceConfig(device_info[0],device_info[1],device_info[2]);

                    }else if(segment==2){
                        oid_string.add(line);
                    }else if(segment==3) {
                        String[] temp = line.split(" ");
                        this.redis_host = temp[0];
                        this.redis_port = Integer.parseInt(temp[1]);
                    }else
                    {
                        continue;
                    }
            }
        }
        this.devices = new DeviceConfig[device_string.size()];
        this.oids = new String[oid_string.size()];
        for (int i=0;i<device_string.size();i++){
            String[] device_info = device_string.get(i).split(" ");
            temp_device = new DeviceConfig(device_info[0],device_info[1],device_info[2]);
            this.devices[i] = temp_device;
        }
        for(int i=0;i<oid_string.size();i++){
            this.oids[i] = oid_string.get(i);
        }
    }
}
class DeviceConfig{
    public String ip;
    public String snmp_version;
    public String password;
    public DeviceConfig(String IP,String SnmpVersion,String Password){
        this.ip = IP.trim();
        this.password = Password.trim();
        this.snmp_version = SnmpVersion.trim();
    }
}

