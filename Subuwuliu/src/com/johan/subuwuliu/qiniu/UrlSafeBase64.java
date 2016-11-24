package com.johan.subuwuliu.qiniu;

public final class UrlSafeBase64 {
	private UrlSafeBase64() {
	}

	public static String encodeToString(String data) {
		return encodeToString(data.getBytes(Config.UTF_8));
	}

	public static String encodeToString(byte[] data) {
		return Base64.encodeToString(data, Base64.URL_SAFE | Base64.NO_WRAP);
	}

	public static byte[] decode(String data) {
		return Base64.decode(data, Base64.URL_SAFE | Base64.NO_WRAP);
	}
}