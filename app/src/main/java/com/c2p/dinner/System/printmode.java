package com.c2p.dinner.System;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Switch;

public class printmode extends Activity  {

    final int BUTTON_1 = 145;
    final int BUTTON_2 = 146;
    final int BUTTON_3 = 147;
    final int BUTTON_4 = 148;
    final int BUTTON_ENTER = 160;
    Switch enablePrintingSwitch;
    Switch sharePrinterSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printmode);
        
        enablePrintingSwitch = findViewById(R.id.enable_printer);
        sharePrinterSwitch = findViewById(R.id.share_printer);
    
        enablePrintingSwitch.setChecked(globalV.enablePrinting);
        sharePrinterSwitch.setChecked(!globalV.doNotSharePrinter);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == BUTTON_1){
            globalV.enablePrinting = !globalV.enablePrinting;
            enablePrintingSwitch.setChecked(globalV.enablePrinting);
        }
        if(keyCode == BUTTON_2){
            globalV.doNotSharePrinter = !globalV.doNotSharePrinter;
            sharePrinterSwitch.setChecked(!globalV.doNotSharePrinter);
        }
        if(keyCode == BUTTON_3){
            Log.d(">>>>>>>>>>>>>>","");
            Intent inte = new Intent(printmode.this,printerIP.class);
            startActivity(inte);
            finish();
        }
        if(keyCode == BUTTON_4){
            finish();
        }

        return super.onKeyDown(keyCode, event);
    }

}
