package com.smokeroom.entity;


import com.common.utils.MyStringUtils;

public class CommonDataBean {
	private int id;
	private String token;//授权码
	private String devId;//设备编号。

	private String lng="0";
	private String lat="0";
	private String timestamp = MyStringUtils.getDate();
	
	private String speed="0";
	private String behavior;
	
	
	private String imagestr;
	private String videostr;
	
	private String dataType;//记录类型。driver_behavior sos  offline_check  door_check 

	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getDevId() {
		return devId;
	}

	public void setDevId(String devId) {
		this.devId = devId;
	}

	public String getLng() {
		return lng;
	}

	

	public String getLat() {
		return lat;
	}

	

	 

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
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

	public String getBehavior() {
		return behavior;
	}

	public void setBehavior(String behavior) {
		this.behavior = behavior;
	}
	
	///临时调试用。后续删除。
	public void setSRCSpeed(String speed) {
		this.speed = speed;
	}

	public String getImagestr() {
		return imagestr;
	}

	public void setImagestr(String imagestr) {
		this.imagestr = imagestr;
	}

	public String getVideostr() {
		return videostr;
	}

	public void setVideostr(String videostr) {
		this.videostr = videostr;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
 
	public void setLng(String lng) {
		this.lng  = lng;
	}
	
 
	public void setLat(String lat) {
		this.lat = lat;
	}

	public static final String driver_behavior ="driver_behavior";
	public static final String sos ="sos";
	public static final String door_check ="door_check";

	@Override
	public String toString() {
		return "CommonDataBean [id=" + id + ", token=" + token + ", devId=" + devId + ", lng=" + lng + ", lat=" + lat
				+ ", timestamp=" + timestamp + ", speed=" + speed + ", behavior=" + behavior + ", imagestr=" + imagestr
				+ ", videostr=" + videostr + ", dataType=" + dataType + "]";
	}
	 
	
	
	
}
