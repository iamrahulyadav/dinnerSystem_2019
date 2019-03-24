package com.c2p.dinner.System;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;

public class multipayment extends Activity {

    EditText amounttext, paymentstext, daytext, nowpayingtext;
    TextView eachtext;
    final int BUTTON_ENTER = 160;
    final int BUTTON_BACK =67;
    double amount, each;
    NumberFormat formatter = NumberFormat.getCurrencyInstance();
    DecimalFormat df = new DecimalFormat("#.##");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        setContentView(R.layout.activity_multipayment);
        this.setFinishOnTouchOutside(false);

        amounttext = findViewById(R.id.mp_amount);
        paymentstext = findViewById(R.id.mp_payments);
        nowpayingtext = findViewById(R.id.mp_payingnow);
        eachtext = findViewById(R.id.mp_eachpay);
        daytext = findViewById(R.id.mp_day);

        Calendar cal = Calendar.getInstance();
        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        if(dayOfMonth > 28){dayOfMonth = 28;}
        daytext.setText(""+dayOfMonth);
        daytext.setSelection(daytext.getText().length());

        Intent intent = getIntent();
        if(intent.getStringExtra("AMOUNT").length() > 0){
            amount = Double.parseDouble(intent.getStringExtra("AMOUNT"));
            amounttext.setText(intent.getStringExtra("AMOUNT"));
            amounttext.setSelection(amounttext.getText().length());
        }else {

        }

        amounttext.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == BUTTON_ENTER && event.getAction() == KeyEvent.ACTION_DOWN){
                    if(amounttext.getText().toString().length() <1 || amounttext.getText().toString().matches("\\.")){
                        showpopup("Please enter amount",true);
                        return true;
                    }
                    if(Double.parseDouble(amounttext.getText().toString()) < 0.01){
                        showpopup("Please enter amount",true);
                        return true;
                    }

                    amount = Double.parseDouble(amounttext.getText().toString());
                    //paymentstext.requestFocus();
                    // return true;
                }
                if (keyCode == BUTTON_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
                    if(amounttext.getText().toString().length() <1){
                        Intent intent=new Intent();
                        intent.putExtra("RES",0);
                        setResult(Activity.RESULT_OK,intent);
                        Log.d("in multi pay","going to finish as cancel");
                        finish();
                        return true;
                    }
                }
                return false;
            }
        });

        paymentstext.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == BUTTON_ENTER && event.getAction() == KeyEvent.ACTION_DOWN){
                    if(paymentstext.getText().toString().length() <1){
                        showpopup("Please enter payments!",true);
                        return true;
                    }
                    int payments = Integer.parseInt(paymentstext.getText().toString());
                    each = amount/payments;
                    eachtext.setText(payments+" Paymnts of "+formatter.format(each)+ ", Totaling "+formatter.format(amount)+".\n1st Payment of:"+formatter.format(each)+" will be charged now.");
                    //mppayments+" Paymnts of "+formatter.format(Double.parseDouble(amount))+" each on "+ mpday+"th of the Month, Totaling "
                    //                                    +formatter.format(Double.parseDouble(totalallpayments))+". 1st Payment of:"+formatter.format(Double.parseDouble(mpeach))+" will be charged now."
                    nowpayingtext.setText(df.format(each));
                    nowpayingtext.setSelection(nowpayingtext.getText().length());
                }
                if (keyCode == BUTTON_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
                    if(paymentstext.getText().toString().length() <1){
                        amounttext.requestFocus();
                        eachtext.setText("");
                        return true;
                    }
                }
                return false;
            }
        });

        nowpayingtext.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == BUTTON_ENTER && event.getAction() == KeyEvent.ACTION_DOWN){
                    if(nowpayingtext.getText().toString().length() <1 || amounttext.getText().toString().matches("\\.")){
                        showpopup("Please enter amount to pay now!",true);
                        return true;
                    }
                    if(Double.parseDouble(nowpayingtext.getText().toString()) <0.01 || Double.parseDouble(nowpayingtext.getText().toString()) > amount){
                        showpopup("Please enter amount to pay now!",true);
                        return true;
                    }
                    int payments = Integer.parseInt(paymentstext.getText().toString());
                    each = (amount-Double.parseDouble(nowpayingtext.getText().toString()))/(payments-1);
                    eachtext.setText(payments-1+" Paymnts of "+formatter.format(each)+ ", \n1st Payment of:"+formatter.format(Double.parseDouble(nowpayingtext.getText().toString()))+" will be charged now."+"\nTotaling "+formatter.format(amount)+".");
                }
                if (keyCode == BUTTON_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
                    if(nowpayingtext.getText().toString().length() <1){
                        paymentstext.requestFocus();
                        eachtext.setText("");
                        return true;
                    }
                }
                return false;
            }
        });

        daytext.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == BUTTON_ENTER && event.getAction() == KeyEvent.ACTION_DOWN){
                    if(daytext.getText().toString().length() <1){
                        showpopup("Please enter day of month to charge CC",true);
                        return true;
                    }
                    int day = Integer.parseInt(daytext.getText().toString());
                    if(day < 1 || day > 28){
                        showpopup("Not a valid day!",true);
                        return true;
                    }
                    Intent intent1=new Intent();
                    intent1.putExtra("RES",1);
                    intent1.putExtra("TOTAL",""+amount);
                    intent1.putExtra("AMOUNT",""+nowpayingtext.getText().toString());
                    intent1.putExtra("EACH",""+each);
                    intent1.putExtra("PAYMENTS",paymentstext.getText().toString());
                    intent1.putExtra("DAY",""+day);
                    setResult(Activity.RESULT_OK,intent1);
                    Log.d("in multi pay","going to finish!");
                    finish();
                }
                if (keyCode == BUTTON_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
                    if(daytext.getText().toString().length() <1){
                        nowpayingtext.requestFocus();
                        return true;
                    }
                }
                return false;
            }
        });

    }

    public void showpopup(String str, boolean error){
        Toast toast = Toast.makeText(this, str, Toast.LENGTH_LONG);
        TextView ttv = new TextView(this);
        ttv.setText(str);
        ttv.setTextSize(28);
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


}
