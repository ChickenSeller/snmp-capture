package Data;

import java.util.*;

/**
 * Created by kaguya on 3/10/17.
 */
public class AcCpu extends Data {
    public Map<Integer,AcCpuNode> CPU;

    public AcCpu(){
        CPU = new HashMap<Integer, AcCpuNode>();
        NodeId = new ArrayList<Integer>();
    }

    public boolean AddNode(int Index,String Descr){
        if(CheckId(Index)){
            this.CPU.put(Index,new AcCpuNode(Index,Descr));
            this.NodeId.add(Index);
            return true;
        }else {
            return false;
        }
    }
}


