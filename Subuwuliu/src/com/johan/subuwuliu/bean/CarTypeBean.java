package com.johan.subuwuliu.bean;

import java.util.List;

public class CarTypeBean {

	public String id;
	public String title;
	public String small_img_url;
	public List<CarTypeDetailBean> sub_title;
	
	public static class CarTypeDetailBean {
		public String id;
		public String title;
		public String big_img_url;
		public String amount;
		public String max_load;
		public String max_tonnage;
		public String start_momey;
		public String exceed_momey;
		public List<CarTypeSizeBean> sub_title;
		
		public static class CarTypeSizeBean {
			public String id;
			public String title;
		}
		
	}
	
}
