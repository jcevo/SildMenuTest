package com.alvin.api.utils;

import com.alvin.api.model.Cache;
import com.alvin.common.utils.CacheUtils;
import com.alvin.common.utils.HttpUtils;
import com.alvin.common.utils.HttpUtils.HttpDownloadItem;

import android.os.Handler;
import android.os.Message;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 用于网络发送和接收的单例
 * 
 * @author pc
 * 
 */
public class SingleThreadDownloadUtils {
    public static List<JsonDownTask> queue = new ArrayList<JsonDownTask>(); // 装载需要下载的
    private static Class currentActivity; // 存储Activity class

    private static SingleThreadDownloadUtils instance;

    private SingleThreadDownloadUtils() {
        downloadThread.start();
    }

    // 创建单例,缓解内存
    public static SingleThreadDownloadUtils getInstance() {
        if (instance == null) {
            instance = new SingleThreadDownloadUtils();
        }
        return instance;
    }

    // 把列表请求改为实时请求了,后期考虑改为缓存
    // 下载线程
    private Thread downloadThread = new Thread() {
        public void run() {
            while (true) {
                while (queue.size() > 0) {
                    JsonDownTask task = queue.get(0);
                    InputStream inputStream = null;
                    try {
                        HttpDownloadItem item = HttpUtils.invokeWithCache(
                                task.getUrl(), CacheUtils.CACHE_INTERNAL,
                                CommonSettingsUtils.TEXTCACHETIME, task.isRefresh());
                        inputStream = item.getInputStream();
                        sendMessages(CommonSettingsUtils.JSON_SUCCESS, inputStream,
                                task.getHandler());
                    } catch (Exception e) {
                        sendMessages(CommonSettingsUtils.JSON_NET_ERROR, null,
                                task.getHandler());
                        e.printStackTrace();
                    }
                    queue.remove(0);
                }
                try {
                    synchronized (this) {
                        // 等待通知
                        wait();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    };

    /**
     * 创建监听方法
     */
    public void addDownloadTask(JsonDownTask task) throws Exception {
        // LogOutput.e("JsonSender", "queue"+task.getUrl());
        if (task.getUrl() == null || task.getUrl().trim().equals("")) {
            throw new Exception(
                    "The url cannot be null in DownloadTask object.");
        }
        if (!task.isRefresh()) {
            // 如果缓存中有已经下载过的数据,直接从缓存中读取该数据
            Cache cache = CacheUtils.getCache(task.getUrl());
            if (cache != null
                    && cache.getExpire() >= System.currentTimeMillis()) {
                if (task.getHandler() != null) {
                    InputStream inputStream = new FileInputStream(
                            cache.getFile());
                    sendMessages(CommonSettingsUtils.JSON_SUCCESS, inputStream,
                            task.getHandler());
                }
                return;
            }
        }

        // 如果Activity切换,清空队列中需要下载的数据
        if (currentActivity != null
                && currentActivity != task.getActivityClass() && task.isClear()) {
            currentActivity = task.getActivityClass();
            queue.clear();
        }
        queue.add(task);
        // LogOutput.e("JsonSender", "queue"+task.getUrl());
        synchronized (downloadThread) {
            // 唤醒线程执行下载
            downloadThread.notify();
        }
    }

    /**
     * 传递消息到相应的Activity
     * 
     * @param result
     *            参数为网络获取的结果
     * @param object
     *            附带对象
     * @param handler
     *            处理消息的handler
     */
    public void sendMessages(int result, Object object, Handler handler) {
        Message message = handler.obtainMessage();
        message.obj = object;
        message.what = result;
        handler.sendMessage(message);
    }
}
