package com.johan.subuwuliu;
 
import com.johan.subuwuliu.application.DiDiApplication;
import com.johan.subuwuliu.bean.DriverInfoBean;
import com.johan.subuwuliu.dialog.ShowPhotoWindow;
import com.johan.subuwuliu.qiniu.Myupdate;
import com.johan.subuwuliu.util.GsonUtil;
import com.johan.subuwuliu.util.NetUtil;
import com.johan.subuwuliu.util.ToastUtil;
import com.johan.subuwuliu.view.RoundImageView;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class PersonMessageActivity extends AppThemeActivity {
	
	private TextView driverPhone;
	
	private RoundImageView identification1, identification2, driverLicense;
	
	private ShowPhotoWindow showPhotoWindow;

	@Override
	public String getThemeTitle() {
		// TODO Auto-generated method stub
		return "个人信息";
	}

	@Override
	public int getContentView() {
		// TODO Auto-generated method stub
		return R.layout.activity_personmessage;
	}

	@Override
	public void findId() {
		// TODO Auto-generated method stub
		driverPhone = (TextView) findViewById(R.id.personmessage_phone);
		identification1 = (RoundImageView) findViewById(R.id.personmessage_identification_1);
		identification2 = (RoundImageView) findViewById(R.id.personmessage_identification_2);
		driverLicense = (RoundImageView) findViewById(R.id.personmessage_driver_license);
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		String driverInfoData = getSharedPreferences().getString(DiDiApplication.KEY_OF_DRIVER_INFO_DATA, DiDiApplication.VALUE_UNKOWN);
		if(DiDiApplication.VALUE_UNKOWN.equals(driverInfoData)) {
			ToastUtil.show(this, "没有司机信息");
			finish();
			return;
		}

		final DriverInfoBean driverInfo = GsonUtil.jsonToObjct(driverInfoData, DriverInfoBean.class);
		driverPhone.setText(driverInfo.mobile);
		final String[] identification = driverInfo.identity_card.split("\\|");
		if(null != identification && identification.length == 2) {
			NetUtil.displayImageFromUrl(this, identification1,  Myupdate.DownloadDemo(identification[0]));
			NetUtil.displayImageFromUrl(this, identification2, Myupdate.DownloadDemo(identification[1]));
			final String aaa=Myupdate.DownloadDemo(identification[0]);
			final String bbb=Myupdate.DownloadDemo(identification[1]);
			identification1.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					showPhoto(aaa);
				}
			});
			identification2.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					showPhoto(bbb);
				}
			});
		}
		NetUtil.displayImageFromUrl(this, driverLicense, Myupdate.DownloadDemo(driverInfo.license));
		final String ccc=Myupdate.DownloadDemo(driverInfo.license);
		driverLicense.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showPhoto(ccc);
			}
		});
	}
	
	private void showPhoto(String url) {
		if(showPhotoWindow == null) {
			showPhotoWindow = new ShowPhotoWindow(this);
		}
		showPhotoWindow.show(findViewById(R.id.personmessage_layout), url);
	}

}
