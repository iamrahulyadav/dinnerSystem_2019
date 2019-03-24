package com.c2p.dinner.System;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class processDialog extends Dialog implements
        android.view.View.OnClickListener {

    public Button yes;
    TextView info;
    TextView titleTv;

    processDialog(Activity a) {
        super(a);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.cust_dialog);
        yes = findViewById(R.id.btn_yes);
        titleTv = findViewById(R.id.dialog_title);
        info = findViewById(R.id.txt_dia);
        yes.setOnClickListener(this);
        this.setCanceledOnTouchOutside(false);

    }

    @Override
    public void onClick(View v) {

        dismiss();
    }
    @Override
    public void dismiss() {
        super.dismiss();
    }

    public void settext(String text){
        info.setText(text);
    }
    
    public void setTitletext(String text){
        titleTv.setText(text);
    }

    public void enableB(){
        yes.setEnabled(true);
    }

}