package com.johan.subuwuliu.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.johan.subuwuliu.bean.LocationInfo;
import com.johan.subuwuliu.util.GsonUtil;

public class LocationDatabase extends DatabaseHelper<LocationInfo> {

	public LocationDatabase(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return LOCATION_TABLE_NAME;
	}

	@Override
	public LocationInfo parseObject(String json) {
		// TODO Auto-generated method stub
		return GsonUtil.jsonToObjct(json, LocationInfo.class);
	}

	@Override
	public ContentValues insertContentValues(LocationInfo object, String json) {
		// TODO Auto-generated method stub
		ContentValues contentValues = new ContentValues();
		contentValues.put(KEY_OF_LOCATION_DRIVER_ID, object.driverId);
		contentValues.put(KEY_OF_LOCATION_CONTENT, object.location);
		return contentValues;
	}
	
	public List<LocationInfo> pullAll() {
		List<LocationInfo> locationInfoList = new ArrayList<LocationInfo>();
		SQLiteDatabase database = getReadableDatabase();
		Cursor cursor = database.rawQuery("select * from " + getTableName(), null);
		while(cursor.moveToNext()) {
			String driverId = cursor.getString(cursor.getColumnIndex(KEY_OF_LOCATION_DRIVER_ID));
			String location = cursor.getString(cursor.getColumnIndex(KEY_OF_LOCATION_CONTENT));
			LocationInfo locationInfo = new LocationInfo(driverId, location);
			locationInfoList.add(locationInfo);
		}
		return locationInfoList;
	}

}
