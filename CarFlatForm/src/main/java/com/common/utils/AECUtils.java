package com.common.utils;
import java.io.UnsupportedEncodingException;
import java.security.AlgorithmParameters;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
/**
 * ACE简介
 *  加密：原始内容、原始秘钥、初始向量。
 *  
 *  解密：加密后内容、原始秘钥、初始向量。
 * @author Administrator
 *
 */
public class AECUtils {
	private static boolean initialized = false;

    /**
     * AES解密加密
     * @param originalContent
     * @param encryptKey
     * @param ivByte
     * @return
     */
    public static byte[] encrypt(byte[] originalContent, byte[] encryptKey, byte[] ivByte) {
        initialize();
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec skeySpec = new SecretKeySpec(encryptKey, "AES");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, new IvParameterSpec(ivByte));
            byte[] encrypted = cipher.doFinal(originalContent);
            return encrypted;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    
    
    /**
     * AES解密
     * 填充模式AES/CBC/PKCS7Padding
     * 解密模式128
     * @param content
     *            目标密文
     * @return
     * @throws Exception 
     * @throws InvalidKeyException 
     * @throws NoSuchProviderException
     */
    private static byte[] decrypt(byte[] content, byte[] aesKey, byte[] ivByte) {
        initialize();
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            Key sKeySpec = new SecretKeySpec(aesKey, "AES");
            cipher.init(Cipher.DECRYPT_MODE, sKeySpec, generateIV(ivByte));// 初始化
            byte[] result = cipher.doFinal(content);
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    
    private static void initialize() {
        if (initialized)
            return;
        Security.addProvider(new BouncyCastleProvider());
        initialized = true;
    }
    
    
    //生成iv
    private static AlgorithmParameters generateIV(byte[] iv) throws Exception {
        AlgorithmParameters params = AlgorithmParameters.getInstance("AES");
        params.init(new IvParameterSpec(iv));
        return params;
    }
    
    public static String decrypt(String iv, String session_key,String encryptedData) {
    	byte [] iv_bytes = Base64.getDecoder().decode(iv );
    	byte []aeskey_bytes = java.util.Base64.getDecoder().decode( session_key ); 
    	byte [] encryptedData_bytes = java.util.Base64.getDecoder().decode( encryptedData );
    	byte [] data = decrypt(encryptedData_bytes, aeskey_bytes, iv_bytes);
    	try {
			String utf8_data = new String(data,"UTF-8");
			return utf8_data;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
    	return null;
    }
}