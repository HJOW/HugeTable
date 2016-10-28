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

package hjow.hgtable.tableset;

import java.sql.ResultSet;
import java.util.List;

import hjow.hgtable.dao.Dao;

/**
 * <p>테이블 객체이며 데이터들을 포함합니다.</p>
 * 
 * <p>이름을 가지고 있으며, 컬럼 데이터를 여럿 가지고 있습니다.</p>
 * <p>각각의 컬럼은 해당 컬럼의 이름과 데이터 타입, 그리고 데이터들을 가집니다.</p>
 * 
 * <p>표 형태로 모식도를 그렸을 때, 세로줄을 여럿 포함한 형태라고 보시면 됩니다. 각 컬럼들은 데이터 갯수가 모두 동일해야 합니다.</p>
 * 
 * @author HJOW
 *
 */
public class DefaultTableSet extends ColumnTableSet
{
	private static final long serialVersionUID = -1216607670834622307L;
	
	/**
	 * <p>기본 생성자입니다. 이 생성자에서는 아무 동작도 하지 않습니다.</p>
	 * 
	 */
	public DefaultTableSet()
	{
		super();
	}
	
	/**
	 * <p>이름과 컬럼 데이터를 집어넣어 객체를 생성할 수 있는 생성자입니다. TableSet 객체 복사에 사용됩니다.</p>
	 * <p>객체 복사를 위해서는 매개변수에 넣기 전 별도로 복사 작업을 해야 합니다.</p>
	 * 
	 * @param name : 테이블 이름
	 * @param columns : 컬럼들(데이터 포함)
	 */
	public DefaultTableSet(String name, List<Column> columns)
	{
		super(name, columns);
	}
	
	/**
	 * <p>DB로부터 데이터를 받아 와 객체를 생성합니다. SELECT 구문과 WHERE 절을 명시할 수 있습니다.</p>
	 * 
	 * <p>Oracle DB를 사용하는 경우 한번에 전체를 가져오지 않고 단계별로 나누어 가져올 수 있습니다.</p>
	 * <p>Oracle DB가 아닌 경우, WHERE 절에 레코드 번호를 가져올 수 있는 키워드를 환경설정 step_keyword 에 명시하면 단계별로 나누어 가져올 수 있습니다.</p>
	 * <p>단계별로 나누어 가져오는 경우 한 단계에서 환경설정의 step_size 값 만큼씩을 가져옵니다. 지정되지 않은 경우 기본값은 100입니다.</p>
	 * <p>Oracle DB를 사용하면서 나누어 가져오고 싶지 않다면 step_size 값에 0을 지정하면 됩니다.</p>
	 * 
	 * @param name : 테이블 이름
	 * @param dao : DB에 접속한 DAO 객체 (반드시 DB에 접속되어 있어야 함)
	 * @param selectQuery : SELECT 구문의 SQL 스크립트
	 * @param additionalWheres : 추가 WHERE 절
	 * @throws Exception : DB에 접속하지 않았거나 DBMS에서 발생한 오류, 혹은 네트워크 문제
	 */
	public DefaultTableSet(String name, Dao dao, String selectQuery, String additionalWheres) throws Exception
	{
		super(name, dao, selectQuery, additionalWheres);
	}
	/**
	 * <p>DB로부터 이미 데이터를 가져온 경우 사용할 수 있는 생성자입니다.</p>
	 * 
	 * @param name : 테이블 이름
	 * @param data : JDBC 호환 ResultSet 객체
	 * @throws Exception : 데이터 관련 문제
	 */
	public DefaultTableSet(String name, ResultSet data) throws Exception
	{	
		super(name, data);
	}
	@Override
	public String help()
	{		
		return null;
	}
}
