package com.smokeroom.entity;
/**
 * 摄像头保活时间控制。
 * @author Administrator
 *
 */
public class VideoKeepAliveTime {
	private String devId;
	private String lasttime;
	private String currenttime;
	private  int num;//预留，未使用。
	
	public String getDevId() {
		return devId;
	}
	public void setDevId(String devId) {
		this.devId = devId;
	}
	public String getLasttime() {
		return lasttime;
	}
	public void setLasttime(String lasttime) {
		this.lasttime = lasttime;
	}
	public String getCurrenttime() {
		return currenttime;
	}
	public void setCurrenttime(String currenttime) {
		this.currenttime = currenttime;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	
}
