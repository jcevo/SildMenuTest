package com.alvin.api.adapter;

import com.alvin.api.activity.common.CustomChannelActivity;
import com.alvin.api.model.Channel;
import com.alvin.api.utils.ChannelUtils;
import com.alvin.common.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class DragAdapter extends BaseAdapter {

	// 由于数组去除之后会留空，改用list,方便后续位置改变后，数据的交换
	private  ArrayList<Channel> channels = new ArrayList<Channel>();
	public void setChannels(ArrayList<Channel> channels) {
		this.channels = channels;
	}

	private Context context;

	public DragAdapter(Context context,ArrayList<Channel> channels) {
		this.context = context;
		this.channels = channels;
	}

	public int getCount() {
		return channels!=null?channels.size():0;
	}

	public Object getItem(int item) {
		return item;
	}

	public long getItemId(int id) {
		return id;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		TextView text1 = null;
		convertView = LayoutInflater.from(context).inflate(
				R.layout.information_custom_channel_grid_item, null);

		text1 = (TextView) convertView.findViewById(R.id.textView);
		int i = position;
		text1.setText(channels.get(i).getChannelName());
		return convertView;
	}

	// 拖拽后交换位置
	public void exchangeData(int dragSrcPosition, int dragPosition) {
		//原始位置
		Channel texttemp3 = channels.get(dragSrcPosition);
		Channel temp1 = channels.get(dragPosition);
		ChannelUtils.changeChannelOrder(texttemp3, temp1);
		channels.remove(dragSrcPosition);
		channels.add(dragPosition, texttemp3);
		undateText(channels);
		notifyDataSetChanged();
		CustomChannelActivity.channelChanged = true;
	}
	// 拖拽后交换位置
	public void remove(int dragSrcPosition) {
			if(null!=listener){
				listener.onRemove(dragSrcPosition);
			}
		
		
	}

	public void undateText(ArrayList<Channel> channels) {
		this.channels = channels;
	}
	public interface DragListener{
		void onRemove(int postion);
	}
	private DragListener listener = null;
	public void setListener(DragListener listener) {
		this.listener = listener;
	}
}
