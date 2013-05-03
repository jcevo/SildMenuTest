package com.alvin.api.activity.common;

/**
 * 这个是带lietView下拉自动刷新的公共类
 * 
 * 每个继承的Activity类都要调用setInitData()
 */
import com.alvin.api.abstractClass.SendDataBaseActivity;

import android.os.Handler;
import android.widget.ListView;

public class BaseListViewActivity extends SendDataBaseActivity {
    protected ListView listView;

    @Override
    protected Handler initHandle() {
        return null;
    }

    @Override
    protected void handleViews(String jsonString) {

    }

    @Override
    public void setupViews() {

    }

    @Override
    public void setTag() {
        TAG = BaseListViewActivity.class.getSimpleName();
    }

}
