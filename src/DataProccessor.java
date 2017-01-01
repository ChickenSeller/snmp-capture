import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.FileWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

import org.w3c.dom.*;
import redis.clients.jedis.Jedis;

/**
 * Created by kaguya on 2016/12/4.
 */
public class DataProccessor {
    public static Config config;
    public static void main(String args[]){
        SolveArgs(args);
    }

    private static int ExportToRedis(NodeList nodes,String prefix){
        if(prefix.length()!=0){
            prefix+="_";
        }
        Jedis jedis = new Jedis("localhost",6379);
        int count = nodes.getLength();
        Node node;
        NamedNodeMap attrs;
        String key,value;
        for(int i=0;i<count;i++){
            node = nodes.item(i);
            attrs = node.getAttributes();
            key = prefix+attrs.getNamedItem("name").getTextContent();
            value = node.getChildNodes().item(1).getTextContent();
            jedis.set(key,value);
        }
        Set<String> x = jedis.keys(prefix+"*");
        System.out.println(x.size());
        return 0;
    }
    private static void SolveArgs(String args[]){
        String file="";
        String mode="";
        String filter="";
        for(int i=0;i<args.length;i++){
            if(args[i].equals("--mode")){
                mode = args[i+1];
            }else if (args[i].equals("--config")){
                file = args[i+1];
            }


            if(mode.equals("capture")){
                //ParseData(file);
            }else if(mode.equals("analyse-data")){
                AnalyseData(filter);
            }
        }
        DataProccessor.config = new Config(file);
        Date date = new Date();
        SimpleDateFormat ft =
                new SimpleDateFormat ("yyyyMMddhhmm");
        String date_str = ft.format(date);
        for (DeviceConfig cfg:DataProccessor.config.devices
                ) {
            CaptureExec x = new CaptureExec(cfg.ip,cfg,date_str);
            x.start();
        }

    }
    private static void ParseData(String file){
        try{
            String xml_content;
            Path path = Paths.get(file);
            byte[] content = Files.readAllBytes(path);
            xml_content = new String(content);
            Charset charset = Charset.forName("UTF-8");
            xml_content = charset.decode(charset.encode(xml_content)).toString();
            xml_content = xml_content.replace("<?xml version=\"1.0\"?>","<?xml version=\"1.0\" encoding=\"gb2312\"?>");
            FileWriter writer = new FileWriter("temp.xml");
            writer.write(xml_content);
            xml_content = null;
            writer.flush();
            writer.close();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document xml_document = db.parse("temp.xml");
            NodeList nodes = xml_document.getElementsByTagName("Instance");
            ExportToRedis(nodes,"");
            System.out.println("Instances:\t"+nodes.getLength());
        }catch (Exception e){
            System.out.println(e);
            System.exit(1);
        }
    }

    private static void AnalyseData(String filter){
        /*
        DataFilter dataFilter = new DataFilter("localhost",6379);
        String res[][] = dataFilter.GetValue(filter);
        int len = res.length;
        for(int i=0;i<len;i++){
            System.out.println(res[i][0]);
            System.out.println(res[i][1]);
        }
        */
        APTreeData aptree = new APTreeData(new DataFilter("localhost",6379));
        aptree.Analyse();
        for (Iterator it = aptree.ap.keySet().iterator(); it.hasNext(); ) {
            String key = (String) it.next();
            APTreeData.APNode value = (APTreeData.APNode) aptree.ap.get(key);
            ArrayList<APTreeData.UserNode> user = value.users;
            for (APTreeData.UserNode temp :user
                 ) {
                System.out.println(temp.UserName+"\t"+temp.UserPhyAddress+"\t"+temp.UserIPAddress+"\t"+temp.UserLocation);
            }
            System.out.println(value.BSSID+"\t"+value.ESSID+"\t"+value.IPAddress);
        }
    }
}
