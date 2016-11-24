package com.johan.subuwuliu.application;

import android.app.Application;
import android.content.Context;
import cn.jpush.android.api.JPushInterface;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.SDKInitializer;
import com.easemob.EMCallBack;
import com.easemob.chatuidemo.DemoHXSDKHelper;
import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.MobclickAgent.EScenarioType;
import com.umeng.analytics.MobclickAgent.UMAnalyticsConfig;

public class DiDiApplication extends Application {

	public static final String PROJECT_DIR = "subuwuliu";
	// 登录用户名和密码
	public static final String KEY_OF_LOGIN_NAME = "subuwuliu_login_name";
	public static final String KEY_OF_LOGIN_PWD = "subuwuliu_login_password";
	// 司机信息
	public static final String KEY_OF_DRIVER_INFO_DATA = "subuwuliu_driver_info";
	public static final String KEY_OF_DRIVER_INFO_TIMESTAMP = "subuwuliu_driver_info_timestamp";
	// 预约信息
	public static final String KEY_OF_APPOINTMENT_DATA = "subuwuliu_appointment_data";
	public static final String KEY_OF_APPOINTMENT_TIMESTAMP = "subuwuliu_appointment_timestamp";
	// 行驶记录
	public static final String KEY_OF_DRIVERRECORD_DATA = "subuwuliu_driverrecord_data";
	public static final String KEY_OF_DRIVERRECORD_TIMESTAMP = "subuwuliu_driverrecord_timestamp";
	// 正在运行的订单
	public static final String KEY_OF_DRIVERORDER_DATA = "subuwuliu_driverorder_data";
	public static final String KEY_OF_DRIVERORDER_TIMESTAMP = "subuwuliu_driverorder_timestamp";
	// 模式
	public static final String KEY_OF_MODE_DATA = "subuwuliu_mode_data";
	public static final String KEY_OF_MODE_TIMESTAMP = "subuwuliu_mode_timestamp";

	// 司机状态
	public static final String KEY_OF_DRIVER_STATE = "subuwuliu_driver_state";
	public static final String KEY_OF_NEED_UPDATE_DRIVER_STATE = "subuwuliu_need_update_driver_state";
	// 模式状态
	public static final String KEY_OF_MODE_STATE = "subuwuliu_mode_state";

	// 最新版本
	public static final String KEY_OF_NEW_VERSION = "subuwuliu_new_version";

	// SharedPreference名字
	public static final String NAME_OF_SHARED_PREFERENCES = "subuwuliu_shared_preferences";

	public static final String VALUE_UNKOWN = "unkown";
	public static final String VALUE_NO_TIMESTAMP = "0";

	public static Context applicationContext;
	private static DiDiApplication instance;
	// login user name
	public final String PREF_USERNAME = "username";

	public static String currentUserNick = "";
	public static DemoHXSDKHelper hxSDKHelper = new DemoHXSDKHelper();

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate(); 
		JPushInterface.init(this);
		System.out.println("初始化所有jar包"); 
		applicationContext = this;
		instance = this;
		SDKInitializer.initialize(applicationContext);
		hxSDKHelper.onInit(applicationContext); 
		MobclickAgent.startWithConfigure(new UMAnalyticsConfig(this, "56f38b3467e58e5a0d001da1", "Umeng", EScenarioType.E_UM_NORMAL, true));
		 CrashReport.initCrashReport(getApplicationContext(), "9ee43f514c", false);
	}

	public static DiDiApplication getInstance() {
		return instance;
	}

	public String getUserName() {
		return hxSDKHelper.getHXId();
	}

	public String getPassword() {
		return hxSDKHelper.getPassword();
	}

	public void setUserName(String username) {
		hxSDKHelper.setHXId(username);
	}

	public void setPassword(String pwd) {
		hxSDKHelper.setPassword(pwd);
	}

	public void logout(final boolean isGCM, final EMCallBack emCallBack) {
		hxSDKHelper.logout(isGCM, emCallBack);
	}

}
