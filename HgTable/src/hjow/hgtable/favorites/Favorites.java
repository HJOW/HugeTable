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
package hjow.hgtable.favorites;

import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;

import hjow.hgtable.util.DataUtil;

/**
 * <p>자주 사용하는 스크립트를 다루는 데 쓰이는 클래스입니다.</p>
 */
public class Favorites implements AbstractFavorites
{
	private static final long serialVersionUID = -4730346963181667022L;
	public static transient final String TYPE = "Favorites";
	protected Long uniqueId = new Long(new Random().nextLong());
	protected String script = "";
	protected String language = "";
	protected String name = "";
	protected String description = "";
	protected Integer shortcut;
	protected Integer mask;
	protected Set<String> parameterSet = new HashSet<String>();

	public Favorites()
	{
		
	}
	
	/**
	 * <p>읽은 문자열을 번역해 자주 사용하는 스크립트 객체로 만듭니다.</p>
	 * 
	 * @param serialized : 읽은 문자열
	 */
	public Favorites(String serialized)
	{
		int mode = 0;
		
		StringTokenizer lineTokenizer = new StringTokenizer(serialized, "\n");
		getParameterSet().clear();
		while(lineTokenizer.hasMoreTokens())
		{
			String line = DataUtil.remove65279(lineTokenizer.nextToken()).trim();
			if(DataUtil.isEmpty(line)) continue;
			if(line.startsWith("#")) continue;
			else if(line.startsWith("@"))
			{
				String eliminated = line.substring(new String("@").length()).trim();
				if(eliminated.startsWith("NAME") || eliminated.startsWith("name") || eliminated.startsWith("Name"))
				{
					String keywordRemoved = eliminated.substring(new String("NAME").length()).trim();
					setName(keywordRemoved);
				}
				else if(eliminated.startsWith("LANGUAGE") || eliminated.startsWith("language") || eliminated.startsWith("Language"))
				{
					String keywordRemoved = eliminated.substring(new String("LANGUAGE").length()).trim();
					setLanguage(keywordRemoved);
				}
				else if(eliminated.startsWith("ID") || eliminated.startsWith("id") || eliminated.startsWith("Id"))
				{
					String keywordRemoved = eliminated.substring(new String("ID").length()).trim();
					setUniqueId(new Long(keywordRemoved));
				}
				else if(eliminated.startsWith("SHORTCUT") || eliminated.startsWith("shortcut") || eliminated.startsWith("Shortcut"))
				{
					String keywordRemoved = eliminated.substring(new String("SHORTCUT").length()).trim();
					setShortcut(new Integer(keywordRemoved));
				}
				else if(eliminated.startsWith("MASK") || eliminated.startsWith("mask") || eliminated.startsWith("Mask"))
				{
					String keywordRemoved = eliminated.substring(new String("MASK").length()).trim();
					setMask(new Integer(keywordRemoved));
				}
				else if(eliminated.equalsIgnoreCase("SCRIPT")) mode = 1;
				else if(eliminated.equalsIgnoreCase("DESCRIPTION")) mode = 2;
				else if(eliminated.equalsIgnoreCase("PARAMETER")) mode = 3;
			}
			else if(mode == 1)
			{
				String before = getScript();
				if(DataUtil.isNotEmpty(before)) before = before + "\n"; 
				setScript(before + line);
			}
			else if(mode == 2)
			{
				String before = getDescription();
				if(DataUtil.isNotEmpty(before)) before = before + "\n"; 
				setDescription(before + line);
			}
			else if(mode == 3)
			{
				getParameterSet().add(line);
			}
		}
	}
	
	@Override
	public String serialize()
	{
		StringBuffer result = new StringBuffer("");
		result = result.append("@ ").append("NAME ").append(getName()).append("\n");
		result = result.append("@ ").append("TYPE ").append(TYPE).append("\n");
		result = result.append("@ ").append("LANGUAGE ").append(getLanguage()).append("\n");
		result = result.append("@ ").append("ID ").append(String.valueOf(getUniqueId())).append("\n");
		result = result.append("@ ").append("SHORTCUT ").append(String.valueOf(getShortcut())).append("\n");
		result = result.append("@ ").append("MASK ").append(String.valueOf(getMask())).append("\n");
		result = result.append("@ ").append("SCRIPT").append("\n");
		result = result.append(getScript()).append("\n");
		result = result.append("@ ").append("DESCRIPTION").append("\n");
		result = result.append(getDescription()).append("\n");
		result = result.append("@ ").append("PARAMETER").append("\n");
		for(String p : getParameterSet())
		{
			result = result.append(p).append("\n");
		}
		return result.toString();
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
	public String help()
	{
		return null;
	}

	@Override
	public String getScript(Map<String, String> params)
	{
		String result = getScript();
		if(params != null && (! params.isEmpty()))
		{
			Set<String> keys = params.keySet();
			for(String k : keys)
			{
				result = result.replace(":" + k, params.get(k));
			}
		}
		return result;
	}

	@Override
	public Long getUniqueId()
	{
		return uniqueId;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public String getLanguage()
	{
		return language;
	}

	@Override
	public String getScript()
	{
		return script;
	}

	@Override
	public String getDescription()
	{
		return description;
	}

	@Override
	public Integer getShortcut()
	{
		return shortcut;
	}

	@Override
	public Integer getMask()
	{
		return mask;
	}

	public void setUniqueId(Long uniqueId)
	{
		this.uniqueId = uniqueId;
	}

	public void setScript(String script)
	{
		this.script = script;
	}

	public void setLanguage(String language)
	{
		this.language = language;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public void setShortcut(Integer shortcut)
	{
		this.shortcut = shortcut;
	}

	public void setMask(Integer mask)
	{
		this.mask = mask;
	}

	@Override
	public String getType()
	{
		return TYPE;
	}

	@Override
	public Set<String> getParameterSet()
	{
		return parameterSet;
	}

	public void setParameterSet(Set<String> parameterSet)
	{
		this.parameterSet = parameterSet;
	}
}
