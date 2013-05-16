package com.alvin.api.abstractClass;

import cn.com.pcgroup.android.framework.cache.CacheManager;
import cn.com.pcgroup.android.framework.db.DBHelper;

import com.alvin.api.config.Config;
import com.alvin.api.config.Env;
import com.alvin.api.utils.CommonSettingsUtils;
import com.alvin.api.utils.PhoneInfoUtils;
import com.alvin.app.Initializer;
import com.alvin.common.R;
import com.alvin.common.utils.CacheUtils;
import com.alvin.db.AlvinDBHelper;
import com.alvin.exception.CrashHandler;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Field;

/**
 * APP43客户端主程序
 */
public class Alvin_Application extends Application {

    protected final static String TAG = Alvin_Application.class.getSimpleName();
    // 安装来源计数器
    public final static String DOWNLOAD_REFERENCE_AVGRIL = "AvGril";

    // public static String sql = DBApp_download.sql + DBUser_behavior.sql
    // + DBFavourite.sql;
    private int backKeyPressTimes = 0;

    public static boolean activityIsFinish = false, serviceIsFinish = true;// 用来判断是否关闭进程
    public static boolean isMenuExit = false;

    private CrashHandler crashHandler;

    private String appHome; // 应用根目录

    private final static String DATABASE_NAME = "application.db"; // 数据库名称
    private int databaseVersion; // 数据库版本号
    private String databaseInitSQL; // 数据库初始化语句
    private String databaseUpdateSQL; // 数据库更新语句

    private boolean isBranch = false;
    private Initializer initializer;

    // 应用数据库初始化语句
    private final static String INIT_APP_TABLE_SQL = "CREATE TABLE IF NOT EXISTS " + Config.FAVORITES_TABLE + " ("
            + "    id INTEGER PRIMARY KEY, " + "    url TEXT, " + "    org_url TEXT, " + "    org_id TEXT, "
            + "    title TEXT, " + "    time NUMERIC, " + "    type INTEGER " + ");" + "CREATE TABLE IF NOT EXISTS "
            + Config.USER_FAVORITES_TABLE + " (" + "    id INTEGER PRIMARY KEY, " + "    url TEXT, "
            + "    org_url TEXT, " + "    org_id TEXT, " + "    title TEXT, " + "    time NUMERIC, "
            + "    type INTEGER " + ");" + "CREATE TABLE IF NOT EXISTS " + Config.PASSPORT_TABLE + " ("
            + "    passport_id INTEGER PRIMARY KEY, " + "    session_id NVARCHAR(255), "
            + "    username NVARCHAR(255), " + "    password NVARCHAR(255), " + "    nick_name NVARCHAR(255), "
            + "    personal_brief TEXT, " + "    login_time NUMERIC " + ");" + "CREATE TABLE IF NOT EXISTS "
            + Config.PLATFORM_USER_TABLE + " (" + "    id INTEGER PRIMARY KEY, " + "    token NVARCHAR(255), "
            + "    secret NVARCHAR(255), " + "    platform_user_id INTEGER, " + "    platform INTEGER, "
            + "    passport_id INTEGER, " + "    nick_name NVARCHAR(255) " + ");" + "CREATE TABLE IF NOT EXISTS "
            + Config.BBS_HISTORY_TABLE + " (" + "    id INTEGER PRIMARY KEY, " + "    topic_id INTEGER, "
            + "    title TEXT, " + "    topic_url TEXT, " + "    visit_time TEXT " + ");"
            + "CREATE TABLE IF NOT EXISTS " + Config.BBS_FAVORITE_TABLE + " (" + "    id INTEGER PRIMARY KEY, "
            + "    forum_id INTEGER, " + "    forum_name TEXT, " + "    forum_logo TEXT, " + "    forum_order INTEGER "
            + ");" + "CREATE TABLE IF NOT EXISTS " + Config.CHANNEL_TABLE + " (" + "    id INTEGER PRIMARY KEY, "
            + "    channel_id INTEGER, " + "    channel_name TEXT, " + "    channel_order REAL, "
            + "    channel_display TEXT, " + "    channel_advert INTEGER, " + "    channel_type TEXT " + ");"
            + "CREATE TABLE IF NOT EXISTS " + Config.GAMES_CHANNEL_TABLE + " (" + "    id INTEGER PRIMARY KEY, "
            + "    channel_id INTEGER, " + "    channel_name TEXT, " + "    channel_order REAL, "
            + "    channel_display TEXT, " + "    channel_advert INTEGER, " + "    channel_type TEXT ,"
            + "    channel_time INTEGER ," + "    parent_id INTEGER " + ");" + "CREATE TABLE IF NOT EXISTS "
            + Config.ARTICLE_SEE_HISTORY_TABLE + " (" + "    id INTEGER PRIMARY KEY, " + "    column_id INTEGER, "
            + "    column_title NVARCHAR(255), " + "    article_id INTEGER, " + "    article_title NVARCHAR(255), "
            + "    look_time NUMERIC " + ");" + "CREATE TABLE IF NOT EXISTS " + Config.USER_ACTIVITY_TABLE + " ("
            + "    id INTEGER PRIMARY KEY, " + "    event TEXT" + ");";

    public void onCreate() {
        super.onCreate();

        if (CommonSettingsUtils.context == null) {
            CommonSettingsUtils.context = this;
        }

        Initializer init = new Initializer() {
            @Override
            public void init(Context context) {
            }
        };
        this.setInitializer(init);
        // 初始化全局日志TAG
        // Env.logTagPrefix = "APP43Client_";

        // 设置安装包来源
        // Env.DOWNLOAD_REFERENCE = DOWNLOAD_REFERENCE_APP43;

        this.setAppHome("MyApp");

        // 获取版本信息
        Env.telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            PackageInfo packageInfo = this.getPackageManager().getPackageInfo(this.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);
            Env.client = packageInfo.packageName;
            Env.versionCode = packageInfo.versionCode;
            Env.versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Get app info error");
            e.printStackTrace();
        }

        // 获取屏幕分辨率和密度
        DisplayMetrics metrics = this.getApplicationContext().getResources().getDisplayMetrics();
        Env.screenWidth = metrics.widthPixels;
        Env.screenHeight = metrics.heightPixels;
        Env.density = metrics.density;

        // 获取状态栏的高度
        Env.statusBarHeight = getStatusBarHeight();

        // 获取标题栏高度
        Env.titleBarHeight = getTitleBarHeight();

        // 获取底部tab高度
        Env.tabHeight = getTabHeight();

        String branchDir = "";
        if (isBranch) {
            branchDir = "/" + Env.client;
        }
        // 初始化数据库和缓存目录4.0
         Env.dbHelper = new DBHelper(this, 400, "PcgroupBrowser.db",
         INIT_APP_TABLE_SQL, "");
         CacheManager.initCacheDir(sdName, this, Env.dbHelper);

//        // 创建或更新数据库旧版本
//        Env.dbHelper = new DBHelper(this.getApplicationContext(), databaseVersion, DATABASE_NAME, databaseInitSQL,
//                databaseUpdateSQL);
//
//        // 数据库设置
//        this.setDatabaseVersion(CommonSettingsUtils.DATABASEVERSION);
//        // this.setDatabaseInitSQL(sql);
//        this.setDatabaseUpdateSQL(null);

        // 初始化Crash处理器
        crashHandler = CrashHandler.getInstance();
        crashHandler.init(CrashHandler.REPORT_TYPE_FILE);
        // 初始化默认Crash日志目录
        crashHandler.setLogFileDir(new File(Environment.getExternalStorageDirectory(), appHome + branchDir + "/log"));

        // 初始化缓存目录
        CacheUtils.cacheDirInternal = new File(this.getCacheDir(), "app");
        CacheUtils.cacheDirExternal = new File(Environment.getExternalStorageDirectory(), appHome + branchDir
                + "/cache/app");
        Env.externalFileDir = new File(Environment.getExternalStorageDirectory(), appHome + branchDir + "/file");

        if (!CacheUtils.cacheDirInternal.exists() || !CacheUtils.cacheDirInternal.isDirectory()) {
            CacheUtils.cacheDirExternal.mkdirs();
        }
        if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            if (!CacheUtils.cacheDirExternal.exists() || !CacheUtils.cacheDirExternal.isDirectory()) {
                CacheUtils.cacheDirExternal.mkdirs();
            }
            if (!Env.externalFileDir.exists() || !Env.externalFileDir.isDirectory()) {
                Env.externalFileDir.mkdirs();
            }
        }

        // 初始化临时存储目录
        CacheUtils.tempCacheDirInternal = new File(CacheUtils.cacheDirInternal, "temp");
        CacheUtils.tempCacheDirExternal = new File(CacheUtils.cacheDirExternal, "temp");
        if (!CacheUtils.tempCacheDirInternal.exists() || CacheUtils.tempCacheDirInternal.isFile()) {
            CacheUtils.tempCacheDirInternal.mkdirs();
        }
        if (!CacheUtils.tempCacheDirExternal.exists() || CacheUtils.tempCacheDirExternal.isFile()) {
            CacheUtils.tempCacheDirExternal.mkdirs();
        }

        CacheUtils.startCacheCleaner();
    }

    public void onLowMemory() {
        CacheUtils.clearTempCache();
        super.onLowMemory();
    }

    public void onTerminate() {
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
     * 
     * @return
     */
    private int getStatusBarHeight() {
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
     * 
     * @return
     */
    private int getTitleBarHeight() {
        return this.getResources().getDrawable(R.drawable.app_top_banner_layout_background).getIntrinsicHeight();
    }

    /**
     * 获取底部Tab高度（实质上获取的是图片的高度）/像素
     * 
     * @return
     */
    private int getTabHeight() {
        return this.getResources().getDrawable(R.drawable.app_information_tab_default).getIntrinsicHeight();
    }

    private String schema; // 协议头
    private String sdName;// 文件在sd卡上存放目录名字

    public void setSdName(String sdName) {
        this.sdName = sdName;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public void exit(final Activity activity) {
        if (isMenuExit) {
            PackageManager pm = Alvin_Application.this.getPackageManager();
            ApplicationInfo piInfo = Alvin_Application.this.getApplicationInfo();
            PhoneInfoUtils.setApp_name(piInfo.loadLabel(pm).toString());
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);
            alertBuilder.setTitle(CommonSettingsUtils.DIALOG_EXIT_TIPS)
                    .setMessage(CommonSettingsUtils.DIALOG_EXIT_MESSAGE + PhoneInfoUtils.getApp_name() + " ?")
                    .setPositiveButton(CommonSettingsUtils.DIALOG_EXIT_SURE, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            CacheUtils.clearTempCache();
                            Env.dbHelper.close();

                            // 退出应用
                            // activity.onBackPressed();
                            // System.exit(0);
                            // 要保持后台下载程序
                            if (serviceIsFinish) {
                                android.os.Process.killProcess(android.os.Process.myPid());
                            } else {
                                activityIsFinish = true;
                                activity.finish();
                            }
                        }
                    }).setNegativeButton(CommonSettingsUtils.DIALOG_EXIT_CANCLE, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            alertBuilder.create().show();
            isMenuExit = false;
            return;
        }
        if (serviceIsFinish) {
            if (backKeyPressTimes == 0) {
                Toast.makeText(Alvin_Application.this, "再按一次返回键退出", Toast.LENGTH_LONG).show();
                backKeyPressTimes = 1;
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            backKeyPressTimes = 0;
                        }
                    }
                }).start();
            } else {
                android.os.Process.killProcess(android.os.Process.myPid());
            }

        } else {
            activityIsFinish = true;
            activity.finish();
        }
    }
}
