package com.alvin.slidMenu;

import com.alvin.api.abstractClass.BaseTabMainFragment;
import com.alvin.common.R;
import com.slidingmenu.lib.SlidingMenu;
import com.viewpagerindicator.TabPageIndicator;

import android.content.Context;
import android.content.Intent;
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
import android.widget.ImageView;


/**
 * liuyx
 * 
 * @author user
 * 
 */
public class AutoBBSBbsMainFragment extends BaseTabMainFragment {
	
	public static int SLIDE_MENU_STATE = MainSlidingActivity.LEFT_AVAILABLE; //记录资讯页slidingmenu状态
	private static final String TAG = "AutoBbsMainFragment";
	private static final String[] CONTENT = new String[] { "最近浏览", "车系","地区","主题"};
	public 	ViewPager pager;
	private MainSlidingActivity mainActivity;
	private TabPageIndicator pageChangeindicator;
	private BBSViewPagerChangerListener pageChangelistener;
	private BBSViewPagerAdapter pageAdapter;
	private ImageView searchImg;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mainActivity = (MainSlidingActivity) this.getActivity();
		pageAdapter = new BBSViewPagerAdapter(getChildFragmentManager());
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		Log.v(TAG, "onCreateView");
		if (container == null){
            return null;
        }
		// 通过生成的Context创建一个LayoutInflater,给fragment设置style
		Context ctxWithTheme = new ContextThemeWrapper(mainActivity.getApplicationContext(), R.style.Theme_AutoBBSBbsPageIndicatorDefaults);
		LayoutInflater localLayoutInflater = inflater.cloneInContext(ctxWithTheme);
		View layout = localLayoutInflater.inflate(R.layout.auto_bbs_main, container,false);
		initView(layout);
		//初始化SlidingMenu状态
		mainActivity.getSlidingMenu().setMode(SlidingMenu.LEFT); // 第一个。SlidingMenu仅有左边有效
		mainActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		return layout;
	}	
	/***
	 * 初始化view
	 * @param layout
	 */
	private void initView(View layout){
		pager = (ViewPager) layout.findViewById(R.id.bbs_pager);
		pageChangeindicator = (TabPageIndicator) layout.findViewById(R.id.bbs_indicator);
		pageChangelistener = new BBSViewPagerChangerListener();
		pager.setAdapter(pageAdapter);
		pageChangeindicator.setViewPager(pager);
		pageChangeindicator.setOnPageChangeListener(pageChangelistener);
		//导航页的touch事件自己处理
		pageChangeindicator.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				((ViewGroup) mainActivity.getSlidingMenu().getContent()).requestDisallowInterceptTouchEvent(true);
				return false;
			}
		});
		
		searchImg = (ImageView) layout.findViewById(R.id.search);
		searchImg.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				Intent intent = new Intent(mainActivity,AutoBbsSearchActivity.class);
//				startActivity(intent);
			}
		});
	}
	/***
	 * page改变
	 * @author poble
	 *
	 */
	class BBSViewPagerChangerListener implements ViewPager.OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageSelected(int arg0) {
			//int lastPostion = CONTENT.length - 1;
			if (arg0 == 0) {
				SLIDE_MENU_STATE = MainSlidingActivity.LEFT_AVAILABLE;
				MainSlidingActivity.changeSlidingMenuState(SLIDE_MENU_STATE);
			}else {
				SLIDE_MENU_STATE = MainSlidingActivity.ALL_UNAVAILABLE;
				MainSlidingActivity.changeSlidingMenuState(SLIDE_MENU_STATE);
			}
			
		}

	}

	/**
	 * 内部类，第二级嵌套选项卡适配器
	 */
	class BBSViewPagerAdapter extends FragmentPagerAdapter {
		public BBSViewPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Fragment frag = null;
			try {
				frag = (Fragment) FRAG_CLASSES[position].newInstance();
				Bundle bundle = new Bundle();  
	            bundle.putString("key", CONTENT[position]); 
	            frag.setArguments(bundle);
			} catch (java.lang.InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return frag;
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
	
	private static final Class<?>[] FRAG_CLASSES = {
//		AutoBbsRecentBrowserFragment.class,AutoBbsCarSerialsFragment.class,AutoBbsAreaFragment.class,AutoBbsThemeFragment.class
	};
	
	@Override
	public int getSlideMenuState() {
		return SLIDE_MENU_STATE;
	}
	
	
}
