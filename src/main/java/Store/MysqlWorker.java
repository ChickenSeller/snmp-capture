package Store;

import Capture.SnmpCapture;
import java.sql.*;

/**
 * Created by kaguya on 3/10/17.
 */
public enum MysqlWorker {
    INSTANCE;
    private String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private String DB_URL;
    private String USER;
    private String PASS;
    public Connection conn = null;
    public Statement stmt = null;
    private MysqlWorker(){
        DB_URL = "jdbc:mysql://"+ SnmpCapture.config.mysql.host+"/"+SnmpCapture.config.mysql.db;
        USER = SnmpCapture.config.mysql.user;
        PASS = SnmpCapture.config.mysql.password;
        try{
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL,USER,PASS);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public ResultSet Query(String sql){
        ResultSet res;
        try{
            this.stmt = this.conn.createStatement();
            res = this.stmt.executeQuery(sql);
            return res;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public int Update(String sql){
        int res=0;
        try{
            this.stmt = this.conn.createStatement();
            res = this.stmt.executeUpdate(sql);
            return res;
        }catch (Exception e){
            e.printStackTrace();
        }
        return res;
    }


}
