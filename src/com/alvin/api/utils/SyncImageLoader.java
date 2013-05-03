package com.alvin.api.utils;

/**
 * 通用同步下载图片类
 */
import com.alvin.api.utils.AsyncImageLoader.ImageCallback;
import com.alvin.common.utils.CacheUtils;
import com.alvin.common.utils.DownloadUtils;
import com.alvin.common.utils.DownloadUtils.DownloadListener;
import com.alvin.common.utils.DownloadUtils.DownloadTask;
import com.alvin.common.utils.HttpUtils;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class SyncImageLoader {
    private static Map<String, String> imageFileCache = new HashMap<String, String>();
    private final static String TAG = SyncImageLoader.class.getSimpleName();

    public static void loadDrawable(final String imageUrl, final ImageCallback callback, Activity activity) {
        if (!imageFileCache.containsKey(imageUrl)) {
            // 同步读取图片更新
            final Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    callback.imageLoaded((Drawable) msg.obj, imageUrl);
                }
            };
            DownloadListener downloadListener = new DownloadListener() {
                @Override
                public void downloadFinished(InputStream inputStream) {
                    if (inputStream != null) {
                        Drawable drawable = Drawable.createFromStream(inputStream, null);
                        if (null != imageFileCache && drawable != null) {
                            imageFileCache.put(imageUrl, imageUrl);
                        }
                        handler.sendMessage(handler.obtainMessage(0, drawable));
                    } else {
                        handler.sendMessage(handler.obtainMessage(0, null));
                    }
                }
            };
            DownloadUtils downloadUtils = DownloadUtils.getInstance();
            DownloadTask downloadTask = new DownloadTask();
            try {
                downloadTask.setActivityClass(activity.getClass());
                downloadTask.setCacheType(CacheUtils.CACHE_EXTERNAL);
                downloadTask.setDownloadListener(downloadListener);
                downloadTask.setExpire(CommonSettingsUtils.ImageCacheTime);
                downloadTask.setRefresh(false);
                downloadTask.setUrl(imageUrl);
                downloadUtils.addDownloadTask(downloadTask);
            } catch (Exception e) {
                e.printStackTrace();
                LogOutputUtils.i(TAG, imageUrl + "DownLoad Failed");
            }
        } else {
            callback.imageLoaded(AsyncImageLoader.loadBitmap(imageUrl), imageUrl);
        }

    }

    // public static Drawable getDrawableByFile(Activity activity, String
    // imageUrl) {
    //
    // HttpDownloadItem httpDownloadItem;
    // boolean isError = false;
    // Drawable drawable = null;
    // try {
    // long time=System.currentTimeMillis();
    // httpDownloadItem = HttpUtils.invokeWithCache(imageUrl,
    // CacheUtils.CACHE_EXTERNAL, Settings.ImageCacheTime, false);
    // LogOutput.e("花费时间",
    // "time:"+(System.currentTimeMillis()-time)+"url:"+imageUrl);
    // if (httpDownloadItem != null) {
    // drawable = Drawable.createFromStream(
    // httpDownloadItem.getInputStream(), "src");
    // }
    // } catch (Exception e) {
    // LogOutput.e(TAG, "load image error");
    // e.printStackTrace();
    // }
    //
    // return drawable;
    // }

    public static Drawable getDrawableByFile(Activity activity, String imageUrl) {
        Drawable drawable = null;
        InputStream inputStream;
        try {
            // long time=System.currentTimeMillis();
            inputStream = new FileInputStream(HttpUtils.getCacheIgnoreExpire(imageUrl));
            // LogOutput.e("花费时间",
            // "time:"+(System.currentTimeMillis()-time)+"url:"+imageUrl);
            if (inputStream != null) {
                drawable = Drawable.createFromStream(inputStream, "src");
            }
        } catch (Exception e) {
            LogOutputUtils.i(TAG, "load image error");
            e.printStackTrace();
        }
        return drawable;
    }

    public interface SyncImageCallback {
        public void imageLoaded(Drawable imageDrawable, String imageUrl);
    }
}
