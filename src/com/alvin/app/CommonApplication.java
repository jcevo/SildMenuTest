package com.alvin.app;

import com.alvin.api.config.Env;
import com.alvin.common.utils.CacheUtils;
import com.alvin.common.utils.CountUtils;
import com.alvin.db.DBHelper;
import com.alvin.exception.CrashHandler;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.File;

/**
 * 客户端共用主程序
 */
public class CommonApplication extends Application {
    private final static String TAG = CommonApplication.class.getSimpleName();

    private CrashHandler crashHandler;

    private String appHome;     //应用根目录

    private final static String DATABASE_NAME = "application.db";   //数据库名称
    private int databaseVersion;                //数据库版本号
    private String databaseInitSQL;      //数据库初始化语句
    private String databaseUpdateSQL;    //数据库更新语句

    private boolean isBranch = false;
    private Initializer initializer;

    public CommonApplication() {
        super();

        //初始化Crash处理器
        crashHandler = CrashHandler.getInstance();
        crashHandler.init(CrashHandler.REPORT_TYPE_FILE);
    }

    public void onCreate() {
        super.onCreate();

        //获取版本信息
        Env.telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            PackageInfo packageInfo = this.getPackageManager().getPackageInfo(
                    this.getPackageName(), PackageManager.GET_CONFIGURATIONS);
            Env.client = packageInfo.packageName;
            Env.versionCode = packageInfo.versionCode;
            Env.versionName = packageInfo.versionName;
        } catch(PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Get app info error");
            e.printStackTrace();
        }

        //创建或更新数据库
        Env.dbHelper = new DBHelper(this.getApplicationContext(), databaseVersion,
                DATABASE_NAME, databaseInitSQL, databaseUpdateSQL);

        String branchDir = "";
        if(isBranch) {
            branchDir = "/" + Env.client;
        }

        //初始化默认Crash日志目录
        CrashHandler.getInstance().setLogFileDir(
                new File(Environment.getExternalStorageDirectory(),
                        appHome + branchDir + "/log"));

        //初始化缓存目录
        CacheUtils.cacheDirInternal = new File(this.getCacheDir(), "app");
        CacheUtils.cacheDirExternal = new File(Environment.getExternalStorageDirectory(),
                appHome + branchDir + "/cache/app");
        Env.externalFileDir = new File(Environment.getExternalStorageDirectory(),
                appHome + branchDir + "/file");

        if(!CacheUtils.cacheDirInternal.exists() || !CacheUtils.cacheDirInternal.isDirectory()) {
            CacheUtils.cacheDirExternal.mkdirs();
        }
        if(Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            if(!CacheUtils.cacheDirExternal.exists()
                    || !CacheUtils.cacheDirExternal.isDirectory()) {
                CacheUtils.cacheDirExternal.mkdirs();
            }
            if(!Env.externalFileDir.exists() || !Env.externalFileDir.isDirectory()) {
                Env.externalFileDir.mkdirs();
            }
        }

        //初始化临时存储目录
        CacheUtils.tempCacheDirInternal = new File(CacheUtils.cacheDirInternal, "temp");
        CacheUtils.tempCacheDirExternal = new File(CacheUtils.cacheDirExternal, "temp");
        if(!CacheUtils.tempCacheDirInternal.exists() || CacheUtils.tempCacheDirInternal.isFile()) {
            CacheUtils.tempCacheDirInternal.mkdirs();
        }
        if(!CacheUtils.tempCacheDirExternal.exists() || CacheUtils.tempCacheDirExternal.isFile()) {
            CacheUtils.tempCacheDirExternal.mkdirs();
        }

        CacheUtils.startCacheCleaner();
    }

    public void onLowMemory(){
        CacheUtils.clearTempCache();
        super.onLowMemory();
    }

    public void onTerminate(){
        CacheUtils.clearTempCache();
        Env.dbHelper.close();
        super.onTerminate();
    }

    public CrashHandler getCrashHandler() {
        return crashHandler;
    }

    public void setAppId(int appId) {
        Env.appID = appId;
    }

    public void setDownloadReference(String ref) {
        Env.downloadReference = ref;
    }

    public void setAppHome(String appHome) {
        this.appHome = appHome;
    }

    public void setDatabaseVersion(int databaseVersion) {
        this.databaseVersion = databaseVersion;
    }

    public void setDatabaseInitSQL(String databaseInitSQL) {
        this.databaseInitSQL = databaseInitSQL;
    }

    public void setDatabaseUpdateSQL(String databaseUpdateSQL) {
        this.databaseUpdateSQL = databaseUpdateSQL;
    }

    public File getExternalFileDirectory() {
        return Env.externalFileDir;
    }

    public void exit(final Activity activity) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);
        alertBuilder.setTitle("退出应用？")
                .setMessage("确定退出应用？")
                .setPositiveButton("退出",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                CacheUtils.clearTempCache();
                                Env.dbHelper.close();
                                CountUtils.updateUseTime(true);
//                                System.out.println("##### exit - " + activity);

                                // 退出应用
//                                activity.onBackPressed();
//                                System.exit(0);
//                                activity.finish();
                                android.os.Process.killProcess(android.os.Process.myPid());
                            }
                        }
                ).setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        }
                );
        alertBuilder.create().show();
    }

    Initializer getInitializer() {
        return initializer;
    }

    protected void setInitializer(Initializer initializer) {
        this.initializer = initializer;
    }

    public boolean isBranch() {
        return isBranch;
    }

    public void setBranch(boolean branch) {
        isBranch = branch;
    }
}
