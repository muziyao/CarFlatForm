package com.smokeroom.ws;

import java.nio.ByteBuffer;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;



public class VoiceChatCallBackWS extends BinaryWebSocketHandler {
	 
	    @Override
	    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
	        super.handleBinaryMessage(session, message);
	        ByteBuffer byteBuffer = message.getPayload();
	        byte[] array = byteBuffer.array();
	        //自己给自己发送。
	        session.sendMessage( new BinaryMessage( array ));
	    }
	  
	 
}
