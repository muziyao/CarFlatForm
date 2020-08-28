package com.smokeroom.entity;

public class GlobalParam {
	public static final boolean isdebuger = true; //上线后改为false。
	public static final boolean isserveropen = true;//上线后改为true。
	

	public static final String remote_server_url = "http://gzhxzt.natapp4.cc/swp/car/";//上游基础路径。要改。
//	public static final String remote_server_url = "https://www.qdnyc.top:8082/swp/car/";//上游基础路径。要改。
	//public static final String remote_server_url = "http://localhost/swp/car/";//上游基础路径。要改。
	public static final String route_analysis_service_url = "http://localhost:9090"; //路线分析服务地址。
	public static final int msg_service_listening_port = 12580;//消息长连接端口。
	public static final int voice_service_listening_port = 12590;//语音长连接端口。
	
	
	public static volatile String inner_token = "6899fa62-86b3-4fe3-925e-8afd31dd3370"; //内部token。和车载主机通信用。
	
	public static String temp_file = "D:/Car_Upgrade_temp";//临时上传和下载的文件。
	
	public static String Car_Upgrade_dir = "D:/Car_Upgrade";//升级程序所在目录。
	
	public static String upgradefile = "upgradefile.bin";//升级程序文件名。
	
	public static boolean checkInnerToken(String token) {
		return inner_token.equals(token);
	}
}
