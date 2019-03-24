package com.c2p.dinner.System;

import android.app.Activity;
import android.net.DhcpInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.ConsoleMessage;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

public class Router_WebView extends Activity {
    Button imgBtn;
    WebView myWebView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.webview_router);
        myWebView = findViewById(R.id.webview_1);
        imgBtn = findViewById(R.id.button_back);
//        WebSettings webSettings = myWebView.getSettings();
//        webSettings.setJavaScriptEnabled(true);
//        webSettings.setDomStorageEnabled(true);
   //     myWebView.setWebViewClient(new MyWebViewClient());
        
    
        imgBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        
//        myWebView.setWebViewClient(new WebViewClient() {
//            @Override
//            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
//                handler.proceed(); // Ignore SSL certificate errors
//            }
//        });
    
//        final String js = "javascript:document.getElementsByName('username').value = 'admin'";
//        myWebView.setWebViewClient(new WebViewClient(){
//
//            public void onPageFinished(WebView view, String url){
//                if (Uri.parse(url).getPath().equals("/cgi-bin/MANGA/index.cgi")) {
//                    view.evaluateJavascript(js, new ValueCallback<String>() {
//                        @Override
//                        public void onReceiveValue(String s) {
//                            System.out.println("in on recieved  " + s );
//                        }
//                    });
//                }
//
//
//            }
//        });
    
        
        myWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                System.out.println(Uri.parse(url).getHost());
            //    if (Uri.parse(url).getPath() != null && Uri.parse(url).getPath().equals("/cgi-bin/MANGA/index.cgi")) {
                if (Uri.parse(url).getHost() != null && Uri.parse(url).getHost().length() > 2) {
                    myWebView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("Trying to log in");
                       //     myWebView.evaluateJavascript("javascript:document.getElementsByName('username').value = 'admin';", null);
    
                            myWebView.evaluateJavascript("javascript: { document.getElementsByName('q').value = 'admin'; };", new ValueCallback<String>() {
                                @Override
                                public void onReceiveValue(String s) {
                                    Log.d("LogName", s); // Prints: "this"
                                }
                            });
    
//                            String str = "var inputs=document.getElementsByTagName(\"input\");    //look for all inputs\n" +
//                                    "\n" +
//                                    "      for(var i=0;i<inputs.length;i++){{    //for each input on document\n" +
//                                    "\n" +
//                                    "            var input=inputs[i];     //look at whatever input\n" +
//                                    "\n" +
//                                    "            if(input.type==\"password\"){\n" +
//                                    "                    {input.value='123451234'}\n" +
//                                    "            }\n" +
//                                    "            if(input.type==\"text\"){\n" +
//                                    "                    {input.value='admin'}\n" +
//                                    "            }\n" +
//                                    "       }};\n" +
//                                    "undefined;";
//                            myWebView.loadUrl(str);
                            
                  //          myWebView.loadUrl("javascript:document.getElementsByName('username').value = 'admin';");
//                            myWebView.loadUrl("javascript:document.getElementsByName('username').value = 'admin'");
//                            myWebView.loadUrl("javascript:document.getElementsByName('password').value = '12341234'");
       //                     myWebView.loadUrl("javascript:document.forms['login_form'].submit_action()");
                        }
                    }, 1000);


                }
                System.out.println("finished loading");
                // Check here if url is equal to your site URL.
            }
        });
    
     //   myWebView.setWebViewClient(new WebViewClient());
    
    
        WebView.setWebContentsDebuggingEnabled(true);
        myWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.d("MyApplication", consoleMessage.message() + " -- From line "
                        + consoleMessage.lineNumber() + " of "
                        + consoleMessage.sourceId());
                return super.onConsoleMessage(consoleMessage);
            }
        });
        
        final WifiManager manager = (WifiManager) super.getApplicationContext().getSystemService(WIFI_SERVICE);
        final DhcpInfo dhcp = manager.getDhcpInfo();
        final String address = Formatter.formatIpAddress(dhcp.gateway);
        
        myWebView.postDelayed(new Runnable() {
            @Override
            public void run() {
                myWebView.measure(100, 100);
                myWebView.getSettings().setUseWideViewPort(true);
                myWebView.getSettings().setLoadWithOverviewMode(false);
                myWebView.getSettings().setJavaScriptEnabled(true);
                myWebView.getSettings().setDomStorageEnabled(true);
                myWebView.getSettings().setAllowContentAccess(true);
                myWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

                
                System.out.println("http://"+ address +"/");
                
                myWebView.loadUrl("http://"+ address +"/");   //     http://192.168.100.1/
            }
        }, 1000);
        
     //   myWebView.loadUrl("http://speedtest.xfinity.com/");
     //   myWebView.loadUrl("http://speedtest.xfinity.com/");   // http://192.168.100.1
        //   myWebView.loadUrl("192.168.100.1");
    
//        final WifiManager manager = (WifiManager) super.getApplicationContext().getSystemService(WIFI_SERVICE);
//        final DhcpInfo dhcp = manager.getDhcpInfo();
//        final String address = Formatter.formatIpAddress(dhcp.gateway);
//        System.out.println(address);
      
      //  myWebView.loadUrl(address);
    }
    
}

class MyWebViewClient extends WebViewClient {
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
//        if (Uri.parse(url).getHost().equals("http://speedtest.xfinity.com/")) {
//            // This is my website, so do not override; let my WebView load the page
//            return false;
//        }
        // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
//        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//        startActivity(intent);
        return true;
    }
}
