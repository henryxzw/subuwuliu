package com.johan.subuwuliu.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.johan.subuwuliu.CommentActivity;
import com.johan.subuwuliu.DriverRecordDetailActivity;
import com.johan.subuwuliu.R;
import com.johan.subuwuliu.application.DiDiApplication;
import com.johan.subuwuliu.bean.DriverRecordBean;
import com.johan.subuwuliu.bean.DriverRecordBean.DriverRecordListBean;
import com.johan.subuwuliu.util.FormatUtil;
import com.johan.subuwuliu.util.GsonUtil;
import com.johan.subuwuliu.util.NetUtil;
import com.johan.subuwuliu.util.ToastUtil;
import com.johan.subuwuliu.util.ToolUtil;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

@SuppressLint("ValidFragment")
public class DriverRecordFragment extends AppFragment {

	private RelativeLayout currentRecordLayout;

	private TextView lishi_yundan, remain, place, contact;

	private List<DriverRecordListBean> driverRecordList = new ArrayList<DriverRecordListBean>();
	private PullToRefreshListView driverRecordListView;

	private DriverRecordAdapter adapter;

	private DriverRecordListBean currentDriverRecord;
	private TextView yundan_A;
	
	private int page = 0,pagesize = 10;
	
	public String username,password;

	public DriverRecordFragment(List<DriverRecordListBean> driverRecordList) {
		for (DriverRecordListBean driverRecord : driverRecordList) {
			if ("1".equals(driverRecord.record_status)) {
				currentDriverRecord = driverRecord;
			} else {
				this.driverRecordList.add(driverRecord);
			}
		}
	}

	@Override
	public int getContentView() {
		// TODO Auto-generated method stub
		return R.layout.fragment_driverrecord;
	}

	@Override
	public void findId() {
		// TODO Auto-generated method stub
		currentRecordLayout = (RelativeLayout) layout
				.findViewById(R.id.driverrecord_current_record);
		remain = (TextView) layout.findViewById(R.id.driverrecord_remain);
		place = (TextView) layout.findViewById(R.id.driverrecord_place);
		contact = (TextView) layout.findViewById(R.id.driverrecord_contact);
		driverRecordListView = (PullToRefreshListView) layout
				.findViewById(R.id.driverrecord_list);
		yundan_A = (TextView) layout.findViewById(R.id.yundan_A);
		lishi_yundan = (TextView) layout.findViewById(R.id.lishi_yundan);
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		if (driverRecordList.size() == 0) {
			driverRecordListView.setVisibility(View.GONE);
			yundan_A.setVisibility(View.VISIBLE);
		} else {
			driverRecordListView.setVisibility(View.VISIBLE);
			yundan_A.setVisibility(View.GONE);
		}
		adapter = new DriverRecordAdapter();
		driverRecordListView.getRefreshableView().setAdapter(adapter);
		//driverRecordListView.setAdapter(adapter);
		initCurrentDriverRecordView();
		InitRefresh();
	}

	private void initCurrentDriverRecordView() {
		if (currentDriverRecord != null) {
			remain.setText("剩" + currentDriverRecord.remain + "个点");
			place.setText(FormatUtil.formatPlace(currentDriverRecord.address));
			String[] contactArr = currentDriverRecord.contact.split("\\|");
			if (contactArr != null && contactArr.length == 2) {
				contact.setText(contactArr[0] + " " + contactArr[1]);
				lishi_yundan.setVisibility(View.VISIBLE);
			}
		} else {
			currentRecordLayout.setVisibility(View.GONE);
			lishi_yundan.setVisibility(View.GONE);
		}
	}
	
	public void InitRefresh()
	{
		//设置刷新时显示的文本
        ILoadingLayout startLayout = driverRecordListView.getLoadingLayoutProxy(true,false);
        startLayout.setPullLabel("正在下拉刷新...");
        startLayout.setRefreshingLabel("正在玩命加载中...");
        startLayout.setReleaseLabel("放开以刷新");
 
 
        ILoadingLayout endLayout = driverRecordListView.getLoadingLayoutProxy(false,true);
        endLayout.setPullLabel("正在上拉加载...");
        endLayout.setRefreshingLabel("正在玩命加载中...");
        endLayout.setReleaseLabel("放开以加载下一页");
        
        driverRecordListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				OnRefresh();
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				
					OnLoadMore();
				
			}
		});
        driverRecordListView.setMode(Mode.BOTH);
        
	}
	
	public void OnRefresh()
	{
		page=1;
		driverRecordListView.setMode(Mode.BOTH);
		GetData();
	}
	
	public void OnLoadMore()
	{
		page++;
		GetData();
	}
	
	public void GetData()
	{
		if(page==1)
		{
			driverRecordList.clear();
		}
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("key", "record_list");
		params.put("username",  username);
		params.put("pwd", password);
		params.put("pageindex", ""+page);
		params.put("pagesize", ""+pagesize);
		params.put("keyword", "");
		String action = ToolUtil.encryptionUrlParams(params);
		NetUtil.getInstance().doGet(NetUtil.DRIVER_URL + action, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				driverRecordListView.onRefreshComplete();
				String status = GsonUtil.getJsonValue(responseInfo.result, "status");
				if("0".equals(status)) {
					DriverRecordBean driverRecordResult = GsonUtil.jsonToObjct(responseInfo.result, DriverRecordBean.class);
			        driverRecordList.addAll(driverRecordResult.list);
			        adapter.notifyDataSetChanged();
			        if(driverRecordResult.totalcount<(page*pagesize))
			        {
			        	driverRecordListView.setMode(Mode.PULL_FROM_START);
			        }
				} else if("200".equals(status)) {
					
				} else {
					
					String msg = GsonUtil.getJsonValue(responseInfo.result, "msg"); 
					ToastUtil.show(getActivity(), msg);
				}
				
			}
			@Override
			public void onFailure(HttpException exception, String info) {
				if(page>1)
				page--;
				ToastUtil.showNetError(getActivity());
				driverRecordListView.onRefreshComplete();
			}
		});
	}
	
	class DriverRecordAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return driverRecordList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return driverRecordList.get(position);
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
						R.layout.item_driverrecord, null);
				viewHolder = new ViewHolder();
				viewHolder.layout = (LinearLayout) convertView
						.findViewById(R.id.item_driverrecord_layout);
				viewHolder.operationNumber = (TextView) convertView
						.findViewById(R.id.item_driverrecord_operation_number);
				viewHolder.useTime = (TextView) convertView
						.findViewById(R.id.item_driverrecord_use_time);
				viewHolder.beginPlace = (TextView) convertView
						.findViewById(R.id.item_driverrecord_begin_place);
				viewHolder.endPlace = (TextView) convertView
						.findViewById(R.id.item_driverrecord_end_place);
				viewHolder.carNumber = (TextView) convertView
						.findViewById(R.id.item_driverrecord_car_number);
				viewHolder.carType = (TextView) convertView
						.findViewById(R.id.item_driverrecord_car_type);
				viewHolder.price = (TextView) convertView
						.findViewById(R.id.item_driverrecord_price);
				viewHolder.comment = (Button) convertView
						.findViewById(R.id.item_driverrecord_comment);
				viewHolder.car_status = (TextView) convertView
						.findViewById(R.id.item_driverrecord_car_status);
				// /item_driverrecord_car_status
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			final DriverRecordListBean driverRecord = driverRecordList
					.get(position);
			// 整个布局
			viewHolder.layout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Bundle data = new Bundle();
					data.putString("yid", driverRecord.yid);
					ToolUtil.goActivity(getActivity(),
							DriverRecordDetailActivity.class, data);
				}
			});
			viewHolder.car_status.setText(driverRecord.car_status);
			// 运单号
			viewHolder.operationNumber.setText("运单号：" + driverRecord.yid);
			// 装货时间
			viewHolder.useTime.setText(FormatUtil.formatTime(
					driverRecord.use_time, "MM-dd HH:mm"));
			// 起点
			viewHolder.beginPlace.setText(FormatUtil
					.formatPlace(driverRecord.address_start));
			// 终点
			viewHolder.endPlace.setText(FormatUtil
					.formatPlace(driverRecord.address_end));
			// 车牌
			viewHolder.carNumber.setText(driverRecord.plate_number);
			// 车型
			viewHolder.carType.setText(FormatUtil
					.formatCarType(driverRecord.car_type));
			// 价格
			viewHolder.price.setText(driverRecord.order_amount + "元");
			// 监听评价
			if ("1".equals(driverRecord.is_comment)) {
				viewHolder.comment.setEnabled(false);
				viewHolder.comment.setText("已评论");
				if (driverRecord.car_status.equals("已取消")) {
					viewHolder.comment.setVisibility(View.GONE);
				} else {
					viewHolder.comment.setVisibility(View.VISIBLE);
				}
			} else {
				viewHolder.comment.setEnabled(true);
				viewHolder.comment.setText("评论");
				if (driverRecord.car_status.equals("已取消")) {
					viewHolder.comment.setVisibility(View.GONE);
				} else {
					viewHolder.comment.setVisibility(View.VISIBLE);
				}
			}
			viewHolder.comment.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					getActivity().finish();
					// 跳转到评论的界面
					Intent intent = new Intent(getActivity(),
							CommentActivity.class);
					intent.putExtra("yid", driverRecord.yid);
					getActivity().startActivity(intent);
				}
			});
			return convertView;
		}

		public class ViewHolder {
			public LinearLayout layout;
			public TextView operationNumber;
			public TextView useTime;
			public TextView beginPlace;
			public TextView endPlace;
			public TextView carNumber;
			public TextView carType;
			public TextView price;
			public Button comment;
			public TextView car_status;
		}

	}

}
