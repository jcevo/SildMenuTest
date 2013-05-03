package com.alvin.db;

/**
 * 下载文件夹数据表
 */
import com.alvin.api.bean.Bean;
import com.alvin.api.utils.LogOutputUtils;
import com.alvin.api.utils.CommonSettingsUtils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.HashMap;
import java.util.Map;

public class DBApp_download extends DbHandle {

    public final static String sql = "CREATE TABLE IF NOT EXISTS "
            + CommonSettingsUtils.APP_DOWNLOAD + " ("
            + "  id INTEGER primary key autoincrement, " + " app_id INTEGER ," // appID
            + CommonSettingsUtils.DB_DOWNLOAD_NAME + "  NVARCHAR(255) ,  " // 程序名
            + CommonSettingsUtils.DB_DOWNLOAD_DOWNLOADURL + " NVARCHAR(255)  ," // 下载的url
            + CommonSettingsUtils.DB_DOWNLOAD_UPDATE_TIME + " NVARCHAR(255)  ," // 更新包的时间
            + CommonSettingsUtils.DB_DOWNLOAD_DATE + "  NUMERIC  ," // 程序点击下载日期
            + CommonSettingsUtils.DB_DOWNLOAD_STATE + " INTEGER, " // 下载状态:1,安装;0,未安装
            + CommonSettingsUtils.DB_DOWNLOAD_PROGRESS + "  INTEGER " // 下载进度

            // + " package NVARCHAR(255)  ," // 包名
            // + " ver_code INTEGER  , " // 版本代码 开发者自定义
            // + " ver_name INTEGER ,"// 版本名称 显示用户看
            // + " category_name NVARCHAR(255)," // 分类名称
            // + " category_id INTEGER," // 分类 ID
            // + " content_url NVARCHAR(255),"// app内容url
            // + " icon_url NVARCHAR(255),"// 图片URL
            // + " apk NVARCHAR(255)"// apkurl
            + ");";

    // 修改数据库都要相应修改ApkDownloadDb
    public DBApp_download(Context context, int version) {
        super(context, version);
    }

    /**
     * 插入数据
     * 
     * 
     * @return true 则插入成功
     */
    public boolean insertProgress(Bean bean, int progress) {
        boolean flag = true;
        ContentValues initialValues = new ContentValues();
        initialValues.put(CommonSettingsUtils.DB_DOWNLOAD_NAME, bean.getTitle());
        initialValues.put(CommonSettingsUtils.DB_DOWNLOAD_DOWNLOADURL,
                bean.getDownloadUrl());
        initialValues.put(CommonSettingsUtils.DB_DOWNLOAD_DATE, bean.getDownDate());
        initialValues.put(CommonSettingsUtils.DB_DOWNLOAD_PROGRESS,
                bean.getProgress());
        initialValues.put(CommonSettingsUtils.DB_DOWNLOAD_STATE, bean.getState());
        initialValues.put(CommonSettingsUtils.DB_DOWNLOAD_UPDATE_TIME,
                bean.getUpdateTime());
        beginTrans();
        Map<String, Integer> map = scanDownloadApk(bean.getTitle());
        if (map.containsKey(bean.getTitle())) {
            // flag = db.update(
            // Settings.APP_DOWNLOAD,
            // initialValues,
            // "package='" + app.getPackageName() + "' and date ="
            // + app.getDownDate(), null) > 0;// 若initialvalues为空时则插入null
            // 如果有则删除之前的记录
            delete(bean.getTitle(), "点击下载删除旧数据");
        } else {
            LogOutputUtils.e("dbapp_downaloda", "开始下载下载名字" + bean.getTitle()
                    + "||下载时间:" + bean.getDownDate());
            flag = db.insert(CommonSettingsUtils.APP_DOWNLOAD, null, initialValues) > 0;// 若initialvalues为空时则插入null
        }
        endTrans();
        return flag;
    }

    public boolean updateAppProgress(String apkName, Long currentTime,
            int progress) {
        boolean flag = true;
        ContentValues initialValues = new ContentValues();
        initialValues.put("progress", progress);
        LogOutputUtils.e("dbapp_downaloda", "更新进度下载名字" + apkName + "||下载时间:"
                + currentTime);
        beginTrans();
        flag = db.update(CommonSettingsUtils.APP_DOWNLOAD, initialValues, "name='"
                + apkName + "' and date =" + currentTime, null) > 0;// 若initialvalues为空时则插入null
        endTrans();
        return flag;
    }

    /**
     * 
     * 作用: 更新数据库设置是否已经安装完成
     * 
     * @state 1:安装完成;0,未安装
     * 
     * @return
     */
    // 安装完成修改数据库
    public boolean updateInstallState(String name, int state) {
        LogOutputUtils.i("列表数据库更新状态", name + "安装?" + state);
        boolean flag = true;
        ContentValues initialValues = new ContentValues();
        initialValues.put(CommonSettingsUtils.DB_DOWNLOAD_STATE, state); // 已经安装完成
        beginTrans();
        flag = db.update(CommonSettingsUtils.APP_DOWNLOAD, initialValues,
                CommonSettingsUtils.DB_DOWNLOAD_NAME + "='" + name + "'", null) > 0;// 若initialvalues为空时则插入null
        endTrans();
        LogOutputUtils.e("数据库更新状态完毕", "state: " + flag);
//        getDownloadApp();
        return flag;
    }

    //
    // public boolean delUpdateState(String packName, int state) {
    // LogOutputUtils.i("应用管理数据库更新状态", packName + "安装?" + state);
    // boolean flag = true;
    // ContentValues initialValues = new ContentValues();
    // initialValues.put("state", state); // 已经安装完成
    // beginTrans();
    // flag = db.update(SettingsUtils.APP_DOWNLOAD, initialValues, "package='"
    // + packName + "'", null) > 0;// 若initialvalues为空时则插入null
    // endTrans();
    // LogOutputUtils.e("数据库更新状态完毕", "state: " + flag);
    // getDownloadApp();
    // return flag;
    // }

    /**
     * 一般的删除数据
     * 
     * @param packageName
     * @return true 则删除成功
     */
    public boolean delete(String appName, String where) {
        LogOutputUtils.e("dbAPp_del", "从哪里删除: " + where + " || " + appName);
        boolean flag = true;
        beginTrans();
        flag = db.delete(CommonSettingsUtils.APP_DOWNLOAD,
                "name = '" + appName + "'", null) > 0;
        endTrans();
        return flag;
    }

    /**
     * 查询所有收藏夹
     * 
     * @return
     */
    public Cursor queryAllRow() {
        Cursor cursor;
        beginTrans();
        cursor = db.rawQuery("select distinct  *  from "
                + CommonSettingsUtils.APP_DOWNLOAD + " order by date desc", null);
        endTrans();
        return cursor;
    }

    /**
     * 扫描下载的数据库
     */
    private Map<String, Integer> scanDownloadApk(String apkName) {
        Map<String, Integer> map = new HashMap<String, Integer>();// 存放包名
        Map<String, Long> timeMap = new HashMap<String, Long>();
        Cursor mcursor = queryAllRow();
        mcursor.moveToFirst();
        for (int i = 0; i < mcursor.getCount(); i++) {
            String name = mcursor.getString(mcursor.getColumnIndex("name"));
            Long time = mcursor.getLong(mcursor.getColumnIndex("date"));
            int progress = mcursor.getInt(mcursor.getColumnIndex("progress"));
            if (apkName.equals(name)) {
                delete(name, "续传下载删除原来的");
                continue;
            }
            map.put(name, progress);
            timeMap.put(name, time);
            mcursor.moveToNext();
            // System.out.println(i+"app_id"+Apps[i]);
        }
        if (mcursor != null) {
            mcursor.close();
        }
        return map;
    }

    // map<title, apkDownload>
    // public Map<String, App> getAllDownloadApk() {
    // Map<String, App> map = new HashMap<String, App>();// 存放程序名
    // Cursor mcursor = queryAllRow();
    // mcursor.moveToFirst();
    // map = getmap(map, mcursor);
    // if (mcursor != null) {
    // mcursor.close();
    // }
    // return map;
    // }

    /**
     * 
     * 作用:从数据库读出数据来转换成映射表
     */
    // private Map<String, App> getmap(Map<String, App> map, Cursor mcursor) {
    // SimpleDateFormat dateFormat = new SimpleDateFormat(
    // "yyyy-mm-dd HH:MM:SS");
    // int lenth = mcursor.getCount();
    // for (int i = 0; i < lenth; i++) {
    // App apkDownloadDb = addApp(mcursor);
    // String name = mcursor.getString(mcursor.getColumnIndex("name"));
    // map.put(name, apkDownloadDb);
    // mcursor.moveToNext();
    // // System.out.println(i+"app_id"+Apps[i]);
    // LogOutputUtils.i(
    // "dbApp_download",
    // i + ": " + apkDownloadDb.getTitle() + "||state : "
    // + apkDownloadDb.getState() + "|| progree"
    // + apkDownloadDb.getProgress() + "||"
    // + dateFormat.format(apkDownloadDb.getDownDate()));
    // }
    // return map;
    // }

    // private App addApp(Cursor mcursor) {
    // App apkDownloadDb = new App();
    // apkDownloadDb.setCategory_id(mcursor.getLong(mcursor
    // .getColumnIndex("category_id")));
    // apkDownloadDb.setCategory_title(mcursor.getString(mcursor
    // .getColumnIndex("category_name")));
    // apkDownloadDb.setDownDate(mcursor.getLong(mcursor
    // .getColumnIndex("date")));
    // apkDownloadDb
    // .setTitle(mcursor.getString(mcursor.getColumnIndex("name")));
    // apkDownloadDb.setPackageName(mcursor.getString(mcursor
    // .getColumnIndex("package")));
    // apkDownloadDb.setProgress(mcursor.getInt(mcursor
    // .getColumnIndex("progress")));
    // apkDownloadDb
    // .setSize(mcursor.getString(mcursor.getColumnIndex("size")));
    // apkDownloadDb.setState(mcursor.getInt(mcursor.getColumnIndex("state")));
    // apkDownloadDb.setVerCode(mcursor.getInt(mcursor
    // .getColumnIndex("ver_code")));
    // apkDownloadDb.setVerName(mcursor.getString(mcursor
    // .getColumnIndex("ver_name")));
    // apkDownloadDb.setContentUrl(mcursor.getString(mcursor
    // .getColumnIndex("content_url")));
    // apkDownloadDb.setImage(mcursor.getString(mcursor
    // .getColumnIndex("icon_url")));
    // apkDownloadDb.setId(mcursor.getInt(mcursor.getColumnIndex("app_id")));
    // apkDownloadDb.setDownloadUrl(mcursor.getString(mcursor
    // .getColumnIndex("apk")));
    // return apkDownloadDb;
    // }

    /**
     * 
     * 作用:只供downloadAppAdapter使用
     * 
     * @param
     * 
     * @return String DOM对象
     */
    // public ArrayList<App> getDownloadApp() {
    // ArrayList<App> list = new ArrayList<App>();// 存放程序名
    // Cursor mcursor = queryAllRow();
    // mcursor.moveToFirst();
    // SimpleDateFormat dateFormat = new SimpleDateFormat(
    // "yyyy-mm-dd HH:MM:SS");
    // for (int i = 0; i < mcursor.getCount(); i++) {
    // App apkDownloadDb = addApp(mcursor);
    // list.add(apkDownloadDb);
    // mcursor.moveToNext();
    // // System.out.println(i+"app_id"+Apps[i]);
    // LogOutputUtils.e(
    // "dbApp_download",
    // i + ": " + apkDownloadDb.getTitle() + "||state : "
    // + apkDownloadDb.getState() + "|| progree"
    // + apkDownloadDb.getProgress() + "||"
    // + dateFormat.format(apkDownloadDb.getDownDate()));
    // }
    // if (mcursor != null) {
    // mcursor.close();
    // }
    // return list;
    // }
}
