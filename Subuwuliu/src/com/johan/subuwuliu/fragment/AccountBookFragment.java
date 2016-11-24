package com.johan.subuwuliu.fragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.johan.subuwuliu.R;
import com.johan.subuwuliu.bean.AccountBookBean.AccountBookListBean;
import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

@SuppressLint("ValidFragment")
public class AccountBookFragment extends AppFragment {

	private ListView accountListView;
	private AccountBookAdapter adapter;

	private List<AccountBookListBean> accountList;

	public AccountBookFragment(List<AccountBookListBean> accountList) {
		this.accountList = accountList;
	}

	@Override
	public int getContentView() {
		// TODO Auto-generated method stub
		return R.layout.fragment_accountbook;
	}

	@Override
	public void findId() {
		// TODO Auto-generated method stub
		accountListView = (ListView) layout.findViewById(R.id.accountbook_list);
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		adapter = new AccountBookAdapter();
		accountListView.setAdapter(adapter);
	}

	class AccountBookAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return accountList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return accountList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder viewHolder;
			if (convertView == null) {
				convertView = LayoutInflater.from(getActivity()).inflate(
						R.layout.item_accountbook, null);
				viewHolder = new ViewHolder();
				viewHolder.time1 = (TextView) convertView
						.findViewById(R.id.item_accountbook_time_1);
				viewHolder.time2 = (TextView) convertView
						.findViewById(R.id.item_accountbook_time_2);
				viewHolder.tip = (TextView) convertView
						.findViewById(R.id.item_accountbook_tip);
				viewHolder.money = (TextView) convertView
						.findViewById(R.id.item_accountbook_money);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			AccountBookListBean currentAccount = accountList.get(position);
			String[] showTime = formatTime(currentAccount.date);
			viewHolder.time1.setText(showTime[0]);
			viewHolder.time2.setText(showTime[1]);
			viewHolder.tip.setText("运单号：" + currentAccount.title);
			if (Double.valueOf(currentAccount.momeny) < 0) {
				viewHolder.money.setText(currentAccount.momeny);
			} else {
				viewHolder.money.setText("+" + currentAccount.momeny);
			}

			return convertView;
		}

		public class ViewHolder {
			public TextView time1;
			public TextView time2;
			public TextView tip;
			public TextView money;
		}

		public String[] formatTime(String time) {
			String[] formatTimeArr = new String[2];
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(System.currentTimeMillis());
			try {
				String formater = "yyyy-MM-dd HH:mm:ss";
				SimpleDateFormat format = new SimpleDateFormat(formater);
				Date date = format.parse(time);
				long timeMillisecond = date.getTime();
				if (timeMillisecond < System.currentTimeMillis()) {
					long currentMillisecond = System.currentTimeMillis();
					if (currentMillisecond - timeMillisecond < 24 * 60 * 60 * 1000) {
						// 今天
						formatTimeArr[0] = "今天";
						formatTimeArr[1] = format(timeMillisecond, "HH:mm");
					} else if (currentMillisecond - timeMillisecond < 2 * 24
							* 60 * 60 * 1000) {
						// 昨天
						formatTimeArr[0] = "昨天";
						formatTimeArr[1] = format(timeMillisecond, "HH:mm");
					} else if (currentMillisecond - timeMillisecond < 3 * 24
							* 60 * 60 * 1000) {
						// 前天
						formatTimeArr[0] = "前天";
						formatTimeArr[1] = format(timeMillisecond, "HH:mm");
					} else {
						formatTimeArr[0] = format(timeMillisecond, "E");
						formatTimeArr[1] = format(timeMillisecond, "MM-dd");
					}
				} else {
					formatTimeArr[0] = format(timeMillisecond, "E");
					formatTimeArr[1] = format(timeMillisecond, "MM-dd");
				}
			} catch (Exception exception) {
				return formatTimeArr;
			}
			return formatTimeArr;
		}

		private String format(long time, String pattern) {
			SimpleDateFormat needFormat = new SimpleDateFormat(pattern);
			Date date = new Date(time);
			return needFormat.format(date);
		}

	}
	public void onResume() {
	    super.onResume();
	    MobclickAgent.onPageStart("CommentFragment"); //统计页面，"MainScreen"为页面名称，可自定义
	}
	public void onPause() {
	    super.onPause();
	    MobclickAgent.onPageEnd("CommentFragment"); 
	}
}
