package com.alvin.app;

import cn.com.pcgroup.android.framework.db.DBHelper;

import com.alvin.api.config.Env;
import com.alvin.common.R;
import com.alvin.common.utils.CacheUtils;
import com.alvin.exception.CrashHandler;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Field;

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
        
        //获取屏幕分辨率和密度
        DisplayMetrics metrics = this.getApplicationContext().getResources().getDisplayMetrics();
        Env.screenWidth = metrics.widthPixels;
        Env.screenHeight = metrics.heightPixels;
        Env.density = metrics.density;
        
        //获取状态栏的高度
        Env.statusBarHeight = getStatusBarHeight();
        
        //获取标题栏高度
        Env.titleBarHeight = getTitleBarHeight();
        
        //获取底部tab高度
        Env.tabHeight = getTabHeight();

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
    
    /**
     * 获取状态栏高度/像素
     * @return
     */
    private int getStatusBarHeight(){
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return statusBarHeight;
    }
    
    /**
     * 获取标题栏的高度（实质上获取的是状态栏背景图片的高度）/像素
     * @return
     */
    private int getTitleBarHeight(){
        return this.getResources().getDrawable(R.drawable.app_top_banner_layout_background).getIntrinsicHeight();
    }
    
    /**
     * 获取底部Tab高度（实质上获取的是图片的高度）/像素
     * @return
     */
    private int getTabHeight(){
        return this.getResources().getDrawable(R.drawable.app_information_tab_default).getIntrinsicHeight();
    }
    
    private String schema; //协议头
    private String sdName;//文件在sd卡上存放目录名字
    public void setSdName(String sdName) {
        this.sdName = sdName;
    }
    
    public void setSchema(String schema) {
        this.schema = schema;
    }
}
