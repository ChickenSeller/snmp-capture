package Capture;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kaguya on 3/11/17.
 */
public enum OidConfig {
    INSTANCE;
    public Map<String,String> Config;
    private OidConfig(){
        this.Config = new HashMap<String,String>();
        for (OidNodeConfig temp_node:SnmpCapture.config.oids
             ) {
            this.Config.put(temp_node.oid,temp_node.name);
        }
    }
}
