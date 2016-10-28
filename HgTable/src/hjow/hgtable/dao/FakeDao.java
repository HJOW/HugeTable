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
package hjow.hgtable.dao;

import java.sql.ResultSet;
import java.util.List;
import java.util.Vector;

import hjow.hgtable.Manager;
import hjow.hgtable.tableset.Column;
import hjow.hgtable.tableset.DefaultTableSet;
import hjow.hgtable.tableset.TableSet;
import hjow.hgtable.util.DataUtil;

/**
 * <p>연습에 활용할 수 있는 가짜 DAO 입니다. 쿼리 내용은 실제로는 실행되지 않고 로그로 출력됩니다.</p>
 * 
 * @author HJOW
 *
 */
public class FakeDao extends JdbcDao
{
	private static final long serialVersionUID = -7951857826286708402L;
	protected boolean connected = false;

	public FakeDao(Manager manager)
	{
		super(manager);
	}

	@Override
	public void connect() throws Exception
	{
		if(DataUtil.isEmpty(getId())) throw new NullPointerException(Manager.applyStringTable("ID cannot be null or empty space") + ".");
		if(DataUtil.isEmpty(getPw())) throw new NullPointerException(Manager.applyStringTable("Password cannot be null or empty space") + ".");
		if(DataUtil.isEmpty(getUrl())) throw new NullPointerException(Manager.applyStringTable("JDBC URL cannot be null or empty space") + ".");
		connected = true;
	}
	
	@Override
	public void connectDirectly() throws Exception
	{
		if(DataUtil.isEmpty(getId())) throw new NullPointerException(Manager.applyStringTable("ID cannot be null or empty space") + ".");
		if(DataUtil.isEmpty(getPw())) throw new NullPointerException(Manager.applyStringTable("Password cannot be null or empty space") + ".");
		if(DataUtil.isEmpty(getUrl())) throw new NullPointerException(Manager.applyStringTable("JDBC URL cannot be null or empty space") + ".");
		connected = true;
	}
	
	@Override
	public void connectParallely(Runnable afterWorks)
	{
		try
		{
			connectDirectly();
			afterWorks.run();
		}
		catch (Exception e)
		{
			getManager().logError(e, Manager.applyStringTable("On connectDirectly"));
		}
	}
	
	@Override
	public void disconnect()
	{
		close();
	}
	
	@Override
	protected TableSet query(String sql, boolean noOut) throws Exception
	{
		getManager().log(Manager.applyStringTable("Executing following SQL") + "...\n" + sql + "\n..." + Manager.applyStringTable("end"));
		TableSet tableSet = new DefaultTableSet();
		
		if(sql.trim().startsWith("select ") || sql.trim().startsWith("SELECT "))
		{
			for(int i=0; i<3; i++)
			{
				Column newColumn = new Column();
				newColumn.setName("COL_" + i);
				newColumn.setType(Column.TYPE_STRING);
				List<String> newData = new Vector<String>();
				for(int j=0; j<3; j++)
				{
					newData.add("DATA_" + i + "." + j);
				}
				newColumn.setData(newData);
				tableSet.addColumn(newColumn);
			}
		}
		else return null;
		
		return tableSet;
	}
	
	@Override
	public TableSet query(String sql, List<String> parameters, List<Integer> types) throws Exception
	{
		getManager().log(Manager.applyStringTable("Executing following SQL") + "...\n" + sql + "\n..." + Manager.applyStringTable("end"));
		getManager().log(Manager.applyStringTable("Parameters") + "...\n" + parameters + "\n..." + Manager.applyStringTable("end"));
		getManager().log(Manager.applyStringTable("Types") + "...\n" + types + "\n..." + Manager.applyStringTable("end"));
		TableSet tableSet = new DefaultTableSet();
		
		if(sql.trim().startsWith("select") || sql.trim().startsWith("SELECT"))
		{
			for(int i=0; i<3; i++)
			{
				Column newColumn = new Column();
				newColumn.setName("COL_" + i);
				newColumn.setType(Column.TYPE_STRING);
				List<String> newData = new Vector<String>();
				for(int j=0; j<3; j++)
				{
					newData.add("DATA_" + i + "." + j);
				}
				newColumn.setData(newData);
				tableSet.addColumn(newColumn);
			}
		}
		else return null;
		
		return tableSet;
	}
	
	@Override
	public ResultSet rawQuery(String sql) throws Exception
	{
		getManager().log(Manager.applyStringTable("Executing following SQL") + "...\n" + sql + "\n..." + Manager.applyStringTable("end"));
		return null;
	}
	
	@Override
	public void commit() throws Exception
	{
		getManager().log(Manager.applyStringTable("Commit"));
	}
	
	@Override
	public void rollback() throws Exception
	{
		getManager().log(Manager.applyStringTable("Rollback"));
	}
	
	@Override
	public void close()
	{
		connected = false;
		super.close();
	}
	
	@Override
	public boolean isAlive()
	{
		return connected;
	}
}
