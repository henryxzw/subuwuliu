package com.johan.subuwuliu.bean;

public class ModeBean {

	public String status;
	public String timestamp;
	public String msg;
	public ModeInfoBean info;
	
	public static class ModeInfoBean {
		public String driver_id;
		public String mode;
		public String back_city;
		public String back_addr;
		public String booking_time;
		public String is_backcar;
	}
	
}
