package com.johan.subuwuliu.server;

import java.io.FileDescriptor;
import java.util.HashMap;

import cn.jpush.a.a.ar;

import com.hp.hpl.sparta.xpath.ThisNodeTest;
import com.johan.subuwuliu.HomeActivity;
import com.johan.subuwuliu.R;
import com.johan.subuwuliu.application.DiDiApplication;
import com.johan.subuwuliu.method.MethodConfig;
import com.johan.subuwuliu.util.NetUtil;
import com.johan.subuwuliu.util.ToolUtil;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import android.R.bool;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class MusicBService extends Service{

	private MediaPlayer mediaPlayer;
	int count = 0;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		mediaPlayer = MediaPlayer.create(this, R.raw.silent);
		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
//			Toast.makeText(MusicBService.this, "播放完音乐了", Toast.LENGTH_LONG).show();
				boolean isRestart = false; //判断是否重启了服务
			if (HomeActivity.trace == null
					&& HomeActivity.client == null) {
				HomeActivity.startfu();
				isRestart = true;
				count++;
			}
			
			if (MethodConfig.hasTrack) {
//			    Toast.makeText(MusicBService.this, "有单上传轨迹", Toast.LENGTH_LONG).show();
				if(isRestart)//当且仅当服务重启了才需要鹰眼服务重新绑定，避免多次重启绑定
				{
				  HomeActivity.startTrace();
				}
			}
			else {
				HomeActivity.stopTrace();
//				Toast.makeText(MusicBService.this, "不存在运营中的单", Toast.LENGTH_LONG).show();
			}
			mediaPlayer.start();
			if(count>=18)
			{
			  HeartBeat();
			}
			}
		});
		HeartBeat();
//		Toast.makeText(this, "时间"+mediaPlayer.getDuration(), Toast.LENGTH_LONG).show();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		if(mediaPlayer!=null)
		mediaPlayer.start();
//		Toast.makeText(this, "开始播放音乐", Toast.LENGTH_LONG).show();
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		if(mediaPlayer!=null && mediaPlayer.isPlaying())
		mediaPlayer.stop();
		//MethodConfig.ShowToast("后台服务关闭");
		super.onDestroy();
	}
	
	public  void HeartBeat()
    {
		SharedPreferences sharedPreferences_C = getSharedPreferences("sijizhangtai",
						Context.MODE_PRIVATE);
	    String driverStatus = 	sharedPreferences_C.getString("sijizhangtai_a", "");
		if (driverStatus.equals("online") || MethodConfig.hasTrack) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("key", "heartbeat");
		SharedPreferences sharedPreferences =  getSharedPreferences(
				DiDiApplication.NAME_OF_SHARED_PREFERENCES, MODE_PRIVATE);
		String loginName = sharedPreferences
				.getString(DiDiApplication.KEY_OF_LOGIN_NAME,
						DiDiApplication.VALUE_UNKOWN);
		params.put("username", loginName);
		
		String action = ToolUtil.encryptionUrlParams(params);
    	NetUtil.getInstance().doGet(NetUtil.DRIVER_URL+action, new RequestCallBack<String>() {
			
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
				Log.e("music heart beat", "心跳包数据："+arg0.result);
			}
			
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}
		});
    }
		else {
			count = 18;
			Log.e("sijixia", "司机未上线");
		}
    }
	
	

}
