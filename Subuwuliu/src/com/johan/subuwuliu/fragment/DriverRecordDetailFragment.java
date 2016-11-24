package com.johan.subuwuliu.fragment;

import com.johan.subuwuliu.DriverRecordDetailActivity;
import com.johan.subuwuliu.R;
import com.johan.subuwuliu.adapter.PictureAdapter;
import com.johan.subuwuliu.bean.DriverDetailBean.DriverDetailInfoBean;
import com.johan.subuwuliu.dialog.Call_And_Send;
import com.johan.subuwuliu.util.FormatUtil;
import com.johan.subuwuliu.view.ScrollGridView;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener; 
import android.widget.LinearLayout;
import android.widget.TextView;

public class DriverRecordDetailFragment extends AppFragment {

	private TextView contactPhone, contactName, extraRequest, goodType,
			extraInfo, remark, price;

	private LinearLayout call;

	private ScrollGridView carListView;

	private DriverDetailInfoBean driverDetailInfo;

	private LinearLayout linear_orderA1, linear_orderA2, linear_orderA3;

	public void setData(DriverDetailInfoBean driverDetailInfo) {
		this.driverDetailInfo = driverDetailInfo;
	}

	@Override
	public int getContentView() {
		// TODO Auto-generated method stub
		return R.layout.fragment_driverrecorddetail;
	}

	@Override
	public void findId() {
		// TODO Auto-generated method stub
		contactPhone = (TextView) layout
				.findViewById(R.id.driverrecorddetail_contact_phone);
		contactName = (TextView) layout
				.findViewById(R.id.driverrecorddetail_contact_name);
		call =  (LinearLayout) layout.findViewById(R.id.driverrecorddetail_AAcall);
		extraRequest = (TextView) layout
				.findViewById(R.id.driverrecorddetail_extra_request);
		goodType = (TextView) layout
				.findViewById(R.id.driverrecorddetail_good_type);
		extraInfo = (TextView) layout
				.findViewById(R.id.driverrecorddetail_extra_info);
		remark = (TextView) layout.findViewById(R.id.driverrecorddetail_remark);
		carListView = (ScrollGridView) layout
				.findViewById(R.id.driverrecorddetail_car_list);
		price = (TextView) layout.findViewById(R.id.driverrecorddetail_price);
		linear_orderA1 = (LinearLayout) layout
				.findViewById(R.id.linear_orderA1);
		linear_orderA2 = (LinearLayout) layout
				.findViewById(R.id.linear_orderA2);
		linear_orderA3 = (LinearLayout) layout
				.findViewById(R.id.linear_orderA3);
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		// 联系人
		contactName.setText(driverDetailInfo.contacts_name);
		contactPhone.setText(driverDetailInfo.contacts_mobile);
		call.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) { 
				Call_And_Send call_send = new Call_And_Send(getActivity(),
						driverDetailInfo.contacts_mobile);
				call_send.showAtLocation(layout.findViewById(R.id.layout_a1),
						Gravity.BOTTOM, 0, 0);

			}
		});
		// 额外需求
		if (driverDetailInfo.extra_request.replace(",", " ").equals("")) {
			linear_orderA1.setVisibility(View.GONE);
		}
		extraRequest.setText(driverDetailInfo.extra_request.replace(",", " "));
		// 货物种类
		if (driverDetailInfo.goods_type.equals("")) {
			linear_orderA2.setVisibility(View.GONE);
		}
		goodType.setText(driverDetailInfo.goods_type);
		// 额外信息
		if (FormatUtil.formatExtraInfo(driverDetailInfo).equals("")) {
			linear_orderA3.setVisibility(View.GONE);
		}
		extraInfo.setText(FormatUtil.formatExtraInfo(driverDetailInfo));
		// 备注
		if (driverDetailInfo.remark.equals("")) {
			remark.setVisibility(View.GONE);
		}
		remark.setText("备注：" + driverDetailInfo.remark);
		// 车辆图片
		if (driverDetailInfo.img_url.equals("")) {
			carListView.setVisibility(View.GONE);
		}
		DriverRecordDetailActivity activity = (DriverRecordDetailActivity) getActivity();
		PictureAdapter adapter = new PictureAdapter(getActivity(),
				FormatUtil.formatPicture(driverDetailInfo.img_url),
				activity.getParentView());
		carListView.setAdapter(adapter);
		// 价格
		String account = "本程收益" + driverDetailInfo.order_amount + "元";
		int accountLength = account.length();
		Spannable accontContent = new SpannableString(account);
		int accountSize = getResources().getDimensionPixelOffset(
				R.dimen.orderdetail_price_text_size);
		accontContent.setSpan(new AbsoluteSizeSpan(accountSize), 4,
				accountLength - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		price.setText(accontContent);
	}

}
