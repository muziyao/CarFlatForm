package com.common.cfg;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.MappedInterceptor;
 
import com.common.interceptor.OpenAccess;

@Configuration
public class WebConfig implements WebMvcConfigurer{
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		//1 普通拦截器。
		OpenAccess  t1 = new OpenAccess();
		//2 映射拦截器  新功能：路径映射。 "/openapi/**/*.action"
		MappedInterceptor m1 =  new MappedInterceptor(new String[] { "/**/*"}, t1);
		registry.addInterceptor( m1 );
	}
	
}
