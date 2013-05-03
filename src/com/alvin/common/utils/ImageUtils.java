package com.alvin.common.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * 图片工具类
 * @author user
 *
 */
public class ImageUtils {
    
    /**
     * 根据输入流获取bitmap
     * @param inputStream
     * @return
     */
    public static Bitmap getBitmap(InputStream inputStream,int requiredWith,int requiredHeight){
        Bitmap bitmap = null;
        BitmapFactory.Options bfOptions=new BitmapFactory.Options();
        bfOptions.inJustDecodeBounds = true; 
        bfOptions.inDither=false;      
        bfOptions.inPurgeable=true; 
        byte[] datas = getBytes(inputStream);
        try{
            if(null!=datas && datas.length>0){
                BitmapFactory.decodeByteArray(datas, 0, datas.length, bfOptions);
                final int REQUIRED_WIDTH=requiredWith;  
                final int REQUIRED_HEIGHT=requiredHeight;
                int width_tmp=bfOptions.outWidth;
                int height_tmp=bfOptions.outHeight;       
                int scale=1;
                while(true){      
                    if(width_tmp<REQUIRED_WIDTH || height_tmp<REQUIRED_HEIGHT)
                        break;             
                    width_tmp/=2;           
                    height_tmp/=2;          
                    scale*=2;       
                }   
                BitmapFactory.Options option = new BitmapFactory.Options();
                option.inJustDecodeBounds = false; 
                option.inSampleSize=scale;
                option.inDither=false;      
                option.inPurgeable=true; 
                option.inTempStorage=new byte[16 * 1024];
           
                bitmap = BitmapFactory.decodeByteArray(datas, 0, datas.length, option);
            }
        }catch(Exception e){
            e.printStackTrace();
        } finally{
            if(inputStream!=null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bitmap;
    }
    
    /**
     * 根据文件路径获取 bitmap
     * @param path
     * @return
     */
    public static Bitmap getBitmapByFilePath(String path,int requiredWith,int requiredHeight){
        Bitmap bitmap = null;
        BitmapFactory.Options bfOptions=new BitmapFactory.Options();
        bfOptions.inJustDecodeBounds = true; 
        bfOptions.inDither=false;      
        bfOptions.inPurgeable=true; 
        File file = new File(path);
        FileInputStream fis=null;
        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if(fis!=null){
            try {
                BitmapFactory.decodeFile(path, bfOptions);
                final int REQUIRED_WIDTH=requiredWith;  
                final int REQUIRED_HEIGHT=requiredHeight;   
                int width_tmp=bfOptions.outWidth;
                int height_tmp=bfOptions.outHeight;       
                int scale=1;
                while(true){      
                    if(width_tmp<REQUIRED_WIDTH || height_tmp<REQUIRED_HEIGHT)
                        break;             
                    width_tmp/=2;           
                    height_tmp/=2;          
                    scale*=2;       
                }   
                
                BitmapFactory.Options o2 = new BitmapFactory.Options(); 
                o2.inJustDecodeBounds = false;  
                o2.inSampleSize=scale;
                o2.inDither=false;      
                o2.inPurgeable=true; 
                o2.inTempStorage=new byte[16 * 1024];
                
                bitmap = BitmapFactory.decodeStream(fis, null, o2);
            }finally{
                if(fis!=null){
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return bitmap;
    }
    
    public static byte[] getBytes(InputStream inStream){
        if(inStream==null){
            return null;
        }
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = -1;
        try {
            while((len = inStream.read(buffer)) !=-1){
                outStream.write(buffer, 0, len);
                outStream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outStream.toByteArray();
    }
}   
