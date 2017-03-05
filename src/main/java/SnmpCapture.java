import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by kaguya on 3/5/17.
 */

public class SnmpCapture {
    public static Config config;

    public static void main(String args[]){
        SolveArgs(args);
        System.out.println("test");
    }

    private static void SolveArgs(String args[]){
        String file="./data.conf";
        String mode="";
        String filter="";

/*
        for(int i=0;i<args.length;i++){
            if(args[i].equals("--mode")){
                mode = args[i+1];
            }else if (args[i].equals("--config")){
                file = args[i+1];
            }

            if(mode.equals("capture")){
                //ParseData(file);
            }else if(mode.equals("analyse-data")){
                AnalyseData(filter);
            }
        }
        */
        SnmpCapture.config = new Config(file);
        Date date = new Date();

        SimpleDateFormat ft =
                new SimpleDateFormat ("yyyyMMddhhmm");
        ft.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        String date_str = ft.format(date);
        System.out.println(date_str);
        for (DeviceConfig cfg:SnmpCapture.config.devices
                ) {
            CaptureWorker x = new CaptureWorker(cfg.ip,cfg,date_str);
            x.start();
        }

    }
}
