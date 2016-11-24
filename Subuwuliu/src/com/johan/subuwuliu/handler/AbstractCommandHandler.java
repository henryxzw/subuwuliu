package com.johan.subuwuliu.handler;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.johan.subuwuliu.application.DiDiApplication;

public abstract class AbstractCommandHandler {

	protected SharedPreferences sharedPreferences;
	protected Context context;
	protected String loginName, loginPassword;
	
	public AbstractCommandHandler(Context context) {
		this.context = context;
		this.sharedPreferences = context.getSharedPreferences(DiDiApplication.NAME_OF_SHARED_PREFERENCES, Context.MODE_PRIVATE);
		this.loginName = sharedPreferences.getString(DiDiApplication.KEY_OF_LOGIN_NAME, DiDiApplication.VALUE_UNKOWN);
		this.loginPassword = sharedPreferences.getString(DiDiApplication.KEY_OF_LOGIN_PWD, DiDiApplication.VALUE_UNKOWN);
	}
	
	public abstract void doCommand(Intent intent);
	
}
