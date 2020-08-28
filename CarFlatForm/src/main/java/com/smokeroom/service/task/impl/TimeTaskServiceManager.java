package com.smokeroom.service.task.impl;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.springframework.stereotype.Service;

import com.smokeroom.service.LoopTaskService;
/**
 * 5 S运行一次。
 * @author Administrator
 *
 */

@Service
public class TimeTaskServiceManager {
	private static String lock ="123";
	private static int time = 5000;
	public static List<CommonTimeTask> tasklist = Collections.synchronizedList(new LinkedList<CommonTimeTask>());
	
	public TimeTaskServiceManager() {
		try {
			init();
		} catch (Exception e) {
		}
	}
	
	public static void init( ) throws Exception {
		 new Timer( ).schedule(new TimerTask() {
				@Override
				public void run() {
					synchronized (tasklist) {
						for(int i=0;i<tasklist.size();i++) {
							CommonTimeTask item = tasklist.get(i);
							if( item.isTimeUp() ) { 
								//时间到，判断到底是一次性任务还是循环任务。
								try {
									item.run( item.getParam());
								}catch (Exception e) {
								}
								if( ! isLoopTask(item)) {
									//非循环任务要移除。
									item.clear();
									tasklist.remove(i);
									i--;
								}else {//是循环任务。//更新执行时间。以便下次继续循环。
									item.updateLastExecutionTime();
								}
							}
						}
					}
				}
			}, 0,time ); 
	}
	 
 
		
	public static  void addTask(CommonTimeTask tsk) {
		tasklist.add(tsk);
	}
	
	
	
	public static  boolean removeTask(CommonTimeTask tsk) {
		return tasklist.remove(tsk);
	}
	 
	
	/**
	 * 是否是循环任务。
	 * @param bean
	 * @return
	 */
	public static boolean isLoopTask(Object bean) {
		return bean instanceof LoopTaskService;
	}
	 
}
