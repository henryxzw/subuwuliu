package com.johan.subuwuliu.fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.jpush.a.a.ar;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapTouchListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.johan.subuwuliu.CommentActivity;
import com.johan.subuwuliu.GoOrderActivity;
import com.johan.subuwuliu.HomeActivity;
import com.johan.subuwuliu.MyAppointmentActivity;
import com.johan.subuwuliu.R;
import com.johan.subuwuliu.UploadPictureActivity;
import com.johan.subuwuliu.application.DiDiApplication;
import com.johan.subuwuliu.bean.DriverInfoBean;
import com.johan.subuwuliu.bean.GoOrderBean.GoOrderInfoBean;
import com.johan.subuwuliu.bean.StatusBean;
import com.johan.subuwuliu.bean.UploadOrderPictureBean;
import com.johan.subuwuliu.database.UploadOrderPictrueDatabase;
import com.johan.subuwuliu.dialog.WaitingDialog;
import com.johan.subuwuliu.method.MethodConfig;
import com.johan.subuwuliu.receiver.MyPushReceiver;
import com.johan.subuwuliu.server.ForegroundServer;
import com.johan.subuwuliu.util.GsonUtil;
import com.johan.subuwuliu.util.ImageUtil;
import com.johan.subuwuliu.util.NetUtil;
import com.johan.subuwuliu.util.NetUtil.UploadFileListener;
import com.johan.subuwuliu.util.ToastUtil;
import com.johan.subuwuliu.util.ToolUtil;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 修改，所有图片不再在此界面上传，分步操作，只会由图片上传界面返回拼接好的7牛图片名称集
 * @author apple
 *
 */
@SuppressLint("ShowToast")
public class GoOrderFragment extends AppFragment implements OnClickListener {

	public static final int REQUEST_UPLOAD_PICTURE = 36;

	private TextView contact, place;
	private com.baidu.mapapi.map.MyLocationConfiguration.LocationMode locationmode;
	private Button remain;
	private SDKReceiver mReceiver;
	private LinearLayout[] butLayouts = new LinearLayout[5];
	private Button but1, but2, but31, but32, but41, but42, but51, but52;
	private LinearLayout linearA1, linearA2;
	private Button butA1, butA2;
	private BitmapDescriptor markBitmap = null;
	private MapView mapView = null;
	private BaiduMap baiduMap;

	private LocationClient client = null;
	private LocationClientOption option = null;

	private GoOrderInfoBean orderinfo;
	private String sijiID;

	private double currentLatitude = 0, currentLongitude = 0;
	private String currentAddress;

	private double targetLatitude, targetLongitude;

	private boolean isFirstLocation = true;

	private int currentState = 0;
	private long clickTime = 0;// 判断当前是否需要跟随
	private Button dangqianweizhi;

	private List<LineWay> lineWayList = new ArrayList<LineWay>();

	private WaitingDialog waitingDialog;
	private BDLocation loca;
	private Dialog goSettingGPSDialog;
	private String yinyan = "0";
	private int image_int = 0;
	private GeoCoder geocodera;

	@Override
	public void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
		unregisterBroadcastReceiver();
		baiduMap.clear();
		if (markBitmap != null) {
			markBitmap.recycle();
		}
		baiduMap.setMyLocationEnabled(false);
		client.stop();
	}

	@Override
	public int getContentView() {
		// TODO Auto-generated method stub
		return R.layout.fragment_goorder;
	}

	public void setOrderinfo(GoOrderInfoBean orderinfo) {
		this.orderinfo = orderinfo;
		System.out.println("order state ---> " + orderinfo.state);
		// new Handler().post(new Runnable() {
		// @Override
		// public void run() {
		// initLineWayData();
		// initView();
		// }
		// });
	}

	@Override
	public void findId() {
		// TODO Auto-generated method stub
		linearA1 = (LinearLayout) layout
				.findViewById(R.id.goorder_but_layout_A1);

		linearA2 = (LinearLayout) layout
				.findViewById(R.id.goorder_but_layout_A2);
		butA1 = (Button) layout.findViewById(R.id.goorder_but_A1);
		butA2 = (Button) layout.findViewById(R.id.goorder_but_A2);
		// //////////////////////////
		remain = (Button) layout.findViewById(R.id.goorder_remain);
		place = (TextView) layout.findViewById(R.id.goorder_info_place);
		contact = (TextView) layout.findViewById(R.id.goorder_info_contact);
		butLayouts[0] = (LinearLayout) layout
				.findViewById(R.id.goorder_but_layout_1);
		butLayouts[1] = (LinearLayout) layout
				.findViewById(R.id.goorder_but_layout_2);
		butLayouts[2] = (LinearLayout) layout
				.findViewById(R.id.goorder_but_layout_3);
		butLayouts[3] = (LinearLayout) layout
				.findViewById(R.id.goorder_but_layout_4);
		butLayouts[4] = (LinearLayout) layout
				.findViewById(R.id.goorder_but_layout_5);
		but1 = (Button) layout.findViewById(R.id.goorder_but_1);
		but2 = (Button) layout.findViewById(R.id.goorder_but_2);
		but31 = (Button) layout.findViewById(R.id.goorder_but_3_1);
		but32 = (Button) layout.findViewById(R.id.goorder_but_3_2);
		but41 = (Button) layout.findViewById(R.id.goorder_but_4_1);
		but42 = (Button) layout.findViewById(R.id.goorder_but_4_2);
		but51 = (Button) layout.findViewById(R.id.goorder_but_5_1);
		but52 = (Button) layout.findViewById(R.id.goorder_but_5_2);
		mapView = (MapView) layout.findViewById(R.id.goorder_map_view);
		dangqianweizhi = (Button) layout.findViewById(R.id.dangqianweizhi);
		dangqianweizhi.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				LatLng latLng = new LatLng(loca.getLatitude(), loca
						.getLongitude());
				MapStatus mapStatus = new MapStatus.Builder().target(latLng)
						.zoom(16).build();
				MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory
						.newMapStatus(mapStatus);
				// 以动画方式更新地图状态
				baiduMap.animateMapStatus(mapStatusUpdate);
			}
		});
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		// 初始化数据
		initLineWayData();
		initView();
		geocodera = GeoCoder.newInstance();
		geocodera.setOnGetGeoCodeResultListener(ongeoa);
		// 初始化上传图片按钮不能按
		but2.setEnabled(false);
		but31.setEnabled(false);
		but41.setEnabled(false);
		but51.setEnabled(false);
		butA1.setEnabled(false);
		butA2.setEnabled(false);
		// ///////////////////
		butA1.setOnClickListener(this);
		butA2.setOnClickListener(this);
		// 监听
		but1.setOnClickListener(this);
		but2.setOnClickListener(this);
		but31.setOnClickListener(this);
		but32.setOnClickListener(this);
		but41.setOnClickListener(this);
		but42.setOnClickListener(this);
		but51.setOnClickListener(this);
		but52.setOnClickListener(this);
		// ///////////////////////////////////////////////////
		// 注册 SDK 广播监听者
		IntentFilter iFilter = new IntentFilter();
		iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
		iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
		mReceiver = new SDKReceiver();
		getActivity().registerReceiver(mReceiver, iFilter);
		// 设置是否显示比例尺控件
		mapView.showScaleControl(true);
		// 设置是否显示缩放控件
		mapView.showZoomControls(false);
		baiduMap = mapView.getMap();
		baiduMap.setMyLocationEnabled(true);
         baiduMap.setOnMapTouchListener(new OnMapTouchListener() {
			
			@Override
			public void onTouch(MotionEvent arg0) {
				if(arg0.getAction()==0)
				{
					clickTime = System.currentTimeMillis();
				}
				else {
					clickTime = System.currentTimeMillis();
				}
				System.out.println(arg0.getAction());
				
			}
		});
       
		baiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
				locationmode, true, null));
		// 设置定位图层配置信息(跟随，罗盘，没有)
		locationmode = com.baidu.mapapi.map.MyLocationConfiguration.LocationMode.COMPASS;
		// 创建定位服务的客户端
		client = new LocationClient(getActivity());
		MyLocationListener listener = new MyLocationListener();
		client.registerLocationListener(listener);
		// 开始定位
		client.setLocOption(getDefaultLocationClientOption());
		client.start();

		// //////////////////////////////////////////////////
		// 等待框
		waitingDialog = new WaitingDialog(getActivity());
		// 画路线
		showRoute();
		// /////////////////////////
		SharedPreferences sharedPreferences = getActivity()
				.getSharedPreferences(
						DiDiApplication.NAME_OF_SHARED_PREFERENCES,
						Context.MODE_PRIVATE);
		String driverInfoData = sharedPreferences.getString(
				DiDiApplication.KEY_OF_DRIVER_INFO_DATA,
				DiDiApplication.VALUE_UNKOWN);
		DriverInfoBean driverInfo = GsonUtil.jsonToObjct(driverInfoData,
				DriverInfoBean.class);
		sijiID = driverInfo.driver_id;
	}

	private void initLineWayData() {
		lineWayList.clear();
		int index = 1;
		int state = Integer.parseInt(orderinfo.state);
		// 出发装货
		LineWay lineWay1 = new LineWay(index, 0, orderinfo.address_start,
				orderinfo.start_time, orderinfo.start_img, state == 0 ? "1"
						: "2", orderinfo.start_contact, "order_start");
		// 到达装货
		LineWay lineWay2 = new LineWay(index, 1, orderinfo.address_start,
				orderinfo.start_time, orderinfo.start_img, state == 1 ? "1"
						: "2", orderinfo.start_contact, "order_arrive");
		// 装货完成
		LineWay lineWay3 = new LineWay(index, 2, orderinfo.address_start,
				orderinfo.start_time, orderinfo.start_img, state == 2 ? "1"
						: "2", orderinfo.start_contact, "order_express");
		lineWayList.add(lineWay1);
		lineWayList.add(lineWay2);
		lineWayList.add(lineWay3);
		index++;
		// 第1个点
		if (!"".equals(orderinfo.address_midway1)) {
			LineWay lineWay4 = new LineWay(index++, 3,
					orderinfo.address_midway1, orderinfo.midway1_time,
					orderinfo.midway1_img, orderinfo.midway1_state,
					orderinfo.midway1_contact, "mid1_finish");
			lineWayList.add(lineWay4);
		}
		// 第2个点
		if (!"".equals(orderinfo.address_midway2)) {
			LineWay lineWay5 = new LineWay(index++, 3,
					orderinfo.address_midway2, orderinfo.midway2_time,
					orderinfo.midway2_img, orderinfo.midway2_state,
					orderinfo.midway2_contact, "mid2_finish");
			lineWayList.add(lineWay5);
		}
		// 第3个点
		if (!"".equals(orderinfo.address_midway3)) {
			LineWay lineWay6 = new LineWay(index++, 3,
					orderinfo.address_midway3, orderinfo.midway3_time,
					orderinfo.midway3_img, orderinfo.midway3_state,
					orderinfo.midway3_contact, "mid3_finish");
			lineWayList.add(lineWay6);
		}
		// 第4个点
		if (!"".equals(orderinfo.address_midway4)) {
			LineWay lineWay7 = new LineWay(index++, 3,
					orderinfo.address_midway4, orderinfo.midway4_time,
					orderinfo.midway4_img, orderinfo.midway4_state,
					orderinfo.midway4_contact, "mid4_finish");
			lineWayList.add(lineWay7);
		}
		// 第5个点
		if (!"".equals(orderinfo.address_midway5)) {
			LineWay lineWay8 = new LineWay(index++, 3,
					orderinfo.address_midway5, orderinfo.midway5_time,
					orderinfo.midway5_img, orderinfo.midway5_state,
					orderinfo.midway5_contact, "mid5_finish");
			lineWayList.add(lineWay8);
		}
		// 最后一个点
		LineWay lineWay9 = new LineWay(index, 4, orderinfo.address_end,
				orderinfo.end_time, orderinfo.end_img, orderinfo.end_state,
				orderinfo.end_contact, "order_sign");
		lineWayList.add(lineWay9);
		// 判断当前点
		currentState = lineWayList.size() - 1;
		for (int i = 0; i < lineWayList.size(); i++) {
			if ("1".equals(lineWayList.get(i).state)) {
				System.out.println("当前在--->" + i);
				currentState = i;
				break;
			}
		}
	}

	private void initView() {
		System.out.println("修改------------------------------");
		if (getActivity() == null || getActivity().isFinishing()) {
			System.out.println("Activity已经关闭，不能刷新界面");
			return;
		}
		LineWay currentLineWay = lineWayList.get(currentState);
		// 当前地点
		String[] placeArr = currentLineWay.address.split("\\|");
		if (placeArr != null && placeArr.length == 3) {
			place.setText(placeArr[0]);
			// target点的经纬度
			targetLongitude = Double.parseDouble(placeArr[1]);
			targetLatitude = Double.parseDouble(placeArr[2]);
		}
		// 当前联系人
		String[] contactArr = currentLineWay.contact.split("\\|");
		if (contactArr != null && contactArr.length == 2) {
			contact.setText(contactArr[0] + " " + contactArr[1]);
		}
		contact.setVisibility(View.GONE);
		// 剩余点
		System.out.println("总共" + lineWayList.size() + "个点");
		int remainDot = lineWayList.get(lineWayList.size() - 1).index
				- currentLineWay.index;
		if (remainDot == 0 || remainDot == 1) {
			remain.setVisibility(View.GONE);
		}
		remain.setText("剩" + String.valueOf(remainDot) + "个点");
		// 修改下面点
		showWhich(currentLineWay.layoutIndex);
		System.out.println("---------->>" + currentLineWay.layoutIndex);
		// 是否已经上传图片
		changeButState();
	}

	@Override
	public void onResume() {
		super.onResume();
		mapView.onResume();
		registerBroadcastReceiver();
	}

	@Override
	public void onPause() {
		super.onPause();
		mapView.onPause();
	}

	@SuppressWarnings("deprecation")
	public LocationClientOption getDefaultLocationClientOption() {
		if (option == null) {
			option = new LocationClientOption();
			option.setOpenGps(true);
			// 修复6.0错误
			option.setPriority(LocationClientOption.GpsFirst);
			option.setLocationMode(LocationMode.Hight_Accuracy);
			// 可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
			option.setCoorType("bd09ll");// 可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
			option.setScanSpan(1500);// 可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
			option.setIsNeedAddress(false);// 可选，设置是否需要地址信息，默认不需要
			option.setIsNeedLocationDescribe(false);// 可选，设置是否需要地址描述
			option.setNeedDeviceDirect(true);// 可选，设置是否需要设备方向结果
			option.setLocationNotify(false);// 可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
			option.setIgnoreKillProcess(false);// 可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
			option.setIsNeedLocationDescribe(false);// 可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
			option.setIsNeedLocationPoiList(false);// 可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
			option.SetIgnoreCacheException(false);// 可选，默认false，设置是否收集CRASH信息，默认收集
		}
		return option;
	}

	// ///////////////////////////////////////
	private class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {

			if (location == null || mapView == null)
				return;
			// 复制当前信息////获得当前的经纬度和地址
			currentLatitude = location.getLatitude();
			currentLongitude = location.getLongitude();
			loca = location;
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					.direction(location.getDirection())
					.latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			// 设置定位数据
			baiduMap.setMyLocationData(locData);
			LatLng latLng = new LatLng(location.getLatitude(),
					location.getLongitude());

			if (MethodConfig.hasTrack) {
					if(clickTime==0 ||  (System.currentTimeMillis() - clickTime)/1000>10)
					{
						clickTime = 0;
						MapStatus mapStatus = new MapStatus.Builder()
						.target(latLng).zoom(15).build();
				MapStatusUpdate update = MapStatusUpdateFactory
						.newMapStatus(mapStatus);
				baiduMap.animateMapStatus(update);
					}
			}

			// 判断地点的距离
			double distance = ToolUtil.distance(currentLongitude,
					currentLatitude, targetLongitude, targetLatitude);
			LineWay currentLineWay = lineWayList.get(currentState);
			System.out.println("修改上传图片的状态：" + currentLineWay.layoutIndex);
			if (distance < 1000) {
				// 改变按钮的状态
				UploadButEnable();
			} else {
				butA1.setEnabled(false);
				butA2.setEnabled(false);
			}
		}

		@SuppressWarnings("unused")
		public void onReceivePoi(BDLocation poiLocation) {
		}
	}

	private void UploadButEnable() {
		LineWay currentLineWay = lineWayList.get(currentState);
		System.out.println("修改上传图片的状态：" + currentLineWay.layoutIndex);

		switch (currentLineWay.layoutIndex) {
		case 1:
			but2.setEnabled(true);
			break;
		case 2:
			but31.setEnabled(true);
			break;
		case 3:
			but41.setEnabled(true);
			butA1.setEnabled(true);
			butA2.setEnabled(false);
			break;
		case 4:
			but51.setEnabled(true);
			butA1.setEnabled(false);
			butA2.setEnabled(true);
			break;
		default:
			break;
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		// //判断是否有网络
		if (!ToolUtil.isNetworkAvailable(getActivity())) {
			ToolUtil.dilog(getActivity(), "网络有问题，请先连接网络");
			return;
		}
		LocationManager locationManager = (LocationManager) getActivity()
				.getSystemService(Context.LOCATION_SERVICE);
		boolean isOpen = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		if (!isOpen) {
			showGoSettingDialog();
			return;
		}
		switch (v.getId()) {
		case R.id.goorder_but_A1:
			linearA1.setVisibility(View.GONE);
			linearA2.setVisibility(View.GONE);
			butLayouts[3].setVisibility(View.VISIBLE);
			break;
		case R.id.goorder_but_A2:
			linearA1.setVisibility(View.GONE);
			linearA2.setVisibility(View.GONE);
			butLayouts[4].setVisibility(View.VISIBLE);
			break;
		case R.id.goorder_but_1:
		case R.id.goorder_but_2:
		case R.id.goorder_but_3_2:
		case R.id.goorder_but_4_2:
			but1.setEnabled(false);
			but2.setEnabled(false);
			but32.setEnabled(false);
			but42.setEnabled(false);
			changeState();
			break;
		case R.id.goorder_but_5_2:
			GoOrderActivity activity = (GoOrderActivity) getActivity();
			waitingDialog.show(activity.getParentLayout());
			doLast();
			break;
		case R.id.goorder_but_3_1:
		case R.id.goorder_but_4_1:
		case R.id.goorder_but_5_1:
			uploadPicture();
			break;
		default:
			break;
		}
	}

	private void showWhich(int which) {
		// 开始定位 xcxfd
		for (int i = 0; i < butLayouts.length; i++) {
			if (which == i) {
				if (which == 3) {
					linearA1.setVisibility(View.VISIBLE);
					linearA2.setVisibility(View.GONE);
					butLayouts[i].setVisibility(View.GONE);

					return;
				}
				if (which == 4) {

					linearA1.setVisibility(View.GONE);
					linearA2.setVisibility(View.VISIBLE);
					butLayouts[i].setVisibility(View.GONE);
					return;
				}

				butLayouts[i].setVisibility(View.VISIBLE);
			} else {
				butLayouts[i].setVisibility(View.GONE);
			}

		}
	}

	public void changeButState() {
		LineWay currentLineWay = lineWayList.get(currentState);
		if (currentLineWay.index == 1 && currentLineWay.layoutIndex == 2) {
			if ("".equals(currentLineWay.img)) {
				but32.setEnabled(false);
			} else {
				but32.setEnabled(true);
			}
		} else if (currentLineWay.index > 1
				&& currentLineWay.index < lineWayList
						.get(lineWayList.size() - 1).index) {
			if ("".equals(currentLineWay.img)) {
				but42.setEnabled(false);
			} else {
				but42.setEnabled(true);
			}
		} else if (currentLineWay.index == lineWayList
				.get(lineWayList.size() - 1).index) {
			if ("".equals(currentLineWay.img)) {
				but52.setEnabled(false);
			} else {
				but52.setEnabled(true);
			}
		}
	}

	private void changeState() {
		if (currentLatitude != 0 || currentLongitude != 0) {
			LatLng laln = new LatLng(currentLatitude, currentLongitude);
			geocodera.reverseGeoCode(new ReverseGeoCodeOption().location(laln));
		}

		final LineWay currentLineWay = lineWayList.get(currentState);
		GoOrderActivity activity = (GoOrderActivity) getActivity();
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("key", "edit_waybill_status");
		params.put("yid", orderinfo.yid);
		params.put("username", activity.getLoginName());
		params.put("pwd", activity.getLoginPassword());
		params.put("edit_type", currentLineWay.type);
		params.put("addr",
				currentAddress + "|" + String.valueOf(currentLongitude) + ","
						+ String.valueOf(currentLatitude));
		params.put("imgurl", "");
		String action = ToolUtil.encryptionUrlParams(params);
		NetUtil.getInstance().doGet(NetUtil.ORDER_URL + action,
				new RequestCallBack<String>() {
					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						try {
							System.out.println("修改订单状态：" + responseInfo.result);
							StatusBean statusBean = GsonUtil.jsonToObjct(
									responseInfo.result, StatusBean.class);
							if (statusBean.status.equals("0")) {
								if (waitingDialog.isShowing()) {
									waitingDialog.dismiss();
								}
								if (!currentLineWay.type.equals("order_sign")) {
									ToastUtil.show(getActivity(), "重启定位");
									MethodConfig.hasTrack = true;
									HomeActivity.startTrace();
								}
								// //启动定位鹰眼服务
								if (currentLineWay.type.equals("order_start")) {
									HomeBindingCarFragment.dinshi_handler = false;
									// ///关闭上传定位
									Intent intentA = new Intent(getActivity(),
											ForegroundServer.class);
									intentA.putExtra(
											ForegroundServer.FOREGROUND_COMMAND_NAME,
											ForegroundServer.COMMAND_DO_LOCATION);
									intentA.putExtra("is_start", false);
									getActivity().startService(intentA);
									MyAppointmentActivity.myappointmentActivity
											.finish();
								}

								// 每次提交参数之后，需要设置需要更新，这样Home页面才能更新司机的状态
								SharedPreferences sharedPreferences = getActivity()
										.getSharedPreferences(
												DiDiApplication.NAME_OF_SHARED_PREFERENCES,
												Context.MODE_PRIVATE);
								sharedPreferences
										.edit()
										.putBoolean(
												DiDiApplication.KEY_OF_NEED_UPDATE_DRIVER_STATE,
												true).commit();
								yinyan = "1";
								if (currentLineWay.index == lineWayList
										.get(lineWayList.size() - 1).index) {
									// baiduMap.clear();
									// /关闭鹰眼服务
									MethodConfig.hasTrack = false;
									HomeActivity.stopTrace();
									// 提交所有状态就清空数据库
									UploadOrderPictrueDatabase database = new UploadOrderPictrueDatabase(
											getActivity());
									database.delete(orderinfo.yid);
									// 跳转到评论的界面
									Intent intent = new Intent(getActivity(),
											CommentActivity.class);
									intent.putExtra("yid", orderinfo.yid);
									getActivity().startActivity(intent);
									GoOrderActivity.activity.finish();
									// getActivity().finish();
									return;
								}
								try {
									currentLineWay.state = "1";
									currentState += 1;
									initView();
								} catch (Exception e) {
									// TODO: handle exception
									System.out
											.println("----------刷新时报错--------");
								}

							} else {
								ToastUtil.show(getActivity(), "提交错误："
										+ statusBean.msg);
								if (waitingDialog.isShowing()) {
									waitingDialog.dismiss();
								}
							}
						} catch (Exception e) {
							// TODO: handle exception
							getActivity().finish();
						}
					}

					@Override
					public void onFailure(HttpException exception, String info) {
						ToastUtil.show(getActivity(), "网络不佳，请重新提交！");
						if (waitingDialog.isShowing()) {
							waitingDialog.dismiss();
						}
					}
				});
	}

	class LineWay {
		public int index;
		public int layoutIndex;
		public String address;
		public String time;
		public String img;
		public String state;
		public String contact;
		public String type;

		public LineWay(int index, int layoutIndex, String address, String time,
				String img, String state, String contact, String type) {
			this.index = index;
			this.layoutIndex = layoutIndex;
			this.address = address;
			this.time = time;
			this.img = img;
			this.state = state;
			this.contact = contact;
			this.type = type;
		}
	}

	private void uploadPicture() {
		Intent intent = new Intent(getActivity(), UploadPictureActivity.class);
		Bundle data = new Bundle();
		data.putString("yid", orderinfo.yid);
		data.putString("node",
				String.valueOf(lineWayList.get(currentState).index));
		intent.putExtras(data);
		getActivity().startActivityForResult(intent, REQUEST_UPLOAD_PICTURE);
	}

	public void setImg(final String img) {
		new Handler().post(new Runnable() {
			@Override
			public void run() {
				lineWayList.get(currentState).img = img;
				changeButState();
			}
		});
	}

	public void doLast() {
		new LastTask().execute();
	}

	class LastTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			// 提交数据库的图片
			UploadOrderPictrueDatabase database = new UploadOrderPictrueDatabase(
					getActivity());
			List<UploadOrderPictureBean> orderPictureList = database
					.pullAll(orderinfo.yid);
			System.out.println("数据库有：" + orderPictureList.size() * 4
					+ "张图片要提交，请稍候");
			if (orderPictureList.size() > 0) {
				UploadTask[] uploadPictureTasks = new UploadTask[orderPictureList
						.size()];
				for (int i = 0; i < orderPictureList.size(); i++) {
					uploadPictureTasks[i] = new UploadPictureTask()
							.setUploadOrderPictureBean(orderPictureList.get(i));
				}
				for (int i = 0; i < orderPictureList.size(); i++) {
					if (i < (orderPictureList.size() - 1)) {
						uploadPictureTasks[i]
								.setNextTask(uploadPictureTasks[i + 1]);
					}
				}
				UploadPictureLastTask lastTask = new UploadPictureLastTask();
				lastTask.setOnFinishListener(new OnFinishListener() {
					@Override
					public void onFinish() {
						System.out.println("提交全部照片");
						// 修改最后的状态
						changeState();
					}
				});
				uploadPictureTasks[orderPictureList.size() - 1]
						.setNextTask(lastTask);
				uploadPictureTasks[0].doTask(0, 0);
			} else {
				// 没有，直接修改最后的状态
				changeState();
			}
			return null;
		}

	}

	public abstract class UploadTask {
		protected UploadOrderPictureBean currentOrderPicture;
		protected UploadTask nextTask;

		public UploadTask setUploadOrderPictureBean(
				UploadOrderPictureBean currentOrderPicture) {
			this.currentOrderPicture = currentOrderPicture;
			return this;
		}

		public UploadTask setNextTask(UploadTask nextTask) {
			this.nextTask = nextTask;
			return this;
		}

		public abstract void doTask(int successCount, int failCount);
	}

	class UploadPictureTask extends UploadTask {
		public void doTask(final int successCount, final int failCount) {
			System.out.println("执行--->，and next-->" + nextTask);
			System.out.println("success count : " + successCount
					+ " | fail count : " + failCount);
			final GoOrderActivity activity = (GoOrderActivity) getActivity();
			// 先上传文件
			String[] urlPath = currentOrderPicture.url.split("\\|");
			if (urlPath == null || urlPath.length != 4) {
				int resutlSuccessCount = successCount;
				int resultFailCount = failCount + 4;
				// ToastUtil.show(getActivity(), "上传A成功" + resutlSuccessCount
				// + "张，失败" + resultFailCount + "张");
				if (nextTask != null) {
					nextTask.doTask(resutlSuccessCount, resultFailCount);
				}
				return;
			}
			String uploadFile1 = null, uploadFile2 = null, uploadFile3 = null, uploadFile4 = null;
			try {
				for (int i = 0; i < urlPath.length; i++) {
					switch (i) {
					case 0:
						uploadFile1 = ImageUtil.compressPhoto(urlPath[0]);
						break;
					case 1:
						uploadFile2 = ImageUtil.compressPhoto(urlPath[1]);
						break;
					case 2:
						uploadFile3 = ImageUtil.compressPhoto(urlPath[2]);
						break;
					case 3:
						uploadFile4 = ImageUtil.compressPhoto(urlPath[3]);
						break;
					default:
						break;
					}
				}

			} catch (IOException e) {
				e.printStackTrace();
				int resutlSuccessCount = successCount;
				int resultFailCount = failCount + 4;
				if (nextTask != null) {
					nextTask.doTask(resutlSuccessCount, resultFailCount);
				}
				return;
			}
			HashMap<String, String> uploadParams = new HashMap<String, String>();
			uploadParams.put("file1", uploadFile1);
			uploadParams.put("file2", uploadFile2);
			uploadParams.put("file3", uploadFile3);
			uploadParams.put("file4", uploadFile4);
			NetUtil.getInstance().uploadFile1(uploadParams,
					new UploadFileListener() {
						@Override
						public void onSuccess(HashMap<String, String> resultMap) {
							String uploadUrl = resultMap.get("file1") + "|"
									+ resultMap.get("file2") + "|"
									+ resultMap.get("file3") + "|"
									+ resultMap.get("file4");
							HashMap<String, String> params = new HashMap<String, String>();
							params.put("key", "upload_goods_img");
							params.put("username", activity.getLoginName());
							params.put("pwd", activity.getLoginPassword());
							params.put("yid", currentOrderPicture.yid);
							params.put("node", currentOrderPicture.node);
							params.put("url", uploadUrl);
							String action = ToolUtil
									.encryptionUrlParams(params);
							NetUtil.getInstance().doGet(
									NetUtil.DRIVER_URL + action,
									new RequestCallBack<String>() {
										@Override
										public void onSuccess(
												ResponseInfo<String> responseInfo) {
											System.out.println("上传成功:"
													+ responseInfo.result);
											StatusBean statusBean = GsonUtil
													.jsonToObjct(
															responseInfo.result,
															StatusBean.class);
											if ("0".equals(statusBean.status)) {
												int resutlSuccessCount = successCount + 4;
												int resultFailCount = failCount;
												// ToastUtil
												// .show(getActivity(),
												// "上传C成功"
												// + resutlSuccessCount
												// + "张，失败"
												// + resultFailCount
												// + "张");
												if (nextTask != null) {
													nextTask.doTask(
															resutlSuccessCount,
															resultFailCount);
												}
											} else {

												int resutlSuccessCount = successCount;
												int resultFailCount = failCount + 4;
												System.out.println("上传照片失败："
														+ statusBean.msg);
												ToastUtil.show(getActivity(),
														"上传D成功" + successCount
																+ "张，失败"
																+ failCount + 4
																+ "张");

												if (nextTask != null) {
													waitingDialog.dismiss();
													dilogA(activity,
															"有"
																	+ resultFailCount
																	+ "张图片上传失败，请检查网络后重新提交！");
													image_int++;
													return;
												}
											}
										}

										@Override
										public void onFailure(
												HttpException exception,
												String info) {
											int resutlSuccessCount = successCount;
											int resultFailCount = failCount + 4;
											System.out.println("上传照片失败："
													+ exception.getMessage());
											ToastUtil
													.show(getActivity(),
															"上传E成功"
																	+ resutlSuccessCount
																	+ "张，失败"
																	+ resultFailCount
																	+ "张");

											if (nextTask != null) {
												waitingDialog.dismiss();
												dilogA(activity, "有"
														+ resultFailCount
														+ "张图片上传失败，请检查网络后重新提交！");
												image_int++;
												return;
											}
										}
									});
						}

						@Override
						public void onFail(String errorMsg) {
							int resutlSuccessCount = successCount;
							int resultFailCount = failCount + 4;
							System.out.println("上传照片失败：" + errorMsg);
							// ToastUtil.show(getActivity(), "上传F成功"
							// + resutlSuccessCount + "张，失败"
							// + resultFailCount + "张");

							if (nextTask != null) {
								waitingDialog.dismiss();
								dilogA(activity, "有" + resultFailCount
										+ "张图片上传失败，请检查网络后重新提交！");
								image_int++;
								return;
							}
						}
					});
		}
	}

	class UploadPictureLastTask extends UploadTask {
		private OnFinishListener finishListener;

		public void setOnFinishListener(OnFinishListener finishListener) {
			this.finishListener = finishListener;
		}

		@Override
		public void doTask(final int successCount, final int failCount) {
			if (finishListener != null) {
				finishListener.onFinish();
			}
		}
	}

	public interface OnFinishListener {
		public void onFinish();
	}

	private void registerBroadcastReceiver() {
		IntentFilter filter = new IntentFilter(
				MyPushReceiver.ACTION_CANCEL_ORDER);
		getActivity().registerReceiver(cancelOrderBroadcastReceiver, filter);
	}

	private void unregisterBroadcastReceiver() {
		getActivity().unregisterReceiver(mReceiver);
		getActivity().unregisterReceiver(cancelOrderBroadcastReceiver);
	}

	private BroadcastReceiver cancelOrderBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String cancelYid = intent.getStringExtra("yid");
			if (orderinfo.yid.equals(cancelYid)) {
				getActivity().finish();
			}
		}
	};

	/**
	 * 路线Listener
	 */
	OnGetRoutePlanResultListener listener = new OnGetRoutePlanResultListener() {
		public void onGetWalkingRouteResult(WalkingRouteResult result) {
			// 获取步行线路规划结果
		}

		public void onGetTransitRouteResult(TransitRouteResult result) {
			// 获取公交换乘路径规划结果
		}

		public void onGetDrivingRouteResult(DrivingRouteResult result) {
			// 获取驾车线路规划结果
			// baiduMap.clear();
			if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
				System.out.println("没有查到路线");
			}
			if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
				// 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
				// drivingRouteResult.getSuggestAddrInfo()
				return;
			}
			if (result.error == SearchResult.ERRORNO.NO_ERROR) {
				DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(
						baiduMap);
				if (result.getRouteLines().size() > 0) {
					drivingRouteOverlay.setData(result.getRouteLines().get(0));
					baiduMap.setOnMarkerClickListener(drivingRouteOverlay);
					drivingRouteOverlay.addToMap();
					drivingRouteOverlay.zoomToSpan();
				}
			}
		}

		@Override
		public void onGetBikingRouteResult(BikingRouteResult result) {
			// TODO Auto-generated method stub

		}
	};

	private void showRoute() {
		if (lineWayList.size() < 3) {
			System.out.println("划线至少4个点");
			return;
		}
		RoutePlanSearch search = RoutePlanSearch.newInstance();
		search.setOnGetRoutePlanResultListener(listener);
		LatLng startLatLng = null, endLatLng = null;
		List<PlanNode> nodeList = new ArrayList<PlanNode>();
		for (int i = 2; i < lineWayList.size(); i++) {
			LineWay currentLineWay = lineWayList.get(i);
			String[] currentPlaceArr = currentLineWay.address.split("\\|");
			if (currentPlaceArr != null && currentPlaceArr.length == 3) {
				if (i == 2) {
					startLatLng = new LatLng(
							Double.parseDouble(currentPlaceArr[2]),
							Double.parseDouble(currentPlaceArr[1]));
				} else if (i == lineWayList.size() - 1) {
					endLatLng = new LatLng(
							Double.parseDouble(currentPlaceArr[2]),
							Double.parseDouble(currentPlaceArr[1]));
				} else {
					PlanNode middleNode = PlanNode.withLocation(new LatLng(
							Double.parseDouble(currentPlaceArr[2]), Double
									.parseDouble(currentPlaceArr[1])));
					nodeList.add(middleNode);
					// 添加标志点
					markBitmap = BitmapDescriptorFactory
							.fromResource(R.drawable.ztd1);
					OverlayOptions overlayOptions = new MarkerOptions()
							.position(
									new LatLng(
											Double.parseDouble(currentPlaceArr[2]),
											Double.parseDouble(currentPlaceArr[1])))
							.icon(markBitmap);
					baiduMap.addOverlay(overlayOptions);
				}
			}
		}
		if (startLatLng != null && endLatLng != null) {
			PlanNode startNode = PlanNode.withLocation(startLatLng);
			PlanNode endNode = PlanNode.withLocation(endLatLng);
			DrivingRoutePlanOption drivingRoutePlanOption = new DrivingRoutePlanOption();
			drivingRoutePlanOption.from(startNode).to(endNode);
			if (nodeList.size() > 0) {
				drivingRoutePlanOption.passBy(nodeList);
			}
			search.drivingSearch(drivingRoutePlanOption);
		}
	}

	private void initGps() {
		LocationManager locationManager = (LocationManager) getActivity()
				.getSystemService(Context.LOCATION_SERVICE);
		boolean isOpen = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		if (!isOpen) {
			showGoSettingDialog();
			return;
		}
	}

	private void showGoSettingDialog() {
		if (goSettingGPSDialog == null) {
			goSettingGPSDialog = new Dialog(getActivity(), R.style.DiDiDialog);
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

	/**
	 * 构造广播监听类，监听 SDK key 验证以及网络异常广播
	 */
	public class SDKReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			String s = intent.getAction();
			System.out.println("action: " + s);
			if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
				Toast.makeText(getActivity(),
						"key 验证出错! 请在 AndroidManifest.xml 文件中检查 key 设置", 0)
						.show();
				;
			} else if (s
					.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
				Toast.makeText(getActivity(), "网络出错", 0).show();
			}
		}
	}

	private void dilogA(final Activity context, String msg) {
		final Dialog logoutDialog = new Dialog(context, R.style.DiDiDialog);
		logoutDialog.setContentView(R.layout.dialog_isnetworkavailable);
		final TextView text = (TextView) logoutDialog
				.findViewById(R.id.isnet_wanglu);
		final Button button = (Button) logoutDialog
				.findViewById(R.id.isnet_but);
		text.setText(msg);
		button.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {
				logoutDialog.dismiss();
				if (image_int >= 3) {
					GoOrderActivity activity = (GoOrderActivity) getActivity();
					waitingDialog.show(activity.getParentLayout());
					changeState();
				} else {
					GoOrderActivity activity = (GoOrderActivity) getActivity();
					waitingDialog.show(activity.getParentLayout());
					doLast();
				}

			}
		});
		logoutDialog.show();

	}

	OnGetGeoCoderResultListener ongeoa = new OnGetGeoCoderResultListener() {

		@Override
		public void onGetGeoCodeResult(GeoCodeResult arg0) {
		}

		@Override
		public void onGetReverseGeoCodeResult(ReverseGeoCodeResult arg0) {
			if (arg0.error == SearchResult.ERRORNO.NO_ERROR) {
				// // // startcity = arg0.getAddressDetail().city;
				// ToastUtil.show(getActivity(), arg0.getAddress());
				currentAddress = arg0.getAddress();
				return;
			}
		}

	};
}
