package com.smokeroom.ws;

import java.io.IOException;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.springframework.stereotype.Component;

@Component
@ServerEndpoint("/videoreview")
public class VideoReivewWS {
	private static Session  session;
	
	@OnOpen
	public void onOpen(Session ss) {
			this.session = ss;
	}

	@OnClose
	public void onClose(Session ss) {
		try {
			ss.close();
		}catch (Exception e) {
		}
	}

	@OnError
	public void onError(Session ss, Throwable error) {
			this.onClose(ss);
	}
	public static void sendData(String txt) {
		if(session == null )return;
		try {
			session.getBasicRemote().sendText(txt);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
