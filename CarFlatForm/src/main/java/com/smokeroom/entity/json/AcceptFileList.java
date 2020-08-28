package com.smokeroom.entity.json;

import java.io.Serializable;
import java.util.List;

public class AcceptFileList implements Serializable {
/*
 * {
 code:1, 
 msg:"OK",  
 num:1,
 date:"2020-07-08",
 data:["2020-xxxx.mp4","2020-yyyyy.mp4",,"2020-yyxxyy.mp4"], 
}
 * */
	private int code;
	private String msg;
	private int num;
	private String date;
	private List<String> data;
	private String msgtype;//前端需要
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
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public List<String> getData() {
		return data;
	}
	public void setData(List<String> data) {
		this.data = data;
	}
	public String getMsgtype() {
		return msgtype;
	}
	public void setMsgtype(String msgtype) {
		this.msgtype = msgtype;
	}
	@Override
	public String toString() {
		return "AcceptFileList [code=" + code + ", msg=" + msg + ", num=" + num + ", date=" + date + ", data=" + data
				+ ", msgtype=" + msgtype + "]";
	}
	 
	
	
	
	
}
