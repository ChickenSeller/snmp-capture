package Store;

/**
 * Created by kaguya on 3/11/17.
 */
public class StoreBase {
    static protected String TableName = "";
    static protected String Time;

    protected String FormatData(String Value){
        String[] ValueArray = Value.split(":");
        ValueArray[0] = ValueArray[0].trim();
        ValueArray[1] = ValueArray[1].trim();
        switch (ValueArray[0]){
            case "STRING":
                return ParseSTRING(ValueArray[1]);
            case "IpAddress":
                return ParseIpAddress(ValueArray[1]);
            case "Hex-STRING":
                return ParseHex_STRING(ValueArray[1]);
        }
        return Value;
    }



    private String ParseSTRING(String string){
        String tempStr = string.replaceAll("\"","");
        return tempStr.trim();
    }

    private String ParseIpAddress(String string){
        return string.trim();
    }

    private String ParseHex_STRING(String string){
        String tempStr = string.replaceAll(" ","-");
        return tempStr.trim();
    }


}
