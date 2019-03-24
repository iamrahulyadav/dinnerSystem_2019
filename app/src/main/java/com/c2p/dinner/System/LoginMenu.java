package com.c2p.dinner.System;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.TextView;

public class LoginMenu extends Activity {

    final int BUTTON_1 = 145;
    final int BUTTON_2 = 146;
    final int BUTTON_3 = 147;
    final int BUTTON_4 = 148;
    final int BUTTON_5 = 149;
    final int BUTTON_6 = 150;
    final int BUTTON_7 = 151;
    final int BUTTON_8 = 152;
    final int BUTTON_9 = 153;
    final int BUTTON_BACK = 67;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login_menu);
        
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == BUTTON_1){
            Intent mintent = new Intent(LoginMenu.this,register.class);
            startActivity(mintent);
            finish();

        }
        if(keyCode == BUTTON_2){
            Intent wifiI = new Intent(LoginMenu.this,testingTools.class);
            startActivity(wifiI);
            finish();
        }
        if(keyCode == BUTTON_3){
            globalV.kioskMode = true;
            //todo show toast
            finish();
        }
        if(keyCode == BUTTON_4){
            finishAffinity();
        }
        if(keyCode == BUTTON_5){
            Intent wifiI = new Intent(LoginMenu.this,wifimenual.class);
            startActivity(wifiI);
            finish();
        }
        if(keyCode == BUTTON_6){
            Intent pmintent = new Intent(LoginMenu.this,printmode.class);
            startActivity(pmintent);
            finish();
        }
        if(keyCode == BUTTON_7){
            DsLogs.UploadLogs();
            finish();
        }
        if(keyCode == BUTTON_8){
            Intent DashBoardI = new Intent(LoginMenu.this,DashBoard.class);
            startActivity(DashBoardI);
            finish();
        }
        if(keyCode == BUTTON_9){
            Intent LIntent = new Intent(LoginMenu.this,Language.class);
            startActivity(LIntent);
            finish();
        }
        
        if(keyCode == BUTTON_BACK){
            finish();
        }

        return super.onKeyDown(keyCode, event);
    }

}
