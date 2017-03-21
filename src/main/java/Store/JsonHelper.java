package Store;

import Data.PowerSupply;
import Data.PowerSupplyNode;

/**
 * Created by kaguya on 3/21/17.
 */
public class JsonHelper {
    public static <T1,T2> String GetJson(T1 t1,T2 t2){
        if(t1 instanceof PowerSupply){
            if(t2 instanceof PowerSupplyNode){

            }else{
                return null;
            }
        }
        return "";
    }

    public static String GetPowerSupplyJson(PowerSupply date){

        return "";
    }
}


