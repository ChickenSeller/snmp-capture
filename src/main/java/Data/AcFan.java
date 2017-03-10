package Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kaguya on 3/10/17.
 */
public class AcFan extends Data {
    List<AcFanNode> Fan;

    public AcFan(){
        NodeId = new ArrayList<Integer>();
        Fan = new ArrayList<AcFanNode>();
    }

    public boolean AddNode(int Index,String Status){
        if(CheckId(Index)){
            this.Fan.add(new AcFanNode(Index,Status));
            this.NodeId.add(Index);
            return true;
        }else {
            return false;
        }
    }
}

class AcFanNode{
    public int Index;
    public String Status;

    public AcFanNode(int Index,String Status){
        this.Index = Index;
        this.Status =Status;
    }
}
