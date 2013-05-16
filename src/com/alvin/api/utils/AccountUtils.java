package com.alvin.api.utils;

import cn.com.pcgroup.android.framework.http.client.AsyncHttpClient;
import cn.com.pcgroup.android.framework.http.client.AsyncHttpResponseHandler;
import cn.com.pcgroup.android.framework.http.client.RequestParams;
import cn.com.pcgroup.common.android.utils.PreferencesUtils;

import com.alvin.api.model.Account;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

/***
 * 用户工具
 * 
 * @author poble
 * 
 */
public class AccountUtils {
	public static final int PASSPORT = 1;// 太平洋账号登陆类型
	public static final int SINA = 2; // 新浪微博登陆类型
	public static final int TENCENT = 3;// 腾讯微博登陆类型
	private static final String ACCOUNT_PRE = "account_pre";
	private static final String ACCOUNT_KEY = "account_key";
	private static String login_url = "http://mrobot.pcauto.com.cn/proxy/passport2/login";
	public final static String COOKIE_EXPIRED="10000";

	// 1、登陆
	public static void login(Context context, final String username, final String password,final LoginResult loginResul) {
		
		AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler(){
			int errorCode;
	       	String errorMessage;
			@Override
			public void onFailure(Throwable error, String content) {
				super.onFailure(error, content);
				errorCode = 5;
				errorMessage = "网络错误";
				loginResul.onFailure(errorCode, errorMessage);
			}

			@Override
			public void onSuccess(String content) {
				System.out.println(content);
				super.onSuccess(content);
				Account account = null;
		       	JSONObject jsonObj;
				try {
					jsonObj = new JSONObject(content);
					int status = jsonObj.optInt("status");
					errorCode = status;
					if(status == 0){//登陆成功
						account = new Account();
						account.setSessionId(jsonObj.optString("session"));
						account.setUserId(jsonObj.optString("userId"));
						account.setUsername(username);
						account.setPassword(password);
						account.setType(PASSPORT);
						account.setLoginTime(System.currentTimeMillis());
						saveAccount(context, account);
						loginResul.onSuccess(account);
						return;
					}else if(status == 2){
						errorMessage = "用户不存在,登录失败";
					}else if(status == 3){
						errorMessage = "密码错,登录失败";
					}else if(status == 4){
						errorMessage = "失败超过3次,登录失败";
					}else{
						errorMessage = "获取错误数据";
					}
				} catch (JSONException e) {
					errorCode = 5;
					errorMessage = "获取错误数据";
					e.printStackTrace();
				}
				loginResul.onFailure(errorCode, errorMessage);
			}
			
		};
		RequestParams params = new RequestParams();
		params.put("username", username);
		params.put("password", password);
		params.put("auto_login", COOKIE_EXPIRED);
		AsyncHttpClient.getHttpClientInstance().post(context, login_url, params, responseHandler);
	}

	// 2、注销
	public static boolean loginOut(Context context) {
		Account account = getLoginAccount(context);
		if(account==null){
			return true;
		}else{
			account.setSessionId("");
			saveAccount(context, account);
		}
		return true;
	}

	// 3、获取登陆用户实体
	public static Account getLoginAccount(Context context) {
		String account_message = PreferencesUtils.getPreference(context,
				ACCOUNT_PRE, ACCOUNT_KEY, "");
		Account account = null;
		try {
			if (!account_message.equals("")) {
				JSONObject accountJson = new JSONObject(account_message);
				account = new Account();
				account.setType(accountJson.optInt("type"));
				account.setUsername(accountJson.optString("username"));
				account.setPassword(accountJson.optString("password"));
				account.setSessionId(accountJson.optString("sessionId"));
				account.setDisplayName(accountJson.optString("displayName"));
				account.setUserId(accountJson.optString("userId"));
				account.setPhotoUrl(accountJson.optString("photoUrl"));
				account.setDescription(accountJson.optString("description"));
				account.setLoginTime(accountJson.optLong("loginTime"));
				account.setDefaults(accountJson.optInt("defaults"));
			}
		} catch (JSONException e) {
			account = null;
			e.printStackTrace();
		}

		return account;
	}
	
	//4、是否登陆
	public static boolean isLogin(Context context){
		Account account = getLoginAccount(context);
		if(null!=account&&!account.getSessionId().equals("")){
			return true;
		}
		return false;
	}

	// 保存登陆信息
	private static void saveAccount(Context context, Account account) {
		JSONObject accountJson = new JSONObject();
		try {
			accountJson.put("type", account.getType());
			accountJson.put("username", account.getUsername());
			accountJson.put("password", account.getPassword());
			accountJson.put("sessionId", account.getSessionId());
			accountJson.put("displayName", account.getDisplayName());
			accountJson.put("userId", account.getUserId());
			accountJson.put("photoUrl", account.getPhotoUrl());
			accountJson.put("description", account.getDescription());
			accountJson.put("loginTime", System.currentTimeMillis());
			accountJson.put("defaults", account.getDefaults());
			PreferencesUtils.setPreferences(context, ACCOUNT_PRE, ACCOUNT_KEY,
					accountJson.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	public interface LoginResult{
		public void onSuccess(Account accunt);
		public void onFailure(int errorCode,String errorMessage);
	}
}
