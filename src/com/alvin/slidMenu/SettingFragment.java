package com.alvin.slidMenu;

import com.alvin.api.abstractClass.BaseFragment;
import com.alvin.api.activity.common.CustomChannelActivity;
import com.alvin.api.activity.common.LoginActivity;
import com.alvin.api.config.Env;
import com.alvin.common.R;
import com.alvin.common.utils.AnimationUtils;
import com.alvin.common.utils.DisplayUtils;
import com.alvin.slidMenu.MainSlidingActivity.WindowFocusChangedListener;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * 设置
 * 
 * @author poble
 * 
 */
public class SettingFragment extends BaseFragment {

	private FrameLayout backLayout; // 返回
	private TextView articleSearch; // 文章搜索
	private TextView offLineDownload; // 离线下载
	private LinearLayout loginPconline; // 登录太平洋通行证
	private LinearLayout loginQQ;// 登录QQ
	private LinearLayout loginSina;// 登录新浪微博
	private TextView textSmall;// 字体小
	private TextView textMiddle;// 字体中
	private TextView textBig;// 字体大
	private ImageView textChoiceImage;// 字体大小被选择后的背景
	private TextView imageNone;// 无图
	private TextView imageSmall;// 小图
	private TextView imageBig;// 大图
	private ImageView imageChoiceImage;// 图片大小被选择后的背景
	private TextView nightOn;// 开启夜间模式
	private TextView nightOff;// 关闭夜间模式
	private ImageView nightChoiceImage;// 夜间模式选择后的背景
	private TextView offlineDownloaOn;// 自动离线下载开启
	private TextView offlineDownloaOff;// 自动离线下载关闭
	private ImageView offlineDownloaChoiceImage;// 自动离线下载选择后的背景
	private TextView pushOn;// 推送开启
	private TextView pushOff;// 推送关闭
	private ImageView pushChoiceImage;// 推送模式选择后的背景
	private TextView fullScreenOn;// 开启全屏浏览
	private TextView fullScreenOff;// 关闭全屏浏览
	private ImageView fullScreenChoiceImage;// 全屏浏览模式选择后的背景
	private TextView guestureOn;// 开启手势
	private TextView guestureOff;// 关闭手势
	private ImageView guestureChoiceImage;// 手势模式选择后的背景
	private TextView defineColumn;// 定义栏目
	private TextView cleanCache;// 清除缓存
	private TextView aboutUs;// 关于我
	private TextView evaluate;// 给个评价
	private ImageView appZaker;// ZAKER
	private ImageView appFf;// 锋锋网
	private ImageView appGq;// 搞趣网
	private ImageView appTbt;// 同步推

	private boolean first;// onWindowFocusChanged中设置页面中所有功能“按钮”位置参数的开关（只打开一次）
	private float switchRate;// 功能开关移动的相对位置
	private boolean nightSwitch;// 夜间开关
	private boolean offlineDownloadSwitch;// 自动离线下载开关
	private boolean pushSwitch;// 推送开关
	private boolean fullScreenSwitch;// 全屏浏览开关
	private boolean guestureSwitch;// 手势开关
	float startText = 0;// 字体大小开关动画的起始值fromX
	float startImage = 0;// 2G/3G网络加载开关动画的起始值fromX

	private MainSlidingActivity mainActivity;

	public SettingFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mainActivity = (MainSlidingActivity) getActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (container == null) {
			return null;
		}
		View layout = inflater.inflate(R.layout.setting_main, container, false);
		initView(layout);
		viewSetClick();
		setEnabled();
		return layout;
	}

	@Override
	public void onResume() {
		super.onResume();
		MainSlidingActivity.windowFocusChangedListener = new WindowFocusChangedListener() {

			@Override
			public void onWindowFocusChanged() {
				if (!first) {
					first = !first;
					setAllSwitchViewLayparam();
					float w1 = nightOn.getWidth();
					float w2 = nightChoiceImage.getWidth();
					switchRate = w1 / w2;
				}
			}

		};
	}

	/**
	 * 设置页面中所有功能开关“按钮”位置参数
	 */
	private void setAllSwitchViewLayparam() {
		int margin = (nightOn.getWidth() - nightChoiceImage.getWidth()) / 2;
		setSwitchView(nightChoiceImage,
				Gravity.CENTER_VERTICAL | Gravity.RIGHT, margin);
		setSwitchView(offlineDownloaChoiceImage, Gravity.CENTER_VERTICAL
				| Gravity.LEFT, margin);
		setSwitchView(pushChoiceImage, Gravity.CENTER_VERTICAL | Gravity.LEFT,
				margin);
		setSwitchView(fullScreenChoiceImage, Gravity.CENTER_VERTICAL
				| Gravity.LEFT, margin);
		setSwitchView(guestureChoiceImage, Gravity.CENTER_VERTICAL
				| Gravity.LEFT, margin);
	}

	/**
	 * 为“字体大小”和“2G/3G”网络加载做起始焦点状态处理（这两项的“按钮”直接用焦点状态做开关)
	 */
	private void setEnabled() {
		textSmall.setEnabled(true);
		textMiddle.setEnabled(false);
		textBig.setEnabled(true);
		textChoiceImage.setEnabled(false);

		imageNone.setEnabled(true);
		imageSmall.setEnabled(false);
		imageBig.setEnabled(true);
		imageChoiceImage.setEnabled(false);

	}

	/**
	 * 设置功能开关“按钮”背景的位置，让其在“开启”或“关闭”端均居中
	 * 
	 * @param view
	 * @param gravity
	 * @param margin
	 */
	private void setSwitchView(View view, int gravity, int margin) {
		FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		flp.gravity = gravity;
		if (gravity == (Gravity.LEFT | Gravity.CENTER_VERTICAL)) {
			flp.leftMargin = margin;
		} else if (gravity == (Gravity.RIGHT | Gravity.CENTER_VERTICAL)) {
			flp.rightMargin = margin;
		}
		view.setLayoutParams(flp);
	}

	/**
	 * 初始化view
	 * 
	 * @param layout
	 */
	private void initView(View layout) {
		backLayout = (FrameLayout) layout.findViewById(R.id.setting_back);
		articleSearch = (TextView) layout
				.findViewById(R.id.setting_article_search);
		articleSearch.setText(mainActivity.getResources().getString(
				R.string.setting_article_search));
		offLineDownload = (TextView) layout
				.findViewById(R.id.setting_offline_download);
		offLineDownload.setText(mainActivity.getResources().getString(
				R.string.setting_offline_download));
		TextView login = (TextView) layout.findViewById(R.id.setting_login);
		login.setText(mainActivity.getResources().getString(
				R.string.setting_login));
		loginPconline = (LinearLayout) layout
				.findViewById(R.id.setting_login_pcoline);
		TextView shareBinding = (TextView) layout
				.findViewById(R.id.setting_share_binding);
		shareBinding.setText(mainActivity.getResources().getString(
				R.string.setting_share_binding));
		loginQQ = (LinearLayout) layout
				.findViewById(R.id.setting_login_tencent_qq);
		loginSina = (LinearLayout) layout
				.findViewById(R.id.setting_login_sina_wibo);
		TextView textSize = (TextView) layout
				.findViewById(R.id.setting_text_size);
		textSize.setText(mainActivity.getResources().getString(
				R.string.setting_text_size));
		textSmall = (TextView) layout.findViewById(R.id.setting_text_small);
		textMiddle = (TextView) layout.findViewById(R.id.setting_text_middle);
		textBig = (TextView) layout.findViewById(R.id.setting_text_big);

		textChoiceImage = (ImageView) layout
				.findViewById(R.id.setting_text_size_choice_background);
		// 设置textChoiceImage的宽度（“字体大小”功能开关所在线性布局的宽度的1/3），并且居中，默认选择“中”
		// 注意单位都转换为px
		FrameLayout.LayoutParams textChoiceImageLP = new FrameLayout.LayoutParams(
				(Env.screenWidth - DisplayUtils.convertDIP2PX(mainActivity, 30)) / 3,
				LayoutParams.WRAP_CONTENT);
		textChoiceImageLP.gravity = Gravity.CENTER;
		textChoiceImage.setLayoutParams(textChoiceImageLP);
		TextView netowork = (TextView) layout
				.findViewById(R.id.setting_image_size);
		netowork.setText(mainActivity.getResources().getString(
				R.string.setting_network));
		imageNone = (TextView) layout.findViewById(R.id.setting_non_image);
		imageSmall = (TextView) layout.findViewById(R.id.setting_small_image);
		imageBig = (TextView) layout.findViewById(R.id.setting_big_image);
		
		imageChoiceImage = (ImageView) layout
				.findViewById(R.id.setting_image_choice_background);
		// 设置imageChoiceImage的宽度（“字体大小”功能开关所在线性布局的宽度的1/3），并且居中，默认选择“中”
		// 注意单位都转换为px
		FrameLayout.LayoutParams imageChoiceImageLP = new FrameLayout.LayoutParams(
				(Env.screenWidth - DisplayUtils.convertDIP2PX(mainActivity, 30)) / 3, LayoutParams.WRAP_CONTENT);
		imageChoiceImageLP.gravity = Gravity.CENTER;
		imageChoiceImage.setLayoutParams(imageChoiceImageLP);

		nightOn = (TextView) layout.findViewById(R.id.setting_night_mode_on);
		nightOff = (TextView) layout.findViewById(R.id.setting_night_mode_off);
		nightChoiceImage = (ImageView) layout
				.findViewById(R.id.setting_night_mode_choice_background);

		offlineDownloaOn = (TextView) layout
				.findViewById(R.id.setting_auto_offline_download_on);
		offlineDownloaOff = (TextView) layout
				.findViewById(R.id.setting_auto_offline_download_off);
		offlineDownloaChoiceImage = (ImageView) layout
				.findViewById(R.id.setting_auto_offline_download_choice_background);

		pushOn = (TextView) layout.findViewById(R.id.setting_push_on);
		pushOff = (TextView) layout.findViewById(R.id.setting_push_off);
		pushChoiceImage = (ImageView) layout
				.findViewById(R.id.setting_push_choice_background);

		fullScreenOn = (TextView) layout
				.findViewById(R.id.setting_full_screen_on);
		fullScreenOff = (TextView) layout
				.findViewById(R.id.setting_full_screen_off);
		fullScreenChoiceImage = (ImageView) layout
				.findViewById(R.id.setting_full_screen_choice_background);
		guestureOn = (TextView) layout.findViewById(R.id.setting_gusture_on);
		guestureOff = (TextView) layout.findViewById(R.id.setting_gusture_off);
		guestureChoiceImage = (ImageView) layout
				.findViewById(R.id.setting_gusture_choice_background);

		defineColumn = (TextView) layout
				.findViewById(R.id.setting_define_column);
		defineColumn.setText(mainActivity.getResources().getString(
				R.string.setting_define_column));
		cleanCache = (TextView) layout.findViewById(R.id.setting_clean_cache);
		cleanCache.setText(mainActivity.getResources().getString(
				R.string.setting_clean_cache));
		aboutUs = (TextView) layout.findViewById(R.id.setting_about_us);
		aboutUs.setText(mainActivity.getResources().getString(
				R.string.setting_about_us));
		evaluate = (TextView) layout.findViewById(R.id.setting_evaluate);
		evaluate.setText(mainActivity.getResources().getString(
				R.string.setting_evaluate));
		TextView qualityApp = (TextView) layout
				.findViewById(R.id.setting_quality_app);
		qualityApp.setText(mainActivity.getResources().getString(
				R.string.setting_quality_app));

		appZaker = (ImageView) layout
				.findViewById(R.id.setting_quality_app_zaker);
		appFf = (ImageView) layout.findViewById(R.id.setting_quality_app_ff);
		appGq = (ImageView) layout.findViewById(R.id.setting_quality_app_gq);
		appTbt = (ImageView) layout.findViewById(R.id.setting_quality_app_tbt);

	}

	/**
	 * 绑定点击事件
	 */
	private void viewSetClick() {
		ViewOnClickListener onClickListener = new ViewOnClickListener();
		backLayout.setOnClickListener(onClickListener);
		articleSearch.setOnClickListener(onClickListener);
		offLineDownload.setOnClickListener(onClickListener);
		loginPconline.setOnClickListener(onClickListener);
		loginQQ.setOnClickListener(onClickListener);
		loginSina.setOnClickListener(onClickListener);
		textSmall.setOnClickListener(onClickListener);
		textMiddle.setOnClickListener(onClickListener);
		textBig.setOnClickListener(onClickListener);
		textChoiceImage.setOnClickListener(onClickListener);
		imageNone.setOnClickListener(onClickListener);
		imageSmall.setOnClickListener(onClickListener);
		imageBig.setOnClickListener(onClickListener);
		imageChoiceImage.setOnClickListener(onClickListener);
		nightOn.setOnClickListener(onClickListener);
		nightOff.setOnClickListener(onClickListener);
		nightChoiceImage.setOnClickListener(onClickListener);
		offlineDownloaOn.setOnClickListener(onClickListener);
		offlineDownloaOff.setOnClickListener(onClickListener);
		offlineDownloaChoiceImage.setOnClickListener(onClickListener);
		pushOn.setOnClickListener(onClickListener);
		pushOff.setOnClickListener(onClickListener);
		pushChoiceImage.setOnClickListener(onClickListener);
		fullScreenOn.setOnClickListener(onClickListener);
		fullScreenOff.setOnClickListener(onClickListener);
		fullScreenChoiceImage.setOnClickListener(onClickListener);
		guestureOn.setOnClickListener(onClickListener);
		guestureOff.setOnClickListener(onClickListener);
		guestureChoiceImage.setOnClickListener(onClickListener);
		defineColumn.setOnClickListener(onClickListener);
		cleanCache.setOnClickListener(onClickListener);
		aboutUs.setOnClickListener(onClickListener);
		evaluate.setOnClickListener(onClickListener);
		appZaker.setOnClickListener(onClickListener);
		appFf.setOnClickListener(onClickListener);
		appGq.setOnClickListener(onClickListener);
		appTbt.setOnClickListener(onClickListener);
	}

	/**
	 * 处理各个View的点击事件
	 * 
	 * @author xjzhao
	 * 
	 */
	class ViewOnClickListener implements OnClickListener {
		AnimationUtils.AinmParams params = new AnimationUtils.AinmParams();
		Intent intent = new Intent();

		@Override
		public void onClick(View v) {
			int id = v.getId();
			if (id == R.id.setting_back) {// 返回
				mainActivity.resumeSlidingMenuState();
				mainActivity.showContent();
			} else if (id == R.id.setting_article_search) {// 文章搜索

			} else if (id == R.id.setting_offline_download) {// 离线下载

			} else if (id == R.id.setting_login_pcoline) {// 登录太平洋通行证
				Intent intent = new Intent(SettingFragment.this.getActivity(),LoginActivity.class);
				startActivity(intent);

			} else if (id == R.id.setting_login_tencent_qq) {// 登录QQ

			} else if (id == R.id.setting_login_sina_wibo) {// 登录新浪微博

			} else if (id == R.id.setting_text_small) {// 字体小
				if (textSmall.isEnabled()) {
					textSmall.setEnabled(false);
					textMiddle.setEnabled(true);
					textBig.setEnabled(true);
					textChoiceImage.setEnabled(true);
					params.setView(textChoiceImage).setFromX(startText)
							.setToX(-1f);
					AnimationUtils.setTranslateAnimation(params);
					startText = -1;
				}

			} else if (id == R.id.setting_text_middle) {// 字体中
				if (textMiddle.isEnabled()) {
					textSmall.setEnabled(true);
					textMiddle.setEnabled(false);
					textBig.setEnabled(true);
					textChoiceImage.setEnabled(true);
					params.setView(textChoiceImage).setFromX(startText)
							.setToX(0f);
					AnimationUtils.setTranslateAnimation(params);
					startText = 0;
				}
			} else if (id == R.id.setting_text_big) {// 字体大
				if (textBig.isEnabled()) {
					textSmall.setEnabled(true);
					textMiddle.setEnabled(true);
					textBig.setEnabled(false);
					textChoiceImage.setEnabled(true);
					params.setView(textChoiceImage).setFromX(startText)
							.setToX(1f);
					AnimationUtils.setTranslateAnimation(params);
					startText = 1;
				}
			} else if (id == R.id.setting_text_size_choice_background) {// 字体大“按钮“背景（因为按钮覆盖在“中”上，如果“按钮”和下层中的高度宽度相等时可对“中不做任何处理）
				textSmall.setEnabled(true);
				textMiddle.setEnabled(false);
				textBig.setEnabled(true);
				textChoiceImage.setEnabled(true);
				params.setView(textChoiceImage).setFromX(startText).setToX(0f);
				AnimationUtils.setTranslateAnimation(params);
				startText = 0;
			} else if (id == R.id.setting_non_image) {// 无图
				if (imageNone.isEnabled()) {
					imageNone.setEnabled(false);
					imageSmall.setEnabled(true);
					imageBig.setEnabled(true);
					imageChoiceImage.setEnabled(true);
					params.setView(imageChoiceImage).setFromX(startImage)
							.setToX(-1f);
					AnimationUtils.setTranslateAnimation(params);
					startImage = -1;
				}
			} else if (id == R.id.setting_small_image) {// 小图
				if (imageSmall.isEnabled()) {
					imageNone.setEnabled(true);
					imageSmall.setEnabled(false);
					imageBig.setEnabled(true);
					imageChoiceImage.setEnabled(true);
					params.setView(imageChoiceImage).setFromX(startImage)
							.setToX(0f);
					AnimationUtils.setTranslateAnimation(params);
					startImage = 0;
				}
			} else if (id == R.id.setting_big_image) {// 大图
				if (imageBig.isEnabled()) {
					imageNone.setEnabled(true);
					imageSmall.setEnabled(true);
					imageBig.setEnabled(false);
					imageChoiceImage.setEnabled(true);
					params.setView(imageChoiceImage).setFromX(startImage)
							.setToX(1f);
					AnimationUtils.setTranslateAnimation(params);
					startImage = 1;
				}
			} else if (id == R.id.setting_image_choice_background) {// 2g/3g网络加载“按钮“背景（同字体大“按钮“背景）
				if (imageChoiceImage.isEnabled()) {
					imageNone.setEnabled(true);
					imageSmall.setEnabled(false);
					imageBig.setEnabled(true);
					imageChoiceImage.setEnabled(true);
					params.setView(imageChoiceImage).setFromX(startImage)
							.setToX(0f);
					AnimationUtils.setTranslateAnimation(params);
					startImage = 0;
				}
			} else if (id == R.id.setting_night_mode_on) {// 开启夜间模式
				if (!nightSwitch) {
					nightSwitch = !nightSwitch;
					params.setView(nightChoiceImage).setFromX(0)
							.setToX(0 - switchRate);
					AnimationUtils.setTranslateAnimation(params);
				}

			} else if (id == R.id.setting_night_mode_off) {// 关闭夜间模式
				if (nightSwitch) {
					nightSwitch = !nightSwitch;
					params.setView(nightChoiceImage).setFromX(0 - switchRate)
							.setToX(0);
					AnimationUtils.setTranslateAnimation(params);
				}
			} else if (id == R.id.setting_night_mode_choice_background) {// 夜间模式“按钮“背景
				if (nightSwitch) {
					nightSwitch = !nightSwitch;
					params.setView(nightChoiceImage).setFromX(0 - switchRate)
							.setToX(0);
					AnimationUtils.setTranslateAnimation(params);
				}

			} else if (id == R.id.setting_auto_offline_download_on) {// 自动离线下载开启
				if (offlineDownloadSwitch) {
					offlineDownloadSwitch = !offlineDownloadSwitch;
					params.setView(offlineDownloaChoiceImage)
							.setFromX(switchRate).setToX(0);
					AnimationUtils.setTranslateAnimation(params);
				}

			} else if (id == R.id.setting_auto_offline_download_off) {// 自动离线下载关闭
				if (!offlineDownloadSwitch) {
					offlineDownloadSwitch = !offlineDownloadSwitch;
					params.setView(offlineDownloaChoiceImage).setFromX(0)
							.setToX(switchRate);
					AnimationUtils.setTranslateAnimation(params);
				}
			} else if (id == R.id.setting_auto_offline_download_choice_background) {// 自动离线下载“按钮“背景
				if (offlineDownloadSwitch) {
					offlineDownloadSwitch = !offlineDownloadSwitch;
					params.setView(offlineDownloaChoiceImage)
							.setFromX(switchRate).setToX(0);
					AnimationUtils.setTranslateAnimation(params);
				}
			} else if (id == R.id.setting_push_on) {// 推送开启
				if (pushSwitch) {
					pushSwitch = !pushSwitch;
					params.setView(pushChoiceImage).setFromX(switchRate)
							.setToX(0);
					AnimationUtils.setTranslateAnimation(params);
				}
			} else if (id == R.id.setting_push_off) {// 推送关闭
				if (!pushSwitch) {
					pushSwitch = !pushSwitch;
					params.setView(pushChoiceImage).setFromX(0)
							.setToX(switchRate);
					AnimationUtils.setTranslateAnimation(params);
				}
			} else if (id == R.id.setting_push_choice_background) {// 推送“按钮“背景
				if (pushSwitch) {
					pushSwitch = !pushSwitch;
					params.setView(pushChoiceImage).setFromX(switchRate)
							.setToX(0);
					AnimationUtils.setTranslateAnimation(params);
				}
			} else if (id == R.id.setting_full_screen_on) {// 开启全屏浏览
				if (fullScreenSwitch) {
					fullScreenSwitch = !fullScreenSwitch;
					params.setView(fullScreenChoiceImage).setFromX(switchRate)
							.setToX(0);
					AnimationUtils.setTranslateAnimation(params);
				}
			} else if (id == R.id.setting_full_screen_off) {// 关闭全屏浏览
				if (!fullScreenSwitch) {
					fullScreenSwitch = !fullScreenSwitch;
					params.setView(fullScreenChoiceImage).setFromX(0)
							.setToX(switchRate);
					AnimationUtils.setTranslateAnimation(params);
				}
			} else if (id == R.id.setting_full_screen_choice_background) {// 全屏浏览“按钮“背景
				if (fullScreenSwitch) {
					fullScreenSwitch = !fullScreenSwitch;
					params.setView(fullScreenChoiceImage).setFromX(switchRate)
							.setToX(0);
					AnimationUtils.setTranslateAnimation(params);
				}
			} else if (id == R.id.setting_gusture_on) {// 开启手势
				if (guestureSwitch) {
					guestureSwitch = !guestureSwitch;
					params.setView(guestureChoiceImage).setFromX(switchRate)
							.setToX(0);
					AnimationUtils.setTranslateAnimation(params);
				}
			} else if (id == R.id.setting_gusture_off) {// 关闭手势
				if (!guestureSwitch) {
					guestureSwitch = !guestureSwitch;
					params.setView(guestureChoiceImage).setFromX(0)
							.setToX(switchRate);
					AnimationUtils.setTranslateAnimation(params);
				}
			} else if (id == R.id.setting_gusture_choice_background) {// 手势“按钮“背景
				if (guestureSwitch) {
					guestureSwitch = !guestureSwitch;
					params.setView(guestureChoiceImage).setFromX(switchRate)
							.setToX(0);
					AnimationUtils.setTranslateAnimation(params);
				}
			} else if (id == R.id.setting_define_column) {// 定义栏目
				Intent intentColumn = new Intent(getActivity(),
						CustomChannelActivity.class);
				startActivityForResult(intentColumn, 5);
			} else if (id == R.id.setting_clean_cache) {// 清除缓存

			} else if (id == R.id.setting_about_us) {// 关于我

			} else if (id == R.id.setting_evaluate) {// 给个评价

			} else if (id == R.id.setting_quality_app_zaker) {// ZAKER
				intent.setData(Uri.parse("http://stackoverflow.com/"));
				intent.setAction(Intent.ACTION_VIEW);
				startActivity(intent);
			} else if (id == R.id.setting_quality_app_ff) {// 锋锋网
				intent.setData(Uri.parse("http://stackoverflow.com/"));
				intent.setAction(Intent.ACTION_VIEW);
				startActivity(intent);
			} else if (id == R.id.setting_quality_app_gq) {// 搞趣网
				intent.setData(Uri.parse("http://stackoverflow.com/"));
				intent.setAction(Intent.ACTION_VIEW);
				startActivity(intent);
			} else if (id == R.id.setting_quality_app_tbt) {// 同步推
				intent.setData(Uri.parse("http://stackoverflow.com/"));
				intent.setAction(Intent.ACTION_VIEW);
				startActivity(intent);
			}

		}
	}

}
