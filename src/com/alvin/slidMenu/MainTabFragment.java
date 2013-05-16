package com.alvin.slidMenu;

import com.alvin.api.abstractClass.BaseFragment;
import com.alvin.api.abstractClass.BaseTabMainFragment;
import com.alvin.common.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabWidget;

/**
 * 客户端主界面
 * 
 * @author Liyang
 * 
 */
@SuppressLint("ValidFragment")
public abstract class MainTabFragment extends BaseFragment {
	private final Class<?>[] tabClasses;
	private final int[] tabImgs;

	private final String[] tab_names;

	/**
	 * 提供点击每个标签跳转的Fragment类的class数组，传入后，实际标签位置与传进来的数组元素顺序一致
	 * 
	 * @return
	 */
	protected abstract Class<?>[] fillInTabClasses();

	/**
	 * 每个tab标签的图片资源，该资源应该要与{@link fillInTabClasses}方法提供的数组数量相同，且一一对应
	 * 
	 * @return
	 */
	protected abstract int[] fillInTabImgs();

	public MainTabFragment() {
		this.tabClasses = fillInTabClasses();
		this.tabImgs = fillInTabImgs();

		tab_names = initTagName();
	}

	/**
	 * 初始化每个tab的标签名字为其类名
	 * 
	 * @return
	 */
	private final String[] initTagName() {
		final int len = tabClasses.length;
		if (len == 0)
			return null;
		final String[] tabNames = new String[len];

		for (int i = 0; i < len; i++) {
			final Class<?> tabClass = tabClasses[i];
			tabNames[i] = tabClass.getSimpleName();
		}

		return tabNames;
	}

	// 每个标签所在的布局
	private ViewGroup[] viewGroups;

	private BaseTabMainFragment[] fragments;

	private TabHost tabHost;
	private TabWidget tw;
	private FragmentTransaction ft;
	private FragmentManager fm;

	private View layout;
	private LayoutInflater inflater;
	// activit引用
	private Activity mainActivity;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		layout = inflater.inflate(R.layout.activity_main, null, false);
		this.inflater = inflater;
		fm = getChildFragmentManager();
		mainActivity = this.getActivity();
		initTabView();
		tabHost.setup();

		// 设置初始选项卡
		tabHost.setCurrentTab(0);
		tabHost.setOnTabChangedListener(tabChangeListener);
		initTab();
		// 设置初始化界面
		tabHost.setCurrentTab(0);
		return layout;
	}

	// 初始化tab视图
	private void initTabView() {
		tabHost = (TabHost) layout.findViewById(android.R.id.tabhost);
		tw = (TabWidget) layout.findViewById(android.R.id.tabs);

		int len = tabClasses.length;
		viewGroups = new ViewGroup[len];
		initFragmentsArray(len);

		setEachTabBgRes(len);
	}

	// 设置每个标签的背景图片
	private void setEachTabBgRes(int len) {

		for (int i = 0; i < len; i++) {
			viewGroups[i] = (RelativeLayout) inflater.inflate(
					R.layout.tab_indicator, tw, false);
			ImageView inforTab = (ImageView) viewGroups[i]
					.findViewById(R.id.tab_icon);
			inforTab.setBackgroundResource(tabImgs[i]);
		}
	}

	private void initFragmentsArray(int len) {
		fragments = new BaseTabMainFragment[len];
		for (int i = 0; i < len; i++) {
			try {
				fragments[i] = (BaseTabMainFragment) tabClasses[i]
						.newInstance();
			} catch (java.lang.InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	// 初始化tab及内容
	private void initTab() {

		final int len = viewGroups.length;
		for (int i = 0; i < len; i++) {
			TabHost.TabSpec tSpecInfor = tabHost.newTabSpec(tab_names[i]);
			tSpecInfor.setIndicator(viewGroups[i]);
			tSpecInfor
					.setContent(new TabContent(mainActivity.getBaseContext()));
			tabHost.addTab(tSpecInfor);
		}
	}

	private void detachIfExists(Fragment fragment) {
		/** 如果存在Detaches掉 */
		if (fragment != null && !fragment.isHidden()) {
			ft.hide(fragment);
			fragment.onPause();
		}
	}

	/**
	 * Tab切换监听器
	 */
	private TabHost.OnTabChangeListener tabChangeListener = new TabHost.OnTabChangeListener() {
		@Override
		public void onTabChanged(String tabId) {
			ft = fm.beginTransaction();
			final int len = tabClasses.length;
			final String[] tabName = tab_names;
			for (int i = 0; i < len; i++) {
				fragments[i] = (BaseTabMainFragment) fm
						.findFragmentByTag(tabName[i]);
				detachIfExists(fragments[i]);
				if (tabId.equalsIgnoreCase(tabName[i])) {
					isTabAtIndex(i);
				}
			}

			ft.commit();
		}

	};

	// 初始化tab中某个位置的Fragment，恢复之前的状态，若已初始化则show出来
	private void isTabAtIndex(int index) {

		if (fragments[index] == null) {
			final String[] tabNames = tab_names;
			BaseTabMainFragment baseFragment = genBaseTabMainFragment(index);
			if (baseFragment != null) {
				ft.add(R.id.realtabcontent, baseFragment, tabNames[index]);
			}
		} else {
			ft.show(fragments[index]);
			recoverOrResetStateOfSlidingMenu(index);
		}

	}
	
	protected void recoverOrResetStateOfSlidingMenu(int index){
		// 恢复“专栏”下的slidingmenu状态
		MainSlidingActivity.changeSlidingMenuState(fragments[index]
				.getSlideMenuState());
	}

	private BaseTabMainFragment genBaseTabMainFragment(int index) {
		BaseTabMainFragment baseFragment = null;
		try {
			baseFragment = (BaseTabMainFragment) tabClasses[index]
					.newInstance();
		} catch (java.lang.InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		}
		;

		return baseFragment;
	}
	@Override
	public void onResume() {
		/*if(tabHost.getCurrentTab()==0){
			ft = fm.beginTransaction();
			BaseTabMainFragment baseFragment = genBaseTabMainFragment(0);
			if (baseFragment != null) {
				ft.add(R.id.realtabcontent, baseFragment, tab_names[0]);
			}
			ft.commit();
			((MainSlidingActivity) mainActivity).resumeSlidingMenuState();
			((SlidingFragmentActivity) mainActivity).showContent();
		}*/
		super.onResume();
	}
}
