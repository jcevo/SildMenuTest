package com.alvin.oauth;

import com.alvin.api.model.Account;
import com.alvin.common.R;
import com.alvin.common.utils.StringUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

public class OAuthUtils {
    private static AlertDialog loginDialog = null;
    private static String[] loginList = null;
    private static void initData(final Context context) {
        if (loginList == null) {
            loginList = context.getResources().getStringArray(R.array.share_list);
        }
    }
    
    /**
     * OAuth登录
     * @param context
     * @param intent {包含：request_code}
     */
    public static void oauthLogin(final Activity context, final Intent intent) {
        initData(context);
        loginDialog = new AlertDialog.Builder(context)
            .setTitle("登录")
            .setItems(loginList, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int position) {
                    if (position == 0) {//新浪微博
                        intent.putExtra("auth_type", Account.AUTH_TYPE_SINA);
                        gotoOAuth(context, intent);
                    }
                }
            }).create();
        loginDialog.show();
    }

    /**
     * OAuth登录
     * @param context
     * @param intent {包含：request_code、auth_type、[oauth_class_name]}
     */
    public static void gotoOAuth(final Activity context, final Intent intent) {
        String oauthClassName = intent.getStringExtra("oauth_class_name");
        if (!StringUtils.isBlank(oauthClassName)) {//指定OAuth类
            intent.setClassName(context, oauthClassName);
        } else {//未指定OAuth类，默认跳到新浪微博
            if (Account.AUTH_TYPE_SINA.equals(intent.getStringExtra("auth_type"))) {
                intent.setClass(context, SinaOAuth.class);
            }
        }
        context.startActivityForResult(intent, intent.getIntExtra("request_code", -1));
    }
    
    /**
     * 切换OAuth帐号
     * @param context
     * @param intent {包含：request_code、auth_type}
     */
    public static void changeOauth(final Activity context, final Intent intent) {
        gotoOAuth(context, intent);
    }
}
