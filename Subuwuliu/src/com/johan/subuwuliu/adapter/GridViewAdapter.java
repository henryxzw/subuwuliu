package com.johan.subuwuliu.adapter;

import java.util.ArrayList;
import java.util.List;

import com.johan.subuwuliu.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class GridViewAdapter extends BaseAdapter {
	
	private Context context;
	
	private List<String> dataList;
	
	private int max;
	
	public GridViewAdapter(Context context, int max) {
		this.context = context;
		this.max = max;
		this.dataList = new ArrayList<String>();
		//预添加一个按钮
		this.dataList.add("add");
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return dataList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return dataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if(convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.item_gridview, null);
			holder = new ViewHolder();
			holder.img = (ImageView) convertView.findViewById(R.id.item_gridview_img);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if(position == getCount() - 1) {
			holder.img.setImageResource(R.drawable.zc_sc);
			return convertView;
		}
		String imgPath = dataList.get(position);
		//设置BitmapFactory的option
		BitmapFactory.Options options = new BitmapFactory.Options();
		//为了可以得到真实图片的大小
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(imgPath, options);
		//取得128像素的图片
		options.inSampleSize = options.outWidth / 128;
		//不进行图片抖动处理，也可以优化内存
		options.inDither = false;
		//设置设置Bitmap之前要变回false
		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeFile(imgPath, options);
		//设置图片
		holder.img.setImageBitmap(bitmap);
		return convertView;
	}
	
	public class ViewHolder {
		ImageView img;
	}
	
	/**
	 * 暂时只需要一下两个方法
	 */
	public void addData(String newData) {
		if(dataList.size() == max + 1) {
			//移除倒数第二张
			dataList.remove(max - 1);
		}
		this.dataList.add(0, newData);
		notifyDataSetChanged();
	}
	
	public List<String> getDatas() {
		return this.dataList;
	}

}
