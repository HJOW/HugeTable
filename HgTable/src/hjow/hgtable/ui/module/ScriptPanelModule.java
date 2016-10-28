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

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.Map;

import javax.swing.JPanel;

import hjow.hgtable.Main;
import hjow.hgtable.Manager;
import hjow.hgtable.jscript.module.ModuleRunner;
import hjow.hgtable.jscript.module.ScriptModuleProtocol;
import hjow.hgtable.ui.GUIManager;
import hjow.hgtable.util.DataUtil;

/**
 * <p>사용자 정의 패널형 모듈 객체입니다.</p>
 * 
 * @author HJOW
 *
 */
public class ScriptPanelModule extends GUIPanelModule implements ScriptModule, ScriptModuleProtocol
{
	private static final long serialVersionUID = 7527619679521739489L;
	protected transient JPanel panel;
	protected transient ModuleRunner runner;
	protected String initScripts = "";
	protected String afterInitScripts = "";
	protected String refreshScripts = "";
	protected String finalizeScripts = "";
	protected String scriptType = "";
	
	/**
	 * <p>기본 생성자입니다. 직렬화를 위해 있을 뿐 실제로 사용되지는 않습니다.</p>
	 * 
	 */
	public ScriptPanelModule()
	{
		super();
	}
	
	/**
	 * <p>내용이 비어 있는 모듈 객체를 만듭니다. 이후 스크립트 내용을 별도로 삽입해 주어야 합니다.</p>
	 * 
	 * @param manager : 매니저 객체
	 */
	public ScriptPanelModule(GUIManager manager)
	{
		super(manager);		
	}
	
	/**
	 * <p>모듈 정보를 토대로 모듈 객체를 만듭니다.</p>
	 * 
	 * @param manager : 매니저 객체
	 * @param contents : 모듈 정보 객체
	 */
	public ScriptPanelModule(GUIManager manager, ModuleDataPack contents)
	{
		super(manager);
		input(contents);
	}
	
	/**
	 * <p>데이터를 삽입합니다.</p>
	 * 
	 * @param packs : 데이터를 읽어 온 단위
	 */
	public void input(ModuleDataPack packs)
	{
		super.input(packs);
		setName(packs.getName());
		setModuleId(packs.getModuleId());
		setInitScripts(packs.getInitScripts());
		setAfterInitScripts(packs.getAfterInitScripts());
		setRefreshScripts(packs.getRefreshScripts());
		setFinalizeScripts(packs.getFinalizeScripts());
		setScriptType(packs.getScriptType());
		setLicense(packs.getDescription());
	}
	
	@Override
	public void refresh(Map<String, Object> additionalData)
	{
		super.refresh(additionalData);
		try
		{
			runner.put("refreshMap", additionalData);
			runner.execute(refreshScripts, this);
		}
		catch(Throwable e)
		{
			manager.logError(e, Manager.applyStringTable("On refreshing module") + " : " + getName() + " " + Manager.applyStringTable("on") + "...\n" + String.valueOf(additionalData));
		}
	}
	
	@Override
	public void initializeComponents()
	{		
		panel = new JPanel();
		panel.setLayout(new BorderLayout());
		
		try
		{
			if(runner == null)
			{
				if(DataUtil.isEmpty(scriptType)) runner = new ModuleRunner(manager);
				else  runner = new ModuleRunner(manager, scriptType);
			}
			runner.put("module_component", panel);
			runner.execute(initScripts, this);
		}
		catch(Throwable e)
		{
			manager.logError(e, Manager.applyStringTable("On initializing module") + " : " + getName());
		}
	}
	
	@Override
	public void doAfterInitialize()
	{
		try
		{
			if(runner == null)
			{
				if(DataUtil.isEmpty(scriptType)) runner = new ModuleRunner(manager);
				else  runner = new ModuleRunner(manager, scriptType);
			}
			runner.execute(afterInitScripts, this);
		}
		catch(Throwable e)
		{
			manager.logError(e, Manager.applyStringTable("On after-init module") + " : " + getName());
		}
	}
	@Override
	public void noMoreUse()
	{
		try
		{
			if(DataUtil.isNotEmpty(finalizeScripts)) this.runner.execute(finalizeScripts, this);
		}
		catch(Throwable e)
		{
			Main.logError(e, Manager.applyStringTable("On finalizing module") + " : " + getName());
		}
		try
		{
			this.runner.close();
		}
		catch(Exception e)
		{
			
		}
		this.runner = null;
		super.noMoreUse();		
	}
	
	@Override
	public void put(String varName, Object obj)
	{
		runner.put(varName, obj);
	}
	
	@Override
	public Component getComponent()
	{
		return panel;
	}

	public ModuleRunner getRunner()
	{
		return runner;
	}

	public void setRunner(ModuleRunner runner)
	{
		this.runner = runner;
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

	public String getAfterInitScripts()
	{
		return afterInitScripts;
	}

	public void setAfterInitScripts(String afterInitScripts)
	{
		this.afterInitScripts = afterInitScripts;
	}

	public JPanel getPanel()
	{
		return panel;
	}

	public void setPanel(JPanel panel)
	{
		this.panel = panel;
	}

	@Override
	public ModuleDataPack toModuleDataPack()
	{
		ModuleDataPack newPack = new ModuleDataPack();
		newPack.setName(getName());
		newPack.setModuleType("Panel");
		newPack.setInitScripts(getInitScripts());
		newPack.setAfterInitScripts(getAfterInitScripts());
		newPack.setRefreshScripts(getRefreshScripts());
		newPack.setFinalizeScripts(getFinalizeScripts());
		newPack.setMoreOptions(getOptions());
		newPack.setScriptType(scriptType);
		newPack.setDescription(getLicense());
		
		return newPack;
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
