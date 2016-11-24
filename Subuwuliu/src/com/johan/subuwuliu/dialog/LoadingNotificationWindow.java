package com.johan.subuwuliu.dialog;

import com.johan.subuwuliu.AppActivity;
import com.johan.subuwuliu.OrderDetailActivity;
import com.johan.subuwuliu.R;
import com.johan.subuwuliu.bean.TuiSongBean;
import com.johan.subuwuliu.util.FormatUtil;

import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LoadingNotificationWindow extends AppActivity  {
	
	private TextView time, address, contact;
	private Button leftBut, rightBut;

	@Override
	public int getContentView() {
		// TODO Auto-generated method stub
		return R.layout.window_loadingnotification;
	}

	@Override
	public void findId() {
		// TODO Auto-generated method stub
		time = (TextView) findViewById(R.id.loadingnotification_time);
		address = (TextView) findViewById(R.id.loadingnotification_address);
		contact = (TextView) findViewById(R.id.loadingnotification_contact);
		leftBut = (Button) findViewById(R.id.loadingnotification_but_left);
		rightBut = (Button) findViewById(R.id.loadingnotification_but_right);
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
		RelativeLayout layout = (RelativeLayout) findViewById(R.id.loadingnotification_layout);
		layout.setPadding(0, getStatusBarHeight(), 0, 0);
		//初始化View
		if(getIntent() == null) {
			onBackPressed();
			return;
		}
		final TuiSongBean dataBean = (TuiSongBean) getIntent().getSerializableExtra("data_bean");
		if("0".equals(dataBean.order_type)) {
			time.setText("装货时间：马上用车");
		} else {
			time.setText(FormatUtil.formatContact(dataBean.use_time));
		}
		address.setText(FormatUtil.formatPlace(dataBean.address_start));
		contact.setText(dataBean.contacts);
		leftBut.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Bundle data = new Bundle();
				data.putString("order_number", dataBean.order_no);
				goActivity(OrderDetailActivity.class, data);
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
