package com.johan.subuwuliu.dialog;

import com.easemob.chatuidemo.activity.ChatActivity;
import com.johan.subuwuliu.R;
import com.johan.subuwuliu.util.ToolUtil;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class Call_And_Send extends PopupWindow implements OnClickListener {

	public static final int SELECT_IMG_FROM_TAKE = 10;
	public static final int SELECT_IMG_FROM_PHOTO = 20;

	private Activity activity;

	private TextView take, photo, cancel;

	private String phone;

	public Call_And_Send(Activity activity, String id) {
		this.activity = activity;
		this.phone = id;
		init();
	}

	private void init() {
		// 设置长宽
		setWidth(LayoutParams.MATCH_PARENT);
		setHeight(LayoutParams.WRAP_CONTENT);
		// 设置布局
		View layout = LayoutInflater.from(activity.getApplicationContext())
				.inflate(R.layout.layout_call_and_send, null);
		take = (TextView) layout.findViewById(R.id.select_picture_way_take1);
		photo = (TextView) layout.findViewById(R.id.select_picture_way_photo1);
		cancel = (TextView) layout
				.findViewById(R.id.select_picture_way_cancel1);
		take.setOnClickListener(this);
		photo.setOnClickListener(this);
		cancel.setOnClickListener(this);
		setContentView(layout);
		// 其他设置
		setBackgroundDrawable(new BitmapDrawable());
		setOutsideTouchable(true);
		setFocusable(true);
		setAnimationStyle(R.style.DiDiPopupWindowAnimStyle);
	}

	public void backgroundAlpha(float bgAlpha) {
		WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
		lp.alpha = bgAlpha; // 0.0-1.0
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

	private void takePicture() {
		ToolUtil.call(activity, phone);
	}

	private void chooseImage() {
		activity.startActivity(new Intent(activity.getApplicationContext(),
				ChatActivity.class).putExtra("userId", phone));
	}

	@Override
	public void onClick(View v) {
		dismiss();
		switch (v.getId()) {
		case R.id.select_picture_way_take1:
			takePicture();
			break;
		case R.id.select_picture_way_photo1:
			chooseImage();
			break;
		case R.id.select_picture_way_cancel1:
			break;
		default:
			break;
		}
	}

}
