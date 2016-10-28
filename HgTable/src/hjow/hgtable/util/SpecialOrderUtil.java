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

import hjow.hgtable.Manager;
import hjow.hgtable.jscript.JScriptRunner;
import hjow.hgtable.util.StreamUtil;

import java.io.File;
import java.net.URL;

/**
 * <p>스크립트에서 special:// 태그 내용을 실행하는 정적 메소드가 있습니다. exit, error_simple, error_detail 는 이 곳에서 실행되지 않습니다.</p>
 * 
 * @author HJOW
 *
 */
public class SpecialOrderUtil
{
	/**
	 * <p>special:// 태그를 실행합니다. exit, error_simple, error_detail 는 이 곳에서 실행되지 않습니다.</p>
	 * <p>외부 액세스(파일, URL 등)의 스크립트로는 special:// 태그가 동작하지 않습니다.</p>
	 * 
	 * @param orders : 태그 내용
	 * @param runner : 스크립트 실행 엔진 객체
	 * @return 실행 결과
	 * @throws Exception : 스크립트 오류, 파일 읽기 문제, 네트워크 문제 등
	 */
	public static Object act(String orders, JScriptRunner runner) throws Throwable
	{
		if(orders.startsWith("file://") || orders.startsWith("FILE://") || orders.startsWith("File://"))
		{
			String filePath = orders.substring(new String("file://").length());
			String scripts = StreamUtil.readText(new File(filePath), Manager.getOption("file_charset"));
			return runner.execute(scripts);
		}
		else if(orders.startsWith("url://") || orders.startsWith("URL://") || orders.startsWith("Url://"))
		{
			String urls = orders.substring(new String("url://").length());
			String scripts = StreamUtil.readText(new URL(urls), Manager.getOption("file_charset"));
			return runner.execute(scripts);
		}
		else if(orders.startsWith("f://") || orders.startsWith("F://"))
		{
			String filePath = orders.substring(new String("f://").length());
			String scripts = StreamUtil.readText(new File(filePath), Manager.getOption("file_charset"));
			return runner.execute(scripts);
		}
		else if(orders.startsWith("u://") || orders.startsWith("U://"))
		{
			String urls = orders.substring(new String("u://").length());
			String scripts = StreamUtil.readText(new URL(urls), Manager.getOption("file_charset"));
			return runner.execute(scripts);
		}
		else if(orders.startsWith("command://") || orders.startsWith("COMMAND://") || orders.startsWith("Command://"))
		{
			return Runtime.getRuntime().exec(orders.substring(new String("command://").length()));
		}
		else if(orders.startsWith("c://") || orders.startsWith("C://"))
		{
			return Runtime.getRuntime().exec(orders.substring(new String("c://").length()));
		}
		else if(orders.equalsIgnoreCase("error_simple"))
		{
			runner.setDefaultErrorSimplicityOption(true);
		}
		else if(orders.equalsIgnoreCase("error_detail"))
		{
			runner.setDefaultErrorSimplicityOption(false);
		}
		
		return null;
	}
}
