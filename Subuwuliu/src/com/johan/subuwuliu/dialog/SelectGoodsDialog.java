package com.johan.subuwuliu.dialog;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.johan.subuwuliu.bean.GoodsBean;
import com.johan.subuwuliu.util.GsonUtil;
import com.lidroid.xutils.http.ResponseInfo;

import android.content.Context;

public class SelectGoodsDialog extends SelectWheelViewDialog {
	
	private List<GoodsBean> goodsList;

	public SelectGoodsDialog(Context context, String title, String url) {
		super(context, title, url);
		// TODO Auto-generated constructor stub
		goodsList = new ArrayList<GoodsBean>();
	}

	@Override
	public List<String> parseData(ResponseInfo<String> responseInfo) {
		// TODO Auto-generated method stub
		List<String> goodData = new ArrayList<String>();
		String resuteStatus = GsonUtil.getJsonValue(responseInfo.result, "status");
		if("0".equals(resuteStatus)) {
			String resultInfo = GsonUtil.getJsonValue(responseInfo.result, "list");
			Type listType = new TypeToken<LinkedList<GoodsBean>>(){}.getType();  
			Gson gson = new Gson();  
			LinkedList<GoodsBean> goods = gson.fromJson(resultInfo, listType);  
			for (Iterator<GoodsBean> iterator = goods.iterator(); iterator.hasNext();) {  
				GoodsBean object = (GoodsBean) iterator.next();
				goodsList.add(object);
				goodData.add(object.title);
			}  
		}
		return goodData;
	}
	
	public String getGoodsIdByTitle(String title) {
		for(GoodsBean good : goodsList) {
			if(good.title.equals(title)) {
				return good.id;
			}
		}
		return null;
	}

}
