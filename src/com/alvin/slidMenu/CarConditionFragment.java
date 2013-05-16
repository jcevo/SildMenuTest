package com.alvin.slidMenu;

import com.alvin.api.abstractClass.BaseFragment;

import android.widget.GridView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.List;

/**
 * 条件选车主页
 * 
 * @author xjzhao
 * 
 */
public class CarConditionFragment extends BaseFragment {

	private RelativeLayout upGridViewLayout;
	private GridView shamGridView;// upGridView收起时的gridview
	private GridView upGridView;
	private GridView downGridView;
	private ListView listView;
	private List<String> shamGridViewData;
	private List<String> upGridViewData;
	private List<String[]> downGridViewData;

//	private CarConditionFragmentUpGridViewAdapter shamGridViewAdapter;
//	private CarConditionFragmentUpGridViewAdapter upGridViewAdapter;
//	private CarConditionFragmentDownGridViewAdapter downGridViewAdapter;
//
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		getData();
//	}
//
//	/**
//	 * 包装gridview中的数据
//	 */
//	private void getData() {
//		// 下面的gridview中的数据包装
//		String[] price = getResources().getStringArray(
//				R.array.car_price_left_listview_item);
//		String[] level = getResources().getStringArray(
//				R.array.car_condition_listview_item_level);
//		String[] outputVolume = getResources().getStringArray(
//				R.array.car_condition_listview_item_output_volume);
//		String[] gearBox = getResources().getStringArray(
//				R.array.car_condition_listview_item_gear_box);
//		String[] structure = getResources().getStringArray(
//				R.array.car_condition_listview_item_structure);
//		String[] country = getResources().getStringArray(
//				R.array.car_condition_listview_item_country);
//		downGridViewData = new ArrayList<String[]>();
//		downGridViewData.add(price);
//		downGridViewData.add(level);
//		downGridViewData.add(outputVolume);
//		downGridViewData.add(gearBox);
//		downGridViewData.add(structure);
//		downGridViewData.add(country);
//
//		// 上面的shamGridView默认给四个空值,shamGridView默认三个空值
//		upGridViewData = new ArrayList<String>();
//		shamGridViewData = new ArrayList<String>();
//		for (int i = 0; i < 4; i++) {
//			if (i != 3) {
//				shamGridViewData.add("");
//			} else {
//				shamGridViewData.add("＋展开");
//			}
//		}
//
//	}
//
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {
//		View view = inflater.inflate(R.layout.car_condition_fragment, null);
//		init(view);
//		return view;
//	}
//
//	private void init(View view) {
//		upGridViewLayout = (RelativeLayout) view
//				.findViewById(R.id.car_condition_fragment_up_gridview_layout);
//		shamGridView = (GridView) view
//				.findViewById(R.id.car_condition_fragment_up_sham_gridview);
//		upGridView = (GridView) view
//				.findViewById(R.id.car_condition_fragment_up_gridview);
//		downGridView = (GridView) view
//				.findViewById(R.id.car_condition_fragment_down_gridview);
//		listView = (ListView) view
//				.findViewById(R.id.car_condition_fragment_listview);
//		shamGridViewAdapter = new CarConditionFragmentUpGridViewAdapter(
//				getActivity(), CarService.CAR_CONDITION_UP_SHAM_GRIDVIEW);
//		upGridViewAdapter = new CarConditionFragmentUpGridViewAdapter(
//				getActivity(), CarService.CAR_CONDITION_UP_GRIDVIEW);
//		downGridViewAdapter = new CarConditionFragmentDownGridViewAdapter(
//				getActivity());
//		shamGridView.setAdapter(shamGridViewAdapter);
//		upGridView.setAdapter(upGridViewAdapter);
//		downGridView.setAdapter(downGridViewAdapter);
//		shamGridViewAdapter.setData(shamGridViewData);
//		upGridViewAdapter.setData(upGridViewData);
//		downGridViewAdapter.setData(downGridViewData.get(0));// 打开后右侧gridview默认显示价格内容
//		listView.setAdapter(new CarConditionFragmentListViewAdapter(
//				getActivity()));
//		setOnClick();
//	}
//
//	private void setOnClick() {
//		CarConditionOnItemClickItem itemClick = new CarConditionOnItemClickItem();
//		shamGridView.setOnItemClickListener(itemClick);
//		listView.setOnItemClickListener(itemClick);
//		upGridView.setOnItemClickListener(itemClick);
//		downGridView.setOnItemClickListener(itemClick);
//	}
//
//	// 锁频后重新开启时将AutoLibraryMainFragment.SLIDE_MENU_STATE重置为锁频前的状态（不能滑动）
//	@Override
//	public void onResume() {
//		super.onResume();
//		AutoLibraryMainFragment.SLIDE_MENU_STATE = MainSlidingActivity.ALL_UNAVAILABLE;
//		MainSlidingActivity
//				.changeSlidingMenuState(AutoLibraryMainFragment.SLIDE_MENU_STATE);
//	}
//
//	private int listViewPosition;// 记录左侧listview的position
//	private int upGridViewCount;//记录shamGridView的item是否填充满
//
//	/**
//	 * listview和gridview的OnItemClickItem事件
//	 * 
//	 * @author xjzhao
//	 * 
//	 */
//	class CarConditionOnItemClickItem implements OnItemClickListener {
//
//		@Override
//		public void onItemClick(AdapterView<?> parent, View view, int position,
//				long id) {
//			int viewID = parent.getId();
//
//			if (viewID == R.id.car_condition_fragment_up_sham_gridview) {// shamGridView的Item点击事件
//				if (position == 3 && upGridViewData.size() > 3) {
//					// shamGridView.setVisibility(View.GONE);
//					upGridViewLayout.setVisibility(View.VISIBLE);
//				}
//			} else if (viewID == R.id.car_condition_fragment_listview) {// listView的Item点击事件
//				downGridViewAdapter.setData(downGridViewData.get(position));
//				downGridViewAdapter.notifyDataSetChanged();
//				listViewPosition = position;
//			} else if (viewID == R.id.car_condition_fragment_up_gridview) {// upGridView的Item点击事件
//
//
//			} else if (viewID == R.id.car_condition_fragment_down_gridview) {// downGridView的Item点击事件
//				view.setBackgroundResource(R.drawable.car_condition_up_gridview_item_transparent);
//				if (upGridViewCount < 3) {
//					shamGridViewData.remove(upGridViewCount);
//					shamGridViewData.add(upGridViewCount,
//							downGridViewData.get(listViewPosition)[position]);
//				} 
//				upGridViewData.add(upGridViewCount++, downGridViewData.get(listViewPosition)[position]);
//				shamGridViewAdapter.notifyDataSetChanged();
//				upGridViewAdapter.notifyDataSetChanged();
//			}
//
//		}
//
//	}
}
