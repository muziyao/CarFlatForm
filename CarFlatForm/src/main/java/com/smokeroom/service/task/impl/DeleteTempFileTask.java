package com.smokeroom.service.task.impl;

import java.io.File;

import org.springframework.stereotype.Service;

import com.smokeroom.entity.GlobalParam;
import com.smokeroom.service.LoopTaskService;

/**  
 * 用法：将自己加入
 * @author Administrator
 *
 */
@Service
public class DeleteTempFileTask extends CommonTimeTask implements LoopTaskService{
	 
	public DeleteTempFileTask() {
		//只存1个小时以内的视频。
		this(60*60,  null );
		TimeTaskServiceManager.addTask(this);
	}
	public DeleteTempFileTask(int seconds, Object param) {
		super(seconds, param);
	}

	@Override
	public void run(Object param) {
		   if( isTimeUp() ) {
			  //执行一波删除代码。
				deleteAllFile();
		   }
	}
	
	public  void deleteAllFile() {
		File dir = new File(  GlobalParam.temp_file );
		File []fs = dir.listFiles();
		long t1 = System.currentTimeMillis();
		if(fs != null ) {
			for(File f:fs) {
				 long t2 = f.lastModified();
				 int sec = (int) ((t1-t2) / 1000);
				 if( sec >  getOffset() ) {
					 f.delete();
					 System.out.println("过期删除文件："+f);
				 }
			}
		}
	}

}
