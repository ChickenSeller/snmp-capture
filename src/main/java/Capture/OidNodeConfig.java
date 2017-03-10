package Capture;

/**
 * Created by kaguya on 3/10/17.
 */
public class OidNodeConfig{
    public String oid;
    public String name;
    public String group;
    public OidNodeConfig(String oid,String name,String group){
        this.oid = oid.trim();
        this.name = name.trim();
        this.group = group.trim();
    }
}
