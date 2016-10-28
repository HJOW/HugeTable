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

import java.util.List;
import java.util.Map;

import hjow.hgtable.jscript.JScriptObject;
import hjow.hgtable.jscript.Refreshable;
import hjow.hgtable.ui.module.ModuleDataPack;

/**
 * <p>모듈 객체임을 나타내는 인터페이스입니다.</p>
 * 
 * @author HJOW
 *
 */
public interface AbstractModule extends JScriptObject, Runnable, Refreshable
{
	/**
	 * <p>데이터를 삽입합니다.</p>
	 * 
	 * @param packs : 데이터를 읽어 온 단위
	 */
	public void input(ModuleDataPack packs);
	
	/**
	 * <p>모듈 이름을 반환합니다.</p>
	 * 
	 * @return 모듈 이름
	 */
	public String getName();
	
	/**
	 * <p>이 메소드는 매니저 객체로부터 호출됩니다. 호출된 사유는 additionalData 에 whyCalled 라는 원소에 텍스트로 삽입됩니다.</p>
	 * 
	 * @param additionalData : 매니저 객체로부터 받은 정보들
	 */
	@Override
	public void refresh(Map<String, Object> additionalData);
	
	
	/**
	 * <p>이 모듈이 OS 터미널 명령을 실행할 수 있는 권한이 있는지 여부를 반환합니다. J스크립트 모듈에서만 유효합니다.</p>
	 * 
	 * @return OS 터미널 명령을 실행할 수 있는 권한 여부
	 */
	public boolean isPriv_allowCommand();
	
	/**
	 * <p>이 모듈이 다른 디렉토리 상의 클래스를 불러오는 명령을 실행할 수 있는 권한이 있는지 여부를 반환합니다. J스크립트 모듈에서만 유효합니다.</p>
	 * 
	 * @return 다른 디렉토리 상의 클래스 불러오기 권한 여부
	 */
	public boolean isPriv_allowLoadClass();
	
	/**
	 * <p>이 모듈이 새 쓰레드 생성 명령을 실행할 수 있는 권한이 있는지 여부를 반환합니다. J스크립트 모듈에서만 유효합니다.</p>
	 * 
	 * @return 쓰레드 사용 여부
	 */
	public boolean isPriv_allowThread();
	
	/**
	 * <p>이 모듈이 보조기억장치에 액세스해 읽는 작업을 할 수 있는 디렉토리 경로들을 반환합니다.</p>
	 * <p>null 이 반환되면 모든 디렉토리에 대해 권한이 허용된 것입니다. 빈 리스트가 반환되면 파일 읽기 권한이 아예 없는 것입니다.</p>
	 * 
	 * @return 읽기 허용 디렉토리 경로들
	 */
	public List<String> getPriv_allowReadFilePath();
	
	/**
	 * <p>이 모듈이 보조기억장치에 액세스해 쓰는 작업을 할 수 있는 디렉토리 경로들을 반환합니다.</p>
	 * <p>null 이 반환되면 모든 디렉토리에 대해 권한이 허용된 것입니다. 빈 리스트가 반환되면 파일 쓰기 권한이 아예 없는 것입니다.</p>
	 * 
	 * @return 쓰기 허용 디렉토리 경로들
	 */
	public List<String> getPriv_allowWriteFilePath();
	
	/**
	 * <p>쓰레드 사용이 필요한 모듈인지 여부를 반환합니다. true 반환 시 쓰레드가 자동 시작됩니다.</p>
	 * 
	 * @return 쓰레드 사용 필요 여부
	 */
	public boolean needThread();
	
	/**
	 * <p>쓰레드를 실행합니다.</p>
	 * 
	 */
	public void startThread();
	
	/**
	 * <p>모듈에 대한 설명문을 반환합니다.</p>
	 * 
	 * @return 설명글
	 */
	public String description();
	
	/**
	 * <p>이 메소드는 매니저 객체가 컴포넌트들을 초기화할 때 호출됩니다. 이 메소드를 재정의하는 대신 initializeComponent() 를 재정의하는 것을 권장합니다.</p>
	 */
	public void init();
	
	/**
	 * <p>이 메소드는 컴포넌트들이 초기화되고 한 번만 호출됩니다. 이 메소드를 재정의하는 대신 doAfterInitialize() 를 재정의하는 것을 권장합니다.</p>
	 * 
	 */
	public void doAfterInit();
	
	/**
	 * <p>이 메소드에서 컴포넌트들을 초기화합니다. 모듈 내 컴포넌트들을 초기화하기 위해 이 메소드를 재정의합니다.</p>
	 * 
	 */
	public void initializeComponents();
	
	/**
	 * <p>이 메소드에서 초기화 직후 수행할 작업들을 합니다. 이 메소드를 재정의하여 사용합니다.</p>
	 * 
	 */
	public void doAfterInitialize();
	
	/**
	 * <p>모듈의 고유 ID 값을 반환합니다.</p>
	 * 
	 * @return 모듈 ID
	 */
	public long getModuleId();
	
	/**
	 * <p>현재 이 모듈에 설정된 옵션들을 반환합니다.</p>
	 * 
	 * @return 옵션들을 포함하는 맵 객체
	 */
	public Map<String, String> getOptions();
	
	/**
	 * <p>이 모듈의 옵션을 통째로 대체합니다.</p>
	 * 
	 * @param options : 옵션들을 담은 맵 객체
	 */
	public void setOptions(Map<String, String> options);
	
	/**
	 * <p>이 모듈의 라이센스 문장을 반환합니다.</p>
	 * 
	 * @return 라이센스
	 */
	public String getLicense();
	
	/**
	 * <p>사용자가 모듈을 직접 실행할 경우 호출되는 메소드입니다.</p>
	 * 
	 */
	public void manage();
	
	/**
	 * <p>사용자가 모듈을 직접 실행할 경우 호출되는 메소드입니다.</p>
	 * 
	 * @param args : 실행 시 입력한 매개 변수들
	 */
	public void manage(Map<String, String> args);
	
	/**
	 * <p>스트링 테이블을 적용한 텍스트를 반환합니다. 모듈 자체 스트링 테이블 내에 해당 항목이 있는지 먼저 검사합니다.</p>
	 * 
	 * @param text : 대상 텍스트
	 * @return 스트링 테이블에 의해 번역된 텍스트
	 */
	public String trans(String text);
}
