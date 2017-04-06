package Store;

import Capture.OidNodeConfig;
import Capture.SnmpCapture;
import redis.clients.jedis.Jedis;

import java.util.*;

/**
 * Created by kaguya on 4/6/17.
 */
public class ApInfoStore extends StoreBase {
    public List<OidNodeConfig> oid;
    static protected String TableName = "s_ap";
    public Map<String, StaticAp> DataSet;
    public Jedis jedis;
    public ApInfoStore(){
        DataSet = new HashMap<String,StaticAp>();
        jedis = new Jedis(SnmpCapture.config.redis_host, SnmpCapture.config.redis_port);
        StaticAcInfoStore.Time = SnmpCapture.DateStr;
    }

    public void Store(){
        UpdateData();
    }

    public void UpdateData(){
        GetApEssid();
    }

    private void GetApEssid(){
        String RedisRegex = "*apESSID*";
        Set<String> res = jedis.keys(RedisRegex);
        for (String temp:res
                ) {
            String[] temp_arr = temp.split("::");
            String mac = FormatData(jedis.get(temp));
            DataSet.put(temp_arr[1],new StaticAp(mac,temp_arr[1]));
            jedis.del(temp);
        }
    }


}

class StaticAp{
    public String bssid;
    public String essid;
    public String ip;
    public int phy_type;
    public int current_channel;
    public int total_time;
    public int inactive_time;
    public int channel_noise;
    public int channel_snr;
    public int transmit_rate;
    public int receive_rate;
    public int tx_packets;
    public int rx_packets;
    public int tx_bytes;
    public int rx_bytes;

    public StaticAp(String bssid,String essid){
        this.bssid = bssid;
        this.essid = essid;
    }
}
