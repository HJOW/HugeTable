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
package hjow.hgtable.util.debug;

import hjow.hgtable.jscript.JScriptObject;

/**
 * <p>디버깅에 쓰이는 클래스들은 이 클래스의 하위 클래스입니다.</p>
 * 
 * @author HJOW
 *
 */
public class DebuggingUtil implements JScriptObject
{
	private static final long serialVersionUID = -8367223177415413161L;
	public static final int RELEASE    = 0;
	public static final int DEBUG      = 1;
	public static final int DEBUG_ONLY = 2;
	
	protected int scope = DEBUG;
	protected String name = "";
	
	/**
	 * <p>객체를 초기화합니다.</p>
	 */
	public void init()
	{
		
	}

	public int getScope()
	{
		return scope;
	}

	public void setScope(int scope)
	{
		this.scope = scope;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	@Override
	public void noMoreUse()
	{
		
	}

	@Override
	public boolean isAlive()
	{
		return false;
	}

	@Override
	public String help()
	{
		return null;
	}
}
