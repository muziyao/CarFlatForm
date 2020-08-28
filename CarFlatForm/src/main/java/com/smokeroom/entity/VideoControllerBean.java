package com.smokeroom.entity;

public class VideoControllerBean {
	private String devId;
	private String token;
	private String operation;
	private int videonum;
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

	public int getVideonum() {
		return videonum;
	}

	public void setVideonum(int videonum) {
		this.videonum = videonum;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	@Override
	public String toString() {
		return "VideoControllerBean [devId=" + devId + ", token=" + token + ", operation=" + operation + ", videonum="
				+ videonum + "]";
	}
	

}
