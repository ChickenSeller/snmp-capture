package Store;

import Data.Data;
import Data.PowerSupply;
import Data.AcFan;
import Data.AcFanNode;
import Data.PowerSupplyNode;
import Data.AcCpu;
import Data.AcCpuNode;
import Data.AcStorage;
import Data.AcStorageNode;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Map;

/**
 * Created by kaguya on 3/21/17.
 */
public class JsonHelper {
    public static String GetJson(Data data){
        String res = "";
        switch (data.getClass().getName()){
            case "Data.PowerSupply":
                res = GetPowerSupplyJson((PowerSupply)data);
                break;
            case "Data.AcFan":
                res = GetAcFanInfoJson((AcFan)data);
                break;
            case "Data.AcCpu":
                res = GetAcCpuInfoJson((AcCpu)data);
                break;
            case "Data.AcStorage":
                res = GetAcStorageInfoJson((AcStorage)data);
                break;


        }
        if(data instanceof PowerSupply){

        }
        return res;
    }

    private static String GetPowerSupplyJson(PowerSupply data){
        JSONArray json = new JSONArray();
        for (Map.Entry<Integer,PowerSupplyNode> entry:data.Power.entrySet()
             ) {
            PowerSupplyNode node = entry.getValue();
            JSONObject obj = new JSONObject();
            obj.put("Index",node.Index);
            obj.put("Status",node.Status);
            json.put(obj);
        }
        return json.toString();
    }

    private static String GetAcFanInfoJson(AcFan data){
        JSONArray json = new JSONArray();
        for (Map.Entry<Integer,AcFanNode> entry:data.Fan.entrySet()
                ) {
            AcFanNode node = entry.getValue();
            JSONObject obj = new JSONObject();
            obj.put("Status",node.Status);
            obj.put("Index",node.Index);
            json.put(obj);
        }
        return json.toString();
    }

    private static String GetAcCpuInfoJson(AcCpu data){
        JSONArray json = new JSONArray();
        for (Map.Entry<Integer,AcCpuNode> entry:data.CPU.entrySet()
                ) {
            AcCpuNode node = entry.getValue();
            JSONObject obj = new JSONObject();
            obj.put("Descr",node.Descr);
            obj.put("Index",node.Index);
            obj.put("Load",node.Load);
            json.put(obj);
        }
        return json.toString();
    }

    private static String GetAcStorageInfoJson(AcStorage data){
        JSONArray json = new JSONArray();
        for (Map.Entry<Integer,AcStorageNode> entry:data.Storage.entrySet()
                ) {
            AcStorageNode node = entry.getValue();
            JSONObject obj = new JSONObject();
            obj.put("Name",node.Name);
            obj.put("Index",node.Index);
            obj.put("Size",node.Size);
            obj.put("Type",node.Type);
            obj.put("Used",node.Used);
            json.put(obj);
        }
        return json.toString();
    }
}


