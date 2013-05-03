package com.alvin.api.abstractClass;

import com.alvin.api.config.Env;
import com.alvin.api.utils.CommonSettingsUtils;
import com.alvin.api.utils.PhoneInfoUtils;
import com.alvin.app.CommonApplication;
import com.alvin.app.Initializer;
import com.alvin.common.utils.CacheUtils;
import com.alvin.exception.CrashHandler;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.widget.Toast;

/**
 * APP43客户端主程序
 */
public class Alvin_Application extends CommonApplication {

    protected final static String TAG = Alvin_Application.class.getSimpleName();
    // 安装来源计数器
    public final static String DOWNLOAD_REFERENCE_AVGRIL = "AvGril";

    // public static String sql = DBApp_download.sql + DBUser_behavior.sql
    // + DBFavourite.sql;
    private int backKeyPressTimes = 0;

    public static boolean activityIsFinish = false, serviceIsFinish = true;// 用来判断是否关闭进程
    public static boolean isMenuExit = false;

    public Alvin_Application() {
        super();

        if (CommonSettingsUtils.context == null) {
            CommonSettingsUtils.context = this;
        }
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(CrashHandler.REPORT_TYPE_FILE);
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

        this.setAppHome("AvGril");

        // 数据库设置
        this.setDatabaseVersion(CommonSettingsUtils.DATABASEVERSION);
        // this.setDatabaseInitSQL(sql);
        this.setDatabaseUpdateSQL(null);
    }

    public void exit(final Activity activity) {
        if (isMenuExit) {
            PackageManager pm = Alvin_Application.this.getPackageManager();
            ApplicationInfo piInfo = Alvin_Application.this
                    .getApplicationInfo();
            PhoneInfoUtils.setApp_name(piInfo.loadLabel(pm).toString());
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);
            alertBuilder
                    .setTitle(CommonSettingsUtils.DIALOG_EXIT_TIPS)
                    .setMessage(
                            CommonSettingsUtils.DIALOG_EXIT_MESSAGE
                                    + PhoneInfoUtils.getApp_name() + " ?")
                    .setPositiveButton(CommonSettingsUtils.DIALOG_EXIT_SURE,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                        int id) {
                                    CacheUtils.clearTempCache();
                                    Env.dbHelper.close();

                                    // 退出应用
                                    // activity.onBackPressed();
                                    // System.exit(0);
                                    // 要保持后台下载程序
                                    if (serviceIsFinish) {
                                        android.os.Process
                                                .killProcess(android.os.Process
                                                        .myPid());
                                    } else {
                                        activityIsFinish = true;
                                        activity.finish();
                                    }
                                }
                            })
                    .setNegativeButton(CommonSettingsUtils.DIALOG_EXIT_CANCLE,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                        int id) {
                                    dialog.cancel();
                                }
                            });
            alertBuilder.create().show();
            isMenuExit = false;
            return;
        }
        if (serviceIsFinish) {
            if (backKeyPressTimes == 0) {
                Toast.makeText(Alvin_Application.this, "再按一次返回键退出",
                        Toast.LENGTH_LONG).show();
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
