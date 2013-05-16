package com.alvin.db;

/**
 * 数据库初始化,数据库统一由程序退出时关闭.各个子类只需要关闭使用的游标
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DbHandle {

    protected static SQLiteDatabase db = null;

    public DbHandle(Context context, int version) {
        if (db != null && db.isOpen()) {
        } else {
            AlvinDBHelper dbHelper = new AlvinDBHelper(context, version);
            db = dbHelper.getWritableDatabase();
        }
    }

    public void close() {
        if (db != null) {
            db.close();
        }
    }

    public void beginTrans() {
        db.beginTransaction();// 事务开始
    }

    public void endTrans() {
        db.setTransactionSuccessful();
        db.endTransaction();
    }

}
