import redis.clients.jedis.Jedis;

import java.util.Set;

/**
 * Created by kaguya on 2016/12/6.
 */
public class DataFilter {
    private Jedis jedis;
    DataFilter(String hostname,int port){
        jedis = new Jedis(hostname,port);
    }
    String[][] GetValue(String filter){
        Set<String> keys = jedis.keys(filter);
        int i = keys.size();
        String result[][] = new String[i][2];
        int j = 0;
        for (String key:keys
             ) {
            String value = jedis.get(key);
            result[j][0] = key;
            result[j][1] = value;
            j++;
        }
        return result;
    }
    String Get(String key){
        return jedis.get(key);
    }

}

