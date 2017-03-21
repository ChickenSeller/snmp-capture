package Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kaguya on 3/10/17.
 */
public class PowerSupply extends Data {
    public Map<Integer,PowerSupplyNode> Power;

    public PowerSupply(){
        Power = new HashMap<Integer,PowerSupplyNode>();
        NodeId = new ArrayList<Integer>();
    }

    public boolean AddNode(int Index,String Status){
        if(CheckId(Index)){
            this.Power.put(Index,new PowerSupplyNode(Index,Status));
            this.NodeId.add(Index);
            return true;
        }else {
            return false;
        }
    }
}
