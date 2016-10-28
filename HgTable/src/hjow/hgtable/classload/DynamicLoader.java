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

import hjow.hgtable.Main;
import hjow.hgtable.Manager;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * <p>이 클래스 로더는 클래스패스를 추가할 수 있습니다.</p>
 * 
 * @author HJOW
 *
 */
public class DynamicLoader extends URLClassLoader
{
	/**
	 * <p>불러올 대상 URL 배열을 반영하는 생성자입니다.</p>
	 * 
	 * @param urls : JAR 파일을 가리키는 URL들의 배열
	 */
	public DynamicLoader(URL[] urls)
	{
		super(urls);
	}
	
	/**
	 * <p>불러올 대상 URL 배열과 상위 클래스 로더를 반영하는 생성자입니다.</p>
	 * 
	 * @param urls : JAR 파일을 가리키는 URL들의 배열
	 * @param parents : 상위 클래스 로더
	 */
	public DynamicLoader(URL[] urls, ClassLoader parents)
	{
		super(urls, parents);
	}
	
	/**
	 * <p>클래스패스에 JAR 파일을 추가합니다.</p>
	 * 
	 * @param jarFile : 파일 객체
	 */
	public void add(File jarFile)
	{
		try
		{
			add(jarFile.toURI().toURL());
		}
		catch (Exception e)
		{
			Main.logError(e, Manager.applyStringTable("On add URL"));
		}
	}
	
	/**
	 * <p>클래스패스에 JAR 파일을 추가합니다.</p>
	 * 
	 * @param url : JAR 파일을 가리키는 URL
	 */
	public void add(URL url)
	{
		addURL(url);
	}
	@Override
	protected void addURL(URL url)
	{
		super.addURL(url);
	}
}
