package com.johan.subuwuliu.fragment;
 
import java.math.BigDecimal;

import com.johan.subuwuliu.OrderDetailActivity;
import com.johan.subuwuliu.R;
import com.johan.subuwuliu.adapter.PictureAdapter;
import com.johan.subuwuliu.bean.OrderBean.OrderDetailBean;
import com.johan.subuwuliu.dialog.Call_And_Send; 
import com.johan.subuwuliu.util.FormatUtil; 
import com.johan.subuwuliu.view.OrderWayLayout;
import com.johan.subuwuliu.view.ScrollGridView;
import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint; 
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener; 
import android.widget.LinearLayout;
import android.widget.TextView;

@SuppressLint("ValidFragment")
public class OrderDetailFragment extends AppFragment {

	private TextView orderNumber, takegoodsTime, contactPhone, contactName,
			extraRequest, goodType, extraInfo, remark, price;

	private OrderWayLayout orderWayLayout;

	private LinearLayout call;

	private ScrollGridView carListView;

	private OrderDetailBean orderDetail;

	private LinearLayout gone_a1, linear_aa1, linear_aa2, linear_aa3;

	@SuppressLint("ValidFragment")
	public OrderDetailFragment(OrderDetailBean orderDetail) {
		this.orderDetail = orderDetail;
	}

	@Override
	public int getContentView() {
		// TODO Auto-generated method stub
		return R.layout.fragment_orderdetail;
	}

	@Override
	public void findId() {
		// TODO Auto-generated method stub
		orderNumber = (TextView) layout
				.findViewById(R.id.orderdetail_ordernumber);
		takegoodsTime = (TextView) layout
				.findViewById(R.id.orderdetail_takegood_time);
		contactPhone = (TextView) layout
				.findViewById(R.id.orderdetail_contact_phone);
		contactName = (TextView) layout
				.findViewById(R.id.orderdetail_contact_name);
		call =  (LinearLayout) layout.findViewById(R.id.orderdetail_BBcall);
		extraRequest = (TextView) layout
				.findViewById(R.id.orderdetail_extra_request);
		goodType = (TextView) layout.findViewById(R.id.orderdetail_good_type);
		extraInfo = (TextView) layout.findViewById(R.id.orderdetail_extra_info);
		remark = (TextView) layout.findViewById(R.id.orderdetail_remark);
		carListView = (ScrollGridView) layout
				.findViewById(R.id.orderdetail_car_list);
		price = (TextView) layout.findViewById(R.id.orderdetail_price);
		orderWayLayout = (OrderWayLayout) layout
				.findViewById(R.id.orderdetail_way_layout);
		gone_a1 = (LinearLayout) layout.findViewById(R.id.gone_a1);
		linear_aa1 = (LinearLayout) layout.findViewById(R.id.linear_aa1);
		linear_aa2 = (LinearLayout) layout.findViewById(R.id.linear_aa2);
		linear_aa3 = (LinearLayout) layout.findViewById(R.id.linear_aa3);
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		if (orderDetail.order_no.equals("")) {
			gone_a1.setVisibility(View.GONE);
			return;
		}
		orderNumber.setText("订单号：" + orderDetail.order_no);
		takegoodsTime.setText("装货时间："
				+ FormatUtil.formatTime(orderDetail.use_time,
						"MM月dd日 HH:mm EEEE"));
		// 路线
		orderWayLayout.add(OrderWayLayout.WAY_BEGIN, orderDetail.address_start,
				orderDetail.address_start_contact);
		orderWayLayout.add(OrderWayLayout.WAY_MIDDLE,
				orderDetail.address_midway1,
				orderDetail.address_midway1_contact);
		orderWayLayout.add(OrderWayLayout.WAY_MIDDLE,
				orderDetail.address_midway2,
				orderDetail.address_midway2_contact);
		orderWayLayout.add(OrderWayLayout.WAY_MIDDLE,
				orderDetail.address_midway3,
				orderDetail.address_midway3_contact);
		orderWayLayout.add(OrderWayLayout.WAY_MIDDLE,
				orderDetail.address_midway4,
				orderDetail.address_midway4_contact);
		orderWayLayout.add(OrderWayLayout.WAY_MIDDLE,
				orderDetail.address_midway5,
				orderDetail.address_midway5_contact);
		orderWayLayout.add(OrderWayLayout.WAY_END, orderDetail.address_end,
				orderDetail.address_end_contact);
		// 联系人
		contactName.setText(orderDetail.contacts_name);
		contactPhone.setText(orderDetail.contacts_mobile);
		call.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				startActivity(new Intent(getActivity(), ChatActivity.class)
//						.putExtra("userId", orderDetail.contacts_mobile));
				Call_And_Send call_send = new Call_And_Send(getActivity(),
						orderDetail.contacts_mobile);
				call_send.showAtLocation(layout.findViewById(R.id.layout_a),
						Gravity.BOTTOM, 0, 0);
			}
		});
		if (orderDetail.extra_request.replace(",", " ").equals("")) {
			linear_aa1.setVisibility(View.GONE);
		}
		// 额外需求
		extraRequest.setText(orderDetail.extra_request.replace(",", " "));
		// 货物种类
		if (orderDetail.goods_type.equals("")) {
			linear_aa2.setVisibility(View.GONE);
		}
		goodType.setText(orderDetail.goods_type);
		// 额外信息
		if (FormatUtil.formatExtraInfo(orderDetail).equals("")) {
			linear_aa3.setVisibility(View.GONE);
		}
		extraInfo.setText(FormatUtil.formatExtraInfo(orderDetail));
		// 备注
		if (orderDetail.remark.equals("")) {
			remark.setVisibility(View.GONE);
		}
		remark.setText("备注：" + orderDetail.remark);
		// 车辆图片
		if (orderDetail.img_url.equals("")) {
			carListView.setVisibility(View.GONE);
		}
		OrderDetailActivity activity = (OrderDetailActivity) getActivity();
		PictureAdapter adapter = new PictureAdapter(getActivity(),
				FormatUtil.formatPicture(orderDetail.img_url),
				activity.getParentView());
		carListView.setAdapter(adapter);
		
		double jiage = Double.valueOf(orderDetail.trip_amount)
				+ Double.valueOf(orderDetail.request_amount)
				+ Double.valueOf(orderDetail.carry_amount);
		double shoyi=jiage-(jiage*(Double.valueOf(orderDetail.spvan_quota)));
		// 价格
		String account = "本程收益" + String.valueOf(new BigDecimal(shoyi).setScale(0, BigDecimal.ROUND_HALF_UP)) + "元";
		int accountLength = account.length();
		Spannable accontContent = new SpannableString(account);
		int accountSize = getResources().getDimensionPixelOffset(
				R.dimen.orderdetail_price_text_size);
		accontContent.setSpan(new AbsoluteSizeSpan(accountSize), 4,
				accountLength - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		price.setText(accontContent);
	}
	public void onResume() {
	    super.onResume();
	    MobclickAgent.onPageStart("OrderDetailFragment"); //统计页面，"MainScreen"为页面名称，可自定义
	}
	public void onPause() {
	    super.onPause();
	    MobclickAgent.onPageEnd("OrderDetailFragment"); 
	}
}
