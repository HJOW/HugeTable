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
 * <p>메뉴 상에 위치하여야 하는 모듈들이 구현하는 인터페이스입니다.</p>
 * 
 * @author HJOW
 *
 */
public interface ModuleOnMenu
{
	/**
	 * <p>이 모듈이 속할 메뉴의 이름을 반환합니다.</p>
	 * 
	 * @return 소속 메뉴 이름
	 */
	public String getMenuLocationName();
	
	/**
	 * <p>이 모듈이 속할 메뉴 코드를 반환합니다.</p>
	 * 
	 * @return 소속 메뉴 코드
	 */
	public int getMenuLocation();
	
	/**
	 * <p>이 모듈을 화면 상에 보입니다.</p>
	 * 
	 */
	public void open();
	
	/**
	 * <p>이 모듈을 화면 상에서 가리거나 닫습니다.</p>
	 * 
	 */
	public void close();
	
	/**
	 * <p>모듈 객체를 반환합니다.</p>
	 * 
	 * @return 모듈 객체
	 */
	public GUIModule getModule();
	
	/**
	 * <p>단축키 코드값을 반환합니다.</p>
	 * 
	 * @return 단축키 키에 대한 ASCII 코드값
	 */
	public Integer getShortcut();
	
	/**
	 * <p>단축키에 대한 MASK 값을 반환합니다.</p>
	 * 
	 * @return MASK 값
	 */
	public Integer getMask();
}
