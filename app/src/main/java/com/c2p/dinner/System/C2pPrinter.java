package com.c2p.dinner.System;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageView;

import com.epson.epos2.Epos2Exception;
import com.epson.epos2.printer.Printer;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;

class C2pPrinter {
    
    private final int SET_ACTIVE = 1;
    private final int SET_NOT_ACTIVE = 0;
    private final int CHECK_STATUS = 2;
    
    private Printer myPrinter = null;
    private static int[] nullCount = new int[50];
    private static boolean[] isFileReady = new boolean[50];
    private long startTime;
    
    void initPrinter(Context context) {
        try {
            myPrinter = new Printer(Printer.TM_T88, Printer.MODEL_ANK, context);
        } catch (Epos2Exception e) {
            e.printStackTrace();
        }
    }
    
    void connectPrinter(final String mac, final int timeOut, ImageView printerImg, final processDialog pd, final Context context) {
        if(myPrinter == null){
            initPrinter(context);
        }
        if (myPrinter.getStatus().getConnection() == 0) {
            
            if(!globalV.doNotSharePrinter){
                PrinterAvailibility(SET_ACTIVE);
            }
            
            int tries = 0;
            while (myPrinter.getStatus().getConnection() == 0 && tries < (timeOut + 1)) {
                System.out.println("trying to connect to myPrinter:" + mac);
                tries++;
                try {
                    myPrinter.connect("TCP:" + mac, 3000);
                } catch (Exception e) {
                    DsLogs.writeLog("catch: " + e.toString() + "\ngetErrorStatus():" + myPrinter.getStatus().getErrorStatus());
                    System.out.println("connect getErrorStatus():" + myPrinter.getStatus().getErrorStatus());
                    if (tries == timeOut)
                        DsLogs.writeLog("Timed out connecting to myPrinter.");
                }
                try {
                    Thread.sleep(500);
                } catch (Exception ignored) {
                
                }
            }
            if (myPrinter.getStatus().getConnection() == 1) {
                System.out.println("myPrinter connected " + (System.currentTimeMillis() - globalV.logTime));
                DsLogs.writeLog("#Printer connected " + (System.currentTimeMillis() - globalV.logTime));
                updateImg(printerImg,"#00000000");
                globalV.printConnected = true;
            } else {
                globalV.printConnected = false;
                updateImg(printerImg,"#65FFFFFF");
                if(!globalV.doNotSharePrinter){
                    PrinterAvailibility(SET_NOT_ACTIVE);
                }
            }
            
        }
    }
    
    boolean printExpress(String[] names, String[] barCodes,ImageView printerImg, final processDialog pd, final Context context) throws Exception {
        System.out.println("myPrinter: i got " + names.length + " images");
        DsLogs.writeLog("myPrinter: i got " + names.length + " images");
        Bitmap[] reciptData = getHttpImagesSingleThread(names, context, pd);
        
        updatePD(pd, context.getString(R.string.Creating_Receipts));
        DsLogs.writeLog("#Printer Finished downloading files ");
        if(!globalV.printConnected){
            updatePD(pd, context.getString(R.string.waiting_for_printer));
            if(!globalV.doNotSharePrinter){
                while(!PrinterAvailibility(CHECK_STATUS)){
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException ignored) { }
        
                    if ((System.currentTimeMillis() - startTime) > 31000) {
                        break;
                    }
                }
            }
            
            connectPrinter(globalV.printerMAC, 15, printerImg,pd,context);
        }
        
        if (myPrinter.getStatus().getConnection() == 1) {
            updatePD(pd, context.getString(R.string.Sending_to_Printer));
            
            for (int i = 0; i < reciptData.length; i++) {
                try {
                    if (reciptData[i] == null) {
                        myPrinter.addText("Missing Document");
                        DsLogs.writeLog("txt Missing Document " + i);
                    } else {
                        myPrinter.addImage(reciptData[i], 0, 0, reciptData[i].getWidth(), reciptData[i].getHeight(), Printer.PARAM_DEFAULT, Printer.PARAM_DEFAULT, Printer.HALFTONE_DITHER, Printer.PARAM_DEFAULT, Printer.COMPRESS_AUTO);
//                        reciptData[i].recycle();
//                        reciptData[i] = null;
                    }
                    if (barCodes[i].length() > 1)
                        myPrinter.addBarcode(barCodes[i], Printer.BARCODE_CODE93, Printer.HRI_NONE, Printer.FONT_A, Printer.PARAM_UNSPECIFIED, 60);
                    
                    
                } catch (Exception e) {
                    DsLogs.writeLog("catch: " + e.toString());
                    e.printStackTrace();
                }
                //  myPrinter.addFeedLine(2);
                myPrinter.addCut(Printer.CUT_FEED);
                myPrinter.beginTransaction();
                myPrinter.sendData(Printer.PARAM_DEFAULT);
                myPrinter.endTransaction();
                myPrinter.clearCommandBuffer();
                System.out.println("finished printing file" + i);
            }
            
            System.out.println("finished printing" + (System.currentTimeMillis() - globalV.logTime));
            updatePD(pd, context.getString(R.string.Thank_You));
        } else {
            updatePD(pd, context.getString(R.string.Error_Printing));
        }
        
        myPrinter.clearCommandBuffer();
        
        // save images to disk
//        for(int i = 0; i < reciptData.length; i ++){
//            new nonStaticUtilities().saveImageToDisk(reciptData[i], context, names[i]);
//        }
        
        return true;
    }
    
    private Bitmap[] getHttpImagesSingleThread(final String[] names, final Context context, final processDialog pd) {
        String url = globalV.recieptsHttpUrl;
        Bitmap[] images = new Bitmap[names.length];
        int success =0;
        int attempts = 0;
    
        for (int i = 0; i < names.length; i++) {
            nullCount[i] = 0;
            isFileReady[i] = false;
        }
    
        while (success < names.length && attempts < 50) {
            for (int i = 0; i < names.length; i++) {
                if (images[i] == null) {
                    images[i] = httpGetImage(url, names[i], i, context);
                    if (images[i] != null) {
                        if (images[i].getHeight() < images[i].getWidth()) {
                            images[i] = RotateBitmap(images[i], 90);
                        }
                        success++;
                        updatePD(pd, context.getString(R.string.Processed) + success + context.getString(R.string.Receipt));
                    }
                }
                attempts++;
            }
            try { Thread.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }
        }
        return images;
    }
    
    private Bitmap httpGetImage(final String urlStr, final String fileName, final int i, Context context) {
        Bitmap img = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(urlStr + fileName);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Connection","Close");
            Log.d("Shimon#################", url.toString() + "  ResponseCode: " + String.valueOf(urlConnection.getResponseCode()) + " " + urlConnection.getResponseMessage());
            if (urlConnection.getResponseCode() == 200) {
                if (isFileReady[i]) {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    img = BitmapFactory.decodeStream(in);
                    in.close();
                    if (img == null) {
                        nullCount[i]++;
                        DsLogs.writeLog("Respond is 200 and null count is " + nullCount[i] + "for" + urlStr + fileName);
                        if (nullCount[i] > 15) {
                            img = tryFtp(fileName);
                            if (img == null) {
                                img = BitmapFactory.decodeResource(context.getResources(), R.drawable.failed_document);
                                DsLogs.writeLog("image Missing Document " + urlStr + fileName);
                            }
                        }
                    } else { // image is not null
                        DsLogs.writeLog("File downloaded " + urlStr + fileName);
                    }
                    
                }
                isFileReady[i] = true;
            }
        } catch (Exception e) {
            DsLogs.writeLog("catch: httpGetImage  for File " + urlStr + fileName + "   " + e.toString());
            //    e.printStackTrace();
        } finally{
            if(urlConnection != null)
                urlConnection.disconnect();
        }
        return img;
    }
    
    private Bitmap tryFtp(final String fileName) {
        DsLogs.writeLog("C2pPrinter: downloading files via FTP  fileName :" + fileName);
        Bitmap img = null;
        try {
            FTPClient ftp = new FTPClient();
            ftp.connect(InetAddress.getByName(globalV.PrintServerFTP), Integer.parseInt(globalV.PrintServerPort));
            System.out.println("logged in " + ftp.login(globalV.FTPUser, globalV.FTPPass));
            ftp.changeWorkingDirectory(globalV.EventPIN);
            ftp.enterLocalPassiveMode();
            ftp.setFileType(FTP.BINARY_FILE_TYPE);
            
            
            //get output stream
            String remoteFilePath = fileName;
            InputStream inStream = ftp.retrieveFileStream(remoteFilePath);
            img = BitmapFactory.decodeStream(inStream);
            DsLogs.writeLog(fileName + " FTP File size is " + img.getByteCount());
            ftp.logout();
            ftp.disconnect();
            
        } catch (Exception ex) {
            DsLogs.writeLog("catch: tryFtp" + ex.toString());
            ex.printStackTrace();
        }
        return img;
    }
    
    private Bitmap RotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }
    
    private void updatePD(final processDialog pd, final String str) {
        try{
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                public void run() {
                    if (pd != null) pd.settext(str);
                }
            });
        }catch(Exception e){
            e.printStackTrace();
        }
        
    }
    
    private void updateImg(final ImageView printerImg, final String color) {
        try{
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                public void run() {
                    if (printerImg != null) printerImg.setImageTintList(ColorStateList.valueOf(Color.parseColor(color)));
                }
            });
        }catch(Exception e){
            e.printStackTrace();
        }
        
    }
    
    boolean disconnectPrinter(ImageView printerImg) {
        boolean disconnected = false;
        DsLogs.writeLog("Disconnecting Printer ");
        try {
            int i = 0;
            while (myPrinter.getStatus().getConnection() == 1 && i < 20) {
                i++;
                try {
                    myPrinter.clearCommandBuffer();
                    myPrinter.disconnect();
                } catch (Epos2Exception e) {
                    DsLogs.writeLog("catch: " + e.toString());
                }
                try {
                    Thread.sleep(300);
                } catch (InterruptedException ignored) {
                
                }
            }
            if (i < 20) {
                disconnected = true;
                globalV.printConnected = false;
                System.out.println("printer disconnected");
                updateImg(printerImg,"#65FFFFFF");
            } else {
                System.out.println("failed disconnecting printer");
            }
        } catch (Exception e) {
            DsLogs.writeLog("catch: " + e.toString());
            System.out.println("disconnect getErrorStatus():" + myPrinter.getStatus().getErrorStatus());
        }
        if(!globalV.doNotSharePrinter){
            PrinterAvailibility(SET_NOT_ACTIVE);
        }
        
        return disconnected;
    }
    
    boolean testPrinter(final String mac,final Context context, final processDialog pd, final ImageView printerImg) {
        boolean success = false;
        try{
            updatePD(pd, context.getString(R.string.Trying_to_connect_to) + "\n \r " + mac);
            //   while (myPrinter.getStatus().getConnection() == 0) {
            try {
                myPrinter.connect("TCP:" + mac, 3000);
                success = true;
            } catch (Exception e) {
                e.printStackTrace();
                success = false;
            }
            //    }
            if (success) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    public void run() {
                        if (pd != null) pd.enableB();
                    }
                });
                try {
                    myPrinter.addFeedLine(2);
                    myPrinter.beginTransaction();
                    myPrinter.sendData(Printer.PARAM_DEFAULT);
                    myPrinter.endTransaction();
                    myPrinter.clearCommandBuffer();
                } catch (Epos2Exception e) {
                    e.printStackTrace();
                }
                updatePD(pd, context.getString(R.string.Successful_connected_to) + mac);
        
                disconnectPrinter(printerImg);
            } else {
                updatePD(pd, context.getString(R.string.Failed_to_connect_to) + mac);
            }
            
        }catch (Exception e){
            e.printStackTrace();
        }
        return success;
    }
    
    boolean PrinterAvailibility(int i){
        boolean PrinterIsAvailable = true;
        try {
            URL url = new URL("https://dinnersystemapp.azurewebsites.net/api/values/DeviceLogin/"+ globalV.deviceMac +"/printer/"+ i);
            Log.d("url",url.toString());
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Connection","Close");
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                total.append(line);
            }
            Log.d("total",total.toString());
            in.close();
            urlConnection.disconnect();
            JSONObject availabilityJson = new JSONArray(total.toString()).getJSONObject(0);
            System.out.println("availabilityJson.getString(\"Active\").contains(\"True\") " + availabilityJson.getString("Active").contains("True"));
            if(availabilityJson.getString("Active").contains("True")){
                PrinterIsAvailable = false;
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return PrinterIsAvailable;
    }
    
    private void printList(JSONArray jsonArray) throws Epos2Exception {
        for(int i = 0; i < jsonArray.length(); i++){
            myPrinter.addTextAlign(Printer.ALIGN_LEFT);
            myPrinter.addText("******");
            myPrinter.addTextAlign(Printer.ALIGN_RIGHT);
            myPrinter.addText("******");
        }
    }
    
    private void printListTest() throws Epos2Exception {
        for(int i = 0; i < 20; i++){
            myPrinter.addTextAlign(Printer.ALIGN_LEFT);
            myPrinter.addText("******");
            myPrinter.addTextAlign(Printer.ALIGN_RIGHT);
            myPrinter.addText("******\n");
        }
    
        myPrinter.addCut(Printer.CUT_FEED);
        myPrinter.beginTransaction();
        myPrinter.sendData(Printer.PARAM_DEFAULT);
        myPrinter.endTransaction();
        myPrinter.clearCommandBuffer();
    }
}
