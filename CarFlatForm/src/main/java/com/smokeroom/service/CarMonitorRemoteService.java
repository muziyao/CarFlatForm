package com.smokeroom.service;

import java.io.File;

import com.smokeroom.entity.CommonDataBean;
import com.smokeroom.entity.OBDInfo;

public interface CarMonitorRemoteService {
	
	public   void sendOBD(OBDInfo bean)throws Exception;
	
	public   void sendBehavior(CommonDataBean  bean,File image,File video) throws Exception;
	
	public   void sendSOS(CommonDataBean  bean,File  image ,File video) throws Exception;
	
	public   void sendDoorCheck(CommonDataBean  bean,File  image ,File video) throws Exception;
	
}
