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

    public boolean AddNode(int Index,String Type,String Size,String Used,String Name){
        if(CheckId(Index)){
            this.Storage.add(new AcStorageNode(Index,Type,Size,Used,Name));
            this.NodeId.add(Index);
            return true;
        }else {
            return false;
        }
    }
}

class AcStorageNode{
    public int Index;
    public String Type;
    public String Size;
    public String Used;
    public String Name;

    public AcStorageNode(int Index,String Type,String Size,String Used,String Name){
        this.Index = Index;
        this.Size = Size;
        this.Type = Type;
        this.Used = Used;
        this.Name = Name;
    }
}
