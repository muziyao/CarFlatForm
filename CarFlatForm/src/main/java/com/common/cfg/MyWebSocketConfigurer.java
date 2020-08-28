package com.common.cfg;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.smokeroom.ws.VoiceChatCallBackWS;
import com.smokeroom.ws.VoiceChatWS;

@Component
public  class MyWebSocketConfigurer implements WebSocketConfigurer {
 
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new VoiceChatWS(), "/carmonitorsys/voice/speaking")
        		//.addHandler(new VoiceChatCallBackWS(), "/speaktoyourself")
        		.setAllowedOrigins("*");
        System.out.println("初始化webSocket");
    }
	 
}