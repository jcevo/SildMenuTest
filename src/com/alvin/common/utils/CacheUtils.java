package com.alvin.common.utils;

import com.alvin.api.config.Env;
import com.alvin.api.model.Cache;
import com.alvin.db.AlvinDBHelper;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;

/**
 * 缓存管理工具类
 */
public class CacheUtils {
    private final static String TAG = CacheUtils.class.getSimpleName();

    private static CacheCleaner cacheCleaner = null;

    public static File cacheDirInternal;        //内存缓存目录
    public static File cacheDirExternal;        //外部缓存目录
    public static File tempCacheDirInternal;    //中转内存缓存目录
    public static File tempCacheDirExternal;    //中转外部缓存目录

    public final static int CACHE_INTERNAL = 1;
    public final static int CACHE_EXTERNAL = 2;

    public final static int EXPIRE_DATA = 3600;
    public final static int EXPIRE_HEAD = 3600;
    public final static int EXPIRE_IMAGE = 1209600;

    private static long MAX_INTERNAL_CACHE_USED = 20 * 1024 * 1024;  //最大内部缓存容量，单位：byte
    private static long MIN_INTERNAL_STORAGE_AVAILABLE = 1 * 1024 * 1024;  //最小内部存储可用空间，单位：byte
    private static long MAX_EXTERNAL_CACHE_SIZE = 500 * 1024 * 1024;  //最大外部缓存容量，单位：M
    private static long MIN_EXTERNAL_STORAGE_AVAILABLE = 10 * 1024 * 1024;  //最小外部存储可用空间，单位：byte

    public static String getCacheKey(String url) {
        return url;
    }

    public static String getCacheFileName(String url) throws UnsupportedEncodingException {
        return URLUtils.encodeURL(url) + ".cache";
    }

    public synchronized static long getAvailableCacheSize(int cacheType) {
        File cacheDir = null;
        if(cacheType == CACHE_INTERNAL) {
            cacheDir = cacheDirInternal;
        } else {
            cacheDir = cacheDirExternal;
        }
        return FileUtils.getAvailableStorageSize(cacheDir);
    }

    public synchronized static long getUsedCacheSize(int cacheType) {
        File cacheDir = null;
        if(cacheType == CACHE_INTERNAL) {
            cacheDir = cacheDirInternal;
        } else {
            cacheDir = cacheDirExternal;
        }
        return FileUtils.getDirSize(cacheDir);
    }

    public synchronized static boolean isSpaceEnough(int cacheType) {
        boolean isEnough = true;
        if(cacheType == CACHE_INTERNAL) {
            if(getAvailableCacheSize(CACHE_INTERNAL) <= MIN_INTERNAL_STORAGE_AVAILABLE) {
                isEnough = false;
            }
        } else {
            if(!Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)
                    || getAvailableCacheSize(CACHE_EXTERNAL) <= MIN_EXTERNAL_STORAGE_AVAILABLE) {
                isEnough = false;
            }
        }
        if(!isEnough) {
            Log.i(TAG, "The cache space [" + cacheType + "] is not enough");
        }
        return isEnough;
    }

    public synchronized static boolean needClear(int cacheType) {
        boolean need = false;
        if(cacheType == CACHE_INTERNAL) {
            if(getAvailableCacheSize(CACHE_INTERNAL) <= MIN_INTERNAL_STORAGE_AVAILABLE
                    || getUsedCacheSize(CACHE_INTERNAL) > MAX_INTERNAL_CACHE_USED) {
                need = true;
            }
        } else {
            if(!Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)
                    || getAvailableCacheSize(CACHE_EXTERNAL) <= MIN_EXTERNAL_STORAGE_AVAILABLE
                    || getUsedCacheSize(CACHE_EXTERNAL) > MAX_EXTERNAL_CACHE_SIZE) {
                need = true;
            }
        }
        if(need) {
            Log.i(TAG, "The cache space [" + cacheType + "] need clear");
        }
        return need;
    }

    public synchronized static boolean isAvailable(int cacheType) {
        boolean isAvailable = true;
        if(cacheType == CACHE_EXTERNAL) {
            if(!Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
                isAvailable = false;
            }
        }
        if(!isAvailable) {
            Log.i(TAG, "Cache is not available for read");
        }
        return isAvailable;
    }

    /**
     * 清除所有缓存数据
     */
    public synchronized static void clearAllCache(int cacheType) {
        //清除缓存数据
        SQLiteDatabase db = Env.dbHelper.getWritableDatabase();
        db.delete(AlvinDBHelper.CACHE_TABLE, "status="+cacheType, null);

        //清除临时缓存
        clearTempCache(cacheType);

        //清除应用缓存
        if(cacheType == CACHE_INTERNAL && cacheDirInternal.exists()
                && cacheDirInternal.isDirectory()) {
            FileUtils.deleteDirectory(cacheDirInternal, false);
            //初始化临时存储目录
            tempCacheDirInternal = new File(cacheDirInternal, "temp");
            if(!tempCacheDirInternal.exists() || tempCacheDirInternal.isFile()) {
                tempCacheDirInternal.mkdirs();
            }
        } else if(cacheType == CACHE_EXTERNAL && cacheDirExternal.exists()
                && cacheDirExternal.isDirectory()) {
            FileUtils.deleteDirectory(cacheDirExternal, false);
            //初始化临时存储目录
            tempCacheDirExternal = new File(cacheDirExternal, "temp");
            if(!tempCacheDirExternal.exists() || tempCacheDirExternal.isFile()) {
                tempCacheDirExternal.mkdirs();
            }
        }
    }

    /**
     * 清除临时缓存数据
     */
    public synchronized static void clearTempCache(int cacheType) {
        //清除中转缓存
        if(cacheType == CACHE_INTERNAL && tempCacheDirInternal.exists()
                && tempCacheDirInternal.isDirectory()) {
            FileUtils.deleteDirectory(tempCacheDirInternal, false);
        } else if(cacheType == CACHE_EXTERNAL && tempCacheDirExternal.exists()
                && tempCacheDirExternal.isDirectory()) {
            FileUtils.deleteDirectory(tempCacheDirExternal, false);
        }
    }

    public synchronized static void clearTempCache() {
        clearTempCache(CACHE_INTERNAL);
        clearTempCache(CACHE_EXTERNAL);
    }

    /**
     * 持久话缓存数据
     * @param key 缓存key
     * @param file 缓存文件
     * @param size 缓存文件大小
     * @param expire 缓存过期时长
     */
    public static void setCache(String key, File file, long size, int expire, int cacheType) {
        if(file == null || !file.exists() || !file.isFile()) {
            return;
        }

        long time = System.currentTimeMillis();
        ContentValues dataValue = new ContentValues();
        dataValue.put("key", key);
        dataValue.put("file", file.getAbsolutePath());
        dataValue.put("size", size);
        dataValue.put("status", cacheType);
        dataValue.put("time", time);
        if(expire > 0) {
            dataValue.put("expire", time + expire*1000);
        } else {
            dataValue.put("expire", time);
        }

        //从一级缓存中查找数据
        try {
            Cache cache = null;
            SQLiteDatabase db = Env.dbHelper.getWritableDatabase();
            Cursor cur = null;
            try {
                cur = db.rawQuery(
                        "select * from " + AlvinDBHelper.CACHE_TABLE + " " +
                        "where key = '" + key + "'", null);
                if(cur != null && cur.getCount() > 0 && cur.moveToNext()) {
                    cache = new Cache();
                    cache.parse(cur);
                }
            } catch(Exception e) {
                throw e;
            } finally {
                if(cur != null) {
                    cur.close();
                }
            }

            if(cache != null) {
                dataValue.put("id", cache.getId());
                String[] parms = new String[]{Long.toString(cache.getId())};
                db.update(AlvinDBHelper.CACHE_TABLE, dataValue, "id=?", parms);
            } else {
                db.insert(AlvinDBHelper.CACHE_TABLE, null, dataValue);
            }
        } catch (Exception e) {
            Log.e(TAG, "set cache data failed: " + key);
            e.printStackTrace();
        }
    }

    /**
     * 获取缓存数据，不论是否缓存已过期
     * @param key 缓存key
     * @return 缓存数据对象
     */
    public static Cache getCacheIgnoreExpire(String key) {
        Cache cache = null;

        //从一级缓存中查找数据
//        cache =  cacheMap.get(key);
//        if(cache == null) {
            //读取数据库缓存数据
//            synchronized(Env.dbHelper) {
                SQLiteDatabase db = Env.dbHelper.getReadableDatabase();
                Cursor cur = null;
                try {
                    cur = db.rawQuery(
                            "select * from " + AlvinDBHelper.CACHE_TABLE + " " +
                            "where key = '" + key + "'", null);
                    if(cur != null && cur.getCount() > 0 && cur.moveToNext()) {
                        try {
                            cache = new Cache();
                            cache.parse(cur);
                        } catch (ParseException e) {
                            Log.e(TAG, "get cache data ignore expire fail: " + key);
                            cache = null;
                            e.printStackTrace();
                        }
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                } finally {
                    if(cur != null) {
                        cur.close();
                    }
//                    db.close();
                }
//            }
            if(cache != null ) {
                if(cache.getFile().exists() && cache.getFile().isFile()) {
                    //更新1级缓存
//                    cacheMap.put(key, cache);
                    Log.i(TAG, "get cache data ignore expire : " + key);
                } else {
                    cache = null;
                }
            }
//        }

        return cache;
    }

    /**
     * 获取缓存数据
     * @param key 缓存key
     * @return 缓存数据对象
     */
    public static Cache getCache(String key) {
        Cache cache = getCacheIgnoreExpire(key);
        if(cache != null && cache.getExpire() > System.currentTimeMillis()
                && cache.getFile() != null && cache.getFile().exists()
                && cache.getFile().isFile()) {
            Log.i(TAG, "get cache data: " + key);
        } else {
            cache = null;
            Log.i(TAG, "get cache data fail: " + key);
        }

        return cache;
    }

    public static class CacheSpaceNotEnoughException extends Exception {

    }

    private static class CacheCleaner extends Thread {

        public void run() {
            SQLiteDatabase db = Env.dbHelper.getWritableDatabase();
            Cursor cur = null;
            Cache cache = null;

            while(true) {
                try {
                    Thread.sleep(60000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                try {
                    cur = db.rawQuery(
                            "select * from " + AlvinDBHelper.CACHE_TABLE + " " +
                            "where expire<=" + System.currentTimeMillis() + "", null);
                    if(cur != null && cur.getCount() > 0 && cur.moveToNext()) {
                        cache = new Cache();
                        cache.parse(cur);
                        if(cache.getFile().delete()) {
                            db.delete(AlvinDBHelper.CACHE_TABLE, "id="+cache.getId(), null);
                        }
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                } finally {
                    if(cur != null) {
                        cur.close();
                    }
                }

                if(needClear(CACHE_INTERNAL)) {
                    Log.i(TAG, "The internal cache space is not enough, clear cache...");
                    clearAllCache(CACHE_INTERNAL);
                }
                if(Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)
                        && needClear(CACHE_EXTERNAL)) {
                    Log.i(TAG, "The external cache space is not enough, clear cache...");
                    clearAllCache(CACHE_EXTERNAL);
                }
            }
        }
    }

    public static void startCacheCleaner() {
        if(cacheCleaner == null) {
            cacheCleaner = new CacheCleaner();
            cacheCleaner.start();
        }
    }
}
