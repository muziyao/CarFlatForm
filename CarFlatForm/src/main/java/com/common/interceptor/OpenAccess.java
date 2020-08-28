package com.common.interceptor;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
 
 
 
 

public class OpenAccess implements HandlerInterceptor{

	@Override
	public boolean preHandle(HttpServletRequest rq, HttpServletResponse rp, Object handler)
			throws Exception {
				rp.setHeader("Access-Control-Allow-Origin", "*");
				rp.setHeader("Access-Control-Allow-Methods", "*");
				rp.setHeader("Access-Control-Allow-Headers", "*");
				rp.setHeader("Access-Control-Max-Age", "3600");
		 return true;
	}
	 
	@Override
	public void postHandle(HttpServletRequest rq, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		 
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
	 
	}

}
