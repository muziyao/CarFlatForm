package com.smokeroom.entity;
/**
 * 共同的URL参数
 * @author Administrator
 *
 */
public class CommonURLParam {
	private String token;
	private String devId;
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
	@Override
	public String toString() {
		return "CommonURLParam [token=" + token + ", devId=" + devId + "]";
	}
	
}
