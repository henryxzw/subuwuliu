package com.johan.subuwuliu;

import java.util.HashMap;

import com.johan.subuwuliu.application.DiDiApplication;
import com.johan.subuwuliu.bean.DriverRecordBean;
import com.johan.subuwuliu.fragment.DriverRecordFragment;
import com.johan.subuwuliu.fragment.WaitingFragment;
import com.johan.subuwuliu.fragment.WaitingFragment.OnLoadListener;
import com.johan.subuwuliu.util.GsonUtil;
import com.johan.subuwuliu.util.NetUtil;
import com.johan.subuwuliu.util.ToolUtil;
import com.umeng.analytics.MobclickAgent;

import android.content.SharedPreferences.Editor;

public class DriverRecordActivity extends AppThemeActivity {
	
	@Override
	public String getThemeTitle() {
		// TODO Auto-generated method stub
		return "运单记录";
	}

	@Override
	public int getContentView() {
		// TODO Auto-generated method stub
		return R.layout.activity_driverrecord;
	}

	@Override
	public void findId() {
		// TODO Auto-generated method stub
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		//加载数据
		String timestamp = getSharedPreferences().getString(DiDiApplication.KEY_OF_DRIVERRECORD_TIMESTAMP, "0");
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("key", "record_list");
		params.put("username", getLoginName());
		params.put("pwd", getLoginPassword());
		params.put("pageindex", "1");
		params.put("pagesize", "5");
		params.put("keyword", "");
		String action = ToolUtil.encryptionUrlParams(params);
		WaitingFragment waitingFragment = new WaitingFragment(NetUtil.DRIVER_URL + action);
		waitingFragment.load(new OnLoadListener() {
			@Override
			public void onLoadSuccess(String result) {
				System.out.println("---->" + result);
				DriverRecordBean driverRecordResult = GsonUtil.jsonToObjct(result, DriverRecordBean.class);
				Editor editor = getSharedPreferences().edit();
				editor.putString(DiDiApplication.KEY_OF_DRIVERRECORD_TIMESTAMP, driverRecordResult.timestamp);
				editor.putString(DiDiApplication.KEY_OF_DRIVERRECORD_DATA, result);
				editor.commit();
				DriverRecordFragment fragment = new DriverRecordFragment(driverRecordResult.list);
				fragment.username= getLoginName();
				fragment.password = getLoginPassword();
				showFragment(fragment);
			}
			@Override
			public void onLoadNoNeedUpdate() {
				String oldResponseInfo = getSharedPreferences().getString(DiDiApplication.KEY_OF_DRIVERRECORD_DATA, DiDiApplication.VALUE_UNKOWN);
				System.out.println("---->" + oldResponseInfo);
				DriverRecordBean oldDriverRecordBean = GsonUtil.jsonToObjct(oldResponseInfo, DriverRecordBean.class);
				DriverRecordFragment fragment = new DriverRecordFragment(oldDriverRecordBean.list);
				fragment.username= getLoginName();
				fragment.password = getLoginPassword();
				showFragment(fragment);
			}
		});
		addFragment(waitingFragment);
	}
 
	@Override
	public int getContainerId() {
		// TODO Auto-generated method stub
		return R.id.driverrecord_fragment;
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
