package com.c2p.dinner.System;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class NetworkUtilities {
    static int signelStrength = 0;

    public static void updateSignelStrength(Context context){
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        int numberOfLevels = 5;
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        signelStrength = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), numberOfLevels);
    }
}
