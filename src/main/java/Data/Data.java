package Data;

import java.util.List;

/**
 * Created by kaguya on 3/10/17.
 */
public abstract class Data {
    protected List<Integer> NodeId;

    protected boolean CheckId(int id){
        for (int tempId:NodeId
                ) {
            if(tempId==id){
                return false;
            }
        }
        return true;
    }
}
