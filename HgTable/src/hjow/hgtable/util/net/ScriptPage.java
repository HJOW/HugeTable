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
package hjow.hgtable.util.net;

import java.util.List;
import java.util.Map;

import hjow.hgtable.Manager;
import hjow.hgtable.jscript.JScriptRunner;
import hjow.web.session.Session;

/**
 * <p>스크립트에서 사용할 수 있는 페이지 객체입니다. 결과물을 스크립트 실행으로 만들어 냅니다.</p>
 * 
 * @author HJOW
 *
 */
public class ScriptPage extends DefaultPage
{
	protected transient JScriptRunner runner;
	protected String init, process, beforeClose, order;
	protected transient Manager manager;
	
	public ScriptPage(Manager manager, JScriptRunner runner)
	{
		this.manager = manager;
		this.runner = runner;
	}
	
	@Override
	public String process(Map<String, List<String>> parameter, Map<String, String> meta, Session session)
	{
		runner.put("parameters", parameter);
		runner.put("metadata", meta);
		try
		{
			return String.valueOf(runner.execute(process));
		}
		catch (Throwable e)
		{
			manager.logError(e, Manager.applyStringTable("On processing page"));
			String errMsg = "<p>Error occured on server : " + e.getMessage() + "</p>\n";
			errMsg = errMsg + "<pre>";
			for(StackTraceElement el : e.getStackTrace())
			{
				errMsg = errMsg + "at " + el + "\n";
			}
			errMsg = errMsg + "</pre>";
			return errMsg;
		}
	}

	@Override
	public void init()
	{
		if(init != null)
		{
			try
			{
				runner.execute(init);
			}
			catch(Throwable e)
			{
				manager.logError(e, Manager.applyStringTable("On initializing page"));
			}
		}
	}

	@Override
	public void close()
	{
		if(beforeClose != null)
		{
			try
			{
				runner.execute(beforeClose);
			}
			catch(Throwable e)
			{
				manager.logError(e, Manager.applyStringTable("On initializing page"));
			}
		}
		runner = null;
		manager = null;
	}

	@Override
	public String getOrder()
	{
		if(order.startsWith("script://") || order.startsWith("SCRIPT://") || order.startsWith("Script://"))
		{
			try
			{
				return String.valueOf(runner.execute(order.substring(new String("script://").length())));
			}
			catch(Throwable e)
			{
				manager.logError(e, Manager.applyStringTable("On initializing page"));
				return null;
			}
		}
		else return order;
	}

	public JScriptRunner getRunner()
	{
		return runner;
	}

	public void setRunner(JScriptRunner runner)
	{
		this.runner = runner;
	}

	public String getInit()
	{
		return init;
	}

	public void setInit(String init)
	{
		this.init = init;
	}

	public String getProcess()
	{
		return process;
	}

	public void setProcess(String process)
	{
		this.process = process;
	}

	public String getBeforeClose()
	{
		return beforeClose;
	}

	public void setBeforeClose(String beforeClose)
	{
		this.beforeClose = beforeClose;
	}

	public Manager getManager()
	{
		return manager;
	}

	public void setManager(Manager manager)
	{
		this.manager = manager;
	}

	public void setOrder(String order)
	{
		this.order = order;
	}
}
