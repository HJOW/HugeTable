/*
 
 Copyright 2015 HJOW

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 
 */

package hjow.hgtable.util;

import hjow.hgtable.Main;
import hjow.hgtable.Manager;

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

/**
 * <p>이 클래스에는 보안에 관련된 다양한 정적 메소드들이 있습니다.</p>
 * 
 * @author HJOW
 *
 */
public class SecurityUtil
{
	/**
	 * <p>텍스트의 해시값을 구합니다.</p>
	 * 
	 * @param text : 원래의 텍스트
	 * @param algorithm : 해싱 알고리즘
	 * @return 해시값
	 */
	public static String hash(String text, String algorithm)
	{
		MessageDigest digest = null;
		String methods = algorithm;
		if(methods == null) methods = "SHA-256";
		
		try
		{
			digest = MessageDigest.getInstance(methods);
			
			byte[] beforeBytes = text.getBytes("UTF-8");
			byte[] afterBytes = digest.digest(beforeBytes);
			
			StringBuffer results = new StringBuffer("");
			
			for(int i=0; i<afterBytes.length; i++)
			{
				results.append( Integer.toString(((afterBytes[i] & 0xf0) >> 4), 16) );
				results.append(Integer.toString((afterBytes[i] & 0x0f), 16));
			}
									
			return String.valueOf(results);
		}
		catch(Throwable e)
		{
			
		}
		return null;
	}
	
	/**
	 * <p>텍스트를 암호화합니다.</p>
	 * 
	 * @param text : 대상이 되는 텍스트
	 * @param key : 암호화에 쓰일 비밀번호
	 * @param algorithm : 암호화 방법 (null 시 AES 사용)
	 * @return 암호화된 텍스트
	 */
	public static String encrypt(String text, String key, String algorithm)
	{
		try
		{
			String passwords = hash(key, null);
			String methods = algorithm;
			if(methods == null) methods = "AES";
			
			String paddings;
			int need_keySize = -1;
			boolean useIv = false;
			
			byte[] befores = text.trim().getBytes("UTF-8");
			byte[] keyByte = passwords.getBytes("UTF-8");
			
			if(methods.equalsIgnoreCase("DES"))
			{
				paddings = "DES/CBC/PKCS5Padding";
				need_keySize = 8;
				useIv = true;
			}
			else if(methods.equalsIgnoreCase("DESede"))
			{
				paddings = "TripleDES/ECB/PKCS5Padding";
				need_keySize = 24;
				useIv = true;
			}
			else if(methods.equalsIgnoreCase("AES"))
			{
				paddings = "AES";
				need_keySize = 16;
				useIv = false;
			}
			else return null;
			
			byte[] checkKeyByte = new byte[need_keySize];
			byte[] ivBytes = new byte[checkKeyByte.length];
			
			for(int i=0; i<checkKeyByte.length; i++)
			{
				if(i < keyByte.length)
				{
					checkKeyByte[i] = keyByte[i];
				}
				else
				{
					checkKeyByte[i] = 0;
				}
			}
			keyByte = checkKeyByte;
			
			SecretKeySpec keySpec = new SecretKeySpec(keyByte, algorithm);
			IvParameterSpec ivSpec = null;
			if(useIv) ivSpec = new IvParameterSpec(ivBytes);
			
			Cipher cipher = null;
			byte[] outputs;
			
			try
			{
				cipher = Cipher.getInstance(paddings);
				if(useIv)
				{
					cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
				}
				else
				{
					cipher.init(Cipher.ENCRYPT_MODE, keySpec);
				}
				
				outputs = new byte[cipher.getOutputSize(befores.length)];
				for(int i=0; i<outputs.length; i++)
				{
					outputs[i] = 0;
				}
				int enc_len = cipher.update(befores, 0, befores.length, outputs, 0);
				enc_len = enc_len + cipher.doFinal(outputs, enc_len);
				
				return new String(Base64.encodeBase64(outputs), "UTF-8");
			}
			catch(Throwable e)
			{
				Main.logError(e, Manager.applyStringTable("On encrypting"));
				return null;
			}
		}
		catch(Throwable e)
		{
			
		}
		return null;
	}
	
	/**
	 * <p>암호화된 텍스트를 복호화해 원래의 텍스트를 구합니다.</p>
	 * 
	 * @param text : 암호화된 텍스트
	 * @param key : 암호화에 쓰인 비밀번호
	 * @param algorithm : 암호화에 쓰인 암호화 방법 (null 시 AES 로 간주)
	 * @return 원래의 텍스트
	 */
	public static String decrypt(String text, String key, String algorithm)
	{
		try
		{
			String passwords = hash(key, null);
			String methods = algorithm;
			if(methods == null) methods = "AES";
			
			String paddings;
			int need_keySize = -1;
			boolean useIv = false;
			
			byte[] befores = text.getBytes("UTF-8");
			byte[] keyByte = passwords.getBytes("UTF-8");
			
			if(methods.equalsIgnoreCase("DES"))
			{
				paddings = "DES/CBC/PKCS5Padding";
				need_keySize = 8;
				useIv = true;
			}
			else if(methods.equalsIgnoreCase("DESede"))
			{
				paddings = "TripleDES/ECB/PKCS5Padding";
				need_keySize = 168;
				useIv = true;
			}
			else if(methods.equalsIgnoreCase("AES"))
			{
				paddings = "AES";
				need_keySize = 16;
				useIv = false;
			}
			else return null;
			
			befores = Base64.decodeBase64(befores);
			
			byte[] checkKeyByte = new byte[need_keySize];
			byte[] ivBytes = new byte[checkKeyByte.length];
			for(int i=0; i<checkKeyByte.length; i++)
			{
				if(i < keyByte.length)
				{
					checkKeyByte[i] = keyByte[i];
				}
				else
				{
					checkKeyByte[i] = 0;
				}
			}
			keyByte = checkKeyByte;
			
			
			SecretKeySpec keySpec = new SecretKeySpec(keyByte, methods);
			IvParameterSpec ivSpec = null;
			if(useIv) ivSpec = new IvParameterSpec(ivBytes);
			
			Cipher cipher = null;
			
			
			try
			{
				cipher = Cipher.getInstance(paddings);
				if(useIv)
				{
					cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
				}
				else
				{
					cipher.init(Cipher.DECRYPT_MODE, keySpec);
				}
				byte[] outputs = new byte[cipher.getOutputSize(befores.length)];
				for(int i=0; i<outputs.length; i++)
				{
					outputs[i] = 0;
				}
				int enc_len = cipher.update(befores, 0, befores.length, outputs, 0);
				enc_len = enc_len + cipher.doFinal(outputs, enc_len);			
				
				return new String(outputs, "UTF-8").trim();
			}
			catch(Throwable e)
			{
				Main.logError(e, Manager.applyStringTable("On decryption"));
				return text;
			}
		}
		catch(Throwable e)
		{
			
		}
		return null;
	}
}
