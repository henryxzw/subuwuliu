package com.johan.subuwuliu.fragment;

import com.johan.subuwuliu.ManagerCarActivity;
import com.johan.subuwuliu.R;
import com.johan.subuwuliu.dialog.ShowPhotoWindow;
import com.johan.subuwuliu.qiniu.Myupdate;
import com.johan.subuwuliu.util.NetUtil;
import com.johan.subuwuliu.view.RoundImageView;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class ManagerCarGoodDataFragment extends AppFragment implements
		OnClickListener {

	private String carNumberInfo, engineNumberInfo, carTypeInfo, carSizeInfo,
			carPicture1Uri, carPicture2Uri, carPicture3Uri, carPicture4Uri,
			driverLicense1Uri, driverLicense2Uri, carInsuranceUri,
			operationUri, customsSupervisionUri;

	private TextView carNumber, engineNumber, carType, carSize;
	private RoundImageView carPicture1, carPicture2, carPicture3, carPicture4,
			driverLicense1, driverLicense2, carInsurance, operation,
			customsSupervision;

	private ShowPhotoWindow showPhotoWindow;

	@Override
	public int getContentView() {
		// TODO Auto-generated method stub
		return R.layout.fragment_managercargooddata;
	}

	@Override
	public void findId() {
		// TODO Auto-generated method stub
		carNumber = (TextView) layout.findViewById(R.id.managercar_car_number);
		engineNumber = (TextView) layout
				.findViewById(R.id.managercar_engine_number);
		carType = (TextView) layout.findViewById(R.id.managercar_car_type);
		carSize = (TextView) layout.findViewById(R.id.managercar_car_size);
		carPicture1 = (RoundImageView) layout
				.findViewById(R.id.managercar_car_picture_1);
		carPicture2 = (RoundImageView) layout
				.findViewById(R.id.managercar_car_picture_2);
		carPicture3 = (RoundImageView) layout
				.findViewById(R.id.managercar_car_picture_3);
		carPicture4 = (RoundImageView) layout
				.findViewById(R.id.managercar_car_picture_4);
		driverLicense1 = (RoundImageView) layout
				.findViewById(R.id.managercar_driver_license_1);
		driverLicense2 = (RoundImageView) layout
				.findViewById(R.id.managercar_driver_license_2);
		carInsurance = (RoundImageView) layout
				.findViewById(R.id.managercar_car_insurance);
		operation = (RoundImageView) layout
				.findViewById(R.id.managercar_operation);
		customsSupervision = (RoundImageView) layout
				.findViewById(R.id.managercar_customs_supervision);
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub 
		carNumber.setText(carNumberInfo);
		engineNumber.setText(engineNumberInfo);
		carType.setText(carTypeInfo);
		carSize.setText(carSizeInfo);
		NetUtil.displayImageFromUrl(getActivity(), carPicture1, carPicture1Uri);
		NetUtil.displayImageFromUrl(getActivity(), carPicture2, carPicture2Uri);
		NetUtil.displayImageFromUrl(getActivity(), carPicture3, carPicture3Uri);
		NetUtil.displayImageFromUrl(getActivity(), carPicture4, carPicture4Uri);
		NetUtil.displayImageFromUrl(getActivity(), driverLicense1,
				driverLicense1Uri);
		NetUtil.displayImageFromUrl(getActivity(), driverLicense2,
				driverLicense2Uri);
		NetUtil.displayImageFromUrl(getActivity(), carInsurance,
				carInsuranceUri);
		NetUtil.displayImageFromUrl(getActivity(), operation, operationUri);
		NetUtil.displayImageFromUrl(getActivity(), customsSupervision,
				customsSupervisionUri);
		carPicture1.setOnClickListener(this);
		carPicture2.setOnClickListener(this);
		carPicture3.setOnClickListener(this);
		carPicture4.setOnClickListener(this);
		driverLicense1.setOnClickListener(this);
		driverLicense2.setOnClickListener(this);
		carInsurance.setOnClickListener(this);
		operation.setOnClickListener(this);
		customsSupervision.setOnClickListener(this);
	}

	public String getCarNumberInfo() {
		return carNumberInfo;
	}

	public void setCarNumberInfo(String carNumberInfo) {
		this.carNumberInfo = carNumberInfo;
	}

	public String getEngineNumberInfo() {
		return engineNumberInfo;
	}

	public void setEngineNumberInfo(String engineNumberInfo) {
		this.engineNumberInfo = engineNumberInfo;
	}

	public String getCarTypeInfo() {
		return carTypeInfo;
	}

	public void setCarTypeInfo(String carTypeInfo) {
		this.carTypeInfo = carTypeInfo;
	}

	public String getCarSizeInfo() {
		return carSizeInfo;
	}

	public void setCarSizeInfo(String carSizeInfo) {
		this.carSizeInfo = carSizeInfo;
	}

	public String getCarPicture1Uri() {
		return carPicture1Uri;
	}

	public void setCarPicture1Uri(String carPicture1Uri) {
		this.carPicture1Uri = carPicture1Uri;
	}

	public String getCarPicture2Uri() {
		return carPicture2Uri;
	}

	public void setCarPicture2Uri(String carPicture2Uri) {
		this.carPicture2Uri = carPicture2Uri;
	}

	public String getCarPicture3Uri() {
		return carPicture3Uri;
	}

	public void setCarPicture3Uri(String carPicture3Uri) {
		this.carPicture3Uri = carPicture3Uri;
	}

	public String getCarPicture4Uri() {
		return carPicture4Uri;
	}

	public void setCarPicture4Uri(String carPicture4Uri) {
		this.carPicture4Uri = carPicture4Uri;
	}

	public String getDriverLicense1Uri() {
		return driverLicense1Uri;
	}

	public void setDriverLicense1Uri(String driverLicense1Uri) {
		this.driverLicense1Uri = driverLicense1Uri;
	}

	public String getDriverLicense2Uri() {
		return driverLicense2Uri;
	}

	public void setDriverLicense2Uri(String driverLicense2Uri) {
		this.driverLicense2Uri = driverLicense2Uri;
	}

	public String getCarInsuranceUri() {
		return carInsuranceUri;
	}

	public void setCarInsuranceUri(String carInsuranceUri) {
		this.carInsuranceUri = carInsuranceUri;
	}

	public String getOperationUri() {
		return operationUri;
	}

	public void setOperationUri(String operationUri) {
		this.operationUri = operationUri;
	}

	public String getCustomsSupervisionUri() {
		return customsSupervisionUri;
	}

	public void setCustomsSupervisionUri(String customsSupervisionUri) {
		this.customsSupervisionUri = customsSupervisionUri;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.managercar_car_picture_1:
			showPhoto(carPicture1Uri);
			break;
		case R.id.managercar_car_picture_2:
			showPhoto(carPicture2Uri);
			break;
		case R.id.managercar_car_picture_3:
			showPhoto(carPicture3Uri);
			break;
		case R.id.managercar_car_picture_4:
			showPhoto(carPicture4Uri);
			break;
		case R.id.managercar_driver_license_1:
			showPhoto(driverLicense1Uri);
			break;
		case R.id.managercar_driver_license_2:
			showPhoto(driverLicense2Uri);
			break;
		case R.id.managercar_car_insurance:
			showPhoto(carInsuranceUri);
			break;
		case R.id.managercar_operation:
			showPhoto(operationUri);
			break;
		case R.id.managercar_customs_supervision:
			showPhoto(customsSupervisionUri);
			break;
		default:
			break;
		}
	}

	private void showPhoto(String url) {
		System.out.println("显示：" + url);
		if (showPhotoWindow == null) {
			showPhotoWindow = new ShowPhotoWindow(getActivity());
		}
		showPhotoWindow.show(
				((ManagerCarActivity) getActivity()).getParentView(), url);
	}

}
