package com.smokeroom.service.task.impl;

public abstract class CommonTimeTask {
	private long offset ;
	private long t;
	private Object param; 
	public CommonTimeTask() {}
	public CommonTimeTask(int  seconds,Object param ) {
		this.offset = seconds;
		this.param = param;
		t = System.currentTimeMillis();
	}

	public boolean isTimeUp() {
		long t2 = System.currentTimeMillis();
		long off = t2 - t;
		return  off > offset;
	}
	public void updateLastExecutionTime() {
		this.t = System.currentTimeMillis();
	}
	/**
	 * 定时时间到了之后要执行的操作。每个任务的run操作都不一样。
	 * @param param
	 */
	public abstract void run(Object param);
	
	public Object getParam() {
		return this.param;
	}
	
	public void clear() {
		param = null;
	}
	
	public long getOffset() {
		return this.offset;
	}
}
