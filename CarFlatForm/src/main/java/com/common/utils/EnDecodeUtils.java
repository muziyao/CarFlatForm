package com.common.utils;
import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
public class EnDecodeUtils {
	
	/**
	 * 原始字符串。
	 * @param src
	 * @return
	 */
	public static String SHA1(String src) {
		return SHA1(src.getBytes());
	}
	/**
	 * 原始字节数组。
	 * @param srcbytes
	 * @return
	 */
	public static String SHA256(String stringSignTemp,String  key ) {
		try {
			 Mac mac =	Mac.getInstance("HmacSHA256");
			  SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(), "HmacSHA256");
			  mac.init(secret_key);
			  byte [] bs = mac.doFinal(stringSignTemp.getBytes("utf-8"));
		      return bytesToStr(bs).toUpperCase();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	 
	}
	 
	
	
	/**
	 * 原始字节数组。
	 * @param srcbytes
	 * @return
	 */
	public static String SHA1(byte [] srcbytes) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA");
			md.update(srcbytes);
			byte []bs = md.digest();
			 return bytesToStr(bs);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	 public static String md5( File file) throws NoSuchAlgorithmException {
		  MessageDigest dg = MessageDigest.getInstance("md5");
		  try( FileInputStream fin = new FileInputStream(file);) {
			  byte []bs = new byte[1024];
				 while(true) {
					 int num = fin.read(bs);
					 if( num == -1)break;
					 dg.update(bs,0,num );
				 }
		  }catch (Exception e) {  
		  }
			
		  byte []rs = dg.digest();
		  return bytesToStr(rs);
	}
	 
	public static String md5_32(String str)   {
		String rs = null;
		try {
			rs= md5(str.getBytes("UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rs;
	}
	
	public static String md5_32(byte [] srcbyte) throws NoSuchAlgorithmException {
		return md5(srcbyte);
	}
	
	public static String md5_16(byte [] srcbyte) throws NoSuchAlgorithmException {
		return md5(srcbyte).substring(8, 24);
	}
	
	
	private static String md5(byte [] srcbyte) throws NoSuchAlgorithmException {
		MessageDigest dg = MessageDigest.getInstance("md5");
		byte [] rs = dg.digest(srcbyte);
		//-128~+127
		return bytesToStr(rs);
	}
	
	private static String bytesToStr(byte []bs) {
		StringBuilder sb = new StringBuilder();
		for(byte b:bs) {
			int a = b<0 ? 256 + b : b;//
			String str = Integer.toHexString(a);
			if(a<16) {
				sb.append("0");
			}
			sb.append(str);
		}
		 return sb.toString();
	}
}