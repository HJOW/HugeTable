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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.Vector;

import hjow.hgtable.Main;
import hjow.hgtable.Manager;
import hjow.hgtable.classload.ClassTool;
import hjow.hgtable.dao.Dao;
import hjow.hgtable.dao.JdbcDao;
import hjow.hgtable.tableset.TableSet;
import hjow.hgtable.tableset.TableSetTreator;
import hjow.hgtable.ui.NeedtoEnd;
import hjow.hgtable.ui.ProgressEvent;
import hjow.hgtable.util.DataUtil;
import hjow.hgtable.util.DirectIOUtil;
import hjow.hgtable.util.JSONUtil;
import hjow.hgtable.util.NetUtil;
import hjow.hgtable.util.StreamUtil;
import hjow.hgtable.util.TableSetBuildUtil;
import hjow.hgtable.util.net.AdvancedCommunicator;
import hjow.hgtable.util.net.AdvancedReceiveHandler;
import hjow.hgtable.util.net.AdvancedScriptReceiveHandler;
import hjow.hgtable.util.net.Communicator;
import hjow.hgtable.util.net.ReceiveHandler;
import hjow.hgtable.util.net.ScriptPage;
import hjow.hgtable.util.net.ScriptReceiveHandler;
import hjow.hgtable.util.net.WebServer;
import hjow.state.FirstStateView;
import hjow.web.content.DefaultPageContent;
import hjow.web.content.Page;
import hjow.web.content.PageContent;

/**
 * <p>이 클래스 객체는 스크립트 엔진에 jtool 이라는 이름으로 포함되어 사용할 수 있습니다.</p>
 * <p>이 클래스 안에 선언된 public 메소드들 역시 jtool 이라는 이름으로 사용할 수 있습니다.</p>
 * 
 * @author HJOW
 *
 */
public class JScriptTool implements JScriptObject
{
	private static final long serialVersionUID = 5436335313417199652L;
	protected JScriptRunner runner;
	protected Manager manager;
	protected Runtime runtime = Runtime.getRuntime();
	protected List<JScriptThread> threads = new Vector<JScriptThread>();
	protected List<NeedtoEnd> canBeEndeds = new Vector<NeedtoEnd>();
	
	/**
	 * <p>생성자입니다. 스크립트 실행이 가능한 JScriptRunner 타입 객체를 받습니다.</p>
	 * 
	 * @param runner : 스크립트 엔진을 가진 JScriptRunner 객체
	 */
	public JScriptTool(JScriptRunner runner, Manager manager)
	{
		this.runner = runner;
		this.manager = manager;
	}
	
	/**
	 * <p>스크립트를 실행합니다.</p>
	 * 
	 * @param script : 실행할 스크립트, String 타입
	 * @return 실행 결과
	 * @throws Exception : 실행 중 발생한 예외
	 */
	public Object eval(String script) throws Throwable
	{
		return runner.execute(script);
	}
	
	/**
	 * <p>생성된 쓰레드를 모두 닫으며 순환 구조를 끊습니다.</p>
	 * 
	 */
	public void close()
	{
		noMoreUse();
	}
	
	/**
	 * <p>쓰지 않는 객체들을 메모리에서 정리합니다.</p>
	 * 
	 */
	public void gc()
	{
		System.gc();
	}
	
	/**
	 * <p>현재 JVM이 활용 가능한 메모리량을 반환합니다..</p>
	 * 
	 * @return JVM이 활용 가능한 메모리량 (byte 단위)
	 */
	public long totalMemory()
	{
		return runtime.totalMemory();
	}
	
	/**
	 * <p>JVM이 사용 가능한 메모리 최대량(확장 가능량 포함)을 반환합니다.</p>
	 * 
	 * @return JVM이 사용 가능한 메모리 최대량 (byte 단위)
	 */
	public long maxMemory()
	{
		return runtime.maxMemory();
	}
	
	/**
	 * <p>남은 메모리량을 반환합니다.</p>
	 * 
	 * @return 남은 메모리량 (byte 단위)
	 */
	public long freeMemory()
	{
		return runtime.freeMemory();
	}
	
	/**
	 * <p>사용 가능한 프로세서 수를 반환합니다.</p>
	 * 
	 * @return 프로세서 수
	 */
	public int availableProcessors()
	{
		return runtime.availableProcessors();
	}
	
	/**
	 * <p>특정 밀리초 만큼 현재 쓰레드를 일시 정지합니다.</p>
	 * 
	 * @param millis : 일시정지할 시간, 밀리초 단위
	 * @throws InterruptedException : 쓰레드 문제
	 */
	public void sleep(long millis) throws InterruptedException
	{
		Thread.sleep(millis);
	}
	
	/**
	 * <p>OS 터미널(콘솔) 명령어를 실행합니다.</p>
	 * 
	 * @param command : OS 명령어
	 * @return 실행 결과
	 * @throws Exception : 권한 문제 및 OS에서 발생한 문제 등
	 */
	public Process command(String command) throws Exception
	{
		if(runner.isPriv_allowCommand()) return runtime.exec(command);
		else throw new NotEnoughPrivilegeException(Manager.applyStringTable("On command(c)"));
	}
	
	/**
	 * <p>JVM의 메소드 호출을 추적하는 기능을 설정합니다.</p>
	 * 
	 * @param b : true 이면 켜고 false 이면 끕니다.
	 * @throws NotEnoughPrivilegeException : 권한 문제
	 */
	public void traceMethodCalls(boolean b) throws NotEnoughPrivilegeException
	{	
		if(runner.isPriv_allowCommand()) runtime.traceMethodCalls(b);
		else throw new NotEnoughPrivilegeException(Manager.applyStringTable("On traceMethodCalls(t)"));
	}
	
	/**
	 * <p>JVM의 명령 추적 기능을 설정합니다.</p>
	 * 
	 * @param b : true 이면 켜고 false 이면 끕니다.
	 * @throws NotEnoughPrivilegeException : 권한 문제
	 */
	public void traceInstrunctions(boolean b) throws NotEnoughPrivilegeException
	{
		if(runner.isPriv_allowCommand()) runtime.traceInstructions(b);
		else throw new NotEnoughPrivilegeException(Manager.applyStringTable("On traceInstrunctions(t)"));
	}
	
	/**
	 * <p>이 프로그램을 종료합니다.</p>
	 * 
	 * @throws NotEnoughPrivilegeException : 권한 문제
	 */
	public void exit() throws NotEnoughPrivilegeException
	{
		if(runner.isPriv_allowCommand()) Main.exitAllProcess();
		else throw new NotEnoughPrivilegeException(Manager.applyStringTable("On exit()"));
	}
	
	/**
	 * <p>이 프로그램을 재시작합니다.</p>
	 * <p>프로그램을 완전히 종료합니다. 종료 시 반환 코드값을 주어, 배치 파일에서 프로그램을 다시 시작합니다. 개발 툴을 통한 프로그램 실행 시에는 이 함수가 동작하지 않습니다.</p>
	 * 
	 * @throws NotEnoughPrivilegeException : 권한 문제
	 */
	public void restart() throws NotEnoughPrivilegeException
	{
		if(runner.isPriv_allowCommand()) Main.restartAllProcess();
		else throw new NotEnoughPrivilegeException(Manager.applyStringTable("On restart()"));
	}
	
	/**
	 * <p>이 프로그램을 종료합니다.</p>
	 * <p>프로그램이 완전히 종료되지 않고 내부적으로 종료 처리한 뒤 메인 함수부터 다시 실행합니다.</p>
	 * 
	 * @throws NotEnoughPrivilegeException : 권한 문제
	 */
	public void restartMain() throws NotEnoughPrivilegeException
	{
		if(runner.isPriv_allowCommand()) Main.restartMain();
		else throw new NotEnoughPrivilegeException(Manager.applyStringTable("On restartMain()"));
	}
	
	/**
	 * <p>JDBC 드라이버를 불러올 때 탐색할 jar 파일 목록에 새 파일을 추가합니다.</p>
	 * 
	 * @param filePath : 새 jar 파일
	 * @throws NotEnoughPrivilegeException : 권한 문제
	 */
	public void addExternalJar(String filePath) throws NotEnoughPrivilegeException
	{
		boolean isAllowed = false;
		String targetFullPath = filePath;
		
		if(runner.getPriv_allowedReadFilePath() != null)
		{
			for(int i=0; i<runner.getPriv_allowedReadFilePath().size(); i++)
			{
				if(targetFullPath.startsWith(runner.getPriv_allowedReadFilePath().get(i)))
				{
					isAllowed = true;
					break;
				}
			}
			
			if(! isAllowed)
			{
				throw new NotEnoughPrivilegeException(Manager.applyStringTable("On jsonToDb")); 
			}
		}
		
		if(isAllowed)
		{
			ClassTool.addJarFileList(new File(filePath));
		}
	}
	
	/**
	 * <p>시스템의 환경 설정을 반환합니다. Java 자체에서 제공하는 특정 키들을 사용할 수 있습니다.</p>
	 * 
	 * @param key : 환경 설정 키
	 * @return 환경 설정값
	 */
	public String getProperty(String key)
	{
		return System.getProperty(key);
	}
	
	/**
	 * <p>시스템 환경 변수값을 반환합니다.</p>
	 * 
	 * @param key : 환경 변수 키
	 * @return 환경 변수 값
	 */
	public String getenv(String key)
	{
		return System.getenv(key);
	}
	
	/**
	 * <p>1970년 1월 1일 이후로 지난 시간을 밀리초 단위로 반환합니다.</p>
	 * 
	 * @return 현재 시간
	 */
	public long currentTime()
	{
		return System.currentTimeMillis();
	}
	
	/**
	 * <p>현재 날짜 데이터가 들어있는 날짜 객체를 반환합니다.</p>
	 * 
	 * @param param : 날짜 형식 문자열
	 * @param format : 형식 지정 문자열 (Java의 SimpleDateFormat 사용, 예 : yyyy-MM-dd)
	 * @return 날짜 객체
	 */
	public Date date(Object param, Object format)
	{
		if(param == null || DataUtil.isEmpty(param) || String.valueOf(param).equals("undefined")) return new Date(System.currentTimeMillis());
		else if(param instanceof Number) return new Date(((Number) param).longValue());
		else if(param instanceof Date) return new Date(((Date) param).getTime());
		else
		{
			Date result = null;
			String paramStr = String.valueOf(param);
			try
			{
				if(format != null) return DataUtil.toDate(String.valueOf(param), String.valueOf(format));
				if(paramStr.indexOf("-") >= 0) result = new SimpleDateFormat("yyyy-MM-dd").parse(paramStr);
				else if(paramStr.indexOf(".") >= 0) result = new SimpleDateFormat("yyyy.MM.dd").parse(paramStr);
				else result = new SimpleDateFormat("yyyyMMdd").parse(paramStr);
			}
			catch (ParseException e)
			{
				e.printStackTrace();
				result = null;
			}
			if(result == null) result = new Date(Long.parseLong(paramStr));
			
			return result;
		}
	}
	
	/**
	 * <p>날짜 객체를 문자열로 변환합니다.</p>
	 * 
	 * @param date : 날짜 객체
	 * @param format : 변환할 형식 지정 문자열 (Java의 SimpleDateFormat 사용, 예 : yyyy-MM-dd)
	 * @return 변환된 문자열
	 */
	public String dateToString(Date date, Object format)
	{
		return DataUtil.toString(date, String.valueOf(format));
	}
	
	/**
	 * <p>JSON 형식의 텍스트 파일을 해석해 DB에 삽입합니다. 규격에 맞아야 합니다. 일정량 단위로 작업을 하므로 내용 전체를 메모리에 올릴 필요는 없습니다.</p>
	 * 
	 * @param name : 테이블 이름
	 * @param dao : DB 접속된 DAO 객체
	 * @param targetFile : 불러올 파일 경로와 이름
	 * @throws Exception 파일 액세스 문제, 네트워크 문제, DBMS에서 발생하는 문제
	 */
	public void jsonToDb(String name, Dao dao, String targetFile) throws Exception
	{
		File target = new File(targetFile);
		if(! target.exists())
		{
			throw new FileNotFoundException(Manager.applyStringTable("On jsonToDb, file is not exist") + " : " + target);
		}
		else
		{
			boolean isAllowed = false;
			String targetFullPath = target.getAbsolutePath();
			
			if(runner.getPriv_allowedReadFilePath() != null)
			{
				for(int i=0; i<runner.getPriv_allowedReadFilePath().size(); i++)
				{
					if(targetFullPath.startsWith(runner.getPriv_allowedReadFilePath().get(i)))
					{
						isAllowed = true;
						break;
					}
				}
				
				if(! isAllowed)
				{
					throw new NotEnoughPrivilegeException(Manager.applyStringTable("On jsonToDb")); 
				}
			}
		}
		JSONUtil.toTableSet(StreamUtil.readText(target, Manager.getOption("file_charset"))).insertIntoDB(dao);		
	}
	
	/**
	 * <p>HGF 형식의 텍스트 파일을 해석해 DB에 삽입합니다. 규격에 맞아야 합니다. 일정량 단위로 작업을 하므로 내용 전체를 메모리에 올릴 필요는 없습니다.</p>
	 * 
	 * @param dao : DB 접속된 DAO 객체
	 * @param targetFile : 불러올 파일 경로와 이름
	 * @throws Exception 파일 액세스 문제, 네트워크 문제, DBMS에서 발생하는 문제
	 */
	public void hgfToDb(Dao dao, String targetFile) throws Exception
	{
		File target = new File(targetFile);
		if(! target.exists())
		{
			throw new FileNotFoundException(Manager.applyStringTable("On hgfToDb, file is not exist") + " : " + target);
		}
		else
		{
			boolean isAllowed = false;
			String targetFullPath = target.getAbsolutePath();
			
			if(runner.getPriv_allowedReadFilePath() != null)
			{
				for(int i=0; i<runner.getPriv_allowedReadFilePath().size(); i++)
				{
					if(targetFullPath.startsWith(runner.getPriv_allowedReadFilePath().get(i)))
					{
						isAllowed = true;
						break;
					}
				}
				
				if(! isAllowed)
				{
					throw new NotEnoughPrivilegeException(Manager.applyStringTable("On hgfToDb")); 
				}
			}
		}
		DirectIOUtil.hgfToDb(dao, target, new Hashtable<String, String>(), new Hashtable<String, Integer>(), new Hashtable<String, List<String>>());
	}
	
	/**
	 * <p>DB에서 값을 가져와 JSON 형식의 파일로 저장합니다. 일정량 단위로 작업을 하므로 내용 전체를 메모리에 올릴 필요는 없습니다.</p>
	 * 
	 * @param name : 테이블 이름
	 * @param dao : DB 접속된 DAO 객체
	 * @param targetFile : 저장할 파일 경로
	 * @param selectQuery : SQL 형식의 문장, DB에서 조회 시 사용
	 * @param additionalWheres : 추가 조건문 (null 사용 가능)
	 * @param separatesRecord : true 시 테이블 셋이 아닌 레코드들의 배열 형태로 저장 (null 시 기본값은 false)
	 * @throws Exception 파일 액세스 문제, 네트워크 문제, DBMS에서 발생하는 문제
	 */
	public void dbToJson(String name, Dao dao, String targetFile, String selectQuery, String additionalWheres, Object separatesRecord) throws Exception
	{
		File target = new File(targetFile);
		if(! target.exists())
		{
			throw new FileNotFoundException(Manager.applyStringTable("On dbToJson, file is not exist") + " : " + target);
		}
		else
		{
			boolean isAllowed = false;
			String targetFullPath = target.getAbsolutePath();
			
			if(runner.getPriv_allowedReadFilePath() != null)
			{
				for(int i=0; i<runner.getPriv_allowedReadFilePath().size(); i++)
				{
					if(targetFullPath.startsWith(runner.getPriv_allowedReadFilePath().get(i)))
					{
						isAllowed = true;
						break;
					}
				}
				
				if(! isAllowed)
				{
					throw new NotEnoughPrivilegeException(Manager.applyStringTable("On dbToJson")); 
				}
			}
		}
		DirectIOUtil.dbToJson(name, dao, target, selectQuery, additionalWheres, separatesRecord);
	}
	
	/**
	 * <p>DB에서 값을 가져와 HGF 형식의 파일로 저장합니다. 일정량 단위로 작업을 하므로 내용 전체를 메모리에 올릴 필요는 없습니다.</p>
	 * 
	 * @param name : 테이블 이름
	 * @param dao : DB 접속된 DAO 객체
	 * @param targetFile : 저장할 파일 경로
	 * @param selectQuery : SQL 형식의 문장, DB에서 조회 시 사용
	 * @param additionalWheres : 추가 조건문 (null 사용 가능)
	 * @throws Exception 파일 액세스 문제, 네트워크 문제, DBMS에서 발생하는 문제
	 */
	public void dbToHgf(String name, Dao dao, String targetFile, String selectQuery, String additionalWheres) throws Exception
	{
		File target = new File(targetFile);
		if(! target.exists())
		{
			throw new FileNotFoundException(Manager.applyStringTable("On dbToHgf, file is not exist") + " : " + target);
		}
		else
		{
			boolean isAllowed = false;
			String targetFullPath = target.getAbsolutePath();
			
			if(runner.getPriv_allowedReadFilePath() != null)
			{
				for(int i=0; i<runner.getPriv_allowedReadFilePath().size(); i++)
				{
					if(targetFullPath.startsWith(runner.getPriv_allowedReadFilePath().get(i)))
					{
						isAllowed = true;
						break;
					}
				}
				
				if(! isAllowed)
				{
					throw new NotEnoughPrivilegeException(Manager.applyStringTable("On dbToHgf")); 
				}
			}
		}
		DirectIOUtil.dbToHgf(name, dao, target, selectQuery, additionalWheres);
	}
	
	/**
	 * <p>파일을 읽어 그 내용을 String 값으로 반환합니다.</p>
	 * 
	 * @param file : 파일 경로 및 이름
	 * @param charset : 캐릭터 셋, UTF-8 사용 추천
	 * @return 읽은 내용
	 * @throws Exception : 읽는 과정에서 발생한 문제, 권한 문제, 혹은 존재하지 않는 파일 등
	 */
	public String readFile(String file, String charset) throws Exception
	{
		String charsets = charset;
		if(charsets == null) charsets = Manager.getOption("file_charset");
		
		if(file == null) throw new NullPointerException(Manager.applyStringTable("On readFile(f, c), f cannot be null"));
		File targetFile = new File(file);
		
		if(! targetFile.exists())
		{
			throw new FileNotFoundException(Manager.applyStringTable("On readFile(f, c), file is not exist") + " : " + file);
		}
		else
		{
			boolean isAllowed = false;
			String targetFullPath = targetFile.getAbsolutePath();
			
			if(runner.getPriv_allowedReadFilePath() != null)
			{
				for(int i=0; i<runner.getPriv_allowedReadFilePath().size(); i++)
				{
					if(targetFullPath.startsWith(runner.getPriv_allowedReadFilePath().get(i)))
					{
						isAllowed = true;
						break;
					}
				}
				
				if(! isAllowed)
				{
					throw new NotEnoughPrivilegeException(Manager.applyStringTable("On readFile(f, c)")); 
				}
			}
		}
		
		return StreamUtil.readText(targetFile, charsets);
	}
	
	/**
	 * <p>웹 URL에 접속해 해당 웹 서버가 보낸 내용을 String 값으로 반환합니다.</p>
	 * <p>파라미터는 GET 방식으로 전달할 수 있습니다.</p>
	 * 
	 * @param url : 웹 주소
	 * @param charset : 캐릭터 셋, UTF-8 사용 추천
	 * @return 읽은 내용
	 * @throws Exception : 읽는 과정에서 발생한 문제, 권한 문제, 네트워크 문제, 혹은 존재하지 않는 URL 등
	 */
	public String readWeb(String url, String charset) throws Exception
	{
		String charsets = charset;
		if(charsets == null) charsets = Manager.getOption("file_charset");
		return StreamUtil.readText(new URL(url), charsets);
	}
	
	/**
	 * <p>파일로부터 맵을 읽어 들입니다.</p>
	 * 
	 * @param target : 읽을 파일, 혹은 URL 객체입니다.
	 * @param useUnzip : GZIP 스트림을 거칠 지 여부를 지정합니다.
	 * @return 읽은 내용
	 * @throws IOException : 파일 혹은 URL이 형식에 맞지 않거나 존재하지 않는 소스를 가리킬 때
	 */
	public static Map<String, ?> readMap(Object target, boolean useUnzip) throws IOException
	{
		if(target instanceof File) return StreamUtil.readMap((File) target, useUnzip);
		else if(target instanceof URL) return StreamUtil.readMap((URL) target, useUnzip);
		else
		{
			String targetStr = String.valueOf(target);
			if(targetStr.startsWith("file://") || targetStr.startsWith("File://") || targetStr.startsWith("FILE://")) return StreamUtil.readMap(new File(targetStr.substring(new String("File://").length())), useUnzip);
			else if(targetStr.startsWith("url://") || targetStr.startsWith("Url://") || targetStr.startsWith("URL://")) return StreamUtil.readMap(new URL(targetStr.substring(new String("Url://").length())), useUnzip);
		}
		throw new IllegalArgumentException(Manager.applyStringTable("The first parameter of readMap function is not URL or File."));
	}
	
	/**
	 * <p>파일에 텍스트 내용을 저장합니다.</p>
	 * 
	 * @param file : 파일 경로 및 이름
	 * @param contents : 저장할 내용
	 * @param charset : 캐릭터 셋, UTF-8 사용 추천
	 * @throws Exception : 저장하는 과정에서 발생한 문제, 권한 문제 등
	 */
	public void saveFile(String file, String contents, String charset) throws Exception
	{
		File targetFile = new File(file);
		
		boolean isAllowed = false;
		String targetFullPath = targetFile.getAbsolutePath();
		
		if(runner.getPriv_allowedWriteFilePath() != null)
		{
			for(int i=0; i<runner.getPriv_allowedWriteFilePath().size(); i++)
			{
				if(targetFullPath.startsWith(runner.getPriv_allowedWriteFilePath().get(i)))
				{
					isAllowed = true;
					break;
				}
			}
			
			if(! isAllowed)
			{
				throw new NotEnoughPrivilegeException(Manager.applyStringTable("On readFile(f, c)")); 
			}
		}		
		
		StreamUtil.saveFile(targetFile, contents, charset);
	}
	
	/**
	 * <p>맵 객체를 저장할 때 사용됩니다.</p>
	 * 
	 * @param file : 저장할 파일
	 * @param map : 저장할 맵 객체
	 * @param useZip : 압축 여부
	 */
	public static void saveMap(File file, Map<String, ?> map, boolean useZip)
	{
		StreamUtil.saveMap(file, map, useZip);
	}
	
	/**
	 * <p>객체를 Java의 String 타입으로 변환합니다.</p>
	 * 
	 * @param ob : 객체
	 * @return 변환된 String
	 */
	public String strings(Object ob)
	{
		return String.valueOf(ob);
	}
	
	/**
	 * <p>객체를 Java의 int 타입으로 변환합니다.</p>
	 * 
	 * @param ob : 객체
	 * @return 변환된 정수 값
	 */
	public int integers(Object ob) throws NumberFormatException
	{
		return Integer.parseInt(String.valueOf(ob));
	}
	
	/**
	 * <p>객체를 Java의 double 타입으로 변환합니다.</p>
	 * 
	 * @param ob : 객체
	 * @return 변환된 실수 값
	 */
	public double floats(Object ob) throws NumberFormatException
	{
		return Double.parseDouble(String.valueOf(ob));
	}
	
	/**
	 * <p>객체를 Java의 boolean 타입으로 변환합니다.</p>
	 * 
	 * @param ob : 객체
	 * @return 변환된 논리 값
	 */
	public boolean booleans(Object ob) throws Exception
	{
		return DataUtil.parseBoolean(String.valueOf(ob));
	}
	
	/**
	 * <p>서비스 로더를 사용하여 객체들을 불러옵니다.</p>
	 * 
	 * @param superClassName : 상위 클래스 이름
	 * @return 불러온 객체들
	 * @throws NotEnoughPrivilegeException
	 * @throws ClassNotFoundException
	 */
	public Set<?> loadServices(String superClassName) throws NotEnoughPrivilegeException, ClassNotFoundException
	{
		if(runner.isPriv_allowLoadClass())
		{
			ServiceLoader<?> loader = ServiceLoader.load(Class.forName(superClassName));
			Set<Object> classSet = new HashSet<Object>();
			for(Object loaded : loader)
			{
				classSet.add(loaded);
			}
			return classSet;
		}
		throw new NotEnoughPrivilegeException(Manager.applyStringTable("On loadServices(c)")); 
	}
	
	/**
	 * <p>스크립트를 일정 간격으로 반복 실행합니다. 별도의 쓰레드에서 실행됩니다.</p>
	 * 
	 * @param scripts : 실행할 스크립트 (콜백함수 사용 불가능)
	 * @param interval : 실행 간격, 밀리초 단위
	 * @return 쓰레드 객체
	 * @throws NotEnoughPrivilegeException : 권한 문제
	 */
	public JScriptThread setInterval(String scripts, Object interval) throws NotEnoughPrivilegeException
	{
		if(runner.isPriv_allowThread())
		{
			JScriptThread newThread = null;
			if(interval == null) newThread = new JScriptThread(runner, scripts);
			else newThread = new JScriptThread(runner, scripts, integers(interval));
			threads.add(newThread);
			newThread.start();
			return newThread;
		}
		else throw new NotEnoughPrivilegeException(Manager.applyStringTable("On setInterval(s, i)")); 
	}
	
	/**
	 * <p>스크립트 쓰레드를 멈춥니다.</p>
	 * 
	 * @param thread : 대상 쓰레드
	 */
	public void clearInterval(JScriptThread thread)
	{
		thread.close();
	}
	
	/**
	 * <p>동적으로 클래스를 불러옵니다. fileList 가 선언되어 있는 경우 해당 파일로부터 클래스 이름에 해당하는 클래스를 찾아 불러옵니다.</p>
	 * <p>JDBC 드라이버를 불러올 때에도 사용할 수 있습니다.</p>
	 * 
	 * @param classPath : 클래스 이름 (풀네임)
	 * @param files : 파일 리스트 (혹은 URL)
	 * @return 불러온 Class 객체, 이를 통해 해당 클래스의 객체를 생성할 수 있습니다.
	 * @throws Exception : 클래스를 불러오는 중 발생하는 문제들
	 */
	public Class<?> loadClass(String classPath, Object filesArr) throws Exception
	{
		if(runner.isPriv_allowLoadClass())
		{
			if(filesArr == null) return ClassTool.loadClass(classPath);
			else return ClassTool.loadClass(classPath, (String[]) filesArr);
		}
		else throw new NotEnoughPrivilegeException(Manager.applyStringTable("On loadClass(class, files)")); 
	}
	
	/**
	 * <p>동적으로 JDBC 드라이버를 불러옵니다. fileList 가 선언되어 있는 경우 해당 파일로부터 클래스 이름에 해당하는 JDBC 드라이버를 찾아 불러옵니다.</p>
	 * 
	 * @param classPath : JDBC 드라이버 클래스 이름 (풀네임)
	 * @param files : 파일 리스트 (혹은 URL)
	 * @throws Exception : 클래스를 불러오는 중 발생하는 문제들
	 */
	public void loadDriver(String classPath, Object files) throws Exception
	{
		if(runner.isPriv_allowLoadClass())
		{
			if(files == null) ClassTool.loadDriver(classPath, null);
			else ClassTool.loadDriver(classPath, DataUtil.arrayToList((String[]) files, new Vector<String>()));
		}
		else throw new NotEnoughPrivilegeException(Manager.applyStringTable("On loadDriver(class, files)")); 
	}
	
	/**
	 * 해당 클래스 풀네임의 클래스를 찾아 객체를 바로 생성합니다.
	 * 
	 * @param className : 클래스 풀네임
	 * @param params : 매개 변수들 (생략 가능)
	 * @return 생성된 객
	 * @throws Exception 클래스명이 잘못되었거나 해당 클래스가 클래스경로에 없는 경우, 매개변수 입력이 잘못된 경우 
	 */
	public Object createInstance(String classPath, Object ... params) throws Exception
	{
		if(runner.isPriv_allowLoadClass())
		{
			return ClassTool.createInstance(classPath, params);
		}
		else throw new NotEnoughPrivilegeException(Manager.applyStringTable("On createInstance(class, params)")); 
	}
	
	/**
	 * <p>HTTP 요청을 보냅니다. 그에 대한 응답을 텍스트로 반환합니다.</p>
	 * 
	 * @param url : URL
	 * @param parameters : 매개 변수들
	 * @param requestProperties : 요청 속성들
	 * @param contentType : HTTP 컨텐츠 타입 옵션, null 시 application/x-www-form-urlencoded 가 사용됨
	 * @param parameterEncoding : 매개 변수 인코딩 방식, null 시 UTF-8 사용
	 * @param post : POST 방식 여부
	 * @return 서버로부터 받은 텍스트
	 * @throws Throwable : 네트워크 문제, URL 문제, 지원되지 않는 인코딩 등
	 */
	public String send(URL url, Map<String, Object> parameters, Map<String, String> requestProperties, String contentType, String parameterEncoding, boolean post) throws Throwable
	{
		if(runner.isPriv_allowNetwork())
		{
			return NetUtil.send(url, parameters, requestProperties, contentType, parameterEncoding, post);
		}
		return null;
	}
	
	/**
	 * <p>서버 역할을 수행할 수 있는 일반 커뮤니케이터 객체를 생성합니다.</p>
	 * 
	 * @return 커뮤니케이터 객체
	 */
	public Communicator createCommunicator()
	{
		Communicator communicator = new Communicator();
		canBeEndeds.add(communicator);
		return communicator;
	} 
	
	/**
	 * <p>일반 커뮤니케이터 객체에 활용할 수 있는 수신 이벤트 핸들러 객체를 생성합니다.</p>
	 * 
	 * @param script : 메시지 수신 시 수행할 스크립트, 수신한 메시지는 recv_msg 라는 변수 이름으로 사용 가능, 클라이언트 정보는 recv_info 라는 변수 이름으로 사용 가능
	 * @return 수신 이벤트 핸들러 객체
	 */
	public ReceiveHandler createReceiveHandler(Object script)
	{
		ReceiveHandler handler = new ScriptReceiveHandler(String.valueOf(script), runner);
		return handler;
	}
	
	/**
	 * <p>서버 역할을 수행할 수 있는 향상된 커뮤니케이터 객체를 생성합니다. 일반 커뮤니케이터와는 달리 객체 자체를 전송하고 받을 수 있습니다.</p>
	 * 
	 * @return 커뮤니케이터 객체
	 */
	public AdvancedCommunicator createAdvancedCommunicator()
	{
		AdvancedCommunicator communicator = new AdvancedCommunicator();
		canBeEndeds.add(communicator);
		return communicator;
	}
	
	/**
	 * <p>향상된 커뮤니케이터 객체에 활용할 수 있는 수신 이벤트 핸들러 객체를 생성합니다.</p>
	 * 
	 * @param script : 메시지 수신 시 수행할 스크립트, 수신한 객체는 recv_msg 라는 변수 이름으로 사용 가능, 클라이언트 정보는 recv_info 라는 변수 이름으로 사용 가능
	 * @return 향상된 커뮤니케이터 전용 수신 이벤트 핸들러 객체
	 */
	public AdvancedReceiveHandler createAdvancedReceiveHandler(Object script)
	{
		AdvancedReceiveHandler handler = new AdvancedScriptReceiveHandler(String.valueOf(script), runner);
		return handler;
	}
	
	/**
	 * <p>웹 서비스가 가능한 서버 객체를 생성합니다. 초기화해야 사용할 수 있습니다. 서비스를 위해 페이지 컨텐츠 객체가 필요하며, 서버 객체를 초기화하기 전에 넣어 주어야 합니다.</p>
	 * 
	 * @return 서버 객체
	 */
	public WebServer createServer()
	{
		WebServer server = new WebServer();
		canBeEndeds.add(server);
		return server;
	}
	
	/**
	 * <p>페이지 컨텐츠 객체를 생성합니다.</p>
	 * <p>사용자가 접속할 때 호출한 요청 URL 에서 컨텍스트 경로를 제외한 부분이 명령 값이 되며, 페이지 컨텐츠 객체는 이 명령 값에 해당하는 페이지 객체를 찾아 호출해 줍니다.</p>
	 * <p>즉, 페이지 컨텐츠 객체 안에 페이지 객체들을 넣어야 사용할 수 있습니다.</p>
	 * 
	 * @return 새 페이지 컨텐츠 객체
	 */
	public PageContent createPageContent()
	{
		return new DefaultPageContent();
	}
	
	/**
	 * <p>페이지 객체를 생성합니다.</p>
	 * 
	 * @return 페이지 객체
	 */
	public Page createPage()
	{
		Page page = new ScriptPage(manager, runner);
		return page;
	}
	
	/**
	 * <p>새 DAO를 만듭니다.</p>
	 * 
	 * @param alias : 별칭 (null 가능)
	 * @return 생성된 DAO
	 */
	public Dao dao(String alias)
	{
		return newDao(alias);
	}
	
	/**
	 * <p>새 DAO를 만듭니다.</p>
	 * 
	 * @param alias : 별칭 (null 가능)
	 * @return 생성된 DAO
	 */
	public Dao newDao(String alias)
	{
		Dao dao = new JdbcDao(manager);
		dao.setRunner(runner);
		if(alias != null) dao.setAliasName(alias);
		manager.addDao(dao);
		return dao;
	}
	
	/**
	 * <p>현재 선택된 DAO 번호(몇 번째 DAO인지)를 반환합니다.</p>
	 * 
	 * @return 현재 선택된 DAO 번호
	 */
	public int getSelectedDao()
	{
		return manager.getSelectedDao();
	}
	
	/**
	 * <p>DAO를 선택합니다.</p>
	 * 
	 * @param index : DAO 번호
	 * @throws NotEnoughPrivilegeException : 권한 문제
	 */
	public void setSelectedDao(int index) throws NotEnoughPrivilegeException
	{
		boolean allowed = false;
		
		if(runner.getPriv_accessDBList() == null) allowed = true;
		else
		{
			for(int i=0; i<runner.getPriv_accessDBList().size(); i++)
			{
				if(runner.getPriv_accessDBList().get(i).equals(manager.getDao(index).getAccessInfo()))
				{
					allowed = true;
					break;
				}
			}
		}
		
		if(! allowed) throw new NotEnoughPrivilegeException(Manager.applyStringTable("On setSelectedDao(index)"));
		
		manager.setSelectedDao(index);
	}
	
	/**
	 * <p>DAO를 선택합니다.</p>
	 * 
	 * @param index : DAO 번호
	 * @throws NotEnoughPrivilegeException : 권한 문제
	 */
	public void selectDao(int index) throws NotEnoughPrivilegeException
	{
		setSelectedDao(index);
	}
	
	/**
	 * <p>DAO 목록을 반환합니다.</p>
	 * 
	 * @return DAO 목록
	 */
	public List<Dao> getDaos()
	{
		List<Dao> newList = new Vector<Dao>();
		
		boolean isAllowed = false;
		
		if(runner.getPriv_accessDBList() == null)
		{
			newList.addAll(manager.getDaos());
		}
		else
		{
			for(int i=0; i<manager.getDaos().size(); i++)
			{
				isAllowed = false;
				for(int j=0; j<runner.getPriv_accessDBList().size(); j++)
				{
					if(manager.getDao(i).getAccessInfo().equals(runner.getPriv_accessDBList().get(j)))
					{
						isAllowed = true;
					}
				}
				if(isAllowed)
				{
					newList.add(manager.getDao(i));
				}
			}
		}
		
		return manager.getDaos();
	}
	
	/**
	 * <p>접속 목록(DAO)들을 정리합니다. 닫혀 있는 DAO들은 한 번 더 확실히 닫는 시도를 하고 목록에서 제거됩니다.</p>
	 * @throws NotEnoughPrivilegeException : 권한 문제
	 */
	public void cleanDaos() throws NotEnoughPrivilegeException
	{
		if(runner.getPriv_accessDBList() == null) manager.cleanDaos();
		else throw new NotEnoughPrivilegeException(Manager.applyStringTable("On cleanDaos()"));
	}
	
	/**
	 * <p>각 로딩 내역이 얼마나 시간이 걸렸는지 내역을 메시지로 반환합니다.</p>
	 * 
	 * @return 로딩 내역
	 */
	public String initReport()
	{
		return FirstStateView.report();
	}
	
	/**
	 * <p>새 진행상태 이벤트 객체를 만듭니다.</p>
	 * 
	 * @param script : 진행률 변동 시 실행할 스크립트 (변수이름 progress 로 현재 진행률을 % 단위로 액세스 가능)
	 * @return 이벤트 객체
	 */
	public ProgressEvent newProgressEvent(final String script)
	{
		return new ProgressEvent()
		{
			@Override
			public void setValue(int v)
			{
				try
				{
					runner.put("progress", new Integer(v));
					runner.execute(script);
				}
				catch (Throwable e)
				{
					
				}
			}

			@Override
			public void setText(String message)
			{
				manager.log(message);
			}
		};
	}
	
	/**
	 * <p>해당 파일에 권한이 있는지를 확인합니다.</p>
	 * 
	 * @param target : 대상 파일 객체
	 * @param writeMode : 쓰기 모드 여부
	 * @throws FileNotFoundException
	 * @throws NotEnoughPrivilegeException
	 */
	public void checkAuthority(Object file, boolean writeMode) throws FileNotFoundException, NotEnoughPrivilegeException
	{
		File target = StreamUtil.transfer(file);
		if(! (target.exists() || writeMode))
		{
			throw new FileNotFoundException(Manager.applyStringTable("On jsonToDb, file is not exist") + " : " + target);
		}
		else
		{
			boolean isAllowed = false;
			String targetFullPath = target.getAbsolutePath();
			
			if(runner.getPriv_allowedReadFilePath() != null)
			{
				for(int i=0; i<runner.getPriv_allowedReadFilePath().size(); i++)
				{
					if(targetFullPath.startsWith(runner.getPriv_allowedReadFilePath().get(i)))
					{
						isAllowed = true;
						break;
					}
				}
				
				if(! isAllowed)
				{
					throw new NotEnoughPrivilegeException(Manager.applyStringTable("On jsonToDb")); 
				}
			}
		}
	}
	
	/**
	 * <p>파일로부터 테이블 셋들을 불러옵니다. 준비된 테이블 셋 불러오기 도구(빌더)들 중 사용 가능한 것 하나를 찾아 사용합니다.</p>
	 * 
	 * @param file : 불러올 파일
	 * @param event : 진행 상태 이벤트 (null 가능)
	 * @return 불러온 테이블 셋 리스트
	 * @throws NotEnoughPrivilegeException 권한 문제
	 * @throws FileNotFoundException 파일을 찾을 수 없는 경우
	 */
	public List<TableSet> loadTable(Object file, ProgressEvent event) throws NotEnoughPrivilegeException, FileNotFoundException
	{
		File target = StreamUtil.transfer(file);
		checkAuthority(target, false);
		return TableSetBuildUtil.load(target, event);
	}
	
	/**
	 * <p>특정 이름의 저장 도구를 선택해 테이블 셋을 파일로 저장합니다.</p>
	 * 
	 * @param file : 저장할 파일
	 * @param tableSet : 저장할 테이블 셋
	 * @param toolName : 저장 도구 이름
	 * @param event : 진행 상태 이벤트 (null 가능)
	 * @throws NotEnoughPrivilegeException 권한 문제
	 */
	public void saveTable(Object file, TableSet tableSet, String toolName, ProgressEvent event) throws NotEnoughPrivilegeException
	{
		File target = StreamUtil.transfer(file);
		try
		{
			checkAuthority(target, true);
		}
		catch(FileNotFoundException e)
		{
			
		}
		TableSetBuildUtil.save(target, tableSet, toolName, event);
	}
	
	/**
	 * <p>특정 이름의 업로드 도구를 선택해 테이블 셋을 파일로부터 데이터 소스로 전송합니다.</p>
	 * 
	 * @param file : 불러올 파일
	 * @param dao : 데이터 소스 접속 DAO
	 * @param tableName : 데이터를 삽입할 테이블 이름
	 * @param toolName : 업로드 도구 이름
	 * @param event : 진행 상태 이벤트 (null 가능)
	 * @throws NotEnoughPrivilegeException 권한 문제
	 * @throws FileNotFoundException 파일을 찾을 수 없는 경우
	 */
	public void uploadTable(Object file, Dao dao, String tableName, String toolName, ProgressEvent event) throws NotEnoughPrivilegeException, FileNotFoundException
	{
		File target = StreamUtil.transfer(file);
		checkAuthority(target, false);
		TableSetBuildUtil.upload(target, dao, tableName, toolName, event);
	}
	
	/**
	 * <p>특정 이름의 다운로드 도구를 선택해 테이블 셋을 데이터 소스에서 조회해 파일로 저장합니다.</p>
	 * 
	 * @param file : 저장할 파일
	 * @param dao : 데이터 소스 접속 DAO
	 * @param query : 조회 스크립트
	 * @param toolName : 다운로드 도구 이름
	 * @param event : 진행 상태 이벤트 (null 가능)
	 * @throws NotEnoughPrivilegeException 권한 문제
	 */
	public void downloadTable(Object file, Dao dao, String query, String toolName, ProgressEvent event) throws NotEnoughPrivilegeException
	{
		File target = StreamUtil.transfer(file);
		try
		{
			checkAuthority(target, true);
		}
		catch(FileNotFoundException e)
		{
			
		}
		TableSetBuildUtil.download(target, dao, query, toolName, event);
	}
	
	/**
	 * <p>사용 준비가 된 테이블 셋 불러오기 도구 이름들을 리스트로 반환합니다.</p>
	 * 
	 * @return 테이블 셋 불러오기 도구 이름 리스트
	 */
	public List<String> builderNames()
	{
		return TableSetBuildUtil.getBuilderNames();
	}
	
	/**
	 * <p>사용 준비가 된 테이블 셋 저장 도구 이름들을 리스트로 반환합니다.</p>
	 * 
	 * @return 테이블 셋 저장 도구 이름 리스트
	 */
	public List<String> writerNames()
	{
		return TableSetBuildUtil.getWriterNames();
	}
	
	/**
	 * <p>사용 준비가 된 테이블 셋 다운로드 도구 이름들을 리스트로 반환합니다.</p>
	 * 
	 * @return 테이블 셋 다운로드 도구 이름 리스트
	 */
	public List<String> downloaderNames()
	{
		return TableSetBuildUtil.getDownloaderNames();
	}
	
	/**
	 * <p>사용 준비가 된 테이블 셋 업로드 도구 이름들을 리스트로 반환합니다.</p>
	 * 
	 * @return 테이블 셋 업로드 도구 이름 리스트
	 */
	public List<String> uploaderNames()
	{
		return TableSetBuildUtil.getUploaderNames();
	}
	
	/**
	 * <p>테이블 셋 도구를 등록합니다.</p>
	 * 
	 * @param tool : 테이블 셋 도구
	 */
	public void register(TableSetTreator tool)
	{
		TableSetBuildUtil.register(tool);
	}
	
	/**
	 * <p>객체를 바이너리로 변환합니다.</p>
	 * 
	 * @param ob : 변환할 객체
	 * @return 바이너리 (바이트 배열)
	 * @throws IOException
	 */
	public byte[] toBytes(Object ob) throws IOException
	{
		return StreamUtil.toBytes(ob);
	}
	
	/**
	 * <p>바이너리를 객체로 변환을 시도합니다.</p>
	 * 
	 * @param bytes : 변환할 바이너리 데이터 (바이트 배열)
	 * @return 변환된 객체
	 * @throws IOException 입출력 관련 오류 (바이너리로부터 객체 인식 실패한 경우)
	 * @throws ClassNotFoundException 인식한 객체에 맞는 클래스를 찾을 수 없는 경우
	 */
	public Object toObject(byte[] bytes) throws IOException, ClassNotFoundException
	{
		return StreamUtil.toObject(bytes);
	}
	
	/**
	 * <p>언어 설정(스트링 테이블)대로 str 을 번역합니다.</p>
	 * 
	 * @param str : 변역할 텍스트
	 * @return 번역된 텍스트
	 */
	public String translate(String str)
	{
		return Manager.applyStringTable(str);
	}
	
	@Override
	public String help()
	{
		StringBuffer results = new StringBuffer("");
		
		results = results.append(Manager.applyStringTable("In JScriptTool object has a lot of methods.") + "\n");
		results = results.append("help() : " + translate("Return this help messages.") + "\n");
		results = results.append("availableProcessors() : " + translate("Return available processors of this system.") + "\n");
		results = results.append("booleans(object) : " + translate("Trying to convert object into boolean value. If this is impossible, this will throw an Exception.") + "\n");
		results = results.append("cleanDaos() : " + translate("Remove closed DAOs from DAO list.") + "\n");
		results = results.append("command(command) : " + translate("Run OS console command.") + "\n");
		results = results.append("currentTime() : " + translate("Return current time value as big number, milliseconds from 1970.1.1.") + "\n");
		results = results.append("eval(script) : " + translate("Run scripts.") + "\n");
		results = results.append("floats(object) : " + translate("Trying to convert object into floating number.") + "\n");
		results = results.append("freeMemory() : " + translate("Return left memory that this program can use.") + "\n");
		results = results.append("gc() : " + translate("Run garbage collector to clean up the memory.") + "\n");
		results = results.append("getDaos() : " + translate("Return all DAO (each have a DB connection) objects.") + "\n");
		results = results.append("getProperty(option) : " + translate("Return OS property value.") + "\n");
		results = results.append("getSelectedDao() : " + translate("Return selected DAO which has a DB connection") + "\n");
		results = results.append("help() : " + translate("Return these help messages.") + "\n");
		results = results.append("integers(object) : " + translate("Trying to convert object into integer.") + "\n");
		results = results.append("loadClass(className) : " + translate("Trying to load java class dynamically and return class object.") + "\n");
		results = results.append("loadClass(className, filePath) : " + translate("Trying to load java class dynamically with class files and return class object.") + "\n");
		results = results.append("loadDriver(className) : " + translate("Trying to load JDBC driver dynamically.") + "\n");
		results = results.append("loadDriver(className, filePath) : " + translate("Trying to load JDBC driver dynamically with class files.") + "\n");
		results = results.append("maxMemory() : " + translate("Return max memory that this program can use.") + "\n");
		results = results.append("newDao(aliasName) : " + translate("Create new DAO object which can access DB. Alias name can be null.") + "\n");
		results = results.append("readFile(filePath, charset) : " + translate("Read and return text from file. filePath should be an full name of file. UTF-8 is recommended of charset.") + "\n");
		results = results.append("readWeb(filePath, charset) : " + translate("Read and return text from file. filePath should be an full name of file. UTF-8 is recommended of charset.") + "\n");
		results = results.append("saveFile(filePath, contents, charset) : " + translate("Save text as file. filePath should be an full name of file. UTF-8 is recommended of charset.") + "\n");
		results = results.append("selectDao(index) : " + translate("Select DAO (DB connection)") + "\n");
		results = results.append("setInterval(scripts) : " + translate("Run scripts and repeats each 100 milliseconds in new thread.") + "\n");
		results = results.append("setInterval(scripts, interval) : " + translate("Run scripts repeats each interval milliseconds in new thread.") + "\n");
		results = results.append("strings(object) : " + translate("Trying to convert object into string.") + "\n");
		results = results.append("totalMemory() : " + translate("Return total memory that this program using.") + "\n");
		results = results.append("addExternalJar(jarFile) : " + translate("Add another JAR file into the JAR file list. This file will be scanned to find available JDBC driver.") + "\n");
		results = results.append("getProperty(key) : " + translate("Return JVM property value.") + "\n");
		results = results.append("getenv(key) : " + translate("Return system environment variable value.") + "\n");
		results = results.append("currentTime() : " + translate("Return UTC system time as milliseconds.") + "\n");
		results = results.append("hgfToDb(dao, filePath) : " + translate("Read HGF file and insert on data source.") + "\n");
		results = results.append("readFile(filePath, charset) : " + translate("Read text file and return as string.") + "\n");
		results = results.append("readWeb(url, charset) : " + translate("Connect to URL and get text value.") + "\n");
		results = results.append("saveFile(filePath, contents, charset) : " + translate("Save contents as file.") + "\n");
		results = results.append("strings(obj) : " + translate("Convert object into string type.") + "\n");
		results = results.append("date(dateFormText, formatter) : " + translate("Convert dateFormText into Date object. If formatter is null, covert dateFormText as yyyyMMdd form text into Date object. Without any parameters, return Date object which indicates today.") + "\n");
		results = results.append("integers(obj) : " + translate("Convert object into integer type.") + "\n");
		results = results.append("floats(obj) : " + translate("Convert object into float type.") + "\n");
		results = results.append("booleans(obj) : " + translate("Convert object into boolean type.") + "\n");
		results = results.append("setInterval(scripts, interval) : " + translate("Each interval milliseconds, run script repeats on another thread. Thread point object will be returned;") + "\n");
		results = results.append("clearInterval(threadPoint) : " + translate("Stop thread.") + "\n");
		results = results.append("loadClass(classFullName, fileArr) : " + translate("Trying to load java class and return Class object.") + "\n");
		results = results.append("loadDriver(classFullName, fileArr) : " + translate("Trying to load JDBC driver.") + "\n");
		results = results.append("createCommunicator() : " + translate("Create communicator object to run server role easily.") + "\n");
		results = results.append("createReceiveHandler(scripts) : " + translate("Create receive handler to use communicator object. If communicator object receive some messages, scripts will be run, and this scripts can access received messages by 'recv_msg' variable.") + "\n");
		results = results.append("createServer() : " + translate("Create new web server object which serve web services.") + "\n");
		results = results.append("createPageContent() : " + translate("Create page content object which determine to serve contents on web server object.") + "\n");
		results = results.append("createPage() : " + translate("Create page object which serve each page on the page content.") + "\n");
		results = results.append("dao(alias) : " + translate("Create new DAO object.") + "\n");
		results = results.append("initReport() : " + translate("Return report message which is about initializing this program.") + "\n");
		
		return results.toString();
	}
	
	// initReport()
	@Override
	public void noMoreUse()
	{
		for(int i=0; i<threads.size(); i++)
		{
			threads.get(i).close();
		}
		threads.clear();
		for(int i=0; i<canBeEndeds.size(); i++)
		{
			canBeEndeds.get(i).noMoreUse();
		}
		this.canBeEndeds.clear();
		this.runner = null;
		this.manager = null;
	}
	@Override
	public boolean isAlive()
	{
		return runner != null;
	}
}
