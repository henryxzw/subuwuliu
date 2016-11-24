package com.johan.subuwuliu.util;
 
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager; 
import android.content.Context;
import android.content.Intent; 
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle; 
import android.util.Base64;
import android.view.View; 
import android.widget.Button; 
import android.widget.TextView; 

import com.google.gson.Gson;
import com.johan.subuwuliu.R;

public class ToolUtil {
	private Dialog goSettingGPSDialog;

	/**
	 * 计算地球上任意两点(经纬度)距离
	 * 
	 * @param long1
	 *            第一点经度
	 * @param lat1
	 *            第一点纬度
	 * @param long2
	 *            第二点经度
	 * @param lat2
	 *            第二点纬度
	 * @return 返回距离 单位：米
	 */
	public static double distance(double long1, double lat1, double long2,
			double lat2) {
		double a, b, R;
		R = 6378137; // 地球半径
		lat1 = lat1 * Math.PI / 180.0;
		lat2 = lat2 * Math.PI / 180.0;
		a = lat1 - lat2;
		b = (long1 - long2) * Math.PI / 180.0;
		double d;
		double sa2, sb2;
		sa2 = Math.sin(a / 2.0);
		sb2 = Math.sin(b / 2.0);
		d = 2
				* R
				* Math.asin(Math.sqrt(sa2 * sa2 + Math.cos(lat1)
						* Math.cos(lat2) * sb2 * sb2));
		return d;
	}

	/**
	 * 把Map加密action
	 * 
	 * @param params
	 * @return
	 */
	public static String encryptionUrlParams(Map params) {
		String json = new Gson().toJson(params);
		System.out.println("------json--------->>" + json);
		String encodeToString = Base64.encodeToString(json.getBytes(),
				Base64.NO_WRAP);
		String encryptDES = null;
		try {
			encryptDES = DES.encrypt(encodeToString);
			String md5 = MD5.code(encryptDES, 16);
			encryptDES = encryptDES + md5;
			return encryptDES;
		} catch (Exception e) {
			System.out.println("加密错误");
		}
		return null;
	}

	/**
	 * 判断手机号码格式是否合法
	 * 
	 * @param mobiles
	 * @return
	 */
	public static boolean isMobileNumber(String mobiles) {
		Pattern p = Pattern
				.compile("^((1[0-9][0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}

	/**
	 * 判断邮箱是否合法
	 * 
	 * @param email
	 * @return
	 */
	public static boolean isEmail(String email) {
		if (null == email || "".equals(email))
			return false;
		Pattern p = Pattern
				.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");// 复杂匹配
		Matcher m = p.matcher(email);
		return m.matches();
	}

	/**
	 * 拨号
	 * 
	 * @param context
	 * @param phone
	 */
	public static void call(Context context, String phone) {
		Uri uri = Uri.parse("tel:" + phone);
		Intent intent = new Intent(Intent.ACTION_DIAL, uri);
		context.startActivity(intent);
	}

	public static void goActivity(Context conetxt, Class<?> targetActivity) {
		Intent intent = new Intent(conetxt, targetActivity);
		conetxt.startActivity(intent);
	}

	public static void goActivity(Context conetxt, Class<?> targetActivity,
			Bundle data) {
		Intent intent = new Intent(conetxt, targetActivity);
		intent.putExtras(data);
		conetxt.startActivity(intent);
	}

	// ////////////////判断当前是否链接网络
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm == null) {
		} else {
			// 　　　 如果仅仅是用来判断网络连接　
			// 则可以使用 cm.getActiveNetworkInfo().isAvailable();
			NetworkInfo[] info = cm.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {

						return true;
					}
				}
			}
		}
		return false;
	}

	public static Dialog logoutDialog;

	public static void dilog(Context context, String msg) {
		if (logoutDialog == null) {
			logoutDialog = new Dialog(context, R.style.DiDiDialog);
			logoutDialog.setContentView(R.layout.dialog_isnetworkavailable);
			final TextView text = (TextView) logoutDialog
					.findViewById(R.id.isnet_wanglu);
			final Button button = (Button) logoutDialog
					.findViewById(R.id.isnet_but);
			text.setText(msg);
			button.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					logoutDialog.dismiss();
				}
			});
			logoutDialog.setCancelable(false);
		}
		logoutDialog.show();

	}

	public static void dilogA(final Activity context, String msg) {
		final Dialog logoutDialog = new Dialog(context, R.style.DiDiDialog);
		logoutDialog.setContentView(R.layout.dialog_isnetworkavailable);
		final TextView text = (TextView) logoutDialog
				.findViewById(R.id.isnet_wanglu);
		final Button button = (Button) logoutDialog
				.findViewById(R.id.isnet_but);
		text.setText(msg);
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				logoutDialog.dismiss();
				context.finish();
			}
		});
		logoutDialog.show();

	}

	// ///////////计算两个时间段多长时间
	@SuppressLint("SimpleDateFormat")
	public static Double settime(String starttime, String stoptime) {
		String str1 = starttime; // "yyyyMMdd"格式 如 20131022
		System.out.println("\n结束时间:");
		String str2 = stoptime; // "yyyyMMdd"格式 如 20131022
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");// 输入日期的格式
		Date date1 = null;
		try {
			date1 = simpleDateFormat.parse(str1);
		} catch (java.text.ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Date date2 = null;
		try {
			date2 = simpleDateFormat.parse(str2);

		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		GregorianCalendar cal1 = new GregorianCalendar();
		GregorianCalendar cal2 = new GregorianCalendar();
		cal1.setTime(date1);
		cal2.setTime(date2);
		double dayCount = (cal2.getTimeInMillis() - cal1.getTimeInMillis())
				/ (1000 * 3600 * 24);// 从间隔毫秒变成间隔天数
		System.out.println("\n相差" + dayCount + "天");

		return dayCount;
	}

	public static void lingsheng(Context context) {
		// ///////////////设置推送消息的铃声/////////////////////
		NotificationManager manger = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		Notification notification = new Notification();

		// 自定义声音 声音文件放在ram目录下，没有此目录自己创建一个
		notification.sound = Uri.parse("android.resource://"
				+ context.getPackageName() + "/" + R.raw.aa);
		// 使用系统默认声音用下面这条
		// / notification.defaults=Notification.DEFAULT_SOUND;
		manger.notify(2, notification);
	}

	// ///////////////////////


}
