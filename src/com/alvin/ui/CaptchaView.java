package com.alvin.ui;

import com.alvin.common.R;
import com.alvin.common.utils.HttpUtils;
import com.alvin.common.utils.HttpUtils.HttpMessage;

import org.apache.http.impl.cookie.BasicClientCookie;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CaptchaView extends FrameLayout implements OnClickListener
{
	/*网络状态异常标识*/
	private static final int NET_STATE_ABNORMAL = 1;
	/*网络状态:异常时错误提示信息*/
	private static final String NET_STATE_ERROR = "网络异常,点击重试";
	/*网络状态:网络读取时提示信息*/
	private static final String NET_STATE_READING = "读取中...";
	/*网络图片URL地址*/
	private String imageURL = "";
	
	
	
	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}

	private ProgressBar progressBar = new ProgressBar(this.getContext(),null,android.R.attr.progressBarStyleSmallInverse);
	private TextView textView = new TextView(getContext());
	private ImageView imageView = new ImageView(getContext());
	private LinearLayout linearLayout = new LinearLayout(getContext());
	private BasicClientCookie cookie;
	
	/*设置验证码框图片的Handler*/
	private Handler imageHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			if(msg.arg1 != NET_STATE_ABNORMAL)
			{
				BitmapDrawable bd =(BitmapDrawable)((HashMap)msg.obj).get("BitmapDrawable");
				cookie = (BasicClientCookie)(((HashMap)msg.obj).get("Cookie"));
				imageView.setImageDrawable(bd);
				/*拿到网络图片后，就覆盖默认显示的图片*/
				imageView.setVisibility(View.VISIBLE);
			}
			else
			{
				progressBar.setVisibility(View.GONE);
				textView.setText(NET_STATE_ERROR);
			}
		};
	};
	
	
	/*开启新线程读取网络图片*/
	private void newImageThread()
	{
		new Thread(new Runnable()
		{
			@Override
			public void run() 
			{
				
				//String URL = "http://captcha.pclady.com.cn/captcha/v.jpg";
				HttpMessage hm = new HttpMessage();
				hm.setUrl(imageURL);
				hm.setRequestMethod(HttpMessage.REQUEST_METHOD_GET);
				BufferedInputStream bufferedInputStream = null;
				Message msg = new Message();
				try 
				{
					//Thread.sleep(5000);
					hm=HttpUtils.invoke(hm);
					bufferedInputStream = new BufferedInputStream(hm.getResponseInputStream());
					Bitmap bt = BitmapFactory.decodeStream(bufferedInputStream);
					BitmapDrawable bd = new BitmapDrawable(bt);
					Map<String,Object> m = new HashMap<String,Object>();
					m.put("BitmapDrawable", bd);
					m.put("Cookie",hm.getResponseCookie("captcha"));
					msg.obj=m;
					imageHandler.sendMessage(msg);
				} catch (Exception e) 
				{
					msg.arg1=NET_STATE_ABNORMAL;
					imageHandler.sendMessage(msg);
					e.printStackTrace();
				}finally
				{
					try 
					{
						if(bufferedInputStream!=null)
						bufferedInputStream.close();
					} catch (IOException e) 
					{
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
	
	/*初始化属性*/
	private void initAttribute()
	{
		textView.setText(NET_STATE_READING);
		imageView.setVisibility(View.GONE);
		newImageThread();
	}
	


	public CaptchaView(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		
		/*这里是通过AttributeSet对象根据XML传入的属性名找到资源的
		 * 
		 * */
		/*int resouceId = -1;
		resouceId = attrs.getAttributeResourceValue(null, "imageURL", 0);
		if (resouceId > 0)
	       imageURL = context.getResources().getText(resouceId).toString();*/
		
		TypedArray typeArray = context.obtainStyledAttributes(attrs, R.styleable.CaptchaView);
		//imageURL = typeArray.getString(R.styleable.CaptchaView_imageURL);
		//float textSize = typeArray.getDimension(R.styleable.CaptchaView_textSize, 36);
		typeArray.recycle();  
		
		initAttribute();
		linearLayout.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
		linearLayout.addView(progressBar);
		linearLayout.addView(textView);
		imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,ViewGroup.LayoutParams.FILL_PARENT));
		addView(linearLayout);
		addView(imageView);
		CaptchaView.this.setOnClickListener(CaptchaView.this);
	}
	
	@Override
	public void onClick(View v) 
	{
		/*图片正常显示，正常点击*/
		if(imageView.getVisibility() == View.VISIBLE )
		{
			imageView.setVisibility(View.GONE);
			newImageThread();
//			System.out.println("点击成功!");
		}
		/*网络异常，正常点击*/
		else if(textView.getText().equals(NET_STATE_ERROR))
		{
			progressBar.setVisibility(View.VISIBLE);
			textView.setText(NET_STATE_READING);
			newImageThread();
		}
		/*网络读取中,点击无效*/
		else {
//		    System.out.println("点击失败!");
        }
	}
	
	/**
	 * 更新验证框图片
	 * 
	 * @param v
	 */
	public void updateImage(View v)
	{
		onClick(v);
	}
	
	/**
	 * @return	服务器发送的Cookie
	 */
	public BasicClientCookie getCookie() {
		return cookie;
	}
	
	/*如果此组件别人是用纯JAVA代码NEW出来,则Android默认调用此构造器*/
	public CaptchaView(Context context) 
	{
		super(context);
	}
	
	public CaptchaView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
}
