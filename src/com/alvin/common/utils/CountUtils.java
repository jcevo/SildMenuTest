package com.alvin.common.utils;

import com.alvin.api.config.Env;
import com.alvin.db.DBHelper;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 用于访问计数的服务
 */
public class CountUtils {
    private final static String TAG = CountUtils.class.getSimpleName();
    private static UserActivitySender sender;
    private static long lastEventTime = System.currentTimeMillis();
    private static int useTime = 0;

    public static String INTERFACE_COUNTER;
    public static String INTERFACE_MOSS_RECEIVER = "http://moss.pconline.cn/receiver";

    public final static int USE_TIME_THRESHOLD = 15 * 60 * 1000;

    public final static int USER_ACTIVITY_EVENT_START = 5;
    public final static int USER_ACTIVITY_EVENT_DEVICE_INFO = 8;
    public final static int USER_ACTIVITY_EVENT_DOWNLOAD_REFERENCE = 10;
    public final static int USER_ACTIVITY_EVENT_STOP = 12;

    public static void incCounterAsyn(final int counterId) {
        new Thread() {
            public void run() {
                CountUtils.incCounterSync(counterId);
            }
        }.start();
    }

    public static boolean incCounterSync(int counterId) {
        boolean success = false;
        if(counterId > 0) {
            success = HttpUtils.sendData(INTERFACE_COUNTER + "?channel=" + counterId, null);
        }

        if(success) {
            Log.i(TAG, "Send count request: " + counterId);
        } else {
            Log.i(TAG, "Send count request fail: " + counterId);
        }

        return success;
    }

    public static void recordUserActivity(int eventId, String eventJson) {
        SQLiteDatabase db = Env.dbHelper.getWritableDatabase();
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            StringBuffer event = new StringBuffer("\"event\":").append(eventId).append(",")
                    .append("\"createtime\":\"").append(dateFormat.format(new Date()) + "\",")
                    .append(eventJson);
            ContentValues data = new ContentValues();
            data.put("event", event.toString());
            db.insert(DBHelper.USER_ACTIVITY_TABLE, null, data);
        } catch (Exception e){
            e.printStackTrace();
        }

        if(sender == null) {
            sender = new UserActivitySender();
            sender.start();
        }
    }

    public static void updateUseTime(boolean isExist) {
        long now = System.currentTimeMillis();
        long diff = now - lastEventTime;
        if(isExist || diff > USE_TIME_THRESHOLD) {
            StringBuffer event = new StringBuffer("\"version\":\"")
                    .append(URLEncoder.encode(Env.versionName)).append("\",")
                    .append("\"duration\":" + Integer.toString((int)Math.round(useTime/1000.0)));
            CountUtils.recordUserActivity(
                    CountUtils.USER_ACTIVITY_EVENT_STOP, event.toString());
            useTime = 0;
        } else {
            useTime += diff;
        }
        lastEventTime = now;
    }

    private static class UserActivitySender extends Thread {

        public void run() {
            while(true) {
                boolean success = false;
                SQLiteDatabase db = Env.dbHelper.getWritableDatabase();
                Cursor cur = null;
                try {
                    //更新安装数据
                    cur = db.rawQuery("select * from " + DBHelper.USER_ACTIVITY_TABLE, null);
                    int count = cur.getCount();
                    if(count > 0) {
                        String url = INTERFACE_MOSS_RECEIVER;

                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        String devID = Env.telephonyManager.getDeviceId();
                        if(devID == null) {
                            devID = "";
                        }
                        devID = URLEncoder.encode(devID);
                        long clientTime = System.currentTimeMillis();
                        params.add(new BasicNameValuePair("DEV-ID", devID));
                        params.add(new BasicNameValuePair("app", Integer.toString(Env.appID)));
                        params.add(new BasicNameValuePair("clienttime", Long.toString(clientTime)));

                        List<Integer> idList = new ArrayList<Integer>(count);
                        StringBuffer eventJson = new StringBuffer("[");
                        while(cur.moveToNext()) {
                            idList.add(cur.getInt(cur.getColumnIndex("id")));
                            eventJson.append("{")
                                     .append(cur.getString(cur.getColumnIndex("event")))
                                     .append("}");
                            if(!cur.isLast()) {
                                eventJson.append(",");
                            }
                        }
                        eventJson.append("]");

                        if(count > 2) {
                            params.add(new BasicNameValuePair("compress", "gzip"));
                            File zip = new File(CacheUtils.tempCacheDirExternal,
                                    Long.toString(System.currentTimeMillis()*10000
                                    + (int)(Math.random()*10000)) + ".zip");
                            try {
                                ZipUtils.compressToFile(zip, eventJson.toString());
                                success = HttpUtils.sendData(url, zip, params);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if(zip.exists() && zip.isFile()) {
                                zip.delete();
                            }
                        } else {
                            params.add(new BasicNameValuePair("json", eventJson.toString()));
                            success = HttpUtils.sendData(url, params);
                        }

                        if(success) {
                            Log.i(TAG, "Send user activity data success: " + url
                                    + "?DEV-ID=" + devID
                                    + "&app=" + Integer.toString(Env.appID)
                                    + "&clienttime=" + Long.toString(clientTime)
                                    + "&json=" + eventJson.toString());
                            for(int id : idList) {
                                db.delete(DBHelper.USER_ACTIVITY_TABLE,
                                        "id=?", new String[]{Integer.toString(id)});
                            }
                        } else {
                            Log.i(TAG, "Send user activity data fail: " + url
                                    + "?DEV-ID=" + devID
                                    + "&app=" + Integer.toString(Env.appID)
                                    + "&clienttime=" + Long.toString(clientTime)
                                    + "&json=" + eventJson.toString());
                        }
                    }
                } finally {
                    if(cur != null) {
                        cur.close();
                    }
                }

                try {
                    Thread.sleep(60000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
