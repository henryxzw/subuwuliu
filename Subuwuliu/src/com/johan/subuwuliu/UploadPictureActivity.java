package com.johan.subuwuliu;

import com.johan.subuwuliu.bean.UploadOrderPictureBean;
import com.johan.subuwuliu.database.UploadOrderPictrueDatabase;
import com.johan.subuwuliu.dialog.SelectPictureWayWindow;
import com.johan.subuwuliu.handler.UpdateViewHandle;
import com.johan.subuwuliu.util.GsonUtil;
import com.johan.subuwuliu.util.ImageUtil;
import com.johan.subuwuliu.util.ToastUtil;
import com.johan.subuwuliu.util.ToolUtil;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ViewFlipper;
/**
 * 修改，由原来上传到服务器改为上传到7牛，并且不再存入数据库
 * @author apple
 *
 */
public class UploadPictureActivity extends AppThemeActivity implements
		OnClickListener, OnTouchListener {

	private ViewFlipper flipper;

	private String yid;
	private String node;

	private String[] smallImgPath = new String[4];
	private ImageView[] smallImgView = new ImageView[4];
	private ImageView[] bigImgView = new ImageView[4];

	private Button submit;

	private int currentSelected = 0;

	private SelectPictureWayWindow selectPictureWayWindow;
	private String tempSelect = "";

	@Override
	public int getContentView() {
		// TODO Auto-generated method stub
		return R.layout.activity_uploadpicture;
	}

	@Override
	public void findId() {
		// TODO Auto-generated method stub
		smallImgView[0] = (ImageView) findViewById(R.id.uploadpicture_smallimg_1);
		smallImgView[1] = (ImageView) findViewById(R.id.uploadpicture_smallimg_2);
		smallImgView[2] = (ImageView) findViewById(R.id.uploadpicture_smallimg_3);
		smallImgView[3] = (ImageView) findViewById(R.id.uploadpicture_smallimg_4);
		bigImgView[0] = (ImageView) findViewById(R.id.uploadpicture_bigimg_1);
		bigImgView[1] = (ImageView) findViewById(R.id.uploadpicture_bigimg_2);
		bigImgView[2] = (ImageView) findViewById(R.id.uploadpicture_bigimg_3);
		bigImgView[3] = (ImageView) findViewById(R.id.uploadpicture_bigimg_4);
		submit = (Button) findViewById(R.id.uploadpicture_submit);
		flipper = (ViewFlipper) findViewById(R.id.uploadpicture_flipper);
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		Bundle needData = getIntent().getExtras();
		yid = needData.getString("yid");
		node = needData.getString("node");
		// 初始化数据
		for (int i = 0; i < smallImgPath.length; i++) {
			smallImgPath[i] = "";
			smallImgView[i].setOnClickListener(this);
			bigImgView[i].setImageResource(R.drawable.datu);
		}
		flipper.setOnTouchListener(this);
		flipper.setLongClickable(true);
		submit.setOnClickListener(this);
	}

	private void showSmallPicture() {
		String imgPath = smallImgPath[currentSelected];
		// 设置BitmapFactory的option
		BitmapFactory.Options options = new BitmapFactory.Options();
		// 为了可以得到真实图片的大小
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(imgPath, options);
		// 取得128像素的图片
		options.inSampleSize = options.outWidth / 128;
		// 不进行图片抖动处理，也可以优化内存
		options.inDither = false;
		// 设置设置Bitmap之前要变回false
		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeFile(imgPath, options);
		// 设置图片
		smallImgView[currentSelected].setImageBitmap(ImageUtil.toRoundCorner(
				bitmap, 5));
	}

	private void showBigPicture() {
		String imgPath = smallImgPath[currentSelected];
		// 我们decodeFile的时候一定要加这个option，不然很容易报oom
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(imgPath, opts);
		opts.inSampleSize = ImageUtil.computeSampleSize(opts, -1, 1280*1280);
		// 这里一定要将其设置回false，因为之前我们将其设置成了true
		opts.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeFile(imgPath, opts);
		// 设置图片
		bigImgView[currentSelected].setImageBitmap(ImageUtil.toRoundCorner(
				bitmap, 10));
	}

	@Override
	public String getThemeTitle() {
		// TODO Auto-generated method stub
		return "上传图片";
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.uploadpicture_smallimg_1:
			currentSelected = 0;
			selectPicture();
			break;
		case R.id.uploadpicture_smallimg_2:
			currentSelected = 1;
			selectPicture();
			break;
		case R.id.uploadpicture_smallimg_3:
			currentSelected = 2;
			selectPicture();
			break;
		case R.id.uploadpicture_smallimg_4:
			currentSelected = 3;
			selectPicture();
			break;
		case R.id.uploadpicture_submit:
			if (!ToolUtil.isNetworkAvailable(this)) {
				ToolUtil.dilog(this, "网络有问题，请先连接网络");
				return;
			}
			submitPicture();
			break;
		default:
			break;
		}
	}

	private void selectPicture() {
		if (selectPictureWayWindow == null) {
			selectPictureWayWindow = new SelectPictureWayWindow(this);
			selectPictureWayWindow.callBack = new UpdateViewHandle() {
				
				@Override
				public void Update(String value) {
					tempSelect = value;
				}
			};
		}
		selectPictureWayWindow.showAtLocation(
				findViewById(R.id.uploadpicture_layout), Gravity.BOTTOM, 0, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && (data != null || requestCode==SelectPictureWayWindow.SELECT_IMG_FROM_TAKE)) {
			switch (requestCode) {
			case SelectPictureWayWindow.SELECT_IMG_FROM_TAKE:
				if (TextUtils.isEmpty(tempSelect)) {
					ToastUtil.show(this, "拍照图片失败");
				}
				else {
					// 添加到
					smallImgPath[currentSelected] = tempSelect;
					showSmallPicture();
					showBigPicture();
					updateSubmitState();
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
				smallImgPath[currentSelected] = photoPath;
				showSmallPicture();
				showBigPicture();
				updateSubmitState();
				break;
			default:
				break;
			}
		}
	}

	private boolean isSelectedFull() {
		for (int i = 0; i < smallImgPath.length; i++) {
			if ("".equals(smallImgPath[i])) {
				return false;
			}
		}
		return true;
	}

	private void updateSubmitState() {
		if (isSelectedFull()) {
			submit.setEnabled(true);
		} else {
			submit.setEnabled(false);
		}
	}

	private void showPreviousView() {
		flipper.setInAnimation(AnimationUtils.loadAnimation(this,
				R.anim.slide_in_from_left));
		flipper.setOutAnimation(AnimationUtils.loadAnimation(this,
				R.anim.slide_out_to_left));
		flipper.showPrevious();
	}

	private void showNextView() {
		flipper.setInAnimation(AnimationUtils.loadAnimation(this,
				R.anim.slide_in_from_right));
		flipper.setOutAnimation(AnimationUtils.loadAnimation(this,
				R.anim.slide_out_to_right));
		flipper.showNext();
	}

	float startX;

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		// TODO Auto-generated method stub
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			startX = event.getX();
			break;
		case MotionEvent.ACTION_UP:
			if (event.getX() > startX) {
				showPreviousView();
			} else if (event.getX() < startX) {
				showNextView();
			}
			break;
		}
		return false;
	}

	private void submitPicture() {
		// 先保存到数据库，最后一次一起上传
		UploadOrderPictureBean uploadOrderPictureBean = new UploadOrderPictureBean(
				yid, node, smallImgPath[0] + "|" + smallImgPath[1] + "|"
						+ smallImgPath[2] + "|" + smallImgPath[3]);
		UploadOrderPictrueDatabase database = new UploadOrderPictrueDatabase(
				this);
		database.push(GsonUtil.objectToJson(uploadOrderPictureBean));
		// 返回
		Intent backIntent = new Intent();
		backIntent.putExtra("url", smallImgPath[0] + "|" + smallImgPath[1]
				+ "|" + smallImgPath[2] + "|" + smallImgPath[3]);
		setResult(RESULT_OK, backIntent);
		finish();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}

}
