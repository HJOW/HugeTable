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

package hjow.hgtable.jscript.module;

import hjow.hgtable.Manager;
import hjow.hgtable.ui.module.ModuleDataPack;
import hjow.hgtable.util.DataUtil;

/**
 * <p>사용자 정의 콘솔용 모듈 객체입니다.</p>
 * 
 * @author HJOW
 *
 */
public class ScriptConsoleModule extends ConsoleModule implements ScriptModuleProtocol
{
	private static final long serialVersionUID = -8757235472611335682L;
	protected String initScripts, afterInitScripts, finalizeScripts, showMenuScripts, actMenuScripts, scriptType;
	protected transient ModuleRunner runner;

	/**
	 * <p>기본 생성자입니다. 사용을 위해서는 매니저 객체 삽입이 필요합니다.</p>
	 * 
	 */
	public ScriptConsoleModule()
	{
		super();
	}
	
	/**
	 * <p>생성자입니다. 매니저 객체가 필요합니다.</p>
	 * 
	 * @param manager : 매니저 객체
	 */
	public ScriptConsoleModule(Manager manager)
	{
		super(manager);
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
		setFinalizeScripts(packs.getFinalizeScripts());
		setShowMenuScripts(packs.getShowMenuScripts());
		setActMenuScripts(packs.getActMenuScripts());
		setScriptType(packs.getScriptType());
		setLicense(packs.getDescription());
	}
	/**
	 * <p>이 메소드에서 컴포넌트들을 초기화합니다. 모듈 내 컴포넌트들을 초기화하기 위해 이 메소드를 재정의합니다.</p>
	 * 
	 */
	public void initializeComponents()
	{
		try
		{
			if(runner == null)
			{
				if(DataUtil.isEmpty(scriptType)) runner = new ModuleRunner(manager);
				else  runner = new ModuleRunner(manager, scriptType);
			}
			runner.execute(initScripts);
		}
		catch(Throwable e)
		{
			manager.logError(e, Manager.applyStringTable("On initializing module") + " : " + getName());
		}
	}
	
	/**
	 * <p>사용자로부터 자체 메뉴 입력을 받을 때 보일 메시지를 반환합니다.</p>
	 * 
	 * @return 메뉴 입력 요청 메시지
	 */
	@Override
	public String getAskMenuInput()
	{
		return super.getAskMenuInput();
	}
	/**
	 * <p>이 메시지는 사용자가 자체 메뉴에서 항목을 선택했거나 텍스트를 입력했을 때 호출됩니다. 사용자가 입력한 텍스트는 inputs 이름으로 액세스 가능합니다.</p>
	 * 
	 * @param inputs : 사용자가 입력한 텍스트
	 * @return true 이면 작업 후 메뉴가 다시 나타납니다. false 이면 작업 후 모듈에서 나가게 됩니다.
	 */
	public boolean actMenu(String inputs)
	{
		try
		{
			if(runner == null)
			{
				if(DataUtil.isEmpty(scriptType)) runner = new ModuleRunner(manager);
				else  runner = new ModuleRunner(manager, scriptType);
			}
			runner.put("inputs", inputs);
			return DataUtil.parseBoolean(runner.execute(actMenuScripts));
		}
		catch(Throwable e)
		{
			manager.logError(e, Manager.applyStringTable("On acting menu selection") + " : " + getName());
			return false;
		}
	}
	
	/**
	 * <p>자체 메뉴를 보입니다.</p>
	 * 
	 */
	public void showMenu()
	{
		try
		{
			if(runner == null)
			{
				if(DataUtil.isEmpty(scriptType)) runner = new ModuleRunner(manager);
				else  runner = new ModuleRunner(manager, scriptType);
			}
			runner.execute(showMenuScripts);
		}
		catch(Throwable e)
		{
			manager.logError(e, Manager.applyStringTable("On showing module's menu") + " : " + getName());
		}
	}
	
	/**
	 * <p>이 메소드에서 초기화 직후 수행할 작업들을 합니다. 이 메소드를 재정의하여 사용합니다.</p>
	 * 
	 */
	public void doAfterInitialize()
	{
		try
		{
			if(runner == null)
			{
				if(DataUtil.isEmpty(scriptType)) runner = new ModuleRunner(manager);
				else  runner = new ModuleRunner(manager, scriptType);
			}
			runner.execute(afterInitScripts);
		}
		catch(Throwable e)
		{
			manager.logError(e, Manager.applyStringTable("On after-init module") + " : " + getName());
		}
	}
	@Override
	public boolean isAlive()
	{
		return super.isAlive();
	}
	@Override
	public void noMoreUse()
	{
		super.noMoreUse();
	}
	public String getInitScripts()
	{
		return initScripts;
	}
	public void setInitScripts(String initScripts)
	{
		this.initScripts = initScripts;
	}
	public String getAfterInitScripts()
	{
		return afterInitScripts;
	}
	public void setAfterInitScripts(String afterInitScripts)
	{
		this.afterInitScripts = afterInitScripts;
	}
	public String getFinalizeScripts()
	{
		return finalizeScripts;
	}
	public void setFinalizeScripts(String finalizeScripts)
	{
		this.finalizeScripts = finalizeScripts;
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
	public ModuleRunner getRunner()
	{
		return runner;
	}
	public void setRunner(ModuleRunner runner)
	{
		this.runner = runner;
	}

	@Override
	public ModuleDataPack toModuleDataPack()
	{
		ModuleDataPack newPack = new ModuleDataPack();
		newPack.setName(getName());
		newPack.setModuleType("Console");
		newPack.setInitScripts(getInitScripts());
		newPack.setAfterInitScripts(getAfterInitScripts());
		newPack.setFinalizeScripts(getFinalizeScripts());
		newPack.setShowMenuScripts(getShowMenuScripts());
		newPack.setActMenuScripts(getActMenuScripts());
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
