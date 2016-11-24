package com.johan.subuwuliu;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

public abstract class AppTheme2Activity extends AppActivity {


	private TextView titleView, tipView;

	@Override
	public void findThemeId() {
		titleView = (TextView) findViewById(R.id.title_2_layout_tip);
		tipView = (TextView) findViewById(R.id.title_2_layout_right);
	}

	@Override
	public void initThemele() {
		// TODO Auto-generated method stub
		titleView.setText(getThemeTitle());
		tipView.setText(getThemeTip());
	}
	
	public void back(View view) {
		// TODO Auto-generated method stub
		finish();
	}
	
	public void clickTip(View view) {
		clickThemeTip();
	}
	
	@Override
	public int getStatuBarColor() {
		// TODO Auto-generated method stub
		return Color.parseColor("#242736");
	}
	
	public void showThemeTip(boolean isShow) {
		if(isShow) { 
			tipView.setVisibility(View.VISIBLE); 
		} else {
			tipView.setVisibility(View.GONE);
		}
	}

	public abstract String getThemeTitle();
	
	public abstract String getThemeTip();
	
	public abstract void clickThemeTip();

	
}
