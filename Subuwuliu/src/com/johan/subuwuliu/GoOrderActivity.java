package com.johan.subuwuliu;

import java.util.HashMap;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.trace.LBSTraceClient;
import com.johan.subuwuliu.bean.GoOrderBean;
import com.johan.subuwuliu.fragment.GoOrderFragment;
import com.johan.subuwuliu.fragment.WaitingFragment;
import com.johan.subuwuliu.fragment.WaitingFragment.OnLoadListener;
import com.johan.subuwuliu.util.GsonUtil;
import com.johan.subuwuliu.util.NetUtil;
import com.johan.subuwuliu.util.ToolUtil;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class GoOrderActivity extends AppTheme2Activity {
	private LBSTraceClient client;
	private String orderNo;
	private GoOrderFragment fragment;
	public static Activity activity;
	

	@Override
	public void preSetContentView(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		SDKInitializer.initialize(getApplicationContext());
	}

	@Override
	public String getThemeTitle() {
		// TODO Auto-generated method stub
		return "速步物流";
	}

	@Override
	public String getThemeTip() {
		// TODO Auto-generated method stub
		return "更多";
	}

	@Override
	public void clickThemeTip() {
		// TODO Auto-generated method stub
		Bundle data = new Bundle();
		data.putString("yid", orderNo);
		goActivity(DriverRecordDetailActivity.class, data);
	}

	@Override
	public int getContentView() {
		// TODO Auto-generated method stub
		return R.layout.activity_goorder;
	}

	@Override
	public void findId() {
		// TODO Auto-generated method stub
		activity=this;
		client = new LBSTraceClient(getApplicationContext());
	}

	@Override
	public void init() {
		// 获取订单号
		orderNo = getIntent().getStringExtra("order_no");
		if ("".equals(orderNo)) {
			finish();
		}
		// 初始化Fragment
		fragment = new GoOrderFragment();
		// 加载数据
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("key", "waybill_line");
		params.put("username", getLoginName());
		params.put("pwd", getLoginPassword());
		// 测试订单号："Y16030922530351"
		params.put("yid", orderNo);
		String action = ToolUtil.encryptionUrlParams(params);
		WaitingFragment waitingFragment = new WaitingFragment(
				NetUtil.DRIVER_URL + action);
		waitingFragment.load(new OnLoadListener() {
			@Override
			public void onLoadSuccess(String result) {
				System.out.println("---->" + result);
				GoOrderBean goOrderBean = GsonUtil.jsonToObjct(result,
						GoOrderBean.class);
				if ("0".equals(goOrderBean.status)) {
					fragment.setOrderinfo(goOrderBean.info);
					showFragment(fragment);
				}
			}

			@Override
			public void onLoadNoNeedUpdate() {

			}
		});
		addFragment(waitingFragment);
	}

	@Override
	public int getContainerId() {
		// TODO Auto-generated method stub
		return R.id.goorder_fragment;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (resultCode == Activity.RESULT_OK
				&& requestCode == GoOrderFragment.REQUEST_UPLOAD_PICTURE) {
			String uploadUrl = data.getStringExtra("url");
			System.out.println("----->" + uploadUrl);
			fragment.setImg(uploadUrl);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public View getParentLayout() {
		return findViewById(R.id.goorder_layout);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
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
