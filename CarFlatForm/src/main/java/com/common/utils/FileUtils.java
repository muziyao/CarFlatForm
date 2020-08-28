package com.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.smokeroom.entity.GlobalParam;

public class FileUtils {
	
	public static File saveTempFileAutoDelete(InputStream in,String src_name) throws IOException {
		String filename = MyStringUtils.randomName(src_name);
		File f = copyFile(in, new File(GlobalParam.temp_file+"/" + filename));
		return f;
	}
	
	
	 
	
	public static File copyFile(InputStream in,File f) throws IOException {
		if(f.exists() == false )f.getParentFile().mkdirs();
		if(f.exists())f.delete(); //删除旧的。
		f.createNewFile();
		FileOutputStream out = new FileOutputStream( f );
		byte [] bs = new byte[1024];
		while(true) {
			 int num = in.read(bs);
			 if( num == -1 )break;
			 out.write(bs, 0, num);
		 }
		bs = null; 
		out.close();
		out = null;
		return f;
	}
	
	public static void deleteAllFile(File dir,int offset) {
		File []fs = dir.listFiles();
		long now = System.currentTimeMillis();
		if(fs != null ) {
			for(File f:fs) {
				long t2 = f.lastModified();
			    int rest = (int) ((now - t2)/1000);
			    if( rest > offset ) {//超时删除一个文件。
			    	f.delete();
			    }
			}
		}
	}
	 
	/**
	 * 上传升级程序到每个devId相应的目录。
	 * @param in
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	public static void copyFile(InputStream in,String devId) throws IOException {
		FileOutputStream out = null;
		if(devId == null || "".equals( devId.trim() )) {//全局上传。
			//先把其他子目录中的文件清空。
			File dir = new File(GlobalParam.Car_Upgrade_dir);
			if(dir.exists()) {
				File fs [] = dir.listFiles();
				for(File f :fs) {
					if(f.isDirectory()) {
						File [] sfs = f.listFiles();
						for(File sf : sfs) {
							if(sf.exists())sf.delete();
						}
					}
				}
			}
			
			out = new FileOutputStream( GlobalParam.Car_Upgrade_dir +"/"+GlobalParam.upgradefile );
		}else {//独立上传。
			File dir = new File(GlobalParam.Car_Upgrade_dir +"/"+devId+"/"+GlobalParam.upgradefile );
			dir.getParentFile().mkdirs();
			out = new FileOutputStream( dir );
		}
		
		byte [] bs = new byte[1024];
		while(true) {
			 int num = in.read(bs);
			 if( num == -1 )break;
			 out.write(bs, 0, num);
		 }
		bs = null; 
		out.close();
		out = null;
	}
	 
	
	public static File copyFile(File f,OutputStream out) throws IOException {
		FileInputStream fin = new FileInputStream( f );
		byte [] bs = new byte[1024];
		while(true) {
			 int num = fin.read(bs);
			 if( num == -1 )break;
			 out.write(bs, 0, num);
		 }
		bs = null; 
		out = null;
		return f;
	}
	
	private static final String ali_oss_host = "https://smokeroom.oss-cn-beijing.aliyuncs.com";
	
	
	public static String uploadAliOSS(InputStream inputStream,String filename) {
		// Endpoint以杭州为例，其它Region请按实际情况填写。
		String endpoint = "oss-cn-beijing.aliyuncs.com";
		// 云账号AccessKey有所有API访问权限，建议遵循阿里云安全最佳实践，创建并使用RAM子账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建。
		String accessKeyId = "LTAI4FhR1cQLXY2vQRRhkXL8";
		String accessKeySecret = "T0HeCFIuqy6v3zszkY5BUOYaolRV5E";

		// 创建OSSClient实例。
		OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
		
		StringBuilder sb = new StringBuilder();
		String date = new SimpleDateFormat("yyyy-MM-dd").format( new Date() );
		sb.append("carmonitor_sys/")    //根目录。
		.append(date)					//日期目录。
		.append("/")
		.append(  MyStringUtils.randomName( filename)  ); //文件名。
		try {
			ossClient.putObject("smokeroom", sb.toString(), inputStream);
			//https://smokeroom.oss-cn-beijing.aliyuncs.com/carmonitor_sys/hello.mp3
			inputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// 关闭OSSClient。
		ossClient.shutdown();
		return ali_oss_host.concat("/").concat( sb.toString() );
	}
	 
	
	public static String uploadAliOSS(File file,String filename) {
		// Endpoint以杭州为例，其它Region请按实际情况填写。
		String endpoint = "oss-cn-beijing.aliyuncs.com";
		// 云账号AccessKey有所有API访问权限，建议遵循阿里云安全最佳实践，创建并使用RAM子账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建。
		String accessKeyId = "LTAI4FhR1cQLXY2vQRRhkXL8";
		String accessKeySecret = "T0HeCFIuqy6v3zszkY5BUOYaolRV5E";

		// 创建OSSClient实例。
		OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
		
		StringBuilder sb = new StringBuilder();
		String date = new SimpleDateFormat("yyyy-MM-dd").format( new Date() );
		sb.append("carmonitor_sys/")    //根目录。
		.append(date)					//日期目录。
		.append("/")
		.append(  MyStringUtils.randomName( filename)  ); //文件名。
		try {
			ossClient.putObject("smokeroom", sb.toString(), file );
			//https://smokeroom.oss-cn-beijing.aliyuncs.com/carmonitor_sys/hello.mp3
			 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// 关闭OSSClient。
		ossClient.shutdown();
		return ali_oss_host.concat("/").concat( sb.toString() );
	}
	 
	
	
	 
}
 