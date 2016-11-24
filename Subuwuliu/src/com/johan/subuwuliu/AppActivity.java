package com.johan.subuwuliu;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;

import com.johan.subuwuliu.application.DiDiApplication;
import com.johan.subuwuliu.util.SystemBarTintManager;
import com.johan.subuwuliu.util.ToastUtil;

public abstract class AppActivity extends Activity {

	private SystemBarTintManager tintManager;

	private SharedPreferences sharedPreferences;
	private String loginName, loginPassword;

	private boolean needLoginInfo = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//////屏幕常亮
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		preSetContentView(savedInstanceState);
		initWindow();
		setContentView(getContentView());
		findThemeId();
		findId();
		initThemele();
		init();
		postInit(savedInstanceState);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (needLoginInfo) {
			if (DiDiApplication.VALUE_UNKOWN.equals(loginName)
					|| DiDiApplication.VALUE_UNKOWN.equals(loginPassword)) {
				goActivity(LoginActivity.class);
				finish();
			}
		}
		inResume();
	}

	public SharedPreferences getSharedPreferences() {
		return sharedPreferences;
	}
	public String getLoginName() {
		return loginName;
	}

	public String getLoginPassword() {
		return loginPassword;
	}

	/**
	 * 如果不想要LoginInfo的话，一定设为false，否则可能返回登录页面
	 * 
	 * @param needLoginInfo
	 */
	public void setNeedLoginInfo(boolean needLoginInfo) {
		this.needLoginInfo = needLoginInfo;
	}

	public abstract int getContentView();

	public abstract void findId();

	public abstract void init();

	public void findThemeId() {

	}

	public void initThemele() {

	}

	public void inResume() {

	}

	public void preSetContentView(Bundle savedInstanceState) {
		sharedPreferences = getSharedPreferences(
				DiDiApplication.NAME_OF_SHARED_PREFERENCES, MODE_PRIVATE);
		loginName = sharedPreferences
				.getString(DiDiApplication.KEY_OF_LOGIN_NAME,
						DiDiApplication.VALUE_UNKOWN);
		loginPassword = sharedPreferences.getString(
				DiDiApplication.KEY_OF_LOGIN_PWD, DiDiApplication.VALUE_UNKOWN);
	}

	public void postInit(Bundle savedInstanceState) {

	}

	protected void goActivity(Class<?> targetActivity) {
		Intent intent = new Intent(this, targetActivity);
		startActivity(intent);
	}

	protected void goActivity(Class<?> targetActivity, Bundle data) {
		Intent intent = new Intent(this, targetActivity);
		intent.putExtras(data);
		startActivity(intent);
	}

	protected void goActivityForResult(Class<?> targetActivity, int requestCode) {
		Intent intent = new Intent(this, targetActivity);
		startActivityForResult(intent, requestCode);
	}

	protected void goActivityForResult(Class<?> targetActivity,
			int requestCode, Bundle data) {
		Intent intent = new Intent(this, targetActivity);
		intent.putExtras(data);
		startActivityForResult(intent, requestCode);
	}

	protected void showToast(String msg) {
		ToastUtil.show(this, msg);
	}

	public int getStatuBarColor() {
		return Color.parseColor("#000000");
	}

	@TargetApi(19)
	private void initWindow() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
			tintManager = new SystemBarTintManager(this);
			tintManager.setStatusBarTintColor(getStatuBarColor());
			tintManager.setStatusBarTintEnabled(true);
		}
	}

	public void goLogin() {
		goActivity(LoginActivity.class);
		finish();
	}

	/**
	 * 以下，只有用到Fragment才用到 注意：如果要用一下方法，一定要重载getContainerId()方法
	 * 
	 * @param fragment
	 */

	public void addFragment(Fragment fragment) {
		FragmentTransaction fragmentTransaction = getFragmentManager()
				.beginTransaction();
		fragmentTransaction.add(getContainerId(), fragment);
		fragmentTransaction.commit();
	}

	public void showFragment(Fragment fragment) {
		if (null == AppActivity.this || AppActivity.this.isFinishing()) {
			System.out.println("activity 已经关闭， 不能切换Fragment");
			return;
		} else {
			System.out.println("activity 没有关闭， 可以切换Fragment");
		}
		FragmentTransaction fragmentTransaction = getFragmentManager()
				.beginTransaction();
		fragmentTransaction.replace(getContainerId(), fragment);
		fragmentTransaction.commit();
	}

	public void removeFragment(Fragment fragment) {
		FragmentTransaction fragmentTransaction = getFragmentManager()
				.beginTransaction();
		fragmentTransaction.remove(fragment);
		fragmentTransaction.commit();
	}

	public int getContainerId() {
		return 0;
	}

}
