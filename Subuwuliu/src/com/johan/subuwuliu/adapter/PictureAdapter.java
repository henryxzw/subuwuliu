package com.johan.subuwuliu.adapter;

import java.util.List;

import com.johan.subuwuliu.R;
import com.johan.subuwuliu.dialog.ShowPhotoWindow;
import com.johan.subuwuliu.qiniu.Myupdate;
import com.johan.subuwuliu.util.NetUtil;
import com.johan.subuwuliu.util.ToastUtil;
import com.johan.subuwuliu.view.RoundImageView;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class PictureAdapter extends BaseAdapter {

	private Activity activity;
	private List<String> pictureList;

	private View parentView;
	private ShowPhotoWindow showPhotoWindow;

	private boolean isShwoBigPicture = true;

	public PictureAdapter(Activity activity, List<String> pictureList,
			View parenView) {
		this.activity = activity;
		this.pictureList = pictureList;
		this.parentView = parenView;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return pictureList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return pictureList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = LayoutInflater.from(activity).inflate(
					R.layout.item_gridview, null);
			viewHolder = new ViewHolder();
			viewHolder.carPicture = (RoundImageView) convertView
					.findViewById(R.id.item_gridview_img);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		NetUtil.displayImageFromUrl(activity, viewHolder.carPicture,
				Myupdate.DownloadDemo(pictureList.get(position)));
		final String aaa = Myupdate.DownloadDemo(pictureList.get(position));
		viewHolder.carPicture.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isShwoBigPicture) {
					showPhoto(aaa);

				}
			}
		});
		return convertView;
	}

	public class ViewHolder {
		public RoundImageView carPicture;
	}

	private void showPhoto(String url) {
		if (showPhotoWindow == null) {
			showPhotoWindow = new ShowPhotoWindow(activity);
		}
		showPhotoWindow.show(parentView, url);
	}

	public void setShowPicture(boolean isShowBigPicture) {
		this.isShwoBigPicture = isShowBigPicture;
	}

}
