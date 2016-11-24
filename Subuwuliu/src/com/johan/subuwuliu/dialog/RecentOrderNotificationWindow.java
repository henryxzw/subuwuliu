package com.johan.subuwuliu.dialog;

import com.johan.subuwuliu.AppActivity;
import com.johan.subuwuliu.R;
import com.johan.subuwuliu.util.FormatUtil;

import android.content.Intent;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class RecentOrderNotificationWindow extends AppActivity  {
	
	private TextView time, address;
	private Button leftBut, rightBut;

	@Override
	public int getContentView() {
		// TODO Auto-generated method stub
		return R.layout.window_recentordernotification;
	}

	@Override
	public void findId() {
		// TODO Auto-generated method stub
		time = (TextView) findViewById(R.id.recentordernotification_time);
		address = (TextView) findViewById(R.id.recentordernotification_address);
		leftBut = (Button) findViewById(R.id.recentordernotification_but_left);
		rightBut = (Button) findViewById(R.id.recentordernotification_but_right);
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		//调节Activity的大小
		WindowManager manager = getWindowManager();
		Display display = manager.getDefaultDisplay();
		WindowManager.LayoutParams params = getWindow().getAttributes();
		params.width = display.getWidth();
		params.height = display.getHeight();
		getWindow().setAttributes(params);
		RelativeLayout layout = (RelativeLayout) findViewById(R.id.recentordernotification_layout);
		layout.setPadding(0, getStatusBarHeight(), 0, 0);
		//初始化View
		if(getIntent() == null) {
			onBackPressed();
			return;
		}
		Intent intent = getIntent();
		String hour = intent.getStringExtra("hour");
		String remainTime = intent.getStringExtra("remain_time");
		String orderAddress = intent.getStringExtra("address");
		time.setText("今日" + hour + "接货  剩余" + remainTime);
		address.setText(FormatUtil.formatPlace(orderAddress));
		leftBut.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		rightBut.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		overridePendingTransition(0, R.anim.anim_popupwindow_out);
	}
	
	/**
	 * 获取状态栏高度
	 * @return
	 */
	public int getStatusBarHeight() {  
        int result = 0;  
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");  
        if (resourceId > 0) {  
            result = getResources().getDimensionPixelSize(resourceId);  
        }  
        return result;  
    }  
	
}
