package com.johan.subuwuliu.fragment;

import java.util.ArrayList;
import java.util.List;

import com.johan.subuwuliu.DriverRecordDetailActivity;
import com.johan.subuwuliu.R;
import com.johan.subuwuliu.adapter.PictureAdapter;
import com.johan.subuwuliu.bean.DriverRouteBean.DriverRouteInfoBean;
import com.johan.subuwuliu.util.FormatUtil;
import com.johan.subuwuliu.view.ScrollGridView;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class DriverRecordRouteFragment extends AppFragment {
	
	private ListView routeListView;
	private List<DriverRoute> routeList = new ArrayList<DriverRoute>();
	private DriverRouteAdapter adapter;
	
	private DriverRouteInfoBean driverRouteInfo;
	
	public void setDriverRouteInfoBean(DriverRouteInfoBean driverRouteInfo) {
		this.driverRouteInfo = driverRouteInfo;
	}
	
	@Override
	public int getContentView() {
		// TODO Auto-generated method stub
		return R.layout.fragment_driverrecordroute;
	}

	@Override
	public void findId() {
		// TODO Auto-generated method stub
		routeListView = (ListView) layout.findViewById(R.id.driverrecordroute_list);
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		TextView footView = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.layout_driverroutefoot, null);
		footView.setText(driverRouteInfo.driver_comment);
		routeListView.addFooterView(footView);
		adapter = new DriverRouteAdapter();
		routeListView.setAdapter(adapter);
		initData();
	}
	
	private void initData() {
		routeList.clear();
		String phone_a = "";
		String phone_a1 = "";
		String phone_a2 = "";
		String phone_a3 = "";
		String phone_a4 = "";
		String phone_a5 = "";
		String phone_a6 = "";
		if (!driverRouteInfo.start_contact.trim().equals("")) {
			String[] addrs = driverRouteInfo.start_contact.split("\\|");
			if(addrs.length>=1)
			{
			phone_a= addrs[0];
			}
			if(addrs.length==2)
			{
			  phone_a+=(" "+driverRouteInfo.start_contact.split("\\|")[1]);
			}
		}
		if (!driverRouteInfo.end_contact.trim().equals("")) {
			String[] addrs = driverRouteInfo.end_contact.split("\\|");
			if(addrs.length>=1)
			{
				phone_a6=addrs[0];
			}
			if(addrs.length==2)
			{
					 phone_a6+=(" "+driverRouteInfo.end_contact.split("\\|")[1]);
			}
		}
		
		DriverRoute route1 = new DriverRoute(driverRouteInfo.address_start, phone_a, driverRouteInfo.start_img, driverRouteInfo.start_time, driverRouteInfo.start_state);
		routeList.add(route1);
		if(!"".equals(driverRouteInfo.address_midway1.trim())) {
			if (!driverRouteInfo.midway1_contact.trim().equals("")) {
				phone_a1=driverRouteInfo.midway1_contact.split("\\|")[0]+" "+driverRouteInfo.midway1_contact.split("\\|")[1];
			}
			DriverRoute route2 = new DriverRoute(driverRouteInfo.address_midway1, phone_a1, driverRouteInfo.midway1_img, driverRouteInfo.midway1_time, driverRouteInfo.midway1_state);
			routeList.add(route2);
		}
		if(!"".equals(driverRouteInfo.address_midway2)) {
			if (!driverRouteInfo.midway2_contact.trim().equals("")) {
				phone_a2=driverRouteInfo.midway2_contact.split("\\|")[0]+" "+driverRouteInfo.midway2_contact.split("\\|")[1];
			}
			DriverRoute route3 = new DriverRoute(driverRouteInfo.address_midway2, phone_a2, driverRouteInfo.midway2_img, driverRouteInfo.midway2_time, driverRouteInfo.midway2_state);
			routeList.add(route3);
		}
		if(!"".equals(driverRouteInfo.address_midway3.trim())) {
			if (!driverRouteInfo.midway3_contact.trim().equals("")) {
				phone_a3=driverRouteInfo.midway3_contact.split("\\|")[0]+" "+driverRouteInfo.midway3_contact.split("\\|")[1];
			}
			DriverRoute route4 = new DriverRoute(driverRouteInfo.address_midway3, phone_a3, driverRouteInfo.midway3_img, driverRouteInfo.midway3_time, driverRouteInfo.midway3_state);
			routeList.add(route4);
		}
		if(!"".equals(driverRouteInfo.address_midway4)) {
			if (!driverRouteInfo.midway4_contact.trim().equals("")) {
				phone_a4=driverRouteInfo.midway4_contact.split("\\|")[0]+" "+driverRouteInfo.midway4_contact.split("\\|")[1];
			}
			DriverRoute route5 = new DriverRoute(driverRouteInfo.address_midway4, phone_a4, driverRouteInfo.midway4_img, driverRouteInfo.midway4_time, driverRouteInfo.midway4_state);
			routeList.add(route5);
		}
		if(!"".equals(driverRouteInfo.address_midway5.trim())) {
			if (!driverRouteInfo.midway5_contact.trim().equals("")) {
				phone_a5=driverRouteInfo.midway5_contact.split("\\|")[0]+" "+driverRouteInfo.midway5_contact.split("\\|")[1];
			}
			DriverRoute route6 = new DriverRoute(driverRouteInfo.address_midway5, phone_a5, driverRouteInfo.midway5_img, driverRouteInfo.midway5_time, driverRouteInfo.midway5_state);
			routeList.add(route6);
		}
		DriverRoute route7 = new DriverRoute(driverRouteInfo.address_end, phone_a6, driverRouteInfo.end_img, driverRouteInfo.end_time, driverRouteInfo.end_state);
		routeList.add(route7);
		adapter.notifyDataSetChanged();
	}
	
	public class DriverRouteAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return routeList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return routeList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder viewHolder;
			if(convertView == null) {
				convertView = LayoutInflater.from(getActivity()).inflate(R.layout.item_driverrecordroute, null);
				viewHolder = new ViewHolder();
				viewHolder.icon = (ImageView) convertView.findViewById(R.id.item_driverrecordroute_icon);
				viewHolder.place = (TextView) convertView.findViewById(R.id.item_driverrecordroute_place);
				viewHolder.contact = (TextView) convertView.findViewById(R.id.item_driverrecordroute_contact);
				viewHolder.picture = (ScrollGridView) convertView.findViewById(R.id.item_driverrecordroute_picture_list);
				viewHolder.time = (TextView) convertView.findViewById(R.id.item_driverrecordroute_time);
				viewHolder.line = convertView.findViewById(R.id.item_driverrecordroute_line);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			DriverRoute driverRouteInfo = routeList.get(position);
			viewHolder.place.setText(driverRouteInfo.place);
			viewHolder.contact.setText(FormatUtil.formatContact(driverRouteInfo.contact));
			if("2".equals(driverRouteInfo.state)) {
				viewHolder.picture.setVisibility(View.VISIBLE);
				viewHolder.time.setVisibility(View.VISIBLE);
				DriverRecordDetailActivity activity = (DriverRecordDetailActivity) getActivity();
				PictureAdapter pictureAdapter = new PictureAdapter(getActivity(), driverRouteInfo.pictureList, activity.getParentView());
				viewHolder.picture.setAdapter(pictureAdapter);
				viewHolder.time.setText(driverRouteInfo.time);
				viewHolder.icon.setImageResource(R.drawable.ywcd);
			} else {
				viewHolder.picture.setVisibility(View.GONE);
				viewHolder.time.setVisibility(View.GONE);
				viewHolder.icon.setImageResource(R.drawable.ywcd_g);
			}	
			if(position == routeList.size() - 1) {
				viewHolder.line.setBackgroundColor(Color.WHITE);
			} else if("1".equals(routeList.get(position+1).state) && "1".equals(routeList.get(position).state)) {
				viewHolder.line.setBackgroundColor(Color.WHITE);
			} else if("1".equals(routeList.get(position+1).state) && "2".equals(routeList.get(position).state)) {
				viewHolder.line.setBackgroundColor(Color.parseColor("#aeaead"));
			} else {
				viewHolder.line.setBackgroundColor(Color.parseColor("#84C148"));
			}
			return convertView;
		}
		
		public class ViewHolder {
			public ImageView icon;
			public TextView place;
			public TextView contact;
			public ScrollGridView picture;
			public TextView time;
			public View line;
		}
		
	}
	
	class DriverRoute {
		public String place;
		public String contact;
		public List<String> pictureList;
		public String time;
		public String state;
		public DriverRoute(String place, String contact, String picture, String time, String state) {
			this.place = FormatUtil.formatPlace(place);
			this.contact = FormatUtil.formatContact(contact);
			this.pictureList = FormatUtil.formatPicture(picture);
			this.time = FormatUtil.formatTime(time, "MM-dd HH:mm");
			this.state = state;
		}
	}

}
