package com.smokeroom.ws;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import com.common.utils.JWTUtils;
import com.common.utils.MyStringUtils;
import com.smokeroom.entity.GlobalParam;
import com.smokeroom.service.Base;
import com.smokeroom.service.MsgConnectionService;
import com.smokeroom.service.VoiceService;


public class VoiceChatWS extends BinaryWebSocketHandler implements Base {
	private static volatile  Map<String,WebSocketSession> sessionmap = new ConcurrentHashMap<String, WebSocketSession>();
	private static final String lock ="hello world";
	private static volatile VoiceService vservice = null;
	
	
	private static void ensureVoiceServiceReady() {
		if(vservice == null) {
    		//无需加锁，因为本来Spring已经保证了它的单实例。
    		vservice = Base.getBean( VoiceService.class );
    	}
	}
	
	private static  String getDevId( WebSocketSession ss) {
		Set<Entry<String,WebSocketSession>> set = sessionmap.entrySet();
		for(Entry<String,WebSocketSession> enty    : set) {
			if( enty.getValue() == ss ) {
				return enty.getKey();
			}
		}
		return null;
	}
	
	private static  boolean isOld( WebSocketSession ss) {
		return sessionmap.containsValue( ss );
	}
	
	
	/**
	 * 客户端有音频数据到来时，就发送给车载主机。
	 */
    @Override
    protected void handleBinaryMessage(WebSocketSession ss, BinaryMessage message) throws Exception {
    	 
    	ensureVoiceServiceReady();
	    if( !isOld(ss) ) {
	    	//1.2 新的连接 提取消息认证权限
    		super.handleBinaryMessage(ss, message);
	        ByteBuffer byteBuffer = message.getPayload();
	        byte[] array = byteBuffer.array();
	        //校验参数为：token=xxx&divId=xxxx\r\n
	        //一共 159+2=161字节。
	        if(array.length < 180 ) {
	        	String devId = readIdentity(array, 0, array.length );
	        	System.out.println("devId="+devId);
	        	array = null;
	        	if( devId == null ) {//如果设备编号不为空。
	        		 closePeacefully(ss);
				}else {
					//新连接校验通过后。也不能往下走。
					saveConnectionClient( devId,ss );
					return;
				}
	        } 
	    }
    		 
    			
    		
    	
    	//发数据前先判断 车载主机离线。
	    String devId = getDevId(ss);
    	if(!vservice.isClientOnline(devId)) {
    		 //System.out.println("车载主机离线。。。");
    		return;//
    	}
    	
    	super.handleBinaryMessage(ss, message);
        ByteBuffer byteBuffer = message.getPayload();
        byte[] array = byteBuffer.array();
        
		
        //2 校验通过再保存。
        
        //发送数据。
        vservice.sendPCMData(devId,array, 0, array.length );
        byteBuffer = null;
        array = null;
        
    }
	  
	    private void saveConnectionClient(String devId,WebSocketSession ss) {
	    	 synchronized (lock) {
	    		 WebSocketSession old_ss = sessionmap.get(devId);
	    		 if(old_ss != ss ) {
	    			 sessionmap.remove(devId);
	    		 }
	    		 sessionmap.put(devId, ss); //把新连接放入容器中保存。
			}
		}
 

		private  void closePeacefully(WebSocketSession ss) {
	    	try {
				ss.close();
			} catch (IOException e) {
			}finally {
				 System.out.println("手动关闭:"+ss);
			}
	    }
	   
	   
		private static String readIdentity( byte [] bs , int off , int len) throws IOException {
			String line = new String(bs,off,len);
			StringTokenizer stk = new StringTokenizer(line,"\r\n");
			Map<String,String> map = MyStringUtils.parseUrl(stk.nextToken());
			String token = map.get("token");
			boolean ok = JWTUtils.verifyToken(token);
			if(ok) {
				return map.get("devId");
			}else {
				return null;
			}
		}
		
		
	  public void afterConnectionEstablished(WebSocketSession ss) throws Exception {
		 
	  }

		 
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		 ensureVoiceServiceReady();
		  System.out.println("前端断开");
		  synchronized (lock) {
			  //挂断当前车载主机。
			  String devId = getDevId(session);
			  //必须要挂断。
			  vservice.offLine( devId );
			  session = null;
		}
	}
	
	  public  static void sendPCMData(String devId, byte [] bs,int off, int len) {
		  WebSocketSession session = sessionmap.get(devId);
		  if( session != null && session.isOpen() ) {
			  try {
				 int length = len;
				 if( length %2 == 1)length = length -1;
				 //System.out.println("数据到这："+length);
				session.sendMessage( new BinaryMessage( bs,off,length ,true));
			 } catch (IOException e) {
				 System.out.println("发送异常");
			}
		  }else {
		  }
	  }
	  
	  public  static void sendPCMData(String devId,byte [] bs) {
		  sendPCMData(devId,bs, 0, bs.length);
	  }
	   
}
