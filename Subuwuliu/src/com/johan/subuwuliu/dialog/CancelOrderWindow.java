package com.johan.subuwuliu.dialog;

import com.johan.subuwuliu.AppActivity;
import com.johan.subuwuliu.R;

import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CancelOrderWindow extends AppActivity  {
	
	private TextView yid;
	private Button ok;

	@Override
	public int getContentView() {
		// TODO Auto-generated method stub
		return R.layout.window_cancelorder;
	}

	@Override
	public void findId() {
		// TODO Auto-generated method stub
		yid = (TextView) findViewById(R.id.cancelorder_yid);
		ok = (Button) findViewById(R.id.cancelorder_but_ok);
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
		RelativeLayout layout = (RelativeLayout) findViewById(R.id.cancelorder_layout);
		layout.setPadding(0, getStatusBarHeight(), 0, 0);
		//初始化View
		if(getIntent() == null) {
			onBackPressed();
			return;
		}
		yid.setText("运单号：" + getIntent().getStringExtra("yid"));
		ok.setOnClickListener(new OnClickListener() {
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
