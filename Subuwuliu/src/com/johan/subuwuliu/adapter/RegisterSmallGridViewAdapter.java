package com.johan.subuwuliu.adapter;

import java.util.ArrayList;
import java.util.List;

import com.johan.subuwuliu.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class RegisterSmallGridViewAdapter extends BaseAdapter {
	
	private static final int TYPE_CONTENT = 0;
	private static final int TYPE_BUTTON = 1;

	private List<String> dataList;
	private LayoutInflater inflater;
	
	private OnClickListener addClickListener;
	
	public RegisterSmallGridViewAdapter(Context context, OnClickListener addClickListener) {
		this.dataList = new ArrayList<String>();
		this.dataList.add("add");
		this.inflater = LayoutInflater.from(context);
		this.addClickListener = addClickListener;
	}

	@Override
	public int getCount() {
		return dataList.size();
	}

	@Override
	public Object getItem(int position) {
		return dataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemViewType(int position) {
		if(position == dataList.size() - 1) {
			return TYPE_BUTTON;
		}
		return TYPE_CONTENT;
	}

	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 2;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder = null;
		AddViewHolder addViewHolder = null;
		int type = getItemViewType(position);
		if(convertView == null) {
			switch (type) {
			case TYPE_CONTENT :
				convertView = inflater.inflate(R.layout.item_registerdriver_content, null);
				viewHolder = new ViewHolder();
				viewHolder.contentView = (TextView) convertView.findViewById(R.id.item_content);
				viewHolder.deleteIcon = (ImageView) convertView.findViewById(R.id.item_icon);
				convertView.setTag(viewHolder);
				break;
			case TYPE_BUTTON :
				convertView = inflater.inflate(R.layout.item_registerdriver_button, null);
				addViewHolder = new AddViewHolder();
				addViewHolder.addBut = (ImageView) convertView.findViewById(R.id.item_but);
				convertView.setTag(addViewHolder);
				break;
			default:
				break;
			}
		} else {
			switch (type) {
			case TYPE_CONTENT :
				viewHolder = (ViewHolder) convertView.getTag();
				break;
			case TYPE_BUTTON :
				addViewHolder = (AddViewHolder) convertView.getTag();
			default:
				break;
			}
		}
		switch (type) {
		case TYPE_CONTENT :
			String cityName = dataList.get(position);
			viewHolder.contentView.setText(cityName);
			viewHolder.deleteIcon.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dataList.remove(position);
					notifyDataSetChanged();
				}
			});
			break;
		case TYPE_BUTTON :
			addViewHolder.addBut.setOnClickListener(addClickListener);
			break;
		default:
			break;
		}
		return convertView;
	}
	
	class ViewHolder {
		public TextView contentView;
		public ImageView deleteIcon;
	}
	
	class AddViewHolder {
		public ImageView addBut;
	}
	
	public void addData(String newData) {
		dataList.add(0, newData);
		notifyDataSetChanged();
	}
	
	public List<String> getDatas() {
		return dataList;
	}
	
}
