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

package hjow.dbtool.common;

import hjow.hgtable.dao.Dao;

/**
 * <p>사용자 정의 DB툴 클래스입니다.</p>
 * 
 * @author HJOW
 *
 */
public class CustomTool extends DBTool
{
	private static final long serialVersionUID = -6968271538413086897L;
	protected String dbName;
	protected String jdbcClassPath;
	protected String tableListQuery_default;
	protected String tableListQuery_allData;
	protected String rownumKey;
	protected String userListQuery_default;
	protected String userListQuery_isDBA;
	protected String userListQuery_allData;
	protected String userListQuery_isDBA_allData;
	protected String tablespaceListQuery_default;
	protected String tablespaceListQuery_isDBA;
	protected String tablespaceListQuery_allData;
	protected String tablespaceListQuery_isDBA_allData;
	protected String viewListQuery_default;
	protected String viewListQuery_isDBA;
	protected String viewListQuery_allData;
	protected String viewListQuery_isDBA_allData;
	protected String jdbcUrlPrefix;
	protected String dbLinkListQuery_default;
	protected String dbLinkListQuery_isDBA;
	protected String dbLinkListQuery_allData;
	protected String dbLinkListQuery_isDBA_allData;
	protected String objectListQuery_default;
	protected String objectListQuery_isDBA;
	protected String objectListQuery_allData;
	protected String objectListQuery_isDBA_allData;
	protected String dataFileListQuery_default;
	protected String dataFileListQuery_allData;
	
	public CustomTool()
	{
		
	}
	
	public CustomTool(Dao dao)
	{
		super(dao);
	}
	
	@Override
	public String getJdbcClassPath()
	{
		return jdbcClassPath;
	}

	@Override
	public String getTableListQuery(boolean allData)
	{
		if(allData) return tableListQuery_allData;
		else return tableListQuery_default;
	}

	@Override
	public String getDBName()
	{
		return dbName;
	}

	@Override
	public String getRownumName()
	{
		return rownumKey;
	}

	@Override
	public String getUserListQuery(boolean isDBA, boolean allData)
	{
		if(isDBA)
		{
			if(allData)
			{
				return userListQuery_isDBA_allData;
			}
			else
			{
				return userListQuery_isDBA;
			}
		}
		else
		{
			if(allData)
			{
				return userListQuery_allData;
			}
			else
			{
				return userListQuery_default;
			}
		}
	}

	@Override
	public String getTablespaceListQuery(boolean isDBA, boolean allData)
	{
		if(isDBA)
		{
			if(allData)
			{
				return tablespaceListQuery_isDBA_allData;
			}
			else
			{
				return tablespaceListQuery_isDBA;
			}
		}
		else
		{
			if(allData)
			{
				return tablespaceListQuery_allData;
			}
			else
			{
				return tablespaceListQuery_default;
			}
		}
	}

	@Override
	public String getViewListQuery(boolean isDBA, boolean allData)
	{
		if(isDBA)
		{
			if(allData)
			{
				return viewListQuery_isDBA_allData;
			}
			else
			{
				return viewListQuery_isDBA;
			}
		}
		else
		{
			if(allData)
			{
				return viewListQuery_allData;
			}
			else
			{
				return viewListQuery_default;
			}
		}
	}

	@Override
	public String getDbLinkListQuery(boolean isDBA, boolean allData)
	{
		if(isDBA)
		{
			if(allData)
			{
				return dbLinkListQuery_isDBA_allData;
			}
			else
			{
				return dbLinkListQuery_isDBA;
			}
		}
		else
		{
			if(allData)
			{
				return dbLinkListQuery_allData;
			}
			else
			{
				return dbLinkListQuery_default;
			}
		}
	}

	@Override
	public String getObjectListQuery(boolean isDBA, boolean allData)
	{
		if(isDBA)
		{
			if(allData)
			{
				return objectListQuery_isDBA_allData;
			}
			else
			{
				return objectListQuery_isDBA;
			}
		}
		else
		{
			if(allData)
			{
				return objectListQuery_allData;
			}
			else
			{
				return objectListQuery_default;
			}
		}
	}

	@Override
	public String getDataFileListQuery(boolean allData)
	{
		if(allData)
		{
			return dataFileListQuery_allData;
		}
		else
		{
			return dataFileListQuery_default;
		}
	}

	public String getDbName()
	{
		return dbName;
	}

	public void setDbName(String dbName)
	{
		this.dbName = dbName;
	}

	public String getTableListQuery_default()
	{
		return tableListQuery_default;
	}

	public void setTableListQuery_default(String tableListQuery_default)
	{
		this.tableListQuery_default = tableListQuery_default;
	}

	public String getTableListQuery_allData()
	{
		return tableListQuery_allData;
	}

	public void setTableListQuery_allData(String tableListQuery_allData)
	{
		this.tableListQuery_allData = tableListQuery_allData;
	}

	public String getJdbcUrlPrefix()
	{
		return jdbcUrlPrefix;
	}

	public void setJdbcUrlPrefix(String jdbcUrlPrefix)
	{
		this.jdbcUrlPrefix = jdbcUrlPrefix;
	}

	public String getRownumKey()
	{
		return rownumKey;
	}

	public void setRownumKey(String rownumKey)
	{
		this.rownumKey = rownumKey;
	}

	public String getUserListQuery_default()
	{
		return userListQuery_default;
	}

	public void setUserListQuery_default(String userListQuery_default)
	{
		this.userListQuery_default = userListQuery_default;
	}

	public String getUserListQuery_isDBA()
	{
		return userListQuery_isDBA;
	}

	public void setUserListQuery_isDBA(String userListQuery_isDBA)
	{
		this.userListQuery_isDBA = userListQuery_isDBA;
	}

	public String getUserListQuery_allData()
	{
		return userListQuery_allData;
	}

	public void setUserListQuery_allData(String userListQuery_allData)
	{
		this.userListQuery_allData = userListQuery_allData;
	}

	public String getUserListQuery_isDBA_allData()
	{
		return userListQuery_isDBA_allData;
	}

	public void setUserListQuery_isDBA_allData(String userListQuery_isDBA_allData)
	{
		this.userListQuery_isDBA_allData = userListQuery_isDBA_allData;
	}

	public String getTablespaceListQuery_default()
	{
		return tablespaceListQuery_default;
	}

	public void setTablespaceListQuery_default(String tablespaceListQuery_default)
	{
		this.tablespaceListQuery_default = tablespaceListQuery_default;
	}

	public String getTablespaceListQuery_isDBA()
	{
		return tablespaceListQuery_isDBA;
	}

	public void setTablespaceListQuery_isDBA(String tablespaceListQuery_isDBA)
	{
		this.tablespaceListQuery_isDBA = tablespaceListQuery_isDBA;
	}

	public String getTablespaceListQuery_allData()
	{
		return tablespaceListQuery_allData;
	}

	public void setTablespaceListQuery_allData(String tablespaceListQuery_allData)
	{
		this.tablespaceListQuery_allData = tablespaceListQuery_allData;
	}

	public String getTablespaceListQuery_isDBA_allData()
	{
		return tablespaceListQuery_isDBA_allData;
	}

	public void setTablespaceListQuery_isDBA_allData(
			String tablespaceListQuery_isDBA_allData)
	{
		this.tablespaceListQuery_isDBA_allData = tablespaceListQuery_isDBA_allData;
	}

	public String getViewListQuery_default()
	{
		return viewListQuery_default;
	}

	public void setViewListQuery_default(String viewListQuery_default)
	{
		this.viewListQuery_default = viewListQuery_default;
	}

	public String getViewListQuery_isDBA()
	{
		return viewListQuery_isDBA;
	}

	public void setViewListQuery_isDBA(String viewListQuery_isDBA)
	{
		this.viewListQuery_isDBA = viewListQuery_isDBA;
	}

	public String getViewListQuery_allData()
	{
		return viewListQuery_allData;
	}

	public void setViewListQuery_allData(String viewListQuery_allData)
	{
		this.viewListQuery_allData = viewListQuery_allData;
	}

	public String getViewListQuery_isDBA_allData()
	{
		return viewListQuery_isDBA_allData;
	}

	public void setViewListQuery_isDBA_allData(String viewListQuery_isDBA_allData)
	{
		this.viewListQuery_isDBA_allData = viewListQuery_isDBA_allData;
	}

	public String getDbLinkListQuery_default()
	{
		return dbLinkListQuery_default;
	}

	public void setDbLinkListQuery_default(String dbLinkListQuery_default)
	{
		this.dbLinkListQuery_default = dbLinkListQuery_default;
	}

	public String getDbLinkListQuery_isDBA()
	{
		return dbLinkListQuery_isDBA;
	}

	public void setDbLinkListQuery_isDBA(String dbLinkListQuery_isDBA)
	{
		this.dbLinkListQuery_isDBA = dbLinkListQuery_isDBA;
	}

	public String getDbLinkListQuery_allData()
	{
		return dbLinkListQuery_allData;
	}

	public void setDbLinkListQuery_allData(String dbLinkListQuery_allData)
	{
		this.dbLinkListQuery_allData = dbLinkListQuery_allData;
	}

	public String getDbLinkListQuery_isDBA_allData()
	{
		return dbLinkListQuery_isDBA_allData;
	}

	public void setDbLinkListQuery_isDBA_allData(
			String dbLinkListQuery_isDBA_allData)
	{
		this.dbLinkListQuery_isDBA_allData = dbLinkListQuery_isDBA_allData;
	}

	public String getObjectListQuery_default()
	{
		return objectListQuery_default;
	}

	public void setObjectListQuery_default(String objectListQuery_default)
	{
		this.objectListQuery_default = objectListQuery_default;
	}

	public String getObjectListQuery_isDBA()
	{
		return objectListQuery_isDBA;
	}

	public void setObjectListQuery_isDBA(String objectListQuery_isDBA)
	{
		this.objectListQuery_isDBA = objectListQuery_isDBA;
	}

	public String getObjectListQuery_allData()
	{
		return objectListQuery_allData;
	}

	public void setObjectListQuery_allData(String objectListQuery_allData)
	{
		this.objectListQuery_allData = objectListQuery_allData;
	}

	public String getObjectListQuery_isDBA_allData()
	{
		return objectListQuery_isDBA_allData;
	}

	public void setObjectListQuery_isDBA_allData(
			String objectListQuery_isDBA_allData)
	{
		this.objectListQuery_isDBA_allData = objectListQuery_isDBA_allData;
	}

	public String getDataFileListQuery_default()
	{
		return dataFileListQuery_default;
	}

	public void setDataFileListQuery_default(String dataFileListQuery_default)
	{
		this.dataFileListQuery_default = dataFileListQuery_default;
	}

	public String getDataFileListQuery_allData()
	{
		return dataFileListQuery_allData;
	}

	public void setDataFileListQuery_allData(String dataFileListQuery_allData)
	{
		this.dataFileListQuery_allData = dataFileListQuery_allData;
	}

	public void setJdbcClassPath(String jdbcClassPath)
	{
		this.jdbcClassPath = jdbcClassPath;
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
	public String getTablespaceListKeyColumnName()
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
	public String getDbLinkListKeyColumnName()
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
	public String getDataFileListKeyColumnName()
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
	public String getFunctionListKeyColumnName()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String jdbcUrlPrefix()
	{
		return jdbcUrlPrefix;
	}

	@Override
	public String getTableInfoQuery(String tableName) {
		// TODO Auto-generated method stub
		return null;
	}

}
