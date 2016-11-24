package com.johan.subuwuliu.database;

import java.util.ArrayList;
import java.util.List;

import com.johan.subuwuliu.bean.UploadOrderPictureBean;
import com.johan.subuwuliu.util.GsonUtil;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class UploadOrderPictrueDatabase extends DatabaseHelper<UploadOrderPictureBean> {

	public UploadOrderPictrueDatabase(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return UPLOADORDERPICTURE_TABLE_NAME;
	}

	@Override
	public UploadOrderPictureBean parseObject(String json) {
		// TODO Auto-generated method stub
		return GsonUtil.jsonToObjct(json, UploadOrderPictureBean.class);
	}

	@Override
	public ContentValues insertContentValues(UploadOrderPictureBean object, String json) {
		// TODO Auto-generated method stub
		ContentValues contentValues = new ContentValues();
		contentValues.put(KEY_OF_UPLOADORDERPICTURE_YID, object.yid);
		contentValues.put(KEY_OF_UPLOADORDERPICTURE_NODE, object.node);
		contentValues.put(KEY_OF_UPLOADORDERPICTURE_URL, object.url);
		return contentValues;
	}
	
	@Override
	public String defineSameCondition(UploadOrderPictureBean object) {
		// TODO Auto-generated method stub
		return KEY_OF_UPLOADORDERPICTURE_YID + "='" + object.yid + "' and " + KEY_OF_UPLOADORDERPICTURE_NODE + "='" + object.node + "'";
	}
	
	public List<UploadOrderPictureBean> pullAll(String yid) {
		List<UploadOrderPictureBean> pictureOrderList = new ArrayList<UploadOrderPictureBean>();
		SQLiteDatabase database = getReadableDatabase();
		Cursor cursor = database.rawQuery("select * from " + getTableName() + " where " + KEY_OF_UPLOADORDERPICTURE_YID + "='" + yid + "'", null);
		while(cursor.moveToNext()) {
			String currentYid = cursor.getString(cursor.getColumnIndex(KEY_OF_UPLOADORDERPICTURE_YID));
			String currentNode = cursor.getString(cursor.getColumnIndex(KEY_OF_UPLOADORDERPICTURE_NODE));
			String currentUrl = cursor.getString(cursor.getColumnIndex(KEY_OF_UPLOADORDERPICTURE_URL));
			UploadOrderPictureBean orderPicture = new UploadOrderPictureBean(currentYid, currentNode, currentUrl);
			pictureOrderList.add(orderPicture);
		}
		cursor.close();
		database.close();
		return pictureOrderList;
	}
	
	public void delete(String yid) {
		SQLiteDatabase database = getWritableDatabase();
		database.delete(getTableName(), KEY_OF_UPLOADORDERPICTURE_YID + "=?", new String[]{yid});
		database.close();
	}

}
