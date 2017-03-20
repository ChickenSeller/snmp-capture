package Store;

import Capture.OidNodeConfig;
import Capture.SnmpCapture;
import Data.*;
import redis.clients.jedis.Jedis;

import java.util.*;

/**
 * Created by kaguya on 3/21/17.
 */
public class DynamicAcInfoStore extends StoreBase {
    public List<OidNodeConfig> oid;
    static protected String TableName = "d_ac";
    public Map<String, DynamicAc> DataSet;
    public Jedis jedis;

    public DynamicAcInfoStore(){
        DataSet = new HashMap<String,DynamicAc>();
        jedis = new Jedis(SnmpCapture.config.redis_host, SnmpCapture.config.redis_port);
        StaticAcInfoStore.Time = SnmpCapture.DateStr;
        oid = new ArrayList<OidNodeConfig>();
        for (OidNodeConfig temp_oid: SnmpCapture.config.oids
                ) {
            if(temp_oid.group=="DynamicAcInfo"){
                oid.add(temp_oid);
            }
        }
    }

    public void Store(){
        UpdateData();
        System.out.println();
    }

    public void UpdateData(){
        GetAcIp();
        GetMemoryInfo();
        GetCpuPercent();
        GetCpuInfo();

    }

    private void GetAcIp(){
        String RedisRegex = "*wlsxSysExtPacketLossPercent*";
        Set<String> res = jedis.keys(RedisRegex);
        for (String temp:res
                ) {
            String[] temp_arr = temp.split("::");
            int packet_loss_percent = Integer.parseInt(FormatData(jedis.get(temp)));
            DataSet.put(temp_arr[1],new DynamicAc(temp_arr[1],packet_loss_percent));
            jedis.del(temp);
        }
    }

    private void GetMemoryInfo(){
        String RedisRegex = "*sysXMemorySize*";
        Set<String> res = jedis.keys(RedisRegex);
        for (String temp:res
                ) {
            String[] temp_arr = temp.split("::");
            if(DataSet.containsKey(temp_arr[1])){
                DynamicAc temp_node = DataSet.get(temp_arr[1]);
                temp_node.memory_size = Integer.parseInt(FormatData(jedis.get(temp)));
                DataSet.put(temp_arr[1],temp_node);
                jedis.del(temp);
            }
        }

        RedisRegex = "*sysXMemoryUsed*";
        res = jedis.keys(RedisRegex);
        for (String temp:res
                ) {
            String[] temp_arr = temp.split("::");
            if(DataSet.containsKey(temp_arr[1])){
                DynamicAc temp_node = DataSet.get(temp_arr[1]);
                temp_node.memory_used = Integer.parseInt(FormatData(jedis.get(temp)));
                DataSet.put(temp_arr[1],temp_node);
                jedis.del(temp);
            }
        }

        RedisRegex = "*sysXMemoryFree*";
        res = jedis.keys(RedisRegex);
        for (String temp:res
                ) {
            String[] temp_arr = temp.split("::");
            if(DataSet.containsKey(temp_arr[1])){
                DynamicAc temp_node = DataSet.get(temp_arr[1]);
                temp_node.memory_free = Integer.parseInt(FormatData(jedis.get(temp)));
                DataSet.put(temp_arr[1],temp_node);
                jedis.del(temp);
            }
        }

        RedisRegex = "*wlsxSysExtMemoryUsedPercent*";
        res = jedis.keys(RedisRegex);
        for (String temp:res
                ) {
            String[] temp_arr = temp.split("::");
            if(DataSet.containsKey(temp_arr[1])){
                DynamicAc temp_node = DataSet.get(temp_arr[1]);
                temp_node.memory_used_percent = Integer.parseInt(FormatData(jedis.get(temp)));
                DataSet.put(temp_arr[1],temp_node);
                jedis.del(temp);
            }
        }
    }

    private void GetCpuPercent(){
        String RedisRegex = "*wlsxSysExtCpuUsedPercent*";
        Set<String> res = jedis.keys(RedisRegex);
        for (String temp:res
                ) {
            String[] temp_arr = temp.split("::");
            if(DataSet.containsKey(temp_arr[1])){
                DynamicAc temp_node = DataSet.get(temp_arr[1]);
                temp_node.cpu_used_percent = Integer.parseInt(FormatData(jedis.get(temp)));
                DataSet.put(temp_arr[1],temp_node);
                jedis.del(temp);
            }
        }
    }

    private void GetCpuInfo(){
        Map<String,AcCpu> data = new HashMap<String,AcCpu>();
        String RedisRegex = "*sysXProcessorDescr*";
        Set<String> res = jedis.keys(RedisRegex);
        for (String temp:res
                ) {
            String[] temp_arr = temp.split("::");
            if(!data.containsKey(temp_arr[1])){
                data.put(temp_arr[1],new AcCpu());
            }
        }
        for (Map.Entry<String,AcCpu> entry:data.entrySet()
             ) {
            RedisRegex = "*"+entry.getKey()+"::sysXProcessorDescr*";
            AcCpu temp_info = entry.getValue();
            res = jedis.keys(RedisRegex);
            for (String temp:res
                 ) {
                int Index = Integer.parseInt(this.GetVariables(temp));
                if(!temp_info.CPU.containsKey(Index)){
                    temp_info.AddNode(Index,FormatData(jedis.get(temp)));
                }else {
                    AcCpuNode temp_node = temp_info.CPU.get(Index);
                    temp_node.Descr = FormatData(jedis.get(temp));
                    temp_info.CPU.put(Index,temp_node);
                }
            }

            RedisRegex = "*"+entry.getKey()+"::sysXProcessorLoad*";
            res = jedis.keys(RedisRegex);
            for (String temp:res
                    ) {
                int Index = Integer.parseInt(this.GetVariables(temp));
                if(temp_info.CPU.containsKey(Index)){
                    AcCpuNode temp_node = temp_info.CPU.get(Index);
                    temp_node.Load = Integer.parseInt(FormatData(jedis.get(temp)));
                    temp_info.CPU.put(Index,temp_node);
                }
            }

            data.put(entry.getKey(),temp_info);
        }
        for (Map.Entry<String,AcCpu> entry: data.entrySet()
             ) {
            DynamicAc dynamic_ac = this.DataSet.get(entry.getKey());
            dynamic_ac.proccessor_info = entry.getValue();
            this.DataSet.put(entry.getKey(),dynamic_ac);
        }
    }

    private void GetStorageInfo(){
        Map<String,AcStorage> data = new HashMap<String,AcStorage>();
        String RedisRegex = "*sysXStorageType*";
        Set<String> res = jedis.keys(RedisRegex);
        for (String temp:res
                ) {
            String[] temp_arr = temp.split("::");
            if(!data.containsKey(temp_arr[1])){
                data.put(temp_arr[1],new AcStorage());
                //TODO
            }
        }
    }

}

class DynamicAc{
    public int memory_size;
    public int memory_used;
    public int memory_free;
    public String ip;
    public AcCpu proccessor_info;
    public AcFan fan_info;
    public AcStorage storage_info;
    public PowerSupply power_supply_info;
    public int cpu_used_percent;
    public int memory_used_percent;
    public int packet_loss_percent;

    public DynamicAc(String ip,int packet_loss_percent){
        this.ip = ip;
        this.memory_free = 0;
        this.memory_size = 0;
        this.memory_used = 0;
        this.memory_used_percent = 0;
        this.cpu_used_percent = 0;
        this.fan_info = new AcFan();
        this.proccessor_info = new AcCpu();
        this.power_supply_info = new PowerSupply();
        this.packet_loss_percent = packet_loss_percent;
        this.storage_info = new AcStorage();
    }
}
