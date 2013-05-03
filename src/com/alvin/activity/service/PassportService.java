package com.alvin.activity.service;

import com.alvin.api.config.Env;
import com.alvin.api.model.Account;
import com.alvin.api.model.LoginInfo;
import com.alvin.common.R;
import com.alvin.common.utils.HttpUtils;
import com.alvin.common.utils.HttpUtils.HttpMessage;
import com.alvin.common.utils.StringUtils;
import com.alvin.db.DBHelper;
import com.alvin.db.UserDBHelper;
import com.alvin.ui.SimpleToast;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import weibo4android.User;
import weibo4android.Weibo;
import weibo4android.WeiboException;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.lang.reflect.Field;

public class PassportService {
    private final static String TAG = PassportService.class.getSimpleName();
    private final static String SESSION_EXPIRE = "3650";         //服务器保存天数

    public static String SESSION_ID_NAME = "common_session_id";

    private static boolean autoAuth = true;

    //登录接口，必须现在app里初始化
    public static String INTERFACE_LOGIN;
    public static String INTERFACE_LOGIN_OAUTH;
    public static String INTERFACE_AUTH_SESSION;
    public static String INTERFACE_REGISTER_EMAIL;
    public static String INTERFACE_REGISTER_OPEN;

    public static Account getAccount(String authType) {
        Account account = null;
        Cursor cursor = null;
        try {
            SQLiteDatabase db = Env.dbHelper.getReadableDatabase();
            cursor = db.rawQuery("select * from " + Env.dbHelper.USER_TABLE
                    + " where auth_type='" + authType + "'", null);
            if (cursor.getCount() > 0) {
                cursor.moveToNext();
                account = Account.parse(cursor);
            }
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }
        return account;
    }

    public static Account getPassportAccount() {
        Account account = null;
        Cursor cursor = null;
        try {
            SQLiteDatabase db = Env.dbHelper.getReadableDatabase();
            cursor = db.rawQuery("select * from " + Env.dbHelper.USER_TABLE
                    + " where is_passport = 1", null);
            if (cursor.getCount() > 0) {
                cursor.moveToNext();
                account = Account.parse(cursor);
            }
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }
        return account;
    }

    /**
     * 简单登录，从数据库中拿出用户名、密码登录
     * 
     * @return 返回登录信息
     */
    public synchronized static LoginInfo simpleLogin() {
        LoginInfo loginInfo = null;
        // 从数据库中读取用户信息
        Cursor cursor = null;
        SQLiteDatabase db = null;
        try {
            db = Env.dbHelper.getWritableDatabase();
            cursor = db.rawQuery("select * from " + Env.dbHelper.USER_TABLE
                    + " where is_passport=1", null);
            if (cursor.moveToNext() && cursor.getCount() > 0) {
                String username = cursor.getString(cursor
                        .getColumnIndex("username"));
                String password = cursor.getString(cursor
                        .getColumnIndex("password"));
                if (null != username && !"".equals(username)
                        && null != password && !"".equals(password)) {
                    loginInfo = login(username, password);
                }
            }
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }
        return loginInfo;
    }

    /**
     * 登录
     * 
     * @param username
     * @param password
     * @return 返回登录信息
     */
    public static LoginInfo login(String username, String password) {
        LoginInfo loginInfo = null;
        if (null == username || "".equals(username.trim()) || null == password
                || "".equals(password.trim())) {
            loginInfo = new LoginInfo();
            loginInfo.setStatus(1);
            loginInfo.setMessage("账号和密码必须输入");
            return loginInfo;
        }
        // ServerInterfaceManager.getInterface(ServerInterfaceManager.INTERFACE_MEIDA_MESSAGE)
//        String requestUrl = "http://test232.pclady.com.cn:7003/passport2/passport/login_m.jsp";
        HttpMessage httpMessage = new HttpMessage();
        httpMessage.setUrl(INTERFACE_LOGIN + "?req_enc=utf-8&resp_enc=utf-8");
        httpMessage.addRequestParameter("username", username.trim());
        httpMessage.addRequestParameter("password", password.trim());
        httpMessage.addRequestParameter("auto_login", SESSION_EXPIRE);
        httpMessage.setRequestMethod(HttpMessage.REQUEST_METHOD_POST);
        String JSONStr = null;
        JSONObject jsonObj = null;
        try {
            httpMessage = HttpUtils.invoke(httpMessage);
            JSONStr = httpMessage.getResponseString();
            jsonObj = new JSONObject(JSONStr);
            if (null != jsonObj) {
                loginInfo = new LoginInfo();
                loginInfo.setStatus(Integer.parseInt(jsonObj
                        .optString("status")));
                loginInfo.setSessionId(jsonObj.optString("session"));
                loginInfo.setMessage(jsonObj.optString("message"));
                loginInfo.setUserId(jsonObj.optString("userId"));
            }
        } catch (JSONException e) {
            Log.e(TAG, "prase loginInfor json error");
            e.printStackTrace();
            loginInfo = null;
        } catch (Exception e1) {
            e1.printStackTrace();
            loginInfo = null;
        }
        // 登录成功后，将用户信息保存到本地数据库中
        if (null != loginInfo) {
            if ("0".equals(jsonObj.optString("status"))) {
                Cursor cursor = null;
                SQLiteDatabase db = null;
                try {
                    db = Env.dbHelper.getWritableDatabase();
                    resetPassportAcount();
                    cursor = db.query(Env.dbHelper.USER_TABLE, new String[] {
                            "username", "password", "passport_id", "session_id","is_passport" },
                            "auth_type=?", new String[] { "passport" }, null,
                            null, null);
                    ContentValues contentVaues = new ContentValues();
                    contentVaues.put("username", username.trim());
                    contentVaues.put("password", password.trim());
                    contentVaues.put("session_id", loginInfo.getSessionId());
                    contentVaues.put("passport_id", loginInfo.getUserId());
                    contentVaues.put("auth_type", "passport");
                    contentVaues.put("is_passport", true);
                    contentVaues.put("time", System.currentTimeMillis());
                    if (cursor.moveToNext() && cursor.getCount() > 0) {
                        db.update(Env.dbHelper.USER_TABLE, contentVaues,
                                "auth_type=?", new String[] { "passport" });
                    } else {
                        db.insert(Env.dbHelper.USER_TABLE, null, contentVaues);
                    }
                } finally {
                    if (null != cursor) {
                        cursor.close();
                    }
                }
            }
            return loginInfo;
        }
        return loginInfo;
    }

    /**
     * 通过该方法，拿到用户登录后的common_session_id
     * 
     * @return session_id
     */
    public static String getCommonSessionId() {
        // 从数据库中读取session信息
        String common_session_id = null;
        Cursor cursor = null;
        SQLiteDatabase db = null;
        try {
            db = Env.dbHelper.getWritableDatabase();
            cursor = db.rawQuery("select * from " + Env.dbHelper.USER_TABLE
                    + " where is_passport=1", null);
            if (cursor.moveToNext() && cursor.getCount() > 0) {
                common_session_id = cursor.getString(cursor
                        .getColumnIndex("session_id"));
            }
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }
        return common_session_id;
    }

    /**
     * 通过该方法，拿到用户登录后的user_id
     * 
     * @return session_id
     */
    public static String getPassportId() {
        // 从数据库中读取user_id信息
        String passport_id = null;
        Cursor cursor = null;
        SQLiteDatabase db = null;
        try {
            db = Env.dbHelper.getWritableDatabase();
            cursor = db.rawQuery("select * from " + Env.dbHelper.USER_TABLE
                    + " where is_passport=1", null);
            if (cursor.moveToNext() && cursor.getCount() > 0) {
                passport_id = cursor.getString(cursor.getColumnIndex("passport_id"));
            }
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }
        return passport_id;
    }

    /**
     * 判断用户是否登录
     * 
     * @return session_id
     */
    public static boolean isLogin() {
        // 从数据库中读取session信息
        String common_session_id = getCommonSessionId();
        if (null != common_session_id && !"".equals(common_session_id)) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * 用户注销
     * 
     * @return true 表示注销成功，false 注销失败
     */
    public static boolean loginOut() { 
//        System.out.println("***********login out:"+System.currentTimeMillis());
        long stat = System.currentTimeMillis();
     // 从数据库中读取user_id信息
        Cursor cursor = null;
        SQLiteDatabase db = null;
        try {
            db = Env.dbHelper.getWritableDatabase();
            db.delete(Env.dbHelper.USER_TABLE, "auth_type!=?", new String[]{
                    Account.AUTH_TYPE_PASSPORT
            });
            cursor = db.rawQuery("select * from " + Env.dbHelper.USER_TABLE
                    + " where is_passport=1", null);
            if (cursor.moveToNext() && cursor.getCount() > 0) {
                ContentValues contentVaues = new ContentValues();
                contentVaues.put("session_id", "");
                contentVaues.put("is_passport", 0);
                db.update(Env.dbHelper.USER_TABLE, contentVaues, "is_passport=1", null);
            }else{
            }
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }
        long end = System.currentTimeMillis();
//        System.out.println("*************** totoal time :  "+(end-stat));
       return true;
    }

    public static void enableAutoAuthenticate() {
        autoAuth = true;
    }

    public static void disableAutoAuthenticate() {
        autoAuth = false;
    }

    public static synchronized boolean authenticate() throws Exception {
        if(!autoAuth) {
            return true;
        }

        boolean isSuccess = true;

        String commonSessionId = PassportService.getCommonSessionId();
        if(commonSessionId != null && commonSessionId.trim().length() > 0) {
            HttpMessage message = new HttpMessage();
            String url = INTERFACE_AUTH_SESSION + "?req_enc=UTF-8&resp_enc=UTF-8&" +
                    SESSION_ID_NAME + "=" + getCommonSessionId();
            message.setUrl(url);
//            System.out.println("##### authenticate session: " + url);
            HttpUtils.invoke(message);
            if(message.getStatusCode() == HttpStatus.SC_UNAUTHORIZED) {
                isSuccess = false;
                Log.i(TAG, "Session expire, need login again");
            }
        } else {
            isSuccess = false;
            Log.i(TAG, "Session lost, need login again");
        }

        if(!isSuccess && Env.topActivity != null) {
//            LoginInfo loginInfo = PassportService.simpleLogin();
//            if(loginInfo == null || loginInfo.getStatus() != LoginInfo.STATUS_SUCCESS) {
                PassportService.LoginListener listener = new PassportService.LoginListener() {
                    private String username;
                    private String password;

                    public void login(String username, String password) {
                        this.username = username;
                        this.password = password;
                    }

                    public void success() {
                        SimpleToast.show(Env.topActivity, "登陆成功", Toast.LENGTH_SHORT);
                    }

                    public void fail() {
                        Env.topActivity.runOnUiThread(new PassportService.LoginThread(
                                this, username, password));
                        SimpleToast.show(Env.topActivity, "登陆失败", Toast.LENGTH_SHORT);
                    }

                    public void error() {
                        SimpleToast.show(Env.topActivity, "用户名、密码不能为空", Toast.LENGTH_SHORT);
                    }

                    public void cancel() {

                    }
                };
                Env.topActivity.runOnUiThread(
                        new PassportService.LoginThread(listener, null, null));
                do {
                    Thread.sleep(100);
                } while(listener.getStatus() != PassportService.LoginListener.STATUS_SUCCESS
                        && listener.getStatus() != PassportService.LoginListener.STATUS_CANCEL);
                if(listener.getStatus() == PassportService.LoginListener.STATUS_SUCCESS) {
                    isSuccess = true;
                }
//            }
        }

        return isSuccess;
    }

    public static class LoginThread implements Runnable {
        private LoginListener listener;
        private String username;
        private String password;

        public LoginThread(LoginListener listener, String username, String password) {
            this.listener = listener;
            this.username = username;
            this.password = password;
        }

        public void run() {
            LayoutInflater factory = LayoutInflater.from(Env.topActivity);
            View textEntryView = factory.inflate(R.layout.login_dialog, null);
            final EditText usernameEdit = (EditText)textEntryView.findViewById(R.id.username_edit);
            final EditText passwordEdit = (EditText)textEntryView.findViewById(R.id.password_edit);

            if(username != null && username.trim().length() > 0
                    && password != null && password.trim().length() > 0) {
                usernameEdit.setText(username);
                passwordEdit.setText(password);
            } else {
                Account account = PassportService.getPassportAccount();
                if(account != null) {
                    if(account.getUsername() != null && account.getUsername().trim().length() > 0) {
                        usernameEdit.setText(account.getUsername());
                    }
                    if(account.getPassword() != null && account.getPassword().trim().length() > 0) {
                        passwordEdit.setText(account.getPassword());
                    }
                }
            }
            new AlertDialog.Builder(Env.topActivity.getParent() == null ?
                    Env.topActivity : Env.topActivity.getParent())
                    .setTitle("登录太平洋会员")
                    .setView(textEntryView)
                    .setPositiveButton("登录", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            String username = usernameEdit.getText().toString();
                            String password = passwordEdit.getText().toString();
                            if(username != null && username.trim().length() > 0
                                    && password != null && password.trim().length() > 0) {
                                try {
                                    Field field = dialog.getClass().getSuperclass()
                                            .getDeclaredField("mShowing");
                                    field.setAccessible(true);
                                    field.set(dialog, true);
                                } catch (NoSuchFieldException e) {
                                    e.printStackTrace();
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                }
                                listener.login(username, password);
                                LoginInfo loginInfo = login(username, password);
                                if(loginInfo != null &&
                                        loginInfo.getStatus() == LoginInfo.STATUS_SUCCESS) {
                                    listener.setStatus(LoginListener.STATUS_SUCCESS);
                                    listener.success();
                                } else {
                                    listener.setStatus(LoginListener.STATUS_FAIL);
                                    listener.fail();
                                }
                            } else {
                                try {
                                    Field field = dialog.getClass().getSuperclass()
                                            .getDeclaredField("mShowing");
                                    field.setAccessible(true);
                                    field.set(dialog, false);
                                } catch (NoSuchFieldException e) {
                                    e.printStackTrace();
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                }
                                listener.setStatus(LoginListener.STATUS_ERROR);
                                listener.error();
                            }
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            listener.setStatus(LoginListener.STATUS_CANCEL);
                            listener.cancel();
                            dialog.cancel();
                        }
                    }).create().show();
        }
    }
    
    /**
     * OAuth登录
     * @param dataIntent {
     *   oauth_user_id  String 新浪用户ID
     *   token          String
     *   secret         String
     * }
     */
    public static boolean oauthLoginPC(final Intent dataIntent) {
        String loginUrl = INTERFACE_LOGIN_OAUTH + "?type=sina_lady&resp_enc=utf-8";
        HttpMessage httpMessage = new HttpMessage();
        httpMessage.setUrl(loginUrl);
        httpMessage.addRequestParameter("open_account_id", String.valueOf(dataIntent.getLongExtra("oauth_user_id", 0)));
        httpMessage.addRequestParameter("auto_login", SESSION_EXPIRE);
        String cookieValue = "oauth_token="+dataIntent.getStringExtra("token")
            + "&oauth_token_secret="+dataIntent.getStringExtra("secret")
            + "&user_id="+dataIntent.getLongExtra("oauth_user_id", 0);
        httpMessage.addRequestCookie("sina_lady.access_token", cookieValue);
        httpMessage.setRequestMethod(HttpMessage.REQUEST_METHOD_POST);
        try {
            HttpUtils.invoke(httpMessage);
            if (httpMessage.getStatusCode() == HttpStatus.SC_OK) {
                String jsonStr = httpMessage.getResponseString();
                if (!StringUtils.isBlank(jsonStr)) {
                    JSONObject rootObject = new JSONObject(jsonStr.trim());
                    if (rootObject.getInt("status") == 0) {
                        dataIntent.putExtra("is_bind_pc", true);
                        dataIntent.putExtra("session", rootObject.getString("session"));
                        dataIntent.putExtra("account", rootObject.getString("account"));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static void resetPassportAcount(){
        ContentValues values = new ContentValues();
        values.put("is_passport", 0);
        Env.dbHelper.getWritableDatabase().update(DBHelper.USER_TABLE, values, null, null);
    }

    public static void updateUserMessage(String sessionId,String accountId){
        resetPassportAcount();
        ContentValues values = new ContentValues();
        values.put("is_passport", 1);
        values.put("passport_id", accountId);
        values.put("session_id", sessionId);
        UserDBHelper.updateUser(values, "", new String[] { Account.AUTH_TYPE_SINA });
    }

    public static abstract class LoginListener {
        private int status = STATUS_NORMAL;

        public final static int STATUS_NORMAL = 0;
        public final static int STATUS_SUCCESS = 1;
        public final static int STATUS_FAIL = -1;
        public final static int STATUS_ERROR = -2;
        public final static int STATUS_CANCEL = -9;

        public abstract void login(String username, String password);
        public abstract void success();
        public abstract void fail();
        public abstract void error();
        public abstract void cancel();

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }
    
    /*
     * 绑定接口规则
	 *URL：/api/register4open.jsp?json=1&type=sina_lady&resp_enc=utf-8
	 *方法：POST
	 *参数：
	 *1）username 通行证用户名
	 *2）password 通行证密码
	 *3）open_account_id 新浪微博的id
	 *4）screen_name 新浪微博的昵称 
	 *5）bind 设为1
	 *Cookie： 
	 *	sina_lady.access_token=oauth_token=<用户的oauth token>&oauth_token_secret=<用户	的oauth token secret>&user_id=<新浪微博账号id>
	 *	返回：
	 *1）status = 0 登录&绑定成功 
	 *2）status = 其他值 绑定失败，失败原因在desc标签中
     * 
     * */
    public static void binding(final String username,final String passWord,
    						   final String open_account_id,final String screen_name,
    						   final String oauth_token,final String oauth_token_secret,
    						   final String user_id,final BindListener bindListener){
    	
    	new Thread(new Runnable(){
			@Override
			public void run() {
				String InfoURL = PassportService.INTERFACE_REGISTER_OPEN +
                        "?json=1&type=sina_lady&req_enc=utf-8&resp_enc=utf-8";
				HttpMessage hm = new HttpMessage();
				hm.setUrl(InfoURL);
				hm.addRequestParameter("username", username);
				hm.addRequestParameter("password", passWord);
				hm.addRequestParameter("open_account_id", open_account_id);
				hm.addRequestParameter("screen_name", screen_name);
				hm.addRequestParameter("bind", "1");
				//Cookie
				String Cookie = "oauth_token="+oauth_token+
								"&oauth_token_secret="+oauth_token_secret+
								"&user_id="+user_id;
				hm.addRequestCookie("sina_lady.access_token", Cookie);
				
				hm.setRequestMethod(HttpMessage.REQUEST_METHOD_POST);
				try {
					hm=HttpUtils.invoke(hm);
					String json = hm.getResponseString();
					bindListener.bind(json);
				} catch (Exception e) {
					e.printStackTrace();
					SimpleToast.show(Env.topActivity, "绑定失败", Toast.LENGTH_SHORT);
				}
			}
		}).start();
    	
    }
    
    public static interface BindListener{
    	public void bind(String json);
    }
    
    
    //操作类型
    //public static final String MEIDA_CREATE = "meida_create";	//发布
    //public static final String MEIDA_LIKE = "meida_like";		//喜欢
    //public static final String MEIDA_COMMENT = "meida_comment";	//评论
    
    public static Intent checkSinaBind(long oauthUserId, String token, String secret) {
        Intent dataIntent = new Intent();
        dataIntent.putExtra("oauth_user_id", oauthUserId);
        dataIntent.putExtra("token", token);
        dataIntent.putExtra("token", secret);
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
                    weibo.setToken(dataIntent.getStringExtra("token"), dataIntent.getStringExtra("secret"));
                    weibo.setOAuthConsumer(Weibo.CONSUMER_KEY, Weibo.CONSUMER_SECRET);
                    try {
                        User sinaUser = weibo.showUser(String.valueOf(dataIntent.getLongExtra("oauth_user_id", 0)));
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
                    String screenName = mCursor.getString(mCursor.getColumnIndex("oauth_nickname"));
                    String openAccountId = String.valueOf(oauthUserId);
                    String userId = openAccountId;
                    BindListener mBindListener = new BindListener() {
                        @Override
                        public void bind(String json) {
//                            Log.i("CDH","binding json:"+json);
//                            Log.i("CDH","binding json:"+json);
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
        return dataIntent;
    }
}
