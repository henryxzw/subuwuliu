package com.johan.subuwuliu;
 

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

public abstract class AppThemeActivity extends AppActivity {

	private TextView titleView;

	@Override
	public void findThemeId() {
		titleView = (TextView) findViewById(R.id.title_layout_tip);
	}

	@Override
	public void initThemele() {
		// TODO Auto-generated method stub
		titleView.setText(getThemeTitle());
	}

	public void back(View view) {
		// TODO Auto-generated method stub  
		finish();
	}

	@Override
	public int getStatuBarColor() {
		// TODO Auto-generated method stub
		return Color.parseColor("#242736");
	}

	public abstract String getThemeTitle();

}
