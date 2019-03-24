package com.c2p.dinner.System;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Handler;
import android.os.Looper;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import javax.net.ssl.HttpsURLConnection;

public class multiname extends Activity {

    BufferedReader reader = null;
    TableLayout table;
    EditText et;
    TextView phonetv;
    ArrayList<TableRow> ntvs = new ArrayList<TableRow>();
    int count = 0;
    final int BUTTON_PROCCESS = 157;   //45  157 on device
    final int BUTTON_ENTER = 160;   //66  160 on device
    final int BUTTON_BACK = 67;
    int selected = 0;
    ShapeDrawable border = new ShapeDrawable(new RectShape());
    Dialog dialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_multiname);

        table = findViewById(R.id.mn_table);
        et = findViewById(R.id.mn_et);
        phonetv = findViewById(R.id.mn_phone);
        border.getPaint().setStyle(Paint.Style.STROKE);
        border.getPaint().setColor(Color.BLACK);

        dialog = new Dialog(this);
        dialog.setTitle("Loading... Please wait");
        dialog.show();


        et.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                String text = et.getText().toString();
                if(keyCode == BUTTON_BACK){
                    Intent intent=new Intent();
                    intent.putExtra("RES",0);
                    setResult(Activity.RESULT_OK,intent);
                    finish();

                }else if(keyCode == BUTTON_PROCCESS){
                    if(selected > 0 && selected<= count){
                        Intent intent=new Intent();
                        intent.putExtra("RES",1);
                        intent.putExtra("INDEX",selected-1);
                        setResult(Activity.RESULT_OK,intent);
                        finish();
                    }
                    //result(selected-1);


                }else if(event.getAction() == KeyEvent.ACTION_UP){
                    String regex = "[0-9]+";
                    if (text.matches(regex)){
                        int selection = Integer.parseInt(text);
                        if(selection <= count && selection != 0){
                            deselect();
                            selected = selection;
                            ntvs.get(selected-1).setBackgroundResource(R.drawable.border_green);

                        }
                        et.setText("");
                    }else{
                        et.setText("");
                    }

                }

                return false;
            }
        });

        Intent intent  = getIntent();
        String phone = intent.getExtras().getString("PHONE");
        phonetv.setText(phone);
        getAccounts(phone);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //finish();
        return super.onKeyDown(keyCode, event);
    }

    public void deselect(){
        for(TableRow tv2:ntvs){
            tv2.setBackground(border);
        }

    }

    public void addRow(String[] data){
        Log.d("!!!!!!!!!!!!!",data[0]);
        TableRow tr = new TableRow(this);
        TextView ntv = new TextView(this);
        count ++;
        ntv.setText(""+count+". ");
        ntv.setTextSize(30.0f);
        tr.addView(ntv);
        ntvs.add(tr);
        for(String s:data){
            TextView tv = new TextView(this);
            tv.setText(s);
            tv.setTextSize(30.0f);
            tr.addView(tv);
            TextView etv = new TextView(this);
            etv.setText("                   ");
            tr.addView(etv);

        }

        ShapeDrawable border = new ShapeDrawable(new RectShape());
        border.getPaint().setStyle(Paint.Style.STROKE);
        border.getPaint().setColor(Color.BLACK);
        tr.setBackground(border);

        table.addView(tr);
    }

    public void result(int index){
    
    }
    
    public void getAccounts(String phone){
        final String phoneNum = phone;
        new Thread( new Runnable() {
            @Override
            public void run() {
                URL url = null;
                HttpsURLConnection urlConnection;
                try {
                    String phone = phoneNum.replaceAll("\\s","");
                    phone = phone.replaceAll("[^0-9+]", "");
                    url = new URL(globalV.URLStart+"/api/values/GetAccount/"+phone+"/"+ globalV.deviceMac);
                    urlConnection = (HttpsURLConnection) url.openConnection();
                    System.out.println("stared connection......."+url);
                    DsLogs.writeLog("Opened connection to "+url.toString());
                    DsLogs.writeLog("ResponseCode "+String.valueOf(urlConnection.getResponseCode()));
                    DsLogs.writeLog("ResponseMessage "+urlConnection.getResponseMessage());
                
                    Log.d("Shimon#################",String.valueOf(urlConnection.getResponseCode()));
                    Log.d("Shimon#################",urlConnection.getResponseMessage());
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    reader = new BufferedReader(new InputStreamReader(in));
                    System.out.println("i got after reader.......");
                
                    StringBuilder total = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        total.append(line);
                    }
                    System.out.println(total);
                    DsLogs.writeLog("Response: " +total.toString());
                    try{
                        System.out.println("in try...");
                        in.close();
                        urlConnection.disconnect();
                        System.out.println("after try...");
                    }catch (IOException e) {DsLogs.writeLog("catch: " + e.toString());

                        System.out.println("error closing.......");
                    }
                
                    globalV.accountsJsonArray = new JSONArray(total.toString());
                    
                    if(globalV.accountsJsonArray.length() == 1){
                        Intent intent=new Intent();
                        intent.putExtra("RES",1);
                        intent.putExtra("INDEX",0);
                        setResult(Activity.RESULT_OK,intent);
                        finish();
                    }else {
               //         String[] accounts = new String[globalV.accountsJsonArray.length()];
                        for(int i = 0; i < globalV.accountsJsonArray.length(); i++){
               //             accounts[i] = globalV.accountsJsonArray.getJSONObject(i).getString("FullName");
                            final String[] singleAccount = new String[]{
                                    globalV.accountsJsonArray.getJSONObject(i).getString("FullName")+"\r\n"+
                                            globalV.accountsJsonArray.getJSONObject(i).getString("FullNameJewish"),
                                    globalV.accountsJsonArray.getJSONObject(i).getString("Address")+"\r\n"+
                                            globalV.accountsJsonArray.getJSONObject(i).getString("Note"),
                            };
                            Log.d("Array",Arrays.toString(singleAccount));
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                public void run() {
                                    addRow(singleAccount);
                                }
                            });
                        }
                    }
                } catch (IOException e) {
                    DsLogs.writeLog("catch: " + e.toString());
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                    DsLogs.writeLog("catch: " + e.toString());
                }
            dialog.dismiss();
                if(globalV.accountsJsonArray.length() != 1){
                    selected = 1;
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        public void run() {
                            ntvs.get(selected-1).setBackgroundResource(R.drawable.border_green);
                        }
                    });
                }
            }
        }).start();
    }
    

}
