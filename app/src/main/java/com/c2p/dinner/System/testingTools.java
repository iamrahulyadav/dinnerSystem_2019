package com.c2p.dinner.System;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Switch;

public class testingTools extends Activity  {

    final int BUTTON_1 = 145;
    final int BUTTON_2 = 146;
    final int BUTTON_3 = 147;
    final int BUTTON_4 = 148;
    final int BUTTON_ENTER = 160;
    Switch testAutomation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_tools);
    
        testAutomation = findViewById(R.id.test_aoutomation);
    
        testAutomation.setChecked(globalV.testAutomation);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == BUTTON_1){
            globalV.testAutomation = !globalV.testAutomation;
            testAutomation.setChecked(globalV.enablePrinting);
        }
        if(keyCode == BUTTON_2){
            Intent routerIntent = new Intent(testingTools.this,SpeedTest.class);
            startActivity(routerIntent);
            finish();
        }
        if(keyCode == BUTTON_3){
            Intent routerIntent = new Intent(testingTools.this,Router_WebView.class);
            startActivity(routerIntent);
            finish();
        }
        if(keyCode == BUTTON_4){
            finish();
        }

        return super.onKeyDown(keyCode, event);
    }

}
