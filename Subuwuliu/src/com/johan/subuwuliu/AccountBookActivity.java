package com.johan.subuwuliu;

import java.util.Calendar;
import java.util.HashMap;

import com.johan.subuwuliu.bean.AccountBookBean;
import com.johan.subuwuliu.fragment.AccountBookFragment;
import com.johan.subuwuliu.fragment.WaitingFragment;
import com.johan.subuwuliu.fragment.WaitingFragment.OnLoadListener;
import com.johan.subuwuliu.util.GsonUtil;
import com.johan.subuwuliu.util.NetUtil;
import com.johan.subuwuliu.util.ToolUtil;
import com.johan.subuwuliu.view.AccountBookMonthView;
import com.umeng.analytics.MobclickAgent;

import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AccountBookActivity extends AppThemeActivity implements OnClickListener {
	
	private TextView yearView, totalAccount;
	private AccountBookMonthView monthView;
	
	private ImageView selectTime;
	
	private PopupWindow selectTimeWindow;
	
	private int currentYear, currentMonth;
	
	private boolean isFirstLoad = true;
	
	private RelativeLayout selectTimeLayout;

	@Override
	public String getThemeTitle() {
		// TODO Auto-generated method stub
		return "记账本";
	}

	@Override
	public int getContentView() {
		// TODO Auto-generated method stub
		return R.layout.activity_accountbook;
	}

	@Override
	public void findId() {
		// TODO Auto-generated method stub
		yearView = (TextView) findViewById(R.id.accountbook_current_year);
		monthView = (AccountBookMonthView) findViewById(R.id.accountbook_current_month);
		selectTime = (ImageView) findViewById(R.id.accountbook_select_time);
		totalAccount = (TextView) findViewById(R.id.accountbook_total_account);
		selectTimeLayout = (RelativeLayout) findViewById(R.id.accountbook_select_time_layout);
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		currentYear = calendar.get(Calendar.YEAR);
		currentMonth = calendar.get(Calendar.MONTH) + 1;
		yearView.setText(String.valueOf(currentYear) + "年");
		monthView.setMonth(formatMonth(currentMonth));
		selectTimeLayout.setOnClickListener(this);
		loadData(currentYear, currentMonth);
	}
	
	public void loadData(int year, int month) {
		yearView.setText(String.valueOf(year) + "年");
		monthView.setMonth(formatMonth(month));
		totalAccount.setText("￥0");
		String postYear = String.valueOf(year);
		String postMonth = formatMonth(month);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("key", "account_book");
		params.put("username", getLoginName());
		params.put("pwd", getLoginPassword());
		params.put("date", postYear + postMonth);
		String action = ToolUtil.encryptionUrlParams(params);
		final WaitingFragment waitingFragment = new WaitingFragment(NetUtil.DRIVER_URL + action);
		waitingFragment.load(new OnLoadListener() {
			@Override
			public void onLoadSuccess(String result) {
				// TODO Auto-generated method stub
				AccountBookBean accountBookBean = GsonUtil.jsonToObjct(result, AccountBookBean.class);
				totalAccount.setText("￥" + accountBookBean.amount);
				AccountBookFragment accountBookFragment = new AccountBookFragment(accountBookBean.list);
				showFragment(accountBookFragment);
			}
			@Override
			public void onLoadNoNeedUpdate() {
				// TODO Auto-generated method stub
				
			}
		});
		if(isFirstLoad) {
			addFragment(waitingFragment);
			isFirstLoad = false;
		} else {
			showFragment(waitingFragment);
		}
	}
	
	private String formatMonth(int month) {
		String months = String.valueOf(month);
		if(months.length() == 1) {
			months = "0" + months;
		}
		return months;
	}
	
	private void showSelectTiemWindow() {
		if(selectTimeWindow == null) {
			LinearLayout view = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.layout_accounttime, null);
			selectTimeWindow = new PopupWindow(view, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			selectTimeWindow.setFocusable(true);
			selectTimeWindow.setBackgroundDrawable(new BitmapDrawable());
			selectTimeWindow.setOutsideTouchable(true);
			((TextView) view.findViewById(R.id.accountbook_date_1)).setText(getSelectTime(0));
			((TextView) view.findViewById(R.id.accountbook_date_2)).setText(getSelectTime(1));
			((TextView) view.findViewById(R.id.accountbook_date_3)).setText(getSelectTime(2));
			((TextView) view.findViewById(R.id.accountbook_date_4)).setText(getSelectTime(3));
			((TextView) view.findViewById(R.id.accountbook_date_5)).setText(getSelectTime(4));
			((TextView) view.findViewById(R.id.accountbook_date_1)).setOnClickListener(this);
			((TextView) view.findViewById(R.id.accountbook_date_2)).setOnClickListener(this);
			((TextView) view.findViewById(R.id.accountbook_date_3)).setOnClickListener(this);
			((TextView) view.findViewById(R.id.accountbook_date_4)).setOnClickListener(this);
			((TextView) view.findViewById(R.id.accountbook_date_5)).setOnClickListener(this);
		}
		selectTimeWindow.showAsDropDown(selectTime, -268, 10);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.accountbook_select_time_layout :
			showSelectTiemWindow();
			break;
		case R.id.accountbook_date_1 :
			selectedTime(0);
			break;
		case R.id.accountbook_date_2 :
			selectedTime(1);
			break;
		case R.id.accountbook_date_3 :
			selectedTime(2);
			break;
		case R.id.accountbook_date_4 :
			selectedTime(3);
			break;
		case R.id.accountbook_date_5 :
			selectedTime(4);
			break;
		default:
			break;
		}
	}
	
	private void selectedTime(int offset) {
		int[] selectedDate = getSelectDate(offset);
		loadData(selectedDate[0], selectedDate[1]);
		selectTimeWindow.dismiss();
	}
	
	private String getSelectTime(int offset) {
		int showMonth = currentMonth;
		int showYear = currentYear;
		if(currentMonth <= offset) {
			showMonth += 12;
			showYear -= 1;
		}
		showMonth -= offset;
		return showYear + "年" + formatMonth(showMonth) + "月";
	}
	
	private int[] getSelectDate(int offset) {
		int[] dates = new int[2];
		if(currentMonth <= offset) {
			dates[0] = currentYear - 1;
			dates[1] = currentMonth + 12 - offset;
			return dates;
		}
		dates[0] = currentYear;
		dates[1] = currentMonth - offset;
		return dates;
	}

	@Override
	public int getContainerId() {
		// TODO Auto-generated method stub
		return R.id.accountbook_fragment;
	}
	public void onResume() {
	    super.onResume();
	    MobclickAgent.onResume(this);       //统计时长
	}
	public void onPause() {
	    super.onPause();
	    MobclickAgent.onPause(this);
	}
}
