package com.alvin.oauth;

import com.alvin.app.CommonActivity.BaseActivity;
import com.alvin.common.R;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public abstract class OAuthActivity extends BaseActivity {
    private static OAuthActivity oauthActivity;
    public static PostOauth mPostOauth;
    protected Button titleBarB1;
    protected TextView titleView;
    protected WebView oauthWebView;
    protected ProgressBar loadProgressBar;
    
    protected final int HIDE_PROGRESS = 1;
    protected Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == HIDE_PROGRESS) {
                hideProgressBar();
                showLongToast("网络不通畅");
            } else {
                super.handleMessage(msg);
            }
        }
    };

    public void onCreate(Bundle savedInstanceState, OAuthActivity subOauthActivity) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.oauth_webview_layout);

        oauthActivity = subOauthActivity;
        initViews();
        oauthLogin();
    }
    
    private void initViews() {
        titleBarB1 = (Button)findViewById(R.id.title_bar_b1);
        titleBarB1.setBackgroundResource(R.drawable.navbar_button_back_default);
        titleBarB1.setText("返回");
        titleBarB1.setOnClickListener(backPressedListener);
        titleView = (TextView)findViewById(R.id.title_bar_text);
        
        loadProgressBar = (ProgressBar)findViewById(R.id.share_round_progress);
        
        oauthWebView = (WebView)findViewById(R.id.oauth_webview);
        oauthWebView.setScrollBarStyle(WebView.SCROLLBARS_INSIDE_OVERLAY);
        oauthWebView.getSettings().setJavaScriptEnabled(true);
        oauthWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url == null) return false;
                if (url.startsWith("weibo4android")) {
                    Intent intent = new Intent(OAuthActivity.this, CallbackActivity.class);
                    intent.setData(Uri.parse(url));
                    OAuthActivity.this.startActivity(intent);
                } else {
                    view.loadUrl(url);//腾讯微博在载入授权页面时会跳转，这里不写会用默认浏览器加载页面
                }
                return true;
            }
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                loadProgressBar.setVisibility(View.VISIBLE);
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                loadProgressBar.setVisibility(View.INVISIBLE);
            }
        });
    }
    
    protected abstract void oauthLogin();
    protected abstract void callback(Intent callbackIntent);
    
    public static void postLogin(Intent callbackIntent) {
        if (oauthActivity != null) {
            oauthActivity.callback(callbackIntent);
        }
        oauthActivity = null;
    }
    
    public void showProgressBar() {
        loadProgressBar.setVisibility(View.VISIBLE);
    }
    
    public void hideProgressBar() {
        loadProgressBar.setVisibility(View.INVISIBLE);
    }
}
