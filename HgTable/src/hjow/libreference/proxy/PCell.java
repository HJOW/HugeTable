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

import hjow.hgtable.util.DataUtil;
import hjow.hgtable.util.InvalidInputException;
import hjow.libreference.HgCell;
import hjow.libreference.HgRichTextString;

import java.util.Date;

public class PCell implements HgCell
{
	private static final long serialVersionUID = -2629899183736747524L;
	protected Object contents;
	protected int type = 0;
	
	public PCell()
	{
		
	}
	
	public PCell(Object value, int type)
	{
		this.contents = value;
		this.type = type;
	}
	
	@Override
	public String help()
	{
		return null;
	}

	@Override
	public HgRichTextString getRichStringCellValue()
	{
		return new PRichTextString(((HgCell) contents).getRichStringCellValue());
	}

	@Override
	public String getStringCellValue()
	{
		return String.valueOf(contents);
	}

	@Override
	public String getCellFormula()
	{
		return String.valueOf(contents);
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
	public Date getDateCellValue()
	{
		return (Date) contents;
	}

	@Override
	public double getNumericCellValue()
	{
		return Double.parseDouble(String.valueOf(contents));
	}

	@Override
	public boolean getBooleanCellValue()
	{
		try
		{
			return DataUtil.parseBoolean(contents);
		}
		catch (InvalidInputException e)
		{
			return false;
		}
	}

	@Override
	public int getCellType()
	{
		return type;
	}

	@Override
	public void setCellValue(String value)
	{
		contents = value;
	}

	@Override
	public void setCellValue(double value)
	{
		contents = new Double(value);
	}

	@Override
	public void setCellValue(boolean value)
	{
		contents = new Boolean(value);
	}

	@Override
	public void setCellValue(HgRichTextString value)
	{
		contents = value;
	}

	@Override
	public void setCellValue(Date value)
	{
		contents = value;		
	}

	public Object getContents()
	{
		return contents;
	}

	public void setContents(Object contents)
	{
		this.contents = contents;
	}

	public int getType()
	{
		return type;
	}

	public void setType(int type)
	{
		this.type = type;
	}
}
