package com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

import com.smokeroom.service.Base;
 

@EnableWebSocket
@SpringBootApplication
public class AppStartUp {
	
	public static void main(String[] args) {
		ConfigurableApplicationContext cxt = SpringApplication.run(AppStartUp.class, args);
		Base.setApplicationContext( cxt );     
	} 
}
