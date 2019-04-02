package com.c2p.dinner.System;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import java.util.List;

public class wifiActivity extends AppCompatActivity {
    
    WifiManager mWifiManager;
    LinearLayout LL;
    List<ScanResult> wifiList;
    StringBuilder sb = new StringBuilder();
    ImageButton refreshBtn;
    ImageButton backBtn;
    
    private final Handler handler = new Handler();
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.wifi_layout);
        LL = findViewById(R.id.main_linear);
        refreshBtn = findViewById(R.id.imageButton);
        backBtn = findViewById(R.id.imageButtonBack);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWifiManager.startScan();
            }
        });
        
    }
    
    @Override
    public void onResume(){
        super.onResume();
            if(checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 87);
            }
        mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        registerReceiver(mWifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        mWifiManager.startScan();
    }
    
    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(mWifiScanReceiver);
    }
    
    private final BroadcastReceiver mWifiScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {
            if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                List<ScanResult> mScanResults = mWifiManager.getScanResults();
                for (int i = 0; i < mScanResults.size(); i++){
                    sb.append(new Integer(i+1).toString() + ".");
                    sb.append((mScanResults.get(i)).SSID);
                    sb.append("\n");
                }
                refreshList(mScanResults);
                Log.d("#### mScanResults",sb.toString());
                // add your logic here
            }
        }
    };
    
    public void refreshList(List<ScanResult> mScanResults){
        LL.removeAllViews();
        for (final ScanResult wifiNetwork : mScanResults) {
            LinearLayout LlOneLine = new LinearLayout(this);
            LlOneLine.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            LlOneLine.setOrientation(LinearLayout.HORIZONTAL);
            LlOneLine.setPadding(0,0,0,0);
            
            final TextView netTV = new TextView(this);
            ImageView myImage = new ImageView(this);
            myImage.setImageResource(getSignalDrwable(WifiManager.calculateSignalLevel(wifiNetwork.level, 5)));
            FrameLayout.LayoutParams myImageLayout = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,FrameLayout.LayoutParams.MATCH_PARENT);
            myImageLayout.gravity = Gravity.CENTER;
            myImage.setLayoutParams(myImageLayout);
            myImage.setPadding(30,10,0,10);
            netTV.setText(wifiNetwork.SSID);
            netTV.setTextSize(TypedValue.COMPLEX_UNIT_SP,32);
            netTV.setPadding(20,10,0,10);
            netTV.setWidth(800);
            netTV.setClickable(true);
            netTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PasswordDialog(netTV.getText().toString());
                }
            });
            LlOneLine.addView(myImage);
            LlOneLine.addView(netTV);
            LL.addView(LlOneLine);
            System.out.println(wifiNetwork.SSID + " " + wifiNetwork.level + "  " + wifiNetwork.capabilities);
        }
    }
    
    private int getSignalDrwable(int db){
        int id = 0;
        switch(db){
            case 0:
                id = (R.drawable.wifilowsmall);
                break;
            case 1:
                id = (R.drawable.wifia1);
                break;
            case 2:
                id = (R.drawable.wifia2);
                break;
            case 3:
                id = (R.drawable.wifia3);
                break;
            case 4:
                id = (R.drawable.wifia4);
                break;
        }
        System.out.println( "db is " +db +" resource id is: " + id);
        return id;
    }
    
    public void PasswordDialog(final String ssid){
        LayoutInflater inflater = getLayoutInflater();
        final View dialogContent = inflater.inflate(R.layout.pass_dialog, null);
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle(ssid);
        final EditText edit = dialogContent.findViewById(R.id.edit_network_password);
        builder.setView(dialogContent);
        builder.setPositiveButton("Done!", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id) {
                connectWiFi(ssid,edit.getText().toString(),"WPA2");
                finish();
            }
        })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id){
                    
                    }
                });
        android.support.v7.app.AlertDialog dialog = builder.create();
        AppCompatCheckBox showPassword = dialogContent.findViewById(R.id.do_not_show);
        dialog.show();
        showPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    // hide password
                    edit.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    edit.setSelection(edit.getText().length());
                } else {
                    // show password
                    edit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    edit.setSelection(edit.getText().length());
                }
            }
        });
    }
    
    public void connectWiFi(String SSID, String password, String Security) {
        try {
            String networkSSID = SSID;
            String networkPass = password;
            
            WifiConfiguration conf = new WifiConfiguration();
            conf.SSID = "\"" + networkSSID + "\"";   // Please note the quotes. String should contain ssid in quotes
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
                
                if (networkPass.matches("^[0-9a-fA-F]+$")) {
                    conf.wepKeys[0] = networkPass;
                } else {
                    conf.wepKeys[0] = "\"".concat(networkPass).concat("\"");
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
                
                conf.preSharedKey = "\"" + networkPass + "\"";
                
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
                if (i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
                    
                    boolean isDisconnected = wifiManager.disconnect();
                    
                    boolean isEnabled = wifiManager.enableNetwork(i.networkId, true);
                    
                    boolean isReconnected = wifiManager.reconnect();
                    
                    break;
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

}


