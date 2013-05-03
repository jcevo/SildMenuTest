package com.alvin.api.adapter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Gallery;

/**
 * 每次指滑动一张 项目名称：alvin_common 类名称：BigImageGalleryWhit1 类描述： 创建人：pc 创建时间：2012-3-21
 * 下午3:51:21 修改人：pc 修改时间：2012-3-21 下午3:51:21 修改备注：
 * 
 * @version
 * 
 */
public class BigImageGalleryWhit1 extends Gallery {

    public BigImageGalleryWhit1(Context context) {
        super(context);
    }

    public BigImageGalleryWhit1(Context paramContext,
            AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
    }

    public BigImageGalleryWhit1(Context paramContext,
            AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
    }

    private boolean isScrollingLeft(MotionEvent e1, MotionEvent e2) {
        return e2.getX() > e1.getX();
    }

    @Override
    public boolean onFling(MotionEvent paramMotionEvent1,
            MotionEvent paramMotionEvent2, float velocityX, float paramFloat2) {
        int kEvent;
        if (isScrollingLeft(paramMotionEvent1, paramMotionEvent2)) {
            // Check if scrolling left
            kEvent = KeyEvent.KEYCODE_DPAD_LEFT;

        } else {
            // Otherwise scrolling right
            kEvent = KeyEvent.KEYCODE_DPAD_RIGHT;
        }
        onKeyDown(kEvent, null);
        return super.onFling(paramMotionEvent1, paramMotionEvent2, 0,
                paramFloat2);

        // 每次只滑一张
        // return false;
    }
}
