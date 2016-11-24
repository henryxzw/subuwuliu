package com.johan.subuwuliu.view;

import com.johan.subuwuliu.R;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class OrderWayLayout extends LinearLayout {

	public static final int WAY_BEGIN = 1;
	public static final int WAY_MIDDLE = 2;
	public static final int WAY_END = 3;
	
	private Context context;
	
	public OrderWayLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		this.context = context;
		setOrientation(LinearLayout.VERTICAL);
	}

	public OrderWayLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
		setOrientation(LinearLayout.VERTICAL);
	}

	public void add(int way, String place, String contact) {
		if("".equals(place)) {
			return;
		}
		RelativeLayout layout = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.item_orderway, null);
		TextView placeView = (TextView) layout.findViewById(R.id.item_orderway_place);
		TextView contactView = (TextView) layout.findViewById(R.id.item_orderway_contact);
		ImageView iconView = (ImageView) layout.findViewById(R.id.item_orderway_icon);
		View line = layout.findViewById(R.id.item_orderway_line);
		String[] placeInfo = place.split("\\|");
		if(placeInfo != null && placeInfo.length == 3) {
			placeView.setText(placeInfo[0]);
		}
		String[] contactInfo = contact.split("\\|");
		if(contactInfo != null && contactInfo.length == 2) {
			contactView.setText(contactInfo[0] + "  " + contactInfo[1]);
		} else {
			contactView.setVisibility(View.GONE);
		}
		switch (way) {
		case WAY_BEGIN:
			iconView.setImageResource(R.drawable.qidian1);
			break;
		case WAY_MIDDLE:
			iconView.setImageResource(R.drawable.yuan);
			break;
		case WAY_END:
			iconView.setImageResource(R.drawable.zhongdian1);
			line.setBackgroundColor(Color.WHITE);
			break;
		default:
			break;
		}
		this.addView(layout);
	}
	
}
