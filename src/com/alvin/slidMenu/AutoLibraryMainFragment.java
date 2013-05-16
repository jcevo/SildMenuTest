package com.alvin.slidMenu;

import com.alvin.common.R;
import com.alvin.slidMenu.MainSlidingActivity.WindowFocusChangedListener;
import com.slidingmenu.lib.SlidingMenu;
import com.viewpagerindicator.TabPageIndicator;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;

/**
 * 车型库主页
 * 
 * @author Liyang
 * 
 */
public class AutoLibraryMainFragment extends TopBannerFragment {
	
	public static int SLIDE_MENU_STATE = MainSlidingActivity.LEFT_AVAILABLE; // 记录资讯页slidingmenu状态
	private String TAG = "AutoLibraryMainFragment";
	private String[] CONTENT = null;
	public CarBrandViewPager pager = null;
	private MainSlidingActivity mainActivity = null;
	private TabPageIndicator pageChangeindicator = null;
	private AutoLibraryViewPagerChangerListener pageChangelistener = null;
	private AutoLibraryViewPagerAdapter pageAdapter = null;

	public static int pagerIndicatorHeight = 0;
	private WindowFocusChangedListener listener;
	
	public AutoLibraryMainFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mainActivity = (MainSlidingActivity) this.getActivity();
		pageAdapter = new AutoLibraryViewPagerAdapter(getChildFragmentManager());
		CONTENT = mainActivity.getResources().getStringArray(
				R.array.auto_library_viewpager_title);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		Log.v(TAG, "onCreateView");
		if (container == null) {
			return null;
		}
		// 通过生成的Context创建一个LayoutInflater,给fragment设置style
		Context ctxWithTheme = new ContextThemeWrapper(
				mainActivity.getApplicationContext(),
				R.style.Theme_PageIndicatorDefaults);
		LayoutInflater localLayoutInflater = inflater
				.cloneInContext(ctxWithTheme);
		View layout = localLayoutInflater.inflate(R.layout.auto_library_main,
				container, false);
		initView(layout);
		// 初始化SlidingMenu状态
		mainActivity.getSlidingMenu().setMode(SlidingMenu.LEFT); // 第一个。SlidingMenu仅有左边有效
		mainActivity.getSlidingMenu().setTouchModeAbove(
				SlidingMenu.TOUCHMODE_FULLSCREEN);
		return layout;
	}

	/***
	 * 初始化view
	 * 
	 * @param layout
	 */
	private void initView(View layout) {
		initTopBannerLayout(layout);
		setTopBannerTittle(R.drawable.app_photos_name);
		// setTopBannerTittle("车型库");
		setRightView(R.drawable.app_search_logo, new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});
		pager = (CarBrandViewPager) layout.findViewById(R.id.auto_library_pager);
		pager.setOffscreenPageLimit(2);//设置当前页面左右两页的内容都被缓存（其实这里只有三页）
		pageChangeindicator = (TabPageIndicator) layout
				.findViewById(R.id.auto_library_indicator);
		pageChangelistener = new AutoLibraryViewPagerChangerListener();
		pager.setAdapter(pageAdapter);
		pageChangeindicator.setViewPager(pager);
		pageChangeindicator.setOnPageChangeListener(pageChangelistener);
		// 导航页的touch事件自己处理
		pageChangeindicator.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
					((ViewGroup) mainActivity.getSlidingMenu().getContent())
							.requestDisallowInterceptTouchEvent(true);
					return false;
			}
		});
	}
	
	@Override
	public void onStart() {
		super.onStart();
		pagerIndicatorHeight = getViewHeight(pageChangeindicator);
	}

	/**
	 * 得到view的高度/宽度 这个可以放在一个utils中
	 * 
	 * @param view
	 * @return
	 */
	public int getViewHeight(View view) {
		int w = View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED);
		int h = View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED);
		view.measure(w, h);
		return view.getMeasuredHeight();
		// return view.getMeasuredWidth();
	}


	/***
	 * page改变
	 * 
	 * @author poble
	 * 
	 */
	class AutoLibraryViewPagerChangerListener implements
			ViewPager.OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageSelected(int arg0) {
			// int lastPostion = CONTENT.length - 1;
			if (arg0 == 0) {
				SLIDE_MENU_STATE = MainSlidingActivity.LEFT_AVAILABLE;
				MainSlidingActivity.changeSlidingMenuState(SLIDE_MENU_STATE);
			} else {
				SLIDE_MENU_STATE = MainSlidingActivity.ALL_UNAVAILABLE;
				System.out.println(SLIDE_MENU_STATE);
				MainSlidingActivity.changeSlidingMenuState(SLIDE_MENU_STATE);
			}
		}

	}

	
	/**
	 * 内部类，第二级嵌套选项卡适配器
	 */
	class AutoLibraryViewPagerAdapter extends FragmentPagerAdapter {

		public AutoLibraryViewPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			switch (position){
			
			case CarService.CAR_BRAND_FRAGMENT://品牌选车
				CarBrandFragment fragmentBrand = new CarBrandFragment();
				fragmentBrand.setViewPager(pager);
				return fragmentBrand;
			case CarService.CAR_PRICE_FRAGMENT://价格选车
				CarPriceFragment fragmentPrice = new CarPriceFragment();
				return fragmentPrice;
			case CarService.CAR_CONFITION_FRAGMENT://条件选车
				CarConditionFragment fragmentCondition = new CarConditionFragment();
				return fragmentCondition;
			}
			return null;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return CONTENT[position % CONTENT.length];
		}

		@Override
		public int getCount() {
			return CONTENT.length;
		}
	}

	@Override
	public int getSlideMenuState() {
		return SLIDE_MENU_STATE;
	}

	
	
}
