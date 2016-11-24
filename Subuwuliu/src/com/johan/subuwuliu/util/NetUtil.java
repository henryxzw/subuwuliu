package com.johan.subuwuliu.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.json.JSONObject;

import com.baidu.trace.D;
import com.johan.subuwuliu.R;
import com.johan.subuwuliu.application.DiDiApplication;
import com.johan.subuwuliu.bean.StatusBean;
import com.johan.subuwuliu.qiniu.Myupdate;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.bitmap.BitmapCommonUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import android.widget.Toast;

public class NetUtil {

	 public static boolean type = true;
// 	 public static final String SERVER_IP = "http://192.168.1.120:8002";
	 public static final String SERVER_IP = "http://121.201.5.229:8002";
	 public static final String GPS_URL = "http://121.201.5.229:8001";
	// + "/gps.ashx";
//	public static final String SERVER_IP = "http://121.201.5.229:8001";
//	public static final String GPS_URL = SERVER_IP + "/api/android/gps.ashx";
	// //帮助
	public static final String Help_IP = "http://www.spvan.com/mycontent.aspx?page=driver_help";
	// //发现
	public static final String Find_IP = "http://spvan.wsq.umeng.com";
	// //关于速步车队
	public static final String Aboutchedui_IP = "http://spvan.wsq.umeng.com";
	// /////////// 设置好账号的ACCESS_KEY和SECRET_KEY
	public static String ACCESS_KEY = "PMCh4cBimTJl0rhChQh88_hra3aW2GP5aFp5rr8U";
	public static String SECRET_KEY = "eOeNSIL-i_ws5hfAmm_V8O2W0k6ctlCEzHxfTIQa";
	// //////
	public static final String wangzhi = "http://media.spvan.com/";

	public static final String DRIVER_URL = SERVER_IP
			+ "/api/android/driver.ashx?action=";

	public static final String ORDER_URL = SERVER_IP
			+ "/api/android/order.ashx?action=";

	public static final String UPLOAD_URL = SERVER_IP
			+ "/api/android/upload_ajax.ashx?action=single_file";

	private final static int DEFAULT_CONNECT_TIMEOUT = 1000 * 10; // 10s

	private HttpUtils httpUtils;

	private static NetUtil INSTANCE = new NetUtil();

	private NetUtil() {
		httpUtils = new HttpUtils(DEFAULT_CONNECT_TIMEOUT);
		httpUtils.configRequestRetryCount(0);
		httpUtils.configCurrentHttpCacheExpiry(1000);
	}

	public static NetUtil getInstance() {
		return INSTANCE;
	}

	public void doGet(String url, RequestCallBack<String> requestCallBack) {
		System.out.println("Get锟斤拷锟斤拷--------------------------");
		
		httpUtils.send(HttpMethod.GET, url, requestCallBack);
	}

	public void doPost(String url, RequestParams params,
			final RequestCallBack<String> callBack) {
		httpUtils.send(HttpMethod.POST, url, params, callBack);
	}

	public void doPost(String url, final RequestCallBack<String> callBack) {
		httpUtils.send(HttpMethod.POST, url, callBack);
	}

	public void uploadFile1(HashMap<String, String> uploadFileMap,
			UploadFileListener listener) {
		HashMap<String, String> resultMap = new HashMap<String, String>();
		Iterator<Entry<String, String>> iterator = uploadFileMap.entrySet()
				.iterator();
		upload1(iterator, resultMap, listener);
	}

	// //上传图片
	private void upload1(final Iterator<Entry<String, String>> iterator,
			final HashMap<String, String> resultMap,
			final UploadFileListener listener) {

		SharedPreferences sharedPreferences = DiDiApplication.applicationContext
				.getSharedPreferences(
						DiDiApplication.NAME_OF_SHARED_PREFERENCES,
						Context.MODE_PRIVATE);
		final String loginName = sharedPreferences
				.getString(DiDiApplication.KEY_OF_LOGIN_NAME,
						DiDiApplication.VALUE_UNKOWN);
		final ArrayList<Entry> list=new ArrayList<Entry>();
	 
		while ( iterator.hasNext()) {
			Entry<String, String> currentEntry = iterator.next();
			list.add(currentEntry); 
		}
		System.out.println("------sss------->>"+String.valueOf(list.get(0).getKey()));
		// 有，就继续  
		final UploadManager uploadManager = new UploadManager();
		final String token = Myupdate.Token();
		uploadManager.put(Myupdate.Bitmap2Bytes(BitmapFactory
				.decodeFile(String.valueOf(list.get(0).getValue()))), loginName + "_"
				+ Myupdate.Timea(), token, new UpCompletionHandler() {
			@Override
			public void complete(String arg0,
					com.qiniu.android.http.ResponseInfo arg1, JSONObject arg2) {
				// TODO Auto-generated method stub
				// res 包含hash、key等信息，具体字段取决于上传策略的设置。
				if (arg1.statusCode == 200) {
					System.out.println("当前图片返回图片名 : " + arg0);
					resultMap.put(String.valueOf(list.get(0).getKey()), arg0); 
					uploadManager.put(Myupdate.Bitmap2Bytes(BitmapFactory
							.decodeFile(String.valueOf(list.get(1).getValue()))), loginName + "_"
							+ Myupdate.Timea(), token, new UpCompletionHandler() {
						@Override
						public void complete(String arg0,
								com.qiniu.android.http.ResponseInfo arg1, JSONObject arg2) {
							// TODO Auto-generated method stub
							// res 包含hash、key等信息，具体字段取决于上传策略的设置。
							if (arg1.statusCode == 200) {
								System.out.println("当前图片返回图片名 : " + arg0);
								resultMap.put(String.valueOf(list.get(1).getKey()), arg0);
								uploadManager.put(Myupdate.Bitmap2Bytes(BitmapFactory
										.decodeFile(String.valueOf(list.get(2).getValue()))), loginName + "_"
										+ Myupdate.Timea(), token, new UpCompletionHandler() {
									@Override
									public void complete(String arg0,
											com.qiniu.android.http.ResponseInfo arg1, JSONObject arg2) {
										// TODO Auto-generated method stub
										// res 包含hash、key等信息，具体字段取决于上传策略的设置。
										if (arg1.statusCode == 200) {
											System.out.println("当前图片返回图片名 : " + arg0);
											resultMap.put(String.valueOf(list.get(2).getKey()), arg0);
											uploadManager.put(Myupdate.Bitmap2Bytes(BitmapFactory
													.decodeFile(String.valueOf(list.get(3).getValue()))), loginName + "_"
													+ Myupdate.Timea(), token, new UpCompletionHandler() {
												@Override
												public void complete(String arg0,
														com.qiniu.android.http.ResponseInfo arg1, JSONObject arg2) {
													// TODO Auto-generated method stub
													// res 包含hash、key等信息，具体字段取决于上传策略的设置。
													if (arg1.statusCode == 200) {
														System.out.println("当前图片返回图片名 : " + arg0);
														resultMap.put(String.valueOf(list.get(3).getKey()), arg0); 
														listener.onSuccess(resultMap);
													} else if (arg1.statusCode == 401) {
														if (listener != null) {
															listener.onFail("上传图片超时");
														}
														return;
													} else {
														if (listener != null) {
															listener.onFail("上传图片失败");
														}
														return;
													}
												}

											}, null);
										} else if (arg1.statusCode == 401) {
											if (listener != null) {
												listener.onFail("上传图片超时");
											}
											return;
										} else {
											if (listener != null) {
												listener.onFail("上传图片失败");
											}
											return;
										}
									}

								}, null);
							} else if (arg1.statusCode == 401) {
								if (listener != null) {
									listener.onFail("上传图片超时");
								}
								return;
							} else {
								if (listener != null) {
									listener.onFail("上传图片失败");
								}
								return;
							}
						}

					}, null);
				} else if (arg1.statusCode == 401) {
					if (listener != null) {
						listener.onFail("上传图片超时");
					}
					return;
				} else {
					if (listener != null) {
						listener.onFail("上传图片失败");
					}
					return;
				}
			}

		}, null);

	}

	public void uploadFile(HashMap<String, String> uploadFileMap,
			UploadFileListener listener) {
		HashMap<String, String> resultMap = new HashMap<String, String>();
		Iterator<Entry<String, String>> iterator = uploadFileMap.entrySet()
				.iterator();
		upload(iterator, resultMap, listener);
	}

	// //上传图片
	private void upload(final Iterator<Entry<String, String>> iterator,
			final HashMap<String, String> resultMap,
			final UploadFileListener listener) {
		if (iterator.hasNext()) {
			// 有，就继续
			final Entry<String, String> currentEntry = iterator.next();
			System.out.println("key : " + currentEntry.getKey());
			if ("".equals(currentEntry.getValue())) {
				resultMap.put(currentEntry.getKey(), currentEntry.getValue());
				// 继续
				upload(iterator, resultMap, listener);
			} else {
				RequestParams requestParams = new RequestParams();
				File uploadFile = new File(currentEntry.getValue());
				System.out.println("正在上传图片路径：" + uploadFile);
				requestParams.addBodyParameter("filename", uploadFile);
				doPost(UPLOAD_URL, requestParams,
						new RequestCallBack<String>() {
							@Override
							public void onSuccess(
									ResponseInfo<String> responseInfo) {
								String uploadResult = responseInfo.result;
								if (uploadResult.contains("status")) {
									StatusBean statusBean = GsonUtil
											.jsonToObjct(uploadResult,
													StatusBean.class);
									System.out.println("上传图片失败内部："
											+ statusBean.msg);
									if (listener != null) {
										listener.onFail(statusBean.msg);
									}
								} else {
									resultMap.put(currentEntry.getKey(),
											uploadResult);
									// 继续
									upload(iterator, resultMap, listener);
								}
							}

							@Override
							public void onFailure(HttpException exception,
									String info) {
								System.out.println("上传图片失败外部：" + info);
								if (listener != null) {
									listener.onFail(info);
								}
							}
						});
			}
		} else {
			// 没有，返回
			listener.onSuccess(resultMap);
		}
	}

	public interface UploadFileListener {
		public void onSuccess(HashMap<String, String> resultMap);

		public void onFail(String errorMsg);
	}

	public static void displayImageFromUrl(Context context,
			ImageView imageView, String uri) {
		displayImageFromUrl(context, imageView, uri, R.drawable.siji,
				R.drawable.siji);
	}

	public static void displayImageFromUrl(Context context,
			ImageView imageView, String uri, int loadingImg, int loadfailImg) {
		BitmapUtils utils = new BitmapUtils(context);
		BitmapDisplayConfig displayConfig = new BitmapDisplayConfig();
		displayConfig.setBitmapConfig(Bitmap.Config.RGB_565);
		displayConfig
				.setBitmapMaxSize(BitmapCommonUtils.getScreenSize(context));
		displayConfig.setLoadingDrawable(context.getResources().getDrawable(
				loadingImg));
		displayConfig.setLoadFailedDrawable(context.getResources().getDrawable(
				loadfailImg));
		utils.display(imageView, uri, displayConfig);
	}

	public void getBitmab(final File file) {
		UploadManager uploadManager = new UploadManager();
		String token = Myupdate.Token();
		String key = Myupdate.Timea();
		uploadManager.put(file, key, token, new UpCompletionHandler() {
			@Override
			public void complete(String arg0,
					com.qiniu.android.http.ResponseInfo arg1, JSONObject arg2) {
				// TODO Auto-generated method stub

			}
		}, null);

	}
}
