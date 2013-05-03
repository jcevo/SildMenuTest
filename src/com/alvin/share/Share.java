package com.alvin.share;

import com.alvin.api.model.Account;
import com.alvin.common.R;
import com.alvin.db.UserDBHelper;
import com.alvin.oauth.SinaOAuth;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

public class Share {
    private static AlertDialog shareDialog = null;
    private static String[] shareList = null;
    private static void initData(final Context context) {
        if (shareList == null) {
            shareList = context.getResources().getStringArray(R.array.share_list);
            for (int i=0; i<shareList.length; i++) {
                shareList[i] = "分享到" + shareList[i];
            }
        }
    }

    /**
     * 分享
     * @param context
     * @param intent {包含：share_content、[photo_file_path/web_photo_url]}
     */
    public static void share(final Activity context, final Intent intent) {
        initData(context);
        shareDialog = new AlertDialog.Builder(context)
            .setTitle("分享")
            .setItems(shareList, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int position) {
                    if (position == 0) {//新浪微博
                        intent.putExtra("auth_type", Account.AUTH_TYPE_SINA);
                        gotoShare(context, intent);
                    }
                }
            }).create();
//        }
        shareDialog.show();
    }
    
    /**
     * 跳转到分享界面
     * @param context
     * @param intent {包含：auth_type、share_content、[photo_file_path/web_photo_url]}
     */
    public static void gotoShare(final Activity context, final Intent intent) {
        String[] tokenSecret = UserDBHelper.getTokenAndSecret(intent.getStringExtra("auth_type"));
        if (tokenSecret != null) {
            intent.putExtra("token", tokenSecret[0]);
            intent.putExtra("secret", tokenSecret[1]);
            intent.setClass(context, ShareActivity.class);
        } else {
            if (Account.AUTH_TYPE_SINA.equals(intent.getStringExtra("auth_type"))) {
                intent.setClass(context, SinaOAuth.class);
            }
            intent.putExtra("goto_share", true);
        }
        context.startActivity(intent);
    }
}
