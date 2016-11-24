package com.johan.subuwuliu.dialog;

import java.math.BigDecimal;
import java.util.HashMap;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.johan.subuwuliu.AppActivity;
import com.johan.subuwuliu.R;
import com.johan.subuwuliu.adapter.PictureAdapter;
import com.johan.subuwuliu.bean.OrderBean;
import com.johan.subuwuliu.bean.OrderBean.OrderDetailBean;
import com.johan.subuwuliu.util.FormatUtil;
import com.johan.subuwuliu.util.GsonUtil;
import com.johan.subuwuliu.util.NetUtil;
import com.johan.subuwuliu.util.ToastUtil;
import com.johan.subuwuliu.util.ToolUtil;
import com.johan.subuwuliu.view.OrderWayLayout;
import com.johan.subuwuliu.view.ScrollGridView;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ReceiverOrderWindow extends AppActivity implements OnClickListener {

	private TextView tip, tipTip, distance, price, receiverTime, Text_order1;

	private TextView extraRequest, extraInfo, remark;

	private ScrollGridView carListView;

	private Button receiverBut;

	private ImageView closeBut;

	private TextView addressBut, mapBut, detailBut;
	private LinearLayout addressLayout, mapLayout, detailLayout, linear_order1,
			linear_order2;
	private boolean isFirstClick = true;

	private int layoutHeight, butHeight;

	private LinearLayout currentLayout;

	private MapView mapView = null;
	private BaiduMap baiduMap;

	private OrderWayLayout orderWayLayout;

	private LatLng startLatLng, endLatLng;

	private OrderDetailBean orderDetail;

	@Override
	public int getContentView() {
		// TODO Auto-generated method stub
		return R.layout.window_receiverorder;
	}

	@Override
	public void preSetContentView(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.preSetContentView(savedInstanceState);
		SDKInitializer.initialize(getApplicationContext());
	}

	@Override
	public int getStatuBarColor() {
		// TODO Auto-generated method stub
		return Color.parseColor("#a0000000");
	}

	@Override
	public void findId() {
		// TODO Auto-generated method stub
		this.tip = (TextView) findViewById(R.id.receiverorder_tip);
		this.tipTip = (TextView) findViewById(R.id.receiverorder_tip_tip);
		this.distance = (TextView) findViewById(R.id.receiverorder_distance);
		this.receiverTime = (TextView) findViewById(R.id.receiverorder_receiver_time);
		this.price = (TextView) findViewById(R.id.receiverorder_price);
		this.addressBut = (TextView) findViewById(R.id.receiverorder_address_but);
		this.mapBut = (TextView) findViewById(R.id.receiverorder_map_but);
		this.detailBut = (TextView) findViewById(R.id.receiverorder_detail_but);
		this.addressLayout = (LinearLayout) findViewById(R.id.receiverorder_address_layout);
		this.mapLayout = (LinearLayout) findViewById(R.id.receiverorder_map_layout);
		this.detailLayout = (LinearLayout) findViewById(R.id.receiverorder_detail_layout);
		this.closeBut = (ImageView) findViewById(R.id.receiverorder_close);
		this.mapView = (MapView) findViewById(R.id.receiverorder_map_view);
		this.orderWayLayout = (OrderWayLayout) findViewById(R.id.receiverorder_address_way_layout);
		this.extraRequest = (TextView) findViewById(R.id.receiverorder_extra_request);
		this.extraInfo = (TextView) findViewById(R.id.receiverorder_extra_info);
		this.remark = (TextView) findViewById(R.id.receiverorder_remark);
		this.carListView = (ScrollGridView) findViewById(R.id.receiverorder_car_list);
		this.receiverBut = (Button) findViewById(R.id.receiverorder_but);
		this.linear_order1 = (LinearLayout) findViewById(R.id.linear_order1);
		this.linear_order2 = (LinearLayout) findViewById(R.id.linear_order2);
		this.Text_order1 = (TextView) findViewById(R.id.Text_order1);
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		// 调节Activity的大小
		WindowManager manager = getWindowManager();
		Display display = manager.getDefaultDisplay();
		WindowManager.LayoutParams params = getWindow().getAttributes();
		params.width = display.getWidth();
		getWindow().setAttributes(params);
		addressLayout.setVisibility(View.VISIBLE);
		LinearLayout layout = (LinearLayout) findViewById(R.id.receiverorder_layout);
		layout.setPadding(0, getStatusBarHeight(), 0, 0);
		// 设置当前layout
		currentLayout = addressLayout;
		// 监听
		addressBut.setOnClickListener(this);
		mapBut.setOnClickListener(this);
		detailBut.setOnClickListener(this);
		closeBut.setOnClickListener(this);
		receiverBut.setOnClickListener(this);
		// tip显示
		tip.setText("正在记载订单");
		tipTip.setText("......");
		// 确认订单和加载订单
		if (getIntent() == null) {
			onBackPressed();
			return;
		}
		String yid = getIntent().getStringExtra("yid");
		new LoadingOrderTask().execute(yid);
	}

	public void initView() {
		// 距离
		distance.setText("距离：" + orderDetail.distance + "公里");
		double jiage = Double.valueOf(orderDetail.trip_amount)
				+ Double.valueOf(orderDetail.request_amount)
				+ Double.valueOf(orderDetail.carry_amount);
		double shoyi=jiage-(jiage*(Double.valueOf(orderDetail.spvan_quota))); 
		// 价格
		price.setText("收益：￥" + String.valueOf(new BigDecimal(shoyi).setScale(0, BigDecimal.ROUND_HALF_UP)));
		// 装货时间
		receiverTime.setText("装货时间："
				+ FormatUtil.formatTime(orderDetail.use_time,
						"MM月dd日 HH:mm EEEE"));
		// 路线
		orderWayLayout.add(OrderWayLayout.WAY_BEGIN, orderDetail.address_start,
				"");
		orderWayLayout.add(OrderWayLayout.WAY_MIDDLE,
				orderDetail.address_midway1, "");
		orderWayLayout.add(OrderWayLayout.WAY_MIDDLE,
				orderDetail.address_midway2, "");
		orderWayLayout.add(OrderWayLayout.WAY_MIDDLE,
				orderDetail.address_midway3, "");
		orderWayLayout.add(OrderWayLayout.WAY_MIDDLE,
				orderDetail.address_midway4, "");
		orderWayLayout.add(OrderWayLayout.WAY_MIDDLE,
				orderDetail.address_midway5, "");
		orderWayLayout.add(OrderWayLayout.WAY_END, orderDetail.address_end, "");
		// 设置是否显示比例尺控件
		mapView.showScaleControl(true);
		// 设置是否显示缩放控件
		mapView.showZoomControls(false);
		baiduMap = mapView.getMap();
		// 更新地图状态
		MapStatus mapStatus = new MapStatus.Builder().zoom(16).build();
		MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory
				.newMapStatus(mapStatus);
		baiduMap.animateMapStatus(mapStatusUpdate);
		String[] startAddress = orderDetail.address_start.split("\\|");
		if (startAddress != null && startAddress.length == 3) {
			startLatLng = new LatLng(Double.parseDouble(startAddress[2]),
					Double.parseDouble(startAddress[1]));
		}
		String[] endAddress = orderDetail.address_end.split("\\|");
		if (endAddress != null && endAddress.length == 3) {
			endLatLng = new LatLng(Double.parseDouble(endAddress[2]),
					Double.parseDouble(endAddress[1]));
		}
		// 额外需求
		if (orderDetail.extra_request.replace(",", " ").equals("")) {
			linear_order1.setVisibility(View.GONE);
		}
		extraRequest.setText(orderDetail.extra_request.replace(",", " "));
		// 额外信息
		if (FormatUtil.formatExtraInfo(orderDetail).equals("")) {
			linear_order2.setVisibility(View.GONE);
		}
		extraInfo.setText(FormatUtil.formatExtraInfo(orderDetail));
		// 备注
		if (orderDetail.remark.equals("")) {
			remark.setVisibility(View.GONE);
		}
		remark.setText("备注：" + orderDetail.remark);
		// 车辆图片
		if (orderDetail.img_url.equals("")) {
			carListView.setVisibility(View.GONE);
			Text_order1.setVisibility(View.GONE);
		}
		PictureAdapter adapter = new PictureAdapter(this,
				FormatUtil.formatPicture(orderDetail.img_url), null);
		adapter.setShowPicture(false);
		carListView.setAdapter(adapter);
	}

	OnGetRoutePlanResultListener listener = new OnGetRoutePlanResultListener() {
		public void onGetWalkingRouteResult(WalkingRouteResult result) {
			// 获取步行线路规划结果
		}

		public void onGetTransitRouteResult(TransitRouteResult result) {
			// 获取公交换乘路径规划结果
		}

		public void onGetDrivingRouteResult(DrivingRouteResult result) {
			// 获取驾车线路规划结果
			baiduMap.clear();
			System.out.println("result ---> " + result.error);
			if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
				Toast.makeText(ReceiverOrderWindow.this, "抱歉，未找到结果",
						Toast.LENGTH_SHORT).show();
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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.receiverorder_address_but:
			addressBut.setEnabled(false);
			mapBut.setEnabled(true);
			detailBut.setEnabled(true);
			selected(addressLayout); 
			break;
		case R.id.receiverorder_map_but: 
			addressBut.setEnabled(true);
			mapBut.setEnabled(false);
			detailBut.setEnabled(true);
			initSize();
			selected(mapLayout);
			// 显示地图
			if (startLatLng != null && endLatLng != null) {  
				RoutePlanSearch search = RoutePlanSearch.newInstance();
				search.setOnGetRoutePlanResultListener(listener);
				PlanNode startNode = PlanNode.withLocation(startLatLng);
				PlanNode endNode = PlanNode.withLocation(endLatLng);
				DrivingRoutePlanOption drivingRoutePlanOption = new DrivingRoutePlanOption();
				drivingRoutePlanOption.from(startNode).to(endNode);
				search.drivingSearch(drivingRoutePlanOption);
			}
			break;
		case R.id.receiverorder_detail_but:
			addressBut.setEnabled(true);
			mapBut.setEnabled(true);
			detailBut.setEnabled(false);
			initSize();
			selected(detailLayout);
			break;
		case R.id.receiverorder_close:
			onBackPressed();
			break;
		case R.id.receiverorder_but:
			System.out.println("关闭接到的订单");
			onBackPressed();
			break;
		default:
			break;
		}
	}

	private void initSize() {
		if (isFirstClick) {
			layoutHeight = addressLayout.getHeight();
			butHeight = mapBut.getHeight();
			isFirstClick = false;
		}
	}

	private void selected(LinearLayout selectedLayout) {
		// 修改选择的高度
		RelativeLayout.LayoutParams selectedParams = (RelativeLayout.LayoutParams) selectedLayout
				.getLayoutParams();
		selectedParams.height = layoutHeight;
		selectedLayout.setLayoutParams(selectedParams);
		selectedLayout.setVisibility(View.VISIBLE);
		// 修改当前的高度
		RelativeLayout.LayoutParams currentParams = (RelativeLayout.LayoutParams) currentLayout
				.getLayoutParams();
		currentParams.height = butHeight;
		currentLayout.setLayoutParams(currentParams);
		currentLayout.setVisibility(View.INVISIBLE);
		// 重新定义当前layout
		currentLayout = selectedLayout;
	}

	/**
	 * 获取状态栏高度
	 * 
	 * @return
	 */
	public int getStatusBarHeight() {
		int result = 0;
		int resourceId = getResources().getIdentifier("status_bar_height",
				"dimen", "android");
		if (resourceId > 0) {
			result = getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		overridePendingTransition(0, R.anim.anim_popupwindow_out);
	}

	class LoadingOrderTask extends AsyncTask<String, Void, Void> {
		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			final String yid = params[0];
			// 获取订单详情
			HashMap<String, String> param = new HashMap<String, String>();
			param.put("key", "order_info");
			param.put("username", getLoginName());
			param.put("pwd", getLoginPassword());
			param.put("order_no", yid);
			param.put("timestamp", "0");
			String action = ToolUtil.encryptionUrlParams(param);
			NetUtil.getInstance().doGet(NetUtil.DRIVER_URL + action,
					new RequestCallBack<String>() {
						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
							// TODO Auto-generated method stub
							OrderBean orderBean = GsonUtil.jsonToObjct(
									responseInfo.result, OrderBean.class);
							orderDetail = orderBean.order;
							Message msg = uiHandler.obtainMessage();
							msg.arg1 = ORDER_SUCCESS;
							uiHandler.sendMessage(msg);
						}

						@Override
						public void onFailure(HttpException arg0, String arg1) {
							ToastUtil.show(ReceiverOrderWindow.this,
									"抱歉，由于网络不佳，无法获取订单详情，但是您接单成功！");
							Message msg = uiHandler.obtainMessage();
							msg.arg1 = ORDER_FAIL;
							uiHandler.sendMessage(msg);
						}
					});
			return null;
		}
	}

	private static final int ORDER_SUCCESS = 1;
	private static final int ORDER_FAIL = 2;

	private Handler uiHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			int result = msg.arg1;
			switch (result) {
			case ORDER_SUCCESS:
				tip.setText("新的意向订单");
				tipTip.setText("（等待用户付款）");
				initView();
				break;
			case ORDER_FAIL:
				tip.setText("确认订单成功，加载订单详情失败");
				tipTip.setText("");
			default:
				break;
			}
		}
	};

}
