package com.smokeroom.entity;

import java.util.Date;

public class OBDRecord {
	private int id;
	private String devId;
	private String timestamp;
	private String lat;
	private String lng;
	private String speed;
	private String src;
	private String gpsplain;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getDevId() {
		return devId;
	}
	public void setDevId(String devId) {
		this.devId = devId;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public String getLat() {
		return lat;
	}
	public void setLat(String lat) {
		this.lat = lat;
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
		this.speed = speed;
	}
	public String getSrc() {
		return src;
	}
	public void setSrc(String src) {
		this.src = src;
	}
	public String getGpsplain() {
		return gpsplain;
	}
	public void setGpsplain(String gpsplain) {
		this.gpsplain = gpsplain;
	}
	@Override
	public String toString() {
		return "OBDRecord [id=" + id + ", devId=" + devId + ", timestamp=" + timestamp + ", lat=" + lat + ", lng=" + lng
				+ ", speed=" + speed + ", src=" + src + ", gpsplain=" + gpsplain + "]";
	}
	
	
}
