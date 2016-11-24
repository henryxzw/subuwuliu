package com.johan.subuwuliu.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.johan.subuwuliu.BackLocationActivity;
import com.johan.subuwuliu.R;
import com.umeng.analytics.MobclickAgent;

public class HomeModeBackLocationFragment extends AppFragment implements OnClickListener {
	
	public static final int REQUEST_LOCATION_CODE = 250;
	
	private TextView searchbar;
	private TextView location;
	
	private String backAddress;
	
	private String backCity;
	
	private String backLongitude;
	private String backLatitude;
	
	private LinearLayout locationLayout;
	private ImageView locationCancel;

	@Override
	public int getContentView() {
		// TODO Auto-generated method stub
		return R.layout.fragment_homemode_backlocation;
	}

	@Override
	public void findId() {
		// TODO Auto-generated method stub
		searchbar = (TextView) layout.findViewById(R.id.homemode_backlocation_search);
		location = (TextView) layout.findViewById(R.id.homemode_backlocation_location);
		locationLayout = (LinearLayout) layout.findViewById(R.id.homemode_backlocation_location_layout);
		locationCancel = (ImageView) layout.findViewById(R.id.homemode_backlocation_cancel);
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		searchbar.setOnClickListener(this);
		locationCancel.setOnClickListener(this);
		initView();
	}
	
	private void initView() {
		if(backAddress == null || "".equals(backAddress)) {
			if(backCity == null || "".equals(backCity)) {
				locationLayout.setVisibility(View.GONE);
				searchbar.setVisibility(View.VISIBLE);
			} else {
				searchbar.setVisibility(View.GONE);
				locationLayout.setVisibility(View.VISIBLE);
				location.setText(backCity);
			}
		} else {
			searchbar.setVisibility(View.GONE);
			locationLayout.setVisibility(View.VISIBLE);
			location.setText(backAddress);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.homemode_backlocation_search :
			Intent intent = new Intent(getActivity(), BackLocationActivity.class);
			getActivity().startActivityForResult(intent, REQUEST_LOCATION_CODE);
			break;
		case R.id.homemode_backlocation_cancel :
			Message message = uiHandler.obtainMessage();
			message.arg1 = 1;
			Bundle messageData = new Bundle();
			messageData.putString("back_city", "");
			messageData.putString("back_address", "");
			message.setData(messageData);
			uiHandler.sendMessage(message);
		default:
			break;
		}
	}
	
	public String getBackAddress() {
		return this.backAddress;
	}
	
	public String getBackCity() {
		return this.backCity;
	}
	
	public String getBackLongitude() {
		return this.backLongitude;
	}
	
	public String getBackLatitude() {
		return this.backLatitude;
	}
	
	private boolean isInited = false;
	
	private Handler uiHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if(!isInited || (msg.arg1 == 1)) {
				isInited = true;
				Bundle data = msg.getData();
				backAddress = data.getString("back_address");
				backCity = data.getString("back_city");
				backLongitude = data.getString("back_longitude");
				backLatitude = data.getString("back_latitude");
				System.out.println("city--->" + backCity);
				initView();
			}
		};
	};
	
	public Handler getUiHandler() {
		return uiHandler;
	}
	
	public void setData(String backAddress, String backCity, String backLongitude, String backLatitude) {
		this.backAddress = backAddress;
		this.backCity = backCity;
		this.backLongitude = backLongitude;
		this.backLatitude = backLatitude;
	}
	public void onResume() {
	    super.onResume();
	    MobclickAgent.onPageStart("HomeModeBackLocationFragment"); //统计页面，"MainScreen"为页面名称，可自定义
	}
	public void onPause() {
	    super.onPause();
	    MobclickAgent.onPageEnd("HomeModeBackLocationFragment"); 
	}
}
