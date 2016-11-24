package com.johan.subuwuliu.util;

import com.johan.subuwuliu.R;

import android.content.Context; 
import android.os.Build;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ToastUtil {

	public static void show(Context context, String msg) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			Toast toast = new Toast(context);
			LinearLayout toastView = (LinearLayout) LayoutInflater
					.from(context).inflate(R.layout.view_toast, null);
			TextView tipView = (TextView) toastView
					.findViewById(R.id.toast_tip);
			tipView.setText(msg);
			toast.setView(toastView);
			toast.setDuration(Toast.LENGTH_SHORT);
			toast.show();
		} else {
			Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
		}
	}

	public static void showNetError(Context context) {
		show(context, "请检查网络");
	}

 
}
