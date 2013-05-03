package com.alvin.api.download.test;

import com.alvin.activity.tabhost.TabHostActivityGroup;
import com.alvin.api.abstractClass.Alvin_Application;
import com.alvin.api.bean.Bean;
import com.alvin.api.utils.LogOutputUtils;
import com.alvin.api.utils.CommonSettingsUtils;
import com.alvin.common.R;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class ServiceDownload extends Service {
    private final String TAG = ServiceDownload.class.getSimpleName();
    DownloadApkThread downloadApkThread;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogOutputUtils.e(TAG, "下载服务开启,intent:" + (intent == null));
        if (intent != null) {
            if (Alvin_Application.serviceIsFinish) {
                Alvin_Application.serviceIsFinish = false;
            }
            if (downloadApkThread == null) {
                downloadApkThread = DownloadApkThread
                        .getDownloadApkThread(this);// 获取下载线程实例并开启线程
            }
            ShowNotifycation.getShowNotifycation(TabHostActivityGroup.class,
                    R.drawable.stat_sys_download_anim5);

            if (intent.getStringExtra(CommonSettingsUtils.TASK_FLAG) != null) {
                if (intent.getStringExtra(CommonSettingsUtils.TASK_FLAG).equals(
                        CommonSettingsUtils.ADD_TASK)) {
                    final Bean app = (Bean) intent
                            .getParcelableExtra(CommonSettingsUtils.APP);
                    Long lCurrentTime = (Long) intent.getLongExtra(
                            CommonSettingsUtils.CURRENT_TIME, -1);
                    try {
                        while (!DownloadApkThread.initFinish) {

                        }

                        downloadApkThread.addDownloadTask(app, lCurrentTime);// 添加下载任务
                        ShowNotifycation.addApkNotify(app.getTitle(),
                                app.getTitle());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (intent.getStringExtra(CommonSettingsUtils.TASK_FLAG)
                        .equals(CommonSettingsUtils.REMOVE_TASK)) {
                    LogOutputUtils.e(TAG, "服务接收到通知取消下载中的任务");
                    downloadApkThread.removeDownloadTask(intent
                            .getStringExtra(CommonSettingsUtils.APK_NAME));// 删除任务或者取消正在下载
                    ShowNotifycation.cancleApkNotify(intent
                            .getStringExtra(CommonSettingsUtils.APK_NAME));
                }
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    public void killServiceProcess() {
        Alvin_Application.serviceIsFinish = true;
        stopSelf();
        // onDestroy();
        if (Alvin_Application.activityIsFinish) {
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }
}
