package com.johan.subuwuliu.util;

import java.util.HashMap;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.johan.subuwuliu.application.DiDiApplication;
import com.johan.subuwuliu.bean.DriverStateBean;
import com.johan.subuwuliu.server.BackgroundServer;
import com.johan.subuwuliu.server.BackgroundServer.ServiceBinder;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

public class TiJiao_TYPE {
	private LocationClient nowClient;
	private String nowCity, nowAddress;
	private double nowLongitude, nowLatitude;

	private void shangxian_type(DriverStateBean driverStateBean,
			String logname, String loginPassword, String key) {
		if (nowClient == null || !nowClient.isStarted()) {
			nowClient = new LocationClient(DiDiApplication.applicationContext);
			nowClient.setLocOption(getNowLocationClientOption());
			nowClient.registerLocationListener(nowDbListener);
			nowClient.start();
		}
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("key", "onji");
		params.put("username", logname);
		params.put("pwd", loginPassword);

		params.put("addr", nowAddress + "|" + String.valueOf(nowLongitude)
				+ "," + String.valueOf(nowLatitude));
		params.put("city", nowCity);

		String action = ToolUtil.encryptionUrlParams(params);
		System.out.println("提交上线信息-------------------------------" + action);
		NetUtil.getInstance().doGet(NetUtil.DRIVER_URL + action,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub

					}
				});
	}

	private LocationClientOption getNowLocationClientOption() {
		LocationClientOption option = new LocationClientOption();
		// 修复6.0错误
		option.setPriority(LocationClientOption.GpsFirst);
		option.setOpenGps(true); // 打开GPRS 关闭gps
		option.setLocationMode(LocationMode.Hight_Accuracy);
		option.setCoorType("bd09ll");
		option.setScanSpan(30 * 1000);
		option.setNeedDeviceDirect(true);
		option.setIsNeedAddress(true);
		return option;
	}

	private BDLocationListener nowDbListener = new BDLocationListener() {
		@Override
		public void onReceiveLocation(BDLocation location) {
			// TODO Auto-generated method stub
			if (null != location
					&& (location.getLocType() == BDLocation.TypeGpsLocation
							|| location.getLocType() == BDLocation.TypeNetWorkLocation || location
							.getLocType() == BDLocation.TypeOffLineLocation)) {
				nowLatitude = location.getLatitude();
				nowLongitude = location.getLongitude();
				nowCity = location.getCity();
				nowAddress = location.getAddrStr();
				System.out.println("定位成功：" + nowLatitude + "," + nowLongitude
						+ "|" + nowCity + "|" + nowAddress);
				nowClient.stop();
				nowClient.unRegisterLocationListener(nowDbListener);
				nowClient = null;
				return;
			} else {
				System.out.println("定位失败---------------------"
						+ location.getLocType());
			}
		}
	};
}
