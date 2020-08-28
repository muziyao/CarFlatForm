package com.common.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.UUID;

import com.alibaba.fastjson.JSON;

public class MyStringUtils {
	
	public static Map<String,String> parseUrl(String strdata){
		Map<String,String> map = new HashMap<String,String>();
		StringTokenizer stk = new StringTokenizer(strdata,"&");
		while(stk.hasMoreElements()) {
			String keyvalues = stk.nextToken(); // ym=001.73
			String []kvs = keyvalues.split("=");
			String key = kvs[0];
			if( kvs.length ==2 ) {
				String value = kvs[1];
				try {
					map.put(key , URLDecoder.decode(value, "UTF-8") );
				} catch (UnsupportedEncodingException e) {
				}
			}else {
				map.put(key , "");
			}
		}
		return map;
	}
	
	
	public static String getDate() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format( new Date() );
	}
	
	public static boolean isEmpty(String field) {
		if(field == null || field.equals("") || field.trim().equals("") )return true;
		return false;
	}
	
	public static boolean isNotEmpty(String field) {
		return ! isEmpty(field);
	}
	
	public static boolean isPhone(String phone) {
		if(phone.matches("^1[0-9]{10}$")) {
			return true;
		}
		return false;
	}
	
	 
	/**
	 * 根据原始文件名的后缀返回同后缀的随机文件名。
	 * @param file_suffix
	 * @return
	 */
	public static String randomName(String file_suffix) {
		return UUID.randomUUID().toString()+file_suffix.substring( file_suffix.lastIndexOf("."), file_suffix.length());
	}
	
		
	public static boolean isNum(Integer num,int  howmany) {
		String str = num + "";
		if(str.matches("^[1-9][0-9]{"+(howmany-1)+",}$")) {
			return true;
		}
		return false;
	}
	 
	
	public static String toJSONString(Object obj) {
		 return JSON.toJSONString(  obj );
	}
	
	 
}
