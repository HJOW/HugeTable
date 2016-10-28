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

import java.util.Map;

import hjow.hgtable.Main;
import hjow.hgtable.Manager;

/**
 * <p>콘솔용 모듈에 관여하는 클래스입니다.</p>
 * 
 * @author HJOW
 *
 */
public class ConsoleModule extends Module implements AbstractConsoleModule
{
	private static final long serialVersionUID = 7632809756780427701L;
	protected transient Manager manager;
	protected transient boolean threadSwitch = true;
	protected transient int menuNumber = -1;
	
	/**
	 * <p>기본 생성자입니다. 사용을 위해서는 매니저 객체 삽입이 필요합니다.</p>
	 * 
	 */
	public ConsoleModule()
	{
		super();
	}
	
	/**
	 * <p>생성자입니다. 매니저 객체가 필요합니다.</p>
	 * 
	 * @param manager : 매니저 객체
	 */
	public ConsoleModule(Manager manager)
	{
		super();
		this.manager = manager;
	}
	
	@Override
	public final void manage(Map<String, String> args)
	{
		threadSwitch = true;
		while(threadSwitch)
		{
			showMenu();
			threadSwitch = actMenu(manager.askInput(getAskMenuInput(), true));
			if(! Main.checkInterrupt(this, Manager.applyStringTable("On console module menu"))) break;
		}
	}
	@Override
	public String help()
	{
		return null;
	}
	
	@Override
	public void showMenu()
	{
		
	}
	
	@Override
	public void exitMenu()
	{
		threadSwitch = false;
	}
	
	@Override
	public String getAskMenuInput()
	{
		return Manager.applyStringTable("What do you want to do?");
	}
	
	@Override
	public boolean actMenu(String inputs)
	{
		return true;
	}
	@Override
	public boolean isAlive()
	{
		return manager != null;
	}
	@Override
	public void noMoreUse()
	{
		manager = null;
		exitMenu();
		super.noMoreUse();
	}

	@Override
	public Manager getManager()
	{
		return manager;
	}

	@Override
	public void setManager(Manager manager)
	{
		this.manager = manager;
	}

	public boolean isThreadSwitch()
	{
		return threadSwitch;
	}

	public void setThreadSwitch(boolean threadSwitch)
	{
		this.threadSwitch = threadSwitch;
	}

	@Override
	public int getMenuNumber()
	{
		return menuNumber;
	}

	@Override
	public void setMenuNumber(int menuNumber)
	{
		this.menuNumber = menuNumber;
	}
}
