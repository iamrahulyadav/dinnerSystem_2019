package com.c2p.dinner.System;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import java.math.BigDecimal;
import java.text.NumberFormat;

public class CurrencyConverter extends Activity {
    
    final int BUTTON_BACK =67;
    final int BUTTON_ENTER = 160;
    
    EditText inEditText;
    EditText outEditText;
    
    TextView rateTv;
    
    BigDecimal inAmount = new BigDecimal(0);
    BigDecimal outAmount = new BigDecimal(0);
    
    NumberFormat formatter = NumberFormat.getCurrencyInstance();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    
        setContentView(R.layout.currency_converter);
    
        inEditText = findViewById(R.id.convert_input);
        outEditText = findViewById(R.id.convert_output);
        rateTv = findViewById(R.id.rate_tv);
    
        rateTv.setText("Conversion rate is : " + globalV.conversion);
        
    
        inEditText.addTextChangedListener(new MoneyTextWatcher(inEditText));
    
    
        inEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == BUTTON_ENTER && event.getAction() == KeyEvent.ACTION_DOWN){

                }else if (keyCode == BUTTON_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
//                    if(inEditText.getText().toString().length() > 0){
//                        try{
//                            String str = inEditText.getText().toString();
//                            str = str.replace(",","");
//                            System.out.println(str);
//                            inAmount = new BigDecimal(str);
//
//                            inEditText.setText(formatter.format(inAmount).substring(1));
//
//                            outAmount = inAmount.divide(globalV.conversion , RoundingMode.HALF_EVEN);
//
//                            outEditText.setText(formatter.format(outAmount));
//                        }catch (Exception e){
//                            e.printStackTrace();
//                        }
//                    }
//
                }else{
                    try{
                        String str = inEditText.getText().toString();
                        str = str.replace("$","").replace(",","");
                        System.out.println(str);
                        inAmount = new BigDecimal(str);
                        System.out.println(inAmount);
                        
                        outAmount = inAmount.multiply(globalV.conversion);

                        outEditText.setText(formatter.format(outAmount));
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
                
                return false;
            }
        });
    }
    
}
