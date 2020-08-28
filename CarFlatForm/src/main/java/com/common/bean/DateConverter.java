package com.common.bean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.common.utils.MyStringUtils;

@Component
public class DateConverter implements Converter<String, Date> {

	@Override
	public Date convert(String source) {
		if(MyStringUtils.isEmpty( source ))return null;
		//1 如果是 yyyy-MM-dd
		//2 如果是yyyy-MM-dd hh:mm:ss格式。
		try {
			if(source.matches( "^\\d{4}[-]\\d{1,2}[-]\\d{1,2}$" )) {
				return new SimpleDateFormat("yyyy-MM-dd").parse(source);
			}else if(source.matches( "^\\d{4}[-]\\d{1,2}[-]\\d{1,2} \\d{1,2}[:]\\d{1,2}[:]\\d{1,2}$" )) {
				return new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(source);
			}else {
				throw new RuntimeException("日期格式错误！必须是yyyy-MM-dd hh:mm:ss或yyyy-MM-dd格式");
			}
		 } catch (ParseException e) {
			 throw new RuntimeException("日期格式错误！必须是yyyy-MM-dd hh:mm:ss或yyyy-MM-dd格式");
	   }
	}
	/*
	 * public static void main(String[] args) { String str = "2019-11-1 11:11:11 ";
	 * String reg ="^\\d{4}[-]\\d{1,2}[-]\\d{1,2} \\d{1,2}[:]\\d{1,2}[:]\\d{1,2}$";
	 * System.out.println( str.matches(reg) ); }
	 */

}
