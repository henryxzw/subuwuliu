package com.johan.subuwuliu;

import java.util.HashMap;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;

import com.johan.subuwuliu.application.DiDiApplication;
import com.johan.subuwuliu.bean.DriverInfoBean;
import com.johan.subuwuliu.bean.GengXing;
import com.johan.subuwuliu.bean.StatusBean;
import com.johan.subuwuliu.dialog.WaitingDialog;
import com.johan.subuwuliu.method.MethodConfig;
import com.johan.subuwuliu.util.GsonUtil;
import com.johan.subuwuliu.util.NetUtil;
import com.johan.subuwuliu.util.ToastUtil;
import com.johan.subuwuliu.util.ToolUtil;
import com.johan.subuwuliu.util.Utils_GX;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.umeng.analytics.MobclickAgent;

public class LoginActivity extends AppActivity implements OnClickListener {

	private EditText phone, password;

	private TextView register, forgetPassword;

	private Button loginBut;
	
	private String old_Verson;

	private WaitingDialog waitingDialog;
	public static Activity logoactivity;

	@Override
	public int getContentView() {
		return R.layout.activity_login;
	}

	@Override
	public void findId() {
		logoactivity = this;
		phone = (EditText) findViewById(R.id.login_phone);
		password = (EditText) findViewById(R.id.login_password);
		register = (TextView) findViewById(R.id.login_register);
		forgetPassword = (TextView) findViewById(R.id.login_forgetpassword);
		loginBut = (Button) findViewById(R.id.login_but);
	}

	@Override
	public void init() {
		gx();
		forgetPassword.setOnClickListener(this);
		register.setOnClickListener(this);
		loginBut.setOnClickListener(this);
		waitingDialog = new WaitingDialog(LoginActivity.this);
		setNeedLoginInfo(false);
		// 环信Push停止
		JPushInterface.stopPush(getApplicationContext());
	}
	
	private void gx() {
		PackageManager manager = this.getPackageManager();
		PackageInfo info;
		try {
			info = manager.getPackageInfo(this.getPackageName(), 0);
			old_Verson = info.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("key", "app_update");
		
		 String action = ToolUtil.encryptionUrlParams(map);
		String urlStr = NetUtil.DRIVER_URL + action;
		NetUtil.getInstance().doGet(urlStr,new RequestCallBack<String>() {
							@Override
							public void onSuccess(ResponseInfo<String> arg0) {
								GengXing qianbaoBean = GsonUtil.jsonToObjct(
										arg0.result, GengXing.class);
								if (qianbaoBean.status==0) {
									String gx_rizi = qianbaoBean.driver_update_msg;
									String gx_ver = qianbaoBean.driver_update_version.trim();
									String gx_version = qianbaoBean.driver_update_version.trim();
									String apkUrl = qianbaoBean.driver_update_url;
									String vername = "_V" + qianbaoBean.driver_update_version.trim();
									PackageManager manager = getPackageManager();
									PackageInfo info;
									try {
										info = manager.getPackageInfo(
												getPackageName(), 0);
										old_Verson = info.versionName;
									} catch (NameNotFoundException e) {
										e.printStackTrace();
									}
									// //判断版本号是不是最新的
									if (!gx_version.equals(old_Verson)) {
										Utils_GX.dialog_gengxing(gx_rizi,
												gx_ver, gx_version, apkUrl,
												vername,LoginActivity.this
												);
									}
								}
							}

							@Override
							public void onFailure(HttpException arg0,
									String arg1) {
								showToast("网络异常.....");
							}
						});
	}


	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		try {
			MobclickAgent.onResume(this); // 统计时长
			SharedPreferences sharedPreferences = getSharedPreferences(
					DiDiApplication.NAME_OF_SHARED_PREFERENCES,
					Context.MODE_PRIVATE);
			// ////修改司机下线状态
			sharedPreferences
					.edit()
					.putBoolean(
							DiDiApplication.KEY_OF_NEED_UPDATE_DRIVER_STATE,
							true).commit();
			String loginName = getSharedPreferences().getString(
					DiDiApplication.KEY_OF_LOGIN_NAME, "unkonw");
			if (!"unkonw".equals(loginName)) {
				phone.setText(loginName);
				password.requestFocus();
			}
		} catch (Exception e) {
			// TODO: handle exception
			phone.setText("");
			password.setText("");
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}
	

	@Override
	public void onBackPressed() {
		if(MethodConfig.CanBack(2))
		{
			finish();
		}
		else {
			MethodConfig.ShowToast("再按一次退出");
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login_forgetpassword:
			goActivity(ForgetPasswordActivity.class);
			break;
		case R.id.login_register:
//			Bundle data = new Bundle();
//			data.putString("check_phone", "18565592421");
//			data.putString("check_password", "123456");
//			goActivity(RegisterDriverActivity.class, data);
			goActivity(CheckPhoneActivity.class);
			break;
		case R.id.login_but:
		    login();
		default:
			break;
		}
	}

	@SuppressWarnings("deprecation")
	private void login() {
		final String loginName = phone.getText().toString();
		final String loginPassword = password.getText().toString();
		final String driverInfoTimestamp = getSharedPreferences().getString(
				DiDiApplication.KEY_OF_DRIVER_INFO_TIMESTAMP,
				DiDiApplication.VALUE_NO_TIMESTAMP);
		if (loginName.length() == 0) {
			showToast("请输入手机号码");
			return;
		}
		if (loginPassword.length() == 0) {
			showToast("请输入密码");
			return;
		}
		if (!ToolUtil.isMobileNumber(loginName)) {
			showToast("手机号码格式错误");
			return;
		}
		waitingDialog.show(findViewById(R.id.login_layout));
		// 机器码
		String telmanagerid, androidid, serialnumber;
		try {
			TelephonyManager telmanager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
			telmanagerid = telmanager.getDeviceId();
		} catch (Exception e) {
			telmanagerid = "0";
		}
		try {
			androidid = Settings.System.getString(getContentResolver(),
					Settings.System.ANDROID_ID);
		} catch (Exception e) {
			androidid = "0";
		}
		try {
			serialnumber = android.os.Build.SERIAL;
		} catch (Exception e) {
			serialnumber = "0";
		}
		if (telmanagerid == null || telmanagerid.equals("")) {
			telmanagerid = "0";
		}
		if (androidid == null || androidid.equals("")) {
			androidid = "0";
		}
		if (serialnumber == null || serialnumber.equals("")) {
			serialnumber = "0";
		}
		String code = telmanagerid + androidid + serialnumber;
		HashMap<String, String> loginParams = new HashMap<String, String>();
		loginParams.put("key", "driver_login");
		loginParams.put("username", loginName);
		loginParams.put("pwd", loginPassword);
		loginParams.put("phone_code", code);
		String loginAction = ToolUtil.encryptionUrlParams(loginParams);
		NetUtil.getInstance().doGet(NetUtil.DRIVER_URL + loginAction,
				new RequestCallBack<String>() {
					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						System.out.println("登录结果：" + responseInfo.result);
						StatusBean loginResult = GsonUtil.jsonToObjct(
								responseInfo.result, StatusBean.class);
						if ("0".equals(loginResult.status)) {
							System.out.println("------------>>"
									+ loginResult.status
									+ "---------------->>111获取司机信息");
							// 获取司机信息
							HashMap<String, String> params = new HashMap<String, String>();
							params.put("key", "driver_info");
							params.put("username", loginName);
							params.put("pwd", loginPassword);
							params.put("timestamp", driverInfoTimestamp);
							String action = ToolUtil
									.encryptionUrlParams(params);
							NetUtil.getInstance().doGet(
									NetUtil.DRIVER_URL + action,
									new RequestCallBack<String>() {
										@Override
										public void onSuccess(
												ResponseInfo<String> responseInfo) {
											System.out.println("司机信息结果："
													+ responseInfo.result);
											DriverInfoBean driverInfo = GsonUtil
													.jsonToObjct(
															responseInfo.result,
															DriverInfoBean.class);
											if ("0".equals(driverInfo.status)) {
												System.out
														.println("------------>>"
																+ driverInfo.status
																+ "---------------->>222获取司机信息");
												// 正常获取到司机信息
												Editor editor = getSharedPreferences()
														.edit();
												editor.putString(
														DiDiApplication.KEY_OF_LOGIN_NAME,
														loginName);
												editor.putString(
														DiDiApplication.KEY_OF_LOGIN_PWD,
														loginPassword);
												editor.putString(
														DiDiApplication.KEY_OF_DRIVER_INFO_TIMESTAMP,
														driverInfo.timestamp);
												editor.putString(
														DiDiApplication.KEY_OF_DRIVER_INFO_DATA,
														responseInfo.result);
												editor.commit();
												goActivity(HomeActivity.class);
												finish();
											} else if ("200"
													.equals(driverInfo.status)) {
												System.out
														.println("------------>>"
																+ driverInfo.status
																+ "---------------->>222获取司机信息");
												// 保存登录信息
												Editor editor = getSharedPreferences()
														.edit();
												editor.putString(
														DiDiApplication.KEY_OF_LOGIN_NAME,
														loginName);
												editor.putString(
														DiDiApplication.KEY_OF_LOGIN_PWD,
														loginPassword);
												editor.commit();
												goActivity(HomeActivity.class);
												finish();
											} else {
												showToast(driverInfo.msg);
											}
										}

										@Override
										public void onFailure(
												HttpException exception,
												String info) {
											ToastUtil
													.showNetError(LoginActivity.this);
										}
									});
						} else if ("4".equals(loginResult.status)) {
							System.out.println("------------->>>"
									+ loginResult.status);
							Bundle remoteData = new Bundle();
							remoteData.putString("remote_phone", loginName);
							goActivity(RemoteLoginActivity.class, remoteData);
						} else {
							showToast(loginResult.msg);
						}
						waitingDialog.dismiss();
					}

					@Override
					public void onFailure(HttpException exception, String info) {
						waitingDialog.dismiss();
						System.out.println("login error : "
								+ exception.getMessage());
						ToastUtil.showNetError(LoginActivity.this);
					}
				});
	}
//
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		// PackageManager pm = getPackageManager();
//		// ResolveInfo homeInfo = pm.resolveActivity(
//		// new Intent(Intent.ACTION_MAIN)
//		// .addCategory(Intent.CATEGORY_HOME), 0);
//		// if (keyCode == KeyEvent.KEYCODE_BACK) {
//		// ActivityInfo ai = homeInfo.activityInfo;
//		// Intent startIntent = new Intent(Intent.ACTION_MAIN);
//		// startIntent.addCategory(Intent.CATEGORY_LAUNCHER);
//		// startIntent
//		// .setComponent(new ComponentName(ai.packageName, ai.name));
//		// startActivitySafely(startIntent);
//		// return true;
//		// } else
//		//android.os.Process.killProcess(android.os.Process.myPid());
//		return super.onKeyDown(keyCode, event);
//	}

	private void startActivitySafely(Intent intent) {
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		try {
			startActivity(intent);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(this, "null-----1", Toast.LENGTH_SHORT).show();
		} catch (SecurityException e) {
			Toast.makeText(this, "null------2", Toast.LENGTH_SHORT).show();
		}
	}
}
