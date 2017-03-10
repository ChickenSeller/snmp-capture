package Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kaguya on 3/10/17.
 */
public class AcCpu extends Data {
    public List<AcCpuNode> CPU;

    public AcCpu(int n){
        CPU = new ArrayList<AcCpuNode>();
        NodeId = new ArrayList<Integer>();
    }

    public boolean AddNode(int Index,String Descr,String Load){
        if(CheckId(Index)){
            this.CPU.add(new AcCpuNode(Index,Descr,Load));
            this.NodeId.add(Index);
            return true;
        }else {
            return false;
        }
    }
}

class AcCpuNode{
    public int Index;
    public String Descr;
    public String Load;
    public AcCpuNode(int Index,String Descr,String Load){
        this.Index = Index;
        this.Descr = Descr;
        this.Load = Load;
    }
}
