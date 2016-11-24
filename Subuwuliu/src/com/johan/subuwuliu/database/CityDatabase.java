package com.johan.subuwuliu.database;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.johan.subuwuliu.application.DiDiApplication;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.widget.Toast;

public class CityDatabase {

	private static final String dbFilePath = Environment.getExternalStorageDirectory() + "/" + DiDiApplication.PROJECT_DIR + "/";
	private static final String dbFileName = dbFilePath + "db_weather.db";

	private Context context;
	
	public CityDatabase(Context context) {
		this.context = context;
	}

	public List<String> getAllCity() {
//		if(!existSDCard()) {
//			Toast.makeText(context, "没有SD卡", Toast.LENGTH_LONG).show();
//			return null;
//		}
		List<String> cityList = new ArrayList<String>();
		File dbFile = getDBFile(); 
		SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
		Cursor cityCursor = database.query("citys", null, null, null, null, null, null);
		while(cityCursor.moveToNext()){  
			//数据表的城市所属省份从0开始，而省份的id从1开始
            String cityName = cityCursor.getString(cityCursor.getColumnIndex("name"));  
            int index = cityName.indexOf(".");
            if(index != -1) {
            	cityName = cityName.substring(index + 1);
            }
            cityList.add(cityName);
		}  
		cityCursor.close();
		return cityList;
	}
	
	public String getProvinceFromCity(String city) {
		if(!existSDCard()) {
			Toast.makeText(context, "没有SD卡", Toast.LENGTH_LONG).show();
			return null;
		}
		File dbFile = getDBFile();
		String provinceName = "";
		SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
		Cursor cityCursor = database.query("citys", null, "name like '%" + city + "%'", null, null, null, null);
		while(cityCursor.moveToNext()){  
			//数据表的城市所属省份从0开始，而省份的id从1开始
            int provinceId = cityCursor.getInt(cityCursor.getColumnIndex("province_id")) + 1;  
            Cursor provinceCursor = database.query("provinces", null, "_id='" + provinceId + "'", null, null, null, null);
            while(provinceCursor.moveToNext()){  
            	provinceName = provinceCursor.getString(provinceCursor.getColumnIndexOrThrow("name"));  
            	break;
            }
            provinceCursor.close();
            break;
		}  
		cityCursor.close();
		return provinceName;
	}
	
	public String getAssetsCacheFile(Context context,String fileName)   {
        File cacheFile = new File(context.getCacheDir(), fileName);
        try {
            InputStream inputStream = context.getAssets().open(fileName);
            try {
                FileOutputStream outputStream = new FileOutputStream(cacheFile);
                try {
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = inputStream.read(buf)) > 0) {
                        outputStream.write(buf, 0, len);
                    }
                } finally {
                    outputStream.close();
                }
            } finally {
                inputStream.close();
            }
        } catch (IOException e) {
           e.printStackTrace();
        }
        return cacheFile.getAbsolutePath();
    }
	
	private File getDBFile() {
		
		return new File(getAssetsCacheFile(context, "db_weather.db"));
//		File dbDir = new File(dbFilePath);
//		if(!dbDir.exists() || !dbDir.isDirectory()) {
//			dbDir.mkdir();
//		}
//		File dbFile = new File(dbFileName);
//		System.out.println("是否存在---》" + dbFile.exists());
//		if(!dbFile.exists() || !dbFile.isFile()){
//			try {  
//				// 得到资源
//				AssetManager am = context.getAssets();
//				// 得到数据库的输入流
//				InputStream is = am.open("db_weather.db");
//				// 用输出流写到SDcard上面
//				FileOutputStream fos = new FileOutputStream(dbFileName);
//				// 创建byte数组 用于1KB写一次
//				byte[] buffer = new byte[1024];
//				int count = 0;
//				while ((count = is.read(buffer)) > 0) {
//					fos.write(buffer, 0, count);
//				}
//				// 最后关闭就可以了
//				fos.flush();
//				fos.close();
//				is.close();
//	        } catch (IOException e) {  
//		          // TODO Auto-generated catch block  
//		          e.printStackTrace();  
//	        }  
//		}
//		return dbFile;
	}
	
	private boolean existSDCard() {
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

}
