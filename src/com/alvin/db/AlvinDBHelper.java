package com.alvin.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * 通用数据库协助类
 */
public class AlvinDBHelper extends SQLiteOpenHelper {
    private final static String TAG = AlvinDBHelper.class.getSimpleName();

    private static Map<SQLiteDatabase, Integer> dbMap = new HashMap<SQLiteDatabase, Integer>();
    private static int dbCount = 0;

    //通用数据库操作语句
    private static String dbName = "app.db";
    public final static String CACHE_TABLE = "app_cache";
    public final static String INFO_TABLE = "app_info";
    public final static String USER_TABLE = "app_user";
    public final static String USER_ACTIVITY_TABLE = "app_user_activity";
    public final static String USER_AUTHORITY_TABLE = "app_user_authority";

    private static String initSQL = 
            "CREATE TABLE IF NOT EXISTS " + CACHE_TABLE + " (" +
            "    id INTEGER PRIMARY KEY, " +
            "    key NVARCHAR(255), " +
            "    file NVARCHAR(255), " +
            "    size NUMERIC, " +
            "    status INTEGER, " +
            "    time NUMERIC, " +
            "    expire NUMERIC" +
            ");" +
            "CREATE TABLE IF NOT EXISTS " + INFO_TABLE + " (" +
            "    id INTEGER PRIMARY KEY, " +
            "    name NVARCHAR(255), " +
            "    value NVARCHAR(255), " +
            "    status INTEGER, " +
            "    time NUMERIC" +
            ");" + 
            "CREATE TABLE IF NOT EXISTS " + USER_TABLE + " (" +
            "    id INTEGER PRIMARY KEY, " +
            "    username NVARCHAR(255), " +
            "    password NVARCHAR(255), " +
            "    passport_id INTEGER, " +
            "    session_id NVARCHAR(255), " +
            "    personal_brief NVARCHAR(255), " +
            "    token NVARCHAR(255), " +
            "    secret NVARCHAR(255), " +
            "    auth_type NVARCHAR(255), " +
            "    oauth_user_id NUMERIC, " +
            "    is_passport INTEGER, " +
            "    oauth_nickname NVARCHAR(255), " +
            "    time NUMERIC " +
            ");" +
            "CREATE TABLE IF NOT EXISTS "+ USER_ACTIVITY_TABLE + " ("+
            "    id INTEGER PRIMARY KEY, "+
            "    event TEXT"+
            ");" +
            "CREATE TABLE IF NOT EXISTS "+ USER_AUTHORITY_TABLE + " ("+
            "    id INTEGER PRIMARY KEY, "+
            "    local_user_id INTEGER, "+
            "    authority_name NVARCHAR(255), " +
            "    status INTEGER " +
            ");";

//    private static String upgradeSQL =
//            "DROP TABLE IF EXISTS " + Config.FAVORITES_TABLE + ";" + initSQL;
    private static String upgradeSQL = initSQL;


    //仅使用通用数据库的构造函数
    public AlvinDBHelper(Context context, int version) {
        super(context, dbName, null, version);
    }

    //同时使用通用和应用数据库的构造函数
    //注意SQL语句必须要有完整的结束符“;”
    public AlvinDBHelper(Context context, int version, String appDbName,
                    String appInitSql, String appUpgradeSql) {
        super(context, appDbName == null ||
                appDbName.trim().equals("") ? dbName : appDbName, null, version);
        if(appInitSql != null && !appInitSql.trim().equals("")) {
            initSQL += appInitSql;
            upgradeSQL += appInitSql;
        }
        if(appUpgradeSql != null && !appUpgradeSql.trim().equals("")) {
            upgradeSQL += appUpgradeSql;
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "Initialize database");
        String[] initSqls = initSQL.split(";");
        for(String initSql : initSqls) {
            Log.i(TAG, "execSQL: " + initSql + ";");
            db.execSQL(initSql + ";");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "Upgrade database");
        String[] upgradeSqls = upgradeSQL.split(";");
        for(String upgradeSql : upgradeSqls) {
            Log.i(TAG, "execSQL: " + upgradeSql + ";");
            db.execSQL(upgradeSql + ";");
        }
        onCreate(db);
    }
}
