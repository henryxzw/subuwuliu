package com.johan.subuwuliu.fragment;

import com.johan.subuwuliu.CarBelongActivity;
import com.johan.subuwuliu.R;
import com.johan.subuwuliu.RegisterCarActivity;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ManagerCarBadDataFragment extends AppFragment {

	private Button but;

	@Override
	public int getContentView() {
		// TODO Auto-generated method stub
		return R.layout.fragment_managercarbaddata;
	}

	@Override
	public void findId() {
		// TODO Auto-generated method stub
		but = (Button) layout.findViewById(R.id.managernocar_but);
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		but.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),
						CarBelongActivity.class);
				getActivity().startActivity(intent);
				getActivity().finish();
			}
		});
	}

}
