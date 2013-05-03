package com.alvin.api.config;

import com.alvin.db.DBHelper;

import android.app.Activity;
import android.telephony.TelephonyManager;
import android.view.Display;

import java.io.File;

/**
 * 通用系统环境变量，须由应用进行初始化，供其他通用API使用
 */
public class Env {
    //设备信息
    public static TelephonyManager telephonyManager;

    //当前版本信息
    public static int appID;                    //应用ID，供用户行为分析系统使用
    public static String client;                //应用名，当前APP的包名
    public static int versionCode;              //版本号，对应于Manifest文件里的versionCode
    public static String versionName;           //用作显示的版本号，对应于Manifest文件里的versionName

    //安装来源
    public static String downloadReference;     //安装来源

    //应用存储环境信息
    public static File externalFileDir;         //文件存放目录

    //应用辅助类信息
    public static Display display;              //显示参数类
    public static DBHelper dbHelper;            //数据库辅助静态类

    //应用运行环境信息
    public static Activity topActivity;         //当前显示的activity
}
