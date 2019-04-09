package com.c2p.dinner.System;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Window;

import com.mynameismidori.currencypicker.CurrencyPicker;
import com.mynameismidori.currencypicker.CurrencyPickerListener;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ChooseCurrency extends FragmentActivity {
    private SweetAlertDialog pDialog;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //   setContentView(R.layout.test_printing_layout);
        
        pDialog = new SweetAlertDialog(ChooseCurrency.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setContentTextSize(42);
        pDialog.setContentText("Loading");
        pDialog.setCancelable(false);
        
        CurrencyPicker picker = CurrencyPicker.newInstance("Select Currency");  // dialog title
        picker.setListener(new CurrencyPickerListener() {
            @Override
            public void onSelectCurrency(String name, String code, String symbol, int flagDrawableResID) {
                
                System.out.println("name " + name + "\t code " + code + "\t " + symbol);
                globalV.currencySymbol = symbol;
                globalV.currencyCode = code;
                
                pDialog.show();
                
                getExchangeRate(code);
                
            }
        });
        picker.show(getSupportFragmentManager(), "CURRENCY_PICKER");
    
        picker.setCancelable(false);
        
    }
    
    private void getExchangeRate(final String code) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://data.fixer.io/api/latest?access_key=475474f3901c8fca218ec19d08420461&format=1");
                    Log.d("url", url.toString());
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestProperty("Connection", "Close");
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder total = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        total.append(line);
                    }
                    Log.d("getExchangeRate", total.toString());
                    in.close();
                    urlConnection.disconnect();
                    JSONObject jsonobject = new JSONObject(total.toString());
                    JSONObject rates = jsonobject.getJSONObject("rates");
                    
                    double newCurrency = rates.getDouble(code);
                    double USD = rates.getDouble("USD");
                    
                    double newToUsd = USD / newCurrency;
    
                    System.out.println("USD rate is " + USD);
                    System.out.println( code +" rate is " + newCurrency);
                    System.out.println("1 " + code + " = " + newToUsd + " USD");
                    
                    globalV.exchangeRate = newToUsd;
                    
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        public void run() {
    
                            new nonStaticUtilities().saveCurrency(ChooseCurrency.this);
                            
                            pDialog.dismiss();
                            new SweetAlertDialog(ChooseCurrency.this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Currency changed!")
                                    .setContentText("Changed to " + code)
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismiss();
                                            finish();
                                        }
                                    })
                                    .show();
                            
                        }
                    });
                    
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        public void run() {
                            pDialog.dismiss();
                            new SweetAlertDialog(ChooseCurrency.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Oops...")
                                    .setContentText("Something went wrong!")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismiss();
                                            finish();
                                        }
                                    })
                                    .show();
                            
                        }
                    });
                }
            }
        }).start();
    }
}

