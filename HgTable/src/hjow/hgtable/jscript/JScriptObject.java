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

import hjow.hgtable.ui.NeedtoEnd;

import java.io.Serializable;

public interface JScriptObject extends Serializable, NeedtoEnd
{
	/**
	 * <p>해당 객체에 대한 도움말을 반환합니다. 스크립트 내에서 help() 메소드에 매개 변수로 넣으면 이 메소드가 호출됩니다.</p>
	 * 
	 * @return 도움말
	 */
	public String help();
}
