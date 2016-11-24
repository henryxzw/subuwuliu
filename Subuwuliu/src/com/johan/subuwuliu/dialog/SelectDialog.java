package com.johan.subuwuliu.dialog;

import com.johan.subuwuliu.R;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SelectDialog {

	private Dialog dialog;
	
	private View contentView;
	
	private View waitingView;
	private ImageView waitingIcon;
	private TextView waitingTip;
	private Animation waitingAnimation;
	
	private OnClickOkListener onClickOkListener;
	
	public SelectDialog(Context context, String title, int contentLayout) {
		dialog = new Dialog(context, R.style.DiDiDialog);
		dialog.setContentView(R.layout.dialog_select);
		LinearLayout layout = (LinearLayout) dialog.findViewById(R.id.select_dialog_contentview);
		ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		//添加waiting的view
		waitingView = LayoutInflater.from(context).inflate(R.layout.view_waiting, null);
		waitingIcon = (ImageView) waitingView.findViewById(R.id.waiting_icon);
		waitingAnimation = AnimationUtils.loadAnimation(context, R.anim.anim_waiting);
		waitingIcon.setAnimation(waitingAnimation);
		waitingTip = (TextView) waitingView.findViewById(R.id.waiting_tip); 
		layout.addView(waitingView, params);
		//添加客户的view
		contentView = LayoutInflater.from(context).inflate(contentLayout, null);
		layout.addView(contentView, params);
		//默认不显示waiting的View
		showWaiting(false);
		//设置标题
		TextView titleView = (TextView) dialog.findViewById(R.id.select_dialog_title);
		titleView.setText(title);
		//监听按钮
		Button selectOk = (Button) dialog.findViewById(R.id.select_dialog_but_ok);
		selectOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(onClickOkListener != null) {
					onClickOkListener.clickOk();
				}
				dialog.dismiss();
			}
		});
		Button selectCancel = (Button) dialog.findViewById(R.id.select_dialog_but_cancel);
		selectCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				// TODO Auto-generated method stub
				showWaiting(false);
			}
		});
	}
	
	public void show() {
		dialog.show();
	}
	
	public void showWaiting(boolean isShow) {
		int showWaitingMode = isShow ? View.VISIBLE : View.GONE;
		int showContentMode = isShow ? View.GONE : View.VISIBLE;
		waitingView.setVisibility(showWaitingMode);
		contentView.setVisibility(showContentMode);
		if(isShow) {
			waitingAnimation.start();
		} else {
			waitingAnimation.cancel();
		}
	}
	
	public void showWaitingIcon(boolean isShow) {
		int showMode = isShow ? View.VISIBLE : View.GONE;
		if(isShow) {
			waitingAnimation.start();
		} else {
			waitingAnimation.cancel();
		}
		waitingIcon.setVisibility(showMode);
	}
	
	public void setWaitingTip(String tip) {
		waitingTip.setText(tip);
	}
	
	public View findViewById(int id) {
		return contentView.findViewById(id);
	}
	
	public void setOnClickOkListener(OnClickOkListener onClickOkListener) {
		this.onClickOkListener = onClickOkListener;
	}
	
}
