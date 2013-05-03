package com.alvin.activity.tabhost;

import com.alvin.api.utils.CommonSettingsUtils;
import com.alvin.common.R;
import com.alvin.common.utils.DisplayUtils;

import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * 项目名称：app43 类名称：TabHostActivityGroup 类描述：Tabhost的父类 创建人：APP43 创建时间：2012-3-13
 * 下午1:06:42 修改人：APP43 修改时间：2012-3-13 下午1:06:42 修改备注：
 * 
 * @version
 * 
 */
public class TabHostActivityGroup extends ActivityGroup {

    protected final static String TAG = TabHostActivityGroup.class
            .getSimpleName();

    protected static Map<Integer, int[]> tabIconMap = new HashMap<Integer, int[]>();// tanhost的item图片ID
    protected static int flag = 0;// 判断是否为从通知栏点击进来应用管理界面
    protected String textId[];
    protected TabHost tabHost;
    protected TabWidget tabWidget;
    protected int ItemType_5 = 5, ItemType_1 = 1;// 5:每个item有独立的icon.1:为统一一张背景
    public int tabhost_top, tabhost_bottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setItemIcon();
        setItemText();
        setTopBottom();
        setItemActivityHeight();
        updateMainTabView();
    }

    public void setTopBottom() {
        tabhost_top = CommonSettingsUtils.TABHOST_TOP;
        tabhost_bottom = CommonSettingsUtils.TABHOST_BOTTOM;
    }

    // 设置各个item图片
    protected void setItemIcon() {
        tabIconMap.put(0, new int[] { R.drawable.main_tab_frame_0_current,
                R.drawable.main_tab_frame_0 });
        tabIconMap.put(1, new int[] { R.drawable.main_tab_frame_1_current,
                R.drawable.main_tab_frame_1 });
        tabIconMap.put(2, new int[] { R.drawable.main_tab_frame_2_current,
                R.drawable.main_tab_frame_2 });
        tabIconMap.put(3, new int[] { R.drawable.main_tab_frame_3_current,
                R.drawable.main_tab_frame_3 });
        tabIconMap.put(4, new int[] { R.drawable.main_tab_frame_4_current,
                R.drawable.main_tab_frame_4 });
    }

    // 各个item标题
    protected void setItemText() {
        textId = new String[] { CommonSettingsUtils.MAIN_TAB_0,
                CommonSettingsUtils.MAIN_TAB_1, CommonSettingsUtils.MAIN_TAB_2,
                CommonSettingsUtils.MAIN_TAB_3, CommonSettingsUtils.MAIN_TAB_4 };
    }

    /**
     * 作用:设置子activity的高度
     */
    protected void setItemActivityHeight() {
        Display display = getWindowManager().getDefaultDisplay();
        int ScreenDipHeight = DisplayUtils.convertPX2DIP(this,
                display.getHeight());
        int tabContentHeight = ScreenDipHeight - tabhost_top - tabhost_bottom; // 设置tab子Activity的高度

        this.setContentView(R.layout.main_tabhost_frame_activity);
        FrameLayout tabFrame = (FrameLayout) this
                .findViewById(android.R.id.tabcontent);
        tabFrame.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT, DisplayUtils
                        .convertDIP2PX(this, tabContentHeight)));
    }

    /**
     * 作用:设置tabhost各个子item
     */
    public void updateMainTabView() {

        // 设置Tabhost
        tabHost = (TabHost) findViewById(R.id.tabhost);
        tabHost.setup(this.getLocalActivityManager());
        tabWidget = tabHost.getTabWidget();
        setTabStyle();

    }

    protected void setTabStyle() {
        // 设置item0tab
        // tabhostAddTab(tabHost, R.layout.main_tabhost_frame_tab_item,
        // SettingsUtils.MAIN_TAB_0, R.drawable.main_tab_frame_0, 0,
        // "item0", TabItem0Activity.class);
        //
        // tabhostAddTab(tabHost, R.layout.main_tabhost_frame_tab_item,
        // SettingsUtils.MAIN_TAB_1, R.drawable.main_tab_frame_1, 1,
        // "item1", TabItem1Activity.class);
        //
        // tabhostAddTab(tabHost, R.layout.main_tabhost_frame_tab_item,
        // SettingsUtils.MAIN_TAB_2, R.drawable.main_tab_frame_2, 2,
        // "item2", TabItem2Activity.class);
        //
        // tabhostAddTab(tabHost, R.layout.main_tabhost_frame_tab_item,
        // SettingsUtils.MAIN_TAB_3, R.drawable.main_tab_frame_3, 3,
        // "item3", TabItem3Activity.class);
        //
        // tabhostAddTab(tabHost, R.layout.main_tabhost_frame_tab_item,
        // SettingsUtils.MAIN_TAB_4, R.drawable.main_tab_frame_4, 4,
        // "item4", TabItem4Activity.class);
        
    }
       
    /**
     * 作用: 每个item有独立,添加完icon之后,要配置动作 setTabAction(ItemType_5);
     */
    protected void addTabWith5(final TabHost tabHost, int itemLayoutId,
            String itemText, int iconID, int itemId, String itemIdString,
            Class activityClass) {
        // 设置tabLayout
        RelativeLayout tabLayout = (RelativeLayout) LayoutInflater.from(this)
                .inflate(itemLayoutId, null);
        TextView item0TabText = (TextView) tabLayout
                .findViewById(R.id.tab_text);
        ImageView item0TabIcon = (ImageView) tabLayout
                .findViewById(R.id.tab_icon);
        item0TabText.setText(itemText);
        item0TabIcon.setBackgroundResource(iconID);

        // 初始化精品intent 和tabspec并添加到tabhost
        Intent tabIntent = new Intent();
        tabIntent.setClass(this, activityClass);
        tabIntent.putExtra("item", itemId);
        TabSpec tabSpec = tabHost.newTabSpec(itemIdString);
        tabSpec.setIndicator(tabLayout);// 加载view
        tabSpec.setContent(tabIntent);
        tabHost.addTab(tabSpec);

    }

    /**
     * 作用:遍历所有item,并且设置当前选中item样式
     */
    private void forTabSet5(TabWidget tabWidget,
            RelativeLayout tabWidgetLayout, TabHost tabHost) {
        for (int i = 0; i < tabWidget.getChildCount(); i++) {
            tabWidgetLayout = (RelativeLayout) tabWidget.getChildAt(i);
            TextView text = (TextView) tabWidgetLayout
                    .findViewById(R.id.tab_text);
            ImageView icon = (ImageView) tabWidgetLayout
                    .findViewById(R.id.tab_icon);
            int[] tabIcon = tabIconMap.get(i);

            // 当前tab为选中时高亮图片
            if (tabHost.getCurrentTab() == i) {
                icon.setBackgroundResource(tabIcon[0]);
                text.setTextColor(this.getResources().getColorStateList(
                        R.color.white));
                tabWidgetLayout
                        .setBackgroundDrawable(getResources()
                                .getDrawable(
                                        R.drawable.main_tab_frame_tabspec_background_current));
            } else {
                icon.setBackgroundResource(tabIcon[1]);
                text.setTextColor(this.getResources().getColorStateList(
                        R.color.gray));
                tabWidgetLayout.setBackgroundDrawable(null);
            }
        }
    }

    /**
     * 作用:只切换背景图,添加完icon之后,要配置动作 setTabAction(ItemType_1);
     */
    protected void addTabWith1(TabHost tabHost, int itemId,
            String itemIdString, Class activityClass) {

        LinearLayout linear = new LinearLayout(this);
        linear.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.FILL_PARENT, 1));
        // 初始化精品intent 和tabspec并添加到tabhost
        Intent tabIntent = new Intent();
        tabIntent.setClass(this, activityClass);
        tabIntent.putExtra("item", itemId);
        TabSpec tabSpec = tabHost.newTabSpec(itemIdString);
        tabSpec.setIndicator(linear);// 加载view
        tabSpec.setContent(tabIntent);
        tabHost.addTab(tabSpec);

    }

    private void forTabSet1(TabWidget tabWidget, LinearLayout tabWidgetLayout,
            TabHost tabHost) {
        for (int i = 0; i < tabWidget.getChildCount(); i++) {
            if (tabHost.getCurrentTab() == i) {
                tabWidget.setBackgroundResource(tabIconMap.get(i)[0]);
            }

        }
    }

    protected void setTabAction(final int type) {
        // 设置初始化样式.

        tabHost.setCurrentTab(flag);
        if (type == 1) {
            LinearLayout layout = null;
            forTabSet1(tabWidget, layout, tabHost);
        } else if (type == 5) {
            RelativeLayout tabWidgetLayout = null;
            forTabSet5(tabWidget, tabWidgetLayout, tabHost);
        }

        tabHost.setOnTabChangedListener(new OnTabChangeListener() {
            @Override
            public void onTabChanged(String arg0) {

                // 设置item
                if (type == 1) {
                    LinearLayout layout = null;
                    forTabSet1(tabWidget, layout, tabHost);
                } else if (type == 5) {
                    RelativeLayout tabWidgetLayout = null;
                    forTabSet5(tabWidget, tabWidgetLayout, tabHost);
                }
            }
        });
    }

}
