package com.alvin.slidMenu;

import com.alvin.api.abstractClass.BaseTabMainFragment;
import com.alvin.common.R;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
/***
 * 顶部banner布局初始化（将五个栏目顶部的banner提取公用，在这个类初始化）
 * @author poble
 *
 */
public abstract class TopBannerFragment extends BaseTabMainFragment {
	
	//左侧  用户
	private FrameLayout leftLayout = null;
	private FrameLayout userLayout = null;
	private ImageView userLogo = null;
	
	//中间   标题
	private ImageView tittleView = null;
	
	//右侧  功能按钮
	private FrameLayout rightLayout = null;
	private ImageView settingLogo = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	/***
	 * 初始化顶部bannerLayout
	 * @param layout
	 */
	public void initTopBannerLayout(View layout){
		
		//左侧
		leftLayout = (FrameLayout) layout.findViewById(R.id.top_banner_left_layout);
		userLayout = (FrameLayout) layout.findViewById(R.id.top_banner_user_layout);
		userLogo = (ImageView) layout.findViewById(R.id.user_logo);
		
		//标题
		tittleView = (ImageView) layout.findViewById(R.id.top_banner_tittle);
		
		//右侧
		rightLayout = (FrameLayout) layout.findViewById(R.id.top_banner_right_layout);
		settingLogo = (ImageView) layout.findViewById(R.id.setting_logo);
		
		initUser();
		setRightView(0,-1,null);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	/**
	 * 设置顶部 标题
	 * @param tittle
	 */
	public void setTopBannerTittle(int resId){
		tittleView.setImageResource(resId);
	}
	private void setRightView(int resId,int visible,OnClickListener clickListener){
		if(resId!=0){
			settingLogo.setImageResource(resId);
		}
		if(-1!=visible){
			settingLogo.setVisibility(visible);
			rightLayout.setVisibility(visible);
		}
		if(clickListener!=null){
			settingLogo.setOnClickListener(clickListener);
			rightLayout.setOnClickListener(clickListener);
		}else{//默认事件
			settingLogo.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					((SlidingFragmentActivity) getActivity()).getSlidingMenu().setMode(SlidingMenu.RIGHT);
					((SlidingFragmentActivity) getActivity()).showSecondaryMenu();
				}
			});
			rightLayout.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					((SlidingFragmentActivity) getActivity()).getSlidingMenu().setMode(SlidingMenu.RIGHT);
					((SlidingFragmentActivity) getActivity()).showSecondaryMenu();
					
				}
			});
		}
	}
	/**
	 * 设置顶部右侧按钮的资源图片和点击事件监听器
	 * @param resId
	 * @param clickListener
	 */
	public void setRightView(int resId,OnClickListener clickListener){
		setRightView(resId, -1, clickListener);
	}
	/**
	 * 设置右侧按钮是否可见
	 * @param visible
	 */
	public void setRightView(int visible){
		setRightView(-1, visible, null);
	}
	private void initUser(){
		userLogo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				((SlidingFragmentActivity) getActivity()).getSlidingMenu().setMode(SlidingMenu.LEFT);
				((SlidingFragmentActivity) getActivity()).showMenu();
			}
		});
	}
	public ImageView getUserLogo() {
		return userLogo;
	}

	public ImageView getSettingLogo() {
		
		return settingLogo;
	}

	public ImageView getTittleView() {
		return tittleView;
	}

	@Override
	public void onPause() {
		super.onPause();
	}
	@Override
	public void onResume() {
		super.onResume();
	}
	
}
