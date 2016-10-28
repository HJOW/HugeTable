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

import hjow.dbtool.common.DBTool;
import hjow.dbtool.cubrid.CTool;
import hjow.dbtool.h2.HTool;
import hjow.dbtool.mariadb.MTool;
import hjow.dbtool.oracle.OTool;
import hjow.dbtool.postgresql.PTool;

import java.util.List;
import java.util.Vector;

/**
 * <p>데이터 소스 툴 객체들의 묶음을 다루는 클래스입니다.</p>
 * 
 * @author HJOW
 *
 */
public class DataSourceToolUtil
{
	public static final List<DBTool> toolList = new Vector<DBTool>();
	
	/**
	 * <p>데이터 소스 툴 리스트를 준비합니다.</p>
	 * 
	 */
	public static void prepareToolList()
	{
		toolList.clear();
		toolList.add(new OTool());
		toolList.add(new CTool());
		toolList.add(new MTool());
		toolList.add(new HTool());
		toolList.add(new PTool());
	}
}
