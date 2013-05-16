package com.alvin.slidMenu;

import cn.com.pcgroup.android.framework.cache.CacheManager;
import cn.com.pcgroup.android.framework.http.client.CacheParams;

import com.alvin.api.abstractClass.BaseMultiImgFragment;
import com.alvin.common.R;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

/**
 * 图集中显示“香车美女”和“汽车图片”的fragment
 * @author xjzhao
 *
 */
public class PhotosGridViewFragment extends BaseMultiImgFragment {
	
//	private GridView gridView = null;
//	String gridViewUrl = null;                        //grieView数据接口
//
//	private PhotosGridViewAdapter gridViewAdapter = null;  //GridView的adapter
//	private List<PhotosFirst> listGridView = null;  //存放gridView显示数据的bean（解析搭配的第一层JSON数据）
//	private ProgressBar progressBar = null; //图片未加载出来的时候要显示的滚动条
//	
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		Bundle bundle = getArguments();
//		gridViewUrl = bundle.getString("url");
//		loadData();
//	}
//
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {
//		
//		View view = inflater.inflate(R.layout.photos_gridview_fragment, null);
//		gridView = (GridView) view.findViewById(R.id.photos_gridview_fragment_gridview);
//		gridViewAdapter = new PhotosGridViewAdapter(getActivity());
//		imageFetcher.setLoadingImage(R.drawable.photos_gridview_background);
//		gridViewAdapter.setImageFetcher(imageFetcher);
//		gridView.setAdapter(gridViewAdapter);
//		gridView.setOnScrollListener(new GridViewOnScrollLostener());
//		gridView.setOnItemClickListener(new GridViewOnItemClickListener());
//		return view;
//	}
//
	public void setProgressBar(ProgressBar progressBar){
//		this.progressBar = progressBar;
	}
//	
//	/**
//	 * 解析gridview用到的JSON数据，得到相应数据
//	 */
//	private void loadData(){
//		listGridView = new ArrayList<PhotosFirst>();
//		CacheParams cacheParams = new CacheParams(CacheManager.TYPE_EXTERNAL, CacheManager.dataCacheExpire, true);
//		AsyncDownloadUtils.getJson(getActivity(), gridViewUrl, cacheParams, jsonHttoHandler);
//	}
//	private JsonHttpHanlder jsonHttoHandler = new JsonHttpHanlder() {
//		
//		@Override
//		public void onSuccess(int statusCode, JSONObject response) {
//			listGridView = PhotosService.getFirstData(response);
//			if(null != listGridView){
//				gridViewAdapter.setList(listGridView).setProgressBar(progressBar);
//				gridViewAdapter.notifyDataSetChanged();
//			}
//		}
//		
//		@Override
//		public void onFailure(Throwable error, String content) {
//			
//		}
//	};
//
//	/**
//	 * 处理GridView的Item点击事件
//	 * @author xjzhao
//	 *
//	 */
//	private class GridViewOnItemClickListener implements OnItemClickListener {
//		@Override
//		public void onItemClick(AdapterView<?> parent, View view, int position,
//				long id) {
//			//将第一层解析到的数据中的url传递到PhotosDetailActivity继续解析取得所需数据
//			String url = listGridView.get(position).getUrl();
//			int number = listGridView.get(position).getPhotoCount();
//			Intent intent = new Intent();
//			intent.putExtra("url", url);
//			intent.putExtra("photosCount", number);
//			intent.setClass(getActivity(), PhotosDetailActivity.class);
//			startActivity(intent);
//			getActivity().overridePendingTransition(R.anim.right_fade_in, R.anim.sham_translate);
//		}
//	}
//	
//	/**
//	 * 监听GridView的滑动状态
//	 * @author xjzhao
//	 *
//	 */
//	private class GridViewOnScrollLostener implements OnScrollListener{
//
//		@Override
//		public void onScrollStateChanged(AbsListView view, int scrollState) {
//			if(scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING){
//				imageFetcher.setPauseWork(true);
//			}else{
//				imageFetcher.setPauseWork(false);
//			}
//		}
//
//		@Override
//		public void onScroll(AbsListView view, int firstVisibleItem,
//				int visibleItemCount, int totalItemCount) {
//			// TODO Auto-generated method stub
//			
//		}
//		
//	}

}
