package com.smokeroom.controller;
 
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.bean.ResultData;
import com.common.controller.BaseController;
import com.common.utils.FileUtils;
import com.common.utils.HttpUtils;
import com.common.utils.JWTUtils;
import com.common.utils.MyStringUtils;
import com.smokeroom.entity.CommonDataBean;
import com.smokeroom.entity.CommonURLParam;
import com.smokeroom.entity.Errlog;
import com.smokeroom.entity.GlobalParam;
import com.smokeroom.entity.OBDRecord;
import com.smokeroom.entity.UpdateRecord;
import com.smokeroom.entity.ext.OBDRecordQueryModel;
import com.smokeroom.entity.json.AcceptFileList;
import com.smokeroom.entity.json.AcceptUploadFile;
import com.smokeroom.mapper.AllDataCommonMapper;
import com.smokeroom.mapper.ErrlogMapper;
import com.smokeroom.mapper.OBDMapper;
import com.smokeroom.mapper.UpdateRecordMapper;
import com.smokeroom.service.Base;
import com.smokeroom.service.CarMonitorRemoteService;
import com.smokeroom.service.MsgConnectionService;
import com.smokeroom.service.RouteService;
import com.smokeroom.service.VoiceService;
import com.smokeroom.ws.DoorCheckWS;
import com.smokeroom.ws.LogWS;
import com.smokeroom.ws.SOSHelpWS;
import com.smokeroom.ws.VideoReivewWS;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


/**
 * 未加权限校验。
 * @author Administrator
 *
 */
@Api("车载主机相关接口")
@RestController
@RequestMapping("carmonitorsys")
public class CarMessageController  extends BaseController{ 
	@Autowired
	private AllDataCommonMapper  mapper;

	@Autowired
	private CarMonitorRemoteService service;
	
	@ApiOperation("司机违法行为检测接口")
	@PostMapping("driver/behavior.action")
	public ResultData execute1( CommonDataBean  bean,String speed ,MultipartHttpServletRequest  rq)  {
		info("司机违法行为检测接口 参数"+bean);
		bean.setTimestamp(MyStringUtils.getDate());
		Map<String,MultipartFile> filemap = getFile(rq);
		MultipartFile iamgefile = filemap.get("image");
		MultipartFile videofile = filemap.get("video");
		try {
			//测试所有图片。
			File uploadimg = FileUtils.saveTempFileAutoDelete( iamgefile.getInputStream(), iamgefile.getOriginalFilename() );
			File uploadvideo = FileUtils.saveTempFileAutoDelete( videofile.getInputStream(), videofile.getOriginalFilename() );
			
			//1 远程调用。
			if(GlobalParam.isserveropen) {
				service.sendBehavior( bean, uploadimg ,uploadvideo);
			}
			//2 上传文件到OSS。
			if(GlobalParam.isdebuger) {
				bean.setDataType( bean.driver_behavior );
				
				String filename = FileUtils.uploadAliOSS( uploadimg, uploadimg.getName() );
				bean.setImagestr( filename );
			 
				String videoname = FileUtils.uploadAliOSS(  uploadvideo,  uploadvideo.getName() );
				bean.setVideostr( videoname );
				
				//用原始收到的速度写入数据库。
				bean.setSRCSpeed(speed);
				mapper.insert(bean);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	 
		return ResultData.success();
	}
	
	
	 
	@ApiOperation("一键救援接口")
	@PostMapping("car/sos.action")
	public ResultData execute2( CommonDataBean  bean,MultipartHttpServletRequest  rq) {
		info("一键救援接口  参数"+bean);
		Map<String,MultipartFile> filemap = getFile(rq);
		MultipartFile iamgefile = filemap.get("image");
		MultipartFile videofile = filemap.get("video");
		try {
			File uploadimg = FileUtils.saveTempFileAutoDelete( iamgefile.getInputStream(), iamgefile.getOriginalFilename() );
			File uploadvideo = FileUtils.saveTempFileAutoDelete( videofile.getInputStream(), videofile.getOriginalFilename() );
			
			//1 远程调用。
			if(GlobalParam.isserveropen) {
				service.sendSOS(bean, uploadimg ,uploadvideo);
			}
			//2  上传文件到OSS。
			if(GlobalParam.isdebuger) {
				
				bean.setDataType( bean.sos );
				
				String filename = FileUtils.uploadAliOSS( iamgefile.getInputStream(), iamgefile.getOriginalFilename() );
				bean.setImagestr( filename );
			 
				String videoname = FileUtils.uploadAliOSS( videofile.getInputStream(), videofile.getOriginalFilename() );
				bean.setVideostr( videoname );
			 
				mapper.insert(bean);
				SOSHelpWS.sendMsg( MyStringUtils.toJSONString( bean ) );
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		return ResultData.success();
	}
	
	
	@Autowired
	private RouteService  rsservice;
	
	@ApiOperation("货箱门检测")
	@PostMapping("door/check.action")
	public ResultData execute78(CommonDataBean bean , MultipartHttpServletRequest  rq) {
		info("货箱门检测： "+bean);
		
		//如果当前是停靠站点。不报警。
		//如果当前不是停靠站点。直接报警。
		if( rsservice.isParking(bean.getDevId())) {
			return ResultData.success();
		} 
		//如果当前不是停靠站点。直接报警。
		
		
		Map<String,MultipartFile> filemap = getFile(rq);
		MultipartFile iamgefile = filemap.get("image");
		MultipartFile videofile = filemap.get("video");
		try {
			File uploadfile = FileUtils.saveTempFileAutoDelete( iamgefile.getInputStream(), iamgefile.getOriginalFilename() );
			File uploadvideo = FileUtils.saveTempFileAutoDelete( videofile.getInputStream(), videofile.getOriginalFilename() );
			
			//1 远程调用。
			if(GlobalParam.isserveropen) {
				if(Integer.parseInt( bean.getSpeed() ) > 5) {
					service.sendDoorCheck( bean , uploadfile,uploadvideo );
				}
			}
			//2  上传文件到OSS。
			if(GlobalParam.isdebuger) {
				
				bean.setDataType( bean.door_check );

				String filename = FileUtils.uploadAliOSS( iamgefile.getInputStream(), iamgefile.getOriginalFilename() );
				bean.setImagestr( filename );
			 
				String videoname = FileUtils.uploadAliOSS( videofile.getInputStream(), videofile.getOriginalFilename() );
				bean.setVideostr( videoname );
				 
				mapper.insert(bean); //插入数据库。
				DoorCheckWS.sendMsg( MyStringUtils.toJSONString( bean ) );
			}
			
		}catch (Exception e) {
		}
		return ResultData.success();
	}
	
	@Autowired
	private MsgConnectionService msgservice;
	//调用长连接给车载主机发送获取文件列表命令
	@GetMapping("video/sendgetfilemsg.action")
	public ResultData sendCMS(String devId,String date,int num) {
		System.out.println("devId = " + devId +"  date="+date);
		try {
			msgservice.sendMsg(devId, "listfile:"+num+":"+date+"\r\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	 
	//调用长连接给车载主机发送下载文件命令
	@GetMapping("video/senddownloadmsg.action")
	public ResultData sendDownLoadMsg(String devId,int num,String date,String filename) {
		File  mp4 = new File( GlobalParam.temp_file + "/" + filename);
		if(mp4.exists() ) {//1优先从本地缓存下载。
			Map<String,String> map = new HashMap<String,String>();
			map.put("msgtype","download");
			VideoReivewWS.sendData(JSONObject.toJSONString( map ));
		}else {//2其次从车载主机哪里获取。
			System.out.println("下载 devId = " + devId +"  date="+date+"  文件="+filename);
			try {
				String msg = "getfile:"+num+":"+date+"/"+filename+"\r\n";
				System.out.println("消息："+msg);
				msgservice.sendMsg(devId, msg);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return ResultData.success();
	}
	
	
	
	//接受文件列表。
	@PostMapping("video/acceptfilelist.action")
	public ResultData acceptFileList(@RequestBody AcceptFileList bean, String devId,String token,HttpServletRequest rq) {
		System.out.println("收到车载主机上传的视频文件列表："+devId+" "+bean);
		if( bean.getCode() == 1 ) {
			bean.setMsgtype("filelist");
			VideoReivewWS.sendData(JSONObject.toJSONString( bean ));
		}
		return ResultData.success();
	}
	
	

	//接受文件列表。
	@GetMapping("video/behavior.action")
	public Map getBehaviorByPage( int page,int limit) {
		CommonDataBean bean = new CommonDataBean();
    	bean.setDataType( bean.driver_behavior);
    	List<CommonDataBean>  data =mapper.getByPage(bean, (page-1)*limit, limit);
		int count = mapper.getByPageTotal(bean); 
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("data", data);
		map.put("code",0);
		map.put("count",count);
		map.put("msg","OK");
		return map;
	}
	
	//接受上传文件名
	@PostMapping("video/upload.action")
	public ResultData AcceptUploadFile(AcceptUploadFile bean, MultipartHttpServletRequest  rq) {
		System.out.println("收到文件="+bean);
		MultipartFile mf = getFile0(rq);
		System.out.println("mf="+mf);
		try {
			FileUtils.copyFile( mf.getInputStream( ), new File( GlobalParam.temp_file + "/" + bean.getFilename( ) ) );
			Map<String,String> map = new HashMap<String,String>();
			map.put("msgtype","download");
			VideoReivewWS.sendData(JSONObject.toJSONString( map ));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ResultData.success();
	}
	
		//车载主机上传完毕之后。前端下载。
		@GetMapping("video/download.action")
		public void download(String filename,HttpServletResponse rp) {
			File f = new File( GlobalParam.temp_file + "/"+filename );
			if( f.exists() ) {
				//读取文件返回到输出流。
				try {
					rp.setContentType("video/mpeg4");
					rp.setContentLength((int) f.length());
					FileUtils.copyFile(f, rp.getOutputStream());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}else {
				
			}
		}
	
	
	private static Map<String,MultipartFile> getFile(MultipartHttpServletRequest  rq) {
		Map<String,MultipartFile> rsmap = new HashMap<String,MultipartFile>();
		MultiValueMap<String, MultipartFile> map = rq.getMultiFileMap();
		 Set<Entry<String, List<MultipartFile>>> sets =  map.entrySet();
		 for(Entry<String, List<MultipartFile>> ent : sets ) {
			 //System.out.println("同名文件："+ ent.getKey());
			 List<MultipartFile> list = ent.getValue();
			 for(MultipartFile mf :list) {
				 rsmap.put( ent.getKey(), mf);
			 }
		 }
		return rsmap;
	}
	
	private static MultipartFile getFile0(MultipartHttpServletRequest  rq) {
	  
		MultiValueMap<String, MultipartFile> map = rq.getMultiFileMap();
		 System.out.println("文件："+ map);
		 Set<Entry<String, List<MultipartFile>>> sets =  map.entrySet();
		 for(Entry<String, List<MultipartFile>> ent : sets ) {
			 System.out.println("同名文件："+ ent.getKey());
			 List<MultipartFile> list = ent.getValue();
			 for(MultipartFile mf :list) {
				 return mf;
			 }
		 }
		 return null;
	}
	 
	@Deprecated
	private static List<InputStream> getFile3(MultipartHttpServletRequest  rq) throws IOException {
		List<InputStream> listmultipartfile = new ArrayList<InputStream>();
		MultiValueMap<String, MultipartFile> map = rq.getMultiFileMap();
		 Set<Entry<String, List<MultipartFile>>> sets =  map.entrySet();
		 for(Entry<String, List<MultipartFile>> ent : sets ) {
			 List<MultipartFile> list = ent.getValue();
			 //System.out.println("同名文件："+ ent.getKey() +  "   "+list.size());
			 for(MultipartFile mf :list) {
				 listmultipartfile.add(mf.getInputStream());
			 }
		 }
		return listmultipartfile;
	}
	
	 
	 
	
	@Autowired
	private VoiceService vs;
	
	@Autowired
	private UpdateRecordMapper ump;
	
 
	@GetMapping("carnum/get.action")
	public String getCarNum(String token,String devId) {
		if(GlobalParam.checkInnerToken(token)) {
			UpdateRecord bean = ump.getByDevId(devId);
			if(bean!=null   ) {
				 return 
				new StringBuilder()
					   .append( bean.getCarNum() ).append(",")
					   .append( ("big".equals( bean.getCarType()) ?1:2 )).append(",")
					   .append( MyStringUtils.getDate()).toString();
			}
			return "devId错误";
		}
		return "token错误";
		
	}
	
	@GetMapping("routeline/get.action")
	public Map<String,Object> getRouteline( String devId) throws Exception {
		StringBuilder param  = new StringBuilder( GlobalParam.remote_server_url+"carmonitorsys/routeline/get.action?" ) ;
		param
		.append("token=").append( URLEncoder.encode(JWTUtils.creatToken(10), "UTF-8") )
		.append("&devId=").append( devId );
		String json_response_str = HttpUtils.get(param.toString());
		Map<String,Object> rsmap = JSONObject.parseObject(json_response_str, Map.class);
		return rsmap;
	}
	
	@Autowired
	private LogWS logws;
	@PostMapping("log/upload.action")
	public ResultData uploadLog(@RequestBody String content,CommonURLParam bean) {
		//发送到页面上。
		Map<String,String> map = new HashMap<String,String>();
		map.put("devId",bean.getDevId());
		map.put("data",content);
		logws.sendMsg( JSON.toJSONString( map ));
		return ResultData.success();
	}
	
	@GetMapping("log/control.action")
	public ResultData logControl(String cmd,CommonURLParam bean) {
		try {
			String str =  "log:"+cmd+"\r\n";
			msgservice.sendMsg(bean.getDevId(),str);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ResultData.success("操作成功");
	}
	
	@Autowired
	private ErrlogMapper errlogmapper;
	@ApiOperation("接收错误日志报告")
	@PostMapping("errlog/upload.action")
	public ResultData errlogUpload(@RequestBody String  content ,CommonURLParam bean) {
		info("接收错误日志报告:"+content+"=="+bean);
		try {
			Errlog errlog = new Errlog();
			errlog.setDevId(bean.getDevId());
			errlog.setToken(bean.getToken());
			errlog.setErrtext(content);
            //插入数据库
            errlogmapper.insert(errlog);
        } catch (Exception e) {
            e.printStackTrace();
            error("[获取request中用POST方式“Content-type”是“text/plain”发送的json数据]异常:{}"+e.getCause());
        }
		return ResultData.success("操作成功");
	}
	
	@ApiOperation("获取错误日志报告")
	@GetMapping("errlog/get.action")
	public ResultData errlogGet(Errlog errlog) {
		info("获取错误日志报告:"+errlog);
		return ResultData.success().setData(errlogmapper.query(errlog));
	}
	
	@GetMapping("analyse/control.action")
	public ResultData analyseControl(String cmd,CommonURLParam bean) {
		try {
			String str =  "analyze:"+cmd+"\r\n";
			msgservice.sendMsg(bean.getDevId(),str);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ResultData.success("操作成功");
	}
	
	@Autowired
	private OBDMapper obdmapper;
	@GetMapping("gps/get.action")
	public ResultData getGPS(OBDRecordQueryModel bean) {
		System.out.println(bean.getTimestamp());
		return ResultData.success("操作成功").setData( obdmapper.query(bean) );
	}
	
	
	
}
