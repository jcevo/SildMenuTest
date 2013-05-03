package com.alvin.api.download.test;

import com.alvin.api.bean.Bean;
import com.alvin.api.download.test.ClickCancleReceive.OnClickCancleListener;
import com.alvin.api.utils.LogOutputUtils;
import com.alvin.api.utils.CommonSettingsUtils;
import com.alvin.db.DBApp_download;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.util.HashMap;
import java.util.Map;

public class ClickAdapter {
    private static final String TAG = ClickAdapter.class.getSimpleName();
    private static ClickAdapter instance;
    private static Map<String, Bean> nameMap = new HashMap<String, Bean>();// <appName,app>
    // 用来存储下载过的应用
    public static Map<String, Long> timeMap = new HashMap<String, Long>();// 加个时间戳,为了区分是否为取消之后的
    private static ClickCancleReceive clickCancleReceive;

    private ClickAdapter() {

    }

    public static ClickAdapter getInstances(Context context) {
        if (instance == null) {
            instance = new ClickAdapter();
        }
        if (clickCancleReceive == null) {
            // 如果点击取消按钮则触发下载队列删除此appName
            OnClickCancleListener listener = new OnClickCancleListener() {

                @Override
                public void OnClickCancle(String appName, Context context) {
                    LogOutputUtils.e(TAG, "点击了取消的");
                    if (appName != null) {
                        if (nameMap.containsKey(appName)) {
                            removeMap(appName);
                            // ProgressListViewAdapter.dbDelete(appName,
                            // "接收到点击取消的动作");
                            Intent intent = new Intent();
                            intent.setAction(CommonSettingsUtils.PROGRESS_BROADCAST_RECEIVE);
                            intent.putExtra(CommonSettingsUtils.APK_NAME, appName);
                            intent.putExtra(CommonSettingsUtils.CANCLE, true);
                            context.sendBroadcast(intent);
                            removeDownloadService(context, appName);
                        }
                    }
                }
            };
            clickCancleReceive = new ClickCancleReceive(listener);
            IntentFilter filter = new IntentFilter();
            filter.addAction(CommonSettingsUtils.CANCLE_INTENT);
            context.registerReceiver(clickCancleReceive, filter);
            // 注册广播接收器

        }
        return instance;
    }

    // 重新下载程序
    public void download(Bean bean, Activity activity) {

        if (!nameMap.containsKey(bean.getTitle())) {
            LogOutputUtils.i(TAG, "downloads" + bean.getDownloadUrl());
            // nameMap.put(app.getTitle(), app);
            // timeMap.put(app.getTitle(), System.currentTimeMillis());
            Long currentTime = System.currentTimeMillis();
            addMap(bean, currentTime);
            addDownLoadService(bean, activity, currentTime);
        }
    }

    // 开启下载服务
    private void addDownLoadService(Bean bean, Context context,
            Long lCurrentTime) {
        // 每开启一次下载都要写到数据库,待重新进入程序的时候可以续传下载
        DBApp_download dbApp_download = new DBApp_download(context,
                CommonSettingsUtils.DATABASEVERSION);
        dbApp_download.insertProgress(bean, 0);
        // LogOutput.e(TAG, "开启下载服务" + app.getTitle());
        Intent intent = new Intent(CommonSettingsUtils.SERVICEDOWNLOAD);
        intent.putExtra(CommonSettingsUtils.TASK_FLAG, CommonSettingsUtils.ADD_TASK);
        intent.putExtra(CommonSettingsUtils.APP, bean);
        intent.putExtra(CommonSettingsUtils.CURRENT_TIME, lCurrentTime);
        // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startService(intent);
    }

    // 断点续传的下载改为重新下载 第一版不做,后期再做.
    // public void loading(App app, int progress, Context context) {
    //
    // if (!nameMap.containsKey(app.getTitle())) {
    // LogOutput.i(TAG, "loading" + app.getDownloadUrl());
    // // nameMap.put(app.getTitle(), app);
    // // timeMap.put(app.getTitle(), System.currentTimeMillis());
    // Long currentTime = System.currentTimeMillis();
    // addMap(app, currentTime);
    // addDownLoadService(app, context, currentTime);
    // // 断点续传使用
    // // ApkDownloadThread.progressMap.put(app.getName(), progress);
    // }
    // }

    /**
     * 通知服务删除下载线程
     */
    private static void removeDownloadService(Context context, String appName
    // App iApp, int iPosition
    ) {
        // LogOutput.e(TAG, "开启多少次服务" + count++);
        LogOutputUtils.e(TAG, "通知服务取消任务");
        Intent intent = new Intent(CommonSettingsUtils.SERVICEDOWNLOAD);
        intent.putExtra(CommonSettingsUtils.TASK_FLAG, CommonSettingsUtils.REMOVE_TASK);
        intent.putExtra(CommonSettingsUtils.APK_NAME, appName);
        context.startService(intent);
    }

    private static void addMap(Bean bean, Long lCurrentTime) {
        bean.setDownDate(lCurrentTime);
        nameMap.put(bean.getTitle(), bean);
        timeMap.put(bean.getTitle(), lCurrentTime);
    }

    public static void removeMap(String appName) {
        if (nameMap.containsKey(appName)) {
            nameMap.remove(appName);
        }
        if (timeMap.containsKey(appName)) {
            timeMap.remove(appName);
        }
    }

}
