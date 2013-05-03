package com.alvin.db;

import com.alvin.api.config.Env;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class UserDBHelper {
    public static Cursor getUserByFlag(String flag) {
        SQLiteDatabase db = Env.dbHelper.getReadableDatabase();
        return db.rawQuery("select * from "+DBHelper.USER_TABLE+" where auth_type = ?", new String[]{flag});
    }
    
    public static Cursor getPassPortUser() {
        SQLiteDatabase db = Env.dbHelper.getReadableDatabase();
        return db.rawQuery("select * from "+DBHelper.USER_TABLE+" where is_passport = 1", null);
    }
    
    public static boolean existsUser(String flag) {
        boolean result = false;
        Cursor cursor = null;
        try {
            cursor = getUserByFlag(flag);
            result = cursor != null && cursor.moveToFirst();
        } finally {
            if (cursor != null) cursor.close();
        }
        return result;
    }
    
    public static boolean existsUser(String authType, String token) {
        boolean result = false;
        Cursor cursor = null;
        try {
            SQLiteDatabase db = Env.dbHelper.getReadableDatabase();
            cursor = db.rawQuery("select * from "+DBHelper.USER_TABLE+" where auth_type = ? and token = ?", new String[]{authType, token});
            result = cursor != null && cursor.moveToFirst();
        } finally {
            if (cursor != null) cursor.close();
        }
        return result;
    }
    
    //改方法仅测试时使用
    private static int deleteUserByUserType(String userType) {
        SQLiteDatabase db = Env.dbHelper.getWritableDatabase();
        return db.delete(DBHelper.USER_TABLE, "auth_type = ?", new String[]{userType});
    }
    
    public static long persistUser(String oauthType, long oauthUserId, String token, String secret, String oauthNickname) {
        SQLiteDatabase db = Env.dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("auth_type", oauthType);
        values.put("oauth_user_id", oauthUserId);
        values.put("token", token);
        values.put("secret", secret);
        values.put("oauth_nickname", oauthNickname);
        long result = 0;
        if (existsUser(oauthType)) {
            result = db.update(DBHelper.USER_TABLE, values, "auth_type = ?", new String[]{oauthType});
        } else {
            values.put("time", System.currentTimeMillis());
            result = db.insert(DBHelper.USER_TABLE, null, values);
        }
        return result;
    }
    
    public static long persistUserAuthority(String authType, String authorityName, boolean hasAuthority) {
        SQLiteDatabase db = Env.dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        Cursor cursor = null;
        long result = -1;
        try {
            cursor = getUserByFlag(authType);
            if (cursor != null && cursor.moveToFirst()) {
                values.put("local_user_id", cursor.getInt(cursor.getColumnIndex("id")));
                values.put("authority_name", authorityName);
                values.put("status", hasAuthority?1:0);
                result = db.insert(DBHelper.USER_AUTHORITY_TABLE, null, values);
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return result;
    }
    
    public static boolean existUserAuthority(String authType, String authorityName) {
        boolean result = false;
        SQLiteDatabase db = Env.dbHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            String sql = "select ua.id from "+DBHelper.USER_AUTHORITY_TABLE+" ua, "+DBHelper.USER_TABLE
                +" u where u.auth_type = ? and u.id = ua.local_user_id and ua.authority_name = ?";
            cursor = db.rawQuery(sql, new String[]{authType, authorityName});
            result = cursor != null && cursor.moveToFirst();
        } finally {
            if (cursor != null) cursor.close();
        }
        return result;
    }
    
    public static boolean checkAuthority(String authType, String authorityName) {
        boolean result = false;
        SQLiteDatabase writeDB = Env.dbHelper.getWritableDatabase();
        Cursor cursor = null;
        try {
            String sql = "select ua.id from "+DBHelper.USER_AUTHORITY_TABLE+" ua, "+DBHelper.USER_TABLE
                +" u where u.auth_type = ? and u.id = ua.local_user_id and ua.authority_name = ? and ua.status = 1";
            cursor = writeDB.rawQuery(sql, new String[]{authType, authorityName});
            result = cursor != null && cursor.moveToFirst();
        } finally {
            if (cursor != null) cursor.close();
        }
        return result;
    }
    
    public static boolean updateAuthority(String authType, String authorityName, boolean hasAuthority) {
        boolean result = false;
        if (!existUserAuthority(authType, authorityName)) {
            persistUserAuthority(authType, authorityName, hasAuthority);
            result = true;
        } else {
            SQLiteDatabase writeDB = Env.dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            Cursor cursor = null;
            try {
                cursor = getUserByFlag(authType);
                if (cursor != null && cursor.moveToFirst()) {
                    values.put("status", hasAuthority?1:0);
                    values.put("authority_name", authorityName);
                    writeDB.update(DBHelper.USER_AUTHORITY_TABLE, values, "local_user_id = "+cursor.getInt(cursor.getColumnIndex("id"))+" and authority_name = ?", new String[] {authorityName});
                    result = true;
                }
            } catch(Exception e) {
                e.printStackTrace();
                result = false;
            } finally {
                if (cursor != null) cursor.close();
            }
        }
        return result;
    }
    
    public static int updateUser(ContentValues values, String where, String[] whereArgs) {
        SQLiteDatabase writeDB = Env.dbHelper.getWritableDatabase();
        try {
            return writeDB.update(DBHelper.USER_TABLE, values, "auth_type = ?", whereArgs);
        } catch(Exception e) {
            return 0;
        }
    }

    @Deprecated
    public static boolean updateUser(String sql, Object[] params) {
        SQLiteDatabase writeDB = Env.dbHelper.getWritableDatabase();
        try {
            if (params == null || params.length == 0) {
                writeDB.execSQL(sql);
            } else {
                writeDB.execSQL(sql, params);
            }
            return true;
        } catch(Exception e) {
            return false;
        }
    }
    
    public static String[] getTokenAndSecret(String authType) {
        String[] result = null;
        Cursor nCursor = null;
        try {
            nCursor = getUserByFlag(authType);
            if (nCursor != null && nCursor.moveToFirst()) {
                result = new String[2];
                result[0] = nCursor.getString(nCursor.getColumnIndex("token"));
                result[1] = nCursor.getString(nCursor.getColumnIndex("secret"));
            }
        } finally {
            if (nCursor != null) nCursor.close();
        }
        return result;
    }
}
