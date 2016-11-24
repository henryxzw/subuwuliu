package com.johan.subuwuliu.dialog;

import com.johan.subuwuliu.R;
import com.johan.subuwuliu.util.NetUtil;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.PopupWindow;

public class ShowPhotoWindow extends PopupWindow {
	
	private Activity activity;
	
	private ImageView photoView;
	
	public ShowPhotoWindow(Activity activity) {
		this.activity = activity;
		DisplayMetrics metrics = new DisplayMetrics();  
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics );  
        int screenWidth = metrics.widthPixels;  
        int screenHeight = metrics.heightPixels;  
		View view = LayoutInflater.from(activity).inflate(R.layout.window_showphoto, null);
		photoView = (ImageView) view.findViewById(R.id.showphoto_img); 
		photoView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});;
		setContentView(view);
		setWidth(screenWidth);
		setHeight(screenHeight);
		setBackgroundDrawable(new BitmapDrawable());
		setFocusable(true);
		setAnimationStyle(R.style.DiDiPopupWindowAnimStyle);
	}
	
	public void show(View parentView, String url) {
		NetUtil.displayImageFromUrl(activity, photoView, url);
		showAtLocation(parentView, Gravity.CENTER, 0, 0);
	}
	
}
