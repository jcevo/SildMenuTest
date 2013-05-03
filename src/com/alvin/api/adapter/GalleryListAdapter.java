package com.alvin.api.adapter;

/**
 * 通用焦点图适配器
 */
import com.alvin.api.utils.AsyncImageLoader;
import com.alvin.api.utils.AsyncImageLoader.ImageCallback;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GalleryListAdapter extends Alvin_BaseAdapter {
    static final String TAG = GalleryListAdapter.class.getSimpleName();
    public int mGalleryItemBackground;
    public int count = Integer.MAX_VALUE; // 取最大值 循环用
    // static final public int MAXIMAGE = 5; // 图片数
    public List<String> focusList = new ArrayList<String>();
    public DisplayMetrics dm;
    public ProgressBar progressBar;

    public int width, heigh, padding, defaultID;
    public ScaleType scaleType;

    // static int max;//判断有多少张图片然后循环

    /**
     * 创建一个新的实例 GalleryListAdapter.
     * 
     * @param activity
     * @param width
     * @param heigh
     * @param padding
     * @param defalutID
     *            默认加载图片资源ID
     * @param scaleType
     *            gallery显示的类型
     */
    public GalleryListAdapter(Activity activity, int width, int heigh,
            int padding, int defalutID, ScaleType scaleType) {
        super(activity);
        dm = new DisplayMetrics();
        dm = activity.getResources().getDisplayMetrics();
        this.width = width;
        this.heigh = heigh;
        this.padding = padding;
        this.defaultID = defalutID;
        this.scaleType = scaleType;
       
    }

    public int getCount() {
        return count;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final ImageView imageView = new ImageView(activity);
        imageView.setImageDrawable(activity.getResources().getDrawable(
                defaultID));
        if (focusList.size() != 0) {
            final int index = position % focusList.size();// 设置图片滑动循环

            if (null != focusList && index <= focusList.size() - 1) {
                String imgUrl = focusList.get(index);
                if (!(imgUrl == null && imgUrl.equals(""))) {
                    Drawable drawable = AsyncImageLoader.loadBitmap(imgUrl);
                    if (drawable != null) {
                        imageView.setImageDrawable(drawable);
                    } else {
                        progressBar.setVisibility(View.VISIBLE);
                        AsyncImageLoader.loadDrawable(focusList.get(index),
                                new ImageCallback() {
                                    public void imageLoaded(
                                            Drawable imageDrawable,
                                            String imageUrl) {
                                        progressBar
                                                .setVisibility(View.INVISIBLE);
                                        if (null != imageDrawable) {
                                            imageView
                                                    .setImageDrawable(imageDrawable);
                                        }
                                    }
                                });
                    }
                }
            }
        }

        // 设置显示比例类型
        imageView.setScaleType(scaleType);
        // 设置布局图片110×200显示
        imageView.setLayoutParams(new Gallery.LayoutParams(width, heigh));
        imageView.setPadding(padding, 0, padding, 0);
        return imageView;
    }

    public void updateImage(List<String> foApps, ProgressBar pb) {
        progressBar = pb;
        focusList = foApps;
        notifyDataSetChanged();
    }

    @Override
    public Map<String, Object> setupViews(View view, int layoutId
            ) {
        return null;
    }

}
