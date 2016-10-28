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

import hjow.hgtable.Manager;

/**
 * <p>스크립트 실행 모듈 생성 클래스입니다.</p>
 * 
 * @author HJOW
 *
 */
public class OtherScriptRunner extends JavaScriptRunner
{
	/**
	 * <p>엔진 생성자입니다.</p>
	 * 
	 * @param manager : 매니저 객체
	 */
	public OtherScriptRunner(Manager manager, String scriptType)
	{
		super(manager, scriptType);
	}
	@Override
	protected void initEngine()
	{
		javax.script.ScriptEngineManager scriptEngine = new javax.script.ScriptEngineManager();
		engine = scriptEngine.getEngineByName(engineType);
	}
}
