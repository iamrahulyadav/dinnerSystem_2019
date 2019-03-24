package com.c2p.dinner.System;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class wifimenual extends Activity {

    final int BUTTON_BACK =67;
    final int BUTTON_PROCCESS = 157;   //45  157 on device
    final int BUTTON_ENTER = 160;   //66  160 on device
    EditText ssidtext, passwordtext;
    Button defaultB, okB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifimenual);

        ssidtext = findViewById(R.id.wifim_ssid);
        passwordtext = findViewById(R.id.wifim_pass);
        defaultB = findViewById(R.id.wifim_default);
        okB = findViewById(R.id.wifim_ok);

        ssidtext.requestFocus();

        defaultB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeToDisk("","");
                finish();
            }
        });

        okB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ssidtext.getText().toString().length() != 0 && passwordtext.getText().toString().length() != 0){
                    writeToDisk(ssidtext.getText().toString(),passwordtext.getText().toString());
                }
                PackageManager packageManager = getPackageManager();
                startActivity(packageManager.getLaunchIntentForPackage("com.example.shimo.lshapelauncher"));
                finish();
            }
        });

    }


    public void writeToDisk(String ssid, String pass){

        String PATH = Environment.getExternalStorageDirectory() + "/Download/";
        File logFile = new File(PATH + "ssid.txt");
        Log.d("PATH", logFile.getPath());
        if (!logFile.exists()){
            try{
                logFile.createNewFile();
            } catch (IOException e){
                e.printStackTrace();
            }
        }

        try{
            JSONObject obj = new JSONObject() ;
            obj.put("SSID", ssid);
            obj.put("PASS", pass);
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, false));
            buf.write(obj.toString());
            buf.close();
            Log.d("json to disk",obj.toString());
        }
        catch (IOException | JSONException e){
            e.printStackTrace();
        }


    }




}
