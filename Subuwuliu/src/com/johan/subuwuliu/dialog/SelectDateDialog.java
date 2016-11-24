package com.johan.subuwuliu.dialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.johan.subuwuliu.R;
import com.johan.subuwuliu.view.WheelView;
import com.johan.subuwuliu.view.WheelView.OnWheelViewListener;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SelectDateDialog {

	private Dialog selectDateDialog;
	
	private int year, month, day;
	
	private WheelView yearWheelView, monthWheelView, dayWheelView;
	
	private List<String> yearDataList, monthDataList, dayDataList;
	
	private OnSelectedListener onSelectedListener;
	
	public SelectDateDialog(Context context) {
		selectDateDialog = new Dialog(context, R.style.DiDiDialog);
		selectDateDialog.setContentView(R.layout.dialog_selecttime);
		init();
	}
	
	private void init() {
		yearWheelView = (WheelView) selectDateDialog.findViewById(R.id.selecttime_picker_year_view);
		monthWheelView = (WheelView) selectDateDialog.findViewById(R.id.selecttime_picker_month_view);
		dayWheelView = (WheelView) selectDateDialog.findViewById(R.id.selecttime_picker_day_view);
		yearDataList = new ArrayList<String>();
		monthDataList = new ArrayList<String>();
		dayDataList = new ArrayList<String>();
		//初始化值
		initData();
		//年轮
		yearWheelView.setOnWheelViewListener(new OnWheelViewListener() {
			@Override
			public void onSelected(int selectedIndex, String item) {
				year = Integer.parseInt(item);
			}
		});
		//月轮
		monthWheelView.setOnWheelViewListener(new OnWheelViewListener() {
			@Override
			public void onSelected(int selectedIndex, String item) {
				month = Integer.parseInt(item);
				//又要取得天数
				int dayCount = getDay(year, month);
				dayDataList.clear();
				for(int i = 1; i <= dayCount; i++) {
					dayDataList.add(String.valueOf(i));
				}
				dayWheelView.setItems(dayDataList);
				//重置day
				dayWheelView.setSeletion(0);
				day = 1;
			}
		});
		//日轮
		dayWheelView.setOnWheelViewListener(new OnWheelViewListener() {
			@Override
			public void onSelected(int selectedIndex, String item) {
				System.out.println("选择天数：" + item);
				day = Integer.parseInt(item);
			}
		});
		//监听
		Button selectedOk = (Button) selectDateDialog.findViewById(R.id.selecttime_but_ok);
		selectedOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(onSelectedListener != null) {
					onSelectedListener.onSelected(dateToString());
				}
				selectDateDialog.dismiss();
			}
		});
		Button selectCancel = (Button) selectDateDialog.findViewById(R.id.selecttime_but_cancel);
		selectCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				selectDateDialog.dismiss();
			}
		});
	}
	
	private void initData() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		setDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
		System.out.println(calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH));
	}
	
	public void setDate(String date) {
		if("".equals(date) || date == null) {
			initData();
			return;
		}
		int[] dates = stringToDate(date);
		setDate(dates[0], dates[1], dates[2]);
	}
	
	private void setDate(int year, int month, int day) {
		this.year = year;     
        this.month = month;      
        this.day = day;    
		yearDataList.clear();
		monthDataList.clear();
		dayDataList.clear();
		for(int i = 2010; i <= 2050; i++) {
			yearDataList.add(String.valueOf(i));
		}
		for(int i = 1; i <= 12; i++) {
			monthDataList.add(String.valueOf(i));
		}
		int dayCount = getDay(year, month);
		for(int i = 1; i <= dayCount; i++) {
			dayDataList.add(String.valueOf(i));
		}
		//滚到指定位置
		yearWheelView.setOffset(2);
		yearWheelView.setItems(yearDataList);
		yearWheelView.setSeletion(year-2010);
		monthWheelView.setOffset(2);
		monthWheelView.setItems(monthDataList);
		monthWheelView.setSeletion(month-1);
		dayWheelView.setOffset(2);
		dayWheelView.setItems(dayDataList);
		dayWheelView.setSeletion(day-1);
	}
	
	private int getDay(int year, int month) {
		int dayCount = 0;
		if(month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) {
			dayCount = 31;
		} else if(month == 2) {
			if((year % 100 == 0 && year % 4 == 0) || year % 400 == 0) {
				dayCount = 29;
			} else {
				dayCount = 28;
			}
		} else {
			dayCount = 30;
		}
		return dayCount;
	}
	
	public void show() {
		selectDateDialog.show();
	}
	
	public void dismiss() {
		selectDateDialog.dismiss();
	}
	
	public void setOnSelectedListener(OnSelectedListener onSelectedListener) {
		this.onSelectedListener = onSelectedListener;
	}
	
	private String dateToString() {
		String monthStr = String.valueOf(month);
		if(month < 10) {
			monthStr = "0" + monthStr;
		}
		String dayStr = String.valueOf(day);
		if(day < 10) {
			dayStr = "0" + dayStr;
		}
		return year + "." + monthStr + "." + dayStr;
	}
	
	private int[] stringToDate(String date) {
		int[] dates = new int[3];
		dates[0] = Integer.parseInt(date.substring(0, 4));
		dates[1] = Integer.parseInt(date.substring(5, 7));
		dates[2] = Integer.parseInt(date.substring(8));
		return dates;
	}
	
	public static String ToUploadData(String date) {
		return date.substring(2, 4) + date.substring(5, 7) + date.substring(8) + "000000";
	}
	
}
