package com.c2p.dinner.System;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;

import androidx.constraintlayout.widget.ConstraintLayout;

import static java.lang.Thread.*;

public class MainActivity extends Activity {
    
    //EditText phoneET;
    C2pPrinter c2pPrinter;
    EditText defaultET, amountText, phoneText, cardText, expText, cvvText, checkText;
    ArrayList<EditText> Etexts = new ArrayList<EditText>();
    TextView reasontext,feedback,dinnerIncome,totalPeople;
    TextView nametext, notetext;
    TextView timetext, usernametext, eventtext, printerConnectionType;
    ImageButton bcash, bcc, bcheck;
    Button buttonAnonymous;
    ImageView wifiic, printerImg, sharedPrinterImg;
    ImageView peopleIcon;
    Button bprepay;
    ConstraintLayout background;
    BufferedReader reader = null;
    final String macAd = globalV.deviceMac;
    ArrayList<String> linksArrList;
    ArrayList<String> barCodesArrList;
    String Name ="";
    int accountId = 0;
    String nameNote = "";
    long startTime;
    int prevKey = 0;
    LinearLayout ccll,peopleLL, nameLL, plagedLL;
    ConstraintLayout bll;
    ImageView processIcon;
    String[] hints;
    
    String prepaidAmount;
    
    String[] array;
    String[] barCodeArray;
    final int BUTTON_CASH = 143;  //54  143 on device
    final int BUTTON_CC = 154;   //52  154 on device
    final int BUTTON_CHECK = 155;   //31  155 on device
    final int BUTTON_BACK =67;
    final int BUTTON_PROCCESS = 157;   //45  157 on device
    final int BUTTON_ENTER = 160;   //66  160 on device
    final int BUTTON_MENU = 156;
    final int CCMODE_NON = 0;
    final int CCMODE_EXP = 1;
    final int CCMODE_CVV = 2;
    final int MODE_CASH = 1;
    final int MODE_CC = 2;
    final int MODE_CCSWIPE = 3;
    final int MODE_CHECK = 4;
    final int MODE_PREPAY = 5;
    final int MODE_NON = 0;
    final int REQ_MULTI_NAME = 901;
    final int REQ_MULTIPAYMENT = 199;
    final int REQ_HONERY = 198;
    boolean MODE_CCMULTIPAYMENT = false;
    boolean running = true;
    boolean gotFiles = false;
    boolean isPrinterTested = false;
    boolean printerConnected = false;
    
    private final int SET_ACTIVE = 1;
    private final int SET_NOT_ACTIVE = 0;
    private final int CHECK_STATUS = 2;


    int ccmode = CCMODE_NON;
    int mode = MODE_NON;
    EditText last = null;
    String phone, amount, card, exp, cvv,cardbuffer,checknumber,last4,mppayments, mpday, totalallpayments, mpeach;
    boolean charidyAnonymous = false;
    String text;
    int reason = -1;
    NumberFormat formatter = NumberFormat.getCurrencyInstance();
    final SimpleDateFormat sdf = new SimpleDateFormat("EE MMM dd yyyy  h:mm aaa",  Locale.US);
    private boolean runThreads = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    
    
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        Locale locale;
        if(new nonStaticUtilities().getLanguage(this).equals("iw_IL")){
            locale = new Locale("iw", "IL");
            conf.locale = locale;
        } else if(new nonStaticUtilities().getLanguage(this).equals("en_US")){
            locale = new Locale("en_US");
            conf.locale = locale;
        }
    
        res.updateConfiguration(conf, dm);
        
        System.out.println("conf.locale.getLanguage()" + conf.locale.getLanguage());
        if (conf.locale.getLanguage().equals("iw"))
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        else
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        
        setContentView(R.layout.activity_full);
        defaultET = findViewById(R.id.full_default);
        phoneText = findViewById(R.id.full_phone);
        amountText = findViewById(R.id.full_amount);
        cardText = findViewById(R.id.full_card);
        expText = findViewById(R.id.full_exp);
        cvvText = findViewById(R.id.full_cvv);
        checkText = findViewById(R.id.full_check);
        nametext = findViewById(R.id.full_name);
        notetext = findViewById(R.id.full_pledged);
        usernametext = findViewById(R.id.full_username);
        eventtext = findViewById(R.id.full_event);
        timetext = findViewById(R.id.full_datetime);
        wifiic = findViewById(R.id.full_wifi);
        printerImg = findViewById(R.id.printer_iv);
        sharedPrinterImg = findViewById(R.id.shared_iv);
        printerConnectionType= findViewById(R.id.printer_connection);
 //       gettingInfo = findViewById(R.id.getting_info);
        reasontext = findViewById(R.id.full_reason);
        feedback = findViewById(R.id.full_feedback);
        totalPeople = findViewById(R.id.full_totalpeople);
        peopleIcon= findViewById(R.id.peopleIcon);
        dinnerIncome = findViewById(R.id.full_totalincome);
        bcash = findViewById(R.id.full_bcash);
        bcc = findViewById(R.id.full_bcc);
        bcheck = findViewById(R.id.full_bcheck);
        bprepay = findViewById(R.id.full_bprepay);
        ccll = findViewById(R.id.full_ccLL);
        bll = findViewById(R.id.full_bLL);
        peopleLL = findViewById(R.id.total_people_ll);
        nameLL = findViewById(R.id.name_ll);
        plagedLL = findViewById(R.id.pleged_ll);
        processIcon = findViewById(R.id.full_picon);
        Etexts.add(amountText);
        Etexts.add(phoneText);
        Etexts.add(cardText);
        Etexts.add(expText);
        Etexts.add(cvvText);
        Etexts.add(checkText);
        phoneText.addTextChangedListener(new PhoneNumberFormattingTextWatcher("US"));
        amountText.addTextChangedListener(new MoneyTextWatcher(amountText));
        expText.addTextChangedListener(new ExpiryDateTextWatcher());
        //phoneET.requestFocus();
    
        hints = new String[]{getString(R.string.enter_any_of_the_above_information), getString(R.string.Enter_Expiration_Date), getString(R.string.Enter_CVV), getString(R.string.Enter_check_number)};
        
        globalV.postLogin = false;
        
        if(!isNetworkAvailable()){ //ToDo add wifi activity
            Log.d("SHIMONS TEST","########### W" +
                    "IFI NOT AVAILABLE");
            connectWiFi("DSHT","dShS@5227#","WPA2");
        }
        if(!globalV.logged_in){
            Intent int1 = new Intent(MainActivity.this,LoginActivity.class);
            startActivity(int1);
            finishAffinity();
        }else{
            c2pPrinter = new C2pPrinter();
            c2pPrinter.initPrinter(this);
        }
        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
                WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        
    
        buttonAnonymous  = findViewById(R.id.anonym);
        buttonAnonymous.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                charidyAnonymous = !charidyAnonymous;
                if(charidyAnonymous){
                    buttonAnonymous.setBackgroundColor(Color.GREEN);//  #aee9a4
                }else{
                    buttonAnonymous.setBackgroundResource(R.drawable.border_black);
                }
            }
        });
    
        Button buttonReasons  = findViewById(R.id.reasons);
        buttonReasons.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(globalV.reasonsObjs.size() > 19){
                    Intent Rintent=new Intent(MainActivity.this,searchReason.class);
                    startActivityForResult(Rintent,REQ_HONERY);
                }else{
                    Intent Rintent=new Intent(MainActivity.this,reason.class);
                    startActivityForResult(Rintent,REQ_HONERY);
                }
            }
        });

        defaultET.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((keyCode == BUTTON_ENTER || keyCode == 66)  && event.getAction() == KeyEvent.ACTION_DOWN ) {
                    text = defaultET.getText().toString();
                    if(text.endsWith("?")){
                        if(mode == MODE_PREPAY){
                            amount ="";
                            amountText.setText("");
                        }
                        mode = MODE_CCSWIPE;
                        bll.setBackgroundResource(R.drawable.border_green);
                        if(text.startsWith("%")){
                            cardbuffer = "";
                        }
                        cardbuffer += text;
                        defaultET.setText("");
                        bll.removeAllViews();
                        ccll.removeAllViews();
                        bll.addView(bcc);
                        ccll.addView(cardText);
                        cardText.setBackgroundResource(R.drawable.border_green);
                        cardText.setText(last4digits());
                        Log.d("cardbuffer",cardbuffer);


                    }else if(text.length() ==0){

                    }else if(ccmode == CCMODE_EXP){
                        if(text.length() != 4){
                            toast( "Invalid Expiration Date!", true);
                        }else{
                            expText.setText(text);
                            exp = text;
                            defaultET.setText("");
                            defaultET.setHint(hints[2]);
                            ccmode = CCMODE_CVV;
                            last = expText;
                            expText.setBackgroundResource(R.drawable.border_green);
                        }

                    }else if(ccmode == CCMODE_CVV){
                        if(text.length() != 4 && text.length() != 3){
                            toast("Invalid CVV!", true);
                        }else {
                            cvvText.setText(text);
                            cvv =text;
                            last = cvvText;
                            ccmode = CCMODE_NON;
                            defaultET.setText("");
                            defaultET.setHint(hints[0]);
                            cvvText.setBackgroundResource(R.drawable.border_green);

                        }

                    }else if(mode == MODE_CHECK && checkText.getText().toString().length() <1){
                        checkText.setText(text);
                        checknumber = text;
                        defaultET.setText("");
                        defaultET.setHint(hints[0]);
                        last = checkText;
                        checkText.setBackgroundResource(R.drawable.border_green);


                    }else if((text.length() > 9 && text.length() < 13)|(text.length() == 11 && text.startsWith("1"))){//its a phone number
                        if(text.length() == 11 && text.startsWith("1")){
                            text = text.substring(1);
                        }
                        phoneText.setTextDirection(View.TEXT_DIRECTION_LTR);
                      //  phoneText.setText("");
                        phoneText.setText(text);
                        phone =text;
                        defaultET.setText("");
                        nametext.setText("");
                        notetext.setText("");
                        getInfo(phoneText.getText().toString());
                        last = phoneText;
                        phoneText.setBackgroundResource(R.drawable.border_green);
                    }else if(text.length() > 14){                          //probebly a credit card
                        if (new Utilities().luhn(text)) {
                            cardText.setText(text);
                            defaultET.setText("");
                            card =text;
                            if(ccll.indexOfChild(expText)<0){ccll.addView(expText);}
                            if(ccll.indexOfChild(cvvText)<0){ccll.addView(cvvText);}
                            defaultET.setHint(hints[1]);
                            ccmode = CCMODE_EXP;
                            if(bll.indexOfChild(bcash)>-1){bll.removeView(bcash);}
                            if(bll.indexOfChild(bcheck)>-1){bll.removeView(bcheck);}
                            last = cardText;
                            cardText.setBackgroundResource(R.drawable.border_green);
                            mode = MODE_CC;
                        } else {
                            toast("Invalid number!", true);
                        }
                        //set hint to enter exp end put in the exp field
                    }else if(text.length() < 10 && text.length() > 0){//probebly an amount
                        if(text.split("\\.")[0].length() < 7){
                            if(!text.contains(".")){
                                text = text+".00";
                            }else {
                                if(text.split("\\.").length < 2){
                                    text+="0";
                                }
                                if((text.split("\\.")[1].length() <2)){
                                    text+="0";
                                }
                                while(text.split("\\.")[1].length() >2){
                                    text = text.substring(0,text.length()-1);
                                }

                            }
    
                            if(mode == MODE_PREPAY){
                                if(Double.valueOf(text) > Double.valueOf(prepaidAmount)){
                                    mode = MODE_NON;
                                    feedback.setText("");
                                    processIcon.setVisibility(View.INVISIBLE);
                                    bll.removeAllViews();
                                    ccll.removeAllViews();
                                    if (!globalV.kioskMode) {
                                        bll.addView(bcash);
                                    }
                                    bll.addView(bcc);
                                    if (!globalV.kioskMode) {
                                        bll.addView(bcheck);
                                    }
                                    ccll.addView(cardText);
                                }
        
                            }
                            
                            amountText.setText(text);
                            amount = text;
                            defaultET.setText("");
                            last = amountText;
                            amountText.setBackgroundResource(R.drawable.border_green);
                            

                        }

                    }
                    if(phoneText.getText().toString().length() > 1 && amountText.getText().toString().length() >1){//check if finished entering info
                        if(mode == MODE_NON){
                        }else if(mode == MODE_CHECK && checkText.getText().toString().length() < 1){
                        }else if(mode == MODE_CC && cvvText.getText().toString().length() < 1){
                        }else if(mode == MODE_CCSWIPE && cardText.getText().toString().length() < 1){
                        }else{
                            defaultET.setHint("");
                            feedback.setText(getString(R.string.Please_press_the_Confirm_button));
                            processIcon.setVisibility(View.VISIBLE);
                        }
                    }
                    return true;
                }else if((keyCode == BUTTON_BACK) && event.getAction() == KeyEvent.ACTION_DOWN ){//back button
                    if((last != null)&&(defaultET.getText().toString().length() == 0)){
                        String cleanText = last.getText().toString().replaceAll("[$,\\s\\-()]", "");
                        defaultET.setText(cleanText);
                        defaultET.setSelection(defaultET.getText().length());
    
                        feedback.setText("");
                        processIcon.setVisibility(View.INVISIBLE);

                        if(mode == MODE_CHECK){
                            mode = MODE_NON;
                            bll.removeAllViews();
                            ccll.removeAllViews();
                            bll.addView(bcash);
                            bll.addView(bcc);
                            bll.addView(bcheck);
                            ccll.addView(cardText);
                            defaultET.setHint(hints[0]);
                        }
                        if(ccmode > 0){
                            ccmode -=1;
                            defaultET.setHint(hints[ccmode]);
                        }
                        if(last == phoneText){
                            nametext.setText("");
                            notetext.setText("");
                        }
                        if(last == cvvText){
                            ccmode = CCMODE_CVV;
                            defaultET.setHint(hints[2]);
                        }
                        if(last == checkText){
                            defaultET.setHint(hints[3]);
                            mode = MODE_CHECK;
                        }

                        last.setBackgroundResource(R.drawable.border_red);
                        last.setText("");
                        last = null;
                        return  true;
                        
                    }else if(defaultET.getText().toString().length() == 0){

                    }
                    return false;
                }else if((keyCode == BUTTON_PROCCESS) && event.getAction() == KeyEvent.ACTION_DOWN ){   //process Button
                    boolean ok = true;
                    if(phoneText.getText().toString().length() <5){
                        ok = false;
                        phoneText.setBackgroundResource(R.drawable.border_red);
                    }
                    if(amountText.getText().toString().length() <3){
                        ok = false;
                        amountText.setBackgroundResource(R.drawable.border_red);
                    }
                    if(mode == MODE_CC){
                        if(cardText.getText().toString().length() <5){
                            ok = false;
                            cardText.setBackgroundResource(R.drawable.border_red);
                        }
                        if(expText.getText().toString().length() <4){
                            ok = false;
                            expText.setBackgroundResource(R.drawable.border_red);
                        }
                        if(cvvText.getText().toString().length() <3){
                            ok = false;
                            cvvText.setBackgroundResource(R.drawable.border_red);
                        }
                    }
                    if(mode == MODE_CHECK && checkText.getText().length() <1){
                        ok = false;
                        checkText.setBackgroundResource(R.drawable.border_red);
                    }
                    if(mode == MODE_NON){
                        ok = false;
                        bll.setBackgroundResource(R.drawable.border_red);
                    }else{
                        bll.setBackgroundResource(R.drawable.border_green);
                    }
                    if(mode == MODE_CASH && ok){
                        proccessCash();
                    }else if(mode == MODE_CHECK && ok){
                        proccessCheck();
                    }else if(mode == MODE_CCSWIPE && ok){
                        proccessCC();
                    }else if(mode == MODE_CC && ok){
                        proccessCCManual();
                    }else if(mode == MODE_PREPAY && ok){
                        proccessPrePaid();
                    }
    
                    return true;

                }else if((keyCode == BUTTON_MENU) && event.getAction() == KeyEvent.ACTION_DOWN){
                    Intent menuIntent = new Intent(MainActivity.this,menu.class);
                    menuIntent.putExtra("CHAA",charidyAnonymous);
                    startActivityForResult(menuIntent,1);
                    return  true;
                }else if((keyCode == BUTTON_CASH) && event.getAction() == KeyEvent.ACTION_DOWN){
                    if(MODE_CCMULTIPAYMENT){
                        toast("You must add Credit Card with Multiple Payments",true);
                    }else if (!globalV.kioskMode) {
                        if(mode == MODE_PREPAY){
                            amount ="";
                            amountText.setText("");
                        }
                        ccmode = CCMODE_NON;
                        mode = MODE_CASH;
                        bll.removeAllViews();
                        bll.addView(bcash);
                        ccll.removeAllViews();
                        defaultET.setHint(hints[0]);
                        bll.setBackgroundResource(R.drawable.border_green);
                        if(phoneText.getText().toString().length() > 1 && amountText.getText().toString().length() >1) {//check if finished entering info
                            defaultET.setHint("");
                            feedback.setText(getString(R.string.Please_press_the_Confirm_button));
                            processIcon.setVisibility(View.VISIBLE);
                        }
                    }
                    return  true;
                }else if((keyCode == BUTTON_CC) && event.getAction() == KeyEvent.ACTION_DOWN){
                    if(mode == MODE_PREPAY){
                        amount ="";
                        amountText.setText("");
                    }
                    mode = MODE_CC;
                    bll.removeAllViews();
                    ccll.removeAllViews();
                    bll.addView(bcc);
                    ccll.addView(cardText);
                    ccll.addView(expText);
                    ccll.addView(cvvText);
                    defaultET.setHint(hints[0]);
                    if(cardText.getText().toString().length() > 10 && expText.getText().toString().length() < 4){
                        ccmode =CCMODE_EXP;
                        defaultET.setHint(hints[1]);
                    }
                    if(expText.getText().toString().length() > 2 && cvvText.getText().toString().length() < 1){
                        ccmode = CCMODE_CVV;
                        defaultET.setHint(hints[2]);
                    }
                    bll.setBackgroundResource(R.drawable.border_green);
                    return  true;
                }else if((keyCode == BUTTON_CHECK) && (event.getAction() == KeyEvent.ACTION_DOWN)){
                    if(!globalV.BUTTON_CHECK_ON){
                        return true;
                    }
                    if(MODE_CCMULTIPAYMENT){
                        toast( "You must add Credit Card with Multiple Payments",true);
                    }else if (!globalV.kioskMode) {
                        if(mode == MODE_PREPAY){
                            amount ="";
                            amountText.setText("");
                        }
                        ccmode = CCMODE_NON;
                        mode = MODE_CHECK;
                        checkText.setText("");
                        checkText.setBackgroundResource(R.drawable.border_black);
                        bll.removeAllViews();
                        ccll.removeAllViews();
                        bll.addView(bcheck);
                        ccll.addView(checkText);
                        defaultET.setHint(hints[3]);
                        bll.setBackgroundResource(R.drawable.border_green);
                    } else {
                    }
                    return  true;
                }
                return false;
            }
        });
    
        if(globalV.chairdyEventId.length() > 0){
            totalPeople.setVisibility(View.VISIBLE);
            dinnerIncome.setVisibility(View.VISIBLE);
            peopleIcon.setVisibility(View.VISIBLE);
            peopleLL.setVisibility(View.VISIBLE);
            buttonAnonymous.setVisibility(View.VISIBLE);
        }else{
//            totalPeople.setVisibility(View.INVISIBLE);
//            dinnerIncome.setVisibility(View.INVISIBLE);
//            peopleIcon.setVisibility(View.INVISIBLE);
//            peopleLL.setVisibility(View.INVISIBLE);
            buttonAnonymous.setVisibility(View.INVISIBLE);
            nameLL.removeViewAt(1);
            plagedLL.removeViewAt(1);
        }
        
        if(!globalV.BUTTON_ANONYMUS_ON){buttonAnonymous.setVisibility(View.INVISIBLE);}
        if(!globalV.BUTTON_HONERY_ON){buttonReasons.setVisibility(View.INVISIBLE);}
        if(!globalV.BUTTON_CHECK_ON){bcheck.setVisibility(View.INVISIBLE);}
        
        if(globalV.doNotSharePrinter){
            sharedPrinterImg.setVisibility(View.INVISIBLE);
        }else{
            sharedPrinterImg.setVisibility(View.VISIBLE);
        }
        
        refresh();

    }

    void setPrepay(String ppammount){
        mode = MODE_PREPAY;
        prepaidAmount = ppammount;
        ccll.removeAllViews();
        bll.removeAllViews();
        if(!ppammount.contains(".")){
            ppammount = ppammount+".00";
        }else {
            if(ppammount.split("\\.").length < 2){
                ppammount+="0";
            }
            if((ppammount.split("\\.")[1].length() <2)){
                ppammount+="0";
            }
        }
        amountText.setText(ppammount);
        amount = ""+ppammount;
        amountText.setBackgroundResource(R.drawable.border_green);
        bll.addView(bprepay);
        defaultET.setHint("");
        feedback.setText(getString(R.string.Please_press_the_Confirm_button));
        processIcon.setVisibility(View.VISIBLE);

    }

    void refresh(){
        bll.removeAllViews();
        ccll.removeAllViews();
        if (!globalV.kioskMode) {
            bll.addView(bcash);
        }
        bll.addView(bcc);
        if (!globalV.kioskMode) {
            bll.addView(bcheck);
        }
        ccll.addView(cardText);
        defaultET.setText("");
        defaultET.setHint(hints[0]);
        nametext.setText("");
        notetext.setText("");
        reasontext.setText("");
        ccmode = CCMODE_NON;
        mode = MODE_NON;
        last = null;
        phone ="";
        accountId = 0;
        amount = "";
        card = "";
        exp = "";
        cvv = "";
        checknumber = "";
        cardbuffer = "";
        mppayments = "";
        mpday = "";
        totalallpayments = "";
        mpeach = "";
        MODE_CCMULTIPAYMENT =false;
        charidyAnonymous = false;
        reason = -1;
        feedback.setText("");
        for (EditText et:Etexts) {
            et.setText("");
            et.setBackgroundResource(R.drawable.border_black);
        }
        bll.setBackgroundResource(R.drawable.border_black);
        processIcon.setVisibility(View.INVISIBLE);
        buttonAnonymous.setBackgroundResource(R.drawable.border_black);
    }

    void refreshPayment(){
        bll.removeAllViews();
        ccll.removeAllViews();
        if (!globalV.kioskMode) {
            bll.addView(bcash);
        }
        bll.addView(bcc);
        if (!globalV.kioskMode) {
            bll.addView(bcheck);
        }
        ccll.addView(cardText);
        defaultET.setText("");
        defaultET.setHint(hints[0]);
        ccmode = CCMODE_NON;
        mode = MODE_NON;
        last = null;
        card = "";
        exp = "";
        cvv = "";
        cardText.setText("");
        expText.setText("");
        cvvText.setText("");
        cardbuffer = "";
        feedback.setText("");
        bll.setBackgroundResource(R.drawable.border_black);
        cardText.setBackgroundResource(R.drawable.border_black);
        expText.setBackgroundResource(R.drawable.border_black);
        cvvText.setBackgroundResource(R.drawable.border_black);
        processIcon.setVisibility(View.INVISIBLE);
    }
    
    void proccessCash(){
        sendPost(MODE_CASH);
    }
    
    void proccessCheck(){
        sendPost(MODE_CHECK);
    }
    
    void proccessPrePaid(){
        sendPost(MODE_PREPAY);
    }

    void proccessCC(){
        sendPost(MODE_CCSWIPE);
    }
    
    void proccessCCManual(){
        sendPost(MODE_CC);
    }
    
    void getInfo(String phone){
        if(TrdCheckServerReachebility()){
            globalV.logTime = System.currentTimeMillis();
            Bundle extras = new Bundle();
            Intent int1 = new Intent(MainActivity.this,multiname.class);
            extras.putString("PHONE",phone);
            int1.putExtras(extras);
            startActivityForResult(int1,REQ_MULTI_NAME);
        }else{
            Toast.makeText(MainActivity.this, "No Network", Toast.LENGTH_SHORT).show();
        }
        
        
    }
    
    void getTotalRaised(){
        new Thread( new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("https://api.charidy.com/api/v1/campaign/" + globalV.chairdyEventId + "?extend[]=campaign_stats");
//                    Log.d("url",url.toString());
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestProperty("Connection","Close");
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder total = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        total.append(line);
                    }
//                    Log.d("total",total.toString());
                    in.close();
                    urlConnection.disconnect();
                    JSONObject jsonobject = new JSONObject(total.toString());
                    final int totalRaised = jsonobject.getJSONObject("data").getJSONObject("relationships").getJSONObject("campaign_stats").getJSONObject("data").getInt("total");
                    final int totalDonors = jsonobject.getJSONObject("data").getJSONObject("relationships").getJSONObject("campaign_stats").getJSONObject("data").getInt("donors_total");
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        public void run() {
                            dinnerIncome.setText(formatter.format(totalRaised));
                            totalPeople.setText(String.valueOf(totalDonors));
                        }
                    });
                    
                } catch (IOException | NumberFormatException | JSONException e) {
                    DsLogs.writeLog("catch: " + e.toString());
                    e.printStackTrace();
                }
            }
        }).start();
    }
    
    void sendPost(int ref) {
        if(TrdCheckServerReachebility()) {
            final int fRef = ref;
            feedback("");
            processIcon.setVisibility(View.INVISIBLE);
            final processDialog pd = new processDialog(this);
            if (accountId == 0) {
                pd.show();
                pd.setTitletext(getString(R.string.Account_Error));
                pd.settext(getString(R.string.Please_reenter_Phone_Number));
                pd.enableB();
            } else {
                pd.show();
                pd.settext(getString(R.string.Processing));
                
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        globalV.logTime = System.currentTimeMillis();
                        com.epson.epos2.printer.Printer thisPrinter = null;
                        
                        final int reasonId = reason;
                
                        try {
                            startTime = SystemClock.uptimeMillis();
                            URL url = new URL(globalV.URLStart + "/api/values/posttrans");
                            Log.i("URL", url.toString());
                            DsLogs.writeLog("URL: " + url.toString());
                            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                            conn.setRequestProperty("Connection","Close");
                            conn.setRequestMethod("POST");
                            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                            conn.setRequestProperty("Accept", "application/json");
                            conn.setDoOutput(true);
                            conn.setDoInput(true);
                    
                            JSONObject jsonParam = new JSONObject();
                            jsonParam.put("AccountID", accountId);
                            jsonParam.put("Amount", amount);
                            jsonParam.put("MacAddress", macAd);
                            jsonParam.put("paymentReasonID", reason);
                            jsonParam.put("Hide", charidyAnonymous);
                    
                            if (fRef == MODE_CHECK) {
                                jsonParam.put("PaymentType", "Check");
                            } else if (fRef == MODE_CASH) {
                                jsonParam.put("PaymentType", "Cash");
                            } else if (fRef == MODE_PREPAY) {
                                jsonParam.put("PaymentType", "Prepaid");
                            } else if (fRef == MODE_CCSWIPE) {
                                jsonParam.put("PaymentType", "CC");
                            } else if (fRef == MODE_CC) {
                                jsonParam.put("PaymentType", "CC_Manual");
                            }
                    
                            if (fRef == MODE_CHECK) {
                                jsonParam.put("RefNum", checknumber);
                            } else if (fRef == MODE_CC || fRef == MODE_CCSWIPE) {
                                jsonParam.put("RefNum", last4);
                            } else {
                                jsonParam.put("RefNum", "");
                            }
                    
                            if (fRef == MODE_CC) {
                                jsonParam.put("ccNum", card);
                                jsonParam.put("exp", exp);
                                jsonParam.put("cvv", cvv);
                                if (MODE_CCMULTIPAYMENT) {
                                    jsonParam.remove("PaymentType");
                                    jsonParam.put("PaymentType", "CC_Multi_manual");
                                    jsonParam.put("cardNumFull", card);
                                    jsonParam.put("numOfPayments", mppayments);
                                    jsonParam.put("monthFrequency", mpday);
                                    jsonParam.put("monthlyAmount", mpeach);
                                }
                            } else if (fRef == MODE_CCSWIPE) {
                                
                                jsonParam.put("ccNum", cardbuffer);
                                if (MODE_CCMULTIPAYMENT) {
                                    jsonParam.remove("PaymentType");
                                    jsonParam.put("PaymentType", "CC_Multi_swipe");
                                    jsonParam.put("cardNumFull", cardbuffer.substring(cardbuffer.indexOf('B') + 1, cardbuffer.indexOf('^')));
                                    String expi = cardbuffer.substring(cardbuffer.indexOf('^', cardbuffer.indexOf('^') + 1) + 1, cardbuffer.indexOf('^', cardbuffer.indexOf('^') + 1) + 5);
                                    jsonParam.put("exp", expi.substring(2, 4) + expi.substring(0, 2));
                                    jsonParam.put("numOfPayments", mppayments);
                                    jsonParam.put("monthFrequency", mpday);
                                    jsonParam.put("monthlyAmount", mpeach);
                                }
                            } else {
                                jsonParam.put("ccNum", "");
                            }
                    
                            Log.i("JSON", jsonParam.toString());
                            DsLogs.writeLog("JSON: " + jsonParam.toString());
                            DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                            // os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
                            os.writeBytes(jsonParam.toString());
                            os.flush();
                            os.close();
                            Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                            Log.i("MSG", conn.getResponseMessage());
                            DsLogs.writeLog("ResponseCode " + String.valueOf(conn.getResponseCode()));
                            DsLogs.writeLog("ResponseMessage " + conn.getResponseMessage());
                    
                            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                            String decodedString;
                            StringBuilder stringBuilder = new StringBuilder();
                            while ((decodedString = in.readLine()) != null) {
                                stringBuilder.append(decodedString);
                            }
                            in.close();
                            String response = stringBuilder.toString();
                            Log.d("response", response);
                            DsLogs.writeLog("response: " + response);
                            response = response.replace("\\", "");
                            //    response = response.replace("\\\"","'");
                            response = response.substring(response.indexOf("{"), response.lastIndexOf("}") + 1);
                            Log.d("response", response);
                            JSONObject obj = new JSONObject(response.substring(response.indexOf("{"), response.lastIndexOf("}") + 1));
                            Log.d("", obj.toString());
                            linksArrList = new ArrayList<String>();
                            barCodesArrList = new ArrayList<String>();
                            globalV.recieptsHttpUrl = obj.getJSONArray("Q").getJSONObject(0).getString("PrintServerURL");
                            if (obj.getJSONArray("Q").getJSONObject(0).has("Copies")) {
                        
                                gotFiles = true;
                                String str;
                                Log.d("Q.length()", "" + obj.getJSONArray("Q").length());
                                for (int i = 0; i < obj.getJSONArray("Q").length(); i++) {
                                    System.out.println("in loop");
                                    for (int count = 0; count < obj.getJSONArray("Q").getJSONObject(i).getInt("Copies"); count++) {
                                
                                        if (obj.getJSONArray("Q").getJSONObject(i).getInt("Childern") < 2) {
                                            str = String.valueOf(obj.getJSONArray("Q").getJSONObject(i).getInt("QueueID"));
                                            str += obj.getJSONArray("Q").getJSONObject(i).getString("FileExt");
                                            linksArrList.add(str);
                                            barCodesArrList.add(obj.getJSONArray("Q").getJSONObject(i).getString("BarCode"));
                                            DsLogs.writeLog(str);
                                            System.out.println(str);
                                        } else {
                                            for (int children = 1; children < obj.getJSONArray("Q").getJSONObject(i).getInt("Childern") + 1; children++) {
                                                str = String.valueOf(obj.getJSONArray("Q").getJSONObject(i).getInt("QueueID"));
                                        
                                                String FileExt = obj.getJSONArray("Q").getJSONObject(i).getString("FileExt");
                                                int number = Integer.valueOf(FileExt.substring(1, FileExt.lastIndexOf('.')));
                                                number += (children - 1);
                                                String strNum = String.format("%04d", number);
                                                str += "-" + strNum + FileExt.substring(FileExt.lastIndexOf('.'), FileExt.length());
                                        
                                                linksArrList.add(str);
                                                barCodesArrList.add(obj.getJSONArray("Q").getJSONObject(i).getString("BarCode"));
                                                System.out.println(str);
                                            }
                                        }
                                
                                    }
                                }
                                array = linksArrList.toArray(new String[0]);
                                barCodeArray = barCodesArrList.toArray(new String[0]);
                                System.out.println(Arrays.toString(array));
                            } else {
                                gotFiles = false;
                        
                                System.out.println("No value for copies.");
                                DsLogs.writeLog("process payment: No value for copies.");
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    public void run() {
                                        pd.settext(getString(R.string.Thank_You));
                                        pd.enableB();
                                        refresh();
                                    }
                                });
                            }
                    
                            if (fRef == MODE_CASH || fRef == MODE_CHECK || fRef == MODE_PREPAY) {
                                if (String.valueOf(conn.getResponseCode()).equals("200")) {
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        public void run() {
                                            if (!globalV.enablePrinting) {
                                                pd.settext(getString(R.string.Thank_You));
                                                pd.enableB();
                                            }
                                    
                                            refresh();
                                        }
                                    });
                                } else {
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        public void run() {
                                            pd.setTitletext(getString(R.string.Payment_Error));
                                            pd.settext(getString(R.string.Unknown_Error));
                                            pd.enableB();
                                            linksArrList.clear();
                                            barCodesArrList.clear();
                                        }
                                    });
                                }
                            } else {
                                if (response.contains("Declined")) {
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        public void run() {
                                            pd.setTitletext(getString(R.string.Declined));
                                            pd.settext(getString(R.string.Please_enter_new_Payment_Or_clear));
                                            pd.enableB();
                                            linksArrList.clear();
                                            barCodesArrList.clear();
                                        }
                                    });
                            
                                } else if (response.contains("Success") || response.contains("Approved")) {
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        public void run() {
                                            pd.setTitletext(getString(R.string.Approved));
                                            if (!globalV.enablePrinting) pd.settext(getString(R.string.Thank_You));
                                            pd.enableB();
                                        }
                                    });
                                } else {
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        public void run() {
                                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                public void run() {
                                                    pd.setTitletext(getString(R.string.Payment_Error));
                                                    pd.settext(getString(R.string.Please_enter_new_Payment_Or_clear));
                                                    pd.enableB();
                                                    linksArrList.clear();
                                                    barCodesArrList.clear();
                                                }
                                            });
                                            refreshPayment();
                                        }
                                
                                    });
                                }
                            }
                    
                    
                            conn.disconnect();
                        } catch (Exception e) {
                            DsLogs.writeLog("catch: " + e.toString());
                            e.printStackTrace();
                    
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                public void run() {
                                    pd.settext("Unknown Error");
                                    pd.enableB();
                                }
                            });
                        }
                        System.out.println("got File Names " + (System.currentTimeMillis() - globalV.logTime));
                        globalV.logTime = System.currentTimeMillis();
                        if (globalV.enablePrinting) {
                            if (gotFiles) {
                                try {
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        public void run() {
                                            refresh();
                                        }
                                    });
                                    
                                    if(globalV.doNotSharePrinter){
                                        c2pPrinter.connectPrinter(globalV.printerMAC, 15, printerImg,pd,MainActivity.this);
                                    }else{
                                        if(c2pPrinter.PrinterAvailibility(CHECK_STATUS)){
                                            c2pPrinter.connectPrinter(globalV.printerMAC, 15, printerImg,pd,MainActivity.this);
                                        }
                                    }
                                    
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        public void run() {
                                            pd.settext(getString(R.string.Finalizing));
                                            pd.enableB();
                                        }
                                    });
                                    c2pPrinter.printExpress(array, barCodeArray,printerImg, pd, MainActivity.this,reasonId);
                                    if (!globalV.doNotSharePrinter) c2pPrinter.disconnectPrinter(printerImg);
                            
                                    Runtime.getRuntime().gc();
                            
                                } catch (Exception e) {
                                    DsLogs.writeLog("catch: " + e.toString());
                                    e.printStackTrace();
                                }
                                linksArrList.clear();
                                barCodesArrList.clear();
                            }
                        }
                        globalV.transactionCompleted = true;
                        try{
                            if(globalV.testAutomation)pd.dismiss();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        
                    }
                });
                thread.start();
            }
        }else{
            Toast.makeText(MainActivity.this, getString(R.string.No_Network), Toast.LENGTH_SHORT).show();
        }
    }
    
    boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    
    void connectWiFi(String SSID,String password,String Security) {
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
            
        } catch (Exception e) {DsLogs.writeLog("catch: " + e.toString());
            e.printStackTrace();
        }

        toast(new nonStaticUtilities().getMyLocalIpAddress(), false);
    }
    
//    void trustAllCertificates() {
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
//        }
//    }
    
    void testPrinter(){
        
        globalV.transactionCompleted = false;
        
        final processDialog pd = new processDialog(this);
        pd.show();
        isPrinterTested = true;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
    
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        public void run() {
                            pd.settext(getString(R.string.waiting_for_printer));
                        }
                    });
    
                    if(!globalV.doNotSharePrinter){
                        startTime = System.currentTimeMillis();
                        while(!c2pPrinter.PrinterAvailibility(CHECK_STATUS)){
                            try {
                                Thread.sleep(300);
                            } catch (InterruptedException ignored) { }
                            if ((System.currentTimeMillis() - startTime) > 31000) {
                                break;
                            }
                        }
                        c2pPrinter.PrinterAvailibility(SET_ACTIVE);
                    }
                    
                    
                    String IP01 = new nonStaticUtilities().getPrinterIp(MainActivity.this);
                    String tmp1 = new nonStaticUtilities().getMyLocalIpAddress();
                    String IP02 = tmp1.substring(0,tmp1.lastIndexOf(".")+1);
                    Log.d("^^^^^^^^^^^^^^^^^",IP02);
                    IP02 += globalV.PRINTER_NUM;
    
                    if(IP01.length() > 2 && c2pPrinter.testPrinter(IP01,MainActivity.this ,pd, printerImg)){
                        globalV.printerMAC = IP01;
                        globalV.PRINTER_IPSTATE ="ip1";
                        printerConnected = true;
                    }else{
                        deleteIP();
                    }
    
                    if(!printerConnected && c2pPrinter.testPrinter(IP02,MainActivity.this , pd, printerImg)){
                        globalV.printerMAC = IP02;
                        globalV.PRINTER_IPSTATE ="ip2";
                        printerConnected = true;
                    }
    
                    if(!printerConnected){
                        if(globalV.printerMAC.equals(globalV.printerMAC_LAN)){
                            if(c2pPrinter.testPrinter(globalV.printerMAC_LAN,MainActivity.this , pd, printerImg)) {
                                globalV.printerMAC = globalV.printerMAC_LAN;
                                globalV.PRINTER_IPSTATE = "mac_L";
                                printerConnected = true;
                            }else if(c2pPrinter.testPrinter(globalV.printerMAC_WIFI,MainActivity.this , pd, printerImg)) {
                                globalV.printerMAC = globalV.printerMAC_WIFI;
                                globalV.PRINTER_IPSTATE = "mac_W";
                                printerConnected = true;
                            }else {
                                updatePD(pd, getString(R.string.Cannot_connect_to_Printer) + "\n \r" +
                                        getString(R.string.Check_Printer_or_Choose_other_Printer),true);
                            }
                        }else {
                            if(c2pPrinter.testPrinter(globalV.printerMAC_WIFI,MainActivity.this , pd, printerImg)) {
                                globalV.printerMAC = globalV.printerMAC_WIFI;
                                globalV.PRINTER_IPSTATE = "mac_W";
                                printerConnected = true;
                            }else if(c2pPrinter.testPrinter(globalV.printerMAC_LAN,MainActivity.this , pd, printerImg)) {
                                globalV.printerMAC = globalV.printerMAC_LAN;
                                globalV.PRINTER_IPSTATE = "mac_L";
                                printerConnected = true;
                            }else {
                                updatePD(pd, getString(R.string.Cannot_connect_to_Printer) + "\n \r" +
                                        getString(R.string.Check_Printer_or_Choose_other_Printer),true);
                            }
                        }
                    }
                    
                    if(printerConnected){
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            public void run() {
                                printerImg.setImageResource(R.drawable.printer_connected);
                                printerConnectionType.setText(globalV.PRINTER_IPSTATE);
                            }
                        });
                    }else{
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            public void run() {
                                printerImg.setImageResource(R.drawable.printer_not_connected);
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                globalV.transactionCompleted = true;
                
            }
        });
        thread.start();
    }

    String last4digits(){
        String tmp1 = cardbuffer.split("\\^")[0];
        try {
            String stg = tmp1.substring(tmp1.length()-4);
            last4 = stg;
            if(stg.matches("[0-9]+")){
                if(new Utilities().luhn(tmp1.substring(2))){
                    return  "****"+stg;
                }else {
                    toast("Invalid number!", true);
                }

            }
            return "Error! Swip Again!";
        } catch (Exception e) {DsLogs.writeLog("catch: " + e.toString());
            e.printStackTrace();
            return "Error! Swip Again!";
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        
        if(resultCode == Activity.RESULT_OK){
            Log.d("resultCode ==", " Activity.RESULT_OK");
    
            if(requestCode==1) {
                if(data.getIntExtra("RES",0)==1){//log out
                    refresh();
                    logOut();
                }
                if(data.getIntExtra("RES",0)==2){//clear
                    refresh();
                }
                if(data.getIntExtra("RES",0)==3){//reason
                    int reason0 = data.getIntExtra("REASON",0);
                    try{
                        reasontext.setText(globalV.reasons.get(reason0));
                        reason0 = reason0+1;
                        reason = reason0;
                        for(ReasonObject obj:globalV.reasonsObjs){
                            if (obj.number == reason0){
                                reason = obj.id;
                            }
                        }
                    }catch (Exception e){DsLogs.writeLog("catch: " + e.toString());
                        e.printStackTrace();
                    }
                }else if(data.getIntExtra("RES",0)== 33){  // reason from search
                    ReasonObject reasonObj = (ReasonObject) data.getSerializableExtra("REASON");
    
                    try{
                        reasontext.setText(reasonObj.name);
                        reason = reasonObj.id;
                    }catch (Exception e){DsLogs.writeLog("catch: " + e.toString());
                        e.printStackTrace();
                    }
                }
                if(data.getIntExtra("RES",0)==4){//clear
                    if (!globalV.kioskMode) {
                        Intent MPintent=new Intent(MainActivity.this,multipayment.class);
                        if(totalallpayments.equals("")){
                            MPintent.putExtra("AMOUNT",amount);

                        }else{
                            MPintent.putExtra("AMOUNT",totalallpayments);
                        }
                        startActivityForResult(MPintent,REQ_MULTIPAYMENT);
                    }


                }

                if(data.getIntExtra("RES",0)==5){//charidyAnonymous
                    charidyAnonymous =(!charidyAnonymous);
                    if(charidyAnonymous){
                        buttonAnonymous.setBackgroundColor(Color.GREEN);
                    }else{
                        buttonAnonymous.setBackgroundResource(R.drawable.border_black);
                    }
                }


            }
            if(requestCode==REQ_MULTI_NAME)
            {
                if(data.getIntExtra("RES",0)==0){//cancel
                    phone = "";
                    String cleantext = last.getText().toString().replaceAll("[$,\\s\\-()]", "");
                    defaultET.setText(cleantext);
                    defaultET.setSelection(defaultET.getText().length());
                    phoneText.setText("");
                    phoneText.setTextDirection(View.TEXT_DIRECTION_RTL);
                    phoneText.setBackgroundResource(R.drawable.border_red);

                }
                Log.d("res =", " "+data.getIntExtra("RES",0));

                if(data.getIntExtra("RES",0)==1){//ok
                    int index = data.getIntExtra("INDEX",0);
                    Log.d("index =", "" + index);
                    if(index < globalV.accountsJsonArray.length()){
                        final int fIndex = index;
                        try {
                            accountId = globalV.accountsJsonArray.getJSONObject(index).getInt("AccountID");
                            Name = globalV.accountsJsonArray.getJSONObject(index).getString("FullName");
                            nameNote = globalV.accountsJsonArray.getJSONObject(index).getString("Note");
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                public void run() {
                                    nametext.setText(Name);
                                    notetext.setText(nameNote);
                                    try {
                                        if(globalV.accountsJsonArray.getJSONObject(fIndex).getInt("Prepaid") > 0){
                                            setPrepay(String.valueOf(globalV.accountsJsonArray.getJSONObject(fIndex).getInt("Prepaid")));
                                        }
                                    } catch (JSONException e) {DsLogs.writeLog("catch: " + e.toString());
                                        e.printStackTrace();
                                    }
                                }
                            });
    
                            if(globalV.enablePrinting && globalV.checkLastYear){
                                getPreparedReceipts(String.valueOf(globalV.accountsJsonArray.getJSONObject(index).getInt("AccountID")));
                            }
        
                        } catch (JSONException e) {DsLogs.writeLog("catch: " + e.toString());
                            e.printStackTrace();
                        }
                    }else{
                        phone = "";
                        String cleantext = last.getText().toString().replaceAll("[$,\\s\\-()]", "");
                        defaultET.setText(cleantext);
                        defaultET.setSelection(defaultET.getText().length());
                        phoneText.setText("");
                        phoneText.setTextDirection(View.TEXT_DIRECTION_RTL);
                        phoneText.setBackgroundResource(R.drawable.border_red);
                    }
                }
            }
            if(requestCode == REQ_HONERY)
            {
                if(data.getIntExtra("RES",999)==0){

                }else if(data.getIntExtra("RES",0)==1){
                    int reason0 = data.getIntExtra("REASON",0);
                    try{
                        reasontext.setText(globalV.reasons.get(reason0));
                        reason0 = reason0+1;
                        reason = reason0;
                        for(ReasonObject obj:globalV.reasonsObjs){
                            if (obj.number == reason0){
                                reason = obj.id;
                            }
                        }
                    }catch (Exception e){DsLogs.writeLog("catch: " + e.toString());
                        e.printStackTrace();
                    }
                }else if(data.getIntExtra("RES",0)==2){
    
                    ReasonObject reasonObj = (ReasonObject) data.getSerializableExtra("REASON");
                    
                    try{
                        reasontext.setText(reasonObj.name);
                        reason = reasonObj.id;
                    }catch (Exception e){DsLogs.writeLog("catch: " + e.toString());
                        e.printStackTrace();
                    }
                }


            }

            if(requestCode == REQ_MULTIPAYMENT)
            {
                Log.d("index =", "############# multy payment reqested ");
                if(data.getIntExtra("RES",999)==0){//canceled

                }

                if(data.getIntExtra("RES",0)==1){//ok

                    totalallpayments = data.getStringExtra("TOTAL");
                    String text =data.getStringExtra("AMOUNT");
                    if(!text.contains(".")){
                        text = text+".00";
                    }else {
                        if(text.split("\\.").length < 2){
                            text+="0";
                        }
                        if((text.split("\\.")[1].length() <2)){
                            text+="0";
                        }
                        while(text.split("\\.")[1].length() >2){
                            text = text.substring(0,text.length()-1);
                        }
                    }
                    amount = text;
                    amountText.setText(amount);
                    amountText.setBackgroundResource(R.drawable.border_green);
                    MODE_CCMULTIPAYMENT = true;
                    mppayments = data.getStringExtra("PAYMENTS");
                    final int mpp1 = Integer.parseInt(mppayments)-1;
                    mpday = data.getStringExtra("DAY");
                    mpeach = data.getStringExtra("EACH");
                    bll.removeAllViews();
                    ccll.removeAllViews();
                    bll.addView(bcc);
                    ccll.addView(cardText);
                    if(mode != MODE_CCSWIPE) {
                        mode = MODE_CC;
                        ccll.addView(expText);
                        ccll.addView(cvvText);
                    }
                    defaultET.setHint(hints[0]);
                    bll.setBackgroundResource(R.drawable.border_green);
                    final Handler h1 = new Handler();
                    h1.post(new Runnable() {
                        @Override
                        public void run() {
                            
                            reasontext.setText(String.valueOf(mpp1) +" Payments of "+formatter.format(Double.parseDouble(mpeach))+" each on "+ mpday+"th of the Month. plus 1 Payment of:"
                                    +formatter.format(Double.parseDouble(amount))+" will be charged now."+" Totaling "
                                    +formatter.format(Double.parseDouble(totalallpayments)));
                        }
                    });

                    if(phoneText.getText().toString().length() > 1 && cardText.getText().toString().length() > 1){
                        defaultET.setHint("");
                        feedback.setText(getString(R.string.Please_press_the_Confirm_button));
                        processIcon.setVisibility(View.VISIBLE);
                    }


                }

            }
        }

    }
    
    @Override
    public void onResume(){
        super.onResume();
        
        startStatusBarUpdater();
        
        if(!isPrinterTested){
            testPrinter();
        }
        
        if(globalV.testAutomation){
            runAutomatedTesting();
        }
        
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("############################### onPause CALLED....................................");
        runThreads = false;
    }
    
    public void onBtnClick(View v) {
        switch (v.getId()) {
            case R.id.full_wifi:
                Intent wifiIntent = new Intent(MainActivity.this, wifiActivity.class);
                startActivity(wifiIntent);
                break;
        }
        
    }
    
    private void startStatusBarUpdater() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                runThreads = true;
                currentThread().setName("startStatusBarUpdater");
                currentThread().setPriority(MIN_PRIORITY);
                while(runThreads){
                    try{
                        final int id;
                        if(isEthernetConnected()){
                            if(checkServerReachebility()){
                                id = MainActivity.this.getResources().getIdentifier("drawable/ethernet_working", null,  MainActivity.this.getPackageName());
                            }else{
                                id =  MainActivity.this.getResources().getIdentifier("drawable/ethernet_not_working", null,  MainActivity.this.getPackageName());
                            }
        
                        }else if(isWifiConnected()){
                            if(checkServerReachebility()){
                                NetworkUtilities.updateSignelStrength(MainActivity.this);
                                id = MainActivity.this.getResources().getIdentifier("drawable/wifi"+NetworkUtilities.signelStrength, null, MainActivity.this.getPackageName());
                            }else{
                                NetworkUtilities.updateSignelStrength(MainActivity.this);
                                id = MainActivity.this.getResources().getIdentifier("drawable/wifi"+NetworkUtilities.signelStrength + "_no_net", null, MainActivity.this.getPackageName());
                            }
    
                        } else{
                            id =  MainActivity.this.getResources().getIdentifier("drawable/wifi0", null,  MainActivity.this.getPackageName());
                        }
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            public void run() {
                                wifiic.setImageResource(id);
                                timetext.setText(sdf.format(Calendar.getInstance().getTime()));
                                usernametext.setText(globalV.fullName);
                                eventtext.setText(globalV.eventName);
                                if(globalV.chairdyEventId.length() > 0){
                                    getTotalRaised();
                                }
                            }
                        });
                        
                        sleep(6000);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                
                }
            
            }
        }).start();
    }
    
    void logOut(){
        globalV.testAutomation = false;
        globalV.postLogin = false;
        new Thread( new Runnable() {
            @Override
            public void run() {
                
                if(globalV.doNotSharePrinter){
                    System.out.println("logOut:  printerMode is single mode");
                    System.out.println("logOut:  c2pPrinter.disconnectPrinter() " + c2pPrinter.disconnectPrinter(printerImg));
                }
                
                URL url = null;
                HttpsURLConnection urlConnection;
                BufferedReader reader = null;
                System.out.println("########################################################################logout");
                try {
                    url = new URL(globalV.URLStart+"/api/values/DeviceLogin/"+macAd+"/LOGOUT/0");
                    urlConnection = (HttpsURLConnection) url.openConnection();
                    urlConnection.setRequestProperty("Connection","Close");
                    System.out.println("stared connection.......");
                    
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    reader = new BufferedReader(new InputStreamReader(in));
                    System.out.println("i got after reader.......");
                    
                    StringBuilder total = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        total.append(line);
                    }
                    System.out.println("total: " +total);
                    
                    try{
                        System.out.println("in try...");
                        in.close();
                        urlConnection.disconnect();
                        System.out.println("after try...");
                    }catch (IOException e) {DsLogs.writeLog("catch: " + e.toString());
                        System.out.println("error closing.......");
                    }
                    
                    globalV.logged_in = true;
                    
                    System.out.println("finished putting..");
                    
                } catch (IOException e) {DsLogs.writeLog("catch: " + e.toString());
                    e.printStackTrace();
                }
                Intent int1 = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(int1);
                finish();
            }
        }).start();
        
        usernametext.setText(getString(R.string.No_User));
        printerConnectionType.setText(" ");
        eventtext.setText(getString(R.string.No_Event));
        running = false;
        
    }
    
    void feedback(String str){
        feedback.setText(str);
    }
    
    void getPreparedReceipts(final String acountId){
        new Thread( new Runnable() {
            @Override
            public void run() {
                globalV.logTime = System.currentTimeMillis();
                URL url = null;
                HttpsURLConnection urlConnection;
                try {
                    url = new URL(globalV.URLStart+"/api/values/GetAccount/"+acountId+"/@"+ globalV.deviceMac);
                    urlConnection = (HttpsURLConnection) url.openConnection();
                    urlConnection.setRequestProperty("Connection","Close");
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
                    in.close();
                    urlConnection.disconnect();
                    
                    String response = total.toString();
    
                    response = response.replace("\\","");
                //    response = response.substring(response.indexOf("{"), response.lastIndexOf("}") + 1);
                    Log.d("response",response);
                    JSONArray jsonArray = new JSONArray(response);
                    Log.d("jsonArray.toString()",jsonArray.toString());
                    globalV.recieptsHttpUrl = jsonArray.getJSONObject(0).getString("PrintServerURL");
                    
              //      JSONObject obj = jsonArray.getJSONObject(0);
                    
                    
                    if(jsonArray.getJSONObject(0).has("Copies")){
                        gotFiles = true;
                        String str;
                        Log.d("jsonArray.length()","" + jsonArray.length());
                        linksArrList = new ArrayList<String>();
                        barCodesArrList = new ArrayList<String>();
                        for(int i = 0; i < jsonArray.length(); i++){
                            System.out.println("in loop");
                            for(int count = 0; count< jsonArray.getJSONObject(i).getInt("Copies"); count++){
                                if(jsonArray.getJSONObject(i).has("Childern")){
                                    if(jsonArray.getJSONObject(i).getInt("Childern") < 2){
                                        str = String.valueOf(jsonArray.getJSONObject(i).getInt("QueueID"));
                                        str += jsonArray.getJSONObject(i).getString("FileExt");
                                        linksArrList.add(str);
                                        barCodesArrList.add(jsonArray.getJSONObject(i).getString("BarCode"));
                                        DsLogs.writeLog(str);
                                        System.out.println(str);
                                    }else{
                                        for(int children = 1; children< jsonArray.getJSONObject(i).getInt("Childern")+1; children++){
                                            str = String.valueOf(jsonArray.getJSONObject(i).getInt("QueueID"));
            
                                            String FileExt = jsonArray.getJSONObject(i).getString("FileExt");
                                            int number = Integer.valueOf(FileExt.substring(1, FileExt.lastIndexOf('.')));
                                            number += (children - 1);
                                            String strNum = String.format("%04d", number);
                                            str += "-" + strNum + FileExt.substring(FileExt.lastIndexOf('.'),FileExt.length());
            
                                            linksArrList.add(str);
                                            barCodesArrList.add(jsonArray.getJSONObject(i).getString("BarCode"));
                                            System.out.println(str);
                                        }
                                    }
                                }else{
                                    str = String.valueOf(jsonArray.getJSONObject(i).getInt("QueueID"));
                                    str += jsonArray.getJSONObject(i).getString("FileExt");
                                    linksArrList.add(str);
                                    barCodesArrList.add(jsonArray.getJSONObject(i).getString("BarCode"));
                                    DsLogs.writeLog(str);
                                    System.out.println(str);
                                }
                                
                
                            }
                        }
                        array = linksArrList.toArray(new String[0]);
                        barCodeArray = barCodesArrList.toArray(new String[0]);
                        System.out.println(Arrays.toString(array));
                    }else {
                        gotFiles = false;
                        System.out.println("No value for copies.");
                    }
                    
                } catch (IOException e) {DsLogs.writeLog("catch: " + e.toString());
                    e.printStackTrace();
                } catch (JSONException e) {DsLogs.writeLog("catch: " + e.toString());
                    e.printStackTrace();
                }
                System.out.println("got File Names " + (System.currentTimeMillis() - globalV.logTime));
                globalV.logTime = System.currentTimeMillis();
                if(gotFiles && globalV.enablePrinting){
                    try {
                        c2pPrinter.connectPrinter(globalV.printerMAC, 15, printerImg, null, MainActivity.this);
                        c2pPrinter.printExpress(array, barCodeArray, printerImg,null, MainActivity.this, -1);
    
                        if(!globalV.doNotSharePrinter)c2pPrinter.disconnectPrinter(printerImg);
    
                        Runtime.getRuntime().gc();
                        linksArrList.clear();
                        barCodesArrList.clear();
                    } catch (Exception e) {DsLogs.writeLog("catch: " + e.toString());
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    void toast(String str, boolean error){
        Toast toast = Toast.makeText(MainActivity.this, str, Toast.LENGTH_LONG);
        TextView ttv = new TextView(this);
        ttv.setText(str);
        ttv.setTextSize(28);
        ttv.setTextColor(Color.BLACK);
        if(error){
            ttv.setBackgroundColor(Color.parseColor("#ffcccc"));
        }else{
            ttv.setBackgroundResource(R.drawable.border_black);
        }
        ttv.setPadding(5,5,5,5);
        ttv.setGravity(Gravity.CENTER);
        toast.setView(ttv);
        toast.show();

    }
    
    private void updatePD(final processDialog pd,final String str, final boolean enable){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                if(pd != null){
                    pd.settext(str);
                    if(enable)pd.enableB();
                }
            }
        });
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
    
    public void deleteIP(){
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(this.openFileOutput("PRINTER_IP", Context.MODE_PRIVATE));
            outputStreamWriter.write("");
            outputStreamWriter.close();
        }
        catch (Exception e) {
            DsLogs.writeLog("catch: " + e.toString());
            
            Log.e("Exception", "File write failed: " + e.toString());
        }
        
    }
    
    //       ##################################################  TEST AOUTOMATION ####################################
    
    private void runAutomatedTesting(){
    
        Thread t = new Thread() {
            @Override
            public void run() {
                currentThread().setName("runAutomatedTesting");
                while (runThreads) {
                    try {
                        sleep(500);
                        if (globalV.transactionCompleted) {
                            sleep(20 * 1000);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        globalV.transactionCompleted = false;
                                        phone = "718" + String.valueOf(new Random().nextInt(9999999));
                                        getAcounts(phone);
                                        amount = String.valueOf(new Random().nextInt(100) + 1000);
                                        sendPost(MODE_CASH);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
    
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        
        
    
        t.start();
        
    }
    
    private void getAcounts(final String phoneNum) throws Exception {
        final boolean[] finished = {false};
        Thread trd = new Thread( new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(globalV.URLStart + "/api/values/GetAccount/" + phoneNum + "/" + macAd);
                    HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                    urlConnection.setRequestProperty("Connection","Close");
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
                    String[] accounts = new String[globalV.accountsJsonArray.length()];
                    for(int i = 0; i < globalV.accountsJsonArray.length(); i++){
                        accounts[i] = globalV.accountsJsonArray.getJSONObject(i).getString("FullName");
                        final String[] singleAccount = new String[]{
                                globalV.accountsJsonArray.getJSONObject(i).getString("FullName")+"\r\n"+
                                        globalV.accountsJsonArray.getJSONObject(i).getString("FullNameJewish"),
                                globalV.accountsJsonArray.getJSONObject(i).getString("Address")+"\r\n"+
                                        globalV.accountsJsonArray.getJSONObject(i).getString("Note"),
                        };
                        
                        Log.d("Array",Arrays.toString(singleAccount));
                    }
    
                    try {
                        accountId = globalV.accountsJsonArray.getJSONObject( 0).getInt("AccountID");
                        Name = globalV.accountsJsonArray.getJSONObject( 0).getString("FullName");
                        nameNote = globalV.accountsJsonArray.getJSONObject( 0).getString("Note");
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            public void run() {
                                nametext.setText(Name);
                                notetext.setText(nameNote);
                                try {
                                    if(globalV.accountsJsonArray.getJSONObject( 0).getInt("Prepaid") > 0){
                                        setPrepay(String.valueOf(globalV.accountsJsonArray.getJSONObject( 0).getInt("Prepaid")));
                                    }
                                } catch (JSONException e) {DsLogs.writeLog("catch: " + e.toString());
                                    e.printStackTrace();
                                }
                            }
                        });
        
                        if(globalV.enablePrinting && globalV.checkLastYear){
                            getPreparedReceipts(String.valueOf(globalV.accountsJsonArray.getJSONObject( 0).getInt("AccountID")));
                        }
        
                    } catch (JSONException e) {DsLogs.writeLog("catch: " + e.toString());
                        e.printStackTrace();
                    }
                    
                    
                    
                } catch (IOException e) {
                    DsLogs.writeLog("catch: " + e.toString());
                    
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                    DsLogs.writeLog("catch: " + e.toString());
                    
                }
                finished[0] = true;
                
            }
        });
        trd.start();
        trd.join();
        while(!finished[0]){
            System.out.println("Waiting for acount...");
        }
        
    }
    

    
    public void postCardknox(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String response;
                try {
                    URL url = new URL("https://x1.cardknox.com/gateway");
                    HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                    conn.setReadTimeout(10000);
                    conn.setConnectTimeout(15000);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
    
                    Uri.Builder builder = new Uri.Builder()
                            .appendQueryParameter("xKey", "DinnersystemDev_Test_0f67defc754e41ab87979a85")
                            .appendQueryParameter("xVersion", "4.5.8")
                            .appendQueryParameter("xSoftwareName", "test")
                            .appendQueryParameter("xSoftwareVersion", "0.1")
                            .appendQueryParameter("xCommand", "cc:sale")
                            .appendQueryParameter("xAmount", amount)
                            .appendQueryParameter("xMagstripe", cardbuffer);
                    
                    String query = builder.build().getEncodedQuery();
    
                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(os, "UTF-8"));
                    writer.write(query);
                    writer.flush();
                    writer.close();
                    os.close();
    
                    conn.connect();
    
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String decodedString;
                    StringBuilder stringBuilder = new StringBuilder();
                    while ((decodedString = in.readLine()) != null) {
                        stringBuilder.append(decodedString);
                    }
                    in.close();
                    response = stringBuilder.toString();
                    System.out.println("response: " + response);
                
                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        
        
        
    }
    
    

}