package com.smokeroom.entity;
/**
 * 更新记录。
 * @author Administrator
 *
 */
public class UpdateRecord {
	private String devId;
	private String upgrade;
	private String creation;
	private String version;
	private String carNum;
	private String carType;
	private String hasContentLength;//版本级别。  H   L  高版本和低版本。
	private String detailinfo;
	private String online="N";
	
	
 
	public String getHasContentLength() {
		return hasContentLength;
	}
	public void setHasContentLength(String hasContentLength) {
		this.hasContentLength = hasContentLength;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getOnline() {
		return online;
	}
	public void setOnline(String online) {
		this.online = online;
	}
	public String getDevId() {
		return devId;
	}
	public void setDevId(String devId) {
		this.devId = devId;
	}
	public String getUpgrade() {
		return upgrade;
	}
	public void setUpgrade(String upgrade) {
		this.upgrade = upgrade;
	}
	public String getCreation() {
		return creation;
	}
	public void setCreation(String creation) {
		this.creation = creation;
	}
	
	
	public String getCarNum() {
		return carNum;
	}
	public void setCarNum(String carNum) {
		this.carNum = carNum;
	}
	public String getCarType() {
		return carType;
	}
	public void setCarType(String carType) {
		this.carType = carType;
	}
	public String getDetailinfo() {
		return detailinfo;
	}
	public void setDetailinfo(String detailinfo) {
		this.detailinfo = detailinfo;
	}
	
	
	@Override
	public String toString() {
		return "UpdateRecord [devId=" + devId + ", upgrade=" + upgrade + ", creation=" + creation + ", version="
				+ version + ", carNum=" + carNum + ", carType=" + carType + ", hasContentLength=" + hasContentLength
				+ ", detailinfo=" + detailinfo + ", online=" + online + "]";
	}
	
	
	
 
	 
	
}
