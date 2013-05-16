package com.alvin.slidMenu;

import com.alvin.api.config.Env;

public class MainTabFragmentGenerator {
	public static MainTabFragment generatorMainTabFragment(int appId){
		switch(appId){
		case Env.MyApp:
			return new PcautoMainTabFragment();
//		case Env.APP_ID_PCONLINE:
//			return new PconlineMainTabFragment();
//		case Env.APP_ID_AUTOBBS:
//			return new AutoBbsMainTabFragment();
		}
		
		return null;
	}
}
