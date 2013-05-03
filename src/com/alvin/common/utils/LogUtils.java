package com.alvin.common.utils;

import com.alvin.api.config.Env;

import android.os.Environment;
import android.text.format.Time;

import java.io.File;
import java.io.FileOutputStream;

public class LogUtils {
    //PUBLISH CHECK: 发布的时候，一定要关闭调试模式
    private final static boolean debugMode = false;
    private final static String logDir = "pcgroup/" + Env.client + "/log";
        
    public static void printLog(String msg){
        if(debugMode){
            System.out.println(msg);
        }
    }
    
    public static void writeLog(String msg){
        if(debugMode){
            try{
                if(!Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
                    return;
                }
                File logFileDir = new File(Environment.getExternalStorageDirectory(),logDir);
                if(!logFileDir.exists()){
                    logFileDir.mkdirs();
                }
                
                File logFile = new File(logFileDir.getPath()+"/log.txt");
                if(!logFile.exists()){
                    logFile.createNewFile();
                }
                
                FileOutputStream outputStream = null;
                try {
                    outputStream = new FileOutputStream(logFile,true);
                    outputStream.write((getCurrentTime()+":"+msg+"\r\n").getBytes());
                    System.out.println(getCurrentTime()+":"+msg);
                } finally {
                    if(outputStream != null) {
                        outputStream.close();
                    }
                }
            }catch(Exception e){
                System.out.println("write log error.");
            }
        }
    }
    
    private static String getCurrentTime(){
        Time time = new Time();
        time.setToNow();
        return time.format("%m-%d %H:%M:%S");
    }
}
