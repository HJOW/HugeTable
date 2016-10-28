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
package hjow.dbtool.postgresql;

import hjow.dbtool.common.DBTool;
import hjow.hgtable.dao.Dao;

/**
 * <p>이 클래스 객체는 PostgresSQL Database 에 대한 주요 정보 및 자주 쓰는 쿼리를 메소드로 가집니다.</p>
 * 
 * @author HJOW
 *
 */
public class PTool extends DBTool
{
	private static final long serialVersionUID = 2969159091392841074L;

	/**
	 * <p>생성자입니다.</p>
	 */
	public PTool()
	{
		
	}
	/**
	 * <p>생성자입니다. DB에 액세스할 수 있는 DAO 객체가 필요합니다.</p>
	 * 
	 * @param dao : DAO 객체
	 */
	public PTool(Dao dao)
	{
		super(dao);
	}
	
	@Override
	public String getJdbcClassPath()
	{
		return "org.postgresql.Driver";
	}

	@Override
	public String getTableListQuery(boolean allData)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTableListKeyColumnName()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDBName()
	{
		return "PostgresSQL";
	}

	@Override
	public String getRownumName()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUserListQuery(boolean isDBA, boolean allData)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUserListKeyColumnName()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTablespaceListQuery(boolean isDBA, boolean allData)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTablespaceListKeyColumnName()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getViewListQuery(boolean isDBA, boolean allData)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getViewListKeyColumnName()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDbLinkListQuery(boolean isDBA, boolean allData)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDbLinkListKeyColumnName()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getObjectListQuery(boolean isDBA, boolean allData)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getObjectListKeyColumnName()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDataFileListQuery(boolean allData)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDataFileListKeyColumnName()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getProcedureListQuery(boolean isDBA, boolean allData)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getProcedureListKeyColumnName()
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
	public String getFunctionListKeyColumnName()
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
	public String getFunctionScriptQuery(String functionName)
	{
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public DBTool newInstance(Dao dao)
	{
		return new PTool(dao);
	}
	@Override
	public String jdbcUrlPrefix()
	{
		return "jdbc:postgresql";
	}
	@Override
	public String getTableInfoQuery(String tableName) {
		// TODO Auto-generated method stub
		return null;
	}

}
