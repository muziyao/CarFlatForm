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

import com.smokeroom.service.Base;


@Component
@ServerEndpoint("/obd") 
public class OBDWS implements Base{
	
	protected static List<Session>  sessionlist = Collections.synchronizedList( new  LinkedList<Session> () );
	public synchronized static void sendMsg(String msg) {
		 for(int i=0;i<sessionlist.size();i++) {
			 try {
				sessionlist.get(i).getBasicRemote().sendText( msg );
			} catch (IOException e) {
				e.printStackTrace();
			}
		 }
	 }
 
 
	 @OnOpen
	    public void onOpen(Session ss){
	    	try {
	    		sessionlist.add(ss);
	    	}catch (Exception e) { }
	   }
	   
	    @OnClose
	    public void onClose(Session ss){
	    	try {
	    		ss.close();
	    	}catch (Exception e) {
			}finally {
				sessionlist.remove( ss );
			}
	    }
	    
	    @OnError
	    public void onError(Session ss, Throwable error){
	    	try {
	    		ss.close();
	    	}catch (Exception e) {
			}finally {
				sessionlist.remove( ss );
			}
	    }
}
