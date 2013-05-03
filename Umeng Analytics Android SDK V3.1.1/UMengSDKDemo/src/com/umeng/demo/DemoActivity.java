package com.umeng.demo;

import com.feedback.NotificationType;
import com.feedback.UMFeedbackService;
import com.mobclick.android.MobclickAgent;
import com.mobclick.android.ReportPolicy;
import com.mobclick.android.UmengConstants;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.PreferenceManager.OnActivityStopListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class DemoActivity extends Activity {
   
	private static final String TAG="umeng.demo";
	private Button online_config;
	private Button feedback;
	private Button on_error;
	private Button simple_event;
	private Button mtag_event0;
	private Button mtag_event1;
	private Button mtag_event2;
	
	private Context context;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    
        findViews();
        MobclickAgent.setDebugMode(true); 
        context= DemoActivity.this;
        MobclickAgent.setSessionContinueMillis(1000);//change 30000(default) to 1000 
        MobclickAgent.onError(this);
        MobclickAgent.setAutoLocation(false);//collect location info, need some permission. = true(default)
          //set debug mode ,will print log in logcat(lable:MobclickAgent) =true(default)
       
        UmengConstants.enableCacheInUpdate = false;
        
        UMFeedbackService.enableNewReplyNotification(this, NotificationType.AlertDialog);
       
        //,"androidmarekt");
        MobclickAgent.setUpdateOnlyWifi(false);//[true(default) update only wifi, false we'll try to update during 2G or 3G ,if you have no better idea,just gore this params]
        //MobclickAgent.enableCacheInUpdate [true(default) enable cached apk to be installed which has been downloaded before]
        //[false we'll always download the latest version from the server. if you have no better idea,just igore this params.]
        MobclickAgent.update(this, 1000*60*60*24);//daily
//        MobclickAgent.updateAutoPopup= false;// set false if you want to handle the update result by yourself.and add a UmengUpdateListener() like below
//        MobclickAgent.setUpdateListener(new UmengUpdateListener(){
//
//			@Override
//			public void onUpdateReturned(int arg) {
//				switch(arg){
//				case 0: 				//has update
//					MobclickAgent.showUpdateDialog(DemoActivity.this);
//					Log.i(TAG, "show dialog");
//					break;
//				case 1:					//has no update
//					Toast.makeText(context, "has no update", Toast.LENGTH_SHORT).show();
//					break;
//				case 2:					//none wifi
//					Toast.makeText(context, "has no update", Toast.LENGTH_SHORT).show();
//					break;				
//				case 3:					//time out
//					Toast.makeText(context, "time out", Toast.LENGTH_SHORT).show();
//					break;
//				}
//			}
//        	
//        });
        
        MobclickAgent.setDefaultReportPolicy(this, ReportPolicy.BATCH_AT_LAUNCH);//set default report policy(rp for short) if fail to get online params. 																	//priority is : our rp < your rp < online rp  
        MobclickAgent.updateOnlineConfig(this);// get online params while the app launch. 
//        MobclickAgent.setOnlineConfigureListener(new UmengOnlineConfigureListener(){
//
//			@Override
//			public void onDataReceived(JSONObject data) {
//				try{
//					if(data.has("web"))
//						Log.i(TAG, "abc:"+data.getString("web"));
//				}catch(Exception e){
//					e.printStackTrace();
//				}
//			}
//        	
//        });
 
    }
    public void findViews(){
    	 online_config= (Button)findViewById(R.id.online_config);
    	 feedback= (Button)findViewById(R.id.feedback);
    	 on_error= (Button)findViewById(R.id.on_error);
         simple_event= (Button)findViewById(R.id.self_event);
         mtag_event0= (Button)findViewById(R.id.self_multitag_event0);
         mtag_event1= (Button)findViewById(R.id.self_multitag_event1);
         mtag_event2= (Button)findViewById(R.id.self_multitag_event2);
         
         online_config.setOnClickListener(l_btn);
         feedback.setOnClickListener(l_btn);
         on_error.setOnClickListener(l_btn);
         simple_event.setOnClickListener(l_btn);
         mtag_event0.setOnClickListener(l_btn);
         mtag_event1.setOnClickListener(l_btn);
         mtag_event2.setOnClickListener(l_btn);
    }
    
    private Button.OnClickListener l_btn= new Button.OnClickListener(){

		@Override
		public void onClick(View v) {
			switch(v.getId()){
			//online config
			case R.id.online_config:
				String onlineParams= MobclickAgent.getConfigParams(context, "abc");//my online param's key is 'abc'
				if(onlineParams.equals("")){
					Toast.makeText(context, "Get No Online Params", Toast.LENGTH_SHORT).show();
				}else
					Toast.makeText(context, "Online Params:"+ onlineParams, Toast.LENGTH_SHORT).show();
				
//				  online switcher to control the ads ，edit: key:ad_switcher value:on
//				  
//				  String switcher=  MobclickAgent.getConfigParams(context, "ad_switcher");
//				  if("".equals(switcher)) return;
//				  else
//				  		if(switcher.equals("on")){
//				  			showAds();
//				 		else
//				  			hideAds();
//				 
//				  or update the additional info ,edit: key:qq values:895341096
//				  String qq=  MobclickAgent.getConfigParams(context, "qq");
//				  if("".equals(qq)) return;
//				  else
//				  		setContacts(new StringBuilder().append("QQ:").append(qq).toString());
//				 See the document for more infomation
				  
				 
				break;
			//feed back
			case R.id.feedback:
				UMFeedbackService.openUmengFeedbackSDK(DemoActivity.this);
				break;
			//error test
			
			case R.id.on_error:
				try{
					@SuppressWarnings("unused")
					int a= 1/0;
				}catch(Exception e){
					MobclickAgent.reportError(context, "error:"+e.getMessage());
				}
				break;
				
			//simple event
			case R.id.self_event:
				MobclickAgent.onEvent(context, "Click");//"SimpleButtonclick");
				Toast.makeText(context, "simple button is clicked", Toast.LENGTH_SHORT).show();
				
				Log.i(TAG, "simple event");
				break;
				
			//Multi-lable event
			case R.id.self_multitag_event0:
				MobclickAgent.onEvent(context, "Level", "Level_one");
				Toast.makeText(context, "pass level one", Toast.LENGTH_SHORT).show();
				break;
			case R.id.self_multitag_event1:
				MobclickAgent.onEvent(context, "Level", "Level_two");
				Toast.makeText(context, "pass level two", Toast.LENGTH_SHORT).show();
				break;
			case R.id.self_multitag_event2:
				MobclickAgent.onEvent(context, "Level", "Level_three");
				Toast.makeText(context, "pass level three", Toast.LENGTH_SHORT).show();
				break;
				
			}
		}

    };
    
    public class MessageHere implements OnActivityStopListener{

		@Override
		public void onActivityStop() {
			// TODO Auto-generated method stub
			
		}
		
    	
    }

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
		
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK){
			if(MobclickAgent.isDownloadingAPK()){
				warningDialog(this);
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private void warningDialog(Context context){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
						builder.setMessage("You are downloading apk, are you sure to exits ?");
						builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								DemoActivity.this.finish();
							}
						
						});
						builder.create();
						builder.show();
	}    
}