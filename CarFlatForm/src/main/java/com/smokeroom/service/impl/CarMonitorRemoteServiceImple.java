package com.smokeroom.service.impl;

import java.io.File;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.common.utils.HttpUtils;
import com.common.utils.JWTUtils;
import com.common.utils.MyStringUtils;
import com.smokeroom.entity.CommonDataBean;
import com.smokeroom.entity.GlobalParam;
import com.smokeroom.entity.OBDInfo;
import com.smokeroom.service.CarMonitorRemoteService;
import com.smokeroom.service.RouteService;
 

@Service
public class CarMonitorRemoteServiceImple implements CarMonitorRemoteService{
	
	@Override
	public void sendOBD(OBDInfo bean) throws Exception  {
		StringBuilder param  = new StringBuilder(); 
		String url = GlobalParam.remote_server_url + "carmonitorsys/car/obd.action";
		Map<String,String> map = new HashMap<String,String>();
		map.put("devId", bean.getDevId() );
		map.put("token", JWTUtils.creatToken(10*60));
		map.put("speed", bean.getSpeed());
		map.put("ym", bean.getYm());
		map.put("xc", bean.getXc());
		map.put("temperature", bean.getTemperature());
		map.put("rotationRate", bean.getRotationRate());
		map.put("sc", bean.getSc());
		map.put("dw", bean.getDw());
		map.put("kt", bean.getKt());
		map.put("errorcode", bean.getErrorcode());
		map.put("lat", bean.getLat() );
		map.put("lng", bean.getLng());
		map.put("timestamp", bean.getTimestamp());
		
		
		 
		
		Set<Entry<String,String>> set = map.entrySet();
		for(Entry<String,String> ent: set) {
			String value =  ent.getValue();
			param.append(ent.getKey())
			.append("=")
			.append( value == null ? "" : URLEncoder.encode( value , "UTF-8") )
			.append("&");
		}
		param.delete(param.length()-1, param.length());
		HttpUtils.postFormNotReadResponse(url, param.toString());
	}
	
	private static final String [] uploadfield = new String[] {"image","video"};
	
	
	public   void sendBehavior(CommonDataBean  bean,File image,File video) throws Exception {
			String url = GlobalParam.remote_server_url +  "carmonitorsys/driver/behavior.action?";
			StringBuilder param  = new StringBuilder(); 
			param.append(url);
			
			Map<String,String> map = new HashMap<String,String>();
			map.put("devId",  bean.getDevId() );
			map.put("token", JWTUtils.creatToken(10*60));
			map.put("behavior", bean.getBehavior());
			map.put("lng", bean.getLng());
			map.put("lat", bean.getLat());
			map.put("speed", bean.getSpeed()); 
			map.put("timestamp",bean.getTimestamp() );
			
			Set<Entry<String,String>> set = map.entrySet();
			for(Entry<String,String> ent: set) {
				String value =  ent.getValue();
				param.append(ent.getKey())
				.append("=")
				.append( value == null ? null : URLEncoder.encode( value , "UTF-8") )
				.append("&");
			}
			param.delete(param.length()-1, param.length());
			//发送。
			String rs = HttpUtils.upload(param.toString(),uploadfield ,image,video);
			//System.out.println("行为分析调用上游响应："+rs);
	    }

	
	
	@Override
	public void sendSOS(CommonDataBean bean,  File image,File video) throws Exception {
		try {
			String url = GlobalParam.remote_server_url +  "carmonitorsys/car/sos.action?";
			StringBuilder param  = new StringBuilder(); 
			param.append(url);
			
			Map<String,String> map = new HashMap<String,String>();
			map.put("devId",  bean.getDevId() );
			map.put("token", JWTUtils.creatToken(10*60));
			map.put("lng", bean.getLng());
			map.put("lat", bean.getLat());
			map.put("timestamp",bean.getTimestamp() );
			
			Set<Entry<String,String>> set = map.entrySet();
			for(Entry<String,String> ent: set) {
				String value =  ent.getValue();
				param.append(ent.getKey())
				.append("=")
				.append( value == null ? "" : URLEncoder.encode( value , "UTF-8") )
				.append("&");
			}
			param.delete(param.length()-1, param.length());
			//请求体加上图片和视频。发送。
			String rs = HttpUtils.upload(param.toString(),uploadfield ,image,video);
			//System.out.println("一件救援响应："+rs);
		} catch (Exception e) {
			 e.printStackTrace();
		}
	}

	
	@Override
	public void sendDoorCheck(CommonDataBean bean, File image,File video ) throws Exception {
		String url = GlobalParam.remote_server_url +  "carmonitorsys/door/check.action?";
		StringBuilder param  = new StringBuilder(); 
		param.append(url);
		Map<String,String> map = new HashMap<String,String>();
		map.put("devId",  bean.getDevId() );
		map.put("token", JWTUtils.creatToken(10*60));
		map.put("msg",  "");
		map.put("speed",  bean.getSpeed());
		map.put("behavior", bean.getBehavior());
		map.put("lng", bean.getLng());
		map.put("lat", bean.getLat());
		map.put("timestamp",bean.getTimestamp() );
		
		Set<Entry<String,String>> set = map.entrySet();
		for(Entry<String,String> ent: set) {
			String value =  ent.getValue();
			param.append(ent.getKey())
			.append("=")
			.append( value == null ? "" : URLEncoder.encode( value , "UTF-8") )
			.append("&");
		}
		param.delete(param.length()-1, param.length());
		//请求体加上图片和视频。发送。
		HttpUtils.upload(param.toString(),uploadfield ,image,video);
	}
	 
}
