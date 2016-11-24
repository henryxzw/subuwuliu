package com.johan.subuwuliu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.LocationMode;
import com.baidu.trace.OnStartTraceListener;
import com.baidu.trace.OnStopTraceListener;
import com.baidu.trace.Trace;
import com.easemob.EMCallBack;
import com.easemob.applib.controller.HXSDKHelper;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.easemob.chatuidemo.Constant;
import com.easemob.chatuidemo.DemoHXSDKHelper;
import com.easemob.chatuidemo.db.UserDao;
import com.easemob.chatuidemo.domain.User;
import com.hp.hpl.sparta.xpath.ThisNodeTest;
import com.johan.subuwuliu.application.DiDiApplication;
import com.johan.subuwuliu.bean.DriverInfoBean;
import com.johan.subuwuliu.bean.GengXing;
import com.johan.subuwuliu.fragment.HomeBindingCarFragment;
import com.johan.subuwuliu.fragment.HomeNoBindingCarFragment;
import com.johan.subuwuliu.method.MethodConfig;
import com.johan.subuwuliu.server.BackgroundServer;
import com.johan.subuwuliu.server.ForegroundServer;
import com.johan.subuwuliu.util.FormatUtil;
import com.johan.subuwuliu.util.GsonUtil;
import com.johan.subuwuliu.util.NetUtil;
import com.johan.subuwuliu.util.ToastUtil;
import com.johan.subuwuliu.util.ToolUtil;
import com.johan.subuwuliu.util.Utils_GX;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.util.LogUtils;
import com.umeng.analytics.MobclickAgent;

public class HomeActivity extends AppActivity implements OnClickListener {

	private String old_Verson;
	public static final String RECEIVER_UNREAD_ACTION = "com.johan.subuwuliu.receiver.unread.action";

	private TextView findView;
	private ImageView meView;

	private Dialog goSettingGPSDialog, goSettingSystemTimeDialog,
			conflictDialog, accountRemovedDialog;
	private DriverInfoBean driverInfo;
	private HomeBindingCarFragment homeBindingCarFragment;

	private String loginName, loginPassword;
	public static Activity activity;
	public static LBSTraceClient client = null;
	// ///开启服务
	public static OnStartTraceListener startTraceListener;
	// //关闭服务
	public static OnStopTraceListener stopTraceListener;
	// /判断鹰眼服务是否开启成功
	public static Trace trace = null;
	public static long serviceId;
	public static int traceType;
	public static String entityName;

	// ///启动关闭
	public static void startfu() {
		final SharedPreferences sharedPreferences_start = DiDiApplication.applicationContext
				.getSharedPreferences("startfuwu", Context.MODE_PRIVATE);
		SharedPreferences sharedPreferences = DiDiApplication.applicationContext
				.getSharedPreferences(
						DiDiApplication.NAME_OF_SHARED_PREFERENCES,
						Context.MODE_PRIVATE);
		String driverInfoData = sharedPreferences.getString(
				DiDiApplication.KEY_OF_DRIVER_INFO_DATA,
				DiDiApplication.VALUE_UNKOWN);
		DriverInfoBean driverInfo = GsonUtil.jsonToObjct(driverInfoData,
				DriverInfoBean.class);
		// 实例化轨迹服务客户端		
		client = new LBSTraceClient(DiDiApplication.applicationContext);
		client.setLocationMode(LocationMode.High_Accuracy);
		client.setInterval(5, 15);
		// 鹰眼服务ID
		serviceId = 114430;
		// 114554
		// entity标识
		entityName = driverInfo.driver_id;
		// 轨迹服务类型（0 : 不上传位置数据，也不接收报警信息； 1 : 不上传位置数据，但接收报警信息；2 : 上传位置数据，且接收报警信息）
		traceType = 2;
		// 实例化轨迹服务
		trace = new Trace(DiDiApplication.applicationContext, serviceId,
				entityName, traceType);
		System.out.println("----正在进行的运单(正在启动服务)---111-->>> ");
        
		// 实例化开启轨迹服务回调接口
		startTraceListener = new OnStartTraceListener() {
			// 开启轨迹服务回调接口（arg0 : 消息编码，arg1 : 消息内容，详情查看类参考）
			@Override
			public void onTraceCallback(int arg0, String arg1) {
				switch (arg0) {
				case 0:
				case 1006:
				{
					sharedPreferences_start.edit().putString("startfuwu", "1001").commit();
					//MethodConfig.ShowToast("轨迹服务开启");
				}
					break;
				case 10002:
				{
					//MethodConfig.ShowToast("上传参数错误");
				}
				break;
				case 10003:
				{
					MethodConfig.ShowToast("网络连接失败");
				}
					break;
				case 10004:
				{
					MethodConfig.ShowToast("网络未开启");
				}
					break;
				case 10005:
				{
					//MethodConfig.ShowToast("服务正在开启");
				}
					break;
				case 10007:
				{
					//MethodConfig.ShowToast("轨迹服务正在停止");
				}
					break;
				case 10008:
				case 10009:
				{
					//MethodConfig.ShowToast("轨迹服务正在缓存轨迹点");
				}
				break;
				default:
					break;
				}
			}

			// 轨迹服务推送接口（用于接收服务端推送消息，arg0 : 消息类型，arg1 : 消息内容，详情查看类参考）
			@Override
			public void onTracePushCallback(byte arg0, String arg1) {
				LogUtils.e(arg1);
			}
		};

		// 实例化停止轨迹服务回调接口
		stopTraceListener = new OnStopTraceListener() {
			// 轨迹服务停止成功
			@Override
			public void onStopTraceSuccess() {
				System.out.println("-------服务停止成功----------->>");
				sharedPreferences_start.edit().putString("startfuwu", "")
						.commit();
			}

			@Override
			public void onStopTraceFailed(int arg0, String arg1) {
				LogUtils.e(arg1);
			}
		};
	}
	
    

	// 开始轨迹服务
	public static void startTrace() {
		LogUtils.e("开始轨迹上传");
		//ToastUtil.show(activity, "开始轨迹上传");	
		client.startTrace(trace, startTraceListener);
	}

	// 结束轨迹服务
	public static void stopTrace() {
//		MethodConfig.ShowToast("停止轨迹");
		client.stopTrace(trace, stopTraceListener);
		SharedPreferences sharedPreferences_start = DiDiApplication.applicationContext
				.getSharedPreferences("startfuwu", Context.MODE_PRIVATE);
		sharedPreferences_start.edit().putString("startfuwu", "").commit();
	}

	// onDestroy轨迹服务
	public static void clien_onDestroy() {
		client.onDestroy();
	}

	@Override
	public int getContentView() {
		return R.layout.activity_home;
	}

	@Override
	public int getStatuBarColor() {
		// TODO Auto-generated method stub
		return Color.parseColor("#1c2134");
	}

	@Override
	public void inResume() {

	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		// TODO Auto-generated method stub
		if (getIntent().getBooleanExtra("conflict", false)) {
			showConflictDialog();
			System.out.println("收到账号在别处登录");
		} else if (getIntent().getBooleanExtra(Constant.ACCOUNT_REMOVED, false)) {
			showAccountRemovedDialog();
			System.out.println("收到账号被移除");
		}
	}

	/**
	 * 显示帐号在别处登录dialog
	 */
	private void showConflictDialog() {
		if (conflictDialog == null) {
			conflictDialog = new Dialog(this, R.style.DiDiDialog);
			conflictDialog.setContentView(R.layout.dialog_account_error);
			TextView contentView = (TextView) conflictDialog
					.findViewById(R.id.accounterror_content);
			contentView.setText("您的账号在其他地方登录");
			Button button = (Button) conflictDialog
					.findViewById(R.id.accounterror_but_ok);
			Button cancelButton = (Button) conflictDialog
					.findViewById(R.id.accounterror_but_cancel);
			cancelButton.setVisibility(ViewAnimator.GONE);
			button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					returnLoginActivity();
					conflictDialog.dismiss();
				}
			});
			conflictDialog.setOnKeyListener(new OnKeyListener() {
				@Override
				public boolean onKey(DialogInterface dialog, int keyCode,
						KeyEvent event) {
					// TODO Auto-generated method stub
					if (keyCode == KeyEvent.KEYCODE_BACK) {
						returnLoginActivity();
						return true;
					}
					return false;
				}
			});
			conflictDialog.setCancelable(false);
		}
		if (conflictDialog.isShowing())
			return;
		conflictDialog.show();
	}

	/**
	 * 帐号被移除的dialog
	 */
	private void showAccountRemovedDialog() {
		if (accountRemovedDialog == null) {
			accountRemovedDialog = new Dialog(this, R.style.DiDiDialog);
			accountRemovedDialog.setContentView(R.layout.dialog_account_error);
			TextView contentView = (TextView) accountRemovedDialog
					.findViewById(R.id.accounterror_content);
			contentView.setText("您的账号已被移除");
			Button button = (Button) accountRemovedDialog
					.findViewById(R.id.accounterror_but_ok);
			Button cancelButton = (Button) accountRemovedDialog
					.findViewById(R.id.accounterror_but_cancel);
			cancelButton.setVisibility(ViewAnimator.GONE);
			button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					returnLoginActivity();
					accountRemovedDialog.dismiss();
				}
			});
			accountRemovedDialog.setOnKeyListener(new OnKeyListener() {
				@Override
				public boolean onKey(DialogInterface dialog, int keyCode,
						KeyEvent event) {
					// TODO Auto-generated method stub
					if (keyCode == KeyEvent.KEYCODE_BACK) {
						returnLoginActivity();
						return true;
					}
					return false;
				}
			});
			accountRemovedDialog.setCancelable(false);
		}
		if (accountRemovedDialog.isShowing())
			return;
		accountRemovedDialog.show();
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
		// 返回LoginActivity
		Intent loginIntent = new Intent(this, LoginActivity.class);
		loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
				| Intent.FLAG_ACTIVITY_NEW_TASK);
		loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 清除上一步缓存
		startActivity(loginIntent);
	}

	private final TagAliasCallback mAliasCallback = new TagAliasCallback() {
		@Override
		public void gotResult(int code, String alias, Set<String> tags) {
			switch (code) {
			case 0:
				break;
			case 6002:
				break;
			default:
				break;
			}
		}
	};

	@Override
	public void findId() {
		activity = this;
		MobclickAgent.onProfileSignIn(getLoginName());// //用户使用自有账号登录时，可以这样统计：
		meView = (ImageView) findViewById(R.id.home_title_layout_me);
		findView = (TextView) findViewById(R.id.home_title_layout_find);
	}

	@Override
	public void init() {
		// // 友盟检测更新
		gx();
		final SharedPreferences sharedPreferences = getSharedPreferences(
				DiDiApplication.NAME_OF_SHARED_PREFERENCES,
				Context.MODE_PRIVATE);
		loginName = sharedPreferences
				.getString(DiDiApplication.KEY_OF_LOGIN_NAME,
						DiDiApplication.VALUE_UNKOWN);
		loginPassword = sharedPreferences.getString(
				DiDiApplication.KEY_OF_LOGIN_PWD, DiDiApplication.VALUE_UNKOWN);
		String driverInfoData = sharedPreferences.getString(
				DiDiApplication.KEY_OF_DRIVER_INFO_DATA,
				DiDiApplication.VALUE_UNKOWN);
		if (DiDiApplication.VALUE_UNKOWN.equals(driverInfoData)) {
			finish();
			return;
		}
		// ///绑定鹰眼服务
		startfu();
		driverInfo = GsonUtil.jsonToObjct(driverInfoData, DriverInfoBean.class);
		// /////////开始时间driverInfo.card_available_time
		// //ToolUtil.settime(driverInfo.card_available_time, );
		FragmentManager manager = getFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		if (driverInfo != null && "0".equals(driverInfo.car_status)) {
			homeBindingCarFragment = new HomeBindingCarFragment();
			transaction.add(R.id.home_layout, homeBindingCarFragment);
		} else {
			transaction.add(R.id.home_layout, new HomeNoBindingCarFragment());
		}
		transaction.commit();
		meView.setOnClickListener(this);
		findView.setOnClickListener(this);
		SharedPreferences shared = getSharedPreferences(
				DiDiApplication.NAME_OF_SHARED_PREFERENCES, MODE_PRIVATE);
		String phone = shared.getString(DiDiApplication.KEY_OF_LOGIN_NAME,
				DiDiApplication.VALUE_UNKOWN);
		JPushInterface.setAliasAndTags(getApplicationContext(), phone, null,
				mAliasCallback);
		System.out.println("--------判断推送是否停止------------>>>"
				+ JPushInterface.isPushStopped(getApplicationContext()));
		// 登录环信
		new LoginHuanxinTask().execute();
		// 校准服务器时间
		new GetServerTimeTask().execute();

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
												vername,HomeActivity.this
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
		case R.id.home_title_layout_me:
			goActivity(MeActivity.class);
			break;
		case R.id.home_title_layout_find:
			goActivity(FindActivity.class);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		new LoginHuanxinTask().execute();
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		try {
			MobclickAgent.onResume(this); // 统计时长
			// ////////////////////////启动得到位置的服务
			Intent locationIntent = new Intent(getApplicationContext(),
					BackgroundServer.class);
			locationIntent.putExtra(BackgroundServer.BACKGROUND_COMMAND_NAME,
					BackgroundServer.COMMAND_GET_LOCATION);
			getApplicationContext().startService(locationIntent);
			// 极光推送初始化
			if (JPushInterface.isPushStopped(getApplicationContext())) {
				JPushInterface.resumePush(getApplicationContext());
				JPushInterface.setAliasAndTags(getApplicationContext(),
						getLoginName(), null, mAliasCallback);
			}

			if (driverInfo != null && "0".equals(driverInfo.car_status)) {
				initGps();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private void initGps() {
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		boolean isOpen = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		if (!isOpen) {
			showGoSettingDialog();
			return;
		}
	}

	private void showGoSettingDialog() {
		if (goSettingGPSDialog == null) {
			goSettingGPSDialog = new Dialog(this, R.style.DiDiDialog);
			goSettingGPSDialog.setContentView(R.layout.dialog_setting_gps);
			Button button = (Button) goSettingGPSDialog
					.findViewById(R.id.setting_gps_but);
			button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent settingIntent = new Intent();
					settingIntent
							.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
					settingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					try {
						startActivity(settingIntent);
					} catch (ActivityNotFoundException exception) {
						settingIntent.setAction(Settings.ACTION_SETTINGS);
						startActivity(settingIntent);
					}
					goSettingGPSDialog.dismiss();
				}
			});
			goSettingGPSDialog.setCancelable(false);
		}
		goSettingGPSDialog.show();
	}

	private void showGoSettingSystemtimeDialog() {
		if (goSettingSystemTimeDialog == null) {
			goSettingSystemTimeDialog = new Dialog(this, R.style.DiDiDialog);
			goSettingSystemTimeDialog
					.setContentView(R.layout.dialog_setting_systemtime);
			Button button = (Button) goSettingSystemTimeDialog
					.findViewById(R.id.setting_systemtime_but);
			button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent settingIntent = new Intent();
					settingIntent
							.setAction(android.provider.Settings.ACTION_DATE_SETTINGS);
					settingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(settingIntent);
					goSettingSystemTimeDialog.dismiss();
				}
			});
			goSettingSystemTimeDialog.setCancelable(false);
		}
		goSettingSystemTimeDialog.show();
	}

	/**
	 * 登录环信任务
	 * 
	 * @author fengyihuan
	 */
	class LoginHuanxinTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			System.out
					.println("正在登录环信-----------密码------" + getLoginPassword());
			// 调用sdk登陆方法登陆聊天服务器
			EMChatManager.getInstance().login(getLoginName(),
					getLoginPassword(), new EMCallBack() {
						@Override
						public void onSuccess() {
							System.out.println("登录成功--------------------");
							// 登陆成功，保存用户名密码
							DiDiApplication.getInstance().setUserName(
									getLoginName());
							DiDiApplication.getInstance().setPassword(
									getLoginPassword());
							try {
								// ** 第一次登录或者之前logout后再登录，加载所有本地群和回话
								// ** manually load all local groups and
								EMGroupManager.getInstance().loadAllGroups();
								EMChatManager.getInstance()
										.loadAllConversations();
								// 处理好友和群组
								initializeContacts();
								System.out.println("环信登录成功");
							} catch (Exception e) {
								e.printStackTrace();
								System.out.println("获取好友失败-------------------");
								runOnUiThread(new Runnable() {
									public void run() {
										DemoHXSDKHelper.getInstance().logout(
												true, null);
										showToast(getResources().getString(
												R.string.login_failure_failed));
									}
								});
							}
						}

						@Override
						public void onProgress(int progress, String status) {
						}

						@Override
						public void onError(final int code, final String message) {
							System.out.println("登录环信失败--》" + message);
							runOnUiThread(new Runnable() {
								public void run() {
									// pd.dismiss();
									// //////////登陆环信失败
									// showToast(getResources().getString(
									// R.string.Login_failed));
								}
							});
						}
					});
			return null;
		}
	}

	private void initializeContacts() {
		Map<String, User> userlist = new HashMap<String, User>();
		// 添加user"申请与通知"
		User newFriends = new User();
		newFriends.setUsername(Constant.NEW_FRIENDS_USERNAME);
		String strChat = getResources().getString(
				R.string.Application_and_notify);
		newFriends.setNick(strChat);

		userlist.put(Constant.NEW_FRIENDS_USERNAME, newFriends);
		// 添加"群聊"
		User groupUser = new User();
		String strGroup = getResources().getString(R.string.group_chat);
		groupUser.setUsername(Constant.GROUP_USERNAME);
		groupUser.setNick(strGroup);
		groupUser.setHeader("");
		userlist.put(Constant.GROUP_USERNAME, groupUser);

		// 添加"Robot"
		User robotUser = new User();
		String strRobot = getResources().getString(R.string.robot_chat);
		robotUser.setUsername(Constant.CHAT_ROBOT);
		robotUser.setNick(strRobot);
		robotUser.setHeader("");
		userlist.put(Constant.CHAT_ROBOT, robotUser);

		// 存入内存
		((DemoHXSDKHelper) HXSDKHelper.getInstance()).setContactList(userlist);
		// 存入db
		UserDao dao = new UserDao(HomeActivity.this);
		List<User> users = new ArrayList<User>(userlist.values());
		dao.saveContactList(users);
	}

	/**
	 * 异步校准服务器时间
	 * 
	 * @author fengyihuan
	 */
	public class GetServerTimeTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			HashMap<String, String> param = new HashMap<String, String>();
			param.put("key", "get_server_time");
			String action = ToolUtil.encryptionUrlParams(param);
			NetUtil.getInstance().doGet(NetUtil.DRIVER_URL + action,
					new RequestCallBack<String>() {
						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
							String serverTime = responseInfo.result;
							long serverTimeMillison = FormatUtil
									.getTime(serverTime);
							System.out.println("server time : "
									+ responseInfo.result);
							long distanceTime = Math.abs(System
									.currentTimeMillis() - serverTimeMillison);
							System.out.println("distance time : "
									+ distanceTime);
							double time = ToolUtil.settime(
									serverTime.split("\\ ")[0],
									driverInfo.card_available_time);

							if (time < 10 && time > 1) {
								ToolUtil.dilog(activity, "驾驶证"
										+ String.valueOf(time).split("\\.")[0]
										+ "天后过期");
							}
							if (distanceTime > 30 * 60 * 1000) { // 如果相差30分钟，提示
								showGoSettingSystemtimeDialog();
							}

						}

						@Override
						public void onFailure(HttpException exception,
								String info) {
							System.out.println("校验服务器时间出错" + info);
						}
					});
			return null;
		}
	}

	public View getHomeLayout() {
		return findViewById(R.id.home_layout);
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	private long exetime = 0;

	@Override
	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// if (keyCode == KeyEvent.KEYCODE_BACK
	// && event.getAction() == KeyEvent.ACTION_DOWN) {
	// if ((System.currentTimeMillis() - exetime) > 2000) {
	// // Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
	// exetime = System.currentTimeMillis();
	// } else {
	//
	// Intent exit = new Intent(Intent.ACTION_MAIN); // 指定跳到系统桌面
	// exit.addCategory(Intent.CATEGORY_HOME);
	// exit.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 清除上一步缓存
	// startActivity(exit);// 开始跳转
	// System.exit(0);
	// }
	// return true;
	// }
	// return super.onKeyDown(keyCode, event);
	// }
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		PackageManager pm = getPackageManager();
		ResolveInfo homeInfo = pm.resolveActivity(
				new Intent(Intent.ACTION_MAIN)
						.addCategory(Intent.CATEGORY_HOME), 0);
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			ActivityInfo ai = homeInfo.activityInfo;
			Intent startIntent = new Intent(Intent.ACTION_MAIN);
			startIntent.addCategory(Intent.CATEGORY_LAUNCHER);
			startIntent
					.setComponent(new ComponentName(ai.packageName, ai.name));
			startActivitySafely(startIntent);
			return true;
		} else
			return super.onKeyDown(keyCode, event);
	}

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

	/**
	 * 获取设备IMEI码
	 * 
	 * @param context
	 * @return
	 */
	public static String getImei(Context context) {
		String mImei = "NULL";
		try {
			mImei = ((TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
		} catch (Exception e) {
			System.out.println("获取IMEI码失败");
			mImei = "NULL";
		}
		return mImei;
	}
}
