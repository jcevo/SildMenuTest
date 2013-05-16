package com.alvin.slidMenu;

import com.alvin.api.config.Env;
import com.alvin.common.R;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

/***
 * 程序主SlidingActivity，
 * 
 * @author poble
 * 
 */
public class MainSlidingActivity extends SlidingFragmentActivity implements OnClickListener {
    private static String TAG = "MainSlidingActivity";
    public static int SLIDINGMENUSTATE = 0;// 记录当前slidingmenu状态;
    protected static SlidingMenu slidingMenu = null;
    // slidingmenu状态类别
    public static final int LEFT_AVAILABLE = 0; // 左侧可滑动
    public static final int ALL_UNAVAILABLE = 1; // 都不可滑动
    public static final int RIGHT_AVAILABLE = 2; // 右侧可以滑动
    public static final int All_AVAILABLE = 3; // 左右都可以滑动

    public static WindowFocusChangedListener windowFocusChangedListener = null;// 监控窗口焦点变化

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        initSlidingMenu();

    }

    /***
     * 初始化slidingmenu
     */
    private void initSlidingMenu() {
        setContentView(R.layout.frame_content);// 内容布局
        setBehindContentView(R.layout.frame_menu);// 左侧“个人中心”布局
        slidingMenu = getSlidingMenu();
        slidingMenu.setShadowWidth(7);// 设置阴影宽度
        slidingMenu.setShadowDrawable(R.drawable.shadow);// 阴影颜色
        slidingMenu.setBehindOffset(30);// 个人中心和设置页预留距离，为0时完全滑出
        slidingMenu.setFadeDegree(0.35f);// 阴影的透明度
        slidingMenu.setMode(SlidingMenu.LEFT_RIGHT);
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        slidingMenu.setSecondaryMenu(R.layout.menu_frame_two);// 右侧“设置”布局
        // 给布局添加内容
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.menu, new PersonalCenterFragment());// 个人中心
        fragmentTransaction.replace(R.id.menu_frame_two, new SettingFragment());// 设置页
        fragmentTransaction.replace(R.id.content, MainTabFragmentGenerator.generatorMainTabFragment(Env.appID));// 主页内容
        fragmentTransaction.commit();
        super.showMenu();
    }

    @Override
    public void onClick(View v) {
        toggle();// 切换菜单
    }

    /***
     * 改变slidingmenu状态
     * 
     * @param state
     */
    public static void changeSlidingMenuState(int state) {
        SLIDINGMENUSTATE = state;
        Log.v(TAG, "SLIDINGMENUSTATE:" + SLIDINGMENUSTATE);
        switch (SLIDINGMENUSTATE) {
        case LEFT_AVAILABLE:
            slidingMenu.setMode(SlidingMenu.LEFT); // 第一个。SlidingMenu仅有左边有效
            slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
            break;
        case ALL_UNAVAILABLE:
            slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE); // 中间，SlidingMenu无效
            break;
        case RIGHT_AVAILABLE:
            slidingMenu.setMode(SlidingMenu.RIGHT); // 最后一个，SlidingMenu仅有右边有效
            slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
            break;
        case All_AVAILABLE:
            slidingMenu.setMode(SlidingMenu.LEFT_RIGHT); // 左右均可,
            slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
            break;
        default:
            slidingMenu.setMode(SlidingMenu.LEFT); // 第一个。SlidingMenu仅有左边有效
            slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
            break;
        }
    }

    /***
     * 恢复slidingmenu之前的滑动状态(点击“个人中心”和“设置”页时， slidingmenu状态被重置，从以上两个页面回复到主页时候，
     * 需要恢复之前slidingmenu的状态)
     */
    @Override
    public void resumeMenuState() {
        resumeSlidingMenuState();
        super.resumeMenuState();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (null != windowFocusChangedListener && hasFocus) {
            windowFocusChangedListener.onWindowFocusChanged();
        }
    }

    /***
     * 恢复之前slidingmenu的状态
     */
    public void resumeSlidingMenuState() {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                changeSlidingMenuState(SLIDINGMENUSTATE);
            }
        }, 600);// 600ms，是滑动动画最大时间，等滑动状态结束，重置slidingmenu的状态
    }

    public interface WindowFocusChangedListener {
        public void onWindowFocusChanged();
    }

}
