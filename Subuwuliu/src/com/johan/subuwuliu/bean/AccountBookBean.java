package com.johan.subuwuliu.bean;

import java.util.List;

public class AccountBookBean {

	public String status;
	public String amount;
	public String msg;
	public List<AccountBookListBean> list;
	
	public static class AccountBookListBean {
		public String date;
		public String title;
		public String momeny;
	}
	
}
