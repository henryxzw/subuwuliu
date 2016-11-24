package com.johan.subuwuliu;

import org.json.JSONObject;

import com.easemob.util.NetUtils; 
import com.johan.subuwuliu.application.DiDiApplication;
import com.johan.subuwuliu.bean.DriverInfoBean;
import com.johan.subuwuliu.dialog.SelectPictureWayWindow; 
import com.johan.subuwuliu.qiniu.Myupdate;
import com.johan.subuwuliu.server.ForegroundServer;
import com.johan.subuwuliu.util.GsonUtil;
import com.johan.subuwuliu.util.ImageUtil;
import com.johan.subuwuliu.util.NetUtil;
import com.johan.subuwuliu.util.ToastUtil;
import com.johan.subuwuliu.util.ToolUtil;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

public class MeActivity extends AppThemeActivity implements OnClickListener {
	private static final int REQUEST_MAKE_HEADVIEW = 205;
	private ImageView headsView;
	private RatingBar creditRatingBar;
	private TextView creditTip, driverName, carNumber, recentWaybill,
			totalWaybill, feedback;
	private TextView personMessage, myAppointment, driveRecord, accountingBook,
			qianbao, carManagement, help, setting;
	private SelectPictureWayWindow selectPictureWayWindow;
	private DriverInfoBean driverInfo;
	public static Activity activity;

	@Override
	public String getThemeTitle() {
		// TODO Auto-generated method stub
		return "我";
	}

	@Override
	public int getContentView() {
		// TODO Auto-generated method stub
		return R.layout.activity_me;
	}

	@Override
	public void findId() {
		// TODO Auto-generated method stub
		activity = this; 
		headsView = (ImageView) findViewById(R.id.me_headsview);
		creditTip = (TextView) findViewById(R.id.me_credit_tip);
		creditRatingBar = (RatingBar) findViewById(R.id.me_credit_rate_bar);
		driverName = (TextView) findViewById(R.id.me_driver_name);
		carNumber = (TextView) findViewById(R.id.me_car_number);
		recentWaybill = (TextView) findViewById(R.id.me_recent_waybill);
		totalWaybill = (TextView) findViewById(R.id.me_total_waybill);
		feedback = (TextView) findViewById(R.id.me_feedback_rate);
		personMessage = (TextView) findViewById(R.id.me_item_person_message);
		myAppointment = (TextView) findViewById(R.id.me_item_my_appointment);
		driveRecord = (TextView) findViewById(R.id.me_item_drive_record);
		accountingBook = (TextView) findViewById(R.id.me_item_accounting_book);
		qianbao = (TextView) findViewById(R.id.me_item_accounting_qiangbao);
		carManagement = (TextView) findViewById(R.id.me_item_car_management);
		help = (TextView) findViewById(R.id.me_item_help);
		setting = (TextView) findViewById(R.id.me_item_setting);
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		creditRatingBar.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				return true;
			}
		});
		SharedPreferences sharedPreferences = getSharedPreferences(
				DiDiApplication.NAME_OF_SHARED_PREFERENCES,
				Context.MODE_PRIVATE);
		String driverInfoData = sharedPreferences.getString(
				DiDiApplication.KEY_OF_DRIVER_INFO_DATA,
				DiDiApplication.VALUE_UNKOWN);
		if (DiDiApplication.VALUE_UNKOWN.equals(driverInfoData)) {
			finish();
			return;
		}
		driverInfo = GsonUtil.jsonToObjct(driverInfoData, DriverInfoBean.class);
		creditTip.setText(driverInfo.credit);
		float credit = Float.parseFloat(driverInfo.credit);
		creditRatingBar.setRating(credit);
		driverName.setText(driverInfo.nick_name);
		carNumber.setText(driverInfo.plate_number);
		recentWaybill.setText(driverInfo.order_near);
		totalWaybill.setText(driverInfo.order_total);
		feedback.setText(driverInfo.goods_comment);
		headsView.setOnClickListener(this);
		personMessage.setOnClickListener(this);
		myAppointment.setOnClickListener(this);
		driveRecord.setOnClickListener(this);
		accountingBook.setOnClickListener(this);
		qianbao.setOnClickListener(this);
		carManagement.setOnClickListener(this);
		help.setOnClickListener(this);
		setting.setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (!"".equals(driverInfo.avatar)) {
			NetUtil.displayImageFromUrl(this, headsView, NetUtil.SERVER_IP
					+ driverInfo.avatar, R.drawable.siji, R.drawable.siji);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (!ToolUtil.isNetworkAvailable(MeActivity.this)) {
			ToolUtil.dilogA(MeActivity.this, "网络异常，请先检查网络是否连接");
			return;
		}
		switch (v.getId()) {
		case R.id.me_headsview:
			if (selectPictureWayWindow == null) {
				selectPictureWayWindow = new SelectPictureWayWindow(this);
			}
			selectPictureWayWindow.showAtLocation(findViewById(R.id.me_layout),
					Gravity.BOTTOM, 0, 0);
			break;
		case R.id.me_item_person_message:
			goActivity(PersonMessageActivity.class);
			break;
		case R.id.me_item_my_appointment:
			goActivity(MyAppointmentActivity.class);
			break;
		case R.id.me_item_drive_record:
			goActivity(DriverRecordActivity.class);
			break;
		case R.id.me_item_accounting_book:
			goActivity(AccountBookActivity.class);
			break;
		case R.id.me_item_accounting_qiangbao:
			goActivity(Myqianbao.class);
			break;
		case R.id.me_item_car_management:
			if (driverInfo.car_status.equals("1")) {
				showToast("证件过期或资料未审核");
				return;
			}
			if (driverInfo.car_status.equals("2")) {
				showToast("未绑定车辆");
				return;
			}
			goActivity(ManagerCarActivity.class);
			break;
		case R.id.me_item_help:
			goActivity(HelperActivity.class);
			break;
		case R.id.me_item_setting:
			goActivity(SettingActivity.class);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && data != null) {
			switch (requestCode) {
			case SelectPictureWayWindow.SELECT_IMG_FROM_TAKE:
				Bundle extras = data.getExtras();
				if (null != extras) {
					Bitmap photo = extras.getParcelable("data");
					String takePicturePath = ImageUtil.saveBitmap(photo,
							ImageUtil.SAVE_JPG);
					if (null != takePicturePath) {
						// 跳转
						Bundle bundle = new Bundle();
						bundle.putString("photo_path", takePicturePath);
						goActivityForResult(MakeHeadsViewActivity.class,
								REQUEST_MAKE_HEADVIEW, bundle);
					} else {
						ToastUtil.show(this, "拍照出错");
					}
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
				// 跳转
				Bundle bundle = new Bundle();
				bundle.putString("photo_path", photoPath);
				goActivityForResult(MakeHeadsViewActivity.class,
						REQUEST_MAKE_HEADVIEW, bundle);
				break;
			case REQUEST_MAKE_HEADVIEW:
				String headUrl = data.getStringExtra("head_url");
				System.out.println("head url ---> " + headUrl);
				if (null != headUrl && !"".equals(headUrl)) {
					System.out.println("===============更新头像");
					Message msg = uiHandler.obtainMessage();
					Bundle msgData = new Bundle();
					msgData.putString("head_url", headUrl);
					msg.setData(msgData);
					uiHandler.sendMessage(msg);
				}
				break;
			default:
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	Handler uiHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			String headUrl = msg.getData().getString("head_url");
			System.out.println("在handler更新头像---->" + headUrl);
			NetUtil.displayImageFromUrl(MeActivity.this, headsView,
					NetUtil.SERVER_IP + headUrl, R.drawable.siji,
					R.drawable.siji);
		};
	};

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}


}
