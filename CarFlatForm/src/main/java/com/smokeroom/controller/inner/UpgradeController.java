package com.smokeroom.controller.inner;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.common.bean.ResultData;
import com.common.utils.FileUtils;
import com.common.utils.MyStringUtils;
import com.smokeroom.entity.CurrentVersion;
import com.smokeroom.entity.GlobalParam;
import com.smokeroom.entity.UpdateRecord;
import com.smokeroom.mapper.UpdateRecordMapper;
import com.smokeroom.service.MsgConnectionService;

@RestController
@RequestMapping("upgrade")
public class UpgradeController {
	
	@Autowired
	private UpdateRecordMapper mapper;
	
	/**
	 * 检测并获取更新。
	 * @param devId
	 * @param token
	 * @param rp
	 */
	@GetMapping("check.action")
	public void check(String devId,String token,HttpServletResponse rp){
		if(devId ==null || "".equals( devId.trim() )) {
			return;
		}
		
		if(!GlobalParam.inner_token.equals( token )) {
			return;
		}
		
		UpdateRecord bean = mapper.getByDevId(devId);
		System.out.println("车载主机请求升级="+bean);
		if( bean == null ) {
			bean = new UpdateRecord();
			bean.setVersion( mapper.getVersion().getVersion());
			bean.setDevId(devId);
			//插入一条记录。
			try {
				bean.setUpgrade("G");//是否需要升级。Y需要。N不需要。G正在升级。
				bean.setCreation(MyStringUtils.getDate() );
				mapper.insert( bean );
				//查询,升级程序。如果存在则返回升级。
				findFile(rp,bean);//耗时操作
				//
				bean.setCreation( MyStringUtils.getDate() );
				bean.setUpgrade("N");//是否需要升级。Y需要。N不需要。G正在升级。
				mapper.update(bean);
			}catch (Exception e) {//异常。还原状态。需要升级。Y
				bean.setCreation( MyStringUtils.getDate() );
				bean.setUpgrade("Y");//是否需要升级。Y需要。N不需要。G正在升级。
				mapper.update(bean);
			}
		}else if(bean.getUpgrade().equals("Y")){
			//查询,升级程序。如果存在则返回升级。
			bean.setDevId(devId);
			try {
				bean.setUpgrade("G");//是否需要升级。Y需要。N不需要。
				bean.setCreation( MyStringUtils.getDate() );
				mapper.update(bean);
				
				findFile(rp,bean);//耗时操作
				
				bean.setCreation( MyStringUtils.getDate() );
				bean.setUpgrade("N");//是否需要升级。Y需要。N不需要。
				mapper.update(bean);
				
			} catch (Exception e) {
				bean.setCreation( MyStringUtils.getDate() );
				bean.setUpgrade("Y");//是否需要升级。Y需要。N不需要。
				mapper.update(bean);
			}
		}else if( "G".equals( bean.getUpgrade()) || "N".equals( bean.getUpgrade() )) {
			//rp.setContentLength(0);
		}
		
	}
	/**
	 * 优先升级独立程序，独立程序不存在，则使用全局程序。
	 * @param rp
	 * @param devId
	 * @throws IOException
	 */
	private  void findFile(HttpServletResponse rp,UpdateRecord bean) throws IOException {
		String devId = bean.getDevId();
		//查询,升级程序。如果存在则返回升级。
		File f = new File( GlobalParam.Car_Upgrade_dir+"/"+devId+"/"+GlobalParam.upgradefile );
		if(f.exists() == false ) {
			f = new File( GlobalParam.Car_Upgrade_dir+"/"+GlobalParam.upgradefile ); 
		}
		if(f.exists()) {
			//rp.setHeader("Content-Disposition", "attachment;filename=upgradefile.bin" );
			//根据程序的版本级别，动态设置Content-Length
			if( "Y".equals( bean.getHasContentLength()) ) { //新版本。需要返回Content-Length。
				rp.setContentLength( (int) f.length() );
			} 
			FileUtils.copyFile(f, rp.getOutputStream() );
			rp.flushBuffer();
			System.out.println("复制成功");
		}
	}
	
	
	@Autowired
	private MsgConnectionService msgService;
	
	
	@GetMapping("record.action")
	public Map<String,Object> getUpgradeResource(String devId,String token){
		Map<String,Object> map = new HashMap<String,Object>();
		/* if(!GlobalParam.inner_token.equals( token )) {
			return;
		  }  */
		
		map.put("version",  mapper.getVersion( ) );
		List<UpdateRecord> list = mapper.getAllUpdateRecord( );
		//更新在线状态。
		for(UpdateRecord r:list) {
			boolean flag = msgService.isCarOnline(r.getDevId());
			if(flag) {
				r.setOnline("Y");
			}
		}
		
		map.put("record",  list  );
		map.put("count",msgService.getOnlineCount());
		 
		return map;
	}
	
	
	@GetMapping("getcarlist.action")
	public ResultData getCarNumList(String devId,String token,String from,HttpServletRequest rq){
		
		List<UpdateRecord> list = mapper.getAllUpdateRecord( );
		//更新在线状态。
		for(UpdateRecord r:list) {
			boolean flag = msgService.isCarOnline(r.getDevId());
			if(flag) {
				r.setOnline("Y");
			}
		}
		if("outer".equals( from )) {
			List<UpdateRecord> list0 = new LinkedList<UpdateRecord>();
			for(UpdateRecord r:list) {
				String carnum = r.getCarNum();
				if(carnum!=null && !"".equals(carnum) && !carnum.contains("A44908")) {
					list0.add(r);
				}
			}
			return ResultData.success().setData( list0 );
		}else {
			return ResultData.success().setData( list );
		}
		
	}
	
	
	private static Map<String,String> uploaduser = new HashMap<String,String>();
	static {
		uploaduser.put("lingyuanhai","lingyuanhai");
		uploaduser.put("raoshutao","raoshutao");
		uploaduser.put("xiaolong","xiaolong");
	}
	
	@PostMapping("beforeupload.action")
	public ResultData beforeupload(String account,String password) {
		if(account ==null || account.length() ==0 || password == null || password.length() ==0 ) {
			return ResultData.fail("请输入用户名或密码");
		}
		String findpassword = uploaduser.get(account);
		if(!password.equals(findpassword)) {
			return ResultData.fail("用户名或密码错误");
		}
		return ResultData.success();
	}
	
	/**
	 * 全局上传文件。前端已经屏蔽。暂时不用。
	 * @param v
	 * @param account
	 * @param password
	 * @param rq
	 * @return
	 */
	@PostMapping("uploadglobal.action")
	public ResultData uploadglobal(CurrentVersion v,String account,String password,MultipartHttpServletRequest  rq) {
		//身份校验。
		ResultData rs = beforeupload(account, password);
		if( rs.getCode() == -1 ) {//说明有错误。
			return rs;
		}
		
		MultipartFile mf = getFile0(rq);
		if(mf ==null ) {
			return ResultData.fail("请选择文件");
		}
		try {
			//针对车型进行修改。
			FileUtils.copyFile(mf.getInputStream(), "" );
			mapper.updateVersion(v);
			
			UpdateRecord ur = new UpdateRecord();
			ur.setCreation(MyStringUtils.getDate());
			ur.setUpgrade("Y"); //全部都需要升级。
			ur.setVersion(v.getVersion());
			ur.setHasContentLength( v.getHasContentLength());
			ur.setDevId( null );
			mapper.update( ur );
		} catch (Exception e) {
			e.printStackTrace();
			return ResultData.fail("上传失败："+e.getMessage());
		}
		return ResultData.success();
	}
	
	/**
	 * 单台上传文件。 主要用这个。
	 * @param bean
	 * @param account
	 * @param password
	 * @param rq
	 * @return
	 */
	@PostMapping("uploadsingle.action")
	public ResultData uploadsingle(UpdateRecord bean,String account, String password,MultipartHttpServletRequest  rq) {
		//身份校验。
		ResultData rs = beforeupload(account, password);
		if( rs.getCode() == -1 ) {//说明有错误。
			return rs;
		}
		if( bean.getHasContentLength() ==null  || bean.getHasContentLength().length() ==0 ) {
			return ResultData.fail("请设置内容长度");
		}
		if( bean.getVersion() ==null  || bean.getVersion().length() ==0 ) {
			return ResultData.fail("请填写版本号");
		}
		if( bean.getDevId() ==null  || bean.getDevId().length() ==0 ) {
			return ResultData.fail("设备编号为空。请联系管理员");
		}
		
		System.out.println("上传参数："+ bean );
		MultipartFile mf = getFile0(rq);
		if(mf ==null ) {
			return ResultData.fail("请选择文件");
		}
		try {
			//针对车型进行修改。
			FileUtils.copyFile(mf.getInputStream(), bean.getDevId() );
			bean.setCreation(MyStringUtils.getDate());
			bean.setUpgrade("Y"); 
			bean.setVersion(  getFileName(mf.getOriginalFilename())  );
			System.out.println("bean="+bean);
			mapper.update( bean );
		} catch (Exception e) {
			e.printStackTrace();
			return ResultData.fail("上传失败："+e.getMessage());
		}
		return ResultData.success();
	}
 
	
	public static String getFileName(String filename) {
		try {
			int i= filename.indexOf("_");
			int j = filename.lastIndexOf(".");
			filename = filename.substring(i+1,j);
			return filename;
		}catch (Exception e) {
		}
		return filename;
	}
	
	
	/**
	 * 单台或全部设备更新升级状态。devId为空时，更新全部。devId不为空时，更新单台。
	 * @param bean
	 * @return
	 */
	@PostMapping("updateAllDeviceStatus.action")
	public void updateAllDeviceStatus( UpdateRecord bean ) {
		mapper.update(bean);
	}

	private static MultipartFile getFile0(MultipartHttpServletRequest  rq) {
		MultiValueMap<String, MultipartFile> map = rq.getMultiFileMap();
		 Set<Entry<String, List<MultipartFile>>> sets =  map.entrySet();
		 for(Entry<String, List<MultipartFile>> ent : sets ) {
			 //System.out.println("同名文件："+ ent.getKey());
			 List<MultipartFile> list = ent.getValue();
			 for(MultipartFile mf :list) {
				 return mf;
			 }
		 }
		 return null;
	}
	
	
	@PostMapping("rebootdevice.action")
	public ResultData rebootdevice(String account, String password,String devId) {
		//身份校验。
		ResultData rs = beforeupload(account, password);
		if( rs.getCode() == -1 ) {//说明有错误。
			return rs;
		}
		try {
			msgService.sendMsg(devId, "device:reboot\r\n");
		} catch (IOException e) {
		}
		return ResultData.success("操作成功");
	}
	
	@PostMapping("upgradecheck.action")
	public ResultData upgradecheck(String account, String password,String devId) {
		//身份校验。
		ResultData rs = beforeupload(account, password);
		if( rs.getCode() == -1 ) {//说明有错误。
			return rs;
		}
		try {
			msgService.sendMsg(devId, "device:upgrade\r\n");
		} catch (IOException e) {
		}
		return ResultData.success("操作成功");
	}
	
}
