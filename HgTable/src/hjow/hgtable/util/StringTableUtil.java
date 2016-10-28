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

package hjow.hgtable.util;

import hjow.hgtable.Main;
import hjow.hgtable.Manager;
import hjow.hgtable.util.StreamUtil;
import hjow.hgtable.stringtable.InvalidPrefixException;
import hjow.hgtable.stringtable.StringTable;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * <p>언어 설정(스트링 테이블) 관리에 사용되는 여러 정적 메소드를 제공하는 클래스입니다.</p>
 * 
 * @author HJOW
 *
 */
public class StringTableUtil
{
	public static final String LINE_PREFIX_TARGET = "TARGET:";
	public static final String LINE_PREFIX_SET    = "SET:";
	public static final String LINE_PREFIX_URL    = "URL:";
	public static final String LINE_PREFIX_FILE   = "FILE:";
	public static final String LINE_PREFIX_NULL   = "NULL:";
	public static final String LINE_PREFIX_END    = "END:";
	public static final String LINE_PREFIX_GOTO   = "GOTO:";
	public static final String LINE_PREFIX_IF     = "IF:";
	public static final String LINE_PREFIX_THEN   = "THEN:";
	
	
	/**
	 * <p>스트링 테이블에 기본 내용을 추가합니다. 시스템 언어 설정을 따릅니다.</p>
	 * 
	 * @param stringTable 스트링 테이블
	 */
	public static void defaultData(StringTable stringTable)
	{
		String locale = Manager.getOption("user_language");
		if(locale == null) locale = System.getProperty("user.language");
		if(locale.equalsIgnoreCase("auto")) locale = System.getProperty("user.language");
		
		if(locale.equalsIgnoreCase("ko") || locale.equalsIgnoreCase("kr")) // 한글
		{
			stringTable.put("DB Connection", "DB 연결");
			stringTable.put("not connected", "연결되지 않음");
			stringTable.put("Main Menu"    , "메인 메뉴");
			stringTable.put("Connect"      , "접속");
			stringTable.put("Disconnect"   , "접속 해제");
			stringTable.put("Query"        , "쿼리");
			stringTable.put("See file contents", "file 내용 보기");
			stringTable.put("DB --> file"  , "DB --> file");
			stringTable.put("file --> DB"  , "file --> DB");
			stringTable.put("Preference"   , "환경설정");
			stringTable.put("Use Module"   , "모듈 사용");
			stringTable.put("Exit"         , "종료");
			stringTable.put("Restart"      , "다시 시작");
			stringTable.put("Exit menu"    , "메뉴에서 나가기");
			stringTable.put("Commit"       , "커밋");
			stringTable.put("Rollback"     , "롤백");
			stringTable.put("Transaction"  , "트랜잭션");
			stringTable.put("Run single line", "단일 J스크립트 실행");
			stringTable.put("Disable", "비활성화");
			stringTable.put("Accept to use", "사용 동의");
			stringTable.put("Authorized", "인증됨");
			stringTable.put("Accepted", "사용에 동의함");
			stringTable.put("Default Module", "기본 제공된 모듈");
			stringTable.put("New tab", "새 탭");
			stringTable.put("Console", "콘솔");
			stringTable.put("Dialog", "대화 상자");
			stringTable.put("Panel", "패널");
			stringTable.put("TableSet", "테이블 셋");
			stringTable.put("Save", "저장");
			stringTable.put("Load", "불러오기");
			stringTable.put("Directory", "폴더");
			stringTable.put("Help", "도움말");
			stringTable.put("OK", "확인");
			stringTable.put("Cancel", "취소");
			stringTable.put("Copy", "복사");
			stringTable.put("Paste", "붙여넣기");
			stringTable.put("Cut", "잘라내기");
			stringTable.put("Review", "미리 보기");
			stringTable.put("Next", "다음");
			stringTable.put("Before", "뒤로");
			stringTable.put("Back", "돌아가기");
			stringTable.put("Send", "보내기");
			stringTable.put("Put", "삽입");
			stringTable.put("Trying to prevent timeout", "접속 끊김을 방지하기 위해 스크립트 실행을 시도합니다.");
			stringTable.put("I don't agree", "동의하지 않습니다.");
			stringTable.put("Add", "추가");
			stringTable.put("New", "추가");
			stringTable.put("Add Directory", "폴더 추가");
			stringTable.put("Remove Selected Files From List", "선택한 파일들을 목록에서 제거");
			stringTable.put("Remove", "제거");
			stringTable.put("Close", "닫기");
			stringTable.put("Clean", "정리");
			stringTable.put("Clear Empty Row", "빈 행 제거");
			stringTable.put("New Column", "새 컬럼");
			stringTable.put("Column", "컬럼");
			stringTable.put("Size", "크기");
			stringTable.put("Clear All", "전부 제거");
			stringTable.put("Execute", "실행");
			stringTable.put("Execute All", "모두 실행");
			stringTable.put("Table name", "테이블 이름");
			stringTable.put("Table List", "테이블 목록");
			stringTable.put("View List", "뷰 목록");
			stringTable.put("Procedure List", "프로시저 목록");
			stringTable.put("Function List", "함수 목록");
			stringTable.put("Memory Viewer", "메모리 관리");
			stringTable.put("Total Memory", "전체 메모리");
			stringTable.put("Free Memory", "여유 메모리");
			stringTable.put("Using Memory", "사용 중인 메모리");
			stringTable.put("Max Memory", "최대 메모리");
			stringTable.put("HTTP Tool", "HTTP 도구");
			stringTable.put("Analyze Function", "분석 함수");
			stringTable.put("Thread View", "쓰레드 보기");
			stringTable.put("Massive Files Executor", "다수 파일 실행 도구");
			stringTable.put("Massive Downloader", "다수 파일 저장 도구");
			stringTable.put("View", "보기");
			stringTable.put("Search", "검색");
			stringTable.put("Add new option", "새 옵션 추가");
			stringTable.put("KEY", "키");
			stringTable.put("VALUE", "값");
			stringTable.put("From file...", "파일로부터...");
			stringTable.put("About...", "이 프로그램에 대하여");
			stringTable.put("Argument Injector for SQL", "SQL용 매개 변수 도구");
			stringTable.put("Add JAR entry", "JAR 추가");
			stringTable.put("Not available", "사용할 수 없음");
			stringTable.put("Selected JAR file is accepted.", "선택한 JAR 파일이 등록되었습니다.");
			stringTable.put("Need restart to apply changes.", "이 프로그램을 종료 후 다시 실행해야 변경 사항이 적용됩니다.");
			stringTable.put("Type SELECT SQL statement here", "이 곳에 SELECT 문을 입력하십시오.");
			stringTable.put("Driver or Data Source name", "드라이버");
			stringTable.put("Password", "암호");
			stringTable.put("Insert this to DB", "DB에 삽입");
			stringTable.put("What do you want to do?", "작업을 선택하십시오.");
			stringTable.put("Input Data source name, or just input space.", "데이터 소스 이름을 입력하거나 엔터 키를 눌러 넘어가십시오.");
			stringTable.put("Input IP address of Oracle server.", "접속하려는 오라클 서버의 IP를 입력하십시오.");
			stringTable.put("Input SID of Oracle server.", "오라클 서버의 서비스 이름을 입력하십시오.");
			stringTable.put("Please input DB account ID", "계정 ID를 입력하십시오.");
			stringTable.put("Please input DB account PW", "계정 비밀번호를 입력하십시오. (주변에 다른 사람이 있는지 확인하십시오 !)");
			stringTable.put("Input port number of Oracle server.", "접속하려는 오라클 서버의 포트 번호를 입력하십시오.");
			stringTable.put("Trying to connect at", "다음 DB에 접속 중 :");
			stringTable.put("Connected at", "다음 DB에 연결됨 :");
			stringTable.put("as", "의");
			stringTable.put("JScript mode is started.", "JScript 실행 모드에 들어왔습니다.");
			stringTable.put("If you want to exit JScript mode, just input \"exit\".", "JScript 실행 모드를 끝내려면 exit 를 입력하십시오.");
			stringTable.put("Preparing to exit...", "종료하고 있습니다.");
			stringTable.put("There are so many elements not in string table", "다음과 같은 문구들이 언어 설정에 없습니다.");
			stringTable.put("How about see manager.help() or dao.help()?", "manager.help() 혹은 dao.help() 를 통해 도움말을 더 보실 수 있습니다.");
			stringTable.put("On running following script", "다음과 같은 스크립트를 실행하는 중 문제가 발생하였습니다.");
			stringTable.put("On running thread", "쓰레드 실행 중 문제가 발생하였습니다.");
			stringTable.put("On reading jscript file", "JScript 파일을 읽는 중 문제가 발생하였습니다.");
			stringTable.put("On making directory to save file", "파일로 저장하기 위해 디렉토리를 생성하는 중 문제가 발생하였습니다.");
			stringTable.put("On saving file", "파일을 저장하는 중 문제가 발생하였습니다.");
			stringTable.put("Cannot cancel requests on this connection.", "요청을 취소할 수 없습니다. 접속된 데이터 소스(DB)가 요청 취소 기능을 지원하지 않는 것 같습니다.");
			stringTable.put("There is no requests to cancel.", "취소할 요청이 없습니다.");
			stringTable.put("Current JDBC PreparedStatement cannot make updatable and return-generated-keys ResultSet. See following error message..."
					, "현재 접속에 사용 중인 JDBC 드라이버의 PreparedStatement 가 업데이트 및 키 생성 반환 기능을 가진 ResultSet 생성을 지원하지 않습니다. 다음 오류 메시지를 참고하십시오.");
			stringTable.put("Cannot use ResultSetTableSet type.", "ResultSetTableSet 타입 테이블 셋을 사용할 수 없습니다.");
			stringTable.put("Try to using default PreparedStatement...", "기본 설정으로 PreparedStatement 을 사용합니다.");
		    stringTable.put("Default table set type is changed to DefaultTableSet.", "기본 설정 테이블셋 타입이 DefaultTableSet 으로 변경되었습니다.");
			stringTable.put("Input the identifier name which identify this table set.", "이 테이블 셋을 참조할 변수 이름을 입력하십시오.");
			stringTable.put("This table set is in script engine named", "이 테이블 셋이 스크립트 엔진에 다음과 같은 변수 이름으로 삽입되었습니다 : ");
			stringTable.put("Run", "실행");
			stringTable.put("End", "끝");
			stringTable.put("Default", "기본");
			stringTable.put("Undo", "되돌리기");
			stringTable.put("Redo", "다시 원래대로");
			stringTable.put("Keyword", "키워드");
			stringTable.put("Replace", "바꾸기");
			stringTable.put("Replaces", "바꿀 대상");
			stringTable.put("Replace All", "전부 바꾸기");
			stringTable.put("Case Sensitive", "대소문자 구분");
			stringTable.put("Name", "이름");
			stringTable.put("Type", "종류");
			stringTable.put("Unique ID", "고유 번호");
			stringTable.put("Init", "초기화");
			stringTable.put("After inited", "초기화 직후");
			stringTable.put("Refresh", "새로 고침");
			stringTable.put("Auto Refresh", "자동 새로 고침");
			stringTable.put("Finalize", "종료");
			stringTable.put("Show Menu", "메뉴 보이기");
			stringTable.put("Act Menu", "메뉴 동작");
			stringTable.put("Options", "옵션");
			stringTable.put("Table Name", "테이블 이름");
			stringTable.put("Create Table", "테이블 생성");
			stringTable.put("Create", "생성");
			stringTable.put("Module Developer", "모듈 개발 도구");
			stringTable.put("Module List", "모듈 목록");
			stringTable.put("Session View", "세션 도구");
			stringTable.put("Basic Information", "기본 정보");
			stringTable.put("Save console contents", "콘솔 내용 저장");
			stringTable.put("Initializing complete", "초기화를 완료하였습니다.");
			stringTable.put("There is no license policy for", "다음에 대한 라이센스 정책이 없습니다 : ");
			stringTable.put("There is no additional privilege to use", "추가 권한 필요 없음 : ");
			stringTable.put("can access following pathes to read contents.", "은/는 다음 경로들에 액세스하여 정보를 읽을 수 있습니다.");
			stringTable.put("can access following pathes to write contents.", "은/는 다음 경로들에 액세스하여 정보를 쓸 수 있습니다.");
			stringTable.put("Input SQL scripts. Input ; to end inputs.", "SQL문을 입력하십시오. 여러 줄 입력이 가능합니다. 입력이 완료되었다면 ; 기호를 입력하십시오.");
			stringTable.put("There is no table", "해당 테이블이 없거나 실행 결과가 없습니다.");
			stringTable.put("There is no data in table", "테이블 내에 데이터가 없습니다.");
			stringTable.put("There is no data in table", "결과가 없습니다.");
			stringTable.put("ROWNUM", "번호");
			stringTable.put("Already connected", "이미 접속되어 있습니다");
			stringTable.put("Run Selected Script", "선택 영역 실행");
			stringTable.put("On loading class file", "클래스 파일을 불러오는 중 문제가 발생하였습니다.");
			stringTable.put("On loading class", "클래스를 불러오는 중 문제가 발생하였습니다.");
			stringTable.put("There is no options applied.", "적용된 환경 설정이 없습니다.");
			stringTable.put("There is no connection, or connection was closed before.", "접속이 되지 않았거나 접속이 끊어졌습니다.");
			stringTable.put("Do you want to input or change something?", "환경 설정을 변경하거나 추가하시겠습니까?");
			stringTable.put("Please input key of option.", "추가/수정할 환경 설정의 키 값을 입력해 주세요.");
			stringTable.put("Please input", "다음");
			stringTable.put("'s value.", "의 값을 입력해 주세요.");
			stringTable.put("Option is applied.", "환경 설정이 적용되었습니다.");
			stringTable.put("String table of Huge Table", "Huge Table의 언어 설정입니다.");
			stringTable.put("The line starts with $ means target, and the line starts with @ means result."
					, "$으로 시작하는 줄이 바꿀 대상, @으로 시작하는 줄이 바꿀 결과값입니다.");
			stringTable.put("Are you sure?", "정말로 이 작업을 계속 수행하시겠습니까?");
			stringTable.put("Do you want to insert this into DB?", "이 항목을 DB에 삽입하시겠습니까?");
			stringTable.put("Input the table name of DB.", "테이블 이름을 입력하십시오.");
			stringTable.put("JScript", "J스크립트");
			stringTable.put("Trying to connect directly with", "다음 파일로부터 접속 가능한 드라이버를 찾고 있습니다");
			stringTable.put("Do you want to save this table set into the attribute store?", "이 항목을 임시 저장소에 저장하시겠습니까?");
			stringTable.put("Input the attribute name you want.", "키 이름을 지정하십시오. 이후 임시 저장소에서 꺼낼 때 식별자로 사용됩니다.");
			stringTable.put("Preparing the following SQL", "다음과 같은 SQL 문 실행을 준비하고 있습니다");
			stringTable.put("Executing SQL", "다음과 같은 SQL 문을 실행하고 있습니다");
			stringTable.put("File", "파일");
			stringTable.put("Tool", "도구");
			stringTable.put("Accept", "동의");
			stringTable.put("Decline", "거절");
			stringTable.put("Module", "모듈");
			stringTable.put("Favorites", "자주 쓰는 스크립트");
			stringTable.put("Favorite Editor", "자주 쓰는 스크립트 편집기");
			stringTable.put("Information", "정보");
			stringTable.put("Treat", "조작");
			stringTable.put("Script", "스크립트");
			stringTable.put("Language", "언어");
			stringTable.put("Shortcut", "단축키");
			stringTable.put("Thread", "쓰레드");
			stringTable.put("Priority", "우선순위");
			stringTable.put("State", "상태");
			stringTable.put("Stop thread", "쓰레드 멈추기");
			stringTable.put("is declined.", "이/가 거부되었습니다.");
			stringTable.put("This operation can harm system. Do you want to do it?", "이 작업은 시스템에 해를 줄 수 있습니다. 계속 진행하시겠습니까?");
			stringTable.put("Do you want to use", "다음 사항을 사용하시겠습니까 : ");
			stringTable.put("Try to convert table set", "테이블 셋으로 변환 시도");
			stringTable.put("Initializing GUI toolkits", "GUI 툴킷 초기화 중입니다");
			stringTable.put("Loading default libraries finished.", "기본 라이브러리를 불러오는 작업이 끝났습니다.");
			stringTable.put("In Huge Table, another libraries are included.", "Huge Table 개발에는 여러 라이브러리가 사용되었습니다.");
			stringTable.put("You should see these license agreements.", "이러한 라이브러리는 각각 사용 라이센스가 있으므로, 이러한 라이센스를 읽고 동의해야 사용하실 수 있습니다.");
			stringTable.put("If you don't agree of these, you cannot use these figures on this program.", "이러한 라이센스에 동의하지 않으면, 해당 라이브러리가 사용되는 기능을 사용할 수 없습니다.");
			stringTable.put("You can see these license agreements on another tab in this dialog."
					, "현재 보고 있는 대화 상자의 상단에 있는 탭을 클릭하여 각각에 대한 라이센스를 보실 수 있습니다.");
			stringTable.put("Huge Table following Apache License 2.0, same as Apache POI.", "Huge Table 또한 Apache POI와 같은 Apache License 2.0을 따릅니다.");
			stringTable.put("But, it doesn't means \'Huge Table is developed by Apache Software Foundation.\'", "그러나, Huge Table 은 아파치 소프트웨어 재단에서 개발된 것이 아닙니다.");
			stringTable.put("So, you cannot get supports about Huge Table from Apache Software Foundation.", "그러므로 아파치 소프트웨어 재단으로부터 Huge Table 에 대한 지원을 받을 수는 없습니다.");
			stringTable.put("If you press or click OK button in this dialog, it means you agree all of these licenses."
					, "이 대화 상자에서 확인 버튼을 클릭한다면, 이러한 라이센스들 모두에 동의한다고 인정하는 것입니다.");
			stringTable.put("For example, if you cannot agree MariaDB Connector libraries license, you should not connect to MariaDB Database with this program."
					, "예를 들어, MariaDB Connector 라이브러리에 대한 라이센스에 동의하지 않는다면, 이 프로그램으로 MariaDB 에 접속해서는 안 됩니다.");
			stringTable.put("Cannot use class autoloader because this feature needs Google Guava library.\n"
					+ "If you want to use it, download this library jar file into the lib directory.", "클래스 자동로드 기능을 사용하려면 Google Guava 라이브러리가 필요합니다.\nlib 폴더에 이 라이브러리 jar 파일을 다운로드 받아 넣으면 이 기능이 동작할 것입니다.");
			stringTable.put("Loading default classes", "기본 클래스들을 불러오고 있습니다");
			stringTable.put("Loading options from arguments", "입력된 매개 변수들로부터 환경 설정을 구성하고 있습니다.");
			stringTable.put("Applying some options", "일부 환경 설정을 지금 적용하고 있습니다");
			stringTable.put("Checking auto login option", "자동 접속 설정이 있는지 확인하고 있습니다");
			stringTable.put("Checking reserved actions", "예약된 다른 환경 설정들을 확인하고 있습니다");
			stringTable.put("Input [end] to finish input.", "[end] 를 입력하여 입력을 완료할 수 있습니다.");
			stringTable.put("Cannot get session information because", "다음의 이유로 세션 정보를 가져올 수 없습니다 : ");
			stringTable.put("Session View can work if DAO is connected at Oracle Database and there is a DBA privilege."
					, "세션 도구는 오라클 DB에 DBA 계정으로 접속해야 사용할 수 있습니다.");
			stringTable.put("What column do you want to analyze?", "어떤 컬럼을 분석하시겠습니까?");
			stringTable.put("There is no column name like", "다음과 같은 컬럼 이름을 찾을 수 없습니다");
			stringTable.put("Cannot read information because there is no data.", "데이터가 없어 정보를 얻을 수 없습니다.");
			stringTable.put("Analyze", "분석");
			stringTable.put("Java Archive (*.jar)", "Java 꾸러미 (*.jar)");
			stringTable.put("Text File (*.txt)", "텍스트 파일 (*.txt)");
			stringTable.put("Huge Table formed text (*.hgf)", "Huge Table 형식 테이블 데이터 파일 (*.hgf)");
			stringTable.put("JavaScript Standard Object Notation (*.json)", "JavaScript 표준 객체 표기법 형식 파일 (*.json)");
			stringTable.put("OOXML spreadsheet (*.xlsx)", "OOXML 표준 스프레드시트 파일 (*.xlsx)");
			stringTable.put("Module Scripts (*.hgm)", "모듈 스크립트 파일 (*.hgm)");
			stringTable.put("Compressed Module Scripts (*.hgmz)", "압축된 모듈 스크립트 파일 (*.hgmz)");
			stringTable.put("Binary Module Scripts (*.hgmb)", "모듈 이진 파일 (*.hgmb)");
			
			stringTable.put("Input the scripts which will be run to initialize the module."
					, "모듈 작동을 준비(초기화)하는 스크립트를 이 곳에 작성하십시오.");
			
			stringTable.put("Input the scripts which will be run after the module is initialized."
					, "모듈이 초기화된 직후 수행해야 할 스크립트를 이 곳에 작성하십시오.");
			
			stringTable.put("Input the scripts which will be run at the program is closing."
					, "프로그램이 종료될 때 수행해야 할 스크립트를 이 곳에 작성하십시오.");
			
			stringTable.put("Input the scripts which will be run at the user control something."
					, "사용자가 조작을 했을 때 수행해야 할 스크립트를 이 곳에 작성하십시오.");
			
			stringTable.put("You can use the constant \'refreshMap\' to access what the user control."
					, "사용자가 수행한 조작에 대한 정보를 상수 refreshMap 을 통해 액세스할 수 있습니다.");
			
			stringTable.put("Input the scripts which will be run to show menu."
					, "사용자에게 메뉴를 보여주는 스크립트를 이 곳에 작성하십시오.");
			
			stringTable.put("Input the scripts which will be run to process the user's inputs on the menu."
					, "사용자가 메뉴에서 입력을 했을 때 수행할 스크립트를 이 곳에 작성하십시오.");
			
			stringTable.put("You can use the constant \'inputs\' to access what the user inputs."
					, "사용자가 입력한 명령에 대한 정보를 상수 inputs 을 통해 액세스할 수 있습니다.");
			
		    StringBuffer r = new StringBuffer("");
		    r = r.append("Copyright 2015 HJOW\n");
		    r = r.append(" \n");
		    r = r.append("Apache License 버전 2.0(본 라이선스)의 적용을 받습니다.\n");
		    r = r.append("이 파일을 사용하기 위해서는 반드시 본 라이선스를 따라야 합니다. \n");
		    r = r.append("본 라이선스의 사본은 다음 사이트에서 구할 수 있습니다.\n");
		    r = r.append(" \n");
		    r = r.append("http://www.apache.org/licenses/LICENSE-2.0\n");
		    r = r.append(" \n");
		    r = r.append("관련 법규나 서면 동의에 의해 구속되지 않는 한, 본 라이선스에 따라 배포되는 소프트웨어는 어떠한 보증이나 조건도 명시적으로나 묵시적으로 설정되지 않는 “있는 그대로”의 상태로 배포됩니다.\n");
		    r = r.append("본 라이선스가 허용하거나 제한하는 사항을 규정한 문언에 대해서는 라이선스를 참조하십시오.\n");
		      
			stringTable.put(LicenseUtil.apacheLicenseNotices(Manager.getOption("copyright_year"), Manager.getOption("copyright_owner")), r.toString());
			stringTable.putAll(LicenseUtil.stringTable(locale));
		}
		
		// TODO : 다른 언어
	}
	
	/**
	 * <p>스트링 테이블에 텍스트로 된 데이터를 입력합니다.</p>
	 * 
	 * <p># 으로 시작하는 줄은 무시됩니다.</p>
	 * <p>TARGET: 으로 시작하는 줄은 변환할 대상 텍스트를 지정하는 곳입니다.</p>
	 * <p>SET: 으로 시작하는 줄은 변환할 결과 텍스트를 지정하는 곳입니다. TARGET 줄을 통해 마지막으로 지정된 대상이 결과 텍스트로 변환됩니다.</p>
	 * <p>URL: 으로 시작하는 줄은 URL로부터 텍스트 내용을 읽어 스트링 테이블에 적용합니다. 해당 URL에서 읽어오는 텍스트도 동일한 규격을 따라야 합니다.</p>
	 * <p>FILE: 으로 시작하는 줄은 파일로부터 텍스트 내용을 읽어 스트링 테이블에 적용합니다. 해당 파일에서 읽어오는 텍스트도 동일한 규격을 따라야 합니다.</p>
	 * <p>NULL: 으로 시작하는 줄은 대상 텍스트 지정을 해제합니다.</p>
	 * <p>END: 으로 시작하는 줄은 스트링 테이블 입력을 종료시킵니다.</p>
	 * <p>GOTO: 으로 시작하는 줄은 해당 줄 번호로 이동해 그 곳부터 처리를 재개하도록 합니다.</p>
	 * <p>IF: 으로 시작하는 줄은 선택된 대상 텍스트와 현재 줄의 IF:를 제거한 텍스트가 같은지를 비교하여, 다음 줄의 THEN: 구문을 실행합니다.</p>
	 * <p>IF: 구문 다음 줄에는 반드시 THEN: 구문이 있어야 합니다. 주석도 허용되지 않습니다.</p>
	 * 
	 * @param stringTable : 대상 스트링 테이블
	 * @param str : 텍스트
	 * @param InvalidPrefixException : 잘못된 구문이 사용된 경우 발생합니다.
	 */
	public static void inputFromText(StringTable stringTable, String str) throws InvalidPrefixException
	{
		StringTokenizer lineTokenizer = new StringTokenizer(str, "\n"); // 입력된 텍스트를 줄 단위로 자릅니다.
		String savedKeys = null;
		String lines;
		String lineResults;
		
		List<String> lineList = new Vector<String>(); // 줄 단위로 자른 각 줄들을 리스트에 넣습니다.
		while(lineTokenizer.hasMoreTokens())
		{
			lineList.add(lineTokenizer.nextToken());
		}
		
		int i=0;    // 현재 선택된 줄 번호입니다. 0을 선택합니다.
		while(true) // 반복을 시작합니다.
		{
			if(i >= lineList.size()) break; // 처리 대상 줄 번호가 전체 줄 수를 넘어서면 반복을 중단합니다.
			
			lines = lineList.get(i);        // 현재 선택된 줄을 변수에 넣습니다.
			lineResults = doLine(stringTable, lines, savedKeys, i);  // 현재 선택된 줄을 처리합니다.
			
			if(lineResults == null) // 처리 결과가 null 이면 다음 줄로 넘어갑니다.
			{
				i++;
				continue;
			}
			
			if(lineResults.startsWith(LINE_PREFIX_IF)) // 처리 결과 IF문을 통과했음이 확인되면
			{
				if(i < lineList.size() - 1) // 다음 줄이 있으면 THEN 구문을 찾습니다.
				{
					if(lineList.get(i + 1).startsWith(LINE_PREFIX_THEN)) // 다음 줄이 THEN 구문인 경우
					{
						// 다음 줄의 THEN 구문을 실행합니다. 처리 결과를 현재의 줄에서 처리한 것처럼 적용합니다.
						lineResults = doLine(stringTable, lineList.get(i + 1).substring(LINE_PREFIX_THEN.length()), savedKeys, i + 1);
					}
					else // 다음 줄이 THEN 구문이 아닌 경우 예외를 발생시킵니다.
					{
						throw new InvalidPrefixException(stringTable.process("Invalid") + " " + LINE_PREFIX_IF + " " + stringTable.process("use"), i, lines);
					}
				}
				else break; // 다음 줄이 없다면 반복을 중단합니다. (IF 구문은 THEN 구문과 반드시 함께 사용하여야 합니다.)
			}
			
			if(lineResults == null) // IF THEN 구문이 있었을 경우 처리 결과가 달라졌으므로 다시 null 인지 검사합니다.
			{
				i++;
				continue;
			}
			
			 // IF 구문 줄에서 THEN 구문 줄까지 처리하게 되므로 그 다음 번 THEN 구문은 처리할 필요가 없습니다.
			if(lineResults.startsWith(LINE_PREFIX_THEN))
			{
				i++;
				continue;
			}
			
			if(lineResults.startsWith(LINE_PREFIX_NULL)) // 처리 결과, 선택된 타겟을 비우라는 구분입니다.
			{
				savedKeys = null;
			}
			else if(lineResults.startsWith(LINE_PREFIX_GOTO)) // 처리 결과 몇 번째 줄로 이동하라는 구문입니다.
			{
				i = Integer.parseInt(lineResults.substring(LINE_PREFIX_GOTO.length()));
			}
			else if(lineResults.startsWith(LINE_PREFIX_END)) // 처리 결과, 스트링 테이블 입력을 중단하라는 구문입니다.
			{
				break;
			}			
			else if(lineResults.startsWith(LINE_PREFIX_TARGET)) // 처리 결과, 타겟을 선택하라는 구문입니다.
			{
				savedKeys = lineResults;
			}
			
			// 처리가 다 되었으면, 다음 줄로 넘어가기 위해 줄 번호에 1을 더합니다.
			i++;
			
			if(! (Main.checkInterrupt(StringTableUtil.class, "inputFromText")))
			{
				break;
			}
		}
	}
	
	/**
	 * <p>스트링 테이블 구문 1줄을 실행합니다. 처리 결과는 inputFromText(~) 메소드로 반환됩니다.</p>
	 * 
	 * @param stringTable : 대상 스트링 테이블
	 * @param lineContent : 해당 줄 내용
	 * @param savedKeys : 현재 선택된 대상 텍스트
	 * @param lineIndex : 현재 줄 번호
	 * @return 처리 결과
	 */
	protected static String doLine(StringTable stringTable, String lineContent, String savedKeys, int lineIndex)
	{
		/*
		 반환값이 null 이면, 다음 줄로 넘어갑니다.
		 아무 작업을 안하면 null 을 반환하게 되어있습니다.
		 */
		
		if(lineContent.startsWith("#")) // # 으로 시작된다면 다음 줄로 넘어갑니다.
		{
			return null;
		}
		else if(lineContent.startsWith(LINE_PREFIX_SET)) // SET 명령을 만났으므로, 지정된 대상 텍스트를 현재 줄 내용으로 변환되도록 정보를 넣습니다.
		{
			if(savedKeys != null) stringTable.put(savedKeys, lineContent.substring(LINE_PREFIX_SET.length()));
		}
		else if(lineContent.startsWith(LINE_PREFIX_URL)) // URL 명령을 만났으므로, URL(웹)을 통해 텍스트를 읽어와 읽는 작업 전체를 실행합니다.
		{
			try
			{
				inputFromText(stringTable, StreamUtil.readText(new URL(lineContent.substring(LINE_PREFIX_URL.length())), "UTF-8", "#"));
			}
			catch (Exception e)
			{
				Main.logError(e, stringTable.process("On reading stringTable from web"), true);
			}
		}
		else if(lineContent.startsWith(LINE_PREFIX_FILE)) // FILE 명령을 만났으므로, 파일을 통해 텍스트를 읽어와 읽는 작업 전체를 실행합니다.
		{
			try
			{
				inputFromText(stringTable, StreamUtil.readText(new File(lineContent.substring(LINE_PREFIX_FILE.length())), "UTF-8", "#"));
			}
			catch (Exception e)
			{
				Main.logError(e, stringTable.process("On reading stringTable from file"), true);
			}
		}
		else if(lineContent.startsWith(LINE_PREFIX_GOTO) || lineContent.startsWith(LINE_PREFIX_END) || lineContent.startsWith(LINE_PREFIX_TARGET))
		{
			/*
			GOTO, END, TARGET 구문은 줄 번호 혹은 대상 선택과 관련되어 있으므로, inputFromText(~) 에서 처리되어야 하므로 내용을 그대로 반환합니다.
			GOTO 구문은 처리 작업 줄 번호를 이동하여 그 곳부터 재개합니다.
			END 구문은 처리 작업을 종료시킵니다.
			TARGET 구문은 대상 문자열을 지정합니다.
			*/
			
			return lineContent;
		}
		else if(lineContent.startsWith(LINE_PREFIX_IF))
		{
			/* 
			IF 구문을 만났으므로, 지정된 대상과 현재 내용이 같은지를 비교해, 같으면 IF 구문 표시를 반환하고
			같지 않으면 다음 줄로 넘어갑니다. 
			*/
			if(savedKeys.equals(lineContent.substring(LINE_PREFIX_IF.length())))
			{
				return LINE_PREFIX_IF;
			}
			else return null;
		}
		return null; // 다음 줄로 넘어갑니다.
	}
}