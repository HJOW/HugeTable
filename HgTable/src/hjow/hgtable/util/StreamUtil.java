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
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import hjow.hgtable.Main;
import hjow.hgtable.Manager;
import hjow.hgtable.streamchain.ChainInputStream;
import hjow.hgtable.streamchain.ChainOutputStream;
import hjow.hgtable.streamchain.ChainReader;
import hjow.hgtable.streamchain.ChainWriter;

/**
 * <p>이 클래스에는 입출력과 관련한 정적 메소드들이 있습니다. 이를 통해 파일을 저장하거나 불러옵니다.</p>
 * 
 * @author HJOW
 *
 */
public class StreamUtil
{
	protected static int BUFFER_SIZE = 1024;
	
	/**
	 * <p>텍스트 파일로부터 텍스트를 읽어 들입니다.</p>
	 * 
	 * @param target : 읽을 파일에 대한 파일 객체입니다.
	 * @param charset : 캐릭터 셋, UTF-8 사용을 권장합니다.
	 * @return 읽은 내용
	 */
	public static String readText(File target, String charset)
	{
		return readText(target, charset, null);
	}
	
	/**
	 * <p>텍스트 파일로부터 텍스트를 읽어 들입니다.</p>
	 * 
	 * @param target : 읽을 파일에 대한 파일 객체입니다.
	 * @param charset : 캐릭터 셋, UTF-8 사용을 권장합니다.
	 * @param useUnzip : GZIP 스트림을 거칠 지 여부를 지정합니다.
	 * @return 읽은 내용
	 */
	public static String readText(File target, String charset, boolean useUnzip)
	{
		return readText(target, charset, null, useUnzip);
	}
	
	/**
	 * <p>텍스트 파일로부터 텍스트를 읽어 들입니다.</p>
	 * 
	 * @param target : 읽을 파일에 대한 파일 객체입니다.
	 * @param charset : 캐릭터 셋, UTF-8 사용을 권장합니다.
	 * @param ignoringPrefix : 주석 표시, 이 내용으로 시작하는 줄은 무시합니다. 중간에 이 내용이 있으면 무시되지 않습니다.
	 * @return 읽은 내용
	 */
	public static String readText(File target, String charset, String ignoringPrefix)
	{
		return readText(target, charset, ignoringPrefix, false);
	}
	
	/**
	 * <p>텍스트 파일로부터 텍스트를 읽어 들입니다.</p>
	 * 
	 * @param target : 읽을 파일에 대한 파일 객체입니다.
	 * @param charset : 캐릭터 셋, UTF-8 사용을 권장합니다.
	 * @param ignoringPrefix : 주석 표시, 이 내용으로 시작하는 줄은 무시합니다. 중간에 이 내용이 있으면 무시되지 않습니다.
	 * @param useUnzip : GZIP 스트림을 거칠 지 여부를 지정합니다.
	 * @return 읽은 내용
	 */
	public static String readText(File target, String charset, String ignoringPrefix, boolean useUnzip)
	{
		FileInputStream fileStream = null;
		ChainInputStream chainStream = null;
		InputStreamReader reader = null;
		BufferedReader bufferedReader = null;
		try
		{
			fileStream = new FileInputStream(target);
			chainStream = new ChainInputStream(fileStream);
			if(useUnzip)
			{
				chainStream.put(java.util.zip.GZIPInputStream.class);
			}
			StreamUtil.additionalSetting(chainStream);
			if(charset == null) reader = new InputStreamReader(chainStream.getInputStream());
			else  reader = new InputStreamReader(chainStream.getInputStream(), charset);
			bufferedReader = new BufferedReader(reader);
			
			String readLine;
			StringBuffer reads = new StringBuffer("");
			
			while(true)
			{
				readLine = bufferedReader.readLine();
				if(readLine == null) break;
				if(ignoringPrefix != null)
				{
					if(readLine.trim().startsWith(ignoringPrefix)) continue;
				}
				reads = reads.append(readLine + "\n");
				
				if(! (Main.checkInterrupt(StreamUtil.class, "readText File")))
				{
					reads = new StringBuffer("");
					break;
				}
			}
			
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
				fileStream.close();
			}
			catch(Throwable e)
			{
				
			}
			
			return remove65279(reads.toString());
		}
		catch(Throwable e)
		{
			Main.logError(e, Manager.applyStringTable("On reading text file") + " : " + target);
			return null;
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
				fileStream.close();
			}
			catch(Throwable e)
			{
				
			}
		}
	}
	
	/**
	 * <p>파일로부터 맵을 읽어 들입니다.</p>
	 * 
	 * @param target : 읽을 파일에 대한 파일 객체입니다.
	 * @param useUnzip : GZIP 스트림을 거칠 지 여부를 지정합니다.
	 * @return 읽은 내용
	 */
	public static Map<String, ?> readMap(File target, boolean useUnzip)
	{
		FileInputStream fileStream = null;
		ChainInputStream chainStream = null;
		try
		{
			fileStream = new FileInputStream(target);
			chainStream = new ChainInputStream(fileStream);
			if(useUnzip)
			{
				chainStream.put(java.util.zip.GZIPInputStream.class);
			}
			StreamUtil.additionalSetting(chainStream);
			
			Properties prop = new Properties();
			prop.loadFromXML(chainStream.getInputStream());
			Set<String> keys = prop.stringPropertyNames();
			Map<String, Object> map = new Hashtable<String, Object>();
			for(String k : keys)
			{
				map.put(k, prop.getProperty(k));
			}
			
			return map;
		}
		catch(Throwable e)
		{
			Main.logError(e, Manager.applyStringTable("On reading text file") + " : " + target);
			return null;
		}
		finally
		{
			try
			{
				chainStream.close();
			}
			catch(Throwable e)
			{
				
			}
			try
			{
				fileStream.close();
			}
			catch(Throwable e)
			{
				
			}
		}
	}
	
	/**
	 * <p>URL로부터 맵을 읽어 들입니다.</p>
	 * 
	 * @param target : 읽을 URL에 대한 URL 객체입니다.
	 * @param useUnzip : GZIP 스트림을 거칠 지 여부를 지정합니다.
	 * @return 읽은 내용
	 */
	public static Map<String, ?> readMap(URL target, boolean useUnzip)
	{
		InputStream stream = null;
		ChainInputStream chainStream = null;
		try
		{
			stream = target.openStream();
			chainStream = new ChainInputStream(stream);
			if(useUnzip)
			{
				chainStream.put(java.util.zip.GZIPInputStream.class);
			}
			StreamUtil.additionalSetting(chainStream);
			
			Properties prop = new Properties();
			prop.loadFromXML(chainStream.getInputStream());
			Set<String> keys = prop.stringPropertyNames();
			Map<String, Object> map = new Hashtable<String, Object>();
			for(String k : keys)
			{
				map.put(k, prop.getProperty(k));
			}
			
			return map;
		}
		catch(Throwable e)
		{
			Main.logError(e, Manager.applyStringTable("On reading text file") + " : " + target);
			return null;
		}
		finally
		{
			try
			{
				chainStream.close();
			}
			catch(Throwable e)
			{
				
			}
			try
			{
				stream.close();
			}
			catch(Throwable e)
			{
				
			}
		}
	}
	
	/**
	 * <p>URL로부터 텍스트를 읽습니다.</p>
	 * 
	 * @param target : 대상 URL
	 * @param charset : 캐릭터 셋, UTF-8 사용을 권장합니다.
	 * @return 읽은 내용
	 */
	public static String readText(URL target, String charset)
	{
		return readText(target, charset, null, false);
	}
	/**
	 * <p>URL로부터 텍스트를 읽습니다.</p>
	 * 
	 * @param target : 대상 URL
	 * @param charset : 캐릭터 셋, UTF-8 사용을 권장합니다.
	 * @param ignoringPrefix : 주석 표시, 이 내용으로 시작하는 줄은 무시합니다. 중간에 이 내용이 있으면 무시되지 않습니다.
	 * @return 읽은 내용
	 */
	public static String readText(URL target, String charset, String ignoringPrefix)
	{
		return readText(target, charset, ignoringPrefix, false);
	}
	/**
	 * 
	 * <p>URL로부터 텍스트를 읽습니다.</p>
	 * 
	 * @param target : 대상 URL
	 * @param charset : 캐릭터 셋, UTF-8 사용을 권장합니다.
	 * @param ignoringPrefix : 주석 표시, 이 내용으로 시작하는 줄은 무시합니다. 중간에 이 내용이 있으면 무시되지 않습니다.
	 * @param useUnzip : GZIP 스트림을 거칠 지 여부를 지정합니다.
	 * @return 읽은 내용
	 */
	public static String readText(URL target, String charset, String ignoringPrefix, boolean useUnzip)
	{
		InputStream stream = null;
		ChainInputStream chainStream = null;
		InputStreamReader reader = null;
		BufferedReader bufferedReader = null;
		try
		{
			stream = target.openStream();
			chainStream = new ChainInputStream(stream);
			if(useUnzip)
			{
				chainStream.put(java.util.zip.GZIPInputStream.class);
			}
			StreamUtil.additionalSetting(chainStream);
			if(charset == null) reader = new InputStreamReader(chainStream.getInputStream());
			else reader = new InputStreamReader(chainStream.getInputStream(), charset);
			bufferedReader = new BufferedReader(reader);
			
			String readLine;
			StringBuffer reads = new StringBuffer("");
			
			while(true)
			{
				readLine = bufferedReader.readLine();
				if(readLine == null) break;
				if(ignoringPrefix != null)
				{
					if(readLine.trim().startsWith(ignoringPrefix)) continue;
				}
				reads = reads.append(readLine + "\n");
				
				if(! (Main.checkInterrupt(StreamUtil.class, "readText URL")))
				{
					reads = new StringBuffer("");
					break;
				}
			}
			
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
				stream.close();
			}
			catch(Throwable e)
			{
				
			}
			
			return remove65279(reads.toString());
		}
		catch(Throwable e)
		{
			Main.logError(e, Manager.applyStringTable("On reading text file") + " : " + target);
			return null;
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
				stream.close();
			}
			catch(Throwable e)
			{
				
			}
		}
	}
	
	/**
	 * <p>파일로부터 객체를 읽어 들입니다.</p>
	 * 
	 * @param target : 읽을 파일에 대한 파일 객체입니다.
	 * @param useUnzip : GZIP 스트림을 거칠 지 여부를 지정합니다.
	 * @return 읽은 객체
	 */
	public static Object readObject(File target, boolean useUnzip)
	{
		FileInputStream fileStream = null;
		ChainInputStream chainStream = null;
		ObjectInputStream objectStream = null;
		
		Object results = null;
		
		try
		{
			fileStream = new FileInputStream(target);
			chainStream = new ChainInputStream(fileStream);
			if(useUnzip)
			{
				chainStream.put(java.util.zip.GZIPInputStream.class);
			}
			StreamUtil.additionalSetting(chainStream);
			objectStream = new ObjectInputStream(chainStream.getInputStream());
			
			results = objectStream.readObject();
			
			try
			{
				objectStream.close();
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
				fileStream.close();
			}
			catch(Throwable e)
			{
				
			}
			
			return results;
		}
		catch(Throwable e)
		{
			Main.logError(e, Manager.applyStringTable("On reading object file") + " : " + target);
			return null;
		}
		finally
		{
			try
			{
				objectStream.close();
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
				fileStream.close();
			}
			catch(Throwable e)
			{
				
			}
		}
	}
	
	/**
	 * <p>Windows 환경에서 유니코드 타입 사용 시 발생하는 \65278 특수기호를 제거합니다.</p>
	 * 
	 * @param target : 문제가 되는 특수 기호가 삽입된 것으로 의심되는 텍스트
	 * @return 제거된 결과 텍스트
	 */
	public static String remove65279(String target)
	{
		char[] targetChar = target.toCharArray();
		
		List<Character> resultChars = new Vector<Character>();
		for(int i=0; i<targetChar.length; i++)
		{
			if(((int) targetChar[i]) != 65279) resultChars.add(new Character(targetChar[i]));
		}
		
		char[] newChar = new char[resultChars.size()];
		for(int i=0; i<newChar.length; i++)
		{
			newChar[i] = resultChars.get(i);
		}
		return new String(newChar);
	}
	
	/**
	 * <p>파일로 내용을 저장합니다. UTF-8을 사용합니다.</p>
	 * 
	 * @param file : 파일
	 * @param contents : 저장할 내용
	 */
	public static void saveFile(File file, String contents)
	{
		saveFile(file, contents, null);
	}
	
	/**
	 * <p>파일로 내용을 저장합니다. UTF-8을 사용합니다.</p>
	 * 
	 * @param file : 파일
	 * @param contents : 저장할 내용
	 * @param charset : 캐릭터 셋, UTF-8 사용을 권장합니다.
	 */
	public static void saveFile(File file, String contents, String charset)
	{
		saveFile(file, contents, charset, false);
	}
	
	/**
	 * <p>파일로 내용을 저장합니다. UTF-8을 사용합니다.</p>
	 * 
	 * @param file : 파일
	 * @param contents : 저장할 내용
	 * @param charset : 캐릭터 셋, UTF-8 사용을 권장합니다.
	 * @param useZip : true 시 압축 기능을 사용합니다.
	 */
	public static void saveFile(File file, String contents, String charset, boolean useZip)
	{
		FileOutputStream outputStream = null;
		ChainOutputStream chainStream = null;
		OutputStreamWriter writer = null;
		BufferedWriter bufferedWriter = null;
		StringTokenizer lineTokenizer = null;
		
		File dir = null;
		try
		{			
			dir = new File(getDirectoryPathOfFile(file));
			if(! dir.exists()) dir.mkdir();
		}
		catch(Throwable e)
		{
			Main.logError(e, Manager.applyStringTable("On making directory to save file") + " : " + dir, true);
		}
		try
		{
			outputStream = new FileOutputStream(file);
			chainStream = new ChainOutputStream(outputStream);
			if(useZip) chainStream.put(java.util.zip.GZIPOutputStream.class);
			StreamUtil.additionalSetting(chainStream);
			if(charset == null) writer = new OutputStreamWriter(chainStream.getOutputStream());
			else writer = new OutputStreamWriter(chainStream.getOutputStream(), charset);
			bufferedWriter = new BufferedWriter(writer);
			lineTokenizer = new StringTokenizer(contents, "\n");
			
			while(lineTokenizer.hasMoreTokens())
			{
				bufferedWriter.write(lineTokenizer.nextToken());
				bufferedWriter.newLine();
			}
		}
		catch(Throwable e)
		{
			Main.logError(e, Manager.applyStringTable("On saving file") + " : " + file);
		}
		finally
		{
			try
			{
				bufferedWriter.close();
			}
			catch(Throwable e)
			{
				
			}
			try
			{
				writer.close();
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
				outputStream.close();
			}
			catch(Throwable e)
			{
				
			}
		}
	}
	
	/**
	 * <p>파일로 객체를 저장합니다</p>
	 * 
	 * @param file : 파일
	 * @param contents : 저장할 객체
	 * @param useZip : true 시 압축 기능을 사용합니다.
	 */
	public static void saveFile(File file, Serializable contents, boolean useZip)
	{
		FileOutputStream outputStream = null;
		ChainOutputStream chainStream = null;
		ObjectOutputStream objectStream = null;
		
		File dir = null;
		try
		{			
			dir = new File(getDirectoryPathOfFile(file));
			if(! dir.exists()) dir.mkdir();
		}
		catch(Throwable e)
		{
			Main.logError(e, Manager.applyStringTable("On making directory to save file") + " : " + dir, true);
		}
		try
		{
			outputStream = new FileOutputStream(file);
			chainStream = new ChainOutputStream(outputStream);
			if(useZip) chainStream.put(java.util.zip.GZIPOutputStream.class);
			StreamUtil.additionalSetting(chainStream);
			objectStream = new ObjectOutputStream(chainStream.getOutputStream());
			
			objectStream.writeObject(contents);
		}
		catch(Throwable e)
		{
			Main.logError(e, Manager.applyStringTable("On saving file") + " : " + file);
		}
		finally
		{
			try
			{
				objectStream.close();
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
				outputStream.close();
			}
			catch(Throwable e)
			{
				
			}
		}
	}
	
	/**
	 * <p>맵 객체를 저장할 때 사용됩니다.</p>
	 * 
	 * @param file : 저장할 파일
	 * @param map : 저장할 맵 객체
	 * @param useZip : 압축 여부
	 */
	public static void saveMap(File file, Map<String, ?> map, boolean useZip)
	{
		FileOutputStream outputStream = null;
		ChainOutputStream chainStream = null;
		
		File dir = null;
		try
		{			
			dir = new File(getDirectoryPathOfFile(file));
			if(! dir.exists()) dir.mkdir();
		}
		catch(Throwable e)
		{
			Main.logError(e, Manager.applyStringTable("On making directory to save file") + " : " + dir, true);
		}
		try
		{
			outputStream = new FileOutputStream(file);
			chainStream = new ChainOutputStream(outputStream);
			if(useZip) chainStream.put(java.util.zip.GZIPOutputStream.class);
			StreamUtil.additionalSetting(chainStream);
			
			Properties properties = new Properties();
			properties.putAll(map);
			properties.storeToXML(chainStream.getOutputStream(), null);
		}
		catch(Throwable e)
		{
			Main.logError(e, Manager.applyStringTable("On saving file") + " : " + file);
		}
		finally
		{
			try
			{
				chainStream.close();
			}
			catch(Throwable e)
			{
				
			}
			try
			{
				outputStream.close();
			}
			catch(Throwable e)
			{
				
			}
		}
	}
	
	/**
	 * <p>해당 파일이 속한 디렉토리 경로를 반환합니다.</p>
	 * 
	 * @param file : 파일
	 * @return 파일이 있는 디렉토리 경로
	 */
	public static String getDirectoryPathOfFile(File file)
	{
		StringTokenizer fileSepTokenizer = new StringTokenizer(file.getAbsolutePath(), Manager.getOption("file_separator"));
		String newDirStr = "";
		int tokens = fileSepTokenizer.countTokens();
		for(int i=0; i<tokens - 1; i++)
		{
			if(i != 0) newDirStr = newDirStr + Manager.getOption("file_separator");
			newDirStr = newDirStr + fileSepTokenizer.nextToken();
		}
		if(! (newDirStr.endsWith(Manager.getOption("file_separator")))) newDirStr = newDirStr + Manager.getOption("file_separator");
		return newDirStr;
	}
	
	/**
	 * <p>파일 객체를 넣으면, 파일이면 그 파일 하나를 담은 리스트 객체를, 디렉토리이면 그 안에 있는 모든 파일과 디렉토리, 그 안의 파일들을 담은 리스트 객체를 반환합니다.</p>
	 * <p>디렉토리의 경우 그 안에서 다시 이 메소드가 호출된 결과를 합하는 방식으로 목록을 완성하게 됩니다.</p>
	 * 
	 * @param dir : 디렉토리 혹은 파일
	 * @param filter : 파일 필터, 필요가 없다면 null 사용 가능
	 * @param includeDirectory : 디렉토리를 리스트에 포함시킬지 여부를 결정, false 로 하면 목록을 만들고 나서 그 중에서 디렉토리를 걸러냄. true 이면 걸러내지 않음.
	 * @return 내부에 있는 모든 파일 리스트
	 */
	public static List<File> listFileRecursively(File dir, FileFilter filter, boolean includeDirectory)
	{
		List<File> fileList = new Vector<File>();
		
		if(! dir.exists()) return fileList;
		
		if(dir.exists())
		{
			if(dir.isDirectory())
			{
				File[] dirFiles = null;
				if(filter == null) dirFiles = dir.listFiles();
				else dirFiles = dir.listFiles(filter);
				
				for(int i=0; i<dirFiles.length; i++)
				{
					/*
					 재귀적 호출, 디렉토리 걸러내기 여부는 마지막에만 수행하면 되므로 재귀 호출에서는 사용하지 않음
					 */
					fileList.addAll(listFileRecursively(dirFiles[i], filter, true));
				}
			}
			else
			{
				if(! filter.accept(dir)) return fileList;
				else fileList.add(dir);
			}
		}
		
		if(! includeDirectory)
		{
			int i=0;
			while(true)
			{
				if(i >= fileList.size()) break;
				if(fileList.get(i).isDirectory())
				{
					fileList.remove(i);
					i = 0;
				}
				i++;
				
				if(! (Main.checkInterrupt(StreamUtil.class, "listFileRecursively")))
				{
					break;
				}
			}
		}
		
		return fileList;
	}
	
	/**
	 * <p>입력 스트림으로 받은 데이터를 출력 스트림으로 전달합니다.</p>
	 * 
	 * @param inputStream : 입력 스트림
	 * @param outputStream : 출력 스트림
	 * @throws IOException : 스트림 문제 (네트워크 등)
	 */
	public static void pipe(InputStream inputStream, OutputStream outputStream) throws IOException
	{
		byte[] buf = new byte[BUFFER_SIZE];
		int read = 0;
		while(true)
		{
			read = inputStream.read(buf);
			if(read < 0) break;
			if(! Main.checkInterrupt(StreamUtil.class, Manager.applyStringTable("On pipe"))) break;
			outputStream.write(buf, 0, read);
		}
		outputStream.flush();
	}
	
	/**
	 * <p>객체를 파일 객체로 변환을 시도합니다.</p>
	 * 
	 * @param file : 변환할 객체
	 * @return 파일 객체 (URL 형식일 경우 실패할 수도 있으며, 이 경우 null)
	 */
	public static File transfer(Object file)
	{
		if(file instanceof File) return (File) file;
		else if(file instanceof URI) return new File((String) file);
		else if(file instanceof URL)
		{
			try
			{
				return new File(((URL) file).toURI());
			}
			catch (URISyntaxException e)
			{
				return null;
			}
		}
		else return new File(String.valueOf(file));
	}
	
	/**
	 * <p>파일의 내용을 바이트 배열로 모두 읽습니다.</p>
	 * 
	 * @param file : 대상 파일
	 * @return byte 배열
	 * @throws IOException 입출력 관련 오류
	 */
	public static byte[] readBytes(File file) throws IOException
	{
		FileInputStream finput = null;
		ByteArrayOutputStream output = null;
		
		try
		{
			finput = new FileInputStream(file);
			byte[] buffer = new byte[BUFFER_SIZE];
			int readLength = -1;
			output = new ByteArrayOutputStream();
			while(true)
			{
				readLength = finput.read(buffer);
				if(readLength < 0) break;
			    output.write(buffer, 0, readLength);
			}
			return output.toByteArray();
		}
		catch(IOException t)
		{
			throw t;
		}
		finally
		{
			try
			{
				finput.close();
			}
			catch(Throwable t2)
			{
				
			}
			try
			{
				output.close();
			}
			catch(Throwable t2)
			{
				
			}
		}
	}
	
	/**
	 * <p>바이트 배열 내용을 바이너리 형태로 파일로 저장합니다.</p>
	 * 
	 * @param file : 저장할 파일
	 * @param bytes : 저장할 이진 데이터 
	 * @throws IOException 입출력 문제
	 */
	public static void saveBytes(File file, byte[] bytes) throws IOException
	{
		FileOutputStream outputs = null;
		if(bytes == null) throw new NullPointerException("Cannot save null as file.");
		try
		{
			outputs = new FileOutputStream(file);
			outputs.write(bytes);
			outputs.close();
		}
		catch(IOException t)
		{
			throw t;
		}
		finally
		{
			try
			{
				outputs.close();
			}
			catch(Throwable t2)
			{
				
			}
		}
	}
	
	/**
	 * <p>객체를 바이너리로 변환합니다.</p>
	 * 
	 * @param ob : 변환할 객체
	 * @return 바이너리 (바이트 배열)
	 * @throws IOException
	 */
	public static byte[] toBytes(Object ob) throws IOException
	{
		ByteArrayOutputStream byteCol = null;
		ObjectOutputStream output = null;
		try
		{
			byteCol = new ByteArrayOutputStream();
			output = new ObjectOutputStream(byteCol);
			output.writeObject(ob);
			output.close();
			return byteCol.toByteArray();
		}
		catch(IOException t)
		{
			throw t;
		}
		finally
		{
			try
			{
				output.close();
			}
			catch(Throwable t2)
			{
				
			}
			try
			{
				byteCol.close();
			}
			catch(Throwable t2)
			{
				
			}
		}
	}
	
	/**
	 * <p>바이너리를 객체로 변환을 시도합니다.</p>
	 * 
	 * @param bytes : 변환할 바이너리 데이터 (바이트 배열)
	 * @return 변환된 객체
	 * @throws IOException 입출력 관련 오류 (바이너리로부터 객체 인식 실패한 경우)
	 * @throws ClassNotFoundException 인식한 객체에 맞는 클래스를 찾을 수 없는 경우
	 */
	public static Object toObject(byte[] bytes) throws IOException, ClassNotFoundException
	{
		ByteArrayInputStream byteCol = null;
		ObjectInputStream objInput = null;
		try
		{
			byteCol = new ByteArrayInputStream(bytes);
			objInput = new ObjectInputStream(byteCol);
			return objInput.readObject();
		}
		catch(IOException t)
		{
			throw t;
		}
		finally
		{
			try
			{
				objInput.close();
			}
			catch(Throwable t2)
			{
				
			}
			try
			{
				byteCol.close();
			}
			catch(Throwable t2)
			{
				
			}
		}
	}
	
	/**
	 * <p>체인 스트림에, 환경 설정에 따라 추가 스트림을 지정합니다. 기본값은, 아무런 추가 스트림을 넣지 않는 것입니다.</p>
	 * 
	 * @param stream : 체인 스트림
	 */
	public static void additionalSetting(ChainInputStream stream)
	{
		
	}
	
	/**
	 * <p>체인 스트림에, 환경 설정에 따라 추가 스트림을 지정합니다. 기본값은, 아무런 추가 스트림을 넣지 않는 것입니다.</p>
	 * 
	 * @param stream : 체인 스트림
	 */
	public static void additionalSetting(ChainOutputStream stream)
	{
		
	}
	
	/**
	 * <p>체인 스트림에, 환경 설정에 따라 추가 스트림을 지정합니다. 기본값은, 아무런 추가 스트림을 넣지 않는 것입니다.</p>
	 * 
	 * @param stream : 체인 스트림
	 */
	public static void additionalSetting(ChainWriter stream)
	{
		
	}
	
	/**
	 * <p>체인 스트림에, 환경 설정에 따라 추가 스트림을 지정합니다. 기본값은, 아무런 추가 스트림을 넣지 않는 것입니다.</p>
	 * 
	 * @param stream : 체인 스트림
	 */
	public static void additionalSetting(ChainReader stream)
	{
		
	}
}
