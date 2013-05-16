package com.alvin.slidMenu;

import com.alvin.common.R;

public class PcautoMainTabFragment extends MainTabFragment {

	
	private static final int[] TAB_IMGS = {
    	R.drawable.app_tab_information,
    	R.drawable.app_tab_bbs,
    	R.drawable.app_tab_lib,
    	R.drawable.app_tab_photos,
    	R.drawable.app_tab_feature,
    };
	
	private static final Class<?>[] TAB_CLASSES = {
    	// -------资讯客户端---------------
    	InformationMainFragment.class,
    	BBSMainFragment.class,
    	AutoLibraryMainFragment.class,
    	PhotosMainFragment.class,
    	FeatureMainFragment.class,
    	
    	//----------独立论坛-----------------
    };

	@Override
	protected Class<?>[] fillInTabClasses() {
		return TAB_CLASSES;
	}

	@Override
	protected int[] fillInTabImgs() {
		return TAB_IMGS;
	}

}
