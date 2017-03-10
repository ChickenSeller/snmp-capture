package Store;

import Capture.SnmpCapture;
import java.sql.*;

/**
 * Created by kaguya on 3/10/17.
 */
public class MysqlWorker {
    private String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private String DB_URL;
    private static MysqlWorker instance = new MysqlWorker();
    private String USER;
    private String PASS;
    public Connection conn = null;
    public Statement stmt = null;
    private MysqlWorker(){
        DB_URL = "jdbc:mysql://"+ SnmpCapture.config.mysql.host+"/"+SnmpCapture.config.mysql.db;
        try{
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL,USER,PASS);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public static MysqlWorker getInstance(){
        return instance;
    }
}
