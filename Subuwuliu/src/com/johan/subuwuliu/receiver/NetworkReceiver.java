package com.johan.subuwuliu.receiver;

import com.johan.subuwuliu.handler.UpdateViewHandle;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkReceiver extends BroadcastReceiver{

	public UpdateViewHandle updateNetHandle;
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		 ConnectivityManager connectivityManager=(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	        NetworkInfo  mobNetInfo=connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
	        NetworkInfo  wifiNetInfo=connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
	       
	        if (!mobNetInfo.isConnected() && !wifiNetInfo.isConnected()) {
	          if(updateNetHandle!=null)
	          {
	        	  updateNetHandle.Update("1");
	          }
	        }else {
	        	if(updateNetHandle!=null)
		          {
		        	  updateNetHandle.Update("0");
		          }
	        }
	}

}
