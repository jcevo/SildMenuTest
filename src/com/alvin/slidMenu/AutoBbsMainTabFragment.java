package com.alvin.slidMenu;

import com.alvin.common.R;
import com.slidingmenu.lib.SlidingMenu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AutoBbsMainTabFragment extends MainTabFragment{

	private static final int[] TAB_IMGS = {
    	R.drawable.app_tab_information,
    	R.drawable.app_tab_bbs,
    	R.drawable.app_tab_lib,
    	R.drawable.app_tab_photos,
    	R.drawable.app_tab_feature,
    };
	
	private static final Class<?>[] TAB_CLASSES = {
		//----------独立论坛-----------------
//    	AutoBBSHomeMainFragment.class,
//    	AutoBBSBbsMainFragment.class,
//    	AutoBBSChoiceMainFragment.class,
//    	AutoBBSMineMainFragment.class,
//    	AutoBbsSettingFragment.class,
    };

	@Override
	protected Class<?>[] fillInTabClasses() {
		return TAB_CLASSES;
	}

	@Override
	protected int[] fillInTabImgs() {
		return TAB_IMGS;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// 将个人中心和设置页面禁用掉
		setTouchModeNone();
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	/**
	 * 重新回到页面时是恢复状态还是重置状态
	 */
	@Override
	protected void recoverOrResetStateOfSlidingMenu(int index) {
		setTouchModeNone();
	}
	
	/**
	 * 禁用掉左右滑动时出现个人中心页面
	 */
	private void setTouchModeNone(){
		((MainSlidingActivity)getActivity()).getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
	}

}
