package com.johan.subuwuliu.database;

import com.johan.subuwuliu.bean.OrderBean;
import com.johan.subuwuliu.bean.OrderBean.OrderDetailBean;
import com.johan.subuwuliu.util.GsonUtil;

import android.content.ContentValues;
import android.content.Context;

public class OrderDetailDatabase extends DatabaseHelper<OrderBean> {
	
	public OrderDetailDatabase(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return ORDER_DETAIL_TABLE_NAME;
	}
	
	@Override
	public ContentValues insertContentValues(OrderBean object, String json) {
		// TODO Auto-generated method stub
		OrderDetailBean orderDetail = object.order;
		ContentValues contentValues = new ContentValues();
		contentValues.put(KEY_OF_ORDER_DETAIL_NUMBER, orderDetail.order_no);
		contentValues.put(KEY_OF_ORDER_DETAIL_TIMESTAMP, orderDetail.timestamp);
		contentValues.put(KEY_OF_ORDER_DETAIL_CONTENT, json);
		return contentValues;
	}

	@Override
	public String defineSameCondition(OrderBean object) {
		// TODO Auto-generated method stub
		return KEY_OF_ORDER_DETAIL_NUMBER + "='" + object.order.order_no + "'";
	}
	
	public OrderDetailBean pull(String orderNumber) {
		OrderBean order = pull(KEY_OF_ORDER_DETAIL_NUMBER, orderNumber, KEY_OF_ORDER_DETAIL_CONTENT);
		if(order != null) {
			return order.order;
		}
		return null;
	}

	@Override
	public OrderBean parseObject(String json) {
		// TODO Auto-generated method stub
		return GsonUtil.jsonToObjct(json, OrderBean.class);
	}
	
}
