package Store;

import Capture.OidNodeConfig;
import Capture.SnmpCapture;
import redis.clients.jedis.Jedis;
import java.sql.ResultSet;
import java.util.*;

/**
 * Created by kaguya on 3/10/17.
 */
public class StaticAcInfoStore extends StoreBase {
    public List<OidNodeConfig> oid;
    static protected String TableName = "s_ac";
    public Map<String,StaticAc> DataSet;
    public Jedis jedis;
    public StaticAcInfoStore(){
        DataSet = new HashMap<String,StaticAc>();
        jedis = new Jedis(SnmpCapture.config.redis_host, SnmpCapture.config.redis_port);
        StaticAcInfoStore.Time = SnmpCapture.DateStr;
        oid = new ArrayList<OidNodeConfig>();
        for (OidNodeConfig temp_oid: SnmpCapture.config.oids
             ) {
            if(temp_oid.group=="StaticAcInfo"){
                oid.add(temp_oid);
            }
        }
    }

    public void Store(){
        UpdateData();
        try {
            for (Map.Entry<String,StaticAc> entry:DataSet.entrySet()
                 ) {
                StaticAc data = entry.getValue();
                String sql = String.format("SELECT count(id) FROM `%s` WHERE `ip` = '%s'",StaticAcInfoStore.TableName,entry.getKey());
                ResultSet res = MysqlWorker.INSTANCE.Query(sql);
                while (res.next()){
                    if(res.getInt(1)!=0){
                        sql = String.format("UPDATE `%s` SET `bssid` = '%s', `location` = '%s', `master_ip` = '%s', `model_name` = '%s', `update_time` = FROM_UNIXTIME('%s') WHERE `ip` = '%s'",
                                StaticAcInfoStore.TableName,data.bssid,data.location,data.master_ip,data.model_name,StaticAcInfoStore.Time,data.ip);
                    }else {
                        sql = String.format("INSERT INTO `%s` (`id`, `bssid`, `ip`, `location`, `master_ip`, `model_name`, `create_time`, `update_time`) VALUES (NULL, '%s', '%s', '%s', '%s', '%s', FROM_UNIXTIME('%s'), FROM_UNIXTIME('%s'))",
                                StaticAcInfoStore.TableName,data.bssid,data.ip,data.location,data.master_ip,data.model_name,StaticAcInfoStore.Time,StaticAcInfoStore.Time);
                    }
                }
                MysqlWorker.INSTANCE.Update(sql);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private boolean UpdateData(){
        GetAcMac();
        GetMasterIp();
        GetModelName();
        GetLocation();
        return true;
    }

    private void GetMasterIp(){
        String RedisRegex = "*wlsxSwitchMasterIp*";
        Set<String> res = jedis.keys(RedisRegex);
        for (String temp:res
                ) {
            String[] temp_arr = temp.split("::");
            if(DataSet.containsKey(temp_arr[1])){
                StaticAc temp_node = DataSet.get(temp_arr[1]);
                temp_node.master_ip = FormatData(jedis.get(temp));
                DataSet.put(temp_arr[1],temp_node);
                jedis.del(temp);
            }
        }
    }

    private void GetLocation(){

        String RedisRegex = "*sysExtSwitchLocation*";
        Set<String> res = jedis.keys(RedisRegex);
        for (String temp:res
                ) {
            String[] temp_arr = temp.split("::");
            if(DataSet.containsKey(temp_arr[1])){
                StaticAc temp_node = DataSet.get(temp_arr[1]);
                if(temp_node.location==""){
                    String str = temp_arr[0]+"::"+temp_arr[1]+"::sysExtSwitchLocation."+temp_arr[1];
                    try{
                        temp_node.location = FormatData(jedis.get(str));
                    }catch (Exception e){

                    }
                    DataSet.put(temp_arr[1],temp_node);
                }
                jedis.del(temp);
            }
        }
    }

    private void GetModelName(){
        String RedisRegex = "*wlsxModelName*";
        Set<String> res = jedis.keys(RedisRegex);
        for (String temp:res
                ) {
            String[] temp_arr = temp.split("::");
            if(DataSet.containsKey(temp_arr[1])){
                StaticAc temp_node = DataSet.get(temp_arr[1]);
                temp_node.model_name = FormatData(jedis.get(temp));
                DataSet.put(temp_arr[1],temp_node);
                jedis.del(temp);
            }
        }
    }

    private void GetAcMac(){
        String RedisRegex = "*wlsxSysExtSwitchBaseMacaddress*";
        Set<String> res = jedis.keys(RedisRegex);
        for (String temp:res
             ) {
            String[] temp_arr = temp.split("::");
            String mac = FormatData(jedis.get(temp));
            DataSet.put(temp_arr[1],new StaticAc(mac,temp_arr[1]));
            jedis.del(temp);
        }
    }
}

class StaticAc{
    String bssid;
    String ip;
    String master_ip;
    String model_name;
    String location;

    public StaticAc(String bssid,String ip){
        this.bssid = bssid;
        this.ip = ip;
        this.master_ip = "";
        this.model_name = "";
        this.location = "";
    }
}
