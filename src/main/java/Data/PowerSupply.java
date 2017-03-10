package Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kaguya on 3/10/17.
 */
public class PowerSupply extends Data {
    public List<PowerSupplyNode> Power;

    public PowerSupply(){
        Power = new ArrayList<PowerSupplyNode>();
        NodeId = new ArrayList<Integer>();
    }

    public boolean AddNode(int Index,String Status){
        if(CheckId(Index)){
            this.Power.add(new PowerSupplyNode(Index,Status));
            this.NodeId.add(Index);
            return true;
        }else {
            return false;
        }
    }
}

class PowerSupplyNode{
    public int Index;
    public String Status;
    public PowerSupplyNode(int Index,String Status){
        this.Index = Index;
        this.Status =Status;
    }
}
