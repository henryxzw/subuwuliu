package com.johan.subuwuliu.bean;

import java.io.Serializable;
import java.util.List;

public class OrderBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	public String status;
	public String msg;
	public OrderDetailBean order;
	
	public static class OrderDetailBean implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public String spvan_quota;
		public String order_amount;
		public String timestamp;
		public String distance;
		public String order_no;
		public String payment_time;
		public String address_start;
		public String address_end;
		public String address_midway1;
		public String address_midway2;
		public String address_midway3;
		public String address_midway4;
		public String address_midway5;
		public String address_start_contact;
		public String address_end_contact;
		public String address_midway1_contact;
		public String address_midway2_contact;
		public String address_midway3_contact;
		public String address_midway4_contact;
		public String address_midway5_contact;
		public String trip_amount;
		public String contacts_name;
		public String contacts_mobile;
		public String accept_name;
		public String accept_mobile;
		public String use_time;
		public String goods_type;
		public String pack;
		public String extra_request;
		public String request_amount;
		public String cartype;
		public String car_quantity;
		public String is_up_goods;
		public String is_down_goods;
		public String is_supercargo;
		public String supercargo_number;
		public String is_up_floor;
		public String floor_number;
		public String is_lift;
		public String carry_amount;
		public String remark;
		public String img_url;
		public List<OrderDetailCarBean> car_list;
	}
	
	public static class OrderDetailCarBean implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public String driver_avatar;
		public String driver_name;
		public String star;
		public String plate_number;
		public String state;
		public String ismycar;
		public String start_time;
		public String sign_time;
		public String driver_id;
		public String mobile;
	}
	
}
