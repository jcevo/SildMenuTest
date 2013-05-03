package com.alvin.api.adapter;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ViewHold {
    public ImageView icon = null;
    public TextView name = null, introduce = null, cup = null,
            state = null;
    public String iconurl, downName;// 回调映射时对应的item
    public Button imageButton;
    public CheckBox selected;
    public int groupPosition, childPosition;
    // , delButton
    ;
    public CheckBox checkBox;
    public ProgressBar progressBar;// 进度条
    public TextView version;
    public ImageView safeImageView;
}
