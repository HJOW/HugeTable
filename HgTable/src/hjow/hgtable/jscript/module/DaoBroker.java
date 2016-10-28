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

import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;

import hjow.datasource.common.DataSourceTool;
import hjow.hgtable.Manager;
import hjow.hgtable.dao.Dao;
import hjow.hgtable.jscript.JScriptObject;
import hjow.hgtable.tableset.ResultStruct;
import hjow.hgtable.tableset.TableSet;
import hjow.hgtable.ui.AccessInfo;

/**
 * <p>이 클래스는 DAO 액세스를 대신하기 위한 객체에 관여합니다. 모듈 내 스크립트에서 dao 이름으로 사용할 수 있습니다.</p>
 * 
 * @author HJOW
 *
 */
public class DaoBroker extends Dao implements JScriptObject
{
	private static final long serialVersionUID = 2087158592851857738L;
	
	/**
	 * <p>객체를 생성합니다.</p>
	 * 
	 * @param manager : 매니저 객체
	 */
	public DaoBroker(Manager manager)
	{
		super(manager);
		this.manager = manager;
	}
	
	@Override
	public TableSet query(String scripts) throws Exception
	{		
		return manager.getDao().query(scripts);
	}
	
	@Override
	public TableSet query(String scripts, Class<? extends TableSet> tableSetType) throws Exception
	{
		return manager.getDao().query(scripts, tableSetType);
	}
	
	@Override
	public TableSet queryWithoutLog(String scripts, Module module)
			throws Exception
	{
		return manager.getDao().queryWithoutLog(scripts, module);
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
	public ResultStruct select(String tableName, String additionalWheres, int startsIndex, int endIndex) throws Exception
	{
		return manager.getDao().select(tableName, additionalWheres, startsIndex, endIndex);
	}
	
	/**
	 * <p>데이터 소스에 접속을 시도합니다.</p>
	 * 
	 * @throws Exception 네트워크 문제, 데이터 소스 문제
	 */
	public void connect() throws Exception
	{
		close();
		manager.getDao().connect();
	}
	
	/**
	 * <p>데이터 소스에 접속을 시도합니다.</p>
	 * 
	 * @param id : 데이터 소스 접속 ID
	 * @param pw : 데이터 소스 접속 비밀번호
	 * @param url : 데이터 소스 URL
	 * @throws Exception 네트워크 문제, 데이터 소스 문제
	 */
	public void connect(String id, String pw, String url) throws Exception
	{
		close();
		manager.getDao().connect(id, pw, url);
	}
	
	/**
	 * <p>데이터 소스에 접속을 시도합니다.</p>
	 * 
	 * @param className : 드라이버 클래스명 (풀네임)
	 * @param id : 데이터 소스 접속 ID
	 * @param pw : 데이터 소스 접속 비밀번호
	 * @param url : 데이터 소스 URL
	 * @throws Exception 네트워크 문제, 데이터 소스 문제
	 */
	public void connect(String className, String id, String pw, String url) throws Exception
	{
		close();
		manager.getDao().setClassPath(className);
		manager.getDao().connect(id, pw, url);
	}
	
	/**
	 * <p>선택한 DAO의 별칭을 반환합니다.</p>
	 * 
	 * @return DAO의 별칭
	 */
	public String getAliasName()
	{
		return manager.getDao().getAliasName();
	}
	
	/**
	 * <p>선택한 DAO의 데이터 소스 툴 객체를 반환합니다.</p>
	 * 
	 * @return 데이터 소스 툴
	 */
	public DataSourceTool getDataSourceTool()
	{
		return manager.getDao().getDataSourceTool();
	}
	
	/**
	 * <p>선택한 DAO의 데이터 소스 이름을 반환합니다.</p>
	 * 
	 * @return 데이터 소스 이름(종류)
	 */
	public String getDataSourceType()
	{
		return manager.getDao().getDataSourceType();
	}
	
	/**
	 * <p>데이터 소스에 접속해 있는지 여부를 반환합니다.</p>
	 * 
	 * @return 접속 여부
	 */
	public boolean isAlive()
	{
		return manager.getDao().isAlive();
	}
	
	/**
	 * <p>데이터 소스에 커밋 명령을 보냅니다. 작업한 내용이 반영됩니다. 트랜잭션을 지원하는 데이터 소스에서만 의미가 있습니다.</p>
	 * 
	 * @throws Exception 네트워크 문제, 데이터 소스 문제
	 */
	public void commit() throws Exception
	{
		manager.getDao().commit();
	}
	
	/**
	 * <p>데이터 소스에 롤백 명령을 보냅니다. 작업한 내용이 취소됩니다. 트랜잭션을 지원하는 데이터 소스에서만 의미가 있습니다.</p>
	 * 
	 * @throws Exception 네트워크 문제, 데이터 소스 문제
	 */
	public void rollback() throws Exception
	{
		manager.getDao().rollback();
	}
	
	/**
	 * <p>선택된 DAO에 입력된 ID를 반환합니다.</p>
	 * 
	 * @return ID
	 */
	public String getId()
	{
		return manager.getDao().getId();
	}
	
	/**
	 * <p>선택된 DAO에 입력된 URL를 반환합니다.</p>
	 * 
	 * @return URL
	 */
	public String getUrl()
	{
		return manager.getDao().getUrl();
	}
	
	/**
	 * <p>선택된 DAO에 대한 toString() 실행 결과를 반환합니다.</p>
	 */
	@Override
	public String toString()
	{
		return manager.getDao().toString();
	}
	
	/**
	 * <p>선택된 DAO의 접속을 닫습니다.</p>
	 * 
	 */
	public void close()
	{
		manager.getDao().close();
	}

	@Override
	public void noMoreUse()
	{
		this.manager = null;
	}

	@Override
	public String help()
	{
		return null;
	}

	@Override
	public void connectParallely(Runnable afterWorks)
	{
		manager.getDao().connectParallely(afterWorks);
	}

	@Override
	public AccessInfo getAccessInfo()
	{
		AccessInfo infos = manager.getDao().getAccessInfo();
		infos.setPw("");
		return infos;
	}
	
	@Override
	public void cancel() throws SQLFeatureNotSupportedException, SQLException
	{
		manager.getDao().cancel();
	}

	@Override
	protected TableSet query(String scripts, boolean noOut) throws Exception
	{
		return null;
	}
	
	@Override
	protected TableSet query(String sql, boolean noOut, Class<? extends TableSet> tableSetType) throws Exception
	{
		return null;
	}

	@Override
	public void connectDirectly() throws Exception
	{
		close();
		manager.getDao().connectDirectly();
	}

	@Override
	public void setAccessInfo(AccessInfo accessInfo)
	{
		manager.getDao().setAccessInfo(accessInfo);
	}
}
