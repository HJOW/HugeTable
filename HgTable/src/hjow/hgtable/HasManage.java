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
package hjow.hgtable;

import java.util.Map;

/**
 * <p>Main 을 통해 프로그램 시작 직후 호출할 메소드가 있는 클래스가 구현하는 인터페이스입니다.</p>
 * 
 * @author HJOW
 *
 */
public interface HasManage
{
	/**
	 * <p>프로그램이 실행되면 처음 호출되는 메소드입니다.</p>
	 * <p>하위 클래스에는 이 메소드가 재정의되어 있으나, super 키워드를 사용하여 최상위 클래스의 메소드 내용이 먼저 실행되어야 합니다.</p>
	 * 
	 * @param args : 매개 변수
	 */
	public void manage(Map<String, String> args);
}
