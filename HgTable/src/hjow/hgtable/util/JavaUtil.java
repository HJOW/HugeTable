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

import java.util.StringTokenizer;

/**
 * <p>자바 런타임에 관련된 여러 정적 메소드들이 있습니다.</p>
 * 
 * @author HJOW
 *
 */
public class JavaUtil
{
	/**
	 * <p>자바 런타임 버전을 반환합니다.</p>
	 * 
	 * @return 자바 런타임 버전
	 */
	public static String fullVer()
	{
		return Runtime.class.getPackage().getImplementationVersion();
	}
	
	/**
	 * <p>자바 런타임 버전을 반환합니다. 세대 번호만 반환합니다.</p>
	 * 
	 * @return 자바 런타임 버전
	 */
	public static int ver()
	{
		StringTokenizer pointTokenizer = new StringTokenizer(fullVer(), ".");
		pointTokenizer.nextToken();
		
		return Integer.parseInt(pointTokenizer.nextToken());
	}
}
