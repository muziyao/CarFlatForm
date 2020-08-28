package com.smokeroom.service;

import java.io.IOException;

public interface MsgConnectionService {
	public String offset_waning = "voice_warn:1\r\n";
	public String danger_waning = "voice_warn:2\r\n";
	public String overspeed_waning = "voice_warn:3\r\n";
	public String prohibit_waning = "voice_warn:4\r\n";
	public String close_push_video = "video_push:off\r\n";
	
	
	public   String sendMsg(String devId,String msgplain) throws IOException;
	
	/**
	 * 查询车辆是否在线。
	 * @param devId
	 * @return
	 */
	public  boolean isCarOnline(String devId);
	
	
	public int getOnlineCount();
}
