package com.johan.subuwuliu;

import java.util.HashMap;

import com.johan.subuwuliu.bean.QianBaoBean;
import com.johan.subuwuliu.util.GsonUtil;
import com.johan.subuwuliu.util.NetUtil;
import com.johan.subuwuliu.util.ToastUtil;
import com.johan.subuwuliu.util.ToolUtil;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Myqianbao extends AppThemeActivity {

	private TextView qianbao_yue;
	private LinearLayout qianbao_tijiao;
	private String money1 = "0";
	public static Myqianbao myqianbao;

	@Override
	public String getThemeTitle() {
		// TODO Auto-generated method stub
		return "钱包";
	}

	@Override
	public int getContentView() {
		// TODO Auto-generated method stub
		return R.layout.activity_qianbao;
	}

	@Override
	public void findId() {
		// TODO Auto-generated method stub
		myqianbao = this;
		qianbao_yue = (TextView) findViewById(R.id.qianbao_yue);
		qianbao_tijiao = (LinearLayout) findViewById(R.id.qianbao_tijiao);

	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		getinto();
		qianbao_tijiao.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (Double.valueOf(money1) < 100) {
					showToast("提现金额必须大于100元");
					return;
				}
				Intent it = new Intent(Myqianbao.this, MyQianBaoTiXian.class);
				it.putExtra("money", money1);
				startActivity(it);
			}
		});
	}

	private void getinto() {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("key", "getwallet");
		params.put("username", getLoginName());
		params.put("pwd", getLoginPassword());
		String action = ToolUtil.encryptionUrlParams(params);
		NetUtil.getInstance().doGet(NetUtil.DRIVER_URL + action,
				new RequestCallBack<String>() {
					@Override
					public void onFailure(HttpException arg0, String arg1) {
						ToastUtil.showNetError(Myqianbao.this);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						System.out.println("结果---->" + arg0.result);
						QianBaoBean qianbaoBean = GsonUtil.jsonToObjct(
								arg0.result, QianBaoBean.class);
						if (qianbaoBean.status.equals("0")) {
							qianbao_yue.setText(qianbaoBean.money);
							money1 = qianbaoBean.money;
						} else {
							Toast.makeText(Myqianbao.this, qianbaoBean.msg,
									Toast.LENGTH_SHORT).show();
						}
					}
				});

	}

}
