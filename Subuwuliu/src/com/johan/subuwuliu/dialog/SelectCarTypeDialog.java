package com.johan.subuwuliu.dialog;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.johan.subuwuliu.bean.CarTypeBean;
import com.johan.subuwuliu.bean.CarTypeBean.CarTypeDetailBean;
import com.johan.subuwuliu.util.GsonUtil;
import com.lidroid.xutils.http.ResponseInfo;

public class SelectCarTypeDialog extends SelectWheelViewDialog {
	
	private List<CarTypeBean> carTypeList;

	public SelectCarTypeDialog(Context context, String title, String url) {
		super(context, title, url);
		// TODO Auto-generated constructor stub
		carTypeList = new ArrayList<CarTypeBean>();
	}

	@Override
	public List<String> parseData(ResponseInfo<String> responseInfo) {
		// TODO Auto-generated method stub
		List<String> carTypeData = new ArrayList<String>();
		String resuteStatus = GsonUtil.getJsonValue(responseInfo.result, "status");
		if("0".equals(resuteStatus)) {
			String resultInfo = GsonUtil.getJsonValue(responseInfo.result, "list");
			Type listType = new TypeToken<LinkedList<CarTypeBean>>(){}.getType();  
			Gson gson = new Gson();  
			LinkedList<CarTypeBean> cartypes = gson.fromJson(resultInfo, listType);  
			for (Iterator<CarTypeBean> iterator = cartypes.iterator(); iterator.hasNext();) {  
				CarTypeBean object = (CarTypeBean) iterator.next();
				carTypeList.add(object);
				carTypeData.add(object.title);
			}  
		}
		return carTypeData;
	}
	
	public List<CarTypeDetailBean> getCarTypeDetailList(String carType) {
		for(CarTypeBean currentCarType : carTypeList) {
			if(carType.equals(currentCarType.title)) {
				return currentCarType.sub_title;
			}
		}
		return null;
	}

}
