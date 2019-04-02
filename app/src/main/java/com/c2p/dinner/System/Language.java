package com.c2p.dinner.System;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Switch;
import android.widget.Toast;

import java.util.Locale;

public class Language extends Activity  {

    final int BUTTON_1 = 145;
    final int BUTTON_2 = 146;
    final int BUTTON_3 = 147;
    final int BUTTON_4 = 148;
    final int BUTTON_ENTER = 160;
    
    Locale locale = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_language);
        
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == BUTTON_1){  //english
            locale = new Locale("en_US");
            new nonStaticUtilities().saveLanguage("en_US",this);
        }
        
        if(keyCode == BUTTON_2){   //yiddish
            locale = new Locale("iw","IL");
            new nonStaticUtilities().saveLanguage("iw_IL",this);
        }
        
    
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = locale;
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(this, LoginActivity.class);
        startActivity(refresh);
        finish();
        
    //    return super.onKeyDown(keyCode, event);
        return true;
    }

}
