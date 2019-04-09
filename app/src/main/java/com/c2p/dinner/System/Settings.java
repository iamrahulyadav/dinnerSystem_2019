package com.c2p.dinner.System;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Switch;

public class Settings extends Activity  {

    final int BUTTON_1 = 145;
    final int BUTTON_2 = 146;
    final int BUTTON_3 = 147;
    final int BUTTON_4 = 148;
    final int BUTTON_ENTER = 160;
    Switch kioskModeSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);
    
        kioskModeSwitch = findViewById(R.id.test_aoutomation);
    
        kioskModeSwitch.setChecked(globalV.kioskMode);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == BUTTON_1){
            globalV.kioskMode = !globalV.kioskMode;
            kioskModeSwitch.setChecked(globalV.kioskMode);
        }
        if(keyCode == BUTTON_2){
            Intent currencyIntent = new Intent(Settings.this,ChooseCurrency.class);
            startActivity(currencyIntent);
            finish();
        }
        if(keyCode == BUTTON_3){
        
        }
        if(keyCode == BUTTON_4){
            finish();
        }

        return super.onKeyDown(keyCode, event);
    }

}
