package com.alvin.slidMenu;
import com.alvin.api.abstractClass.BaseFragmentActivity;
import com.alvin.api.utils.ChannelUtils;
import com.alvin.common.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * 启动界面
 */
public class LauncherActivity extends BaseFragmentActivity {
    private final static String TAG = "LauncherActivity";
    private static int STAY = 2;     //界面停留时间，单位：秒
    private static Activity activity;
    
    //launcher界面元素
    
    public static boolean FMTtag = false; //富媒体广告标记使用（正常启动时，标记为true，按照广告正常情况显示，
                                          //应用置于后台被删除后，再次启动，标记为false，富媒体广告按特殊情况处理（在富媒体广告组件中处理））

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        long startTime = System.currentTimeMillis();
        System.out.println("laucher startTime :"+startTime);
        
        this.setContentView(R.layout.launcher);
        activity = this;
        new PreloadThread(this, handler).start();
    }
    
    
    private static final Handler handler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
        	forwordActivity(activity,MainSlidingActivity.class);
        }
    };
    
    private static class PreloadThread extends Thread{
    	private Handler handler;
        private Context context;

        public PreloadThread(){}
        
        public PreloadThread(Context context,Handler handler) {
            this.handler = handler;
            this.context = context;
        }
        
        public void run(){
        	try {
        		ChannelUtils.initClinetChannel(activity);
				Thread.sleep(STAY*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        	 handler.sendMessage(handler.obtainMessage());
        }
    }
    
    
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish(); 
        System.exit(0); 
    }
    
    private static void forwordActivity(Context context , Class<?> cls){
        Intent intent = new Intent(context, cls);
        context.startActivity(intent);
        activity.finish();
    }
}
