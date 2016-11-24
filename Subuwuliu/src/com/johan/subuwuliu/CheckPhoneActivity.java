package com.johan.subuwuliu;

import java.util.HashMap;

import com.johan.subuwuliu.bean.StatusBean;
import com.johan.subuwuliu.util.GsonUtil;
import com.johan.subuwuliu.util.NetUtil;
import com.johan.subuwuliu.util.ToolUtil;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.umeng.analytics.MobclickAgent;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

public class CheckPhoneActivity extends AppThemeActivity implements
		OnClickListener {
	private EditText phone, checkNumber, newPassword, confirmPassword;
	private Button getCheckNumber, next;

	public void findId() {
		next = (Button) findViewById(R.id.checkphone_next);
		getCheckNumber = (Button) findViewById(R.id.checkphone_get_checknumber);
		phone = (EditText) findViewById(R.id.checkphone_phone);
		checkNumber = (EditText) findViewById(R.id.checkphone_checknumber);
		newPassword = (EditText) findViewById(R.id.checkphone_new_password);
		confirmPassword = (EditText) findViewById(R.id.checkphone_confirm_password);
	}

	public void init() {
		next.setOnClickListener(this);
		getCheckNumber.setOnClickListener(this);
		setNeedLoginInfo(false);
	}

	@Override
	public int getContentView() {
		return R.layout.activity_checkphone;
	}

	@Override
	public String getThemeTitle() {
		// TODO Auto-generated method stub
		return "验证手机";
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.checkphone_next:
			// goActivity(RegisterDriverActivity.class);
			next();
			break;
		case R.id.checkphone_get_checknumber:
			hideSoftInputMethod();
			getCheckNumber();
			break;
		default:
			break;
		}
	}

	private void getCheckNumber() {
		String phoneStr = phone.getText().toString();
		if (!ToolUtil.isMobileNumber(phoneStr)) {
			showToast("请输入11位有效手机号码");
			return;
		}
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("key", "send_sms");
		params.put("mobile", phoneStr);
		params.put("code_type", "reg");
		String action = ToolUtil.encryptionUrlParams(params);
		// 计时器开始
		countDownTimer.start();
		NetUtil.getInstance().doGet(NetUtil.DRIVER_URL + action,
				new RequestCallBack<String>() {
					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						String result = responseInfo.result;
						StatusBean status = GsonUtil.jsonToObjct(result,
								StatusBean.class);
						if ("0".equals(status.status)) {
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

	private void next() {
		final String phoneStr = phone.getText().toString();
		final String newPasswordStr = newPassword.getText().toString();
		String confirmPasswordStr = confirmPassword.getText().toString();
		String checkNumberStr = checkNumber.getText().toString();
		if (!ToolUtil.isMobileNumber(phoneStr)) {
			showToast("请输入11位有效手机号码");
			return;
		}
		if (newPasswordStr.length() < 6) {
			showToast("请输入6位以上的密码");
			return;
		}
		if (!newPasswordStr.equals(confirmPasswordStr)) {
			showToast("新密码和确认密码不一致");
			return;
		}
		// 验证获得的验证码
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("key", "verify_sms_code");
		params.put("code_type", "reg");
		params.put("mobile", phoneStr);
		params.put("code", checkNumberStr);
		String action = ToolUtil.encryptionUrlParams(params);
		NetUtil.getInstance().doGet(NetUtil.DRIVER_URL + action,
				new RequestCallBack<String>() {
					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						String result = responseInfo.result;
						StatusBean status = GsonUtil.jsonToObjct(result,
								StatusBean.class);
						if ("0".equals(status.status)) {
							Bundle data = new Bundle();
							data.putString("check_phone", phoneStr);
							data.putString("check_password", newPasswordStr);
							goActivity(RegisterDriverActivity.class, data);
							CheckPhoneActivity.this.finish();
						} else {
							showToast(status.msg);
						}
					}

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						showToast("验证失败");
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
