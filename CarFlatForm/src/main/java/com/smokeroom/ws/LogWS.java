package com.smokeroom.ws;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.springframework.stereotype.Component;

import com.common.utils.MyStringUtils;
import com.smokeroom.entity.CommonDataBean;
import com.smokeroom.mapper.AllDataCommonMapper;
import com.smokeroom.service.Base;

@Component
@ServerEndpoint("/logws") 
public class LogWS implements Base{
	
	protected static Session  session  = null;
	public synchronized static void sendMsg(String msg) {
			 try {
				 if(session!= null && session.isOpen() )
				 session.getBasicRemote().sendText( msg );
			} catch (IOException e) {
			}
	 }
	 
 
	  @OnOpen
	    public void onOpen(Session ss){
	    	try {
	    		session = ss;
	    	}catch (Exception e) { }
	    }
	   
	    @OnClose
	    public void onClose(Session ss){
	    	try {
	    		ss.close();
	    	}catch (Exception e) {
			}
	    }
	    
	    @OnError
	    public void onError(Session ss, Throwable error){
	    	try {
	    		ss.close();
	    	}catch (Exception e) {
			} 
	    }
}
