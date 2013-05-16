package com.alvin.api.utils;

import cn.com.pcgroup.common.android.utils.FileUtils;
import cn.com.pcgroup.common.android.utils.ListUtils;

import com.alvin.api.config.Config;
import com.alvin.api.config.Env;
import com.alvin.api.model.Channel;
import com.alvin.common.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChannelUtils {
    
    public static void initClinetChannel(Context context){
        try{            
            HashMap<String,List<Channel>> clientChannel = getClientChannel(context);
            if(!(clientChannel!=null&&clientChannel.size()>0)){
                String channelString = FileUtils.readTextInputStream(context.getAssets().open("channel.config"));
                String jsonString = channelString.substring(channelString.indexOf("{"),channelString.lastIndexOf("}")+1);
                checkClientChannel(context, jsonString);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    /**
     * 获取服务器栏目信息
     * @param context
     * @param jsonString
     * @return
     * @throws JSONException
     */
    public static HashMap<String,List<Channel>> getServerChannel(Context context, String jsonString) throws JSONException{
        
        JSONObject json = new JSONObject(jsonString);
        String[] channelKeys = context.getResources().getStringArray(R.array.main_channel_items);
        
        HashMap<String,List<Channel>> channelMap = new HashMap<String, List<Channel>>();
        JSONArray jsonArray = null;
        Channel channel = null;
        List<Channel> channelList = null;
        for(String key : channelKeys){
            int channel_display_inc = 0;
            jsonArray = json.optJSONArray(key);
            if(jsonArray==null){
                continue;
            }
            channelList = new ArrayList<Channel>();
            for(int i=0; i<jsonArray.length(); i++){
                channel_display_inc++;
                channel = new Channel();
                channel.setChannelId(jsonArray.getJSONArray(i).getLong(0));
                channel.setChannelName(jsonArray.getJSONArray(i).getString(1));
                channel.setChannelAdvert(jsonArray.getJSONArray(i).getLong(2));
                channel.setChannelType(key);
                channel.setChannelOrder(channel_display_inc);
                if(channel_display_inc>8){
                    channel.setChannelDisplay("more");
                }else{
                    channel.setChannelDisplay("nav");
                }
                channelList.add(channel);
            }
            channelMap.put(key, channelList);
        }
        
        return channelMap;
    }
    
    /**
     * 获取本地客户端栏目信息
     * @param context
     * @return
     * @throws JSONException
     */
    public static HashMap<String,List<Channel>> getClientChannel(Context context) throws JSONException{
        String[] channelKeys = context.getResources().getStringArray(R.array.main_channel_items);
        HashMap<String,List<Channel>> channelMap = new HashMap<String, List<Channel>>();
        
        Cursor cursor = null;
        SQLiteDatabase db = Env.dbHelper.getReadableDatabase();
        Channel channel = null;
        List<Channel> channelList = null;
        
        try{
            for(String key : channelKeys){
                cursor = db.rawQuery("select * from "+Config.CHANNEL_TABLE+" where channel_type='"+key+"'", null);
                channelList = new ArrayList<Channel>();
                if(cursor!=null&&cursor.getCount()>0){
                    while(cursor.moveToNext()){
                        channel = new Channel();
                        channel.setChannelId(cursor.getLong(cursor.getColumnIndex("channel_id")));
                        channel.setChannelName(cursor.getString(cursor.getColumnIndex("channel_name")));
                        channel.setChannelOrder(cursor.getLong(cursor.getColumnIndex("channel_order")));
                        channel.setChannelDisplay(cursor.getString(cursor.getColumnIndex("channel_display")));
                        channel.setChannelAdvert(cursor.getLong(cursor.getColumnIndex("channel_advert")));
                        channel.setChannelType(key);
                        channelList.add(channel);
                    }
                    channelMap.put(key, channelList);
                }
                
                if(cursor!=null){
                    cursor.close();
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if(cursor!=null){
                cursor.close();
            }
        }
        return channelMap;
    }
    
    /**
     * 检测服务器端数据和本地客户端数据，如果服务器端数据有变化，则更新客户端数据
     * @param context
     * @throws JSONException 
     */
    public static synchronized void checkClientChannel(Context context, String jsonString) throws JSONException{
        HashMap<String,List<Channel>> serverChannel = getServerChannel(context, jsonString);
        HashMap<String,List<Channel>> clientChannel = getClientChannel(context);
        String[] channelKeys = context.getResources().getStringArray(R.array.main_channel_items);
        ListUtils<Channel> listUtils = new ListUtils<Channel>();
        for(String key:channelKeys){
            if(!listUtils.isListEquals(serverChannel.get(key), clientChannel.get(key))){
                if(clientChannel.get(key)==null||clientChannel.get(key).size()==0){
                    insertClientChannel(serverChannel.get(key));
                }else{
                    updateClientChannel(clientChannel.get(key), serverChannel.get(key));
                }
            }
        }
    }
    
    /**
     * 插入服务端数据到本地客户端数据库中
     * @param channels
     */
    public static void insertClientChannel(List<Channel> channels){
        SQLiteDatabase db = Env.dbHelper.getWritableDatabase();
        Cursor cursor = null;
        try{
            for(Channel c:channels){
                cursor = db.rawQuery("select * from "+Config.CHANNEL_TABLE+" where channel_id="+c.getChannelId()+" and channel_advert="+c.getChannelAdvert()+" and channel_name='"+c.getChannelName()+"'", null);
                if(cursor!=null&&cursor.getCount()>0){
                    cursor.close();
                    continue;
                }else if(cursor!=null){
                    cursor.close();
                }
                db.execSQL("insert into "+Config.CHANNEL_TABLE+"(channel_id,channel_name,channel_order,channel_display,channel_advert,channel_type) values("+c.getChannelId()+",'"+c.getChannelName()+"',"+c.getChannelOrder()+",'"+c.getChannelDisplay()+"',"+c.getChannelAdvert()+",'"+c.getChannelType()+"')");
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if(cursor!=null){
                cursor.close();
            }
        }
    }
    
    /**
     * 更新服务端数据到本地客户端数据库中
     * @param channels
     */
    public static void updateClientChannel(List<Channel> clientChannels, List<Channel> serverChannels){
        SQLiteDatabase db = Env.dbHelper.getWritableDatabase();
        try{
            //在本地客户端删除服务器上没有的栏目
            for(Channel c:clientChannels){
                if(!serverChannels.contains(c)){
                    db.execSQL("delete from "+Config.CHANNEL_TABLE+" where channel_id="+c.getChannelId()+" and channel_type='"+c.getChannelType()+"'");
                }
            }
            
            //在本地客户端增加服务器上新增的栏目
            for(Channel c:serverChannels){
                if(!clientChannels.contains(c)){
                    insertClientSingleChannel(c);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    /**
     * 插入单个栏目
     * @param c
     */
    public static void insertClientSingleChannel(Channel c){
        SQLiteDatabase db = Env.dbHelper.getWritableDatabase();
        Cursor cursor = null;
        try{
            cursor = db.rawQuery("select * from "+Config.CHANNEL_TABLE+" where channel_id="+c.getChannelId()+" and channel_advert="+c.getChannelAdvert()+" and channel_name='"+c.getChannelName()+"'", null);
            if(cursor!=null&&cursor.getCount()>0){
                cursor.close();
                return;
            }else if(cursor!=null){
                cursor.close();
            }
            db.execSQL("insert into "+Config.CHANNEL_TABLE+"(channel_id,channel_name,channel_order,channel_display,channel_advert,channel_type) values("+c.getChannelId()+",'"+c.getChannelName()+"',"+c.getChannelOrder()+",'"+c.getChannelDisplay()+"',"+c.getChannelAdvert()+",'"+c.getChannelType()+"')");
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if(cursor!=null){
                cursor.close();
            }
        }
    }
    
    /**
     * 对新列表重新排序
     * @param id 被拖拽项的id
     * @param channelOrder 拖拽位置项的channel_order
     * @param channelType 拖拽类型比如news,product等
     * @param channelDisplay 显示位置nav还是more
     * @return
     */
    public static boolean dragClinetChannel(long id, float channelOrder, String channelType, String channelDisplay){
        SQLiteDatabase db = Env.dbHelper.getWritableDatabase();
        try{
            db.execSQL("update "+Config.CHANNEL_TABLE +" set channel_order=(channel_order+1) where channel_order>="+channelOrder +" and channel_type='"+channelType+"'");
            db.execSQL("update "+Config.CHANNEL_TABLE +" set channel_order="+channelOrder+",channel_display='"+channelDisplay+"' where id="+id);
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
    /***
     * 更改频道顺序
     * @param startChannel
     * @param endChannel
     * @return
     */
    public static boolean changeChannelOrder(Channel startChannel,Channel endChannel){
    	 SQLiteDatabase db = Env.dbHelper.getWritableDatabase();
    	 try{
    		 if(startChannel.getChannelOrder()>endChannel.getChannelOrder()){//从后向前拖动
    			 db.execSQL("update "+Config.CHANNEL_TABLE +" set channel_order=(channel_order+1) where channel_order>="+endChannel.getChannelOrder() +" and channel_order<"+startChannel.getChannelOrder());
    		 }else if(startChannel.getChannelOrder()<endChannel.getChannelOrder()){//从前向后拖动
    			 db.execSQL("update "+Config.CHANNEL_TABLE +" set channel_order=(channel_order-1) where channel_order>"+startChannel.getChannelOrder() +" and channel_order<="+endChannel.getChannelOrder());
    		 }else{
    			 return false; 
    		 }
    		 db.execSQL("update "+Config.CHANNEL_TABLE +" set channel_order="+endChannel.getChannelOrder()+",channel_display='"+startChannel.getChannelDisplay()+"' where channel_id="+startChannel.getChannelId());
    		 return true; 
         }catch(Exception e){
             e.printStackTrace();
             return false;
         }
    }
    
    /***
     * 从导航移动到更多
     * @param channel
     * @return
     */
    public static boolean moveNavToMore(Channel channel){
    	 SQLiteDatabase db = Env.dbHelper.getWritableDatabase();
    	 try{
    		 db.execSQL("update "+Config.CHANNEL_TABLE +" set channel_order=(channel_order-1) where channel_order>="+channel.getChannelOrder());
    		 db.execSQL("update "+Config.CHANNEL_TABLE +" set channel_display='"+channel.getChannelDisplay()+"' where channel_id="+channel.getChannelId());
    		 return true; 
         }catch(Exception e){
             e.printStackTrace();
             return false;
         }
    }
    
    /***
     * 充更多移动到导航
     * @param channel
     * @param context
     * @return
     */
    public static boolean moveMoreToNav(Channel channel,Context context){
   	 List<Channel> channels = getNavAndEventChannel(context, 0);
   	 Channel last = channels.get(channels.size()-1);
   	 channel.setChannelOrder(last.getChannelOrder()+1);
   	 SQLiteDatabase db = Env.dbHelper.getWritableDatabase();
   	 String sql = "update "+Config.CHANNEL_TABLE +" set channel_order = "+(last.getChannelOrder()+1)+" , channel_display='"+channel.getChannelDisplay()+"' where channel_id="+channel.getChannelId();
   	 db.execSQL(sql);
   	 return true;
   }
    /**
     * 获取导航和重要事件的栏目
     * @param context
     * @param index
     * @return
     */
    public static List<Channel> getNavAndEventChannel(Context context, int index){
        Cursor cursor = null;
        SQLiteDatabase db = Env.dbHelper.getReadableDatabase();
        Channel channel = null;
        List<Channel> channelList = null;
        String[] channelKeys = context.getResources().getStringArray(R.array.main_channel_items);
        if(index<=channelKeys.length-1){
            try{
                cursor = db.rawQuery("select * from "+Config.CHANNEL_TABLE+" where channel_display = 'nav' and channel_type='"+channelKeys[index]+"' order by channel_order", null);

                channelList = new ArrayList<Channel>();
                if(cursor!=null&&cursor.getCount()>0){
                    while(cursor.moveToNext()){
                        channel = new Channel();
                        channel.setChannelId(cursor.getLong(cursor.getColumnIndex("channel_id")));
                        channel.setChannelName(cursor.getString(cursor.getColumnIndex("channel_name")));
                        channel.setChannelOrder(cursor.getLong(cursor.getColumnIndex("channel_order")));
                        channel.setChannelType(channelKeys[index]);
                        channel.setChannelDisplay(cursor.getString(cursor.getColumnIndex("channel_display")));
                        channel.setChannelAdvert(cursor.getLong(cursor.getColumnIndex("channel_advert")));
                        channelList.add(channel);
                    }
                }
                
                if(cursor!=null){
                    cursor.close();
                }
                cursor = db.rawQuery("select * from "+Config.CHANNEL_TABLE+" where channel_type='events' order by channel_order", null);
                
                int eventLocation = 4;
                if(cursor!=null&&cursor.getCount()>0){
                    while(cursor.moveToNext()){
                        channel = new Channel();
                        channel.setChannelId(cursor.getLong(cursor.getColumnIndex("channel_id")));
                        channel.setChannelName(cursor.getString(cursor.getColumnIndex("channel_name")));
                        channel.setChannelOrder(cursor.getLong(cursor.getColumnIndex("channel_order")));
                        channel.setChannelType(channelKeys[index]);
                        channel.setChannelDisplay(cursor.getString(cursor.getColumnIndex("channel_display")));
                        channel.setChannelAdvert(cursor.getLong(cursor.getColumnIndex("channel_advert")));
                        channelList.add(eventLocation-cursor.getCount(), channel);
                        eventLocation++;
                    }
                }
            }catch(Exception e){
                e.printStackTrace();
            }finally{
                if(cursor!=null){
                    cursor.close();
                }
            }
        }
        return channelList;
    }
    /**
     * 获取更多的栏目
     * @param context
     * @param index
     * @return
     */
    public static List<Channel> getMoreChannel(Context context, int index){
        Cursor cursor = null;
        SQLiteDatabase db = Env.dbHelper.getReadableDatabase();
        Channel channel = null;
        List<Channel> channelList = null;
        String[] channelKeys = context.getResources().getStringArray(R.array.main_channel_items);
        if(index<=channelKeys.length-1){
            try{
                cursor = db.rawQuery("select * from "+Config.CHANNEL_TABLE+" where channel_display = 'more' and channel_type='"+channelKeys[index]+"' order by channel_order", null);

                channelList = new ArrayList<Channel>();
                if(cursor!=null&&cursor.getCount()>0){
                    while(cursor.moveToNext()){
                        channel = new Channel();
                        channel.setChannelId(cursor.getLong(cursor.getColumnIndex("channel_id")));
                        channel.setChannelName(cursor.getString(cursor.getColumnIndex("channel_name")));
                        channel.setChannelOrder(cursor.getLong(cursor.getColumnIndex("channel_order")));
                        channel.setChannelType(channelKeys[index]);
                        channel.setChannelDisplay(cursor.getString(cursor.getColumnIndex("channel_display")));
                        channel.setChannelAdvert(cursor.getLong(cursor.getColumnIndex("channel_advert")));
                        channelList.add(channel);
                    }
                }
            }catch(Exception e){
                e.printStackTrace();
            }finally{
                if(cursor!=null){
                    cursor.close();
                }
            }
        }
        return channelList;
    }
    
    /**
     * 根据index获得对应的栏目
     * @param context
     * @param index
     * @return
     */
    public static List<Channel> getClientChannelByIndex(Context context,int index){
    	Cursor cursor = null;
        SQLiteDatabase db = Env.dbHelper.getReadableDatabase();
        Channel channel = null;
        List<Channel> channelList = null;
        String[] channelKeys = context.getResources().getStringArray(R.array.main_channel_items);
        if(index<=channelKeys.length-1){
            try{
                cursor = db.rawQuery("select * from "+Config.CHANNEL_TABLE+" where channel_type='"+channelKeys[index]+"' order by channel_order", null);
                channelList = new ArrayList<Channel>();
                if(cursor!=null&&cursor.getCount()>0){
                    while(cursor.moveToNext()){
                        channel = new Channel();
                        channel.setChannelId(cursor.getLong(cursor.getColumnIndex("channel_id")));
                        channel.setChannelName(cursor.getString(cursor.getColumnIndex("channel_name")));
                        channel.setChannelOrder(cursor.getLong(cursor.getColumnIndex("channel_order")));
                        channel.setChannelType(channelKeys[index]);
                        channel.setChannelDisplay(cursor.getString(cursor.getColumnIndex("channel_display")));
                        channel.setChannelAdvert(cursor.getLong(cursor.getColumnIndex("channel_advert")));
                        channelList.add(channel);
                    }
                }
            }catch(Exception e){
                e.printStackTrace();
            }finally{
                if(cursor!=null){
                    cursor.close();
                }
            }
        }
        return channelList;
    }
/*
    *//**
     * 保存选择的城市
     * @param cityName
     *//*
    public static void upateCacheCity(String cityName){
        SQLiteDatabase db = Env.dbHelper.getWritableDatabase();
        ContentValues contentVaues=new ContentValues();
        contentVaues.put("time", System.currentTimeMillis());
        contentVaues.put("value", cityName);
        Cursor cursor= db.rawQuery("select * from "+DBHelper.INFO_TABLE +" where name='cityname'", null);
        if(cursor.moveToNext() && cursor.getCount()>0){
            db.update(DBHelper.INFO_TABLE, contentVaues, "name=?", new String[]{"cityname"});
        }else{
            contentVaues.put("name", "cityname");
            db.insert(DBHelper.INFO_TABLE, null, contentVaues);
        }
        if(null!=cursor ) {
            cursor.close();
        }
    }
    
    *//**
     * 获取已选择的城市
     * @return 
     *//*
    public static String getCacheCity(){
        String cityName = null;
        SQLiteDatabase db = Env.dbHelper.getWritableDatabase();
        Cursor cursor= db.rawQuery("select * from "+DBHelper.INFO_TABLE +" where name='cityname'", null);
        if(cursor.moveToNext() && cursor.getCount()>0){
            cityName=cursor.getString(cursor.getColumnIndex("value")); 
        }
        if(null!=cursor ) {
            cursor.close();
        }
        return cityName;
    }
*/
}
