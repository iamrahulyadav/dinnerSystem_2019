package com.c2p.dinner.System;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;

public class nonStaticUtilities {
    public SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm",Locale.ENGLISH);
    public void saveEventName(String name, Context cont){
        try {
            String var = dateFormat.format(new Date(System.currentTimeMillis()));
            System.out.println(var);
            OutputStreamWriter timeOutputStreamWriter = new OutputStreamWriter(cont.openFileOutput("TIME_REGISTERED", Context.MODE_PRIVATE));
            timeOutputStreamWriter.write(var);
            timeOutputStreamWriter.close();
            
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(cont.openFileOutput("EVENT_NAME", Context.MODE_PRIVATE));
            outputStreamWriter.write(name);
            outputStreamWriter.close();
            
        }
        catch (Exception e) {
            DsLogs.writeLog("catch: " + e.toString());
            
            Log.e("Exception", "File write failed: " + e.toString());
            
        }
        
    }
    
    public void saveEventPin(String name, Context cont){
        try {
            
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(cont.openFileOutput("EVENT_PIN", Context.MODE_PRIVATE));
            outputStreamWriter.write(name);
            outputStreamWriter.close();
            
        }
        catch (Exception e) {
            DsLogs.writeLog("catch: " + e.toString());
            
            Log.e("Exception", "File write failed: " + e.toString());
            
        }
        
    }
    
    String getEventName(Context cont){
        String ret = "No Event, Press menu to register:";
        double diffInHours = 99;
        
        try {
            InputStream inputStream = cont.openFileInput("TIME_REGISTERED");
            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();
                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }
                inputStream.close();
                String timeSaved = stringBuilder.toString();
                Date date1=new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(timeSaved);
                Date date2 = new Date(System.currentTimeMillis());
                System.out.println(date1.toString());
                System.out.println(date2.toString());
                
                int diffInMilli = (int)(date2.getTime() - date1.getTime());
                diffInHours = diffInMilli / (1000 * 60 * 60);
                System.out.println("diffInHours is " + diffInHours);
                System.out.println(timeSaved+"\t"+dateFormat.format(date1));
            }
        }
        catch (FileNotFoundException e) {
            DsLogs.writeLog("catch: " + e.toString());
            
            Log.e("login activity", "File not found: " + e.toString());
        } catch (Exception e) {
            DsLogs.writeLog("catch: " + e.toString());
            
            Log.e("login activity", "Can not read file: " + e.toString());
        }
        
        if(diffInHours < 1000){
            try {
                InputStream inputStream = cont.openFileInput("EVENT_NAME");
                if ( inputStream != null ) {
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String receiveString = "";
                    StringBuilder stringBuilder = new StringBuilder();
                    while ( (receiveString = bufferedReader.readLine()) != null ) {
                        stringBuilder.append(receiveString);
                    }
                    inputStream.close();
                    ret = stringBuilder.toString();
                }
            }
            catch (FileNotFoundException e) {
                DsLogs.writeLog("catch: " + e.toString());
                
                Log.e("login activity", "File not found: " + e.toString());
            } catch (Exception e) {
                DsLogs.writeLog("catch: " + e.toString());
                
                Log.e("login activity", "Can not read file: " + e.toString());
            }
        }else{
            ret = "";
        }
        
        return ret;
    }
    
    String getEventPin(Context cont){
        String ret = "";
            try {
                InputStream inputStream = cont.openFileInput("EVENT_PIN");
                if ( inputStream != null ) {
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String receiveString = "";
                    StringBuilder stringBuilder = new StringBuilder();
                    while ( (receiveString = bufferedReader.readLine()) != null ) {
                        stringBuilder.append(receiveString);
                    }
                    inputStream.close();
                    ret = stringBuilder.toString();
                }
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        
        return ret;
    }
    
    public void saveDeviceName(String name, Context cont){
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(cont.openFileOutput("DEVICE_NAME", Context.MODE_PRIVATE));
            outputStreamWriter.write(name);
            outputStreamWriter.close();
        }
        catch (Exception e) {
            DsLogs.writeLog("catch: " + e.toString());
            
            Log.e("Exception", "File write failed: " + e.toString());
        }
        
    }
    
    public void saveImageToDisk(Bitmap bmp,Context context,String str){
        String path = Environment.getExternalStorageDirectory() + "/download/";
        OutputStream fOutputStream = null;
     //   File file = new File(path + "tickets/", str + ".jpg");
        File file = new File(path, str + ".jpg");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    
        try {
            fOutputStream = new FileOutputStream(file);
    
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fOutputStream);
        
            fOutputStream.flush();
            fOutputStream.close();
        
            MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }
    
    public String getDeviceName(Context cont){
        String ret = "";
        
        try {
            InputStream inputStream = cont.openFileInput("DEVICE_NAME");
            
            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();
                
                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }
                
                inputStream.close();
                ret = stringBuilder.toString();
            }
            
        }
        catch (FileNotFoundException e) {
            DsLogs.writeLog("catch: " + e.toString());
            
            Log.e("login activity", "File not found: " + e.toString());
        } catch (Exception e) {
            DsLogs.writeLog("catch: " + e.toString());
            
            Log.e("login activity", "Can not read file: " + e.toString());
        }
        
        return ret;
    }
    
    public String getPrinterIp(Context cont){
        String ret = "";
        
        try {
            InputStream inputStream = cont.openFileInput("PRINTER_IP");
            
            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();
                
                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }
                
                inputStream.close();
                ret = stringBuilder.toString();
            }
            
        }
        catch (FileNotFoundException e) {
            DsLogs.writeLog("catch: " + e.toString());
            
            Log.e("login activity", "File not found: " + e.toString());
        } catch (Exception e) {
            DsLogs.writeLog("catch: " + e.toString());
            
            Log.e("login activity", "Can not read file: " + e.toString());
        }
        
        return ret;
    }
    
    public String getMyLocalIpAddress() {
        try {
            Enumeration networkInterfaces = NetworkInterface.getNetworkInterfaces();  // gets All networkInterfaces of your device
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface inet = (NetworkInterface) networkInterfaces.nextElement();
                Enumeration address = inet.getInetAddresses();
                while (address.hasMoreElements()) {
                    InetAddress inetAddress = (InetAddress) address.nextElement();
                    if (inetAddress.isSiteLocalAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (Exception e) {DsLogs.writeLog("catch: " + e.toString());
            // Handle Exception
        }
        return "";
    }
    
}
