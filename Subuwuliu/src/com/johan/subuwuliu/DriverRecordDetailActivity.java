package com.johan.subuwuliu;

import java.util.HashMap;

import com.johan.subuwuliu.bean.DriverDetailBean;
import com.johan.subuwuliu.bean.DriverRouteBean;
import com.johan.subuwuliu.database.DriverDetailDatabase;
import com.johan.subuwuliu.database.DriverRouteDatabase;
import com.johan.subuwuliu.fragment.DriverRecordDetailFragment;
import com.johan.subuwuliu.fragment.DriverRecordRouteFragment;
import com.johan.subuwuliu.fragment.WaitingFragment;
import com.johan.subuwuliu.fragment.WaitingFragment.OnLoadListener;
import com.johan.subuwuliu.util.GsonUtil;
import com.johan.subuwuliu.util.NetUtil;
import com.johan.subuwuliu.util.ToolUtil;
import com.umeng.analytics.MobclickAgent;

import android.app.Fragment;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class DriverRecordDetailActivity extends AppActivity implements OnClickListener {

	private ImageView back;
	
	private TextView titleLeft, titleRight;
	
	private String yid;
	
	private Fragment currentFragment;

	@Override
	public int getContentView() {
		// TODO Auto-generated method stub
		return R.layout.activity_driverrecorddetail;
	}

	@Override
	public void findId() {
		// TODO Auto-generated method stub
		back = (ImageView) findViewById(R.id.driverrecorddetail_back);
		titleLeft = (TextView) findViewById(R.id.driverrecorddetail_title_left);
		titleRight = (TextView) findViewById(R.id.driverrecorddetail_title_right);
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		yid = getIntent().getExtras().getString("yid", "0");
		if("0".equals(yid)) {
			finish();
		}
		back.setOnClickListener(this);
		titleLeft.setOnClickListener(this);
		titleRight.setOnClickListener(this);
		loadRouteData();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.driverrecorddetail_back :
			finish();
			break;
		case R.id.driverrecorddetail_title_left :
			loadRouteData();
			break;
		case R.id.driverrecorddetail_title_right :
			loadDetailData();
			break;
		default:
			break;
		}
	}
	
	private void loadRouteData() {
		titleLeft.setSelected(true);
		titleRight.setSelected(false);
		HashMap<String, String> params = new HashMap<String, String>();
		String currentTimeStamp = "0";
		final DriverRouteDatabase database = new DriverRouteDatabase(this);
		final DriverRouteBean driverRoute = database.pull(yid);
		if(driverRoute != null) {
			currentTimeStamp = driverRoute.timestamp;
		}
		params.put("key", "record_list_line");
		params.put("username", getLoginName());
		params.put("pwd", getLoginPassword());
		params.put("timestamp", currentTimeStamp);
		params.put("yid", yid);
		String action = ToolUtil.encryptionUrlParams(params);
		WaitingFragment waitingFragment = new WaitingFragment(NetUtil.DRIVER_URL + action);
		waitingFragment.load(new OnLoadListener() {
			@Override
			public void onLoadSuccess(String result) {
				System.out.println("result----->" + result);
				DriverRouteBean driverRouteBean = GsonUtil.jsonToObjct(result, DriverRouteBean.class);
				database.pull(result);
				DriverRecordRouteFragment fragment = new DriverRecordRouteFragment();
				fragment.setDriverRouteInfoBean(driverRouteBean.info);
				showFragment(fragment);
				currentFragment = fragment;
			}
			@Override
			public void onLoadNoNeedUpdate() {
				System.out.println("不需要更新");
				DriverRecordRouteFragment fragment = new DriverRecordRouteFragment();
				fragment.setDriverRouteInfoBean(driverRoute.info);
				showFragment(fragment);
				currentFragment = fragment;
			}
		});
		if(currentFragment == null) {
			addFragment(waitingFragment);
		} else {
			showFragment(waitingFragment);
		}
	}
	private void loadDetailData() {
		titleLeft.setSelected(false);
		titleRight.setSelected(true);
		HashMap<String, String> params = new HashMap<String, String>();
		String currentTimeStamp = "0";
		final DriverDetailDatabase database = new DriverDetailDatabase(this);
		final DriverDetailBean driverDetail = database.pull(yid);
		if(driverDetail != null) {
			currentTimeStamp = driverDetail.timestamp;
		}
		params.put("key", "record_list_info");
		params.put("username", getLoginName());
		params.put("pwd", getLoginPassword());
		params.put("timestamp", currentTimeStamp);
		params.put("yid", yid);
		String action = ToolUtil.encryptionUrlParams(params);
		WaitingFragment waitingFragment = new WaitingFragment(NetUtil.DRIVER_URL + action);
		waitingFragment.load(new OnLoadListener() {
			@Override
			public void onLoadSuccess(String result) {
				System.out.println("result----->" + result);
				DriverDetailBean driverDetailBean = GsonUtil.jsonToObjct(result, DriverDetailBean.class);
				database.pull(result);
				DriverRecordDetailFragment fragment = new DriverRecordDetailFragment();
				fragment.setData(driverDetailBean.info);
				showFragment(fragment);
				currentFragment = fragment;
			}
			@Override
			public void onLoadNoNeedUpdate() {
				System.out.println("不需要更新");
				DriverRecordDetailFragment fragment = new DriverRecordDetailFragment();
				fragment.setData(driverDetail.info);
				showFragment(fragment);
				currentFragment = fragment;
			}
		});
		if(currentFragment == null) {
			addFragment(waitingFragment);
		} else {
			showFragment(waitingFragment);
		}
	}
	
	@Override
	public int getStatuBarColor() {
		// TODO Auto-generated method stub
		return Color.parseColor("#242736");
	}

	@Override
	public int getContainerId() {
		// TODO Auto-generated method stub
		return R.id.driverrecorddetail_fragment;
	}
	
	public View getParentView() {
		return findViewById(R.id.driverrecorddetail_layout);
	}
	public void onResume() {
	    super.onResume();
	    MobclickAgent.onResume(this);       //统计时长
	}
	public void onPause() {
	    super.onPause();
	    MobclickAgent.onPause(this);
	}
}
