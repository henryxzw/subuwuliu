package com.johan.subuwuliu.view;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Sqlite
{
	public static final String DB_NAME = "BBB.db";
	public static final String TABLE_NAME = "lishijilub";
	public static final int VERSION = 1;

	private static final String csql = "create table lishijilub(" + "_id INTEGER PRIMARY KEY," + "order_no varchar(100),"
			+ "addr_start varchar(100)," + "addr_end varchar(100)," + "payment_time varchar(100)," + "plate_numberA varchar(100),"
			+ "car_typeA varchar(100)," + "car_statusA varchar(100)," + "plate_numberB varchar(100)," + "car_typeB varchar(100),"
			+ "car_statusB varchar(100)," + "plate_numberC varchar(100)," + "car_typeC varchar(100)," + "car_statusC varchar(100),"
			+ "plate_numberD varchar(100)," + "car_typeD varchar(100)," + "car_statusD varchar(100)," + "plate_numberE varchar(100),"
			+ "car_typeE varchar(100)," + "car_statusE varchar(100))";

	// 上下文对象
	public Context context = null;

	public SQLiteDatabase mySQLitedb;

	public MySQLiteOpenHelper mySQLiteHelper;

	public Sqlite(Context ct)
	{
		context = ct;
	}

	public void open()
	{
		this.mySQLiteHelper = new MySQLiteOpenHelper(context);
		this.mySQLitedb = this.mySQLiteHelper.getWritableDatabase();
	}

	public void close()
	{
		this.mySQLiteHelper.close();
	}

	/**
	 * 添加收藏, 并返回主键
	 * 
	 * @param type
	 *            N, C ,D
	 * @param sid
	 *            nid,cid,did
	 */
	public long insert(String order_no, String addr_start, String addr_end, String payment_time, String a1, String b1, String c1, String a2,
			String b2, String c2, String a3, String b3, String c3, String a4, String b4, String c4, String a5, String b5, String c5)
	{
		ContentValues cv = new ContentValues();
		cv.put("order_no", order_no);
		cv.put("addr_start", addr_start);
		cv.put("addr_end", addr_end);
		cv.put("payment_time", payment_time);
		cv.put("plate_numberA", a1);
		cv.put("car_typeA", b1);
		cv.put("car_statusA", c1);
		cv.put("plate_numberB", a2);
		cv.put("car_typeB", b2);
		cv.put("car_statusB", c2);
		cv.put("plate_numberC", a3);
		cv.put("car_typeC", b3);
		cv.put("car_statusC", c3);
		cv.put("plate_numberD", a4);
		cv.put("car_typeD", b4);
		cv.put("car_statusD", c4);
		cv.put("plate_numberE", a5);
		cv.put("car_typeE", b5);
		cv.put("car_statusE", c5);
		return this.mySQLitedb.insert(TABLE_NAME, null, cv);
	}

	public Cursor select()
	{
		return this.mySQLitedb.query(TABLE_NAME, null, null, null, null, null, null);
	}

	public Cursor selectAllByTypeId(String order_no)
	{
		StringBuffer scids = new StringBuffer();
		Cursor cs = this.mySQLitedb.rawQuery("select * from " + TABLE_NAME + " where order_no='" + order_no, null);
		System.out.println("....................selectAll:" + scids.toString());
		return cs;
	}

	// ////
	public void delect()
	{
		this.mySQLitedb.delete(TABLE_NAME, null, null);
	}

	// /删除单个的sid
	public void delectSid(String order_no)
	{
		this.mySQLitedb.delete(TABLE_NAME, "  order_no=? ", new String[] { order_no });
	}

	/**
	 * 清空所有收藏
	 */
	public void deleteAll()
	{
		this.mySQLitedb.delete(TABLE_NAME, null, null);
	}

	/**
	 * 通过内部类的方式创建openHelper类，实现数据库表的创建与更新
	 * 
	 * @author xyn
	 * 
	 */
	class MySQLiteOpenHelper extends SQLiteOpenHelper
	{

		public MySQLiteOpenHelper(Context ct)
		{
			super(ct, DB_NAME, null, VERSION);
		}

		// 初始化表，只调用一次，反调
		@Override
		public void onCreate(SQLiteDatabase db)
		{
			db.execSQL(csql);
		}

		// 当version发生变化的时候调用，反调
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{
			// 1. 删除老的表
			db.execSQL("drop table if exists " + TABLE_NAME);
			// 2.重新创建
			onCreate(db);

		}

	}

}
