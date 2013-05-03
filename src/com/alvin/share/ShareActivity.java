package com.alvin.share;

import com.alvin.api.model.Account;
import com.alvin.app.CommonActivity.BaseActivity;
import com.alvin.common.R;
import com.alvin.oauth.SinaOAuth;
import com.alvin.oauth.TencentOAuth;
import com.tencent.weibo.beans.QParameter;
import com.tencent.weibo.utils.OAuthClient;
import com.tencent.weibo.utils.QHttpClient;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ShareActivity extends BaseActivity {
    private final ShareActivity THIS = this;
    private EditText mEditView;
    private TextView mTextView;
    private ProgressBar roundProgressBar;
    private static Random RAND = new Random();
    private Handler shareHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case PostShareListener.RESULT_OK:
                    THIS.showShortToast("分享成功");
                    THIS.onBackPressed();
                    break;
                case PostShareListener.RESULT_FIALED:
                    THIS.showShortToast("分享失败");
                    break;
                case PostShareListener.RESULT_SAME:
                    THIS.showShortToast("不能分享相同内容");
                    break;
            }
            if (roundProgressBar != null) roundProgressBar.setVisibility(View.INVISIBLE);
        }
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_layout);
        initViews();
    }
    
    private void initViews() {
        final Intent intent = this.getIntent();
        Button titleBarB1 = (Button)findViewById(R.id.title_bar_b1);
        titleBarB1.setBackgroundResource(R.drawable.navbar_button_back_default);
        titleBarB1.setText("返回");
        titleBarB1.setOnClickListener(backPressedListener);
        TextView titleTextView = (TextView)findViewById(R.id.title_bar_text);
        if (Account.AUTH_TYPE_SINA.equals(intent.getStringExtra("auth_type"))) {
            titleTextView.setText("分享到微博");
        }
        Button titleBarB2 = (Button)findViewById(R.id.title_bar_b2);
        titleBarB2.setBackgroundResource(R.drawable.navbar_button_red_default_9patch);
        titleBarB2.setVisibility(View.VISIBLE);
        titleBarB2.setText("切换帐号");
        titleBarB2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Account.AUTH_TYPE_SINA.equals(intent.getStringExtra("auth_type"))) {
                    intent.setClass(THIS, SinaOAuth.class);
                }
                THIS.startActivity(intent);
            }
        });

        roundProgressBar = (ProgressBar)findViewById(R.id.share_round_progress);
        mEditView = (EditText)findViewById(R.id.share_content);
        mEditView.setText(intent.getStringExtra("share_content"));
        mEditView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                mTextView.setText("还可输入"+(140 - mEditView.getText().toString().length())+"字");
            }
        });
        mTextView = (TextView)findViewById(R.id.share_word_limit);

        mTextView.setText("还可输入"+(140 - mEditView.getText().toString().length())+"字");
        
        findViewById(R.id.share_submit).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                roundProgressBar.setVisibility(View.VISIBLE);
                if (Account.AUTH_TYPE_SINA.equals(intent.getStringExtra("auth_type"))) {
                    PostShareListener mPostShareListener = new PostShareListener() {
                        @Override
                        public void postShare(int status) {
                            shareHandler.sendEmptyMessage(status);
                        }
                    };
                    SinaOAuth.sendWeiboWithPhoto(THIS, intent, mPostShareListener);
                } else if (Account.AUTH_TYPE_TENCENT.equals(intent.getStringExtra("auth_type"))) {
                    //创建腾讯API中的T_API.java对象会抛异常，这里自己实现发微博功能
                    List<QParameter> parameters = new ArrayList<QParameter>();
                    parameters.add(new QParameter("format", "json"));
                    parameters.add(new QParameter("content", mEditView.getText().toString()));
                    parameters.add(new QParameter("clientip", "127.0.0.1"));
                    parameters.add(new QParameter("jing", ""));
                    parameters.add(new QParameter("wei", ""));
                    parameters.add(new QParameter("oauth_consumer_key", TencentOAuth.CONSUMER_KEY));
                    parameters.add(new QParameter("oauth_signature_method", "HMAC-SHA1"));
                    long timestamp = System.currentTimeMillis() / 1000;
                    long nonce = timestamp + RAND.nextInt();
                    parameters.add(new QParameter("oauth_timestamp", String.valueOf(timestamp)));
                    parameters.add(new QParameter("oauth_nonce", String.valueOf(nonce)));
                    parameters.add(new QParameter("oauth_token", intent.getStringExtra("token")));
                    parameters.add(new QParameter("oauth_version", "1.0"));

                    OAuthClient oac = new OAuthClient();
                    String url = "http://open.t.qq.com/api/t/add";
                    String queryString = oac.getOauthParams(url, "POST", TencentOAuth.CONSUMER_SECRET, intent.getStringExtra("secret"), parameters);

                    QHttpClient http = new QHttpClient();
                    try {
                        String result = http.httpPost(url, queryString);
                        if (result.contains("\"errcode\":0,")) {
                            showShortToast("成功分享到腾讯微博");
                        } else if(result.contains("\"errcode\":13,")) {
                            showShortToast("不能发送相同内容");
                        } else if(result.contains("\"errcode\":10,")) {
                            showShortToast("分享失败，发表频率太快");
                        } else if(result.contains("\"errcode\":4,")) {
                            showShortToast("分享失败，内容脏话过多");
                        } else if(result.contains("\"errcode\":9,")) {
                            showShortToast("分享失败，包含垃圾信息");
                        } else {
                            showShortToast("分享失败，内容不合适");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        showShortToast("分享失败");
                    }
                }
            }
        });
    }
    
    public static interface PostShareListener {
        public static final int RESULT_OK = 0;//分享成功
        public static final int RESULT_FIALED = 1;//分享失败
        public static final int RESULT_SAME = 2;//内容相同
        public static final int RESULT_INVALID = 3;//帐号失效
        public void postShare(int status);
    }
}
