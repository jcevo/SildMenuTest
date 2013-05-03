package com.alvin.activity.service;

import com.alvin.api.config.Env;
import com.alvin.common.R;
import com.alvin.common.utils.HttpUtils;
import com.alvin.db.DBHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.TypedValue;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 用于系统更新的服务
 */
public class UpdateService extends Service{
    private final int DOWNLOAD_COMPLETE = 0;
    private final int DOWNLOAD_URL_REEOR = 1;
    private final int DOWNLOAD_FAIL = 2;
    private final int CREATE_FILE_FAIL = 3;
    
    private String updateNotificationTitle = null;
    
    //更新文件和更新跳转
    private File  updateFile = null;
    private PendingIntent updatePendingIntent = null;
    private int updateTotalSize = 0;
    private NotificationManager updateNotificationManager = null;
    private Notification updateNotification = null;
    
    //布局资源
    private int iconID = 0;
    private int titleID = R.string.app_name;
    private int layoutID = R.layout.update_notification;
    private int textViewID = R.id.update_notification_progresstext;
    private int blockViewID = R.id.update_notification_progressblock;
    private int processbarViewID = R.id.update_notification_progressbar;
    
    //onStartCommand状态参数
    private Intent startIntent = null;
    private int startFlags = 0;
    private int startId = 0;
    private int startCount = 0;
    
    private int updateApkVersionCode=0;
    private String updateApkDownloadUrl="";
    
    private Handler updateHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case DOWNLOAD_COMPLETE:
                    updateNotification.contentView.setViewVisibility(blockViewID, View.GONE);
                    
                    //下载完成后校验文件是否正确
                    try{                        
                        PackageManager pManager = UpdateService.this.getPackageManager();
                        PackageInfo pInfo = pManager.getPackageArchiveInfo(
                                updateFile.getPath(), PackageManager.GET_ACTIVITIES);
                        if(pInfo==null){
                            updateNotification.contentView.setTextViewText(
                                    textViewID, "校验文件失败，请下次启动后重新下载");
                            updateNotificationManager.notify(iconID, updateNotification);
                            updateFile.delete();
                            if(startCount<4){                                
                                onStartCommand(startIntent,startFlags,startId);
                            }
                            stopSelf();
                            return;
                        }
                    }catch(Exception ex){
                        updateNotification.contentView.setTextViewText(
                                textViewID, "校验文件失败，请下次启动后重新下载");
                        updateNotificationManager.notify(iconID, updateNotification);
                        updateFile.delete();
                        if(startCount<4){                                
                            onStartCommand(startIntent,startFlags,startId);
                        }
                        stopSelf();
                        return;
                    }
                    
                    //files目录下文件安装需要权限，赋予777权限
                    String cmd = "chmod 777 " +updateFile.getPath();
                    try {
                        Runtime.getRuntime().exec(cmd);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    
                    //设置点击跳转Activity
                    Uri uri = Uri.fromFile(updateFile);
                    Intent updateCompleteIntent = new Intent(Intent.ACTION_VIEW);
                    updateCompleteIntent.setDataAndType(
                            uri, "application/vnd.android.package-archive");
                    updatePendingIntent = PendingIntent.getActivity(
                            UpdateService.this, titleID, updateCompleteIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    
                    updateNotification.defaults = Notification.DEFAULT_SOUND;
                    updateNotification.contentIntent = updatePendingIntent;
                    updateNotification.contentView.setTextViewText(textViewID, "下载完成，点击安装！");
                    updateNotificationManager.notify(iconID, updateNotification);
                    
                    //应用正在运行
                    ActivityManager myAM=(ActivityManager)getApplicationContext().getSystemService(
                            Context.ACTIVITY_SERVICE);
                    List<RunningAppProcessInfo> runningApps = myAM.getRunningAppProcesses();
                    
                    boolean isAppRunning = false;
                    for(RunningAppProcessInfo r : runningApps){
                        if(r.processName.equals(Env.client)){
                            isAppRunning = true;
                            break;
                        }
                    }
                    if(isAppRunning){
//                      下载完成后，跳转到用户确认升级界面
                        Intent installIntent = new Intent(
                                UpdateService.this, UpdateService.class);
                        installIntent.putExtra("updateFilePath", updateFile.getPath());
                        installIntent.putExtra("iconID", iconID);
                        installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(installIntent);
                    }
                    break;
                case DOWNLOAD_URL_REEOR:
                    //下载地址为空
                    updateNotification.setLatestEventInfo(UpdateService.this,
                            updateNotificationTitle, "下载失败 ，请重新下载！", updatePendingIntent);
                    updateNotificationManager.notify(iconID, updateNotification);
                    break;
                case CREATE_FILE_FAIL:
                    //创建文件失败
                    updateNotification.setLatestEventInfo(UpdateService.this,
                            updateNotificationTitle, "下载失败，请检测SDCARD是否可以访问！",
                            updatePendingIntent);
                    updateNotificationManager.notify(iconID, updateNotification);
                    break;
                case DOWNLOAD_FAIL:
                     //其他错误
                    updateNotification.setLatestEventInfo(UpdateService.this,
                            updateNotificationTitle, "下载失败，请重新下载！", updatePendingIntent);
                    updateNotificationManager.notify(iconID, updateNotification);
                    break;
                default:
            }
            stopSelf();
            super.handleMessage(msg);
        }        
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        
        this.startCount++;
        this.startIntent = intent;
        this.startFlags = flags;
        this.startId = startId;
        
        //接受参数
        iconID = intent.getIntExtra("iconID", 0);
//        titleID = intent.getIntExtra("titleID", 0);
//        layoutID = intent.getIntExtra("layoutID", 0);
//        textViewID = intent.getIntExtra("textViewID", 0);
//        blockViewID = intent.getIntExtra("blockViewID", 0);
//        processbarViewID = intent.getIntExtra("processbarViewID", 0);
        
        updateApkVersionCode = intent.getIntExtra("versionCode", 0);
        updateApkDownloadUrl = intent.getStringExtra("downloadUrl");
        
        //创建文件
        if(android.os.Environment.MEDIA_MOUNTED.equals(
                android.os.Environment.getExternalStorageState())){
            updateFile = new File(Env.externalFileDir,Env.client+".apk");
        }else{
            updateFile = new File(getFilesDir().getPath(),Env.client+".apk");
        }
        
        this.updateNotificationTitle = getResources().getString(titleID);
        
        this.updateNotificationManager =
                (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        this.updateNotification = new Notification();

        updateNotification.contentView =
                new RemoteViews(getApplication().getPackageName(), layoutID);

        Intent updateCompletingIntent = new Intent();
        updateCompletingIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        updateCompletingIntent.setClass(getApplication().getApplicationContext(),
                UpdateService.class);
        
        updatePendingIntent = PendingIntent.getActivity(UpdateService.this, titleID,
                updateCompletingIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                
        updateNotification.icon = iconID;
        updateNotification.tickerText = "开始下载";
        updateNotification.contentIntent = updatePendingIntent;
        updateNotification.contentView.setImageViewResource(R.id.update_notification_icon, iconID);
        updateNotification.contentView.setProgressBar(processbarViewID, 100, 0, false);
        updateNotification.contentView.setTextViewText(textViewID, "0%");
        updateNotificationManager.cancel(iconID);
        updateNotificationManager.notify(iconID, updateNotification);
        
        new Thread(new updateRunnable()).start();
        
        return super.onStartCommand(intent, flags, startId);
    }


    class updateRunnable implements Runnable{
        Message message = updateHandler.obtainMessage();
        public void run() {
            try{   
                message.what = DOWNLOAD_COMPLETE;
                
                if(!updateFile.exists()){
                    updateFile.createNewFile();
                }else{
                    //判断SDCARD上是否已经下载了最新版本
                    try{
                        PackageManager pManager = UpdateService.this.getPackageManager();
                        PackageInfo pInfo = pManager.getPackageArchiveInfo(
                                updateFile.getPath(), PackageManager.GET_ACTIVITIES);
                        //如果已经下载，检查下载文件的版本
                        if(pInfo != null){
                            if(updateApkVersionCode > 0
                                    && updateApkVersionCode == pInfo.versionCode){
                                updateHandler.sendMessage(message);
                                return;
                            }else{
                                //如果是老版本的，删除并准备重新下载
                                updateFile.delete();
                                updateFile.createNewFile();
                            }
                        }
                    }catch(Exception ex){
                        ex.printStackTrace();
                    }
                }
               
                long size = 0;
                //空判断服务器下载地址
                if(updateApkDownloadUrl == null || "".equals(updateApkDownloadUrl)){
                    message.what = DOWNLOAD_URL_REEOR;
                }else{
                    size = downloadUpdateFile(updateApkDownloadUrl, updateFile, true);
                    if(size == 0){ 
                        message.what = DOWNLOAD_FAIL;
                    }
                }
                updateHandler.sendMessage(message);
            }catch(Exception ex){
                ex.printStackTrace();
                message.what = DOWNLOAD_FAIL;
                updateHandler.sendMessage(message);
            }
        }
        
        //下载文件
        public long downloadUpdateFile(String urlStr, File dest, boolean append) throws Exception {
            
            int downloadCount = 0;
            int currentSize = 0;
            long totalSize = 0;
            
            if(append) {
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(dest);
                    currentSize = fis.available();
                } catch(IOException ex) {
                    ex.printStackTrace();
                } finally {
                    if(fis != null) {
                        fis.close();
                    }
                }
            }
            
            HttpURLConnection httpConnection = null;
            InputStream is = null;
            FileOutputStream fos = null;
            
            try {
                URL url = new URL(urlStr);
                httpConnection = (HttpURLConnection)url.openConnection();
                httpConnection.setRequestProperty("User-Agent", "PacificHttpClient");
                if(currentSize > 0) {
                    httpConnection.setRequestProperty("RANGE", "bytes=" + currentSize + "-");
                }
                httpConnection.setConnectTimeout(10000);
                httpConnection.setReadTimeout(20000);
                updateTotalSize = httpConnection.getContentLength();
                if (httpConnection.getResponseCode() == 404) {
                    throw new Exception("Cannot find remote file:" + urlStr);
                }
                is = httpConnection.getInputStream();
                fos = new FileOutputStream(dest, append);
                byte buffer[] = new byte[4096];
                int readsize = 0;
                while((readsize = is.read(buffer)) > 0){
                    fos.write(buffer, 0, readsize);
                    totalSize += readsize;
                    if((downloadCount == 0)||(int) (totalSize*100/updateTotalSize)-8>downloadCount){ 
                        downloadCount += 8;
                        updateNotification.contentView.setProgressBar(
                                processbarViewID, 100, (int)(totalSize*100/updateTotalSize), false);
                        updateNotification.contentView.setTextViewText(
                                textViewID, (int)(totalSize*100/updateTotalSize)+"%");
                        updateNotificationManager.notify(iconID, updateNotification);
                    }
                }
            }finally {
                if(httpConnection != null) {
                    httpConnection.disconnect();
                }
                if(is != null) {
                    is.close();
                }
                if(fos != null) {
                    fos.close();
                }
            }
            return totalSize;
        }
    }


    //检查最新版本信息
    public static UpdateInfo check(String checkUrl) {
        UpdateInfo info = null;

        try {
            String json = HttpUtils.invokeText(checkUrl);
            if(json != null && json.trim().length() > 0) {
                info = new UpdateInfo();
                JSONObject jsonObject = new JSONObject(json);
                info.setVersionCode(jsonObject.getInt("version-code"));
                info.setVersionName(jsonObject.getString("version-name"));
                info.setApk(jsonObject.getString("apk"));
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                info.setTime(dateFormat.parse(jsonObject.getString("time")));
                JSONArray descJson = jsonObject.getJSONArray("description");
                if(descJson != null && descJson.length() > 0) {
                    StringBuffer description = new StringBuffer();
                    for(int i=0; i<descJson.length(); i++) {
                        description.append(descJson.getString(i));
                        if(i < description.length()-1) {
                            description.append("\r\n");
                        }
                    }
                    info.setDescription(description.toString());
                }
            }
        } catch (Exception e) {
            info = null;
            e.printStackTrace();
        }

        return info;
    }

    public static void update(final String checkUrl, final int currentVersionCode,
                              final int notificationIconResId, final boolean repeat) {
        new Thread() {
            public void run() {
                UpdateInfo info = check(checkUrl);

                if(info != null) {
                    Date now = new Date();
//                    System.out.println("##### now is after " + info.getTime() + " = " + now.after(info.getTime()));
                    if(info.getVersionCode() > currentVersionCode
                            && now.after(info.getTime())) {
                        if(repeat || !isNoRemindVersion(info.getVersionCode())) {
                            Env.topActivity.runOnUiThread(new UpdateThread(Env.topActivity, info,
                                    notificationIconResId));
                        }
                    }
                }
            }
        }.start();
    }

    public static class UpdateInfo {
        private int versionCode;
        private String versionName;
        private String description;
        private String apk;
        private Date time;

        public int getVersionCode() {
            return versionCode;
        }

        public void setVersionCode(int versionCode) {
            this.versionCode = versionCode;
        }

        public String getVersionName() {
            return versionName;
        }

        public void setVersionName(String versionName) {
            this.versionName = versionName;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getApk() {
            return apk;
        }

        public void setApk(String apk) {
            this.apk = apk;
        }

        public Date getTime() {
            return time;
        }

        public void setTime(Date time) {
            this.time = time;
        }
    }

    private static class UpdateThread implements Runnable {
        private Activity activity;
        private UpdateInfo updateInfo;
        private int notificationIconResId;

        public UpdateThread(Activity activity, UpdateInfo updateInfo,
                            int notificationIconResId) {
            this.activity = activity;
            this.updateInfo = updateInfo;
            this.notificationIconResId = notificationIconResId;
        }

        public void run() {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(Env.topActivity);
            alertBuilder.setTitle("软件升级")
                .setPositiveButton("更新", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //开启下载服务
                        Intent serviceIntent =
                                new Intent(Env.topActivity, UpdateService.class);
                        //参数ID参考布局文件update_notification.xml
                        serviceIntent.putExtra("iconID", notificationIconResId);//图标
//                        serviceIntent.putExtra("titleID", notificationTitle);//标题
//                        serviceIntent.putExtra("layoutID", R.layout.update_notification);//update_notification.xml，可以修改成其他布局
//                        serviceIntent.putExtra("textViewID", R.id.update_notification_progresstext);//显示文字
//                        serviceIntent.putExtra("blockViewID", R.id.update_notification_progressblock);//兼容2.2一下版本进度条的bug而设立的区域布局
//                        serviceIntent.putExtra("processbarViewID", R.id.update_notification_progressbar);//进度条
                        serviceIntent.putExtra("downloadUrl", updateInfo.getApk());
                        serviceIntent.putExtra("versionCode", updateInfo.getVersionCode());
                        Env.topActivity.startService(serviceIntent);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .setView(createUpdateDialog());
            alertBuilder.create().show();
        }

        /**
         * 弹出自动更新的对话框自定义布局
         * @return
         */
        private View createUpdateDialog(){

            LinearLayout linearLayout=new LinearLayout(activity);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setPadding(12, 5, 12, 0);
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.FILL_PARENT);

            TextView textView = new TextView(activity);
            textView.setPadding(5, 0, 5, 0);
            textView.setText(updateInfo.getDescription());
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,18f);
            textView.setTextColor(Color.WHITE);

            CheckBox checkBox =new CheckBox(activity);
            checkBox.setText("不再提醒");
            checkBox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener(){
                public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                    createUpdateRemind(isChecked, updateInfo.getVersionCode());
                }

            });

            linearLayout.addView(textView,param);
            linearLayout.addView(checkBox);

            return linearLayout;
        }

        /**
         * "不再提醒"选中框关联数据库
         * @param noRemindChecked
         * @param noRemindVersionCode
         */
        private void createUpdateRemind(boolean noRemindChecked,int noRemindVersionCode){
            String remindKey = "NoRemindVersionCode";
            SQLiteDatabase remindDb = Env.dbHelper.getWritableDatabase();
            try{
                if(noRemindChecked){
                    //选中不再提醒，此次更新版本不再提示更新
                    remindDb.execSQL("insert into " + DBHelper.INFO_TABLE + "(name,value)" +
                            " values('" + remindKey + "'," + noRemindVersionCode + ")");

                }else{
                    //取消选中
                    remindDb.execSQL("delete from " + DBHelper.INFO_TABLE +" where name='" +
                            remindKey + "' and value='" + noRemindVersionCode+"'");
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * 检查用户是否不再提醒
     * @return
     */
    private static boolean isNoRemindVersion(int versionCode){
        boolean isNoRemind = false;
        String remindKey = "NoRemindVersionCode";
        SQLiteDatabase remindDb = Env.dbHelper.getWritableDatabase();
        try{
            Cursor remindCursor = null;
            remindCursor = remindDb.rawQuery("select * from " + DBHelper.INFO_TABLE +
                    " where name='" + remindKey + "' and value='" + versionCode + "'", null);
            try{
                if(remindCursor != null && remindCursor.getCount() > 0){
                    isNoRemind = true;
                }else{
                    isNoRemind = false;
                }
            }finally{
                if(remindCursor != null){
                    remindCursor.close();
                    remindCursor = null;
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return isNoRemind;
    }
}

