package com.johan.subuwuliu.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5锷犲瘑宸ュ叿绫?
 * @author LEE.SIU.WAH
 * @email lixiaohua7@163.com
 * @date 2014骞?12链?7镞? 涓嫔崃4:21:55
 * @version 1.0
 */
public final class MD5 {
	

	
//	=============
	
    private final static char[] hexDigits = { '0', '1', '2', '3', '4', '5',  
        '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };  
	private static String bytesToHex(byte[] bytes) {  
	    StringBuffer sb = new StringBuffer();  
	    int t;  
	    for (int i = 0; i < 16; i++) {  
	        t = bytes[i];  
	        if (t < 0)  
	            t += 256;  
	        sb.append(hexDigits[(t >>> 4)]);  
	        sb.append(hexDigits[(t % 16)]);  
	    }  
	    return sb.toString();  
	}  
	public static String md5(String input) throws Exception {  
	    return code(input, 32);  
	}  
	public static String code(String input, int bit) throws Exception {  
	    try {  
	        MessageDigest md = MessageDigest.getInstance(System.getProperty(  
	                "MD5.algorithm", "MD5"));  
	        if (bit == 16)  
	            return bytesToHex(md.digest(input.getBytes("utf-8")))  
	                    .substring(8, 24);  
	        return bytesToHex(md.digest(input.getBytes("utf-8")));  
	    } catch (NoSuchAlgorithmException e) {  
	        e.printStackTrace();  
	        throw new Exception("Could not found MD5 algorithm.", e);  
	    }  
	}  
	public static String md5_3(String b) throws Exception{  
	    MessageDigest md = MessageDigest.getInstance(System.getProperty(  
	            "MD5.algorithm", "MD5"));  
	    byte[] a = md.digest(b.getBytes());  
	    a = md.digest(a);  
	    a = md.digest(a);  
	      
	    return bytesToHex(a);  
	} 
	/**
	 * MD5锷犲瘑鏂规硶
	 * @param str 鏄庢枃
	 * @return 锷犲瘑鍚庣殑瀛楃涓?
	 * @throws Exception
	 */
	public static String getMD5(String str) throws Exception{
		/** 鍒涘缓MD5锷犲瘑瀵硅薄 */
		MessageDigest md = MessageDigest.getInstance("MD5");
		/** 杩涜锷犲瘑 */
		md.update(str.getBytes());
		/** 銮峰彇锷犲瘑鍚庣殑瀛楄妭鏁扮粍  闀垮害16 */
		byte[] md5Bytes = md.digest();
		
		String res = "";
		/**  
		 * 灏?16浣嶉昵搴︾殑瀛楄妭鏁扮粍杞寲鎴?32浣嶉昵搴︾殑瀛楃涓? 
		 * (灏嗗叾涓殑涓?浣嶈浆鍖栨垚16杩涘埗镄勪簩浣嶏紝涓嶅涓や綅鍓嶉溃琛ラ浂) 
		 */
		for (int i = 0; i < md5Bytes.length; i++){
			int temp = md5Bytes[i] & 0xFF;
			if (temp <= 0xf){
				res += "0";
			}
			res += Integer.toHexString(temp);
		}
		return res;
	}
}