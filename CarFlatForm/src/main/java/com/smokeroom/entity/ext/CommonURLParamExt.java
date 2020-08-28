package com.smokeroom.entity.ext;

import com.smokeroom.entity.CommonURLParam;

public class CommonURLParamExt extends CommonURLParam {
	private int num;

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	@Override
	public String toString() {
		return "CommonURLParamExt [num=" + num + ", getToken()=" + getToken() + ", getDevId()=" + getDevId() + "]";
	}
	
	
}
