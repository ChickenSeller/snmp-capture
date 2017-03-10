package Capture;

/**
 * Created by kaguya on 3/10/17.
 */
public class MysqlConfig{
    public String host;
    public String user;
    public String password;
    public String db;
    public MysqlConfig(String host,String user,String password,String db){
        this.db = db;
        this.host = host;
        this.password = password;
        this.user = user;
    }
}
