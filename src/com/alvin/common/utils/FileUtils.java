package com.alvin.common.utils;

import android.os.StatFs;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * 用于文件操作的工具类
 */
public class FileUtils {
    private final static String TAG = FileUtils.class.getSimpleName();
    private final static int BUFFER = 8192;

    public static long getAvailableStorageSize(File dir) {
        long size = -1;
        if(dir != null && dir.exists() && dir.isDirectory()) {
            StatFs stat = new StatFs(dir.getPath());
            size = (long)stat.getBlockSize() * stat.getAvailableBlocks();
        }
//        System.out.println("##### availableSize[" + dir + "] = " + size);
        return size;
    }

    public static long getDirSize(File dir) {
        long size = 0;
        if(dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            for(File file : files) {
                if(file.isFile()) {
                    size += file.length();
                } else {
                    size += getDirSize(file);
                }
            }
        }
//        System.out.println("##### dirSize[" + dir + "] = " + size);
        return size;
    }

    //复制文件或目录
    public static void copy(File sourceFile, File targetFile) throws IOException {
        if(!sourceFile.exists()){
            Log.i(TAG, "the source file is not exists: " +
                    sourceFile.getAbsolutePath());
        }else{
            if(sourceFile.isFile()){
                copyFile(sourceFile, targetFile);
            } else {
                copyDirectory(sourceFile, targetFile);
            }
        }
    }

    //复制文件
    public static void copyFile(File sourceFile, File targetFile)
            throws IOException {
        BufferedInputStream inBuff = null;
        BufferedOutputStream outBuff = null;
        try {
            inBuff = new BufferedInputStream(new FileInputStream(sourceFile));
            outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));
            byte[] buffer = new byte[BUFFER];
            int length;
            while ((length = inBuff.read(buffer)) != -1) {
                outBuff.write(buffer, 0, length);
            }
            outBuff.flush();
        } finally {
            if(inBuff != null) {
                inBuff.close();
            }
            if(outBuff != null) {
                outBuff.close();
            }
        }
    }

    //复制文件夹
    public static void copyDirectory(File sourceDir, File targetDir)
            throws IOException {
        //新建目标目录
        targetDir.mkdirs();
        //遍历源目录下所有文件或目录
        File[] file = sourceDir.listFiles();
        for (int i = 0; i < file.length; i++) {
            if (file[i].isFile()) {
                File sourceFile = file[i];
                File targetFile = new
                        File(targetDir.getAbsolutePath()
                        + File.separator + file[i].getName());
                copyFile(sourceFile, targetFile);
            } else if (file[i].isDirectory()) {
                File dir1 = new File(sourceDir, file[i].getName());
                File dir2 = new File(targetDir, file[i].getName());
                copyDirectory(dir1, dir2);
            }
        }
    }

    //删除文件或目录
    public static boolean delete(File file) {
        if(!file.exists()){
            Log.i(TAG, "the file is not exists: " +
                    file.getAbsolutePath());
            return false;
        }else{
            if(file.isFile()){
                return deleteFile(file);
            }else{
                return deleteDirectory(file, true);
            }
        }
    }

    //删除文件
    public static boolean deleteFile(File file) {
        if(file.isFile() && file.exists()){
            file.delete();
            return true;
        }else{
            Log.i(TAG, "the file is not exists: " +
                    file.getAbsolutePath());
            return false;
        }
    }

    //删除目录
    public static boolean deleteDirectory(File dirFile, boolean includeSelf) {
        return deleteDirectory(dirFile, null, includeSelf, false);
    }

    //删除目录
    public static boolean deleteDirectory (
            File dirFile, String extension, boolean includeSelf, boolean onlyFile) {
        if(!dirFile.exists() || !dirFile.isDirectory()){
            Log.i(TAG, "the directory is not exists: " + dirFile.getAbsolutePath());
            return false;
        }
        boolean flag = true;
        File[] files = dirFile.listFiles();
        for(int i=0;i<files.length;i++) {
            if(files[i].isFile()){
                if(extension == null ||
                        files[i].getName().toLowerCase().endsWith("." + extension.toLowerCase())) {
//                    System.out.println("DELETE FILE: " + files[i].getName());
                    flag = deleteFile(files[i]);
                    if(!flag){
                        break;
                    }
                }
            } else {
                if(!onlyFile) {
                    flag = deleteDirectory(files[i], true);
                    if(!flag){
                        break;
                    }
                }
            }
        }

        if(!flag){
            Log.i(TAG, "delete directory fail: " +
                    dirFile.getAbsolutePath());
            return false;
        }

        if(includeSelf) {
            if(dirFile.delete()){
                return true;
            }else{
                Log.i(TAG, "delete directory fail: " +
                        dirFile.getAbsolutePath());
                return false;
            }
        } else {
            return true;
        }
    }

    public static void move(File src, File dest) throws IOException {
        copy(src, dest);
        delete(src);
    }

    //从输入流读取文本内容
	public static String readTextInputStream(InputStream is) throws IOException {
        StringBuffer strbuffer = new StringBuffer();
        String line;
        BufferedReader reader = null;
        try{
            reader = new BufferedReader(new InputStreamReader(is));
            while((line = reader.readLine()) != null) {
                strbuffer.append(line).append("\r\n");
            }
        } finally {
            if(reader != null) {
                reader.close();
            }
        }
        return strbuffer.toString();
    }

    //从文件读取文本内容
	public static String readTextFile(File file) throws IOException {
        String text = null;
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            text = readTextInputStream(is);;
        } finally {
            if(is != null) {
                is.close();
            }
        }
        return text;
    }

    //将文本内容写入文件
    public static void writeTextFile(File file, String str) throws IOException {
        DataOutputStream out = null;
        try {
            out = new DataOutputStream(new FileOutputStream(file));
            out.write(str.getBytes());
        } finally {
            if(out != null) {
                out.close();
            }
        }
    }

    //将一系列字符串写入文件
    public static void writeTextFile(File file, String[] strArray) throws IOException {
    	String str = "";
    	for(int i=0; i<strArray.length; i++) {
    		str += strArray[i];
    		if(i!=strArray.length-1)
    			str += "\r\n";
    	}

        DataOutputStream out = null;
        try {
            out = new DataOutputStream(new FileOutputStream(file));
            out.write(str.getBytes());
        } finally {
            if(out != null) {
                out.close();
            }
        }
    }

    //合并多个文本文件的内容到一个文件
    public static void combineTextFile(File[] sFiles, File dFile) throws IOException {
        BufferedReader in = null;
        BufferedWriter out = null;

        try {
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dFile)));

            for(int i=0; i<sFiles.length; i++) {
                in = new BufferedReader(new InputStreamReader(new FileInputStream(sFiles[i])));
                String oldLine = in.readLine();
                String newLine = null;
                while((newLine = in.readLine()) != null) {
                    out.write(oldLine);
                    out.newLine();
                    oldLine = newLine;
                }
                out.write(oldLine);

                if(i != sFiles.length - 1)
                    out.newLine();

                out.flush();
            }
        } finally {
            if(in != null) {
                in.close();
            }
            if(out != null) {
                out.close();
            }
        }
    }

    //写入数据到文件
    public static void writeFile(File file, byte[] data) throws Exception {
    	DataOutputStream out = null;
        try {
            out = new DataOutputStream(new FileOutputStream(file));
            out.write(data);
        } finally {
            if(out != null) {
                out.close();
            }
        }
    }

    //将输入流中的数据写入文件
    public static int writeFile(File file, InputStream inStream) throws IOException {
        long dataSize = 0;
    	DataInputStream in = null;
    	DataOutputStream out = null;

        try {
            byte buffer[] = new byte[BUFFER];
            out = new DataOutputStream(new FileOutputStream(file));
            in = new DataInputStream(inStream);

            int nbyteread;
            while((nbyteread = in.read(buffer)) != -1) {
                out.write(buffer, 0, nbyteread);
                dataSize += nbyteread;
            }
        } finally {
            if(in != null) {
                in.close();
            }
            if(out != null) {
                out.close();
            }
        }

        return (int)(dataSize/1024);
    }
}
