package com.alvin.app;

import com.alvin.api.config.Env;
import com.alvin.api.model.Info;
import com.alvin.common.utils.CountUtils;
import com.alvin.common.utils.NetworkUtils;
import com.alvin.db.AlvinDBHelper;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import java.net.URLEncoder;

public class CommonLauncherActivity extends CommonActivity.BaseActivity {
    private final static String TAG = CommonLauncherActivity.class.getSimpleName();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Thread() {
            public void run() {
                SQLiteDatabase db = Env.dbHelper.getWritableDatabase();

                //检查安装状态
                Cursor cur = null;
                try {
                    Info info = new Info();
                    cur = db.rawQuery(
                            "select * from " + AlvinDBHelper.INFO_TABLE + " " +
                            "where name = '" + Info.NAME_VERSION_CODE + "'", null);
                    if(cur.getCount() > 0 && cur.moveToNext()) {
                        info.parse(cur);
                        if(Env.versionCode > Integer.parseInt(info.getValue())) {   //更新
                            Log.i(TAG, "This app was updated recently");
                            //发送安装来源信息
                            StringBuffer event = new StringBuffer("\"app-version\":\"")
                                    .append(URLEncoder.encode(Env.versionName)).append("\",")
                                    .append("\"Source\":\"")
                                    .append(URLEncoder.encode(Env.downloadReference)).append("\"");
                            CountUtils.recordUserActivity(
                                    CountUtils.USER_ACTIVITY_EVENT_DOWNLOAD_REFERENCE,
                                    event.toString());
                            //更改数据库版本信息
                            ContentValues dataValue = new ContentValues();
                            dataValue.put("value", Env.versionCode);
                            dataValue.put("time", System.currentTimeMillis());
                            String[] parms = new String[]{Long.toString(info.getId())};
                            db.update(AlvinDBHelper.INFO_TABLE, dataValue, "id=?", parms);
                        }
                    } else {    //新装
                        //发送设备信息
                        Log.i(TAG, "This app was installed recently");
                        StringBuffer event = new StringBuffer("\"DEV-NAME\":\"Android Phone\",")
                                .append("\"DEV-Type\":\"Android Phone\",")
                                .append("\"DEV-Model\":\"")
                                .append(URLEncoder.encode(Build.MODEL)).append("\",")
                                .append("\"OS-version\":\"")
                                .append(URLEncoder.encode(Build.VERSION.RELEASE)).append("\",")
                                .append("\"resolution\":\"" + Env.display.getWidth() + "x"
                                        + Env.display.getHeight() + "\"");
                        CountUtils.recordUserActivity(
                                CountUtils.USER_ACTIVITY_EVENT_DEVICE_INFO, event.toString());
                        //发送安装来源信息
                        event = new StringBuffer("\"app-version\":\"")
                                .append(URLEncoder.encode(Env.versionName)).append("\",")
                                .append("\"Source\":\"")
                                .append(URLEncoder.encode(Env.downloadReference)).append("\"");
                        CountUtils.recordUserActivity(
                                CountUtils.USER_ACTIVITY_EVENT_DOWNLOAD_REFERENCE,
                                event.toString());
                        //更改数据库版本信息
                        ContentValues dataValue = new ContentValues();
                        dataValue.put("name", Info.NAME_VERSION_CODE);
                        dataValue.put("value", Env.versionCode);
                        dataValue.put("time", System.currentTimeMillis());
                        db.insert(AlvinDBHelper.INFO_TABLE, null, dataValue);
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                } finally {
                    if(cur != null) {
                        cur.close();
                    }
                }

                //记录打开行为到行为分析系统
                try {
                    String network = "UNKNOWN";
                    int networkState = NetworkUtils.getNetworkStates(CommonLauncherActivity.this);
                    if(networkState == NetworkUtils.STATE_WIFI) {
                        network = "WIFI";
                    } else if(networkState == NetworkUtils.STATE_MOBILE) {
                        network = "2G/3G";
//                    } else if(networkState == NetworkUtils.STATE_NONE) {
//                        network = "NONE";
                    }
                    StringBuffer event = new StringBuffer("\"version\":\"")
                            .append(Env.versionName).append("\",")
                            .append("\"osversion\":\"")
                            .append(URLEncoder.encode(Build.VERSION.RELEASE)).append("\",")
                            .append("\"net\":\"").append(network).append("\"");
                    CountUtils.recordUserActivity(
                            CountUtils.USER_ACTIVITY_EVENT_START, event.toString());
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
