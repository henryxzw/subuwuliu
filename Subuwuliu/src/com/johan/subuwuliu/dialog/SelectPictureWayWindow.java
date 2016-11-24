package com.johan.subuwuliu.dialog;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.johan.subuwuliu.R;
import com.johan.subuwuliu.application.DiDiApplication;
import com.johan.subuwuliu.handler.UpdateViewHandle;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class SelectPictureWayWindow extends PopupWindow implements OnClickListener {
	
	public static final int SELECT_IMG_FROM_TAKE = 10;
	public static final int SELECT_IMG_FROM_PHOTO = 20;

	private Activity activity;
	
	private TextView take, photo, cancel;
	public UpdateViewHandle callBack;
	
	public SelectPictureWayWindow(Activity activity) {
		this.activity = activity;
		init();
	}
	
	private void init() {
		//设置长宽
		setWidth(LayoutParams.MATCH_PARENT);
		setHeight(LayoutParams.WRAP_CONTENT);
		//设置布局
		View layout = LayoutInflater.from(activity.getApplicationContext()).inflate(R.layout.layout_select_pictrue_way, null);
		take = (TextView) layout.findViewById(R.id.select_picture_way_take);
		photo = (TextView) layout.findViewById(R.id.select_picture_way_photo);
		cancel = (TextView) layout.findViewById(R.id.select_picture_way_cancel);
		take.setOnClickListener(this);
		photo.setOnClickListener(this);
		cancel.setOnClickListener(this);
		setContentView(layout);
		//其他设置
		setBackgroundDrawable(new BitmapDrawable());
		setOutsideTouchable(true);
		setFocusable(true); 
		setAnimationStyle(R.style.DiDiPopupWindowAnimStyle);
	}
	
	public void backgroundAlpha(float bgAlpha)  {  
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();  
        lp.alpha = bgAlpha; //0.0-1.0  
        activity.getWindow().setAttributes(lp);  
    }  
	
	@Override
	public void showAtLocation(View parent, int gravity, int x, int y) {
		// TODO Auto-generated method stub
		super.showAtLocation(parent, gravity, x, y);
		backgroundAlpha(0.5f);
	}
	
	
	@Override
	public void dismiss() {
		// TODO Auto-generated method stub
		super.dismiss();
		backgroundAlpha(1);
	}
	
	
	private File takePicture() {
		if (!android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			Toast.makeText(activity, "请插入SD卡", Toast.LENGTH_LONG).show();
			return null; 
		}
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		String takePicturePath = Environment.getExternalStorageDirectory()
				+ "/" + DiDiApplication.PROJECT_DIR ;
		File takePictureDir = new File(takePicturePath);
		if (!takePictureDir.isDirectory()) {
			takePictureDir.mkdir();
		}
		File file = new File(takePictureDir, new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())+ ".jpg");
		if(callBack!=null)
	    {
	    	callBack.Update(file.getAbsolutePath());

			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
	    }
		else {
			
		}
		activity.startActivityForResult(intent, SELECT_IMG_FROM_TAKE);
		 
			
		return file;
	}
	
	private void chooseImage() {
		Intent openAlbumIntent = new Intent();
		openAlbumIntent.setType("image/*");
		openAlbumIntent.setAction(Intent.ACTION_GET_CONTENT);
		activity.startActivityForResult(openAlbumIntent, SELECT_IMG_FROM_PHOTO);
	}

	@Override
	public void onClick(View v) {
		dismiss();
		switch (v.getId()) {
		case R.id.select_picture_way_take :
			takePicture();
			break;
		case R.id.select_picture_way_photo :
			chooseImage();
			break;
		case R.id.select_picture_way_cancel :
			break;
		default:
			break;
		}
	}
	
}
