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

package hjow.hgtable.dao;

import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Random;

import hjow.datasource.common.DataSourceTool;
import hjow.hgtable.HasManyServices;
import hjow.hgtable.IncludesException;
import hjow.hgtable.Manager;
import hjow.hgtable.jscript.JScriptObject;
import hjow.hgtable.jscript.JScriptRunner;
import hjow.hgtable.jscript.module.Module;
import hjow.hgtable.tableset.ColumnTableSet;
import hjow.hgtable.tableset.DefaultTableSet;
import hjow.hgtable.tableset.ResultSetTableSet;
import hjow.hgtable.tableset.ResultStruct;
import hjow.hgtable.tableset.TableSet;
import hjow.hgtable.ui.AccessInfo;
import hjow.hgtable.util.ModuleUtil;

/**
 * <p>데이터 소스 (RDBMS 등) 와 데이터를 주고받기 위한 객체에 관여하는 클래스입니다.</p>
 * 
 * @author HJOW
 *
 */
public abstract class Dao extends HasManyServices implements JScriptObject
{	
	private static final long serialVersionUID = 820888887157056106L;
	protected long daoId = new Random().nextLong();
	protected String id, pw, url;
	protected String classPath = null;
	protected String classFile = null;
	protected String aliasName = "";
	protected JScriptRunner runner = null;
	protected Manager manager = null;
	protected transient Class<? extends TableSet> defaultTableSetType = DefaultTableSet.class;
	
	@SuppressWarnings("unchecked")
	public Dao(Manager manager)
	{
		this.manager = manager;
		try
		{
			defaultTableSetType = (Class<? extends TableSet>) Class.forName("defaultTableSetClass");
			if(defaultTableSetType == null) defaultTableSetType = DefaultTableSet.class;
		}
		catch (ClassNotFoundException e)
		{
			defaultTableSetType = DefaultTableSet.class;
		}
		initServiceCleaner();
	}
	
	/**
	 * <p>연결된 매니저 객체를 반환합니다.</p>
	 * 
	 * @return 매니저 객체
	 */
	public Manager getManager()
	{
		return manager;
	}
	
	/**
	 * <p>데이터 소스와 연결합니다.</p>
	 * 
	 * @throws Exception : 네트워크 문제, 인증 실패, 권한 문제, 데이터 소스 내 문제
	 */
	public abstract void connect() throws Exception;
	
	/**
	 * <p>데이터 소스와 연결합니다.</p>
	 * 
	 * @param id : 데이터 소스의 ID
	 * @param pw : 데이터 소스의 비밀번호
	 * @param url : 데이터 소스의 URL
	 * @throws Exception : 네트워크 문제, 인증 실패, 권한 문제, 데이터 소스 내 문제
	 */
	public abstract void connect(String id, String pw, String url) throws Exception;
	
	/**
	 * <p>직접 드라이버 클래스를 불러와 접속을 시도합니다. 이 기능이 구현되어 있지 않은 DAO의 경우 예외를 발생합니다.</p>
	 * 
	 * @throws Exception 파일 액세스 문제, 클래스 이름 문제, 네트워크 문제, 인증 문제 등
	 */
	public abstract void connectDirectly() throws Exception;
	
	/**
	 * <p>별도의 쓰레드에서 접속을 시도합니다.</p>
	 * 
	 * @param afterWorks : 접속 시도 후 실행할 작업을 run() 메소드에 담은 Runnable 객체, null 가능
	 */
	public abstract void connectParallely(Runnable afterWorks);
	
	/**
	 * <p>데이터 소스와의 연결을 종료합니다.</p>
	 * 
	 */
	public abstract void close();
	
	/**
	 * <p>연결이 아직 유효한지 여부를 반환합니다.</p>
	 * 
	 * @return true 이면 연결이 아직 유효한 것
	 */
	public abstract boolean isAlive();
	
	/**
	 * <p>데이터 소스에 스크립트를 보내 실행하고 그 결과를 받아 옵니다. 결과가 없으면 null 을 반환합니다.</p>
	 * 
	 * @param scripts : 데이터 소스 스크립트
	 * @return 테이블 셋 객체
	 * @throws Exception 네트워크 문제, 데이터 소스 스크립트 문법 오류, 데이터 소스 문제
	 */
	public TableSet query(String scripts) throws Exception
	{
		return query(scripts, false);
	}
	
	/**
	 * <p>데이터 소스에 스크립트를 보내 실행하고 그 결과를 받아 옵니다. 결과가 없으면 null 을 반환합니다.</p>
	 * 
	 * @param scripts : 데이터 소스 스크립트
	 * @param tableSetType : 테이블 셋 타입
	 * @return 테이블 셋 객체
	 * @throws Exception 네트워크 문제, 데이터 소스 스크립트 문법 오류, 데이터 소스 문제
	 */
	public abstract TableSet query(String scripts, Class<? extends TableSet> tableSetType) throws Exception;
	
	/**
	 * <p>데이터 소스에 스크립트를 보내 실행하고 그 결과를 받아 옵니다. 결과가 없으면 null 을 반환합니다.</p>
	 * 
	 * @param scripts : 데이터 소스 스크립트
	 * @param tableSetTypeName : 테이블 셋 타입 클래스명
	 * @return 테이블 셋 객체
	 * @throws ClassNotFoundException : 클래스명이 잘못된 경우
	 * @throws Exception 네트워크 문제, 데이터 소스 스크립트 문법 오류, 데이터 소스 문제
	 */
	@SuppressWarnings("unchecked")
	public TableSet query(String sql, String tableSetTypeName) throws ClassNotFoundException, Exception
	{
		return query(sql, (Class<? extends TableSet>) Class.forName(tableSetTypeName));
	}
	
	/**
	 * <p>데이터 소스에 스크립트를 보내 실행하고 그 결과를 받아 옵니다. 결과가 없으면 null 을 반환합니다.</p>
	 * 
	 * @param scripts : 데이터 소스 스크립트
	 * @param noOut : true 시 실행 내역 콘솔에 출력 안 함
	 * @return 테이블 셋 객체
	 * @throws Exception 네트워크 문제, 데이터 소스 스크립트 문법 오류, 데이터 소스 문제
	 */
	protected abstract TableSet query(String scripts, boolean noOut) throws Exception;
	
	/**
	 * <p>DB에 SQL문을 넘겨 실행합니다. 결과가 없으면 null, 있으면 TableSet 형태로 반환합니다.</p>
	 * 
	 * @param sql : SQL 스크립트
	 * @param noOut : true 시 실행 내역 콘솔에 출력 안 함
	 * @param tableSetType : 테이블 셋 타입
	 * @return 결과가 없으면 null, 있으면 TableSet 객체
	 * @throws Exception : DBMS에서의 오류, 혹은 네트워크 문제
	 */
	protected abstract TableSet query(String sql, boolean noOut, Class<? extends TableSet> tableSetType) throws Exception;
	
	/**
	 * <p>로그 출력 없이 데이터 소스에 스크립트를 보내 실행해 그 결과를 받아 옵니다. 인증된 모듈 객체를 매개변수로 넣어야 실행이 가능합니다.</p>
	 * 
	 * @param scripts : 데이터 소스 스크립트
	 * @param module : 사용할 모듈 객체, 인증된 모듈이어야 동작함
	 * @return 테이블 셋 객체
	 * @throws Exception 네트워크 문제, 데이터 소스 스크립트 문법 오류, 데이터 소스 문제, 혹은 인증 안 된 모듈인 경우
	 */
	public TableSet queryWithoutLog(String scripts, Module module) throws Exception
	{
		if(ModuleUtil.checkAuthorize(module))
		{
			return query(scripts, true);
		}
		else return null; // TODO : 예외 반환해야 함
//		else throw new InvalidPri;
	}
	
	/**
	 * <p>데이터를 데이터 소스로부터 가져옵니다.</p>
	 * 
	 * @param tableName : 조회할 테이블 이름 혹은 구조
	 * @param additionalWheres : 조건문 혹은 옵션
	 * @param startsIndex : 시작 번호
	 * @param endIndex : 끝 번호
	 * @return 조회 결과 객체
	 * @throws Exception : 데이터 소스에서의 오류, 혹은 네트워크 문제
	 */
	public abstract ResultStruct select(String tableName, String additionalWheres, int startsIndex, int endIndex) throws Exception;
	
	/**
	 * <p>접속 정보를 반환합니다.</p>
	 * 
	 * @return 접속 정보 객체
	 */
	public abstract AccessInfo getAccessInfo();
	
	/**
	 * <p>접속에 필요한 정보를 미리 입력합니다.</p>
	 * 
	 * @param accessInfo : 접속 정보 객체
	 */
	public abstract void setAccessInfo(AccessInfo accessInfo);
	
	/**
	 * <p>데이터 소스에 커밋(작업한 내용 일괄 적용) 명령을 보냅니다. 트랜잭션을 지원하는 데이터 소스에서 유효합니다.</p>
	 * 
	 * @throws Exception : 네트워크 문제, 데이터 소스 상에서의 문제
	 */
	public abstract void commit() throws Exception;
	
	/**
	 * <p>데이터 소스에 롤백(적용하지 않은 작업 내용 취소) 명령을 보냅니다. 트랜잭션을 지원하는 데이터 소스에서 유효합니다.</p>
	 * 
	 * @throws Exception : 네트워크 문제, 데이터 소스 상에서의 문제
	 */
	public abstract void rollback() throws Exception;
	
	/**
	 * <p>데이터 소스의 종류를 반환합니다. 예를 들어, RDBMS의 경우 그 이름을 반환합니다.</p>
	 * 
	 * @return 데이터 소스 종류
	 */
	public abstract String getDataSourceType();
	
	/**
	 * <p>해당 데이터 소스에 해당하는 툴 객체를 반환합니다.</p>
	 * 
	 * @return 툴 객체
	 */
	public abstract DataSourceTool getDataSourceTool();
	
	/**
	 * <p>조회 결과 생성할 테이블 셋 객체 형태를 지정합니다.</p>
	 * 
	 * @param tableSetType : 테이블 셋 종류 (클래스 이름, 혹은 클래스 객체)
	 */
	@SuppressWarnings("unchecked")
	public void setTableSetType(Object tableSetType)
	{
		if(tableSetType instanceof Class<?>) setDefaultTableSetType((Class<? extends TableSet>) tableSetType);
		else if(tableSetType instanceof TableSet) setDefaultTableSetType((Class<? extends TableSet>) tableSetType.getClass());
		else
		{
			try
			{
				setDefaultTableSetType((Class<? extends TableSet>) Class.forName(String.valueOf(tableSetType)));
			}
			catch (ClassNotFoundException e)
			{
				throw new IncludesException(e);
			}
		}
	}
	
	/**
	 * <p>조회 시 생성할 테이블 셋 설정을 반환합니다.</p>
	 * 
	 * @return 테이블 셋 클래스 객체
	 */
	public Class<? extends TableSet> getTableSetType()
	{
		return defaultTableSetType;
	}
	
	/**
	 * <p>조회 시 생성할 테이블 셋 설정에 해당하는 빈 테이블 셋 객체를 만들어 반환합니다.</p>
	 * 
	 * @return
	 */
	public TableSet createEmptyTableSet()
	{
		TableSet testInstance = null;
		try
		{
			if(defaultTableSetType != null) testInstance = defaultTableSetType.newInstance();
			else testInstance = defaultTableSetType.newInstance();
		}
		catch(Exception e)
		{
			throw new IncludesException(e);
		}
		try
		{
			testInstance.noMoreUse();
		}
		catch(Exception e)
		{
			
		}
		
		if(testInstance instanceof ColumnTableSet) return new DefaultTableSet();
		else if(testInstance instanceof ResultSetTableSet) return new ResultSetTableSet();
		else 
		{
			return testInstance;
		}
	}
	
	/**
	 * <p>스크립트 실행 모듈을 삽입합니다.</p>
	 * 
	 * @param runner : 스크립트 실행 모듈
	 */
	public void setRunner(JScriptRunner runner)
	{
		this.runner = runner;
	}
	
	/**
	 * <p>현재 입력된 ID 를 반환합니다.</p>
	 * 
	 * @return ID
	 */
	public String getId()
	{
		return id;
	}
	public void setId(String id)
	{
		this.id = id;
	}
	public String getPw()
	{
		return pw;
	}
	public void setPw(String pw)
	{
		this.pw = pw;
	}
	public String getUrl()
	{
		return url;
	}
	public void setUrl(String url)
	{
		this.url = url;
	}
	public String getClassPath()
	{
		return classPath;
	}
	public void setClassPath(String classPath)
	{
		this.classPath = classPath;
	}
	public String getClassFile()
	{
		return classFile;
	}
	public void setClassFile(String classFile)
	{
		this.classFile = classFile;
	}
	public String getAliasName()
	{
		return aliasName;
	}
	public void setAliasName(String aliasName)
	{
		this.aliasName = aliasName;
	}
	public JScriptRunner getRunner()
	{
		return runner;
	}
	@Override
	public void noMoreUse()
	{
		close();
		closeServiceCleaner();
		manager = null;
		defaultTableSetType = null;
	}

	public long getDaoId()
	{
		return daoId;
	}

	public Class<? extends TableSet> getDefaultTableSetType()
	{
		return defaultTableSetType;
	}

	public void setDefaultTableSetType(Class<? extends TableSet> defaultTableSetType)
	{
		this.defaultTableSetType = defaultTableSetType;
	}

	public void setDaoId(long daoId)
	{
		this.daoId = daoId;
	}

	public void setManager(Manager manager)
	{
		this.manager = manager;
	}

	/**
	 * <p>요청을 취소합니다. 이 기능을 지원하지 않으면 예외를 발생합니다.</p>
	 * 
	 * @throws SQLException : 취소 요청 중 발생한 데이터 소스 내 오류
	 * @throws SQLFeatureNotSupportedException : 데이터 소스가 요청 취소 기능을 지원하지 않는 경우
	 */
	public void cancel() throws SQLException, SQLFeatureNotSupportedException
	{
		throw new SQLFeatureNotSupportedException(Manager.applyStringTable("Cannot cancel requests on this connection."));
	}
}