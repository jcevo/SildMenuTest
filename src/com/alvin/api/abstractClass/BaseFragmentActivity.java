package com.alvin.api.abstractClass;

import cn.com.pcgroup.android.framework.http.client.AsyncHttpClient;

import com.alvin.common.R;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import java.util.List;

/**
 * 基类Activity
 */
public class BaseFragmentActivity extends FragmentActivity {
    
    public static boolean isHomeBack = false;
    //app是否被置于后台，用于全屏广告的显示条件
    private static boolean appIsBack = false;
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
    protected void onStop() {
        super.onStop();
        //Debug.stopMethodTracing();
        ActivityManager manager=(ActivityManager)BaseFragmentActivity.this.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);
        if(null!=runningTaskInfos && !runningTaskInfos.isEmpty()){
            if(!runningTaskInfos.get(0).topActivity.getPackageName().equals(this.getPackageName())){
                isHomeBack = true;
                appIsBack = true;
            }
        }
    }
    
    protected void onRestart() {
        super.onRestart();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //取消当前页面要执行的任务
        AsyncHttpClient.getHttpClientInstance().cancelRequests(getApplicationContext(), true);
    }
    
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(0, R.anim.right_fade_out);
    }
}