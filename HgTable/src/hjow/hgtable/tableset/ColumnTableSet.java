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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import hjow.dbtool.common.DBTool;
import hjow.hgtable.IncludesException;
import hjow.hgtable.Main;
import hjow.hgtable.Manager;
import hjow.hgtable.NotConnectedException;
import hjow.hgtable.dao.Dao;
import hjow.hgtable.dao.JdbcDao;
import hjow.hgtable.util.DataUtil;
import hjow.hgtable.util.InvalidInputException;
import hjow.hgtable.util.StreamUtil;


/**
 * <p>테이블 객체이며 데이터들을 포함합니다.</p>
 * 
 * <p>이름을 가지고 있으며, 컬럼 데이터를 여럿 가지고 있습니다.</p>
 * <p>각각의 컬럼은 해당 컬럼의 이름과 데이터 타입, 그리고 데이터들을 가집니다.</p>
 * 
 * <p>표 형태로 모식도를 그렸을 때, 세로줄을 여럿 포함한 형태라고 보시면 됩니다. 각 컬럼들은 데이터 갯수가 모두 동일해야 합니다.</p>
 * 
 * @author HJOW
 *
 */
public abstract class ColumnTableSet extends AbstractTableSet
{
	private static final long serialVersionUID = 6183558891609057771L;
	protected String name;	
	protected List<Column> columns = new Vector<Column>();
	
	/**
	 * <p>기본 생성자입니다. 이 생성자에서는 아무 동작도 하지 않습니다.</p>
	 * 
	 */
	public ColumnTableSet()
	{
		super();
	}
	
	/**
	 * <p>이름과 컬럼 데이터를 집어넣어 객체를 생성할 수 있는 생성자입니다. TableSet 객체 복사에 사용됩니다.</p>
	 * <p>객체 복사를 위해서는 매개변수에 넣기 전 별도로 복사 작업을 해야 합니다.</p>
	 * 
	 * @param name : 테이블 이름
	 * @param columns : 컬럼들(데이터 포함)
	 */
	public ColumnTableSet(String name, List<Column> columns)
	{
		super();
		this.name = name;
		this.columns = columns;
	}
	
	/**
	 * <p>DB로부터 데이터를 받아 와 객체를 생성합니다. SELECT 구문과 WHERE 절을 명시할 수 있습니다.</p>
	 * 
	 * <p>Oracle DB를 사용하는 경우 한번에 전체를 가져오지 않고 단계별로 나누어 가져올 수 있습니다.</p>
	 * <p>Oracle DB가 아닌 경우, WHERE 절에 레코드 번호를 가져올 수 있는 키워드를 환경설정 step_keyword 에 명시하면 단계별로 나누어 가져올 수 있습니다.</p>
	 * <p>단계별로 나누어 가져오는 경우 한 단계에서 환경설정의 step_size 값 만큼씩을 가져옵니다. 지정되지 않은 경우 기본값은 100입니다.</p>
	 * <p>Oracle DB를 사용하면서 나누어 가져오고 싶지 않다면 step_size 값에 0을 지정하면 됩니다.</p>
	 * 
	 * @param name : 테이블 이름
	 * @param dao : DB에 접속한 DAO 객체 (반드시 DB에 접속되어 있어야 함)
	 * @param selectQuery : SELECT 구문의 SQL 스크립트
	 * @param additionalWheres : 추가 WHERE 절
	 * @throws Exception : DB에 접속하지 않았거나 DBMS에서 발생한 오류, 혹은 네트워크 문제
	 */
	public ColumnTableSet(String name, Dao dao, String selectQuery, String additionalWheres) throws Exception
	{
		super();
		setData(name, dao, selectQuery, additionalWheres);
	}
	
	public ColumnTableSet(String name, Dao dao, String additionalWheres) throws Exception
	{
		super();
		addData(name, dao, additionalWheres);
	}
	
	public ColumnTableSet(String name, ResultSet data) throws Exception
	{		
		super();
		addData(data);
	}
	
	@Override
	public void addData(TableSet tableSet) throws Exception
	{
		if(tableSet == null) return;
		
		for(int i=0; i<tableSet.getColumns().size(); i++)
		{
			if(getColumn(tableSet.getColumn(i).getName()) == null) throw new NoColumnException(Manager.applyStringTable("On addData, there is no column which name is") 
					+ " : " + tableSet.getColumn(i).getName());
			if(getColumn(tableSet.getColumn(i).getName()).getType() == tableSet.getColumn(i).getType())
			{
				throw new CannotMatchTypeException(Manager.applyStringTable("On addData, cannot match type") 
						+ " : " + getColumn(tableSet.getColumn(i).getName()).getType()
						+ " " + Manager.applyStringTable("and") + " " + tableSet.getColumn(i).getType());
			}
		}
		
		for(int i=0; i<tableSet.getColumns().size(); i++)
		{
			getColumn(tableSet.getColumn(i).getName()).getData().addAll(tableSet.getColumn(i).getData());
		}
	}
	
	@Override
	public void addData(ResultSet data) throws Exception
	{
		ResultSetMetaData metaData = data.getMetaData();
		int typeVal;
		
		Set<String> tableNames = new HashSet<String>();
		String name = "RESULT";
				
		String metaTableName;
		for(int i=0; i<metaData.getColumnCount(); i++)
		{
			typeVal = metaData.getColumnType(i + 1);
			if(DataUtil.isEmpty(getColumn(metaData.getColumnLabel(i + 1)))) columns.add(new Column(metaData.getColumnLabel(i + 1), typeVal));
			
			metaTableName = metaData.getTableName(i + 1);
			if(DataUtil.isNotEmpty(metaTableName)) tableNames.add(metaTableName);
		}
		
		int j = 0;
		for(String s : tableNames)
		{
			if(j == 0) name = s;
			else name = name + "~" + s;
			
			j++;
		}
		setName(name);
		
		try
		{
			data.beforeFirst();
		}
		catch(Throwable e)
		{
			
		}
		
		String dataVal = null;
		int dataIndex = 0;
		while(data.next())
		{
			for(int i=0; i<columns.size(); i++)
			{
				try
				{
					switch(columns.get(i).getType())
					{
					case Types.VARCHAR:
						dataVal = data.getString(columns.get(i).getName());
						break;
					case Types.CHAR:
						dataVal = data.getString(columns.get(i).getName());
						break;
					case Types.CLOB:
						dataVal = data.getString(columns.get(i).getName());
						break;
					case Types.NUMERIC:
						dataVal = String.valueOf(data.getDouble(columns.get(i).getName()));
						break;
					case Types.DOUBLE:
						dataVal = String.valueOf(data.getDouble(columns.get(i).getName()));
						break;
					case Types.FLOAT:
						dataVal = String.valueOf(data.getDouble(columns.get(i).getName()));
						break;
					case Types.INTEGER:
						dataVal = String.valueOf(data.getDouble(columns.get(i).getName()));
						break;
					case Types.BIGINT:
						dataVal = String.valueOf(data.getLong(columns.get(i).getName()));
						break;
					case Types.DATE:
						Date date = data.getDate(columns.get(i).getName());
						SimpleDateFormat formatter = new SimpleDateFormat(Manager.getOption("defaultDateFormat"));
						dataVal = formatter.format(date);
						break;
					case Types.BOOLEAN:
						dataVal = String.valueOf(data.getBoolean(columns.get(i).getName()));
						break;
					case Types.BLOB:
						Blob blob = data.getBlob(columns.get(i).getName());
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
					case Types.JAVA_OBJECT:
						dataVal = DataUtil.base64(StreamUtil.toBytes(data.getObject(columns.get(i).getName())));
						break;
					case Types.NULL:
						dataVal = "";
						break;
					default:
						dataVal = data.getString(columns.get(i).getName());
						break;
					}
					columns.get(i).getData().add(dataVal);
				}
				catch(Throwable e)
				{
					e.printStackTrace();
					Main.logError(e, Manager.applyStringTable("Error occured while loading from DB") + ", " + dataIndex + ", " + i);
				}
			}
			dataIndex++;
		}
		for(int i=0; i<columns.size(); i++)
		{
			columns.get(i).switchTypeCodeToSheet();
		}
	}
	/**
	 * <p>DB로부터 데이터를 받아 옵니다. SELECT 구문과 WHERE 절을 명시할 수 있습니다.</p>
	 * <p>주의 : 이 메소드는 기존 객체가 가진 데이터를 전부 제거합니다. 기존 데이터와 병합하려면 별도의 변수에 데이터나 참조값을 옮겨 놓은 후 사용하십시오.</p>
	 * 
	 * <p>Oracle DB를 사용하는 경우 한번에 전체를 가져오지 않고 단계별로 나누어 가져올 수 있습니다.</p>
	 * <p>Oracle DB가 아닌 경우, WHERE 절에 레코드 번호를 가져올 수 있는 키워드를 환경설정 step_keyword 에 명시하면 단계별로 나누어 가져올 수 있습니다.</p>
	 * <p>단계별로 나누어 가져오는 경우 한 단계에서 환경설정의 step_size 값 만큼씩을 가져옵니다. 지정되지 않은 경우 기본값은 100입니다.</p>
	 * <p>Oracle DB를 사용하면서 나누어 가져오고 싶지 않다면 step_size 값에 0을 지정하면 됩니다.</p>
	 * 
	 * @param name : 테이블 이름
	 * @param dao : DB에 접속한 DAO 객체 (반드시 DB에 접속되어 있어야 함)
	 * @param selectQuery : SELECT 구문의 SQL 스크립트
	 * @param additionalWheres : 추가 WHERE 절
	 * @throws Exception : DB에 접속하지 않았거나 DBMS에서 발생한 오류, 혹은 네트워크 문제
	 */
	public void setData(String name, Dao dao, String selectQuery, String additionalWheres) throws Exception
	{
		if(! dao.isAlive()) // 접속 여부 확인
		{
			throw new NotConnectedException(Manager.applyStringTable("There is no connection, or connection was closed before."));
		}
		
		this.setColumns(new Vector<Column>());
		System.gc();
						
		Main.setPercent(0);
		int percentValue = 0;
		
		if(dao instanceof JdbcDao)
		{
			String rownumberKeyword = null;
			
			Integer stepSize = new Integer(100); // 단계별 레코드 갯수 기본값 100으로 설정
			if(Manager.getOption("step_size") != null) // step_size 환경설정 값 확인
			{
				stepSize = new Integer(Manager.getOption("step_size")); // step_size 값을 단계별 레코드 갯수로 지정
			}
			if(stepSize != null && stepSize.intValue() <= 0) stepSize = null; // step_size 값이 0 이하이면 단계별 나누어 가져오기 기능 끄기
			
			DBTool dbTool = ((JdbcDao) dao).getDBTool();
			
			if(dbTool != null)
			{
				if(dbTool.getRownumName() != null)
				{
					rownumberKeyword = dbTool.getRownumName();				
				}
			}
			if(Manager.getOption("step_keyword") != null)
			{
				rownumberKeyword = Manager.getOption("step_keyword");
			}
			
			if(rownumberKeyword == null)
			{
				stepSize = null;
			}
			
			String countQuery = "SELECT COUNT(*) FROM ( " + selectQuery + " )";
			if(additionalWheres != null) countQuery = countQuery + " WHERE " + additionalWheres;
			
			TableSet countSet = dao.query(countQuery);
			if(countSet == null) throw new NullPointerException(Manager.applyStringTable("There is no result of count query") + " : " + countQuery);
			
			int rowCounts = -1;
			
			try
			{
				rowCounts = Integer.parseInt(countSet.getColumns().get(0).getData().get(0));
			}
			catch(NumberFormatException e)
			{
				rowCounts = (int) Double.parseDouble(countSet.getColumns().get(0).getData().get(0));
			}
			
			Main.log(Manager.applyStringTable("Counts") + " : " + rowCounts);
			
			String getQuery;
			TableSet results;
			
			if(stepSize == null)
			{
				getQuery = "SELECT * FROM ( " + selectQuery + " )";
				if(additionalWheres != null) getQuery = getQuery + " WHERE " + additionalWheres;
				
				results = new DefaultTableSet(name, ((JdbcDao) dao).rawQuery(selectQuery));
				setColumns(results.getColumns());
				setName(results.getName());
			}
			else
			{
				getQuery = "SELECT * FROM (" + selectQuery + ")";
				if(additionalWheres != null)
				{
					getQuery = getQuery + " WHERE " + additionalWheres + " AND ";
				}
				else
				{
					getQuery = getQuery + " WHERE ";
				}
				
				int minRange = 1;
				int maxRange = stepSize.intValue();		
				boolean columnCheck = false;
				String getQueryWithStep = null;
				boolean whileSwitch = true;
				
				while(whileSwitch)
				{
					getQueryWithStep = getQuery + rownumberKeyword + ">=" + String.valueOf(minRange) + " AND " + rownumberKeyword + "<" + maxRange;
					Main.log(Manager.applyStringTable("Following query will be run") + "...\n" + getQueryWithStep + "\n");
					
					results = new DefaultTableSet(name, ((JdbcDao) dao).rawQuery(getQueryWithStep));
					
					for(int i=0; i<results.getColumns().size(); i++)
					{
						columnCheck = false;
						for(int j=0; j<columns.size(); j++)
						{
							if(results.getColumns().get(i).getName().equals(columns.get(j).getName()))
							{
								columnCheck = true;
							}
						}
						if(! columnCheck)
						{
							Column newColumn = new Column(results.getColumns().get(i).getName(), results.getColumns().get(i).getType());
							columns.add(newColumn);
						}
						
						for(int j=0; j<columns.size(); j++)
						{
							if(results.getColumns().get(i).getName().equals(columns.get(j).getName()))
							{
								columns.get(j).getData().addAll(results.getColumns().get(i).getData());
								if(results.getColumns().get(i).getData().size() < stepSize.intValue())
								{
									whileSwitch = false;
								}
							}
						}
					}
					
					percentValue = (int) Math.round((((double) getRecordCount()) / ((double)rowCounts)) * 100.0);
					if(percentValue > 100) percentValue = 100;
					Main.setPercent(percentValue);
					
					minRange = minRange + stepSize.intValue();
					maxRange = maxRange + stepSize.intValue();
					
					if(! (Main.checkInterrupt(this, "setData")))
					{
						break;
					}
				}			
			}
		}
		
		
		Main.setPercent(100);
	}
	@Override
	public void removeEmptyColumn(boolean exceptWhenNameExists)
	{
		int i=0;
		
		while(i < columns.size())
		{
			if(columns.get(i).isEmpty())
			{
				if(exceptWhenNameExists && DataUtil.isNotEmpty(columns.get(i).getName()))
				{
					i++;
				}
				else
				{
					columns.remove(i);
					i = 0;
				}
			}
			else i++;
			
			if(! (Main.checkInterrupt(this, "removeEmptyColumn")))
			{
				break;
			}
		}
	}
	@Override
	public void removeEmptyRow()
	{
		int i=0;
		while(i < getRecordCount())
		{
			boolean isEmpty = true;
			for(int j=0; j<columns.size(); j++)
			{
				if(DataUtil.isNotEmpty(columns.get(j).getData(i)))
				{
					isEmpty = false;
					break;
				}
			}
			
			if(isEmpty)
			{
				for(int j=0; j<columns.size(); j++)
				{
					columns.get(j).getData().remove(i);
				}
				i = 0;
			}
			else i++;
			
			if(! (Main.checkInterrupt(this, "removeEmptyRow")))
			{
				break;
			}
		}
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
	public List<Column> getColumns()
	{
		return columns;
	}
	@Override
	public void setColumns(List<Column> columns)
	{
		this.columns = columns;
	}
	@Override
	public int getRecordCount()
	{
		if(columns.size() <= 0) return 0;
		return getColumn(0).getData().size();
	}
	
	/**
	 * <p>테이블 데이터를 실제 DB에 넣습니다.</p>
	 * 
	 * <p>컬럼과 레코드를 반복을 돌리며 꺼내 INSERT INTO 구문을 만듭니다. 컬럼 이름을 함수 필요 여부 맵에서 찾아, 있으면 함수를 덮고, 아니면 그대로 데이터를 붙입니다.</p>
	 * <p>레코드 하나에 대한 INSERT INTO 구문이 완성되면 DB에 전송해 실행합니다. 모두 실행되면 COMMIT 합니다.</p>
	 * 
	 * @param dao : DB에 연결된 DAO 객체
	 * @param params : 매개 변수들
	 * @param columnSurroundFunctions : 매개변수의 함수 필요 여부가 담긴 Map
	 * @param columnSurroundFunctionParamIndex : 매개변수의 함수 필요한 경우 몇 번째 파라미터에 데이터가 들어가야 하는지가 담긴 Map
	 * @param columnSurroundFunctionParams : 매개변수의 함수 필요한 경우 다른 파라미터들
	 */
	@Override
	public void insertIntoDB(Dao dao, Map<String, String> params, Map<String, String> columnSurroundFunctions, Map<String, Integer> columnSurroundFunctionParamIndex, Map<String, List<String>> columnSurroundFunctionParams)
	{
		if(dao instanceof JdbcDao) // RDBMS 에 대해서만 완성되어 있음
		{
			StringBuffer results = new StringBuffer("");
			boolean useJDBCInjection = false;
			boolean skipDateForm = false;
			boolean isEmpty = false;
			
			if(params.get("use_jdbc_injection") != null)
			{
				try
				{
					useJDBCInjection = DataUtil.parseBoolean(params.get("use_jdbc_injection"));
				}
				catch (InvalidInputException e)
				{
					
				}
			}
			
			if(params.get("skip_date_form") != null)
			{
				try
				{
					skipDateForm = DataUtil.parseBoolean(params.get("skip_date_form"));
				}
				catch (InvalidInputException e)
				{
					
				}
			}
			
			try
			{
				double processPercents = 0.0;
				
				// TODO : 고도화 필요
				/*
				 실행 과정
				 
				 레코드 하나씩 반복하며 INSERT 문을 만들고 --> 데이터 소스(DB)에 보내 실행
				 
				 INSERT 문 만드는 과정
				 1. INSERT문 앞부분 지정 : "INSERT INTO 이름 ("
				 2. 컬럼 이름 모두 기입
				 3. 데이터 입력 시작 부분 기입 : ") VALUES ("
				 4  데이터가 빈 값(NULL 포함) 검사 (빈 값이면 변환 함수 적용 안 함)
				 5. JDBC 자체 매개변수 삽입 기능 사용여부 분기 (JDBC 자체 기능 사용 시 --> 매개변수 장소에 ? 를 넣은 후 setInt() 등의 메소드 사용)
				 6. 변환 함수 필요 여부 분기 (필요 시 변환 함수 이름과 괄호 사용, TO_CHAR(데이터) 같은 형태)
				 7. 변환 함수에 추가 매개변수가 필요한지의 여부 분기 (변환 함수 내 다른 매개 변수들 필요 여부 검사)
				 8. 실행
				 
				 모두 완료되면 COMMIT 호출
				 실패 시 ROLLBACK 호출
				 
				 */
				
				for(int j=0; j<getRecordCount(); j++) // 레코드 하나씩 반복
				{
					results = results.append("INSERT INTO " + name + " ("); // INSERT 구문 도입, 테이블명 사용
					
					for(int i=0; i<columns.size(); i++) // 컬럼 이름 기입
					{
						results = results.append(columns.get(i).getName());
						if(i < columns.size() - 1) results = results.append(",");
					}
					
					results = results.append(") VALUES ("); // 데이터 입력 부분 시작
					
					List<String> paramAccumList = new Vector<String>();
					List<Integer> paramTypeList = new Vector<Integer>();
					
					for(int i=0; i<columns.size(); i++) // 컬럼 별로 데이터 입력
					{
						if(useJDBCInjection)           // JDBC 자체 지원 매개변수 삽입 기능 사용
						{
							paramAccumList.clear();
							if(columnSurroundFunctions.get(columns.get(i).getName()) != null)   // 덮어야 할 함수가 있는 경우
							{
								results = results.append(columnSurroundFunctions.get(columns.get(i).getName()) + "(");          // 함수 이름과 괄호 열기 기호 추가
								
								if(DataUtil.isNotEmpty(columnSurroundFunctionParams.get(columns.get(i).getName())))  // 이 함수가 다른 파라미터를 가지고 있는 경우
								{
									List<String> paramList = columnSurroundFunctionParams.get(columns.get(i).getName());        // 파라미터 값들을 구분자를 | 로 해서 나눔
									
									for(int k=0; k<paramList.size(); k++)                                                       // 파라미터들 반복
									{
										if(k == columnSurroundFunctionParamIndex.get(columns.get(i).getName()).intValue())       // 데이터가 들어가야 할 곳이면
										{
											results = results.append("?");                                                       // 데이터 삽입 부분 넣기
											paramAccumList.add(columns.get(i).getData().get(j));                                 // 데이터 리스트에 데이터 넣기
											paramTypeList.add(new Integer(columns.get(i).getType()));                            // 데이터 타입 리스트에 타입 정보 넣기
											
											if(k < paramList.size() - 1)                                                         // 마지막 파라미터가 아니면
											{
												results = results.append(",");                                                   // 콤마 삽입
											}
										}
										else
										{
											results = results.append(paramList.get(k));                                          // 파라미터 삽입
											if(k < paramList.size() - 1)                                                         // 마지막 파라미터가 아니면
											{
												results = results.append(",");                                                   // 콤마 삽입
											}
										}
									}
									results = results.append(")");                                                               // 괄호를 닫아 함수 호출부 끝냄
								}
								else                                                                              // 다른 파라미터가 없는 경우
								{
									results = results.append("?)");                                                              // 데이터 삽입부 넣고 바로 괄호 닫음
									paramAccumList.add(columns.get(i).getData().get(j));                                         // 데이터 리스트에 데이터 넣기
									paramTypeList.add(new Integer(columns.get(i).getType()));                                    // 데이터 타입 리스트에 타입 정보 넣기
								}
							}
							else
							{
								results = results.append("?");                                   // 덮어야 할 함수가 없는 경우, 데이터 삽입부 바로 넣기
								paramAccumList.add(columns.get(i).getData().get(j));             // 데이터 리스트에 데이터 넣기
								paramTypeList.add(new Integer(columns.get(i).getType()));        // 데이터 타입 리스트에 타입 정보 넣기
							}
						}
						else                          // 매개변수 직접 삽입
						{
							isEmpty = DataUtil.isEmpty(columns.get(i).getData().get(j));
							
							if(isEmpty)
							{
								results = results.append("NULL");
							}
							else
							{
								if(columnSurroundFunctions.get(columns.get(i).getName()) != null)     // 덮어야 할 함수가 있는 경우
								{
									results = results.append(columnSurroundFunctions.get(columns.get(i).getName()) + "(");          // 함수 이름과 괄호 열기 기호 추가
									
									if(DataUtil.isNotEmpty(columnSurroundFunctionParams.get(columns.get(i).getName())))  // 이 함수가 다른 파라미터를 가지고 있는 경우
									{
										List<String> paramList = columnSurroundFunctionParams.get(columns.get(i).getName());        // 파라미터 값들을 구분자를 | 로 해서 나눔
										
										for(int k=0; k<paramList.size(); k++)                                                       // 파라미터들 반복
										{
											if(k == columnSurroundFunctionParamIndex.get(columns.get(i).getName()).intValue())      // 데이터가 들어가야 할 곳이면
											{
												if(Column.TYPE_NUMERIC == columns.get(i).getType())                                 // 데이터 삽입 부분 넣기
												{
													results = results.append(columns.get(i).getData().get(j));
												}
												else if(Column.TYPE_DATE == columns.get(i).getType())
												{
													if(skipDateForm)
													{
														results = results.append("NULL");
													}
													else results = results.append(columns.get(i).getData().get(j));
												}
												else if(Column.TYPE_BLANK == columns.get(i).getType())
												{
													results = results.append("NULL");
												}
												else
												{
													results = results.append("'" + DataUtil.castQuote(false, columns.get(i).getData().get(j)) + "'");
												}
												
												if(k < paramList.size() - 1)                                                         // 마지막 파라미터가 아니면
												{
													results = results.append(",");                                                   // 콤마 삽입
												}
											}
											else
											{
												results = results.append(paramList.get(k));                                          // 파라미터 삽입
												if(k < paramList.size() - 1)                                                         // 마지막 파라미터가 아니면
												{
													results = results.append(",");                                                   // 콤마 삽입
												}
											}
										}
										results = results.append(")");                                                               // 괄호를 닫아 함수 호출부 끝냄
									}
									else                                                                              // 다른 파라미터가 없는 경우
									{
										if(Column.TYPE_NUMERIC == columns.get(i).getType())                                           // 데이터 삽입부 넣고 바로 괄호 닫음
										{
											results = results.append(columns.get(i).getData().get(j));
										}
										else if(Column.TYPE_DATE == columns.get(i).getType())
										{
											if(skipDateForm)
											{
												results = results.append("NULL");
											}
											else results = results.append(columns.get(i).getData().get(j));
										}
										else if(Column.TYPE_BLANK == columns.get(i).getType())
										{
											results = results.append("NULL");
										}
										else
										{
											results = results.append("'" + DataUtil.castQuote(false, columns.get(i).getData().get(j)) + "'");
										}
										results = results.append(")");
									}
								}
								else                                                                // 덮어야 할 함수가 없는 경우 데이터 넣기
								{
									if(Column.TYPE_NUMERIC == columns.get(i).getType())
									{
										results = results.append(columns.get(i).getData().get(j));
									}
									else if(Column.TYPE_DATE == columns.get(i).getType())
									{
										if(skipDateForm)
										{
											results = results.append("NULL");
										}
										else results = results.append(columns.get(i).getData().get(j));
									}
									else if(Column.TYPE_BLANK == columns.get(i).getType())
									{
										results = results.append("NULL");
									}
									else
									{
										results = results.append("'" + DataUtil.castQuote(false, columns.get(i).getData().get(j)) + "'");
									}		
								}
							}
						}
						if(i < columns.size() - 1) results = results.append(",");					
					}
					results = results.append(")");
									
					if(params.get("print_progress") != null)
					{
						if(params.get("print_progress").equals("Query"))
						{
							Main.log("Trying to insert...");
							Main.log(results.toString());					
						}
						else if(params.get("print_progress").equals("Progress"))
						{
							if(j % 100 == 0) Main.logNotLn(".");
						}
					}
					
					if(useJDBCInjection)           // JDBC 자체 지원 매개변수 삽입 기능 사용한 경우
					{
						((JdbcDao) dao).query(results.toString(), paramAccumList, paramTypeList);
					}
					else                           // 직접 데이터 삽입한 경우
					{
						dao.query(results.toString());
					}
					
					results = new StringBuffer("");
					
					// 몇 퍼센트 완료되었는지 계산
					processPercents = (((double) j) / ((double) columns.get(0).getData().size())) * 100.0;
					Main.setPercent((int) Math.round(processPercents));
				}
				dao.commit();
			}
			catch(Throwable e)
			{
				try
				{
					dao.rollback();
				}
				catch(Throwable e1)
				{
					
				}
				Main.logError(e, "On TableSet --> DB");			
			}	
		}
	}
	
	@Override
	public TableSet subTable(int startRecordIndex, int endRecordIndex)
	{
		return subTable(0, columns.size(), startRecordIndex, endRecordIndex);
	}
	@Override
	public TableSet subTable(int startColumnIndex, int endColumnIndex, int startRecordIndex, int endRecordIndex)
	{
		List<Column> newColumnList = new Vector<Column>();
		int lastColumnIndex, lastRecordIndex;
		lastColumnIndex = endColumnIndex;
		if(lastColumnIndex > columns.size()) lastColumnIndex = columns.size();
		for(int i=startColumnIndex; i<lastColumnIndex; i++)
		{
			Column newColumn = new Column();
			newColumn.setName(columns.get(i).getName());
			newColumn.setType(columns.get(i).getType());
			newColumn.setData(new Vector<String>());
			
			lastRecordIndex = endRecordIndex;
			if(lastRecordIndex > columns.get(i).getData().size()) lastRecordIndex = columns.get(i).getData().size();
			for(int j=startRecordIndex; j<lastRecordIndex; j++)
			{
				newColumn.getData().add(columns.get(i).getData().get(j));
			}
		}
		
		return new DefaultTableSet(getName(), newColumnList);
	}
	@Override
	public Column getColumn(int index)
	{
		try
		{
			return columns.get(index);
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			StringBuffer err = new StringBuffer("");
			for(StackTraceElement errEl : e.getStackTrace())
			{
				err = err.append("\t " + errEl + "\n");
			}
			
			RuntimeException newExc = new ArrayIndexOutOfBoundsException(Manager.applyStringTable("Array index out of range") + ", " 
					+ Manager.applyStringTable("request") + " : " + index + "/" + getColumnCount()
					+ "\n " + Manager.applyStringTable("<-\n") + err
					+ "\n " + Manager.applyStringTable("Original Message") + "..."
					+ e.getMessage() + "\n" + Manager.applyStringTable("End"));
			
			throw newExc;
		}
	}
	@Override
	public Column getColumn(String name)
	{
		for(int i=0; i<columns.size(); i++)
		{
			if(columns.get(i).getName().equals(name)) return columns.get(i);
		}
		return null;
	}
	@Override
	public int getColumnCount()
	{
		return columns.size();
	}
	
	@Override
	public void addData(String[] dataArray)
	{
		for(int i=0; i<getColumnCount(); i++)
		{
			columns.get(i).getData().add(dataArray[i]);
		}
	}
	
	@Override
	public void addData(List<String> dataList)
	{
		for(int i=0; i<getColumnCount(); i++)
		{
			columns.get(i).getData().add(dataList.get(i));
		}
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
	public Record getRecord(int index)
	{
		List<Integer> types = new Vector<Integer>();
		List<String> columnName = new Vector<String>();
		List<Object> rowData = new Vector<Object>();
		Record newRecord = new Record();
		
		for(int i=0; i<columns.size(); i++)
		{
			columnName.add(columns.get(i).getName());
			types.add(new Integer(columns.get(i).getType()));
			for(int j=0; j<columns.get(i).getData().size(); j++)
			{
				if(j == index)
				{
					rowData.add(columns.get(i).getData().get(j));
				}
			}
		}
		
		newRecord.setColumnName(columnName);
		newRecord.setDatas(rowData);
		newRecord.setTypes(types);
		
		return newRecord;
	}
	@Override
	public List<Record> toRecordList()
	{
		List<Record> recordList = new Vector<Record>();
		
		for(int i=0; i<getRecordCount(); i++)
		{
			recordList.add(getRecord(i));
		}
		
		return recordList;
	}
	@Override
	public void setData(int col, int row, String data)
	{
		columns.get(col).getData().set(row, data);
	}
	@Override
	public String getData(int col, int row)
	{
		return columns.get(col).getData(row);
	}
	@Override
	public void noMoreUse()
	{
		columns.clear();
	}
	@Override
	public void removeColumn(String columnName)
	{
		int targets = -1;
		
		if(DataUtil.isEmpty(columnName)) return;
		
		boolean caseAlphabet = false;
		try
		{
			caseAlphabet = DataUtil.parseBoolean("case_columnname");
		}
		catch (InvalidInputException e)
		{
			caseAlphabet = false;
			Main.logError(e, Manager.applyStringTable("On checking case column name option"), true);
		}
		
		for(int i=0; i<columns.size(); i++)
		{
			if(caseAlphabet)
			{
				if(columns.get(i).getName().equals(columnName))
				{
					targets = i;
					break;
				}
			}
			else
			{
				if(columns.get(i).getName().equalsIgnoreCase(columnName))
				{
					targets = i;
					break;
				}
			}
		}
		
		if(targets >= 0)
		{
			columns.remove(targets);
		}
	}
	
	@Override
	public String  getCreateTableSQL(boolean useVarchar2, Map<String, String> options)
	{
		StringBuffer results = new StringBuffer("");
		
		results = results.append("CREATE TABLE " + getName() + "\n(\n");
		for(int i=0; i<columns.size(); i++)
		{
			results = results.append(columns.get(i).getName() + " " + columns.get(i).SQLType(true, useVarchar2));
			if(options != null && DataUtil.isNotEmpty(options.get(columns.get(i).getName())))
			{
				results = results.append(" " + options.get(columns.get(i).getName()));
			}
			if(i < columns.size() - 1) results = results.append("\n, ");
		}
		results = results.append("\n)");
		
		return results.toString();
	}
	@Override
	public boolean isAlive()
	{
		return true;
	}
	
	@Override
	public void addColumn(Column newColumn)
	{
		if(newColumn == null) throw new NullPointerException(Manager.applyStringTable("Cannot add null as new column"));
		if(columns.size() <= 0)
		{
			columns.add(newColumn);
		}
		else
		{
			if(newColumn.size() != getRecordCount())
			{
				throw new InvalidInputException(Manager.applyStringTable("Cannot add new column because of data count difference") + " : "
						+ String.valueOf(getRecordCount() + " " + Manager.applyStringTable("and") + " " + newColumn.size()));
			}
			else
			{
				columns.add(newColumn);
			}
		}
		
	}
	
	@Override
	public String getColumnName(int index)
	{
		return columns.get(index).getName();
	}
	
	@Override
	public int getColumnType(int index)
	{
		return columns.get(index).getType();
	}
	
	@Override
	public String getColumnTypeName(int index)
	{
		return columns.get(index).type();
	}

	@Override
	public void clear()
	{
		for(int i=0; i<getColumnCount(); i++)
		{
			getColumn(i).getData().clear();
		}
	}

	@Override
	public Record set(int index, Record element)
	{
		Record befores = getRecord(index);
		List<String> dataList = new Vector<String>();
		for(int i=0; i<getColumnCount(); i++)
		{
			String value = "";
			for(int j=0; j<element.getColumnName().size(); j++)
			{
				if(getColumn(i).getName().equals(element.getColumnName(new Integer(j))))
				{
					value = String.valueOf(element.getData(String.valueOf(getColumn(i).getName())));
				}
			}
			getColumn(i).getData().add(value);
		}
		for(int i=0; i<getColumnCount(); i++)
		{
			getColumn(i).getData().set(index, dataList.get(i));
		}
		return befores;
	}

	@Override
	public void add(int index, Record element)
	{
		List<String> dataList = new Vector<String>();
		for(int i=0; i<getColumnCount(); i++)
		{
			String value = "";
			for(int j=0; j<element.getColumnName().size(); j++)
			{
				if(getColumn(i).getName().equals(element.getColumnName(new Integer(j))))
				{
					value = String.valueOf(element.getData(String.valueOf(getColumn(i).getName())));
				}
			}
			getColumn(i).getData().add(value);
		}
		for(int i=0; i<getColumnCount(); i++)
		{
			getColumn(i).getData().add(index, dataList.get(i));
		}
	}

	@Override
	public Record remove(int index)
	{
		Record returns = null;
		for(int i=0; i<getColumnCount(); i++)
		{
			returns = getRecord(index);
			getColumn(index).getData().remove(index);
		}
		return returns;
	}

	@Override
	public int lastIndexOf(Object o)
	{
		for(int i=getRecordCount()-1; i>=0; i--)
		{
			if(getRecord(i).equals(o))
			{
				return i;
			}
		}
		return -1;
	}
}
