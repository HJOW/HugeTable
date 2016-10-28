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

package hjow.dbtool.mariadb;

import hjow.dbtool.common.DBTool;
import hjow.hgtable.dao.Dao;

/**
 * <p>이 클래스 객체는 MariaDB Database 혹은 MariaDB 에 대한 주요 정보 및 자주 쓰는 쿼리를 메소드로 가집니다.</p>
 * 
 * @author HJOW
 *
 */
public class MTool extends DBTool
{
	private static final long serialVersionUID = 6651732960141029944L;
	/**
	 * <p>생성자입니다.</p>
	 */
	public MTool()
	{
		
	}
	/**
	 * <p>생성자입니다. DB에 액세스할 수 있는 DAO 객체가 필요합니다.</p>
	 * 
	 * @param dao : DAO 객체
	 */
	public MTool(Dao dao)
	{
		super(dao);
	}
	
	/**
	 * <p>MySQL JDBC 드라이버의 클래스 이름을 풀네임으로 반환합니다.</p>
	 * 
	 * @return JDBC 드라이버 클래스명
	 */
	@Override
	public String getJdbcClassPath()
	{
		return "org.mariadb.jdbc.Driver";
	}
	@Override
	public String getTableListQuery(boolean allData)
	{
		if(allData) return "SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE LIKE '%TABLE%'";
		else return "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE LIKE '%TABLE%'";
	}
	@Override
	public String getUserListQuery(boolean isDBA, boolean allData)
	{
		if(allData)
		{
			if(isDBA) return "SELECT * FROM FROM MYSQL.USER";
			else return "SELECT * FROM MYSQL.USER";
		}
		else
		{
			if(isDBA) return "SELECT USER FROM MYSQL.USER";
			else return "SELECT USER FROM MYSQL.USER";
		}
	}
	@Override
	public String getTablespaceListQuery(boolean isDBA, boolean allData)
	{
		if(allData)
		{
			if(isDBA) return "SHOW DATABASES";
			else return "SHOW DATABASES";
		}
		else
		{
			if(isDBA) return "SHOW DATABASES";
			else return "SHOW DATABASES";
		}
	}
	@Override
	public String getViewListQuery(boolean isDBA, boolean allData)
	{
		if(allData) return "SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE LIKE '%VIEW%'";
		else return "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE LIKE '%VIEW%'";
	}
	@Override
	public String getDbLinkListQuery(boolean isDBA, boolean allData)
	{
		return null;
	}
	@Override
	public String getObjectListQuery(boolean isDBA, boolean allData)
	{
		if(allData) return "SELECT * FROM INFORMATION_SCHEMA.TABLES";
		else return "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES";
	}
	@Override
	public String getDataFileListQuery(boolean allData)
	{
		return null;
	}
	@Override
	public String getDBName()
	{
		return "MariaDB";
	}
	@Override
	public String getRownumName()
	{
		return null;
	}
	@Override
	public String getProcedureListQuery(boolean isDBA, boolean allData)
	{
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getFunctionListQuery(boolean isDBA, boolean allData)
	{
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getProcedureScriptQuery(String procedureName)
	{
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getFunctionScriptQuery(String procedureName)
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	
	
	@Override
	public String getTableListKeyColumnName()
	{
		return "TABLE_NAME";
	}
	@Override
	public String getUserListKeyColumnName()
	{
		return "USER";
	}
	@Override
	public String getTablespaceListKeyColumnName()
	{
		return "Database";
	}
	@Override
	public String getViewListKeyColumnName()
	{
		return "TABLE_NAME";
	}
	@Override
	public String getDbLinkListKeyColumnName()
	{
		return null;
	}
	@Override
	public String getObjectListKeyColumnName()
	{
		return "TABLE_NAME";
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
	public DBTool newInstance(Dao dao)
	{
		return new MTool(dao);
	}
	@Override
	public String jdbcUrlPrefix()
	{
		return "jdbc:mariadb";
	}
	@Override
	public String getTableInfoQuery(String tableName) {
		// TODO Auto-generated method stub
		return null;
	}
}
