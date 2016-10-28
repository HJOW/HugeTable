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
package hjow.hgtable.tableset;

import java.io.Serializable;

/**
 * <p>JDBC DAO의 select() 메소드 호출 결과로 반환되는 객체에 관여합니다.</p>
 * 
 * @author HJOW
 *
 */
public class ResultStruct implements Serializable
{
	private static final long serialVersionUID = -3760675579842408556L;
	protected TableSet tableSet = null;
	protected int resultCount = 0;
	public ResultStruct()
	{
		
	}
	public ResultStruct(TableSet tableSet, int resultCount)
	{
		super();
		this.tableSet = tableSet;
		this.resultCount = resultCount;
	}
	public TableSet getTableSet()
	{
		return tableSet;
	}
	public void setTableSet(TableSet tableSet)
	{
		this.tableSet = tableSet;
	}
	public int getResultCount()
	{
		return resultCount;
	}
	public void setResultCount(int resultCount)
	{
		this.resultCount = resultCount;
	}
	@Override
	public String toString()
	{
		return tableSet.toString();
	}
}
