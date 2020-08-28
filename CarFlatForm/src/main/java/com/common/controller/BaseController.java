package com.common.controller;
 
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
 
import com.common.bean.ResultData;
 

import io.swagger.annotations.ApiOperation;
@RestController
@RequestMapping("/base")
public class BaseController  {
	
	 
	protected   final Log loger = LogFactory.getLog( getClass());
	protected    void debug(String msg) {
		loger.debug(msg);	
	}
	 
	protected   void info(String msg) {
		loger.info(msg);	
	}
	
	protected   void error(String msg) {
		loger.error(msg);	
	}
	
	public static ResultData quickReturn(boolean ok) {
		if(ok ) {
			return ResultData.success();
		}else {
			return ResultData.fail();
		}
	}
	public static ResultData quickReturn (int row) {
		 return quickReturn(row > 0);
	}
	
	public static ResultData quickReturn (int row,String success,String error) {
		 if(row > 0)return ResultData.success(success);
		 return ResultData.fail(error);
	}
	
	public static ResultData quickReturn (List list) {
		if( list != null && list.size() >0 ) {
			return ResultData.success().setData(list);
		}else {
			return ResultData.fail().setData(list);
		}
	}
}
