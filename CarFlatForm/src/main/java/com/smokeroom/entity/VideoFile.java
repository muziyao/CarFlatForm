package com.smokeroom.entity;

public class VideoFile {
	private String devId;
	private String num;//摄像头编号。1 2 3 4 
	private String date;//该日期。yyyy-MM-dd
	private String filename; //文件名。
	
	public String getDevId() {
		return devId;
	}
	public void setDevId(String devId) {
		this.devId = devId;
	}
	public String getNum() {
		return num;
	}
	public void setNum(String num) {
		this.num = num;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	@Override
	public String toString() {
		return "VideoFile [devId=" + devId + ", num=" + num + ", date=" + date + ", filename=" + filename + "]";
	}
	
}
