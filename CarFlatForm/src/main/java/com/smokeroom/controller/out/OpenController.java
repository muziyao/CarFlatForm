package com.smokeroom.controller.out;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.common.bean.ResponseMessage;
import com.common.bean.ResultData;
import com.common.controller.BaseController;
import com.common.utils.JWTUtils;
import com.smokeroom.entity.GlobalParam;
import com.smokeroom.entity.VideoControllerBean;
import com.smokeroom.service.MsgConnectionService;
import com.smokeroom.service.RouteService;
import com.smokeroom.service.VoiceService;
import com.smokeroom.service.impl.VoiceServiceImpl;
import com.smokeroom.service.task.impl.VideoKeepAliveTask;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
@Api("车载主机相关接口")
@RestController
@RequestMapping("openapi")
public class OpenController  extends BaseController{
	 
    @Autowired
	private MsgConnectionService  msgService;
    
    @Autowired
    private RouteService routeService;
    
    
	@ApiOperation("车辆状态")
	@GetMapping("car/status.action")
	public ResponseMessage execute789(String token ,String devId) { //http://localhost:8083/carmonitorsys/route/sync.action
		boolean flag;
		try {
			flag = JWTUtils.verifyToken(token);
			if( flag) {
				//上游不需要做业务逻辑，只需要做显示用。因此返回字符串即可。{data:"在线" }
				return ResponseMessage.ok().setData(msgService.isCarOnline(devId)?"在线":"离线");
			} 
		} catch (Exception e) {
		}
		return ResponseMessage.fail( "token校验失败");
	} 
	
	//临时用。
	@GetMapping("test/token.action")
	public ResultData testToken() {
		try {
			return ResultData.success().setData(JWTUtils.creatToken(30));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	
	@ApiOperation("打开/关闭摄像头")
	@GetMapping("video/control.action")
	public ResponseMessage execute(VideoControllerBean bean) { //http://localhost:8083/carmonitorsys/route/sync.action
		boolean flag;
		try {
			flag = JWTUtils.verifyToken(bean.getToken());
			System.out.println("bean="+bean);
			if( flag) {
				//推流/关流命令  video_push:operation:num\r\n
				String msg = "video_push:"+bean.getOperation()+":"+bean.getVideonum()+"\r\n";
				msgService.sendMsg(bean.getDevId(), msg);
				System.out.println("msg="+msg);
				//添加定时任务
				VideoKeepAliveTask.addOne(bean.getDevId() );
				return ResponseMessage.ok().setData("操作成功");
			} 
		} catch (Exception e) {
			 
		}
		return ResponseMessage.fail( "token校验失败" );
		 
	} 
	
	@Autowired
	private VoiceService voiservice;
	
	
	@ApiOperation("查询和后台通信的车载主机接口")
	@GetMapping("car/getvoiceonline.action")
	public ResponseMessage execute11( String token) { 
		boolean flag;
		Map<String,String> map = new HashMap<String,String>();
		try {
			flag = JWTUtils.verifyToken( token );
			if( flag) {
				VoiceServiceImpl impl = (VoiceServiceImpl) voiservice;
				return ResponseMessage.ok().setData( impl.getOnlineCars() );
			} 
		} catch (Exception e) {
			 
		}
		return ResponseMessage.fail( "token校验失败" );
		 
	} 
	
	@ApiOperation("保活接口。")
	@GetMapping("video/keepalive.action")
	public ResponseMessage keepVideoAlive( String token,String devId) { 
		boolean flag;
		try {
			flag = JWTUtils.verifyToken( token );
			if( flag) {
				VideoKeepAliveTask.updateTime(devId);
				return ResponseMessage.ok();
			} 
		} catch (Exception e) {
			 
		}
		return ResponseMessage.fail( "token校验失败" );
		 
	} 
	
	 
	
	@ApiOperation("语音控制接口。")
	@GetMapping("voice/control.action")
	public ResponseMessage voiceControl( String token,String devId,String operation) { 
		//1 操作错误。
		if(!"on".equals( operation ) && !"off".equals( operation )) {
			return ResponseMessage.fail( "operation参数错误。只能选择on或off" );
		}
		try {
			//2身份校验。
			boolean flag = JWTUtils.verifyToken( token );
			if( flag) {
				//3 指令控制： 打开或关闭语音连接。
				String msg="操作成功";
				if( "on".equals( operation ) ) {
					msg = msgService.sendMsg( devId, "voice_speak:\r\n");
				}else {
					voiservice.offLine( devId );//让车载主机下线。
				}
				return ResponseMessage.ok().setMsg( msg );
			} 
		} catch (Exception e) {
			 
		}
		return ResponseMessage.fail( "token校验失败" );
		 
	} 
	
	 
	@Deprecated  //上线后删除。
	@ApiOperation("断开语音连接接口。")
	@GetMapping("voice/offline.action")
	public ResponseMessage offline( String token,String devId) { 
		boolean flag;
		try {
			flag = JWTUtils.verifyToken( token );
			if( flag) {
				voiservice.offLine( devId );//让车载主机下线。
				return ResponseMessage.ok();
			} 
		} catch (Exception e) {
			 
		}
		return ResponseMessage.fail( "token校验失败" );
		 
	} 
	
	/**
	 * 上线后删除。
	 * @param token
	 * @param devId
	 * @return
	 */
	@Deprecated  
	@ApiOperation("呼叫车载主机连接接口。")
	@GetMapping("voice/connection.action")
	public ResponseMessage connection( String token,String devId) { 
		boolean flag;
		try {
			flag = JWTUtils.verifyToken( token );
			if( flag) {
				//通过消息呼叫车载主机快点发送语音长连接过来。
				String msg = msgService.sendMsg(devId, "voice_speak:\r\n");
				
				return ResponseMessage.ok().setMsg(msg);
			} 
		} catch (Exception e) {
			 
		}
		return ResponseMessage.fail( "token校验失败" );
		 
	} 
	
}
