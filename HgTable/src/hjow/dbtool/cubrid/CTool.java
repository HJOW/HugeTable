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

package hjow.dbtool.cubrid;

import hjow.dbtool.common.DBTool;
import hjow.hgtable.dao.Dao;

/**
 * <p>이 클래스 객체는 Cubrid Database 에 대한 주요 정보 및 자주 쓰는 쿼리를 메소드로 가집니다.</p>
 * 
 * @author HJOW
 *
 */
public class CTool extends DBTool
{
	private static final long serialVersionUID = -7037136791901440512L;
	/**
	 * <p>생성자입니다.</p>
	 */
	public CTool()
	{
		
	}
	/**
	 * <p>생성자입니다. DB에 액세스할 수 있는 DAO 객체가 필요합니다.</p>
	 * 
	 * @param dao : DAO 객체
	 */
	public CTool(Dao dao)
	{
		super(dao);
	}
	
	@Override
	public DBTool newInstance(Dao dao)
	{
		return new CTool(dao);
	}
	
	/**
	 * <p>오라클 JDBC 드라이버의 클래스 이름을 풀네임으로 반환합니다.</p>
	 * 
	 * @return JDBC 드라이버 클래스명
	 */
	@Override
	public String getJdbcClassPath()
	{
		return "cubrid.jdbc.driver.CUBRIDDriver";
	}
	
	@Override
	public String getTableListQuery(boolean allData)
	{
		if(allData) return "SELECT * FROM DB_CLASS WHERE IS_SYSTEM_CLASS = 'NO'";
		else return "SELECT CLASS_NAME FROM DB_CLASS WHERE IS_SYSTEM_CLASS = 'NO'";
	}
	@Override
	public String getUserListQuery(boolean isDBA, boolean allData)
	{
		if(allData)
		{
			if(isDBA) return "SELECT * FROM DB_USER";
			else return "SELECT * FROM DB_USER";
		}
		else
		{
			if(isDBA) return "SELECT NAME FROM DB_USER";
			else return "SELECT NAME FROM DB_USER";
		}		
	}
	@Override
	public String getTablespaceListQuery(boolean isDBA, boolean allData)
	{
		if(allData)
		{
			if(isDBA) return null;
			else return null;
		}
		else
		{
			if(isDBA) return null;
			else return null;
		}
	}
	@Override
	public String getViewListQuery(boolean isDBA, boolean allData)
	{
		if(allData)
		{
			if(isDBA) return null;
			else return null;
		}
		else
		{
			if(isDBA) return null;
			else return null;
		}	
	}
	@Override
	public String getDbLinkListQuery(boolean isDBA, boolean allData)
	{
		if(allData)
		{
			if(isDBA) return null;
			else return null;
		}
		else
		{
			if(isDBA) return null;
			else return null;
		}	
	}
	@Override
	public String getObjectListQuery(boolean isDBA, boolean allData)
	{
		if(allData)
		{
			if(isDBA) return "SELECT * FROM DB_CLASS";
			else return "SELECT * FROM DB_CLASS";
		}
		else
		{
			if(isDBA) return "SELECT CLASS_NAME FROM DB_CLASS";
			else return "SELECT CLASS_NAME FROM DB_CLASS";
		}
	}
	@Override
	public String getDataFileListQuery(boolean allData)
	{
		if(allData)
		{
			return null;
		}
		else
		{
			return null;
		}
	}
	@Override
	public String getDBName()
	{
		return "Cubrid";
	}
	@Override
	public String getRownumName()
	{
		return "ROWNUM";
	}
	@Override
	public String getProcedureListQuery(boolean isDBA, boolean allData)
	{
		return null;
	}
	@Override
	public String getFunctionListQuery(boolean isDBA, boolean allData)
	{
		return null;
	}
	@Override
	public String getProcedureScriptQuery(String procedureName)
	{
		return null;
	}
	@Override
	public String getFunctionScriptQuery(String procedureName)
	{
		return null;
	}
	@Override
	public String getTableListKeyColumnName()
	{
		return "CLASS_NAME";
	}
	@Override
	public String getUserListKeyColumnName()
	{
		return "NAME";
	}
	@Override
	public String getTablespaceListKeyColumnName()
	{
		return null;
	}
	@Override
	public String getViewListKeyColumnName()
	{
		return null;
	}
	@Override
	public String getDbLinkListKeyColumnName()
	{
		return null;
	}
	@Override
	public String getObjectListKeyColumnName()
	{
		return "CLASS_NAME";
	}
	@Override
	public String getDataFileListKeyColumnName()
	{
		return null;
	}
	@Override
	public String getProcedureListKeyColumnName()
	{
		return null;
	}
	@Override
	public String getFunctionListKeyColumnName()
	{
		return null;
	}
	@Override
	public String jdbcUrlPrefix()
	{
		return "jdbc:cubrid";
	}
	@Override
	public String getTableInfoQuery(String tableName) {
		// TODO Auto-generated method stub
		return null;
	}
}
