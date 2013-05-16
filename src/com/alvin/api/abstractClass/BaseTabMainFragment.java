package com.alvin.api.abstractClass;

import com.alvin.slidMenu.MainSlidingActivity;

public abstract class BaseTabMainFragment extends BaseFragment {
	protected MainSlidingActivity mainActivity;
	
	protected BaseTabMainFragment(){
		mainActivity = (MainSlidingActivity) getActivity();
	}
	public abstract int getSlideMenuState();
}
