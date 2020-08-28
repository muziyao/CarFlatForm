package com.common.cfg;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@Component
public class WebSocketConfig {
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
    	System.out.println("ServerEndpointExporter 初始化。。");
        return new ServerEndpointExporter();
    }
}
