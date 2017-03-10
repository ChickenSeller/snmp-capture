package Store;

import Capture.OidNodeConfig;
import Capture.SnmpCapture;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kaguya on 3/10/17.
 */
public class StaticAcInfoStore {
    public List<OidNodeConfig> oid;
    public StaticAcInfoStore(){
        oid = new ArrayList<OidNodeConfig>();
        for (OidNodeConfig temp_oid: SnmpCapture.config.oids
             ) {
            if(temp_oid.group=="StaticAcInfo"){
                oid.add(temp_oid);
            }
        }
    }

    public void Store(){
        try {
            MysqlWorker mysql = MysqlWorker.getInstance();
            mysql.stmt = mysql.conn.createStatement();


        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
