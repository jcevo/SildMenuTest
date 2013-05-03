package com.alvin.common.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BitmapUtils {
    
    public static Bitmap getScaleBitmap(Context context, int resId, int dstWidth, int dstHeight){
        Bitmap origialBitmap = BitmapFactory.decodeResource(context.getResources(), resId);
        Bitmap scaleBitmap = Bitmap.createScaledBitmap(origialBitmap, dstWidth, dstHeight, true);
        if(origialBitmap!=null&&!origialBitmap.isRecycled()){
            origialBitmap.recycle();
        }
        return scaleBitmap;
    }
    
    public static Bitmap getScaleBitmap(Bitmap origialBitmap, int dstWidth, int dstHeight){
        Bitmap scaleBitmap = Bitmap.createScaledBitmap(origialBitmap, dstWidth, dstHeight, true);
        if(origialBitmap!=null&&!origialBitmap.isRecycled()){
            origialBitmap.recycle();
        }
        return scaleBitmap;
    }
}
