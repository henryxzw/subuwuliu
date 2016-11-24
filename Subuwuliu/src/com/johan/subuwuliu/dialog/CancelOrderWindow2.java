package com.johan.subuwuliu.dialog;

import java.util.HashMap;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.johan.subuwuliu.AppActivity;
import com.johan.subuwuliu.HomeActivity;
import com.johan.subuwuliu.MyQianBaoTiXian;
import com.johan.subuwuliu.Myqianbao;
import com.johan.subuwuliu.Myqianbao_chenggong;
import com.johan.subuwuliu.R;
import com.johan.subuwuliu.bean.QianBaoBean;
import com.johan.subuwuliu.bean.StatusBean;
import com.johan.subuwuliu.bean.TuiSongBean;
import com.johan.subuwuliu.server.ForegroundServer;
import com.johan.subuwuliu.util.GsonUtil;
import com.johan.subuwuliu.util.NetUtil;
import com.johan.subuwuliu.util.ToolUtil;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import android.content.Intent;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CancelOrderWindow2 extends AppActivity {

	private TextView yid, tip;
	private Button but1, but2;
	private LocationClient mLocationClient;
	private BDLocationListener myListener = new MyLocationListener();
	private boolean isFirst = true;
	private String end_addr;

	private LinearLayout linear_tf1, linear_tf2;
	private TextView linear_text_a, linear_text_b;
	private Button linear_text_c;

	@Override
	public int getContentView() {
		// TODO Auto-generated method stub
		return R.layout.window_cancelorder2;
	}

	@Override
	public void findId() { 
		// TODO Auto-generated method stub
		yid = (TextView) findViewById(R.id.cancelorder2_yid);
		tip = (TextView) findViewById(R.id.cancelorder2_tip);
		but1 = (Button) findViewById(R.id.cancelorder2_but_1);
		but2 = (Button) findViewById(R.id.cancelorder2_but_2);
		linear_tf1 = (LinearLayout) findViewById(R.id.linearlayou_tf1);
		linear_tf2 = (LinearLayout) findViewById(R.id.linearlayou_tf2);
		linear_tf2.setVisibility(View.GONE);
		linear_text_a = (TextView) findViewById(R.id.linear_text_a);
		linear_text_b = (TextView) findViewById(R.id.linear_text_b);
		linear_text_c = (Button) findViewById(R.id.linear_text_c);

	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		mLocationClient = new LocationClient(this); // 声明LocationClient类
		// 调节Activity的大小
		WindowManager manager = getWindowManager();
		Display display = manager.getDefaultDisplay();
		WindowManager.LayoutParams params = getWindow().getAttributes();
		params.width = display.getWidth();
		params.height = display.getHeight();
		getWindow().setAttributes(params);
		RelativeLayout layout = (RelativeLayout) findViewById(R.id.cancelorder2_layout);
		layout.setPadding(0, getStatusBarHeight(), 0, 0);
		// 初始化View
		if (getIntent() == null) {
			onBackPressed();
			return;
		}
		final TuiSongBean dataBean = (TuiSongBean) getIntent()
				.getSerializableExtra("data_bean");
		yid.setText("订单号：" + dataBean.yid);
		tip.setText("已行驶" + dataBean.travel_distance + "公里，返空费"
				+ dataBean.return_money + "元");
		but1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getinto("1", dataBean.yid, end_addr);
			}
		});
		but2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getinto("2", dataBean.yid, end_addr);

			}
		});
		linear_text_c.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		overridePendingTransition(0, R.anim.anim_popupwindow_out);
	}

	private void getinto(final String type, String yid, String end_addr) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("key", "confirm_cancel");
		params.put("username", getLoginName());
		params.put("pwd", getLoginPassword());
		params.put("type", type);
		params.put("yid", yid);
		params.put("end_addr", end_addr);
		String action = ToolUtil.encryptionUrlParams(params);
		final WaitingDialog dialog = new WaitingDialog(this);
		dialog.show(findViewById(R.id.cancelorder2_layout));
		NetUtil.getInstance().doGet(NetUtil.ORDER_URL + action,
				new RequestCallBack<String>() {
					@Override
					public void onFailure(HttpException arg0, String arg1) {
						showToast("没有连网络");
						dialog.dismiss();

					}

					@Override
					public void onSuccess(final ResponseInfo<String> arg0) {
						System.out.println("--------------------->>>"
								+ arg0.result);
						StatusBean StatusBean = GsonUtil.jsonToObjct(
								arg0.result, StatusBean.class);
						dialog.dismiss();
						if (StatusBean.status.equals("0")) {
							linear_tf1.setVisibility(View.GONE);
							linear_tf2.setVisibility(View.VISIBLE);
							// // onBackPressed();
							if (type.equals("1")) {
								linear_text_a.setText("您已同意运单的取消");
								linear_text_b.setText("返空费用将会在7个工作日内达到账号上");
							} else {
								linear_text_a.setText("您已申请客服介入");
								linear_text_b.setText("我们的客服将会在3个工作日内联系您");
							}
						} else {
							showToast(StatusBean.msg);
						}
					}
				});

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
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		initLocation();
	}

	private void initLocation() {
		mLocationClient = new LocationClient(this); // 声明LocationClient类
		mLocationClient.registerLocationListener(myListener); // 注册监听函数
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);// 设置定位模式
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
		option.setScanSpan(5000);// 设置发起定位请求的间隔时间为5000ms
		option.setIsNeedAddress(true);// 返回的定位结果包含地址信息
		option.setNeedDeviceDirect(true);// 返回的定位结果包含手机机头的方向
		mLocationClient.setLocOption(option);
		mLocationClient.start();
	}

	public class MyLocationListener implements BDLocationListener {

		public void onReceiveLocation(BDLocation location) {
			// TODO Auto-generated method stub
			if (isFirst) {
				isFirst = false;
				// // now_dizi.setText("当前位置:" + location.getAddrStr());
				Double lat = location.getLatitude();
				Double lng = location.getLongitude();
				end_addr = location.getAddrStr() + "|" + lng + "|" + lat;
				System.out.println("--------定位的经纬度----------------->>>>"
						+ end_addr);
				mLocationClient.stop();// 定位一次之后 就停止
			}
		}
	}
}
