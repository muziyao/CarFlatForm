package com.common.bean;

public class ResponseMessage {
	private int code;
	private String msg;
	private Object data;
	public int getCode() {
		return code;
	}
	public ResponseMessage setCode(int code) {
		this.code = code;
		return this;
	}
	public String getMsg() {
		return msg;
	}
	public ResponseMessage setMsg(String msg) {
		this.msg = msg;
		return this;
	}
	public Object getData() {
		return data;
	}
	public ResponseMessage  setData(Object data) {
		this.data = data;
		return this;
	}
	
	public static ResponseMessage ok() {
		return  new ResponseMessage().setCode(1);
	}
	
	public static ResponseMessage fail(String errormsg) {
		return  new ResponseMessage().setCode( 0 ).setMsg(errormsg);
	}
	
	@Override
	public String toString() {
		return "ResponseMessage [code=" + code + ", msg=" + msg + ", data=" + data + "]";
	}
	
	
}
