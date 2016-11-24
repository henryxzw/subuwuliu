package com.johan.subuwuliu.server;

import java.util.HashMap;
import java.util.List;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.johan.subuwuliu.HomeActivity;
import com.johan.subuwuliu.R;
import com.johan.subuwuliu.application.DiDiApplication;
import com.johan.subuwuliu.bean.DriverInfoBean;
import com.johan.subuwuliu.bean.LocationInfo;
import com.johan.subuwuliu.database.LocationDatabase;
import com.johan.subuwuliu.handler.AbstractCommandHandler;
import com.johan.subuwuliu.method.MethodConfig;
import com.johan.subuwuliu.server.UploadLocationExecutor.PostCallback;
import com.johan.subuwuliu.util.FormatUtil;
import com.johan.subuwuliu.util.GsonUtil;
import com.johan.subuwuliu.util.ToolUtil;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;

public class ForegroundServer extends Service {

	public static final String FOREGROUND_COMMAND_NAME = "foreground_command_name";

	private static final int ONGOING_NOTIFICATION = 205;

	public static final String COMMAND_OPERATION_FOREGROUND = "start_stop_foreground";
	public static final String COMMAND_DO_LOCATION = "do_location";
	public static final String GUIJI = "guiji";
	private HashMap<String, AbstractCommandHandler> commandExecutor = new HashMap<String, AbstractCommandHandler>();

	private LocationClient client;

	private double lastLongitude, lastLatitude;

	private String driverId;

	private StringBuffer postDataBuffer = new StringBuffer();

//	private UploadLocationExecutor uploadLocationExecutor;
	public static boolean isCheck = false;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();

		System.out
				.println("初始化命令-------------ForegroundServer----------------");
		// 初始化命令
		commandExecutor.put(COMMAND_OPERATION_FOREGROUND,
				new ForegroundNotificationHandler(this));
		commandExecutor.put(COMMAND_DO_LOCATION, new DoLocationHandler(this));
		commandExecutor.put(GUIJI, new GuijiHandler(this));
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub

		if (intent != null) {
			String command = intent.getStringExtra(FOREGROUND_COMMAND_NAME);
			AbstractCommandHandler handler = commandExecutor.get(command);
			handler.doCommand(intent);
		} 
		return super.onStartCommand(intent, flags, startId);
	}

	private void aaa() {
		new Thread() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				while (isCheck) {
					if (HomeActivity.trace == null
							&& HomeActivity.client == null) {
						HomeActivity.startfu();
					}
					try {
						// //Thread.sleep(60 * 1000);
						Thread.sleep(20 * 60 * 1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						System.out.println("thread sleep failed");
					}
					System.out.println("--------20分钟执行一次服务开启---------->>>");
					if (!isCheck) {
						return;
					}
					HomeActivity.startTrace();
				}
			}

		}.start();
	}

	/*
	 * 启动轨迹
	 */

	class GuijiHandler extends AbstractCommandHandler {

		public GuijiHandler(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void doCommand(Intent intent) {
			// TODO Auto-generated method stub
		 isCheck = intent.getBooleanExtra("server_state", false);

			//aaa();
		}
	}

	/**
	 * 前台服务Handler
	 * 
	 * @author Administrator
	 */
	class ForegroundNotificationHandler extends AbstractCommandHandler {
		public ForegroundNotificationHandler(Context context) {
			super(context);
		}

		@Override
		public void doCommand(Intent intent) {
			// TODO Auto-generated method stub
			System.out.println("启动前台服务------------------------------------");
			boolean serverState = intent.getBooleanExtra("server_state", false);

			if (serverState) {
				Notification notification = new Notification(R.drawable.logo,
						getText(R.string.app_name), System.currentTimeMillis());
				Intent notificationIntent = new Intent(context,
						HomeActivity.class);
				PendingIntent pendingIntent = PendingIntent.getActivity(
						context, 0, notificationIntent, 0);
				notification.setLatestEventInfo(context,
						getText(R.string.app_name), "正在运行中", pendingIntent);
				startForeground(ONGOING_NOTIFICATION, notification);
				System.out.println("设置为前台服务-------------");
				try {
					SharedPreferences sharedPreferences_start = getSharedPreferences(
							"startfuwu", Context.MODE_PRIVATE); 
					if (sharedPreferences_start.getString("startfuwu", "").equals(
							"1001")) { 
						isCheck = true;
						//aaa();
					} else {
						isCheck = false;
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
				if(MethodConfig.hasTrack)
				{
				Intent intent2 = new Intent(context,MusicBService.class);
				startService(intent2);
				}
				
			} else {
				stopForeground(true);
				// 停止定位
				if (client != null && client.isStarted()) {
					String driverInfoJson = sharedPreferences.getString(
							DiDiApplication.KEY_OF_DRIVER_INFO_DATA,
							DiDiApplication.VALUE_UNKOWN);
					DriverInfoBean driverInfo = GsonUtil.jsonToObjct(
							driverInfoJson, DriverInfoBean.class);
					driverId = driverInfo.driver_id;
//					uploadLocationExecutor = UploadLocationExecutor
//							.getInstance();
//					uploadLocationExecutor.registerPostCallback(callback);
					client.stop();
					client.unRegisterLocationListener(dbListener);
					client = null;
					unregisterNetworkReceiver();
				}
			}
		}
	}

	/**
	 * 定位服务
	 * 
	 * @author Administrator
	 */
	class DoLocationHandler extends AbstractCommandHandler {
		public DoLocationHandler(Context context) {
			super(context);
		}

		@Override
		public void doCommand(Intent intent) {
			// TODO Auto-generated method stub
			boolean isStart = intent.getBooleanExtra("is_start", false);
			if (!isStart) {
				System.out.println("关闭定位服务--------------------------");
				if (client != null && client.isStarted()) {
					client.stop();
					client.unRegisterLocationListener(dbListener);
					client = null;
					unregisterNetworkReceiver();
				} else {
					System.out.println("定位服务已经关闭");
				}
			} else {
				if (client == null || !client.isStarted()) {
					String driverInfoJson = sharedPreferences.getString(
							DiDiApplication.KEY_OF_DRIVER_INFO_DATA,
							DiDiApplication.VALUE_UNKOWN);
					DriverInfoBean driverInfo = GsonUtil.jsonToObjct(
							driverInfoJson, DriverInfoBean.class);
					driverId = driverInfo.driver_id;
//					uploadLocationExecutor = UploadLocationExecutor
//							.getInstance();
//					uploadLocationExecutor.registerPostCallback(callback);
					client = new LocationClient(ForegroundServer.this);
					client.setLocOption(getDefaultLocationClientOption());
					client.registerLocationListener(dbListener);
					client.start();
					registerNetworkReceiver();
				} else {
					System.out.println("定位服务已经启动");
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	private LocationClientOption getDefaultLocationClientOption() {
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);
		option.setCoorType("bd09ll");
		option.setOpenGps(false); // 打开GPRS
		option.setScanSpan(30 * 1000);
		option.setNeedDeviceDirect(true);
		// 修复6.0错误
		option.setPriority(LocationClientOption.NetWorkFirst);
		return option;
	}

	private BDLocationListener dbListener = new BDLocationListener() {
		@Override
		public void onReceiveLocation(BDLocation location) {
			// TODO Auto-generated method stub
			if (null != location
					&& (location.getLocType() == BDLocation.TypeGpsLocation
							|| location.getLocType() == BDLocation.TypeNetWorkLocation || location
							.getLocType() == BDLocation.TypeOffLineLocation)) {
				StringBuffer locationResult = new StringBuffer();
				locationResult.append(FormatUtil.formatTime(
						System.currentTimeMillis(), "yyMMddHHmmss"));
				locationResult.append(",");
				if (location.getDirection() < 0) {
					locationResult.append("0");
				} else {
					int direction = (int) location.getDirection();
					locationResult.append(direction);
				}
				locationResult.append(",");
				locationResult.append(location.getLatitude());
				locationResult.append(",");
				locationResult.append(location.getLongitude());
				locationResult.append(",");
				if (location.getSpeed() < 0) {
					locationResult.append("0");
				} else {
					int speed = (int) location.getSpeed();
					locationResult.append(speed);
				}
				if (lastLatitude == 0 || lastLongitude == 0) {
					handlerData(locationResult.toString());
				} else {
					// 判断距离
					double distance = ToolUtil.distance(lastLongitude,
							lastLatitude, location.getLongitude(),
							location.getLatitude());
					System.out.println("距离--->" + distance);
					handlerData(locationResult.toString());
				}
				lastLatitude = location.getLatitude();
				lastLongitude = location.getLongitude();
				System.out.println("定位结果--->" + locationResult);
			} else {
				handlerData(null);
				System.out.println("定位失败---------------------"
						+ location.getLocType());
			}
		}
	};

	private void handlerData(String location) {
		System.out.println("----上传的定位数据------>>>" + location);
		// /获取传过来的值
		// /上线状态
		if (location != null) {
			// 每次提交一次数据
			System.out.println("上线---格式化之后的位置信息：" + location);
//			uploadLocationExecutor.post(new LocationInfo(driverId, location),
//					UploadLocationExecutor.POST_PRIORITY_HIGH);
			System.out.println("上线---删除之后的位置信息：" + location);
		}
	}

	private PostCallback callback = new PostCallback() {
		@Override
		public void onSuccess() {
			// / wited1(loca + "/");
			System.out.println("上传gps数据成功");
			// Toast.makeText(getApplicationContext(), "上传gps数据成功",
			// Toast.LENGTH_SHORT).show();

		}

		@Override
		public void onFail(String driverId, String location) {
			// 如果上传失败，保存到数据库
			System.out.println("上传gps数据失败，存到数据库-----------------");
			// Toast.makeText(getApplicationContext(),
			// "上传gps数据失败，存到数据库---------------", Toast.LENGTH_SHORT)
			// .show();
			// / wited2(location + "/");
			LocationDatabase database = new LocationDatabase(
					ForegroundServer.this);
			LocationInfo locationInfo = new LocationInfo(driverId, location);
			database.push(GsonUtil.objectToJson(locationInfo));
			System.out.println("----上传失败的定位数据------>>>" + location);
		}
	};

	/**
	 * 监听网络广播
	 */
	private BroadcastReceiver networkReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			ConnectivityManager manager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo activeInfo = manager.getActiveNetworkInfo();
			//调用鹰眼服务
//			if (activeInfo != null && activeInfo.isConnected()) {
//				System.out.println("联网，同步gps数据");
//				// 尝试post以前的数据
//				LocationDatabase database = new LocationDatabase(context);
//				List<LocationInfo> locationInfoList = database.pullAll();
//				database.clear();
//				if (locationInfoList.size() > 0) {
//					for (LocationInfo locationInfo : locationInfoList) {
//						uploadLocationExecutor.post(locationInfo,
//								UploadLocationExecutor.POST_PRIORITY_NORMAL);
//					}
//				}
//			}
		}
	};

	private void registerNetworkReceiver() {
		IntentFilter filter = new IntentFilter(
				ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(networkReceiver, filter);
	}

	private void unregisterNetworkReceiver() {
		unregisterReceiver(networkReceiver);
	}

}
