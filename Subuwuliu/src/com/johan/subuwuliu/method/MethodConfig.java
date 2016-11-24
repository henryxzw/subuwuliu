package com.johan.subuwuliu.method;

import com.johan.subuwuliu.application.DiDiApplication;
import com.johan.subuwuliu.util.ToastUtil;

import android.R.integer;
import android.content.Context;
import android.widget.Toast;

/*
 * create by henryxzw
 */
public class MethodConfig {
	public static boolean hasTrack = false; //是否存在运单（判断是否启动鹰眼轨迹）
	public static long currentTicks = 0;
	
	public static boolean CanBack(int space)
	{
		if(currentTicks==0)
		{
			currentTicks = System.currentTimeMillis();
			return false;
		}
		else {
			long nowTicks = System.currentTimeMillis();
			int time = (int)(nowTicks - currentTicks)/1000;
			currentTicks = nowTicks;
			if(time>=space)
			{
				return false;
			}
		}
		return true;
	}
	
	public static void ShowToast(String msg)
	{
		ToastUtil.show(DiDiApplication.applicationContext, msg);
	}

}
