package com.johan.subuwuliu.dialog;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.johan.subuwuliu.R;
import com.johan.subuwuliu.view.WheelView;
import com.johan.subuwuliu.view.WheelView.OnWheelViewListener;

public class SelectAppointmentTimeDialog extends PopupWindow {
	
	private Activity activity;
	
	private int beginDay;
	
	private int lastYear, lastMonth, lastDay, lastHour;
	
	private int selectedYear, selectedMonth, selectedDay, selectedHour; 
	
	private WheelView dayWheelView, hourWheelView;
	
	private Button selectedOk;
	
	private List<String> dayDataList, hourDataList;
	
	private OnSelectedListener onSelectedListener;
	
	public SelectAppointmentTimeDialog(Activity activity, int beginDay, int lastYear, int lastMonth, int lastDay, int lastHour) {
		System.out.println("beginDay : " + beginDay);
		System.out.println("lastYear : " + lastYear);
		System.out.println("lastMonth : " + lastMonth);
		System.out.println("lastDay : " + lastDay);
		System.out.println("lastHour : " + lastHour);
		this.activity = activity;
		this.beginDay = beginDay;
		this.lastYear = lastYear;
		this.lastMonth = lastMonth;
		this.lastDay = lastDay;
		this.lastHour = lastHour;
		this.selectedYear = lastYear;
		this.selectedMonth = lastMonth;
		this.selectedDay = lastDay;
		this.selectedHour = lastHour;
		init();     
	}
	
	public void show(View parent) {
		showAtLocation(parent, Gravity.NO_GRAVITY, 0, 0);
	}
	
	private void init() {
		View view = LayoutInflater.from(activity).inflate(R.layout.dialog_selectappointmenttime, null);
		dayWheelView = (WheelView) view.findViewById(R.id.selectappointmenttime_picker_day_view);
		hourWheelView = (WheelView) view.findViewById(R.id.selectappointmenttime_picker_hour_view);
		selectedOk = (Button) view.findViewById(R.id.selectappointmenttime_but_ok);
		RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.selectappointmenttime_layout);
		LinearLayout chidLayout = (LinearLayout) view.findViewById(R.id.selectappointmenttime_child_layout);
		dayDataList = new ArrayList<String>();
		hourDataList = new ArrayList<String>();
		//初始化值
		setDate();
		//日轮
		dayWheelView.setOnWheelViewListener(new OnWheelViewListener() {
			@Override
			public void onSelected(int selectedIndex, String item) {
				System.out.println("---C-->" + selectedDay);
				selectedDay = Integer.parseInt(item);
				hourDataList.clear();
				System.out.println("-------"+selectedDay+"--------->>>"+lastDay);
				if(selectedDay != lastDay) {
					for(int i = 1;i <= 23; i++) {
						hourDataList.add(String.valueOf(i));
					}
				} else {
					for(int i = lastHour; i <= 23; i++) {
						hourDataList.add(String.valueOf(i));
					}
				}
				hourWheelView.setOffset(2);
				hourWheelView.setItems(hourDataList);
				hourWheelView.setSeletion(0);
				selectedHour=Integer.valueOf(hourDataList.get(0));
			}
		});
		//时轮
		hourWheelView.setOnWheelViewListener(new OnWheelViewListener() {
			@Override
			public void onSelected(int selectedIndex, String item) {
			
				selectedHour = Integer.parseInt(item);
				System.out.println("------->>"+selectedHour);
			}
		});
		//监听
		selectedOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(onSelectedListener != null) {
					onSelectedListener.onSelected(parseTime());
				}
				dismiss();
			}
		});
		chidLayout.setClickable(true);
		chidLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				
			}
		});
		layout.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				dismiss();
				return false;
			}
		});
		setContentView(view);
		DisplayMetrics dm = new DisplayMetrics();
		//获取屏幕信息
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        int screenHeigh = dm.heightPixels;
		setHeight(screenHeigh);
		setWidth(screenWidth);
		//其他设置
		setBackgroundDrawable(new BitmapDrawable());
		setOutsideTouchable(true);
		setFocusable(true); 
		setAnimationStyle(R.style.DiDiPopupWindowAnimStyle);
	}
	
	private void setDate() {
		dayDataList.clear();
		hourDataList.clear();
		int dayCount = getDay(lastYear, lastMonth);
		for(int i = lastDay; i <= beginDay + 4; i++) {
			int currentDay = i % dayCount;
			if(currentDay == 0) {
				currentDay = dayCount;
			}
			System.out.println("-A-->" + currentDay);
			dayDataList.add(String.valueOf(currentDay));
		}
		for(int i = lastHour; i <= 23; i++) {
			System.out.println("-B-->"+String.valueOf(i));
			hourDataList.add(String.valueOf(i));
		}
		//滚到指定位置
		dayWheelView.setOffset(2);
		dayWheelView.setItems(dayDataList);
		dayWheelView.setSeletion(0);
		hourWheelView.setOffset(2);
		hourWheelView.setItems(hourDataList);
		hourWheelView.setSeletion(0);
		
	}
	
	private int getDay(int year, int month) {
		int day = 0;
		if(month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) {
			day = 31;
		} else if(month == 2) {
			if((year % 100 == 0 && year % 4 == 0) || year % 400 == 0) {
				day = 28;
			} else {
				day = 29;
			}
		} else {
			day = 30;
		}
		return day;
	}
	
	public void setOnSelectedListener(OnSelectedListener onSelectedListener) {
		this.onSelectedListener = onSelectedListener;
	} 
	
	public String parseTime() {
		if(selectedDay < lastDay) {
			selectedMonth ++;
		}
		if(selectedMonth > 12) {
			selectedMonth --;
			selectedYear ++;
		}
		return String.valueOf(selectedYear).substring(2) + parseTwo(String.valueOf(selectedMonth)) + parseTwo(String.valueOf(selectedDay)) + parseTwo(String.valueOf(selectedHour)) + "0000";
	}
	
	public String parseTwo(String one) {
		if(one.length() == 1) {
			return "0" + one;
		}
		return one;
	}
	
}
