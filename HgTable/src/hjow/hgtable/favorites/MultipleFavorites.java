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
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import hjow.hgtable.util.DataUtil;
import hjow.hgtable.util.FavoriteUtil;

/**
 * <p>자주 사용하는 스크립트 여러 개를 묶어서 한 번에 다루는 데 쓰이는 클래스입니다.</p>
 * 
 * @author HJOW
 *
 */
public class MultipleFavorites implements AbstractFavorites
{
	private static final long serialVersionUID = -2520155547522325626L;
	public static transient final String TYPE = "Multiple";
	protected List<AbstractFavorites> favoritesList = new Vector<AbstractFavorites>();
	protected Long uniqueId = new Long(new Random().nextLong());
	protected String language = "";
	protected String name = "";
	protected String description = "";
	protected Integer shortcut;
	protected Integer mask;
	
	/**
	 * <p>기본 생성자입니다.</p>
	 */
	public MultipleFavorites()
	{
		
	}
	
	/**
	 * <p>읽은 문자열을 번역해 자주 사용하는 스크립트 객체로 만듭니다.</p>
	 * 
	 * @param serialized : 읽은 문자열
	 * @throws Throwable 
	 */
	public MultipleFavorites(String serialized) throws Throwable
	{
		int mode = 0;
		
		StringBuffer favoriteAccumulation = new StringBuffer("\n");
		int objectReading = 0;
		
		StringTokenizer lineTokenizer = new StringTokenizer(serialized, "\n");
		getParameterSet().clear();
		while(lineTokenizer.hasMoreTokens())
		{
			String line = DataUtil.remove65279(lineTokenizer.nextToken()).trim();
			if(DataUtil.isEmpty(line)) continue;
			if(line.startsWith("#")) continue;
			else if(objectReading == 0)
			{
				if(line.startsWith("@"))
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
					else if(eliminated.equalsIgnoreCase("DESCRIPTION")) mode = 2;
					else if(eliminated.equalsIgnoreCase("PARAMETER")) mode = 3;
				}
				else if(line.startsWith("!"))
				{
					String eliminated = line.substring(new String("!").length()).trim();
					if(eliminated.equalsIgnoreCase("START")) objectReading++;
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
			else
			{
				if(line.startsWith("!"))
				{
					String eliminated = line.substring(new String("!").length()).trim();
					if(eliminated.equalsIgnoreCase("END")) objectReading--;
					else if(eliminated.equalsIgnoreCase("START")) objectReading++;
					if(objectReading >= 1)
					{
						favoriteAccumulation = favoriteAccumulation.append(line);
					}
					else
					{
						favoritesList.add(FavoriteUtil.instantiation(favoriteAccumulation.toString()));
						favoriteAccumulation = new StringBuffer("");
					}
				}
				else
				{
					favoriteAccumulation = favoriteAccumulation.append(line);
				}
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
		result = result.append("@ ").append("DESCRIPTION").append("\n");
		result = result.append(getDescription()).append("\n");
		result = result.append("@ ").append("PARAMETER").append("\n");
		for(String p : getParameterSet())
		{
			result = result.append(p).append("\n");
		}
		result = result.append("! ").append("START").append("\n");
		for(AbstractFavorites fav : favoritesList)
		{
			result = result.append(fav.serialize()).append("\n");
		}
		result = result.append("! ").append("END").append("\n");
		return result.toString();
	}

	@Override
	public String help()
	{
		return null;
	}

	@Override
	public void noMoreUse()
	{
		for(AbstractFavorites fav : favoritesList)
		{
			fav.noMoreUse();
		}
		favoritesList.clear();
	}

	@Override
	public boolean isAlive()
	{
		for(AbstractFavorites fav : favoritesList)
		{
			if(! fav.isAlive()) return false;
		}
		return true;
	}
	
	@Override
	public Set<String> getParameterSet()
	{
		HashSet<String> params = new HashSet<String>();
		for(AbstractFavorites fav : favoritesList)
		{
			params.addAll(fav.getParameterSet());
		}
		return params;
	}
	
	@Override
	public String getScript()
	{
		StringBuffer results = new StringBuffer("");
		for(AbstractFavorites fav : favoritesList)
		{
			String script = fav.getScript();
			results = results.append(script).append("\n");
			if(getLanguage().equals("SQL") && (! script.trim().endsWith(";")))
			{
				results = results.append(";\n");
			}
		}
		return results.toString();
	}

	@Override
	public String getScript(Map<String, String> params)
	{
		StringBuffer results = new StringBuffer("");
		for(AbstractFavorites fav : favoritesList)
		{
			String script = fav.getScript();
			results = results.append(script).append("\n");
			if(getLanguage().equals("SQL") && (! script.trim().endsWith(";")))
			{
				results = results.append(";\n");
			}
		}
		
		String resultStr = results.toString();
		if(params != null && (! params.isEmpty()))
		{
			Set<String> keys = params.keySet();
			for(String k : keys)
			{
				resultStr = resultStr.replace(":" + k, params.get(k));
			}
		}
		
		return resultStr;
	}

	public List<AbstractFavorites> getFavoritesList()
	{
		return favoritesList;
	}

	public void setFavoritesList(List<AbstractFavorites> favoritesList)
	{
		this.favoritesList = favoritesList;
	}

	@Override
	public Long getUniqueId()
	{
		return uniqueId;
	}

	public void setUniqueId(Long uniqueId)
	{
		this.uniqueId = uniqueId;
	}
	
	@Override
	public String getType()
	{
		return TYPE;
	}

	@Override
	public String getLanguage()
	{
		return language;
	}

	public void setLanguage(String language)
	{
		this.language = language;
	}

	@Override
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	@Override
	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	@Override
	public Integer getShortcut()
	{
		return shortcut;
	}

	public void setShortcut(Integer shortcut)
	{
		this.shortcut = shortcut;
	}

	@Override
	public Integer getMask()
	{
		return mask;
	}

	public void setMask(Integer mask)
	{
		this.mask = mask;
	}
}
