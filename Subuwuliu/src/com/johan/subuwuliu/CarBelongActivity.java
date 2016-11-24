package com.johan.subuwuliu;

import com.umeng.analytics.MobclickAgent;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class CarBelongActivity extends AppThemeActivity implements OnClickListener {
	
	private Button selectPerson, selectCompany;

	@Override
	public int getContentView() {
		// TODO Auto-generated method stub
		return R.layout.activity_carbelong;
	}
	
	@Override
	public String getThemeTitle() {
		// TODO Auto-generated method stub
		return "车辆归属";
	}

	@Override
	public void findId() {
		// TODO Auto-generated method stub
		selectPerson = (Button) findViewById(R.id.carbelong_item_person_select);
		selectCompany = (Button) findViewById(R.id.carbelong_item_company_select);
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		selectPerson.setOnClickListener(this);
		selectCompany.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.carbelong_item_person_select :
			goActivity(RegisterCarActivity.class);
			break;
		case R.id.carbelong_item_company_select :
			goActivity(ApplicationBindingActivity.class);
			break;
		default:
			break;
		}
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
