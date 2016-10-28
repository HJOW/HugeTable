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

package hjow.dbtool.common;

import java.util.List;

import hjow.datasource.common.DataSourceTool;
import hjow.hgtable.Manager;
import hjow.hgtable.dao.Dao;
import hjow.hgtable.tableset.TableSet;
import hjow.hgtable.util.DataUtil;

/**
 * <p>이 클래스 객체는 DB에 대한 주요 정보 및 자주 쓰는 쿼리를 메소드로 가집니다.</p>
 * 
 * @author HJOW
 *
 */
public abstract class DBTool extends DataSourceTool
{
	private static final long serialVersionUID = 5638128250495756834L;
	
	/**
	 * 
	 * <p>기본 생성자입니다.</p>
	 */
	public DBTool()
	{
		super();
	}
	
	/**
	 * <p>생성자입니다. DB에 액세스할 수 있는 DAO 객체가 필요합니다.</p>
	 * 
	 * @param dao : DAO 객체
	 */
	public DBTool(Dao dao)
	{
		super(dao);
	}
	
	/**
	 * <p>DB의 JDBC 드라이버의 클래스 이름을 풀네임으로 반환합니다.</p>
	 * 
	 * @return JDBC 드라이버 클래스명
	 */
	public abstract String getJdbcClassPath();
	
	/**
	 * <p>JDBC URL의 앞부분 형식을 반환합니다.</p>
	 * 
	 * @return JDBC URL 앞부분
	 */
	public abstract String jdbcUrlPrefix();
	
	/**
	 * <p>테이블 목록을 반환하는 SQL 문장을 반환합니다.</p>
	 * 
	 * @param allData : true 시 이름 뿐만 아니라 관련 정보 모두를 반환하는 쿼리 문장을 반환합니다.
	 * @return SQL 문장
	 */
	public abstract String getTableListQuery(boolean allData);
	
	/**
	 * <p>테이블 목록 가져올 때 테이블 이름이 되는 컬럼 이름을 반환합니다.</p>
	 * 
	 * @return 테이블 목록 조회 결과 테이블 이름 컬럼명
	 */
	public abstract String getTableListKeyColumnName();
	
	/**
	 * <p>툴 객체가 지원하는 DB 이름을 반환합니다.</p>
	 * 
	 * @return DB 이름
	 */
	public abstract String getDBName();
	
	/**
	 * <p>행 번호를 의미하는 키워드 예약어를 반환합니다. 이 기능을 지원하지 않는 경우 null 을 반환합니다.</p>
	 * 
	 * @return 행 번호를 의미하는 키워드 예약어
	 */
	public abstract String getRownumName();
	
	/**
	 * <p>테이블 목록을 반환합니다. 결과는 TableSet 으로 반환됩니다.</p>
	 * 
	 * @param allData : false 사용 시 테이블 이름에 해당하는 컬럼 값만 반환됩니다. true 사용 시 쿼리 결과에 나온 다른 결과까지 모두 반환됩니다.
	 * @param searchCondition : 검색어 (null 혹은 빈 값 시 조건 없이 검색)
	 * @return 테이블 목록 (TableSet 객체)
	 * @throws Exception DBMS에서 발생한 문제, 네트워크 문제 등
	 */
	public TableSet tableList(boolean allData, String searchCondition) throws Exception
	{
		if(getTableListQuery(allData) == null) return null;
		String statements = getTableListQuery(allData);
		if(DataUtil.isNotEmpty(searchCondition))
		{
			statements = "SELECT * FROM ( " + statements + " ) WHERE " + getTableListKeyColumnName() + " LIKE '%" + searchCondition + "%'";
		}
		return dao.query(statements);
	}
	
	/**
	 * <p>해당 이름의 테이블의 정보(주로 컬럼 정보)를 담은 테이블 셋 객체를 반환합니다. 이 기능을 지원하지 않는 경우 null 을 반환합니다.</p>
	 * 
	 * @param tableName : 테이블 이름
	 * @return 테이블 정보
	 * @throws Exception DBMS에서 발생한 문제, 네트워크 문제 등
	 */
	public TableSet tableInfo(String tableName) throws Exception
	{
		String sql = getTableInfoQuery(tableName);
		if(DataUtil.isNotEmpty(sql)) return dao.query(sql);
		return null;
	}
	
	/**
	 * <p>해당 이름의 테이블의 정보(주로 컬럼 정보)를 조회하는 SQL 문장을 반환합니다.</p>
	 * 
	 * @param tableName : 테이블 이름
	 * @return 테이블 정보 조회 SQL 문장
	 */
	public abstract String getTableInfoQuery(String tableName);
	
	/**
	 * <p>사용자 목록을 조회하는 SQL 문장을 반환합니다.</p>
	 * 
	 * @param isDBA : DBA 계정인 경우에만 true 를 사용할 수 있습니다. true 사용 시 접속한 DB의 모든 사용자 목록을 반환합니다.
	 * @param allData : false 사용 시 테이블 이름에 해당하는 컬럼 값만 반환됩니다. true 사용 시 쿼리 결과에 나온 다른 결과까지 모두 반환됩니다.
	 * @return SQL 문장
	 */
	public abstract String getUserListQuery(boolean isDBA, boolean allData);
	
	/**
	 * <p>사용자 목록 가져올 때 사용자 이름이 되는 컬럼 이름을 반환합니다.</p>
	 * 
	 * @return 사용자 목록 조회 결과 사용자 이름 컬럼명
	 */
	public abstract String getUserListKeyColumnName();
	
	/**
	 * <p>사용자 목록을 반환합니다. 결과는 TableSet 으로 반환됩니다.</p>
	 * 
	 * @param isDBA : DBA 계정인 경우에만 true 를 사용할 수 있습니다. true 사용 시 접속한 DB의 모든 사용자 목록을 반환합니다.
	 * @param allData : false 사용 시 테이블 이름에 해당하는 컬럼 값만 반환됩니다. true 사용 시 쿼리 결과에 나온 다른 결과까지 모두 반환됩니다.
	 * @param searchCondition : 검색어 (null 혹은 빈 값 시 조건 없이 검색)
	 * @return 사용자 목록 (TableSet 객체)
	 * @throws Exception DBMS에서 발생한 문제, 네트워크 문제 등
	 */
	public TableSet userList(boolean isDBA, boolean allData, String searchCondition) throws Exception
	{
		if(getUserListQuery(isDBA, allData) == null) return null;
		String statements = getUserListQuery(isDBA, allData);
		if(DataUtil.isNotEmpty(searchCondition))
		{
			statements = "SELECT * FROM ( " + statements + " ) WHERE " + getUserListKeyColumnName() + " LIKE '%" + searchCondition + "%'";
		}
		return dao.query(statements);
	}
	
	/**
	 * <p>테이블스페이스(혹은 데이터베이스) 목록을 조회하는 SQL 문장을 반환합니다. 해당 개념이 없는 DBMS의 경우 null을 반환합니다.</p>
	 * 
	 * @param isDBA : DBA 계정인 경우에만 true 를 사용할 수 있습니다. true 사용 시 접속한 DB의 모든 테이블스페이스 목록을 반환합니다.
	 * @param allData : false 사용 시 테이블스페이스 이름에 해당하는 컬럼 값만 반환됩니다. true 사용 시 쿼리 결과에 나온 다른 결과까지 모두 반환됩니다.
	 * @return SQL 문장
	 */
	public abstract String getTablespaceListQuery(boolean isDBA, boolean allData);
	
	/**
	 * <p>테이블스페이스 목록 가져올 때 테이블스페이스 이름이 되는 컬럼 이름을 반환합니다.</p>
	 * 
	 * @return 테이블스페이스 목록 조회 결과 테이블스페이스 이름 컬럼명
	 */
	public abstract String getTablespaceListKeyColumnName();
	
	/**
	 * <p>테이블스페이스(혹은 데이터베이스) 목록을 반환합니다. 해당 개념이 없는 DBMS의 경우 null을 반환합니다. 결과는 TableSet 으로 반환됩니다.</p>
	 * 
	 * @param isDBA : DBA 계정인 경우에만 true 를 사용할 수 있습니다. true 사용 시 접속한 DB의 모든 테이블스페이스 목록을 반환합니다.
	 * @param allData : false 사용 시 테이블스페이스 이름에 해당하는 컬럼 값만 반환됩니다. true 사용 시 쿼리 결과에 나온 다른 결과까지 모두 반환됩니다.
	 * @param searchCondition : 검색어 (null 혹은 빈 값 시 조건 없이 검색)
	 * @return 테이블스페이스(혹은 데이터베이스) 목록 (TableSet 객체)
	 * @throws Exception DBMS에서 발생한 문제, 네트워크 문제 등
	 */
	public TableSet tablespaceList(boolean isDBA, boolean allData, String searchCondition) throws Exception
	{
		if(getTablespaceListQuery(isDBA, allData) == null) return null;
		String statements = getTablespaceListQuery(isDBA, allData);
		if(DataUtil.isNotEmpty(searchCondition))
		{
			statements = "SELECT * FROM ( " + statements + " ) WHERE " + getTablespaceListKeyColumnName() + " LIKE '%" + searchCondition + "%'";
		}
		return dao.query(statements);
	}
	
	/**
	 * <p>뷰 목록을 조회하는 SQL 문장을 반환합니다. 조회할 수 없는 DBMS인 경우 null 을 반환합니다.</p>
	 * 
	 * @param isDBA : DBA 계정인 경우에만 true 를 사용할 수 있습니다. true 사용 시 접속한 DB의 모든 뷰 목록을 반환합니다.
	 * @param allData : false 사용 시 뷰 이름에 해당하는 컬럼 값만 반환됩니다. true 사용 시 쿼리 결과에 나온 다른 결과까지 모두 반환됩니다.
	 * @return SQL 문장
	 */
	public abstract String getViewListQuery(boolean isDBA, boolean allData);
	
	/**
	 * <p>뷰 목록 가져올 때 뷰 이름이 되는 컬럼 이름을 반환합니다.</p>
	 * 
	 * @return 뷰 목록 조회 결과 뷰 이름 컬럼명
	 */
	public abstract String getViewListKeyColumnName();
	
	/**
	 * <p>뷰 목록을 반환합니다. 결과는 TableSet 으로 반환됩니다.</p>
	 * 
	 * @param isDBA : DBA 계정인 경우에만 true 를 사용할 수 있습니다. true 사용 시 접속한 DB의 모든 뷰 목록을 반환합니다.
	 * @param allData : false 사용 시 뷰 이름에 해당하는 컬럼 값만 반환됩니다. true 사용 시 쿼리 결과에 나온 다른 결과까지 모두 반환됩니다.
	 * @param searchCondition : 검색어 (null 혹은 빈 값 시 조건 없이 검색)
	 * @return 뷰 목록 (TableSet 객체)
	 * @throws Exception DBMS에서 발생한 문제, 네트워크 문제 등
	 */
	public TableSet viewList(boolean isDBA, boolean allData, String searchCondition) throws Exception
	{
		if(getViewListQuery(isDBA, allData) == null) return null;
		String statements = getViewListQuery(isDBA, allData);
		if(DataUtil.isNotEmpty(searchCondition))
		{
			statements = "SELECT * FROM ( " + statements + " ) WHERE " + getViewListKeyColumnName() + " LIKE '%" + searchCondition + "%'";
		}
		return dao.query(statements);
	}
	
	/**
	 * <p>데이터베이스 링크 목록을 조회하는 SQL 문장을 반환합니다. 해당 개념이 없는 DBMS의 경우 null을 반환합니다.</p>
	 * 
	 * @param isDBA : DBA 계정인 경우에만 true 를 사용할 수 있습니다. true 사용 시 접속한 DB의 모든 데이터베이스 링크 목록을 반환합니다.
	 * @param allData : false 사용 시 데이터베이스 링크 이름에 해당하는 컬럼 값만 반환됩니다. true 사용 시 쿼리 결과에 나온 다른 결과까지 모두 반환됩니다.
	 * @return SQL 문장
	 */
	public abstract String getDbLinkListQuery(boolean isDBA, boolean allData);
	
	/**
	 * <p>데이터베이스 링크 목록 가져올 때 데이터베이스 링크 이름이 되는 컬럼 이름을 반환합니다.</p>
	 * 
	 * @return 데이터베이스 링크 목록 조회 결과 데이터베이스 링크 이름 컬럼명
	 */
	public abstract String getDbLinkListKeyColumnName();
	
	/**
	 * <p>데이터베이스 링크 목록을 반환합니다. 해당 개념이 없는 DBMS의 경우 null을 반환합니다. 결과는 TableSet 으로 반환됩니다.</p>
	 * 
	 * @param isDBA : DBA 계정인 경우에만 true 를 사용할 수 있습니다. true 사용 시 접속한 DB의 모든 데이터베이스 링크 목록을 반환합니다.
	 * @param allData : false 사용 시 데이터베이스 링크 이름에 해당하는 컬럼 값만 반환됩니다. true 사용 시 쿼리 결과에 나온 다른 결과까지 모두 반환됩니다.
	 * @param searchCondition : 검색어 (null 혹은 빈 값 시 조건 없이 검색)
	 * @return 데이터베이스 링크 목록 (TableSet 객체)
	 * @throws Exception DBMS에서 발생한 문제, 네트워크 문제 등
	 */
	public TableSet dbLinkList(boolean isDBA, boolean allData, String searchCondition) throws Exception
	{
		if(getDbLinkListQuery(isDBA, allData) == null) return null;
		String statements = getDbLinkListQuery(isDBA, allData);
		if(DataUtil.isNotEmpty(searchCondition))
		{
			statements = "SELECT * FROM ( " + statements + " ) WHERE " + getDbLinkListKeyColumnName() + " LIKE '%" + searchCondition + "%'";
		}
		return dao.query(statements);
	}
	
	/**
	 * <p>객체 목록을 조회하는 SQL 문장을 반환합니다. 해당 개념이 없는 DBMS의 경우 null을 반환합니다.</p>
	 * 
	 * @param isDBA : DBA 계정인 경우에만 true 를 사용할 수 있습니다. true 사용 시 접속한 DB의 모든 객체 목록을 반환합니다.
	 * @param allData : false 사용 시 객체 이름에 해당하는 컬럼 값만 반환됩니다. true 사용 시 쿼리 결과에 나온 다른 결과까지 모두 반환됩니다.
	 * @return SQL 문장
	 */
	public abstract String getObjectListQuery(boolean isDBA, boolean allData);
	
	/**
	 * <p>객체 목록 가져올 때 객체 이름이 되는 컬럼 이름을 반환합니다.</p>
	 * 
	 * @return 객체 목록 조회 결과 객체 이름 컬럼명
	 */
	public abstract String getObjectListKeyColumnName();
	
	/**
	 * <p>객체 목록을 반환합니다. 해당 개념이 없는 DBMS의 경우 null을 반환합니다. 결과는 TableSet 으로 반환됩니다.</p>
	 * 
	 * @param isDBA : DBA 계정인 경우에만 true 를 사용할 수 있습니다. true 사용 시 접속한 DB의 모든 객체 링크 목록을 반환합니다.
	 * @param allData : false 사용 시 객체 이름에 해당하는 컬럼 값만 반환됩니다. true 사용 시 쿼리 결과에 나온 다른 결과까지 모두 반환됩니다.
	 * @param searchCondition : 검색어 (null 혹은 빈 값 시 조건 없이 검색)
	 * @return 객체 목록 (TableSet 객체)
	 * @throws Exception DBMS에서 발생한 문제, 네트워크 문제 등
	 */
	public TableSet objectList(boolean isDBA, boolean allData, String searchCondition) throws Exception
	{
		if(getObjectListQuery(isDBA, allData) == null) return null;
		String statements = getObjectListQuery(isDBA, allData);
		if(DataUtil.isNotEmpty(searchCondition))
		{
			statements = "SELECT * FROM ( " + statements + " ) WHERE " + getObjectListKeyColumnName() + " LIKE '%" + searchCondition + "%'";
		}
		return dao.query(statements);
	}
	
	/**
	 * <p>데이터 파일 목록을 조회하는 SQL 문장을 반환합니다. 해당 개념이 없는 DBMS의 경우 null을 반환합니다.</p>
	 * 
	 * @param allData : false 사용 시 데이터 파일 이름에 해당하는 컬럼 값만 반환됩니다. true 사용 시 쿼리 결과에 나온 다른 결과까지 모두 반환됩니다.
	 * @return SQL 문장
	 */
	public abstract String getDataFileListQuery(boolean allData);
	
	/**
	 * <p>데이터 파일 목록 가져올 때 데이터 파일 이름이 되는 컬럼 이름을 반환합니다.</p>
	 * 
	 * @return 데이터 파일 목록 조회 결과 데이터 파일 이름 컬럼명
	 */
	public abstract String getDataFileListKeyColumnName();
	
	/**
	 * <p>데이터 파일 목록을 반환합니다. 해당 개념이 없는 DBMS의 경우 null을 반환합니다. 결과는 TableSet 으로 반환됩니다.</p>
	 * 
	 * @param allData : false 사용 시 데이터 파일 이름에 해당하는 컬럼 값만 반환됩니다. true 사용 시 쿼리 결과에 나온 다른 결과까지 모두 반환됩니다.
	 * @param searchCondition : 검색어 (null 혹은 빈 값 시 조건 없이 검색)
	 * @return 데이터 파일 목록 (TableSet 객체)
	 * @throws Exception DBMS에서 발생한 문제, 네트워크 문제 등
	 */
	public TableSet dataFileList(boolean allData, String searchCondition) throws Exception
	{
		if(getDataFileListQuery(allData) == null) return null;
		String statements = getDataFileListQuery(allData);
		if(DataUtil.isNotEmpty(searchCondition))
		{
			statements = "SELECT * FROM ( " + statements + " ) WHERE " + getDataFileListKeyColumnName() + " LIKE '%" + searchCondition + "%'";
		}
		return dao.query(statements);
	}
	
	/**
	 * <p>테이블, 혹은 뷰 내용을 조회하는 쿼리를 반환합니다. ROWNUM 키워드를 지원하지 않는 DB인 경우 전체 데이터가 조회됩니다.</p>
	 * 
	 * @param tableOrViewName : 테이블, 혹은 뷰 이름
	 * @return SQL 문장
	 */
	public String getSimpleSelectQuery(String tableOrViewName)
	{
		if(getRownumName() != null) return "SELECT * FROM " + tableOrViewName + " WHERE " + getRownumName() + " <= 100";
		return "SELECT * FROM " + tableOrViewName;
	}
	
	/**
	 * <p>테이블, 혹은 뷰 내용을 조회한 결과를 반환합니다.</p>
	 * 
	 * @param tableOrViewName : 테이블, 혹은 뷰 이름
	 * @return 조회 결과
	 * @throws Exception DBMS에서 발생한 문제, 네트워크 문제 등
	 */
	public TableSet simpleSelect(String tableOrViewName) throws Exception
	{
		if(getSimpleSelectQuery(tableOrViewName) == null) return null;
		return dao.query(getSimpleSelectQuery(tableOrViewName));
	}
	
	/**
	 * <p>테이블, 혹은 뷰를 조회합니다.</p>
	 * 
	 * @param tableOrViewName : 테이블, 혹은 뷰 이름
	 * @return 조회 결과
	 * @throws Exception DBMS에서 발생한 문제, 네트워크 문제 등
	 */
	public TableSet select(String tableOrViewName) throws Exception
	{
		return select(tableOrViewName, null, null);
	}
	
	/**
	 * <p>테이블, 혹은 뷰를 조회합니다.</p>
	 * 
	 * @param tableOrViewName : 테이블, 혹은 뷰 이름
	 * @param columns : 조회할 컬럼 목록 (null 시 *)
	 * @return 조회 결과
	 * @throws Exception DBMS에서 발생한 문제, 네트워크 문제 등
	 */
	public TableSet select(String tableOrViewName, List<String> columns) throws Exception
	{
		return select(tableOrViewName, columns, null);
	}
	
	/**
	 * <p>테이블, 혹은 뷰를 조회합니다.</p>
	 * 
	 * @param tableOrViewName : 테이블, 혹은 뷰 이름
	 * @param where : 조건절 내용
	 * @return 조회 결과
	 * @throws Exception DBMS에서 발생한 문제, 네트워크 문제 등
	 */
	public TableSet select(String tableOrViewName, String where) throws Exception
	{
		return select(tableOrViewName, null, where);
	}
	
	/**
	 * <p>테이블, 혹은 뷰를 조회합니다.</p>
	 * 
	 * @param tableOrViewName : 테이블, 혹은 뷰 이름
	 * @param columns : 조회할 컬럼 목록 (null 시 *)
	 * @param where : 조건절 내용
	 * @return 조회 결과
	 * @throws Exception DBMS에서 발생한 문제, 네트워크 문제 등
	 */
	public TableSet select(String tableOrViewName, List<String> columns, String where) throws Exception
	{
		String selectColumns = "*";
		if(columns != null)
		{
			selectColumns = "";
			for(String c : columns)
			{
				if(! selectColumns.equals("")) selectColumns = selectColumns + ",";
				selectColumns = selectColumns + c;
			}
		}
		if(where != null) return dao.query("SELECT " + selectColumns + " FROM " + tableOrViewName + " WHERE " + where);
		return dao.query("SELECT " + selectColumns + " FROM " + tableOrViewName);
	}
	
	/**
	 * <p>프로시저 목록을 조회하는 SQL 문장을 반환합니다. 해당 개념이 없는 DBMS의 경우 null을 반환합니다.</p>
	 * 
	 * @param isDBA : DBA 계정인 경우에만 true 를 사용할 수 있습니다. true 사용 시 접속한 DB의 모든 프로시저 목록을 반환합니다.
	 * @param allData : false 사용 시 프로시저 이름에 해당하는 컬럼 값만 반환됩니다. true 사용 시 쿼리 결과에 나온 다른 결과까지 모두 반환됩니다.
	 * @return SQL 문장
	 */
	public abstract String getProcedureListQuery(boolean isDBA, boolean allData);
	
	/**
	 * <p>프로시저 목록 가져올 때 프로시저 이름이 되는 컬럼 이름을 반환합니다.</p>
	 * 
	 * @return 프로시저 목록 조회 결과 프로시저 이름 컬럼명
	 */
	public abstract String getProcedureListKeyColumnName();
	
	/**
	 * <p>함수 목록을 조회하는 SQL 문장을 반환합니다. 해당 개념이 없는 DBMS의 경우 null을 반환합니다.</p>
	 * 
	 * @param isDBA : DBA 계정인 경우에만 true 를 사용할 수 있습니다. true 사용 시 접속한 DB의 모든 함수 목록을 반환합니다.
	 * @param allData : false 사용 시 함수 이름에 해당하는 컬럼 값만 반환됩니다. true 사용 시 쿼리 결과에 나온 다른 결과까지 모두 반환됩니다.
	 * @return SQL 문장
	 */
	public abstract String getFunctionListQuery(boolean isDBA, boolean allData);
	
	/**
	 * <p>함수 목록 가져올 때 함수 이름이 되는 컬럼 이름을 반환합니다.</p>
	 * 
	 * @return 함수 목록 조회 결과 함수 이름 컬럼명
	 */
	public abstract String getFunctionListKeyColumnName();
	
	/**
	 * <p>프로시저 목록을 반환합니다. 해당 개념이 없는 DBMS의 경우 null을 반환합니다. 결과는 TableSet 으로 반환됩니다.</p>
	 * 
	 * @param isDBA : DBA 계정인 경우에만 true 를 사용할 수 있습니다. true 사용 시 접속한 DB의 모든 프로시저 목록을 반환합니다.
	 * @param allData : false 사용 시 프로시저 이름에 해당하는 컬럼 값만 반환됩니다. true 사용 시 쿼리 결과에 나온 다른 결과까지 모두 반환됩니다.
	 * @param searchCondition : 검색어 (null 혹은 빈 값 시 조건 없이 검색)
	 * @return 프로시저 목록 (TableSet 객체)
	 * @throws Exception DBMS에서 발생한 문제, 네트워크 문제 등
	 */
	public TableSet procedureList(boolean isDBA, boolean allData, String searchCondition) throws Exception
	{
		if(getProcedureListQuery(isDBA, allData) == null) return null;
		String statements = getProcedureListQuery(isDBA, allData);
		if(DataUtil.isNotEmpty(searchCondition))
		{
			statements = "SELECT * FROM ( " + statements + " ) WHERE " + getProcedureListKeyColumnName() + " LIKE '%" + searchCondition + "%'";
		}
		return dao.query(statements);
	}
	
	/**
	 * <p>함수 목록을 반환합니다. 해당 개념이 없는 DBMS의 경우 null을 반환합니다. 결과는 TableSet 으로 반환됩니다.</p>
	 * 
	 * @param isDBA : DBA 계정인 경우에만 true 를 사용할 수 있습니다. true 사용 시 접속한 DB의 모든 함수 목록을 반환합니다.
	 * @param allData : false 사용 시 함수 이름에 해당하는 컬럼 값만 반환됩니다. true 사용 시 쿼리 결과에 나온 다른 결과까지 모두 반환됩니다.
	 * @param searchCondition : 검색어 (null 혹은 빈 값 시 조건 없이 검색)
	 * @return 함수 목록 (TableSet 객체)
	 * @throws Exception DBMS에서 발생한 문제, 네트워크 문제 등
	 */
	public TableSet functionList(boolean isDBA, boolean allData, String searchCondition) throws Exception
	{
		if(getFunctionListQuery(isDBA, allData) == null) return null;
		String statements = getFunctionListQuery(isDBA, allData);
		if(DataUtil.isNotEmpty(searchCondition))
		{
			statements = "SELECT * FROM ( " + statements + " ) WHERE " + getFunctionListKeyColumnName() + " LIKE '%" + searchCondition + "%'";
		}
		return dao.query(statements);
	}
	
	/**
	 * <p>프로시저의 스크립트 내용을 조회하는 SQL 문장을 반환합니다. 해당 개념이 없는 DBMS의 경우 null을 반환합니다.</p>
	 * 
	 * @param procedureName : 프로시저 이름
	 * @return SQL 문장
	 */
	public abstract String getProcedureScriptQuery(String procedureName);
	
	/**
	 * <p>함수의 스크립트 내용을 조회하는 SQL 문장을 반환합니다. 해당 개념이 없는 DBMS의 경우 null을 반환합니다.</p>
	 * 
	 * @param functionName : 함수 이름
	 * @return SQL 문장
	 */
	public abstract String getFunctionScriptQuery(String functionName);
	
	/**
	 * <p>프로시저 내용을 반환합니다. 해당 개념이 없는 DBMS의 경우 null을 반환합니다.</p>
	 * 
	 * @param dao : DB에 접속한 DAO 객체
	 * @param procedureName : 내용을 조회할 프로시저 이름
	 * @return 프로시저 스크립트 내용
	 * @throws Exception : 접속 문제, 네트워크 문제, DBMS 에서 발생한 문제
	 */
	public String getProcedureScript(Dao dao, String procedureName) throws Exception
	{
		String queries = getProcedureScriptQuery(procedureName);
		if(queries == null) return null;
		TableSet results = dao.query(queries);
		if(results.getRecordCount() <= 0) return "";
		else
		{
			StringBuffer resultScript = new StringBuffer("");
			for(int i=0; i<results.getRecordCount(); i++)
			{
				resultScript = resultScript.append(results.getRecord(i).getDataOf(0) + "\n");
			}
			return resultScript.toString();
		}
	}
	
	/**
	 * <p>함수 내용을 반환합니다. 해당 개념이 없는 DBMS의 경우 null을 반환합니다.</p>
	 * 
	 * @param dao : DB에 접속한 DAO 객체
	 * @param functionName : 내용을 조회할 함수 이름
	 * @return 함수 스크립트 내용
	 * @throws Exception : 접속 문제, 네트워크 문제, DBMS 에서 발생한 문제 
	 */
	public String getFunctionScript(Dao dao, String functionName) throws Exception
	{
		String queries = getProcedureScriptQuery(functionName);
		if(queries == null) return null;
		TableSet results = dao.query(queries);
		if(results.getRecordCount() <= 0) return "";
		else
		{
			StringBuffer resultScript = new StringBuffer("");
			for(int i=0; i<results.getRecordCount(); i++)
			{
				resultScript = resultScript.append(results.getRecord(i).getDataOf(0) + "\n");
			}
			return resultScript.toString();
		}
	}
	
	/**
	 * <p>프로시저, 혹은 함수의 스크립트 내용을 반환합니다. 해당 개념이 없는 DBMS의 경우 null을 반환합니다.</p>
	 * 
	 * @param objectName : 프로시저, 혹은 함수 이름
	 * @return 해당 프로시저, 혹은 함수의 스크립트 내용
	 * @throws Exception 네트워크 문제, 데이터 소스 스크립트 문법 오류, 데이터 소스 문제
	 */
	public String getScriptOf(String objectName) throws Exception
	{
		return null;
	}
	
	/**
	 * <p>프로시저들의 이름과 그 상태 정보를 반환합니다. 컬럼 이름은 각각 NAME 과 STATUS 이며, STATUS 값은 O 또는 X 입니다. 해당 개념이 없는 DBMS의 경우 null을 반환합니다.</p>
	 * 
	 * @param wheres : 추가 조건문
	 * @return 프로시저 이름과 상태값 정보 (테이블 셋 객체)
	 * @throws Exception : 접속 문제, 네트워크 문제, DBMS 에서 발생한 문제 
	 */
	public TableSet getProcedureWithStatus(String wheres) throws Exception
	{
		return null;
	}
	
	/**
	 * <p>함수들의 이름과 그 상태 정보를 반환합니다. 컬럼 이름은 각각 NAME 과 STATUS 이며, STATUS 값은 O 또는 X 입니다. 해당 개념이 없는 DBMS의 경우 null을 반환합니다.</p>
	 * 
	 * @param wheres : 추가 조건문
	 * @return 함수 이름과 상태값 정보 (테이블 셋 객체)
	 * @throws Exception : 접속 문제, 네트워크 문제, DBMS 에서 발생한 문제 
	 */
	public TableSet getFunctionWithStatus(String wheres) throws Exception
	{
		return null;
	}
	
	/**
	 * <p>뷰들의 이름과 그 상태 정보를 반환합니다. 컬럼 이름은 각각 NAME 과 STATUS 이며, STATUS 값은 O 또는 X 입니다. 해당 개념이 없는 DBMS의 경우 null을 반환합니다.</p>
	 * 
	 * @param wheres : 추가 조건문
	 * @return 뷰 이름과 상태값 정보 (테이블 셋 객체)
	 * @throws Exception : 접속 문제, 네트워크 문제, DBMS 에서 발생한 문제 
	 */
	public TableSet getViewWithStatus(String wheres) throws Exception
	{
		return null;
	}
	
	/**
	 * <p>프로시저들의 이름과 그 상태 정보를 반환합니다. 컬럼 이름은 각각 NAME 과 STATUS 이며, STATUS 값은 O 또는 X 입니다. 해당 개념이 없는 DBMS의 경우 null을 반환합니다.</p>
	 * 
	 * @param wheres : 추가 조건문
	 * @param keyword : 검색어
	 * @return 프로시저 이름과 상태값 정보 (테이블 셋 객체)
	 * @throws Exception : 접속 문제, 네트워크 문제, DBMS 에서 발생한 문제 
	 */
	public TableSet getProcedureWithStatus(String keyword, String wheres) throws Exception
	{
		return null;
	}
	
	/**
	 * <p>함수들의 이름과 그 상태 정보를 반환합니다. 컬럼 이름은 각각 NAME 과 STATUS 이며, STATUS 값은 O 또는 X 입니다. 해당 개념이 없는 DBMS의 경우 null을 반환합니다.</p>
	 * 
	 * @param wheres : 추가 조건문
	 * @param keyword : 검색어
	 * @return 함수 이름과 상태값 정보 (테이블 셋 객체)
	 * @throws Exception : 접속 문제, 네트워크 문제, DBMS 에서 발생한 문제 
	 */
	public TableSet getFunctionWithStatus(String keyword, String wheres) throws Exception
	{
		return null;
	}
	
	/**
	 * <p>뷰들의 이름과 그 상태 정보를 반환합니다. 컬럼 이름은 각각 NAME 과 STATUS 이며, STATUS 값은 O 또는 X 입니다. 해당 개념이 없는 DBMS의 경우 null을 반환합니다.</p>
	 * 
	 * @param wheres : 추가 조건문
	 * @param keyword : 검색어
	 * @return 뷰 이름과 상태값 정보 (테이블 셋 객체)
	 * @throws Exception : 접속 문제, 네트워크 문제, DBMS 에서 발생한 문제 
	 */
	public TableSet getViewWithStatus(String keyword, String wheres) throws Exception
	{
		return null;
	}
	
	/**
	 * <p>이 DB툴 객체가 연결된 DAO을 반환합니다.</p>
	 * 
	 * @return DAO 객체 (DB 연결)
	 */
	public Dao getDao()
	{
		return dao;
	}
	
	/**
	 * <p>DB툴 객체에 새 DAO을 연결합니다. 기존 DAO 객체가 닫히지는 않습니다.</p>
	 * 
	 * @param DAO 객체 (DB 연결)
	 */
	public void setDao(Dao dao)
	{
		this.dao = dao;
	}
	
	
	@Override
	public String help()
	{
		StringBuffer results = new StringBuffer("");
		
		results = results.append(Manager.applyStringTable("This class has useful methods and informations for") + " " + getDBName() + "." + "\n");
		results = results.append("getDBName()" + " : " + Manager.applyStringTable("Return DB name for this class.") + "\n");
		results = results.append("getJdbcClassPath()" + " : " + Manager.applyStringTable("Return JDBC driver class fullname.") + "\n");
		results = results.append("getDao()" + " : " + Manager.applyStringTable("Return dao object connected with this tool object.") + "\n");
		
		
		if(getRownumName() != null)	results = results.append("getRownumName()" + " : " + Manager.applyStringTable("Return keyword means row number.") + "\n");
		if(getTableListQuery(true) != null)
		{
			results = results.append("tableList(allData)" + " : " 
					+ Manager.applyStringTable("Return table list. If allData is true, other informations will be shown.") + "\n");
		}
		if(getUserListQuery(true, true) != null)
		{
			results = results.append("userList(isDBA, allData)" + " : " 
					+ Manager.applyStringTable("Return user list of DB. If isDBA is true, all user list in DB is returned. If allData is true, other informations will be shown.") + "\n");
		}
		if(getTablespaceListQuery(true, true) != null)
		{
			results = results.append("tablespaceList(isDBA, allData)" + " : "
					+ Manager.applyStringTable("Return tablespace list of DB. If isDBA is true, all tablespace list in DB is returned. If allData is true, other informations will be shown.") + "\n");
		}
		if(getViewListQuery(true, true) != null)
		{
			results = results.append("viewList(isDBA, allData)" + " : "
					+ Manager.applyStringTable("Return view list of DB. If isDBA is true, all view list in DB is returned. If allData is true, other informations will be shown.") + "\n");
		}
		if(getDbLinkListQuery(true, true) != null)
		{
			results = results.append("dbLinkList(isDBA, allData)" + " : "
					+ Manager.applyStringTable("Return database link list of DB. If isDBA is true, all database link list in DB is returned. If allData is true, other informations will be shown.") + "\n");
		}
		if(getObjectListQuery(true, true) != null)
		{
			results = results.append("objectList(isDBA, allData)" + " : "
					+ Manager.applyStringTable("Return object list of DB. If isDBA is true, all object list in DB is returned. If allData is true, other informations will be shown.") + "\n");
		}
		if(getDataFileListQuery(true) != null)
		{
			results = results.append("dataFileList(allData)" + " : "
					+ Manager.applyStringTable("Return data file list of DB. If allData is true, other informations will be shown.") + "\n");
		}
		
		
		return results.toString();
	}
	
	@Override
	public void noMoreUse()
	{
		
	}
	
	@Override
	public boolean isSQLBased()
	{
		return true;
	}
	
	@Override
	public DBTool newInstance(Dao dao)
	{
		return (DBTool) super.newInstance(dao);
	}
}
