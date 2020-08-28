package com.smokeroom.mapper;

import java.util.List;

import com.smokeroom.entity.CurrentVersion;
import com.smokeroom.entity.UpdateRecord;

public interface UpdateRecordMapper {
	/**
	 * 获取全部升级记录。
	 * @return
	 */
	List<UpdateRecord> getAllUpdateRecord();
	
	/**
	 * 根据设备编号获取单台升级信息。
	 * @param devId
	 * @return
	 */
	UpdateRecord getByDevId(String devId);
	
	/**
	 * 添加单台版本参数。
	 * @param bean
	 * @return
	 */
	int insert(UpdateRecord bean );
	
	/**
	 * 更新单台版本参数。
	 * @param bean
	 * @return
	 */
	int update(UpdateRecord bean );
	
	
	/**
	 * 添加全局版本参数。
	 * @param bean
	 * @return
	 */
	int insertVersion(CurrentVersion bean);
	/**
	 * 更新全局版本参数。
	 * @param bean
	 * @return
	 */
	int updateVersion(CurrentVersion bean);
	
	
	CurrentVersion getVersion( );
	
}
