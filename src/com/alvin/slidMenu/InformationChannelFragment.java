package com.alvin.slidMenu;

import cn.com.pcgroup.android.bitmap.util.ImageFetcher;
import cn.com.pcgroup.android.bitmap.util.ImageFetcherUtils;
import cn.com.pcgroup.android.framework.cache.CacheManager;
import cn.com.pcgroup.android.framework.http.client.AsynLoadImageUtils;
import cn.com.pcgroup.android.framework.http.client.AsynLoadImageUtils.BuildParams;
import cn.com.pcgroup.android.framework.http.client.CacheParams;

import com.alvin.api.abstractClass.BaseFragment;
import com.alvin.api.config.Env;
import com.alvin.api.model.Channel;
import com.alvin.common.R;
import com.alvin.ui.HeaderGallery;
import com.alvin.ui.SimpleToast;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InformationChannelFragment extends BaseFragment {
	
//	//传递channel信息
//	private Channel channel;
//	
//	//全局使用
//	private boolean isAddMore = false;//是否是加载更多
//	private LayoutInflater inflater = null;
//	private MainSlidingActivity mainActivity = null; 
//	private boolean isNewChannel = false;//是否是“最新”栏目
//	private static String TAG = "InformationChannelFragment";
//	private String currentUrl = "";
//	private int pageNo = 0;		//当前页码
//	private int pageSize = 0;	//页面大小
//	private int topSize = 0;	//页面顶部添加项
//	private ImageFetcher imageFetcherViewPager;//图片下载
//	
//	//焦点图
//	private View headerView = null;
//	//private FrameLayout headerLayout = null;
//	private HeaderGallery focusGallery = null;
//	private List<ArticlListItem> focusList;
//	private FocuseAdapter focuseAdapter = null;
//	private int focuseSize = 5;
//	private ProgressBar focuseProgressBar = null;
//	
//	//焦点图底部导航
//	private ImageView bottomIndicateImage = null;//焦点图底部指示图片
//	private ScaleAnimation animation = null;
//	private int focusBottomImageWidth = 0;//焦点图底部指示图片宽度
//	private int oldPosition = 0;
//	
//	//文章列表
//	private PullToRefreshListView articleListView = null;
//	private List<ArticlListItem> articleList = new ArrayList<ArticlListItem>();
//	private InforAdapter inforAdapter = null;
//	
//	/**数据加载模式，第一次启动，加载本地缓存（忽略过期）本地缓存加载后，填充数据并执行网络刷新加载，然后再填充网络数据**/
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		//接收传递数据
//		channel = (Channel) getArguments().getSerializable("channel");
//		
//		//初始化mainActivity
//		mainActivity = (MainSlidingActivity) getActivity();
//		
//		//初始化焦点图底部只是图片宽度
//		this.focusBottomImageWidth = Env.screenWidth / 5;
//		
//		currentUrl =  "http://mrobot.pconline.com.cn/v2/cms/channels/"+channel.getChannelId();
//		
//		imageFetcherViewPager = ImageFetcherUtils.instanceImageFecher(mainActivity, getChildFragmentManager(), null);
//		imageFetcherViewPager.setLoadingImage(R.drawable.app_thumb_default_80_60);
//	}
//
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {
//		Log.v(TAG, "onCreateView"+channel.getChannelName());
//		//初始化inflater
//		this.inflater = inflater;
//		
//		//加载布局
//		View view = inflater.inflate(R.layout.information_channel_fragment,null);
//		
//		//初始化布局下的view
//		initView(view);
//		return view;
//	}
//	
//	/***
//	 * 初始化view
//	 * @param layout
//	 */
//	private void initView(View layout){
//		//初始化listview
//		articleListView = (PullToRefreshListView) layout.findViewById(R.id.information_infor_list);
//		if(articleList!=null&&articleList.size()>0){
//			articleListView.setVisibility(View.VISIBLE);
//		}
//		articleListView.setTimeTag(channel.getChannelId()+"");
//		inforAdapter =  new InforAdapter(getActivity(),articleList,imageFetcherViewPager);
//		
//		//最新栏目下  加载header
//		if(channel.getChannelName().equals("最新")){
//			isNewChannel = true;
//			initHeader();
//			focuseAdapter = new FocuseAdapter(); 
//			focusGallery.setAdapter(focuseAdapter);
//			int oldPosition = Integer.MAX_VALUE/2-3;
//			focusGallery.setSelection(oldPosition);
//		}
//		articleListView.setAdapter(inforAdapter);
//		articleListView.setPullLoadEnable(true);
//		//设置listview下拉刷新监听事件
//		articleListView.setPullAndRefreshListViewListener(pullAndRefreshlistListener);
//		//listview滚动监听事件
//		articleListView.setOnScrollListener(scrollListener);
//		//listview item 点击事件
//		articleListView.setOnItemClickListener(OnItemClickListener);
//		
//		//加载本地数据
//		firstLoadLocalDdata();
//	}
//	
//	/***
//	 * 初始化header信息
//	 */
//	private void initHeader() {
//		
//		//headerView
//		headerView = inflater.inflate(R.layout.focuse_list_header, null);
//		//headerLayout = (FrameLayout) headerView.findViewById(R.id.show_picture_layout);
//		focusGallery = (HeaderGallery) headerView.findViewById(R.id.show_picture);
//		focuseProgressBar = (ProgressBar) headerView.findViewById(R.id.header_gallery_loadprogress);
//		//焦点图底部指示图片
//		bottomIndicateImage = (ImageView) headerView.findViewById(R.id.id_image);
//		LinearLayout.LayoutParams para = new LinearLayout.LayoutParams(focusBottomImageWidth, LayoutParams.WRAP_CONTENT);
//		para.gravity = Gravity.BOTTOM;
//		bottomIndicateImage.setLayoutParams(para);
//		
//		//将焦点图加入list的header
//		articleListView.addHeaderView(headerView);
//		//focusGallery.setChangerListener(listener);
//		focusGallery.setOnItemSelectedListener(itemSelectedlistener);
//		
//		//焦点图的touch事件自己处理（屏蔽掉slidingMenu和viewPager对tocuh事件的拦截）
//		focusGallery.setOnTouchListener(new OnTouchListener() {
//			
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				((ViewGroup) mainActivity.getSlidingMenu().getContent()).requestDisallowInterceptTouchEvent(true);
//				InformationMainFragment.pager.requestDisallowInterceptTouchEvent(true);
//				return false;
//			}
//		});
//	}
//	
//	
//	/***
//	 * 首次启动，加载本地数据
//	 */
//	private void firstLoadLocalDdata(){
//		//异步下载本地缓存数据（忽略缓存过期时间）
//		AsyncTask<String, String, String> myTask = new AsyncTask<String, String, String>(){
//
//			@Override
//			protected String doInBackground(String... arg0) {
//				byte[] bytes = CacheManager.getCacheIgnoreExpire(currentUrl+"?pageNo=1");
//				String content = "";
//				if(null!=bytes&&bytes.length>0){
//					 content = new String(bytes);
//				}
//				return content;
//			}
//			@Override
//			protected void onPostExecute(String result) {
//				if(null!=result&&result.length()>0){
//					JSONObject object = null;
//					try {
//						object = new JSONObject(result);
//						getInforHandler.onSuccess(0, object);
//					} catch (JSONException e) {
//						e.printStackTrace();
//					}
//				}
//				//资讯栏目时候，刷新
//				if(isNewChannel&&null!=InformationMainFragment.fragments.get(0)&&!InformationMainFragment.isLooked(channel.getChannelId()+"")){
//					InformationMainFragment.fragments.get(0).startLoad();
//					InformationMainFragment.putLooked(channel.getChannelId()+"");
//				}
//				super.onPostExecute(result);
//			}
//		};
//		//执行本地加载
//		myTask.execute("");
//	}
//	
//	/***
//	 * 开始加载图片
//	 */
	public void startLoad(){
			//加载数据
//			articleListView.showHeaderAndRefresh();
	}
//	
//	/***
//	 * listview item点击监听器
//	 */
//	private OnItemClickListener OnItemClickListener = new OnItemClickListener(){
//
//		@Override
//		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
//				long arg3) {
//	                if (null != articleList && articleList.size() > 0) {
//	                	ArticlListItem itemInfor = null;
//	                	if(isNewChannel){//资讯时候，去掉焦点图和下拉刷新占据的两个位置
//	                		itemInfor = articleList.get(arg2 - 2);
//	                	}else{//非资讯时候，去掉下拉刷新占据的位置
//	                		itemInfor = articleList.get(arg2 - 1);
//	                	}
//	                        if (itemInfor.getClickCounter() != null) {
//	                            //AdUtils.incCounterAsyn(itemInfor.getClickCounter());
//	                        }
//
//	                       /* if (URIUtils.hasURI(itemInfor.getUrl())) {
//	                            URIUtils.gotoURI(itemInfor.getUrl(),InformationBrowserActivity.this);
//	                        } else {*/
//	                        	Intent intent = new Intent(mainActivity,InformatioinArticleActivity.class);
//	                            Bundle b = new Bundle();
//	                            b.putString("id", itemInfor.getId());
//	                            b.putString("channelId",channel.getChannelId()+"");
//	                            b.putString("channelAdvert",channel.getChannelAdvert()+"");
//	                            b.putString("channelName", channel.getChannelName());
//	                            intent.putExtras(b);
//	                            startActivity(intent);
//	                            //getActivity().overridePendingTransition(R.anim.right_fade_in, R.anim.left_fade_out);
//	                            getActivity().overridePendingTransition(R.anim.right_fade_in, R.anim.sham_translate);
//	                       // }
//	                }
//		}};
//	
//	/***
//	 * listview滚动监听器
//	 */
//	private OnScrollListener scrollListener = new OnScrollListener(){
//
//		
//		@Override
//		public void onScrollStateChanged(AbsListView view, int scrollState) {
//			switch (scrollState) {  
//            case AbsListView.OnScrollListener.SCROLL_STATE_FLING:  
//            	imageFetcherViewPager.setPauseWork(true);
//                break;  
//            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:  
//            	imageFetcherViewPager.setPauseWork(false);
//                break;  
//            case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:  
//            	imageFetcherViewPager.setPauseWork(false);
//                break;  
//            default:  
//                break;  
//            }  
//		}
//		
//		@Override
//		public void onScroll(AbsListView view, int firstVisibleItem,
//				int visibleItemCount, int totalItemCount) {
//			
//		}
//	
//	};
//	
//	/***
//	 * listview下拉刷新和加载更多监听器
//	 */
//	private PullAndRefreshListViewListener pullAndRefreshlistListener = new PullAndRefreshListViewListener(){
//
//		@Override
//		public void onRefresh() {
//			loadData(false);
//		}
//
//		@Override
//		public void onLoadMore() {
//			loadData(true);
//		}
//	};
//	
//	/***
//	 * 数据加载方法
//	 * @param isAddMore   是否是加载更多（分页加载时候为true，刷新为false）
//	 */
//	private void loadData(boolean isAddMore){
//		this.isAddMore = isAddMore; 
//		if(isNewChannel&&!isAddMore){//最新页面，刷新时，停止焦点图自动切换
//			focusGallery.onPouse();
//		}
//		if(isAddMore){//加载更多时候，初始化分页
//			int downloadPage = ((int) Math.ceil((inforAdapter.getCount() - topSize) / (float) pageSize)) + 1;
//			if(downloadPage>pageNo){
//				pageNo = downloadPage;
//			}else{
//				return;
//			}
//		}else{//刷新时，重置分页
//			pageNo = 1;
//		}
//		String pageUrl = currentUrl +"?pageNo="+pageNo;
//		System.out.println("**pageUrl:"+pageUrl);
//		CacheParams cacheParams = new CacheParams(CacheManager.TYPE_INTERNAL, CacheManager.dataCacheExpire, true);
//	    AsyncDownloadUtils.getJson(mainActivity, pageUrl, cacheParams, getInforHandler);
//		
//	}
//	
//	
//	/***
//	 * 数据解析
//	 */
//	private JsonHttpHanlder getInforHandler = new JsonHttpHanlder(){
//
//		@Override
//		public void onSuccess(int statusCode, JSONObject response) {
//			Map<String,List<ArticlListItem>> inforMap = InformationApiService.getItemInforMap(response);
//			if(null!=inforMap){
//                if(isNewChannel&&!isAddMore){//最新栏目时候，加载焦点图
//                	focusList = inforMap.get("focus");
//                	showFocuseImage();
//                }
//                List<ArticlListItem> articles = inforMap.get("articleList");
//                if(!isAddMore){//刷新时，需要清空之前的集合并重新初始化分页信息
//                	articleList.clear();
//                	initPageMessage(articles);
//                }
//                articleList.addAll(articles);
//                inforAdapter.notifyDataSetChanged();
//                articleListView.setVisibility(View.VISIBLE);
//		  	}else{
//		  		SimpleToast.show(mainActivity, "没有数据", Toast.LENGTH_SHORT);
//		  	}
//			articleListView.stopRefresh();
//			articleListView.stopLoadMore();
//		}
//		public void onFailure(Throwable error, String content) {
//			articleListView.stopRefresh();
//			articleListView.stopLoadMore();
//			  if(isNewChannel){//最新栏目时候，加载焦点图
//              	showFocuseImage();
//              }
//		};
//	};
//	
//	/***
//	 * 初始化文章分页信息
//	 * @param tempArticleList
//	 */
//	private void initPageMessage(List<ArticlListItem> tempArticleList ){
//		pageSize = 0;
//		topSize = 0;
//		 for (ArticlListItem item : tempArticleList) {
//             if (item.getType() == ArticlListItem.TYPE_NORMAL) {
//                 pageSize++;
//             } else {
//                 topSize++;
//             }
//         }
//	}
//	
//	/***
//	 * listview  item被选中状态监听器
//	 */
//	private OnItemSelectedListener itemSelectedlistener = new OnItemSelectedListener(){
//
//		@Override
//		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
//				long arg3) {
//			oldPosition = oldPosition%focuseSize;
//			int nowpos = arg2;
//			nowpos = nowpos%focuseSize;
//			if(oldPosition == arg2){//相等
//				
//			}else{
//				oldPosition ++;
//				nowpos++;
//				animation = new ScaleAnimation(oldPosition*1.0f, nowpos*1.0f, 1f, 1f);
//				animation.setFillAfter(true);
//				animation.setDuration(300);
//				bottomIndicateImage.clearAnimation();
//				bottomIndicateImage.startAnimation(animation);
//			}
//			oldPosition = arg2;
//		}
//
//		@Override
//		public void onNothingSelected(AdapterView<?> arg0) {
//			
//		}
//		
//	};
//	
//	/***
//	 * 显示焦点图
//	 */
//	private void showFocuseImage(){
//		if(null!=focusList&&focusList.size()>0){
//			focuseSize = focusList.size();
//	     	focuseAdapter.notifyDataSetChanged();
//	     	bottomIndicateImage.setVisibility(View.VISIBLE);
//	     	//开启焦点图的自动切换
//	 		focusGallery.startAutoSwitc();
//    	}
//	}
//
//	@Override
//	public void onSaveInstanceState(Bundle outState) {
//		super.onSaveInstanceState(outState);
//	}
//	
//	/***
//	 * 焦点图适配器
//	 * @author user
//	 *
//	 */
//	class FocuseAdapter extends BaseAdapter {
//		private AsynLoadImageUtils imageUtil = null;
//	    private  AsynLoadImageUtils.BuildParams buildParams  = null;
//	    public FocuseAdapter(){
//	    	 imageUtil = AsynLoadImageUtils.getInstance();
//	         buildParams  = new BuildParams(); 
//	         buildParams.setCache(true).setProgressBar(focuseProgressBar);
//	    }
//
//		@Override
//		public int getCount() {
//			return Integer.MAX_VALUE;
//		}
//
//		@Override
//		public Object getItem(int arg0) {
//			return arg0;
//		}
//
//		@Override
//		public long getItemId(int arg0) {
//			return arg0;
//		}
//
//		@Override
//		public View getView(int arg0, View arg1, ViewGroup arg2) {
//			ImageView image = null;
//			if (arg1 == null) {
//				 arg1 = new ImageView(mainActivity);
//	             image = (ImageView) arg1;
//	        }else{
//	              image = (ImageView) arg1;
//	        }
//			image.setScaleType(ImageView.ScaleType.FIT_XY);
//			image.setAdjustViewBounds(false);
//			Gallery.LayoutParams param  = new Gallery.LayoutParams(focusBottomImageWidth*5,focusBottomImageWidth*5 * 330 / 640);
//			image.setLayoutParams(param);
//			if(arg0<0){
//				arg0 = arg0 + focuseSize;
//			}
//			arg0 = arg0 % focuseSize;
//			image.setImageResource(R.drawable.app_thumb_default_640_330);
//			if(null!=focusList){
//				ArticlListItem item =  focusList.get(arg0);
//				String imageUrl = item.getImage();
//				image.setTag(imageUrl);
//				imageUtil.loadImage(mainActivity, imageUrl,image, buildParams);
//			}
//			return arg1;
//		}
//	}
//	@Override
//	public void onPause() {
//		Log.v(TAG, "onPause");
//		if(focusGallery!=null){
//			focusGallery.onPouse();
//		}
//		ImageFetcherUtils.onPause(imageFetcherViewPager);
//		super.onPause();
//	}
//	@Override
//	public void onResume() {
//		Log.v(TAG, "onResume");
//		if(focusGallery!=null){
//			focusGallery.onResume();
//		}
//		ImageFetcherUtils.onResume(imageFetcherViewPager);
//		super.onResume();
//	}
//	@Override
//	public void onDestroy() {
//		Log.v(TAG, "onDestroy");
//		super.onDestroy();
//		ImageFetcherUtils.onDestroy(imageFetcherViewPager);
//	}
//	@Override
//	public void onDestroyView() {
//		Log.v(TAG, "onDestroyView");
//		super.onDestroyView();
//	}
}
