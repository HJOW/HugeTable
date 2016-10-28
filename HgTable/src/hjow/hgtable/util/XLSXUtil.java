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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Vector;

import hjow.hgtable.Main;
import hjow.hgtable.Manager;
import hjow.hgtable.streamchain.ChainOutputStream;
import hjow.hgtable.tableset.Column;
import hjow.hgtable.tableset.DefaultTableSet;
import hjow.hgtable.tableset.TableSet;
import hjow.hgtable.util.debug.DebuggingUtil;

/**
 * <p>이 클래스에는 XLSX 를 다루는 여러 정적 메소드들이 있습니다.</p>
 * 
 * @author HJOW
 *
 */
public class XLSXUtil
{
	/**
	 * <p>XLSX 파일로부터 테이블 셋 객체를 만듭니다. XLSX 파일 내 첫 번째 시트 데이터만을 꺼냅니다.</p>
	 * 
	 * @param name : 테이블 셋의 새 이름
	 * @param file : XLSX 파일
	 * @return 테이블 셋 객체
	 */
	public static TableSet toTableSet(String name, File file)
	{
		TableSet tableSet = toTableSets(file).get(0);
		tableSet.setName(name);
		return tableSet;
	}
	
	/**
	 * <p>XLSX 파일로부터 테이블 셋 객체들을 만듭니다. 각 시트별로 테이블 셋 객체들이 각각 만들어지며, 각 시트의 이름은 그에 해당하는 테이블 셋 이름이 됩니다.</p>
	 * 
	 * @param file : XLSX 파일
	 * @return 테이블 셋 객체 리스트
	 */
	public static List<TableSet> toTableSets(File file)
	{
		List<TableSet> tableSets = new Vector<TableSet>();
		
		org.apache.poi.ss.usermodel.Workbook workbook = null;
		
		if(file == null) throw new NullPointerException(Manager.applyStringTable("Please select file !!"));
		if(! file.exists()) throw new NullPointerException(Manager.applyStringTable("File") + " " + file.getAbsolutePath() + " " + Manager.applyStringTable("is not exist"));
		
		boolean isHead = true;
		int rowNum = 0;
		int cellNum = 0;
		
		int cellCount = 0;
		
		FileInputStream fileStream = null;
		try
		{
			if(file.getAbsolutePath().endsWith(".xlsx") || file.getAbsolutePath().endsWith(".XLSX"))
			{				
				workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook(file);
			}
			else if(file.getAbsolutePath().endsWith(".xls") || file.getAbsolutePath().endsWith(".XLS"))
			{
				fileStream = new FileInputStream(file);
				workbook = new org.apache.poi.hssf.usermodel.HSSFWorkbook(fileStream);
			}
			
			org.apache.poi.ss.usermodel.FormulaEvaluator evals = workbook.getCreationHelper().createFormulaEvaluator();
			
			org.apache.poi.ss.usermodel.Sheet sheet = null;
			
			for(int x=0; x<workbook.getNumberOfSheets(); x++)
			{
				TableSet newTableSet = new DefaultTableSet();
				newTableSet.setColumns(new Vector<Column>());
				
				sheet = workbook.getSheetAt(x);
				newTableSet.setName(sheet.getSheetName());
				
				rowNum = 0;
				isHead = true;
				
				String targetData = null;
				
				for(org.apache.poi.ss.usermodel.Row row : sheet)
				{
					cellNum = 0;
					for(org.apache.poi.ss.usermodel.Cell cell : row)
					{			
						try
						{
							if(cellNum >= cellCount)
							{
								throw new IndexOutOfBoundsException(Manager.applyStringTable("There are some cells not have their heads") 
										+ ", " + Manager.applyStringTable("Head count") + " : " + cellCount + ", " + Manager.applyStringTable("Cell Number") + " : " + cellNum);
							}
							
							switch(cell.getCellType())
							{
							case org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING:
								if(isHead)
								{
									newTableSet.getColumns().add(new Column(cell.getRichStringCellValue().getString(), Column.TYPE_STRING));
								}
								else
								{
									targetData = cell.getRichStringCellValue().getString();
									newTableSet.getColumns().get(cellNum).setType(cell.getCellType());
								}
								break;
							case org.apache.poi.ss.usermodel.Cell.CELL_TYPE_NUMERIC:
								if(org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell))
								{
									if(isHead)
									{
										newTableSet.getColumns().add(new Column(String.valueOf(cell.getStringCellValue()), Column.TYPE_DATE));
									}
									else
									{
										targetData = String.valueOf(cell.getDateCellValue());
										newTableSet.getColumns().get(cellNum).setType(cell.getCellType());
									}
								}
								else
								{
									if(isHead)
									{
										newTableSet.getColumns().add(new Column(String.valueOf(cell.getStringCellValue()), Column.TYPE_NUMERIC));
									}
									else
									{	
										double values = cell.getNumericCellValue();
										double intPart = values - ((double) ((int) values));
										if(intPart == 0.0)
										{
											targetData = String.valueOf(((int) values));
											newTableSet.getColumns().get(cellNum).setType(Column.TYPE_INTEGER);
										}
										else
										{
											targetData = String.valueOf(values);
											newTableSet.getColumns().get(cellNum).setType(cell.getCellType());
										}
									}
								}
								break;						
							case org.apache.poi.ss.usermodel.Cell.CELL_TYPE_BOOLEAN:
								if(isHead)
								{
									newTableSet.getColumns().add(new Column(String.valueOf(cell.getStringCellValue()), Column.TYPE_BOOLEAN));
								}
								else
								{
									targetData = String.valueOf(cell.getBooleanCellValue());
									newTableSet.getColumns().get(cellNum).setType(cell.getCellType());
								}
								break;
							case org.apache.poi.ss.usermodel.Cell.CELL_TYPE_FORMULA:
								if(isHead)
								{
									newTableSet.getColumns().add(new Column(String.valueOf(cell.getStringCellValue()), Column.TYPE_NUMERIC));
								}
								else
								{
									if(evals.evaluateFormulaCell(cell) == 0)
									{
										targetData = String.valueOf(cell.getNumericCellValue());
										newTableSet.getColumns().get(cellNum).setType(Column.TYPE_NUMERIC);
									}
									else if(evals.evaluateFormulaCell(cell) == 1)
									{
										targetData = String.valueOf(cell.getStringCellValue());
										newTableSet.getColumns().get(cellNum).setType(Column.TYPE_STRING);
									}
									else if(evals.evaluateFormulaCell(cell) == 4)
									{
										targetData = String.valueOf(cell.getBooleanCellValue());
										newTableSet.getColumns().get(cellNum).setType(Column.TYPE_BOOLEAN);
									}
									else
									{
										targetData = String.valueOf(cell.getCellFormula());
										newTableSet.getColumns().get(cellNum).setType(cell.getCellType());
									}
								}
								break;
							case org.apache.poi.ss.usermodel.Cell.CELL_TYPE_BLANK:
								if(isHead)
								{
									newTableSet.getColumns().add(new Column("", Column.TYPE_STRING));
								}
								else
								{
									targetData = "";
									newTableSet.getColumns().get(cellNum).setType(Column.TYPE_BLANK);
								}
								break;
							default:
								if(isHead)
								{
									newTableSet.getColumns().add(new Column("", Column.TYPE_STRING));
								}
								else
								{
									try
									{
										targetData = cell.getStringCellValue();
									}
									catch(Exception e1)
									{
										e1.printStackTrace();
									}
									newTableSet.getColumns().get(cellNum).setType(cell.getCellType());
								}
								break;						
							}
							
							if(isHead)
							{
								cellCount++;
							}
							else
							{
								while(rowNum > 0 && newTableSet.getColumns().get(cellNum).getData().size() < rowNum)
								{
									newTableSet.getColumns().get(cellNum).getData().add("");
								}
								if(targetData != null) newTableSet.getColumns().get(cellNum).getData().add(targetData);
								else
								{
									newTableSet.getColumns().get(cellNum).getData().add("");
								}
							}
						}
						catch(ArrayIndexOutOfBoundsException e1)
						{
							StringBuffer err = new StringBuffer("");
							for(StackTraceElement errEl : e1.getStackTrace())
							{
								err = err.append("\t " + errEl + "\n");
							}
							
							String cellObject = null;
							try
							{
								cellObject = cell.getStringCellValue();
							}
							catch(Exception e2)
							{
								
							}
							
							throw new ArrayIndexOutOfBoundsException(Manager.applyStringTable("Array index out of range")
									+ " <- " + Manager.applyStringTable("Reading xlsx file") + " : " + file.getName()
									+ ", " + sheet.getSheetName()
									+ "\n" + Manager.applyStringTable("On") + " "
									+ Manager.applyStringTable("Row") + " " + rowNum + ", " + Manager.applyStringTable("Cell") + " " + cellNum
									+ ", " + Manager.applyStringTable("Value") + " : " + String.valueOf(cellObject)
									+ "\n " + Manager.applyStringTable("<-\n") + err
									+ "\n " + Manager.applyStringTable("Original Message") + "...\n"
									+ e1.getMessage() + "\n" + Manager.applyStringTable("End"));
						}
						
						cellNum++;
					}
					
					isHead = false;
					rowNum++;
				}
				
				fillTableSet(newTableSet);
				newTableSet.removeEmptyColumn(true);
				
				tableSets.add(newTableSet);
			}
			
			return tableSets;
		}
		catch(Throwable e)
		{
			if(Main.MODE >= DebuggingUtil.DEBUG) e.printStackTrace();
			Main.logError(e, Manager.applyStringTable("On reading xlsx") + " : " + file + "\n"
					+ Manager.applyStringTable("At rownum") + " " + rowNum + ", " + Manager.applyStringTable("cellnum") + " " + cellNum);
			
			return null;
		}
		finally
		{
			try
			{
				workbook.close();
			}
			catch(Throwable e)
			{
				
			}
			try
			{
				if(fileStream != null) fileStream.close();
			}
			catch(Throwable e)
			{
				
			}			
		}
	}
	
	/**
	 * <p>테이블 셋 내 컬럼 갯수가 다른 경우 빈 값을 넣어 갯수를 맞춥니다.</p>
	 * <p>빈 값은 무조건 기존의 데이터 이후에 위치하게 됩니다.</p>
	 * 
	 * @param tableSet : 테이블 셋 객체
	 */
	public static void fillTableSet(TableSet tableSet)
	{
		List<Column> columns = tableSet.getColumns();
		int recordCount = tableSet.getRecordCount();
		
		for(Column c : columns)
		{
			while(c.getData().size() < recordCount)
			{
				c.getData().add("");
			}
		}
	}

	/**
	 * <p>테이블 셋 객체를 XLSX 형식으로 파일로 저장합니다.</p>
	 * 
	 * @param tableSet : 테이블 셋 객체
	 * @param file : 파일 객체
	 */
	public static void save(TableSet tableSet, File file)
	{
		org.apache.poi.ss.usermodel.Workbook workbook = null;
		if(file == null) throw new NullPointerException(Manager.applyStringTable("Please select file !!"));
		
		FileOutputStream fileStream = null;
		ChainOutputStream chainStream = null;
		
		try
		{
			String targetPath = StreamUtil.getDirectoryPathOfFile(file);
			File dir = new File(targetPath);
			// Main.println(dir);
			if(! dir.exists()) dir.mkdir();
		}
		catch(Throwable e)
		{
			Main.logError(e, "On mkdir on saving xlsx");
		}
		
		try
		{
			fileStream = new FileOutputStream(file);
			chainStream = new ChainOutputStream(fileStream);
			StreamUtil.additionalSetting(chainStream);
			
			workbook = new org.apache.poi.xssf.streaming.SXSSFWorkbook();
//			workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook();
			org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet();
			
			org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(0);
			for(int i=0; i<tableSet.getColumns().size(); i++)
			{
				org.apache.poi.ss.usermodel.Cell headerCell = headerRow.createCell(i);
				headerCell.setCellValue(tableSet.getColumns().get(i).getName());
			}
			
			for(int i=0; i<tableSet.getRecordCount(); i++)
			{
				org.apache.poi.ss.usermodel.Row row = sheet.createRow(i + 1);
				for(int j=0; j<tableSet.getColumns().size(); j++)
				{
					org.apache.poi.ss.usermodel.Cell cell = row.createCell(j);
					
					if(Column.TYPE_STRING == tableSet.getColumns().get(j).getType())
					{
						cell.setCellValue(tableSet.getColumns().get(j).getData().get(i));
					}
					else if(Column.TYPE_NUMERIC == tableSet.getColumns().get(j).getType())
					{
						cell.setCellValue(Double.parseDouble(tableSet.getColumns().get(j).getData().get(i)));
					}
					else if(Column.TYPE_DATE == tableSet.getColumns().get(j).getType())
					{
						cell.setCellValue(tableSet.getColumns().get(j).getData().get(i));
					}
					else if(Column.TYPE_BOOLEAN == tableSet.getColumns().get(j).getType())
					{
						cell.setCellValue(DataUtil.parseBoolean(tableSet.getColumns().get(j).getData().get(i)));
					}
					else if(Column.TYPE_BLANK == tableSet.getColumns().get(j).getType())
					{
						cell.setCellValue("");
					}
					else
					{
						cell.setCellValue(tableSet.getColumns().get(j).getData().get(i));
					}
				}
			}
			
			workbook.write(chainStream.getOutputStream());
			workbook.close();
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				workbook.close();
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
	}
}
