package com.alvin.api.activity.menu;

import com.alvin.api.activity.common.BaseListViewActivity;

import android.widget.Button;
import android.widget.FrameLayout;

import java.util.List;

public class FavouriteActivity extends BaseListViewActivity {
    public int[] appID; // 收藏数据库的appId
    boolean gotoDisplayFsh = true;// 显示的是否为删除按钮
    Button delButton;// 删除按钮,完成按钮
    private List<Integer> delId;// 要删除的appid
    FavouriteActivity instance;
    FrameLayout frameLayout;
}
