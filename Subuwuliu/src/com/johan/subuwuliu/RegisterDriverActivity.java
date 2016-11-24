package com.johan.subuwuliu;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.harmony.javax.security.auth.login.AppConfigurationEntry;
import org.json.JSONObject;

import com.johan.subuwuliu.adapter.GridViewAdapter;
import com.johan.subuwuliu.adapter.RegisterSmallGridViewAdapter;
import com.johan.subuwuliu.application.DiDiApplication;
import com.johan.subuwuliu.bean.StatusBean;
import com.johan.subuwuliu.database.CityDatabase;
import com.johan.subuwuliu.dialog.OnSelectedListener;
import com.johan.subuwuliu.dialog.SelectDateDialog;
import com.johan.subuwuliu.dialog.SelectGoodsDialog;
import com.johan.subuwuliu.dialog.SelectPictureWayWindow;
import com.johan.subuwuliu.dialog.ShowPictureWindow;
import com.johan.subuwuliu.dialog.WaitingDialog;
import com.johan.subuwuliu.handler.UpdateViewHandle;
import com.johan.subuwuliu.qiniu.Config;
import com.johan.subuwuliu.qiniu.Myupdate;
import com.johan.subuwuliu.util.GsonUtil;
import com.johan.subuwuliu.util.ImageUtil;
import com.johan.subuwuliu.util.NetUtil;
import com.johan.subuwuliu.util.NetUtil.UploadFileListener;
import com.johan.subuwuliu.util.ToastUtil;
import com.johan.subuwuliu.util.ToolUtil;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.qiniu.android.common.ServiceAddress;
import com.qiniu.android.common.Zone;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomButton;

public class RegisterDriverActivity extends AppThemeActivity implements
		OnClickListener {

	private static final int SELECT_IDNETIFICATION = 1;
	private static final int SELECT_DRIVER_LICENSE = 2;
	private static final int SELECT_LIMIT_IDENTIFICATION = 3;
	private static final int SELECT_LIMIT_DRIVER_LICENSE = 4;

	private static final int SELECT_LOCATION_REQUEST_CODE = 30;
	private static final int SELECT_CITY_REQUEST_CODE = 260;

	private String checkPhone, checkPassword;

	private TextView residentArea, identificationLimitTime,
			driverLicenseLimitTime;

	private String month_time;

	// 常驻地区的经纬度
	private double residentAreaLatitude, residentAreaLongitude;

	private EditText firstName, lastName, driverTime;

	private GridView familiarCity, goodsPreference, uploadIdentification,
			uploadDriverLicense;

	private Button next;

	private SelectGoodsDialog selectGoodDialog;

	private SelectPictureWayWindow selectPictureWayWindow;

	private ShowPictureWindow showPictureWindow;

	private SelectDateDialog selectDateView;

	private int uploadWhich = 0;
	private int limitWhich = 0;

	private RegisterSmallGridViewAdapter familiarCityAdapter,
			goodsPreferenceAdapter;

	private GridViewAdapter uploadIdentificationAdapter,
			uploadDriverLicenseAdapter;

	private WaitingDialog waitingDialog;

	// 最后真正要提交的数据
	private String submitCode, submitGoodsType, submitResidentArea, submitName,
			submitDriverTime, submitCityLine, submitDriverLicenseLimitTime,
			submitIdentificationLimitTime;
	private String tempSelect = ""; //保存拍照选择缓存路径

	@Override
	public int getContentView() {
		return R.layout.activity_registerdriver;
	}

	@Override
	public String getThemeTitle() {
		// TODO Auto-generated method stub
		return "注册司机";
	}

	@Override
	public void findId() {
		firstName = (EditText) findViewById(R.id.registerdriver_first_name);
		lastName = (EditText) findViewById(R.id.registerdriver_last_name);
		driverTime = (EditText) findViewById(R.id.registredriver_driver_time);
		residentArea = (TextView) findViewById(R.id.registerdriver_resident_area);
		familiarCity = (GridView) findViewById(R.id.registerdriver_familiar_city);
		goodsPreference = (GridView) findViewById(R.id.registerdriver_goods_preference);
		uploadIdentification = (GridView) findViewById(R.id.registerdriver_upload_identification_img);
		uploadDriverLicense = (GridView) findViewById(R.id.registerdriver_upload_driver_license_img);
		identificationLimitTime = (TextView) findViewById(R.id.registerdriver_upload_identification_limittime);
		driverLicenseLimitTime = (TextView) findViewById(R.id.registerdriver_upload_driver_license_limittime);
		next = (Button) findViewById(R.id.registerdriver_next);
		setTime();
	}

	public void setTime() {
		String url = NetUtil.SERVER_IP + "/api/android/driver.ashx?action=";
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("key", "get_server_time");
		String action = ToolUtil.encryptionUrlParams(params);
		NetUtil.getInstance().doGet(url + action,
				new RequestCallBack<String>() {
					@Override
					public void onFailure(HttpException arg0, String arg1) {
						month_time = "0000.00.00";
					}

					@Override
					public void onSuccess(final ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						if (arg0.result.equals("")) {
							month_time = "0000.00.00";
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

	@Override
	public void init() {
		// 得到验证手机的信息
		Bundle data = getIntent().getExtras();
		if (data != null) {
			checkPhone = data.getString("check_phone");
			checkPassword = data.getString("check_password");
			System.out.println("进入注册司机页面:" + checkPhone + "----"
					+ checkPassword);
		}
		// 选择常驻地区
		residentArea.setOnClickListener(this);
		// 熟悉城市
		familiarCityAdapter = new RegisterSmallGridViewAdapter(
				RegisterDriverActivity.this, familiarCityClickListener);
		familiarCity.setAdapter(familiarCityAdapter);
		familiarCity.setSelector(new ColorDrawable(Color.TRANSPARENT));
		// 货物偏好
		goodsPreferenceAdapter = new RegisterSmallGridViewAdapter(
				RegisterDriverActivity.this, goodsPreferenceClickListener);
		goodsPreference.setAdapter(goodsPreferenceAdapter);
		goodsPreference.setSelector(new ColorDrawable(Color.TRANSPARENT));
		// 上传身份证
		uploadIdentificationAdapter = new GridViewAdapter(
				RegisterDriverActivity.this, 2);
		uploadIdentification.setAdapter(uploadIdentificationAdapter);
		uploadIdentification.setSelector(new ColorDrawable(Color.TRANSPARENT));
		uploadIdentification.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == uploadIdentificationAdapter.getCount() - 1) {
					uploadWhich = SELECT_IDNETIFICATION;
					if (selectPictureWayWindow == null) {
						selectPictureWayWindow = new SelectPictureWayWindow(
								RegisterDriverActivity.this);
						selectPictureWayWindow.callBack = new UpdateViewHandle() {
							
							@Override
							public void Update(String value) {
								// TODO Auto-generated method stub
								tempSelect = value;
							}
						};
					}
					selectPictureWayWindow.showAtLocation(getParentLayout(),
							Gravity.BOTTOM, 0, 0);
				} else {
					showPictureWindow(uploadIdentificationAdapter, position);
				}
			}
		});
		// 上传驾驶证
		uploadDriverLicenseAdapter = new GridViewAdapter(
				RegisterDriverActivity.this, 1);
		uploadDriverLicense.setAdapter(uploadDriverLicenseAdapter);
		uploadDriverLicense.setSelector(new ColorDrawable(Color.TRANSPARENT));
		uploadDriverLicense.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == uploadDriverLicenseAdapter.getCount() - 1) {
					uploadWhich = SELECT_DRIVER_LICENSE;
					if (selectPictureWayWindow == null) {
						selectPictureWayWindow = new SelectPictureWayWindow(
								RegisterDriverActivity.this);
                           selectPictureWayWindow.callBack = new UpdateViewHandle() {
							
							@Override
							public void Update(String value) {
								// TODO Auto-generated method stub
								tempSelect = value;
							}
						};
					}
					selectPictureWayWindow.showAtLocation(getParentLayout(),
							Gravity.BOTTOM, 0, 0);
				} else {
					showPictureWindow(uploadDriverLicenseAdapter, position);
				}
			}
		});
		// 选择证件的有效期
		identificationLimitTime.setOnClickListener(this);
		driverLicenseLimitTime.setOnClickListener(this);
		// 下一步
		next.setOnClickListener(this);
		waitingDialog = new WaitingDialog(RegisterDriverActivity.this);
		setNeedLoginInfo(false);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.registerdriver_resident_area:
			goActivityForResult(SelectLocationActivity.class,
					SELECT_LOCATION_REQUEST_CODE);
			break;
		case R.id.registerdriver_upload_identification_limittime:
			limitWhich = SELECT_LIMIT_IDENTIFICATION;
			showSelectTimeDialog();
			break;
		case R.id.registerdriver_upload_driver_license_limittime:
			limitWhich = SELECT_LIMIT_DRIVER_LICENSE;
			showSelectTimeDialog();
			break;
		case R.id.registerdriver_next:
			waitingDialog.show(findViewById(R.id.registerdriver_layout));
	        Timer timer = new Timer();
	        TimerTask timerTask =new TimerTask() {
				
				@Override
				public void run() {
					registerDriver();
				}
			};
			timer.schedule(timerTask, 0);
			break;
		default:
			break;
		}
	}

	private OnClickListener goodsPreferenceClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			showSelectGoodDialog();
		}
	};

	private OnClickListener familiarCityClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(RegisterDriverActivity.this,
					SelectCityActivity.class);
			startActivityForResult(intent, SELECT_CITY_REQUEST_CODE);
		}
	};

	/**
	 * 选择货物dialog
	 */
	private void showSelectGoodDialog() {
		if (selectGoodDialog == null) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("key", "goods_type");
			String action = ToolUtil.encryptionUrlParams(params);
			String requestGoodsUrl = NetUtil.DRIVER_URL + action;
			selectGoodDialog = new SelectGoodsDialog(
					RegisterDriverActivity.this, "货物偏好", requestGoodsUrl);
			selectGoodDialog.setOnSelectedListener(new OnSelectedListener() {
				@Override
				public void onSelected(String selectedData) {
					goodsPreferenceAdapter.addData(selectedData);
				}
			});
		}
		selectGoodDialog.show();
	}

	/**
	 * 选择时间Dialog
	 */
	private void showSelectTimeDialog() {
		if (selectDateView == null) {
			selectDateView = new SelectDateDialog(RegisterDriverActivity.this);
			selectDateView.setOnSelectedListener(new OnSelectedListener() {
				@SuppressLint("SimpleDateFormat")
				@Override
				public void onSelected(String selectedData) {
					// TODO Auto-generated method stub
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
					if (limitWhich == SELECT_LIMIT_IDENTIFICATION) {

						identificationLimitTime.setText(selectedData);
					}
					if (limitWhich == SELECT_LIMIT_DRIVER_LICENSE) {
						driverLicenseLimitTime.setText(selectedData);
					}
				}
			});
		} else {
			if (limitWhich == SELECT_LIMIT_IDENTIFICATION) {
				selectDateView.setDate(identificationLimitTime.getText()
						.toString());
			}
			if (limitWhich == SELECT_LIMIT_DRIVER_LICENSE) {
				selectDateView.setDate(driverLicenseLimitTime.getText()
						.toString());
			}
		}
		selectDateView.show();
	}

	private View getParentLayout() {
		return findViewById(R.id.registerdriver_layout);
	}

	private void showPictureWindow(GridViewAdapter adapter, int position) {
		if (showPictureWindow == null) {
			showPictureWindow = new ShowPictureWindow(
					RegisterDriverActivity.this);
		}
		showPictureWindow.setImage(adapter.getDatas().get(position));
		showPictureWindow.setAdapter(uploadIdentificationAdapter);
		showPictureWindow.show(getParentLayout());
	}
	
	Handler handler = new Handler();
	
	public void HideDialog()
	{
		handler.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
			    if(waitingDialog.isShowing())
			    {
			    	waitingDialog.dismiss();
			    }
			}
		});
	}
	public void ShowToast(final String msg)
	{
		handler.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				showToast(msg);
			}
		});
	}
	
	private String idCard1, idCard2, driverLicense;

	private void registerDriver() {
//		String tempIdCard3 = uploadIdentificationAdapter.getDatas().get(0);
//		
//	  byte[] buffers =	Myupdate.GetBitmapCommBytes(new File(tempIdCard3));
//	  Log.e("register", "=============buffer length"+buffers.length+"==============");
		System.out.println("开始注册-----------------------------");
//		waitingDialog.show(findViewById(R.id.registerdriver_layout));
		String firstNameStr = firstName.getText().toString();
		//String lastNameStr = lastName.getText().toString();
		String driverTimeStr = driverTime.getText().toString();
		String residentAreaStr = residentArea.getText().toString();
		String identificationLimitTimeStr = identificationLimitTime.getText()
				.toString();
		String driverLicenseLimitTimeStr = driverLicenseLimitTime.getText()
				.toString();
		if ("".equals(firstNameStr)) {
			HideDialog();
			ShowToast("请输入姓名");
			return;
		}
//		if ("".equals(lastNameStr)) {
//			HideDialog();
//			ShowToast("请输入名字");
//			return;
//		}
		if ("".equals(driverTimeStr)) {
			HideDialog();
			ShowToast("请输入驾龄");
			return;
		}
		if ("".equals(residentAreaStr)) {
			HideDialog();
			ShowToast("请选择常驻地区");
			return;
		}
		if ("".equals(identificationLimitTimeStr)) {
			HideDialog();
			ShowToast("请选择身份证的有效时间");
			return;
		}
		if ("".equals(driverLicenseLimitTimeStr)) {
			HideDialog();
			ShowToast("请选择驾驶证的有效时间");
			return;
		}
		// 检查是否选择熟悉城市
		if (familiarCityAdapter.getCount() < 2) {
			HideDialog();
			ShowToast("请选择至少1个熟悉城市");
			return;
		}
		// 检查货物偏好
		if (goodsPreferenceAdapter.getCount() < 2) {
			HideDialog();
			ShowToast("请选择至少一个货物偏好");
			return;
		}
		// 检查身份证图片
		if (uploadIdentificationAdapter.getCount() != 3) {
			HideDialog();
			ShowToast("请上传身份证的正反面2张图片");
			return;
		}
		// 检查驾驶证图片
		if (uploadDriverLicenseAdapter.getCount() != 2) {
			HideDialog();
			ShowToast("请上传驾驶证的图片1张");
			return;
		}

		/************************************* 整理要提交的数据 ************************************/
		// 机器码
		String telmanagerid, androidid, serialnumber;
		try {
			TelephonyManager telmanager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
			telmanagerid = telmanager.getDeviceId();
		} catch (Exception e) {
			telmanagerid = "0";
		}
		try {
			androidid = Settings.System.getString(getContentResolver(),
					Settings.System.ANDROID_ID);
		} catch (Exception e) {
			androidid = "0";
		}
		try {
			serialnumber = android.os.Build.SERIAL;
		} catch (Exception e) {
			serialnumber = "0";
		}
		if (telmanagerid == null || telmanagerid.equals("")) {
			telmanagerid = "0";
		}
		if (androidid == null || androidid.equals("")) {
			androidid = "0";
		}
		if (serialnumber == null || serialnumber.equals("")) {
			serialnumber = "0";
		}
		submitCode = telmanagerid + androidid + serialnumber;
		// 姓名
		submitName = firstNameStr;
		// 驾龄
		submitDriverTime = driverTimeStr;
		// 货物偏好id
		submitGoodsType = "";
		for (int i = 0; i < goodsPreferenceAdapter.getCount() - 1; i++) {
			String goodsTitle = goodsPreferenceAdapter.getDatas().get(i);
			String goodsId = selectGoodDialog.getGoodsIdByTitle(goodsTitle);
			if (null != goodsId) {
				submitGoodsType += goodsId + "|";
			}
		}
		submitGoodsType = submitGoodsType.substring(0,
				submitGoodsType.length() - 1);
		// 常驻地区
		submitResidentArea = residentAreaStr + "|" + residentAreaLatitude + ","
				+ residentAreaLongitude;
		// 熟悉城市
		submitCityLine = "";
		CityDatabase database = new CityDatabase(RegisterDriverActivity.this);
		for (int i = 0; i < familiarCityAdapter.getCount() - 1; i++) {
			String familiarCity = familiarCityAdapter.getDatas().get(i);
			String familiarProvince = database
					.getProvinceFromCity(familiarCity);
			submitCityLine += familiarProvince + "," + familiarCity + "|";
		}
		submitCityLine = submitCityLine.substring(0,
				submitCityLine.length() - 1);
		// 有效期
		submitDriverLicenseLimitTime = SelectDateDialog
				.ToUploadData(driverLicenseLimitTimeStr);
		submitIdentificationLimitTime = SelectDateDialog
				.ToUploadData(identificationLimitTimeStr);
		// 身份证图片地址
		String tempIdCard1 = uploadIdentificationAdapter.getDatas().get(0);
		final String tempIdCard2 = uploadIdentificationAdapter.getDatas()
				.get(1);
		
		/*** 图片要压缩一下 ***/
		// 驾驶证地址
		final String tempDriverLicense = uploadDriverLicenseAdapter.getDatas()
				.get(0);
		// //////////////////////////////
	

		final UploadManager uploadManager = new UploadManager();
		final String token = Myupdate.Token();
		
		uploadManager.put(
				Myupdate.GetBitmapCommBytes(new File(tempIdCard1)),
				checkPhone + "_" + Myupdate.Timea(), token,
				new UpCompletionHandler() {

					@Override
					public void complete(String arg0,
							com.qiniu.android.http.ResponseInfo arg1,
							JSONObject arg2) {
						// TODO Auto-generated method stub
						if (arg1.statusCode == 200) {
							idCard1 = arg0;
							uploadManager.put(Myupdate
									.GetBitmapCommBytes(new File(tempIdCard2)),
									checkPhone + "_" + Myupdate.Timea(),
									token, new UpCompletionHandler() {

										@Override
										public void complete(
												String arg0,
												com.qiniu.android.http.ResponseInfo arg1,
												JSONObject arg2) {
											// TODO Auto-generated method stub
											if (arg1.statusCode == 200) {
												idCard2 = arg0;
											
												uploadManager.put(
														Myupdate.GetBitmapCommBytes(new File(tempDriverLicense)),
														checkPhone
																+ "_"
																+ Myupdate
																		.Timea(),
														token,
														new UpCompletionHandler() {

															@Override
															public void complete(
																	String arg0,
																	com.qiniu.android.http.ResponseInfo arg1,
																	JSONObject arg2) {
																// TODO
																// Auto-generated
																// method stub
																if (arg1.statusCode == 200) {
																	driverLicense = arg0;
																	/*** 图片要压缩一下 ***/
																	/************************************* 整理要提交的数据 ************************************/
																	/************************************* 开始注册 ******************************************/
																	HashMap<String, String> params = new HashMap<String, String>();
																	params.put(
																			"key",
																			"driver_reg");
																	params.put(
																			"username",
																			checkPhone);
																	params.put(
																			"pwd",
																			checkPassword);
																	params.put(
																			"code",
																			submitCode);
																	params.put(
																			"goods_type_id",
																			submitGoodsType);
																	params.put(
																			"area",
																			submitResidentArea);
																	params.put(
																			"nick_name",
																			submitName);
																	params.put(
																			"driving_age",
																			submitDriverTime);
																	params.put(
																			"line",
																			submitCityLine);
																	params.put(
																			"license_available_time",
																			submitDriverLicenseLimitTime);
																	params.put(
																			"card_available_time",
																			submitIdentificationLimitTime);

																	params.put(
																			"driving_license",
																			driverLicense);
																	params.put(
																			"identity_card1",
																			idCard1);
																	params.put(
																			"identity_card2",
																			idCard2);
																	String action = ToolUtil
																			.encryptionUrlParams(params);
																	NetUtil.getInstance()
																			.doGet(NetUtil.DRIVER_URL
																					+ action,
																					new RequestCallBack<String>() {
																						@Override
																						public void onSuccess(
																								ResponseInfo<String> responseInfo) {
																							System.out
																									.println("注册结果------>"
																											+ responseInfo.result);
																							StatusBean registerResult = GsonUtil
																									.jsonToObjct(
																											responseInfo.result,
																											StatusBean.class);
																							if ("0".equals(registerResult.status)) {
																								// 保存登录信息
																								SharedPreferences sharedPreferences = getSharedPreferences(
																										DiDiApplication.NAME_OF_SHARED_PREFERENCES,
																										MODE_PRIVATE);
																								Editor editor = sharedPreferences
																										.edit();
																								editor.putString(
																										DiDiApplication.KEY_OF_LOGIN_NAME,
																										checkPhone);
																								editor.commit();
																								// 显示提示dialog
																							final	Dialog tipDialog = new Dialog(
																										RegisterDriverActivity.this,
																										R.style.DiDiDialog);
																								tipDialog
																										.setContentView(R.layout.dialog_registerdriverfinish);
																								Button button = (Button) tipDialog
																										.findViewById(R.id.registerderiverfinish_but);
																								button.setOnClickListener(new OnClickListener() {
																									@Override
																									public void onClick(
																											View v) {
																										// 结束
																										RegisterDriverActivity.this
																												.finish();
																									}
																								});
																								tipDialog
																										.setOnKeyListener(new OnKeyListener() {
																											@Override
																											public boolean onKey(
																													DialogInterface dialog,
																													int keyCode,
																													KeyEvent event) {
																												// TODO
																												// Auto-generated
																												// method
																												// stub
																												if (keyCode == KeyEvent.KEYCODE_BACK) {
																													// 结束
																													tipDialog.dismiss();
																													onBackPressed();
																													
																												}
																												return false;
																											}
																										});
																								tipDialog
																										.setCancelable(false);
																								tipDialog
																										.show();
																							} else {
																								showToast(registerResult.msg);
																							}
																							waitingDialog
																									.dismiss();
																						}

																						@Override
																						public void onFailure(
																								HttpException exception,
																								String info) {
																							System.out
																									.println("--->"
																											+ exception
																													.getMessage());
																							showToast(info);
																							waitingDialog
																									.dismiss();
																						}
																					});
																} else if (arg1.statusCode == 401) {
																	ToastUtil
																			.show(getApplicationContext(),
																					"上传图片超时");
																	HideDialog();
																	return;
																} else {
																	Toast.makeText(
																			DiDiApplication.applicationContext,
																			"上传失败",
																			0)
																			.show();
																	HideDialog();
																	return;
																}
															}

														}, new UploadOptions(null ,null, false, new UpProgressHandler() {
															
															@Override
															public void progress(String arg0, double arg1) {
																Log.i("qiniu",   "third: " + arg1);
															}
														}, null));
											} else if (arg1.statusCode == 401) {
												ToastUtil
														.show(getApplicationContext(),
																"上传图片超时");
												HideDialog();
												return;
											} else {
												Toast.makeText(
														DiDiApplication.applicationContext,
														"上传失败", 0).show();
												HideDialog();
												return;
											}
										}

									}, new UploadOptions(null ,null, false, new UpProgressHandler() {
										
										@Override
										public void progress(String arg0, double arg1) {
											Log.e("qiniu",   "second: " + arg1);
										}
									}, null));
						} else if (arg1.statusCode == 401) {
							ToastUtil.show(getApplicationContext(), "上传图片超时");
							HideDialog();
							return;
						} else {
							Toast.makeText(DiDiApplication.applicationContext,
									"上传失败", 0).show();
							HideDialog();
							return;
						}
					}

				}, new UploadOptions(null ,null, false, new UpProgressHandler() {
					
					@Override
					public void progress(String arg0, double arg1) {
						Log.e("qiniu",   "first: " + arg1);
					}
				}, null));
		// idCard1 = getLoginName() + "_" + Myupdate.Timea();
		// ImageUtil.getBitmab(idCard1, BitmapFactory.decodeFile(tempIdCard1));
		// idCard2 = getLoginName() + "_" + Myupdate.Timea();
		// ImageUtil.getBitmab(idCard2, BitmapFactory.decodeFile(tempIdCard2));
		// /*** 图片要压缩一下 ***/
		//
		// /*** 图片要压缩一下 ***/
		// driverLicense = getLoginName() + "_" + Myupdate.Timea();
		// ImageUtil.getBitmab(driverLicense,
		// BitmapFactory.decodeFile(tempDriverLicense));

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && (data != null || requestCode==SelectPictureWayWindow.SELECT_IMG_FROM_TAKE)) {
			switch (requestCode) {
			case SELECT_LOCATION_REQUEST_CODE:
//				("tag", "====================================");
				String address = data.getStringExtra("location_address");
				
				residentAreaLatitude = data.getDoubleExtra("location_latitude",
						0.0f);
//				Log.e("tag", "================22222222222222222===================="+residentAreaLatitude);
				residentAreaLongitude = data.getDoubleExtra(
						"location_longitude", 0.0f);
//				Log.e("tag", "===============33333333333333"+    residentAreaLongitude    + "+333333333====================="+address);
				residentArea.setText(address);
				break;
			case SELECT_CITY_REQUEST_CODE:
				String selectedCity = data.getStringExtra("selected_city");
				familiarCityAdapter.addData(selectedCity);
				break;
			case SelectPictureWayWindow.SELECT_IMG_FROM_TAKE:
				if(TextUtils.isEmpty(tempSelect))
				{
					ToastUtil.show(this, "拍照图片失败");
				}
				else {
					// 添加到列表中
					if (uploadWhich == SELECT_IDNETIFICATION) {
						// 只能上传2张身份证图片
						uploadIdentificationAdapter.addData(tempSelect);
					}
					if (uploadWhich == SELECT_DRIVER_LICENSE) {
						// 只能上传1张驾驶证图片
						uploadDriverLicenseAdapter.addData(tempSelect);
					}
					tempSelect = "";
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
				System.out.println("你选择了----------->" + photoPath);
				if (uploadWhich == SELECT_IDNETIFICATION) {
					// 只能上传2张身份证图片
					uploadIdentificationAdapter.addData(photoPath);
				}
				if (uploadWhich == SELECT_DRIVER_LICENSE) {
					// 只能上传1张驾驶证图片
					uploadDriverLicenseAdapter.addData(photoPath);
				}
				break;
			default:
				break;
			}
		} else {
			System.out
					.println("intent 为 null----------------------------------------");
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}

}
