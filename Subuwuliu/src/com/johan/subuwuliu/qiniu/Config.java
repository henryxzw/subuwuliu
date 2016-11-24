package com.johan.subuwuliu.qiniu;

import java.nio.charset.Charset;

public final class Config {
	public static final String VERSION = "7.0.0";
	public static final int BLOCK_SIZE = 4 * 1024 * 1024;
	public static final Charset UTF_8 = Charset.forName("UTF-8");
	public static String API_HOST = "http://api.qiniu.com";
	public static String RSF_HOST = "http://rsf.qbox.me";
	public static String RS_HOST = "http://rs.qbox.me";
	public static String IO_HOST = "http://iovip.qbox.me";
	public static String UP_HOST = "http://up.qiniu.com";
	public static String UP_HOST_BACKUP = "http://upload.qiniu.com";
	public static int PUT_THRESHOLD = BLOCK_SIZE;
	public static int CONNECT_TIMEOUT = 10 * 1000;
	public static int RESPONSE_TIMEOUT = 30 * 1000;
	public static int RETRY_MAX = 5;

	private Config() {
	}
}