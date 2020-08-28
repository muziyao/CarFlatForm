package com.common.cfg;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.smokeroom.mapper")
public class MybaitsCfg {

}
