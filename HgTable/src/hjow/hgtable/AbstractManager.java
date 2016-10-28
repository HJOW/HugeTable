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

import java.io.IOException;
import java.util.Map;

import hjow.hgtable.jscript.JScriptObject;
import hjow.hgtable.jscript.Refreshable;
import hjow.hgtable.jscript.module.Module;
import hjow.hgtable.tableset.TableSet;

/**
 * <p>사용자와의 의사 소통을 담당하는 클래스들이 구현하는 인터페이스입니다.</p>
 * 
 * @author HJOW
 *
 */
public interface AbstractManager extends Runnable, JScriptObject, HasManage, Refreshable
{
	/**
	 * 매니저 객체가 지원하는 명령어를 실행합니다.
	 * 
	 * @param order : 명령
	 * @param module : 명령을 실행하려는 모듈 객체 (보통 this 사용)
	 */
	public void activeOrder(String order, Module module);
	/**
	 * <p>로그를 출력하고 줄을 띄웁니다.</p>
	 * 
	 * @param ob : 출력할 객체
	 */
	public void log(Object ob);
	
	/**
	 * <p>로그를 출력합니다.</p>
	 * 
	 * @param ob : 출력할 객체
	 */
	public void logNotLn(Object ob);
	
	/**
	 * <p>콘솔 환경에서는 log(msg) 와 동일하게 동작합니다. GUI 환경에서는 별도의 경고 대화상자를 띄워 메시지를 표시합니다.</p>
	 * 
	 * @param msg : 보일 메시지
	 */
	public void alert(Object ob);
	
	/**
	 * <p>로그를 출력하고 줄을 띄웁니다.</p>
	 * <p>현재 설정한 단계에 따라 출력 여부가 달라집니다.</p>
	 * 
	 * @param ob : 출력할 객체
	 * @param logLevel : 출력할 메시지의 의도에 해당하는 상수값
	 */
	public void log(Object ob, int logLevel);
	
	/**
	 * <p>로그를 출력합니다.</p>
	 * <p>현재 설정한 단계에 따라 출력 여부가 달라집니다.</p>
	 * 
	 * @param ob : 출력할 객체
	 * @param logLevel : 출력할 메시지의 의도에 해당하는 상수값
	 */
	public void logNotLn(Object ob, int logLevel);
	
	/**
	 * <p>직선을 출력합니다.</p>
	 */
	public void logDrawBar();
	
	/**
	 * <p>TableSet 객체를 로그로 출력합니다.</p>
	 * 
	 * @param table : TableSet 객체
	 */
	public void logTable(TableSet table);
	
	/**
	 * <p>예외 혹은 오류를 출력합니다.</p>
	 * 
	 * @param e : Exception 객체
	 * @param addMsg : 오류 발생 상황을 알 수 있게 해 주는 추가 메시지
	 */
	public void logError(Throwable e, String addMsg);
	
	/**
	 * <p>예외 혹은 오류를 출력합니다.</p>
	 * 
	 * @param e : Exception 객체
	 * @param addMsg : 오류 발생 상황을 알 수 있게 해 주는 추가 메시지
	 * @param simplify : true 시 자세한 스택 추적 내용을 출력하지 않습니다.
	 */
	public void logError(Throwable e, String addMsg, boolean simplify);
	
	/**
	 * <p>사용자에게 y 혹은 n 입력을 받습니다. 결과값은 y의 경우 true, n의 경우 false 로 반환합니다.</p>
	 * 
	 * @param msg : 입력 받을 때 보일 메시지
	 * @return 사용자의 입력 결과
	 */
	public boolean askYes(String msg);
	
	/**
	 * <p>사용자에게 문장을 입력받습니다. 여러 줄로 된 긴 문장을 입력받으려는 경우 사용자가 ; 기호를 입력할 때까지 입력받게 됩니다.</p>
	 * 
	 * @param msg : 입력 받을 때 보일 메시지
	 * @param isShort : 한 줄 입력 받기 여부 선택, true 이면 엔터 입력 시 입력이 완료됩니다. false 시 ; 입력 후 엔터 입력 시 완료됩니다.
	 * @return 사용자의 입력 결과
	 */
	public String askInput(String msg, boolean isShort);
	
	/**
	 * <p>사용자에게 문장을 입력받습니다. 여러 줄로 된 긴 문장을 입력받으려는 경우 사용자가 ; 기호를 입력할 때까지 입력받게 됩니다.</p>
	 * 
	 * @param msg : 입력 받을 때 보일 메시지
	 * @param isShort : 한 줄 입력 받기 여부 선택, true 이면 엔터 입력 시 입력이 완료됩니다. false 시 ; 입력 후 엔터 입력 시 완료됩니다.
	 * @return 사용자의 입력 결과
	 */
	public String askInput(String msg);
	
	/**
	 * <p>SQL 문장을 입력받습니다.</p>
	 * 
	 * @return SQL 문장
	 */
	public String askQuery(String msg);
	
	/**
	 * <p>맵 객체를 입력받습니다.</p>
	 * 
	 * @param msg : 사용자에게 보일 메시지
	 * @return 맵 객체
	 */
	public Map<String, String> askMap(String msg);
	
	/**
	 * <p>맵 객체를 입력받습니다.</p>
	 * 
	 * @param msg : 사용자에게 보일 메시지
	 * @param befores : 이전에 입력한 데이터 (null 가능)
	 * @return 맵 객체
	 */
	public Map<String, String> askMap(String msg, Map<String, String> befores);
	
	/**
	 * <p>파일을 선택받습니다. 내용은 바이트 배열로 받습니다.</p>
	 * 
	 * @param msg : 입력받을 때 보일 메시지
	 * @return 바이트 배열 (파일 내용)
	 * @throws IOException 입출력 관련 오류
	 */
	public byte[] askFile(String msg) throws IOException;
	
	/**
	 * <p>저장할 장소와 이름을 선택받습니다. 내용은 바이트 배열로 넣어야 합니다.</p>
	 * 
	 * @param msg : 입력받을 때 보일 메시지
	 * @param bytes : 저장할 내용
	 * @throws IOException 입출력 관련 오류
	 */
	public void askSave(String msg, byte[] bytes) throws IOException;
}
