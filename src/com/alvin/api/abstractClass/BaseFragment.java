package com.alvin.api.abstractClass;

import android.os.Bundle;
import android.support.v4.app.Fragment;
/**
 * Fragment基类
 * @author Liyang
 *
 */
public class BaseFragment extends Fragment{
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    }
    

}
