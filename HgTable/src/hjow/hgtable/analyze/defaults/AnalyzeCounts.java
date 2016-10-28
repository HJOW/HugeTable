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
package hjow.hgtable.analyze.defaults;

import java.util.List;
import java.util.Map;
import java.util.Vector;

import hjow.hgtable.Manager;
import hjow.hgtable.analyze.AnalyzeFunction;
import hjow.hgtable.tableset.Column;
import hjow.hgtable.tableset.DefaultTableSet;
import hjow.hgtable.tableset.TableSet;

/**
 * <p>컬럼의 데이터들이 각각 얼마나 포함되었는지의 여부를 분석하는 분석 함수입니다.</p>
 * 
 * @author HJOW
 *
 */
public class AnalyzeCounts extends AnalyzeFunction
{
	private static final long serialVersionUID = 6843047037457838614L;
	
	public AnalyzeCounts()
	{
		setName("count");
		setModuleId(new Long(1987581521626373725L));
	}
	
	@Override
	public boolean canAnalyzeColumn()
	{
		return true;
	}
	
	@Override
	public boolean canAnalyzeTableSet()
	{
		return true;
	}

	@Override
	public TableSet analyze(Map<String, String> arguments,
			TableSet... tableSets)
	{
		Column targetColumn = tableSets[0].getColumn(arguments.get("column"));
		if(targetColumn == null) throw new NullPointerException(Manager.applyStringTable("There is no column which name is") + " " + arguments.get("column") + " "
				 + Manager.applyStringTable("on") + " " + tableSets[0].getName());
		TableSet newTableSet = analyze(arguments, targetColumn);
		newTableSet.setName("count of " + targetColumn.getName() + " in " + tableSets[0].getName());
		
		return newTableSet;
	}

	@Override
	public TableSet analyze(Map<String, String> arguments, Column... columns)
	{
		TableSet newTableSet = new DefaultTableSet();
		Column targetColumn = columns[0];
		newTableSet.setName("count of " + targetColumn.getName());
		
		Column columnValue = new Column();
		Column countValue = new Column();
		
		columnValue.setName("VALUE");
		countValue.setName("COUNT");
		
		columnValue.setType(targetColumn.getType());
		countValue.setType(Column.TYPE_INTEGER);
		
		List<String> distincts = targetColumn.distincts().getData();
		
		for(int i=0; i<distincts.size(); i++)
		{
			columnValue.getData().add(distincts.get(i));
			countValue.getData().add(String.valueOf(targetColumn.has(distincts.get(i))));
		}
		
		newTableSet.addColumn(columnValue);
		newTableSet.addColumn(countValue);
		
		return newTableSet;
	}

	@Override
	public List<String> getNeedArguments(boolean forTableSet)
	{
		List<String> needs = new Vector<String>();
		
		if(forTableSet)
		{
			needs.add("column");
		}
		
		return needs;
	}
}
