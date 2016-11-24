package com.johan.subuwuliu;

import java.util.ArrayList;
import java.util.List;

import com.johan.subuwuliu.bean.TiXingBean;
import com.johan.subuwuliu.database.TiXingDatabase;
import com.johan.subuwuliu.receiver.MyPushReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class TiXingActivity extends AppTheme2Activity {

	private ListView listView;
	private List<TiXingBean> list = new ArrayList<TiXingBean>();
	private TiXingAdapter adapter;
	
	private TiXingDatabase database;
	
	@Override
	public String getThemeTitle() {
		// TODO Auto-generated method stub
		return "提醒";
	}

	@Override
	public String getThemeTip() {
		// TODO Auto-generated method stub
		return "清空";
	}

	@Override
	public void clickThemeTip() {
		// TODO Auto-generated method stub
		if(database != null) {
			database.clear();
			list.clear();
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public int getContentView() {
		// TODO Auto-generated method stub
		return R.layout.activity_tixing;
	}

	@Override
	public void findId() {
		// TODO Auto-generated method stub
		listView = (ListView) findViewById(R.id.tixing_list);
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		initData();
		adapter = new TiXingAdapter();
		listView.setAdapter(adapter);
		// 注册广播
		registerReceiver();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver();
		super.onDestroy();
	}
	
	private void initData() {
		database = new TiXingDatabase(this);
		List<TiXingBean> originalData = database.pullAll();
		for(int i = originalData.size()-1; i >= 0; i--) {
			list.add(originalData.get(i));
		}
	}

	class TiXingAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}
		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return list.get(position);
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
			if(convertView == null) {
				convertView = LayoutInflater.from(TiXingActivity.this).inflate(R.layout.item_tixinglist, null);
				viewHolder = new ViewHolder();
				viewHolder.contentView = (TextView) convertView.findViewById(R.id.item_tixinglist_content);
				viewHolder.timeView = (TextView) convertView.findViewById(R.id.item_tixinglist_time);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			TiXingBean tixingBean = list.get(position);
			viewHolder.contentView.setText(tixingBean.content);
			System.out.println("time:" + tixingBean.time);
			viewHolder.timeView.setText(tixingBean.time);
			return convertView;
		}
		public class ViewHolder {
			public TextView contentView;
			public TextView timeView;
		}
	}
	
	private void registerReceiver() {
		IntentFilter filter = new IntentFilter(MyPushReceiver.ACTION_RECEIVER_TIXING);
		registerReceiver(tixingReceiver, filter);
	}
	
	private void unregisterReceiver() {
		unregisterReceiver(tixingReceiver);
	}
	
	private BroadcastReceiver tixingReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String content = intent.getStringExtra("content");
			String time = intent.getStringExtra("time");
			TiXingBean tiXingBean = new TiXingBean(content, time);
			list.add(0, tiXingBean);
			adapter.notifyDataSetChanged();
		}
	};

}
