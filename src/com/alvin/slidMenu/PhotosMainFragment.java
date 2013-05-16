package com.alvin.slidMenu;

import com.alvin.api.config.Env;
import com.alvin.common.R;
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
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ProgressBar;

/**
 * 图集主页
 * 
 * @author Liyang
 * 
 */
public class PhotosMainFragment extends TopBannerFragment {
	public static int SLIDE_MENU_STATE = MainSlidingActivity.LEFT_AVAILABLE; // 记录资讯页slidingmenu状态
	private String TAG = "PhotosMainFragment";
	private String[] CONTENT = null;
	public ViewPager pager = null;
	private TabPageIndicator pageChangeindicator = null;
	private PhotosViewPagerChangerListener pageChangelistener = null;
	private PhotosViewPagerAdapter pageAdapter = null;
	private PhotosGridViewFragment fragment = null;    //显示“香车美女”或“汽车图片”的fragment
	private ProgressBar progressBar = null;
	private String[] URL = null;
	public static int GRIDVIEW_ITEM_HEIGHT = 0;
	private MainSlidingActivity mainActivity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		pageAdapter = new PhotosViewPagerAdapter(getChildFragmentManager());
		mainActivity = (MainSlidingActivity) getActivity();
		CONTENT = mainActivity.getResources().getStringArray(R.array.photos_viewpager_title);
		URL = mainActivity.getResources().getStringArray(R.array.photos_url);
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
		View layout = localLayoutInflater.inflate(R.layout.photos_main,
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
		setRightView(View.GONE);
		pager = (ViewPager) layout.findViewById(R.id.photos_pager);
		pageChangeindicator = (TabPageIndicator) layout
				.findViewById(R.id.photos_indicator);
		progressBar = (ProgressBar) layout.findViewById(R.id.gridview_image_load_progress);
		pageChangelistener = new PhotosViewPagerChangerListener();
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

	/***
	 * page改变
	 * 
	 * @author poble
	 * 
	 */
	class PhotosViewPagerChangerListener implements
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
				MainSlidingActivity.changeSlidingMenuState(SLIDE_MENU_STATE);
			}
		}

	}

	/**
	 * 内部类，第二级嵌套选项卡适配器
	 */
	class PhotosViewPagerAdapter extends FragmentPagerAdapter {
		public PhotosViewPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			fragment = new PhotosGridViewFragment();
			Bundle bundle = new Bundle();
			bundle.putString("url", URL[position]);
			fragment.setArguments(bundle);
			fragment.setProgressBar(progressBar);
			return fragment;
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

	/**
	 * 在onStart()中计算出gridview的item高度（每页显示两项）
	 */
	@Override
	public void onStart() {
		super.onStart();
		int pagerIndicatorHeight = getViewHeight(pageChangeindicator);
		// gridview的高度是屏幕高度减去状态栏、标题栏、Indicator、底部tab和两个gridview间距(10px)的1.5倍（1.5倍是为了
		// 让图集初始界面中gridview的第二个item和底部tab之间的间距为item间距的一半）再除以2
		GRIDVIEW_ITEM_HEIGHT = (int)((Env.screenHeight - Env.titleBarHeight
				- Env.statusBarHeight - pagerIndicatorHeight - Env.tabHeight
				- 30) / 2.5);
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
	
	@Override
	public int getSlideMenuState() {
		return SLIDE_MENU_STATE;
	}
	
}
