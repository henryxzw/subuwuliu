package com.johan.subuwuliu;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONObject;

import com.johan.subuwuliu.adapter.GridViewAdapter;
import com.johan.subuwuliu.application.DiDiApplication;
import com.johan.subuwuliu.bean.StatusBean;
import com.johan.subuwuliu.dialog.OnSelectedListener;
import com.johan.subuwuliu.dialog.SelectDateDialog;
import com.johan.subuwuliu.dialog.SelectPictureWayWindow;
import com.johan.subuwuliu.dialog.ShowPictureWindow;
import com.johan.subuwuliu.dialog.WaitingDialog;
import com.johan.subuwuliu.handler.UpdateViewHandle;
import com.johan.subuwuliu.qiniu.Myupdate;
import com.johan.subuwuliu.server.BackgroundServer;
import com.johan.subuwuliu.server.ForegroundServer;
import com.johan.subuwuliu.util.GsonUtil;
import com.johan.subuwuliu.util.ImageUtil;
import com.johan.subuwuliu.util.NetUtil;
import com.johan.subuwuliu.util.NetUtil.UploadFileListener;
import com.johan.subuwuliu.util.ToastUtil;
import com.johan.subuwuliu.util.ToolUtil;
import com.johan.subuwuliu.view.ScrollGridView;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("SimpleDateFormat")
public class UploadCertificatesActivity extends AppThemeActivity implements
		OnClickListener {

	private static final int MODE_DRIVER_LICENSE = 1;
	private static final int MODE_CAR_INSURANCE = 2;
	private static final int MODE_OPERATION = 3;
	private static final int MODE_CUSTOMS_SUPERVISION_BOOK = 4;

	private int selectMode;

	private ScrollGridView driverLicense, carInsurance, operation,
			customsSupervisionBook;

	private GridViewAdapter driverLinceseAdapter, carInsuranceAdapter,
			operationAdapter, customsSupervisionBookAdapter;

	private SelectPictureWayWindow selectPictureWayWindow;

	private ShowPictureWindow showPictureWindow;

	private TextView driverLinceseValidityPeriod, carInsuranceValidityPeriod,
			operationValidityPeriod, customsSupervisionBookValidityPeriod;

	private SelectDateDialog selectDateDialog;

	private CheckBox customsSupervisionBookCheckBox;

	private Button registerCarButton;

	private Dialog shenghe;

	private String month_time;

	private WaitingDialog waitingDialog;
	// 这些都是从RegisterCarActivity传过来的
	private String carNumber, engineNumber, carTypeId, isCarType1, isCarType2,
			isCarType3, carPicture1, carPicture2, carPicture3, carPicture4;
	private String phone, password;
	
	private String tempSelect = "";

	@Override
	public int getContentView() {
		// TODO Auto-generated method stub
		return R.layout.activity_uploadcertificates;
	}

	@Override
	public String getThemeTitle() {
		// TODO Auto-generated method stub
		return "上传证件";
	}

	@Override
	public void findId() {
		// TODO Auto-generated method stub
		driverLicense = (ScrollGridView) findViewById(R.id.uploadcertificates_driver_license);
		carInsurance = (ScrollGridView) findViewById(R.id.uploadcertificates_car_insurance);
		operation = (ScrollGridView) findViewById(R.id.uploadcertificates_operation);
		customsSupervisionBook = (ScrollGridView) findViewById(R.id.uploadcertificates_customs_supervision_book);
		driverLinceseValidityPeriod = (TextView) findViewById(R.id.uploadcertificates_driver_license_validity_period);
		carInsuranceValidityPeriod = (TextView) findViewById(R.id.uploadcertificates_car_insurance_validity_period);
		operationValidityPeriod = (TextView) findViewById(R.id.uploadcertificates_operation_validity_period);
		customsSupervisionBookValidityPeriod = (TextView) findViewById(R.id.uploadcertificates_customs_supervision_book_validity_period);
		customsSupervisionBookCheckBox = (CheckBox) findViewById(R.id.uploadcertificates_customs_supervision_book_checkbox);
		registerCarButton = (Button) findViewById(R.id.uploadcertificates_but);
		setTime();
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		// 得到传过来的数据
		Bundle data = getIntent().getExtras();
		carNumber = data.getString("car_number");
		engineNumber = data.getString("engine_number");
		carTypeId = data.getString("car_type_id");
		isCarType1 = data.getString("is_type1");
		isCarType2 = data.getString("is_type2");
		isCarType3 = data.getString("is_type3");
		carPicture1 = data.getString("car_picture_1");
		carPicture2 = data.getString("car_picture_2");
		carPicture3 = data.getString("car_picture_3");
		carPicture4 = data.getString("car_picture_4");
		// 得到用户名（手机）和密码
		SharedPreferences sharedPreferences = getSharedPreferences(
				DiDiApplication.NAME_OF_SHARED_PREFERENCES, MODE_PRIVATE);
		phone = sharedPreferences.getString(DiDiApplication.KEY_OF_LOGIN_NAME,
				DiDiApplication.VALUE_UNKOWN);
		password = sharedPreferences.getString(
				DiDiApplication.KEY_OF_LOGIN_PWD, DiDiApplication.VALUE_UNKOWN);
		// 初始化
		driverLinceseAdapter = new GridViewAdapter(
				UploadCertificatesActivity.this, 2);
		driverLicense.setAdapter(driverLinceseAdapter);
		driverLicense.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				selectMode = MODE_DRIVER_LICENSE;
				handlerItemClick(position, driverLinceseAdapter);
			}
		});
		carInsuranceAdapter = new GridViewAdapter(
				UploadCertificatesActivity.this, 1);
		carInsurance.setAdapter(carInsuranceAdapter);
		carInsurance.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				selectMode = MODE_CAR_INSURANCE;
				handlerItemClick(position, carInsuranceAdapter);
			}
		});
		operationAdapter = new GridViewAdapter(UploadCertificatesActivity.this,
				1);
		operation.setAdapter(operationAdapter);
		operation.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				selectMode = MODE_OPERATION;
				handlerItemClick(position, operationAdapter);
			}
		});
		customsSupervisionBookAdapter = new GridViewAdapter(
				UploadCertificatesActivity.this, 1);
		customsSupervisionBook.setAdapter(customsSupervisionBookAdapter);
		customsSupervisionBook
				.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						if (customsSupervisionBookCheckBox.isChecked()) {
							selectMode = MODE_CUSTOMS_SUPERVISION_BOOK;
							handlerItemClick(position,
									customsSupervisionBookAdapter);
						} else {
							showToast("请先勾选上，在进行选择图片");
						}
					}
				});
		driverLinceseValidityPeriod.setOnClickListener(this);
		carInsuranceValidityPeriod.setOnClickListener(this);
		operationValidityPeriod.setOnClickListener(this);
		customsSupervisionBookValidityPeriod.setOnClickListener(this);
		registerCarButton.setOnClickListener(this);
		waitingDialog = new WaitingDialog(UploadCertificatesActivity.this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && (data != null || requestCode == SelectPictureWayWindow.SELECT_IMG_FROM_TAKE)) {
			switch (requestCode) {
			case SelectPictureWayWindow.SELECT_IMG_FROM_TAKE:
				if(!TextUtils.isEmpty(tempSelect))
				{
					addImg(tempSelect);
					tempSelect = "";
				}
				else {
					ToastUtil.show(this, "拍照图片失败");
				}
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
				addImg(photoPath);
				break;
			default:
				break;
			}
		}
	}

	private void handlerItemClick(int position, GridViewAdapter adapter) {
		boolean isLastItem = false;
		String imgPath = null;
		if (position == adapter.getCount() - 1) {
			isLastItem = true;
		} else {
			imgPath = adapter.getDatas().get(position);
		}
		if (isLastItem) {
			if (selectPictureWayWindow == null) {
				selectPictureWayWindow = new SelectPictureWayWindow(
						UploadCertificatesActivity.this);
				selectPictureWayWindow.callBack = new UpdateViewHandle() {
					
					@Override
					public void Update(String value) {
						tempSelect = value;
					}
				};
			}
			selectPictureWayWindow.showAtLocation(
					findViewById(R.id.uploadcertificates_layout),
					Gravity.BOTTOM, 0, 0);
		} else {
			if (showPictureWindow == null) {
				showPictureWindow = new ShowPictureWindow(
						UploadCertificatesActivity.this);
			}
			showPictureWindow.setAdapter(adapter);
			showPictureWindow.setImage(imgPath);
			showPictureWindow
					.show(findViewById(R.id.uploadcertificates_layout));
		}
	}

	private void addImg(String imgPath) {
		switch (selectMode) {
		case MODE_DRIVER_LICENSE:
			driverLinceseAdapter.addData(imgPath);
			driverLinceseAdapter.notifyDataSetChanged();
			break;
		case MODE_CAR_INSURANCE:
			carInsuranceAdapter.addData(imgPath);
			carInsuranceAdapter.notifyDataSetChanged();
			break;
		case MODE_OPERATION:
			operationAdapter.addData(imgPath);
			operationAdapter.notifyDataSetChanged();
			break;
		case MODE_CUSTOMS_SUPERVISION_BOOK:
			customsSupervisionBookAdapter.addData(imgPath);
			customsSupervisionBookAdapter.notifyDataSetChanged();
			break;
		default:
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.uploadcertificates_driver_license_validity_period:
			selectMode = MODE_DRIVER_LICENSE;
			break;
		case R.id.uploadcertificates_car_insurance_validity_period:
			selectMode = MODE_CAR_INSURANCE;
			break;
		case R.id.uploadcertificates_operation_validity_period:
			selectMode = MODE_OPERATION;
			break;
		case R.id.uploadcertificates_customs_supervision_book_validity_period:
			if (customsSupervisionBookCheckBox.isChecked()) {
				selectMode = MODE_CUSTOMS_SUPERVISION_BOOK;
			} else {
				showToast("请先勾选上，再选择时间");
				return;
			}
			break;
		case R.id.uploadcertificates_but:
			registerCar();
			return;
		default:
			break;
		}
		showSelectTimeDialog();
	}

	/**
	 * 选择时间Dialog
	 */
	private void showSelectTimeDialog() {
		if (selectDateDialog == null) {
			selectDateDialog = new SelectDateDialog(
					UploadCertificatesActivity.this);
			selectDateDialog.setOnSelectedListener(new OnSelectedListener() {
				@Override
				public void onSelected(String selectedData) {
					DateFormat df = new SimpleDateFormat("yyyy.MM.dd");
					try {
						Date dt1 = df.parse(selectedData);
						Date dt2 = df.parse(month_time);
						if (dt1.getTime() == dt2.getTime()) {
							showToast("选着时间有误");
							return;
						} else if (dt1.getTime() > dt2.getTime()) {
						} else if (dt1.getTime() < dt2.getTime()) {
							showToast("选着时间有误");
							return;
						}
					} catch (Exception exception) {
						exception.printStackTrace();
					}
					switch (selectMode) {
					case MODE_DRIVER_LICENSE:
						driverLinceseValidityPeriod.setText(selectedData);
						break;
					case MODE_CAR_INSURANCE:
						carInsuranceValidityPeriod.setText(selectedData);
						break;
					case MODE_OPERATION:
						operationValidityPeriod.setText(selectedData);
						break;
					case MODE_CUSTOMS_SUPERVISION_BOOK:
						customsSupervisionBookValidityPeriod
								.setText(selectedData);
						break;
					default:
						break;
					}
				}
			});
		} else {
			switch (selectMode) {
			case MODE_DRIVER_LICENSE:
				selectDateDialog.setDate(driverLinceseValidityPeriod.getText()
						.toString());
				break;
			case MODE_CAR_INSURANCE:
				selectDateDialog.setDate(carInsuranceValidityPeriod.getText()
						.toString());
				break;
			case MODE_OPERATION:
				selectDateDialog.setDate(operationValidityPeriod.getText()
						.toString());
				break;
			case MODE_CUSTOMS_SUPERVISION_BOOK:
				selectDateDialog.setDate(customsSupervisionBookValidityPeriod
						.getText().toString());
				break;
			default:
				break;
			}
		}
		selectDateDialog.show();
	}

	// ////获取服务器时间
	public void setTime() {
		String url = NetUtil.SERVER_IP + "/api/android/driver.ashx?action=";
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("key", "get_server_time");
		String action = ToolUtil.encryptionUrlParams(params);
		NetUtil.getInstance().doGet(url + action,
				new RequestCallBack<String>() {
					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// // dismissProgressDialog();
						showToast("网络连接中断");
						month_time = "0000";
					}

					@Override
					public void onSuccess(final ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						if (arg0.result.equals("")) {
							month_time = "0000";
							return;
						}
						month_time = arg0.result.split("\\ ")[0].split("\\-")[0]
								+ "."
								+ arg0.result.split("\\ ")[0].split("\\-")[1]
								+ "."
								+ arg0.result.split("\\ ")[0].split("\\-")[2]; // //2014-4-15
					}
				});
	}

	private String didiCarPicture1, didiCarPicture2, didiCarPicture3,
			didiCarPicture4, didiDriverLicense1, didiDriverLicense2,
			didiCarInsurance, didiOperation;
	private String didiCustomsSupervisionBook = "";
 

	private void registerCar() {
		final WaitingDialog dialog = new WaitingDialog(this);
		dialog.show(findViewById(R.id.uploadcertificates_layout));
		registerCarButton.setEnabled(false);
		// 检查
		if (driverLinceseAdapter.getCount() != 3) {
			showToast("请选择2张行驶证");
			dialog.dismiss();
			registerCarButton.setEnabled(true);
			return;
		}	
		if (carInsuranceAdapter.getCount() != 2) {
			showToast("请选择1张车辆保险");
			dialog.dismiss();
			registerCarButton.setEnabled(true);
			return;
		}
		if (operationAdapter.getCount() != 2) {
			showToast("请选择1张营运证");
			registerCarButton.setEnabled(true);
			dialog.dismiss();
			return;
		}
		if (customsSupervisionBookCheckBox.isChecked()) {
			if (customsSupervisionBookAdapter.getCount() != 2) {
				showToast("请选择1张海关监管簿");
				registerCarButton.setEnabled(true);
				dialog.dismiss();
				return;
			}
		}
		//TODO 简化判断
		if (TextUtils.isEmpty(driverLinceseValidityPeriod.getText()) ||driverLinceseValidityPeriod.getText().equals(month_time)
				|| month_time.equals("0000")) {
			showToast("请选择行驶证的有效期");
			registerCarButton.setEnabled(true);
			dialog.dismiss();
			return;
		}
		if (TextUtils.isEmpty(carInsuranceValidityPeriod.getText()) || carInsuranceValidityPeriod.getText().equals(month_time)
				|| month_time.equals("0000")) {
			showToast("请选择车辆保险的有效期");
			registerCarButton.setEnabled(true);
			dialog.dismiss();
			return;
		}
		if (TextUtils.isEmpty(operationValidityPeriod.getText()) || operationValidityPeriod.getText().equals(month_time)
				|| month_time.equals("0000")) {
			showToast("请选择营运证的有效期");
			registerCarButton.setEnabled(true);
			dialog.dismiss();
			return;
		}
		if (customsSupervisionBookCheckBox.isChecked()) {
			if (TextUtils.isEmpty(customsSupervisionBookValidityPeriod.getText()) || customsSupervisionBookValidityPeriod.getText().equals(
					month_time)
					|| month_time.equals("0000")) {
				showToast("请选择海关管理簿的有效期");
				registerCarButton.setEnabled(true);
				dialog.dismiss();
				return;
			}
		}
		// 取值
		final String driverLinceseValidityDate = driverLinceseValidityPeriod
				.getText().toString();
		final String carInsuranceValidityDate = carInsuranceValidityPeriod
				.getText().toString();
		final String operationValidityDate = operationValidityPeriod.getText()
				.toString();
		final String customsSupervisionBookValidityDate = customsSupervisionBookValidityPeriod
				.getText().toString();
		// 压缩图片
		System.out.println("开始压缩所有图片-----------------------------------");

		// //////////////车辆图片////////////////////////////
		//
		// didiCarPicture1 = getLoginName() + "_" + Myupdate.Timea();
		// ImageUtil.getBitmab(didiCarPicture1,
		// BitmapFactory.decodeFile(carPicture1));
		// didiCarPicture2 = getLoginName() + "_" + Myupdate.Timea();
		// ImageUtil.getBitmab(didiCarPicture2,
		// BitmapFactory.decodeFile(carPicture2));
		// didiCarPicture3 = getLoginName() + "_" + Myupdate.Timea();
		// ImageUtil.getBitmab(didiCarPicture3,
		// BitmapFactory.decodeFile(carPicture3));
		// didiCarPicture4 = getLoginName() + "_" + Myupdate.Timea();
		// ImageUtil.getBitmab(didiCarPicture4,
		// BitmapFactory.decodeFile(carPicture4));
		// // //////行驶证/////////
		// didiDriverLicense1 = getLoginName() + "_" + Myupdate.Timea();
		// ImageUtil.getBitmab(didiDriverLicense1, BitmapFactory
		// .decodeFile(driverLinceseAdapter.getDatas().get(0)));
		// didiDriverLicense2 = getLoginName() + "_" + Myupdate.Timea();
		// ImageUtil.getBitmab(didiDriverLicense2, BitmapFactory
		// .decodeFile(driverLinceseAdapter.getDatas().get(1)));
		// // ////////车辆保险//////////////////////
		// didiCarInsurance = getLoginName() + "_" + Myupdate.Timea();
		// ImageUtil
		// .getBitmab(didiCarInsurance, BitmapFactory
		// .decodeFile(carInsuranceAdapter.getDatas().get(0)));
		// // //////////运营证////////////////////////
		// didiOperation = getLoginName() + "_" + Myupdate.Timea();
		// ImageUtil.getBitmab(didiOperation,
		// BitmapFactory.decodeFile(operationAdapter.getDatas().get(0)));
		// if (customsSupervisionBookCheckBox.isChecked()) {
		//
		// didiCustomsSupervisionBook = getLoginName() + "_"
		// + Myupdate.Timea();
		// ImageUtil.getBitmab(didiCustomsSupervisionBook,
		// BitmapFactory.decodeFile(customsSupervisionBookAdapter
		// .getDatas().get(0)));
		// }
		final HashMap<String, String> uploadFileMap = new HashMap<String, String>();
		uploadFileMap.put("car_picture1", carPicture1);
		uploadFileMap.put("car_picture2", carPicture2);
		uploadFileMap.put("car_picture3", carPicture3);
		uploadFileMap.put("car_picture4", carPicture4);
		uploadFileMap.put("driving_license1", driverLinceseAdapter.getDatas()
				.get(0));
		uploadFileMap.put("driving_license2", driverLinceseAdapter.getDatas()
				.get(1));
		uploadFileMap.put("car_safe", carInsuranceAdapter.getDatas().get(0));
		uploadFileMap.put("taxi_license", operationAdapter.getDatas().get(0));
		if (customsSupervisionBookCheckBox.isChecked()) {
			uploadFileMap.put("customs", customsSupervisionBookAdapter
					.getDatas().get(0));
		} else {
			uploadFileMap.put("customs", "");
		} 
			final UploadManager uploadManager = new UploadManager();
			final String token = Myupdate.Token();
			//////////////////
			uploadManager.put(Myupdate.GetBitmapCommBytes(new File(uploadFileMap.get("car_picture1"))),
					getLoginName() + "_" + Myupdate.Timea(), token,
					new UpCompletionHandler() {
						@Override
						public void complete(String arg0,
								com.qiniu.android.http.ResponseInfo arg1,
								JSONObject arg2) {
							// TODO Auto-generated method stub
							if (arg1.statusCode == 200) {
								System.out.println("------------->>"+arg0);
								didiCarPicture1 = arg0;
								///////////////////////
								uploadManager.put(Myupdate.GetBitmapCommBytes(new File(uploadFileMap.get("car_picture2"))),
										getLoginName() + "_" + Myupdate.Timea(), token,
										new UpCompletionHandler() {
											@Override
											public void complete(String arg0,
													com.qiniu.android.http.ResponseInfo arg1,
													JSONObject arg2) {
												// TODO Auto-generated method stub
												if (arg1.statusCode == 200) {
													System.out.println("------------->>"+arg0);
													didiCarPicture2 = arg0;
													/////////////////////////
													uploadManager.put(Myupdate.GetBitmapCommBytes(new File(uploadFileMap.get("car_picture3"))),
															getLoginName() + "_" + Myupdate.Timea(), token,
															new UpCompletionHandler() {
																@Override
																public void complete(String arg0,
																		com.qiniu.android.http.ResponseInfo arg1,
																		JSONObject arg2) {
																	// TODO Auto-generated method stub
																	if (arg1.statusCode == 200) {
																		System.out.println("------------->>"+arg0);
																		didiCarPicture3 = arg0;
																		/////////////////////////////
																		uploadManager.put(Myupdate.GetBitmapCommBytes(new File(uploadFileMap.get("car_picture4"))),
																				getLoginName() + "_" + Myupdate.Timea(), token,
																				new UpCompletionHandler() {
																					@Override
																					public void complete(String arg0,
																							com.qiniu.android.http.ResponseInfo arg1,
																							JSONObject arg2) {
																						// TODO Auto-generated method stub
																						if (arg1.statusCode == 200) {
																							System.out.println("------------->>"+arg0);
																							didiCarPicture4 = arg0;
																							///////////////////////////////
																							uploadManager.put(Myupdate.GetBitmapCommBytes(new File(uploadFileMap.get("driving_license1"))), 
																									getLoginName() + "_" + Myupdate.Timea(), token,
																									new UpCompletionHandler() {
																										@Override
																										public void complete(String arg0,
																												com.qiniu.android.http.ResponseInfo arg1,
																												JSONObject arg2) {
																											// TODO Auto-generated method stub
																											if (arg1.statusCode == 200) {
																												System.out.println("------------->>"+arg0);
																												didiDriverLicense1 = arg0;
																												uploadManager.put(Myupdate.GetBitmapCommBytes(new File(uploadFileMap.get("driving_license2"))), 
																														getLoginName() + "_" + Myupdate.Timea(), token,
																														new UpCompletionHandler() {
																															@Override
																															public void complete(String arg0,
																																	com.qiniu.android.http.ResponseInfo arg1,
																																	JSONObject arg2) {
																																// TODO Auto-generated method stub
																																if (arg1.statusCode == 200) {
																																	System.out.println("------------->>"+arg0);
																																	didiDriverLicense2 = arg0;
																																	/////////////////////////////////
																																	uploadManager.put(Myupdate.GetBitmapCommBytes(new File(uploadFileMap.get("car_safe"))), 
																																			getLoginName() + "_" + Myupdate.Timea(), token,
																																			new UpCompletionHandler() {
																																				@Override
																																				public void complete(String arg0,
																																						com.qiniu.android.http.ResponseInfo arg1,
																																						JSONObject arg2) {
																																					// TODO Auto-generated method stub
																																					if (arg1.statusCode == 200) {
																																						System.out.println("------------->>"+arg0);
																																						didiCarInsurance = arg0;
																																						/////////////////////////////////
																																						uploadManager.put(Myupdate.GetBitmapCommBytes(new File(uploadFileMap.get("taxi_license"))), 
																																								getLoginName() + "_" + Myupdate.Timea(), token,
																																								new UpCompletionHandler() {
																																									@Override
																																									public void complete(String arg0,
																																											com.qiniu.android.http.ResponseInfo arg1,
																																											JSONObject arg2) {
																																										// TODO Auto-generated method stub
																																										if (arg1.statusCode == 200) {
																																											System.out.println("------------->>"+arg0);
																																											didiOperation = arg0;
																																											if (uploadFileMap.get("customs").equals("")) {
																																												HashMap<String, String> params = new HashMap<String, String>();
																																												params.put("key", "car_reg");
																																												params.put("username", phone);
																																												params.put("pwd", password);
																																												params.put("plate_number", carNumber);
																																												params.put("is_weiban", isCarType1);
																																												params.put("is_dancemen", isCarType2);
																																												params.put("is_shuangcemen", isCarType3);
																																												params.put("is_haiguang",
																																														customsSupervisionBookCheckBox.isChecked() ? "1" : "0");
																																												params.put("engine_number", engineNumber);
																																												params.put("car_type_id", carTypeId);
																																												params.put("car_safe_time",
																																														SelectDateDialog.ToUploadData(carInsuranceValidityDate));
																																												params.put("taxi_license_time",
																																														SelectDateDialog.ToUploadData(operationValidityDate));

																																												params.put("car_picture1", didiCarPicture1);
																																												params.put("car_picture2", didiCarPicture2);
																																												params.put("car_picture3", didiCarPicture3);
																																												params.put("car_picture4", didiCarPicture4);
																																												params.put("driving_license1", didiDriverLicense1);
																																												params.put("driving_license2", didiDriverLicense2);
																																												params.put("car_safe", didiCarInsurance);
																																												params.put("taxi_license", didiOperation);
																																												if (customsSupervisionBookCheckBox.isChecked()) {
																																													params.put("customs", didiCustomsSupervisionBook);
																																												} else {
																																													params.put("customs", "");
																																												}

																																												if (customsSupervisionBookCheckBox.isChecked()) {
																																													params.put("customs_time", SelectDateDialog
																																															.ToUploadData(customsSupervisionBookValidityDate));
																																												} else {
																																													params.put("customs_time", "");
																																												}
																																												params.put("driving_license_time",
																																														SelectDateDialog.ToUploadData(driverLinceseValidityDate));
																																												String action = ToolUtil.encryptionUrlParams(params);
																																												NetUtil.getInstance().doGet(NetUtil.DRIVER_URL + action,
																																														new RequestCallBack<String>() {
																																															@Override
																																															public void onSuccess(ResponseInfo<String> responseInfo) {
																																																System.out.println("注册结果------>" + responseInfo.result);
																																																StatusBean registerResult = GsonUtil.jsonToObjct(
																																																		responseInfo.result, StatusBean.class);
																																																if ("0".equals(registerResult.status)) {
																																																	showToast("注册成功，请等待审核");
																																																	shenghe = new Dialog(
																																																			UploadCertificatesActivity.this,
																																																			R.style.DiDiDialog);
																																																	shenghe.setContentView(R.layout.dengdaishenghe);
																																																	TextView textview = (TextView) shenghe
																																																			.findViewById(R.id.textView_shenghe);
																																																	textview.setOnClickListener(new View.OnClickListener() {
																																																		@Override
																																																		public void onClick(View arg0) {
																																																			// TODO Auto-generated
																																																			// method stub
																																																			returnLoginActivity();
																																																			UploadCertificatesActivity.this.finish();
																																																			shenghe.dismiss();
																																																		}
																																																	});
																																																	shenghe.show();
																																																} else {
																																																	showToast(registerResult.msg);
																																																}
																																																dialog.dismiss();
																																															}

																																															@Override
																																															public void onFailure(HttpException exception, String info) {
																																																System.out.println("--->" + exception.getMessage());
																																																showToast(info);
																																																registerCarButton.setEnabled(true);
																																																dialog.dismiss();
																																															}
																																														});
																																												return;
																																											}
																																											uploadManager.put(Myupdate.GetBitmapCommBytes(new File(uploadFileMap.get("customs"))), 
																																													getLoginName() + "_" + Myupdate.Timea(), token,
																																													new UpCompletionHandler() {
																																														@Override
																																														public void complete(String arg0,
																																																com.qiniu.android.http.ResponseInfo arg1,
																																																JSONObject arg2) {
																																															// TODO Auto-generated method stub
																																															if (arg1.statusCode == 200) {
																																																System.out.println("------------->>"+arg0);
																																																didiCustomsSupervisionBook = arg0;
																																																try {
																																																	HashMap<String, String> params = new HashMap<String, String>();
																																																	params.put("key", "car_reg");
																																																	params.put("username", phone);
																																																	params.put("pwd", password);
																																																	params.put("plate_number", carNumber);
																																																	params.put("is_weiban", isCarType1);
																																																	params.put("is_dancemen", isCarType2);
																																																	params.put("is_shuangcemen", isCarType3);
																																																	params.put("is_haiguang",
																																																			customsSupervisionBookCheckBox.isChecked() ? "1" : "0");
																																																	params.put("engine_number", engineNumber);
																																																	params.put("car_type_id", carTypeId);
																																																	params.put("car_safe_time",
																																																			SelectDateDialog.ToUploadData(carInsuranceValidityDate));
																																																	params.put("taxi_license_time",
																																																			SelectDateDialog.ToUploadData(operationValidityDate));

																																																	params.put("car_picture1", didiCarPicture1);
																																																	params.put("car_picture2", didiCarPicture2);
																																																	params.put("car_picture3", didiCarPicture3);
																																																	params.put("car_picture4", didiCarPicture4);
																																																	params.put("driving_license1", didiDriverLicense1);
																																																	params.put("driving_license2", didiDriverLicense2);
																																																	params.put("car_safe", didiCarInsurance);
																																																	params.put("taxi_license", didiOperation);
																																																	if (customsSupervisionBookCheckBox.isChecked()) {
																																																		params.put("customs", didiCustomsSupervisionBook);
																																																	} else {
																																																		params.put("customs", "");
																																																	}

																																																	if (customsSupervisionBookCheckBox.isChecked()) {
																																																		params.put("customs_time", SelectDateDialog
																																																				.ToUploadData(customsSupervisionBookValidityDate));
																																																	} else {
																																																		params.put("customs_time", "");
																																																	}
																																																	params.put("driving_license_time",
																																																			SelectDateDialog.ToUploadData(driverLinceseValidityDate));
																																																	String action = ToolUtil.encryptionUrlParams(params);
																																																	NetUtil.getInstance().doGet(NetUtil.DRIVER_URL + action,
																																																			new RequestCallBack<String>() {
																																																				@Override
																																																				public void onSuccess(ResponseInfo<String> responseInfo) {
																																																					System.out.println("注册结果------>" + responseInfo.result);
																																																					StatusBean registerResult = GsonUtil.jsonToObjct(
																																																							responseInfo.result, StatusBean.class);
																																																					if ("0".equals(registerResult.status)) {
																																																						showToast("注册成功，请等待审核");
																																																						shenghe = new Dialog(
																																																								UploadCertificatesActivity.this,
																																																								R.style.DiDiDialog);
																																																						shenghe.setContentView(R.layout.dengdaishenghe);
																																																						TextView textview = (TextView) shenghe
																																																								.findViewById(R.id.textView_shenghe);
																																																						textview.setOnClickListener(new View.OnClickListener() {
																																																							@Override
																																																							public void onClick(View arg0) {
																																																								// TODO Auto-generated
																																																								// method stub
																																																								returnLoginActivity();
																																																								UploadCertificatesActivity.this.finish();
																																																								shenghe.dismiss();
																																																							}
																																																						});
																																																						shenghe.show();
																																																					} else {
																																																						showToast(registerResult.msg);
																																																					}
																																																					dialog.dismiss();
																																																				}

																																																				@Override
																																																				public void onFailure(HttpException exception, String info) {
																																																					System.out.println("--->" + exception.getMessage());
																																																					showToast(info);
																																																					registerCarButton.setEnabled(true);
																																																					dialog.dismiss();
																																																				}
																																																			});
																																																} catch (Exception e) {
																																																	// TODO: handle exception
																																																	registerCarButton.setEnabled(true);
																																																	dialog.dismiss();
																																																}
																																															} else if (arg1.statusCode == 401) {
																																																registerCarButton.setEnabled(true);
																																																dialog.dismiss();
																																																return;
																																															} else {
																																																registerCarButton.setEnabled(true);
																																																dialog.dismiss();
																																																return;
																																															}
																																														}
																																													}, null);
																																										} else if (arg1.statusCode == 401) {
																																											registerCarButton.setEnabled(true);
																																											dialog.dismiss();
																																											return;
																																										} else {
																																											registerCarButton.setEnabled(true);
																																											dialog.dismiss();
																																											return;
																																										}
																																									}
																																								}, null);
																																					} else if (arg1.statusCode == 401) {
																																						registerCarButton.setEnabled(true);
																																						dialog.dismiss();
																																						return;
																																					} else {
																																						registerCarButton.setEnabled(true);
																																						dialog.dismiss();
																																						return;
																																					}
																																				}
																																			}, null);
																																} else if (arg1.statusCode == 401) {
																																	registerCarButton.setEnabled(true);
																																	dialog.dismiss();
																																	return;
																																} else {
																																	registerCarButton.setEnabled(true);
																																	dialog.dismiss();
																																	return;
																																}
																															}
																														}, null);
																											} else if (arg1.statusCode == 401) {
																												registerCarButton.setEnabled(true);
																												dialog.dismiss();
																												return;
																											} else {
																												registerCarButton.setEnabled(true);
																												dialog.dismiss();
																												return;
																											}
																										}
																									}, null);
																						} else if (arg1.statusCode == 401) {
																							registerCarButton.setEnabled(true);
																							dialog.dismiss();
																							return;
																						} else {
																							registerCarButton.setEnabled(true);
																							dialog.dismiss();
																							return;
																						}
																					}
																				}, null);
																	} else if (arg1.statusCode == 401) {
																		registerCarButton.setEnabled(true);
																		dialog.dismiss();
																		return;
																	} else {
																		registerCarButton.setEnabled(true);
																		dialog.dismiss();
																		return;
																	}
																}
															}, null);
												} else if (arg1.statusCode == 401) {
													registerCarButton.setEnabled(true);
													dialog.dismiss();
													return;
												} else {
													registerCarButton.setEnabled(true);
													dialog.dismiss();
													return;
												}
											}
										}, null);  
							} else if (arg1.statusCode == 401) {
								registerCarButton.setEnabled(true);
								dialog.dismiss();
								return;
							} else {
								registerCarButton.setEnabled(true);
								dialog.dismiss();
								return;
							}
						}
					}, null);
		 
		

	}

 
	private void returnLoginActivity() {
		// 清除password的保存，不让自动登录
		SharedPreferences sharedPreferences = getSharedPreferences(
				DiDiApplication.NAME_OF_SHARED_PREFERENCES,
				Context.MODE_PRIVATE);
		sharedPreferences
				.edit()
				.putString(DiDiApplication.KEY_OF_LOGIN_PWD,
						DiDiApplication.VALUE_UNKOWN).commit();
		// 清除application
		DiDiApplication.getInstance().setUserName(DiDiApplication.VALUE_UNKOWN);
		DiDiApplication.getInstance().setPassword(DiDiApplication.VALUE_UNKOWN);
		// 启动前程服务
		Intent intent = new Intent(this, ForegroundServer.class);
		intent.putExtra(ForegroundServer.FOREGROUND_COMMAND_NAME,
				ForegroundServer.COMMAND_OPERATION_FOREGROUND);
		intent.putExtra("server_state", false);
		startService(intent);
		// 下线处理
		Intent downlineIntent = new Intent(this, BackgroundServer.class);
		downlineIntent.putExtra(BackgroundServer.BACKGROUND_COMMAND_NAME,
				BackgroundServer.COMMAND_DONWLINE);
		startService(downlineIntent);
		// 跳转带登录页面
		Intent loginIntent = new Intent(this, LoginActivity.class);
		loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
				| Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(loginIntent);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}

}
