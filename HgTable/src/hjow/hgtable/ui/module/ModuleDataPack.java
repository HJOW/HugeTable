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

package hjow.hgtable.ui.module;

import hjow.hgtable.Manager;
import hjow.hgtable.jscript.module.Module;
import hjow.hgtable.jscript.module.ScriptConsoleModule;
import hjow.hgtable.ui.GUIManager;
import hjow.hgtable.util.DataUtil;
import hjow.hgtable.util.InvalidInputException;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * <p>모듈을 불러오는 데 쓰이는 데이터 단위입니다.</p>
 * 
 * @author HJOW
 *
 */
public class ModuleDataPack implements Serializable
{
	private static final long serialVersionUID = -7414162854431888402L;
	protected String name, initScripts, afterInitScripts, refreshScripts, finalizeScripts, showMenuScripts, actMenuScripts, moduleType, description, scriptType;
	protected Long moduleId = new Long(new Random().nextLong());
	protected Map<String, String> moreOptions = new Hashtable<String, String>();
	
	public ModuleDataPack()
	{
		
	}
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getInitScripts()
	{
		return initScripts;
	}

	public void setInitScripts(String initScripts)
	{
		this.initScripts = initScripts;
	}

	public String getRefreshScripts()
	{
		return refreshScripts;
	}

	public void setRefreshScripts(String refreshScripts)
	{
		this.refreshScripts = refreshScripts;
	}

	public String getFinalizeScripts()
	{
		return finalizeScripts;
	}

	public void setFinalizeScripts(String finalizeScripts)
	{
		this.finalizeScripts = finalizeScripts;
	}

	public String getModuleType()
	{
		return moduleType;
	}

	public void setModuleType(String moduleType)
	{
		this.moduleType = moduleType;
	}
	public String getAfterInitScripts()
	{
		return afterInitScripts;
	}
	public void setAfterInitScripts(String afterInitScripts)
	{
		this.afterInitScripts = afterInitScripts;
	}
	public Map<String, String> getMoreOptions()
	{
		return moreOptions;
	}
	public void setMoreOptions(Map<String, String> moreOptions)
	{
		this.moreOptions = moreOptions;
	}
	public String getShowMenuScripts()
	{
		return showMenuScripts;
	}
	public void setShowMenuScripts(String showMenuScripts)
	{
		this.showMenuScripts = showMenuScripts;
	}
	public String getActMenuScripts()
	{
		return actMenuScripts;
	}
	public void setActMenuScripts(String actMenuScripts)
	{
		this.actMenuScripts = actMenuScripts;
	}
	public Long getModuleId()
	{
		return moduleId;
	}
	public void setModuleId(Long moduleId)
	{
		this.moduleId = moduleId;
	}
	
	/**
	 * <p>실제 모듈 객체를 반환합니다.</p>
	 * 
	 * @param manager : 모듈 객체 생성을 위한 매니저 객체
	 * @return 모듈 객체
	 */
	public Module toModule(Manager manager)
	{
		Module newModule = null;
		if(getModuleType().equalsIgnoreCase("Dialog"))
		{
			if(! (manager instanceof GUIManager)) throw new InvalidInputException(Manager.applyStringTable("Dialog module must have GUI manager object."));
			newModule = new ScriptDialogModule((GUIManager) manager);		
		}
		else if(getModuleType().equalsIgnoreCase("Panel"))
		{
			if(! (manager instanceof GUIManager)) throw new InvalidInputException(Manager.applyStringTable("Dialog module must have GUI manager object."));
			newModule = new ScriptPanelModule((GUIManager) manager);
		}
		else if(getModuleType().equalsIgnoreCase("Console"))
		{
			newModule = new ScriptConsoleModule(manager);
		}
		
		newModule.setName(name);
		newModule.setModuleId(moduleId);
		newModule.input(this);
		
		return newModule;
	}
	
	@Override
	public String toString()
	{
		StringBuffer results = new StringBuffer("");
		
		results = results.append(" # get from pack" + "\n");
		results = results.append("name://" + getName() + "\n");
		results = results.append("type://" + getModuleType() + "\n");
		if(getModuleId() != null) results = results.append("id://" + String.valueOf(getModuleId()) + "\n");
		
		StringTokenizer lineTokenizer = null;
		
		if(DataUtil.isNotEmpty(getInitScripts()))
		{
			results = results.append("init://" + "\n");
			lineTokenizer = new StringTokenizer(getInitScripts(), "\n");
			while(lineTokenizer.hasMoreTokens())
			{
				results = results.append(lineTokenizer.nextToken() + "\n");
			}
		}
		
		if(DataUtil.isNotEmpty(getAfterInitScripts()))
		{
			results = results.append("afterinit://" + "\n");
			lineTokenizer = new StringTokenizer(getAfterInitScripts(), "\n");
			while(lineTokenizer.hasMoreTokens())
			{
				results = results.append(lineTokenizer.nextToken() + "\n");
			}
		}
		
		if(DataUtil.isNotEmpty(getFinalizeScripts()))
		{
			results = results.append("finalize://" + "\n");
			lineTokenizer = new StringTokenizer(getFinalizeScripts(), "\n");
			while(lineTokenizer.hasMoreTokens())
			{
				results = results.append(lineTokenizer.nextToken() + "\n");
			}
		}
		
		if(DataUtil.isNotEmpty(getShowMenuScripts()))
		{
			results = results.append("showmenu://" + "\n");
			lineTokenizer = new StringTokenizer(getShowMenuScripts(), "\n");
			while(lineTokenizer.hasMoreTokens())
			{
				results = results.append(lineTokenizer.nextToken() + "\n");
			}
		}
		
		if(DataUtil.isNotEmpty(getActMenuScripts()))
		{
			results = results.append("actmenu://" + "\n");
			lineTokenizer = new StringTokenizer(getActMenuScripts(), "\n");
			while(lineTokenizer.hasMoreTokens())
			{
				results = results.append(lineTokenizer.nextToken() + "\n");
			}
		}
		
		if(DataUtil.isNotEmpty(getMoreOptions()))
		{
			results = results.append("option://" + "\n");
			Set<String> keys = getMoreOptions().keySet();
			for(String s : keys)
			{
				results = results.append(s + "=" + getMoreOptions().get(s) + "\n");
			}
		}
		
		// TODO
		
		return results.toString();
	}
	public String getDescription()
	{
		return description;
	}
	public void setDescription(String description)
	{
		this.description = description;
	}
	public String getScriptType()
	{
		return scriptType;
	}
	public void setScriptType(String scriptType)
	{
		this.scriptType = scriptType;
	}
}
