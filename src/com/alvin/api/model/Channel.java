package com.alvin.api.model;

import java.io.Serializable;
import java.util.List;


public class Channel implements Serializable{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long id;
    private String channelType;//资讯，产品
    private long channelId;
    private String channelName;
    private float channelOrder;//排序
    private String channelDisplay;//导航，更多
    private String url;
    private long channelAdvert;
    private long time;  //时间戳
    //-2代表这是一个父栏目，
    //-1代表这是一个独立栏目，
    //>0代表这个数字是其父栏目id
    private long parentId;
    
    private List<Channel> childChannels;
    
    public Channel() {
    }

    //设置名称和标签
    public Channel(String channelName, String channelDisplay) {
        this.channelName = channelName;
        this.channelDisplay = channelDisplay;
    }
    
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getChannelType() {
        return channelType;
    }
    public void setChannelType(String channelType) {
        this.channelType = channelType;
    }
    public long getChannelId() {
        return channelId;
    }
    public void setChannelId(long channelId) {
        this.channelId = channelId;
    }
    public String getChannelName() {
        return channelName;
    }
    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }
    public String getChannelDisplay() {
        return channelDisplay;
    }
    public void setChannelDisplay(String channelDisplay) {
        this.channelDisplay = channelDisplay;
    }
    public float getChannelOrder() {
        return channelOrder;
    }
    public void setChannelOrder(float channelOrder) {
        this.channelOrder = channelOrder;
    }
    public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
    public long getChannelAdvert() {
        return channelAdvert;
    }
    public void setChannelAdvert(long channelAdvert) {
        this.channelAdvert = channelAdvert;
    }
    
    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public List<Channel> getChildChannels() {
        return childChannels;
    }

    public void setChildChannels(List<Channel> childChannels) {
        this.childChannels = childChannels;
    }

    /**
     * ID和名称相等即可
     */
    @Override
    public boolean equals(Object o) {
        if(!(o instanceof Channel)){
            return false;
        }
        Channel channel = (Channel)o;
        //id,名称和广告id全部相同才算是相等
        return channel.channelId==this.channelId&&channel.channelName.equals(this.channelName)&&channel.channelAdvert==this.channelAdvert;    
    }
}
