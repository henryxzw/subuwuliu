package com.johan.subuwuliu.dialog;

import java.util.List;

import com.johan.subuwuliu.R;
import com.johan.subuwuliu.util.NetUtil;
import com.johan.subuwuliu.view.WheelView;
import com.johan.subuwuliu.view.WheelView.OnWheelViewListener;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import android.content.Context;

public abstract class SelectWheelViewDialog {

	private String selectedData;
	
	private SelectDialog dialog;
	
	private WheelView wheelView;
	
	private OnSelectedListener onSelectedListener;
	
	public SelectWheelViewDialog(Context context, String title, String url) {
		dialog = new SelectDialog(context, title, R.layout.view_wheelview);
		wheelView = (WheelView) dialog.findViewById(R.id.wheel_view);
		dialog.showWaiting(true);
		NetUtil.getInstance().doGet(url, new RequestCallBack<String>() {				
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				List<String> dataList = parseData(responseInfo);
				//初始值
				if(dataList.size() > 0) {
					selectedData = dataList.get(0);
				}
				wheelView.setOffset(2);
				wheelView.setItems(dataList);
				wheelView.setSeletion(0);
				wheelView.setOnWheelViewListener(new OnWheelViewListener() {
					@Override
					public void onSelected(int selectedIndex, String item) {
						System.out.println("---->" + item);
						selectedData = item;
					}
				});
				dialog.showWaiting(false);
			}
			@Override
			public void onFailure(HttpException exception, String info) {
				dialog.setWaitingTip(info);
				dialog.showWaitingIcon(false);
			}
		});
		wheelView = (WheelView) dialog.findViewById(R.id.wheel_view);
		dialog.setOnClickOkListener(new OnClickOkListener() {
			@Override
			public void clickOk() {
				// TODO Auto-generated method stub
				if(onSelectedListener != null) {
					onSelectedListener.onSelected(selectedData);
				}
			}
		});
	}
	
	public void show() {
		dialog.show();
	}
	
	public void setOnSelectedListener(OnSelectedListener onSelectedListener) {
		this.onSelectedListener = onSelectedListener;
	}
	
	public abstract List<String> parseData(ResponseInfo<String> responseInfo);
	
}
