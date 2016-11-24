package com.johan.subuwuliu;

import java.util.HashMap;

import com.easemob.EMCallBack;
import com.easemob.chatuidemo.DemoHXSDKHelper;
import com.johan.subuwuliu.application.DiDiApplication;
import com.johan.subuwuliu.bean.DriverInfoBean;
import com.johan.subuwuliu.bean.StatusBean;
import com.johan.subuwuliu.dialog.WaitingDialog;
import com.johan.subuwuliu.util.GsonUtil;
import com.johan.subuwuliu.util.NetUtil;
import com.johan.subuwuliu.util.ToastUtil;
import com.johan.subuwuliu.util.ToolUtil;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

public class RemoteLoginActivity extends AppThemeActivity implements OnClickListener {
	
	private EditText phone, checkNumber, password;
	
	private Button getCheckNumber, ok;
	
	private WaitingDialog waitingDialog;
	
	private String remotePhone;

	@Override
	public String getThemeTitle() {
		// TODO Auto-generated method stub
		return "异地登录";
	}

	@Override
	public int getContentView() {
		// TODO Auto-generated method stub
		
		return R.layout.activity_remotelogin;
	}

	@Override
	public void findId() {
		phone = (EditText) findViewById(R.id.remotelogin_phone);
		checkNumber = (EditText) findViewById(R.id.remotelogin_checknumber);
		password = (EditText) findViewById(R.id.remotelogin_password);
		getCheckNumber = (Button) findViewById(R.id.remotelogin_get_checknumber);
		ok = (Button) findViewById(R.id.remotelogin_but);
	}

	@Override
	public void init() {
		Bundle data = getIntent().getExtras();
		remotePhone = data.getString("remote_phone");
		if(null != remotePhone) {
			phone.setText(data.getString("remote_phone"));
		}
		phone.setEnabled(false);
		getCheckNumber.setOnClickListener(this);
		ok.setOnClickListener(this);
		waitingDialog = new WaitingDialog(RemoteLoginActivity.this);
		setNeedLoginInfo(false);
	}
	
	@Override
	public void postInit(Bundle savedInstanceState) {
		if(remotePhone == null) {
			if(savedInstanceState != null) {
				Bundle data = getIntent().getExtras();
				remotePhone = data.getString("remote_phone");
				if(null != remotePhone) {
					phone.setText(data.getString("remote_phone"));
				} else {
					finish();
				}
			} else {
				finish();
			}
		}
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putString("remote_phone", remotePhone);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.remotelogin_get_checknumber :
			
			hideSoftInputMethod();
			getCheckNumber();
			break;
		case R.id.remotelogin_but :
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
			login();
			break;
		default:
			break;
		}
	}
	
	private void getCheckNumber() {
		String phoneStr = phone.getText().toString();
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("key", "send_sms");
		params.put("mobile", phoneStr);
		params.put("code_type", "pwd");
		String action = ToolUtil.encryptionUrlParams(params);
		//计时器开始
		countDownTimer.start();
		NetUtil.getInstance().doGet(NetUtil.DRIVER_URL + action, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				String result = responseInfo.result;
				StatusBean status = GsonUtil.jsonToObjct(result, StatusBean.class);
				if("0".equals(status.status)) {
					showToast("验证码已经发送成功");
				} else {
					showToast("获取验证失败，" + status.msg);
					countDownTimer.cancel();
					getCheckNumber.setEnabled(true);
					getCheckNumber.setText("获取验证码");
				}
			}
			@Override
			public void onFailure(HttpException exception, String arg1) {
				showToast("获取验证失败，请重新获取" + arg1);
				countDownTimer.cancel();
				getCheckNumber.setEnabled(true);
				getCheckNumber.setText("重新获取");
			}
		});
	}
	
	private CountDownTimer countDownTimer = new CountDownTimer(60000, 1000) {
		@Override
		public void onTick(long millisUntilFinished) {
			getCheckNumber.setEnabled(false);
			getCheckNumber.setText(millisUntilFinished / 1000 + "秒");
		}
		@Override
		public void onFinish() {
			getCheckNumber.setEnabled(true);
			getCheckNumber.setText("重新获取");
		}
	};
	
	private void login() {
		final String phoneStr = phone.getText().toString();
		final String passwordStr = password.getText().toString();
		final String checkNumberStr = checkNumber.getText().toString();
		if(checkNumberStr.length() == 0) {
			showToast("请输入验证码");
			return;
		}
		if(password.length() == 0) {
			showToast("请输入密码");
			return;
		}
		waitingDialog.show(findViewById(R.id.remotelogin_layout));
		//先验证
		HashMap<String, String> checkParams = new HashMap<String, String>();
		checkParams.put("key", "verify_sms_code");
		checkParams.put("code_type", "pwd");
		checkParams.put("mobile", phoneStr);
		checkParams.put("code", checkNumberStr);
		String checkAction = ToolUtil.encryptionUrlParams(checkParams);
		NetUtil.getInstance().doGet(NetUtil.DRIVER_URL + checkAction, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				System.out.println("验证码结果：" + responseInfo.result);
				String result = responseInfo.result;
				StatusBean status = GsonUtil.jsonToObjct(result, StatusBean.class);
				if("0".equals(status.status)) {
					LoginActivity.logoactivity.finish();
					//机器码
					String telmanagerid, androidid, serialnumber;
					try {
						TelephonyManager telmanager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
						telmanagerid = telmanager.getDeviceId();
					} catch (Exception e) {
						telmanagerid = "0";
					}
					try {
						androidid = Settings.System.getString(getContentResolver(), Settings.System.ANDROID_ID);
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
					HashMap<String, String> params = new HashMap<String, String>();
					params.put("key", "validate_code");
					params.put("username", phoneStr);
					params.put("pwd", passwordStr);
					params.put("phone_code", code);
					String action = ToolUtil.encryptionUrlParams(params);
					NetUtil.getInstance().doGet(NetUtil.DRIVER_URL + action, new RequestCallBack<String>() {
						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
							System.out.println("登录结果：" + responseInfo.result);
							StatusBean loginResult = GsonUtil.jsonToObjct(responseInfo.result, StatusBean.class);
							if("0".equals(loginResult.status)) {
								//获取司机信息
								String driverInfoTimestamp = getSharedPreferences().getString(DiDiApplication.KEY_OF_DRIVER_INFO_TIMESTAMP, DiDiApplication.VALUE_NO_TIMESTAMP);
								HashMap<String, String> params = new HashMap<String, String>();
								params.put("key", "driver_info");
								params.put("username", phoneStr);
								params.put("pwd", passwordStr);
								params.put("timestamp", driverInfoTimestamp);
								String action = ToolUtil.encryptionUrlParams(params);
								NetUtil.getInstance().doGet(NetUtil.DRIVER_URL + action, new RequestCallBack<String>() {
									@Override
									public void onSuccess(ResponseInfo<String> responseInfo) {
										System.out.println("司机信息结果：" + responseInfo.result);
										DriverInfoBean driverInfo = GsonUtil.jsonToObjct(responseInfo.result, DriverInfoBean.class);
										if("0".equals(driverInfo.status)) {
											//保存获取到司机信息
											Editor editor = getSharedPreferences().edit();
											editor.putString(DiDiApplication.KEY_OF_LOGIN_NAME, phoneStr);
											editor.putString(DiDiApplication.KEY_OF_LOGIN_PWD, passwordStr);
											editor.putString(DiDiApplication.KEY_OF_DRIVER_INFO_TIMESTAMP, driverInfo.timestamp);
											editor.putString(DiDiApplication.KEY_OF_DRIVER_INFO_DATA, responseInfo.result);
											editor.commit();
											goHomeActivity();
										} else if("200".equals(driverInfo.status)) {
											//保存登录信息
											Editor editor = getSharedPreferences().edit();
											editor.putString(DiDiApplication.KEY_OF_LOGIN_NAME, phoneStr);
											editor.putString(DiDiApplication.KEY_OF_LOGIN_PWD, passwordStr);
											editor.commit();
											goHomeActivity();
										} else {
											showToast(driverInfo.msg);
											finish();
										}
									}
									@Override
									public void onFailure(HttpException exception, String info) {
										ToastUtil.showNetError(RemoteLoginActivity.this);
										finish();
									}
								});
							} else {
								showToast(loginResult.msg);
								finish();
							}
							waitingDialog.dismiss();
						}
						@Override
						public void onFailure(HttpException exception, String info) {
							showToast(info);
							waitingDialog.dismiss();
						}
					});
				} else {
					showToast(status.msg);
					waitingDialog.dismiss();
				}
			}
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				showToast("验证失败");
				waitingDialog.dismiss();
			}
		});
	}
	
	private void hideSoftInputMethod() {
		View view = getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
	}
	
	private void goHomeActivity() {
		Intent homeIntent = new Intent(this, HomeActivity.class);
		homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(homeIntent);
	}

}
