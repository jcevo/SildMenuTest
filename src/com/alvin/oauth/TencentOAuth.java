package com.alvin.oauth;

import com.alvin.api.model.Account;
import com.alvin.db.UserDBHelper;
import com.alvin.share.ShareActivity;
import com.tencent.weibo.beans.OAuth;
import com.tencent.weibo.utils.OAuthClient;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class TencentOAuth extends OAuthActivity {
    public static final String CONSUMER_KEY = "801000209";
    public static final String CONSUMER_SECRET = "618503d56971889dbae26467dae129ab";
    private OAuthClient oauthClient;
    private OAuth oauth; 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, this);
        titleView.setText("腾讯微博登录");
    }

    @Override
    protected void oauthLogin() {
        oauth = new OAuth(CONSUMER_KEY, CONSUMER_SECRET, "weibo4android://CallbackActivity");
        oauthClient = new OAuthClient();
        try {
            oauthClient.requestToken(oauth);
            String authUrl = "http://open.t.qq.com/cgi-bin/authorize?oauth_token="+oauth.getOauth_token();
            oauthWebView.loadUrl(authUrl);//自定义WebView
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void callback(Intent callbackIntent) {
        Uri uri = callbackIntent.getData();
        oauth.setOauth_verifier(uri.getQueryParameter("oauth_verifier"));
        try {
            oauthClient.accessToken(oauth);
        } catch (Exception e) {
            e.printStackTrace();
        }
        UserDBHelper.persistUser(Account.AUTH_TYPE_TENCENT, 0, oauth.getOauth_token(), oauth.getOauth_token_secret(),"");

        Intent intent = new Intent(TencentOAuth.this, ShareActivity.class);
        intent.putExtra("share_content", getIntent().getStringExtra("share_content"));
        intent.putExtra("auth_type", Account.AUTH_TYPE_TENCENT);
        intent.putExtra("token", oauth.getOauth_token());
        intent.putExtra("secret", oauth.getOauth_token_secret());
        this.startActivity(intent);
        this.finish();
    }
}
