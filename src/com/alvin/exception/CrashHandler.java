package com.alvin.exception;

import com.alvin.common.utils.CountUtils;

import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 全局异常处理类
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private static CrashHandler self;
    private int reportType = REPORT_TYPE_FILE;
    private File logFileDir;
    private String logEmail;
    private String logInterface;

    public final static int REPORT_TYPE_FILE = 0;
    public final static int REPORT_TYPE_EMAIL = 1;
    public final static int REPORT_TYPE_INTERFACE = 2;

    private CrashHandler() {

    }

    public synchronized static CrashHandler getInstance() {
        if(self == null) {
            self = new CrashHandler();
            Thread.setDefaultUncaughtExceptionHandler(self);
        }
        return self;
    }

    public void init(int reportType) {
        this.reportType = reportType;
    }

    public void uncaughtException(Thread thread, Throwable exception) {
        Log.e("CrashHandler", "Find uncaught exception");
        exception.printStackTrace();
        switch (reportType) {
            case REPORT_TYPE_FILE:
                this.reportByFile(exception);
                break;
            case REPORT_TYPE_EMAIL:
                this.reportByEmail(exception);
                break;
            case REPORT_TYPE_INTERFACE:
                this.reportByInterface(exception);
                break;
        }

        CountUtils.updateUseTime(true);
//        System.exit(0);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    private String getLogString(Throwable exception, Date time) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return timeFormat.format(time) + "\r\n" + Log.getStackTraceString(exception) + "\r\n\r\n";
    }

    private void reportByFile(Throwable exception) {
        if(logFileDir != null && logFileDir.exists() && logFileDir.isDirectory()) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date time = new Date();
            File logFile = new File(logFileDir, dateFormat.format(time) + ".log");
            FileWriter fileWriter = null;
            try {
                fileWriter = new FileWriter(logFile, true);
                fileWriter.write(this.getLogString(exception, time));
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if(fileWriter != null) {
                    try {
                        fileWriter.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void reportByEmail(Throwable exception) {

    }

    private void reportByInterface(Throwable exception) {

    }

    public void setLogInterface(String logInterface) {
        this.logInterface = logInterface;
    }

    public void setLogFileDir(File logFileDir) {
        this.logFileDir = logFileDir;
        if(!logFileDir.exists() || !logFileDir.isDirectory()) {
            logFileDir.mkdirs();
        }
    }

    public void setLogEmail(String logEmail) {
        this.logEmail = logEmail;
    }
}
