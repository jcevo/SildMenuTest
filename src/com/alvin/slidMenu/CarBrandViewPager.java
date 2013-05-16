package com.alvin.slidMenu;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 定制ViewPager，实现可以控制其滑动效果
 * @author xjzhao
 *
 */
public class CarBrandViewPager extends ViewPager {

	private boolean enabled = true;
	public CarBrandViewPager(Context context) {
		super(context);
	}

	public CarBrandViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}


	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (enabled) {
			return super.onTouchEvent(event);
		}

		return false;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		if (enabled) {
			return super.onInterceptTouchEvent(event);
		}

		return false;
	}

	/**
	 * 设置viewpager滑动是否有效
	 * @param enabled
	 */
	public void setViewPagerSlidEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
