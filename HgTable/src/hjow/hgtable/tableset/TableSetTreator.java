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
package hjow.hgtable.tableset;

/**
 * <p>테이블 셋을 다루는 도구들의 상위 인터페이스입니다.</p>
 * 
 * @author HJOW
 *
 */
public interface TableSetTreator
{
	/**
	 * <p>현재 이 빌더 이름을 반환합니다.</p>
	 * 
	 * @return 테이블 셋 빌더 이름 
	 */
	public String getName();
}
