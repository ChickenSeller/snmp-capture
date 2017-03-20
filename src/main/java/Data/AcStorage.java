package Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kaguya on 3/10/17.
 */
public class AcStorage extends Data {
    public List<AcStorageNode> Storage;

    public AcStorage(){
        NodeId = new ArrayList<Integer>();
        Storage = new ArrayList<AcStorageNode>();
    }

    public boolean AddNode(int Index,String Type){
        if(CheckId(Index)){
            this.Storage.add(new AcStorageNode(Index,Type));
            this.NodeId.add(Index);
            return true;
        }else {
            return false;
        }
    }
}

