package com.alvin.slidMenu;

import cn.com.pcgroup.common.android.utils.DisplayUtils;

import com.alvin.api.abstractClass.BaseFragment;
import com.alvin.api.config.Env;
import com.alvin.common.R;
import com.alvin.common.utils.AnimationUtils;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;

import java.util.ArrayList;


/**
 * 品牌选车主页面
 * @author xjzhao
 *
 */
public class CarBrandFragment extends BaseFragment {

	// 模拟数据
	private ArrayList<String> indexList = new ArrayList<String>();
	private ArrayList<ArrayList<String>> name = new ArrayList<ArrayList<String>>();
	private ArrayList<String> nameA = new ArrayList<String>();
	private ArrayList<String> nameB = new ArrayList<String>();
	private ArrayList<String> nameC = new ArrayList<String>();
	private ArrayList<String> nameD = new ArrayList<String>();
	private ArrayList<String> nameE = new ArrayList<String>();

//	private AlphabetListView alphabetListView;
//	private PinnedHeaderListView pinnedHeaderListView;
//	private CarBrandSectionListAdapter sectionListAdapter;
//	private CarBrandViewPager viewPager;
//
//	// detailFragment的布局参数
//	public static int datilLayoutW;
//	
//	//处理detailFragment的出现与消失
//	private int currentAnim = -1;
//	private int lastAnim = -2;
//	
//	public static FrameLayout detailFragmentLayout;
//
//	private CarBrandDetailFragment detailFragment;
//
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		indexList.add("A");
//		nameA.add("奥迪");
//		nameA.add("奥迪奥迪");
//		nameA.add("奥迪奥迪奥迪");
//		nameA.add("奥迪奥迪奥迪奥迪");
//		name.add(nameA);
//
//		indexList.add("B");
//		nameB.add("宝马");
//		nameB.add("宝马宝马");
//		nameB.add("宝马宝马宝马");
//		nameB.add("宝马宝马宝马宝马");
//		name.add(nameB);
//
//		indexList.add("C");
//		nameC.add("C");
//		nameC.add("CC");
//		nameC.add("CCC");
//		nameC.add("CCCC");
//		name.add(nameC);
//
//		indexList.add("D");
//		nameD.add("大众");
//		nameD.add("大众大众");
//		nameD.add("大众大众大众");
//		nameD.add("大众大众大众大众");
//		name.add(nameD);
//
//		indexList.add("E");
//		nameE.add("E");
//		nameE.add("EE");
//		nameE.add("EEE");
//		nameE.add("EEEE");
//		name.add(nameE);
//	}
//
//	@Override
//	public View onCreateView(final LayoutInflater inflater,
//			ViewGroup container, Bundle savedInstanceState) {
//
//		View view = inflater.inflate(R.layout.car_brand_fragment, null);
//		initView(view);
//		sectionListAdapter = new CarBrandSectionListAdapter(inflater, getData());
//		alphabetListView.setAdapter(pinnedHeaderListView, sectionListAdapter,
//				listener, indexList);
//		pinnedHeaderListView.setOnScrollListener(sectionListAdapter);
//		pinnedHeaderListView.setPinnedHeaderView(inflater.inflate(
//				R.layout.car_pinnedheaderlistview_header_listview,
//				pinnedHeaderListView, false));
//		return view;
//	}
//
//	
//
//	@Override
//	public void onStart() {
//		super.onStart();
//		replaceDetailFragment();
//		datilLayoutW = Env.screenWidth
//				- DisplayUtils.convertDIP2PX(getActivity(), 20)
//				- getActivity().getResources().getDrawable(R.drawable.app_icon)
//						.getIntrinsicWidth();
//	}
//
//	private void initView(View view) {
//		alphabetListView = (AlphabetListView) view
//				.findViewById(R.id.car_brand_fragment_alphabetlistview);
//		pinnedHeaderListView = (PinnedHeaderListView) view
//				.findViewById(R.id.car_brand_fragment_pinnedheaderlistview);
//		setItemClick();
//	}
//
//
//	/**
//	 * 为当前页面的listview绑定点击事件。
//	 * 效果：1、detailFragment不可见，点击任意item时detailFragment动画滑出；
//	 *       2、detailFragment可见，点击不同item时只更新detailFragment中的listview的内容；点击相同item时，detailFragment
//	 * 			动画滑出
//	 */
//	private void setItemClick() {
//		pinnedHeaderListView.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				currentAnim = position;
//				if (detailFragmentLayout.getVisibility() == View.GONE) {
//					detailFragmentAnim(View.VISIBLE, R.anim.right_fade_in);
//					//当detailListView出现时禁止所有的左右滑动
//					AutoLibraryMainFragment.SLIDE_MENU_STATE = MainSlidingActivity.ALL_UNAVAILABLE;
//					MainSlidingActivity.changeSlidingMenuState(AutoLibraryMainFragment.SLIDE_MENU_STATE);
//					viewPager.setViewPagerSlidEnabled(false);
//					CarService.CAR_DETAIL_FRAGMENT_VISABLE = true;
//				} else {
//					if (lastAnim == currentAnim) {
//						detailFragmentAnim(View.GONE, R.anim.right_fade_out);
//						//当detailListView不可见时允许所有的左右滑动
//						AutoLibraryMainFragment.SLIDE_MENU_STATE = MainSlidingActivity.LEFT_AVAILABLE;
//						MainSlidingActivity.changeSlidingMenuState(AutoLibraryMainFragment.SLIDE_MENU_STATE);
//						viewPager.setViewPagerSlidEnabled(true);
//						CarService.CAR_DETAIL_FRAGMENT_VISABLE = false;
//						
//					} else {
//						/**
//						 * 
//						 * 只要动画执行一次，然后再点击其他item时只更新listview的adapter
//						 * 
//						 */
//					}
//				}
//			}
//		});
//
//	}
//
//	/**
//	 * 为detailFragment做动画处理
//	 * @param visable
//	 * @param animID
//	 */
//	public void detailFragmentAnim(int visable, int animID) {
//		detailFragmentLayout.setVisibility(visable);
//		Animation anim = android.view.animation.AnimationUtils.loadAnimation(
//				getActivity(), animID);
//		anim.setAnimationListener(new AnimationListener() {
//
//			@Override
//			public void onAnimationStart(Animation animation) {
//				// TODO Auto-generated method stub
//
//			}
//
//			@Override
//			public void onAnimationRepeat(Animation animation) {
//				// TODO Auto-generated method stub
//
//			}
//
//			@Override
//			public void onAnimationEnd(Animation animation) {
//				// TODO Auto-generated method stub
//				lastAnim = currentAnim;
//			}
//		});
//		detailFragmentLayout.startAnimation(anim);
//	}
//
//	/**
//	 * 设置DetailFragment浮在当前fragment之上
//	 */
//	private void replaceDetailFragment() {
//		detailFragment = new CarBrandDetailFragment();
//		FragmentTransaction fgTransaction = getFragmentManager()
//				.beginTransaction();
//		fgTransaction.replace(R.id.car_brand_fragment, detailFragment);
//		fgTransaction.commit();
//		detailFragment.setViewPager(viewPager);
//		AnimationUtils.AinmParams params = new AnimationUtils.AinmParams();
//		params.setView(detailFragmentLayout).setFromX(0).setToX(1f).setTime(1);
//		AnimationUtils.setTranslateAnimation(params);
//	}
//
//	/**
//	 * 得到CarBrandDetailFragment中的detailFragmentLayout
//	 * @param layout
//	 */
//	public static void setDetailFragmentLayout(FrameLayout layout) {
//		detailFragmentLayout = layout;
//	}
//
//	/**
//	 * 得到模拟填充的数据
//	 * 
//	 * @return
//	 */
//	private ArrayList<CarSerialModel> getData() {
//		int len1 = indexList.size();
//		int len2 = nameA.size();
//		ArrayList<CarSerialModel> data = new ArrayList<CarSerialModel>();
//		for (int i = 0; i < len1; i++) {
//			for (int j = 0; j < len2; j++) {
//				CarSerialModel car = new CarSerialModel();
//				data.add(car.setSection(indexList.get(i)).setItem(
//						name.get(i).get(j)));
//			}
//		}
//		return data;
//	}
//
//	AlphabetPositionListener listener = new AlphabetPositionListener() {
//
//		@Override
//		public int getPosition(String letter) {
//			return sectionListAdapter.getPositionbySection(letter);
//		}
//	};
//
//	/**
//	 * 得到车型库模块的ViewPager
//	 * @param viewPager
//	 */
	public void setViewPager(CarBrandViewPager viewPager) {
//		this.viewPager = viewPager;
	}
//
//	//锁频后重新开启时将AutoLibraryMainFragment.SLIDE_MENU_STATE重置为锁频前的状态（不能滑动）
//	@Override
//	public void onResume() {
//		super.onResume();
//		if (CarService.CAR_DETAIL_FRAGMENT_VISABLE) {
//			AutoLibraryMainFragment.SLIDE_MENU_STATE = MainSlidingActivity.ALL_UNAVAILABLE;
//			MainSlidingActivity
//					.changeSlidingMenuState(AutoLibraryMainFragment.SLIDE_MENU_STATE);
//		}
//		
//	}
	
}
