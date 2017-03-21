package Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kaguya on 3/10/17.
 */
public class AcStorage extends Data {
    public Map<Integer,AcStorageNode> Storage;

    public AcStorage(){
        NodeId = new ArrayList<Integer>();
        Storage = new HashMap<Integer,AcStorageNode>();
    }

    public boolean AddNode(int Index,String Type){
        if(CheckId(Index)){
            this.Storage.put(Index,new AcStorageNode(Index,Type));
            this.NodeId.add(Index);
            return true;
        }else {
            return false;
        }
    }
}

