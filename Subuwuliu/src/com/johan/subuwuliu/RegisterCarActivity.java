package com.johan.subuwuliu;

import java.util.HashMap;
import java.util.List;

import com.johan.subuwuliu.adapter.GridViewAdapter;
import com.johan.subuwuliu.bean.CarTypeBean.CarTypeDetailBean;
import com.johan.subuwuliu.dialog.OnSelectedListener;
import com.johan.subuwuliu.dialog.SelectCarSizeDialog;
import com.johan.subuwuliu.dialog.SelectCarTypeDialog;
import com.johan.subuwuliu.dialog.SelectPictureWayWindow;
import com.johan.subuwuliu.dialog.ShowPictureWindow;
import com.johan.subuwuliu.handler.UpdateViewHandle;
import com.johan.subuwuliu.util.AllCapTransformationMethod;
import com.johan.subuwuliu.util.ImageUtil;
import com.johan.subuwuliu.util.NetUtil;
import com.johan.subuwuliu.util.ToastUtil;
import com.johan.subuwuliu.util.ToolUtil;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

public class RegisterCarActivity extends AppThemeActivity implements
		OnClickListener, OnCheckedChangeListener {

	private static final int MAX_PICTURE_UPLOAD = 4;

	private GridView carPhoto;
	private GridViewAdapter adapter;

	private EditText carNumber, engineNumber;

	// 选择图片的popupwindow
	private SelectPictureWayWindow selectPictureWayWindow;

	// 显示图片
	private ShowPictureWindow showPictureWindow;

	private SelectCarTypeDialog selectCarTypeDialog;

	// 是否有尾板，单侧门还是双侧们
	private CheckBox carType1, carType2, carType3;
	private boolean isCarType1 = false;
	private boolean isCarType2 = false;
	private boolean isCarType3 = false;
	// 车型id
	private String carTypeId = "0";
	private String tempSelect = "";

	private Button selectCarAttributeButton, nextButton;

	@Override
	public int getContentView() {
		// TODO Auto-generated method stub
		return R.layout.activity_registercar;
	}

	@Override
	public String getThemeTitle() {
		// TODO Auto-generated method stub
		return "车辆注册";
	}

	@Override
	public void findId() {
		// TODO Auto-generated method stub
		carNumber = (EditText) findViewById(R.id.registercar_carnumber);
		engineNumber = (EditText) findViewById(R.id.registercar_enginenumber);
		carType1 = (CheckBox) findViewById(R.id.registercar_type_1);
		carType2 = (CheckBox) findViewById(R.id.registercar_type_2);
		carType3 = (CheckBox) findViewById(R.id.registercar_type_3);
		carPhoto = (GridView) findViewById(R.id.registercar_carphoto);
		selectCarAttributeButton = (Button) findViewById(R.id.registercar_carattribute);
		nextButton = (Button) findViewById(R.id.registercar_but);
		carNumber.setTransformationMethod(new AllCapTransformationMethod());
		engineNumber.setTransformationMethod(new AllCapTransformationMethod());
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		adapter = new GridViewAdapter(RegisterCarActivity.this,
				MAX_PICTURE_UPLOAD);
		carPhoto.setAdapter(adapter);
		carPhoto.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == adapter.getCount() - 1) {
					// 最后一个
					if (selectPictureWayWindow == null) {
						selectPictureWayWindow = new SelectPictureWayWindow(
								RegisterCarActivity.this);
						selectPictureWayWindow.callBack = new UpdateViewHandle() {
							
							@Override
							public void Update(String value) {
								// TODO Auto-generated method stub
								tempSelect = value;
							}
						};
					}
					selectPictureWayWindow.showAtLocation(
							findViewById(R.id.registercar_layout),
							Gravity.BOTTOM, 0, 0);
				} else {
					if (showPictureWindow == null) {
						showPictureWindow = new ShowPictureWindow(
								RegisterCarActivity.this);
					}
					String imgPath = adapter.getDatas().get(position);
					showPictureWindow.setImage(imgPath);
					showPictureWindow.setAdapter(adapter);
					showPictureWindow
							.show(findViewById(R.id.registercar_layout));
				}
			}
		});
		selectCarAttributeButton.setOnClickListener(this);
		nextButton.setOnClickListener(this);
		carType1.setOnCheckedChangeListener(this);
		carType2.setOnCheckedChangeListener(this);
		carType3.setOnCheckedChangeListener(this);
		carType2.setChecked(false);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && (data != null || requestCode==SelectPictureWayWindow.SELECT_IMG_FROM_TAKE)) {
			switch (requestCode) {
			case SelectPictureWayWindow.SELECT_IMG_FROM_TAKE:
				if(!TextUtils.isEmpty(tempSelect))
				{
					adapter.addData(tempSelect);
					tempSelect = "";
				}
				else {
					ToastUtil.show(this, "拍照图片失败");
				}
//				Bundle extras = data.getExtras();
//				if (null != extras) {
//					Bitmap photo = extras.getParcelable("data");
//					String photoPath = ImageUtil.saveBitmap(photo,
//							ImageUtil.SAVE_JPG);
//					if (photoPath == null) {
//						ToastUtil.show(this, "拍照图片失败");
//					}
//					// 添加到adapter
//					adapter.addData(photoPath);
//				}
				break;
			case SelectPictureWayWindow.SELECT_IMG_FROM_PHOTO:
				Uri photoUri = data.getData();
				if (photoUri == null)
					break;
				String photoPath = ImageUtil.getPath(this, photoUri);
				if (photoPath == null) {
					ToastUtil.show(this, "图片选择失败");
					return;
				}
				// 添加到adapter
				adapter.addData(photoPath);
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.registercar_but:
			// 检查
			String carNumberStr = carNumber.getText().toString();
			String engineNumberStr = engineNumber.getText().toString();
			// 去掉所有空格
			carNumberStr = carNumberStr.replaceAll(" +", "");
			engineNumberStr = engineNumberStr.replaceAll(" +", "");
			if (carNumberStr.length() == 0) {
				showToast("请填入车牌号码");
				return;
			}
			if (engineNumberStr.length() == 0) {
				showToast("请输入发动机号码");
				return;
			}
			if ("0".equals(carTypeId)) {
				showToast("请选择车型及规格尺寸");
				return;
			}
			if (adapter.getDatas().size() != 5) {
				showToast("请选择4张车辆照片");
				return;
			}
			// 传送数据
			Bundle nextData = new Bundle();
			nextData.putString("car_number", carNumberStr);
			nextData.putString("engine_number", engineNumberStr);
			nextData.putString("car_type_id", carTypeId);
			nextData.putString("is_type1", isCarType1 ? "1" : "0");
			nextData.putString("is_type2", isCarType2 ? "1" : "0");
			nextData.putString("is_type3", isCarType3 ? "1" : "0");
			// System.out.println("-------尾板-------->>"
			// + String.valueOf(isCarType1 ? "1" : "0") + "------->>"
			// + isCarType1);
			// System.out.println("-------单侧门-------->>"
			// + String.valueOf(isCarType2 ? "1" : "0") + "------->>"
			// + isCarType2);
			// System.out.println("---------双侧门------>>"
			// + String.valueOf(isCarType3 ? "1" : "0") + "------->>"
			// + isCarType3);
			nextData.putString("car_picture_1", adapter.getDatas().get(0));
			nextData.putString("car_picture_2", adapter.getDatas().get(1));
			nextData.putString("car_picture_3", adapter.getDatas().get(2));
			nextData.putString("car_picture_4", adapter.getDatas().get(3));
			goActivity(UploadCertificatesActivity.class, nextData);
			RegisterCarActivity.this.finish();
			break;
		case R.id.registercar_carattribute:
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("key", "car_type");
			String action = ToolUtil.encryptionUrlParams(params);
			String requestCartypeUrl = NetUtil.ORDER_URL + action;
			if (selectCarTypeDialog == null) {
				selectCarTypeDialog = new SelectCarTypeDialog(
						RegisterCarActivity.this, "选择车型", requestCartypeUrl);
				selectCarTypeDialog
						.setOnSelectedListener(new OnSelectedListener() {
							@Override
							public void onSelected(String selectedData) {
								final String selectedCarType = selectedData;
								List<CarTypeDetailBean> detailList = selectCarTypeDialog
										.getCarTypeDetailList(selectedData);
								SelectCarSizeDialog dialog = new SelectCarSizeDialog(
										RegisterCarActivity.this, detailList);
								dialog.setOnSelectedListener(new OnSelectedListener() {
									@Override
									public void onSelected(String selectedData) {
										String[] selectedDataArr = selectedData
												.split("#");
										carTypeId = selectedDataArr[2];
										System.out.println("cartypeid_----->"
												+ carTypeId);
										Message message = updateHandler
												.obtainMessage();
										Bundle data = new Bundle();
										data.putString("show_text",
												selectedCarType + " "
														+ selectedDataArr[0]
														+ " "
														+ selectedDataArr[1]);
										message.setData(data);
										updateHandler.sendMessage(message);
									}
								});
								dialog.show();
							}
						});
			}
			selectCarTypeDialog.show();
			break;
		default:
			break;
		}
	}

	private Handler updateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			String showText = msg.getData().getString("show_text");
			selectCarAttributeButton.setText(showText);
		}
	};

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		case R.id.registercar_type_1:

			isCarType1 = isChecked;

			System.out.println("------isChecked--------->>" + isChecked);
			break;
		case R.id.registercar_type_2:
			isCarType2 = isChecked;
			isCarType3 = false;
			carType3.setChecked(false);
			carType2.setChecked(isChecked);
			break;
		case R.id.registercar_type_3:
			isCarType2 = false;
			isCarType3 = isChecked;
			carType2.setChecked(false);
			carType3.setChecked(isChecked);
			break;
		default:
			break;
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}

}
