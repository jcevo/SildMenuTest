package com.alvin.api.dialog;

import com.alvin.api.adapter.ShareListAdapter;
import com.alvin.api.adapter.ShareListAdapter.ShareSelectListener;
import com.alvin.api.bean.ShareCompanyInfo;
import com.alvin.api.utils.CommonSettingsUtils;
import com.alvin.common.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class ShareDialog extends Dialog {
    public final String TAG = ShareDialog.class.getSimpleName();
    private Button mCloseButton;
    private Button mOKButton;

    private List<ShareCompanyInfo> list;
    private ListView shareListViewl;
    private ShareListAdapter shareListAdapter;
    private Activity activity;
    ShareSelectListener shareSelectListener;

    public ShareDialog(Activity activity) {
        this(activity, R.style.LiteDialog);
    }

    public ShareDialog(Activity activity, int theme) {
        super(activity, theme);
        setContentView(R.layout.share_dialog);
        this.activity = activity;
        initData();
        initWidget();

    }

    public void initData() {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(
                CommonSettingsUtils.USERINFO_COLLECTION, activity.MODE_PRIVATE);
        list = new ArrayList<ShareCompanyInfo>();
        ShareCompanyInfo shareCompanyInfo = new ShareCompanyInfo();

        // 添加微博项
        shareCompanyInfo.setImgPath(R.drawable.sina_default);
        shareCompanyInfo.setText("分享至新浪微博");
        shareCompanyInfo.setCompany(CommonSettingsUtils.SINA_WEIBO);
        shareCompanyInfo.setSelected(sharedPreferences.getBoolean(
                shareCompanyInfo.getCompany(), false));
        list.add(shareCompanyInfo);

        // 添加微博项
        shareCompanyInfo = new ShareCompanyInfo();
        shareCompanyInfo.setCompany(CommonSettingsUtils.TENCENT_WEIBO);
        shareCompanyInfo.setImgPath(R.drawable.tencent_default);
        shareCompanyInfo.setText("分享至腾讯微博");
        shareCompanyInfo.setSelected(sharedPreferences.getBoolean(
                shareCompanyInfo.getCompany(), false));
        list.add(shareCompanyInfo);

        shareSelectListener = new ShareSelectListener() {

            @Override
            public void checkChanged(int position, boolean isSelected) {
                ShareCompanyInfo info = list.get(position);
                info.setSelected(isSelected);
                if (info.isSelected()) {
                    SharedPreferences sharedPreferences = activity
                            .getSharedPreferences(
                                    CommonSettingsUtils.USERINFO_COLLECTION,
                                    activity.MODE_PRIVATE);
                    String isbind = sharedPreferences.getString(
                            info.getCompany(), "");
                    if (isbind.equals("")) {
                        Log.e(TAG, "登录绑定微博" + info.getCompany());
                        if (info.getCompany().equals(CommonSettingsUtils.SINA_WEIBO)) {
                          //  activity.startActivity(new Intent(activity,AuthorizeActivity.class));
                        }
                    }
                } else {
                    Log.e(TAG, "没有选中" + info.getCompany());
                }
            }
        };

    }

    private void initWidget() {
        mCloseButton = (Button) findViewById(R.id.exit_close_window);
        mCloseButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mOKButton = (Button) findViewById(R.id.exit_ok_button);
        shareListViewl = (ListView) findViewById(R.id.share_list);
        shareListAdapter = new ShareListAdapter(activity, shareSelectListener);
        shareListViewl.setAdapter(shareListAdapter);
        shareListAdapter.setCount(list.size());
        shareListAdapter.updateList(list);
    }

    public void setExitListener(android.view.View.OnClickListener l) {
        mOKButton.setOnClickListener(l);
    }

}
