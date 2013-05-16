package com.alvin.ui;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Adapter;
import android.widget.Gallery;

public class HeaderGallery extends Gallery {
	private Handler mHanler = null;//handerl
	private Runnable scrollRunable = null;//自动滚动runnable
	private final static int AUTO_SWITCH_TIME = 4000;
	public HeaderGallery(Context paramContext){
	   super(paramContext);
    }

	public HeaderGallery(Context paramContext, AttributeSet paramAttributeSet){
	    super(paramContext, paramAttributeSet);
	}

	public HeaderGallery(Context paramContext, AttributeSet paramAttributeSet, int paramInt){
	    super(paramContext, paramAttributeSet, paramInt);
	}

	private boolean isScrollingLeft(MotionEvent e1, MotionEvent e2)
	{  
	    return e2.getX() > e1.getX(); 
	}
	  
	
	// 用户按下触摸屏、快速移动后松开
	public boolean onFling(MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2, float paramFloat1, float paramFloat2){
		int kEvent;  
		if(isScrollingLeft(paramMotionEvent1, paramMotionEvent2)){ 
		    //Check if scrolling left     
			kEvent = KeyEvent.KEYCODE_DPAD_LEFT;  
		} else{ 
		     //Otherwise scrolling right    
			 kEvent = KeyEvent.KEYCODE_DPAD_RIGHT;
		}  
		onKeyDown(kEvent, null);  
		
	/*	

        float x1 = paramMotionEvent1.getX();
        float x2 = paramMotionEvent2.getX();
        float y1 = paramMotionEvent1.getY();
        float y2 = paramMotionEvent2.getY();
        
        if(((Math.abs(paramFloat1))>100) && (y2<y1)&&(x1>x2) && ((x1-x2) > 2*(y1-y2))){//左上下方
            return true;
        }else if((y2<y1)&&(x1>x2) && ((x1-x2) < (y1-y2))){//左上上方
            return false;
        }else if(((Math.abs(paramFloat1))>100) && (y2>y1)&&(x1>x2)  && ((x1-x2)>2*(y2-y1))){//左下上方
            return true;
        }else if((y2>y1)&&(x1>x2) && ((x1-x2)<(y2-y1))){//左下下方
            return false;
        }else if((x2>x1)&&(y1>y2)&&((x2-x1)<(y1-y2))){//右上上方
            return false;
        }else if(((Math.abs(paramFloat1))>100)&&(x2>x1)&&(y1>y2)&&((x2-x1)>2*(y1-y2))){//右上下方
            return true;
        }else if((x2>x1) && (y2>y1) && ((x2-x1)<(y2-y1))){  //右下下方"
            return false;
        }else if(((Math.abs(paramFloat1))>100)&&(x2>x1) && (y2>y1) && ((x2-x1)>2*(y2-y1))){  //右下上方
            return true;
        }
    */
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		return false;
	 }
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {//touch的时候，取消自动滚动
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			onPouse();
		}else if(event.getAction() == MotionEvent.ACTION_UP){
			onResume();
		}else if(event.getAction() == MotionEvent.ACTION_CANCEL){
			onResume();
		}
		return super.onTouchEvent(event);
	}
	 
	 //用户按下屏幕并拖动
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,float distanceY) {
		return super.onScroll(e1, e2, distanceX, distanceY);
	}
	
	public void setSelection(int position) {
		super.setSelection(position);
	};
	public interface GalleryChangeListener {
		public void scrollLeft(int currentPosition);
		public void scrollRight(int currentPosition);
	}
	//开启自动切换
	public void startAutoSwitc(){
		mHanler = new Handler(){
			public void handleMessage(android.os.Message msg) {
				Adapter  ada = HeaderGallery.this.getAdapter();
				if(null!=ada&&ada.getCount()>0){
					HeaderGallery.this.onScroll(null, null, 1, 0);
					HeaderGallery.this.onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT, null);  
				}
				
			};
		};
		scrollRunable = new Runnable() {
			
			@Override
			public void run() {
				onResume();
				mHanler.sendMessage(new Message());
			}
		};
		mHanler.postDelayed(scrollRunable,AUTO_SWITCH_TIME);
	}
	//暂停自动切换
	public void onPouse(){
		if(null!=mHanler&&null!=scrollRunable){
			mHanler.removeCallbacks(scrollRunable);
		}
	}
	//重新开启自动切换
	public void onResume(){
		if(null!=mHanler&&null!=scrollRunable){
			mHanler.removeCallbacks(scrollRunable);
			mHanler.postDelayed(scrollRunable, AUTO_SWITCH_TIME);
		}
	}
}
