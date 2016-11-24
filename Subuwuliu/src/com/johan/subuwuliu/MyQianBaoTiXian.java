package com.johan.subuwuliu;

import java.util.HashMap;

import com.johan.subuwuliu.application.DiDiApplication;
import com.johan.subuwuliu.bean.DriverInfoBean;
import com.johan.subuwuliu.bean.DriverRecordBean;
import com.johan.subuwuliu.bean.QianBaoBean;
import com.johan.subuwuliu.bean.StatusBean;
import com.johan.subuwuliu.dialog.WaitingDialog;
import com.johan.subuwuliu.fragment.DriverRecordFragment;
import com.johan.subuwuliu.fragment.WaitingFragment;
import com.johan.subuwuliu.fragment.WaitingFragment.OnLoadListener;
import com.johan.subuwuliu.util.GsonUtil;
import com.johan.subuwuliu.util.NetUtil;
import com.johan.subuwuliu.util.ToolUtil;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MyQianBaoTiXian extends AppThemeActivity {

	private EditText tixian_name, tixian_kahao, tixian_jine, tixian_mima;
	private DriverInfoBean driverInfo;
	private Button tixian_tijiao;
	private String name, kahao, jine, mima;
	private TextView TextViewh;
	private double money;
	private String time = "";
	private Dialog logoutDialog;

	@Override
	public String getThemeTitle() {
		// TODO Auto-generated method stub
		return "确认提现";
	}

	@Override
	public int getContentView() {
		// TODO Auto-generated method stub
		return R.layout.activity_myqianbaotixian;
	}

	@Override
	public void findId() {
		// TODO Auto-generated method stub
		TextViewh = (TextView) findViewById(R.id.TextViewh);
		tixian_name = (EditText) findViewById(R.id.tixian_name);
		tixian_kahao = (EditText) findViewById(R.id.tixian_kahao);
		tixian_jine = (EditText) findViewById(R.id.tixian_jine);
		tixian_mima = (EditText) findViewById(R.id.tixian_mima);
		tixian_tijiao = (Button) findViewById(R.id.tixian_tijiao);
		TextViewh.setText(getIntent().getStringExtra("money"));
		money = Double.valueOf(getIntent().getStringExtra("money"));

	}

	private void isyc() {
		if (!tixian_name.getText().toString().trim().equals("")
				&& !tixian_kahao.getText().toString().trim().equals("")
				&& !tixian_jine.getText().toString().trim().equals("")
				&& !tixian_mima.getText().toString().trim().equals("")) {
			tixian_tijiao.setEnabled(true);
			tixian_tijiao
					.setBackgroundResource(R.drawable.myappointment_item_but_background);
		} else {
			tixian_tijiao.setBackgroundResource(R.color.qianhuise);
			tixian_tijiao.setEnabled(false);
		}
	}

	private void dilog() {
		tixian_tijiao.setEnabled(true);
		logoutDialog = new Dialog(this, R.style.DiDiDialog);
		logoutDialog.setContentView(R.layout.dilog_qianbao);
		final TextView name_A = (TextView) logoutDialog.findViewById(R.id.name);
		final TextView kahao_A = (TextView) logoutDialog
				.findViewById(R.id.kahao);
		final TextView tixian_A = (TextView) logoutDialog
				.findViewById(R.id.tixian);
		final TextView time_A = (TextView) logoutDialog.findViewById(R.id.time);
		final Button a = (Button) logoutDialog.findViewById(R.id.quren_A);
		final Button b = (Button) logoutDialog.findViewById(R.id.quxiao_A);

		name_A.setText(name);
		kahao_A.setText(kahao);
		tixian_A.setText(jine + "元");
		time_A.setText(time);
		a.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				logoutDialog.dismiss();
				getinto();
			}
		});
		b.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				logoutDialog.dismiss();
			}
		});
		logoutDialog.show();

	}

	public void setTime() {
		tixian_tijiao.setEnabled(false);
		String url =NetUtil.SERVER_IP+"/api/android/driver.ashx?action=";
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("key", "get_server_time");
		String action = ToolUtil.encryptionUrlParams(params);
		NetUtil.getInstance().doGet(url + action,
				new RequestCallBack<String>() {
					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// dismissProgressDialog();// /取消加载框'
						showToast("没有连网络");
						time = "";
					}

					@Override
					public void onSuccess(final ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						String month2 = arg0.result.split("\\ ")[0]
								.split("\\-")[2];
						int a = Integer.valueOf(month2) + 1;
						time = arg0.result.split("\\ ")[0].split("\\-")[0]
								+ "-"
								+ arg0.result.split("\\ ")[0].split("\\-")[1]
								+ "-" + String.valueOf(a) + " "
								+ arg0.result.split("\\ ")[1].split("\\:")[1]
								+ ":"
								+ arg0.result.split("\\ ")[1].split("\\:")[2];
						System.out.println("=======time========>>" + time);
						dilog();
					}
				});
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		System.out.println("---------------------第1个");
		SharedPreferences sharedPreferences = getSharedPreferences(
				DiDiApplication.NAME_OF_SHARED_PREFERENCES,
				Context.MODE_PRIVATE);
		String driverInfoData = sharedPreferences.getString(
				DiDiApplication.KEY_OF_DRIVER_INFO_DATA,
				DiDiApplication.VALUE_UNKOWN);
		System.out.println("---------------------第2个");
		if (DiDiApplication.VALUE_UNKOWN.equals(driverInfoData)) {
			finish();
			return;
		}
		System.out.println("---------------------第3个");
		driverInfo = GsonUtil.jsonToObjct(driverInfoData, DriverInfoBean.class);
		System.out.println("---------------------第4个" + driverInfo.nick_name);
		tixian_name.setText(driverInfo.nick_name);
		System.out.println("---------------------第5个" + driverInfo.nick_name);
		tixian_name.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}

			@Override
			public void afterTextChanged(Editable arg0) {
				isyc();
			}
		});
		tixian_kahao.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}

			@Override
			public void afterTextChanged(Editable arg0) {
				isyc();
			}
		});
		tixian_jine.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}

			@Override
			public void afterTextChanged(Editable arg0) {
				isyc();
			}
		});
		tixian_mima.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}

			@Override
			public void afterTextChanged(Editable arg0) {
				isyc();
			}
		});
		tixian_tijiao.setEnabled(false);
		tixian_tijiao.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				if (tixian_name.getText().toString().trim().equals("")) {
					Toast.makeText(MyQianBaoTiXian.this, "姓名为空",
							Toast.LENGTH_SHORT).show();
					return;
				} else {
					name = tixian_name.getText().toString().trim();
					if (tixian_kahao.getText().toString().trim().equals("")) {
						Toast.makeText(MyQianBaoTiXian.this, "请填账号",
								Toast.LENGTH_SHORT).show();
						return;
					} else {
						if (!checkBankCard(tixian_kahao.getText().toString()
								.trim())) {

							showToast("输入的卡号不合法");
							return;
						}
						kahao = tixian_kahao.getText().toString().trim();
						if (tixian_jine.getText().toString().trim().equals("")
								|| tixian_jine.getText().toString().trim()
										.equals("0")) {
							Toast.makeText(MyQianBaoTiXian.this, "金额不能等于空或0",
									Toast.LENGTH_SHORT).show();
							return;
						} else {
							if (Double.valueOf(tixian_jine.getText().toString()
									.trim()) > money) {
								Toast.makeText(MyQianBaoTiXian.this,
										"输入金额超过最大提现金额", Toast.LENGTH_SHORT)
										.show();
								return;
							} else {
								jine = tixian_jine.getText().toString().trim();
								if (tixian_mima.getText().toString().trim()
										.equals("")) {
									Toast.makeText(MyQianBaoTiXian.this,
											"密码不能为空", Toast.LENGTH_SHORT)
											.show();
									return;
								} else {
									if (tixian_mima.getText().toString().trim()
											.equals(getLoginPassword())) {
										mima = tixian_mima.getText().toString()
												.trim();
										setTime();
									} else {
										Toast.makeText(MyQianBaoTiXian.this,
												"密码不正确", Toast.LENGTH_SHORT)
												.show();
									}

								}

							}
						}
					}
				}
			}
		});
	}

	private void getinto() {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("key", "extract_money");
		params.put("username", getLoginName());
		params.put("pwd", mima);
		params.put("account_number", kahao);// /开户账号
		params.put("account_name", name);// /开户名
		params.put("money", jine);// /提现金额
		String action = ToolUtil.encryptionUrlParams(params);
		final WaitingDialog dialog = new WaitingDialog(this);
		dialog.show(findViewById(R.id.aaaaa));
		NetUtil.getInstance().doGet(NetUtil.DRIVER_URL + action,
				new RequestCallBack<String>() {
					@Override
					public void onFailure(HttpException arg0, String arg1) {
						showToast("没有连网络");
						dialog.dismiss();
					}

					@Override
					public void onSuccess(final ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						System.out.println("司机信息结果：" + arg0.result);
						QianBaoBean loginResult = GsonUtil.jsonToObjct(
								arg0.result, QianBaoBean.class);
						if (loginResult.status.equals("0")) {
							Intent it = new Intent(MyQianBaoTiXian.this,
									Myqianbao_chenggong.class);
							it.putExtra("kahao", kahao);
							it.putExtra("name", name);
							it.putExtra("money", jine);
							startActivity(it);
							MyQianBaoTiXian.this.finish();
							Myqianbao.myqianbao.finish();
						} else {
							Toast.makeText(MyQianBaoTiXian.this,
									loginResult.msg.split("\\：")[1],
									Toast.LENGTH_SHORT).show();
						}
						dialog.dismiss();
					}
				});

	}

	// //判断银行卡是否合法
	public static boolean checkBankCard(String cardId) {
		char bit = getBankCardCheckCode(cardId
				.substring(0, cardId.length() - 1));
		if (bit == 'N') {
			return false;
		}
		return cardId.charAt(cardId.length() - 1) == bit;

	}

	private static char getBankCardCheckCode(String nonCheckCodeCardId) {
		if (nonCheckCodeCardId == null
				|| nonCheckCodeCardId.trim().length() == 0
				|| !nonCheckCodeCardId.matches("\\d+")) {
			// 如果传的不是数据返回N
			return 'N';
		}
		char[] chs = nonCheckCodeCardId.trim().toCharArray();
		int luhmSum = 0;
		for (int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
			int k = chs[i] - '0';
			if (j % 2 == 0) {
				k *= 2;
				k = k / 10 + k % 10;
			}
			luhmSum += k;
		}
		return (luhmSum % 10 == 0) ? '0' : (char) ((10 - luhmSum % 10) + '0');
	}
}
