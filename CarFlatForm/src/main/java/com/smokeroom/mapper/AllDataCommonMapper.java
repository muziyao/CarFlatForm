package com.smokeroom.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.smokeroom.entity.CommonDataBean;

public interface AllDataCommonMapper {
	
	int insert(CommonDataBean bean);
	
	List<CommonDataBean> get(CommonDataBean bean);
	
	List<CommonDataBean> getByPage(@Param("bean") CommonDataBean bean,@Param("start")int start,@Param("pageSize")int pageSize);
	int getByPageTotal(@Param("bean") CommonDataBean bean);
}
