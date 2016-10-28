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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import hjow.hgtable.Main;
import hjow.hgtable.Manager;
import hjow.hgtable.dao.Dao;
import hjow.hgtable.streamchain.ChainInputStream;
import hjow.hgtable.util.AnalyzeUtil;
import hjow.hgtable.util.DataUtil;

// TODO 구현 필요

/**
 * <p>메모리 사용량을 최소화하기 위해 파일 시스템을 사용하는 테이블 셋입니다.</p>
 * <p>미완성</p>
 * 
 * @author HJOW
 *
 */
public class FileTableSet extends AbstractTableSet
{
	private static final long serialVersionUID = -8273752738080692876L;
	protected File tableSetFile;
	
	/**
	 * <p>기본 생성자입니다.</p>
	 * 
	 */
	public FileTableSet()
	{
		
	}
	
	/**
	 * <p>대상 파일을 지정해 테이블 셋 객체를 만듭니다.</p>
	 * 
	 * @param file : 파일 객체
	 * @throws FileNotFoundException : 해당 파일이 존재하지 않는 경우
	 */
	public FileTableSet(File file) throws FileNotFoundException
	{
		if(! file.exists()) throw new FileNotFoundException(Manager.applyStringTable("Cannot create the table set"));
		this.tableSetFile = file;
	}

	@Override
	public String getName()
	{
		FileInputStream fileStream = null;
		ChainInputStream chainStream = null;
		InputStreamReader readerStream = null;
		BufferedReader reader = null;
		String name = null;
		
		try
		{
			fileStream = new FileInputStream(tableSetFile);
			chainStream = new ChainInputStream(fileStream);
			readerStream = new InputStreamReader(chainStream.getInputStream(), Manager.getOption("file_charset"));
			reader = new BufferedReader(readerStream);
			
			String lines = null;
			while(true)
			{
				lines = reader.readLine();
				if(lines == null) break;
				if(DataUtil.isEmpty(lines)) continue;
				lines = lines.trim();
				
				if(lines.endsWith(";")) lines = lines.substring(0, lines.length() - 1).trim();
				
				if(lines.startsWith("@"))
				{
					name = DataUtil.removeQuote(lines.substring(1));
				}
			}
		}
		catch(Throwable e)
		{
			
		}
		finally
		{
			try
			{
				reader.close();
			}
			catch(Throwable e)
			{
				
			}
			try
			{
				readerStream.close();
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
		return name;
	}

	@Override
	public List<Column> getColumns()
	{
		FileInputStream fileStream = null;
		ChainInputStream chainStream = null;
		InputStreamReader readerStream = null;
		BufferedReader reader = null;
		List<Column> columns = new Vector<Column>();
		
		try
		{
			fileStream = new FileInputStream(tableSetFile);
			chainStream = new ChainInputStream(fileStream);
			readerStream = new InputStreamReader(chainStream.getInputStream(), Manager.getOption("file_charset"));
			reader = new BufferedReader(readerStream);
			
			String lines = null;
			StringTokenizer semicolonTokenizer = null;
			String columnBlock = null;
			String[] colonSplit = null;
			
			String columnName;
			String columnType;
			
			Column newColumn;
			
			int targetColumn;
			
			while(true)
			{
				lines = reader.readLine();
				if(lines == null) break;
				if(DataUtil.isEmpty(lines)) continue;
				lines = lines.trim();
				
				if(lines.startsWith("#")) continue;
				else if(lines.startsWith("%"))
				{
					semicolonTokenizer = new StringTokenizer(lines.substring(1).trim(), ";");
					while(semicolonTokenizer.hasMoreTokens())
					{
						columnBlock = semicolonTokenizer.nextToken().trim();
						colonSplit = columnBlock.split(":");
						
						columnName = colonSplit[0].trim();
						columnType = colonSplit[1].trim();
						
						columnName = DataUtil.removeQuote(columnName);
						columnType = DataUtil.removeQuote(columnType);
						
						newColumn = new Column();
						newColumn.setName(columnName);
						
						if(DataUtil.isInteger(columnType))
						{
							newColumn.setType(Integer.parseInt(columnType));
						}
						else newColumn.setType(columnType);
						
						columns.add(newColumn);
					}
				}
				else if(lines.startsWith("$"))
				{
					semicolonTokenizer = new StringTokenizer(lines.substring(1).trim(), ";");
					targetColumn = 0;
					while(semicolonTokenizer.hasMoreTokens())
					{
						columns.get(targetColumn).getData().add(DataUtil.removeQuote(semicolonTokenizer.nextToken()));
						targetColumn++;
						if(targetColumn >= columns.size()) break;
					}
				}
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
				readerStream.close();
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
			
			return columns;
		}
		catch(Throwable e)
		{
			Main.logError(e, Manager.applyStringTable("On getColumns from file tableset"));
		}
		finally
		{
			try
			{
				reader.close();
			}
			catch(Throwable e)
			{
				
			}
			try
			{
				readerStream.close();
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
		
		return null;
	}

	@Override
	public String getData(int col, int row)
	{
		FileInputStream fileStream = null;
		ChainInputStream chainStream = null;
		InputStreamReader readerStream = null;
		BufferedReader reader = null;
		
		String lines;
		StringTokenizer semicolonTokenizer;
		String gets;
		String results = null;
		
		int rowIndex = 0;
		
		try
		{
			fileStream = new FileInputStream(tableSetFile);
			chainStream = new ChainInputStream(fileStream);
			readerStream = new InputStreamReader(chainStream.getInputStream(), Manager.getOption("file_charset"));
			reader = new BufferedReader(readerStream);
			
			while(true)
			{
				lines = reader.readLine();
				if(lines == null) break;
				if(DataUtil.isEmpty(lines)) continue;
				
				lines = lines.trim();
				
				if(lines.startsWith("#")) continue;
				else if(lines.startsWith("$"))
				{
					semicolonTokenizer = new StringTokenizer(lines.substring(1).trim(), ";");
					
					for(int i=0; i<col; i++)
					{
						gets = semicolonTokenizer.nextToken();
						if(rowIndex == row && i == col)
						{
							results = DataUtil.removeQuote(gets);
							break;
						}
					}
				}
				
				rowIndex++;
			}
		}
		catch(Throwable e)
		{
			Main.logError(e, Manager.applyStringTable("On getData from file tableset"));
		}
		finally
		{
			try
			{
				reader.close();
			}
			catch(Throwable e)
			{
				
			}
			try
			{
				readerStream.close();
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
		// TODO Auto-generated method stub
		return results;
	}

	@Override
	public int getRecordCount()
	{
		FileInputStream fileStream = null;
		ChainInputStream chainStream = null;
		InputStreamReader readerStream = null;
		BufferedReader reader = null;
		
		try
		{
			fileStream = new FileInputStream(tableSetFile);
			chainStream = new ChainInputStream(fileStream);
			readerStream = new InputStreamReader(chainStream.getInputStream(), Manager.getOption("file_charset"));
			reader = new BufferedReader(readerStream);
			
			// TODO
		}
		catch(Throwable e)
		{
			Main.logError(e, Manager.applyStringTable("On getRecordCount from file tableset"));
		}
		finally
		{
			try
			{
				reader.close();
			}
			catch(Throwable e)
			{
				
			}
			try
			{
				readerStream.close();
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
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void insertIntoDB(Dao dao)
	{
		FileInputStream fileStream = null;
		ChainInputStream chainStream = null;
		InputStreamReader readerStream = null;
		BufferedReader reader = null;
		
		try
		{
			fileStream = new FileInputStream(tableSetFile);
			chainStream = new ChainInputStream(fileStream);
			readerStream = new InputStreamReader(chainStream.getInputStream(), Manager.getOption("file_charset"));
			reader = new BufferedReader(readerStream);
			
			
		}
		catch(Throwable e)
		{
			
		}
		finally
		{
			try
			{
				reader.close();
			}
			catch(Throwable e)
			{
				
			}
			try
			{
				readerStream.close();
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void insertIntoDB(Dao dao, Map<String, String> params)
	{
		FileInputStream fileStream = null;
		ChainInputStream chainStream = null;
		InputStreamReader readerStream = null;
		BufferedReader reader = null;
		
		try
		{
			fileStream = new FileInputStream(tableSetFile);
			chainStream = new ChainInputStream(fileStream);
			readerStream = new InputStreamReader(chainStream.getInputStream(), Manager.getOption("file_charset"));
			reader = new BufferedReader(readerStream);
			
			
		}
		catch(Throwable e)
		{
			
		}
		finally
		{
			try
			{
				reader.close();
			}
			catch(Throwable e)
			{
				
			}
			try
			{
				readerStream.close();
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void insertIntoDB(Dao dao, Map<String, String> params,
			Map<String, String> columnSurroundFunctions,
			Map<String, Integer> columnSurroundFunctionParamIndex,
			Map<String, List<String>> columnSurroundFunctionParams)
	{
		FileInputStream fileStream = null;
		ChainInputStream chainStream = null;
		InputStreamReader readerStream = null;
		BufferedReader reader = null;
		
		try
		{
			fileStream = new FileInputStream(tableSetFile);
			chainStream = new ChainInputStream(fileStream);
			readerStream = new InputStreamReader(chainStream.getInputStream(), Manager.getOption("file_charset"));
			reader = new BufferedReader(readerStream);
			
			
		}
		catch(Throwable e)
		{
			
		}
		finally
		{
			try
			{
				reader.close();
			}
			catch(Throwable e)
			{
				
			}
			try
			{
				readerStream.close();
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public String toInsertSQL()
	{
		FileInputStream fileStream = null;
		ChainInputStream chainStream = null;
		InputStreamReader readerStream = null;
		BufferedReader reader = null;
		
		try
		{
			fileStream = new FileInputStream(tableSetFile);
			chainStream = new ChainInputStream(fileStream);
			readerStream = new InputStreamReader(chainStream.getInputStream(), Manager.getOption("file_charset"));
			reader = new BufferedReader(readerStream);
			
			
		}
		catch(Throwable e)
		{
			
		}
		finally
		{
			try
			{
				reader.close();
			}
			catch(Throwable e)
			{
				
			}
			try
			{
				readerStream.close();
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TableSet subTable(int startRecordIndex, int endRecordIndex)
	{
		FileInputStream fileStream = null;
		ChainInputStream chainStream = null;
		InputStreamReader readerStream = null;
		BufferedReader reader = null;
		
		try
		{
			fileStream = new FileInputStream(tableSetFile);
			chainStream = new ChainInputStream(fileStream);
			readerStream = new InputStreamReader(chainStream.getInputStream(), Manager.getOption("file_charset"));
			reader = new BufferedReader(readerStream);
			
			
		}
		catch(Throwable e)
		{
			
		}
		finally
		{
			try
			{
				reader.close();
			}
			catch(Throwable e)
			{
				
			}
			try
			{
				readerStream.close();
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TableSet subTable(int startColumnIndex, int endColumnIndex,
			int startRecordIndex, int endRecordIndex)
	{
		FileInputStream fileStream = null;
		ChainInputStream chainStream = null;
		InputStreamReader readerStream = null;
		BufferedReader reader = null;
		
		try
		{
			fileStream = new FileInputStream(tableSetFile);
			chainStream = new ChainInputStream(fileStream);
			readerStream = new InputStreamReader(chainStream.getInputStream(), Manager.getOption("file_charset"));
			reader = new BufferedReader(readerStream);
			
			
		}
		catch(Throwable e)
		{
			
		}
		finally
		{
			try
			{
				reader.close();
			}
			catch(Throwable e)
			{
				
			}
			try
			{
				readerStream.close();
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Column getColumn(int index)
	{
		FileInputStream fileStream = null;
		ChainInputStream chainStream = null;
		InputStreamReader readerStream = null;
		BufferedReader reader = null;
		
		try
		{
			fileStream = new FileInputStream(tableSetFile);
			chainStream = new ChainInputStream(fileStream);
			readerStream = new InputStreamReader(chainStream.getInputStream(), Manager.getOption("file_charset"));
			reader = new BufferedReader(readerStream);
			
			
		}
		catch(Throwable e)
		{
			
		}
		finally
		{
			try
			{
				reader.close();
			}
			catch(Throwable e)
			{
				
			}
			try
			{
				readerStream.close();
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Column getColumn(String name)
	{
		FileInputStream fileStream = null;
		ChainInputStream chainStream = null;
		InputStreamReader readerStream = null;
		BufferedReader reader = null;
		
		try
		{
			fileStream = new FileInputStream(tableSetFile);
			chainStream = new ChainInputStream(fileStream);
			readerStream = new InputStreamReader(chainStream.getInputStream(), Manager.getOption("file_charset"));
			reader = new BufferedReader(readerStream);
			
			
		}
		catch(Throwable e)
		{
			
		}
		finally
		{
			try
			{
				reader.close();
			}
			catch(Throwable e)
			{
				
			}
			try
			{
				readerStream.close();
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Record getRecord(int index)
	{
		FileInputStream fileStream = null;
		ChainInputStream chainStream = null;
		InputStreamReader readerStream = null;
		BufferedReader reader = null;
		
		try
		{
			fileStream = new FileInputStream(tableSetFile);
			chainStream = new ChainInputStream(fileStream);
			readerStream = new InputStreamReader(chainStream.getInputStream(), Manager.getOption("file_charset"));
			reader = new BufferedReader(readerStream);
			
			
		}
		catch(Throwable e)
		{
			
		}
		finally
		{
			try
			{
				reader.close();
			}
			catch(Throwable e)
			{
				
			}
			try
			{
				readerStream.close();
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Record> toRecordList()
	{
		FileInputStream fileStream = null;
		ChainInputStream chainStream = null;
		InputStreamReader readerStream = null;
		BufferedReader reader = null;
		
		try
		{
			fileStream = new FileInputStream(tableSetFile);
			chainStream = new ChainInputStream(fileStream);
			readerStream = new InputStreamReader(chainStream.getInputStream(), Manager.getOption("file_charset"));
			reader = new BufferedReader(readerStream);
			
			
		}
		catch(Throwable e)
		{
			
		}
		finally
		{
			try
			{
				reader.close();
			}
			catch(Throwable e)
			{
				
			}
			try
			{
				readerStream.close();
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toJSON(boolean basedRecord)
	{
		FileInputStream fileStream = null;
		ChainInputStream chainStream = null;
		InputStreamReader readerStream = null;
		BufferedReader reader = null;
		
		try
		{
			fileStream = new FileInputStream(tableSetFile);
			chainStream = new ChainInputStream(fileStream);
			readerStream = new InputStreamReader(chainStream.getInputStream(), Manager.getOption("file_charset"));
			reader = new BufferedReader(readerStream);
			
			
		}
		catch(Throwable e)
		{
			
		}
		finally
		{
			try
			{
				reader.close();
			}
			catch(Throwable e)
			{
				
			}
			try
			{
				readerStream.close();
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toHGF()
	{
		FileInputStream fileStream = null;
		ChainInputStream chainStream = null;
		InputStreamReader readerStream = null;
		BufferedReader reader = null;
		
		try
		{
			fileStream = new FileInputStream(tableSetFile);
			chainStream = new ChainInputStream(fileStream);
			readerStream = new InputStreamReader(chainStream.getInputStream(), Manager.getOption("file_charset"));
			reader = new BufferedReader(readerStream);
			
			
		}
		catch(Throwable e)
		{
			
		}
		finally
		{
			try
			{
				reader.close();
			}
			catch(Throwable e)
			{
				
			}
			try
			{
				readerStream.close();
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCreateTableSQL(boolean useVarchar2,
			Map<String, String> options)
	{
		FileInputStream fileStream = null;
		ChainInputStream chainStream = null;
		InputStreamReader readerStream = null;
		BufferedReader reader = null;
		
		try
		{
			fileStream = new FileInputStream(tableSetFile);
			chainStream = new ChainInputStream(fileStream);
			readerStream = new InputStreamReader(chainStream.getInputStream(), Manager.getOption("file_charset"));
			reader = new BufferedReader(readerStream);
			
			
		}
		catch(Throwable e)
		{
			
		}
		finally
		{
			try
			{
				reader.close();
			}
			catch(Throwable e)
			{
				
			}
			try
			{
				readerStream.close();
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
		// TODO Auto-generated method stub
		return null;
	}

	public File getTableSetFile()
	{
		return tableSetFile;
	}

	public void setTableSetFile(File tableSetFile)
	{
		this.tableSetFile = tableSetFile;
	}
	@Override
	public boolean isAlive()
	{
		return tableSetFile != null && tableSetFile.exists();
	}


	@Override
	public TableSet analyze(String analyzeFunction,
			Map<String, String> arguments)
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
	public String getColumnName(int index)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getColumnType(int index)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getColumnTypeName(int index)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getColumnCount()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setColumns(List<Column> columns)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setData(int col, int row, String data)
	{
		// TODO Auto-generated method stub
		
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
		// TODO Auto-generated method stub
		
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
	public String help()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void noMoreUse()
	{
		// TODO Auto-generated method stub
		
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
