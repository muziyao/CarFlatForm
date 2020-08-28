package com.common.cfg;

import java.io.File;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.common.utils.FileUtils;
import com.smokeroom.entity.GlobalParam;

@Component
public class FileConfig implements ApplicationListener<ContextRefreshedEvent> {

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		System.out.println("检测升级目录是否创建。");
		File dir = new File(  GlobalParam.Car_Upgrade_dir );
		if(dir.exists() == false ) {
			dir.mkdirs();
		}
		//确保重启时所有的临时文件都被清空。
		if(dir.exists() == false ) {
			System.out.println(GlobalParam.Car_Upgrade_dir+"目录不存在。请使用管理员权限运行应用程序。");
			System.exit( -1 );
		}
		//检测文件上传临时目录
		File dir_temp = new File(  GlobalParam.temp_file );
		FileUtils.deleteAllFile(dir_temp,1*60*60);
	}
	
}
 