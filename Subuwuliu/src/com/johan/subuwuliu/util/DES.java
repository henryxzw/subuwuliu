package com.johan.subuwuliu.util;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

import android.util.Base64;


public class DES {
	
	

//	 public final static String DES_KEY_STRING = "ABSujsuu";
     
	    public static String encrypt(String message) throws Exception {
	        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
	 
	        DESKeySpec desKeySpec = new DESKeySpec(password.getBytes("UTF-8"));
	 
	        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
	        SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
	        IvParameterSpec iv = new IvParameterSpec(password.getBytes("UTF-8"));
	        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
	 
	        return encodeBase64(cipher.doFinal(message.getBytes("UTF-8")));
	    }
	 
	    public static String decrypt(String message, String key) throws Exception {
	 
	        byte[] bytesrc = decodeBase64(message);//convertHexString(message);
	        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
	        DESKeySpec desKeySpec = new DESKeySpec(key.getBytes("UTF-8"));
	        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
	        SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
	        IvParameterSpec iv = new IvParameterSpec(key.getBytes("UTF-8"));
	 
	        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
	 
	        byte[] retByte = cipher.doFinal(bytesrc);
	        return new String(retByte);
	    }
	 
	    public static byte[] convertHexString(String ss) {
	        byte digest[] = new byte[ss.length() / 2];
	        for (int i = 0; i < digest.length; i++) {
	            String byteString = ss.substring(2 * i, 2 * i + 2);
	            int byteValue = Integer.parseInt(byteString, 16);
	            digest[i] = (byte) byteValue;
	        }
	 
	        return digest;
	    }
	 
	    public static String toHexString(byte b[]) {
	        StringBuffer hexString = new StringBuffer();
	        for (int i = 0; i < b.length; i++) {
	            String plainText = Integer.toHexString(0xff & b[i]);
	            if (plainText.length() < 2)
	                plainText = "0" + plainText;
	            hexString.append(plainText);
	        }
	 
	        return hexString.toString();
	    }
	 
	     
	    public static String encodeBase64(byte[] b) {//Base64.encodeToString(b, Base64.DEFAULT);
	        return Base64.encodeToString(b, Base64.NO_WRAP);
	    }
	     
	    public static byte[] decodeBase64(String base64String) {//Base64.decode(base64String, Base64.DEFAULT);
	        return Base64.decode(base64String, Base64.NO_WRAP);
	    }
	    
	
//	=================================
	final static String password = "spvan33@";
	/**
	 * 锷犲瘑
	 * @param datasource
	 *            byte[]
	 * @param password
	 *            String
	 * @return byte[]
	 */
	public static byte[] DESEncode(byte[] datasource) {
		try {
			SecureRandom random = new SecureRandom();
			DESKeySpec desKey = new DESKeySpec(password.getBytes());
			// 鍒涘缓涓?涓瘑鍖椤伐铡傦紝铹跺悗鐢ㄥ畠鎶奃ESKeySpec杞崲鎴?
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");//DES
			SecretKey securekey = keyFactory.generateSecret(desKey);
			// Cipher瀵硅薄瀹为台瀹屾垚锷犲瘑鎿崭綔
			Cipher cipher = Cipher.getInstance("DES");
			// 鐢ㄥ瘑鍖椤垵濮嫔寲Cipher瀵硅薄
			cipher.init(Cipher.ENCRYPT_MODE, securekey, random);
			// 鐜板湪锛岃幏鍙栨暟鎹苟锷犲瘑
			// 姝ｅ纺镓ц锷犲瘑鎿崭綔
			
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 瑙ｅ瘑
	 * 
	 * @param src
	 *            byte[]
	 * @param password
	 *            String
	 * @return byte[]
	 * @throws Exception
	 */
	public static byte[] decrypt(byte[] src) throws Exception {
		// DES绠楁硶瑕佹眰链変竴涓彲淇′换镄勯殢链烘暟婧?
		SecureRandom random = new SecureRandom();
		// 鍒涘缓涓?涓狣ESKeySpec瀵硅薄
		DESKeySpec desKey = new DESKeySpec(password.getBytes());
		// 鍒涘缓涓?涓瘑鍖椤伐铡?
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		// 灏咲ESKeySpec瀵硅薄杞崲鎴怱ecretKey瀵硅薄
		SecretKey securekey = keyFactory.generateSecret(desKey);
		// Cipher瀵硅薄瀹为台瀹屾垚瑙ｅ瘑鎿崭綔
		Cipher cipher = Cipher.getInstance("DES");
		// 鐢ㄥ瘑鍖椤垵濮嫔寲Cipher瀵硅薄
		cipher.init(Cipher.DECRYPT_MODE, securekey, random);
		// 鐪熸寮?濮嬭В瀵嗘搷浣?
		return cipher.doFinal(src);
	}
}
