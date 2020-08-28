package com.smokeroom.entity.json;

import com.smokeroom.entity.CommonURLParam;
/**
 * 接收用户上传的文件参数。
 * @author Administrator
 *
 */
public class AcceptUploadFile extends CommonURLParam{
	private String num;
	private String filename;
	public String getNum() {
		return num;
	}
	public void setNum(String num) {
		this.num = num;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	@Override
	public String toString() {
		return "AcceptUploadFile [num=" + num + ", filename=" + filename + ", getNum()=" + getNum() + ", getFilename()="
				+ getFilename() + "]";
	}
	
	
}
