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
package hjow.hgtable.dao.net;

import java.net.URL;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Map;

import hjow.datasource.common.DataSourceTool;
import hjow.hgtable.Manager;
import hjow.hgtable.dao.Dao;
import hjow.hgtable.tableset.Record;
import hjow.hgtable.tableset.ResultStruct;
import hjow.hgtable.tableset.TableSet;
import hjow.hgtable.ui.AccessInfo;
import hjow.hgtable.util.DataUtil;
import hjow.hgtable.util.JSONUtil;
import hjow.hgtable.util.NetUtil;

/**
 * <p>원격 접속용 DAO 입니다.</p>
 * 
 * @author HJOW
 *
 */
public class RemoteDao extends Dao
{
	private static final long serialVersionUID = -6055934409931468238L;
	protected String remoteUrl;
	
	public RemoteDao(Manager manager, String remoteUrl)
	{
		super(manager);
		this.remoteUrl = remoteUrl;
		if(! this.remoteUrl.endsWith("/")) this.remoteUrl = this.remoteUrl + "/";
	}

	@Override
	public void connect() throws Exception
	{
		Map<String, Object> parameters = new Hashtable<String, Object>();
		parameters.put("order", "connect");
		parameters.put("id", id);
		parameters.put("pw", pw);
		parameters.put("url", url);
		try
		{
			String gets = NetUtil.sendPost(new URL(remoteUrl + "connect"), parameters);
			Record result = JSONUtil.toRecord(gets);
			if(! DataUtil.parseBoolean(result.get("success"))) throw new SQLException(String.valueOf(result.get("error")));
		}
		catch (Exception e)
		{
			throw e;
		}
		catch (Throwable e)
		{
			manager.logError(e, Manager.applyStringTable("On connect remote"));
		}
	}

	@Override
	public void connect(String id, String pw, String url) throws Exception
	{
		Map<String, Object> parameters = new Hashtable<String, Object>();
		parameters.put("order", "connect");
		parameters.put("id", id);
		parameters.put("pw", pw);
		parameters.put("url", url);
		try
		{
			String gets = NetUtil.sendPost(new URL(remoteUrl + "connect"), parameters);
			Record result = JSONUtil.toRecord(gets);
			if(! DataUtil.parseBoolean(result.get("success"))) throw new SQLException(String.valueOf(result.get("error")));
		}
		catch (Exception e)
		{
			throw e;
		}
		catch (Throwable e)
		{
			manager.logError(e, Manager.applyStringTable("On connect remote"));
		}
	}

	@Override
	public void connectDirectly() throws Exception
	{
		connect();
	}

	@Override
	public void connectParallely(Runnable afterWorks)
	{
		try
		{
			connect();
			afterWorks.run();
		}
		catch (Exception e)
		{
			manager.logError(e, Manager.applyStringTable("On connectParallely"));
		}
	}

	@Override
	public void close()
	{
		Map<String, Object> parameters = new Hashtable<String, Object>();
		parameters.put("order", "close");
		try
		{
			String gets = NetUtil.sendPost(new URL(remoteUrl + "close"), parameters);
			Record result = JSONUtil.toRecord(gets);
			if(! DataUtil.parseBoolean(result.get("success"))) throw new SQLException(String.valueOf(result.get("error")));
		}
		catch (Throwable e)
		{
			manager.logError(e, Manager.applyStringTable("On close"));
		}
	}

	@Override
	public boolean isAlive()
	{
		Map<String, Object> parameters = new Hashtable<String, Object>();
		parameters.put("order", "isalive");
		try
		{
			String gets = NetUtil.sendPost(new URL(remoteUrl + "isalive"), parameters);
			Record result = JSONUtil.toRecord(gets);
			if(! DataUtil.parseBoolean(result.get("success"))) throw new SQLException(String.valueOf(result.get("error")));
			return DataUtil.parseBoolean(result.get("result"));
		}
		catch (Throwable e)
		{
			manager.logError(e, Manager.applyStringTable("On isAlive"));
			return false;
		}
	}

	@Override
	protected TableSet query(String scripts, boolean noOut) throws Exception
	{
		Map<String, Object> parameters = new Hashtable<String, Object>();
		parameters.put("order", "query");
		parameters.put("script", scripts);
		try
		{
			String gets = NetUtil.sendPost(new URL(remoteUrl + "query"), parameters);
			Record result = JSONUtil.toRecord(gets);
			if(! DataUtil.parseBoolean(result.get("success"))) throw new SQLException(String.valueOf(result.get("error")));
			return JSONUtil.toTableSet(String.valueOf(result.get("result")));
		}
		catch (Throwable e)
		{
			manager.logError(e, Manager.applyStringTable("On query"));
			return null;
		}
	}
	
	@Override
	protected TableSet query(String sql, boolean noOut, Class<? extends TableSet> tableSetType) throws Exception
	{
		// TODO : 테이블 셋 타입 적용
		Map<String, Object> parameters = new Hashtable<String, Object>();
		parameters.put("order", "query");
		parameters.put("script", sql);
		try
		{
			String gets = NetUtil.sendPost(new URL(remoteUrl + "query"), parameters);
			Record result = JSONUtil.toRecord(gets);
			if(! DataUtil.parseBoolean(result.get("success"))) throw new SQLException(String.valueOf(result.get("error")));
			return JSONUtil.toTableSet(String.valueOf(result.get("result")));
		}
		catch (Throwable e)
		{
			manager.logError(e, Manager.applyStringTable("On query"));
			return null;
		}
	}

	@Override
	public ResultStruct select(String tableName, String additionalWheres, int startsIndex, int endIndex)
			throws Exception
	{
		Map<String, Object> parameters = new Hashtable<String, Object>();
		parameters.put("order", "select");
		parameters.put("table", tableName);
		parameters.put("additionalWheres", additionalWheres);
		parameters.put("startsIndex", new Integer(startsIndex));
		parameters.put("endIndex", new Integer(endIndex));
		try
		{
			String gets = NetUtil.sendPost(new URL(remoteUrl + "select"), parameters);
			Record result = JSONUtil.toRecord(gets);
			if(! DataUtil.parseBoolean(result.get("success"))) throw new SQLException(String.valueOf(result.get("error")));
			TableSet results = JSONUtil.toTableSet(String.valueOf(result.get("result")));
			ResultStruct resultStruct = new ResultStruct(results, results.getRecordCount());
			return resultStruct;
		}
		catch (Throwable e)
		{
			manager.logError(e, Manager.applyStringTable("On select"));
			return null;
		}
	}

	@Override
	public AccessInfo getAccessInfo()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void commit() throws Exception
	{
		Map<String, Object> parameters = new Hashtable<String, Object>();
		parameters.put("order", "commit");
		try
		{
			String gets = NetUtil.sendPost(new URL(remoteUrl + "commit"), parameters);
			Record result = JSONUtil.toRecord(gets);
			if(! DataUtil.parseBoolean(result.get("success"))) throw new SQLException(String.valueOf(result.get("error")));
		}
		catch (Throwable e)
		{
			manager.logError(e, Manager.applyStringTable("On commit"));
		}
	}

	@Override
	public void rollback() throws Exception
	{
		Map<String, Object> parameters = new Hashtable<String, Object>();
		parameters.put("order", "rollback");
		try
		{
			String gets = NetUtil.sendPost(new URL(remoteUrl + "rollback"), parameters);
			Record result = JSONUtil.toRecord(gets);
			if(! DataUtil.parseBoolean(result.get("success"))) throw new SQLException(String.valueOf(result.get("error")));
		}
		catch (Throwable e)
		{
			manager.logError(e, Manager.applyStringTable("On rollback"));
		}
	}

	@Override
	public String getDataSourceType()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DataSourceTool getDataSourceTool()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	
	@Override
	public String help()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TableSet query(String scripts, Class<? extends TableSet> tableSetType) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAccessInfo(AccessInfo accessInfo)
	{
		// TODO Auto-generated method stub
		
	}
}
