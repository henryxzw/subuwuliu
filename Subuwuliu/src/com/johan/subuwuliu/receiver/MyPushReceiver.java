package com.johan.subuwuliu.receiver;

import java.util.HashMap;

import com.johan.subuwuliu.HomeActivity;
import com.johan.subuwuliu.SplashActivity;
import com.johan.subuwuliu.application.DiDiApplication;
import com.johan.subuwuliu.bean.StatusBean;
import com.johan.subuwuliu.bean.TiXingBean;
import com.johan.subuwuliu.bean.TuiSongBean;
import com.johan.subuwuliu.database.TiXingDatabase;
import com.johan.subuwuliu.dialog.CancelOrderWindow;
import com.johan.subuwuliu.dialog.CancelOrderWindow2;
import com.johan.subuwuliu.dialog.LoadingNotificationWindow;
import com.johan.subuwuliu.dialog.ReceiverOrderWindow;
import com.johan.subuwuliu.method.MethodConfig;
import com.johan.subuwuliu.server.BackgroundServer;
import com.johan.subuwuliu.server.ForegroundServer;
import com.johan.subuwuliu.util.FormatUtil;
import com.johan.subuwuliu.util.GsonUtil;
import com.johan.subuwuliu.util.NetUtil;
import com.johan.subuwuliu.util.ToolUtil;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import cn.jpush.android.api.JPushInterface;

public class MyPushReceiver extends BroadcastReceiver {

	public static final String ACTION_RECEIVER_TIXING = "action_receiver_tixing";
	public static final String ACTION_ORDER_COUNT = "action_order_count";
	public static final String ACTION_CANCEL_ORDER = "action_cancel_order";
	private static final String ACTION_SHUTDOWN = "android.intent.action.ACTION_SHUTDOWN";
	private static String yid = "";

	@Override
	public void onReceive(final Context context, Intent intent) {
		// TODO Auto-generated method stub
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				DiDiApplication.NAME_OF_SHARED_PREFERENCES,
				Context.MODE_PRIVATE);
		String loginName = sharedPreferences
				.getString(DiDiApplication.KEY_OF_LOGIN_NAME,
						DiDiApplication.VALUE_UNKOWN);
		String loginPassword = sharedPreferences.getString(
				DiDiApplication.KEY_OF_LOGIN_PWD, DiDiApplication.VALUE_UNKOWN);
		if (DiDiApplication.VALUE_UNKOWN.equals(loginName)
				|| DiDiApplication.VALUE_UNKOWN.equals(loginPassword)) {
			System.out.println("没有账户信息，接收到推送的内容不能执行");
			return;
		}

		// ///系统关闭时
		if (intent.getAction().equals(ACTION_SHUTDOWN)) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("key", "downline");
			params.put("username", loginName);
			params.put("pwd", loginPassword);
			String action = ToolUtil.encryptionUrlParams(params);
			NetUtil.getInstance().doGet(NetUtil.DRIVER_URL + action,
					new RequestCallBack<String>() {
						@Override
						public void onFailure(HttpException exception,
								String info) {
						}

						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
							// TODO Auto-generated method stub
							// // 启动前程服务
							Intent intent = new Intent(context,
									ForegroundServer.class);
							intent.putExtra(
									ForegroundServer.FOREGROUND_COMMAND_NAME,
									ForegroundServer.COMMAND_OPERATION_FOREGROUND);
							intent.putExtra("server_state", false);
							context.startService(intent);
						}
					});

		}
		// /////////////////////////////////////////////
		Bundle bundle = intent.getExtras();
		if (bundle == null) {
			return;
		}
		String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
		System.out.println("extras--->" + extras);
		final TuiSongBean tuiSongBean = GsonUtil.jsonToObjct(extras,
				TuiSongBean.class);
		if (tuiSongBean == null)
			return;
		if ("send_new_order".equals(tuiSongBean.type)) {

			if (yid != tuiSongBean.order_no) {
				yid = tuiSongBean.order_no;
				ToolUtil.lingsheng(context);

				// 接收订单

				HashMap<String, String> confirmParams = new HashMap<String, String>();
				confirmParams.put("key", "confirm_order");
				confirmParams.put("order_no", yid);
				confirmParams.put("username", loginName);
				confirmParams.put("pwd", loginPassword);
				System.out.println("确认订单：" + yid);
				String confirmAction = ToolUtil
						.encryptionUrlParams(confirmParams);

				NetUtil.getInstance().doGet(NetUtil.DRIVER_URL + confirmAction,
						new RequestCallBack<String>() {
							@Override
							public void onFailure(HttpException exception,
									String info) {
								MethodConfig.ShowToast("网络问题，不能确认订单");
							}

							@Override
							public void onSuccess(
									ResponseInfo<String> responseInfo) {
								StatusBean statusBean = GsonUtil.jsonToObjct(
										responseInfo.result, StatusBean.class);
								if ("0".equals(statusBean.status)) {
									Intent goIntent = new Intent(context,
											ReceiverOrderWindow.class);
									goIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
									goIntent.putExtra("yid", yid);
									context.startActivity(goIntent);
								} else {
									MethodConfig.ShowToast(statusBean.msg
											+ "问题，不能确认订单");
								}
							}
						});

			}
		} else if ("cancel_waybill_nopay".equals(tuiSongBean.type)
				|| "cancel_waybill_nodelivery".equals(tuiSongBean.type)) {
			// 取消订单
			ToolUtil.lingsheng(context);
			String yid = tuiSongBean.yid;
			Intent goIntent = new Intent(context, CancelOrderWindow.class);
			goIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			goIntent.putExtra("yid", yid);
			context.startActivity(goIntent);
			// 发送广播
			Intent sendIntent = new Intent(ACTION_CANCEL_ORDER);
			sendIntent.putExtra("yid", yid);
			context.sendBroadcast(sendIntent);
			// 发送广播，获取信息预约数量
			Intent sendIntents = new Intent(ACTION_ORDER_COUNT);
			context.sendBroadcast(sendIntents);
		} else if ("pay_success".equals(tuiSongBean.type)) {
			// ///铃声提醒
			ToolUtil.lingsheng(context);
			// 装货通知
			Intent goIntent = new Intent(context,
					LoadingNotificationWindow.class);
			goIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			goIntent.putExtra("data_bean", tuiSongBean);
			context.startActivity(goIntent);
			// 发送广播，获取信息预约数量
			Intent sendIntent = new Intent(ACTION_ORDER_COUNT);
			context.sendBroadcast(sendIntent);
		} else if ("cancel_waybill".equals(tuiSongBean.type)) {
			try {
				HomeActivity.stopTrace();
			} catch (Exception e) {
				// TODO: handle exception
			}
			// 取消订单（已付款）
			ToolUtil.lingsheng(context);
			Intent goIntent = new Intent(context, CancelOrderWindow2.class);
			goIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			goIntent.putExtra("data_bean", tuiSongBean);
			context.startActivity(goIntent);
			// 发送广播
			Intent sendIntent = new Intent(ACTION_CANCEL_ORDER);
			sendIntent.putExtra("yid", tuiSongBean.yid);
			context.sendBroadcast(sendIntent);
			// 发送广播，获取信息预约数量
			Intent sendIntents = new Intent(ACTION_ORDER_COUNT);
			context.sendBroadcast(sendIntents);
		} else if ("check_driver_state".equals(tuiSongBean.type)) {
			// 校验司机状态
			Intent goIntent = new Intent(context, BackgroundServer.class);
			goIntent.putExtra(BackgroundServer.BACKGROUND_COMMAND_NAME,
					BackgroundServer.COMMAND_UPLOAD_DRIVER_INFO);
			context.startService(goIntent);
		} else if ("push_tips".equals(tuiSongBean.type)) {
			// 接收到提醒消息，保存到数据库
			String content = tuiSongBean.tips_text;
			System.out.println("content---->" + content);
			String time = FormatUtil.formatTime(System.currentTimeMillis(),
					"yyyy年MM月dd日  HH:mm EEEE");
			TiXingBean tixingBean = new TiXingBean(content, time);
			TiXingDatabase database = new TiXingDatabase(context);
			database.push(GsonUtil.objectToJson(tixingBean));
			// 发送广播
			Intent goIntent = new Intent(ACTION_RECEIVER_TIXING);
			goIntent.putExtra("content", content);
			goIntent.putExtra("time", time);
			context.sendBroadcast(goIntent);
		} else if ("user_online".equals(tuiSongBean.type)) {
			HashMap<String, String> confirmParams = new HashMap<String, String>();
			confirmParams.put("key", "user_online");
			confirmParams.put("username", loginName);
			String confirmAction = ToolUtil.encryptionUrlParams(confirmParams);
			NetUtil.getInstance().doGet(NetUtil.DRIVER_URL + confirmAction,
					new RequestCallBack<String>() {
						@Override
						public void onFailure(HttpException exception,
								String info) {
							System.out.println("网络问题，不能确认上线状态");
						}

						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
							System.out.println("上线状态提交成功");
						}
					});

		}
	}

}
