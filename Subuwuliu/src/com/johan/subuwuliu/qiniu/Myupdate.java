package com.johan.subuwuliu.qiniu;

import android.R.integer;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.internal.ca;
import com.johan.subuwuliu.util.NetUtil;
import com.qiniu.android.utils.UrlSafeBase64;

@SuppressLint("SimpleDateFormat")
public class Myupdate {
	// //前一个参数是从七牛网站上得到的AccessKey,后一个参数是SecretKey
	// Auth auth=Auth.create("yPwTOJsk52Ggl3eG_cHngHfd-8Stou1We7zs9DPn",
	// "Onx-C3o_uG6hQKIhdu46Wjx78Tg-1bYKccq62dwb");
	// //第一个参数是你建立的空间名称，第三个是时间长度(按毫秒算的，我这里写的是10年)
	// String token=auth.uploadToken("appimages", null, 1000*3600*24*12*10,
	// null);
	// public static String Token() {
	//
	// Auth auth = Auth.create("HJpy1Mona6_F3SrA2AtbhigfbS2f_wAv7TCD3bgJ",
	// "uM7jIeUxSQAATY1o_lLRwrJUYMlPK3fEpAihvPwY");
	// String token = auth.uploadToken("updateb", null, 1000 * 3600 * 24 * 12
	// * 10, null);
	// ///////////////////////////
	// return token;
	// }

	public static String Token() {
		// 设置好账号的ACCESS_KEY和SECRET_KEY
		Auth auth = Auth.create("PMCh4cBimTJl0rhChQh88_hra3aW2GP5aFp5rr8U", "eOeNSIL-i_ws5hfAmm_V8O2W0k6ctlCEzHxfTIQa");
		// Auth auth = Auth.create("PMCh4cBimTJl0rhChQh88_hra3aW2GP5aFp5rr8U",
		// "eOeNSIL-i_ws5hfAmm_V8O2W0k6ctlCEzHxfTIQa");
		String token = auth.uploadToken("spvan", null, 1000 * 3600 * 24 * 12
				* 10, null);
		return token;
	}

//	public static String DownloadDemo(String namekey) { 
//		// 密钥配置
//		Auth auth = Auth.create(NetUtil.ACCESS_KEY, NetUtil.SECRET_KEY);
//		// 构造私有空间的需要生成的下载的链接
//		String URL = NetUtil.wangzhi + namekey;
//		// 调用privateDownloadUrl方法生成下载链接,第二个参数可以设置Token的过期时间
//		String downloadRUL = auth.privateDownloadUrl(URL, 1000 * 3600 * 24 * 12
//				* 10);
//		System.out.println(downloadRUL);
//		return downloadRUL;
//	}

	public static String DownloadDemo(String namekey) {
		// 设置好账号的ACCESS_KEY和SECRET_KEY
		String ACCESS_KEY = "PMCh4cBimTJl0rhChQh88_hra3aW2GP5aFp5rr8U";
		String SECRET_KEY = "eOeNSIL-i_ws5hfAmm_V8O2W0k6ctlCEzHxfTIQa";
		// 密钥配置
		Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);
		// 构造私有空间的需要生成的下载的链接
		String URL = NetUtil.wangzhi + namekey;
		// 调用privateDownloadUrl方法生成下载链接,第二个参数可以设置Token的过期时间
		String downloadRUL = auth.privateDownloadUrl(URL, 3600);
		System.out.println(downloadRUL);
		return downloadRUL;
	}
	// ///////////

	public static String Timea() {
		SimpleDateFormat sdFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		Date dataa = new Date(System.currentTimeMillis());
		String rime = sdFormat.format(dataa);
		return rime;
	}

	public static byte[] Bitmap2Bytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		byte[] file = baos.toByteArray();
		return file;
	}
	
	@SuppressLint("NewApi")
	public static byte[] GetBitmapCommBytes(File file)
	{
		Log.e("bitmap", "==================bitmap lenght:"+file.length()+"================");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int maxSize = 500*1024;
		Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
		int commSize = 1;
		if(file.length()>maxSize)
		{
			
			double x  =  bitmap.getByteCount()*2.0f/(maxSize*8);
			x = x<4? 2 : Math.sqrt(x);
			
			commSize = (int)x;
			Log.e("bitmap", "=============comm:"+commSize+"==========");
		}
		
		Log.e("bitmap","size:"+bitmap.getWidth()+"    "+bitmap.getHeight());
		Log.e("bitmap", "============= src bitmap :"+bitmap.getAllocationByteCount()+"      "+bitmap.getByteCount()+"==========");
			Bitmap bitmap2 = Bitmap.createBitmap(bitmap.getWidth()/commSize, bitmap.getHeight()/commSize, Config.RGB_565) ;
			Canvas canvas = new Canvas(bitmap2);
			canvas.drawBitmap(bitmap, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()), new Rect(0, 0, bitmap2.getWidth(), bitmap2.getHeight()), new Paint());
			Log.e("bitmap","bitmap 2 size:"+bitmap2.getWidth()+"    "+bitmap2.getHeight());
			Log.e("bitmap", "=============bitmap:"+bitmap2.getByteCount()+"==========");
			bitmap2.compress(Bitmap.CompressFormat.PNG, 100, baos);
		    bitmap.recycle();
		    bitmap2.recycle();
		return baos.toByteArray();
	}
	
	public static byte[] Bitmap2BytesComm(Bitmap bm)
	{
		long lenght = bm.getByteCount();
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		if(lenght/2>1024*8*300)
		{
			int cc = (int)(100 * 1024*8*300*2/lenght);
			
			bm.compress(Bitmap.CompressFormat.PNG, cc, baos);
		}
		else {
			bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		}
		byte[] file = baos.toByteArray();
		
		return file;

	}

	 
}