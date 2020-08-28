package com.common.cfg;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.common.bean.ResultData;
 
//@ControllerAdvice("com.smokeroom.controller")
public class ErrorCfg{
	protected   final Log loger = LogFactory.getLog( getClass());
	@ExceptionHandler(Exception.class)
    @ResponseBody
    public ResultData handleException(Exception e){
        return ResultData.fail(e.toString());
  }
}
 
