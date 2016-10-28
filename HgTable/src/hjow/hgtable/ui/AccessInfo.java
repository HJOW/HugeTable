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

package hjow.hgtable.ui;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import hjow.hgtable.dao.Dao;

/**
 * <p>접속 정보 객체입니다.</p>
 * 
 * @author HJOW
 *
 */
public class AccessInfo implements Serializable, Comparable<AccessInfo>, Map<String, String>
{
	private static final long serialVersionUID = 1339964619551158735L;
	protected String url = null;
	protected String classPath = null;
	protected String id = null;
	protected String pw = null;
	public AccessInfo()
	{
		
	}
	public AccessInfo(String url, String classPath, String id,
			String pw)
	{
		super();
		this.url = url;
		this.classPath = classPath;
		this.id = id;
		this.pw = pw;
	}
	public String getUrl()
	{
		return url;
	}
	public void setUrl(String url)
	{
		this.url = url;
	}	
	public String getClassPath()
	{
		return classPath;
	}
	public void setClassPath(String classPath)
	{
		this.classPath = classPath;
	}
	public String getId()
	{
		return id;
	}
	public void setId(String id)
	{
		this.id = id;
	}
	public String getPw()
	{
		return pw;
	}
	public void setPw(String pw)
	{
		this.pw = pw;
	}
	public String toString()
	{
		return url + " ~ " + id;
	}
	@Override
	public boolean equals(Object others)
	{
		if(others instanceof AccessInfo) return (compareTo((AccessInfo) others) == 0);
		else if(others instanceof Dao) return (compareTo(((Dao) others).getAccessInfo()) == 0);
		else return false;
	}
	@Override
	public int compareTo(AccessInfo o)
	{		
		if(this.id != o.id) return 1;
		if(this.pw != o.pw) return 1;
		if(this.url != o.url) return 1;
		
		return 0;
	}
	@Override
	public int size()
	{
		return 4;
	}
	@Override
	public boolean isEmpty()
	{
		return false;
	}
	@Override
	public boolean containsKey(Object key)
	{
		return key != null && (String.valueOf(key).equalsIgnoreCase("url") 
				|| String.valueOf(key).equalsIgnoreCase("classPath") 
				|| String.valueOf(key).equalsIgnoreCase("id") 
				|| String.valueOf(key).equalsIgnoreCase("pw"));
	}
	@Override
	public boolean containsValue(Object value)
	{
		String val = String.valueOf(value);
		return value != null && (id.equalsIgnoreCase(val)
				|| pw.equalsIgnoreCase(val)
				|| url.equalsIgnoreCase(val)
				|| classPath.equalsIgnoreCase(val));
	}
	@Override
	public String get(Object key)
	{
		String keyVal = String.valueOf(key);
		if(keyVal.equalsIgnoreCase("url")) return url;
		if(keyVal.equalsIgnoreCase("classPath")) return classPath;
		if(keyVal.equalsIgnoreCase("id")) return id;
		if(keyVal.equalsIgnoreCase("pw")) return pw;
		return null;
	}
	@Override
	public String put(String key, String value)
	{
		String oldOne = null;
		
		if(key.equalsIgnoreCase("url"))
		{
			oldOne = url;
			url = value;
		}
		if(key.equalsIgnoreCase("classPath"))
		{
			oldOne = classPath;
			classPath = value;
		}
		if(key.equalsIgnoreCase("id"))
		{
			oldOne = id;
			id = value;
		}
		if(key.equalsIgnoreCase("pw"))
		{
			oldOne = pw;
			pw = value;
		}
		
		return oldOne;
	}
	@Override
	public String remove(Object key)
	{
		String keyVal = String.valueOf(key);
		String oldOne = null;
		if(keyVal.equalsIgnoreCase("url"))
		{
			oldOne = url;
			url = "";
		}
		if(keyVal.equalsIgnoreCase("classPath"))
		{
			oldOne = classPath;
			classPath = "";
		}
		if(keyVal.equalsIgnoreCase("id"))
		{
			oldOne = id;
			id = "";
		}
		if(keyVal.equalsIgnoreCase("pw"))
		{
			oldOne = pw;
			pw = "";
		}
		return oldOne;
	}
	@Override
	public void putAll(Map<? extends String, ? extends String> m)
	{
		Set<? extends String> keys = m.keySet();
		for(String k : keys)
		{
			put(k, m.get(k));
		}
	}
	@Override
	public void clear()
	{
		Set<? extends String> keys = keySet();
		for(String k : keys)
		{
			remove(k);
		}
	}
	@Override
	public Set<String> keySet()
	{
		Set<String> sets = new HashSet<String>();
		sets.add("id");
		sets.add("pw");
		sets.add("url");
		sets.add("classPath");
		return sets;
	}
	@Override
	public Collection<String> values()
	{
		List<String> vals = new Vector<String>();
		Set<String> sets = keySet();
		for(String k : sets)
		{
			vals.add(get(k));
		}
		
		return vals;
	}
	@Override
	public Set<java.util.Map.Entry<String, String>> entrySet()
	{
		Set<Entry<String, String>> entries = new HashSet<Map.Entry<String,String>>();
		Set<String> sets = keySet();
		for(String k : sets)
		{
			Entry<String, String> newEntry = new hjow.hgtable.util.Entry<String, String>(k, get(k));
			entries.add(newEntry);
		}
		return entries;
	}
}
