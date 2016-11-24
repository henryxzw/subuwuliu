package com.johan.subuwuliu.bean;

import java.util.List;

public class DriverRecordBean {

	public String status;
	public String timestamp;
	public String msg;
	public int totalcount;
	public List<DriverRecordListBean> list;
	
	public static class DriverRecordListBean {
		public String record_status;
		public String id;
		public String yid;
		public String order_no;
		public String use_time;
		public String address_start;
		public String address_end;
		public String plate_number;
		public String car_type;
		public String order_amount;
		public String address;
		public String contact;
		public String remain;
		public String is_comment;
		public String car_status;
	}
	
}
