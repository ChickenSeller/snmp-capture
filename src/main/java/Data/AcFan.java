package Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kaguya on 3/10/17.
 */
public class AcFan extends Data {
    public Map<Integer,AcFanNode> Fan;

    public AcFan(){
        NodeId = new ArrayList<Integer>();
        Fan = new HashMap<Integer,AcFanNode>();
    }

    public boolean AddNode(int Index,String Status){
        if(CheckId(Index)){
            this.Fan.put(Index,new AcFanNode(Index,Status));
            this.NodeId.add(Index);
            return true;
        }else {
            return false;
        }
    }
}

