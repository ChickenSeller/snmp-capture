package Capture;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by kaguya on 3/5/17.
 */

public class SnmpCapture {
    public static Config config;
    static public String DateStr;
    public static int ThreadCount;

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


        SnmpCapture.DateStr = Long.toString(date.getTime()/1000);
        System.out.println(SnmpCapture.DateStr);
        for (DeviceConfig cfg: SnmpCapture.config.devices
                ) {
            CaptureWorker x = new CaptureWorker(cfg.ip,cfg,SnmpCapture.DateStr);
            x.start();
        }

    }

    public static void main(String args[]){
        SnmpCapture.ThreadCount = 0;
        SolveArgs(args);
    }
}
