package com.alvin.db;

/**
 * 用户收藏列表数据表
 */
import com.alvin.api.bean.WeiboInfo;
import com.alvin.api.utils.CommonSettingsUtils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class DBWeiboInfo extends DbHandle {
    public final static String sql = "CREATE TABLE IF NOT EXISTS "
            + CommonSettingsUtils.WEIBO_DB + " ("
            + " id INTEGER primary key autoincrement, "
            + CommonSettingsUtils.SINA_WEIBO_ACCESS_TOKEN + " NVARCHAR(255)  ," // access_token
            + CommonSettingsUtils.SINA_WEIBO_TOKEN_SECRET + " NVARCHAR(255)  ," // TOKEN_SECRET
            + CommonSettingsUtils.WEIBO_TYPE + " NVARCHAR(255)  ," // 微博类型
            + ");";

    public DBWeiboInfo(Context context, int version) {
        super(context, version);
    }

    /**
     * 插入数据
     * 
     * @param tableName
     * @param appid
     * @return true 则插入成功
     */
    public boolean insert(WeiboInfo weiboInfo) {
        boolean flag = true;

        ContentValues initialValues = new ContentValues();
        initialValues.put(CommonSettingsUtils.SINA_WEIBO_ACCESS_TOKEN,
                weiboInfo.getAccessToken());
        initialValues.put(CommonSettingsUtils.SINA_WEIBO_TOKEN_SECRET,
                weiboInfo.getTokenSercet());
        initialValues.put(CommonSettingsUtils.WEIBO_TYPE, weiboInfo.getWeiboType());
        db.beginTransaction();
        flag = db.insert(CommonSettingsUtils.WEIBO_DB, null, initialValues) > 0;// 若initialvalues为空时则插入null

        db.setTransactionSuccessful();
        db.endTransaction();
        return flag;
    }

    /**
     * 删除数据
     * 
     * @param appid
     * @return true 则删除成功
     */
    public boolean delete(WeiboInfo weiboInfo) {
        boolean flag = true;
        db.beginTransaction();
        flag = db.delete(CommonSettingsUtils.FAVOURITE_APP, CommonSettingsUtils.WEIBO_TYPE
                + "='" + weiboInfo.getWeiboType() + "'", null) > 0;
        db.setTransactionSuccessful();
        db.endTransaction();
        return flag;
    }

    /**
     * 查询所有收藏夹
     * 
     * @return
     */
    public Cursor query(String type) {
        boolean flag = true;
        db.beginTransaction();
        Cursor cursor = db.rawQuery("select distinct  *  from "
                + CommonSettingsUtils.WEIBO_DB + "where " + CommonSettingsUtils.WEIBO_TYPE
                + "='" + type + "' order by date desc", null);
        db.setTransactionSuccessful();
        db.endTransaction();
        return cursor;

    }

    /**
     * 获取收藏夹appsID
     * 
     * @param userString
     * @return
     */
    public WeiboInfo getWeiboInfo(String type) {
        Cursor mcursor = query(type);
        WeiboInfo weiboInfo=null;
        if(mcursor.getCount()<=0)
        {
            return null;
        }
        mcursor.moveToFirst();
        weiboInfo.setAccessToken(mcursor.getString(mcursor.getColumnIndex(CommonSettingsUtils.SINA_WEIBO_ACCESS_TOKEN)));
      
        if (mcursor != null) {
            mcursor.close();
        }
        return weiboInfo;
    }

}
