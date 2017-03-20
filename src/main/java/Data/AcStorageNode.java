package Data;

/**
 * Created by kaguya on 3/21/17.
 */
public class AcStorageNode{
    public int Index;
    public String Type;
    public int Size;
    public int Used;
    public String Name;

    public AcStorageNode(int Index,String Type,int Size,int Used,String Name){
        this.Index = Index;
        this.Size = Size;
        this.Type = Type;
        this.Used = Used;
        this.Name = Name;
    }
    public AcStorageNode(int Index,String Type){
        this.Index = Index;
        this.Size = 0;
        this.Type = Type;
        this.Used = 0;
        this.Name = "";
    }
}