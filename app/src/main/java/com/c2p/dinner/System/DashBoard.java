package com.c2p.dinner.System;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
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
import java.net.URL;
import java.text.NumberFormat;
import javax.net.ssl.HttpsURLConnection;

public class DashBoard extends Activity {
    TextView textView;
    TextView bkrndTextView;
    TextView remainingTimeTv;
    ConstraintLayout constraintLayout;
    private boolean runThreads;
    private int totalAmount = 0;
    NumberFormat formatter = NumberFormat.getCurrencyInstance();
    private Button backBtn;
    private Button menuBtn;
    private long lastTouchTime = 0;
    private long currentTouchTime = 0;
    int[] color = {0xff0000ff, 0xff303F9F, 0xffffff00};
    Bitmap img = null;
    
    LinearLayout donorRoot;
    LinearLayout honoreeRoot;
    
    JSONArray jsonArray;
    JSONArray dp;
    JSONObject jSONObject;
    String dateTimeRemaining;
    
    TextView[] nameTv;
    TextView[] amountTv;
    TextView[] honorTv;
    TextView[] minAgo;
    
    TextView[] HonoreeNameTv;
    TextView[] HonoreeRaisedTv;
    TextView[] HonoreeGoalTv;
    TextView[] HonoreeMinAgo;
    
    final int TOTAL_ONLY = 1;
    final int TOTAL_WITH_DONORS = 2;
    final int TOTAL_WITH_HONOREES = 3;
    
    int DashboardType = 0;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dash_board);
        
        nameTv = new TextView[]{findViewById(R.id.name_8),findViewById(R.id.name_4),findViewById(R.id.name_7),findViewById(R.id.name_3)
                ,findViewById(R.id.name_6),findViewById(R.id.name_2),findViewById(R.id.name_5),findViewById(R.id.name_1)};
    
        amountTv = new TextView[]{findViewById(R.id.amount_8),findViewById(R.id.amount_4),findViewById(R.id.amount_7),findViewById(R.id.amount_3)
                ,findViewById(R.id.amount_6),findViewById(R.id.amount_2),findViewById(R.id.amount_5),findViewById(R.id.amount_1)};
    
    
        honorTv = new TextView[]{findViewById(R.id.honor_8),findViewById(R.id.honor_4),findViewById(R.id.honor_7),findViewById(R.id.honor_3)
                ,findViewById(R.id.honor_6),findViewById(R.id.honor_2),findViewById(R.id.honor_5),findViewById(R.id.honor_1)};
    
        minAgo =    new TextView[]{findViewById(R.id.min_ago_8),findViewById(R.id.min_ago_4),findViewById(R.id.min_ago_7),findViewById(R.id.min_ago_3)
                ,findViewById(R.id.min_ago_6),findViewById(R.id.min_ago_2),findViewById(R.id.min_ago_5),findViewById(R.id.min_ago_1)};
    
    
        HonoreeNameTv = new TextView[]{findViewById(R.id.name_d_8),findViewById(R.id.name_d_4),findViewById(R.id.name_d_7),findViewById(R.id.name_d_3)
                ,findViewById(R.id.name_d_6),findViewById(R.id.name_d_2),findViewById(R.id.name_d_5),findViewById(R.id.name_d_1)};
    
        HonoreeRaisedTv = new TextView[]{findViewById(R.id.raised_d_8),findViewById(R.id.raised_d_4),findViewById(R.id.raised_d_7),findViewById(R.id.raised_d_3)
                ,findViewById(R.id.raised_d_6),findViewById(R.id.raised_d_2),findViewById(R.id.raised_d_5),findViewById(R.id.raised_d_1)};
    
        HonoreeGoalTv = new TextView[]{findViewById(R.id.goal_tv_8),findViewById(R.id.goal_tv_4),findViewById(R.id.goal_tv_7),findViewById(R.id.goal_tv_3)
                ,findViewById(R.id.goal_tv_6),findViewById(R.id.goal_tv_2),findViewById(R.id.goal_tv_5),findViewById(R.id.goal_tv_1)};
    
        HonoreeMinAgo =    new TextView[]{findViewById(R.id.min_ago_d_8),findViewById(R.id.min_ago_d_4),findViewById(R.id.min_ago_d_7),findViewById(R.id.min_ago_d_3)
                ,findViewById(R.id.min_ago_d_6),findViewById(R.id.min_ago_d_2),findViewById(R.id.min_ago_d_5),findViewById(R.id.min_ago_d_1)};
        
        
        formatter.setMaximumFractionDigits(0);
        
        textView = findViewById(R.id.dash_tv);
        bkrndTextView = findViewById(R.id.dash_tv2);
        remainingTimeTv = findViewById(R.id.remaining_time);
        constraintLayout = findViewById(R.id.dash_root);
        backBtn = findViewById(R.id.dash_back);
        menuBtn = findViewById(R.id.dash_menu);
        donorRoot = findViewById(R.id.honoree_root);
        honoreeRoot = findViewById(R.id.honoree_root2);
        
        textView.setShadowLayer(20, 0, 0, Color.YELLOW);
    
        logIn();
    
        getReasons();
    
        backBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                lastTouchTime = currentTouchTime;
                currentTouchTime = System.currentTimeMillis();
                if (currentTouchTime - lastTouchTime < 500) {
                    lastTouchTime = 0;
                    currentTouchTime = 0;
                    finish();
                }
            }
        });
    
        menuBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                lastTouchTime = currentTouchTime;
                currentTouchTime = System.currentTimeMillis();
                if (currentTouchTime - lastTouchTime < 500) {
                    lastTouchTime = 0;
                    currentTouchTime = 0;
                    
                    
                }
            }
        });
        
    }
    
    @Override
    public void onResume(){
        super.onResume();
    
        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/segment.ttf");
        textView.setTypeface(custom_font);
        textView.setTextColor(color[2]);
        constraintLayout.setBackgroundColor(color[0]);
    
        bkrndTextView.setTypeface(custom_font);
        
        httpGetImage();
    
        switch (DashboardType) {
            case TOTAL_ONLY:
                totalOnlyUpdater();
                break;
            case TOTAL_WITH_DONORS:
                totalWithDonorsUpdater();
                break;
            case TOTAL_WITH_HONOREES:
                totalWithHonoreesUpdater();
                break;
        }
        
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("############################### onPause CALLED....................................");
        runThreads = false;
    }
    
    private void totalOnlyUpdater() {
        
        if(donorRoot.getChildCount() > 0){
            donorRoot.removeAllViews();
            ViewGroup.LayoutParams params = donorRoot.getLayoutParams();
            params.height = 1;
            donorRoot.setLayoutParams(params);
        }
    
        if(honoreeRoot.getChildCount() > 0){
            honoreeRoot.removeAllViews();
            ViewGroup.LayoutParams params = honoreeRoot.getLayoutParams();
            params.height = 1;
            honoreeRoot.setLayoutParams(params);
        }
        
        runThreads = true;
        Thread t = new Thread() {
            @Override
            public void run() {
                currentThread().setName("DashBoard");
                String url = "https://api.dinner.systems/api/values/DeviceLogin/" + globalV.deviceMac + "/total/1";
                while (runThreads) {
                    try {
                        Log.d("url", url);
                        HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
                        Log.d("valueUpdater", String.valueOf(urlConnection.getResponseCode()));
                        InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                        StringBuilder total = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            total.append(line);
                        }
                        Log.d("valueUpdater", total.toString());
                        in.close();
                        urlConnection.disconnect();
                        
                        
                        jsonArray = new JSONArray(total.toString());
                        final JSONObject jSONObject = jsonArray.getJSONObject(0);
                        System.out.println(jSONObject);
                        
                        totalAmount = jSONObject.getInt("TotalRaised");
                        final String dateTimeRemaining = jSONObject.getString("DateTimeRemaining");
                        
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    textView.setText(formatter.format(totalAmount).substring(1));
                                    bkrndTextView.setText(backgrountText(totalAmount));
                                    
                                    remainingTimeTv.setText(dateTimeRemaining.substring(0, dateTimeRemaining.length()-3));
                                    
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        
                        
                        sleep(10 * 1000);
                        
                        
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        
        
        t.start();
    }
    
    private void totalWithDonorsUpdater() {
        if(honoreeRoot.getChildCount() > 0){
            honoreeRoot.removeAllViews();
        }
        runThreads = true;
        Thread t = new Thread() {
            @Override
            public void run() {
                currentThread().setName("DashBoard");
                String url = "https://api.dinner.systems/api/values/DeviceLogin/" + globalV.deviceMac + "/total/1";
                while (runThreads) {
                    try {
                        Log.d("url", url);
                        HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
                        Log.d("valueUpdater", String.valueOf(urlConnection.getResponseCode()));
                        InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                        StringBuilder total = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            total.append(line);
                        }
                        Log.d("valueUpdater", total.toString());
                        in.close();
                        urlConnection.disconnect();
                        
                        jsonArray = new JSONArray(total.toString());
                        jSONObject = jsonArray.getJSONObject(0);
                        System.out.println(jSONObject);
                        System.out.println(jSONObject.get("Total"));
                        
                        totalAmount = jSONObject.getInt("TotalRaised");
                        dateTimeRemaining = jSONObject.getString("DateTimeRemaining");
    
    
                        dp = jSONObject.getJSONArray("DP");
    
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    textView.setText(formatter.format(totalAmount).substring(1));
                                    bkrndTextView.setText(backgrountText(totalAmount));
    
                                    remainingTimeTv.setText(dateTimeRemaining.substring(0, dateTimeRemaining.length()-3));
                                    
                                        for(int i = 0; i < dp.length(); i++){
                                            JSONObject jsonObject = dp.getJSONObject(i);
        
                                            System.out.println(jsonObject.toString());
        
                                            if(jsonObject.has("FullName")){
                                                nameTv[i].setText(jsonObject.getString("FullName"));
                                            }else{
                                                nameTv[i].setText("");
                                            }
        
                                            if(jsonObject.has("Amount")){
                                                amountTv[i].setText(formatter.format(jsonObject.getInt("Amount")));
                                            }else{
                                                amountTv[i].setText("");
                                            }
        
        
                                            if(jsonObject.has("ReasonID")){
                                                System.out.println("jsonObject.has(\"ReasonID\")" + jsonObject.has("ReasonID"));
                                                honorTv[i].setText(getReasonFromId(jsonObject.getInt("ReasonID")));
                                            }else{
                                                honorTv[i].setText("");
                                            }
        
                                            if(jsonObject.has("MinutesAgo")){
                                                minAgo[i].setText(jsonObject.getString("MinutesAgo") + "m ago");
                                            }
        
                                        }
                                    
                                    
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        
                        
                        sleep(10 * 1000);
                        
                        
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        
        
        t.start();
    }
    
    private void totalWithHonoreesUpdater() {
        if(donorRoot.getChildCount() > 0){
            donorRoot.removeAllViews();
        }
        runThreads = true;
        Thread t = new Thread() {
            @Override
            public void run() {
                currentThread().setName("DashBoard");
                String url = "https://api.dinner.systems/api/values/DeviceLogin/" + globalV.deviceMac + "/total/1";
                while (runThreads) {
                    try {
                        Log.d("url", url);
                        HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
                        Log.d("valueUpdater", String.valueOf(urlConnection.getResponseCode()));
                        InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                        StringBuilder total = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            total.append(line);
                        }
                        Log.d("valueUpdater", total.toString());
                        in.close();
                        urlConnection.disconnect();
                        
                        jsonArray = new JSONArray(total.toString());
                        jSONObject = jsonArray.getJSONObject(0);
                        System.out.println(jSONObject);
                        System.out.println(jSONObject.get("Total"));
                        
                        totalAmount = jSONObject.getInt("TotalRaised");
                        dateTimeRemaining = jSONObject.getString("DateTimeRemaining");
                        
                        
                        dp = jSONObject.getJSONArray("DP");
                        
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    textView.setText(formatter.format(totalAmount).substring(1));
                                    bkrndTextView.setText(backgrountText(totalAmount));
                                    
                                    remainingTimeTv.setText(dateTimeRemaining.substring(0, dateTimeRemaining.length()-3));
                                    
                                    for(int i = 0; i < dp.length(); i++){
                                        JSONObject jsonObject = dp.getJSONObject(i);
                                        
                                        System.out.println(jsonObject.toString());
    
                                        if(jsonObject.has("ReasonID")){
                                            System.out.println("jsonObject.has(\"ReasonID\")" + jsonObject.has("ReasonID"));
                                            HonoreeNameTv[i].setText(getReasonFromId(jsonObject.getInt("ReasonID")));
                                        }else{
                                            HonoreeNameTv[i].setText("");
                                        }
                                        
                                        if(jsonObject.has("HonoreeTotal")){
                                            HonoreeRaisedTv[i].setText(formatter.format(jsonObject.getInt("HonoreeTotal")));
                                        }else{
                                            HonoreeRaisedTv[i].setText("");
                                        }
    
                                        if(jsonObject.has("HonoreeGoal")){
                                            HonoreeGoalTv[i].setText(formatter.format(jsonObject.getInt("HonoreeGoal")));
                                        }else{
                                            HonoreeGoalTv[i].setText("");
                                        }
                                        
                                        if(jsonObject.has("MinutesAgo")){
                                            HonoreeMinAgo[i].setText(jsonObject.getString("MinutesAgo") + "m ago");
                                        }
                                        
                                    }
                                    
                                    
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        
                        
                        sleep(10 * 1000);
                        
                        
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        
        
        t.start();
    }
    
    private String getReasonFromId(int id){
        String Str = "";
        for(int i = 0; i < globalV.reasons.size(); i ++){
            if(globalV.reasonsObjs.get(i).id == id){
                Str = globalV.reasonsObjs.get(i).name;
            }
        }
    
        if(Str.contains("-")){
            Str = Str.substring(0, Str.indexOf("-"));
        }
        
        return Str;
    }
    
    private void httpGetImage() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection urlConnection = null;
                try {
                    URL url = new URL("http://printserver.c2p.group:7647/Events/" + new nonStaticUtilities().getEventPin(DashBoard.this) + "/TotalRaisedBackround.png");
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestProperty("Connection","Close");
                    Log.d("httpGetImage", url.toString() + "  ResponseCode: " + String.valueOf(urlConnection.getResponseCode()) + " " + urlConnection.getResponseMessage());
                    if (urlConnection.getResponseCode() == 200) {
                        InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                        img = BitmapFactory.decodeStream(in);
                        in.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally{
                    if(urlConnection != null)
                        urlConnection.disconnect();
                }
    
                if(img != null){
                    try{
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            public void run() {
                                try{
                                    constraintLayout.setBackground(new BitmapDrawable(DashBoard.this.getResources(),img));
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        });
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
        
        
        
    }
    
    private void getReasons(){
        new Thread( new Runnable() {
            @Override
            public void run() {
                URL url = null;
                HttpsURLConnection urlConnection;
                BufferedReader reader = null;
                try {
                    url = new URL(globalV.URLStart+"/api/values/PaymentReasons/"+globalV.deviceMac);
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
                    
                    in.close();
                    urlConnection.disconnect();
                    
                    JSONArray reasonJSON = new JSONArray(total.toString());
                    globalV.reasonsObjs.clear();
                    globalV.reasons.clear();
                    for(int i = 0; i<reasonJSON.length();i++){
                        JSONObject jsonobject = reasonJSON.getJSONObject(i);
                        globalV.reasonsObjs.add(new ReasonObject(jsonobject.getString("reasonNameJewish"),jsonobject.getInt("reasonId"),jsonobject.getInt("number")));
                    }
                    for(int i = 0; i<reasonJSON.length();i++){
                        globalV.reasons.add(globalV.reasonsObjs.get(i).name);
                    }
                } catch (Exception e) {DsLogs.writeLog("catch: " + e.toString());
                    e.printStackTrace();
                }
                
            }
        }).start();
        
    }
    
    private String backgrountText(int amount){
        String str = "";
        String amountStr = String.valueOf(amount);
        
        for(int i = 0; i < amountStr.length(); i ++){
            System.out.println(amountStr.length());
            if(i != 0 && (amountStr.length() - i == 3  || amountStr.length() - i == 6) ){
                str += ",";
            }
            str += "8";
        }
        
        return str;
    }
    
    public void logIn() {
        
        new Thread(new Runnable() {
            @Override
            public void run() {
                
                    URL url = null;
                    HttpsURLConnection urlConnection;
                    BufferedReader reader = null;
                    try {
                        url = new URL(globalV.URLStart + "/api/values/DeviceLogin/" + globalV.deviceMac + "/7180005227/1");
                        urlConnection = (HttpsURLConnection) url.openConnection();
                        urlConnection.setRequestProperty("Connection", "Close");
                        DsLogs.writeLog("Opened connection to " + url.toString());
                        DsLogs.writeLog("ResponseCode " + String.valueOf(urlConnection.getResponseCode()));
                        DsLogs.writeLog("ResponseMessage " + urlConnection.getResponseMessage());
                        
                        Log.d("Opened connection to", url.toString());
                        Log.d("Shimon#################", String.valueOf(urlConnection.getResponseCode()));
                        Log.d("Shimon#################", urlConnection.getResponseMessage());
                        
                        InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                        reader = new BufferedReader(new InputStreamReader(in));
                        StringBuilder total = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            total.append(line);
                        }
                        System.out.println("total: " + total);
                        
                        in.close();
                        urlConnection.disconnect();
                        
                        
                        JSONArray infoJSON = new JSONArray(total.toString());
    
                        JSONArray jsonArrayO = infoJSON.getJSONObject(0).getJSONArray("E").getJSONObject(0).getJSONArray("D").getJSONObject(0).getJSONArray("P").getJSONObject(0).getJSONArray("O");
    
                        for (int i = 0; i < jsonArrayO.length(); i++){
                            
                            if(jsonArrayO.getJSONObject(i).getString("Type").equals("Dashboard")){
                                
                                System.out.println(jsonArrayO.getJSONObject(i));
    
                                switch (jsonArrayO.getJSONObject(i).getString("Text")) {
                                    case "TotalWithReasons":
                                        DashboardType = TOTAL_WITH_HONOREES;
                                        
                                        break;
                                    case "TotalWithDonors":
                                        DashboardType = TOTAL_WITH_DONORS;
                                        break;
                                    default:
                                        DashboardType = TOTAL_ONLY;
                                        break;
                                }
    
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    public void run() {
                                        DashBoard.this.onResume();
                                    }
                                });
                                
                            }
                        }
    
                    } catch (Exception e) {
                        e.printStackTrace();
                        DashboardType = TOTAL_ONLY;
    
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            public void run() {
                                DashBoard.this.onResume();
                            }
                        });
                        
                    }
                    
                
            }
        }).start();
    }
    
}
