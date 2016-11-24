package com.johan.subuwuliu;

import com.umeng.analytics.MobclickAgent;

public class ChangeCarActivity extends AppThemeActivity {

	@Override
	public String getThemeTitle() {
		// TODO Auto-generated method stub
		return "更改车辆";
	}

	@Override
	public int getContentView() {
		// TODO Auto-generated method stub
		return R.layout.activity_changecar;
	}

	@Override
	public void findId() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this); // 统计时长
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
