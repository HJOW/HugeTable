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
package hjow.hgtable.favorites;

import java.util.Map;
import java.util.Set;

import hjow.hgtable.jscript.JScriptObject;

/**
 * <p>자주 사용하는 스크립트를 다루는 데 쓰이는 클래스입니다.</p>
 */
public interface AbstractFavorites extends JScriptObject
{
	public Long getUniqueId();
	public String getName();
	public String getLanguage();
	public String getScript();
	public String getDescription();
	public Integer getShortcut();
	public Integer getMask();
	public Set<String> getParameterSet();
	
	/**
	 * <p>실행할 스크립트를 반환합니다.</p>
	 * 
	 * @param params : 매개 변수들
	 * @return 스크립트
	 */
	public String getScript(Map<String, String> params);
	
	/**
	 * <p>문자열 형태로 직렬화합니다.</p>
	 * 
	 * @return 문자열
	 */
	public String serialize();
	
	/**
	 * <p>자주 사용하는 스크립트의 유형을 반환합니다.</p>
	 * 
	 * @return 자주 사용하는 스크립트 유형
	 */
	public String getType();
}
