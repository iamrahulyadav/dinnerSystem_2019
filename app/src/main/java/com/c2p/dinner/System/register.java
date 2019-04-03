package com.c2p.dinner.System;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class register extends Activity {

    TextView iptext, mactext, feedback;
    EditText eventtext;
    LinearLayout background;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register);

        iptext = findViewById(R.id.register_ip);
        mactext = findViewById(R.id.register_mac);
        feedback = findViewById(R.id.register_feedback);
        eventtext = findViewById(R.id.register_eventnum);
        background = findViewById(R.id.register_background
        );
        iptext.setText(getMyLocalIpAddress());
        mactext.setText(getMacAddr());

        eventtext.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if(keyCode == 160 && event.getAction() == KeyEvent.ACTION_DOWN && eventtext.getText().toString().length() > 0){
                    globalV.EventPIN = eventtext.getText().toString();
                    registerDevice(eventtext.getText().toString());
                    
                }else if(keyCode == 67 && event.getAction() == KeyEvent.ACTION_DOWN &&  eventtext.getText().toString().length() <1){
                    finish();
                }

                return false;
            }
        });

    }

    public void registerDevice(final String eventNumber){
        Thread trd =new Thread( new Runnable() {
            @Override
            public void run() {
                URL url = null;
                HttpsURLConnection urlConnection;
                BufferedReader reader = null;
                try {
                    url = new URL(globalV.URLStart+"/api/values/deviceregister/"+eventNumber+"/"+getMacAddr());
                    urlConnection = (HttpsURLConnection) url.openConnection();
                    urlConnection.setRequestProperty("Connection","Close");
                    Log.d("Opened connection to",url.toString());
                    DsLogs.writeLog("Opened connection to "+url.toString());
                    DsLogs.writeLog("ResponseCode "+String.valueOf(urlConnection.getResponseCode()));
                    DsLogs.writeLog("ResponseMessage "+urlConnection.getResponseMessage());
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder total = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        total.append(line);
                    }
                    if(String.valueOf(urlConnection.getResponseCode()).equals("200")){
                        Log.d("total: " ,total.toString());
                        DsLogs.writeLog("Response: " +total.toString());
                        globalV.registeredTo = total.toString();
                    }else{
                        globalV.registeredTo = "";
                    }
                    
                    try{
                        in.close();
                        urlConnection.disconnect();
                    }catch (IOException e) {
                        DsLogs.writeLog("catch: " + e.toString());

                    }
                } catch (IOException e) {
                    DsLogs.writeLog("catch: " + e.toString());

                    e.printStackTrace();
                }
    
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    public void run() {
                        String name = globalV.registeredTo;
                        if(!name.equals("")){
                            new nonStaticUtilities().saveEventName(name,register.this);
                            new nonStaticUtilities().saveEventPin(globalV.EventPIN,register.this);
                            finish();
                        }
                        
                    }
                });
            
            }
        });
        trd.start();
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
        } catch (Exception ex) {
            DsLogs.writeLog("catch: " + ex.toString());

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
        } catch (Exception e) {
            DsLogs.writeLog("catch: " + e.toString());
            // Handle Exception
        }
        return "";
    }

}
