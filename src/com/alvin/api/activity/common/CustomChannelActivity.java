package com.alvin.api.activity.common;

import com.alvin.api.abstractClass.BaseFragmentActivity;
import com.alvin.api.adapter.DragAdapter;
import com.alvin.api.adapter.DragAdapter.DragListener;
import com.alvin.api.model.Channel;
import com.alvin.api.utils.ChannelUtils;
import com.alvin.common.R;
import com.alvin.ui.DragGridView;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;

import java.util.ArrayList;

/***
 * 栏目定制
 * 
 * @author poble
 * 
 */
public class CustomChannelActivity extends BaseFragmentActivity {
	//现有栏目
	private DragGridView navGridview = null;
	private DragAdapter navAdapter = null;
	private int position;
	private ArrayList<Channel> navchannels = null;
	//更多栏目
	private GridView moreGridView = null;
	private DragAdapter moreAdapter = null;
	private ArrayList<Channel> moreChannels = null;
	
	public static boolean channelChanged = false;//标记栏目信息是否发生改变

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.custom_channel_activity);
		channelChanged = false;
		//初始化组件
		navGridview = (DragGridView) findViewById(R.id.custom_channel_drag_grid);
		moreGridView = (GridView) findViewById(R.id.custom_channel_more_grid);
		
		//现有栏目数据初始
		navchannels = (ArrayList<Channel>) ChannelUtils.getNavAndEventChannel(CustomChannelActivity.this, 0);
		navchannels.remove(0);
		navAdapter = new DragAdapter(CustomChannelActivity.this,navchannels);
		navGridview.setAdapter(navAdapter);
		navGridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
		
		//更多栏目数据初始
		moreChannels = (ArrayList<Channel>) ChannelUtils.getMoreChannel(CustomChannelActivity.this, 0);
		moreAdapter = new DragAdapter(CustomChannelActivity.this,moreChannels);
		moreGridView.setAdapter(moreAdapter);
		moreGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		
		//现有栏目添加长按事件
		navGridview.setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				position = arg2;
				navGridview.start(position);
				return true;
			}
		});
		
		
		//现有栏目添加单击事件
		navGridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Channel channel = navchannels.get(arg2);
				channel.setChannelDisplay("more");
				ChannelUtils.moveNavToMore(channel);
				update();
				
			}
		});
		
		//更多栏目单击事件
		moreGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Channel channel = moreChannels.get(arg2);
				channel.setChannelDisplay("nav");
				ChannelUtils.moveMoreToNav(channel, CustomChannelActivity.this);
				update();
			}
		});
		
		//现有栏目添加remove接口
		navAdapter.setListener(new DragListener() {
			
			@Override
			public void onRemove(int postion) {
				Channel channel = navchannels.get(postion);
				channel.setChannelDisplay("more");
				ChannelUtils.moveNavToMore(channel);
				update();
			}
		});
	}
	
	/***
	 * 更新栏目数据
	 */
	private void update(){
		navchannels = (ArrayList<Channel>) ChannelUtils.getNavAndEventChannel(CustomChannelActivity.this, 0);
		navchannels.remove(0);
		moreChannels = (ArrayList<Channel>) ChannelUtils.getMoreChannel(CustomChannelActivity.this, 0);
		moreAdapter.setChannels(moreChannels);
		navAdapter.setChannels(navchannels);
		moreAdapter.notifyDataSetChanged();
		navAdapter.notifyDataSetChanged();
		CustomChannelActivity.channelChanged = true;
	}
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
}
