package com.johan.subuwuliu;

import java.util.HashMap;

import com.johan.subuwuliu.application.DiDiApplication;
import com.johan.subuwuliu.bean.DriverInfoBean;
import com.johan.subuwuliu.bean.StatusBean;
import com.johan.subuwuliu.dialog.ConfirmUnbindingDialog;
import com.johan.subuwuliu.dialog.OnClickOkListener;
import com.johan.subuwuliu.dialog.SelectUnbindingWayWindow;
import com.johan.subuwuliu.dialog.SelectUnbindingWayWindow.OnSelectedUnbindingWayListener;
import com.johan.subuwuliu.dialog.WaitingDialog;
import com.johan.subuwuliu.fragment.ManagerCarBadDataFragment;
import com.johan.subuwuliu.fragment.ManagerCarGoodDataFragment;
import com.johan.subuwuliu.qiniu.Myupdate;
import com.johan.subuwuliu.server.BackgroundServer;
import com.johan.subuwuliu.server.ForegroundServer;
import com.johan.subuwuliu.util.GsonUtil;
import com.johan.subuwuliu.util.NetUtil;
import com.johan.subuwuliu.util.ToastUtil;
import com.johan.subuwuliu.util.ToolUtil;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.umeng.analytics.MobclickAgent;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.Gravity;
import android.view.View;

public class ManagerCarActivity extends AppTheme2Activity {

	private static final int MODE_UNBINDING_CAR = 1;
	private static final int MODE_UNBINDING_COMPANY = 2;

	private ConfirmUnbindingDialog confirmUnbindingDialog;

	private String carCid;
	private String carNumber;

	private ManagerCarGoodDataFragment managerCarGoodDataFragment;
	private ManagerCarBadDataFragment managerCarBadDataFragment;

	@Override
	public String getThemeTitle() {
		// TODO Auto-generated method stub
		return "车辆管理";
	}

	@Override
	public String getThemeTip() {
		// TODO Auto-generated method stub
		return "解绑";
	}

	@Override
	public void clickThemeTip() {
		// TODO Auto-generated method stub
		unbinding();
	}

	@Override
	public int getContentView() {
		// TODO Auto-generated method stub
		return R.layout.activity_managercar;
	}

	@Override
	public void findId() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init() {
		showThemeTip(false);
		SharedPreferences sharedPreferences = getSharedPreferences(
				DiDiApplication.NAME_OF_SHARED_PREFERENCES,
				Context.MODE_PRIVATE);
		String driverInfoData = sharedPreferences.getString(
				DiDiApplication.KEY_OF_DRIVER_INFO_DATA,
				DiDiApplication.VALUE_UNKOWN);
		if (DiDiApplication.VALUE_UNKOWN.equals(driverInfoData)) {
			finish();
			return;
		}
		DriverInfoBean driverInfo = GsonUtil.jsonToObjct(driverInfoData,
				DriverInfoBean.class);
		if (!"0".equals(driverInfo.car_status) || driverInfo == null) {
			// 初始化view
			managerCarBadDataFragment = new ManagerCarBadDataFragment();
			showFragment(managerCarBadDataFragment);
			return;
		}
		managerCarGoodDataFragment = new ManagerCarGoodDataFragment();
		showFragment(managerCarGoodDataFragment);
		carNumber = driverInfo.plate_number;
		carCid = driverInfo.cid;
		managerCarGoodDataFragment.setCarNumberInfo(driverInfo.plate_number);
		managerCarGoodDataFragment
				.setEngineNumberInfo(driverInfo.engine_number);
		managerCarGoodDataFragment.setCarTypeInfo(driverInfo.car_type);
		managerCarGoodDataFragment.setCarSizeInfo(driverInfo.sizes);
		
		managerCarGoodDataFragment.setCarPicture1Uri(Myupdate.DownloadDemo(driverInfo.car_picture1));
		managerCarGoodDataFragment.setCarPicture2Uri(Myupdate.DownloadDemo(driverInfo.car_picture2));
		managerCarGoodDataFragment.setCarPicture3Uri(Myupdate.DownloadDemo(driverInfo.car_picture3));
		managerCarGoodDataFragment.setCarPicture4Uri(Myupdate.DownloadDemo(driverInfo.car_picture4));
		managerCarGoodDataFragment.setCarInsuranceUri(Myupdate.DownloadDemo(driverInfo.car_safe));
		managerCarGoodDataFragment
				.setDriverLicense1Uri(Myupdate.DownloadDemo(driverInfo.driving_license1));
		managerCarGoodDataFragment
				.setDriverLicense2Uri(Myupdate.DownloadDemo(driverInfo.driving_license2));
		managerCarGoodDataFragment.setOperationUri(Myupdate.DownloadDemo(driverInfo.taxi_license));
		managerCarGoodDataFragment.setCustomsSupervisionUri(Myupdate.DownloadDemo(driverInfo.customs));
		showThemeTip(true);
	}

	private void unbinding() {
		if (confirmUnbindingDialog == null) {
			confirmUnbindingDialog = new ConfirmUnbindingDialog(
					ManagerCarActivity.this, carNumber);
			confirmUnbindingDialog
					.setOnClickOkListener(new OnClickOkListener() {
						@Override
						public void clickOk() {
							unbinding(1);//////1可以忽略
						}
					});
		}
		confirmUnbindingDialog.show();
	}

	// ////////解绑车辆
	private void unbinding(final int mode) {
		final SharedPreferences sharedPreferences = getSharedPreferences(
				DiDiApplication.NAME_OF_SHARED_PREFERENCES, MODE_PRIVATE);
		final WaitingDialog waitingDialog = new WaitingDialog(
				ManagerCarActivity.this);
		waitingDialog.show(findViewById(R.id.managercares_layout));
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("key", "relieve_bind");
		params.put("username", getLoginName());
		params.put("pwd", getLoginPassword());
		params.put("cid", carCid);
		String action = ToolUtil.encryptionUrlParams(params);
		NetUtil.getInstance().doGet(NetUtil.DRIVER_URL + action,
				new RequestCallBack<String>() {
					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						System.out.println("解绑的结果：" + responseInfo.result);
						waitingDialog.dismiss();
						StatusBean unbindingResult = GsonUtil.jsonToObjct(
								responseInfo.result, StatusBean.class);
						if ("0".equals(unbindingResult.status)) {
							showToast("解绑成功");
							finish();
							HomeActivity.activity.finish();
							HashMap<String, String> params = new HashMap<String, String>();
							params.put("key", "driver_info");
							params.put("username", getLoginName());
							params.put("pwd", getLoginPassword());
							params.put("timestamp", "0");
							String action = ToolUtil
									.encryptionUrlParams(params);
							NetUtil.getInstance().doGet(
									NetUtil.DRIVER_URL + action,
									new RequestCallBack<String>() {
										@Override
										public void onSuccess(
												ResponseInfo<String> responseInfo) {
											DriverInfoBean driverInfo = GsonUtil
													.jsonToObjct(
															responseInfo.result,
															DriverInfoBean.class);
											if ("0".equals(driverInfo.status)) {
												// 正常获取到司机信息
												Editor editor = sharedPreferences
														.edit();
												editor.putString(
														DiDiApplication.KEY_OF_DRIVER_INFO_TIMESTAMP,
														driverInfo.timestamp);
												editor.putString(
														DiDiApplication.KEY_OF_DRIVER_INFO_DATA,
														responseInfo.result);
												editor.commit();
											} else {
												showToast(driverInfo.msg);
											}
										}

										@Override
										public void onFailure(
												HttpException exception,
												String info) {
										}
									});
							returnLoginActivity();

						} else {
							showToast(unbindingResult.msg);
						}
						waitingDialog.dismiss();
					}

					@Override
					public void onFailure(HttpException exception, String info) {
						showToast(info);
					}
				});
	}

	@Override
	public int getContainerId() {
		// TODO Auto-generated method stub
		return R.id.managercar_fragment;
	}

	public View getParentView() {
		return findViewById(R.id.managercares_layout);
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
		// // 启动前程服务
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
		MeActivity.activity.finish();
		finish();
		// 跳转带登录页面
		Intent loginIntent = new Intent(this, LoginActivity.class);
		loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 清除上一步缓存
		startActivity(loginIntent);
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
