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

import hjow.hgtable.jscript.JScriptObject;

/**
 * <p>자신이 속한 테이블 셋 정보를 포함한 컬럼 객체입니다.</p>
 * 
 * @author HJOW
 *
 */
public class ColumnWithTableInfo implements JScriptObject
{
	private static final long serialVersionUID = -7267452810353321255L;
	protected TableSet tableSet;
	protected int columnNo;
	
	public ColumnWithTableInfo(TableSet tableSet, int columnNo)
	{
		this.tableSet = tableSet;
		this.columnNo = columnNo;
	}
	
	@Override
	public String toString()
	{
		return tableSet.getColumn(columnNo).getName() + " " + "in" + " " + tableSet.getName();
	}
	
	/**
	 * <p>대상이 되는 컬럼을 반환합니다.</p>
	 * 
	 * @return 컬럼 객체
	 */
	public Column getColumn()
	{
		return tableSet.getColumn(columnNo);
	}
	
	@Override
	public void noMoreUse()
	{
		tableSet = null;
	}
	@Override
	public boolean isAlive()
	{
		return (tableSet != null && tableSet.isAlive());
	}
	@Override
	public String help()
	{
		return null;
	}
	public TableSet getTableSet()
	{
		return tableSet;
	}
	public void setTableSet(TableSet tableSet)
	{
		this.tableSet = tableSet;
	}
	public int getColumnNo()
	{
		return columnNo;
	}
	public void setColumnNo(int columnNo)
	{
		this.columnNo = columnNo;
	}
}
