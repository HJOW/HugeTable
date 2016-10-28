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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;

import hjow.hgtable.Main;
import hjow.hgtable.Manager;
import hjow.hgtable.streamchain.ChainInputStream;
import hjow.hgtable.tableset.TableSet;

/**
 * <p>네트워킹 관련 정적 메소드들이 있습니다.</p>
 * 
 * @author HJOW
 *
 */
public class NetUtil
{	
	/**
	 * <p>POST 방식으로 요청을 보냅니다. 그에 대한 응답을 텍스트로 반환합니다.</p>
	 * 
	 * @param url : URL
	 * @param parameters : 매개 변수들
	 * @param contentType : HTTP 컨텐츠 타입 옵션, null 시 application/x-www-form-urlencoded 가 사용됨
	 * @param parameterEncoding : 매개 변수 인코딩 방식, null 시 UTF-8 사용
	 * @param post : POST 방식 여부
	 * @return 서버로부터 받은 텍스트
	 * @throws Throwable : 네트워크 문제, URL 문제, 지원되지 않는 인코딩 등
	 */
	public static String sendPost(URL url, Map<String, Object> parameters, String contentType, String parameterEncoding, boolean post) throws Throwable
	{
		return send(url, parameters, null, contentType, parameterEncoding, post);
	}
	
	/**
	 * <p>POST 방식으로 요청을 보냅니다. 그에 대한 응답을 텍스트로 반환합니다.</p>
	 * 
	 * @param url : URL
	 * @param parameters : 매개 변수들
	 * @return 서버로부터 받은 텍스트
	 * @throws Throwable : 네트워크 문제, URL 문제, 지원되지 않는 인코딩 등
	 */
	public static String sendPost(URL url, Map<String, Object> parameters) throws Throwable
	{
		return sendPost(url, parameters, null, null, true);
	}
	
	/**
	 * <p>POST 방식으로 요청을 보냅니다. 그에 대한 응답을 텍스트로 반환합니다.</p>
	 * 
	 * @param url : URL
	 * @param parameters : 매개 변수들
	 * @param contentType : HTTP 컨텐츠 타입 옵션, null 시 application/x-www-form-urlencoded 가 사용됨
	 * @param parameterEncoding : 매개 변수 인코딩 방식, null 시 UTF-8 사용
	 * @param post : POST 방식 여부
	 * @return 서버로부터 받은 텍스트
	 * @throws Throwable : 네트워크 문제, URL 문제, 지원되지 않는 인코딩 등
	 */
	public static String send(URL url, Map<String, Object> parameters, Map<String, String> requestProperty, String contentType, String parameterEncoding, boolean post) throws Throwable
	{
		InputStream gets = sendStream(url, parameters, requestProperty, contentType, parameterEncoding, post);
		StringBuffer results = new StringBuffer("");
		
		ChainInputStream chainStream = null;
		InputStreamReader reader = null;
		BufferedReader bufferedReader = null;
		
		String reads = "";
		
		try
		{
			chainStream = new ChainInputStream(gets);
			if(DataUtil.isNotEmpty(parameterEncoding)) reader = new InputStreamReader(chainStream.getInputStream(), parameterEncoding);
			else  reader = new InputStreamReader(chainStream.getInputStream(), "UTF-8");
			bufferedReader = new BufferedReader(reader);
			
			while(true)
			{
				reads = bufferedReader.readLine();
				if(reads == null) break;
				if(! Main.checkInterrupt(NetUtil.class, Manager.applyStringTable("On sendPost")));
				results = results.append(reads + "\n");
			}
			
			return results.toString();
		}
		catch(Throwable e)
		{
			throw e;
		}
		finally
		{
			try
			{
				bufferedReader.close();
			}
			catch(Throwable e)
			{
				
			}
			try
			{
				reader.close();
			}
			catch(Throwable e)
			{
				
			}
			try
			{
				chainStream.close();
			}
			catch(Throwable e)
			{
				
			}
			try
			{
				gets.close();
			}
			catch(Throwable e)
			{
				
			}
		}
	}
	
	/**
	 * <p>POST 방식으로 요청을 보냅니다.</p>
	 * 
	 * @param url : URL
	 * @param parameters : 매개 변수들
	 * @param contentType : HTTP 컨텐츠 타입 옵션, null 시 application/x-www-form-urlencoded 가 사용됨
	 * @param parameterEncoding : 매개 변수 인코딩 방식, null 시 UTF-8 사용
	 * @param post : POST 방식 여부
	 * @return 서버로부터의 응답을 받는 입력 스트림
	 * @throws IOException
	 */
	public static InputStream sendStream(URL url, Map<String, Object> parameters, String contentType, String parameterEncoding, boolean post) throws IOException
	{
		return sendStream(url, parameters, null, contentType, parameterEncoding, post);
	}
	
	/**
	 * <p>POST 방식으로 요청을 보냅니다.</p>
	 * 
	 * @param url : URL
	 * @param parameters : 매개 변수들
	 * @param requestProperty : 요청 속성, null 가능
	 * @param contentType : HTTP 컨텐츠 타입 옵션, null 시 application/x-www-form-urlencoded 가 사용됨
	 * @param parameterEncoding : 매개 변수 인코딩 방식, null 시 UTF-8 사용
	 * @param post : POST 방식 여부
	 * @return 서버로부터의 응답을 받는 입력 스트림
	 * @throws IOException
	 */
	public static InputStream sendStream(URL url, Map<String, Object> parameters, Map<String, String> requestProperty, String contentType, String parameterEncoding, boolean post) throws IOException
	{
		String parameterTexts = "";
		Set<String> parameterKeySet = parameters.keySet();
		String stringed = null;
		for(String s : parameterKeySet)
		{
			if(parameters.get(s) instanceof TableSet) stringed = ((TableSet) parameters.get(s)).toHGF();
			else stringed = String.valueOf(parameters.get(s));
			if(DataUtil.isNotEmpty(parameterEncoding)) parameterTexts = parameterTexts + "&" + s + "=" + URLEncoder.encode(stringed, parameterEncoding);
			else parameterTexts = parameterTexts + "&" + s + "=" + URLEncoder.encode(stringed, "UTF-8");
		}
		parameterTexts = parameterTexts.trim();
		if(parameterTexts.startsWith("&")) parameterTexts = parameterTexts.substring(1);
		
		if(DataUtil.isNotEmpty(parameterEncoding)) return sendStream(url, parameterTexts.getBytes(parameterEncoding), requestProperty, contentType, post);
		else return sendStream(url, parameterTexts.getBytes("UTF-8"), requestProperty, contentType, post);
	}
	
	/**
	 * <p>요청을 보냅니다.</p>
	 * 
	 * @param url : URL
	 * @param parameters : 매개 변수 이진 데이터
	 * @param contentType : HTTP 컨텐츠 타입 옵션, null 시 application/x-www-form-urlencoded 가 사용됨
	 * @return 서버로부터의 응답을 받는 입력 스트림
	 * @param post : POST 방식 여부
	 * @throws IOException
	 */
	public static InputStream sendStream(URL url, byte[] parameters, String contentType, boolean post) throws IOException
	{
		return sendStream(url, parameters, null, contentType, post);
	}
	
	/**
	 * <p>요청을 보냅니다.</p>
	 * 
	 * @param url : URL
	 * @param parameters : 매개 변수 이진 데이터
	 * @param requestProperty : 요청 속성, null 가능
	 * @param contentType : HTTP 컨텐츠 타입 옵션, null 시 application/x-www-form-urlencoded 가 사용됨
	 * @param post : POST 방식 여부
	 * @return 서버로부터의 응답을 받는 입력 스트림
	 * @throws IOException
	 */
	public static InputStream sendStream(URL url, byte[] parameters, Map<String, String> requestProperty, String contentType, boolean post) throws IOException
	{
		HttpURLConnection connection = null;
		OutputStream out = null;
		
		try
		{			
			connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			if(post) connection.setRequestMethod("POST");
			else connection.setRequestMethod("GET");
			connection.setUseCaches(false);
			connection.setAllowUserInteraction(false);
			if(contentType != null) connection.setRequestProperty("Content-Type", contentType);
			else connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			
			if(DataUtil.isNotEmpty(requestProperty))
			{
				for(String s : requestProperty.keySet())
				{
					connection.setRequestProperty(s, requestProperty.get(s));
				}
			}
			
			out = connection.getOutputStream();
			
			out.write(parameters);
			
			out.flush();
			
			return connection.getInputStream();
		}
		catch(IOException e)
		{
			throw e;
		}
		finally
		{
			try
			{
				out.close();
			}
			catch(Throwable e)
			{
				
			}
		}		
	}	
}
