package com.alvin.api.activity.common;

import com.alvin.api.abstractClass.BaseFragmentActivity;
import com.alvin.api.model.Account;
import com.alvin.api.utils.AccountUtils;
import com.alvin.api.utils.AccountUtils.LoginResult;
import com.alvin.common.R;
import com.alvin.ui.SimpleToast;
import com.imofan.android.develop.sns.MFSnsUser;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class LoginActivity extends BaseFragmentActivity {
	private EditText usernameEdit = null;
	private EditText passwordEdit = null;
	private Button loginBtn = null;
	private RelativeLayout sinaLayout = null;
	private RelativeLayout qqLayout = null;
	private String username = null;
	private String password = null;
	public Account account;
	private ProgressBar porgressBar = null;
	private MFSnsUser user = null;
	private Context authContext;
	private String type = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.common_login_activity);
		initView();
	}
	
	private void initView(){
		usernameEdit = (EditText) findViewById(R.id.common_login_username);
	    passwordEdit = (EditText) findViewById(R.id.common_login_password);
	    loginBtn = (Button) findViewById(R.id.common_login_button);
	    sinaLayout = (RelativeLayout) findViewById(R.id.common_login_sina_layout);
	    qqLayout = (RelativeLayout) findViewById(R.id.common_login_qq_layout);
	    porgressBar = (ProgressBar) findViewById(R.id.login_progress);
	    loginBtn.setOnClickListener(clickListener);
	    sinaLayout.setOnClickListener(clickListener);
	    qqLayout.setOnClickListener(clickListener);
	}
	
	//点击
	private OnClickListener clickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			int id = v.getId();
			if(id == R.id.common_login_button){
				login();
			}else if(id == R.id.common_login_sina_layout){
				
			}else if(id == R.id.common_login_qq_layout){
				
			}
		}
	};
	
	private void login(){
		username = usernameEdit.getText().toString().trim();
	    password = passwordEdit.getText().toString().trim();
	    if ("".equals(username) || "".equals(password)) {// 用户名或密码为空
	        SimpleToast.show(this, "用户名或密码不能为空！", Toast.LENGTH_SHORT);
	     } else {// 执行登录
	            porgressBar.setVisibility(View.VISIBLE);
	            AccountUtils.login(this, username, password, new LoginResult() {
					
					@Override
					public void onSuccess(Account accunt) {
						 porgressBar.setVisibility(View.GONE);
						 SimpleToast.show(LoginActivity.this, "登录成功！", Toast.LENGTH_SHORT);
						 LoginActivity.this.finish();
					}
					
					@Override
					public void onFailure(int errorCode, String errorMessage) {
						 porgressBar.setVisibility(View.GONE);
						SimpleToast.show(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT);
					}
				});

	        }
	}
}
