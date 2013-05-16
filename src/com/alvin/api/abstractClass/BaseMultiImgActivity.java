package com.alvin.api.abstractClass;

import cn.com.pcgroup.android.bitmap.util.ImageFetcher;
import cn.com.pcgroup.android.bitmap.util.ImageFetcherUtils;

import android.os.Bundle;
/**
 * 多图下载的Activity继承此类 如果需要设置buildParams,在子类super.onCreate(savedInstanceState);之前进行设置
 * @author Liyang
 *
 */
public class BaseMultiImgActivity extends BaseFragmentActivity {
    
    protected ImageFetcher imageFetcher;
    protected ImageFetcherUtils.BuildParams buildParams;
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageFetcher = ImageFetcherUtils.instanceImageFecher(this,
                getSupportFragmentManager(), buildParams);
    }
    
    
    @Override
    protected void onPause() {
        super.onPause();
        ImageFetcherUtils.onPause(imageFetcher);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        ImageFetcherUtils.onResume(imageFetcher);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImageFetcherUtils.onDestroy(imageFetcher);
    }
}
