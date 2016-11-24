package com.johan.subuwuliu.dialog;

import java.util.Iterator;
import java.util.List;

import com.johan.subuwuliu.R;
import com.johan.subuwuliu.adapter.GridViewAdapter;
import com.johan.subuwuliu.util.ImageUtil;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class ShowPictureWindow extends PopupWindow implements OnClickListener {
	
	private Activity activity;
	
	private ImageView showImg;
	
	private String imgPath;
	
	private GridViewAdapter adapter;
	
	@SuppressWarnings("deprecation")
	public ShowPictureWindow(Activity activity) {
		this.activity = activity;
		DisplayMetrics metrics = new DisplayMetrics();  
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics );  
        int screenWidth = metrics.widthPixels;  
        int screenHeight = metrics.heightPixels;  
		View view = LayoutInflater.from(activity).inflate(R.layout.layout_show_picture, null);
		setContentView(view);
		setWidth(screenWidth);
		setHeight(screenHeight - getStatusBarHeight());
		ImageView showBack = (ImageView) view.findViewById(R.id.show_picture_back);
		TextView showDelete = (TextView) view.findViewById(R.id.show_picture_delete);
		showImg = (ImageView) view.findViewById(R.id.show_picture_img);
		showBack.setOnClickListener(this);
		showDelete.setOnClickListener(this);
		setBackgroundDrawable(new BitmapDrawable());
		setFocusable(true);
	}
	
	public void show(View parentView) {
		showAtLocation(parentView, Gravity.CENTER, 0, getStatusBarHeight());
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.show_picture_back) {
			dismiss();
		} else if(v.getId() == R.id.show_picture_delete) {
			if(adapter != null) {
				List<String> dataList = adapter.getDatas();
				Iterator<String> iterator = dataList.iterator();
				while(iterator.hasNext()) {
					String currentImgPath = iterator.next();
					if(currentImgPath.equals(imgPath)) {
						iterator.remove();
						adapter.notifyDataSetChanged();
						break;
					}
				}
			}
			dismiss();
		}
	}
	
	public void setAdapter(GridViewAdapter adapter) {
		this.adapter = adapter;
	}
	
	public void setImage(String imgPath) {
		this.imgPath = imgPath;
		//我们decodeFile的时候一定要加这个option，不然很容易报oom
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(imgPath, opts);
		opts.inSampleSize = ImageUtil.computeSampleSize(opts, -1, 512*512);
		//这里一定要将其设置回false，因为之前我们将其设置成了true      
		opts.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeFile(imgPath, opts);
		//设置图片
		showImg.setImageBitmap(bitmap);
	}
	
	/**
	 * 获取状态栏高度
	 * @return
	 */
	public int getStatusBarHeight() {  
        int result = 0;  
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");  
        if (resourceId > 0) {  
            result = activity.getResources().getDimensionPixelSize(resourceId);  
        }  
        return result;  
    }  

}
