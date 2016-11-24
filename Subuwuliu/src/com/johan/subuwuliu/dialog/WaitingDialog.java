package com.johan.subuwuliu.dialog;

import com.johan.subuwuliu.R;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout.LayoutParams;

public class WaitingDialog extends PopupWindow {
	
	private Activity activity;
	
	private Animation waitingAnimation;
	
	private ImageView waitingIcon;
	
	public WaitingDialog(Activity activity) {
		this.activity = activity;
		View view = LayoutInflater.from(activity).inflate(R.layout.dialog_waiting, null);
		waitingIcon = (ImageView) view.findViewById(R.id.waiting_dialog_icon);
		setContentView(view);
		setWidth(activity.getResources().getDimensionPixelOffset(R.dimen.waiting_dialog_size));
		setHeight(activity.getResources().getDimensionPixelOffset(R.dimen.waiting_dialog_size));
		//在PopupWindow里面就加上下面代码，让键盘弹出时，不会挡住pop窗口。  
		setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);           
		setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);  
		//点击空白处时，隐藏掉pop窗口  
		setFocusable(true);
		setTouchable(false);////点击以外的部分，加载消失
		setBackgroundDrawable(new BitmapDrawable());  	
		setAnimationStyle(R.style.DiDiPopupWindowAnimStyle);
		
	}
	
	public void show(View parentView) {
		backgroundAlpha(0.5f);
		waitingAnimation = AnimationUtils.loadAnimation(activity, R.anim.anim_waiting);
		waitingIcon.setAnimation(waitingAnimation);
		waitingAnimation.start();
		showAtLocation(parentView, Gravity.CENTER, 0, 0);
	}
	
	/** 
     * 设置添加屏幕的背景透明度 
     * @param bgAlpha 
     */  
    public void backgroundAlpha(float bgAlpha) {  
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();  
        lp.alpha = bgAlpha; //0.0-1.0  
        activity.getWindow().setAttributes(lp);  
    }  
    
    @Override
    public void dismiss() {
    	// TODO Auto-generated method stub
    	super.dismiss();
    	backgroundAlpha(1f);  
    	waitingAnimation.cancel();
    }
	
}
