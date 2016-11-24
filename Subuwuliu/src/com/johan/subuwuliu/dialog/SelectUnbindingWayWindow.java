package com.johan.subuwuliu.dialog;

import com.johan.subuwuliu.R;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

public class SelectUnbindingWayWindow extends PopupWindow implements OnClickListener {
	
	private Activity activity;
	
	private TextView unbindingCar, unbindingCompany, cancel;
	
	private OnSelectedUnbindingWayListener onSelectedUnbindingWayListener;
	
	public SelectUnbindingWayWindow(Activity activity) {
		this.activity = activity;
		init();
	}
	
	private void init() {
		//设置长宽
		setWidth(LayoutParams.MATCH_PARENT);
		setHeight(LayoutParams.WRAP_CONTENT);
		//设置布局
		View layout = LayoutInflater.from(activity.getApplicationContext()).inflate(R.layout.layout_select_unbinding_way, null);
		unbindingCar = (TextView) layout.findViewById(R.id.select_unbinding_way_car);
		unbindingCompany = (TextView) layout.findViewById(R.id.select_unbinding_way_company);
		cancel = (TextView) layout.findViewById(R.id.select_unbinding_way_cancel);
		unbindingCar.setOnClickListener(this);
		unbindingCompany.setOnClickListener(this);
		cancel.setOnClickListener(this);
		setContentView(layout);
		//其他设置
		setBackgroundDrawable(new BitmapDrawable());
		setOutsideTouchable(true);
		setFocusable(true); 
		setAnimationStyle(R.style.DiDiPopupWindowAnimStyle);
	}
	
	public void backgroundAlpha(float bgAlpha)  {  
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();  
        lp.alpha = bgAlpha; //0.0-1.0  
        activity.getWindow().setAttributes(lp);  
    }  
	
	@Override
	public void showAtLocation(View parent, int gravity, int x, int y) {
		// TODO Auto-generated method stub
		super.showAtLocation(parent, gravity, x, y);
		backgroundAlpha(0.5f);
	}
	
	
	@Override
	public void dismiss() {
		// TODO Auto-generated method stub
		super.dismiss();
		backgroundAlpha(1);
	}

	@Override
	public void onClick(View v) {
		dismiss();
		switch (v.getId()) {
		case R.id.select_unbinding_way_car :
			if(onSelectedUnbindingWayListener != null) {
				onSelectedUnbindingWayListener.onSelectedUnbindingbCar();
			}
			break;
		case R.id.select_unbinding_way_company :
			if(onSelectedUnbindingWayListener != null) {
				onSelectedUnbindingWayListener.onSelectedUnbindingCompany();
			}
			break;
		case R.id.select_unbinding_way_cancel :
			break;
		default:
			break;
		}
	}
	
	public void setOnSelectedUnbindingWayListener(OnSelectedUnbindingWayListener onSelectedUnbindingWayListener) {
		this.onSelectedUnbindingWayListener = onSelectedUnbindingWayListener;
	}
	
	public interface OnSelectedUnbindingWayListener {
		void onSelectedUnbindingbCar();
		void onSelectedUnbindingCompany();
	}
	
}
