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

import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import hjow.hgtable.Manager;
import hjow.hgtable.dao.Dao;
import hjow.hgtable.util.InvalidInputException;

/**
 * // TODO 구현 필요
 * <p>테이블 객체입니다. ColumnTableSet 과는 반대로, 레코드들을 위주로 배열되는 수평형 테이블입니다.</p>
 * <p>ColumnTableSet 과 사용 목적은 같지만, 특정 작업에서는 ColumnTableSet 보다 더 빠르고 수월합니다.</p>
 * <p>미완성</p>
 * 
 * @author HJOW
 *
 */
public class RecordTableSet extends AbstractTableSet
{
	private static final long serialVersionUID = 7914536810615610667L;
	protected String name;
	protected List<Record> records = new Vector<Record>();

	

	@Override
	public List<Column> getColumns()
	{
		List<Column> columns = new Vector<Column>();
		if(getRecordCount() <= 0) return null;
		
		for(int i=0; i<records.get(0).getColumnName().size(); i++)
		{
			Column newColumn = new Column(records.get(0).getColumnName().get(i), records.get(0).getTypes().get(i).intValue());
			columns.add(newColumn);
		}
		
		for(int i=0; i<getRecordCount(); i++)
		{
			for(int j=0; j<columns.size(); j++)
			{
				columns.get(i).getData().add(String.valueOf(getRecord(i).getData(columns.get(i).getName())));
			}
		}
		
		return columns;
	}

	@Override
	public int getRecordCount()
	{
		return records.size();
	}

	@Override
	public void insertIntoDB(Dao dao)
	{
		try
		{
			for(int i=0; i<records.size(); i++)
			{
				records.get(i).insertIntoDB(dao, name, false);
			}
			dao.commit();
		}
		catch(Exception e)
		{
			try
			{
				dao.rollback();
			}
			catch(Exception e1)
			{
				
			}
			dao.getManager().logError(e, Manager.applyStringTable("On insert into DB"));
		}
	}

	public List<Record> getRecords()
	{
		return records;
	}

	public void setRecords(List<Record> records)
	{
		this.records = records;
	}

	@Override
	public String help()
	{
		return null;
	}

	@Override
	public void noMoreUse()
	{
		records.clear();
		records = null;
	}

	@Override
	public boolean isAlive()
	{
		return true;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public void setName(String name)
	{
		this.name = name;
	}

	@Override
	public int getColumnCount()
	{
		if(records.size() <= 0) return -1;
		return records.get(0).getColumnName().size();
	}
	
	@Override
	public String getColumnName(int index)
	{
		if(records.size() <= 0) return null;
		return records.get(0).getColumnName().get(0);
	}
	
	@Override
	public String getColumnTypeName(int index)
	{
		if(records.size() <= 0) return null;
		return records.get(0).types().get(0);
	}
	
	@Override
	public int getColumnType(int index)
	{
		if(records.size() <= 0) return -1;
		return records.get(0).getTypes().get(0);
	}

	@Override
	public void setColumns(List<Column> columns)
	{
		records.clear();
		if(columns.size() <= 0) return;
		
		int recordCounts = columns.get(0).getData().size();
		for(int i=0; i<recordCounts; i++)
		{
			Record newRecord = new Record();
			
			for(int j=0; j<columns.size(); j++)
			{
				newRecord.getColumnName().add(columns.get(j).getName());
				newRecord.getTypes().add(new Integer(columns.get(j).getType()));
				newRecord.getDatas().add(columns.get(j).getData().get(i));
			}
			
			records.add(newRecord);
		}
	}

	@Override
	public void setData(int col, int row, String data)
	{
		records.get(row).getDatas().set(col, data);
	}

	@Override
	public void insertIntoDB(Dao dao, Map<String, String> params, Map<String, String> columnSurroundFunctions,
			Map<String, Integer> columnSurroundFunctionParamIndex,
			Map<String, List<String>> columnSurroundFunctionParams)
	{
		// TODO : 미완성, 대규모 작업 필요
		
	}

	@Override
	public TableSet subTable(int startColumnIndex, int endColumnIndex, int startRecordIndex, int endRecordIndex)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Column getColumn(int index)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Column getColumn(String name)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addColumn(Column newColumn)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addData(ResultSet data) throws Exception
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addData(TableSet tableSet) throws Exception
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void addData(String[] dataArray)
	{
		Record newRecord = new Record();
		if(getRecordCount() <= 0)
		{
			for(int i=0; i<dataArray.length; i++)
			{
				newRecord.getTypes().add(new Integer(Column.TYPE_STRING));
				newRecord.getColumnName().add("COL_" + String.format("%03d", i));
			}
		}
		else
		{
			for(int i=0; i<records.get(0).getColumnName().size(); i++)
			{
				newRecord.getColumnName().add(new String(records.get(0).getColumnName().get(i)));
			}
			for(int i=0; i<records.get(0).getTypes().size(); i++)
			{
				newRecord.getTypes().add(new Integer(records.get(0).getTypes().get(i)));
			}
		}
		for(String s : dataArray)
		{
			newRecord.getDatas().add(s);
		}
		records.add(newRecord);
	}

	@Override
	public Record getRecord(int index)
	{
		return records.get(index);
	}

	@Override
	public List<Record> toRecordList()
	{
		return records;
	}

	@Override
	public void addData(Record record) throws InvalidInputException
	{
		if(records.size() <= 0)
		{
			records.add(record);
		}
		else
		{
			Record compares = records.get(0);
			
			if(record.getColumnName().size() != compares.getColumnName().size()) throw new InvalidInputException(Manager.applyStringTable("Different column name size") + " : " 
					+ String.valueOf(record.getColumnName().size()) + " " + Manager.applyStringTable("and") + " " + String.valueOf(compares.getColumnName().size()));
			
			if(record.getTypes().size() != compares.getTypes().size()) throw new InvalidInputException(Manager.applyStringTable("Different types count") + " : " 
					+ String.valueOf(record.getTypes().size()) + " " + Manager.applyStringTable("and") + " " + String.valueOf(compares.getTypes().size()));
			
			// TODO : 해당 컬럼이 존재하는지 확인해 모자라면 예외 발생해야 함.
			// TODO : 컬럼 이름 순서가 다른지, 순서 다르면 데이터와 타입 순서도 그에 맞춰 바꾸어 삽입해야 함
			
			records.add(record);
		}
	}

	@Override
	public String toHGF()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeEmptyColumn(boolean exceptWhenNameExists)
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void removeEmptyRow()
	{
		
	}

	@Override
	public void removeColumn(String columnName)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getCreateTableSQL(boolean useVarchar2, Map<String, String> options)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addData(List<String> dataArray)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clear()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public Record set(int index, Record element)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void add(int index, Record element)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public Record remove(int index)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int lastIndexOf(Object o)
	{
		// TODO Auto-generated method stub
		return 0;
	}
}
