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

import hjow.hgtable.ui.module.ModuleDataPack;

/**
 * <p>스크립트로 구동되는 모듈들은 이 인터페이스를 구현합니다.</p>
 * 
 * @author HJOW
 *
 */
public interface ScriptModuleProtocol
{
	/**
	 * <p>모듈 데이터 팩 객체를 반환합니다.</p>
	 * 
	 * @return
	 */
	public ModuleDataPack toModuleDataPack();
}
