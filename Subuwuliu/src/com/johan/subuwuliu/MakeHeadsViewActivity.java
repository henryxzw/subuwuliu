package com.johan.subuwuliu;

import java.util.HashMap;

import com.johan.subuwuliu.application.DiDiApplication;
import com.johan.subuwuliu.bean.DriverInfoBean;
import com.johan.subuwuliu.bean.StatusBean;
import com.johan.subuwuliu.dialog.WaitingDialog;
import com.johan.subuwuliu.util.GsonUtil;
import com.johan.subuwuliu.util.ImageUtil;
import com.johan.subuwuliu.util.NetUtil;
import com.johan.subuwuliu.util.NetUtil.UploadFileListener;
import com.johan.subuwuliu.util.ToastUtil;
import com.johan.subuwuliu.util.ToolUtil;
import com.johan.subuwuliu.view.ClipImageView;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.umeng.analytics.MobclickAgent;

import android.content.Intent;
import android.graphics.Bitmap;

public class MakeHeadsViewActivity extends AppTheme2Activity {
	
	private ClipImageView srcView;
	
	private WaitingDialog waitingDialog;

	@Override
	public String getThemeTitle() {
		// TODO Auto-generated method stub
		return "移动和缩放";
	}

	@Override
	public int getContentView() {
		// TODO Auto-generated method stub
		return R.layout.activity_makeheadsview;
	}

	@Override
	public void findId() {
		// TODO Auto-generated method stub
		srcView = (ClipImageView) findViewById(R.id.makeheadsview_srcview);
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		String photoPath = getIntent().getExtras().getString("photo_path");
		Bitmap bitmap = ImageUtil.getBitmapFromPath(this, photoPath, 512);
		if(null != bitmap) {
			srcView.setImageBitmap(bitmap);
		}
		waitingDialog = new WaitingDialog(this);
	}
	
	@Override
	public String getThemeTip() {
		// TODO Auto-generated method stub
		return "确定";
	}

	@Override
	public void clickThemeTip() {
		// TODO Auto-generated method stub
		Bitmap bitmap = srcView.clip();
		String clipPhotoPath = ImageUtil.saveBitmap(bitmap, ImageUtil.SAVE_PNG);
		if(clipPhotoPath != null) {
			waitingDialog.show(findViewById(R.id.makeheadsview_layout));
			//上传
			HashMap<String, String> uploadParams = new HashMap<String, String>();
			uploadParams.put("avatar", clipPhotoPath);
			NetUtil.getInstance().uploadFile(uploadParams, new UploadFileListener() {
				@Override
				public void onSuccess(final HashMap<String, String> resultMap) {
					HashMap<String, String> params = new HashMap<String, String>();
					params.put("key", "edit_user_avatar");
					params.put("username", getLoginName());
					params.put("pwd", getLoginPassword());
					params.putAll(resultMap);
					String action = ToolUtil.encryptionUrlParams(params);
					NetUtil.getInstance().doGet(NetUtil.DRIVER_URL + action, new RequestCallBack<String>() {
						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
							StatusBean submitResult = GsonUtil.jsonToObjct(responseInfo.result, StatusBean.class);
							if("0".equals(submitResult.status)) {
								//保存
								String driverInfoData = getSharedPreferences().getString(DiDiApplication.KEY_OF_DRIVER_INFO_DATA, DiDiApplication.VALUE_UNKOWN);
								if(!DiDiApplication.VALUE_UNKOWN.equals(driverInfoData)) {
									DriverInfoBean driverInfoBean = GsonUtil.jsonToObjct(driverInfoData, DriverInfoBean.class);
									driverInfoBean.avatar = resultMap.get("avatar");
									getSharedPreferences().edit().putString(DiDiApplication.KEY_OF_DRIVER_INFO_DATA, GsonUtil.objectToJson(driverInfoBean)).commit();
									//回传
									Intent data = new Intent();
									data.putExtra("head_url", resultMap.get("avatar"));
									setResult(RESULT_OK, data);
								}
								ToastUtil.show(MakeHeadsViewActivity.this, "上传头像成功");
								finish();
							} else {
								ToastUtil.show(MakeHeadsViewActivity.this, "上传头像失败，请重新提交");
							}
							waitingDialog.dismiss();
						}
						@Override
						public void onFailure(HttpException exception, String info) {
							waitingDialog.dismiss();
							ToastUtil.show(MakeHeadsViewActivity.this, "上传头像失败，请重新提交");
						}
					});
				}
				@Override
				public void onFail(String errorMsg) {
					waitingDialog.dismiss();
					ToastUtil.show(MakeHeadsViewActivity.this, "上传头像失败，请重新提交");
				}
			});
			
		} else {
			ToastUtil.show(this, "保存头像失败，请检查SD卡");
		}
	}
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this); // 统计时长
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
