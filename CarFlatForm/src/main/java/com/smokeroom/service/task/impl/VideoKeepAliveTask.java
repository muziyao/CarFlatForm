package com.smokeroom.service.task.impl;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
 

import com.smokeroom.service.Base;
import com.smokeroom.service.LoopTaskService;
import com.smokeroom.service.MsgConnectionService;
/**
 * 保活任务。
 * 每个车载主机的不更存活时间时，生命周期一到，摄对应像头就会断开。 防止流量过剩。每个摄像头默认50s。
 * @author Administrator
 *
 */
public class VideoKeepAliveTask extends CommonTimeTask implements LoopTaskService {
	private static Map<String,AliveTime> map =Collections.synchronizedMap(  new HashMap<String, AliveTime>());
	private static MsgConnectionService msgservice = null;
	public VideoKeepAliveTask(String devId ) {
		//任务执行周期24s。
		super(24*1000, null);
		AliveTime bean = new AliveTime();
		bean.setDevId(devId); //设置插在主机编号。
		bean.setLasttime(System.currentTimeMillis()); //设置最新时间。
		map.put(devId, bean); //
		if(msgservice == null ) {
			msgservice = Base.getBean(MsgConnectionService.class);
		}
	}
	
	public static   void addOne(String devId ) {
		AliveTime bean = map.get(devId); //
		if(bean != null ) {
			if(!bean.isTimeUp()) {//时间未到。
				return;
			}
		}
		TimeTaskServiceManager.addTask(   new VideoKeepAliveTask(devId) );
	}

	public  void run(Object param) {
		//System.out.println("执行一次："+System.currentTimeMillis());
		Set<Entry<String,AliveTime>>  set = map.entrySet();
		for( Entry<String,AliveTime> entry  :  set ) {
			AliveTime item = entry.getValue();
			if( item.isTimeUp() ) {
				 //到达指定时间，未更新。
				try {
					//关闭所有摄像头。
					msgservice.sendMsg(item.getDevId(), "video_push:off\r\n");
					//移除这个任务。
					TimeTaskServiceManager.removeTask(this);
					//移除这个类。
					map.remove( item.getDevId());
					//System.out.println("时间到：关闭摄像头"+item);
				} catch (IOException e) {
				}
			}
		}
	}
	
	public  static void updateTime( String devId) {
		AliveTime bean = map.get(devId);
		if( bean != null ) {
			bean.setLasttime( System.currentTimeMillis() );
		}
	}
	
	private  class AliveTime{
		private long lasttime;
		private long offset = 50*1000;//推流连接存活时间。30 S 
		private String devId;
		
		public String getDevId() {
			return devId;
		}

		public void setDevId(String devId) {
			this.devId = devId;
		}

		 
		public void setLasttime(long lasttime) {
			this.lasttime = lasttime;
		}
		 
		public boolean isTimeUp() {
			long t2 = System.currentTimeMillis();
			long off = t2 - lasttime;
			return  off > offset;
		}

	} 
	 
}
