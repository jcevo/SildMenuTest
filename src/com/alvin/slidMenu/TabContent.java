package com.alvin.slidMenu;


import android.content.Context;
import android.view.View;
import android.widget.TabHost.TabContentFactory;

public class TabContent implements TabContentFactory {
    
    private Context mContext;
    
    public TabContent(Context context){
        mContext = context;
    }
    
	@Override
	public View createTabContent(String tag) {
	    View v = new View(mContext);
        return v;
	}

}
