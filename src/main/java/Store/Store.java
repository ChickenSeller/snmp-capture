package Store;

/**
 * Created by kaguya on 3/10/17.
 */
public class Store {
    private StaticAcInfoStore static_ac;
    private DynamicAcInfoStore dynamic_ac;
    private ApInfoStore static_ap;
    public Store(){
        static_ac = new StaticAcInfoStore();
        dynamic_ac = new DynamicAcInfoStore();
        static_ap = new ApInfoStore();
    }

    public void ExecStore(){
        static_ac.Store();
        dynamic_ac.Store();
        static_ap.Store();
    }
}
