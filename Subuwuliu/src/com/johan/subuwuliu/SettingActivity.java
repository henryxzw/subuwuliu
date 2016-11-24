package com.johan.subuwuliu;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.easemob.EMCallBack;
import com.easemob.chatuidemo.DemoHXSDKHelper;
import com.johan.subuwuliu.application.DiDiApplication;
import com.johan.subuwuliu.bean.GengXing;
import com.johan.subuwuliu.bean.QianBaoBean;
import com.johan.subuwuliu.dialog.WaitingDialog;
import com.johan.subuwuliu.fragment.HomeBindingCarFragment;
import com.johan.subuwuliu.method.MethodConfig;
import com.johan.subuwuliu.server.BackgroundServer;
import com.johan.subuwuliu.server.ForegroundServer;
import com.johan.subuwuliu.server.MusicBService;
import com.johan.subuwuliu.util.GsonUtil;
import com.johan.subuwuliu.util.NetUtil;
import com.johan.subuwuliu.util.ToastUtil;
import com.johan.subuwuliu.util.ToolUtil;
import com.johan.subuwuliu.util.Utils_GX;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class SettingActivity extends AppThemeActivity implements
		OnClickListener {

	private TextView about, currentVersion, newVersion, logout;
	private Dialog logoutDialog;
	private String old_Verson;
	public Activity settingactivity;

	// //////////////////
	@Override
	public String getThemeTitle() {
		// TODO Auto-generated method stub
		return "设置";
	}

	@Override
	public int getContentView() {
		// TODO Auto-generated method stub
		return R.layout.activity_setting;
	}

	@Override
	public void findId() {
		// TODO Auto-generated method stub
		about = (TextView) findViewById(R.id.setting_about);
		currentVersion = (TextView) findViewById(R.id.setting_currentversion);
		newVersion = (TextView) findViewById(R.id.setting_new_version);
		logout = (TextView) findViewById(R.id.setting_logout);
	}

	@Override
	public void init() {
		settingactivity = this;
		currentVersion.setText("当前版本：V" + getVersion());
		about.setOnClickListener(this);
		logout.setOnClickListener(this);
	}

	/**
	 * 获取当前的版本号
	 * 
	 * @return
	 */
	public String getVersion() {
		try {
			PackageManager manager = this.getPackageManager();
			PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
			String version = info.versionName;
			old_Verson = info.versionName;
			return version;
		} catch (Exception e) {
			e.printStackTrace();
			return "检测版本号错误";
		}
	}

	public void checkVersion(View view) {
		final WaitingDialog dialog = new WaitingDialog(this);
		dialog.show(findViewById(R.id.setting_linear));
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
												vername,settingactivity
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
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.setting_about:
			goActivity(AboutCheDuiActivity.class);
			break;
		case R.id.setting_logout:
			showLogoutDialog();
			break;
		default:
			break;
		}
	}

	private void showLogoutDialog() {
		if (logoutDialog == null) {
			logoutDialog = new Dialog(this, R.style.DiDiDialog);
			logoutDialog.setContentView(R.layout.dialog_account_error);
			TextView contentView = (TextView) logoutDialog
					.findViewById(R.id.accounterror_content);
			contentView.setText("是否退出登录");
			Button button = (Button) logoutDialog
					.findViewById(R.id.accounterror_but_ok);
			Button cancelButton = (Button) logoutDialog
					.findViewById(R.id.accounterror_but_cancel);
			cancelButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					logoutDialog.dismiss();
				}
			});
			button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					SharedPreferences sharedPreferences_C = getSharedPreferences(
							"sijizhangtai", Context.MODE_PRIVATE);
					String aa = sharedPreferences_C.getString("sijizhangtai_a",
							"");
					// ToastUtil.show(settingactivity, aa);
					if (aa.equals("online")) {
						sijixiaixan();
					} else {
						returnLoginActivity();
					}

					logoutDialog.dismiss();
				}
			});

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
		if (logoutDialog.isShowing())
			return;
		logoutDialog.show();
	}

	private void returnLoginActivity() {
		try {
			SharedPreferences sharedPreferences_start = getSharedPreferences(
					"startfuwu", Context.MODE_PRIVATE);
			sharedPreferences_start.edit().putString("startfuwu", "1002")
					.commit();
			// 清除password的保存，不让自动登录
			SharedPreferences sharedPreferences = getSharedPreferences(
					DiDiApplication.NAME_OF_SHARED_PREFERENCES,
					Context.MODE_PRIVATE);
			sharedPreferences
					.edit()
					.putString(DiDiApplication.KEY_OF_LOGIN_PWD,
							DiDiApplication.VALUE_UNKOWN).commit();
			// ////修改司机下线状态
			sharedPreferences
					.edit()
					.putBoolean(
							DiDiApplication.KEY_OF_NEED_UPDATE_DRIVER_STATE,
							true).commit();
			// 清除application
			DiDiApplication.getInstance().setUserName(
					DiDiApplication.VALUE_UNKOWN);
			DiDiApplication.getInstance().setPassword(
					DiDiApplication.VALUE_UNKOWN);
			// // 启动前程服务
			Intent intent = new Intent(this, ForegroundServer.class);
			intent.putExtra(ForegroundServer.FOREGROUND_COMMAND_NAME,
					ForegroundServer.COMMAND_OPERATION_FOREGROUND);
			intent.putExtra("server_state", false);
			startService(intent);
			// 跳转带登录页面
			Intent loginIntent = new Intent(this, LoginActivity.class);
			loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
					| Intent.FLAG_ACTIVITY_NEW_TASK); 
			startActivity(loginIntent);
			HomeActivity.stopTrace();
			MethodConfig.hasTrack = false;
			stopService(new Intent(this,MusicBService.class));
			MeActivity.activity.finish();
			HomeActivity.activity.finish();
			finish();
			
		} catch (Exception e) {
			// TODO: handle exception
			Toast.makeText(settingactivity, "退出登录出错",Toast.LENGTH_SHORT).show();
			Intent loginIntent = new Intent(this, LoginActivity.class);
			loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
					| Intent.FLAG_ACTIVITY_NEW_TASK); 
			startActivity(loginIntent);
			System.out.println("退出登录出错");
		}
	}

	private int i = 0;

	private void sijixiaixan() {
		final WaitingDialog dialog = new WaitingDialog(this);
		dialog.show(findViewById(R.id.setting_linear));
		// 上传司机下线状态
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("key", "downline");
			params.put("username", getLoginName());
			params.put("pwd", getLoginPassword());
			String action = ToolUtil.encryptionUrlParams(params);
			NetUtil.getInstance().doGet(NetUtil.DRIVER_URL + action,
					new RequestCallBack<String>() {
						@Override
						public void onFailure(HttpException exception,
								String info) {
							// TODO Auto-generated method stub
							System.out.println("司机下线失败，原因：" + info);
							if (i > 3) {
								dialog.dismiss();
								ToastUtil.show(settingactivity, "下线失败，请手动下线");
								return;
							}
							i++;
							sijixiaixan();
						}

						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
							// TODO Auto-generated method stub
							System.out.println("司机下线成功：" + responseInfo.result);
							QianBaoBean loginResult = GsonUtil.jsonToObjct(
									responseInfo.result, QianBaoBean.class);
							System.out.println("---------->>"
									+ responseInfo.result);
							if (loginResult.status.equals("0")) {
								dialog.dismiss();
								returnLoginActivity();
							} else {
								if (i > 3) {
									dialog.dismiss();
									ToastUtil.show(settingactivity,
											"下线失败，请手动下线");
									return;
								}
								i++;
								sijixiaixan();
							}
						}
					});
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("下线出问题");
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		HomeActivity.clien_onDestroy();
	}
}
