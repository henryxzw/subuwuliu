package com.johan.subuwuliu;

import java.util.HashMap;

import com.easemob.EMCallBack;
import com.easemob.chatuidemo.DemoHXSDKHelper;
import com.johan.subuwuliu.application.DiDiApplication;
import com.johan.subuwuliu.bean.DriverInfoBean;
import com.johan.subuwuliu.bean.QianBaoBean;
import com.johan.subuwuliu.server.ForegroundServer;
import com.johan.subuwuliu.util.GsonUtil;
import com.johan.subuwuliu.util.NetUtil;
import com.johan.subuwuliu.util.ToastUtil;
import com.johan.subuwuliu.util.ToolUtil;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import cn.jpush.android.api.JPushInterface;

public class SplashActivity extends Activity {

	private long showTime = 1000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash);
		MobclickAgent.setDebugMode(true);
		// //////////////////////////////////////
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// new WaitingTask().execute();
		WaitingTask();
		DemoHXSDKHelper.getInstance().logout(true, new EMCallBack() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgress(int arg0, String arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub

			}
		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		JPushInterface.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		JPushInterface.onPause(this);
	}

	/**
	 * 加载数据，预留
	 */
	private void WaitingTask() {
		// class WaitingTask extends AsyncTask<Void, Void, Void> {
		//
		// @Override
		// protected Void doInBackground(Void... data) {
		final SharedPreferences sharedPreferences = getSharedPreferences(
				DiDiApplication.NAME_OF_SHARED_PREFERENCES, MODE_PRIVATE);
		final String loginName = sharedPreferences
				.getString(DiDiApplication.KEY_OF_LOGIN_NAME,
						DiDiApplication.VALUE_UNKOWN);
		final String loginPassword = sharedPreferences.getString(
				DiDiApplication.KEY_OF_LOGIN_PWD, DiDiApplication.VALUE_UNKOWN);
		String driverInfoTimestamp = sharedPreferences.getString(
				DiDiApplication.KEY_OF_DRIVER_INFO_TIMESTAMP,
				DiDiApplication.VALUE_NO_TIMESTAMP);
		// 没有，跳转登录页面
		if (DiDiApplication.VALUE_UNKOWN.equals(loginName)
				|| DiDiApplication.VALUE_UNKOWN.equals(loginPassword)) {
			System.out.println("没有，跳转登录页面");
			showTime = 2000;
			goNext(LoginActivity.class);
			return;
		}
		System.out.println("没有，跳转登录页面11111111111111111--->>"
				+ driverInfoTimestamp);
		// 有，获取司机信息
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("key", "driver_info");
		params.put("username", loginName);
		params.put("pwd", loginPassword);
		if (driverInfoTimestamp.equals("")) {
			driverInfoTimestamp = "0";
		}
		params.put("timestamp", driverInfoTimestamp);
		String action = ToolUtil.encryptionUrlParams(params);
		NetUtil.getInstance().doGet(NetUtil.DRIVER_URL + action,
				new RequestCallBack<String>() {
					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						System.out.println("司机信息结果：" + responseInfo.result);
						DriverInfoBean driverInfo = GsonUtil.jsonToObjct(
								responseInfo.result, DriverInfoBean.class);
						if ("0".equals(driverInfo.status)) {
							// 正常获取到司机信息
							Editor editor = sharedPreferences.edit();
							editor.putString(
									DiDiApplication.KEY_OF_DRIVER_INFO_TIMESTAMP,
									driverInfo.timestamp);
							editor.putString(
									DiDiApplication.KEY_OF_DRIVER_INFO_DATA,
									responseInfo.result);
							editor.commit();
							goNext(HomeActivity.class);
						} else if ("200".equals(driverInfo.status)) {
							goNext(HomeActivity.class);
						} else {
							showToast(driverInfo.msg);
							goNext(LoginActivity.class);
						}
					}

					@Override
					public void onFailure(HttpException exception, String info) {
						ToastUtil.showNetError(SplashActivity.this);
						goNext(LoginActivity.class);
					}
				});
	}

	// return null;
	// }
	//
	// }

	private void goNext(final Class<?> targetActivity) {
		new Handler(getMainLooper()).postDelayed(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(SplashActivity.this, targetActivity);
				SplashActivity.this.startActivity(intent);
				@SuppressWarnings("deprecation")
				int version = Integer.valueOf(android.os.Build.VERSION.SDK);
				if (version >= 5) {
					overridePendingTransition(R.anim.anim_main_in,
							R.anim.anim_splash_out);
				}
				SplashActivity.this.finish();
			}
		}, showTime);
	}

	protected void showToast(String msg) {
		ToastUtil.show(SplashActivity.this, msg);
	}

}
