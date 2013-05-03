package com.alvin.oauth;

import com.alvin.activity.service.PassportService;
import com.alvin.activity.service.PassportService.BindListener;
import com.alvin.api.config.Env;
import com.alvin.api.model.Account;
import com.alvin.common.utils.CacheUtils;
import com.alvin.common.utils.DownloadUtils;
import com.alvin.common.utils.DownloadUtils.DownloadListener;
import com.alvin.common.utils.StringUtils;
import com.alvin.db.UserDBHelper;
import com.alvin.share.ShareActivity;
import com.alvin.share.ShareActivity.PostShareListener;

import weibo4android.User;
import weibo4android.Weibo;
import weibo4android.WeiboException;
import weibo4android.http.AccessToken;
import weibo4android.http.RequestToken;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Date;

public class SinaOAuth extends OAuthActivity {
    private final SinaOAuth THIS = this;
    private static final String PCLADY_SINA_USER_ID = "2345187730";
    private RequestToken requestToken;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, this);
        titleView.setText("新浪微博登录");
    }

    @Override
    protected void oauthLogin() {
        new Thread() {
            @Override
            public void run() {
                System.setProperty("weibo4j.oauth.consumerKey", Weibo.CONSUMER_KEY);
                System.setProperty("weibo4j.oauth.consumerSecret", Weibo.CONSUMER_SECRET);
                Weibo weibo = new Weibo();
                try {
                    requestToken = weibo.getOAuthRequestToken("weibo4android://CallbackActivity");
                    Uri uri = Uri.parse(requestToken.getAuthenticationURL()+ "&display=mobile");
                    oauthWebView.loadUrl(uri.toString());//自定义WebView
                } catch (WeiboException e) {
                    mHandler.sendEmptyMessage(HIDE_PROGRESS);
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    public void callback(Intent callbackIntent) {
        Uri uri = callbackIntent.getData();
        AccessToken accessToken = null;
        try {
            accessToken = requestToken.getAccessToken(uri.getQueryParameter("oauth_verifier"));
        } catch (WeiboException e) {
            e.printStackTrace();
            accessToken = null;
        }

        if (accessToken == null) {
            THIS.showShortToast("授权失败，请重新授权");
        } else if (mPostOauth != null) {
            mPostOauth.postOauth(this, accessToken.getUserId(), accessToken.getToken(), accessToken.getTokenSecret());
            mPostOauth = null;
        } else {
            loadProgressBar.setVisibility(View.VISIBLE);
            //登录成功
            loginSuccess(getIntent(), accessToken);
        }
    }
    
    private void loginSuccess(final Intent dataIntent, final AccessToken accessToken) {
        new Thread() {
            @Override
            public void run() {
                //存储数据到Intent
                dataIntent.putExtra("auth_type", Account.AUTH_TYPE_SINA);
                dataIntent.putExtra("oauth_user_id", accessToken.getUserId());
                dataIntent.putExtra("token", accessToken.getToken());
                dataIntent.putExtra("secret", accessToken.getTokenSecret());

                //存储数据到数据库
                UserDBHelper.persistUser(Account.AUTH_TYPE_SINA, accessToken.getUserId(), accessToken.getToken(), accessToken.getTokenSecret(), "");

                //首页登录需要Check绑定
                if (dataIntent.getBooleanExtra("check_bind", false)) {
                    boolean noError = PassportService.oauthLoginPC(dataIntent);
                    if (noError) {//loginPC没有异常
                        Cursor mCursor = UserDBHelper.getPassPortUser();
                        boolean hasPassportUser = mCursor != null && mCursor.moveToFirst();
                        if (dataIntent.getBooleanExtra("is_bind_pc", false)) {//微博帐号与Passport绑定过
                            if (!hasPassportUser) {
                                PassportService.resetPassportAcount();
                                ContentValues values = new ContentValues();
                                values.put("session_id", dataIntent.getStringExtra("session"));
                                values.put("passport_id", Integer.valueOf(dataIntent.getStringExtra("account")));
                                values.put("is_passport", 1);
                                UserDBHelper.updateUser(values, "auth_type = ?", new String[]{Account.AUTH_TYPE_SINA});
                            }
                        } else {//微博帐号没有与Passport绑定
                            if (!hasPassportUser) {
                                //当前没有Passport帐号，需要获取微博呢称和头像URL
                                Weibo weibo = new Weibo();
                                weibo.setToken(accessToken);
                                weibo.setOAuthConsumer(Weibo.CONSUMER_KEY, Weibo.CONSUMER_SECRET);
                                try {
                                    User sinaUser = weibo.showUser(String.valueOf(accessToken.getUserId()));
                                    if (sinaUser != null) {
                                        dataIntent.putExtra("screen_name", sinaUser.getScreenName());
                                        dataIntent.putExtra("profile_image_url", sinaUser.getProfileImageURL().toString());
                                    }
                                } catch (WeiboException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                //当前有Passport帐号
                                String username = mCursor.getString(mCursor.getColumnIndex("username"));
                                String passWord = mCursor.getString(mCursor.getColumnIndex("password"));
                                String openAccountId = String.valueOf(dataIntent.getLongExtra("oauth_user_id", 0));
                                String screenName = mCursor.getString(mCursor.getColumnIndex("oauth_nickname"));
                                String token = dataIntent.getStringExtra("token");
                                String secret = dataIntent.getStringExtra("secret");
                                String userId = openAccountId;
                                BindListener mBindListener = new BindListener() {
                                    @Override
                                    public void bind(String json) {
//                                        Log.i("CDH","binding json:"+json);
//                                        Log.i("CDH","binding json:"+json);
                                    }
                                };
                                PassportService.binding(username, passWord, openAccountId, screenName
                                        , token, secret, userId, mBindListener);
                            }
                        }
                        if (mCursor != null) mCursor.close();
                    } else {
                        dataIntent.putExtra("oauth_login_pc_error", true);
                    }
                }

                if (dataIntent.getBooleanExtra("goto_share", false)) {
                    dataIntent.setClass(THIS, ShareActivity.class);
                    THIS.startActivity(dataIntent);
                } else {
                    THIS.setResult(RESULT_OK, dataIntent);
                }
                THIS.finish();
            }
        }.start();
    }
    
    /**
     * 检查是否关注PClady新浪微博
     * @param context
     */
    public static void checkFollowPClady(final Context context) {
        Cursor mCursor = UserDBHelper.getUserByFlag(Account.AUTH_TYPE_SINA);
        try {
            if (mCursor != null && mCursor.moveToFirst()) {
                checkAttentionPClady(context
                        , mCursor.getString(mCursor.getColumnIndex("token"))
                        , mCursor.getString(mCursor.getColumnIndex("secret"))
                        , String.valueOf(mCursor.getLong(mCursor.getColumnIndex("oauth_user_id")))
                        , null);
            }
        } finally {
            if (mCursor != null) mCursor.close();
        }
    }

    /**
     * 检查是否关注PClady新浪微博 http://weibo.com/pcladymeida
     * @param token String 新浪Token
     * @param secret  String 新浪Secret
     * @param oauthUserId  String 新浪用户ID
     * @param mPostShareListener
     */
    public static void checkAttentionPClady(final Context context, final String token, final String secret, final String oauthUserId, final PostShareListener mPostShareListener) {
        new Thread() {
            @Override
            public void run() {
                int status = PostShareListener.RESULT_FIALED;
                Weibo weibo = new Weibo();
                weibo.setToken(token, secret);
                weibo.setOAuthConsumer(Weibo.CONSUMER_KEY, Weibo.CONSUMER_SECRET);
                try {
                    boolean hadAttention = weibo.existsFriendship(oauthUserId, PCLADY_SINA_USER_ID);
                    if (hadAttention) {
                        status = PostShareListener.RESULT_OK;
                        //保存到SharePreference
                        SharedPreferences sPre = context.getSharedPreferences("Meida", Context.MODE_PRIVATE);
                        if (!sPre.getBoolean("is_follow", false)) {
                            sPre.edit().putBoolean("is_follow", true).commit();
                        }
                    }
                } catch (WeiboException e) {
                    e.printStackTrace();
                    status = PostShareListener.RESULT_FIALED;
                }
                if (mPostShareListener != null) mPostShareListener.postShare(status);
            }
        }.start();
    }
    
    /**
     * 关注PClady新浪微博 http://weibo.com/pcladymeida
     * @param intent {
     *    token   String 新浪Token
     *    secret  String 新浪Secret
     * }
     * @param mPostShareListener
     */
    public static void attentionPClady(final Context context, final String token, final String secret, final PostShareListener mPostShareListener) {
        new Thread() {
            @Override
            public void run() {
                int status = PostShareListener.RESULT_FIALED;
                Weibo weibo = new Weibo();
                weibo.setToken(token, secret);
                weibo.setOAuthConsumer(Weibo.CONSUMER_KEY, Weibo.CONSUMER_SECRET);
                try {
                    User sinaUser = weibo.createFriendshipByUserid(PCLADY_SINA_USER_ID);
                    if (sinaUser != null) {
                        status = PostShareListener.RESULT_OK;
                        //保存到SharePreference
                        SharedPreferences sPre = context.getSharedPreferences("Meida", Context.MODE_PRIVATE);
                        if (!sPre.getBoolean("is_follow", false)) {
                            sPre.edit().putBoolean("is_follow", true).commit();
                        }
                    }
                } catch (WeiboException e) {
                    e.printStackTrace();
                    status = PostShareListener.RESULT_FIALED;
                }
                if (mPostShareListener != null) mPostShareListener.postShare(status);
            }
        }.start();
    }

    /**
     * 取消关注
     * @param context
     */
    public static void unfollowPClady(final Context context) {
        String[] tokenSecret = UserDBHelper.getTokenAndSecret(Account.AUTH_TYPE_SINA);
        if (tokenSecret != null) {
            unfollowPClady(context, tokenSecret[0], tokenSecret[1], null);
        }
    }

    /**
     * 取消关注
     * @param context
     * @param token
     * @param secret
     * @param mPostShareListener
     */
    public static void unfollowPClady(final Context context, final String token, final String secret, final PostShareListener mPostShareListener) {
        new Thread() {
            @Override
            public void run() {
                int status = PostShareListener.RESULT_FIALED;
                Weibo weibo = new Weibo();
                weibo.setToken(token, secret);
                weibo.setOAuthConsumer(Weibo.CONSUMER_KEY, Weibo.CONSUMER_SECRET);
                try {
                    User sinaUser = weibo.destroyFriendshipByUserid(PCLADY_SINA_USER_ID);
                    if (sinaUser != null) {
                        status = PostShareListener.RESULT_OK;
                        //保存到SharePreference
                        SharedPreferences sPre = context.getSharedPreferences("Meida", Context.MODE_PRIVATE);
                        if (sPre.getBoolean("is_follow", false)) {
                            sPre.edit().putBoolean("is_follow", false).commit();
                        }
                    }
                } catch (WeiboException e) {
                    e.printStackTrace();
                    status = PostShareListener.RESULT_FIALED;
                }
                if (mPostShareListener != null) mPostShareListener.postShare(status);
            }
        }.start();
    }

    /**
     * 发送不带图片的微博信息
     * @param context
     * @param intent [包含：token、secret、share_content]
     * @param mPostShareListener 发送微博后的回调
     */
    public static void sendWeiboWithoutPhoto(final Activity context, final Intent intent, final PostShareListener mPostShareListener) {
        new Thread() {
            @Override
            public void run() {
                sendWeibo(context, intent, mPostShareListener, null);
            }
        }.start();
    }

    /**
     * 发送带图片的微博信息
     * @param context
     * @param intent [包含：token、secret、share_content、photo_file_path/web_photo_url]
     * @param mPostShareListener 发送微博后的回调
     */
    public static void sendWeiboWithPhoto(final Activity context, final Intent intent, final PostShareListener mPostShareListener) {
        new Thread() {
            @Override
            public void run() {
                String photoPath = intent.getStringExtra("photo_file_path");//本地图片
                if (photoPath == null) {
                    photoPath = intent.getStringExtra("web_photo_url");//网络图片
                    if (photoPath != null) {
                        //获取网上图片
                        DownloadListener mDownloadListener = new DownloadListener() {
                            @Override
                            public void downloadFinished(InputStream inputStream) {
                                String fileName = StringUtils.formatDate2String(new Date(), "'photo'_yyyyMMddHHmmss")+".jpg";
                                File mFile = new File(Env.externalFileDir, fileName);
                                FileOutputStream mFileOutputStream = null;
                                try {
                                    mFileOutputStream = new FileOutputStream(mFile);
                                    byte[] buffer = new byte[1024];
                                    while (inputStream.read(buffer) != -1) {
                                        mFileOutputStream.write(buffer);
                                        mFileOutputStream.flush();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    mFile = null;
                                } finally {
                                    try {
                                        if (mFileOutputStream != null) mFileOutputStream.close();
                                        if (inputStream != null) inputStream.close();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                sendWeibo(context, intent, mPostShareListener, mFile);
                            }
                        };
                        DownloadUtils mDownloadUtils = DownloadUtils.getInstance();
                        try {
                            mDownloadUtils.addDownloadTask(SinaOAuth.class, photoPath, CacheUtils.EXPIRE_IMAGE
                                   , CacheUtils.CACHE_EXTERNAL, mDownloadListener, false);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        //没用图片
                        sendWeibo(context, intent, mPostShareListener, null);
                    }
                } else {
                    sendWeibo(context, intent, mPostShareListener, new File(photoPath));
                }
            }
        }.start();
    }
    
    private static void sendWeibo(Activity context, Intent intent, PostShareListener mPostShareListener, File photoFIle) {
        int status = PostShareListener.RESULT_FIALED;
        AccessToken accessToken = new AccessToken(intent.getStringExtra("token"), intent.getStringExtra("secret"));
        Weibo weibo = new Weibo();
        weibo.setToken(accessToken);
        weibo.setOAuthConsumer(Weibo.CONSUMER_KEY, Weibo.CONSUMER_SECRET);
        try {
            if (photoFIle == null) {
                weibo.updateStatus(intent.getStringExtra("share_content"));
            } else {
                weibo.uploadStatus(intent.getStringExtra("share_content"), photoFIle);
            }
            status = PostShareListener.RESULT_OK;
        } catch (WeiboException e) {
            if (e.getStatusCode() == 400 && e.getMessage().contains("40025")) {
                //不能发送相同内容的微博信息
                status = PostShareListener.RESULT_SAME;
            } else if ((e.getStatusCode() == 401 && e.getMessage().contains("40013")) || (e.getStatusCode() == 400 && e.getMessage().contains("40072"))) {
                status = PostShareListener.RESULT_INVALID;

                //删除当前失效Token
                //UserDBHelper.deleteUserByUserType(Account.AUTH_TYPE_SINA);

                //重新登录
                if (context != null) {
                    intent.putExtra("share_content", intent.getStringExtra("share_content"));
                    intent.setClass(context, SinaOAuth.class);
                    context.startActivityForResult(intent, intent.getIntExtra("request_code", -1));
                }
            } else {
                //分享失败
                status = PostShareListener.RESULT_FIALED;
                e.printStackTrace();
            }
        }
        if (mPostShareListener != null) mPostShareListener.postShare(status);
    }
}
