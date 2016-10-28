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

import hjow.hgtable.jscript.JScriptObject;
import hjow.web.core.Server;

/**
 * <p>웹 서비스를 할 수 있는 서버 객체입니다. 대부분은 hweb.jar 라이브러리의 것을 사용합니다.</p>
 * 
 * @author HJOW
 *
 */
public class WebServer extends Server implements JScriptObject
{
	private static final long serialVersionUID = -8328606001222286527L;

	@Override
	public void noMoreUse()
	{
		this.close();
	}

	@Override
	public boolean isAlive()
	{
		return this.isAlive();
	}

	@Override
	public String help()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
