package com.smokeroom.entity;

import com.common.utils.MyStringUtils;

public class Errlog {
	
	private int id;
	private  String devId;
    private String token;//对于车载主机而言是授权码。对于上游而言是JWT的token。
    private String errtext;
	private  String timestamp = MyStringUtils.getDate();
	
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
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getErrtext() {
		return errtext;
	}
	public void setErrtext(String errtext) {
		this.errtext = errtext;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	@Override
	public String toString() {
		return "Errlog [id=" + id + ", devId=" + devId + ", token=" + token + ", errtext=" + errtext + ", timestamp="
				+ timestamp + "]";
	}
	
	
	
}
