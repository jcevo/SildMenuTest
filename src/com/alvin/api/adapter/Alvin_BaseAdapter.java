package com.alvin.api.adapter;

import com.alvin.api.utils.AsyncImageLoader;
import com.alvin.api.utils.AsyncImageLoader.ImageCallback;
import com.alvin.api.utils.LogOutputUtils;
import com.alvin.api.utils.SyncImageLoader;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.Map;

public abstract class Alvin_BaseAdapter extends BaseAdapter {

    public Activity activity;
    public int count = 0;

    public Alvin_BaseAdapter(Activity activity) {
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }

    /**
     * 作用:获取viewhold和视图可以参照下面的方法返回视图Map
     */
    public abstract Map<String, Object> setupViews(View view, int layoutId);

    // {
    // Map<String, Object> map = new HashMap<String, Object>();
    // if (view == null) {
    // holder = new ViewHolder();
    // view = LayoutInflater.from(activity).inflate(layoutId, null);
    // holder.icon = (ImageView) view
    // .findViewById(R.id.recommend_app_icon);
    // holder.name = (TextView) view.findViewById(R.id.recommend_app_name);
    // holder.version = (TextView) view
    // .findViewById(R.id.recommend_app_version_name);
    // holder.size = (TextView) view.findViewById(R.id.recommend_app_size);
    // holder.summary = (TextView) view
    // .findViewById(R.id.recommend_app_introduce);
    // holder.imageButton = (Button) view
    // .findViewById(R.id.recommend_app_button);
    // holder.progressBar = (ProgressBar) view
    // .findViewById(R.id.loadProgressBar);
    // holder.checkBox = (CheckBox) view
    // .findViewById(R.id.recommend_app_select_button);
    // holder.state = (TextView) view.findViewById(R.id.download_state);
    // view.setTag(holder);
    // holder.safeImageView = (ImageView) view
    // .findViewById(R.id.recommend_app_safe);
    // } else {
    // holder = (ViewHolder) view.getTag();
    // }
    // map.put(SettingsUtils.VIEW, view);
    // map.put(SettingsUtils.VIEWHOLDE, holder);
    // return map;}

    /**
     * 作用:同步图片必须要用viewhold,否则会在同一个item上图片不停更新
     */
    protected void syncImage(String imgUrl, final ViewHold holder) {
        ImageCallback syncImageCallback = new ImageCallback() {
            public void imageLoaded(Drawable imageDrawable, String imageUrl) {
                LogOutputUtils.i("getView()", "imageurl:" + imageUrl + "drawable" + (imageDrawable == null));
                if (null != imageDrawable) {
                    if (holder.iconurl.equals(imageUrl)) {
                        holder.icon.setImageDrawable(imageDrawable);
                    }
                }
            }
        };

        SyncImageLoader.loadDrawable(imgUrl, syncImageCallback, activity);
    }

    /**
     * 
     * 作用: 异步下载图片
     * 
     * @param
     * 
     * @return String DOM对象
     */
    public static void AsyncImage(String iconUrl, final ViewHold holder) {
        AsyncImageLoader.loadDrawable(iconUrl, new ImageCallback() {
            public void imageLoaded(Drawable imageDrawable, String imageUrl) {
                if (null != imageDrawable) {
                    if (holder.iconurl.equals(imageUrl)) {
                        holder.icon.setImageDrawable(imageDrawable);
                    }
                }
            }
        });
    }

    public void justNotiView() {
        notifyDataSetChanged();
    }

}
