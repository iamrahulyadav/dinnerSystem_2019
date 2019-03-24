package com.c2p.dinner.System;
import org.json.JSONArray;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class globalV {
    static String URLStart = "https://dinnersystemapp.azurewebsites.net";
    static boolean logged_in = false;
    static boolean postLogin = false;
    static String deviceMac = "";
    static String fullName = "";
    static String MSG = "";
    static String eventName = "";
    static String EventPIN = "";
    static String deviceName = "";
    static String printerName = "";
    static String registeredTo = "No Event";
    static ArrayList<String> reasons = new ArrayList<String>();
    static ArrayList<ReasonObject> reasonsObjs = new ArrayList<>();
    static boolean kioskMode = false;
    static boolean demomode = false;
    static JSONArray accountsJsonArray;
    static long logTime;
    static String chairdyEventId = "";
    static boolean BUTTON_ANONYMUS_ON = false;
    static boolean BUTTON_HONERY_ON = false;
    static boolean BUTTON_CHECK_ON = true;
    static long Last_Log_Update = 0;
    
    static boolean checkLastYear = false;

    /*******************************PRINTING****************************/
    static final int PRINT_MODE_LOCAL = 450;
    static final int PRINT_MODE_SERVER = 460;
    static String printerMAC ="";//D8:61:62:31:CC:ED
    static String printerMAC_LAN ="";
    static String printerMAC_WIFI ="";
    static String MacMode = "";
    static String printerIp ="";
    static String PrintServerFTP = "";
    static String PrintServerPort = "";
    static String FTPUser = "";
    static String FTPPass = "";
    static String recieptsHttpUrl = "";
    static int printMode = PRINT_MODE_LOCAL;
    static boolean printConnected = false;
    static boolean enablePrinting = true;
    
    static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm",Locale.ENGLISH);
    static String PRINTER_NUM;
    static String PRINTER_IPSTATE = "";
    static boolean doNotSharePrinter = false;
    
    static boolean testAutomation = false;
    static boolean transactionCompleted = true;
    
    static void setDemomode(boolean Dmode){

    }

    


}
