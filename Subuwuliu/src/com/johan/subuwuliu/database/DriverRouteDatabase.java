package com.johan.subuwuliu.database;

import com.johan.subuwuliu.bean.DriverRouteBean;
import com.johan.subuwuliu.util.GsonUtil;

import android.content.ContentValues;
import android.content.Context;

public class DriverRouteDatabase extends DatabaseHelper<DriverRouteBean> {

	public DriverRouteDatabase(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return DRIVER_ROUTE_TABLE_NAME;
	}

	@Override
	public ContentValues insertContentValues(DriverRouteBean object, String json) {
		// TODO Auto-generated method stub
		ContentValues contentValues = new ContentValues();
		contentValues.put(KEY_OF_DRIVER_ROUTE_YID, object.info.yid);
		contentValues.put(KEY_OF_DRIVER_ROUTE_TIMESTAMP, object.timestamp);
		contentValues.put(KEY_OF_DRIVER_ROUTE_CONTENT, json);
		return contentValues;
	}
	
	@Override
	public String defineSameCondition(DriverRouteBean object) {
		// TODO Auto-generated method stub
		return KEY_OF_DRIVER_ROUTE_YID + "='" + object.info.yid + "'";
	}

	@Override
	public DriverRouteBean parseObject(String json) {
		// TODO Auto-generated method stub
		return GsonUtil.jsonToObjct(json, DriverRouteBean.class);
	}
	
	public DriverRouteBean pull(String yid) {
		return pull(KEY_OF_DRIVER_ROUTE_YID, yid, KEY_OF_DRIVER_ROUTE_CONTENT);
	}

}
