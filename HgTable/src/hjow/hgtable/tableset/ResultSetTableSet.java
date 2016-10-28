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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import hjow.hgtable.IncludesException;
import hjow.hgtable.Manager;
import hjow.hgtable.dao.Dao;
import hjow.hgtable.util.DataUtil;
import hjow.hgtable.util.InvalidInputException;
import hjow.hgtable.util.StreamUtil;

public class ResultSetTableSet extends AbstractTableSet
{
	private static final long serialVersionUID = 4394091247492318524L;
	protected PreparedStatement statement;
	protected ResultSet resultSet;
	protected ResultSetMetaData metaData;
	protected Dao dao;

	public ResultSetTableSet()
	{
		super();
	}
	
	public ResultSetTableSet(ResultSet resultSet, Dao dao)
	{
		init(resultSet, null, dao);
	}
	
	public ResultSetTableSet(ResultSet resultSet, PreparedStatement statement, Dao dao)
	{
		init(resultSet, statement, dao);
	}
	
	public void init(ResultSet resultSet, PreparedStatement statement, Dao dao)
	{
		this.resultSet = resultSet;
		this.dao = dao;
		this.statement = statement;
		try
		{
			this.metaData = resultSet.getMetaData();
		}
		catch (SQLException e)
		{
			throw new IncludesException(e);
		}
	}
	
	@Override
	public List<Column> getColumns()
	{
		List<Column> columns = new Vector<Column>();
		try
		{
			resultSet.beforeFirst();
			while(resultSet.next())
			{
				
			}
		}
		catch (SQLException e)
		{
			throw new IncludesException(e);
		}
		return columns;
	}

	@Override
	public String getColumnName(int index)
	{
		try
		{
			return metaData.getColumnName(index + 1);
		}
		catch (SQLException e)
		{
			throw new IncludesException(e);
		}
	}

	@Override
	public int getColumnType(int index)
	{
		try
		{
			int sqlVal = metaData.getColumnType(index + 1);
			return Column.typeCodeToDB(sqlVal);
		}
		catch (SQLException e)
		{
			throw new IncludesException(e);
		}
	}

	@Override
	public String getColumnTypeName(int index)
	{
		try
		{
			return Column.typeName(Column.typeCodeToSheet(metaData.getColumnType(index)));
		}
		catch (SQLException e)
		{
			throw new IncludesException(e);
		}
	}

	@Override
	public int getColumnCount()
	{
		try
		{
			return metaData.getColumnCount();
		}
		catch (SQLException e)
		{
			throw new IncludesException(e);
		}
	}

	@Override
	public void setColumns(List<Column> columns)
	{
		throw new InvalidInputException("Cannot use setColumns(cols) method on ResultSetTableSet. Try to get DefaultTableSet and use this method again.");
	}
	
	public TableSet getDefaultTableSet()
	{
		try
		{
			return new DefaultTableSet(metaData.getTableName(0), resultSet);
		}
		catch (Exception e)
		{
			throw new IncludesException(e);
		}
	}

	@Override
	public void setData(int col, int row, String data)
	{
		try
		{
			resultSet.absolute(row + 1);
			resultSet.updateString(col + 1, data);
			resultSet.updateRow();
		}
		catch (SQLException e)
		{
			throw new IncludesException(e);
		}
	}

	@Override
	public int getRecordCount()
	{
		try
		{
			resultSet.beforeFirst();
			int sum = 0;
			while(resultSet.next())
			{
				sum = sum + 1;
			}
			return sum;
		}
		catch (SQLException e)
		{
			throw new IncludesException(e);
		}
	}

	@Override
	public void insertIntoDB(Dao dao, Map<String, String> params, Map<String, String> columnSurroundFunctions,
			Map<String, Integer> columnSurroundFunctionParamIndex,
			Map<String, List<String>> columnSurroundFunctionParams)
	{
		throw new InvalidInputException("Cannot use insertIntoDB(dao, params, colProcessFuncs, colProcessFuncIndexes, colProcessFuncParams) method on ResultSetTableSet. Try to get DefaultTableSet and use this method again.");
		// TODO Auto-generated method stub
	}

	@Override
	public TableSet subTable(int startColumnIndex, int endColumnIndex, int startRecordIndex, int endRecordIndex)
	{
		TableSet newTableSet = new DefaultTableSet();
		try
		{
			resultSet.beforeFirst();
			newTableSet.setName(metaData.getTableName(0));
			
			resultSet.absolute(startRecordIndex + 1);
			List<Column> columns = new Vector<Column>();
			for(int colIdx=0; colIdx<metaData.getColumnCount(); colIdx++)
			{
				String colName = metaData.getColumnName(colIdx + 1);
				int colType = Column.typeCodeToSheet(metaData.getColumnType(colIdx + 1));
				Column newCol = new Column(colName, colType);
				columns.add(newCol);
			}
			
			while(resultSet.next())
			{
				for(int colIdx=0; colIdx<columns.size(); colIdx++)
				{
					String dataVal = "";
					switch(columns.get(colIdx).getType())
					{
					case Types.VARCHAR:
						dataVal = resultSet.getString(colIdx + 1);
						break;
					case Types.CHAR:
						dataVal = resultSet.getString(colIdx + 1);
						break;
					case Types.CLOB:
						dataVal = resultSet.getString(colIdx + 1);
						break;
					case Types.BOOLEAN:
						dataVal = String.valueOf(resultSet.getBoolean(colIdx + 1));
						break;
					case Types.INTEGER:
						dataVal = String.valueOf(resultSet.getInt(colIdx + 1));
						break;
					case Types.BIGINT:
						dataVal = String.valueOf(resultSet.getLong(colIdx + 1));
						break;
					case Types.DATE:
						Date date = resultSet.getDate(colIdx + 1);
						SimpleDateFormat formatter = new SimpleDateFormat(Manager.getOption("defaultDateFormat"));
						dataVal = formatter.format(date);
						break;
					case Types.NUMERIC:
						dataVal = String.valueOf(resultSet.getDouble(colIdx + 1));
						break;
					case Types.DOUBLE:
						dataVal = String.valueOf(resultSet.getDouble(colIdx + 1));
						break;
					case Types.BLOB:
						Blob blob = resultSet.getBlob(colIdx + 1);
						InputStream blobInputStream = null;
						OutputStream collector = null;
						try
						{
							blobInputStream = blob.getBinaryStream();
							collector = new ByteArrayOutputStream();
							List<Byte> byteList = new Vector<Byte>();
							byte[] buffers = new byte[1024];
							int getLengths = -1;
							getLengths = blobInputStream.read(buffers);
							for(int idx=0; idx<getLengths; idx++)
							{
								byteList.add(new Byte(buffers[idx]));
							}
							byte[] results = new byte[byteList.size()];
							for(int idx=0; idx<byteList.size(); idx++)
							{
								results[idx] = byteList.get(idx).byteValue();
							}
							dataVal = DataUtil.base64(results);
							
							blobInputStream.close();
						}
						catch(Throwable t)
						{
							throw new IncludesException(t);
						}
						finally
						{
							try
							{
								blobInputStream.close();
							}
							catch(Throwable t1)
							{
								
							}
							try
							{
								collector.close();
							}
							catch(Throwable t1)
							{
								
							}
						}
						
						break;
					case Types.NULL:
						dataVal = "";
						break;
					default:
						dataVal = resultSet.getString(colIdx + 1);
						break;
					}
					columns.get(colIdx).getData().add(dataVal);
				}
			}
			newTableSet.setColumns(columns);
		}
		catch (SQLException e)
		{
			throw new IncludesException(e);
		}
		return newTableSet;
	}
	
	@Override
	public void addColumn(Column newColumn)
	{
		throw new InvalidInputException("Cannot use addColumn(newCol) method on ResultSetTableSet. Try to get DefaultTableSet and use this method again.");
	}

	@Override
	public Column getColumn(int index)
	{
		throw new InvalidInputException("Cannot use getColumn(idx) method on ResultSetTableSet. Try to get DefaultTableSet and use this method again.");
		// TODO Auto-generated method stub
	}

	@Override
	public Column getColumn(String name)
	{
		throw new InvalidInputException("Cannot use getColumn(name) method on ResultSetTableSet. Try to get DefaultTableSet and use this method again.");
		// TODO Auto-generated method stub
	}

	@Override
	public void addData(ResultSet data) throws Exception
	{
		throw new InvalidInputException("Cannot use addData(resultSet) method on ResultSetTableSet. Try to get DefaultTableSet and use this method again.");
		// TODO Auto-generated method stub
	}

	@Override
	public void addData(TableSet tableSet) throws Exception
	{
		throw new InvalidInputException("Cannot use addData(tableSet) method on ResultSetTableSet. Try to get DefaultTableSet and use this method again.");
		// TODO Auto-generated method stub
	}

	@Override
	public void addData(String[] dataArray)
	{
		throw new InvalidInputException("Cannot use addData(dataArray) method on ResultSetTableSet. Try to get DefaultTableSet and use this method again.");
		// TODO Auto-generated method stub
	}

	@Override
	public void addData(List<String> dataArray)
	{
		throw new InvalidInputException("Cannot use addData(dataList) method on ResultSetTableSet. Try to get DefaultTableSet and use this method again.");
		// TODO Auto-generated method stub
	}

	@Override
	public Record getRecord(int index)
	{
		try
		{
			resultSet.absolute(index + 1);
			Record record = new Record();
			List<Object> data = record.getDatas();
			List<String> colNames = record.getColumnName();
			List<Integer> types = record.getTypes();
			for(int idx=0; idx<metaData.getColumnCount(); idx++)
			{
				String dataVal = null;
				switch(metaData.getColumnType(idx + 1))
				{
				case Types.VARCHAR:
					dataVal = resultSet.getString(idx + 1);
					break;
				case Types.CHAR:
					dataVal = resultSet.getString(idx + 1);
					break;
				case Types.CLOB:
					dataVal = resultSet.getString(idx + 1);
					break;
				case Types.BOOLEAN:
					dataVal = String.valueOf(resultSet.getBoolean(idx + 1));
					break;
				case Types.INTEGER:
					dataVal = String.valueOf(resultSet.getInt(idx + 1));
					break;
				case Types.BIGINT:
					dataVal = String.valueOf(resultSet.getLong(idx + 1));
					break;
				case Types.DATE:
					Date date = resultSet.getDate(idx + 1);
					SimpleDateFormat formatter = new SimpleDateFormat(Manager.getOption("defaultDateFormat"));
					dataVal = formatter.format(date);
					break;
				case Types.NUMERIC:
					dataVal = String.valueOf(resultSet.getDouble(idx + 1));
					break;
				case Types.DOUBLE:
					dataVal = String.valueOf(resultSet.getDouble(idx + 1));
					break;
				case Types.BLOB:
					Blob blob = resultSet.getBlob(idx + 1);
					InputStream blobInputStream = null;
					OutputStream collector = null;
					try
					{
						blobInputStream = blob.getBinaryStream();
						collector = new ByteArrayOutputStream();
						List<Byte> byteList = new Vector<Byte>();
						byte[] buffers = new byte[1024];
						int getLengths = -1;
						getLengths = blobInputStream.read(buffers);
						for(int bdx=0; bdx<getLengths; bdx++)
						{
							byteList.add(new Byte(buffers[bdx]));
						}
						byte[] results = new byte[byteList.size()];
						for(int bdx=0; bdx<byteList.size(); bdx++)
						{
							results[bdx] = byteList.get(bdx).byteValue();
						}
						dataVal = DataUtil.base64(results);
						
						blobInputStream.close();
					}
					catch(Throwable t)
					{
						throw new IncludesException(t);
					}
					finally
					{
						try
						{
							blobInputStream.close();
						}
						catch(Throwable t1)
						{
							
						}
						try
						{
							collector.close();
						}
						catch(Throwable t1)
						{
							
						}
					}
					
					break;
				case Types.NULL:
					dataVal = "";
					break;
				default:
					dataVal = resultSet.getString(idx + 1);
					break;
				}
				
				data.add(dataVal);
				types.add(new Integer(Column.typeCodeToSheet(metaData.getColumnType(idx + 1))));
				colNames.add(metaData.getColumnName(idx + 1));
			}
			return record;
		}
		catch (SQLException e)
		{
			throw new IncludesException(e);
		}
	}

	@Override
	public void removeEmptyColumn(boolean exceptWhenNameExists)
	{
		throw new InvalidInputException("Cannot use removeEmptyColumn(exceptNameExists) method on ResultSetTableSet. Try to get DefaultTableSet and use this method again.");
		// TODO Auto-generated method stub
	}

	@Override
	public void removeEmptyRow()
	{
		throw new InvalidInputException("Cannot use removeEmptyRow() method on ResultSetTableSet. Try to get DefaultTableSet and use this method again.");
		// TODO Auto-generated method stub
	}

	@Override
	public void removeColumn(String columnName)
	{
		throw new InvalidInputException("Cannot use removeColumn(colName) method on ResultSetTableSet. Try to get DefaultTableSet and use this method again.");
		// TODO Auto-generated method stub
	}

	@Override
	public String getCreateTableSQL(boolean useVarchar2, Map<String, String> options)
	{
		throw new InvalidInputException("Cannot use getCreateTableSQL(useVarchar2, options) method on ResultSetTableSet. Try to get DefaultTableSet and use this method again.");
		// TODO Auto-generated method stub
	}

	@Override
	public void noMoreUse()
	{
		try
		{
			this.resultSet.close();
		}
		catch (SQLException e)
		{
			
		}
		this.statement = null;
		this.resultSet = null;
		this.metaData = null;
		this.dao = null;
	}

	@Override
	public String help()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void clear()
	{
		try
		{
			int preventInf = getRecordCount();
			resultSet.beforeFirst();
			while(resultSet.next() && preventInf >= 0)
			{
				resultSet.deleteRow();
				resultSet.beforeFirst();
				preventInf--;
			}
		}
		catch (SQLException e)
		{
			throw new IncludesException(e);
		}
	}

	@Override
	public Record set(int index, Record element)
	{
		try
		{
			resultSet.absolute(index + 1);
			Record beforeRecord = new Record(resultSet);
			
			for(int i=0; i<element.getColumnName().size(); i++)
			{
				String colName = element.getColumnName().get(i);
				int type = element.getTypes().get(i);
				Object data = element.getDataOf(i);
				if(type == Column.TYPE_INTEGER)
				{
					resultSet.updateInt(colName, Integer.parseInt(String.valueOf(data)));
				}
				else if(type == Column.TYPE_FLOAT)
				{
					resultSet.updateDouble(colName, Double.parseDouble(String.valueOf(data)));
				}
				else if(type == Column.TYPE_NUMERIC)
				{
					resultSet.updateDouble(colName, Double.parseDouble(String.valueOf(data)));
				}
				else if(type == Column.TYPE_DATE)
				{
					if(data instanceof Date)
					{
						resultSet.updateDate(colName, (Date) data);
					}
					else
					{
						resultSet.updateDate(colName, new Date(DataUtil.toDate(String.valueOf(data), Manager.getOption("defaultDateFormat")).getTime()));
					}
				}
				else if(type == Column.TYPE_STRING)
				{
					resultSet.updateString(colName, String.valueOf(data));
				}
				else if(type == Column.TYPE_BOOLEAN)
				{
					resultSet.updateBoolean(colName, DataUtil.parseBoolean(data));
				}
				else if(type == Column.TYPE_BLOB)
				{
					ByteArrayInputStream byteStream = null;
					if(data instanceof byte[]) byteStream = new ByteArrayInputStream((byte[]) data);
					else byteStream = new ByteArrayInputStream(DataUtil.base64(String.valueOf(data)));
					resultSet.updateBlob(colName, byteStream);
				}
				else if(type == Column.TYPE_OBJECT)
				{
					if(data instanceof String) resultSet.updateObject(colName, StreamUtil.toObject(DataUtil.base64((String) data)));
					else resultSet.updateObject(colName, data);
				}
			}
			
			return beforeRecord;
		}
		catch (SQLException e)
		{
			throw new IncludesException(e);
		}
		catch (ParseException e)
		{
			throw new IncludesException(e);
		}
		catch (IOException e)
		{
			throw new IncludesException(e);
		}
		catch (ClassNotFoundException e)
		{
			throw new IncludesException(e);
		}
	}

	@Override
	public void add(int index, Record element)
	{
		try
		{
			resultSet.moveToInsertRow();
			for(int idx=0; idx<element.columnCount(); idx++)
			{
				String colName = element.getColumnName(idx);
				Object dataVal = element.getData(colName);
				int type = element.getTypes().get(idx).intValue();
				if(type == Column.TYPE_STRING)
				{
					resultSet.updateString(colName, String.valueOf(dataVal));
				}
				else if(type == Column.TYPE_DATE)
				{
					if(dataVal instanceof java.util.Date)
						resultSet.updateDate(colName, new Date(((java.util.Date) dataVal).getTime()));
					else
						resultSet.updateDate(colName, new Date(DataUtil.toDate(String.valueOf(dataVal), Manager.getOption("defaultDateFormat")).getTime()));
				}
				else if(type == Column.TYPE_BOOLEAN)
				{
					resultSet.updateBoolean(colName, DataUtil.parseBoolean(dataVal));
				}
				else if(type == Column.TYPE_NUMERIC || type == Column.TYPE_FLOAT)
				{
					resultSet.updateDouble(colName, Double.parseDouble(String.valueOf(dataVal)));
				}
				else if(type == Column.TYPE_INTEGER)
				{
					resultSet.updateInt(colName, Integer.parseInt(String.valueOf(dataVal)));
				}
			}
			resultSet.insertRow();
		}
		catch (Exception e)
		{
			throw new IncludesException(e);
		}
	}

	@Override
	public Record remove(int index)
	{
		try
		{
			resultSet.absolute(index + 1);
			Record newRec = new Record();
			for(int idx=0; idx<metaData.getColumnCount(); idx++)
			{
				String dataVal = null;
				switch(metaData.getColumnType(idx + 1))
				{
				case Types.VARCHAR:
					dataVal = resultSet.getString(idx + 1);
					break;
				case Types.CHAR:
					dataVal = resultSet.getString(idx + 1);
					break;
				case Types.CLOB:
					dataVal = resultSet.getString(idx + 1);
					break;
				case Types.BOOLEAN:
					dataVal = String.valueOf(resultSet.getBoolean(idx + 1));
					break;
				case Types.INTEGER:
					dataVal = String.valueOf(resultSet.getInt(idx + 1));
					break;
				case Types.BIGINT:
					dataVal = String.valueOf(resultSet.getLong(idx + 1));
					break;
				case Types.DATE:
					Date date = resultSet.getDate(idx + 1);
					SimpleDateFormat formatter = new SimpleDateFormat(Manager.getOption("defaultDateFormat"));
					dataVal = formatter.format(date);
					break;
				case Types.NUMERIC:
					dataVal = String.valueOf(resultSet.getDouble(idx + 1));
					break;
				case Types.DOUBLE:
					dataVal = String.valueOf(resultSet.getDouble(idx + 1));
					break;
				case Types.BLOB:
					Blob blob = resultSet.getBlob(idx + 1);
					InputStream blobInputStream = null;
					OutputStream collector = null;
					try
					{
						blobInputStream = blob.getBinaryStream();
						collector = new ByteArrayOutputStream();
						List<Byte> byteList = new Vector<Byte>();
						byte[] buffers = new byte[1024];
						int getLengths = -1;
						getLengths = blobInputStream.read(buffers);
						for(int bdx=0; bdx<getLengths; bdx++)
						{
							byteList.add(new Byte(buffers[bdx]));
						}
						byte[] results = new byte[byteList.size()];
						for(int bdx=0; bdx<byteList.size(); bdx++)
						{
							results[bdx] = byteList.get(bdx).byteValue();
						}
						dataVal = DataUtil.base64(results);
						
						blobInputStream.close();
					}
					catch(Throwable t)
					{
						throw new IncludesException(t);
					}
					finally
					{
						try
						{
							blobInputStream.close();
						}
						catch(Throwable t1)
						{
							
						}
						try
						{
							collector.close();
						}
						catch(Throwable t1)
						{
							
						}
					}
					
					break;
				case Types.NULL:
					dataVal = "";
					break;
				default:
					dataVal = resultSet.getString(idx + 1);
					break;
				}
				newRec.getDatas().add(dataVal);
				newRec.getTypes().add(new Integer(Column.typeCodeToSheet(metaData.getColumnType(idx + 1))));
				newRec.getColumnName().add(metaData.getColumnName(idx + 1));
			}
			resultSet.deleteRow();
			return newRec;
		}
		catch (SQLException e)
		{
			throw new IncludesException(e);
		}
	}
	
	/**
	 * <p>변경 사항을 실제 DB에 적용합니다.</p>
	 * 
	 * @throws Exception : dao 객체가 유효하지 않거나 접속이 끊김, 혹은 교착상태
	 */
	public void commit() throws Exception
	{
		dao.commit();
	}
	
	/**
	 * <p>조회 중일 경우 조회 요청을 취소합니다. 이후 noMoreUse() 가 호출되므로 이 테이블 셋 객체를 더 사용할 수 없게 됩니다.</p>
	 * 
	 * @throws SQLException : 취소 요청을 지원하지 않는 JDBC인 경우
	 */
	public void cancel() throws SQLException
	{
		statement.cancel();
		noMoreUse();
	}

	@Override
	public int lastIndexOf(Object o)
	{
		for(int i=size()-1; i>=0; i--)
		{
			if(get(i) == o) return i;
		}
		return -1;
	}

	public ResultSet getResultSet()
	{
		return resultSet;
	}

	public void setResultSet(ResultSet resultSet)
	{
		this.resultSet = resultSet;
	}

	public ResultSetMetaData getMetaData()
	{
		return metaData;
	}

	public void setMetaData(ResultSetMetaData metaData)
	{
		this.metaData = metaData;
	}

	public Dao getDao()
	{
		return dao;
	}

	public void setDao(Dao dao)
	{
		this.dao = dao;
	}

	public PreparedStatement getStatement()
	{
		return statement;
	}

	public void setStatement(PreparedStatement statement)
	{
		this.statement = statement;
	}
}
