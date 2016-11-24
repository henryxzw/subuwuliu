package com.johan.subuwuliu;

import java.util.HashMap;

import com.johan.subuwuliu.bean.OrderBean;
import com.johan.subuwuliu.bean.OrderBean.OrderDetailBean;
import com.johan.subuwuliu.database.OrderDetailDatabase;
import com.johan.subuwuliu.fragment.OrderDetailFragment;
import com.johan.subuwuliu.fragment.WaitingFragment;
import com.johan.subuwuliu.fragment.WaitingFragment.OnLoadListener;
import com.johan.subuwuliu.util.GsonUtil;
import com.johan.subuwuliu.util.NetUtil;
import com.johan.subuwuliu.util.ToolUtil;

import android.view.View;

public class OrderDetailActivity extends AppThemeActivity {

	@Override
	public String getThemeTitle() {
		// TODO Auto-generated method stub
		return "订单详情";
	}

	@Override
	public int getContentView() {
		// TODO Auto-generated method stub
		return R.layout.activity_orderdetail;
	}

	@Override
	public void findId() {
		// TODO Auto-generated method stub
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		String orderNumberValue = getIntent().getExtras().getString("order_number");
		//先查找数据库的
		final OrderDetailDatabase database = new OrderDetailDatabase(this);
		final OrderDetailBean orderDetail = database.pull(orderNumberValue);
		String orderTimestamp = "0";
		if(null != orderDetail) {
			orderTimestamp = orderDetail.timestamp;
		}
		//请求
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("key", "order_info");
		params.put("username", getLoginName());
		params.put("pwd", getLoginPassword());
		params.put("order_no", orderNumberValue);
		String action = ToolUtil.encryptionUrlParams(params);
		WaitingFragment waitingFragment = new WaitingFragment(NetUtil.DRIVER_URL + action);
		waitingFragment.load(new OnLoadListener() {
			@Override
			public void onLoadSuccess(String result) {
				database.push(result);
				OrderBean orderBean = GsonUtil.jsonToObjct(result, OrderBean.class);
				OrderDetailFragment fragment = new OrderDetailFragment(orderBean.order);
				showFragment(fragment);
			}
			@Override
			public void onLoadNoNeedUpdate() {
				OrderDetailFragment fragment = new OrderDetailFragment(orderDetail);
				showFragment(fragment);
			}
		});
		addFragment(waitingFragment);
	}

	@Override
	public int getContainerId() {
		// TODO Auto-generated method stub
		return R.id.orderdetail_fragment;
	}
	
	public View getParentView() {
		return findViewById(R.id.orderdetail_layout);
	}

}
