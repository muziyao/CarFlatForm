package com.smokeroom.service;

import com.smokeroom.entity.OBDInfo;

public interface RouteService {
	/**
	 * 加载路线。
	 */
	public void loadRouteLine(String devId);
	/**
	 * 进行路线分析。
	 * @param obdinfo
	 * @throws Exception
	 */
	public void run(OBDInfo obdinfo)throws Exception;
	
	/**
	 * 是否时停靠点。
	 * @param devId
	 * @return
	 */
	public boolean isParking(String devId);
}
