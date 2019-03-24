package com.c2p.dinner.System;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class searchReason extends Activity {
    TextView textView;
    LinearLayout linearLayout;
    String typedStr;
    ImageButton backBtn;
    EditText editText;
    final int BUTTON_BACK =67;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_search);
        
        textView = findViewById(R.id.typed_tv);
        linearLayout = findViewById(R.id.serach_rslts);
        backBtn = findViewById(R.id.back_btn);
        editText = findViewById(R.id.editText);
    
        typedStr = "";
        updateSearchResults(globalV.reasonsObjs);
    
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == BUTTON_BACK  && event.getAction() == KeyEvent.ACTION_DOWN) {
                    finish();
                }
                return  true;
            }
        });
    }
    
    public void onClickBtn(View btn) {
        char c = idToChar(btn.getId());
        if(c == '<'){
            if (typedStr.length() > 0)typedStr = typedStr.substring(0, typedStr.length() - 1);
        }else{
            typedStr += c;
        }
        textView.setText(typedStr);
        
        updateSearchResults(searchName(typedStr));
        
        
    }
    
    private char idToChar(int id){
        char c = 'a';
        
        switch (id) {
            case R.id.button1:      c = 'א';  break;
            case R.id.button2:      c = 'ב';  break;
            case R.id.button3:      c = 'ג';  break;
            case R.id.button4:      c = 'ד';  break;
            case R.id.button5:      c = 'ה';  break;
            case R.id.button6:      c = 'ו';  break;
            case R.id.button7:      c = 'ז';  break;
            case R.id.button8:      c = 'ח';  break;
            case R.id.button9:      c = 'ט';  break;
            case R.id.button10:     c = 'י';  break;
            case R.id.button20:     c = 'כ';  break;
            case R.id.button30:     c = 'ל';  break;
            case R.id.button40:     c = 'מ';  break;
            case R.id.button50:     c = 'נ';  break;
            case R.id.button60:     c = 'ס';  break;
            case R.id.button70:     c = 'ע';  break;
            case R.id.button80:     c = 'פ';  break;
            case R.id.button90:     c = 'צ';  break;
            case R.id.button100:    c = 'ק';  break;
            case R.id.button200:    c = 'ר';  break;
            case R.id.button300:    c = 'ש';  break;
            case R.id.button400:    c = 'ת';  break;
            case R.id.button_spc:   c = ' ';  break;
            case R.id.backspace:   c = '<';  break;
        }
        
        return c;
    }
    
    private void updateSearchResults(ArrayList<ReasonObject> reasonObjects){
        linearLayout.removeAllViews();
        
        for(int i = 0 ; i < reasonObjects.size(); i++){
            final ReasonObject reasonObject = reasonObjects.get(i);
            
            TextView tv = new TextView(searchReason.this);
            tv.setText(reasonObject.name);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP,30);
            tv.setTypeface(tv.getTypeface(), Typeface.BOLD);
            tv.setPadding(0, 15, 0, 15);
            
            tv.setClickable(true);
            
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println(reasonObject.name);
                    
                    Intent intent=new Intent();
                    intent.putExtra("RES",2);
                    intent.putExtra("REASON",reasonObject);  // todo maybe i need to send bake the index
                //    intent.putExtra("REASON_NAME",reasonObject.name);
                    setResult(Activity.RESULT_OK,intent);
                    finish();
                }
            });
            
            linearLayout.addView(tv);
        }
    }
    
    private ArrayList<ReasonObject> searchName(String str){
        ArrayList<ReasonObject> reasonObjects = new ArrayList<>();
        for(int i = 0 ; i < globalV.reasonsObjs.size(); i++){
            if(globalV.reasonsObjs.get(i).name.contains(str)){
                reasonObjects.add(globalV.reasonsObjs.get(i));
            }
        }
        return reasonObjects;
    }
    
}
