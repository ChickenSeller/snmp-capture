import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by kaguya on 2016/12/7.
 */
public class APTreeData {
    class APNode{
        public String BSSID;
        public String ESSID;
        public String IPAddress;
        public ArrayList<UserNode> users;
        public APNode(String bssid){
            BSSID = bssid;
            users = new ArrayList<UserNode>();
        }
        public void Analyse(){

        }
    }
    class UserNode{
        public String UserName;
        public String UserPhyAddress;
        public String UserLocation;
        public String UserIPAddress;
        public String ESSID;
        public String UserRole;
        public String AuthMethod;

    }
    private DataFilter data;
    public Hashtable ap;
    public APTreeData(DataFilter data){
        this.data = data;
        ap = new Hashtable();
    }
    public void Analyse(){
        ArrayList<String> BSSIDs = new ArrayList<String>();
        String apres[][] = data.GetValue("apESSID*");
        int len = apres.length;
        for(int i=0;i<len;i++){
            String apMAC = apres[i][0].substring(apres[i][0].indexOf(".")+1);
            APNode tempAP = new APNode(apMAC);
            tempAP.ESSID = apres[i][1];
            tempAP.IPAddress = data.Get("apIpAddress."+apMAC);
            //System.out.println();
            ap.put(apMAC,tempAP);
        }
        String user_res[][] = data.GetValue("nUserApBSSID*");
        len = user_res.length;
        for(int i=0;i<len;i++){
            UserNode user = new UserNode();
            String userIdentity = user_res[i][0].substring(user_res[i][0].indexOf(".")+1);
            String[] userIdentityArr = userIdentity.split("\\.");
            String userMac =""+userIdentityArr[0];
            String userIP = ""+userIdentityArr[6];
            for(int j=1;j<5;j++){
                userMac = userMac+"."+userIdentityArr[j];
            }
            for(int j=7;j<9;j++){
                userIP = userIP+"."+userIdentityArr[j];
            }
            userIP = userIP+"."+userIdentityArr[9];
            userMac = userMac+"."+userIdentityArr[5];

            user.UserName = data.Get("nUserName."+userIdentity);
            user.UserIPAddress = userIP;
            user.UserPhyAddress = userMac;
            user.UserLocation =data.Get("nUserApLocation."+userIdentity);
            String[] mac_arr = user_res[i][1].split("-");
            String ap_mac = ""+Integer.parseInt(mac_arr[0],16);
            for(int j=1;j<5;j++){
                ap_mac = ap_mac+"."+Integer.parseInt(mac_arr[j],16);
            }
            ap_mac = ap_mac+"."+Integer.parseInt(mac_arr[5],16);
            APNode ap_node =(APNode)ap.get(ap_mac);
            if(ap_node==null){
                continue;
            }
            ap_node.users.add(user);
            ap.replace(ap_node,ap_node);
        }
    }
}
