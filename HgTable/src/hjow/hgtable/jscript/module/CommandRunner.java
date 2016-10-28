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

import java.io.BufferedReader;
import java.io.InputStreamReader;

import hjow.hgtable.Manager;
import hjow.hgtable.jscript.JScriptRunner;

/**
 * <p>OS 명령어 실행 엔진입니다.</p>
 * 
 * @author HJOW
 *
 */
public class CommandRunner extends JScriptRunner
{
	protected Runtime runtime = Runtime.getRuntime();
	protected Manager manager;
	
	public CommandRunner(Manager manager)
	{
		super();
		this.manager = manager;
		engineType = "Command";
	}
	
	@Override
	public Object executeOnThread(String script, boolean simplifyError, boolean notTraceHere) throws Throwable
	{
		BufferedReader inputCollector = null;
		try
		{
			Process process = runtime.exec(script);
			inputCollector = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = null;
			String results = "";
			while(true)
			{
				line = inputCollector.readLine();
				if(line == null) break;
				results = results + "\n" + line;
			}
			
			try
			{
				inputCollector.close();
			}
			catch(Throwable t)
			{
				
			}
			
			return results;
		}
		catch(Throwable t)
		{
			if(notTraceHere) throw t;
			manager.logError(t, Manager.applyStringTable("On execute following scripts : ") + script, simplifyError);
			return t;
		}
		finally
		{
			try
			{
				inputCollector.close();
			}
			catch(Throwable t)
			{
				
			}
		}
	}
	@Override
	public void noMoreUse()
	{
		super.noMoreUse();
		this.manager = null;
		this.runtime = null;
	}
}
