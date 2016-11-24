package com.johan.subuwuliu;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Myqianbao_chenggong extends AppThemeActivity {

	private TextView cg_kahao, cg_name, cg_money;
	private Button cg_ok;

	@Override
	public String getThemeTitle() {
		// TODO Auto-generated method stub
		return "确认提现";
	}

	@Override
	public int getContentView() {
		// TODO Auto-generated method stub
		return R.layout.activity_myqianbao_chengong;
	}

	@Override
	public void findId() {
		// TODO Auto-generated method stub
		cg_kahao = (TextView) findViewById(R.id.cg_kahao);
		cg_name = (TextView) findViewById(R.id.cg_name);
		cg_money = (TextView) findViewById(R.id.cg_money);
		cg_ok = (Button) findViewById(R.id.cg_ok);

	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		Intent it = getIntent();
		cg_kahao.setText(it.getStringExtra("kahao"));
		cg_name.setText(it.getStringExtra("name"));
		cg_money.setText(it.getStringExtra("money")+"元");
		cg_ok.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});

	}

}
