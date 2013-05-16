package com.alvin.slidMenu;

import com.alvin.common.R;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
/**
 * 专栏主页面
 * @author Liyang
 *
 */
public class FeatureMainFragment extends TopBannerFragment{
	public static int SLIDE_MENU_STATE = MainSlidingActivity.LEFT_AVAILABLE; //记录资讯页slidingmenu状态
	private String TAG = "FeatureMainFragment";
	private MainSlidingActivity mainActivity = null;
    public FeatureMainFragment(){}
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainSlidingActivity) this.getActivity();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        
        if (container == null){
            return null;
        }
        
        LayoutInflater myInflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
        View layout = myInflater.inflate(R.layout.feature_main, container, false); 
        initView(layout);
        return layout;
    }
    /***
	 * 初始化view
	 * @param layout
	 */
	private void initView(View layout){
		initTopBannerLayout(layout);
		setTopBannerTittle(R.drawable.app_feature_name);
		setRightView(View.GONE);
	}
	
	@Override
	public int getSlideMenuState() {
		return SLIDE_MENU_STATE;
	}
}
