package com.smokeroom.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.common.utils.MapUtils;
import com.common.utils.MyStringUtils;

public class OBDInfo{
	
	private  String devId;
    private String token;//对于车载主机而言是授权码。对于上游而言是JWT的token。
	private  String speed="0";
	private  String ym="0";
	private  String xc="0";
	private  String temperature="0";
	private  String rotationRate="0";
	private  String sc="0";
	private  String dw="0";
	private  String kt="未开启";
	private  String errorcode="-";
	private  String lat=""; //纬度。
	private  String lng=""; //精度。
	private  String timestamp;
	private String gpsplain;
	
	
	
	public String getGpsplain() {
		return gpsplain;
	}

	public void setGpsplain(String gpsplain) {
		this.gpsplain = gpsplain;
	}

	public String getLat() {
		return lat;
	}
	
	public void setLat(String lat) {
		this.lat = lat ;
	}
	
	public String getLng() {
		return lng;
	}
	public void setLng(String lng) {
		this.lng = lng;
	}
	
	public String getSpeed() {
		return speed;
	}
	public void setSpeed(String speed) {
	   try {
			  float sp = Float.parseFloat(speed);
			  this.speed =((int )sp) + "";
		} catch (Exception e) {
			this.speed = "0";
		}
	}
	public String getYm() {
		return ym;
	}
	public void setYm(String ym) {
		this.ym = ym;
	}
	public String getXc() {
		return xc;
	}
	public void setXc(String xc) {
		this.xc = xc;
	}
	public String getTemperature() {
		return temperature;
	}
	public void setTemperature(String temperature) {
		this.temperature = temperature;
	}
	public String getRotationRate() {
		return rotationRate;
	}
	public void setRotationRate(String rotationRate) {
		this.rotationRate = rotationRate;
	}
	public String getSc() {
		return sc;
	}
	public void setSc(String sc) {
		this.sc = sc;
	}
	public String getDw() {
		return dw;
	}
	public void setDw(String dw) {
		this.dw = dw;
	}
	public String getKt() {
		return kt;
	}
	public void setKt(String kt) {
		this.kt = kt;
	}
	public String getErrorcode() {
		return errorcode;
	}
	public void setErrorcode(String errorcode) {
		this.errorcode = errorcode;
	}
 
	public String getDevId() {
		return devId;
	}
	 
	public void setDevId(String devId) {
		this.devId = devId;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	
	
	 
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	
	
	
	
	@Override
	public String toString() {
		return "OBDInfo [devId=" + devId + ", token=" + token + ", speed=" + speed + ", ym=" + ym + ", xc=" + xc
				+ ", temperature=" + temperature + ", rotationRate=" + rotationRate + ", sc=" + sc + ", dw=" + dw
				+ ", kt=" + kt + ", errorcode=" + errorcode + ", lat=" + lat + ", lng=" + lng + ", timestamp="
				+ timestamp + ", gpsplain=" + gpsplain + "]";
	}

	public static boolean isLatLngEmpty(OBDInfo bean) {
		if( bean.getLat() == null || "".equals( bean.getLat().trim()) || "0".equals(bean.getLat().trim() ))return true;
		if( bean.getLng() == null || "".equals( bean.getLng().trim()) || "0".equals(bean.getLng().trim() ) )return true;
		return false;
	}
 
	
	public static OBDInfo parse(String strdata) {
		//System.out.println("解析前OBD==="+strdata);
		if(strdata == null )return new OBDInfo();
		Map<String,String> map = MyStringUtils.parseUrl(strdata);
//		if(strdata.contains("8986032094855063363")) {
//			for(String key : map.keySet()){
//			    String value = map.get(key);
//			    System.err.println(key+":"+value);
//			}
//		}
		OBDInfo obd = new OBDInfo();
		obd.setDevId( map.get("devId"));
		obd.setDw(map.get("dw"));
		obd.setSpeed(map.get("speed"));
		obd.setErrorcode(map.get("errorcode"));
		obd.setKt(map.get("kt"));
		obd.setLat(map.get("lat"));
		obd.setLng( map.get("lng"));
		obd.setRotationRate(map.get("rotationRate"));
		obd.setSc(map.get("sc"));
		obd.setXc( map.get("xc"));
		obd.setTemperature(map.get("temperature"));
		obd.setYm( map.get("ym"));
		obd.setGpsplain( map.get("gpsplain"));
		obd.setTimestamp( map.get("datetime"));
//		System.err.println("gpsplain=="+map.get("gpsplain"));
		return obd;
	}
	 
}
