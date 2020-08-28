package com.smokeroom.mapper;

import java.util.List;

import com.smokeroom.entity.OBDRecord;
import com.smokeroom.entity.ext.OBDRecordQueryModel;

public interface OBDMapper {
	int insert( OBDRecord obd );
	List<OBDRecord> query(OBDRecordQueryModel query);
}
