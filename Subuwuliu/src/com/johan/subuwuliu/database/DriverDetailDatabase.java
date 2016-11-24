package com.johan.subuwuliu.database;

import android.content.ContentValues;
import android.content.Context;

import com.johan.subuwuliu.bean.DriverDetailBean;
import com.johan.subuwuliu.util.GsonUtil;

public class DriverDetailDatabase extends DatabaseHelper<DriverDetailBean> {

	public DriverDetailDatabase(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return DRIVER_DETAIL_TABLE_NAME;
	}

	@Override
	public ContentValues insertContentValues(DriverDetailBean object, String json) {
		// TODO Auto-generated method stub
		ContentValues contentValues = new ContentValues();
		contentValues.put(KEY_OF_DRIVER_DETAIL_YID, object.info.yid);
		contentValues.put(KEY_OF_DRIVER_DETAIL_TIMESTAMP, object.timestamp);
		contentValues.put(KEY_OF_DRIVER_DETAIL_CONTENT, json);
		return contentValues;
	}

	@Override
	public String defineSameCondition(DriverDetailBean object) {
		// TODO Auto-generated method stub
		return KEY_OF_DRIVER_DETAIL_YID + "='" + object.info.yid + "'";
	}

	@Override
	public DriverDetailBean parseObject(String json) {
		// TODO Auto-generated method stub
		return GsonUtil.jsonToObjct(json, DriverDetailBean.class);
	}
	
	public DriverDetailBean pull(String yid) {
		return pull(KEY_OF_DRIVER_DETAIL_YID, yid, KEY_OF_DRIVER_DETAIL_CONTENT);
	}

}
