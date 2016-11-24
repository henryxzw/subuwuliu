package com.johan.subuwuliu;

import java.util.HashMap;

import com.johan.subuwuliu.application.DiDiApplication;
import com.johan.subuwuliu.bean.StatusBean;
import com.johan.subuwuliu.dialog.WaitingDialog;
import com.johan.subuwuliu.util.GsonUtil;
import com.johan.subuwuliu.util.NetUtil;
import com.johan.subuwuliu.util.ToastUtil;
import com.johan.subuwuliu.util.ToolUtil;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.umeng.analytics.MobclickAgent;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

public class ForgetPasswordActivity extends AppThemeActivity implements OnClickListener {

	private EditText phone, checkNumber, password;
	
	private Button getCheckNumber, ok;
	
	private WaitingDialog waitingDialog;
	
	@Override
	public int getContentView() {
		return R.layout.activity_forgetpassword;
	}
	
	@Override
	public String getThemeTitle() {
		// TODO Auto-generated method stub
		return "忘记密码";
	}
	
	@Override
	public void findId() {
		phone = (EditText) findViewById(R.id.forgetpassword_phone);
		checkNumber = (EditText) findViewById(R.id.forgetpassword_checknumber);
		password = (EditText) findViewById(R.id.forgetpassword_new_password);
		getCheckNumber = (Button) findViewById(R.id.forgetpassword_get_checknumber);
		ok = (Button) findViewById(R.id.forgetpassword_but);
	}
	
	@Override
	public void init() {
		getCheckNumber.setOnClickListener(this);
		ok.setOnClickListener(this);
		waitingDialog = new WaitingDialog(ForgetPasswordActivity.this);
		setNeedLoginInfo(false);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.forgetpassword_get_checknumber :
			hideSoftInputMethod();
			getCheckNumber();
			break;
		case R.id.forgetpassword_but :
			changePassword();
			break;
		default:
			break;
		}
	}
	
	private void getCheckNumber() {
		String phoneStr = phone.getText().toString();
		if(!ToolUtil.isMobileNumber(phoneStr)) {
			showToast("请输入11位有效手机号码");
			return;
		}
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
	
	private void changePassword() {
		final String phoneStr = phone.getText().toString();
		final String newPassword = password.getText().toString();
		final String checkNumberStr = checkNumber.getText().toString();
		if(!ToolUtil.isMobileNumber(phoneStr)) {
			showToast("请输入11位有效手机号码");
			return;
		}
		if(checkNumberStr.length() == 0) {
			showToast("请输入验证码");
			return;
		}
		if(newPassword.length() == 0) {
			showToast("请输入新密码");
			return;
		}
		waitingDialog.show(findViewById(R.id.forgetpassword_layout));
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
					params.put("key", "forget_pwd");
					params.put("username", phoneStr);
					params.put("new_pwd", newPassword);
					params.put("phone_code", code);
					String action = ToolUtil.encryptionUrlParams(params);
					NetUtil.getInstance().doGet(NetUtil.DRIVER_URL + action, new RequestCallBack<String>() {
						@Override
						public void onFailure(HttpException exception, String info) {
							waitingDialog.dismiss();
							ToastUtil.show(ForgetPasswordActivity.this, "网络不佳，修改密码失败");
						}
						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
							System.out.println("修改密码结果：" + responseInfo.result);
							StatusBean statusBean = GsonUtil.jsonToObjct(responseInfo.result, StatusBean.class);
							if("0".equals(statusBean.status)) {
								//保存登录信息
								SharedPreferences sharedPreferences = getSharedPreferences(DiDiApplication.NAME_OF_SHARED_PREFERENCES, MODE_PRIVATE);
								Editor editor = sharedPreferences.edit();
								editor.putString(DiDiApplication.KEY_OF_LOGIN_NAME, phoneStr);
								editor.putString(DiDiApplication.KEY_OF_LOGIN_PWD, newPassword);
								editor.commit();
								waitingDialog.dismiss();
								//结束
								ForgetPasswordActivity.this.finish();
							} else {
								waitingDialog.dismiss();
								ToastUtil.show(ForgetPasswordActivity.this, "修改密码错误：" + statusBean.msg);
							}
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
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this); // 统计时长
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
