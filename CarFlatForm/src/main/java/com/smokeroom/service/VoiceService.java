package com.smokeroom.service;

import java.io.IOException;

public interface VoiceService {
	public   void sendPCMData(String devId,byte []bs,int off, int len) throws IOException;
	public void offLine(String devId);
	public boolean isClientOnline(String devId);
}
