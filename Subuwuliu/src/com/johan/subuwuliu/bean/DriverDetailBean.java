package com.johan.subuwuliu.bean;

public class DriverDetailBean {

	public String status;
	public String timestamp;
	public DriverDetailInfoBean info;
	
	public class DriverDetailInfoBean {
		public String id;
		public String yid;
		public String order_no;
		public String contacts_name;
		public String contacts_mobile;
		public String goods_type;
		public String pack;
		public String extra_request;
		public String is_up_goods;
		public String is_down_goods;
		public String is_supercargo;
		public String supercargo_number;
		public String is_up_floor;
		public String floor_number;
		public String is_lift;
		public String remark;
		public String order_amount;
		public String img_url;
	}
	
}
