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

package hjow.hgtable.classload;

import java.io.File;
import java.io.FileFilter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import org.reflections.Reflections;

import hjow.hgtable.Main;
import hjow.hgtable.Manager;
import hjow.hgtable.util.DataUtil;
import hjow.hgtable.util.InvalidInputException;
import hjow.hgtable.util.StreamUtil;

/**
 * <p>이 클래스에는 외부 클래스를 동적으로 불러오는 데 쓰이는 정적 메소드들이 있습니다.</p>
 * 
 * @author HJOW
 *
 */
public class ClassTool
{
	// private static boolean seeErrorSimplify = true;
	public static List<File> fileList = new Vector<File>();
	// public static Map<String, Class<? extends Driver>> unLoadedDriverList = new Hashtable<String, Class<? extends Driver>>();
	public static Map<String, Class<?>> loadedClasses = new Hashtable<String, Class<?>>();
	public static FileFilter jarFilter = new FileFilter()
	{					
		@Override
		public boolean accept(File pathname)
		{
			if(pathname.isDirectory()) return true;
			String fullname = pathname.getAbsolutePath();
			return (fullname.endsWith(".jar") || fullname.endsWith(".JAR") || fullname.endsWith(".Jar"));
		}
	};
	
		
	/**
	 * <p>기본으로 사용되는 클래스들을 불러옵니다.</p>
	 * 
	 */
	@SuppressWarnings("resource")
	public static void loadDefaultClasses(Manager manager)
	{
		try
		{
			if(Manager.getOption("safe_mode") != null && DataUtil.parseBoolean(Manager.getOption("safe_mode")))
			{
				return;
			}
		}
		catch (InvalidInputException e1)
		{
			
		}
		
		List<File> targets = localJarFiles(false);
				
		StringTokenizer defaultClassTokenizer = new StringTokenizer(Manager.getOption("default_class_list"), ";");
		while(defaultClassTokenizer.hasMoreTokens())
		{
			String classPath = defaultClassTokenizer.nextToken();
			try
			{
				for(File f : targets)
				{
					URL targetUrl = f.toURI().toURL();
					URL[] urlArr = new URL[1];
					urlArr[0] = targetUrl;
					
					DynamicLoader classLoader = new DynamicLoader(urlArr, System.class.getClassLoader());
					classLoader.add(f);
					
//					try
//					{
//						loadedClasses.put(classPath, classLoader.loadClass(classPath));
//						break;
//					}
//					catch(Exception e)
//					{
//						manager.logError(e, Manager.applyStringTable("Fail to load class") + " : " + classPath + " " + Manager.applyStringTable("from") + " " + String.valueOf(f));
//					}
//					catch(Throwable e)
//					{
//						manager.logError(e, Manager.applyStringTable("Fail to load class") + " : " + classPath + " " + Manager.applyStringTable("from") + " " + String.valueOf(f));
//					}
				}
			}
			catch(Exception e)
			{	
				manager.logError(e, Manager.applyStringTable("Fail to load class") + " : " + String.valueOf(classPath), true);
			}
			catch(Throwable e)
			{	
				manager.logError(e, Manager.applyStringTable("Fail to load class") + " : " + String.valueOf(classPath));
			}
		}
		
		manager.log(Manager.applyStringTable("Loading default libraries finished."));
	}
			
	/**
	 * <p>동적으로 클래스를 불러옵니다. fileList 가 선언되어 있는 경우 해당 파일로부터 클래스 이름에 해당하는 클래스를 찾아 불러옵니다.</p>
	 * <p>JDBC 드라이버를 불러올 때에도 사용할 수 있습니다.</p>
	 * 
	 * @param classPath : 클래스 이름 (풀네임)
	 * @param files : 파일 리스트 (혹은 URL)
	 * @return 불러온 Class 객체, 이를 통해 해당 클래스의 객체를 생성할 수 있습니다.
	 * @throws Exception : 클래스를 불러오는 중 발생하는 문제들
	 */
	public static Class<?> loadClass(String classPath, String[] files) throws Exception
	{
		return loadClass(classPath, files, false);
	}
	
	/**
	 * <p>동적으로 클래스를 불러옵니다. fileList 가 선언되어 있는 경우 해당 파일로부터 클래스 이름에 해당하는 클래스를 찾아 불러옵니다.</p>
	 * <p>JDBC 드라이버를 불러올 때에도 사용할 수 있습니다.</p>
	 * 
	 * @param classPath : 클래스 이름 (풀네임)
	 * @param files : 파일 리스트 (혹은 URL)
	 * @param simplifyError : 오류 발생 시 콘솔에 자세히 오류 내역을 띄울지를 지정합니다.
	 * @return 불러온 Class 객체, 이를 통해 해당 클래스의 객체를 생성할 수 있습니다.
	 * @throws Exception : 클래스를 불러오는 중 발생하는 문제들
	 */
	@SuppressWarnings("resource")
	public static Class<?> loadClass(String classPath, String[] files, boolean simplifyError) throws Exception
	{		
		if(files != null)
		{	
			DynamicLoader classLoader = null;
			try
			{
				URL[] urls = new URL[files.length];
				for(int i=0; i<files.length; i++)
				{
					urls[i] = new URL(files[i]);
				}
				classLoader = new DynamicLoader(urls);
				return classLoader.loadClass(classPath);
			}
			catch(Throwable e1)
			{
				Main.logError(e1, Manager.applyStringTable("On loading class file"), true);
			}
			finally
			{
				try
				{
					
				}
				catch(Throwable e2)
				{
					
				}
			}
			return null;
		}
		else
		{
			return Class.forName(classPath);
		}
	}
	
	/**
	 * <p>동적으로 클래스를 불러옵니다.</p>
	 * <p>JDBC 드라이버를 불러올 때에도 사용할 수 있습니다.</p>
	 * 
	 * @param classPath : 클래스 이름 (풀네임)
	 * @param simplifyError : 오류 발생 시 콘솔에 자세히 오류 내역을 띄울지를 지정합니다.
	 * @return 불러온 Class 객체, 이를 통해 해당 클래스의 객체를 생성할 수 있습니다.
	 * @throws Exception : 클래스를 불러오는 중 발생하는 문제들
	 */
	public static Class<?> loadClass(String classPath, boolean simplifyError) throws Exception
	{
		return loadClass(classPath, null, simplifyError);
	}
	
	/**
	 * <p>동적으로 클래스를 불러옵니다.</p>
	 * <p>JDBC 드라이버를 불러올 때에도 사용할 수 있습니다.</p>
	 * 
	 * @param classPath : 클래스 이름 (풀네임)
	 * @return 불러온 Class 객체, 이를 통해 해당 클래스의 객체를 생성할 수 있습니다.
	 * @throws Exception : 클래스를 불러오는 중 발생하는 문제들
	 */
	public static Class<?> loadClass(String classPath) throws Exception
	{
		return loadClass(classPath, null, false);
	}
	
	/**
	 * <p>동적으로 JDBC 드라이버를 불러옵니다.</p>
	 * 
	 * @param classPath : 클래스 이름 (풀네임)
	 * @return 불러온 Class 객체, 이를 통해 해당 클래스의 객체를 생성할 수 있습니다.
	 * @throws Exception : 클래스를 불러오는 중 발생하는 문제들
	 */
	public static void loadDriver(String classPath) throws Exception
	{
		loadDriver(classPath, null, false);
	}
	
	/**
	 * <p>동적으로 JDBC 드라이버를 불러옵니다. files 매개변수를 넣은 경우 해당 파일로부터 클래스 이름에 해당하는 JDBC 드라이버를 찾아 불러옵니다.</p>
	 * 
	 * @param classPath : JDBC 드라이버 클래스 이름 (풀네임)
	 * @param files : 파일 리스트 (혹은 URL)
	 * @throws Exception : 클래스를 불러오는 중 발생하는 문제들
	 */
	public static void loadDriver(String classPath, List<String> files) throws Exception
	{
		ClassLoader classLoader = null;
		if(files != null)
		{			
			try
			{
				List<URL> urlList = new Vector<URL>();
				for(int i=0; i<files.size(); i++)
				{
					urlList.add(new URL(files.get(i)));
				}
				
				for(int i=0; i<fileList.size(); i++)
				{
					urlList.add(fileList.get(i).toURI().toURL());
				}
				
				URL[] urls = new URL[urlList.size()];
				for(int i=0; i<urlList.size(); i++)
				{
					urls[i] = urlList.get(i);
				}
				
				classLoader = new DynamicLoader(urls);
				classLoader.loadClass(classPath);
			}
			catch(NoClassDefFoundError e)
			{
				loadDriver(classPath);				
			}
			catch(Throwable e1)
			{
				Main.logError(e1, Manager.applyStringTable("On loading class file"));
			}
		}
		else
		{
			Class.forName(classPath);
		}
	}
	
	/**
	 * <p>동적으로 JDBC 드라이버를 불러옵니다. files 매개변수를 넣은 경우 해당 파일로부터 클래스 이름에 해당하는 JDBC 드라이버를 찾아 불러옵니다.</p>
	 * 
	 * @param classPath : JDBC 드라이버 클래스 이름 (풀네임)
	 * @param files : 파일 리스트
	 * @param includesFileList : 기본 경로에 있던 파일들을 클래스 검색 대상에 포함합니다. 파일 리스트를 null 로 줬다면 이 매개변수는 의미가 없습니다.
	 * @throws Exception : 클래스를 불러오는 중 발생하는 문제들
	 */
	public static void loadDriver(String classPath, File[] files, boolean includesFileList) throws Exception
	{
		ClassLoader classLoader = null;
		if(files != null)
		{			
			try
			{
				List<File> targetFiles = new Vector<File>();
				if(includesFileList) targetFiles.addAll(fileList);
				for(int i=0; i<files.length; i++)
				{
					targetFiles.add(files[i]);
				}
				URL[] urls = new URL[targetFiles.size()];
				for(int i=0; i<urls.length; i++)
				{
					urls[i] = targetFiles.get(i).toURI().toURL();
				}
				classLoader = new DynamicLoader(urls);
				classLoader.loadClass(classPath);
			}
			catch(Throwable e1)
			{
				Main.logError(e1, Manager.applyStringTable("On loading class file"));
			}
		}
		else
		{			
			// Class.forName(classPath);
			Class.forName(classPath);
		}
	}
	
	/**
	 * <p>추가로 불러올 대상 파일 목록에 추가합니다. JAR 파일이어야 합니다.</p>
	 * 
	 * @param file : 추가할 파일 객체
	 */
	public static void addJarFileList(File file)
	{
		if(jarFilter.accept(file))
		{
			fileList.add(file);
		}
	}
	
	/**
	 * <p>모듈이 들어간 경로를 반환합니다.</p>
	 * 
	 * @return 모듈 경로들
	 */
	public static List<File> modulePathes()
	{
		List<File> targetDirs = new Vector<File>();
		targetDirs.add(new File(Manager.getOption("config_path") + "externalModules" + Manager.getOption("file_separator")));
		targetDirs.add(new File(ClassTool.currentDirectory() + "externalModules" + Manager.getOption("file_separator")));
		
		return targetDirs;
	}
	
	/**
	 * <p>jar 파일을 불러올 기본 디렉토리 경로들을 리스트로 반환합니다.</p>
	 * 
	 * @return 파일 객체 리스트
	 */
	public static List<File> localPathes()
	{
		List<File> targetDirs = new Vector<File>();
		targetDirs.add(new File(Manager.getOption("config_path")));
		targetDirs.add(new File(Manager.getOption("config_path") + "lib" + Manager.getOption("file_separator")));
		targetDirs.add(new File(ClassTool.currentDirectory()));
		targetDirs.add(new File(ClassTool.currentDirectory() + "lib" + Manager.getOption("file_separator")));
		
		return targetDirs;
	}
	
	/**
	 * <p>JDBC 드라이버 jar 파일을 불러올 기본 디렉토리 경로들을 리스트로 반환합니다.</p>
	 * 
	 * @return 파일 객체 리스트
	 */
	public static List<File> localJdbcPathes()
	{
		List<File> targetDirs = new Vector<File>();
		targetDirs.add(new File(Manager.getOption("config_path")));
		targetDirs.add(new File(Manager.getOption("config_path") + "jdbc" + Manager.getOption("file_separator")));
		targetDirs.add(new File(ClassTool.currentDirectory()));
		targetDirs.add(new File(ClassTool.currentDirectory() + "jdbc" + Manager.getOption("file_separator")));
		targetDirs.add(new File(Manager.getOption("config_path") + "lib" + Manager.getOption("file_separator")));
		targetDirs.add(new File(ClassTool.currentDirectory()));
		targetDirs.add(new File(ClassTool.currentDirectory() + "lib" + Manager.getOption("file_separator")));
		
		return targetDirs;
	}
	
	/**
	 * <p>기본 디렉토리 경로 상에 있는 jar 파일들을 리스트로 반환합니다.</p>
	 * 
	 * @param includesFileList : true 시 파일 리스트를 검색 목록에 포함합니다.
	 * @return 파일 객체 리스트
	 * 
	 */
	public static List<File> localJarFiles(boolean includesFileList)
	{
		List<File> dirs = localPathes();
		List<File> jarFiles = new Vector<File>();
				
		for(int i=0; i<dirs.size(); i++)
		{
			if(dirs.get(i).exists())
			{
				File[] jarFileArr = dirs.get(i).listFiles(jarFilter);
				
				for(File f : jarFileArr)
				{
					if(f.isDirectory())
					{
						jarFiles.addAll(jarFilesInDirectory(f));
					}
					else jarFiles.add(f);
				}
			}
		}
		
		if(includesFileList) jarFiles.addAll(fileList);
		
		return jarFiles;
	}
	
	/**
	 * <p>기본 디렉토리 경로 상에 있는 JDBC jar 파일들을 리스트로 반환합니다.</p>
	 * 
	 * @param includesFileList : true 시 파일 리스트를 검색 목록에 포함합니다.
	 * @return 파일 객체 리스트
	 * 
	 */
	public static List<File> localJdbcFiles(boolean includesFileList)
	{
		List<File> dirs = localJdbcPathes();
		List<File> jarFiles = new Vector<File>();
				
		for(int i=0; i<dirs.size(); i++)
		{
			if(dirs.get(i).exists())
			{
				File[] jarFileArr = dirs.get(i).listFiles(jarFilter);
				
				for(File f : jarFileArr)
				{
					if(f.isDirectory())
					{
						jarFiles.addAll(jarFilesInDirectory(f));
					}
					else jarFiles.add(f);
				}
			}
		}
		
		if(includesFileList) jarFiles.addAll(fileList);
		
		return jarFiles;
	}
	private static List<File> jarFilesInDirectory(File directory)
	{
		List<File> jarFiles = new Vector<File>();
		if(directory.isDirectory())
		{
			File[] jarFileArr = directory.listFiles(jarFilter);
			
			if(jarFileArr == null) return jarFiles;
			if(jarFileArr.length == 0) return jarFiles;
			for(File f : jarFileArr)
			{
				if(f.isDirectory())
				{
					jarFiles.addAll(jarFilesInDirectory(f));
				}
				else jarFiles.add(f);
			}
			return jarFiles;
		}
		else
		{
			jarFiles.add(directory);
			return jarFiles;
		}
	}
	
	/**
	 * <p>패키지 내의 클래스들을 반환합니다.</p>
	 * 
	 * @param packageName : 패키지 이름
	 * @return 클래스들의 집합
	 */
	public static Set<Class<? extends Object>> getClassesOn(String packageName)
	{
		Reflections reflections = new Reflections(packageName);
		return reflections.getSubTypesOf(Object.class);
	}
		
	/**
	 * <p>현재 이 프로그램이 설치된 디렉토리 경로를 반환합니다.</p>
	 * 
	 * @return 디렉토리 경로
	 */
	public static String currentDirectory()
	{
		String currentDir = currentDirOrArchive();
		
		if(! (currentDir.endsWith(System.getProperty("file.separator")) || currentDir.endsWith("/")))
		{
			File tests = new File(currentDir);
			if(! (tests.exists() && tests.isDirectory()))
			{
				currentDir = StreamUtil.getDirectoryPathOfFile(new File(currentDir));
			}
			else currentDir = tests.getAbsolutePath();
		}
		if(! (currentDir.endsWith(System.getProperty("file.separator")) 
				|| currentDir.endsWith("/"))) currentDir = currentDir + System.getProperty("file.separator");
		return currentDir;
	}
	
	/**
	 * <p>현재 이 프로그램 실행 파일이 있는 디렉토리, 혹은 jar 파일 경로를 반환합니다.</p>
	 * 
	 * @return 디렉토리 경로 혹은 jar 파일 경로
	 */
	public static String currentDirOrArchive()
	{
		try
		{
			return URLDecoder.decode(Manager.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath(), "UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		catch (URISyntaxException e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 해당 클래스 풀네임의 클래스를 찾아 객체를 바로 생성합니다.
	 * 
	 * @param className : 클래스 풀네임
	 * @param params : 매개 변수들 (생략 가능)
	 * @return 생성된 객
	 * @throws Exception 클래스명이 잘못되었거나 해당 클래스가 클래스경로에 없는 경우, 매개변수 입력이 잘못된 경우 
	 */
	public static Object createInstance(String className, Object ... params) throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException
	{
		Class<?> classObj = Class.forName(className);
		if(params == null) return classObj.newInstance();
		
		Class<?>[] paramClasses = new Class<?>[params.length];
		for(int i=0; i<params.length; i++)
		{
			paramClasses[i] = params[i].getClass();
		}
		
		Constructor<?> constructor = classObj.getConstructor(paramClasses);
		return constructor.newInstance(params);
	}
}