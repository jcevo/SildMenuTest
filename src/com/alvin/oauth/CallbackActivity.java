package com.alvin.oauth;

import android.app.Activity;
import android.os.Bundle;

public class CallbackActivity extends Activity {
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		OAuthActivity.postLogin(this.getIntent());
		this.finish();
	}
}
