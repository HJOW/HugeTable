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

package hjow.libreference.proxy;

import hjow.hgtable.tableset.Column;
import hjow.libreference.HgFormulaEvaluator;
import hjow.libreference.HgRow;
import hjow.libreference.HgSheet;
import hjow.libreference.HgWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Vector;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class PHWorkbook implements HgWorkbook
{
	private static final long serialVersionUID = 8114219948244365191L;
	protected List<HgSheet> sheets = new Vector<HgSheet>();

	public PHWorkbook()
	{
		
	}
	
	public PHWorkbook(String sheetName, FileInputStream stream) throws IOException
	{
		// TODO : 동적 참조로 변환 필요
		HSSFWorkbook workbook = null;
		try
		{
			PSheet newSheet = new PSheet();
			
			workbook = new HSSFWorkbook(stream);
			Sheet sheet = workbook.getSheetAt(0);
			
			for(Row r : sheet)
			{
				HgRow newRow = new PRow();
				
				for(Cell c : r)
				{
					PCell newCell = new PCell();
					
					if(((Cell) c).getCellType() == Column.TYPE_BLANK)
					{
						newCell.setCellValue("");
						newCell.setType(Column.TYPE_BLANK);
					}
					else if(((Cell) c).getCellType() == Column.TYPE_BOOLEAN)
					{
						newCell.setCellValue(((Cell) c).getBooleanCellValue());
						newCell.setType(Column.TYPE_BOOLEAN);
					}
					else if(((Cell) c).getCellType() == Column.TYPE_DATE)
					{
						newCell.setCellValue(((Cell) c).getDateCellValue());
						newCell.setType(Column.TYPE_DATE);
					}
					else if(((Cell) c).getCellType() == Column.TYPE_ERROR)
					{
						newCell.setCellValue("");
						newCell.setType(Column.TYPE_ERROR);
					}
					else if(((Cell) c).getCellType() == Column.TYPE_FLOAT)
					{
						newCell.setCellValue(((Cell) c).getNumericCellValue());
						newCell.setType(Column.TYPE_FLOAT);
					}
					else if(((Cell) c).getCellType() == Column.TYPE_NUMERIC)
					{
						newCell.setCellValue(((Cell) c).getNumericCellValue());
						newCell.setType(Column.TYPE_NUMERIC);
					}
					else if(((Cell) c).getCellType() == Column.TYPE_FORMULA)
					{
						newCell.setCellValue(((Cell) c).getCellFormula());
						newCell.setType(Column.TYPE_FORMULA);
					}
					else if(((Cell) c).getCellType() == Column.TYPE_INTEGER)
					{
						newCell.setCellValue((int) ((Cell) c).getNumericCellValue());
						newCell.setType(Column.TYPE_INTEGER);
					}
					else
					{
						newCell.setCellValue(((Cell) c).getStringCellValue());
						newCell.setType(Column.TYPE_STRING);
					}
					
					newRow.add(newCell);
				}
				newSheet.add(newRow);
			}
			
			newSheet.setName(sheetName);
			sheets.add(newSheet);
		}
		catch(IOException e)
		{
			throw e;
		}
		finally
		{
			try
			{
				workbook.close();
			}
			catch(Exception e)
			{
				
			}
		}
	}
	
	public Object toWorkbook()
	{
		return null;
	}

	@Override
	public String help()
	{
		return null;
	}

	@Override
	public HgFormulaEvaluator createFormulaEvaluator()
	{
		// TODO : 동적 참조로 변환 필요
		
		
				
		return null;
	}

	@Override
	public HgSheet getSheetAt(int i)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HgSheet getSheet(String name)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void write(OutputStream stream) throws IOException
	{
		// TODO Auto-generated method stub
		
	}

	public List<HgSheet> getSheets()
	{
		return sheets;
	}

	public void setSheets(List<HgSheet> sheets)
	{
		this.sheets = sheets;
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
}
