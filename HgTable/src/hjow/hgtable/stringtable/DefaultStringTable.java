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

package hjow.hgtable.stringtable;

import hjow.hgtable.Main;
import hjow.hgtable.Manager;
import hjow.hgtable.util.StreamUtil;
import hjow.hgtable.util.StringTableUtil;

import java.io.File;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

/**
 * <p>Hashtable 기반의 스트링 테이블 클래스입니다. 없는 내용 추적 기능이 있습니다.</p>
 * 
 * @author HJOW
 *
 */
public class DefaultStringTable extends Hashtable<String, String> implements StringTable
{
	private static final long serialVersionUID = -8937842491724104228L;
	private Set<String> noInStringTableStores = new HashSet<String>();
	private boolean collectNoInString = false;
	
	/**
	 * <p>유일한 생성자입니다.</p>
	 * 
	 */
	public DefaultStringTable()
	{
		// StringTableUtil.defaultData(this);
	}
	
	@Override
	public String process(String str)
	{
		if(str == null) return "";
		if(get(str) == null)
		{
			if(collectNoInString)
			{
				noInStringTableStores.add(str);
				put(str, str);
			}
			return str;
		}
		else return get(str);
	}
	@Override
	public void read(File file)
	{
		String gets = StreamUtil.readText(file, "UTF-8", "#");
		try
		{
			StringTableUtil.inputFromText(this, gets);
		}
		catch (InvalidPrefixException e)
		{
			Main.logError(e, process("On getting string table data from following text") + "...\n"
					 + e.getPrefixProblemLine() + process(" : ") + e.getPrefixProblemLineContents(), true);			
		}
	}
	
	@Override
	public void save(File file)
	{
		try
		{
			StreamUtil.saveFile(file, toString(), "UTF-8");
		}
		catch(Throwable e1)
		{
			
		}
	}
	
	/**
	 * <p>파일로부터 스트링 테이블 객체를 받습니다.</p>
	 * 
	 * @return 스트링 테이블 객체
	 * @throws Exception 입출력 관련 오류, 권한 문제
	 */
	public static StringTable getFromFile() throws Exception
	{
		StringTable newStringTable = new DefaultStringTable();
		File configFile = new File(Manager.getOption("config_path") + Manager.getOption("user_language") + ".lang");
		
		if(configFile.exists())
		{
			newStringTable.read(configFile);
		}
		return newStringTable;
	}
	
	@Override
	public void finalize()
	{
		if(noInStringTableStores.size() >= 1)
		{
			Main.println(process("There are so many elements not in string table") + "...\n\n");
			
			for(String s : noInStringTableStores)
			{
				Main.println(s);
			}
			
			Main.println("\n\n..." + process("end"));
		}
	}
	public Set<String> getNoInStringTableStores()
	{
		return noInStringTableStores;
	}
	public void setNoInStringTableStores(Set<String> noInStringTableStores)
	{
		this.noInStringTableStores = noInStringTableStores;
	}
	@Override
	public boolean isCollectNoInString()
	{
		return collectNoInString;
	}
	@Override
	public void setCollectNoInString(boolean collectNoInString)
	{
		this.collectNoInString = collectNoInString;
	}
	@Override
	public String toString()
	{
		StringBuffer results = new StringBuffer("");
		
		Set<String> keys = keySet();
		
		results = results.append("# " + process("String table of Huge Table") + "\n");
		results = results.append("# " + process("The line starts with $ means target, and the line starts with @ means result.") + "\n");
		results = results.append("# ------------------------------------\n");
		
		for(String k : keys)
		{
			results = results.append(StringTableUtil.LINE_PREFIX_TARGET + k + "\n");
			results = results.append(StringTableUtil.LINE_PREFIX_SET + process(k) + "\n");
		}
		results = results.append("# " + process("End"));
		
		return results.toString();
	}
}