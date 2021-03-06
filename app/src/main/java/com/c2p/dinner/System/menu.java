package com.c2p.dinner.System;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class menu extends Activity {

    final int BUTTON_1 = 145;
    final int BUTTON_2 = 146;
    final int BUTTON_3 = 147;
    final int BUTTON_4 = 148;
    final int BUTTON_5 = 149;
    final int BUTTON_6 = 150;
    final int BUTTON_7 = 151;
    final int BUTTON_BACK = 67;
    final int REQ_MULTIPAYMENT = 109;
    TextView opt1;
    TextView opt3, opt5;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    
        Configuration conf = getResources().getConfiguration();
        System.out.println("conf.locale.getLanguage()" + conf.locale.getLanguage());
        if (conf.locale.getLanguage().equals("iw"))
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        else
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        
        setContentView(R.layout.activity_menu);
        
        opt1 = findViewById(R.id.menu_op1);
        opt3 = findViewById(R.id.menu_op3);
        opt5 = findViewById(R.id.menu_op5);

        if (globalV.kioskMode){
           opt1.setEnabled(false);
           opt3.setEnabled(false);//todo check if this even bothers to work
       }

       if(getIntent().getBooleanExtra("CHAA",false)){
            opt5.setBackgroundColor(Color.parseColor("#aee9a4"));
       }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d("key pressed",""+keyCode);
        if(keyCode == BUTTON_1){
            if (!globalV.kioskMode) {
                Intent intent=new Intent();
                intent.putExtra("RES",1);
                setResult(Activity.RESULT_OK,intent);
                finish();
            }

        }
        if(keyCode == BUTTON_2){
            Intent intent=new Intent();
            intent.putExtra("RES",2);
            setResult(Activity.RESULT_OK,intent);
            finish();
        }
        if(keyCode == BUTTON_3){
            if (!globalV.kioskMode) {
                if(globalV.reasonsObjs.size() > 19){
                    Intent Rintent=new Intent(menu.this,searchReason.class);
                    startActivityForResult(Rintent,1);
                }else{
                    Intent Rintent=new Intent(menu.this,reason.class);
                    startActivityForResult(Rintent,1);
                }
            }

        }
        if(keyCode == BUTTON_4){
            Intent intent=new Intent();
            intent.putExtra("RES",4);
            setResult(Activity.RESULT_OK,intent);
            finish();
        }
        if(keyCode == BUTTON_5){
            Intent intent=new Intent();
            intent.putExtra("RES",5);
            setResult(Activity.RESULT_OK,intent);
            finish();
        }
        
        if(keyCode == BUTTON_6){
            Intent Rintent=new Intent(menu.this,CurrencyConverter.class);
            startActivityForResult(Rintent,6);
            finish();
        }
        
        if(keyCode == BUTTON_BACK){
            finish();
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode==1)  // honoree
        {
            if(data.getIntExtra("RES",999)==0){//canceled
                finish();
            }
            if(data.getIntExtra("RES",0)==1){//ok
                Intent intent=new Intent();
                intent.putExtra("RES",3);
                intent.putExtra("REASON", (ReasonObject) data.getSerializableExtra("REASON"));
                setResult(Activity.RESULT_OK,intent);
                finish();
            }else if(data.getIntExtra("RES",0) == 2) {//ok
                Intent intent = new Intent();
                intent.putExtra("RES", 33);
                intent.putExtra("REASON", (ReasonObject) data.getSerializableExtra("REASON"));
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        }

        if(requestCode == REQ_MULTIPAYMENT)
        {
            if(data.getIntExtra("RES",999)==0){//canceled
                finish();
            }

            if(data.getIntExtra("RES",0)==1){//ok
                Intent intent=new Intent();
                intent.putExtra("RES",4);
                intent.putExtra("CHANGE",data.getIntExtra("CHANGE",-1));
                setResult(Activity.RESULT_OK,intent);
                Log.d("in menu",""+data.getIntExtra("MULTI NAME",-1));
                finish();
            }

        }
    }

}
