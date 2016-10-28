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
package hjow.hgtable.util.debug;

import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Vector;

import hjow.hgtable.Main;
import hjow.hgtable.classload.ClassTool;
import hjow.hgtable.jscript.JScriptRunner;

/**
 * <p>디버깅만을 위한 객체들을 관리하는 클래스입니다. 정적 필드와 메소드들로 이루어져 있습니다. 배포 시에는 이 클래스의 대부분의 기능을 비활성화합니다.</p>
 * 
 * @author HJOW
 *
 */
public class DebuggingUtilCollection
{
	private static final List<DebuggingUtil> utils = new Vector<DebuggingUtil>();
	private static final List<String> utilClassPaths = new Vector<String>();
	
	/**
	 * <p>디버깅용 객체들의 클래스 이름들을 반환합니다.</p>
	 * 
	 * @return 디버깅용 클래스 풀네임 리스트
	 */
	protected static List<String> debuggingUtilClassNames()
	{
		List<String> results = new Vector<String>();
		
		results.add("externalModules.debug.h1");
		
		return results;
	}
	
	/**
	 * <p>디버깅용 객체들을 준비합니다.</p>
	 */
	public static void init()
	{
		File moduleDir = new File(ClassTool.currentDirectory() + "externalModules" + System.getProperty("file.separator") + "debug" + System.getProperty("file.separator"));
		File[] files = moduleDir.listFiles(new FileFilter()
		{
			@Override
			public boolean accept(File pathname)
			{
				return pathname.getAbsolutePath().endsWith(".class") || pathname.getAbsolutePath().endsWith(".CLASS");
			}
		});
		
		URL[] classLoaderUrl = null;
		ClassLoader classModuleLoader = null;
		Object utilObj;
		
		if(files.length >= 1)
		{
			classLoaderUrl = new URL[files.length];
			for(int i=0; i<files.length; i++)
			{
				try
				{
					classLoaderUrl[i] = files[i].toURI().toURL();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		
		utilClassPaths.addAll(debuggingUtilClassNames());
		
		for(String s : utilClassPaths)
		{
			try
			{	
				if(classLoaderUrl != null && classLoaderUrl.length >= 1)
				{
					classModuleLoader = new URLClassLoader(classLoaderUrl, ClassLoader.getSystemClassLoader());
					utilObj = classModuleLoader.loadClass(s).newInstance();
				}
				else utilObj = Class.forName(s).newInstance();
				
				if(utilObj instanceof DebuggingUtil) utils.add((DebuggingUtil) utilObj);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * <p>스크립트 엔진에 객체들을 삽입합니다.</p>
	 * 
	 * @param runner : 스크립트 엔진 객체
	 */
	public static void putAll(JScriptRunner runner)
	{
		for(int i=0; i<utils.size(); i++)
		{
			if(Main.MODE >= DebuggingUtil.DEBUG)
			{
				utils.get(i).init();
				runner.put(utils.get(i).getName(), utils.get(i));
			}
			else if(Main.MODE == DebuggingUtil.RELEASE)
			{
				if(utils.get(i).getScope() == DebuggingUtil.RELEASE)
				{
					utils.get(i).init();
					runner.put(utils.get(i).getName(), utils.get(i));
				}
			}
		}
	}
	
	/**
	 * <p>디버깅 객체들을 모두 닫습니다.</p>
	 * 
	 */
	public static void closeAll()
	{
		for(int i=0; i<utils.size(); i++)
		{
			try
			{
				utils.get(i).noMoreUse();
			}
			catch(Exception e)
			{
				
			}
		}
	}
}
