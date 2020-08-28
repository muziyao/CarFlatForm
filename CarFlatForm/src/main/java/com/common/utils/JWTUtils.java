package com.common.utils;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
/**
	1 依赖：
 		
        2 运行main函数测试。
    3 Secret_key按需求改。
 * @author Administrator
 *
 */
public class JWTUtils {
    //加密解密的secret_key。
    public static final String SECRET_KEY = "8162d2e8-6ff6-48ec-81df-71fcf96493c3"; //内部加密密钥。
    
    /**
     *   
     * @param second 从当前时间开始的有效时间。单位s。
     * @return  
     * @throws Exception
     */
    public static String creatToken(int second ) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("alg", "HS256");
        map.put("typ", "JWT");
        Calendar d =  Calendar.getInstance();
        d.add(d.SECOND, second);
        String token = JWT.create()
        		.withHeader(map)
        		.withExpiresAt(d.getTime())
                .sign(Algorithm.HMAC384(SECRET_KEY));
        return token;
    }
    
   
    /**
     *   
     * @param token 
     * @return
     * @throws Exception
     */
    public static boolean verifyToken(String token) throws IOException {
    	if(token == null )return false;
        JWTVerifier verifier = JWT.require( Algorithm.HMAC384( SECRET_KEY ) ).build();
        try {
        	 verifier.verify( token );
        	 return true;
        }catch (Exception e) {
		    return false;
		}
    }
    
    
    public static void main(String[] args) throws Exception {
		 String token = creatToken(6);
		  Thread.sleep( 4000 );
		  boolean flag = JWTUtils.verifyToken( token );
		  System.out.println(flag);
		  
		  Thread.sleep( 4000 );
		  flag = JWTUtils.verifyToken( token );
		  System.out.println(flag);
		 
	}
    
}
