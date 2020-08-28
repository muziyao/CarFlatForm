package com.smokeroom.entity.json;
 

public  class ExecuteData{
	private int code;
	private String msg;
	private ExecuteFlag data = new ExecuteFlag();//确保要实例化。
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public ExecuteFlag getData() {
		return data;
	}
	public void setData(ExecuteFlag data) {
		this.data = data;
	}
	@Override
	public String toString() {
		return "ExecuteData [code=" + code + ", msg=" + msg + ", data=" + data + "]";
	}
	 
}