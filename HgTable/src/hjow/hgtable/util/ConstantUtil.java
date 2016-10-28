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

import hjow.hgtable.classload.ClassTool;

/**
 * <p>이 클래스에는 상수 설정과 관련된 여러 정적 메소드들이 있습니다.</p>
 * 
 * @author HJOW
 *
 */
public class ConstantUtil
{
	public static int TYPE_BLANK()
	{
		try
		{
			return Integer.parseInt(String.valueOf(ClassTool.loadClass("org.apache.poi.ss.usermodel.Cell").getDeclaredField("CELL_TYPE_BLANK").get("CELL_TYPE_BLANK")));
			// return org.apache.poi.ss.usermodel.Cell.CELL_TYPE_BLANK;
		}
		catch(Throwable e)
		{
			//e.printStackTrace();
			return 3;
		}
	}
	public static int TYPE_BOOLEAN()
	{
		try
		{
			return Integer.parseInt(String.valueOf(ClassTool.loadClass("org.apache.poi.ss.usermodel.Cell").getDeclaredField("CELL_TYPE_BOOLEAN").get("CELL_TYPE_BOOLEAN")));
		}
		catch(Throwable e)
		{
			return 4;
		}
	}
	public static int TYPE_ERROR()
	{
		try
		{
			return Integer.parseInt(String.valueOf(ClassTool.loadClass("org.apache.poi.ss.usermodel.Cell").getDeclaredField("CELL_TYPE_ERROR").get("CELL_TYPE_ERROR")));
		}
		catch(Throwable e)
		{
			return 5;
		}
	}
	public static int TYPE_FORMULA()
	{
		try
		{
			return Integer.parseInt(String.valueOf(ClassTool.loadClass("org.apache.poi.ss.usermodel.Cell").getDeclaredField("CELL_TYPE_FORMULA").get("CELL_TYPE_FORMULA")));
		}
		catch(Throwable e)
		{
			//e.printStackTrace();
			return 2;
		}
	}
	public static int TYPE_NUMERIC()
	{
		try
		{
			return Integer.parseInt(String.valueOf(ClassTool.loadClass("org.apache.poi.ss.usermodel.Cell").getDeclaredField("CELL_TYPE_NUMERIC").get("CELL_TYPE_NUMERIC")));
		}
		catch(Throwable e)
		{
			//e.printStackTrace();
			return 0;
		}
	}
	public static int TYPE_INTEGER()
	{	
		return 1264853;
	}
	public static int TYPE_FLOAT()
	{
		return 1264854;
	}
	public static int TYPE_STRING()
	{
		try
		{
			return Integer.parseInt(String.valueOf(ClassTool.loadClass("org.apache.poi.ss.usermodel.Cell").getDeclaredField("CELL_TYPE_STRING").get("CELL_TYPE_STRING")));
		}
		catch(Throwable e)
		{
			//e.printStackTrace();
			return 1;
		}
	}
	public static int TYPE_OBJECT()
	{
		return 16171911;
	}
	public static int TYPE_BLOB()
	{
		return 161371911;
	}
	public static int TYPE_DATE()
	{
		return -1264855;
	}
}
