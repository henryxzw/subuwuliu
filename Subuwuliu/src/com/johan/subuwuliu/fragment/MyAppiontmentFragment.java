package com.johan.subuwuliu.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.johan.subuwuliu.GoOrderActivity;
import com.johan.subuwuliu.OrderDetailActivity;
import com.johan.subuwuliu.R;
import com.johan.subuwuliu.application.DiDiApplication;
import com.johan.subuwuliu.bean.DriverInfoBean;
import com.johan.subuwuliu.bean.DriverRecordBean;
import com.johan.subuwuliu.bean.MyAppointmentBean;
import com.johan.subuwuliu.bean.MyAppointmentBean.MyAppointmentDetailBean;
import com.johan.subuwuliu.receiver.MyPushReceiver;
import com.johan.subuwuliu.util.FormatUtil;
import com.johan.subuwuliu.util.GsonUtil;
import com.johan.subuwuliu.util.NetUtil;
import com.johan.subuwuliu.util.ToastUtil;
import com.johan.subuwuliu.util.ToolUtil;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class MyAppiontmentFragment extends AppFragment {

	private PullToRefreshListView appointmentListView;

	private List<MyAppointmentDetailBean> appointmentList = new ArrayList<MyAppointmentDetailBean>();

	private AppointmentAdapter adapter;
	private TextView myYuyue_A;
	
	private int page = 0,pagesize = 10;
	public String username,password;
	@Override
	public int getContentView() {
		// TODO Auto-generated method stub
		return R.layout.fragment_myappointment;
	}

	@Override
	public void findId() {
		// TODO Auto-generated method stub
		appointmentListView = (PullToRefreshListView) layout
				.findViewById(R.id.myappointment_list);
		myYuyue_A = (TextView) layout.findViewById(R.id.myYuyue_A);
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		if (appointmentList.size()==0) {
			System.out.println("-----我的预约------没有数据-------->>");
			appointmentListView.setVisibility(View.GONE);
		} else {
			System.out.println("-----我的预约------有数据-------->>");
			appointmentListView.setVisibility(View.VISIBLE);
		}
		adapter = new AppointmentAdapter();
		appointmentListView.getRefreshableView().setAdapter(adapter);
		appointmentListView.getRefreshableView().setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String orderNumber = appointmentList.get(position-1).order_no;
				Bundle data = new Bundle();
				data.putString("order_number", orderNumber);
				ToolUtil.goActivity(getActivity(), OrderDetailActivity.class,
						data);
			}
		});
		InitRefresh();
	}
	
	public void InitRefresh()
	{
		//设置刷新时显示的文本
        ILoadingLayout startLayout = appointmentListView.getLoadingLayoutProxy(true,false);
        startLayout.setPullLabel("正在下拉刷新...");
        startLayout.setRefreshingLabel("正在玩命加载中...");
        startLayout.setReleaseLabel("放开以刷新");
 
 
        ILoadingLayout endLayout = appointmentListView.getLoadingLayoutProxy(false,true);
        endLayout.setPullLabel("正在上拉加载...");
        endLayout.setRefreshingLabel("正在玩命加载中...");
        endLayout.setReleaseLabel("放开以加载下一页");
        
        appointmentListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

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
        appointmentListView.setMode(Mode.BOTH);
	}
	
	public void OnRefresh()
	{
		page=1;
		appointmentList.clear();
		appointmentListView.setMode(Mode.BOTH);
		GetData();
	}
	
	public void OnLoadMore()
	{
		page++;
		GetData();
	}
	
	public void GetData()
	{
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("key", "booking_list");
		params.put("username", username);
		params.put("pwd", password);
		params.put("pageindex", "1");
		params.put("pagesize", "10");
		params.put("keyword", "");
		String action = ToolUtil.encryptionUrlParams(params);
		
		NetUtil.getInstance().doGet(NetUtil.DRIVER_URL + action, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				appointmentListView.onRefreshComplete();
				String status = GsonUtil.getJsonValue(responseInfo.result, "status");
				if("0".equals(status)) {
					MyAppointmentBean appointmentResult = GsonUtil.jsonToObjct(responseInfo.result, MyAppointmentBean.class);
			        appointmentList.addAll(appointmentResult.list);
			        adapter.notifyDataSetChanged();
			        if(appointmentResult.totalcount<(page*pagesize))
			        {
			        	appointmentListView.setMode(Mode.PULL_FROM_START);
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
				appointmentListView.onRefreshComplete();
			}
		});
	}

	public class AppointmentAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return appointmentList.size();
		}

		@Override
		public Object getItem(int position) {

			return appointmentList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder viewHolder;
			if (convertView == null) {
				convertView = LayoutInflater.from(getActivity()).inflate(
						R.layout.item_myappointment, null);
				viewHolder = new ViewHolder();
				viewHolder.operationNumber = (TextView) convertView
						.findViewById(R.id.item_myappointment_operation_number);
				viewHolder.receiverTime = (TextView) convertView
						.findViewById(R.id.item_myappointment_receiver_time);
				viewHolder.doBut = (Button) convertView
						.findViewById(R.id.item_myappointment_do);
				viewHolder.beginPlace = (TextView) convertView
						.findViewById(R.id.item_myappointment_begin_place);
				viewHolder.endPlace = (TextView) convertView
						.findViewById(R.id.item_myappointment_end_place);
				viewHolder.carNumber = (TextView) convertView
						.findViewById(R.id.item_myappointment_car_number);
				viewHolder.carType = (TextView) convertView
						.findViewById(R.id.item_myappointment_car_type);
				viewHolder.price = (TextView) convertView
						.findViewById(R.id.item_myappointment_price);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			final MyAppointmentDetailBean currentMyAppointmentDetail = appointmentList
					.get(position);

			// 订单号
			viewHolder.operationNumber.setText("运单号："
					+ currentMyAppointmentDetail.yid);
			// 接货时间
			viewHolder.receiverTime.setText("接货时间："
					+ FormatUtil.formatTime(
							currentMyAppointmentDetail.use_time,
							"MM月dd日 HH:mm EEEE"));
			// 执行按钮
			viewHolder.doBut.setFocusable(false);
			viewHolder.doBut.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(),
							GoOrderActivity.class);
					intent.putExtra("order_no", currentMyAppointmentDetail.yid);
					getActivity().startActivity(intent);
					// 然后remove出list
					appointmentList.remove(position);
					adapter.notifyDataSetChanged();
					// 发送广播，获取信息预约数量
					Intent sendIntent = new Intent(
							MyPushReceiver.ACTION_ORDER_COUNT);
					getActivity().sendBroadcast(sendIntent);
				}
			});
			// 起点
			viewHolder.beginPlace.setText(FormatUtil
					.formatPlace(currentMyAppointmentDetail.address_start));
			// 终点
			viewHolder.endPlace.setText(FormatUtil
					.formatPlace(currentMyAppointmentDetail.address_end));
			// 车牌
			viewHolder.carNumber
					.setText(currentMyAppointmentDetail.plate_number);
			// 车型
			viewHolder.carType.setText(FormatUtil
					.formatCarType(currentMyAppointmentDetail.car_type));
			// 价格
			viewHolder.price.setText(currentMyAppointmentDetail.order_amount
					+ "元");
			return convertView;
		}

		public class ViewHolder {
			public TextView operationNumber;
			public TextView receiverTime;
			public Button doBut;
			public TextView beginPlace;
			public TextView endPlace;
			public TextView carNumber;
			public TextView carType;
			public TextView price;
		}

	}

	public void setData(List<MyAppointmentDetailBean> appointmentList) {
		this.appointmentList.clear();
		this.appointmentList.addAll(appointmentList);
		onAttach(getActivity());
	}

}
