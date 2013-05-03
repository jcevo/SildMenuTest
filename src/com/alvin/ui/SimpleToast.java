package com.alvin.ui;

import android.content.Context;
import android.widget.Toast;

/**
 * 统一的消息提示类
 */
public class SimpleToast {
    private static Toast toast;

    public synchronized static void show(Context context, String msg, int duration) {
        try {
            if (toast == null) {
                toast = Toast.makeText(context, msg, duration);
            }
            toast.cancel(); //先取消之前的通知
            toast.setText(msg);
            toast.setDuration(duration);
            toast.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
