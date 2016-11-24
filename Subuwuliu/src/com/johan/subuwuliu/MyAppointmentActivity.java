package com.johan.subuwuliu;

import java.util.HashMap;

import com.johan.subuwuliu.application.DiDiApplication;
import com.johan.subuwuliu.bean.MyAppointmentBean;
import com.johan.subuwuliu.fragment.MyAppiontmentFragment;
import com.johan.subuwuliu.fragment.WaitingFragment;
import com.johan.subuwuliu.fragment.WaitingFragment.OnLoadListener;
import com.johan.subuwuliu.util.GsonUtil;
import com.johan.subuwuliu.util.NetUtil;
import com.johan.subuwuliu.util.ToolUtil;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.SharedPreferences.Editor;

public class MyAppointmentActivity extends AppThemeActivity {

	public static Activity myappointmentActivity;

	@Override
	public String getThemeTitle() {
		// TODO Auto-generated method stub
		return "我的预约";
	}

	@Override
	public int getContentView() {
		// TODO Auto-generated method stub
		return R.layout.activity_myappointment;
	}

	@Override
	public void findId() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		myappointmentActivity = this;
//		String timestamp = getSharedPreferences().getString(
//				DiDiApplication.KEY_OF_APPOINTMENT_TIMESTAMP, "0");
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("key", "booking_list");
		params.put("username", getLoginName());
		params.put("pwd", getLoginPassword());
		params.put("pageindex", "1");
		params.put("pagesize", "10");
		params.put("keyword", "");
		String action = ToolUtil.encryptionUrlParams(params);
		System.out.println("预约订单消息提交参----------------------------" + action);
		WaitingFragment waitingFragment = new WaitingFragment(
				NetUtil.DRIVER_URL + action);

		waitingFragment.load(new OnLoadListener() {
			@Override
			public void onLoadSuccess(String result) {
				System.out.println("预约订单消息返回参----------------------------"
						+ result);
				MyAppointmentBean myAppointmentResult = GsonUtil.jsonToObjct(
						result, MyAppointmentBean.class);
				Editor editor = getSharedPreferences().edit();
				editor.putString(DiDiApplication.KEY_OF_APPOINTMENT_TIMESTAMP,
						myAppointmentResult.timestamp);
				editor.putString(DiDiApplication.KEY_OF_APPOINTMENT_DATA,
						result);
				editor.commit();
				// 显示真正的Fragment
				MyAppiontmentFragment fragment = new MyAppiontmentFragment();
				fragment.username = getLoginName();
				fragment.password = getLoginPassword();
				showFragment(fragment);
				fragment.setData(myAppointmentResult.list);
			}

			@Override
			public void onLoadNoNeedUpdate() {

				String oldResponseInfo = getSharedPreferences().getString(
						DiDiApplication.KEY_OF_APPOINTMENT_DATA,
						DiDiApplication.VALUE_UNKOWN);
				System.out.println("不需要更新预约订单消息----------------------------"
						+ oldResponseInfo);
				MyAppointmentBean oldAppointmentBean = GsonUtil.jsonToObjct(
						oldResponseInfo, MyAppointmentBean.class);
				// 显示真正的Fragment
				MyAppiontmentFragment fragment = new MyAppiontmentFragment();
				fragment.username = getLoginName();
				fragment.password = getLoginPassword();
				showFragment(fragment);
				fragment.setData(oldAppointmentBean.list);
			}
		});
		addFragment(waitingFragment);
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
//		String timestamp = getSharedPreferences().getString(
//				DiDiApplication.KEY_OF_APPOINTMENT_TIMESTAMP, "0");
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("key", "booking_list");
		params.put("username", getLoginName());
		params.put("pwd", getLoginPassword());
		params.put("pageindex", "1");
		params.put("pagesize", "10");
		params.put("keyword", "");
		String action = ToolUtil.encryptionUrlParams(params);
		System.out.println("预约订单消息提交参----------------------------" + action);
		WaitingFragment waitingFragment = new WaitingFragment(
				NetUtil.DRIVER_URL + action);

		waitingFragment.load(new OnLoadListener() {
			@Override
			public void onLoadSuccess(String result) {
				System.out.println("预约订单消息返回参----------------------------"
						+ result);
				MyAppointmentBean myAppointmentResult = GsonUtil.jsonToObjct(
						result, MyAppointmentBean.class);
				Editor editor = getSharedPreferences().edit();
				editor.putString(DiDiApplication.KEY_OF_APPOINTMENT_TIMESTAMP,
						myAppointmentResult.timestamp);
				editor.putString(DiDiApplication.KEY_OF_APPOINTMENT_DATA,
						result);
				editor.commit();
				// 显示真正的Fragment
				MyAppiontmentFragment fragment = new MyAppiontmentFragment();
				showFragment(fragment);
				fragment.setData(myAppointmentResult.list);
			}

			@Override
			public void onLoadNoNeedUpdate() {

				String oldResponseInfo = getSharedPreferences().getString(
						DiDiApplication.KEY_OF_APPOINTMENT_DATA,
						DiDiApplication.VALUE_UNKOWN);
				System.out.println("不需要更新预约订单消息----------------------------"
						+ oldResponseInfo);
				MyAppointmentBean oldAppointmentBean = GsonUtil.jsonToObjct(
						oldResponseInfo, MyAppointmentBean.class);
				// 显示真正的Fragment
				MyAppiontmentFragment fragment = new MyAppiontmentFragment();
				showFragment(fragment);
				fragment.setData(oldAppointmentBean.list);
			}
		});
	}

	@Override
	public int getContainerId() {
		// TODO Auto-generated method stub
		return R.id.myappointment_fragment;
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
