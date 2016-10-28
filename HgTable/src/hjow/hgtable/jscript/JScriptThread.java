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

package hjow.hgtable.jscript;

import hjow.hgtable.Main;
import hjow.hgtable.Manager;

/**
 * <p>이 클래스는 JDK에서 제공하는 스크립트 엔진이 setInterval() 함수를 제공하지 않는 것을 보완하기 위해 만들어졌습니다.</p>
 * <p>이 기능을 이용해 쓰레드를 활용할 수 있습니다.</p>
 * <p>콜백 함수를 받을 수는 없습니다. String 타입으로 스크립트를 받아야 합니다.</p>
 * 
 * @author HJOW
 *
 */
public class JScriptThread extends Thread
{
	private String scripts;
	private JScriptRunner runner;
	private boolean threadSwitch = true;
	private int interval = 100;
	
	public JScriptThread()
	{
		
	}
	public JScriptThread(JScriptRunner runner, String scripts)
	{
		this.runner = runner;
		this.scripts = scripts;
	}
	public JScriptThread(JScriptRunner runner, String scripts, int interval)
	{
		this.runner = runner;
		this.scripts = scripts;
		this.interval = interval;
	}
	
	public void close()
	{
		threadSwitch = false;
		runner = null;
	}
	
	@Override
	public void run()
	{
		while(threadSwitch)
		{
			try
			{
				if(runner == null) break;
				runner.execute(scripts);
			}
			catch (Throwable e1)
			{
				Main.logError(e1, Manager.applyStringTable("On running thread"));
			}
			try
			{
				Thread.sleep(interval);
			}
			catch(Throwable e)
			{
				
			}
			
			if(! (Main.checkInterrupt(this, "scriptThread")))
			{
				break;
			}
		}
	}
	public String getScripts()
	{
		return scripts;
	}
	public void setScripts(String scripts)
	{
		this.scripts = scripts;
	}
	public JScriptRunner getRunner()
	{
		return runner;
	}
	public void setRunner(JScriptRunner runner)
	{
		this.runner = runner;
	}
	public boolean isThreadSwitch()
	{
		return threadSwitch;
	}
	public void setThreadSwitch(boolean threadSwitch)
	{
		this.threadSwitch = threadSwitch;
	}
	public int getInterval()
	{
		return interval;
	}
	public void setInterval(int interval)
	{
		this.interval = interval;
	}
}
