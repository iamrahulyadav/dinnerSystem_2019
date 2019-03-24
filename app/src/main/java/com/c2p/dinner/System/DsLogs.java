package com.c2p.dinner.System;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

public class DsLogs {
    
    
    public static void writeLog(final String str){
    
        new Thread( new Runnable() {
            @Override
            public void run() {
                TimeZone timeZone = TimeZone.getTimeZone("America/New_York");
                Calendar calendar = Calendar.getInstance(timeZone);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);
                simpleDateFormat.setTimeZone(timeZone);
                String time = simpleDateFormat.format(calendar.getTime());

                String PATH = Environment.getExternalStorageDirectory() + "/download/";
                File logFile = new File(PATH + "DsLogs.txt");
                if (!logFile.exists()){
                    try{
                        logFile.createNewFile();
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                }

                try{
                    BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
                    buf.append(time +": "+str);
                    buf.newLine();
                    buf.close();
                //    System.out.println(logFile.getPath());
                    System.out.println("writen to file "+ time +": "+str);
                }
                catch (IOException  e){
                    DsLogs.writeLog("catch: " + e.toString());
                    e.printStackTrace();
                }

                if ((SystemClock.uptimeMillis() - globalV.Last_Log_Update) > (1000 * 60 * 20)) {
                    UploadLogs();
                    globalV.Last_Log_Update = SystemClock.uptimeMillis();
                }


            }
        }).start();
    
        
    }
    
    public  static void UploadLogs(){
        new Thread( new Runnable() {
            @Override
            public void run() {
                String PATH = Environment.getExternalStorageDirectory() + "/download/";
                File logFile = new File(PATH + "DsLogs.txt");
                if (!logFile.exists()){
                    System.out.println("File not exist");
                }else{
                    TimeZone timeZone = TimeZone.getTimeZone("America/New_York");
                    Calendar calendar = Calendar.getInstance(timeZone);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh_mm^ss^SSS", Locale.US);
                    simpleDateFormat.setTimeZone(timeZone);
                    String time = simpleDateFormat.format(calendar.getTime());
    
                    try        {
                        System.out.println("Uploading logs");
                        FTPClient ftpClient = new FTPClient();
                        ftpClient.connect("devicelogs.c2p.group",37387);
        
                        if (ftpClient.login("DeviceLogs", "d3v1cElO}$c2Pp-D"))
                        {
                            ftpClient.enterLocalPassiveMode(); // important!
                            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                            String data = PATH + "DsLogs.txt";
            
                            FileInputStream in = new FileInputStream(new File(data));
                            boolean result = ftpClient.storeFile("/DsLogs "+ time+ " # " + readGuid() +".txt", in);
                            in.close();
                            if(result){
                                logFile.delete();
                            }
                            System.out.println(result);
                            if (result) Log.d("upload result", "succeeded");
                            ftpClient.logout();
                            ftpClient.disconnect();
                        }
                    }
                    catch (Exception e)
                    {
                        DsLogs.writeLog("catch: " + e.toString());
                        e.printStackTrace();
                    }
                }
                
            }
        }).start();
        
    }
   
    
    public static String readLogs(){
        
        String PATH = Environment.getExternalStorageDirectory() + "/download/";
        File logFile = new File(PATH + "DsLogs.txt");
        if (!logFile.exists()){
            return"File does not exist.";
        }else{
            String str = null;
            FileInputStream stream = null;
            try {
                stream = new FileInputStream(logFile);
                FileChannel fc = stream.getChannel();
                MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
                str = Charset.defaultCharset().decode(bb).toString();
            } catch (IOException e) {
                DsLogs.writeLog("catch: " + e.toString());
                e.printStackTrace();
            } finally {
                try {
                    stream.close();
                } catch (IOException e) {
                    DsLogs.writeLog("catch: " + e.toString());
                    e.printStackTrace();
                }
            }
            return str;
        }
        
    }
    
    public static String readGuid(){
        
        String PATH = Environment.getExternalStorageDirectory() + "/download/";
        File logFile = new File(PATH + "log.txt");
        if (!logFile.exists()){
            return" guid_NULL";
        }else{
            String str = null;
            FileInputStream stream = null;
            try {
                stream = new FileInputStream(logFile);
                FileChannel fc = stream.getChannel();
                MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
                str = Charset.defaultCharset().decode(bb).toString();
            } catch (IOException e) {
                DsLogs.writeLog("catch: " + e.toString());
                e.printStackTrace();
            } finally {
                try {
                    stream.close();
                } catch (IOException e) {
                    DsLogs.writeLog("catch: " + e.toString());
                    e.printStackTrace();
                }
            }
    
            JSONObject json = null;
            try {
                json = new JSONObject(str);
            } catch (JSONException e) {
                DsLogs.writeLog("catch: " + e.toString());
                e.printStackTrace();
            }
    
            try {
                str = json.getString("GUID");
            } catch (JSONException e) {
                DsLogs.writeLog("catch: " + e.toString());
                e.printStackTrace();
            }
    
            return str;
        }
        
    }
    
    
    
}
