package com.alvin.slidMenu;

import com.alvin.api.abstractClass.BaseFragment;
import com.alvin.common.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * 价格选车主页面
 * 
 * @author xjzhao
 * 
 */
public class CarPriceFragment extends BaseFragment {

	private ListView listView;
//	private AlphabetListView alphabetListView;
//	private PinnedHeaderListView pinnedHeaderListView;
//	private CarPriceSectionListAdapter sectionListAdapter;
//
//	// 模拟数据
//	private ArrayList<String> indexList = new ArrayList<String>();
//	private ArrayList<String> header = new ArrayList<String>();
//	private ArrayList<ArrayList<String>> name = new ArrayList<ArrayList<String>>();
//	private ArrayList<String> nameA = new ArrayList<String>();
//	private ArrayList<String> nameB = new ArrayList<String>();
//	private ArrayList<String> nameC = new ArrayList<String>();
//	private ArrayList<String> nameD = new ArrayList<String>();
//	private ArrayList<String> nameE = new ArrayList<String>();
//
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		indexList.add("A");
//		indexList.add("B");
//		indexList.add("C");
//		indexList.add("D");
//		indexList.add("E");
//		
//		header.add("A 奥迪");
//		header.add("B BQQ");
//		header.add("C CC");
//		header.add("D 大众"); 
//		header.add("E ES");
//
//		nameA.add("奥迪Q4");
//		nameA.add("奥迪Q5");
//		nameA.add("奥迪Q6");
//		nameA.add("奥迪Q7-最爱");
//
//		nameB.add("宝马X4");
//		nameB.add("宝马X5");
//		nameB.add("宝马X6");
//		nameB.add("宝马X7");
//
//		nameC.add("C-1");
//		nameC.add("C-2");
//		nameC.add("C-3");
//		nameC.add("C-4");
//
//		nameD.add("大众SB1");
//		nameD.add("大众SB2");
//		nameD.add("大众SB3");
//		nameD.add("大众SB4");
//
//		nameE.add("E-1");
//		nameE.add("E-2");
//		nameE.add("E-3");
//		nameE.add("E-4");
//
//		name.add(nameA);
//		name.add(nameC);
//		name.add(nameD);
//		name.add(nameB);
//		name.add(nameE);
//
//	}
//
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {
//		View view = inflater.inflate(R.layout.car_price_fragment, null);
//		initView(inflater, view);
//		return view;
//	}
//
//	private void initView(LayoutInflater inflater, View view) {
//		listView = (ListView) view
//				.findViewById(R.id.car_price_fragment_listview);
//		alphabetListView = (AlphabetListView) view
//				.findViewById(R.id.car_price_fragment_alphabetlistview);
//		pinnedHeaderListView = (PinnedHeaderListView) view
//				.findViewById(R.id.car_price_fragment_pinnedheaderlistview);
//		sectionListAdapter = new CarPriceSectionListAdapter(inflater, getData());
//		alphabetListView.setAdapter(pinnedHeaderListView, sectionListAdapter,
//				listener, indexList);
//		pinnedHeaderListView.setOnScrollListener(sectionListAdapter);
//		pinnedHeaderListView.setPinnedHeaderView(inflater.inflate(
//				R.layout.car_pinnedheaderlistview_header_listview,
//				pinnedHeaderListView, false));
//
//		listView.setAdapter(new CarPriceFragmentLeftListViewAdapter(getActivity()));
//		listView.setOnItemClickListener(new LeftListViewItemClickListener());
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
//				data.add(car.setSection(indexList.get(i)).setHeader(header.get(i)).setImage(R.drawable.app_icon).setName(
//						name.get(i).get(j)).setType("SUV").setPrice("1~2"));
//			}
//		}
//		return data;
//	}
//	
//	class LeftListViewItemClickListener implements OnItemClickListener{
//		
//		@Override
//		public void onItemClick(AdapterView<?> parent, View view, int position,
//				long id) {
//			/**
//			 * 在这里更新右侧listview的adapter的数据
//			 */
//		}
//		
//	}
//	//锁频后重新开启时将AutoLibraryMainFragment.SLIDE_MENU_STATE重置为锁频前的状态（不能滑动）
//	@Override
//	public void onResume() {
//		super.onResume();
//		AutoLibraryMainFragment.SLIDE_MENU_STATE = MainSlidingActivity.ALL_UNAVAILABLE;
//		MainSlidingActivity.changeSlidingMenuState(AutoLibraryMainFragment.SLIDE_MENU_STATE);
//		
//	}
}
