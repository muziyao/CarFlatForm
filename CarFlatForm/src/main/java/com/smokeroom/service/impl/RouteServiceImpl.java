package com.smokeroom.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.common.utils.MapUtils;
import com.common.utils.HttpUtils;
import com.common.utils.JWTUtils;
import com.smokeroom.entity.GlobalParam;
import com.smokeroom.entity.OBDInfo;
import com.smokeroom.entity.json.ExecuteData;
import com.smokeroom.entity.json.ExecuteFlag;
import com.smokeroom.service.MsgConnectionService;
import com.smokeroom.service.RouteService;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service
public class RouteServiceImpl implements RouteService{
	private static String ready_status = "ready";  //ready   destination   end
	private static Map<String,String> allline = new  ConcurrentHashMap<String, String>();
	private static Map<String,ExecuteFlag> routelineExecuteFlag = new  ConcurrentHashMap<String, ExecuteFlag>();
	@Autowired
	private MsgConnectionService msgservice;
	
	private volatile static Integer gloabal_speed;
	
  
	/**
	 * 加载路线。
	 * @param devId
	 */
	public void loadRouteLine(String devId) {
		//先清除，再加载。
		allline.remove( devId );
		//有速度。尝试加载路线。
		Map<String,Object> rsmap = null;
		try {
			 rsmap = loadRemoteRouteLine(devId);
			  Map data = (Map) rsmap.get("data");
			  String gmax_speed = data.get("globalmax_speed")+"";
			 if( gmax_speed != null ) {
				 gloabal_speed = Integer.parseInt( gmax_speed +"");
			 }
		}catch (Exception e) {
			 System.out.println("加载路线失败：");
			 return;
		}
		String code = rsmap.get("code")+"";
		if( "1".equals( code ) ) {//如果有路线则返回1，没有规划好路线则为0
			//System.out.println("==============加载路线成功，即将初始化路线================");
			try {
				initLocalRouteLine(rsmap,devId);
				allline.put( devId, "OK");
			}catch (Exception e) {
				System.out.println("初始化本地路线失败。");
			}
			System.out.println("===========初始化路线分析系统完毕==============="+rsmap);
		}else {
			System.out.println("加载上游路线失败："+rsmap);
		}
	}

	private static Map<String,Integer> countMap = new HashMap<String,Integer>();
	
	private boolean offsetTimeUp(String devId,int maxtimes) {
		Integer count = countMap.get(devId);
		if( count == null ) {
			count = 0;
		}else {
			count ++;
		}
		
		
		if( count == maxtimes ) {//路线分析 20  * 2 = 40s 播报一次。
			countMap.put(devId,0);
			return true;
		}else {
			countMap.put(devId,count);
		}
		return false;
		
	}
	
	@Override
	public void run(OBDInfo obd ) throws Exception {
		//【没有路线】不进行路线分析。
		String devId = obd.getDevId();
		//不管有没有路线。都要判断全局速度是否超速。
		if(  gloabal_speed!=null && obd.getSpeed()!= null && obd.getSpeed().length() >0 ) {
			if( Integer.parseInt( obd.getSpeed() ) > gloabal_speed ) {
				ExecuteFlag bb = new ExecuteFlag();
				bb.setLimitspeed(true);
				triggleWarning("overspeed",obd,bb);
			}
		}
		 
		
		if( ! allline.containsKey(devId) ) {
			//没有路线。
			return ;
		}
		
		//【速度小于2】不进行路线分析。认为是停车或者接近停车状态。
		if(Integer.parseInt( obd.getSpeed() ) <= 2 ) {
			return;
		}
		
		//System.out.println("====执行路线分析===========车速："+obd.getSpeed() );
		ExecuteData bean = null;
		try {
			 bean = localExecute(obd);
		}catch (Exception e) {
			System.out.println("路线分析异常。");
			 return;
		}
		
		ExecuteFlag exe_rs = bean.getData();
		//把bean作为每个车载主机的全局参数。 货箱门异常检测需要用到 两个参数。1速度。2是否停靠站点。
		if( ! exe_rs.getParking() ) { //不是停靠点。
			if( exe_rs.getOffset() ) {//路线偏移。
				triggleWarning("offset",obd,exe_rs);
			}
		}
		
		//2 危险区域。
		if(  exe_rs.getDanger() ) {
			triggleWarning("danger",obd,exe_rs);
		}
		
		//3 限速区域，并且超速。
		if(  exe_rs.getLimitspeed() ) {
			triggleWarning("overspeed",obd,exe_rs);
		}
		
		//4 禁止区域 
		if(  exe_rs.getProhibit() ) {
			triggleWarning("prohibitarea",obd,exe_rs);
		}
	}
	
	/**
	 * 所有参数。
	 *	devId
		token
		type => offset     danger     overspeed   prohibitarea
		msg
		lng
		lat
		timestamp 
	 * @throws IOException 
	*/
	private void triggleWarning(String type,OBDInfo obd,ExecuteFlag bean) throws IOException {
		System.out.println("报警="+type+" "+bean+"  obd="+obd);
		//路线偏离。频率控制。5次警报一次。也就是10s。
		boolean flag =offsetTimeUp( obd.getDevId(), 5 );
		if( ! flag )return;
		//如果服务端未打开，则关闭请求。
		if( !GlobalParam.isserveropen )return;
		//通用字段消息。
		StringBuilder sb = new StringBuilder( );
		sb
		.append("devId=").append(obd.getDevId())
		.append("&type=").append(type)
		.append("&token=").append(obd.getToken())
		.append("&speed=").append(obd.getSpeed() )
		.append("&lng=").append(obd.getLng())
		.append("&lat=").append( obd.getLat() )
		.append("&timestamp=").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())); 
		
		
		if( "offset".equals( type) ) {
			sb.append("&msg=").append("路线偏移");
		} else if( "danger".equals( type)) {
			sb.append("&msg=").append("您已经进入危险区域，请谨慎驾驶。");
		} else if( "overspeed".equals( type)) {
			sb.append("&msg=").append("您已在限速区域内超速行驶，请减速慢行。");
		}else if("prohibitarea".equals( type ) ) {
			sb.append("&msg=").append("您已进入禁止区域，请尽快驶出禁止区域。");
		}
		
		HttpUtils.postForm(GlobalParam.remote_server_url+"carmonitorsys/routeline/warn.action", sb.toString());
		
		 
	 
		 //2 通知车载主机。语音提示。
		if( bean.getOffset() ) {
			msgservice.sendMsg(obd.getDevId(), msgservice.offset_waning );//通过长连接给车载主机发送语音提示。
		}
		if( bean.getDanger() ) {
			msgservice.sendMsg( obd.getDevId() , msgservice.danger_waning );//通过长连接给车载主机发送语音提示。
		}
		if(bean.getLimitspeed() ) {
			System.out.println("超速报警="+ obd.getDevId());
			msgservice.sendMsg( obd.getDevId() , msgservice.overspeed_waning );//通过长连接给车载主机发送语音提示。
		}
		//4 禁止区域 
		if(  bean.getProhibit() ) {
			msgservice.sendMsg(obd.getDevId(), msgservice.prohibit_waning );//通过长连接给车载主机发送语音提示。
		}
		
	}
	
	
	
	
	/**
	 * 根据车载主机编号加载路线。
	 * @param devId
	 */
	private Map<String,Object> loadRemoteRouteLine(String devId)throws Exception  {
		StringBuilder param  = new StringBuilder( GlobalParam.remote_server_url+"carmonitorsys/routeline/get.action?" ) ;
		param
		.append("token=").append( URLEncoder.encode(JWTUtils.creatToken(1000), "UTF-8") )
		.append("&devId=").append( devId );
		System.out.println("发送参数："+param);
		String json_response_str = HttpUtils.get(param.toString());
		Map<String,Object> rsmap = JSONObject.parseObject(json_response_str, Map.class);
		return rsmap;
	}
	
	/**
	 * 初始化本地路线。
	 * @param rsmap
	 * @param devId
	 * @throws Exception
	 */
	public  void initLocalRouteLine(Map<String,Object> rsmap ,String devId) throws Exception {
		OkHttpClient client = new OkHttpClient();//可以设置为单例，perform best 
		RequestBody body = RequestBody.create(HttpUtils.JSON,JSONObject.toJSONString( rsmap) );
		Request request = new Request.Builder()
				   //后期加上校验。
			      .url( GlobalParam.route_analysis_service_url+"/load?devId="+devId)
			      .method("POST", body)
			      .build();
			  try (Response response = client.newCall(request).execute()) {
			      //此处可能会报错，但无需接受响应。
			  }
	}
	
	/**
	 * 远程执行分析。也就是调用node.js下面的一个http应用进行分析。
	 * 端口9090 经纬度需要转换。
	 * @param obd
	 * @return
	 * @throws IOException
	 */
	public ExecuteData localExecute (OBDInfo obd) throws IOException{
		//经纬度需要在这里转换。
		String lat = obd.getLat();
		String lng = obd.getLng();
		try {
			Map<String,String> lat_lng_map = MapUtils.toBMapPoint( lat,  lng );
			//转换后放回去。
			lat = lat_lng_map.get("lat");
			lng = lat_lng_map.get("lng");
		}catch (Exception e) {
		}
		
		String rs = HttpUtils.get(
				 GlobalParam.route_analysis_service_url
				   +"/execute?"
					+ "token="+obd.getToken()
					+ "&devId="+obd.getDevId()
					+"&lat="+lat
					+"&lng="+lng
					+"&speed="+obd.getSpeed() );
		
		JSONObject json =  (JSONObject) JSONObject.parse( rs ); 
		ExecuteData bean = json.toJavaObject(  ExecuteData.class );
		return bean;
	}
	
	@Override
	public boolean isParking(String devId) {
		ExecuteFlag bean = routelineExecuteFlag.get(devId);
		if(bean == null ) {
			return false;
		}else {
			return bean.getParking();
		}
	}
	 
	
	
	 
}
