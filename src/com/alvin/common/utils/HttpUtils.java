package com.alvin.common.utils;

import com.alvin.api.config.Env;
import com.alvin.api.model.Cache;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * 用于HTTP请求处理的工具类
 */

public class HttpUtils {
    private final static String TAG = HttpUtils.class.getSimpleName();
    private final static String USER_AGENT = Env.client + " " + Env.versionName;
    private final static String ACCEPT_ENCODING = "gzip, deflate";
    private final static int BUFFER = 8192;
    private static int taskSeq = 1;

    //网络超时设置
    public static int CONNECT_TIMEOUT = 10;
    public static int DATA_TIMEOUT = 15;

    /**
     * 获取指定URL的输入流
     * @param urlStr 要获取的文本文件的URL
     * @return 远端文本文件的输入流
     * @throws IOException
     */
//    public static InputStream getInputStream(String urlStr)
//            throws IOException {
//        URL url = new URL(urlStr);
//        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
//        conn.setRequestProperty("User-Agent", USER_AGENT);
//        int responseCode = conn.getResponseCode();
//        if(responseCode == HttpURLConnection.HTTP_OK) {
//            return conn.getInputStream();
//        }
//        return null;
//    }

    /**
     * 获取指定URL的文本内容
     * @param urlStr 要获取的文本文件的URL
     * @return 文本文件的内容
     * @throws IOException
     */
    public static String invokeText(String urlStr) throws IOException {
        Log.i(TAG, "invokeText: " + urlStr);
        StringBuffer textBuff = new StringBuffer();

        HttpGet request = new HttpGet(urlStr);
        request.addHeader("User-Agent", USER_AGENT);
        request.addHeader("Accept-Encoding", ACCEPT_ENCODING);

        HttpParams params = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(params, CONNECT_TIMEOUT *1000);
        HttpConnectionParams.setSoTimeout(params, DATA_TIMEOUT *1000);
        HttpClient httpClient = new DefaultHttpClient(params);

        String line;
        BufferedReader reader = null;
        try {
            HttpResponse response = httpClient.execute(request);
            if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                InputStream is = response.getEntity().getContent();
                Header contentEncoding = response.getFirstHeader("Content-Encoding");
                if(contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
                    is = new GZIPInputStream(is);
                }
                reader = new BufferedReader(new InputStreamReader(is));
                while((line = reader.readLine()) != null) {
                    textBuff.append(line).append("\r\n");
                }
            }
        } finally {
            if(reader != null) {
                reader.close();
            }
        }

        return textBuff.toString();
    }

    /**
     * 获取指定URL的二进制数据
     * @param urlStr 要获取的二进制文件的URL
     * @return 二进制文件的数据内容
     * @throws IOException
     */
//    public static byte[] invokeData(String urlStr) throws IOException {
//        byte[] data = null;
//        URL url = new URL(urlStr);
//        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
//        conn.setRequestProperty("User-Agent", USER_AGENT);
//        int responseCode = conn.getResponseCode();
//        if(responseCode == HttpURLConnection.HTTP_OK) {
//            InputStream is = conn.getInputStream();
//            int length = conn.getContentLength();
//            if(length > 0) {
//                data = new byte[length];
//                byte[] buffer = new byte[BUFFER];
//                int readLen = 0;
//                int destPos = 0;
//                while((readLen = is.read(buffer)) > 0){
//                    System.arraycopy(buffer, 0, data, destPos, readLen);
//                    destPos += readLen;
//                }
//            }
//        }
//        return data;
//    }

    /**
     * 发送网络数据
     * @param urlStr 接收数据的url
     * @return 是否发送成功
     * @throws IOException
     */
    public static boolean sendData(String urlStr, List<NameValuePair> params) {
        boolean success =  false;
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost request = new HttpPost(urlStr);
            if(params != null && params.size() > 0) {
                request.setEntity(new UrlEncodedFormEntity(params));
            }
            request.addHeader("User-Agent", USER_AGENT);
            request.addHeader("Accept-Encoding", ACCEPT_ENCODING);

            HttpParams config = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(config, CONNECT_TIMEOUT *1000);
            HttpConnectionParams.setSoTimeout(config, DATA_TIMEOUT *1000);

            HttpResponse response = httpClient.execute(request);
            if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                success = true;
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        return success;
    }

    /**
     * 发送网络数据
     * @param urlStr 接收数据的url
     * @return 是否发送成功
     * @throws IOException
     */
    public static boolean sendData(String urlStr, File file, List<NameValuePair> params) {
        boolean success =  false;
        HttpResponse response = uploadData(urlStr, file, params);
        if(response != null && response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            success = true;
        }
//        System.out.println("response: " + response.getStatusLine().getStatusCode());
        return success;
    }
    
    private static HttpResponse uploadData(String urlStr, File file, List<NameValuePair> params) {
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost request = new HttpPost(urlStr);
            if(file != null && file.isFile() && file.exists()) {
                MultipartEntity entity = new MultipartEntity();
                entity.addPart("file", new FileBody(file, "zip"));
                if (params != null) {
                    for(NameValuePair param : params) {
                        entity.addPart(param.getName(), new StringBody(param.getValue()));
                    }
                }
                request.setEntity(entity);
            }
            request.addHeader("User-Agent", USER_AGENT);
            request.addHeader("Accept-Encoding", ACCEPT_ENCODING);

            HttpParams config = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(config, CONNECT_TIMEOUT *1000);
            HttpConnectionParams.setSoTimeout(config, DATA_TIMEOUT *1000);

            return httpClient.execute(request);
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取指定URL的远端文件大小
     * @param urlStr 远端文件的URL
     * @return 远端文件的大小，单位：byte，返回-1表示未成功取得文件大小
     * @throws IOException
     */
//    public long getRemoteFileSize(String urlStr) throws IOException {
//        int fileLength = -1;
//        URL url = new URL(urlStr);
//        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//        conn.addRequestProperty("User-Agent", USER_AGENT);
//        conn.addRequestProperty("Accept-Encoding", ACCEPT_ENCODING);
//        int responseCode = conn.getResponseCode();
//        if(responseCode == HttpURLConnection.HTTP_OK) {
//            InputStream is = conn.getInputStream();
//            fileLength = conn.getContentLength();
//        }
//        return fileLength;
//    }

    /**
     * 停止所有通过downloadWithCache方法下载的任务
     */
    public static void stopDownload() {
        taskSeq++;
    }



    /**
     * 下载指定URL的文件
     * @param urlStr 要下载的文件URL
     * @param dest 下载后保存文件的地址
     * @param append 是否使用断点续传
     * @throws IOException
     */
    private static long download(String urlStr, File dest, boolean append) throws Exception {
        return download(urlStr, dest, append, null);
    }

    /**
     * 下载指定URL的文件
     * @param urlStr 要下载的文件URL
     * @param dest 下载后保存文件的地址
     * @param append 是否使用断点续传
     * @param adapter 下载监控适配器
     * @throws IOException
     */
    public static long download(String urlStr, File dest, boolean append,
                                HttpDownloadAdapter adapter) throws Exception {
        Log.i(TAG, "Download file[" + taskSeq + "]: " + urlStr);
        int downloadSeq = taskSeq;
        boolean canDownload = true;
        long fileSize = -1;
        int progress = 0;

        //清除目标文件
        if(!append && dest.exists() && dest.isFile()) {
            dest.delete();
        }

        //预处理断点续传
        if(append && dest.exists() && dest.exists()) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(dest);
                fileSize = fis.available();
            } catch(IOException e) {
                Log.i(TAG, "Get local file size fail: " + dest.getAbsolutePath());
                throw e;
            } finally {
                if(fis != null) {
                    fis.close();
                }
            }
        }

        HttpGet request = new HttpGet(urlStr);
        request.addHeader("User-Agent", USER_AGENT);
        request.addHeader("Accept-Encoding", ACCEPT_ENCODING);
        if(fileSize > 0) {
            request.addHeader("RANGE", "bytes=" + fileSize + "-");
        }

        HttpParams params = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(params, CONNECT_TIMEOUT *1000);
        HttpConnectionParams.setSoTimeout(params, DATA_TIMEOUT *1000);

//        SchemeRegistry reg = new SchemeRegistry();
//        reg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
//        reg.register(new Scheme("http", SSLSocketFactory.getSocketFactory(), 443));
//        ClientConnectionManager connectionManager = new ThreadSafeClientConnManager(params, reg);

//        HttpClient httpClient = new DefaultHttpClient(connectionManager, params);
        HttpClient httpClient = new DefaultHttpClient(params);

        InputStream is = null;
        FileOutputStream os = null;
        try {
            long time = System.currentTimeMillis();
            HttpResponse response = httpClient.execute(request);
            long time2 = System.currentTimeMillis() - time;
            if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK||response.getStatusLine().getStatusCode()==HttpStatus.SC_PARTIAL_CONTENT) {
                is = response.getEntity().getContent();
                long remoteFileSize ;
                if(response.getStatusLine().getStatusCode()==HttpStatus.SC_PARTIAL_CONTENT){
                    remoteFileSize= response.getEntity().getContentLength()+fileSize;
                }else {
                    remoteFileSize = response.getEntity().getContentLength();
                }
                Header contentEncoding = response.getFirstHeader("Content-Encoding");
                if(contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
                    is = new GZIPInputStream(is);
                }
                os = new FileOutputStream(dest, append);
                byte buffer[] = new byte[BUFFER];
                int readSize = 0;
                Log.e(TAG, "开始下载拉");
                while((canDownload = (downloadSeq==taskSeq)) && (readSize = is.read(buffer)) > 0){
                    Log.e(TAG, "开始下载拉");
                    os.write(buffer, 0, readSize);
                    os.flush();
                    fileSize += readSize;
                    if(adapter != null) {
                        if(remoteFileSize > 0) {
//                            System.out.println("##### fileSize = " + fileSize + ", remoteSize = "
//                                    + remoteFileSize);
                            int tempProgress = (int)(fileSize*100/remoteFileSize);
                            if(tempProgress > progress) {
                                progress = tempProgress;
                                adapter.notifyProgress(progress);
                            }
                        } else {
                            if(progress == 0) {
                                progress = -1;
                                adapter.notifyProgress(progress);
                            }
                        }
                    }
                }
                if(adapter != null && progress < 100) {
                    adapter.notifyProgress(100);
                }
            }
            if(fileSize < 0) {
                fileSize = 0;
            }
            long time3 = System.currentTimeMillis()-time;
            Log.i(TAG, "Download time = " + time2/1000.0 + "/" + time3/1000.0
                    + "\tsize=" + Math.round(fileSize/1.024)/1000 + "k\turl=" + urlStr);
        } finally {
            if(os != null) {
                os.close();
            }
            if(is != null) {
                is.close();
            }
            httpClient.getConnectionManager().shutdown();
        }

        if(!canDownload) {
            Log.i(TAG, "Download file stop: " + urlStr);
            throw new Exception("Download file stop: " + urlStr);
        }

        if(fileSize < 0) {
            throw new Exception("Download file fail: " + urlStr);
        }

        return fileSize;
    }
    
    /**
     * 下载并缓存指定URL的文件到本地文件系统
     * @param urlStr 要下载的文件URL
     * @param expire 缓存过期时间，单位：秒
     * @return 下载到本地的文件
     */
    private static File downloadWithCache(String urlStr, String cacheKey,
                                          int storeType, int expire, boolean refresh)
            throws Exception {
        Log.e("downloadCache", "#### downloadWithCache = " + urlStr);
        if(cacheKey == null || cacheKey.trim().length() == 0) {
            cacheKey = CacheUtils.getCacheKey(urlStr);
        }
        File cacheFile = null;
        File tempFile = null;
        String cacheFileName = CacheUtils.getCacheFileName(urlStr);

        //从缓存中查找数据
        Cache cache = CacheUtils.getCacheIgnoreExpire(cacheKey);
        if(!refresh && cache != null && cache.getExpire() > System.currentTimeMillis()
            && cache.getFile() != null && cache.getFile().exists()
            && cache.getFile().isFile()) {
            cacheFile = cache.getFile();
            Log.i(TAG, "getCache data success: " + urlStr);
        } else {
            if(CacheUtils.isSpaceEnough(storeType)) {
                long cacheSize = 0;

                //下载文件
                if(storeType == CacheUtils.CACHE_INTERNAL) {
                    cacheFile = new File(CacheUtils.cacheDirInternal, cacheFileName);
                    tempFile = new File(CacheUtils.tempCacheDirInternal, cacheFileName);
                } else {
                    cacheFile = new File(CacheUtils.cacheDirExternal, cacheFileName);
                    tempFile = new File(CacheUtils.tempCacheDirExternal, cacheFileName);
                }
                try {
                    cacheSize = download(urlStr, tempFile, false);
                    if(tempFile.exists() && tempFile.isFile()) {
                        if(cacheFile.exists() && cacheFile.isFile()) {
                            cacheFile.delete();
                        }
                        FileUtils.move(tempFile, cacheFile);
                    } else {
                        throw new Exception("Download file fail: " + urlStr);
                    }
                } catch(Exception e) {
                    Log.e(TAG, "Download file fail: " + urlStr);
                    //如果下载出错，则使用过期的缓存
                    if(!refresh && cache != null && cache.getFile() != null && cache.getFile().exists()
                            && cache.getFile().isFile()) {
                        cacheFile = cache.getFile();
                    } else {
                        throw e;
                    }
                }

                //设置缓存
                CacheUtils.setCache(cacheKey, cacheFile, cacheSize, expire, storeType);
                Log.i(TAG, "setCache data success: " + urlStr);
            } else {
                throw new CacheUtils.CacheSpaceNotEnoughException();
            }
        }

        if(!cacheFile.exists() || !cacheFile.isFile()) {
            cacheFile = null;
        }

        return cacheFile;
    }

    /**
     * 下载并缓存指定URL的文件到本地文件系统
     * @param urlStr 要下载的文件URL
     * @param expire 缓存过期时间，单位：秒
     * @return 下载到本地的文件
     */
    public static HttpDownloadItem invokeWithCache(String urlStr, String cacheKey,
                   int storeType, int expire, boolean refresh) throws Exception {
        HttpDownloadItem item = null;
        boolean isCacheAvailable = CacheUtils.isAvailable(storeType);
//        System.out.println("##### isCacheAvaiable = " + isCacheAvailable);

        //判断缓存空间是否可用
        if(isCacheAvailable) {  //缓存可用
            InputStream inputStream = null;
            try {
                inputStream = new FileInputStream(
                        downloadWithCache(urlStr, cacheKey, storeType, expire, refresh));
            } catch (Exception e) {
                if(e instanceof CacheUtils.CacheSpaceNotEnoughException) {
                    isCacheAvailable = false;
                } else {
                    throw e;
                }
            }
            if(inputStream != null) {
                item = new HttpDownloadItem();
                item.setUrl(urlStr);
                item.setType(HttpUtils.HttpDownloadItem.TYPE_FILE);
                item.setInputStream(inputStream);
            }
        } else {
            HttpMessage http = new HttpMessage();
            http.setUrl(urlStr);
            invoke(http);
            if(http.getStatusCode() == HttpStatus.SC_OK) {
                InputStream in = http.getResponseInputStream();
                if(in != null) {
                    item = new HttpDownloadItem();
                    item.setUrl(urlStr);
                    item.setType(HttpUtils.HttpDownloadItem.TYPE_INTERNET);
                    item.setInputStream(in);
                }
            }
        }

        return item;
    }

    public static HttpDownloadItem invokeWithCache(
            String urlStr, int storeType, int expire, boolean refresh) throws Exception {
        return invokeWithCache(urlStr, urlStr, storeType, expire, refresh);
    }

   

    public static InputStream delayInvokeWithAsset(Context context, String url,
            int storeType, int expire, boolean refresh) throws Exception {
        InputStream inputStream = null;

        Cache cache = CacheUtils.getCacheIgnoreExpire(CacheUtils.getCacheKey(url));

        //如果无HTTP缓存，则读取Asset预置缓存
        if(cache == null) {
            refresh = true;
            String assetName = null;
            try {
                assetName = URLUtils.encodeURL(url);
                inputStream = context.getResources().getAssets().open(assetName);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        //否则读取Http缓存
        } else {
            if(cache.getExpire() <= System.currentTimeMillis()) {
                refresh = true;
            }
            inputStream = new FileInputStream(cache.getFile());
        }

        //异步更新
        if(refresh) {
            DownloadUtils.getInstance().addDownloadTask(
                    null, url, expire, storeType, null, refresh);
        }

        return inputStream;
    }

    /**
     * 获取缓存，不论其是否过期
     * @param urlStr 要下载的文件URL
     * @return 缓存文件
     */
    public static File getCacheIgnoreExpire(String urlStr) throws IOException {
        String cacheKey = CacheUtils.getCacheKey(urlStr);
        Cache cache = CacheUtils.getCacheIgnoreExpire(cacheKey);
        File file = null;
        if(cache != null) {
            file = cache.getFile();
        }
        return file;
    }

    //下载监控适配器接口
    public interface HttpDownloadAdapter {

        /**
         * 通知下载进度的方法
         * @param progress 下载进度，返回值为0－100，表示0%-100%，如果返回值<0表示无法获取下载文件的总体积
         */
        public void notifyProgress(int progress);
    }
    
    /**
     * 得到服务器响应内容
     */
    public static HttpMessage invoke(HttpMessage httpMessage) throws Exception {
        if(httpMessage == null || !httpMessage.check()) {
            return null;
        }

        //设置请求超时
        HttpParams config = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(config, httpMessage.getConnectTimeout() * 1000);
        HttpConnectionParams.setSoTimeout(config, httpMessage.getDataTimeout() * 1000);

        //获取HttpClient对象
		DefaultHttpClient client = new DefaultHttpClient(config);
        httpMessage.setHttpClient(client);

        //创建Request对象
        HttpRequestBase request;
        if(httpMessage.getRequestMethod() == HttpMessage.REQUEST_METHOD_GET) {
            StringBuffer url = new StringBuffer(httpMessage.getUrl());
            StringBuffer params = new StringBuffer();
            if(httpMessage.getParameters() != null && httpMessage.getParameters().size() > 0) {
                for(NameValuePair param : httpMessage.getParameters()) {
                    params.append(param.getName()).append("=").append(param.getValue()).append("&");
                }
            }
            if(url.toString().indexOf("?") < 0) {
                url.append("?").append(params);
            } else {
                url.append("&").append(params);
            }
            request = new HttpGet(url.toString());
        } else if(httpMessage.getRequestMethod() == HttpMessage.REQUEST_METHOD_POST) {
            request = new HttpPost(httpMessage.getUrl());
            if(httpMessage.getParameters() != null && httpMessage.getParameters().size() > 0) {
                ((HttpPost)request).setEntity(
                        new UrlEncodedFormEntity(httpMessage.getParameters(), HTTP.UTF_8));
            }
        } else {
            request = new HttpPost(httpMessage.getUrl());
                MultipartEntity entity = new MultipartEntity();
            if(httpMessage.getFileMap() != null && httpMessage.getFileMap().size() > 0) {
                for(Map.Entry<String, FileBody> entry : httpMessage.getFileMap().entrySet())  {
                    entity.addPart(entry.getKey(), entry.getValue());
                }
            }
            if (httpMessage.getParameters() != null && httpMessage.getParameters().size() > 0) {
                for(NameValuePair param : httpMessage.getParameters()) {
                    entity.addPart(param.getName(), new StringBody(param.getValue()));
                }
            }
            ((HttpPost)request).setEntity(entity);
        }

        //设置请求Header
        if(httpMessage.getRequestHeaders() != null && httpMessage.getRequestHeaders().size() > 0) {
            Map<String, String> headers = httpMessage.getRequestHeaders();
            if(headers.get("User-Agent") == null) {
                headers.put("User-Agent", USER_AGENT);
            }
            if(headers.get("Accept-Encoding") == null) {
                headers.put("Accept-Encoding", ACCEPT_ENCODING);
            }
            for(Map.Entry<String, String> header: headers.entrySet()) {
                request.addHeader(header.getKey(), header.getValue());
            }
        }

        //设置请求Cookie
        if(httpMessage.getRequestCookies() != null && httpMessage.getRequestCookies().size() > 0) {
            StringBuffer cookies = new StringBuffer();
            for(Map.Entry<String, Cookie> cookieEntry :
                    httpMessage.getRequestCookies().entrySet()) {
                cookies.append(cookieEntry.getKey()).append("=")
                        .append(cookieEntry.getValue().getValue()).append(";");
            }
            request.addHeader("Cookie", cookies.toString());
        }

        //发送请求
		HttpResponse response = client.execute(request);

        //获取响应信息
        httpMessage.setResponse(response);
        httpMessage.setStatusCode(response.getStatusLine().getStatusCode());
		
		return httpMessage;
    }

   
    //下载资源封装类
    public static class HttpDownloadItem {
        private int type;                   //文件输入流类型
        private String url;                 //下载资源的url
        private InputStream inputStream;    //输入流

        public final static int TYPE_FILE = 0;      //本地文件流类型
        public final static int TYPE_INTERNET = 1;  //网络文件流类型

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public InputStream getInputStream() {
            return inputStream;
        }

        public void setInputStream(InputStream inputStream) {
            this.inputStream = inputStream;
        }
    }

    //HTTP请求、响应封装类
    public static class HttpMessage {
        private String url;                                 //请求的URL
        private List<NameValuePair> parameters;             //请求参数
        private Map<String, String> requestHeaders;         //请求Header
        private Map<String, Cookie> requestCookies;         //请求Cookie
        private int requestMethod = REQUEST_METHOD_GET;     //请求方式
        private Map<String, FileBody> fileMap;                //要上传的文件的FileBody对象
        private DefaultHttpClient httpClient;               //HttpClient对象
        private HttpResponse response;                      //响应
        private int statusCode;                             //响应状态码
        private Map<String, Cookie> responseCookies;        //响应Cookie
        private int connectTimeout = CONNECT_TIMEOUT;       //请求连接超时时间
        private int dataTimeout = DATA_TIMEOUT;             //请求数据传输超时时间
        private String responseString;

        public final static int REQUEST_METHOD_GET = 0;
        public final static int REQUEST_METHOD_POST = 1;
        public final static int REQUEST_METHOD_MULTIPART = 2;

        public boolean check() {
            if(url == null || url.trim().equals("")) {
                return false;
            }
            return true;
        }

        //添加请求参数
        public void addRequestParameter(String name, String value) {
            if(parameters == null) {
                parameters = new ArrayList<NameValuePair>();
            }
            parameters.add(new BasicNameValuePair(name, value));
        }

        //添加请求Header
        public void addRequestHeader(String name, String value) {
            if(requestHeaders == null) {
                requestHeaders = new HashMap<String, String>();
            }
            requestHeaders.put(name, value);
        }

        //添加请求Cookie
        public void addRequestCookie(String name, String value) {
            if(requestCookies == null) {
                requestCookies = new HashMap<String, Cookie>();
            }
            requestCookies.put(name, new BasicClientCookie(name, value));
        }

        //添加请求Cookie
        public void addRequestCookie(Cookie cookie) {
            if(requestCookies == null) {
                requestCookies = new HashMap<String, Cookie>();
            }
            requestCookies.put(cookie.getName(), cookie);
        }

        //批量添加请求Cookie
        public void addRequestCookies(Map<String, Cookie> cookies) {
            if(requestCookies == null) {
                requestCookies = new HashMap<String, Cookie>();
            }
            requestCookies.putAll(cookies);
        }

        //添加要上传的文件
        public void addFile(String name, FileBody fileBody) {
            if(fileMap == null) {
                fileMap = new HashMap<String, FileBody>();
            }
            fileMap.put(name, fileBody);
        }

        //简单添加要上传的文件
        public void addFile(String name, File file) {
            if(fileMap == null) {
                fileMap = new HashMap<String, FileBody>();
            }
            fileMap.put(name, new FileBody(file));
        }

        //获取指定名城的所有Header值
        public String[] getResponseHeaders(String name) {
            if(statusCode == HttpStatus.SC_OK && response != null) {
                Header[] headers = response.getHeaders(name);
                if(headers != null && headers.length > 0) {
                    String[] headerValues = new String[headers.length];
                    for(int i=0; i<headers.length; i++) {
                        headerValues[i] = headers[i].getValue();
                    }
                }
            }
            return null;
        }

        //获取指定名称的第一个Header值
        public String getResponseFirstHeader(String name) {
            if(statusCode == HttpStatus.SC_OK && response != null) {
                Header header = response.getFirstHeader(name);
                if(header != null) {
                    return header.getValue();
                }
            }
            return null;
        }

        //获取指定名称的最后一个Header值
        public String getResponseLastHeader(String name) {
            if(statusCode == HttpStatus.SC_OK && response != null) {
                Header header = response.getLastHeader(name);
                if(header != null) {
                    return header.getValue();
                }
            }
            return null;
        }

        //提取所有响应Cookie
        private void extractCookies() {
            responseCookies = new HashMap<String, Cookie>();
            if(statusCode == HttpStatus.SC_OK && response != null) {
                List<Cookie> cookies = httpClient.getCookieStore().getCookies();
                for(Cookie cookie : cookies) {
                    responseCookies.put(cookie.getName(), cookie);
                }
            }
        }

        //获取指定的响应Cookie
        public Cookie getResponseCookie(String name) {
            if(responseCookies == null) {
                extractCookies();
            }

            if(responseCookies.get(name) != null) {
                return responseCookies.get(name);
            }

            return null;
        }

        //获取http响应内容的输入流
        public InputStream getResponseInputStream() throws IOException {
            if(statusCode == HttpStatus.SC_OK && response != null) {
                InputStream in = null;
                in = response.getEntity().getContent();
                String contentEncoding = getResponseFirstHeader("Content-Encoding");
                if(contentEncoding != null && contentEncoding.trim().equalsIgnoreCase("gzip")) {
                    in = new GZIPInputStream(in);
                }
                return in;
            }

            return null;
        }

        //获取http响应的文本内容
        public String getResponseString() throws IOException {
            if(responseString == null) {
                InputStream in = getResponseInputStream();
                if(in != null) {
                    BufferedReader reader = null;
                    String line;
                    StringBuffer content = new StringBuffer();
                    try {
                        reader = new BufferedReader(new InputStreamReader(in));
                        while((line = reader.readLine()) != null) {
                            content.append(line).append("\r\n");
                        }
                        responseString = content.toString();
                    } finally {
                        if(reader != null) {
                            reader.close();
                        }
                        if(in != null) {
                            in.close();
                        }
                    }
                }
            }

            return responseString;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public List<NameValuePair> getParameters() {
            return parameters;
        }

        public Map<String, String> getRequestHeaders() {
            return requestHeaders;
        }

        public Map<String, Cookie> getRequestCookies() {
            return requestCookies;
        }

        public int getRequestMethod() {
            return requestMethod;
        }

        public void setRequestMethod(int requestMethod) {
            this.requestMethod = requestMethod;
        }

        public Map<String, FileBody> getFileMap() {
            return fileMap;
        }

        public void setHttpClient(DefaultHttpClient httpClient) {
            this.httpClient = httpClient;
        }

        public void setResponse(HttpResponse response) {
            this.response = response;
        }

        public int getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(int statusCode) {
            this.statusCode = statusCode;
        }

        public Map<String, Cookie> getResponseCookies() {
            if(responseCookies == null) {
                extractCookies();
            }

            return responseCookies;
        }

        public int getConnectTimeout() {
            return connectTimeout;
        }

        public void setConnectTimeout(int connectTimeout) {
            this.connectTimeout = connectTimeout;
        }

        public int getDataTimeout() {
            return dataTimeout;
        }

        public void setDataTimeout(int dataTimeout) {
            this.dataTimeout = dataTimeout;
        }
    }
}
