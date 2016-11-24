package com.johan.subuwuliu.util;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.johan.subuwuliu.bean.DriverDetailBean.DriverDetailInfoBean;
import com.johan.subuwuliu.bean.OrderBean.OrderDetailBean;

public class FormatUtil {

	public static long getTime(String time) {
		return getDate(time).getTime();
	}

	public static Date getDate(String time) {
		try {
			String formater = "yyyy-MM-dd HH:mm:ss";
			SimpleDateFormat format = new SimpleDateFormat(formater);
			return format.parse(time);
		} catch (Exception exception) {
			return new Date(System.currentTimeMillis());
		}
	}

	public static String formatTime(long time, String pattern) {
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		Date date = new Date(time);
		return format.format(date);
	}

	public static String formatTime(String time, String pattern) {
		long millisecode = getTime(time);
		return formatTime(millisecode, pattern);
	}

	public static String formatPlace(String place) {
		String[] placeArr = place.split("\\|");
		if (null == placeArr || placeArr.length != 3) {
			return "地址格式不对";
		}
		return placeArr[0];
	}

	public static String formatCarType(String carType) {
		carType = carType.replace("T", "吨");
		String[] carTypeArr = carType.split(",");
		if (null == carTypeArr || carTypeArr.length < 2) {
			return "车型格式不对";
		}
		return carTypeArr[0] + carTypeArr[1];
	}

	public static String formatExtraInfo(OrderDetailBean orderDetail) {
		StringBuffer extraInfomation = new StringBuffer();
		if ("1".equals(orderDetail.is_up_goods)) {
			extraInfomation.append("上货 ");
		}
		if ("1".equals(orderDetail.is_down_goods)) {
			extraInfomation.append("下货 ");
		}
		if ("1".equals(orderDetail.is_supercargo)) {
			String carGoNumber = orderDetail.supercargo_number;
			extraInfomation.append("跟车" + carGoNumber + "人 ");
		}
		String foolNumber = orderDetail.floor_number;
		if (foolNumber.equals("0")) {
			extraInfomation.append("");
		} else {
			extraInfomation.append("上" + foolNumber + "楼");
		}

		if ("1".equals(orderDetail.is_lift)) {
			extraInfomation.append("有电梯");
		} else {
			extraInfomation.append("");
		}
		return extraInfomation.toString();
	}

	public static String formatExtraInfo(DriverDetailInfoBean orderDetail) {
		StringBuffer extraInfomation = new StringBuffer();
		if ("1".equals(orderDetail.is_up_goods)) {
			extraInfomation.append("上货 ");
		}
		if ("1".equals(orderDetail.is_down_goods)) {
			extraInfomation.append("下货 ");
		}
		if ("1".equals(orderDetail.is_supercargo)) {
			String carGoNumber = orderDetail.supercargo_number;
			extraInfomation.append("跟车" + carGoNumber + "人 ");
		}
		String foolNumber = orderDetail.floor_number;

		if (!"0".equals(foolNumber)) {
			extraInfomation.append("上" + foolNumber + "楼");
		}
		if ("1".equals(orderDetail.is_lift)) {
			extraInfomation.append("有电梯");
		}  
		return extraInfomation.toString();
	}

	public static List<String> formatPicture(String picture) {
		String[] pictureArr = picture.split("\\|");
		return Arrays.asList(pictureArr);
	}

	public static String formatContact(String contact) {
		return contact.replace("\\|", " ");
	}

}
