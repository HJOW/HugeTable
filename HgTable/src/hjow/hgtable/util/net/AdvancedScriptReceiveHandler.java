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

import java.util.Map;

import hjow.hgtable.jscript.JScriptRunner;

/**
 * <p>스크립트에서 사용할 수 있는 핸들러 객체입니다.</p>
 * 
 * @author HJOW
 *
 */
public class AdvancedScriptReceiveHandler implements AdvancedReceiveHandler
{
	private static final long serialVersionUID = -8887554664200258787L;
	protected String script;
	protected JScriptRunner runner;

	public AdvancedScriptReceiveHandler(String script, JScriptRunner runner)
	{
		this.script = script;
		this.runner = runner;
	}

	@Override
	public void noMoreUse()
	{
		script = null;
		runner = null;
	}

	@Override
	public boolean isAlive()
	{
		return runner != null && runner.isAlive();
	}

	@Override
	public void receive(Object message, Map<String, Object> anotherInfo) throws Throwable
	{
		runner.put("recv_msg", message);
		runner.put("recv_info", anotherInfo);
		runner.execute(script);
	}
	@Override
	public String help()
	{
		// TODO Auto-generated method stub
		return null;
	}
}
