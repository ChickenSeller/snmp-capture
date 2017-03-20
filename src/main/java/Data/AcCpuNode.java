package Data;

/**
 * Created by kaguya on 3/21/17.
 */

public class AcCpuNode{
    public int Index;
    public String Descr;
    public int Load;
    public AcCpuNode(int Index,String Descr){
        this.Index = Index;
        this.Descr = Descr;
        this.Load = 0;
    }
    public AcCpuNode(int Index){
        this.Index = Index;
        this.Descr = "";
        this.Load = 0;
    }
}
