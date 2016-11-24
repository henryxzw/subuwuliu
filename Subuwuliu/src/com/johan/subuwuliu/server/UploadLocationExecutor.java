package com.johan.subuwuliu.server;
 
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.Handler; 

import com.baidu.trace.Trace;
import com.johan.subuwuliu.HomeActivity;
import com.johan.subuwuliu.application.DiDiApplication;
import com.johan.subuwuliu.bean.LocationInfo;
import com.johan.subuwuliu.bean.StatusBean; 
import com.johan.subuwuliu.util.GsonUtil;
import com.johan.subuwuliu.util.NetUtil;
import com.johan.subuwuliu.util.ToolUtil;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

public class UploadLocationExecutor {

	public static final int POST_PRIORITY_NORMAL = 21;
	public static final int POST_PRIORITY_HIGH = 22;

	private static UploadLocationExecutor INSTANCE = new UploadLocationExecutor();

	private List<LocationInfo> locationInfoList;

	private Object object = new Object();

	private boolean isRunning = false;

	private PostCallback callback;

	private Handler handler = new Handler();

	private UploadLocationExecutor() { 
		locationInfoList = new ArrayList<LocationInfo>();
	}

	public static UploadLocationExecutor getInstance() {
		return INSTANCE;
	}

	public void registerPostCallback(PostCallback callback) {
		this.callback = callback;
	}

	public void post(final LocationInfo locationInfo, int priority) {
		synchronized (object) {
			if (priority == POST_PRIORITY_HIGH) {
				locationInfoList.add(0, locationInfo);
			} else {
				locationInfoList.add(locationInfo);
			}
		}
		postData();
	}

	private synchronized void postData() {
		if (isRunning)
			return;
		isRunning = true;
		handler.post(postDataRunnable);
	}

	private Runnable postDataRunnable = new Runnable() {
		@Override
		public void run() {
			LocationInfo locationInfo = null;
			synchronized (object) {
				if (locationInfoList.size() > 0) {
					locationInfo = locationInfoList.get(0);
					locationInfoList.remove(0);
				}
			}
			if (locationInfo == null) {
				isRunning = false;
				System.out.println("没有了");
				return;
			}
			System.out.println("提交新的");
			final String driverId = locationInfo.driverId;
			final String location = locationInfo.location;
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("driver_id", driverId);
			params.put("gps", location);
			String action = ToolUtil.encryptionUrlParams(params);
			RequestParams requestParams = new RequestParams("UTF8");
			requestParams.addBodyParameter("gps", action);
			NetUtil.getInstance().doPost(NetUtil.GPS_URL, requestParams,
					new RequestCallBack<String>() {
						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
							StatusBean statusBean = GsonUtil.jsonToObjct(
									responseInfo.result, StatusBean.class);
							if ("0".equals(statusBean.status)) {
								callback.onSuccess();
								handler.postDelayed(postDataRunnable, 100);
								System.out.println("---GPS上传成功-->>>");
							} else {
								callback.onFail(driverId, location);
								handler.postDelayed(postDataRunnable, 100);
								System.out.println("---GPS上传失败-->>>");
							}
						}

						@Override
						public void onFailure(HttpException exception,
								String info) {
							System.out.println("错误======>"
									+ exception.getMessage());
							callback.onFail(driverId, location);
							handler.postDelayed(postDataRunnable, 100);
							System.out.println("------->>");

						}
					});
		}
	};

	public interface PostCallback {
		public void onSuccess(); 
		public void onFail(String driverId, String location);
	}

}
