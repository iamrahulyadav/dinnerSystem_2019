package com.c2p.dinner.System;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

public class reason extends Activity {

    TextView rtv1, rtv2, rtv3, rtv4, rtv5, rtv6, rtv7, rtv8, rtv9, rtv10, rtv11, rtv12, rtv13, rtv14, rtv15, rtv16, rtv17, rtv18, rtv19, rtv20;
    ArrayList<TextView> tvs = new ArrayList<TextView>();
    EditText et;
    final int BUTTON_PROCCESS = 157;
    final int BUTTON_ENTER = 160;   //66  160 on device
    final int BUTTON_BACK = 67;
    int selected = 0;
    int count = 0;
    ShapeDrawable border = new ShapeDrawable(new RectShape());




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_reason);


        border.getPaint().setStyle(Paint.Style.STROKE);
        border.getPaint().setColor(Color.BLACK);


        rtv1 = findViewById(R.id.rtv1);
        rtv2 = findViewById(R.id.rtv2);
        rtv3 = findViewById(R.id.rtv3);
        rtv4 = findViewById(R.id.rtv4);
        rtv5 = findViewById(R.id.rtv5);
        rtv6 = findViewById(R.id.rtv6);
        rtv7 = findViewById(R.id.rtv7);
        rtv8 = findViewById(R.id.rtv8);
        rtv9 = findViewById(R.id.rtv9);
        rtv10 = findViewById(R.id.rtv10);
        rtv11 = findViewById(R.id.rtv11);
        rtv12 = findViewById(R.id.rtv12);
        rtv13 = findViewById(R.id.rtv13);
        rtv14 = findViewById(R.id.rtv14);
        rtv15 = findViewById(R.id.rtv15);
        rtv16 = findViewById(R.id.rtv16);
        rtv17 = findViewById(R.id.rtv17);
        rtv18 = findViewById(R.id.rtv18);
        rtv19 = findViewById(R.id.rtv19);
        rtv20 = findViewById(R.id.rtv20);

        tvs.add(rtv1);
        tvs.add(rtv2);
        tvs.add(rtv3);
        tvs.add(rtv4);
        tvs.add(rtv5);
        tvs.add(rtv6);
        tvs.add(rtv7);
        tvs.add(rtv8);
        tvs.add(rtv9);
        tvs.add(rtv10);
        tvs.add(rtv11);
        tvs.add(rtv12);
        tvs.add(rtv13);
        tvs.add(rtv14);
        tvs.add(rtv15);
        tvs.add(rtv16);
        tvs.add(rtv17);
        tvs.add(rtv18);
        tvs.add(rtv19);
        tvs.add(rtv20);

        for (TextView tv:tvs) {
            tv.setEllipsize(TextUtils.TruncateAt.END);
        }
        list();



        et = findViewById(R.id.etreason);
        et.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                String text = et.getText().toString();
                if(keyCode == BUTTON_BACK){
                    Intent intent=new Intent();
                    intent.putExtra("RES",0);
                    setResult(Activity.RESULT_OK,intent);
                    finish();

                }else if(keyCode == BUTTON_PROCCESS){
                    Intent intent=new Intent();
                    intent.putExtra("RES",1);
                    intent.putExtra("REASON",selected-1);
                    setResult(Activity.RESULT_OK,intent);
                    finish();

                }else if(event.getAction() == KeyEvent.ACTION_UP){
                    String regex = "[0-9]+";
                    if (text.matches(regex)){
                        deselect();
                        int selection = Integer.parseInt(text);
                        Log.d("selection",""+selection);
                        if(selection <= count && selection != 0){
                            selected = selection;
                            tvs.get(selected-1).setBackgroundResource(R.drawable.border_green);

                        }else{et.setText("");}
                        //et.setText("");
                    }else{
                        et.setText("");
                    }

                }
                return false;
            }
        });
    }


    public void list(){
        ArrayList<String> reasons = globalV.reasons;
       
        for (int i =0; i < reasons.size();i++) {
            count ++;
            if(i == 20){break;}
            tvs.get(i).setText(""+(i+1)+". "+reasons.get(i));
            tvs.get(i).setPadding(10,0,10,0);
            tvs.get(i).setTextSize(28.0f);
            tvs.get(i).setBackground(border);
        }
    }

    public void deselect(){
        for(TextView tv2:tvs){
            tv2.setBackground(border);
        }
        selected = 0;

    }



}
