package com.smokeroom.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.common.utils.MyStringUtils;
import com.smokeroom.entity.GlobalParam;
import com.smokeroom.service.VoiceService;
import com.smokeroom.ws.VoiceChatWS;

/**
 *  
 * 语音业务类。
 * @author Administrator
 *
 */
@Service
public class VoiceServiceImpl implements VoiceService{
	private static String lock  = "123";
	private static volatile Map<String,Socket> clients = new ConcurrentHashMap<String, Socket>();
	public VoiceServiceImpl() {
		new Thread() {
			public void run() {
				try {
					initServer();
				} catch (IOException e) {
				}
			}
		}.start();
	}

	public static String getOnlineCars() {
		StringBuilder sb = new StringBuilder();
		Set<String> keysets = clients.keySet();
		for(String key : keysets ) {
			sb.append(key).append("\r\n");
		}
		return sb.toString();
	}

	/**
	 * 给车载主机发送PCM数据。
	 * @param devId
	 * @param msg
	 * @throws IOException
	 */
	@Override
	public   void sendPCMData(String devId,byte []bs,int off, int len){
		Socket socket = clients.get(devId);
		if( socket != null ) {
			//System.out.println("发送语音...");
			try {
				 OutputStream out = socket.getOutputStream();
				 out.write(bs, off, len);
				 out.flush();
			}catch (Exception e) {
				 //如果发生异常。表明客户端已经关闭。
				ensureClosePeacefully(socket);
			} 
		}else {
			System.out.println("车载主机未连接");
		}
		bs = null;
	}

	/**
	 * @throws IOException
	 * 
	 */
	private  static void initServer() throws IOException {
		ServerSocket server = new ServerSocket( GlobalParam.voice_service_listening_port);
		while (true) {
			Socket current = server.accept();
			current.setSoTimeout(30*1000); //30s内 没有连接则挂断。
			try {
				checkAndSaveClient(current);//如果校验不通过。则关闭当前连接。
			}catch (Exception e) {
				ensureClosePeacefully(current);
			}
		}
	}
	 

	private static void checkAndSaveClient( Socket current  ) throws IOException {
		byte [] bs = new byte[128];
		int num = current.getInputStream().read(bs);
		if( num <= 0 ) {
			throw new RuntimeException("客户端身份校验失败:num="+-1);
		}
		String devId = checkIdentity(bs, 0, num);
		if( devId == null ) {//如果设备编号不为空。
			throw new RuntimeException("客户端身份校验失败");
		}
		clients.put(devId, current );
		keepConnectinAlive(devId, current );
	}
	
	private static String checkIdentity( byte [] bs , int off , int len) {
		String line = new String(bs,off,len);
		bs = null;
		StringTokenizer stk = new StringTokenizer(line,"\r\n");
		Map<String,String> map = MyStringUtils.parseUrl(stk.nextToken());
		String token = map.get("token");
		if(GlobalParam.inner_token.equals(token)) {
			return map.get("devId");
		}else {
			return null;
		}
	}
	
	private  static void keepConnectinAlive(String devId, Socket current) {
		 new Thread() {
				public void run() {
					 byte [] bs = new byte[1024];
					 InputStream in = null;
					 //try中的任何代码。都有可能引发异常。因为别的线程打断。
					 try {
						 in = current.getInputStream(); 
						 while( true ) {
							 int num = in.read(bs);// 
							 //System.out.println("收到来自"+devId+"的音频数据："+num+" 字节");
							 if(num > 0 ) {
									onMessageComing(devId,bs,0,num);
							 }else {
								 System.out.println("退出程序。");
								 break;
							 }
							
						 }
					} catch (IOException e) {
						System.out.println("异常关闭一个连接。");
					}finally {
						bs = null ;
						ensureClosePeacefully(current);
						clients.remove(devId);
						if(in!= null ) {
							try {
								in.close();
							} catch (IOException e) {
							}
						}
					}
				}
		}.start();
	}
	
	 
	private static void delay(int num) {
		try {
			Thread.sleep(num);
		} catch (InterruptedException e) {
		}//
	}
	/**
	 * 保证关闭无异常。不管是空指针异常，还是已关闭异常。
	 * @param socket
	 */
	private static void ensureClosePeacefully(Socket socket) {
		try {
			if(socket!= null) {
				 socket.close(); //有可能会报错。因为可能已经被关闭。所以要try-catch。因为这个是通用代码。
			}
			
		} catch (Exception e) {
		}finally {
			socket = null;
		}
	}
	
	
	 
	/**
	 * 收到车载主机的语音对讲。目前只有一台车载主机。
	 * 需要调用WebSocket发送给前端。
	 * @param bs
	 * @param i
	 * @param num
	 */
	protected synchronized static void onMessageComing(String devId,byte[] bs, int off, int len) {
		VoiceChatWS.sendPCMData(devId,bs, off, len);
	}


	@Override
	public  void offLine(String devId) {
		synchronized (clients) {
			Socket client = clients.get(devId);
			if(client != null ) {
				try {
					client.close();//关闭连接。
					clients.remove(devId); //移除它。
				} catch (IOException e) {
				} 
			}

		}
	}


	@Override
	public boolean isClientOnline(String devId) {
		return clients.containsKey(devId);
	}
	 
}
