package com.alvin.api.abstractClass;


import com.alvin.api.config.Env;
import com.alvin.api.utils.PhoneInfoUtils;
import com.alvin.api.utils.CommonSettingsUtils;
import com.alvin.app.CommonActivity.BaseActivity;
import com.alvin.common.R;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

/**
 * 启动界面
 */
public abstract class Alvin_LauncherActivity extends BaseActivity {

    public int STAY = 2; // 界面停留时间，单位：秒
    protected Class firstDestClass, secondDestClass;
    public int flag = 1;// 默认为1,启动页就是程序主页,2:从启动页跳转到程序主页,3:从启动页跳转,如果第一主页运行过就直接跳到第二主页

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PhoneInfoUtils.setPhoneInfo(this);
        setLayout();

    }

    public void setLayout() {
        this.setContentView(R.layout.launcher_activity);
        if (Env.versionName != null) {
            TextView textView = (TextView) this
                    .findViewById(R.id.launcher_version_text);
            // textView.setTextSize(DisplayUtils.convertDIP2PX(this, 18));
            textView.setText("V " + Env.versionName);
        }
    };

    private final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            setHandleMsg();
        }
    };

    protected void setHandleMsg() {
        Intent intent = new Intent();
        switch (flag) {
        case 1:
            break;
        case 2:

            intent.setClass(Alvin_LauncherActivity.this, firstDestClass);
            startActivity(intent);
            Alvin_LauncherActivity.this.overridePendingTransition(R.anim.fade,
                    R.anim.hold);
            Alvin_LauncherActivity.this.finish();
            break;

        case 3:
            SharedPreferences sharedPreferences = getSharedPreferences(
                    CommonSettingsUtils.USERINFO_COLLECTION, MODE_PRIVATE);
            if (sharedPreferences.getBoolean(CommonSettingsUtils.ISFIRSTIN, true)) {
                intent.setClass(Alvin_LauncherActivity.this, firstDestClass);
                int width = getWindowManager().getDefaultDisplay().getWidth();
                int height = getWindowManager().getDefaultDisplay().getHeight();
                PhoneInfoUtils.setResolution(width + "*" + height);
            } else {
                intent.setClass(Alvin_LauncherActivity.this, secondDestClass);
            }
            startActivity(intent);
            Alvin_LauncherActivity.this.overridePendingTransition(R.anim.fade,
                    R.anim.hold);
            Alvin_LauncherActivity.this.finish();
            break;
        default:
            break;
        }

    }

    /**
     * 
     * 作用:设置启动页
     * 
     * @param
     */
    public abstract void setFirstDestClass(Class firstDestClass);

    /**
     * 作用:设置程序主页
     * 
     * @param
     */
    public abstract void setSecondDestClass(Class secondDestClass);

    /**
     * 作用:设置跳转标记
     * 
     * @param f
     *            : 为1时,不用设置,firstDestClass,为2时,只设置firstDestClass,为3时,两个都要设置
     */
    public abstract void setFlag(int f);

    private class PreloadThread extends Thread {
        private Handler handler;

        public PreloadThread(Handler handler) {
            this.handler = handler;
        }

        public void run() {
            long start = System.currentTimeMillis();
            try {
                // 初始化系统配置
                // Config.initWithoutNetwork(LauncherActivity.this);
                //
                // //初始化栏目信息
                // ChannelUtils.initClinetChannel(LauncherActivity.this);
                //
                // //初始化离线下载网络状态
                // MoreSettingActivity.defaultNetworkSetting();
                //
                // new Thread() {
                // public void run() {
                // //异步加载初始化论坛板块数据，无需访问网络
                // BbsApiService bbsApiService =
                // BbsApiService.getInstance(LauncherActivity.this);
                // bbsApiService.getRootForum();
                //
                // //异步加载初始化车型库数据
                // if(Config.WEBSITE == Config.WEBSITE_PCAUTO){
                // ProductApiService productApiService =
                // ProductApiService.getInstance(LauncherActivity.this);
                // productApiService.getBrandList();
                // }
                // }
                // }.start();
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                while ((System.currentTimeMillis() - start) <= STAY * 1000) {
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            handler.sendMessage(handler.obtainMessage());
        }
    }

    public void onResume() {
        super.onResume();
        // 预载首界面数据，然后跳转到首界面
        new PreloadThread(handler).start();
    }
}
