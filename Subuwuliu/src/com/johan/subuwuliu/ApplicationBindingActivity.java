package com.johan.subuwuliu;

import java.util.HashMap;

import com.johan.subuwuliu.application.DiDiApplication;
import com.johan.subuwuliu.bean.StatusBean;
import com.johan.subuwuliu.dialog.WaitingDialog;
import com.johan.subuwuliu.server.BackgroundServer;
import com.johan.subuwuliu.server.ForegroundServer;
import com.johan.subuwuliu.util.GsonUtil;
import com.johan.subuwuliu.util.NetUtil;
import com.johan.subuwuliu.util.ToolUtil;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.method.ReplacementTransformationMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ApplicationBindingActivity extends AppThemeActivity implements
		OnClickListener {

	private EditText bindingRegisterCode, bindingCarNumber;

	private Button bindingOK, bindingCancel;

	private String submitLoginName, submitLoginPassword;

	private WaitingDialog waitingDialog;

	@Override
	public int getContentView() {
		// TODO Auto-generated method stub
		return R.layout.activity_applicationbinding;
	}

	@Override
	public String getThemeTitle() {
		// TODO Auto-generated method stub
		return "申请绑定";
	}

	@Override
	public void findId() {
		// TODO Auto-generated method stub
		bindingRegisterCode = (EditText) findViewById(R.id.applicationbinding_register_code);
		bindingCarNumber = (EditText) findViewById(R.id.applicationbinding_car_number);
		bindingRegisterCode
				.setTransformationMethod(new AllCapTransformationMethod());
		bindingCarNumber
				.setTransformationMethod(new AllCapTransformationMethod());
		bindingOK = (Button) findViewById(R.id.application_but_ok);
		bindingCancel = (Button) findViewById(R.id.application_but_cancel);
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		SharedPreferences sharedPreferences = getSharedPreferences(
				DiDiApplication.NAME_OF_SHARED_PREFERENCES, MODE_PRIVATE);
		submitLoginName = sharedPreferences
				.getString(DiDiApplication.KEY_OF_LOGIN_NAME,
						DiDiApplication.VALUE_UNKOWN);
		submitLoginPassword = sharedPreferences.getString(
				DiDiApplication.KEY_OF_LOGIN_PWD, DiDiApplication.VALUE_UNKOWN);
		bindingOK.setOnClickListener(this);
		bindingCancel.setOnClickListener(this);
		waitingDialog = new WaitingDialog(ApplicationBindingActivity.this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.application_but_ok:
			binding();
			break;
		case R.id.application_but_cancel:
			ApplicationBindingActivity.this.finish();
			break;
		default:
			break;
		}
	}

	private void binding() {
		String submitBindingRegisterCode = bindingRegisterCode.getText()
				.toString();
		String submitBindingCarNumber = bindingCarNumber.getText().toString();
		if (submitBindingRegisterCode.length() == 0) {
			showToast("请输入您所属企业/车队的注册编码");
			return;
		}
		if (submitBindingCarNumber.length() == 0) {
			showToast("请输入要绑定的车牌号码");
			return;
		}
		waitingDialog.show(findViewById(R.id.applicationbinding_layout));
		// 开始提交
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("key", "bind_company");
		params.put("username", submitLoginName);
		params.put("pwd", submitLoginPassword);
		params.put("register_no", submitBindingRegisterCode);
		params.put("plate_number", submitBindingCarNumber);
		String action = ToolUtil.encryptionUrlParams(params);
		NetUtil.getInstance().doGet(NetUtil.DRIVER_URL + action,
				new RequestCallBack<String>() {
					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						System.out.println("绑定结果：" + responseInfo.result);
						StatusBean bindingResult = GsonUtil.jsonToObjct(
								responseInfo.result, StatusBean.class);
						if ("0".equals(bindingResult.status)) {
							Dialog shenghe = new Dialog(
									ApplicationBindingActivity.this,
									R.style.DiDiDialog);
							shenghe.setContentView(R.layout.dengdaishenghe);
							TextView textview = (TextView) shenghe
									.findViewById(R.id.textView_shenghe);
							textview.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View arg0) {
									// TODO Auto-generated
									// method stub
									returnLoginActivity();
									showToast("绑定成功");
									ApplicationBindingActivity.this.finish();
								}
							});
							shenghe.show();

						} else {
							showToast(bindingResult.msg);
						}
						waitingDialog.dismiss();
					}

					@Override
					public void onFailure(HttpException exception, String info) {
						waitingDialog.dismiss();
					}
				});
	}

	public class AllCapTransformationMethod extends
			ReplacementTransformationMethod {
		@Override
		protected char[] getOriginal() {
			char[] original = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i',
					'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u',
					'v', 'w', 'x', 'y', 'z' };
			return original;
		}

		@Override
		protected char[] getReplacement() {
			char[] replacement = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I',
					'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U',
					'V', 'W', 'X', 'Y', 'Z' };
			return replacement;
		}
	}

	private void returnLoginActivity() {
		// 清除password的保存，不让自动登录
		SharedPreferences sharedPreferences = getSharedPreferences(
				DiDiApplication.NAME_OF_SHARED_PREFERENCES,
				Context.MODE_PRIVATE);
		sharedPreferences
				.edit()
				.putString(DiDiApplication.KEY_OF_LOGIN_PWD,
						DiDiApplication.VALUE_UNKOWN).commit();
		// 清除application
		DiDiApplication.getInstance().setUserName(DiDiApplication.VALUE_UNKOWN);
		DiDiApplication.getInstance().setPassword(DiDiApplication.VALUE_UNKOWN);
		// 启动前程服务
		Intent intent = new Intent(this, ForegroundServer.class);
		intent.putExtra(ForegroundServer.FOREGROUND_COMMAND_NAME,
				ForegroundServer.COMMAND_OPERATION_FOREGROUND);
		intent.putExtra("server_state", false);
		startService(intent);
		// 下线处理
		Intent downlineIntent = new Intent(this, BackgroundServer.class);
		downlineIntent.putExtra(BackgroundServer.BACKGROUND_COMMAND_NAME,
				BackgroundServer.COMMAND_DONWLINE);
		startService(downlineIntent);
		// 跳转带登录页面
		Intent loginIntent = new Intent(this, LoginActivity.class);
		loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
				| Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(loginIntent);
	}
}
