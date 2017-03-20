package Store;

/**
 * Created by kaguya on 3/10/17.
 */
public class Store {
    private StaticAcInfoStore static_ac;
    public Store(){
        static_ac = new StaticAcInfoStore();
    }

    public void ExecStore(){
        static_ac.Store();
    }
}
