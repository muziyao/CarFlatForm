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
@ServerEndpoint("/door") 
public class DoorCheckWS implements Base{
	
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
	
	public static void sendDelayMsg(String msg, int delay) {
		new Thread() {
    		public void run() {
    			try {
					Thread.sleep( delay );
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
    			sendMsg(msg );
    		}
    	}.start();
	}
 
	 @OnOpen
	 public void onOpen(Session ss){
	    	try {
	    		sessionlist.add(ss);
	    		System.out.println(" 连接到来 ");
	    	}catch (Exception e) { }
	    	
	    	//把所有消息加载出来。
	    	CommonDataBean bean = new CommonDataBean();
	    	bean.setDataType( bean.door_check);
	    	sendDelayMsg( MyStringUtils.toJSONString(  Base.getBean(AllDataCommonMapper.class ).get(bean ) ) , 1000);

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
