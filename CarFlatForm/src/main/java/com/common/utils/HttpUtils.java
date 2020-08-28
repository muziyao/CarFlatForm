package com.common.utils;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.MultipartBody.Builder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpUtils {
	 
	public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
	public static final MediaType FORM = MediaType.get("application/x-www-form-urlencoded");
	public static final MediaType XML = MediaType.get("application/xml; charset=utf-8");
	public static OkHttpClient client = new OkHttpClient();//可以设置为单例，perform best  
	
	 
	public static String PostJson(String url, String paramster) {
		try {
			return post(url, paramster, JSON);
		} catch (IOException e) {
		}
		return null;
	}
	
	public static String postXML(String url, String paramster) {
		try {
			return post(url, paramster, XML);
		} catch (IOException e) {
		}
		return null;
	}
	
	public static String postForm(String url, String paramster) {
		try {
			return post(url, paramster, FORM);
		} catch (IOException e) {
		 
		}
		return null;
	}
	
	
	private static String post(String url, String paramster,MediaType type) throws IOException {
		   //json   queryString 
		  RequestBody body = RequestBody.create(type,paramster);
		  //建造者模式。
		  Request request = new Request.Builder()
		      .url(url)
		      .post(body)
		      .build();
		  try (Response response = client.newCall(request).execute()) {
		    return response.body().string();
		  }
	}
	
	public static String get(String url) throws IOException {
		  Request request = new Request.Builder()
		      .url(url)
		      .build();
		  try (Response response = client.newCall(request).execute()) {
		    return response.body().string();
		  }
	}
	
	 
	
	public static void postFormNotReadResponse(String url, String paramster) throws IOException {
		   //json   queryString 
		  RequestBody body = RequestBody.create(FORM,paramster);
		  //建造者模式。
		  Request request = new Request.Builder()
		      .url(url)
		      .post(body)
		      .build();
		  try (Response response = client.newCall(request).execute()) {//自动关流。
		  }
	}
	
	public static void upload(String url,File file,String fiedlname) throws IOException {
		    OkHttpClient client = new OkHttpClient();
	        
	        RequestBody requestBody = new MultipartBody.Builder()
	                .setType(MultipartBody.FORM)
	                .addFormDataPart(fiedlname, file.getName(),
	                        RequestBody.create(MediaType.parse("multipart/form-data"), file ))
	                .build();

	        Request request = new Request.Builder()
	                .header("Authorization", "Client-ID " + UUID.randomUUID())
	                .url(url)
	                .post(requestBody)
	                .build();

	        try(Response response = client.newCall(request).execute()) {
			} catch (IOException e) {
				 throw e;
			}
	}
	

	public static String upload(String url,String []fields,File ...file) {
		    OkHttpClient client = new OkHttpClient();
		    Builder builder = new MultipartBody.Builder();
		    builder.setType(MultipartBody.FORM);
		    int i = 0;
	        for(File f :file) {
	        	builder .addFormDataPart(fields[i++], f.getName(), 
	        			RequestBody.create(MediaType.parse("multipart/form-data"), f ));
	 	              
	        }
	        RequestBody requestBody = builder.build();

	        Request request = new Request.Builder()
	                .header("Authorization", "Client-ID " + UUID.randomUUID())
	                .url(url)
	                .post(requestBody)
	                .build();

	        try(Response response = client.newCall(request).execute();) {
				return response.toString();
			} catch (IOException e) {//老子不关心上游的结果。
			}
			return url;
	}
	
	
	 
}
