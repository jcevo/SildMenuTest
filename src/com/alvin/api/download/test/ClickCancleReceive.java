package com.alvin.api.download.test;

import com.alvin.api.utils.CommonSettingsUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ClickCancleReceive extends BroadcastReceiver {

    OnClickCancleListener onClickCancleListener;

    public ClickCancleReceive(OnClickCancleListener listener) {
        onClickCancleListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

//        if (intent.getBooleanExtra(Settings.CANCLE_INTENT, false)) {
            
            if (intent.getStringExtra(CommonSettingsUtils.APK_NAME) != null
                    && (!intent.getStringExtra(CommonSettingsUtils.APK_NAME).equals(""))
                    && (intent.getStringExtra(CommonSettingsUtils.APK_NAME) != "")) {

                onClickCancleListener.OnClickCancle(intent
                        .getStringExtra(CommonSettingsUtils.APK_NAME),context);
            }
//        }
    }

    public interface OnClickCancleListener {
        public void OnClickCancle(String appName,Context context);
    }

}
