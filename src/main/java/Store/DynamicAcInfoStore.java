package Store;

import Capture.OidNodeConfig;
import Capture.SnmpCapture;
import Data.*;
import org.omg.PortableInterceptor.INACTIVE;
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

        try {
            for (Map.Entry<String,DynamicAc> entry:DataSet.entrySet()
                 ) {
                DynamicAc data = entry.getValue();
                String sql = String.format("INSERT INTO `%s` (`id`, `ip`, `proccessor_info`, `fan_info`, `storage_info`, `memory_size`, `memory_used`, `memory_free`, `power_supply_info`, `cpu_used_percent`, `memory_used_percent`, `packet_loss_percent`, `create_time`, `update_time`) VALUES (NULL, '%s', '%s', '%s', '%s', '%d', '%d', '%d', '%s', '%d', '%d', '%d', FROM_UNIXTIME('%s'), FROM_UNIXTIME('%s'))",
                        DynamicAcInfoStore.TableName,data.ip,data.proccessor_info_json,data.fan_info_json,data.storage_info_json,data.memory_size,data.memory_used,data.memory_free,data.power_supply_info_json,data.cpu_used_percent,data.memory_used_percent,data.packet_loss_percent,DynamicAcInfoStore.Time,DynamicAcInfoStore.Time);
                MysqlWorker.INSTANCE.Update(sql);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println();
    }

    public void UpdateData(){
        GetAcIp();
        GetMemoryInfo();
        GetCpuPercent();
        GetCpuInfo();
        GetStorageInfo();
        GetFanInfo();
        GetPowerInfo();

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
                jedis.del(temp);
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
                    jedis.del(temp);
                }
            }

            data.put(entry.getKey(),temp_info);
        }
        for (Map.Entry<String,AcCpu> entry: data.entrySet()
             ) {
            DynamicAc dynamic_ac = this.DataSet.get(entry.getKey());
            dynamic_ac.proccessor_info = entry.getValue();
            dynamic_ac.proccessor_info_json = JsonHelper.GetJson(dynamic_ac.proccessor_info);
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
            }
        }
        for (Map.Entry<String,AcStorage> entry:data.entrySet()
             ) {
            AcStorage temp_info = entry.getValue();
            RedisRegex = "*"+entry.getKey()+"::sysXStorageType*";
            res = jedis.keys(RedisRegex);
            for (String temp:res
                 ) {
                int Index = Integer.parseInt(this.GetVariables(temp));
                if(!temp_info.Storage.containsKey(Index)){
                    temp_info.AddNode(Index,FormatData(jedis.get(temp)));
                }else {
                    AcStorageNode temp_node = temp_info.Storage.get(Index);
                    temp_node.Type = FormatData(jedis.get(temp));
                    temp_info.Storage.put(Index,temp_node);
                }
                jedis.del(temp);
            }

            RedisRegex = "*"+entry.getKey()+"::sysXStorageName*";
            res = jedis.keys(RedisRegex);
            for (String temp:res
                    ) {
                int Index = Integer.parseInt(this.GetVariables(temp));
                if(temp_info.Storage.containsKey(Index)){
                    AcStorageNode temp_node = temp_info.Storage.get(Index);
                    temp_node.Name = FormatData(jedis.get(temp));
                    temp_info.Storage.put(Index,temp_node);
                    jedis.del(temp);
                }
            }

            RedisRegex = "*"+entry.getKey()+"::sysXStorageSize*";
            res = jedis.keys(RedisRegex);
            for (String temp:res
                    ) {
                int Index = Integer.parseInt(this.GetVariables(temp));
                if(temp_info.Storage.containsKey(Index)){
                    AcStorageNode temp_node = temp_info.Storage.get(Index);
                    temp_node.Size = Integer.parseInt(FormatData(jedis.get(temp)));
                    temp_info.Storage.put(Index,temp_node);
                    jedis.del(temp);
                }
            }

            RedisRegex = "*"+entry.getKey()+"::sysXStorageUsed*";
            res = jedis.keys(RedisRegex);
            for (String temp:res
                    ) {
                int Index = Integer.parseInt(this.GetVariables(temp));
                if(temp_info.Storage.containsKey(Index)){
                    AcStorageNode temp_node = temp_info.Storage.get(Index);
                    temp_node.Used = Integer.parseInt(FormatData(jedis.get(temp)));
                    temp_info.Storage.put(Index,temp_node);
                    jedis.del(temp);
                }
            }
        }

        for (Map.Entry<String,AcStorage> entry:data.entrySet()
             ) {
            DynamicAc dynamic_ac = this.DataSet.get(entry.getKey());
            dynamic_ac.storage_info = entry.getValue();
            dynamic_ac.storage_info_json = JsonHelper.GetJson(dynamic_ac.storage_info);
            this.DataSet.put(entry.getKey(),dynamic_ac);
        }
    }

    private void GetFanInfo(){
        Map<String,AcFan> data = new HashMap<String,AcFan>();
        String RedisRegex = "*sysExtFanStatus*";
        Set<String> res = jedis.keys(RedisRegex);
        for (String temp:res
                ) {
            String[] temp_arr = temp.split("::");
            if(!data.containsKey(temp_arr[1])){
                data.put(temp_arr[1],new AcFan());
            }
        }
        for (Map.Entry<String,AcFan> entry:data.entrySet()
             ) {
            AcFan temp_info = entry.getValue();
            RedisRegex = "*"+entry.getKey()+"::sysExtFanStatus*";
            res = jedis.keys(RedisRegex);
            for (String temp:res
                    ) {
                int Index = Integer.parseInt(this.GetVariables(temp));
                if(!temp_info.Fan.containsKey(Index)){
                    temp_info.AddNode(Index,FormatData(jedis.get(temp)));
                }else {
                    AcFanNode temp_node = temp_info.Fan.get(Index);
                    temp_node.Status = FormatData(jedis.get(temp));
                    temp_info.Fan.put(Index,temp_node);
                }
                jedis.del(temp);
            }
        }
        for (Map.Entry<String,AcFan> entry:data.entrySet()
             ) {
            DynamicAc dynamic_ac = this.DataSet.get(entry.getKey());
            dynamic_ac.fan_info = entry.getValue();
            dynamic_ac.fan_info_json = JsonHelper.GetJson(dynamic_ac.fan_info);
            this.DataSet.put(entry.getKey(),dynamic_ac);
        }

    }

    private void GetPowerInfo(){
        Map<String,PowerSupply> data = new HashMap<String,PowerSupply>();
        String RedisRegex = "*sysExtPowerSupplyStatus*";
        Set<String> res = jedis.keys(RedisRegex);
        for (String temp:res
                ) {
            String[] temp_arr = temp.split("::");
            if(!data.containsKey(temp_arr[1])){
                data.put(temp_arr[1],new PowerSupply());
            }
        }
        for (Map.Entry<String,PowerSupply> entry:data.entrySet()
                ) {
            PowerSupply temp_info = entry.getValue();
            RedisRegex = "*"+entry.getKey()+"::sysExtPowerSupplyStatus*";
            res = jedis.keys(RedisRegex);
            for (String temp:res
                    ) {
                int Index = Integer.parseInt(this.GetVariables(temp));
                if(!temp_info.Power.containsKey(Index)){
                    temp_info.AddNode(Index,FormatData(jedis.get(temp)));
                }else {
                    PowerSupplyNode temp_node = temp_info.Power.get(Index);
                    temp_node.Status = FormatData(jedis.get(temp));
                    temp_info.Power.put(Index,temp_node);
                }
                jedis.del(temp);
            }
        }
        for (Map.Entry<String,PowerSupply> entry:data.entrySet()
                ) {
            DynamicAc dynamic_ac = this.DataSet.get(entry.getKey());
            dynamic_ac.power_supply_info = entry.getValue();
            dynamic_ac.power_supply_info_json = JsonHelper.GetJson(dynamic_ac.power_supply_info);
            this.DataSet.put(entry.getKey(),dynamic_ac);
        }
    }

}

class DynamicAc{
    public int memory_size;
    public int memory_used;
    public int memory_free;
    public String ip;
    public AcCpu proccessor_info;
    public String proccessor_info_json;
    public AcFan fan_info;
    public String fan_info_json;
    public AcStorage storage_info;
    public String storage_info_json;
    public PowerSupply power_supply_info;
    public String power_supply_info_json;
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
