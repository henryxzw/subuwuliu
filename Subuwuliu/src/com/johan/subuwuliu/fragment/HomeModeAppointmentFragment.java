package com.johan.subuwuliu.fragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.johan.subuwuliu.R;
import com.johan.subuwuliu.dialog.HomeModeWindow;
import com.johan.subuwuliu.dialog.OnSelectedListener;
import com.johan.subuwuliu.dialog.SelectAppointmentTimeDialog;
import com.johan.subuwuliu.fragment.HomeModeAppointmentFragment.AppointmentTimeBuilder.AppointmentTime;
import com.johan.subuwuliu.util.ToastUtil;
import com.umeng.analytics.MobclickAgent;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class HomeModeAppointmentFragment extends AppFragment {

	private ListView appointmentListView;

	private List<AppointmentTime> appointmentList = new ArrayList<AppointmentTime>();

	private AppointmentTimeAdapter adapter;

	@Override
	public int getContentView() {
		return R.layout.fragment_homemode_appointment;
	}

	@Override
	public void findId() {
		// TODO Auto-generated method stub
		appointmentListView = (ListView) layout
				.findViewById(R.id.homemode_appointment_list);
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		adapter = new AppointmentTimeAdapter();
		appointmentListView.setAdapter(adapter);
	}

	class AppointmentTimeBuilder {

		public AppointmentTime appointmentTime;

		public AppointmentTimeBuilder(int type) {
			appointmentTime = new AppointmentTime(type);
		}

		public AppointmentTimeBuilder setBeginTime(String beginTime) {
			appointmentTime.setBeginTime(beginTime);
			return this;
		}

		public AppointmentTimeBuilder setEndTime(String endTime) {
			appointmentTime.setEndTime(endTime);
			return this;
		}

		public AppointmentTime build() {
			return appointmentTime;
		}

		class AppointmentTime {
			public String beginTime = "";
			public String endTime = "";
			public int type;

			public AppointmentTime(int type) {
				this.type = type;
			}

			public void setType(int type) {
				this.type = type;
			}

			public void setBeginTime(String beginTime) {
				this.beginTime = beginTime;
			}

			public void setEndTime(String endTime) {
				this.endTime = endTime;
			}
		}
	}

	class AppointmentTimeAdapter extends BaseAdapter {

		public static final int TYPE_TIME = 1;
		public static final int TYPE_TIP = 2;
		public static final int TYPE_TIME_TIP = 3;
		public static final int COUNT = 4;

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return appointmentList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return appointmentList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public int getItemViewType(int position) {
			// TODO Auto-generated method stub
			return appointmentList.get(position).type;
		}

		@Override
		public int getViewTypeCount() {
			// TODO Auto-generated method stub
			return COUNT;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder viewHolder;
			ViewHolder2 viewHolder2;
			ViewHolder3 viewHolder3;
			int type = getItemViewType(position);
			switch (type) {
			case TYPE_TIME:
				if (convertView == null) {
					convertView = LayoutInflater.from(getActivity()).inflate(
							R.layout.item_homemode_appointment_list_1, null);
					viewHolder = new ViewHolder();
					viewHolder.beginTime = (TextView) convertView
							.findViewById(R.id.item_appointment_begin_time);
					viewHolder.endTime = (TextView) convertView
							.findViewById(R.id.item_appointment_end_time);
					viewHolder.beginDelete = (ImageView) convertView
							.findViewById(R.id.item_appointment_begin_delete);
					viewHolder.endDelete = (ImageView) convertView
							.findViewById(R.id.item_appointment_end_delete);
					convertView.setTag(viewHolder);
				} else {
					viewHolder = (ViewHolder) convertView.getTag();
				}
				AppointmentTime curretAppointmentTime = appointmentList
						.get(position);
				viewHolder.beginTime
						.setText(parseTime(curretAppointmentTime.beginTime));
				viewHolder.endTime
						.setText(parseTime(curretAppointmentTime.endTime));
				viewHolder.endDelete.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						appointmentList.remove(position);
						if (appointmentList.size() == 0
								|| appointmentList.get(appointmentList.size() - 1).type == TYPE_TIME) {
							AppointmentTime appointmentTime = new AppointmentTimeBuilder(
									AppointmentTimeAdapter.TYPE_TIP).build();
							appointmentList.add(appointmentTime);
						}
						adapter.notifyDataSetChanged();
					}
				});
				viewHolder.beginDelete
						.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								appointmentList.remove(position);
								if (appointmentList.size() == 0
										|| appointmentList.get(appointmentList
												.size() - 1).type == TYPE_TIME) {
									AppointmentTime appointmentTime = new AppointmentTimeBuilder(
											AppointmentTimeAdapter.TYPE_TIP)
											.build();
									appointmentList.add(appointmentTime);
								}
								adapter.notifyDataSetChanged();
							}
						});
				break;
			case TYPE_TIP:
				if (convertView == null) {
					convertView = LayoutInflater.from(getActivity()).inflate(
							R.layout.item_homemode_appointment_list_2, null);
					viewHolder2 = new ViewHolder2();
					viewHolder2.beginTip = (TextView) convertView
							.findViewById(R.id.item_appointment_begin_time_title);
					viewHolder2.endTip = (TextView) convertView
							.findViewById(R.id.item_appointment_end_time_title);
					convertView.setTag(viewHolder2);
				} else {
					viewHolder2 = (ViewHolder2) convertView.getTag();
				}
				viewHolder2.beginTip.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						MyDate lastDate = getLastDate();
						SelectAppointmentTimeDialog dialog = new SelectAppointmentTimeDialog(
								getActivity(), getBeginDay(), lastDate.year,
								lastDate.month, lastDate.day, lastDate.hour);
						dialog.setOnSelectedListener(new OnSelectedListener() {
							@Override
							public void onSelected(String selectedData) {
								// TODO Auto-generated method stub
								System.out.println("您选择的时间-->" + selectedData);
								AppointmentTime curretAppointmentTime = appointmentList
										.get(position);
								curretAppointmentTime
										.setBeginTime(selectedData);
								curretAppointmentTime.setType(TYPE_TIME_TIP);
								adapter.notifyDataSetChanged();

							}
						});
						dialog.show(((HomeModeWindow) getActivity())
								.getParentView());
					}
				});
				viewHolder2.endTip.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						ToastUtil.show(getActivity(), "请先选择开始时间");
					}
				});
				break;
			case TYPE_TIME_TIP:
				if (convertView == null) {
					convertView = LayoutInflater.from(getActivity()).inflate(
							R.layout.item_homemode_appointment_list_3, null);
					viewHolder3 = new ViewHolder3();
					viewHolder3.beginTime = (TextView) convertView
							.findViewById(R.id.item_appointment_begin_time);
					viewHolder3.endTip = (TextView) convertView
							.findViewById(R.id.item_appointment_end_time_title);
					viewHolder3.beginDelete = (ImageView) convertView
							.findViewById(R.id.item_appointment_begin_delete);
					convertView.setTag(viewHolder3);
				} else {
					viewHolder3 = (ViewHolder3) convertView.getTag();
				}
				AppointmentTime curretAppointmentTime3 = appointmentList
						.get(position);
				viewHolder3.beginTime
						.setText(parseTime(curretAppointmentTime3.beginTime));
				viewHolder3.beginDelete
						.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								appointmentList.remove(position);
								if (appointmentList.size() < 5) {
									AppointmentTime appointmentTime = new AppointmentTimeBuilder(
											AppointmentTimeAdapter.TYPE_TIP)
											.build();
									appointmentList.add(appointmentTime);
								}
								adapter.notifyDataSetChanged();
							}
						});
				viewHolder3.endTip.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						MyDate lastDate = getLastDate();
						SelectAppointmentTimeDialog dialog = new SelectAppointmentTimeDialog(
								getActivity(), getBeginDay(), lastDate.year,
								lastDate.month, lastDate.day, lastDate.hour);
						dialog.setOnSelectedListener(new OnSelectedListener() {
							@Override
							public void onSelected(String selectedData) {
								// TODO Auto-generated method stub
								System.out.println("您选择的时间-->" + selectedData);
								AppointmentTime curretAppointmentTime = appointmentList
										.get(position);
								curretAppointmentTime.setEndTime(selectedData);
								curretAppointmentTime.setType(TYPE_TIME);
								// 自动添加一个新的
								if (appointmentList.size() < 5) {
									AppointmentTime appointmentTime = new AppointmentTimeBuilder(
											AppointmentTimeAdapter.TYPE_TIP)
											.build();
									appointmentList.add(appointmentTime);
								}
								adapter.notifyDataSetChanged();
							}
						});
						dialog.show(((HomeModeWindow) getActivity())
								.getParentView());
					}
				});
				break;
			default:
				break;
			}

			return convertView;
		}

		public class ViewHolder {
			public TextView beginTime;
			public TextView endTime;
			public ImageView beginDelete;
			public ImageView endDelete;
		}

		public class ViewHolder2 {
			public TextView beginTip;
			public TextView endTip;
		}

		public class ViewHolder3 {
			public TextView beginTime;
			public ImageView beginDelete;
			public TextView endTip;
		}

	}

	public void setData(String appointmentTimeInfo) {
		appointmentList.clear();
		// 解析时间
		if (!"".equals(appointmentTimeInfo)) {
			String[] timeInfos = appointmentTimeInfo.split("\\|");
			for (String timeInfo : timeInfos) {
				String[] infos = timeInfo.split(",");
				if (infos != null && infos.length == 2) {
					AppointmentTime appointmentTimes = new AppointmentTimeBuilder(
							AppointmentTimeAdapter.TYPE_TIME)
							.setBeginTime(infos[0]).setEndTime(infos[1])
							.build();
					appointmentList.add(appointmentTimes);
				}
			}
			// 排序
			Collections.sort(appointmentList,
					new Comparator<AppointmentTime>() {
						@Override
						public int compare(AppointmentTime left,
								AppointmentTime right) {
							// TODO Auto-generated method stub
							if (Long.parseLong(left.beginTime) > Long
									.parseLong(right.beginTime)) {
								return 1;
							} else {
								return -1;
							}
						}
					});
			if (appointmentList.size() < 5) {
				AppointmentTime appointmentTime = new AppointmentTimeBuilder(
						AppointmentTimeAdapter.TYPE_TIP).build();
				appointmentList.add(appointmentTime);
			}
		} else {
			AppointmentTime appointmentTime = new AppointmentTimeBuilder(
					AppointmentTimeAdapter.TYPE_TIP).build();
			appointmentList.add(appointmentTime);
		}
	}

	public String parseTime(String time) {
		if (time.length() < 8) {
			return "0";
		}
		return time.substring(4, 6) + "日" + time.substring(6, 8) + "时";
	}

	public int getYear(String time) {
		if (time.length() < 8) {
			return 0;
		}
		return Integer.parseInt("20" + time.substring(0, 2));
	}

	public int getMonth(String time) {
		if (time.length() < 8) {
			return 0;
		}
		return Integer.parseInt(time.substring(2, 4));
	}

	public int getDay(String time) {
		if (time.length() < 8) {
			return 0;
		}
		return Integer.parseInt(time.substring(4, 6));
	}

	public int getHour(String time) {
		if (time.length() < 8) {
			return 0;
		}
		return Integer.parseInt(time.substring(6, 8));
	}

	private MyDate getLastDate() {
		if (appointmentList.size() < 2) {
			if (appointmentList.get(0).type == AppointmentTimeAdapter.TYPE_TIP) {
				Calendar calendar = Calendar.getInstance();
				calendar.setTimeInMillis(System.currentTimeMillis());
				return new MyDate(calendar.get(Calendar.YEAR),
						calendar.get(Calendar.MONTH) + 1,
						calendar.get(Calendar.DAY_OF_MONTH),
						calendar.get(Calendar.HOUR_OF_DAY));
			} else {
				String lastTime = appointmentList.get(0).beginTime;
				return new MyDate(getYear(lastTime), getMonth(lastTime),
						getDay(lastTime), getHour(lastTime));
			}
		}
		AppointmentTime lastAppointmentTime;
		if (appointmentList.get(appointmentList.size() - 1).type == AppointmentTimeAdapter.TYPE_TIME_TIP) {
			lastAppointmentTime = appointmentList
					.get(appointmentList.size() - 1);
			String lastTime = lastAppointmentTime.beginTime;
			return new MyDate(getYear(lastTime), getMonth(lastTime),
					getDay(lastTime), getHour(lastTime));
		} else {
			lastAppointmentTime = appointmentList
					.get(appointmentList.size() - 2);
			String lastTime = lastAppointmentTime.endTime;
			return new MyDate(getYear(lastTime), getMonth(lastTime),
					getDay(lastTime), getHour(lastTime));
		}
	}

	private int getBeginDay() {
		if (appointmentList.get(0).type == AppointmentTimeAdapter.TYPE_TIP) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(System.currentTimeMillis());
			return calendar.get(Calendar.DAY_OF_MONTH);
		} else {
			return getDay(appointmentList.get(0).beginTime);
		}
	}

	class MyDate {
		public int year;
		public int month;
		public int day;
		public int hour;

		public MyDate(int year, int month, int day, int hour) {
			this.year = year;
			this.month = month;
			this.day = day;
			this.hour = hour;
		}
	}

	public String getAppointmentTime() {
		StringBuffer buffer = new StringBuffer();
		for (AppointmentTime appointmentTimeInfo : appointmentList) {
			if (appointmentTimeInfo.type == AppointmentTimeAdapter.TYPE_TIME) {
				buffer.append(appointmentTimeInfo.beginTime).append(",")
						.append(appointmentTimeInfo.endTime).append("|");
			}
		}
		String allAppointmentTime = buffer.toString();
		System.out.println("appointment time : " + allAppointmentTime);
		if (allAppointmentTime.length() == 0) {
			return "";
		}

		return allAppointmentTime.substring(0, allAppointmentTime.length() - 1);
	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("HomeModeAppointmentFragment"); // 统计页面，"MainScreen"为页面名称，可自定义
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("HomeModeAppointmentFragment");
	}
}
