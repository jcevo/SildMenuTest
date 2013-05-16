package com.alvin.ui;

import com.alvin.api.adapter.DragAdapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

public class DragGridView extends GridView {
	private ImageView dragImageView;
	private int dragSrcPosition;
	private int dragPosition;

	// x,y坐标的计算
	private int dragPointX;
	private int dragPointY;
	private int dragOffsetX;
	private int dragOffsetY;

	private WindowManager windowManager;
	private WindowManager.LayoutParams windowParams;
	private int x, y;
	private int scaledTouchSlop;

	private int upScrollBounce;
	private int downScrollBounce;

	public DragAdapter ma;

	public MotionEvent ev;

	public DragGridView(Context context, AttributeSet set) {
		super(context, set);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		/*
		 * 使用时发现MotionEvent貌似没法实例化，而这里弃用了
		 * onInterceptionEvent方法，所以这里先把MotionEvent事件初始化
		 */
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			x = (int) event.getX();
			y = (int) event.getY();
			this.ev = event;
		}

		if (dragImageView != null && dragPosition != INVALID_POSITION) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_UP:
				int upX = (int) event.getX();
				int upY = (int) event.getY();
				stopDrag();
				onDrop(upX, upY);
				break;

			case MotionEvent.ACTION_MOVE:
				int moveX = (int) event.getX();
				int moveY = (int) event.getY();
				onDrag(moveX, moveY);
				break;

			default:
				break;
			}
			return super.onTouchEvent(event);
		}
		return super.onTouchEvent(event);
	}

	public boolean start(int position) {
		/*
		 * 由于屏幕的敏感度很高，长按的时候经常会产生ACTION_MOVE的事件， 所以判断时加上
		 */
		if (ev.getAction() == MotionEvent.ACTION_DOWN
				|| ev.getAction() == MotionEvent.ACTION_MOVE) {

			dragSrcPosition = dragPosition = position;

			if (dragPosition == AdapterView.INVALID_POSITION) {
				return false;
			}

			ViewGroup itemView = (ViewGroup) getChildAt(dragPosition - getFirstVisiblePosition());

			dragPointX = x - itemView.getLeft();
			dragPointY = y - itemView.getTop();

			dragOffsetX = (int) (ev.getRawX() - x);
			dragOffsetY = (int) (ev.getRawY() - y);

			// 如果选中拖动图标
			if (itemView != null) {

				upScrollBounce = Math.min(y - scaledTouchSlop, getHeight() / 4);
				downScrollBounce = Math.max(y + scaledTouchSlop,
						getHeight() * 3 / 4);

				itemView.setDrawingCacheEnabled(true);
				Bitmap bm = Bitmap.createBitmap(itemView.getDrawingCache());
				startDrag(bm, x, y);
			}
			return false;
		}
		return false;
	}

	public void startDrag(Bitmap bm, int x, int y) {
		stopDrag();

		windowParams = new WindowManager.LayoutParams();
		windowParams.gravity = Gravity.TOP | Gravity.LEFT;
		windowParams.x = x - dragPointX + dragOffsetX-20;
		windowParams.y = y - dragPointY + dragOffsetY-20;
		windowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		windowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		windowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
		windowParams.format = PixelFormat.TRANSLUCENT;
		windowParams.windowAnimations = 0;

		ImageView imageView = new ImageView(getContext());
		imageView.setImageBitmap(bm);

		windowManager = (WindowManager) getContext().getSystemService("window");
		windowManager.addView(imageView, windowParams);

		dragImageView = imageView;
	}

	public void stopDrag() {
		if (dragImageView != null) {
			windowManager.removeView(dragImageView);
			dragImageView = null;
		}
	}

	public void onDrag(int x, int y) {
		if (dragImageView != null) {
			windowParams.alpha = 0.8f;
			windowParams.x = x - dragPointX + dragOffsetX;
			windowParams.y = y - dragPointY + dragOffsetY;
			windowManager.updateViewLayout(dragImageView, windowParams);
		}

		int tempPosition = pointToPosition(x, y);
		if (tempPosition != AdapterView.INVALID_POSITION) {
			dragPosition = tempPosition;
		}

		// 滚动
		if (y < upScrollBounce || y > downScrollBounce) {
			// 使用setSelection来实现滚动
			setSelection(dragPosition);
		}
	}

	public void onDrop(int x, int y) {

		// 为了避免滑动到分割线的时候，返回-1的问题
		int tempPosition = pointToPosition(x, y);
		if (tempPosition != AdapterView.INVALID_POSITION) {
			dragPosition = tempPosition;
		}
	/*	System.out.println("*******y:"+y);
		System.out.println("*******top:"+this.getTop());
		System.out.println("*******bottom:"+this.getBottom());
		// 超出边界处理
		if (y < getChildAt(0).getTop()) {
			// 超出上边界
			dragPosition = 0;
		} else if ((y > getChildAt(getChildCount() - 1).getTop() && x > getChildAt(
						getChildCount() - 1).getRight())) {
			dragPosition = getCount() - 1;
		}else if(y > getChildAt(getChildCount() - 1).getBottom()){//超出下边界
			System.out.println("********超出下边界");
			ma = (DragAdapter) this.getAdapter();
			ma.remove(dragSrcPosition);
			return;
		}*/
		int top = this.getTop();
		int bottom = this.getBottom();
		int lastRightX  =  getChildAt(getChildCount() - 1).getRight();
		int lastTop = getChildAt(getChildCount() - 1).getTop();
		if(y<top){
			dragPosition = 0;
		}else if(y>lastTop&&y<bottom&&x>lastRightX){
			dragPosition = getCount() - 1;
		}else if(y>bottom){
			ma = (DragAdapter) this.getAdapter();
			ma.remove(dragSrcPosition);
			return;
		}

		// 数据交换
		if (dragPosition != dragSrcPosition && dragPosition > -1
				&& dragPosition < getCount()) {

			ma = (DragAdapter) this.getAdapter();
			ma.exchangeData(dragSrcPosition, dragPosition);
		}

	}

}
