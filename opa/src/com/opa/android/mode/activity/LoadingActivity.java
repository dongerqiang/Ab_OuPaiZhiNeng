package com.opa.android.mode.activity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

import com.opa.android.R;

import android.os.Handler;

@EActivity(R.layout.activity_loading_layout)
public class LoadingActivity extends BaseActivity {
	@Override
	@AfterViews
	public void initViews() {
		super.initViews();
		
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				if(app.isLogin()){
					com.opa.android.mode.activity.LoginActivity_.intent(LoadingActivity.this).start();
				}else{
					com.opa.android.MainActivity_.intent(LoadingActivity.this).start();					
				}
				finish();
			}
		}, 1500);
	}
}
