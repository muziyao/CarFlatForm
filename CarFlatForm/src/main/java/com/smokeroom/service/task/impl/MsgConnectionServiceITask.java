package com.smokeroom.service.task.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.common.utils.MyStringUtils;
import com.smokeroom.entity.GlobalParam;
import com.smokeroom.entity.OBDInfo;
import com.smokeroom.entity.OBDRecord;
import com.smokeroom.mapper.OBDMapper;
import com.smokeroom.service.Base;
import com.smokeroom.service.CarMonitorRemoteService;
import com.smokeroom.service.LoopTaskService;
import com.smokeroom.service.MsgConnectionService;
import com.smokeroom.service.RouteService;
 


/**
 * 消息连接业务类。
 * 
 * @author Administrator
 *
 */
 
@Service
public class MsgConnectionServiceITask  extends CommonTimeTask   implements MsgConnectionService,LoopTaskService{
	public static  Map<String,SocketWrapper> socketMap =   new HashMap<String,SocketWrapper>();
	//用于判断心跳包
	private static final int offline_timeout = 15*1000;//超时时间。心跳包超过这个时间不更新就下线设备。
	public MsgConnectionServiceITask() {
		new Thread() {
			public void run() {
				try {
					initServer();
					System.out.println("启动消息长连接服务...");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
		//加入循环任务中。任务管理器默认5S运行一次。
		TimeTaskServiceManager.addTask( this );
	}
	
	


	@Override
	public void run(Object param) {
		 //监测最后更新时间有没有超时。超时就干掉一个。
		long t1 = System.currentTimeMillis(); 
		List<String> removeDevIdlist = new LinkedList<String>();
		//用于删除。因为在for循环中删除Map元素会抛出异常。所以只能先放在一个列表中。
		
		Set<Entry<String,SocketWrapper>> sets = socketMap.entrySet();
		for(Entry<String,SocketWrapper> set : sets ) {
			SocketWrapper wrapper = set.getValue();
			Long t0 = wrapper.getTime();
			if( t0 != null ) {
				long offset = t1 - t0; //认为t1比t0早。
				if(offset > offline_timeout ) { //超过30s要断开。
					String devId = set.getKey();
					removeDevIdlist.add(devId);
					System.out.println("超时设备："+devId+" 加入待关闭列表   time="+System.currentTimeMillis());
				}
			}
		}
		
		//批量移除。
		try {
			for(String devId : removeDevIdlist) {
				SocketWrapper wrapper = socketMap.remove(devId);
				if( wrapper != null ) {
					wrapper.close();
				}
			}
		}catch (Exception e) {
		}
		
	}
	/**
	 * 给车载主机发送数据。
	 * 
	 * @param devId
	 * @param msg
	 * @throws IOException
	 */
	@Override
	public   String sendMsg(String devId,String msgplain) throws IOException {
		SocketWrapper socketwrapper = socketMap.get(devId);
		try {
			Socket socket = socketwrapper.getSocket();
			if ( socket != null ) {
				OutputStream out = socket.getOutputStream();
				out.write(msgplain.getBytes());
				out.flush();
				return "OK";
			}  
		}  catch (Exception e) {
		}
		return "车载主机未连接";
	}
	
	@Autowired
	private RouteService rservice;
	private void initServer() throws Exception {
		Thread.sleep(2000); //确保SpringApplication容器准备好。
		ServerSocket server = new ServerSocket( GlobalParam.msg_service_listening_port);
		while (true) {
			Socket current = server.accept();
			current.setKeepAlive(true);
			try {
				String devId = checkAndSaveClient( current );//如果校验不通过。则关闭当前连接。
				System.out.println("联网设备："+devId+" 时间="+MyStringUtils.getDate());
				//【重要，设备启动/重新联网时初始化路线 】
				rservice.loadRouteLine(devId);
				//【重要，设备启动/重新联网时关闭所有推流】
				sendMsg(devId, close_push_video);
			}catch (Exception e) {//【异常关闭】
				e.printStackTrace();
			}
		}
	}
	
	@Autowired
	private OBDMapper obdmapper;
	
	private void keepAlive(final String devId,Socket current) {
		 new Thread() {
			 public void run() {
				 //java-8新特性。try()自动关流。
				 int num=0;
				 try (BufferedReader bur = new BufferedReader(new InputStreamReader(current.getInputStream()));
					  BufferedWriter buw =new BufferedWriter( new OutputStreamWriter( current.getOutputStream()))){
					 while(true) {
						 String src_obd_data = bur.readLine(); //每次读一行\r\n结束。
						 //更新心跳包时间。
						 SocketWrapper sockrapper = socketMap.get(devId);
						 if( sockrapper != null ) {
							 sockrapper.updateTime();//更新存活时间。
						 }
						 OBDInfo bean = null;
						 try {//加入这个try-catch的目的是为了让解析出错的时候。连接不退出。即车辆不会离线。
							 bean = OBDInfo.parse( src_obd_data  ); //解析后的数据。
							 //GPS没有数据不上传。
							 if(bean == null )continue;
							 //GPS没有数据。不上传。
							 if( OBDInfo.isLatLngEmpty(bean) )continue;
							//GPS没有传入时间过来
							if( bean.getTimestamp() == null || bean.getTimestamp().length() ==0 ) {
								bean.setTimestamp(MyStringUtils.getDate());
							}
							 doOBDService(bean);
						 }catch (Exception e) {
							 System.out.println("OBD解析异常");
						 }finally {
							 insertOBD(bean, src_obd_data);
						 }
						 buw.write("ok\r\n");//给车载主机返回1。
						 buw.flush();
					 }
				} catch (Exception e) {
				}finally {
					ensureClosePeacefully(devId,"异常关闭退出");
				}
			 }
		 }.start();
	}

	private  void insertOBD(OBDInfo obd,String src) {
		OBDRecord bean = new OBDRecord();
		try {
			BeanUtils.copyProperties(obd, bean);
			bean.setSrc(src);
			if(bean.getDevId() ==null || bean.getDevId().length() ==0) {
				bean.setDevId("--no--devId--");
			}
			obdmapper.insert(bean);
		} catch (Exception e) {
		}
	}
	
	private static void doOBDService(OBDInfo bean) {
		//【1】把数据发送给上游。
		if(GlobalParam.isserveropen) {
			try {
				CarMonitorRemoteService service = Base.getBean(CarMonitorRemoteService.class);
				service.sendOBD(bean);
			}catch (Exception e) {
			}
		}
		//【2】路线分析。
		try {
			RouteService rservice = Base.getBean(RouteService.class);
			rservice.run(bean);
		} catch (Exception e) {
		}
		
	}
	/**
	 * 校验和保存车载主机连接。
	 * 如果身份不通过直接，直接关闭连接。
	 * @param current
	 * @throws IOException
	 */
	private   String checkAndSaveClient( Socket current  ) throws IOException {
		byte [] bs = new byte[ 128 ];
		int num = current.getInputStream().read(bs);
		if( num <= 0 ) {
			throw new RuntimeException("客户端身份校验失败1");
		}
		String devId = checkIdentity(bs, 0, num);
		if( devId == null || "".equals(devId) ) {//如果设备编号不为空。
			throw new RuntimeException("客户端身份校验失败2");
		}
		//直接覆盖不考虑关闭上一个。
		SocketWrapper wrapper = new SocketWrapper(current);
		socketMap.put(devId, wrapper);
		//【重要  保活】
		keepAlive(devId,current);
		return devId;
	}
	
	
	/**
	 * 校验车载主机身份。
	 * @param bs
	 * @param off
	 * @param len
	 * @return
	 */
	private static String checkIdentity( byte [] bs , int off , int len) {
		String line = new String(bs,off,len);
		StringTokenizer stk = new StringTokenizer(line,"\r\n");
		Map<String,String> map =  MyStringUtils.parseUrl(stk.nextToken());
		String token = map.get("token");
		if(GlobalParam.inner_token.equals(token)) {
			return map.get("devId");
		}else {
			return null;
		}
	}
	 

	/**
	 * 保证关闭无异常。不管是空指针异常，还是已关闭异常。
	 * @param socket
	 */
	private static void ensureClosePeacefully(String devId,String msg) {
		try {
			SocketWrapper sock = socketMap.get(devId);
			if(sock != null ) {
				socketMap.remove(devId);
				sock.close();
			}
			System.out.println("设备断开："+devId+" 时间="+MyStringUtils.getDate() +" 来源="+msg);
		}catch (Exception e) {
		}
	}
	
	@Override
	public boolean isCarOnline(String devId) {
		return socketMap.containsKey(devId);
	}

	@Override
	public int getOnlineCount() {
		return socketMap.entrySet().size();
	}

	private static class SocketWrapper{
		private Socket socket;
		private long time;
		public SocketWrapper(Socket socket) {
			this.socket = socket;
			this.time = System.currentTimeMillis();
		}
		public Socket getSocket() {
			return socket;
		}
		 
		public long getTime() {
			return time;
		}
		public void updateTime( ) {
			this.time = System.currentTimeMillis();
		}
		public void close() {
			try {
				socket.close();
			}catch (Exception e) {
			}
			this.socket = null;
		}
		
	}

}
