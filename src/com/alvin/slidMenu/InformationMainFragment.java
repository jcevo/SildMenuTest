package com.alvin.slidMenu;

import com.alvin.api.activity.common.CustomChannelActivity;
import com.alvin.api.model.Channel;
import com.alvin.api.utils.ChannelUtils;
import com.alvin.common.R;
import com.slidingmenu.lib.SlidingMenu.OnClosedListener;
import com.viewpagerindicator.TabPageIndicator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 资讯主页
 * 
 * @author Liyang
 * 
 */
public class InformationMainFragment extends TopBannerFragment {
	public static int SLIDE_MENU_STATE = MainSlidingActivity.LEFT_AVAILABLE; //记录资讯页slidingmenu状态
	private String TAG = "InformationFragment";
	public 	static ViewPager pager = null;
	private MainSlidingActivity mainActivity = null;
	private TabPageIndicator pageChangeindicator = null;
	private InforViewPagerChangerListener pageChangelistener = null;
	private InforViewPagerAdapter pageAdapter = null;
	private List<Channel> channels = null;
	private InformationChannelFragment fragment = null;
	private static ArrayList<String> haveLookedChannel = new ArrayList<String>();
	private int currentPos = 0;
	@SuppressLint("UseSparseArrays")
	public static HashMap<Integer, InformationChannelFragment> fragments = new HashMap<Integer, InformationChannelFragment>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mainActivity = (MainSlidingActivity) this.getActivity();
		pageAdapter = new InforViewPagerAdapter(getChildFragmentManager());
		channels = ChannelUtils.getNavAndEventChannel(mainActivity, 0);
		Log.v(TAG, "onCreate");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.v(TAG, "onCreateView");
		if (container == null){
            return null;
        }
		// 通过生成的Context创建一个LayoutInflater,给fragment设置style
		Context ctxWithTheme = new ContextThemeWrapper(mainActivity.getApplicationContext(), R.style.Theme_PageIndicatorDefaults);
		LayoutInflater localLayoutInflater = inflater.cloneInContext(ctxWithTheme);
		View layout = localLayoutInflater.inflate(R.layout.information_main, container,false);
		initView(layout);
		//初始化SlidingMenu状态
		MainSlidingActivity.changeSlidingMenuState(SLIDE_MENU_STATE);
		return layout;
	}
	/***
	 * 初始化view
	 * @param layout
	 */
	private void initView(View layout){
		initTopBannerLayout(layout);
		pager = (ViewPager) layout.findViewById(R.id.pager);
		pageChangeindicator = (TabPageIndicator) layout.findViewById(R.id.indicator);
		pageChangelistener = new InforViewPagerChangerListener();
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
		changeSlidingMenuState(currentPos);
	}
	@Override
	public void onPause() {
		super.onPause();
		Log.v(TAG, "InformationFragment onPause");
	}

	@Override
	public void onResume() {
		super.onResume();
		
		if(CustomChannelActivity.channelChanged){//自定义栏目页  返回时，更新
			channels = ChannelUtils.getNavAndEventChannel(mainActivity, 0);
			pageAdapter.notifyDataSetChanged();
			pageChangeindicator.notifyDataSetChanged();
			CustomChannelActivity.channelChanged = false;
			OnClosedListener closedListener = new OnClosedListener() {
				
				@Override
				public void onClosed() {
					changeSlidingMenuState(currentPos);
					mainActivity.getSlidingMenu().setOnClosedListener(null);
				}
			};
			mainActivity.getSlidingMenu().setOnClosedListener(closedListener);
		}
		Log.v(TAG, "InformationFragment onresume");
	}
	
	private void changeSlidingMenuState(int pos){
		int channelSize = channels.size();
		if(channelSize<=1){
			SLIDE_MENU_STATE = MainSlidingActivity.All_AVAILABLE;
		}else{
			if (pos == 0) {
				SLIDE_MENU_STATE = MainSlidingActivity.LEFT_AVAILABLE;
			} else if (pos == channelSize-1) {
				SLIDE_MENU_STATE = MainSlidingActivity.RIGHT_AVAILABLE;
			} else {
				SLIDE_MENU_STATE = MainSlidingActivity.ALL_UNAVAILABLE;
			}
		}
		MainSlidingActivity.changeSlidingMenuState(SLIDE_MENU_STATE);
	}
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}




	/***
	 * page改变
	 * @author poble
	 *
	 */
	class InforViewPagerChangerListener implements ViewPager.OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageSelected(int arg0) {
			currentPos = arg0;
			changeSlidingMenuState(currentPos);
			if(null!=fragments.get(arg0)&&!isLooked(channels.get(arg0).getChannelId()+"")){
				fragments.get(arg0).startLoad();
				putLooked(channels.get(arg0).getChannelId()+"");
			}
			
		}

	}

	/**
	 * 内部类，第二级嵌套选项卡适配器
	 */
	class InforViewPagerAdapter extends FragmentStatePagerAdapter {
		public InforViewPagerAdapter(FragmentManager fm) {
			super(fm);
		}

	
		
		@Override
		public CharSequence getPageTitle(int position) {
			return channels.get(position).getChannelName();
		}

		@Override
		public int getCount() {
			return channels.size();
		}
		
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			super.destroyItem(container, position, object);
		}
		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		@Override
		public Fragment getItem(int arg0) {
			fragment =new  InformationChannelFragment();
			Bundle bundle = new Bundle();  
		    bundle.putSerializable("channel", channels.get(arg0));
		    fragment.setArguments(bundle);
		    fragments.put(arg0, fragment);
			return fragment;
		}
	}
	
	@Override
	public int getSlideMenuState() {
		return SLIDE_MENU_STATE;
	}
	public static boolean isLooked(String channelId){
		for(String id:haveLookedChannel){
			if(id.equals(channelId)){
				return true;
			}
		}
		return false;
	}
	public static void putLooked(String channelId){
		if(!isLooked(channelId)){
			haveLookedChannel.add(channelId);
		}
	}
}
