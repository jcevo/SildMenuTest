package com.alvin.api.adapter;

import com.alvin.api.bean.ShareCompanyInfo;
import com.alvin.api.utils.CommonSettingsUtils;
import com.alvin.common.R;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShareListAdapter extends Alvin_BaseAdapter {
    public final String TAG = ShareListAdapter.class.getSimpleName();
    public List<ShareCompanyInfo> shareList = new ArrayList<ShareCompanyInfo>();
    private ShareSelectListener shareSelectListener;

    public ShareListAdapter(Activity activity,
            ShareSelectListener shareSelectListener) {
        super(activity);
        this.shareSelectListener = shareSelectListener;
    }

    @Override
    public Map<String, Object> setupViews(View view, int layoutId) {
        Map<String, Object> map = new HashMap<String, Object>();
        ViewHold viewHold;
        if (view == null) {
            viewHold = new ViewHold();
            view = LayoutInflater.from(activity).inflate(layoutId, null);
            viewHold.icon = (ImageView) view.findViewById(R.id.share_com_img);
            viewHold.name = (TextView) view.findViewById(R.id.share_com_text);
            viewHold.selected = (CheckBox) view
                    .findViewById(R.id.share_com_select);
            view.setTag(viewHold);
        } else {
            viewHold = (ViewHold) view.getTag();
        }
        map.put(CommonSettingsUtils.VIEW, view);
        map.put(CommonSettingsUtils.VIEWHOLDE, viewHold);
        return map;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHold viewHold = new ViewHold();
        Map<String, Object> map = new HashMap<String, Object>();
        map = setupViews(convertView, R.layout.share_item);
        convertView = (View) map.get(CommonSettingsUtils.VIEW);
        viewHold = (ViewHold) map.get(CommonSettingsUtils.VIEWHOLDE);
        Log.e("viewhold", "null?" + (viewHold == null));
        final int index = position;
        final ShareCompanyInfo shareCompanyInfo = shareList.get(index);
        viewHold.icon.setBackgroundResource(shareCompanyInfo.getImgPath());
        viewHold.name.setText(shareCompanyInfo.getText());
        final CheckBox checkBox = viewHold.selected;
        viewHold.selected.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                shareCompanyInfo.setSelected(!shareCompanyInfo.isSelected());
                checkBox.setChecked(shareCompanyInfo.isSelected());
                shareSelectListener.checkChanged(index,
                        shareCompanyInfo.isSelected());
            }
        });

        convertView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                shareCompanyInfo.setSelected(!shareCompanyInfo.isSelected());
                checkBox.setChecked(shareCompanyInfo.isSelected());
                shareSelectListener.checkChanged(index,
                        shareCompanyInfo.isSelected());
            }
        });

        return convertView;
    }

    public void updateList(List<ShareCompanyInfo> list) {
        shareList = list;
        justNotiView();
    }

    public interface ShareSelectListener {
        public void checkChanged(int position, boolean isSelected);
    }
}
