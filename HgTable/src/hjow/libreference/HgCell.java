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

package hjow.libreference;

import java.util.Date;

public interface HgCell extends HgSheetObject
{
	public HgRichTextString getRichStringCellValue();
	public String getStringCellValue();
	public String getCellFormula();
	public Date getDateCellValue();
	public double getNumericCellValue();
	public boolean getBooleanCellValue();
	public int getCellType();
	public void setCellValue(String value);
	public void setCellValue(double value);
	public void setCellValue(boolean value);
	public void setCellValue(HgRichTextString value);
	public void setCellValue(Date value);
}
