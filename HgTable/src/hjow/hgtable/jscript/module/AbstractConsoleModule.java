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

import java.util.Map;

import hjow.hgtable.Manager;

/**
 * <p>콘솔용 모듈임을 구별하는 데 사용하는 인터페이스입니다. 이를 이용하면 GUI 모드와 콘솔 모드 모두를 지원하는 모듈 작성이 가능해집니다.</p>
 * 
 * @author HJOW
 *
 */
public interface AbstractConsoleModule extends AbstractModule
{
	/**
	 * <p>사용자가 모듈을 실행할 경우 호출되는 메소드입니다. 자체 메뉴를 보이고 입력을 받습니다.</p>
	 * 
	 */
	@Override
	public void manage(Map<String, String> args);
	
	/**
	 * <p>자체 메뉴를 보입니다.</p>
	 * 
	 */
	public void showMenu();
	
	/**
	 * <p>자체 메뉴에서 나갑니다.</p>
	 * 
	 */
	public void exitMenu();
	
	/**
	 * <p>사용자로부터 자체 메뉴 입력을 받을 때 보일 메시지를 반환합니다.</p>
	 * 
	 * @return 메뉴 입력 요청 메시지
	 */
	public String getAskMenuInput();
	
	/**
	 * <p>이 메시지는 사용자가 자체 메뉴에서 항목을 선택했거나 텍스트를 입력했을 때 호출됩니다.</p>
	 * 
	 * @param inputs : 사용자가 입력한 텍스트
	 * @return true 이면 작업 후 메뉴가 다시 나타납니다. false 이면 작업 후 모듈에서 나가게 됩니다.
	 */
	public boolean actMenu(String inputs);
	
	public int getMenuNumber();
	public void setMenuNumber(int menuNumber);
	public Manager getManager();
	
	/**
	 * <p>매니저 객체를 삽입합니다.</p>
	 * 
	 * @param manager : 매니저 객체
	 */
	public void setManager(Manager manager);
}
