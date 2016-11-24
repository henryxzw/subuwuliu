package com.johan.subuwuliu.fragment;

import com.johan.subuwuliu.CarBelongActivity;
import com.johan.subuwuliu.R;
import com.johan.subuwuliu.application.DiDiApplication;
import com.johan.subuwuliu.bean.DriverInfoBean;
import com.johan.subuwuliu.util.FormatUtil;
import com.johan.subuwuliu.util.GsonUtil;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class HomeNoBindingCarFragment extends AppFragment implements
		OnClickListener {

	private TextView timeView, nameView, nobindingTip;

	private Button nobindingBut;

	@Override
	public int getContentView() {
		// TODO Auto-generated method stub
		return R.layout.fragment_home_no_binding_car;
	}

	@Override
	public void findId() {
		// TODO Auto-generated method stub
		timeView = (TextView) layout.findViewById(R.id.home_time);
		nameView = (TextView) layout.findViewById(R.id.home_driver_name);
		nobindingTip = (TextView) layout
				.findViewById(R.id.home_no_binding_car_tip);
		nobindingBut = (Button) layout
				.findViewById(R.id.home_no_binding_car_but);
	}

	@SuppressLint("ResourceAsColor")
	@Override
	public void init() {
		// TODO Auto-generated method stub

		SharedPreferences sharedPreferences = getActivity()
				.getSharedPreferences(
						DiDiApplication.NAME_OF_SHARED_PREFERENCES,
						Context.MODE_PRIVATE);
		String driverInfoData = sharedPreferences.getString(
				DiDiApplication.KEY_OF_DRIVER_INFO_DATA,
				DiDiApplication.VALUE_UNKOWN);
		if (DiDiApplication.VALUE_UNKOWN.equals(driverInfoData)) {
			getActivity().finish();
			return;
		}
		nobindingBut.setOnClickListener(this);
		DriverInfoBean driverDetail = GsonUtil.jsonToObjct(driverInfoData,
				DriverInfoBean.class);
		if (driverDetail != null) {
			timeView.setText(FormatUtil.formatTime(System.currentTimeMillis(),
					"yyyy年MM月dd日  EEEE"));
			nameView.setText(driverDetail.nick_name);
			nobindingTip.setText(driverDetail.car_msg);
			if (driverDetail.car_status.equals("1")) {
				nobindingBut.setText("证件过期或资料未审核");
				nobindingBut.setEnabled(false);
				System.out.println("-------1------>>>证件过期或资料未审核");
				return;
			} else {
				System.out.println("------------->>>绑定车辆"+driverDetail.car_status);
				nobindingBut.setText("绑定车辆");
				return;
			}
		}
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.home_no_binding_car_but:
			Intent intent = new Intent(getActivity(), CarBelongActivity.class);
			getActivity().startActivity(intent);
			break;
		default:
			break;
		}
	}

}
