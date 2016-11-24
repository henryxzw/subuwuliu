package com.johan.subuwuliu.dialog;

import java.util.ArrayList;
import java.util.List;

import com.johan.subuwuliu.R;
import com.johan.subuwuliu.bean.CarTypeBean.CarTypeDetailBean;
import com.johan.subuwuliu.bean.CarTypeBean.CarTypeDetailBean.CarTypeSizeBean;
import com.johan.subuwuliu.view.WheelView;
import com.johan.subuwuliu.view.WheelView.OnWheelViewListener;

import android.content.Context;

public class SelectCarSizeDialog {

	private SelectDialog dialog;
	
	private String selectedWeight, selectedSize;
	
	private List<CarTypeDetailBean> carTypeDetailList;
	private List<String> weightList = new ArrayList<String>();
	private List<String> sizeList = new ArrayList<String>();
	
	private OnSelectedListener onSelectedListener;
		
	public SelectCarSizeDialog(Context context, List<CarTypeDetailBean> carTypeDetailList) {
		this.carTypeDetailList = carTypeDetailList;
		dialog = new SelectDialog(context, "规格尺寸", R.layout.view_carsize);
		final WheelView weightView = (WheelView) dialog.findViewById(R.id.carsize_weight);
		final WheelView sizeView = (WheelView) dialog.findViewById(R.id.carsize_size);
		for(CarTypeDetailBean detail : carTypeDetailList) {
			weightList.add(detail.title);
		}
		weightView.setOffset(2);
		weightView.setFullLine(true);
		weightView.setItems(weightList);
		weightView.setSeletion(0);
		weightView.setOnWheelViewListener(new OnWheelViewListener() {
			@Override
			public void onSelected(int selectedIndex, String item) {
				System.out.println("---->" + item);
				selectedWeight = item;
				List<CarTypeSizeBean> currentSizeList = getCarTypeSizeList(item);
				if(currentSizeList != null) {
					sizeList.clear();
					for(CarTypeSizeBean size : currentSizeList) {
						sizeList.add(size.title);
					}
					sizeView.setItems(sizeList);
					sizeView.setSeletion(0);
					selectedSize = sizeList.get(0);
				}
			}
		});
		selectedWeight = carTypeDetailList.get(0).title;
		for(CarTypeSizeBean size : carTypeDetailList.get(0).sub_title) {
			sizeList.add(size.title);
		}
		sizeView.setOffset(2);
		sizeView.setFullLine(true);
		sizeView.setItems(sizeList);
		sizeView.setSeletion(0);
		sizeView.setOnWheelViewListener(new OnWheelViewListener() {
			@Override
			public void onSelected(int selectedIndex, String item) {
				System.out.println("---->" + item);
				selectedSize = item;
			}
		});
		//有一定的风险，可能没有size，不过这是不可能的
		selectedSize = carTypeDetailList.get(0).sub_title.get(0).title;
		//监听
		dialog.setOnClickOkListener(new OnClickOkListener() {
			@Override
			public void clickOk() {
				// TODO Auto-generated method stub
				if(onSelectedListener != null) {
					onSelectedListener.onSelected(selectedWeight + "#" + selectedSize + "#" + getIdFromWeightAndSize());
				}
			}
		});
	}
	
	private List<CarTypeSizeBean> getCarTypeSizeList(String weight) {
		for(CarTypeDetailBean detail : carTypeDetailList) {
			if(weight.equals(detail.title)) {
				return detail.sub_title;
			}
		}
		return null;
	}
	
	private String getIdFromWeightAndSize() {
		for(CarTypeDetailBean detail : carTypeDetailList) {
			System.out.println(detail.title + "--" + selectedWeight);
			if(selectedWeight.equals(detail.title)) {
				for(CarTypeSizeBean size : detail.sub_title) {
					System.out.println(size.title + "--" + selectedSize);
					if(selectedSize.equals(size.title)) {
						return size.id;
					}
				}
			}
		}
		return null;
	}
	
	public void setOnSelectedListener(OnSelectedListener onSelectedListener) {
		this.onSelectedListener = onSelectedListener;
	}
	
	public void show() {
		dialog.show();
	}
	
}
