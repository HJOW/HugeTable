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
package hjow.hgtable.ui.module;

/**
 * <p>해당 모듈이 스크립트 기반인 경우 이 인터페이스를 구현합니다.</p>
 * 
 * 
 * @author HJOW
 *
 */
public interface ScriptModule
{
	/**
	 * <p>스크립트 엔진 안에 객체를 삽입합니다.</p>
	 * 
	 * @param varName : 변수 이름
	 * @param obj : 삽입할 객체
	 */
	public void put(String varName, Object obj);
}
