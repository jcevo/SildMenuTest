package com.alvin.common.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;


/**
 * 网络可用的判断和配置
 */
public class NetworkUtils {
    
    public final static int STATE_NONE = 0;
    public final static int STATE_WIFI = 1;
    public final static int STATE_MOBILE = 2;
    
    /**
     * 获取当前网络状态(是否可用)
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context){
        boolean netWorkStatus = false;
        
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        if(connManager.getActiveNetworkInfo()!=null){
            netWorkStatus = connManager.getActiveNetworkInfo().isAvailable();
        }
        
        return netWorkStatus;
    }
    
    /**
     * 获取3G或者WIFI网络
     * @param context
     * @return
     */
    public static int getNetworkStates(Context context){
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        
        //Wifi网络判断
        State state = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        if(state == State.CONNECTED||state == State.CONNECTING){
            return STATE_WIFI;
        }
        
        //3G网络判断
        state = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
        if(state == State.CONNECTED||state == State.CONNECTING){
            return STATE_MOBILE;
        }

        return STATE_NONE;
    }
}
