package com.alvin.api.abstractClass;

import com.alvin.api.config.Env;

import android.app.Activity;
import android.os.Bundle;

/**
 * 峰峰客户端公用基类，用于统一处理各种数据更新或重新初始化的工作
 */
public abstract class Alvin_BaseActivity extends Activity {
    // 如果发现系统配置没有初始化则初始化配置
    protected String TAG = Alvin_BaseActivity.class.getSimpleName();

    public Alvin_BaseActivity() {
        setTag();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Alvin_Application.activityIsFinish) {
            Alvin_Application.activityIsFinish = false;
        }
        // 获取屏幕尺寸
        Env.display = this.getWindowManager().getDefaultDisplay();
        // LogOutputUtils.i(TAG, "width:" + Env.display.getWidth() +
        // "|| height:"
        // + Env.display.getHeight());
        // int ScreenDipHeight = DisplayUtils.convertPX2DIP(this,
        // Env.display.getHeight());
        // Config.tabContentHeight = ScreenDipHeight -
        // Config.STATUS_BAR_HEIGHT - Config.TAB_BAR_HEIGHT;
    }

    /**
     * 作用: 设置tag名称
     */
    public abstract void setTag();

}
