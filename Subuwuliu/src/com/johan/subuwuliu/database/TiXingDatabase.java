package com.johan.subuwuliu.database;

import java.util.ArrayList;
import java.util.List;

import com.johan.subuwuliu.bean.TiXingBean;
import com.johan.subuwuliu.util.GsonUtil;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class TiXingDatabase extends DatabaseHelper<TiXingBean> {

	public TiXingDatabase(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return TIXING_TABLE_NAME;
	}

	@Override
	public TiXingBean parseObject(String json) {
		// TODO Auto-generated method stub
		return GsonUtil.jsonToObjct(json, TiXingBean.class);
	}

	@Override
	public ContentValues insertContentValues(TiXingBean object, String json) {
		// TODO Auto-generated method stub
		ContentValues contentValues = new ContentValues();
		contentValues.put(KEY_OF_TIXING_CONTENT, json);
		return contentValues;
	}
	
	public List<TiXingBean> pullAll() {
		List<TiXingBean> list = new ArrayList<TiXingBean>();
		SQLiteDatabase database = getReadableDatabase();
		Cursor cursor = database.rawQuery("select * from " + getTableName(), null);
		while(cursor.moveToNext()) {
			String content = cursor.getString(cursor.getColumnIndex(KEY_OF_TIXING_CONTENT));
			TiXingBean tixing = GsonUtil.jsonToObjct(content, TiXingBean.class);
			list.add(tixing);
		}
		cursor.close();
		database.close();
		return list;
	}

}
