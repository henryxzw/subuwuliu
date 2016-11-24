package com.johan.subuwuliu.dialog;

import java.util.HashMap;

import com.johan.subuwuliu.AppActivity;
import com.johan.subuwuliu.R;
import com.johan.subuwuliu.application.DiDiApplication;
import com.johan.subuwuliu.bean.CityBean;
import com.johan.subuwuliu.bean.ModeBean;
import com.johan.subuwuliu.bean.ModeBean.ModeInfoBean;
import com.johan.subuwuliu.bean.StatusBean;
import com.johan.subuwuliu.database.CityHistoryDatabase;
import com.johan.subuwuliu.fragment.HomeModeAppointmentFragment;
import com.johan.subuwuliu.fragment.HomeModeBackLocationFragment;
import com.johan.subuwuliu.fragment.WaitingFragment;
import com.johan.subuwuliu.fragment.WaitingFragment.OnLoadListener;
import com.johan.subuwuliu.util.GsonUtil;
import com.johan.subuwuliu.util.NetUtil;
import com.johan.subuwuliu.util.ToastUtil;
import com.johan.subuwuliu.util.ToolUtil;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HomeModeWindow extends AppActivity implements OnClickListener {

	private static final int HOME_MODE_1 = 1;
	private static final int HOME_MODE_2 = 2;

	private View titleView;
	private TextView title1, title2;

	private Button finish;

	private boolean isHalf = false;

	private HomeModeBackLocationFragment backLocationFragment;
	private HomeModeAppointmentFragment appointmentFragment;

	private ModeBean modeBean;

	private int lastMode = 0;
	private int currentMode = 0;

	private boolean isReady = false;

	@Override
	public void findId() {
		// TODO Auto-generated method stub
		title1 = (TextView) findViewById(R.id.home_mode_title_1);
		title2 = (TextView) findViewById(R.id.home_mode_title_2);
		titleView = findViewById(R.id.home_mode_title_view);
		finish = (Button) findViewById(R.id.home_mode_big_but);
	}

	@Override
	public void init() {
		// //调节Activity的大小
		WindowManager manager = getWindowManager();
		Display display = manager.getDefaultDisplay();
		WindowManager.LayoutParams params = getWindow().getAttributes();
		params.width = display.getWidth();
		params.height = display.getHeight();
		getWindow().setAttributes(params);
		LinearLayout layout = (LinearLayout) findViewById(R.id.home_mode_layout);
		layout.setPadding(0, getStatusBarHeight(), 0, 0);
		finish.setOnClickListener(this);
		// 两种Fragment
		backLocationFragment = new HomeModeBackLocationFragment();
		appointmentFragment = new HomeModeAppointmentFragment();
		// 获取模式内容
		final SharedPreferences sharedPreferences = getSharedPreferences();
		String timestamp = sharedPreferences.getString(
				DiDiApplication.KEY_OF_MODE_TIMESTAMP,
				DiDiApplication.VALUE_NO_TIMESTAMP);
		HashMap<String, String> getModeParams = new HashMap<String, String>();
		getModeParams.put("key", "getmode");
		getModeParams.put("username", sharedPreferences
				.getString(DiDiApplication.KEY_OF_LOGIN_NAME,
						DiDiApplication.VALUE_UNKOWN));
		getModeParams
				.put("pwd", sharedPreferences.getString(
						DiDiApplication.KEY_OF_LOGIN_PWD,
						DiDiApplication.VALUE_UNKOWN));
		getModeParams.put("timestamp", timestamp);
		String action = ToolUtil.encryptionUrlParams(getModeParams);
		WaitingFragment waitingFragment = new WaitingFragment(
				NetUtil.DRIVER_URL + action);
		waitingFragment.load(new OnLoadListener() {
			@Override
			public void onLoadSuccess(String result) {
				System.out.println("获取模式：" + result);
				ModeBean newModeBean = GsonUtil.jsonToObjct(result,
						ModeBean.class);
				if ("0".equals(newModeBean.status)) {
					sharedPreferences
							.edit()
							.putString(DiDiApplication.KEY_OF_MODE_DATA, result)
							.commit();
					sharedPreferences
							.edit()
							.putString(DiDiApplication.KEY_OF_MODE_TIMESTAMP,
									newModeBean.timestamp).commit();
					modeBean = newModeBean;
					initView();
				} else if ("3".equals(newModeBean.status)) {
					initView();
				}
			}

			@Override
			public void onLoadNoNeedUpdate() {
				String oldModeBeanInfo = sharedPreferences.getString(
						DiDiApplication.KEY_OF_MODE_DATA,
						DiDiApplication.VALUE_UNKOWN);
				modeBean = GsonUtil
						.jsonToObjct(oldModeBeanInfo, ModeBean.class);
				System.out.println("不用更新");
				initView();
			}
		});
		getFragmentManager().beginTransaction()
				.add(R.id.home_mode_fragment, waitingFragment).commit();
	}

	private void initView() {
		title1.setOnClickListener(HomeModeWindow.this);
		title2.setOnClickListener(HomeModeWindow.this);
		// 初始化数据
		String backCity = "";
		String backAddress = "";
		String backLongitude = "";
		String backLatitude = "";
		String appointmentTime = "";
		if (modeBean != null) {
			lastMode = currentMode = Integer.parseInt(modeBean.info.mode);
			if (!"".equals(modeBean.info.back_addr)) {
				String[] locationInfo = modeBean.info.back_addr.split("\\|");
				backAddress = locationInfo[0];
				String[] loglat = locationInfo[1].split(",");
				backLongitude = loglat[0];
				backLatitude = loglat[1];
			}
			backCity = modeBean.info.back_city;
			System.out.println("back city : " + backCity);
			appointmentTime = modeBean.info.booking_time;
		}
		if (currentMode == 0) {
			initMode(HOME_MODE_1);
			backLocationFragment.setData(backAddress, backCity, backLongitude,
					backLatitude);
			if (null == this || this.isFinishing()) {
				System.out.println("已经关闭了");
				return;
			} else {
				getFragmentManager().beginTransaction()
						.replace(R.id.home_mode_fragment, backLocationFragment)
						.commit();
			}
			appointmentFragment.setData(appointmentTime);
		} else {
			initMode(HOME_MODE_2);
			appointmentFragment.setData(appointmentTime);
			if (null == this || this.isFinishing()) {
				System.out.println("已经关闭了");
				return;
			} else {
				getFragmentManager().beginTransaction()
						.replace(R.id.home_mode_fragment, appointmentFragment)
						.commit();
			}
			backLocationFragment.setData(backAddress, backCity, backLongitude,
					backLatitude);
		}
		isReady = true;
	}

	@Override
	public int getStatuBarColor() {
		// TODO Auto-generated method stub
		return Color.parseColor("#242736");
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
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.home_mode_title_1:
			updateMode(HOME_MODE_1);
			break;
		case R.id.home_mode_title_2:
			updateMode(HOME_MODE_2);
			break;
		case R.id.home_mode_big_but:
			postModeData();
			break;
		default:
			break;
		}
	}

	private void postModeData() {
		if (!isReady) {
			System.out.println("数据没有准备好，直接关闭");
			finish();
			return;
		}
		final HashMap<String, String> params = new HashMap<String, String>();
		final ModeBean postModeBean = new ModeBean();
		postModeBean.msg = "";
		postModeBean.status = "0";
		postModeBean.timestamp = "0";
		ModeInfoBean postModeInfoBean = new ModeInfoBean();
		postModeInfoBean.driver_id = "0";
		params.put("key", "setmode");
		params.put("username", getLoginName());
		params.put("pwd", getLoginPassword());
		params.put("mode", String.valueOf(currentMode));
		postModeInfoBean.mode = String.valueOf(currentMode);
		System.out.println("pppp--->" + backLocationFragment.getBackAddress());
		// 得到数据
		String backAddress = backLocationFragment.getBackAddress() == null ? ""
				: backLocationFragment.getBackAddress();
		String backCity = backLocationFragment.getBackCity() == null ? ""
				: backLocationFragment.getBackCity();
		String backLongitude = backLocationFragment.getBackLongitude() == null ? ""
				: backLocationFragment.getBackLongitude();
		String backLatitude = backLocationFragment.getBackLatitude() == null ? ""
				: backLocationFragment.getBackLatitude();
		String allAppointmentTime = appointmentFragment.getAppointmentTime() == null ? ""
				: appointmentFragment.getAppointmentTime();
		if (currentMode == 1 && "".equals(allAppointmentTime)) {
			ToastUtil.show(this, "请添加预约时间");
			return;
		}
		if (lastMode == currentMode) {
			if (currentMode == 0
					&& isSameMode1(backAddress, backCity, backLongitude,
							backLatitude)
					|| (currentMode == 1 && isSameMode2(allAppointmentTime))) {
				System.out.println("与上次模式相同，不用提交新的数据");
				finish();
				return;
			}
		}
		System.out.println(backAddress + "|" + backCity);
		if ("".equals(backCity)) {
			params.put("is_backcar", "0");
			params.put("back_city", "");
			params.put("back_addr", "");
			postModeInfoBean.is_backcar = "0";
			postModeInfoBean.back_city = "";
			postModeInfoBean.back_addr = "";
		} else {
			params.put("is_backcar", "1");
			params.put("back_city", backCity);
			postModeInfoBean.is_backcar = "1";
			postModeInfoBean.back_city = backCity;
			if (!"".equals(backAddress) && !"".equals(backLongitude)
					&& !"".equals(backLatitude)) {
				params.put("back_addr", backAddress + "|" + backLongitude + ","
						+ backLatitude);
				postModeInfoBean.back_addr = backAddress + "|" + backLongitude
						+ "," + backLatitude;
			} else {
				params.put("back_addr", "");
				postModeInfoBean.back_addr = "";
			}
		}
		params.put("booking_time", allAppointmentTime);
		postModeInfoBean.booking_time = allAppointmentTime;
		postModeBean.info = postModeInfoBean;
		String action = ToolUtil.encryptionUrlParams(params);
		NetUtil.getInstance().doGet(NetUtil.DRIVER_URL + action,
				new RequestCallBack<String>() {
					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						System.out.println("设置模式结果：" + responseInfo.result);
						StatusBean statusBean = GsonUtil.jsonToObjct(
								responseInfo.result, StatusBean.class);
						if ("0".equals(statusBean.status)) {
							ToastUtil.show(HomeModeWindow.this, "模式设置成功");
							// 保存到sharepreference
							SharedPreferences sharedPreferences = getSharedPreferences(
									DiDiApplication.NAME_OF_SHARED_PREFERENCES,
									Context.MODE_PRIVATE);
							sharedPreferences
									.edit()
									.putString(
											DiDiApplication.KEY_OF_MODE_STATE,
											GsonUtil.objectToJson(postModeBean))
									.commit();
							finish();
						} else {
							ToastUtil.show(HomeModeWindow.this, statusBean.msg);
							finish();
						}
					}

					@Override
					public void onFailure(HttpException exception, String info) {
						ToastUtil.showNetError(HomeModeWindow.this);
						///finish();
					}
				});
	}

	private boolean isSameMode1(String backAddress, String backCity,
			String backLongitude, String backLatitude) {
		return modeBean.info.back_addr.equals(backAddress + "|" + backLongitude
				+ "," + backLatitude)
				&& modeBean.info.back_city.equals(backCity);
	}

	private boolean isSameMode2(String allAppointmentTime) {
		return modeBean.info.booking_time.equals(allAppointmentTime);
	}

	private void updateMode(int mode) {
		int titleViewWidth = titleView.getWidth();
		if (mode == HOME_MODE_1) {
			currentMode = 0;
			isHalf = false;
			final ObjectAnimator animator = ObjectAnimator.ofFloat(titleView,
					"translationX", titleViewWidth, 0).setDuration(300);
			animator.addUpdateListener(new AnimatorUpdateListener() {
				@Override
				public void onAnimationUpdate(ValueAnimator animation) {
					// TODO Auto-generated method stub
					if (animation.getCurrentPlayTime() > 150 && !isHalf) {
						isHalf = true;
						title1.setEnabled(false);
						title2.setEnabled(true);
					}
				}
			});
			animator.start();
			getFragmentManager().beginTransaction()
					.replace(R.id.home_mode_fragment, backLocationFragment)
					.commit();
		} else {
			currentMode = 1;
			isHalf = false;
			ObjectAnimator animator = ObjectAnimator.ofFloat(titleView,
					"translationX", 0, titleViewWidth).setDuration(300);
			animator.addUpdateListener(new AnimatorUpdateListener() {
				@Override
				public void onAnimationUpdate(ValueAnimator animation) {
					// TODO Auto-generated method stub
					if (animation.getCurrentPlayTime() > 150 && !isHalf) {
						isHalf = true;
						title1.setEnabled(true);
						title2.setEnabled(false);
					}
				}
			});
			animator.start();
			getFragmentManager().beginTransaction()
					.replace(R.id.home_mode_fragment, appointmentFragment)
					.commit();
		}
	}

	private void initMode(int mode) {
		if (mode == HOME_MODE_1) {
			title1.setEnabled(false);
			title2.setEnabled(true);
		} else {
			ObjectAnimator animator = ObjectAnimator.ofFloat(titleView,
					"translationX", 0, titleView.getWidth()).setDuration(10);
			title1.setEnabled(true);
			title2.setEnabled(false);
			animator.start();
		}
	}

	@Override
	public int getContentView() {
		// TODO Auto-generated method stub
		return R.layout.window_home_mode;
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		overridePendingTransition(0, R.anim.anim_home_mode_out);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (requestCode == HomeModeBackLocationFragment.REQUEST_LOCATION_CODE
				&& resultCode == Activity.RESULT_OK) {
			String backCity = data.getStringExtra("backlocation_city");
			String backName = data.getStringExtra("backlocation_name");
			String backAddress = data.getStringExtra("backlocation_address");
			String backLongitude = data
					.getStringExtra("backlocation_longitude");
			String backLatitude = data.getStringExtra("backlocation_latitude");
			updateBackLocationFragment(backCity, backAddress, backLongitude,
					backLatitude, 1);
			// 数据库保存搜索记录
			CityBean cityBean = new CityBean(backName,
					"".equals(backAddress) ? backName : backAddress, backCity,
					backLongitude, backLatitude);
			CityHistoryDatabase database = new CityHistoryDatabase(this);
			database.push(GsonUtil.objectToJson(cityBean));
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void updateBackLocationFragment(String backCity,
			String backAddress, String backLongitude, String backLatitude,
			int arg1) {
		Handler uiHandler = backLocationFragment.getUiHandler();
		Message message = uiHandler.obtainMessage();
		message.arg1 = arg1;
		Bundle messageData = new Bundle();
		messageData.putString("back_city", backCity);
		messageData.putString("back_address", backAddress);
		messageData.putString("back_longitude", backLongitude);
		messageData.putString("back_latitude", backLatitude);
		message.setData(messageData);
		uiHandler.sendMessage(message);
	}

	public View getParentView() {
		return findViewById(R.id.home_mode_layout);
	}

}
