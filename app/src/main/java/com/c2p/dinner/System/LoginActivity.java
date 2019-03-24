package com.c2p.dinner.System;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.URLConnection;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class LoginActivity extends Activity {
    
    EditText printerNum;
    EditText cashierNum;
    TextView gettingInfo;
    TextView ipText;
    TextView eventtext;
    TextView versionTv;
    ImageView wifiic;
    final String macAd = getMacAddr();
    
    final int ETHERNET_GOOD = R.drawable.ethernet_working;
    final int ETHERNET_NO_NET = R.drawable.ethernet_not_working;
    
    final int WIFI_GOOD_0 = R.drawable.wifi0;
    final int WIFI_GOOD_1 = R.drawable.wifi1;
    final int WIFI_GOOD_2 = R.drawable.wifi2;
    final int WIFI_GOOD_3 = R.drawable.wifi3;
    final int WIFI_GOOD_4 = R.drawable.wifi4;
    
    final int WIFI_NO_NET_0 = R.drawable.wifi_no_net;
    final int WIFI_NO_NET_1 = R.drawable.wifi1_no_net;
    final int WIFI_NO_NET_2 = R.drawable.wifi2_no_net;
    final int WIFI_NO_NET_3 = R.drawable.wifi3_no_net;
    final int WIFI_NO_NET_4 = R.drawable.wifi4_no_net;
    
    LinearLayout background;
    private boolean runThreads;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.login_layout);
        cashierNum = findViewById(R.id.cashier);
        printerNum = findViewById(R.id.printer_et);
        gettingInfo = findViewById(R.id.login_Getting);
        ipText = findViewById(R.id.ip);
        eventtext = findViewById(R.id.login_ecentname);
        wifiic = findViewById(R.id.login_wifi);
        background = findViewById(R.id.login_root);
        versionTv = findViewById(R.id.version);
        
        versionTv.setText("Version "+ BuildConfig.VERSION_NAME);
        
        cashierNum.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
    
        cashierNum.requestFocus();
        globalV.PRINTER_IPSTATE ="";
        globalV.deviceMac = getMacAddr();

        gettingInfo.setText("");
        ipText.setText("MAC: "+ globalV.deviceMac +"               Device:"+new nonStaticUtilities().getDeviceName(this)+"                  IP: " + getMyLocalIpAddress());
        globalV.postLogin = false;
    
        cashierNum.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((keyCode == 160) && event.getAction() == KeyEvent.ACTION_DOWN && cashierNum.getText().length() > 1) {
                    printerNum.requestFocus();
                    return true;
                }
                if ((keyCode == 156) && event.getAction() == KeyEvent.ACTION_DOWN){
                    Intent mintent = new Intent(LoginActivity.this,LoginMenu.class);
                    startActivity(mintent);
                    return true;
                }

                return false;
            }
        });
    
        printerNum.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((keyCode == 160 || keyCode == 66) && event.getAction() == KeyEvent.ACTION_DOWN && printerNum.getText().length() > 1 && cashierNum.getText().length() > 1) {  // if ((keyCode == 160) && event.getAction() == KeyEvent.ACTION_DOWN && printerNum.getText().length() > 1 && cashierNum.getText().length() > 1) {
                    if(TrdCheckServerReachebility()){
                        getInfo();
                        globalV.PRINTER_NUM = printerNum.getText().toString();
                        return true;
                    }else {
                        Toast.makeText(LoginActivity.this, "No Network", Toast.LENGTH_SHORT).show();
                    }
                    
                }else if(keyCode== 67 && event.getAction() == KeyEvent.ACTION_DOWN && printerNum.getText().length() < 1){
                    cashierNum.requestFocus();
                }
                if ((keyCode == 156) && event.getAction() == KeyEvent.ACTION_DOWN){
                    Intent mintent = new Intent(LoginActivity.this,LoginMenu.class);
                    startActivity(mintent);
                    return true;
                }
                return false;
            }
        });

        if(isEthernetConnected()){
            System.out.println("##################  CONNECTED To Ethernet");
        }else{
            if(!isNetworkAvailable()){
                connectWiFi("DSHT","dShS@5227#","WPA2");
            }
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("activity paused");
    
        runThreads = false;
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        
        if(new nonStaticUtilities().getEventName(this).length() < 1){
            Intent intent = new Intent(LoginActivity.this,register.class);
            startActivity(intent);
        }
        eventtext.setText(new nonStaticUtilities().getEventName(this));
        
        if (ContextCompat.checkSelfPermission(LoginActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            
            if (ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(LoginActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},0);
            }
        }
    
        waitForNet();
        
        startStatusBarUpdater();
        
        DsLogs.UploadLogs();
    }
    
    private void startStatusBarUpdater() {
        
        new Thread(new Runnable() {
            @Override
            public void run() {
                runThreads = true;
                while(runThreads){
                    try{
                        final int id = getConnectionState();
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            public void run() {
                                wifiic.setImageResource(id);
                                ipText.setText("MAC: "+getMacAddr()+"               Device:"+new nonStaticUtilities().getDeviceName(LoginActivity.this)+"                  IP: " + getMyLocalIpAddress());
                            }
                        });
    
                        Thread.sleep(2000);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    
                }
                
            }
        }).start();
        
    
    }
    
    public void getInfo(){
        if(!globalV.postLogin){
            globalV.postLogin = true;
            gettingInfo.setText(getString(R.string.getting_info));
            background.setBackgroundColor(Color.parseColor("#b7b7b7"));
            final String phoneNum = cashierNum.getText().toString();
            final String priNum = printerNum.getText().toString();
    
            new Thread( new Runnable() {
                @Override
                public void run() {
                    URL url = null;
                    HttpsURLConnection urlConnection;
                    BufferedReader reader = null;
                    try {
                        String phone = phoneNum.replaceAll("\\s","");
                        phone = phone.replaceAll("[^0-9+]", "");
                        url = new URL(globalV.URLStart+"/api/values/DeviceLogin/"+macAd+"/"+ phone +"/"+ priNum);
                        urlConnection = (HttpsURLConnection) url.openConnection();
                        urlConnection.setRequestProperty("Connection","Close");
                        DsLogs.writeLog("Opened connection to "+url.toString());
                        DsLogs.writeLog("ResponseCode "+String.valueOf(urlConnection.getResponseCode()));
                        DsLogs.writeLog("ResponseMessage "+urlConnection.getResponseMessage());
                        
                        Log.d("Opened connection to",url.toString());
                        Log.d("Shimon#################",String.valueOf(urlConnection.getResponseCode()));
                        Log.d("Shimon#################",urlConnection.getResponseMessage());
                
                        InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                        reader = new BufferedReader(new InputStreamReader(in));
                        StringBuilder total = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            total.append(line);
                        }
                        System.out.println("total: " +total);
    
                        DsLogs.writeLog("Response: " +total.toString());
                
                        try{
                            in.close();
                            urlConnection.disconnect();
                        }catch (IOException e) {
                            DsLogs.writeLog("catch: " + e.toString());
                        }
    
                        globalV.logged_in = true;
                
                
                        JSONArray infoJSON = new JSONArray(total.toString());
                        
                        globalV.fullName = infoJSON.getJSONObject(0).getString("FullName");
                        globalV.eventName = infoJSON.getJSONObject(0).getJSONArray("E").getJSONObject(0).getString("EventName");
                        globalV.EventPIN = String.valueOf(infoJSON.getJSONObject(0).getJSONArray("E").getJSONObject(0).getInt("EventPIN"));
                        if(infoJSON.getJSONObject(0).getJSONArray("E").getJSONObject(0).has("CharidyEventID")){
                            globalV.chairdyEventId = infoJSON.getJSONObject(0).getJSONArray("E").getJSONObject(0).getString("CharidyEventID");
                        }else {
                            globalV.chairdyEventId = "";
                        }
                        if(infoJSON.getJSONObject(0).getJSONArray("E").getJSONObject(0).has("PrinterMacAddress")){globalV.printerMAC = infoJSON.getJSONObject(0).getJSONArray("E").getJSONObject(0).getString("PrinterMacAddress");}
                        
                        globalV.deviceName = infoJSON.getJSONObject(0).getJSONArray("E").getJSONObject(0).getJSONArray("D").getJSONObject(0).getString("DeviceName");
                        new nonStaticUtilities().saveDeviceName(infoJSON.getJSONObject(0).getJSONArray("E").getJSONObject(0).getJSONArray("D").getJSONObject(0).getString("DeviceName"),LoginActivity.this);
                        
                        JSONObject p = infoJSON.getJSONObject(0).getJSONArray("E").getJSONObject(0).getJSONArray("D").getJSONObject(0).getJSONArray("P").getJSONObject(0);
                        globalV.FTPPass = p.getString("FTPPass");
                        globalV.FTPUser = p.getString("FTPUser");
                        globalV.PrintServerPort = p.getString("PrintServerPort");
                        globalV.PrintServerFTP = p.getString("PrintServerFTP");
                        globalV.BUTTON_ANONYMUS_ON = false;
                        globalV.BUTTON_HONERY_ON = false;
                        globalV.BUTTON_CHECK_ON = true;
                        globalV.checkLastYear = false;
                        
                        if(p.has("O")){
                            JSONArray O = p.getJSONArray("O");
                            for(int i = 0; i < O.length(); i++){
                                if(O.getJSONObject(i).has("MethodName")){
                                    if(O.getJSONObject(i).getString("MethodName").contains("buttonAnonymous"))globalV.BUTTON_ANONYMUS_ON = true;
                                    if(O.getJSONObject(i).getString("MethodName").contains("buttonReasons"))globalV.BUTTON_HONERY_ON = true;
                                    if(O.getJSONObject(i).getString("MethodName").contains("ButtonCheck"))globalV.BUTTON_CHECK_ON = false;
    
                                    if(O.getJSONObject(i).getString("MethodName").contains("LastYear"))globalV.checkLastYear = true;
                                }
                                
                        }
                        }
    
                        if(p.has("PrinterName")){globalV.printerName = p.getString("PrinterName");}
                        if(p.has("PrinterMacAddress")){globalV.printerMAC_WIFI = p.getString("PrinterMacAddress");}
                        if(p.has("PrinterMacAddressLAN")){globalV.printerMAC_LAN = p.getString("PrinterMacAddressLAN");}    //Todo fix this
                        if(isEthernetConnected()){
                            globalV.printerMAC = globalV.printerMAC_LAN;
                            globalV.MacMode = " L ";
                        }else  {
                            globalV.printerMAC = globalV.printerMAC_WIFI;
                            globalV.MacMode = " W ";
                        }
                        
                        
                    } catch (IOException e) {DsLogs.writeLog("catch: " + e.toString());
                        e.printStackTrace();
                        globalV.logged_in = false;
                        globalV.postLogin = false;
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            public void run() {
                                //Toast.makeText(LoginActivity.this, "Error. Try again. ", Toast.LENGTH_SHORT).show();
                                gettingInfo.setText("");
                                background.setBackgroundColor(Color.parseColor("#ffffff"));
                                finish();
                            }
                        });
                    } catch (JSONException e) {DsLogs.writeLog("catch: " + e.toString());
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            public void run() {
                                Toast.makeText(LoginActivity.this, "INVALID PRINTER NUMBER", Toast.LENGTH_SHORT).show();
                            }
                        });
                        e.printStackTrace();
                        globalV.logged_in = false;
                        globalV.postLogin = false;
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                        public void run() {
                            //Toast.makeText(LoginActivity.this, "Error. Try again. ", Toast.LENGTH_SHORT).show();
                            gettingInfo.setText("");
                            background.setBackgroundColor(Color.parseColor("#ffffff"));
                            finish();
                        }
                    });
                    
                    }

                    getReasons();
                }
            }).start();
        }
    }
    
    public void getReasons(){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                gettingInfo.setText(getString(R.string.getting_reasons));
            }
        });
            
            new Thread( new Runnable() {
                @Override
                public void run() {
                    URL url = null;
                    HttpsURLConnection urlConnection;
                    BufferedReader reader = null;
                    try {
                        url = new URL(globalV.URLStart+"/api/values/PaymentReasons/"+macAd);
                        urlConnection = (HttpsURLConnection) url.openConnection();
                        urlConnection.setRequestProperty("Connection","Close");
                        Log.d("Opened connection to",url.toString());
                        InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                        reader = new BufferedReader(new InputStreamReader(in));
                        
                        Log.d("Shimon#################",String.valueOf(urlConnection.getResponseCode()));
                        Log.d("Shimon#################",urlConnection.getResponseMessage());
                        DsLogs.writeLog("Opened connection to "+url.toString());
                        DsLogs.writeLog("ResponseCode "+String.valueOf(urlConnection.getResponseCode()));
                        DsLogs.writeLog("ResponseMessage "+urlConnection.getResponseMessage());
                        StringBuilder total = new StringBuilder();
                        String line;
                        if(reader == null){
                            total.append("");
                        }else{
                            while ((line = reader.readLine()) != null) {
                                total.append(line);
                            }
                        }
                        
                        Log.d("total: " ,total.toString());
                        DsLogs.writeLog("Response: " +total.toString());
                        try{
                            System.out.println("in try...");
                            in.close();
                            urlConnection.disconnect();
                            System.out.println("after try...");
                        }catch (IOException e) {DsLogs.writeLog("catch: " + e.toString());
                            System.out.println("error closing.......");
                        }
                        
                        JSONArray reasonJSON = new JSONArray(total.toString());
                        globalV.reasonsObjs.clear();
                        globalV.reasons.clear();
                        for(int i = 0; i<reasonJSON.length();i++){
                            JSONObject jsonobject = reasonJSON.getJSONObject(i);
                            globalV.reasonsObjs.add(new ReasonObject(jsonobject.getString("reasonName"),jsonobject.getInt("reasonId"),jsonobject.getInt("number")));
                        }
                        for(int i = 0; i<reasonJSON.length();i++){
                            globalV.reasons.add(globalV.reasonsObjs.get(i).name);
                        }
                        
                    //    finish();
    
                        Intent mintent = new Intent(LoginActivity.this,MainActivity.class);
                        startActivity(mintent);
                
                    } catch (IOException e) {DsLogs.writeLog("catch: " + e.toString());
                        e.printStackTrace();
                        globalV.logged_in = false;
                        globalV.postLogin = false;
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            public void run() {
                                //Toast.makeText(LoginActivity.this, "Error. Try again. ", Toast.LENGTH_SHORT).show();
                                gettingInfo.setText("");
                                background.setBackgroundColor(Color.parseColor("#ffffff"));
                            //    finish();
                            }
                        });
                    } catch (JSONException e) {DsLogs.writeLog("catch: " + e.toString());
                        e.printStackTrace();
                        globalV.logged_in = false;
                        globalV.postLogin = false;
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                        public void run() {
                            //Toast.makeText(LoginActivity.this, "Error. Try again. ", Toast.LENGTH_SHORT).show();
                            gettingInfo.setText("");
                            background.setBackgroundColor(Color.parseColor("#ffffff"));
                         //   finish();
                        }
                    });
                    
                    }
            
                }
            }).start();
        
    }
    
    public void connectWiFi(String SSID,String password,String Security) {
        try {
    
            WifiConfiguration conf = new WifiConfiguration();
            conf.SSID = "\"" + SSID + "\"";   // Please note the quotes. String should contain ssid in quotes
            conf.status = WifiConfiguration.Status.ENABLED;
            conf.priority = 40;
            
            System.out.println("Starting connection...");
            
            if (Security.toUpperCase().contains("WEP")) {
                Log.v("rht", "Configuring WEP");
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                conf.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                conf.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                conf.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                
                if (password.matches("^[0-9a-fA-F]+$")) {
                    conf.wepKeys[0] = password;
                } else {
                    conf.wepKeys[0] = "\"".concat(password).concat("\"");
                }
                
                conf.wepTxKeyIndex = 0;
                
            } else if (Security.toUpperCase().contains("WPA")) {
                System.out.println("it is wpa...");
                conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                conf.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                
                conf.preSharedKey = "\"" + password + "\"";
                
            } else {
                System.out.println("it is open...");
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                conf.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                conf.allowedAuthAlgorithms.clear();
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            }
            
            //        WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiManager wifiManager = (WifiManager)this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            
            wifiManager.setWifiEnabled(true);
            
            int networkId = wifiManager.addNetwork(conf);
            System.out.println("middle connection...");
            
            List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
            for (WifiConfiguration i : list) {
                if (i.SSID != null && i.SSID.equals("\"" + SSID + "\"")) {
                    
                    boolean isDisconnected = wifiManager.disconnect();
                    
                    boolean isEnabled = wifiManager.enableNetwork(i.networkId, true);
                    
                    boolean isReconnected = wifiManager.reconnect();
                    
                    break;
                }
            }
            
        } catch (Exception e) {DsLogs.writeLog("catch: " + e.toString());
            e.printStackTrace();
        }
        
       // Toast.makeText(LoginActivity.this, getMyLocalIpAddress(), Toast.LENGTH_LONG).show();
    }
    
    public static String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;
                
                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }
                
                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(Integer.toHexString(b & 0xFF) + ":");
                }
                
                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {DsLogs.writeLog("catch: " + ex.toString());

            //handle exception
        }
        return "";
    }
    
    public String getMyLocalIpAddress() {
        try {
            Enumeration networkInterfaces = NetworkInterface.getNetworkInterfaces();  // gets All networkInterfaces of your device
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface inet = (NetworkInterface) networkInterfaces.nextElement();
                Enumeration address = inet.getInetAddresses();
                while (address.hasMoreElements()) {
                    InetAddress inetAddress = (InetAddress) address.nextElement();
                    if (inetAddress.isSiteLocalAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (Exception e) {DsLogs.writeLog("catch: " + e.toString());
            // Handle Exception
        }
        return "";
    }
    
    boolean isWifiConnected(){
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mWifi.isConnected();
    }
    
    Boolean isEthernetConnected(){
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
        return mWifi.isConnected();
    }
    
    
    void waitForNet(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isNetworkAvailable()) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        public void run() {
                            background.setBackgroundColor(Color.parseColor("#FF555555"));  //FF713333
                            gettingInfo.setText(getString(R.string.waiting_for_network));
                        }
                    });
                }
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    public void run() {
                        gettingInfo.setText("");
                        background.setBackgroundColor(Color.parseColor("#ffffff"));
                    }
                });
            }
        });
        
        thread.start();
    }
    
    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }
    
    boolean checkServerReachebility(){
        boolean serverIsReacheble = false;
        try {
            URL url = new URL("http://connectivitycheck.gstatic.com/generate_204");
            System.out.println(url);
            URLConnection conn = url.openConnection();
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setRequestProperty("Connection","Close");
            httpConn.setAllowUserInteraction(false);
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setRequestMethod("GET");
            httpConn.setConnectTimeout(8000);
            httpConn.setReadTimeout(8000);
            httpConn.connect();
            try {
                int response = httpConn.getResponseCode();
                System.out.println(response);
                if (response == 204) {
                    serverIsReacheble = true;
                }
            } catch (Exception e) {
                System.out.println("204 FAILED");
            }
        } catch (Exception e) {
            System.out.println("204 FAILED");
        }
        return serverIsReacheble;
    }
    
    boolean TrdCheckServerReachebility(){
        final boolean[] serverIsReacheble = {false};
        Thread trd = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://connectivitycheck.gstatic.com/generate_204");
                    System.out.println(url);
                    URLConnection conn = url.openConnection();
                    HttpURLConnection httpConn = (HttpURLConnection) conn;
                    httpConn.setAllowUserInteraction(false);
                    httpConn.setInstanceFollowRedirects(true);
                    httpConn.setRequestMethod("GET");
                    httpConn.setConnectTimeout(8000);
                    httpConn.setReadTimeout(8000);
                    httpConn.connect();
                    try {
                        int response = httpConn.getResponseCode();
                        System.out.println(response);
                        if (response == 204) {
                            serverIsReacheble[0] = true;
                        }
                    } catch (Exception e) {
                        System.out.println("204 FAILED");
                    }
                } catch (Exception e) {
                    System.out.println("204 FAILED");
                }
            }
        });
        trd.start();
        try {
            trd.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    
        return serverIsReacheble[0];
    }

//    public void trustAllCertificates() {
//        try {
//            TrustManager[] trustAllCerts = new TrustManager[]{
//                    new X509TrustManager() {
//                        public X509Certificate[] getAcceptedIssuers() {
//                            X509Certificate[] myTrustedAnchors = new X509Certificate[0];
//                            return myTrustedAnchors;
//                        }
//
//                        @Override
//                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
//                        }
//
//                        @Override
//                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
//                        }
//                    }
//            };
//
//            SSLContext sc = SSLContext.getInstance("SSL");
//            sc.init(null, trustAllCerts, new SecureRandom());
//            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
//            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
//                @Override
//                public boolean verify(String arg0, SSLSession arg1) {
//                    return true;
//                }
//            });
//        } catch (Exception e) {DsLogs.writeLog("catch: " + e.toString());
//
//        }
//    }
    
    int getConnectionState(){
        if(isEthernetConnected()){
            if(checkServerReachebility()){
                return ETHERNET_GOOD;
            }else{
                return  ETHERNET_NO_NET;
            }
            
        }else if(isWifiConnected()){
            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            
            if(checkServerReachebility()){
                switch (WifiManager.calculateSignalLevel(wifiInfo.getRssi(), 5)) {
                    case 0:
                        return  WIFI_GOOD_0;
                    case 1:
                        return  WIFI_GOOD_1;
                    case 2:
                        return  WIFI_GOOD_2;
                    case 3:
                        return  WIFI_GOOD_3;
                    case 4:
                        return  WIFI_GOOD_4;
                }
                
            }else{
                switch (NetworkUtilities.signelStrength) {
                    case 0:
                        return  WIFI_NO_NET_0;
                    case 1:
                        return  WIFI_NO_NET_1;
                    case 2:
                        return  WIFI_NO_NET_2;
                    case 3:
                        return  WIFI_NO_NET_3;
                    case 4:
                        return  WIFI_NO_NET_4;
                }
            }
        }
        return WIFI_GOOD_0;
    }
    
    
    
}
