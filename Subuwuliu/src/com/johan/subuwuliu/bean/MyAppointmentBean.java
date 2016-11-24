package com.johan.subuwuliu.bean;

import java.util.List;

public class MyAppointmentBean {

	public String status;
	public String timestamp;
	public String msg;
	public int totalcount;
	public List<MyAppointmentDetailBean> list;

	public static class MyAppointmentDetailBean {
		public String id;
		public String yid;
		public String order_no;
		public String use_time;
		public String address_start;
		public String address_end;
		public String plate_number;
		public String car_type;
		public String order_amount;
	}
	
}
