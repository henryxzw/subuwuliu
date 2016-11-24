package com.johan.subuwuliu.server;

import java.util.HashMap;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.easemob.EMCallBack;
import com.easemob.chatuidemo.DemoHXSDKHelper;
import com.johan.subuwuliu.HomeActivity;
import com.johan.subuwuliu.application.DiDiApplication;
import com.johan.subuwuliu.bean.ModeBean;
import com.johan.subuwuliu.bean.ModeBean.ModeInfoBean;
import com.johan.subuwuliu.handler.AbstractCommandHandler;
import com.johan.subuwuliu.handler.CheckOrderRecentHandler;
import com.johan.subuwuliu.util.GsonUtil;
import com.johan.subuwuliu.util.NetUtil;
import com.johan.subuwuliu.util.ToolUtil;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;

public class BackgroundServer extends Service {

	public static final String BACKGROUND_COMMAND_NAME = "background_command_name";

	public static final String COMMAND_GET_LOCATION = "get_location";
	public static final String COMMAND_UPLOAD_DRIVER_INFO = "upload_driver_info";
	public static final String COMMAND_CHECK_RECENT_ORDER = "check_recent_order";
	public static final String COMMAND_DONWLINE = "downline";

	private HashMap<String, AbstractCommandHandler> commandExecutor = new HashMap<String, AbstractCommandHandler>();
	private LocationClient nowClient;
	private String nowCity, nowAddress;
	private double nowLongitude, nowLatitude;
	private GeoCoder geocodera;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return new ServiceBinder();
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		System.out
				.println("初始化命令------------BackgroundServer-----------------");
		// 初始化命令
		commandExecutor.put(COMMAND_GET_LOCATION, new GetLocationHandler(this));
		commandExecutor.put(COMMAND_UPLOAD_DRIVER_INFO, new UploadDriverInfo(
				this));
		commandExecutor.put(COMMAND_CHECK_RECENT_ORDER,
				new CheckOrderRecentHandler(this));
		commandExecutor.put(COMMAND_DONWLINE, new DownlineHandler(this));
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		flags = START_STICKY;
		if (intent != null) {
			String command = intent.getStringExtra(BACKGROUND_COMMAND_NAME);
			AbstractCommandHandler handler = commandExecutor.get(command);
			handler.doCommand(intent);
		} else {
			commandExecutor.get(COMMAND_GET_LOCATION).doCommand(null);
		}
		return super.onStartCommand(intent, flags, startId);
	}

	/**
	 * 获取当前定位服务，应用一进来就获取
	 */
	class GetLocationHandler extends AbstractCommandHandler {
		public GetLocationHandler(Context context) {
			super(context);
		}

		@Override
		public void doCommand(Intent intent) {
			if (nowClient == null || !nowClient.isStarted()) {
				nowClient = new LocationClient(BackgroundServer.this);
				nowClient.setLocOption(getNowLocationClientOption());
				nowClient.registerLocationListener(nowDbListener);
				nowClient.start();
			}
		}
	}

	/**
	 * 改变状态
	 * 
	 * @author Administrator
	 */
	class UploadDriverInfo extends AbstractCommandHandler {
		public UploadDriverInfo(Context context) {
			super(context);
		}

		@Override
		public void doCommand(Intent intent) {
			if (nowLatitude == 0 || nowLongitude == 0 || nowCity == null
					|| nowAddress == null) {
				System.out.println("定位失败，不能上传司机状态和模式状态");
				return;
			}
			// 上传司机状态
			String driverInfoState = sharedPreferences.getString(
					DiDiApplication.KEY_OF_DRIVER_STATE, "downline");
			// 上传司机状态
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("key", driverInfoState);
			params.put("username", loginName);
			params.put("pwd", loginPassword);
			params.put("addr", nowAddress + "|" + String.valueOf(nowLongitude)
					+ "," + String.valueOf(nowLatitude));
			params.put("city", nowCity);
			String action = ToolUtil.encryptionUrlParams(params);
			NetUtil.getInstance().doGet(NetUtil.DRIVER_URL + action,
					new RequestCallBack<String>() {
						@Override
						public void onFailure(HttpException exception,
								String info) {
							// TODO Auto-generated method stub
							System.out.println("再次提交司机状态失败："
									+ exception.getMessage());
						}

						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
							// TODO Auto-generated method stub
							System.out.println("效验司机状态成功："
									+ responseInfo.result);
						}
					});
			// 上传模式信息
			String modeInfo = sharedPreferences.getString(
					DiDiApplication.KEY_OF_MODE_STATE,
					DiDiApplication.VALUE_UNKOWN);
			if (DiDiApplication.VALUE_UNKOWN.equals(modeInfo)) {
				return;
			}
			ModeBean modeBean = GsonUtil.jsonToObjct(modeInfo, ModeBean.class);
			String currentMode = modeBean.info.mode;
			ModeInfoBean modeInfoBean = modeBean.info;
			HashMap<String, String> paramss = new HashMap<String, String>();
			paramss.put("key", "setmode");
			paramss.put("username", loginName);
			paramss.put("pwd", loginPassword);
			paramss.put("mode", currentMode);
			String backCity = modeInfoBean.back_city;
			String backAddress = modeInfoBean.back_addr;
			if ("".equals(backCity)) {
				paramss.put("is_backcar", "0");
				paramss.put("back_city", "");
				paramss.put("back_addr", "");
			} else {
				paramss.put("is_backcar", "1");
				paramss.put("back_city", backCity);
				if (!"".equals(backAddress)) {
					paramss.put("back_addr", backAddress);
				} else {
					paramss.put("back_addr", "");
				}
			}
			String allAppointmentTime = modeInfoBean.booking_time;
			if ("".equals(allAppointmentTime)) {
				paramss.put("booking_time", "");
			} else {
				paramss.put("booking_time", allAppointmentTime);
			}

			String actions = ToolUtil.encryptionUrlParams(paramss);
			NetUtil.getInstance().doGet(NetUtil.DRIVER_URL + actions,
					new RequestCallBack<String>() {
						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
							System.out.println("再次提交司机模式成功："
									+ responseInfo.result);
						}

						@Override
						public void onFailure(HttpException exception,
								String info) {
							System.out.println("再次提交司机模式失败："
									+ exception.getMessage());
						}
					});
		}
	}

	/**
	 * 司机下线
	 * 
	 * @author fengyihuan
	 */
	public class DownlineHandler extends AbstractCommandHandler {
		public DownlineHandler(Context context) {
			super(context);
		}

		@Override
		public void doCommand(Intent intent) {
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
			sharedPreferences.edit()
					.putString(DiDiApplication.KEY_OF_DRIVER_STATE, "downline")
					.commit();

			SharedPreferences sharedPreferences_A = getSharedPreferences(
					"sijizhangtai", Context.MODE_PRIVATE);
			String a = sharedPreferences_A.getString("sijizhangtai_a", "");
			if (a.equals("online")) {
				sijixiaxian(loginName, loginPassword);
			}

		}
	}

	private int i = 0;

	private void sijixiaxian(final String loginName, final String loginPassword) {
		try {
			// 上传司机状态
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("key", "downline");
			params.put("username", loginName);
			params.put("pwd", loginPassword);
			params.put("addr", nowAddress + "|" + String.valueOf(nowLongitude)
					+ "," + String.valueOf(nowLatitude));
			params.put("city", nowCity);
			String action = ToolUtil.encryptionUrlParams(params);
			NetUtil.getInstance().doGet(NetUtil.DRIVER_URL + action,
					new RequestCallBack<String>() {
						@Override
						public void onFailure(HttpException exception,
								String info) {
							// TODO Auto-generated method stub
							System.out.println("司机下线失败，原因：" + info);
							if (i > 3) {
								return;
							}
							i++;
							sijixiaxian(loginName, loginPassword);
						}

						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
							// TODO Auto-generated method stub
							System.out.println("司机下线成功：" + responseInfo.result);
						}
					});
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("司机下线出错");
		}
	}

	private LocationClientOption getNowLocationClientOption() {
		LocationClientOption option = new LocationClientOption();
		// 修复6.0错误
		option.setPriority(LocationClientOption.GpsFirst);
		option.setOpenGps(true); // 打开GPRS 关闭gps
		option.setLocationMode(LocationMode.Hight_Accuracy);
		option.setCoorType("bd09ll");
		option.setScanSpan(1000);
		option.setNeedDeviceDirect(true);
		option.setIsNeedAddress(true);
		return option;
	}

	private BDLocationListener nowDbListener = new BDLocationListener() {
		@Override
		public void onReceiveLocation(BDLocation location) {
			// TODO Auto-generated method stub
			try {
				if (null != location
						&& (location.getLocType() == BDLocation.TypeGpsLocation
								|| location.getLocType() == BDLocation.TypeNetWorkLocation || location
								.getLocType() == BDLocation.TypeOffLineLocation)) {
					nowLatitude = location.getLatitude();
					nowLongitude = location.getLongitude();
					nowCity = location.getCity();
					nowAddress = location.getAddrStr();
					SharedPreferences sharedPreferences_C = getSharedPreferences(
							"sijizhangtai", Context.MODE_PRIVATE);
					System.out.println("定位成功：" + nowLatitude + ","
							+ nowLongitude + "|" + nowCity + "|" + nowAddress);
					nowClient.stop();
					nowClient.unRegisterLocationListener(nowDbListener);
					nowClient = null;
					return;
				} else {
					System.out.println("定位失败---------------------"
							+ location.getLocType());
				}
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println("-----定位出现错误---------->>");
			}
		}
	};

	public class ServiceBinder extends Binder {
		public BackgroundServer getService() {
			return BackgroundServer.this;
		}
	}

	public String getNowCity() {
		return nowCity;
	}

	public String getNowAddress() {
		return nowAddress;
	}

	public double getNowLongitude() {
		return nowLongitude;
	}

	public double getNowLatitude() {
		return nowLatitude;
	}

	OnGetGeoCoderResultListener ongeoa = new OnGetGeoCoderResultListener() {

		@Override
		public void onGetGeoCodeResult(GeoCodeResult arg0) {
		}

		@Override
		public void onGetReverseGeoCodeResult(ReverseGeoCodeResult arg0) {
			if (arg0.error == SearchResult.ERRORNO.NO_ERROR) {
				nowCity = arg0.getAddressDetail().city;
				nowAddress = arg0.getAddress();
				return;
			}
		}

	};
}
