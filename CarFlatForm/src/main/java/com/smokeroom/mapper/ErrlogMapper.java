package com.smokeroom.mapper;

import java.util.List;

import com.smokeroom.entity.Errlog;

public interface ErrlogMapper {
	int insert( Errlog errlog );
	List<Errlog> query(Errlog errlog);
}
