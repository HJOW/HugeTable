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

package hjow.hgtable.util;

import hjow.dbtool.common.DBTool;
import hjow.hgtable.Main;
import hjow.hgtable.Manager;
import hjow.hgtable.NotConnectedException;
import hjow.hgtable.dao.Dao;
import hjow.hgtable.dao.JdbcDao;
import hjow.hgtable.streamchain.ChainInputStream;
import hjow.hgtable.streamchain.ChainOutputStream;
import hjow.hgtable.util.StreamUtil;
import hjow.hgtable.tableset.Column;
import hjow.hgtable.tableset.Record;
import hjow.hgtable.tableset.TableSet;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * <p>이 클래스에는 DB와 파일에 동시에 액세스하여 대용량 데이터 처리가 가능한 정적 메소드들이 있습니다.</p>
 * 
 * @author HJOW
 *
 */
public class DirectIOUtil
{
	public static final int TYPE_JSON = 0;
	public static final int TYPE_INSERT = 1;
	public static final int TYPE_XLSX = 2;
	
	/**
	 * <p>HGF 형식의 텍스트 파일을 해석해 DB에 삽입합니다. 규격에 맞아야 합니다. 일정량 단위로 작업을 하므로 내용 전체를 메모리에 올릴 필요는 없습니다.</p>
	 * 
	 * @param dao : DB 접속된 DAO 객체
	 * @param target : 불러올 파일에 해당하는 파일 객체
	 * @throws Exception 파일 액세스 문제, 네트워크 문제, DBMS에서 발생하는 문제
	 */
	public static void hgfToDb(Dao dao, File target) throws Exception
	{
		hgfToDb(dao, target, new Hashtable<String, String>(), new Hashtable<String, Integer>(), new Hashtable<String, List<String>>());
	}
	
	/**
	 * <p>HGF 형식의 텍스트 파일을 해석해 DB에 삽입합니다. 규격에 맞아야 합니다. 일정량 단위로 작업을 하므로 내용 전체를 메모리에 올릴 필요는 없습니다.</p>
	 * 
	 * @param dao : DB 접속된 DAO 객체
	 * @param target : 불러올 파일에 해당하는 파일 객체
	 * @param functionData : 타입 별 변환 함수 정보, 없으면 그대로 들어감
	 * @param functionIndexData : 타입 별 변환 함수에서 해당 데이터가 몇 번째 파라미터인지를 지정하는 정보
	 * @param functionOtherParams : 타입 별 변환 함수에서 다른 파라미터 데이터들 정보
	 * @throws Exception 파일 액세스 문제, 네트워크 문제, DBMS에서 발생하는 문제
	 */
	public static void hgfToDb(Dao dao, File target, Map<String, String> functionData, Map<String, Integer> functionIndexData, Map<String, List<String>> functionOtherParams) throws Exception
	{
		if(! dao.isAlive()) // 접속 여부 확인
		{
			throw new NotConnectedException(Manager.applyStringTable("There is no connection, or connection was closed before."));
		}
		
		// TODO
		
		Main.setPercent(0);
		int percentValue = 0;
		long totalSize = target.length();
		long finishedSize = 0L;
		
		FileInputStream fileStream = null;
		ChainInputStream chainStream = null;
		InputStreamReader reader = null;
		BufferedReader buffered = null;
		
		String tableName = null;
		String data = "";
		String selectedColumn = null;
		char inQuote = ' ';
		Column newColumn;
		List<Column> columeDatas = new Vector<Column>();
		
		try
		{
			fileStream = new FileInputStream(target);
			chainStream = new ChainInputStream(fileStream);
			StreamUtil.additionalSetting(chainStream);
			
			String defaultCharset = "UTF-8";
			if(Manager.getOption("file_charset") != null) defaultCharset = Manager.getOption("file_charset");
			
			reader = new InputStreamReader(chainStream.getInputStream(), defaultCharset);
			buffered = new BufferedReader(reader);
			
			String readLines = null;
			while(true)
			{
				readLines = buffered.readLine();
				if(readLines == null) break;
				
				finishedSize = readLines.getBytes().length;
				percentValue = (int)((((double) finishedSize)/((double) totalSize)) * 100.0);
				if(percentValue > 100) percentValue = 100;
				Main.setPercent(percentValue);
				
				readLines = readLines.trim();
				
				if(readLines.startsWith("#"))
				{
					continue;
				}
				else if(readLines.startsWith("@"))
				{
					tableName = readLines.substring(1);
					if(tableName.endsWith(";")) tableName = tableName.substring(0, tableName.length() - 1);
					if(tableName.startsWith("\""))
					{
						tableName = DataUtil.reCastTotal(true, tableName.substring(1, tableName.length() - 1));
					}
					else if(tableName.startsWith("'"))
					{
						tableName = DataUtil.reCastTotal(false, tableName.substring(1, tableName.length() - 1));
					}
				}
				else if(readLines.startsWith("%"))
				{
					String columns = readLines.substring(1).trim();
					char[] columnsChar = columns.toCharArray();
					inQuote = ' ';
					data = "";
					selectedColumn = null;
					for(int i=0; i<columnsChar.length; i++)
					{										
						if(inQuote == '"') // 따옴표 안에 있는 경우
						{
							if(columnsChar[i] == '"') // 따옴표를 만나면
							{
								inQuote = ' '; // 따옴표 밖으로 벗어남
								if(i == columnsChar.length - 1) // 마지막 글자인 경우
								{
									if(selectedColumn != null) // 컬럼 이름이 선택된 경우 --> 마지막 글자이므로 새 컬럼 추가
									{
										newColumn = new Column();
										newColumn.setName(selectedColumn);
										newColumn.setType(DataUtil.reCastTotal(true, data));
										columeDatas.add(newColumn);
										data = "";
										selectedColumn = null;
									}
								}
							}
							else // 그 외의 글자를 만나면
							{
								data = data + String.valueOf(columnsChar[i]);
							}
						}
						else // 따옴표 바깥에 있는 경우
						{
							if(columnsChar[i] == ' ') continue; // 공백이면 넘어감
							else if(columnsChar[i] == '"') // 따옴표를 만난 경우
							{
								inQuote = '"'; // 따옴표 안으로 진입
								data = "";
							}
							else if(columnsChar[i] == ':') // : 기호를 만나면, 이전까지 만난 글자들을 컬럼 이름으로 선택
							{
								selectedColumn = DataUtil.reCastTotal(true, data);
								data = "";
							}
							else if(columnsChar[i] == ';') // ; 기호를 만나면, 선택된 컬럼 이름에 이전까지 만난 글자들을 데이터 타입으로 하여 새 컬럼으로 추가
							{
								if(selectedColumn != null)
								{
									newColumn = new Column();
									newColumn.setName(selectedColumn);
									newColumn.setType(DataUtil.reCastTotal(true, data));
									columeDatas.add(newColumn);
									data = "";
									selectedColumn = null;
								}
							}
							else // 그 외의 글자를 만나면
							{
								data = data + String.valueOf(columnsChar[i]);
							}
						}
					}
				}
				else if(readLines.startsWith("$"))
				{
					String recordData = readLines.substring(1).trim();
					char[] recordChar = recordData.toCharArray();
					
					data = "";
					List<String> recordDatas = new Vector<String>();
					List<String> columnNames = new Vector<String>();
					List<Integer> typeDatas = new Vector<Integer>();
					
					for(int i=0; i<recordChar.length; i++)
					{
						if(inQuote == '"') // 따옴표 안에 있는 경우
						{
							if(recordChar[i] == '"')
							{
								inQuote = ' ';
								if(i == recordChar.length - 1) // 마지막 글자인 경우
								{
									recordDatas.add(DataUtil.reCastTotal(true, data));
									data = "";
								}
							}
							else
							{
								data = data + String.valueOf(recordChar[i]);
							}
						}
						else // 따옴표 바깥에 있는 경우
						{
							if(recordChar[i] == ' ') continue; // 공백이면 넘어감
							else if(recordChar[i] == '"')
							{
								inQuote = '"';
								data = "";
							}
							else if(recordChar[i] == ';')
							{
								recordDatas.add(DataUtil.reCastTotal(true, data));
								data = "";
							}
							else
							{
								data = data + String.valueOf(recordChar[i]);
							}
						}
					}
					
					Record newRecord = new Record();
					
					newRecord.setDatas(new Vector<Object>());
					for(int i=0; i<recordDatas.size(); i++)
					{
						newRecord.getDatas().add(recordDatas.get(i));
					}
					
					for(int i=0; i<columeDatas.size(); i++)
					{
						columnNames.add(columeDatas.get(i).getName());
						typeDatas.add(new Integer(columeDatas.get(i).getType()));
					}
					
					newRecord.setColumnName(columnNames);
					newRecord.setTypes(typeDatas);
					
					newRecord.insertIntoDB(dao, tableName, "false", functionData, functionIndexData, functionOtherParams);
				}
				
				if(! (Main.checkInterrupt(DirectIOUtil.class, "hgfToDb")))
				{
					break;
				}
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
			Main.logError(e, Manager.applyStringTable("On hgfToDb") + " " + Manager.applyStringTable("from") + " " + String.valueOf(target) + " "
					+ Manager.applyStringTable("to") + " " + String.valueOf(dao));
		}
		finally
		{
			try
			{
				buffered.close();
			}
			catch(Throwable e)
			{
				
			}
			try
			{
				reader.close();
			}
			catch(Throwable e)
			{
				
			}
			try
			{
				chainStream.close();
			}
			catch(Throwable e)
			{
				
			}
			try
			{
				fileStream.close();
			}
			catch(Throwable e)
			{
				
			}
		}
		
		Main.setPercent(100);
	}
	
	/**
	 * <p>DB에서 값을 가져와 HGF 형식의 파일로 저장합니다. 일정량 단위로 작업을 하므로 내용 전체를 메모리에 올릴 필요는 없습니다.</p>
	 * 
	 * @param name : 테이블 이름
	 * @param dao : DB 접속된 DAO 객체
	 * @param target : 저장할 파일 경로에 해당하는 파일 객체
	 * @param selectQuery : SQL 형식의 문장, DB에서 조회 시 사용
	 * @param additionalWheres : 추가 조건문 (null 사용 가능)
	 * @throws Exception 파일 액세스 문제, 네트워크 문제, DBMS에서 발생하는 문제
	 */
	public static void dbToHgf(String name, Dao dao, File target, String selectQuery, String additionalWheres) throws Exception
	{
		dbTo(name, dao, target, selectQuery, additionalWheres, null, "HGF");
	}
	
	/**
	 * <p>DB에서 값을 가져와 JSON 형식의 파일로 저장합니다. 일정량 단위로 작업을 하므로 내용 전체를 메모리에 올릴 필요는 없습니다.</p>
	 * 
	 * @param name : 테이블 이름
	 * @param dao : DB 접속된 DAO 객체
	 * @param target : 저장할 파일 경로에 해당하는 파일 객체
	 * @param selectQuery : SQL 형식의 문장, DB에서 조회 시 사용
	 * @param additionalWheres : 추가 조건문 (null 사용 가능)
	 * @param separatesRecord : true 시 테이블 셋이 아닌 레코드들의 배열 형태로 저장 (null 시 기본값은 false)
	 * @throws Exception 파일 액세스 문제, 네트워크 문제, DBMS에서 발생하는 문제
	 */
	public static void dbToJson(String name, Dao dao, File target, String selectQuery, String additionalWheres, Object separatesRecord) throws Exception
	{
		dbTo(name, dao, target, selectQuery, additionalWheres, separatesRecord, "JSON");
	}
	
	/**
	 * <p>DB에서 값을 가져와 지정한 형식의 파일로 저장합니다. 일정량 단위로 작업을 하므로 내용 전체를 메모리에 올릴 필요는 없습니다.</p>
	 * 
	 * @param name : 테이블 이름
	 * @param dao : DB 접속된 DAO 객체
	 * @param target : 저장할 파일 경로에 해당하는 파일 객체
	 * @param selectQuery : SQL 형식의 문장, DB에서 조회 시 사용
	 * @param additionalWheres : 추가 조건문 (null 사용 가능)
	 * @param separatesRecord : true 시 테이블 셋이 아닌 레코드들의 배열 형태로 저장 (null 시 기본값은 false)
	 * @throws Exception 파일 액세스 문제, 네트워크 문제, DBMS에서 발생하는 문제
	 */
	public static void dbTo(String name, Dao dao, File target, String selectQuery, String additionalWheres, Object separatesRecord, String textForm) throws Exception
	{
		
		try
		{
			if(! dao.isAlive()) // 접속 여부 확인
			{
				throw new NotConnectedException(Manager.applyStringTable("There is no connection, or connection was closed before."));
			}
			
			String forms = textForm;
			if(forms == null) forms = "HGF";
			if(forms.equalsIgnoreCase("JSON")) forms = "JSON";
			else if(forms.equalsIgnoreCase("HGF")) forms = "HGF";
			else throw new InvalidInputException(Manager.applyStringTable("On dbTo, invalid textForm") + " : " + textForm);
			
			Main.setPercent(0);
			int percentValue = 0;
			
			if(dao instanceof JdbcDao)
			{
				String rownumberKeyword = null;
				String rownumberAlias = null;
				
				Integer stepSize = new Integer(100); // 단계별 레코드 갯수 기본값 100으로 설정
				if(Manager.getOption("step_size") != null) // step_size 환경설정 값 확인
				{
					stepSize = new Integer(Manager.getOption("step_size")); // step_size 값을 단계별 레코드 갯수로 지정
				}
				if(stepSize != null && stepSize.intValue() <= 0) stepSize = null; // step_size 값이 0 이하이면 단계별 나누어 가져오기 기능 끄기
				
				int finished = 0;
				
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
				if(Manager.getOption("step_keyword_alias") != null)
				{
					rownumberAlias = Manager.getOption("step_keyword_alias");
				}
				else
				{
					if(rownumberKeyword != null) rownumberAlias = "ROWNUM_FOR_HG";
				}
				
				if(rownumberKeyword == null)
				{
					stepSize = null;
				}
				
				String countQuery = "SELECT COUNT(*) FROM ( " + selectQuery + " )";
				if(additionalWheres != null) countQuery = countQuery + " WHERE " + additionalWheres;
				
				TableSet countSet = dao.query(countQuery);
				if(countSet == null) throw new NullPointerException(Manager.applyStringTable("There is no result of count query") + " : " + countQuery);
				
				int rowCounts = 0;
				
				try
				{
					rowCounts = Integer.parseInt(countSet.getColumns().get(0).getData().get(0));
				}
				catch(Throwable e)
				{
					rowCounts = (int) Double.parseDouble(countSet.getColumns().get(0).getData().get(0));
				}
				
				Main.log(Manager.applyStringTable("Counts") + " : " + rowCounts);
				
				String getQuery;
				
				FileOutputStream fileStream = null;
				ChainOutputStream chainStream = null;
				OutputStreamWriter writer = null;
				BufferedWriter buffered = null;
				
				TableSet takeResults;
				
				try
				{
					fileStream = new FileOutputStream(target);
					chainStream = new ChainOutputStream(fileStream);
					StreamUtil.additionalSetting(chainStream);
					
					writer = new OutputStreamWriter(chainStream.getOutputStream(), Manager.getOption("file_charset"));
					buffered = new BufferedWriter(writer);
					
					StringTokenizer lineTokenizer = null;
					
					if(textForm.equalsIgnoreCase("JSON"))
					{
						if(separatesRecord == null || (! DataUtil.parseBoolean(separatesRecord)))
						{
							buffered.write("{");
							buffered.newLine();
							buffered.write("  name : " + "'" + name + "'");
							buffered.newLine();
							buffered.write("   ,");
							buffered.newLine();
							buffered.write("  records : ");
							buffered.newLine();
							buffered.write("    [");
							buffered.newLine();
						}
						else
						{
							buffered.write("[");
							buffered.newLine();
						}
					}
					
					boolean isFirstLoop = true;
					
					int beforeFinished = 0;
					boolean skipFirst = true; 
					long roopCount = 0;
					
					while(finished < rowCounts)
					{
						beforeFinished = finished;
						roopCount++;
						
						percentValue = (int) Math.round((((double) finished) / ((double) rowCounts)) * 100.0);
						Main.setPercent(percentValue);
						
						getQuery = "SELECT * FROM (" + selectQuery + ") A";
						
						if(stepSize != null)
						{
							int withSteps = finished + stepSize.intValue();
							if(withSteps > rowCounts) withSteps = rowCounts;
							
							getQuery = "SELECT * FROM (SELECT " + rownumberKeyword + " AS " + rownumberAlias + ", A.* FROM (" + getQuery + ") A) B";
							
							getQuery = getQuery + " WHERE " + "B." + rownumberAlias + " >= " + finished + " AND " + "B." + rownumberAlias + " < " + String.valueOf(withSteps);
							
							if(additionalWheres != null)
							{
								getQuery = getQuery + " AND " + additionalWheres;
							}
						}
						else if(additionalWheres != null)
						{
							getQuery = getQuery + " WHERE " + additionalWheres;
						}
						
						takeResults = dao.query(getQuery);
						List<Record> records = takeResults.toRecordList();
						
						if(textForm.equalsIgnoreCase("HGF"))
						{
							if(isFirstLoop)
							{
								takeResults.setName(name);
								if(stepSize != null) takeResults.removeColumn(rownumberAlias);
								lineTokenizer = new StringTokenizer(takeResults.toHGF(), "\n");
								while(lineTokenizer.hasMoreTokens())
								{
									buffered.write(lineTokenizer.nextToken());
									buffered.newLine();
								}
							}
						}
						
						for(int i=0; i<records.size(); i++)
						{
							if(textForm.equalsIgnoreCase("JSON"))
							{
								lineTokenizer = new StringTokenizer(records.get(i).toJSON(true), "\n");
								while(lineTokenizer.hasMoreTokens())
								{
									buffered.write(lineTokenizer.nextToken());
									buffered.newLine();
								}
							}
							else if(textForm.equalsIgnoreCase("HGF"))
							{
								if(! isFirstLoop)
								{
									buffered.write(records.get(i).toHGF());
									buffered.newLine();
								}
							}
							
							finished++;
							
							
							if(i < records.size() - 1)
							{
								if(textForm.equalsIgnoreCase("JSON"))
								{
									buffered.write(",");
									buffered.newLine();
								}
							}
						}
						
						if(beforeFinished == finished)
						{
							if(skipFirst)
							{
								skipFirst = false;
							}
							else
							{
								String whyProblem = "";
								
								whyProblem = whyProblem + "TEXTFORM : " + textForm + "\n";
								whyProblem = whyProblem + "ROOPS : " + String.valueOf(roopCount) + "\n";
								whyProblem = whyProblem + "STEPS : " + String.valueOf(stepSize) + "\n";
								whyProblem = whyProblem + "FINISHED : " + String.valueOf(finished) + "\n";
								whyProblem = whyProblem + "ROWCOUNT : " + String.valueOf(rowCounts) + "\n";
								whyProblem = whyProblem + "LAST_QUERY COUNT : " + String.valueOf(records.size()) + "\n";
								whyProblem = whyProblem + "LAST_QUERY...\n" + getQuery + "\n...";
								
								throw new Exception(Manager.applyStringTable("On DB --> file") + "...\n" + whyProblem);
							}
						}
						
						isFirstLoop = false;
						takeResults.noMoreUse();
						takeResults = null;
						System.gc();
						
						if(! (Main.checkInterrupt(DirectIOUtil.class, "DBto")))
						{
							throw new Exception(Manager.applyStringTable("Interrupted"));
						}
					}
					
					if(textForm.equalsIgnoreCase("JSON"))
					{
						if(separatesRecord == null || (! DataUtil.parseBoolean(separatesRecord)))
						{
							buffered.write("    ]");
							buffered.newLine();
							buffered.write("}");
						}
						else
						{
							buffered.write("]");
						}
					}
				}
				catch(Throwable e)
				{
					Main.logError(e, Manager.applyStringTable("On dbToJson Util"));
				}
				finally
				{
					try
					{
						buffered.close();
					}
					catch(Throwable e)
					{
						
					}
					try
					{
						writer.close();
					}
					catch(Throwable e)
					{
						
					}
					try
					{
						chainStream.close();
					}
					catch(Throwable e)
					{
						
					}
					try
					{
						fileStream.close();
					}
					catch(Throwable e)
					{
						
					}
				}
				
				Main.setPercent(100);
			}
		}
		catch(Exception e)
		{
			throw e;
		}
	}
}
