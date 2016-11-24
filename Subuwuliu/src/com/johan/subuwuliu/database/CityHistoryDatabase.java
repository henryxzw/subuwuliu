package com.johan.subuwuliu.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.johan.subuwuliu.bean.CityBean;
import com.johan.subuwuliu.util.GsonUtil;

public class CityHistoryDatabase extends DatabaseHelper<CityBean> {

	public CityHistoryDatabase(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return CITY_HISTORY_TABLE_NAME;
	}

	@Override
	public CityBean parseObject(String json) {
		// TODO Auto-generated method stub
		return GsonUtil.jsonToObjct(json, CityBean.class);
	}

	@Override
	public ContentValues insertContentValues(CityBean object, String json) {
		// TODO Auto-generated method stub
		ContentValues contentValues = new ContentValues();
		contentValues.put(KEY_OF_CITY_HISTORY_ADDRESS, object.address);
		contentValues.put(KEY_OF_CITY_HISTORY_NAME, object.name);
		contentValues.put(KEY_OF_CITY_HISTORY_CITY, object.city);
		contentValues.put(KEY_OF_CITY_HISTORY_LONGITUDE, object.longitude);
		contentValues.put(KEY_OF_CITY_HISTORY_LATITUDE, object.latitude);
		return contentValues;
	}
	
	public List<CityBean> pullAll(String searchCity) {
		List<CityBean> allCityList = new ArrayList<CityBean>();
		Cursor historyCursor = getWritableDatabase().rawQuery("select * from " + getTableName() + " where " + KEY_OF_CITY_HISTORY_CITY + "=?", new String[]{searchCity});
		while(historyCursor.moveToNext()){  
			String name = historyCursor.getString(historyCursor.getColumnIndex(KEY_OF_CITY_HISTORY_NAME));
			String address = historyCursor.getString(historyCursor.getColumnIndex(KEY_OF_CITY_HISTORY_ADDRESS));
			String city = historyCursor.getString(historyCursor.getColumnIndex(KEY_OF_CITY_HISTORY_CITY));
			String longitude = historyCursor.getString(historyCursor.getColumnIndex(KEY_OF_CITY_HISTORY_LONGITUDE));
			String latitude = historyCursor.getString(historyCursor.getColumnIndex(KEY_OF_CITY_HISTORY_LATITUDE));
			CityBean cityBean = new CityBean(name, address, city, longitude, latitude);
			allCityList.add(cityBean);
		}
		historyCursor.close();
		getWritableDatabase().close();
		return allCityList;
	}

	@Override
	public String defineSameCondition(CityBean object) {
		// TODO Auto-generated method stub
		return KEY_OF_CITY_HISTORY_ADDRESS + "='" + object.address + "'";
	}

}
