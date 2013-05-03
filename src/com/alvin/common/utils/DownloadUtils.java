package com.alvin.common.utils;

import com.alvin.api.model.Cache;
import com.alvin.common.utils.HttpUtils.HttpDownloadItem;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import android.os.Handler;
import android.os.Message;

public class DownloadUtils {
    public static List<DownloadTask> queue = new ArrayList<DownloadTask>(); //装载需要下载的
    private static Class<?> currentActivity;  //存储Activity class
    private static DownloadUtils instance;
    private DownloadUtils(){
        downloadThread.start();
    }
    
    //创建单例,缓解内存
    public synchronized static DownloadUtils getInstance(){
        if(instance == null){
            instance = new DownloadUtils();
        }
        return instance;
    }

    //下载线程
    private Thread downloadThread = new Thread(){
        public void run(){
            while(true){
                while(queue.size()>0){
                    DownloadTask task = queue.get(0);
                    InputStream inputStream = download(task);
                    if(task.getDownloadListener() != null){
                        task.getDownloadListener().downloadFinished(inputStream);
                    }
                    queue.remove(0);
                }
                try {
                    synchronized (this) {
                        //等待通知
                        wait();
                    }
                }catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    };
    
    /**
     * 添加下载任务
     */
    public void addDownloadTask(DownloadTask task) throws Exception {
        if(task.getUrl() == null || task.getUrl().trim().equals("")) {
            throw new Exception("The url cannot be null in DownloadTask object.");
        }
        if(!task.isRefresh()) {
            
            //如果缓存中有已经下载过的数据,直接从缓存中读取该数据
            Cache cache = CacheUtils.getCache(CacheUtils.getCacheKey(task.getUrl()));
            
            if(cache != null && cache.getExpire() >= System.currentTimeMillis()){
                if(task.getDownloadListener() != null) {
                    InputStream inputStream = new FileInputStream(cache.getFile());
                    task.getDownloadListener().downloadFinished(inputStream);
                }
                return;
            }
        }
        
        //如果Activity切换,清空队列中上一个Activity中需要下载的任务
        if (task.getActivityClass() != null && task.getActivityClass() != currentActivity) {
            currentActivity = task.getActivityClass();
            for(int i=0;i<queue.size();i++){
                if(null!=queue.get(i).getActivityClass()){
                    queue.remove(i);
                }
            }
        }
        queue.add(task);
        synchronized (downloadThread) {
            //唤醒线程执行下载
            downloadThread.notify();
        }
    }

    /**
     * 创建下载任务
     * @param activityClass Activity class
     * @param imageUrl  需要下载的url
     * @param expire    缓存周期
     * @param cacheType 缓存类型(内部/外部存储)
     * @param listener  下载监听接口
     * @param isRefresh 是否刷新
     * @throws Exception
     */
    public void addDownloadTask(Class activityClass , final String imageUrl, String cacheKey,
                                int expire,int cacheType,final DownloadListener listener,
                                boolean isRefresh) throws Exception {
        DownloadTask task = new DownloadTask();
        task.setActivityClass(activityClass);
        task.setDownloadListener(listener);
        task.setExpire(expire);
        task.setCacheType(cacheType);
        task.setRefresh(isRefresh);
        task.setUrl(imageUrl);
        task.setCacheKey(cacheKey);

        this.addDownloadTask(task);
    }

    public void addDownloadTask(Class activityClass, final String imageUrl, int expire,
                                int cacheType, final DownloadListener listener,
                                boolean isRefresh) throws Exception {
        this.addDownloadTask(activityClass, imageUrl, null, expire, cacheType, listener, isRefresh);
    }

    /**
     * 根据url执行下载
     */
    private InputStream download(DownloadTask task) {
        InputStream inputStream = null;
        try {
            HttpDownloadItem item = null;
            if(task.getCacheKey() == null || task.getCacheKey().trim().length() == 0) {
                item = HttpUtils.invokeWithCache(task.getUrl(), task.getCacheType(),
                        task.getExpire(), task.isRefresh());
            } else {
                item = HttpUtils.invokeWithCache(task.getUrl(), task.getCacheKey(),
                        task.getCacheType(), task.getExpire(), task.isRefresh());
            }
            inputStream = item.getInputStream();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return inputStream;
    }
    
    public static interface DownloadListener {
        public void downloadFinished(InputStream inputStream);
    }

    public static abstract class SimpleDownloadListener implements DownloadListener {
        private String url;
        private InputStream inputStream;

        private Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                SimpleDownloadListener listener = (SimpleDownloadListener)msg.obj;
                simpleDownloadFinished(listener.getUrl(), listener.getInputStream());
            }
        };

        public SimpleDownloadListener(String url) {
            this.url = url;
        }

        public void downloadFinished(InputStream inputStream) {
            this.inputStream = inputStream;
            handler.sendMessage(handler.obtainMessage(0, this));
        }

        public abstract void simpleDownloadFinished(String url, InputStream inputStream);

        public String getUrl() {
            return url;
        }

        public InputStream getInputStream() {
            return inputStream;
        }
    }
    
    /**
     * 存储需要下载的数据
     * @author user
     */
    public static class DownloadTask {
        private Class<?> activityClass;
        private String url;
        private String cacheKey;
        private int cacheType = CacheUtils.CACHE_EXTERNAL;
        private int expire;
        private boolean refresh = false;
        private DownloadListener downloadListener;

        public Class<?> getActivityClass() {
            return activityClass;
        }

        public void setActivityClass(Class<?> activityClass) {
            this.activityClass = activityClass;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getCacheKey() {
            return cacheKey;
        }

        public void setCacheKey(String cacheKey) {
            this.cacheKey = cacheKey;
        }

        public int getCacheType() {
            return cacheType;
        }

        public void setCacheType(int cacheType) {
            this.cacheType = cacheType;
        }

        public int getExpire() {
            return expire;
        }

        public void setExpire(int expire) {
            this.expire = expire;
        }

        public boolean isRefresh() {
            return refresh;
        }

        public void setRefresh(boolean refresh) {
            this.refresh = refresh;
        }

        public DownloadListener getDownloadListener() {
            return downloadListener;
        }

        public void setDownloadListener(DownloadListener downloadListener) {
            this.downloadListener = downloadListener;
        }
    }
}
