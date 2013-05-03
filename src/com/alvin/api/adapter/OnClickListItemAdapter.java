package com.alvin.api.adapter;

import com.alvin.api.bean.Bean;
import com.alvin.api.utils.CommonSettingsUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

/**
 * 跳转到没有上一页下一页的终端页.所以不用传serverlist 项目名称：app43 类名称：BaseOnClickListItemAdapter
 * 类描述： 创建人：APP43 创建时间：2012-2-19 下午2:02:41 修改人：APP43 修改时间：2012-2-19 下午2:02:41
 * 修改备注：
 * 
 * @version
 * 
 */
public abstract class OnClickListItemAdapter extends BaseAdapter {
    protected String TAG;
    public Activity activity;
    public int appcount = 0;

    public OnClickListItemAdapter(Activity activitys, String tag) {
        TAG = tag;
        activity = activitys;

    }

    public void setAppcount(int appcount) {
        this.appcount = appcount;
    }

    @Override
    public int getCount() {
        return appcount;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }

    public void justNotifyView() {
        notifyDataSetChanged();
    }

    /**
     * 通用listView点击Item跳转到终端页
     */
    public void onClickItemToContent(String stateText, String pageUrl,
            int pageNo, int localId, ArrayList<Bean> serverAppList,
            Activity activitys) {
    };

    /**
     * category跳转到相应Activity
     * 
     * @param pageUrl
     * @param pageNo
     * @param activitys
     */
    public void onClickItemToList(String pageUrl, int pageNo,
            Activity fromActivity, Activity toActivity) {
        Intent intent = new Intent();
        intent.setClass(fromActivity, toActivity.getClass());
        intent.putExtra(CommonSettingsUtils.PAGEURL, pageUrl);
        intent.putExtra(CommonSettingsUtils.PAGENO, pageNo);
        fromActivity.startActivity(intent);
    }

    /**
     * 
     * 作用:通用列表adapter
     * 
     * @param
     * 
     * @return String DOM对象
     */
    public void updateList(ArrayList<Bean> sApps, boolean serverFinish,
            List<Bean> lApps, boolean localFinish, List<Bean> iApps,
            boolean installFinish, ProgressBar pb, String pageUrl, int pageNo,
            String who) {
    }

    /**
     * 
     * 作用: 收藏夹adapter用
     * 
     * @param
     * 
     * @return String DOM对象
     */
    public void updateList(ArrayList<Bean> sApps, boolean serverFinish,
            List<Bean> lApps, boolean localFinish, List<Bean> iApps,
            boolean installFinish, ProgressBar pb, boolean del,
            Handler handler, String pageUrl, int pageNo, int apps[], String who) {
    }



    public static class ProgressData {
        public String apkName;
        public int progress;

        public String getApkName() {
            return apkName;
        }

        public void setApkName(String apkName) {
            this.apkName = apkName;
        }

        public int getProgress() {
            return progress;
        }

        public void setProgress(int progress) {
            this.progress = progress;
        }
    }
}
