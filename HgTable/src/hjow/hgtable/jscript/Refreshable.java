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

import java.util.Map;

/**
 * <p>이 인터페이스는 DAO, Manager 등의 상태가 변경되었을 때 적용 사항이 반영되어야 할 객체들이 가지고 있어야 할 메소드를 포함합니다.</p>
 * 
 * @author HJOW
 *
 */
public interface Refreshable
{
	/**
	 * <p>이 메소드는 매니저 객체로부터 호출됩니다. 호출된 사유는 additionalData 에 whyCalled 라는 원소에 텍스트로 삽입됩니다.</p>
	 * 
	 * @param additionalData : 매니저 객체로부터 받은 정보들
	 */
	public void refresh(Map<String, Object> additionalData);
}
