package com.alvin.slidMenu;
import com.alvin.api.abstractClass.BaseFragment;
import com.alvin.common.R;
import com.alvin.ui.SimpleToast;
import com.viewpagerindicator.TabPageIndicator;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 个人中心
 * @author Liyang
 *
 */
public class PersonalCenterFragment extends BaseFragment{
//    private ViewPager viewPager;
//    private PagerAdapter pagerAdapter;
//    private TabPageIndicator tabPageIndicator;
//    private BaseFragment baseFragment;
//    
//    private CircularImage loginImg;       //默认登录头像
//    private FrameLayout loginLayout;      //默认头像的布局
//    private LinearLayout otherLayout;
//    private ImageView sinaLogin;      //新浪登录
//    private ImageView tencentLogin;   //腾讯登录
//    private ImageView pconlineLogin;  //太平洋登录
//    private LinearLayout funMenu;     //功能按钮布局（个人设置、意见反馈）
//    private PopupWindow popWindow;
//    
//    private TextView personalSetting; //个人设置
//    private TextView feedBack;        //意见反馈
//    
//    private ImageView arrowImg;      //返回箭头 
//    private String[] titles;         //切换卡标题
//    private MainSlidingActivity mainActivity;
//    
//    public PersonalCenterFragment(){}
//    
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        pagerAdapter = new PagerAdapter(getChildFragmentManager());
//        mainActivity = (MainSlidingActivity) getActivity();
//        titles = mainActivity.getResources().getStringArray(R.array.personal_center_channel);
//    }
//    
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//            Bundle savedInstanceState) {
//        if (container == null){
//            return null;
//        }
//        Context ctxWithTheme = new ContextThemeWrapper(
//                getActivity().getApplicationContext(),
//                R.style.Theme_PersonalCenterIndicator);
//        LayoutInflater localLayoutInflater = inflater
//                .cloneInContext(ctxWithTheme);
//        View layout = localLayoutInflater.inflate(R.layout.personal_center_main, container, false); 
//        viewPager = (ViewPager) layout.findViewById(R.id.viewpager);
//        viewPager.setAdapter(pagerAdapter);
//        tabPageIndicator = (TabPageIndicator) layout.findViewById(R.id.indicator);
//        tabPageIndicator.setViewPager(viewPager);
//        viewPager.setCurrentItem(2);
//        initView(layout);
//        return layout;
//    }   
//    
//    //初始化组建
//    private void initView(View view){
//        arrowImg = (ImageView) view.findViewById(R.id.personal_center_arrow);
//        loginImg = (CircularImage) view.findViewById(R.id.app_user_avatar);
//        sinaLogin = (ImageView) view.findViewById(R.id.personal_center_sina_login);
//        tencentLogin = (ImageView) view.findViewById(R.id.personal_center_tencent_login);
//        pconlineLogin = (ImageView) view.findViewById(R.id.personal_center_pconline_login);
//        funMenu = (LinearLayout) view.findViewById(R.id.personal_center_menu);
//        loginLayout = (FrameLayout) view.findViewById(R.id.personal_center_login_layout);
//        otherLayout = (LinearLayout) view.findViewById(R.id.persoanl_other_login);
//        //初始化登录显示
//        initLoginView();
//        
//        //注册按钮监听器
//        arrowImg.setOnClickListener(clickListener);
//        funMenu.setOnClickListener(clickListener);
//        loginLayout.setOnClickListener(clickListener);
//        sinaLogin.setOnClickListener(clickListener);
//        tencentLogin.setOnClickListener(clickListener);
//        pconlineLogin.setOnClickListener(clickListener);
//    }
//    
//    //初始化登录方式
//    private void initLoginView(){
//        loginLayout.setVisibility(View.VISIBLE);
//        otherLayout.setVisibility(View.GONE);
//    }
//    
//    View.OnClickListener clickListener = new OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            int viewId = v.getId();
//            if(viewId==arrowImg.getId()){   //返回按钮
//                MainSlidingActivity.changeSlidingMenuState(MainSlidingActivity.SLIDINGMENUSTATE);
//                mainActivity.showContent();
//            }else if(viewId==loginLayout.getId()){//默认登录按钮
//                loginLayout.setVisibility(View.GONE);
//                otherLayout.setVisibility(View.VISIBLE);
//            }else if(viewId==sinaLogin.getId()){ //新浪登录
//                SimpleToast.show(getActivity(), "新浪登录", Toast.LENGTH_SHORT);
//            }else if(viewId==tencentLogin.getId()){//腾讯登录
//                SimpleToast.show(getActivity(), "腾讯登录", Toast.LENGTH_SHORT);
//            }else if(viewId==pconlineLogin.getId()){//太平洋登录
//                SimpleToast.show(getActivity(), "太平洋登录", Toast.LENGTH_SHORT);
//            }else if(viewId==funMenu.getId()){  //个人设置、意见反馈
//                // 获取自定义布局文件poplayout.xml的视图
//                LayoutInflater layoutInflater = (LayoutInflater) (getActivity()).getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                View popview = layoutInflater.inflate(R.layout.personal_funmenu_view, null);
//                popWindow = SimplePopupWindow.createPopupWindow(getActivity(), popview); 
//                
//                if(popWindow.isShowing()){
//                    popWindow.dismiss();
//                }else{
//                    //规定弹窗的位置
//                    popWindow.showAtLocation(funMenu, Gravity.LEFT | Gravity.TOP,funMenu.getPaddingLeft(),funMenu.getBottom()*2);
//                }
//                personalSetting =(TextView) popview.findViewById(R.id.personal_funmenu_setting);
//                
//                personalSetting.setOnClickListener(new OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent intent = new Intent(getActivity(),PersonalModifyHead.class);
//                        startActivity(intent);
//                        getActivity().overridePendingTransition(R.anim.right_fade_in, R.anim.sham_translate);
//                        popWindow.dismiss();
//                    }
//                });
//    
//                feedBack = (TextView) popview.findViewById(R.id.personal_funmenu_feedback);
//                feedBack.setOnClickListener(new OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                    }
//                });
//            }else{
//                popWindow.dismiss();
//            }
//        }
//    };
//    
//    class PagerAdapter extends FragmentPagerAdapter {
//        public PagerAdapter(FragmentManager fm) {
//            super(fm);
//        }
//
//        @Override
//        public Fragment getItem(int position) {
//            if(getPageTitle(position).equals("论坛收藏")){
//                baseFragment = new PersonalBbsFavorites();
//            }else if(getPageTitle(position).equals("车型收藏")){
//                baseFragment = new PersonalCarModelFavorites();
//            }else{
//                baseFragment = new PersonalPostsFavorites();
//            }
//            return baseFragment;
//        }
//
//        @Override
//        public CharSequence getPageTitle(int position) {
//            return titles[position % titles.length];
//        }
//
//        @Override
//        public int getCount() {
//            return titles.length;
//        }
//    }

}
