package com.johan.subuwuliu.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public abstract class DatabaseHelper <T> extends SQLiteOpenHelper {

	public static final String DIDI_DATABASE_NAME = "subuliu";
	public static final int DIDI_DATEBASE_VERSION = 1;
	
	protected static final String DRIVER_DETAIL_TABLE_NAME = "driver_detail";
	protected static final String DRIVER_ROUTE_TABLE_NAME = "driver_route";
	protected static final String ORDER_DETAIL_TABLE_NAME = "order_detail";
	protected static final String LOCATION_TABLE_NAME = "location";
	protected static final String CITY_HISTORY_TABLE_NAME = "city_history";
	protected static final String UPLOADORDERPICTURE_TABLE_NAME = "upload_order_picture";
	protected static final String TIXING_TABLE_NAME = "tixing";
	
	protected static final String KEY_OF_ORDER_DETAIL_ID = "od_id";
	protected static final String KEY_OF_ORDER_DETAIL_NUMBER = "od_number";
	protected static final String KEY_OF_ORDER_DETAIL_TIMESTAMP = "od_timestamp";
	protected static final String KEY_OF_ORDER_DETAIL_CONTENT = "od_content";
	protected static final String KEY_OF_DRIVER_ROUTE_ID = "dr_id";
	protected static final String KEY_OF_DRIVER_ROUTE_YID = "dr_yid";
	protected static final String KEY_OF_DRIVER_ROUTE_TIMESTAMP = "dr_timestamp";
	protected static final String KEY_OF_DRIVER_ROUTE_CONTENT = "dr_content";
	protected static final String KEY_OF_DRIVER_DETAIL_ID = "dd_id";
	protected static final String KEY_OF_DRIVER_DETAIL_YID = "dd_yid";
	protected static final String KEY_OF_DRIVER_DETAIL_TIMESTAMP = "dd_timestamp";
	protected static final String KEY_OF_DRIVER_DETAIL_CONTENT = "dd_content";
	protected static final String KEY_OF_LOCATION_ID = "lo_id";
	protected static final String KEY_OF_LOCATION_DRIVER_ID = "lo_driver_id";
	protected static final String KEY_OF_LOCATION_CONTENT = "lo_content";
	protected static final String KEY_OF_CITY_HISTORY_ID = "ch_id";
	protected static final String KEY_OF_CITY_HISTORY_NAME = "ch_name";
	protected static final String KEY_OF_CITY_HISTORY_ADDRESS = "ch_address";
	protected static final String KEY_OF_CITY_HISTORY_CITY = "ch_city";
	protected static final String KEY_OF_CITY_HISTORY_LONGITUDE = "ch_longitude";
	protected static final String KEY_OF_CITY_HISTORY_LATITUDE = "ch_latitude";
	protected static final String KEY_OF_UPLOADORDERPICTURE_ID = "uop_id";
	protected static final String KEY_OF_UPLOADORDERPICTURE_YID = "uop_yid";
	protected static final String KEY_OF_UPLOADORDERPICTURE_NODE = "uop_node";
	protected static final String KEY_OF_UPLOADORDERPICTURE_URL = "uop_url";
	protected static final String KEY_OF_TIXING_ID = "tx_id";
	protected static final String KEY_OF_TIXING_CONTENT = "tx_content";
	
	//订单预约表
	private String table_create_describe_1 = "CREATE TABLE IF NOT EXISTS " + ORDER_DETAIL_TABLE_NAME + " (" + 
											 KEY_OF_ORDER_DETAIL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + 
											 KEY_OF_ORDER_DETAIL_NUMBER + " VARCHAR(20)," + 
											 KEY_OF_ORDER_DETAIL_TIMESTAMP + " VARCHAR(15)," +
											 KEY_OF_ORDER_DETAIL_CONTENT + " TEXT" + 
											 ");";
	
	//行驶记录表
	private String table_create_describe_2 = "CREATE TABLE IF NOT EXISTS " + DRIVER_ROUTE_TABLE_NAME + " (" + 
											 KEY_OF_DRIVER_ROUTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + 
											 KEY_OF_DRIVER_ROUTE_YID + " VARCHAR(20)," + 
											 KEY_OF_DRIVER_ROUTE_TIMESTAMP + " VARCHAR(15)," +
											 KEY_OF_DRIVER_ROUTE_CONTENT + " TEXT" + 
											 ");";
	
	//行驶记录表
	private String table_create_describe_3 = "CREATE TABLE IF NOT EXISTS " + DRIVER_DETAIL_TABLE_NAME + " (" + 
											 KEY_OF_DRIVER_DETAIL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + 
											 KEY_OF_DRIVER_DETAIL_YID + " VARCHAR(20)," + 
											 KEY_OF_DRIVER_DETAIL_TIMESTAMP + " VARCHAR(15)," +
											 KEY_OF_DRIVER_DETAIL_CONTENT + " TEXT" + 
											");";
	
	//位置表
	private String table_create_describe_4 = "CREATE TABLE IF NOT EXISTS " + LOCATION_TABLE_NAME + " (" + 
											 KEY_OF_LOCATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + 
											 KEY_OF_LOCATION_DRIVER_ID + " VARCHAR(20)," + 
											 KEY_OF_LOCATION_CONTENT + " VARCHAR(100)" +
											 ");";
	
	//城市历史表
	private String table_create_describe_5 = "CREATE TABLE IF NOT EXISTS " + CITY_HISTORY_TABLE_NAME + " (" + 
											 KEY_OF_CITY_HISTORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + 
											 KEY_OF_CITY_HISTORY_NAME + " VARCHAR(10)," + 
											 KEY_OF_CITY_HISTORY_ADDRESS + " VARCHAR(50)," +
											 KEY_OF_CITY_HISTORY_CITY + " VARCHAR(10)," +
											 KEY_OF_CITY_HISTORY_LONGITUDE + " VARCHAR(25)," +
											 KEY_OF_CITY_HISTORY_LATITUDE + " VARCHAR(25)" +
											 ");";
	
	//城市历史表
	private String table_create_describe_6 = "CREATE TABLE IF NOT EXISTS " + UPLOADORDERPICTURE_TABLE_NAME + " (" + 
											 KEY_OF_UPLOADORDERPICTURE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + 
											 KEY_OF_UPLOADORDERPICTURE_YID + " VARCHAR(20)," + 
											 KEY_OF_UPLOADORDERPICTURE_NODE + " VARCHAR(5)," +
											 KEY_OF_UPLOADORDERPICTURE_URL + " VARCHAR(200)" + 
											 ");";
	
	//提醒表
	private String table_create_describe_7 = "CREATE TABLE IF NOT EXISTS " + TIXING_TABLE_NAME + " (" + 
											 KEY_OF_TIXING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + 
											 KEY_OF_TIXING_CONTENT + " TEXT" + 
											 ");";
		
	
	public DatabaseHelper(Context context) {
		this(context, DIDI_DATABASE_NAME, null, DIDI_DATEBASE_VERSION);
	}

	public DatabaseHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		//创建数据表
		db.execSQL(table_create_describe_1);
		db.execSQL(table_create_describe_2);
		db.execSQL(table_create_describe_3);
		db.execSQL(table_create_describe_4);
		db.execSQL(table_create_describe_5);
		db.execSQL(table_create_describe_6);
		db.execSQL(table_create_describe_7);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}
	
	public abstract String getTableName();
	
	public long push(String json) {
		SQLiteDatabase database = getWritableDatabase();
		T object = parseObject(json);
		//否有相同，相同数据则删除旧数据
		Cursor cursor = database.rawQuery("select * from " + getTableName() + " where " + defineSameCondition(object), null);
		if(cursor.moveToNext()) {
			database.delete(getTableName(), defineSameCondition(object), null);
		}
		cursor.close();
		//插入新数据
		long insertId = database.insert(getTableName(), null, insertContentValues(object, json));
		database.close();
		return insertId;
	}
	
	public abstract T parseObject(String json);
	
	public abstract ContentValues insertContentValues(T object, String json);
	
	public T pull(String key, String value, String contetnKey) {
		SQLiteDatabase database = getReadableDatabase();
		Cursor cursor = database.rawQuery("select * from " + getTableName() + " where " + key + "=?", new String[]{value});
        if(cursor.moveToNext()){
        	String orderContent = cursor.getString(cursor.getColumnIndex(contetnKey));
    		T object = parseObject(orderContent);
        	cursor.close();
        	database.close();
        	return object;
        }
        cursor.close();
    	database.close();
        return null;
	}
	
	public void clear() {
		SQLiteDatabase database = getWritableDatabase();
		database.execSQL("delete from " + getTableName() + " where 1;");
		database.close();
	}
	
	public String defineSameCondition(T object) {
		//绝对否定
		return "1==0";
	}
	
}
