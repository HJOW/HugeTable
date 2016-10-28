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

package hjow.datasource.common;

import hjow.hgtable.Manager;
import hjow.hgtable.dao.Dao;
import hjow.hgtable.jscript.JScriptObject;

/**
 * <p>데이터 소스 (RDBMS 등) 를 유용하게 사용하기 위한 여러 메소드와 정보를 제공하는 툴 객체 생성에 관여하는 클래스입니다.</p>
 * 
 * @author HJOW
 *
 */
public abstract class DataSourceTool implements JScriptObject
{
	private static final long serialVersionUID = 8324671997327573949L;
	protected Dao dao;
	
	/**
	 * <p>기본 생성자입니다.</p>
	 * 
	 */
	public DataSourceTool()
	{
		
	}
	
	/**
	 * <p>생성자입니다. 데이터 소스에 액세스할 수 있는 DAO 객체가 필요합니다.</p>
	 * 
	 * @param dao : DAO 객체
	 */
	public DataSourceTool(Dao dao)
	{
		this.dao = dao;
	}
	
	/**
	 * <p>DAO과의 연결을 닫아 순환 참조를 끊습니다. DAO 객체가 닫히지는 않습니다.</p>
	 */
	public void close()
	{
		dao = null;
	}
	
	/**
	 * <p>접속이 계속 유지되기 위해 주기적으로 실행해도 될 스크립트를 반환합니다. 이 기능이 지원되지 않으면 null 을 반환합니다.</p>
	 * 
	 * @return 의미 없지만 실행 가능한 스크립트
	 */
	public String getPreventTimeoutScript()
	{
		return null;
	}
	
	@Override
	public boolean isAlive()
	{
		return dao != null && dao.isAlive();
	}
	
	/**
	 * <p>SQL 기반 데이터베이스인 경우 true 를 반환합니다.</p>
	 * 
	 * @return SQL 사용 여부
	 */
	public boolean isSQLBased()
	{
		return false;
	}
	
	
	/**
	 * <p>새 툴 객체를 생성해 반환합니다.</p>
	 * 
	 * @param dao : DAO 객체
	 * @return 새 객체
	 */
	public DataSourceTool newInstance(Dao dao)
	{
		try
		{
			return this.getClass().getDeclaredConstructor(Dao.class).newInstance(dao);
		}
		catch (Throwable e)
		{
			dao.getManager().logError(e, Manager.applyStringTable("On creating new Data source tool"));
			return null;
		}
	}
}
