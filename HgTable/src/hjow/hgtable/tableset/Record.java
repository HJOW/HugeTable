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

import hjow.hgtable.IncludesException;
import hjow.hgtable.Main;
import hjow.hgtable.Manager;
import hjow.hgtable.dao.Dao;
import hjow.hgtable.jscript.JScriptObject;
import hjow.hgtable.util.DataUtil;
import hjow.hgtable.util.InvalidInputException;
import hjow.hgtable.util.StreamUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

/**
 * <p>한 행에 해당하는 레코드 클래스입니다.</p>
 * 
 * @author HJOW
 *
 */
public class Record implements JScriptObject, Comparable<Record>, Map<String, Object>
{
	private static final long serialVersionUID = -8362348554919491120L;
	protected List<Integer> types = new Vector<Integer>();
	protected List<String> columnName = new Vector<String>();
	protected List<Object>  datas = new Vector<Object>();
	
	protected transient String targetColumnToCompare = null;
	
	/**
	 * <p>기본 생성자입니다.</p>
	 * 
	 */
	public Record()
	{
		
	}
	
	/**
	 * <p>맵 객체로부터 행 객체를 만듭니다.</p>
	 * 
	 * @param mapData 맵 객체
	 */
	public Record(Map<String, ?> mapData)
	{
		Set<String> keys = mapData.keySet();
		for(String k : keys)
		{
			Object data = mapData.get(k);
			if(data instanceof String)
			{
				types.add(new Integer(Column.TYPE_STRING));
				columnName.add(k);
				datas.add(data);
			}
			else if((data instanceof Integer) || (data instanceof BigInteger))
			{
				types.add(new Integer(Column.TYPE_INTEGER));
				columnName.add(k);
				datas.add(new Integer(String.valueOf(data)));
			}
			else if((data instanceof Double) || (data instanceof Float) || (data instanceof BigDecimal))
			{
				types.add(new Integer(Column.TYPE_NUMERIC));
				columnName.add(k);
				datas.add(new Double(String.valueOf(data)));
			}
			else if((data instanceof Boolean))
			{
				types.add(new Integer(Column.TYPE_BOOLEAN));
				columnName.add(k);
				datas.add(DataUtil.parseBoolean(data));
			}
			else
			{
				types.add(new Integer(Column.TYPE_STRING));
				columnName.add(k);
				datas.add(String.valueOf(data));
			}
		}
	}
	
	public Record(ResultSet resultSet) throws SQLException
	{
		ResultSetMetaData metaData = resultSet.getMetaData();
		List<Integer> types = new Vector<Integer>();
		List<Object> datas = new Vector<Object>();
		List<String> colNames = new Vector<String>();
		
		for(int i=0; i<metaData.getColumnCount(); i++)
		{
			int type = metaData.getColumnType(i+1);
			types.add(new Integer(Column.typeCodeToSheet(type)));
			String colName = metaData.getColumnName(i+1);
			colNames.add(colName);
			
			switch(type)
			{
			case Types.INTEGER:
				datas.add(resultSet.getInt(colName));
				break;
			case Types.BIGINT:
				datas.add(new Long(resultSet.getLong(colName)));
				break;
			case Types.VARCHAR:
				datas.add(resultSet.getString(colName));
				break;
			case Types.CHAR:
				datas.add(resultSet.getString(colName));
				break;
			case Types.CLOB:
				datas.add(resultSet.getString(colName));
				break;
			case Types.BLOB:
				Blob blob = resultSet.getBlob(colName);
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
					datas.add(DataUtil.base64(results));
					
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
			case Types.FLOAT:
				datas.add(new Double(resultSet.getDouble(colName)));
				break;
			case Types.DOUBLE:
				datas.add(new Double(resultSet.getDouble(colName)));
				break;
			case Types.NUMERIC:
				datas.add(new Double(resultSet.getDouble(colName)));
				break;
			case Types.BOOLEAN:
				datas.add(new Boolean(resultSet.getBoolean(colName)));
				break;
			case Types.JAVA_OBJECT:
				try
				{
					datas.add(DataUtil.base64(StreamUtil.toBytes(resultSet.getObject(colName))));
				}
				catch (IOException e)
				{
					throw new IncludesException(e);
				}
				break;
			case Types.NULL:
				datas.add("");
				break;
	        default:
	        	datas.add(resultSet.getString(colName));
	        	break;
			}
		}
		
		setTypes(types);
		setDatas(datas);
		setColumnName(colNames);
	}

	/**
	 * <p>컬럼 이름을 반환합니다.</p>
	 * 
	 * @param index : 컬럼 번호 (0부터 시작)
	 * @return 컬럼 이름
	 */
	public String getColumnName(Object index)
	{
		if(index instanceof Integer) return columnName.get(((Integer) index).intValue()); 
		return columnName.get(Integer.parseInt(String.valueOf(index)));
	}
	
	/**
	 * <p>컬럼 이름을 변경합니다.</p>
	 * 
	 * @param index : 컬럼 번호 (0부터 시작)
	 * @param newColumnName : 새 컬럼 이름
	 */
	public void setColumnName(Object index, Object newColumnName)
	{
		if(index instanceof Integer) columnName.set(((Integer) index).intValue(), String.valueOf(newColumnName)); 
		columnName.set(Integer.parseInt(String.valueOf(index)), String.valueOf(newColumnName));
	}
	/**
	 * <p>컬럼 타입 리스트를 반환합니다. 타입 코드들의 리스트로 반환됩니다/</p>
	 * 
	 * @return 타입 리스트
	 */
	public List<Integer> getTypes()
	{
		return types;
	}

	public void setTypes(List<Integer> types)
	{
		this.types = types;
	}

	/**
	 * <p>이 레코드에 포함된 데이터 전부를 리스트로 반환합니다.</p>
	 * 
	 * @return 데이터 리스트
	 */
	public List<Object> getDatas()
	{
		return datas;
	}
	
	/**
	 * <p>index 번째 컬럼의 데이터를 반환합니다. 0부터 시작합니다.</p>
	 * 
	 * @param index : 컬럼 번호 (0부터 시작)
	 * @return 해당 값
	 */
	public Object getDataOf(int index)
	{
		try
		{
			return datas.get(index);
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			StringBuffer err = new StringBuffer("");
			for(StackTraceElement errEl : e.getStackTrace())
			{
				err = err.append("\t " + errEl + "\n");
			}
			
			throw new ArrayIndexOutOfBoundsException(Manager.applyStringTable("Array index out of range") + ", " 
					+ Manager.applyStringTable("request") + " : " + index + "/" + datas.size()
					+ "\n " + Manager.applyStringTable("<-\n") + err+ "\n " + Manager.applyStringTable("Original Message") + "..."
					+ e.getMessage() + "\n" + Manager.applyStringTable("End"));
		}
	}
	
	/**
	 * <p>해당 컬럼 이름의 데이터를 반환합니다.</p>
	 * 
	 * @param columnName : 컬럼 이름
	 * @return 해당 값
	 */
	public Object getData(Object columnName)
	{
		int i = 0;
		
		if(columnName instanceof Integer)
		{
			try
			{
				return getDataOf(((Integer) columnName).intValue());
			}
			catch(IndexOutOfBoundsException e)
			{
				return null;
			}
		}
		
		try
		{
			return getDataOf(Integer.parseInt(String.valueOf(columnName)));
		}
		catch(NumberFormatException e)
		{
			try
			{
				for(i=0; i<this.columnName.size(); i++)
				{
					if(Manager.getOption("case_columnname") == null)
					{
						if(this.columnName.get(i).equalsIgnoreCase(String.valueOf(columnName)))
						{
							return getDataOf(i);
						}
					}
					else if(DataUtil.parseBoolean(Manager.getOption("case_columnname")))
					{
						if(this.columnName.get(i).equals(String.valueOf(columnName)))
						{
							return getDataOf(i);
						}
					}
					else
					{
						if(this.columnName.get(i).equalsIgnoreCase(String.valueOf(columnName)))
						{
							return getDataOf(i);
						}
					}
				}
				return null;
			}
			catch(Throwable e1)
			{
				Main.logError(e1, Manager.applyStringTable("On getData, trying to get") + " " + columnName + " " + Manager.applyStringTable("'s data of") + "...\n" + this.toString());
				return null;
			}
		}
	}
	
	/**
	 * <p>index 번째 컬럼의 데이터를 지정합니다.</p>
	 * 
	 * @param index : 컬럼 번호 (0부터 시작)
	 * @param data : 변경할 새 데이터
	 */
	public void setDataOf(int index, String data)
	{
		datas.set(index, data);
	}
	
	/**
	 * <p>해당 컬럼 이름의 데이터를 반환합니다.</p>
	 * 
	 * @param columnName : 컬럼 이름
	 * @return 해당 값
	 */
	public void setData(Object columnName, Object data)
	{
		int i = 0;
		
		if(columnName instanceof Integer)
		{
			setDataOf(((Integer) columnName).intValue(), String.valueOf(data));
		}
		
		try
		{
			setDataOf(Integer.parseInt(String.valueOf(columnName)), String.valueOf(data));
		}
		catch(NumberFormatException e)
		{
			try
			{
				for(i=0; i<this.columnName.size(); i++)
				{
					if(Manager.getOption("case_columnname") == null)
					{
						if(this.columnName.get(i).equalsIgnoreCase(String.valueOf(columnName)))
						{
							setDataOf(i, String.valueOf(data));
						}
					}
					else if(DataUtil.parseBoolean(Manager.getOption("case_columnname")))
					{
						if(this.columnName.get(i).equals(String.valueOf(columnName)))
						{
							setDataOf(i, String.valueOf(data));
						}
					}
					else
					{
						if(this.columnName.get(i).equalsIgnoreCase(String.valueOf(columnName)))
						{
							setDataOf(i, String.valueOf(data));
						}
					}
				}
			}
			catch(Throwable e1)
			{
				Main.logError(e1, Manager.applyStringTable("On setData, trying to set") + " " + columnName + " " + Manager.applyStringTable("'s data of") + "...\n" + this.toString()
						+ "\n... " + Manager.applyStringTable("to") + " : " + String.valueOf(data));
			}
		}
	}
	
	/**
	 * <p>index 번째 컬럼의 타입 이름을 반환합니다. 0부터 시작합니다.</p>
	 * 
	 * @param index : 컬럼 번호 (0부터 시작)
	 * @return 데이터 타입 이름
	 */
	public String typeOf(int index)
	{
		if(Column.TYPE_BLANK == this.types.get(index))
		{
			return "BLANK";
		}
		else if(Column.TYPE_BOOLEAN == this.types.get(index))
		{
			return "BOOLEAN";
		}
		else if(Column.TYPE_DATE == this.types.get(index))
		{
			return "DATE";
		}
		else if(Column.TYPE_ERROR == this.types.get(index))
		{
			return "ERROR";
		}
		else if(Column.TYPE_FORMULA == this.types.get(index))
		{
			return "FORMULA";
		}
		else if(Column.TYPE_NUMERIC == this.types.get(index))
		{
			return "NUMERIC";
		}
		else if(Column.TYPE_INTEGER == this.types.get(index))
		{
			return "INTEGER";
		}
		else if(Column.TYPE_FLOAT == this.types.get(index))
		{
			return "FLOAT";
		}
		else if(Column.TYPE_STRING == this.types.get(index))
		{
			return "STRING";
		}
		else return "BLANK";
	}
	/**
	 * <p>해당 컬럼의 데이터 타입 이름을 반환합니다.</p>
	 * 
	 * @param columnName : 컬럼 이름, 혹은 컬럼 번호 (0부터 시작)
	 * @return 데이터 타입 이름
	 */
	public String type(Object columnName)
	{
		if(columnName instanceof Integer)
		{
			try
			{
				return typeOf(((Integer) columnName).intValue());
			}
			catch(IndexOutOfBoundsException e)
			{
				return null;
			}
		}
		
		String colName = String.valueOf(columnName);
		
		try
		{
			return typeOf(Integer.parseInt(colName));
		}
		catch(NumberFormatException e)
		{
			for(int i=0; i<this.columnName.size(); i++)
			{
				if(this.columnName.get(i).equalsIgnoreCase(colName))
				{
					return typeOf(i);
				}
			}
		}
		
		return null;
	}
	
	/**
	 * <p>index 번째 컬럼의 타입을 지정합니다. 0부터 시작합니다. 기존 데이터가 변환되지는 않습니다.</p>
	 * 
	 * @param index : 컬럼 번호 (0부터 시작)
	 * @param typeName : 타입 이름, BLANK, BOOLEAN, DATE, INTEGER, FLOAT, NUMERIC, STRING 사용 가능
	 */
	public void setTypeOf(int index, String typeName)
	{
		if(typeName.equalsIgnoreCase("BLANK"))
		{
			types.set(index, new Integer(Column.TYPE_BLANK));
		}
		else if(typeName.equalsIgnoreCase("BOOLEAN"))
		{
			types.set(index, new Integer(Column.TYPE_BOOLEAN));
		}
		else if(typeName.equalsIgnoreCase("DATE"))
		{
			types.set(index, new Integer(Column.TYPE_DATE));
		}
		else if(typeName.equalsIgnoreCase("ERROR"))
		{
			types.set(index, new Integer(Column.TYPE_ERROR));
		}
		else if(typeName.equalsIgnoreCase("FORMULA"))
		{
			types.set(index, new Integer(Column.TYPE_FORMULA));
		}
		else if(typeName.equalsIgnoreCase("NUMERIC"))
		{
			types.set(index, new Integer(Column.TYPE_NUMERIC));
		}
		else if(typeName.equalsIgnoreCase("INTEGER"))
		{
			types.set(index, new Integer(Column.TYPE_INTEGER));
		}
		else if(typeName.equalsIgnoreCase("FLOAT"))
		{
			types.set(index, new Integer(Column.TYPE_FLOAT));
		}
		else if(typeName.equalsIgnoreCase("STRING"))
		{
			types.set(index, new Integer(Column.TYPE_STRING));
		}
	}
	/**
	 * <p>해당 컬럼의 데이터 타입을 지정합니다.</p>
	 * 
	 * @param columnName : 컬럼 이름
	 * @param typeName : 타입 이름, BLANK, BOOLEAN, DATE, INTEGER, FLOAT, NUMERIC, STRING 사용 가능
	 */
	public void setType(Object columnName, Object typeName)
	{
		if(columnName instanceof Integer) setTypeOf(((Integer) columnName).intValue(), String.valueOf(typeName));
		
		try
		{
			setTypeOf(Integer.parseInt(String.valueOf(columnName)), String.valueOf(columnName));
		}
		catch(NumberFormatException e)
		{
			for(int i=0; i<this.columnName.size(); i++)
			{
				if(this.columnName.get(i).equalsIgnoreCase(String.valueOf(columnName)))
				{
					setTypeOf(i, String.valueOf(typeName));
				}
			}
		}
	}
	public void setDatas(List<Object> datas)
	{
		this.datas = datas;
	}

	public List<String> getColumnName()
	{
		return columnName;
	}

	public void setColumnName(List<String> columnName)
	{
		this.columnName = columnName;
	}
	
	/**
	 * <p>SQL INSERT 문으로 반환합니다.</p>
	 * 
	 * @param name : 테이블 이름
	 * @return INSERT 문
	 */
	public String toInsertSQL(String name)
	{
		StringBuffer results = new StringBuffer("INSERT INTO " + name + " (");
		for(int i=0; i<columnName.size(); i++)
		{
			results = results.append(columnName.get(i));
			if(i < columnName.size() - 1) results = results.append(",");
		}
		results = results.append(") VALUES (");
		
		for(int i=0; i<datas.size(); i++)
		{
			if(getTypes().get(i).intValue() == Column.TYPE_NUMERIC 
					|| getTypes().get(i).intValue() == Column.TYPE_BOOLEAN
					|| getTypes().get(i).intValue() == Column.TYPE_FLOAT
					|| getTypes().get(i).intValue() == Column.TYPE_INTEGER)
			{
				results = results.append(String.valueOf(datas.get(i)));
			}
			else
			{
				results = results.append("'");
				results = results.append(DataUtil.castQuote(false, String.valueOf(datas.get(i))));
				results = results.append("'");
			}
			if(i < datas.size() - 1) results = results.append(",");
		}
		
		results = results.append(")");
		
		return results.toString();
	}

	/**
	 * <p>SQL INSERT 문을 반환합니다.</p>
	 * 
	 * @param name : 테이블 이름
	 * @param transaction : true 시 트랜잭션 사용 (성공 시 commit, 실패 시 rollback)
	 * @param skipDataForm : true 시 날짜 형식 무시
	 * @param functionData : 타입 별 변환 함수 정보, 없으면 그대로 들어감
	 * @param functionIndexData : 타입 별 변환 함수에서 해당 데이터가 몇 번째 파라미터인지를 지정하는 정보
	 * @param functionOtherParams : 타입 별 변환 함수에서 다른 파라미터 데이터들 정보
	 * @return INSERT 문
	 */
	public String toInsertSQL(String name, Object transaction, Object skipDataForm, Map<String, String> functionData, Map<String, Integer> functionIndexData, Map<String, List<String>> functionOtherParams)
	{
		// INSERT 문장 생성 시작
		String sqls = "INSERT INTO " + name + " (";
		
		// 컬럼부 생성
		for(int i=0; i<columnName.size(); i++)
		{
			if(types.get(i) == Column.TYPE_DATE && DataUtil.parseBoolean(skipDataForm)) // 날짜 형식이고, 날짜 형식 생략하기로 한 경우
			{
				if(i == columnName.size() - 1) // 마지막 컬럼이면서 쉼표로 끝나고 있는 경우 쉼표 제거
				{
					sqls = sqls.trim();
					if(sqls.endsWith(",")) sqls = sqls.substring(0, sqls.length() - 1);
				}
				continue; // 건너뛰기
			}
			
			// INSERT 문장에 컬럼이름 추가
			sqls = sqls + columnName.get(i);
			
			// 마지막 컬럼이 아니면 쉼표 추가
			if(i < columnName.size() - 1) sqls = sqls + ", ";
		}
		
		// 컬럼부 완료, 데이터부 시작
		sqls = sqls + ") VALUES (";
		
		// 변수 준비
		String dataCell = "";
		boolean isIndexExist = false;
		boolean isEmpty = false;
		List<String> functionParams = null;
		String functionTemp;
				
		for(int i=0; i<datas.size(); i++)
		{
			isIndexExist = false;
			isEmpty = DataUtil.isEmpty(datas.get(i));
			
			if(types.get(i) == Column.TYPE_DATE && DataUtil.parseBoolean(skipDataForm)) // 날짜 형식이고, 날짜 형식 생략하기로 한 경우
			{
				if(i == columnName.size() - 1) // 마지막 컬럼이면서 쉼표로 끝나고 있는 경우 쉼표 제거
				{
					sqls = sqls.trim();
					if(sqls.endsWith(",")) sqls = sqls.substring(0, sqls.length() - 1);
				}
				continue; // 건너뛰기
			}
			
			if(isEmpty)
			{
				dataCell = "NULL";
			}
			else
			{
				// 데이터를 변수에 담기
				dataCell = DataUtil.castQuote(true, String.valueOf(datas.get(i)));
				
				// 변환 함수 적용여부 확인, 적용
				if(functionData != null && functionData.get(columnName.get(i)) != null) // 변환 함수를 거쳐야 하는 경우
				{
					if(functionIndexData != null && functionOtherParams != null) // 변환 함수에 다른 매개변수까지 필요한 경우인지 확인
					{
						if(functionIndexData.get(columnName.get(i)) != null && functionIndexData.get(columnName.get(i)).intValue() >= 0)
						{
							isIndexExist = true;
						}
					}
					
					if(isIndexExist) // 변환 함수 필요, 매개변수 필요한 경우
					{
						functionParams = functionOtherParams.get(columnName.get(i)); // 매개변수 리스트를 받아 옴
						
						functionTemp = functionData.get(columnName.get(i)) + "("; // 변환 함수 이름을 넣고 괄호 시작
						
						for(int k=0; k<functionParams.size(); k++) // 매개변수 하나하나씩 적용
						{
							if(functionIndexData.get(columnName.get(i)).intValue() == k) // 현재 위치에 대상 데이터가 들어가야 하는 경우 --> 대상 데이터를 넣는다.
							{
								if(types.get(i).intValue() == Column.TYPE_NUMERIC || types.get(i).intValue() == Column.TYPE_BOOLEAN || types.get(i).intValue() == Column.TYPE_INTEGER
										|| types.get(i).intValue() == Column.TYPE_FLOAT) // 숫자, 논리값인 경우 --> 따옴표 없이 넣는다.
								{
									functionTemp = functionTemp + dataCell;
								}
								else functionTemp = functionTemp + "\"" + dataCell + "\""; // 그 외의 경우 --> 따옴표를 붙여 넣는다. (이미 쌍따옴표 캐스팅은 적용된 상태임)
								if(k < functionParams.size() - 1) functionTemp = functionTemp + ","; // 마지막 매개변수가 아닌 경우 쉼표를 붙인다.
							}
							else // 현재 위치가 아닌 경우 --> 매개변수 리스트의 데이터를 넣는다.
							{
								functionTemp = functionTemp + functionParams.get(k);
								if(k < functionParams.size() - 1) functionTemp = functionTemp + ",";
							}
						}
						
						dataCell = functionTemp + ")"; // 괄호를 닫아 변환 함수 형식 완성
					}
					else // 변환 함수는 필요하나 매개변수 필요 없는 경우
					{
						if(types.get(i).intValue() == Column.TYPE_NUMERIC || types.get(i).intValue() == Column.TYPE_BOOLEAN || types.get(i).intValue() == Column.TYPE_INTEGER
								|| types.get(i).intValue() == Column.TYPE_FLOAT) // 숫자, 논리값인 경우 --> 따옴표 없이 넣는다.
						{
							dataCell = functionData.get(columnName.get(i)) + "(" + dataCell + ")";
						}
						else dataCell = functionData.get(columnName.get(i)) + "(\"" + dataCell + "\")"; // 그 외의 경우 --> 따옴표를 붙여 넣는다. (이미 쌍따옴표 캐스팅은 적용된 상태임)
					}
				}
				else dataCell = "\"" + dataCell + "\""; // 변환 함수를 거칠 필요가 없는 경우
			}
			
			sqls = sqls + dataCell;
			
			if(i < datas.size() - 1) sqls = sqls + ", ";
		}
		
		sqls = sqls.trim();
		if(sqls.endsWith(",")) sqls = sqls.substring(0, sqls.length() - 1);
		sqls = sqls + ")";
		return sqls;
	}
	
	@Override
	public String help()
	{
		StringBuffer results = new StringBuffer("");
		
		results = results.append(Manager.applyStringTable("This is record object."));
		results = results.append("getDatas()" + " : " + Manager.applyStringTable("Return all data of this record as list."));
		results = results.append("getData(columnName)" + " : " + Manager.applyStringTable("Return value of columnName column."));
		results = results.append("setData(columnName, newDataValue)" + " : " + Manager.applyStringTable("Modify value of columnName column."));
		results = results.append("type(columnName)" + " : " + Manager.applyStringTable("Return type name of columnName column."));
		results = results.append("types()" + " : " + Manager.applyStringTable("Return type names."));
		results = results.append("setType(columnName, newTypeName)" + " : " + Manager.applyStringTable("Modify type of columnName column."));
		results = results.append("getColumnName(index)" + " : " + Manager.applyStringTable("Return column name of index's column."));
		results = results.append("setColumnName(index, newColumnName)" + " : " + Manager.applyStringTable("Modify column name of index's column."));
		results = results.append("insertIntoDB(dao, tableName, useTransaction)" + " : " + Manager.applyStringTable("Insert this record into DB. useTransaction can be null (default : true)."));
		results = results.append("columnCount()" + " : " + Manager.applyStringTable("Return how many columns."));
		results = results.append("toString()" + " : " + Manager.applyStringTable("Return this record data as JSON form."));
		
		return results.toString();
	}
	
	/**
	 * <p>테이블 데이터를 실제 DB에 넣습니다.</p>
	 * 
	 * <p>컬럼과 레코드를 반복을 돌리며 꺼내 INSERT INTO 구문을 만듭니다. 컬럼 이름을 함수 필요 여부 맵에서 찾아, 있으면 함수를 덮고, 아니면 그대로 데이터를 붙입니다.</p>
	 * <p>레코드 하나에 대한 INSERT INTO 구문이 완성되면 DB에 전송해 실행합니다. 모두 실행되면 COMMIT 합니다.</p>
	 * 
	 * @param dao : DB에 연결된 DAO 객체
	 * @param name : 테이블 이름
	 * @param transaction : true 시 트랜잭션 사용 (성공 시 commit, 실패 시 rollback), null 가능 (기본값은 true)
	 * @exception DBMS와의 문제, 데이터 수 문제 등
	 */
	public void insertIntoDB(Dao dao, String name, Object transaction) throws Exception
	{
		insertIntoDB(dao, name, transaction, new Hashtable<String, String>(), new Hashtable<String, Integer>(), new Hashtable<String, List<String>>());
	}
	
	/**
	 * <p>테이블 데이터를 실제 DB에 넣습니다.</p>
	 * 
	 * <p>컬럼과 레코드를 반복을 돌리며 꺼내 INSERT INTO 구문을 만듭니다. 컬럼 이름을 함수 필요 여부 맵에서 찾아, 있으면 함수를 덮고, 아니면 그대로 데이터를 붙입니다.</p>
	 * <p>레코드 하나에 대한 INSERT INTO 구문이 완성되면 DB에 전송해 실행합니다. 모두 실행되면 COMMIT 합니다.</p>
	 * 
	 * @param dao : DB에 연결된 DAO 객체
	 * @param name : 테이블 이름
	 * @param transaction : true 시 트랜잭션 사용 (성공 시 commit, 실패 시 rollback), null 가능 (기본값은 true)
	 * @param functionData : 타입 별 변환 함수 정보, 없으면 그대로 들어감
	 * @param functionIndexData : 타입 별 변환 함수에서 해당 데이터가 몇 번째 파라미터인지를 지정하는 정보
	 * @param functionOtherParams : 타입 별 변환 함수에서 다른 파라미터 데이터들 정보
	 * @exception DBMS와의 문제, 데이터 수 문제 등
	 */
	public void insertIntoDB(Dao dao, String name, Object transaction, Map<String, String> functionData, Map<String, Integer> functionIndexData, Map<String, List<String>> functionOtherParams) throws Exception
	{
		// 트랜잭션 사용여부 확인
		boolean useTransaction = true;
		
		if(transaction != null)
		{
			try
			{
				useTransaction = DataUtil.parseBoolean(transaction);
			}
			catch (InvalidInputException e)
			{
				Main.logError(e, Manager.applyStringTable("On insertIntoDB, insert") + " " + this.toString());
				return;
			}
		}
		
		// 날짜 형식 생략 여부 설정 확인
		boolean skipDataForm = false;
		try
		{
			skipDataForm = DataUtil.parseBoolean(Manager.getOption("skip_date_form"));
		}
		catch (InvalidInputException e2)
		{
			Main.logError(e2, Manager.applyStringTable("On checking skip date-form column option"));
			skipDataForm = false;
		}
		
		String sqls = toInsertSQL(name, transaction, new Boolean(skipDataForm), functionData, functionIndexData, functionOtherParams);
		
		try
		{	
			dao.query(sqls);
			if(useTransaction) dao.commit();
		}
		catch (Exception e)
		{
			try
			{
				if(useTransaction) dao.rollback();
			}
			catch(Throwable e1)
			{
				
			}
			if(e instanceof SQLException)
			{
				throw new SQLException(Manager.applyStringTable("Error") + " " + e.getMessage() 
						+ " " + Manager.applyStringTable("on trying to execute query") + " : " + sqls);
			}
			else if(e instanceof IOException)
			{
				throw new IOException(Manager.applyStringTable("Error") + " " + e.getMessage() 
						+ " " + Manager.applyStringTable("on trying to execute query") + " : " + sqls);
			}
			else
			{
				throw new Exception(Manager.applyStringTable("Error") + " " + e.getMessage() 
						+ " " + Manager.applyStringTable("on trying to execute query") + " : " + sqls);
			}
				
		}
	}
	
	/**
	 * <p>컬럼 타입 이름들을 순서대로 반환합니다.</p>
	 * 
	 * @return 컬럼 타입들
	 */
	public List<String> types()
	{
		List<String> typeData = new Vector<String>();
		for(int i=0; i<this.types.size(); i++)
		{
			if(Column.TYPE_BLANK == this.types.get(i).intValue())
			{
				typeData.add("BLANK");
			}
			else if(Column.TYPE_BOOLEAN == this.types.get(i).intValue())
			{
				typeData.add("BOOLEAN");
			}
			else if(Column.TYPE_DATE == this.types.get(i).intValue())
			{
				typeData.add("DATE");
			}
			else if(Column.TYPE_ERROR == this.types.get(i).intValue())
			{
				typeData.add("ERROR");
			}
			else if(Column.TYPE_FORMULA == this.types.get(i).intValue())
			{
				typeData.add("FORMULA");
			}
			else if(Column.TYPE_NUMERIC == this.types.get(i).intValue())
			{
				typeData.add("NUMERIC");
			}
			else if(Column.TYPE_INTEGER == this.types.get(i).intValue())
			{
				typeData.add("INTEGER");
			}
			else if(Column.TYPE_FLOAT == this.types.get(i).intValue())
			{
				typeData.add("FLOAT");
			}
			else if(Column.TYPE_STRING == this.types.get(i).intValue())
			{
				typeData.add("STRING");
			}
			else
			{
				typeData.add("BLANK");
			}
		}
		return typeData;
	}
	/**
	 * <p>컬럼 갯수를 반환합니다.</p>
	 * 
	 * @return 컬럼 갯수
	 */
	public int columnCount()
	{
		return datas.size();
	}
	
	/**
	 * <p>HGF 형태로 변환합니다.</p>
	 * 
	 * @return HGF 텍스트
	 */
	public String toHGF()
	{
		StringBuffer results = new StringBuffer("");
		
		results = results.append("$");
		for(int i=0; i<datas.size(); i++)
		{
			results = results.append("\"");
			results = results.append(DataUtil.castTotal(true, String.valueOf(datas.get(i))));
			results = results.append("\"");
			if(i < datas.size() - 1) results = results.append(";");
		}
		
		return results.toString();
	}
	/**
	 * <p>레코드 데이터를 JSON 형태의 텍스트를 반환합니다.</p>
	 * 
	 * @return JSON 텍스트
	 */
	public String toJSON()
	{
		return toString();
	}
	/**
	 * <p>레코드 데이터를 JSON 형태의 텍스트를 반환합니다.</p>
	 * 
	 * @param details : true 시 컬럼 이름, 타입 정보가 포함됩니다.
	 * @return JSON 텍스트
	 */
	public String toJSON(boolean details)
	{
		return toJSON(details, 0);
	}
	/**
	 * <p>레코드 데이터를 JSON 형태의 텍스트를 반환합니다.</p>
	 * 
	 * @param details : true 시 컬럼 이름, 타입 정보가 포함됩니다.
	 * @param privSpaces : 양수 입력 시 그 수만큼 각 줄에 공백이 포함됩니다.
	 * @return JSON 텍스트
	 */
	public String toJSON(boolean details, int privSpaces)
	{
		if(details)
		{
			StringBuffer results;
			String spaces = "";
			
			for(int i=0; i<privSpaces; i++)
			{
				spaces = spaces + " ";
			}
			
			results = new StringBuffer(spaces + "[\n");
			
			List<String> typeNames = types();
			for(int i=0; i<datas.size(); i++)
			{	
				results = results.append(spaces);
				results = results.append(" {\n");
				results = results.append(spaces);
				results = results.append("   name : \"" + DataUtil.castTotal(true, columnName.get(i)) + "\",\n");
				results = results.append(spaces);
				results = results.append("   data : \"" + DataUtil.castTotal(true,  String.valueOf(datas.get(i))) + "\",\n");
				results = results.append(spaces);
				results = results.append("   type : \"" + DataUtil.castTotal(true,  String.valueOf(typeNames.get(i))) + "\"\n");
				results = results.append(spaces);
				results = results.append(" }\n");
				results = results.append(spaces);
				if(i < datas.size() - 1) results = results.append(", ");
			}
			results = results.append(spaces);
			
			return results.toString() + "]";
		}
		else return toString();
	}
	
	@Override
	public String toString()
	{
		StringBuffer results = new StringBuffer("{\n");
		
		for(int i=0; i<datas.size(); i++)
		{
			results = results.append(columnName.get(i) + " : \"" + DataUtil.castTotal(true, String.valueOf(datas.get(i))) + "\"");
			if(i < datas.size() - 1) results = results.append(", \n");
		}
		
		return results.toString() + "}";
	}
	@Override
	public void noMoreUse()
	{
		
	}
	
	@Override
	public boolean isAlive()
	{
		return true;
	}
	
	@Override
	public int compareTo(Record o)
	{
		if(DataUtil.isEmpty(getTargetColumnToCompare())) return 0;
		
		if(getColumnName().contains(getTargetColumnToCompare()))
		{
			Object hereData = getData(getTargetColumnToCompare());
			Object otherData = o.getData(getTargetColumnToCompare());
			
			if((hereData instanceof Number) && (otherData instanceof Number))
			{
				return (int) (((Number) hereData).doubleValue() - ((Number) otherData).doubleValue());
			}
			else
			{
				return String.valueOf(hereData).compareTo(String.valueOf(otherData));
			}
		}
		
		return 0;
	}

	public String getTargetColumnToCompare()
	{
		return targetColumnToCompare;
	}

	public void setTargetColumnToCompare(String targetColumnToCompare)
	{
		this.targetColumnToCompare = targetColumnToCompare;
	}
	
	/**
	 * <p>내용이 없는 레코드인지 여부를 검사합니다.</p>
	 * 
	 * @return 비어 있을 경우 true 반환
	 */
	public boolean isEmpty()
	{
		return DataUtil.isEmpty(datas);
	}



	@Override
	public int size()
	{
		return getColumnName().size();
	}



	@Override
	public boolean containsKey(Object key)
	{
		return getColumnName().contains(key);
	}



	@Override
	public boolean containsValue(Object value)
	{
		return getDatas().contains(value);
	}



	@Override
	public Object get(Object key)
	{
		int index = -1;
		for(int i=0; i<getColumnName().size(); i++)
		{
			if(getColumnName().get(i).equals(key))
			{
				index = i;
				break;
			}
		}
		Object value = getDataOf(index);
		if(getTypes().get(index).intValue() == Column.TYPE_STRING)
		{
			return String.valueOf(value);
		}
		else if(getTypes().get(index).intValue() == Column.TYPE_BLANK)
		{
			return null;
		}
		else if(getTypes().get(index).intValue() == Column.TYPE_BOOLEAN && (! (value instanceof Boolean)))
		{
			return new Boolean(DataUtil.parseBoolean(value));
		}
		else if(getTypes().get(index).intValue() == Column.TYPE_INTEGER && (! ((value instanceof Integer) || (value instanceof BigInteger))))
		{
			return new BigInteger(String.valueOf(value));
		}
		else if((getTypes().get(index).intValue() == Column.TYPE_NUMERIC || getTypes().get(index).intValue() == Column.TYPE_FLOAT) 
				&& (! ((value instanceof Float) || (value instanceof Double)|| (value instanceof BigDecimal))))
		{
			return new BigDecimal(String.valueOf(value));
		}
		return String.valueOf(value);
	}



	@Override
	public Object put(String key, Object value)
	{
		getColumnName().add(key);
		getDatas().add(value);
		if(value instanceof String) getTypes().add(new Integer(Column.TYPE_STRING));
		else if((value instanceof Integer) || (value instanceof BigInteger)) getTypes().add(new Integer(Column.TYPE_INTEGER));
		else if((value instanceof Float) || (value instanceof Double) || (value instanceof BigDecimal)) getTypes().add(new Integer(Column.TYPE_FLOAT));
		else if(value instanceof Boolean) getTypes().add(new Integer(Column.TYPE_BOOLEAN));
		else if(value instanceof Date) getTypes().add(new Integer(Column.TYPE_DATE));
		return value;
	}



	@Override
	public String remove(Object key)
	{
		String befores = String.valueOf(getData(key));
		int location = -1;
		for(int i=0; i<getColumnName().size(); i++)
		{
			if(getColumnName().get(i).equals(key))
			{
				location = i;
				break;
			}
		}
		if(location >= 0)
		{
			getColumnName().remove(location);
			getDataOf(location);
			getTypes().remove(location);
			return befores;
		}
		return null;
	}



	@Override
	public void putAll(Map<? extends String, ?> m)
	{
		Set<? extends String> keys = m.keySet();
		for(String k : keys)
		{
			put(k, m.get(k));
		}
	}



	@Override
	public void clear()
	{
		getTypes().clear();
		getColumnName().clear();
		getDatas().clear();
	}



	@Override
	public Set<String> keySet()
	{
		Set<String> results = new HashSet<String>();
		results.addAll(getColumnName());
		return results;
	}



	@Override
	public Collection<Object> values()
	{
		Collection<Object> newCol = new Vector<Object>();
		((Vector<Object>) newCol).addAll(getDatas());
		return newCol;
	}



	@Override
	public Set<java.util.Map.Entry<String, Object>> entrySet()
	{
		Map<String, Object> resultMap = new Hashtable<String, Object>();
		for(String k : getColumnName())
		{
			resultMap.put(k, getData(k));
		}
		return resultMap.entrySet();
	}
	
	/**
	 * <p>두 번째 타입까지 문자열인 맵 객체를 반환합니다.</p>
	 * 
	 * @return 문자열 타입 맵 객체
	 */
	public Map<String, String> toStringMap()
	{
		Map<String, String> newMap = new Hashtable<String, String>();
		Set<String> keys = keySet();
		for(String key : keys)
		{
			newMap.put(key, String.valueOf(get(key)));
		}
		return newMap;
	}
}
