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

import java.io.IOException;
import java.util.List;
import java.util.Map;

import hjow.hgtable.AbstractManager;
import hjow.hgtable.Main;
import hjow.hgtable.Manager;
import hjow.hgtable.tableset.TableSet;
import hjow.hgtable.util.ConsoleUtil;

/**
 * <p>이 클래스의 객체는 매니저 객체를 대신하여 제한된 권한 내에서 메소드들을 제공합니다. 모듈 내 스크립트에서 manager 이름으로 사용할 수 있습니다.</p>
 * <p>이 클래스는 Manager 객체와 호환되지 않으므로, Manager 객체를 필요로 하는 메소드 호출에 사용할 수 없습니다.</p>
 * 
 * @author HJOW
 *
 */
public class ManagerBroker implements AbstractManager
{
	private static final long serialVersionUID = 4006383293495866765L;
	protected Manager manager;
	protected DaoBroker daoBroker;
	
	public ManagerBroker(Manager manager)
	{
		this.manager = manager;
		daoBroker = new DaoBroker(manager);
	}

	/**
	 * <p>로그를 출력하고 줄을 띄웁니다.</p>
	 * 
	 * @param ob : 출력할 객체
	 */
	public void log(Object ob)
	{
		manager.log(ob);
	}
	
	/**
	 * <p>로그를 출력합니다.</p>
	 * 
	 * @param ob : 출력할 객체
	 */
	public void logNotLn(Object ob)
	{
		manager.logNotLn(ob);
	}
	
	/**
	 * <p>콘솔 환경에서는 log(msg) 와 동일하게 동작합니다. GUI 환경에서는 별도의 경고 대화상자를 띄워 메시지를 표시합니다.</p>
	 * 
	 * @param msg : 보일 메시지
	 */
	public void alert(Object ob)
	{
		manager.alert(String.valueOf(ob));
	}
	
	/**
	 * <p>로그를 출력하고 줄을 띄웁니다.</p>
	 * <p>현재 설정한 단계에 따라 출력 여부가 달라집니다.</p>
	 * 
	 * @param ob : 출력할 객체
	 * @param logLevel : 출력할 메시지의 의도에 해당하는 상수값
	 */
	public void log(Object ob, int logLevel)
	{
		manager.log(ob, logLevel);
	}
	/**
	 * <p>로그를 출력합니다.</p>
	 * <p>현재 설정한 단계에 따라 출력 여부가 달라집니다.</p>
	 * 
	 * @param ob : 출력할 객체
	 * @param logLevel : 출력할 메시지의 의도에 해당하는 상수값
	 */
	public void logNotLn(Object ob, int logLevel)
	{	
		manager.logNotLn(ob, logLevel);
	}
	
	/**
	 * <p>메모리 현황을 로그로 출력합니다.</p>
	 * 
	 */
	public void logMemory()
	{
		log(ConsoleUtil.memoryData(true, false));
	}
	
	/**
	 * <p>상세한 메모리 현황을 로그로 출력합니다.</p>
	 * 
	 */
	public void logMemoryDetails()
	{
		log(ConsoleUtil.memoryData(true, true));
	}
	
	/**
	 * <p>직선을 출력합니다.</p>
	 */
	public void logDrawBar()
	{
		manager.logDrawBar();
	}
	
	/**
	 * <p>TableSet 객체를 로그로 출력합니다.</p>
	 * 
	 * @param table : TableSet 객체
	 */
	public void logTable(TableSet table)
	{
		manager.logTable(table);
	}
	
	/**
	 * <p>예외 혹은 오류를 출력합니다.</p>
	 * 
	 * @param e : Exception 객체
	 * @param addMsg : 오류 발생 상황을 알 수 있게 해 주는 추가 메시지
	 */
	public void logError(Throwable e, String addMsg)
	{
		manager.logError(e, addMsg);
	}
	
	/**
	 * <p>예외 혹은 오류를 출력합니다.</p>
	 * 
	 * @param e : Exception 객체
	 * @param addMsg : 오류 발생 상황을 알 수 있게 해 주는 추가 메시지
	 * @param simplify : true 시 자세한 스택 추적 내용을 출력하지 않습니다.
	 */
	public void logError(Throwable e, String addMsg, boolean simplify)
	{
		manager.logError(e, addMsg, simplify);
	}
	
	/**
	 * <p>사용자에게 y 혹은 n 입력을 받습니다. 결과값은 y의 경우 true, n의 경우 false 로 반환합니다.</p>
	 * 
	 * @param msg : 입력 받을 때 보일 메시지
	 * @return 사용자의 입력 결과
	 */
	public boolean askYes(String msg)
	{
		return manager.askYes(msg);
	}
	
	/**
	 * <p>사용자에게 문장을 입력받습니다. 여러 줄로 된 긴 문장을 입력받으려는 경우 사용자가 ; 기호를 입력할 때까지 입력받게 됩니다.</p>
	 * 
	 * @param msg : 입력 받을 때 보일 메시지
	 * @param isShort : 한 줄 입력 받기 여부 선택, true 이면 엔터 입력 시 입력이 완료됩니다. false 시 ; 입력 후 엔터 입력 시 완료됩니다.
	 * @return 사용자의 입력 결과
	 */
	public String askInput(String msg, boolean isShort)
	{
		return manager.askInput(msg, isShort);
	}
	
	/**
	 * <p>SQL 문장을 입력받습니다.</p>
	 * 
	 * @return SQL 문장
	 */
	public String askQuery(String msg)
	{
		return manager.askQuery(msg);
	}
	
	/**
	 * <p>맵 객체를 입력받습니다.</p>
	 * 
	 * @param msg : 사용자에게 보일 메시지
	 * @return 맵 객체
	 */
	public Map<String, String> askMap(String msg)
	{
		return manager.askMap(msg);
	}
	
	/**
	 * <p>맵 객체를 입력받습니다.</p>
	 * 
	 * @param msg : 사용자에게 보일 메시지
	 * @param befores : 이전에 입력한 데이터 (null 가능)
	 * @return 맵 객체
	 */
	public Map<String, String> askMap(String msg, Map<String, String> befores)
	{
		return manager.askMap(msg, befores);
	}
	
	/**
	 * <p>사용자에게 문장을 입력받습니다. 사용자가 ; 기호를 입력할 때까지 입력받게 됩니다.</p>
	 * 
	 * @param msg : 입력 받을 때 보일 메시지
	 * @return 사용자의 입력 결과
	 */
	public String askInput(String msg)
	{
		return askInput(msg, false);
	}
	
	/**
	 * <p>환경 설정 값을 반환합니다.</p>
	 * 
	 * @param key : 환경 설정 키
	 * @return 환경 설정 값
	 */
	public String getOption(String key)
	{
		return Manager.getOption(key);
	}
	
	/**
	 * <p>현재 적용되어 있는 환경 설정 키들을 반환합니다.</p>
	 * 
	 * @return 환경 설정 키들
	 */
	public List<String> optionList()
	{
		return Manager.optionList();
	}
	
	
	/**
	 * <p>현재 선택된 DAO 정보들을 반환합니다. 설정된 비밀번호는 반환되지 않습니다.</p>
	 * 
	 * @return DAO 리스트 정보
	 */
	public List<String> daoList()
	{
		return manager.daoList();
	}
	
	/**
	 * <p>현재 선택된 DAO 정보를 반환합니다. 설정된 비밀번호는 반환되지 않습니다.</p>
	 * 
	 * @return 선택된 DAO 정보
	 */
	public String getSelectedDao()
	{
		return Main.getSelectedDao();
	}
	
	/**
	 * <p>DAO 대행 객체를 반환합니다. 이 DAO 대행 객체는 선택된 DAO를 통해 작업을 대신 수행하고, 일부 권한 없는 작업은 거부합니다.</p>
	 * 
	 * @return DAO 대행 객체
	 */
	public DaoBroker getDao()
	{
		return daoBroker;
	}

	@Override
	public void noMoreUse()
	{
		manager = null;
		daoBroker.noMoreUse();
	}

	@Override
	public String help()
	{
		return null;
	}

	@Override
	public boolean isAlive()
	{
		return false;
	}

	@Override
	public void run()
	{
		
	}

	@Override
	public void manage(Map<String, String> args)
	{
		
	}

	@Override
	public void refresh(Map<String, Object> additionalData)
	{
		
	}

	@Override
	public byte[] askFile(String msg) throws IOException
	{
		return manager.askFile(msg);
	}

	@Override
	public void askSave(String msg, byte[] bytes) throws IOException
	{
		manager.askSave(msg, bytes);
	}

	@Override
	public void activeOrder(String order, Module module) {
	    manager.activeOrder(order, module);
		
	}
}
