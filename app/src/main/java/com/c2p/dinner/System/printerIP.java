package com.c2p.dinner.System;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.OutputStreamWriter;

public class printerIP extends Activity {

    EditText iptext;
    Button saveB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printer_ip);

        iptext = findViewById(R.id.pip_ip);
        try{
            iptext.setText(new nonStaticUtilities().getPrinterIp(printerIP.this));
        }catch (Exception e){
            e.printStackTrace();
        }
        saveB = findViewById(R.id.pip_save);

        saveB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(">>>>>>>>>>>>>>","going to save ip");

                saveIP(iptext.getText().toString(),printerIP.this);
                finish();
            }
        });

    }


    public void saveIP(String text, Context cont){
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(cont.openFileOutput("PRINTER_IP", Context.MODE_PRIVATE));
            outputStreamWriter.write(text);
            outputStreamWriter.close();
        }
        catch (Exception e) {
            DsLogs.writeLog("catch: " + e.toString());

            Log.e("Exception", "File write failed: " + e.toString());
        }

    }


}
