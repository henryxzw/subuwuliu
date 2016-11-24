package com.johan.subuwuliu.handler;

import java.util.HashMap;

import com.johan.subuwuliu.application.DiDiApplication;
import com.johan.subuwuliu.bean.MyAppointmentBean;
import com.johan.subuwuliu.bean.MyAppointmentBean.MyAppointmentDetailBean;
import com.johan.subuwuliu.dialog.RecentOrderNotificationWindow;
import com.johan.subuwuliu.util.FormatUtil;
import com.johan.subuwuliu.util.GsonUtil;
import com.johan.subuwuliu.util.NetUtil;
import com.johan.subuwuliu.util.ToolUtil;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Handler;

public class CheckOrderRecentHandler extends AbstractCommandHandler {

	private boolean isExcetued = false;
	private boolean isChecked = false;
	
	private Handler handler = new Handler();
	
	public CheckOrderRecentHandler(Context context) {
		super(context);
	}

	@Override
	public void doCommand(Intent intent) {
		//如果已经再执行了，就不用再次执行
		if(isExcetued) return;
		//开始检测订单情况
		isExcetued = true;
		handler.post(doTask);
	}
	
	private Runnable doTask = new Runnable() {
		@Override
		public void run() {
			//入股检测到了，就不在检测
			if(isChecked) return;
			String timestamp = sharedPreferences.getString(DiDiApplication.KEY_OF_APPOINTMENT_TIMESTAMP, "0");
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("key", "booking_list");
			params.put("username", loginName);
			params.put("pwd", loginPassword);
			params.put("pageindex", "1");
			params.put("pagesize", "50");
			params.put("keyword", "");
			String action = ToolUtil.encryptionUrlParams(params);
			NetUtil.getInstance().doGet(NetUtil.DRIVER_URL + action, new RequestCallBack<String>() {
				@Override
				public void onSuccess(ResponseInfo<String> responseInfo) {
					System.out.println("获取预约订单结果：" + responseInfo.result);
					MyAppointmentBean myAppointmentResult = GsonUtil.jsonToObjct(responseInfo.result, MyAppointmentBean.class);
					if("0".equals(myAppointmentResult.status)) {
						//保存
						Editor editor = sharedPreferences.edit();
						editor.putString(DiDiApplication.KEY_OF_APPOINTMENT_TIMESTAMP, myAppointmentResult.timestamp);
						editor.putString(DiDiApplication.KEY_OF_APPOINTMENT_DATA, responseInfo.result);
						editor.commit();
						checkHasRecentOrder(myAppointmentResult);
						//30分钟后再次检测
						handler.postDelayed(doTask, 30 * 60 * 1000);
					} else if("200".equals(myAppointmentResult.status)) {
						String oldResponseInfo = sharedPreferences.getString(DiDiApplication.KEY_OF_APPOINTMENT_DATA, DiDiApplication.VALUE_UNKOWN);
						MyAppointmentBean oldAppointmentBean = GsonUtil.jsonToObjct(oldResponseInfo, MyAppointmentBean.class);
						checkHasRecentOrder(oldAppointmentBean);
						//30分钟后再次检测
						handler.postDelayed(doTask, 30 * 60 * 1000);
					} else {
//						ToastUtil.show(context, "抱歉，由于网络不佳，无法预测是否有订单即将执行，请自行查看预约订单，以免忘记执行订单");
						//30分钟后再次检测
						handler.postDelayed(doTask, 30 * 60 * 1000);
					}
				}
				@Override
				public void onFailure(HttpException arg0, String arg1) {
//					ToastUtil.show(context, "抱歉，由于网络不佳，无法预测是否有订单即将执行，请自行查看预约订单，以免忘记执行订单");
					//30分钟后再次检测
					handler.postDelayed(doTask, 30 * 60 * 1000);
				}
			});
		}
	};
	
	private void checkHasRecentOrder(MyAppointmentBean appointmentBean) {
		for(MyAppointmentDetailBean detailBean : appointmentBean.list) {
			long appointmentOrderTime = FormatUtil.getTime(detailBean.use_time);
			long currentTime = System.currentTimeMillis();
			//如果大于现在的时间，小于两小时2 * 3600 * 1000
			if(appointmentOrderTime > currentTime && (appointmentOrderTime - currentTime) < 2 * 3600 * 1000) {
				Intent goIntent = new Intent(context, RecentOrderNotificationWindow.class);
				goIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				String hour = FormatUtil.formatTime(appointmentOrderTime, "HH:mm");
				int remainHour = (int)((appointmentOrderTime - currentTime) / (3600 * 1000));
				int remainMin = (int)((appointmentOrderTime - currentTime - 3600 * 1000 * remainHour) / (60 * 1000));
				String remainTime = remainHour + "小时" + remainMin + "分钟";
				String address = detailBean.address_start;
				goIntent.putExtra("hour", hour);
				goIntent.putExtra("remain_time", remainTime);
				goIntent.putExtra("address", address);
				context.startActivity(goIntent);
				isChecked = true;
			}
		}
	}

}
