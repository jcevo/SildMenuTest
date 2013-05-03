package com.alvin.api.download.test;

import com.alvin.api.utils.LogOutputUtils;
import com.alvin.api.utils.CommonSettingsUtils;
import com.alvin.common.R;
import com.alvin.db.DBApp_download;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.ProgressBar;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShowNotifycation {
    static String TAG = ShowNotifycation.class.getSimpleName();
    static int DOWNLOADINGID = 19871017;
    private static ShowNotifycation instance;
    private static NotificationManager notificationManager;
    private static Context context;
    private static List<String> nameList = new ArrayList<String>();// apkName
    private static Map<String, String> appPackMap = new HashMap<String, String>();// apkName,packName
    static Intent downloadingIntent;
    static PendingIntent pendingIntent;
    static Notification notification;
    static TextView textView;
    static ProgressBar progressBar;
    static RemoteViews remoteViews;
    static String bfb = "%";

    // private static Map<String, Integer> installCanclMap = new HashMap<String,
    // Integer>();

    private ShowNotifycation(Class destActivity, int iconId) {
        context = CommonSettingsUtils.context;
        notificationManager = (NotificationManager) this.context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.notify_downloading);
        if (iconId == CommonSettingsUtils.NO_ICON) {
            remoteViews.setImageViewResource(R.id.notificationImage,
                    R.drawable.stat_sys_download_anim5);
        } else {
            remoteViews.setImageViewResource(R.id.notificationImage, iconId);
        }
        downloadingIntent = new Intent();
        downloadingIntent.setClass(context, destActivity);
        downloadingIntent.putExtra(CommonSettingsUtils.NOTIFICATION_ONCLIAK,
                CommonSettingsUtils.NOTIFICATION_INDEX);
        pendingIntent = PendingIntent.getActivity(context, 0,
                downloadingIntent, Intent.FLAG_ACTIVITY_NEW_TASK);
        notification = new Notification();
        notification.icon = R.drawable.download_animation;
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notification.contentView = remoteViews;
        notification.contentIntent = pendingIntent;
        notification.flags = Notification.FLAG_ONGOING_EVENT;
    }

    /**
     * 作用:
     * 
     * @param destActivity
     *            : 点击通知要跳转的activity
     * @param iconid
     *            :通知栏中的icon,=SettingUtils.NO_ICON时使用默认图片
     */
    public static ShowNotifycation getShowNotifycation(Class destActivity,
            int iconId) {
        if (instance == null) {
            instance = new ShowNotifycation(destActivity, iconId);
        }
        return instance;
    }

    /**
     * 作用:添加apk通知用
     */
    public static void addApkNotify(String apkName, String packName) {
        // nameList.add(apkName);
        addName(apkName, packName);
        String text = setDownloadText(apkName);
        notificationManager.cancel(DOWNLOADINGID);
        notification.contentView.setTextViewText(R.id.notificationTitle, text);
        notification.contentView.setTextViewText(R.id.notificationPercent, ""
                + 0 + bfb);
        notification.contentView.setProgressBar(R.id.notificationProgress, 100,
                0, false);
        show(apkName, "已加入下载队列");
    }

    /**
     * 作用:显示进度通知
     * 
     * @param map
     *            整个下载队列
     * @param currentName
     *            正在下载的apk
     * @param progress
     *            正在下载的进度
     */
    public static void showProgressNotify(String apkName, int progress) {
        String text = setDownloadText(apkName);
        notification.contentView.setTextViewText(R.id.notificationTitle, text);
        notification.contentView.setTextViewText(R.id.notificationPercent, ""
                + progress + bfb);
        notification.contentView.setProgressBar(R.id.notificationProgress, 100,
                progress, false);
        show(text);
    }

    /**
     * 作用:下载完成安装通知
     * 
     * @param 下载完成的
     *            apkName
     */
    public static void finishNotify(String apkName) {
        // nameList.remove(apkName);
        LogOutputUtils.e(TAG, "包名" + appPackMap.get(apkName));

        // installNotify(apkName, appPackMap.get(apkName));//只对应下载apk有用
        removeName(apkName);
        if (nameList.isEmpty()) {
            notificationManager.cancel(DOWNLOADINGID);
        } else {
            String text = setDownloadText(nameList.get(0));
            notification.contentView.setTextViewText(R.id.notificationTitle,
                    text);
            notification.contentView.setTextViewText(R.id.notificationPercent,
                    "" + 0 + bfb);
            notification.contentView.setProgressBar(R.id.notificationProgress,
                    100, 0, false);
            show(text);
        }
    }

    /**
     * 作用:取消通知
     */
    public static void cancleApkNotify(String apkName) {
        notificationManager.cancel(DOWNLOADINGID);
        if (nameList.contains(apkName)) {
            // nameList.remove(apkName);
            removeName(apkName);
            if (nameList.isEmpty()) {
                show(apkName, "已取消");
                notificationManager.cancel(DOWNLOADINGID);
                return;
            }
            String text = setDownloadText(nameList.get(0));
            notification.contentView.setTextViewText(R.id.notificationTitle,
                    text);
            notification.contentView.setTextViewText(R.id.notificationPercent,
                    "" + 0 + bfb);
            notification.contentView.setProgressBar(R.id.notificationProgress,
                    100, 0, false);
            show(apkName, "已取消");
        }
    }

    /**
     * 作用:发送通知 只在添加下载任务时在状态栏显示
     */
    private static void show(String apkName, String tips) {
        notification.number = nameList.size();
        notification.tickerText = apkName + "  " + tips;
        notificationManager.notify(DOWNLOADINGID, notification);
    }

    /**
     * 作用:发送通知 只需要显示进度条
     */
    private static void show(String apkName) {
        notification.number = nameList.size();
        notificationManager.notify(DOWNLOADINGID, notification);
    }

    private static void installNotify(String apkName, String packName) {
        String filePath = CommonSettingsUtils.getRootPath() + apkName + ".apk";
        File file = new File(filePath);
        if (file.exists() && file.isFile()) {
            // PackageManager pm = context.getPackageManager();
            // PackageInfo info = pm.getPackageArchiveInfo(filePath,
            // PackageManager.GET_ACTIVITIES);
            // ApplicationInfo appInfo = info.applicationInfo;
            // String appName = pm.getApplicationLabel(appInfo).toString();
            // // String packageName = appInfo.packageName;
            // Drawable icon = pm.getApplicationIcon(appInfo);
            NotifyInstall notifyInstall = new NotifyInstall(apkName, packName);
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_PACKAGE_REPLACED);
            filter.addAction(Intent.ACTION_PACKAGE_ADDED);
            filter.addDataScheme("package");
            CommonSettingsUtils.context.registerReceiver(notifyInstall, filter);
            RemoteViews remoteView = new RemoteViews(context.getPackageName(),
                    R.layout.notify_finish_download);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file),
                    "application/vnd.android.package-archive");
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                    intent, 0);
            Notification notification = new Notification();
            notification.icon = R.drawable.stat_sys_download_anim5;
            notification.tickerText = apkName + "  下载完成";
            notification.flags = Notification.FLAG_AUTO_CANCEL;
            remoteView.setTextViewText(R.id.finishName, apkName);
            remoteView.setTextViewText(R.id.finishTips, "下载完成,点击安装");

            Bitmap bitmap = GetApkIcon.getIcon(filePath, context);
            if (bitmap == null) {
                remoteView.setImageViewResource(R.id.finishIcon,
                        R.drawable.stat_sys_download_anim5);
            } else {
                remoteView.setImageViewBitmap(R.id.finishIcon, bitmap);
            }

            notification.contentView = remoteView;
            notification.contentIntent = contentIntent;
            long time = System.currentTimeMillis();
            int ID = (int) time;
            // installCanclMap.put(apkName, ID);

            Editor editor = getSP().edit();
            editor.putInt(getName(apkName), ID);
            editor.commit();
            int i = getSP().getInt(getName(apkName), -1);
            LogOutputUtils.e(TAG, "name" + apkName + "ID" + i);
            notificationManager.notify(ID, notification);

        } else {
            Toast.makeText(context, apkName + "不存在,请重新下载", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private static SharedPreferences getSP() {
        SharedPreferences sharedPreferences = CommonSettingsUtils.context
                .getSharedPreferences(CommonSettingsUtils.USERINFO_COLLECTION,
                        Context.MODE_PRIVATE);
        return sharedPreferences;
    }

    private static String setDownloadText(String apkName) {
        String text = nameList.get(0);
        int count = 0;
        for (int i = 0; i < nameList.size(); i++) {
            if (!nameList.get(i).equals(apkName)) {
                count++;
                text += ", " + nameList.get(i);
            }
            if (count == 2) {
                if (nameList.size() > 3) {
                    text += "...";
                }
                break;
            }
        }
        return text;
    }

    public static void installCancle(String apkName) {
        SharedPreferences sp = getSP();
        String name = getName(apkName);
        int id = sp.getInt(name, -1);
        LogOutputUtils.e(TAG, "name" + apkName + "ID" + id);
        if (id != -1) {
            sp.edit().remove(name);
            if (notificationManager == null) {
                notificationManager = (NotificationManager) CommonSettingsUtils.context
                        .getSystemService(Context.NOTIFICATION_SERVICE);
            }
            notificationManager.cancel(id);
        }
    }

    private static String getName(String apkName) {
        return CommonSettingsUtils.NOTIFYID + apkName;
    }

    private static class NotifyInstall extends BroadcastReceiver {
        String packageName = "", apkName = "";

        public NotifyInstall(String apkName, String packageName) {
            this.packageName = packageName;
            this.apkName = apkName;
            LogOutputUtils.e(TAG, " 点击的apk: " + this.packageName);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            LogOutputUtils.i("added", intent.getDataString() + "||action:"
                    + action);
            if (action.equals(Intent.ACTION_PACKAGE_ADDED)
                    || action.equals(Intent.ACTION_PACKAGE_REPLACED)) {
                String installName = intent.getDataString();
                LogOutputUtils.e(TAG, "现在安装的apk: " + installName
                        + "|| 点击的apk: " + packageName);
                if (installName.equals("package:" + packageName)) {
                    // TODO 卸载之后还要更新各个列表图标
                    if (ClickAdapter.timeMap.containsKey(apkName)) {
                        ClickAdapter.removeMap(apkName);
                    }
                    installCancle(apkName);
                    DBApp_download dbApp_download = new DBApp_download(context,
                            CommonSettingsUtils.DATABASEVERSION);
                    dbApp_download.updateInstallState(apkName, 1);
                    // ProgressListViewAdapter.installAppList.add(app);
                    // updateDb(appName, 1);// 已安装
                }
            }
        }
    }

    private static void addName(String apkName, String packName) {
        nameList.add(apkName);
        appPackMap.put(apkName, packName);
    }

    private static void removeName(String apkName) {
        if (nameList.contains(apkName)) {
            nameList.remove(apkName);
        }
        if (appPackMap.containsKey(apkName)) {
            appPackMap.remove(apkName);
        }
    }
}
