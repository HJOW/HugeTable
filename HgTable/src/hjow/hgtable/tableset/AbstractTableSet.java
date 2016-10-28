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
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Vector;
import java.util.Random;

import hjow.dbtool.common.DBTool;
import hjow.hgtable.Main;
import hjow.hgtable.Manager;
import hjow.hgtable.NotConnectedException;
import hjow.hgtable.dao.Dao;
import hjow.hgtable.dao.JdbcDao;
import hjow.hgtable.jscript.JScriptRunner;
import hjow.hgtable.util.AnalyzeUtil;
import hjow.hgtable.util.DataUtil;
import hjow.hgtable.util.InvalidInputException;

/**
 * <p>대부분의 테이블 셋 클래스들이 공통으로 가지는 메소드들을 정의하기 위한 클래스입니다.</p>
 * 
 * @author HJOW
 *
 */
public abstract class AbstractTableSet implements TableSet
{
	private static final long serialVersionUID = 7980514461867997659L;
	protected String name;
	protected transient String alias;
	protected transient Long uniqueId = new Long(new Random().nextLong());
	
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
	public String getData(int col, int row)
	{
		return String.valueOf(getRecord(row).getDataOf(col));
	}
	
	@Override
	public String toInsertSQL()
	{
		StringBuffer results = new StringBuffer("");
		
		for(int j=0; j<getRecordCount(); j++)
		{
			results = results.append("INSERT INTO " + name + " (");
			for(int i=0; i<getColumnCount(); i++)
			{
				results = results.append(getColumn(i).getName());
				if(i < getColumnCount() - 1) results = results.append(",");
			}
			results = results.append(") VALUES (");
			for(int i=0; i<getColumnCount(); i++)
			{
				if(Column.TYPE_NUMERIC == getColumnType(i))
				{
					results = results.append(getData(i, j));
				}
				else if(Column.TYPE_DATE == getColumnType(i))
				{
					results = results.append(getData(i, j));
				}
				else if(Column.TYPE_BLANK == getColumnType(i))
				{
					results = results.append("NULL");
				}
				else
				{
					results = results.append("'" + DataUtil.castQuote(false, getData(i, j)) + "'");
				}		
				if(i < getColumnCount() - 1) results = results.append(",");
			}
			results = results.append(");\n");
		}
		
		return results.toString();
	}

	@Override
	public void createTable(Dao dao, Map<String, String> options, boolean useVarchar2, boolean insertData) throws Exception
	{
		String createTables = getCreateTableSQL(useVarchar2, options);
		if(DataUtil.isNotEmpty(createTables))
		{
			dao.query(getCreateTableSQL(useVarchar2, options));
		}
		else throw new InvalidInputException(Manager.applyStringTable("Cannot create create statement"));
		
		if(insertData)
		{
			insertIntoDB(dao);
		}
	}
	
	@Override
	public boolean equals(Object tableSet)
	{
		if(tableSet instanceof TableSet)
		{
			TableSet gets = (TableSet) tableSet;
			if(gets.getRecordCount() != getRecordCount()) return false;
			if(gets.getColumns().size() != getColumns().size()) return false;
			
			for(int i=0; i<getRecordCount(); i++)
			{
				for(int j=0; j<getRecord(i).getDatas().size(); j++)
				{
					if(! getRecord(i).getDataOf(j).equals(gets.getRecord(i).getDataOf(j)))
					{
						return false;
					}
				}
			}
			return true;
		}
		
		return false;
	}

	@Override
	public void insertIntoDB(Dao dao)
	{
		insertIntoDB(dao, new Hashtable<String, String>());
	}
	@Override
	public void insertIntoDB(Dao dao, Map<String, String> params)
	{
		insertIntoDB(dao, params, new Hashtable<String, String>(), new Hashtable<String, Integer>(), new Hashtable<String, List<String>>());
	}

	@Override
	public TableSet clone()
	{
		return subTable(0, getRecordCount());
	}

	@Override
	public TableSet subTable(int startRecordIndex, int endRecordIndex)
	{
		return subTable(0, getColumnCount(), startRecordIndex, endRecordIndex);
	}

	@Override
	public String applyScript(String script, JScriptRunner runner) throws Throwable
	{
		String results = null;
		
		runner.put("target", this);		
		results = String.valueOf(runner.execute(script));
		
		return results;
	}

	@Override
	public void addData(String name, Dao dao, String additionalWheres)  throws Exception
	{
		this.setName(name);
		
		if(dao.isAlive())
		{
			if(dao instanceof JdbcDao)
			{
				String countSql = "SELECT COUNT(*) FROM " + name;
				int counts = 0;
				/*
				TableSet countResult = dao.query(countSql);				
				try
				{
					counts = Integer.parseInt(String.valueOf(countResult.getRecord(0).getDatas().get(0)));
				}
				catch(Exception e)
				{
					counts = Integer.parseInt(String.valueOf(Double.parseDouble(String.valueOf(countResult.getRecord(0).getDatas().get(0)))));
				}
				*/
				
				
				ResultSet results = ((JdbcDao) dao).rawQuery(countSql);
				while(results.next())
				{
					counts = results.getInt(1);
				}
				
				
				boolean useTransactionDivide = false;
				int finished = 0;
				int gap = 100;
				int preventInfiniteLoop = 0;
				int getSizes = 0;
				
				if(Manager.getOption("step_size") != null)
				{
					gap = Integer.parseInt(Manager.getOption("step_size"));
					useTransactionDivide = true;
				}
				else
				{
					useTransactionDivide = false;
				}
				
				if(useTransactionDivide)
				{
					if(dao.getDataSourceType() == null) useTransactionDivide = false;
				}
				
				DBTool dbTool = ((JdbcDao) dao).getDBTool();
				
				String selectSql = "SELECT * FROM " + name;
				String transDivSql = "";
				
				boolean additionalWhereApplied = false;
				
				String rownumKeyword = null;
				
				if(dbTool != null)
				{
					if(dbTool.getRownumName() != null)
					{
						rownumKeyword = dbTool.getRownumName();
					}
				}
				
				while(finished < counts)
				{
					if(preventInfiniteLoop >= 1000000) break;
					if(finished >= counts) break;
					
					if(rownumKeyword != null) 
					{
						transDivSql = " WHERE " + rownumKeyword + " >= " + finished + " AND " + rownumKeyword + " < " + (finished + gap);
						if(additionalWheres != null)
						{
							transDivSql = transDivSql + " AND " + additionalWheres;
							additionalWhereApplied = true;
						}
					}
					
					if((! additionalWhereApplied) && (additionalWheres != null))
					{
						selectSql = selectSql + " WHERE " + additionalWheres;
					}
					
					results = ((JdbcDao) dao).rawQuery(selectSql + transDivSql);
					
					getSizes = 0;
					while(results.next())
					{
						getSizes++;
					}
					
					finished = finished + getSizes;
					
					addData(results);
					
					preventInfiniteLoop++;
					
					if(! (Main.checkInterrupt(this, "addData")))
					{
						break;
					}
				}
			}			
		}
		else throw new NotConnectedException(Manager.applyStringTable("There is no connection, or connection was closed before."));
	}

	@Override
	public String toJSON(boolean basedRecord)
	{
		StringBuffer results = new StringBuffer("");
		
		if(basedRecord)
		{
			results = results.append("{");
			results = results.append("  name : " + "'" + getName() + "'");
			results = results.append("   ,");
			results = results.append("  type : " + "'" + "TABLE" + "'");
			results = results.append("   ,");
			results = results.append("  columns : ");
			results = results.append("    [");
			for(int i=0; i<getColumns().size(); i++)
			{
				results = results.append("      {");
				results = results.append("        name : " + "'" + getColumn(i).getName() + "'");
				results = results.append("         ,");
				results = results.append("        type : " + "'" + getColumn(i).getType() + "'");
				results = results.append("      }");
				if(i < getColumns().size() - 1) results = results.append("       ,");
			}
			results = results.append("    ]");
			results = results.append("   ,");
			results = results.append("  records : ");
			results = results.append("    [");
			for(int j=0; j<getRecordCount(); j++)
			{
				results = results.append("       [");				
				for(int i=0; i<getColumns().size(); i++)
				{
					results = results.append("         \'" + getColumn(i).getData().get(j) + "\'");
				}
				results = results.append("       ]");
				if(j < getRecordCount() - 1) results = results.append("       ,");				
			}			
			results = results.append("    ]");
			results = results.append("}");
		}
		else			
		{
			results = results.append("{\n");
			results = results.append("  name : " + "'" + getName() + "'\n");
			results = results.append("   ,\n");
			results = results.append("  type : " + "'" + "TABLE" + "'\n");
			results = results.append("   ,\n");
			results = results.append("  records : \n");
			results = results.append("    [\n");
			for(int i=0; i<getRecordCount(); i++)
			{
				results = results.append(getRecord(i).toJSON(true));
				results = results.append("\n");
				if(i < getRecordCount() - 1) results = results.append("      ,\n");
			}
			results = results.append("    ]\n");
			results = results.append("}");
			/*
			results = results.append("{");
			results = results.append("  name : " + "'" + getName() + "'");
			results = results.append("   ,");
			results = results.append("  columns : ");
			results = results.append("    [");
			for(int i=0; i<columns.size(); i++)
			{
				results = results.append("      {");
				results = results.append("        name : " + "'" + columns.get(i).getName() + "'");
				results = results.append("         ,");
				results = results.append("        type : " + "'" + columns.get(i).getType() + "'");
				results = results.append("         ,");
				results = results.append("        data : ");
				results = results.append("          [");
				for(int j=0; j<getRecordCount(); j++)
				{					
					results = results.append("          \'" + columns.get(i).getData().get(j) + "\'");
					if(j < getRecordCount() - 1) results = results.append("           ,");
				}
				results = results.append("          ]");
				results = results.append("      }");
				if(i < columns.size() - 1) results = results.append("       ,");
			}
			results = results.append("    ]");			
			results = results.append("}");
			*/
		}
		
		return results.toString();
	}

	@Override
	public TableSet difference(TableSet others) throws InvalidInputException
	{
		TableSet newTableSet = new DefaultTableSet();
		
		if(this.getColumnCount() != others.getColumns().size())
		{
			throw new InvalidInputException(Manager.applyStringTable("Different column count") 
					+ " : " + String.valueOf(this.getColumnCount()) + ", " + String.valueOf(others.getColumns().size()));
		}
		
		if(getRecordCount() != others.getRecordCount())
		{
			throw new InvalidInputException(Manager.applyStringTable("Different record count") 
					+ " : " + String.valueOf(getRecordCount()) + ", " + String.valueOf(others.getRecordCount()));
		}
		
		List<Column> newColumns = new Vector<Column>();
		for(int i=0; i<getColumnCount(); i++)
		{
			Column newColumn = new Column();
			newColumn.setType(Column.TYPE_STRING);
			newColumn.setName(getColumn(i).getName());
			newColumns.add(newColumn);
		}
		newTableSet.setColumns(newColumns);
		
		boolean allSame = true;
		for(int i=0; i<getRecordCount(); i++)
		{
			Record thisRecord = getRecord(i);
			Record targetRecord = others.getRecord(i);
			Record newRecord = new Record();
			
			for(String col : thisRecord.getColumnName())
			{
				String thisData = String.valueOf(thisRecord.getData(col)).trim();
				String targetData = String.valueOf(targetRecord.getData(col));
				
				newRecord.getColumnName().add(col);
				newRecord.getTypes().add(new Integer(Column.TYPE_STRING));
				
				if(thisData.equals(targetData))
				{
					newRecord.setData(col, "");
				}
				else
				{
					newRecord.setData(col, thisData + "|" + targetData);
					allSame = false;
				}
			}
			newTableSet.addData(newRecord);
		}
		
		if(allSame) return null;
		
		return newTableSet;
	}
	
	@Override
	public String toHGF()
	{
		StringBuffer results = new StringBuffer("  # " + Manager.applyStringTable("HGF of") + " " + getName() + ";\n");
		results = results.append("@\"" + DataUtil.castTotal(true, getName()) + "\";\n");
		
		results = results.append("%");
		for(int i=0; i<getColumnCount(); i++)
		{
			results = results.append("\"" + DataUtil.castTotal(true, getColumnName(i)) + "\":" + String.valueOf(getColumnType(i)) + ";");
		}
		results = results.append("\n");
		
		for(int i=0; i<getRecordCount(); i++)
		{
			results = results.append(getRecord(i).toHGF());
			results = results.append("\n");
		}
		
		return results.toString();
	}
	
	@Override
	public void addData(Record record) throws InvalidInputException
	{		
		String[] inputColumn = new String[getColumnCount()];
		for(int i=0; i<inputColumn.length; i++)
		{
			inputColumn[i] = "NULL";
		}
		
		for(int j=0; j<getColumnCount(); j++)
		{
			for(int i=0; i<record.getColumnName().size(); i++)
			{
				if(Manager.getOption("case_columnname") == null || (! DataUtil.parseBoolean(Manager.getOption("case_columnname"))))
				{
					if(getColumnName(j).equalsIgnoreCase(record.getColumnName().get(i)))
					{
						if(! (getColumnTypeName(j).equalsIgnoreCase(record.typeOf(i))))
						{
							String oneType = getColumnTypeName(j).toUpperCase();
							String twoType = record.typeOf(i).toUpperCase();
							if(twoType.equals("BLANK"))
							{
								inputColumn[j] = "NULL";
							}
							else if(oneType.equals("INTEGER") && twoType.equals("NUMERIC"))
							{
								inputColumn[j] = String.valueOf((int) Double.parseDouble(String.valueOf(record.getDataOf(i))));
							}
							else if(oneType.equals("FLOAT") && twoType.equals("NUMERIC"))
							{
								inputColumn[j] = String.valueOf(record.getDataOf(i));
							}
							else if(oneType.equals("NUMERIC") && twoType.equals("INTEGER"))
							{
								inputColumn[j] = String.valueOf(record.getDataOf(i));
							}
							else if(oneType.equals("NUMERIC") && twoType.equals("FLOAT"))
							{
								inputColumn[j] = String.valueOf(record.getDataOf(i));
							}
							else throw new InvalidInputException(Manager.applyStringTable("On addData from record object") + " : " + record);
						}
						else inputColumn[j] = String.valueOf(record.getDataOf(i));
					}
				}
				else
				{
					if(getColumnName(j).equals(record.getColumnName().get(i)))
					{
						if(! (getColumnTypeName(j).equalsIgnoreCase(record.typeOf(i))))
						{
							String oneType = getColumnTypeName(j).toUpperCase();
							String twoType = record.typeOf(i).toUpperCase();
							if(twoType.equals("BLANK"))
							{
								inputColumn[j] = "NULL";
							}
							else if(oneType.equals("INTEGER") && twoType.equals("NUMERIC"))
							{
								inputColumn[j] = String.valueOf((int) Double.parseDouble(String.valueOf(record.getDataOf(i))));
							}
							else if(oneType.equals("FLOAT") && twoType.equals("NUMERIC"))
							{
								inputColumn[j] = String.valueOf(record.getDataOf(i));
							}
							else if(oneType.equals("NUMERIC") && twoType.equals("INTEGER"))
							{
								inputColumn[j] = String.valueOf(record.getDataOf(i));
							}
							else if(oneType.equals("NUMERIC") && twoType.equals("FLOAT"))
							{
								inputColumn[j] = String.valueOf(record.getDataOf(i));
							}
							else throw new InvalidInputException(Manager.applyStringTable("On addData from record object") + " : " + record);
						}
						else inputColumn[j] = String.valueOf(record.getDataOf(i));
					}
				}
			}
		}
		
		addData(inputColumn);
		System.gc();
	}

	@Override
	public TableSet analyze(String analyzeFunction, Map<String, String> arguments)
	{
		return AnalyzeUtil.analyze(analyzeFunction, arguments, this);
	}
	
	@Override
	public TableSet analyze(String analyzeFunction,
			Map<String, String> arguments, TableSet... otherTableSets)
	{
		List<TableSet> otherTableSetList = new Vector<TableSet>();
		otherTableSetList.add(this);
		if(otherTableSets != null)
		{
			for(TableSet c : otherTableSets)
			{
				otherTableSetList.add(c);
			}
		}
		return AnalyzeUtil.analyze(analyzeFunction, arguments, otherTableSetList.toArray(new TableSet[otherTableSetList.size()]));
	}
	
	@Override
	public String getAlias()
	{
		return alias;
	}

	@Override
	public void setAlias(String alias)
	{
		this.alias = alias;
	}

	@Override
	public String toString()
	{
		if(DataUtil.isNotEmpty(alias))
		{
			return getName() + "(" + getAlias() + ")";
		}
		
		StringBuffer results = new StringBuffer("");
		for(int i=0; i<getColumnCount(); i++)
		{
			results = results.append(getColumnName(i));
			if(i < getColumnCount() - 1) results = results.append("\t");
		}
		results = results.append("\n------------------------------------\n");
		
		int i = 0, j = 0;
		
		for(i=0; i<getRecordCount(); i++)
		{
			try
			{
				for(j=0; j<getColumnCount(); j++)
				{
					results = results.append(getData(j, i));
				}
				results = results.append("\n");
			}
			catch(ArrayIndexOutOfBoundsException e)
			{
				StringBuffer err = new StringBuffer("");
				for(StackTraceElement errEl : e.getStackTrace())
				{
					err = err.append("\t " + errEl + "\n");
				}
				
				throw new ArrayIndexOutOfBoundsException(Manager.applyStringTable("Array index out of range") + ", " 
						+ Manager.applyStringTable("request") + " : " + j + "/" + getColumnCount() + ", " + i + "/" + getRecordCount()
						+ "\n " + Manager.applyStringTable("<-\n") + err
						+ "\n " + Manager.applyStringTable("Original Message") + "..."
						+ e.getMessage() + "\n" + Manager.applyStringTable("End"));
			}
		}
		return results.toString();
	}

	@Override
	public Long getUniqueId()
	{
		return uniqueId;
	}

	public void setUniqueId(Long uniqueId)
	{
		this.uniqueId = uniqueId;
	}
	
	@Override
	public int size()
	{
		return getRecordCount();
	}

	@Override
	public boolean isEmpty()
	{
		return getRecordCount() <= 0;
	}

	@Override
	public boolean contains(Object o)
	{
		for(int i=0; i<getRecordCount(); i++)
		{
			if(o.equals(getRecord(i)))
			{
				return true;
			}
		}
		return false;
	}
	
	@Override
	public List<Record> toRecordList()
	{
		List<Record> newList = new Vector<Record>();
		for(int i=0; i<getRecordCount(); i++)
		{
			newList.add(getRecord(i));
		}
		return newList;
	}

	@Override
	public Iterator<Record> iterator()
	{
		return toRecordList().iterator();
	}

	@Override
	public Object[] toArray()
	{
		return toRecordList().toArray();
	}

	@Override
	public <T> T[] toArray(T[] a)
	{
		return toRecordList().toArray(a);
	}

	@Override
	public boolean add(Record e)
	{
		addData(e);
		return true;
	}

	@Override
	public boolean containsAll(Collection<?> c)
	{
		return toRecordList().containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends Record> c)
	{
		for(Record r : c)
		{
			addData(r);
		}
		return true;
	}

	@Override
	public boolean addAll(int index, Collection<? extends Record> c)
	{
		int nowIndex = 0;
		for(Record r : c)
		{
			if(nowIndex >= index) break;
			addData(r);
			nowIndex++;
		}
		return false;
	}

	@Override
	public Record get(int index)
	{
		return getRecord(index);
	}

	@Override
	public int indexOf(Object o)
	{
		for(int i=0; i<getRecordCount(); i++)
		{
			if(getRecord(i).equals(o))
			{
				return i;
			}
		}
		return -1;
	}

	@Override
	public ListIterator<Record> listIterator()
	{
		return toRecordList().listIterator();
	}

	@Override
	public ListIterator<Record> listIterator(int index)
	{
		return toRecordList().listIterator(index);
	}

	@Override
	public List<Record> subList(int fromIndex, int toIndex)
	{
		return subTable(0, getColumnCount(), fromIndex, toIndex);
	}
	
	@Override
	public boolean remove(Object o)
	{
		for(int i=0; i<getRecordCount(); i++)
		{
			if(getRecord(i).equals(o))
			{
				remove(i);
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean removeAll(Collection<?> c)
	{
		boolean isExist = false;
		for(Object obj : c)
		{
			isExist = remove(obj);
			if(isExist) return isExist;
		}
		return false;
	}
	
	@Override
	public boolean retainAll(Collection<?> c)
	{
		List<Object> delList = new Vector<Object>();
		boolean isExist = false;
		for(Object obj : c)
		{
			for(int i=0; i<getRecordCount(); i++)
			{
				Record r = getRecord(i);
				if(! (r.equals(obj)))
				{
					delList.add(r);
					isExist = true;
					break;
				}
			}
		}
		removeAll(delList);
		return isExist;
	}
	
	@Override
	public TableSet typeInfo()
	{
		TableSet tableSet = new DefaultTableSet();
		if(getRecordCount() <= 0) throw new NullPointerException(Manager.applyStringTable("Cannot read information because there is no data."));
		
		for(int i=0; i<getColumnCount(); i++)
		{
			Map<String, String> record = new Hashtable<String, String>();
			record.put("TABLE_NAME", getName());
			record.put("COLUMN_ID", null);
			record.put("COLUMN_NAME", getColumnName(i));
			record.put("DATA_TYPE_NAME", getColumnTypeName(i));
			record.put("DATA_LENGTH", null);
			record.put("DEFAULT_VALUE", null);
		}
		
		return tableSet;
	}
}
