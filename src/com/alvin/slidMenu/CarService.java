package com.alvin.slidMenu;

/**
 * 存放车型库中公用的或者需静态的量
 * @author xjzhao
 *
 */
public class CarService {

	/*让detailFragment在初次加载视图的时候消失（改值只在每次打开车型库模块的时候改变一次，直到下次打开前不在改变）*/
	public static boolean CAR_BRAND_DETAIL_FRAGMENT_FIRST = true;
	
	/*detailFragment是否可见*/
	public static boolean CAR_DETAIL_FRAGMENT_VISABLE = false;
	
//	/*detailFragment可见时有没有通过viewPager的indicator去过当前模块其他页面(有没有去过价格选车和条件选车）*/
//	public static boolean CAR_BRAND_DETAIL_FRAGMENT_GO_OTHER_FRAGMENT = false;
//	
//	/*改模块中viewPager是否可以滑动*/
//	public static boolean CAR_VIEWPAGER_SLIDE = true;
	
	/*判断viewpager当前所处页面的标志*/
	public static final int CAR_BRAND_FRAGMENT = 0;
	public static final int CAR_PRICE_FRAGMENT = 1;
	public static final int CAR_CONFITION_FRAGMENT = 2;
	
	/*判断条件选车页面上面gridview的标志*/
	public static final int CAR_CONDITION_UP_GRIDVIEW = 4;
	public static final int CAR_CONDITION_UP_SHAM_GRIDVIEW = 5;

}
