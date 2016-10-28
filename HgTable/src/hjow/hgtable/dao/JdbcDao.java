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

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import hjow.datasource.common.DataSourceTool;
import hjow.dbtool.common.DBTool;
import hjow.dbtool.cubrid.CTool;
import hjow.dbtool.h2.HTool;
import hjow.dbtool.mariadb.MTool;
import hjow.dbtool.oracle.OTool;
import hjow.hgtable.HThread;
import hjow.hgtable.IncludesException;
import hjow.hgtable.Main;
import hjow.hgtable.Manager;
import hjow.hgtable.NotConnectedException;
import hjow.hgtable.classload.ClassTool;
import hjow.hgtable.classload.DynamicLoader;
import hjow.hgtable.jscript.JScriptRunner;
import hjow.hgtable.tableset.Column;
import hjow.hgtable.tableset.ColumnTableSet;
import hjow.hgtable.tableset.DefaultTableSet;
import hjow.hgtable.tableset.ResultSetTableSet;
import hjow.hgtable.tableset.ResultStruct;
import hjow.hgtable.tableset.TableSet;
import hjow.hgtable.ui.AccessInfo;
import hjow.hgtable.ui.NeedtoEnd;
import hjow.hgtable.util.DataSourceToolUtil;
import hjow.hgtable.util.DataUtil;
import hjow.hgtable.util.debug.DebuggingUtil;

/**
 * <p>이 클래스 객체는 DB에 JDBC 드라이버를 통해 접속하고 질의를 전송하며 결과를 받습니다.</p>
 * 
 * @author HJOW
 *
 */
public class JdbcDao extends Dao
{
	private static final long serialVersionUID = 6658551622704304541L;
	protected transient Connection connection = null;
	
	protected transient PreparedStatement prepared = null;	
	protected DBTool tool = null;
	
	protected transient ContinuousThread continuer = null;
	protected transient ConnectThread connectThread = null;
	protected transient boolean isFirstToMakePrepared = true;
	
	/**
	 * <p>기본 생성자입니다.</p>
	 * 
	 */
	public JdbcDao(Manager manager)
	{
		super(manager);
	}
	
	/**
	 * <p>DB의 ID, 암호, URL를 받아, 객체를 생성하자마자 접속을 시도합니다.</p>
	 * 
	 * <p>별도의 JDBC 드라이버를 불러와야 할 경우 사전에 classPath 필드에 JDBC 드라이버 클래스명을 삽입해야 합니다.</p>
	 * 
	 * @param manager : 매니저 객체
	 * @param id : DB ID
	 * @param pw : DB 암호
	 * @param url : JDBC URL
	 * @throws SQLException : 접속 실패 시 발생, ID 혹은 암호가 틀렸거나 해당 URL에 서버가 없을 경우
	 */	
	public JdbcDao(Manager manager, String id, String pw, String url) throws Exception
	{
		super(manager);
		connect(id, pw, url);
	}
	
	/**
	 * <p>DB의 접속 정보를 받아, 객체를 생성하자마자 접속을 시도합니다.</p>
	 * 
	 * @param manager : 매니저 객체
	 * @param access : DB 접속 정보
	 * @throws SQLException : 접속 실패 시 발생, ID 혹은 암호가 틀렸거나 해당 URL에 서버가 없을 경우
	 */
	public JdbcDao(Manager manager, AccessInfo access) throws Exception
	{
		super(manager);
		if(access.getClassPath() != null)
		{
			this.classPath = access.getClassPath();
		}
		connect(access.getId(), access.getPw(), access.getUrl());
	}
	
	/**
	 * <p>DB의 ID, 암호, URL를 받아, 객체를 생성하자마자 접속을 시도합니다.</p>
	 * 
	 * @param manager : 매니저 객체
	 * @param jdbcClassPath : JDBC 드라이버 클래스명 (풀네임)
	 * @param id : DB ID
	 * @param pw : DB 암호
	 * @param url : JDBC URL
	 * @throws SQLException : 접속 실패 시 발생, ID 혹은 암호가 틀렸거나 해당 URL에 서버가 없을 경우
	 */
	public JdbcDao(Manager manager, String jdbcClassPath, String id, String pw, String url) throws Exception
	{
		super(manager);
		this.classPath = jdbcClassPath;
		connect(id, pw, url);
	}
	
	/**
	 * <p>DB의 ID, 암호, URL를 받아, 객체를 생성하자마자 접속을 시도합니다.</p>
	 * 
	 * @param manager : 매니저 객체
	 * @param jdbcClassFile : JDBC 드라이버가 있는 jar 파일, 혹은 class 파일들이 있는 경로
	 * @param jdbcClassPath : JDBC 드라이버 클래스명 (풀네임)
	 * @param id : DB ID
	 * @param pw : DB 암호
	 * @param url : JDBC URL
	 * @throws SQLException : 접속 실패 시 발생, ID 혹은 암호가 틀렸거나 해당 URL에 서버가 없을 경우
	 */
	public JdbcDao(Manager manager, String jdbcClassFile, String jdbcClassPath, String id, String pw, String url) throws Exception
	{
		super(manager);
		this.classFile = jdbcClassFile;
		this.classPath = jdbcClassPath;
		connect(id, pw, url);
	}
	
	/**
	 * <p>DB에 접속을 시도합니다.</p>
	 * 
	 * <p>별도의 JDBC 드라이버를 불러와야 할 경우 사전에 classPath 필드에 JDBC 드라이버 클래스명을 삽입해야 합니다.
	 *    이를 위해 setClassPath(String classPath) 메소드를 먼저 사용하시면 됩니다.</p>
	 * 
	 * @param id : DB ID
	 * @param pw : DB 암호
	 * @param url : JDBC URL
	 * @throws SQLException : 접속 실패 시 발생, ID 혹은 암호가 틀렸거나 해당 URL에 서버가 없을 경우
	 */
	@Override
	public void connect(String id, String pw, String url) throws Exception
	{
		this.id = id;
		this.pw = pw;
		this.url = url;		
		connect();
	}
	
	/**
	 * <p>DB에 접속을 시도합니다.</p>
	 * 
	 * <p>사전에 ID, 암호, URL에 해당하는 필드에 값을 먼저 삽입한 후 이용해야 합니다.</p>
	 * 
	 * @throws Exception : 접속 실패 시 발생, ID 혹은 암호가 틀렸거나 해당 URL에 서버가 없을 경우
	 */
	@Override
	public void connect() throws Exception
	{
		close();
		
		try
		{
			if(classFile != null)
			{
				String[] files = classFile.split("|");
				List<String> fileList = new Vector<String>();
				for(int i=0; i<files.length; i++)
				{
					fileList.add(files[i]);
				}
				ClassTool.loadDriver(classPath, fileList);
			}
			else if(classPath != null)
			{
				ClassTool.loadDriver(classPath);
			}
		}
		catch(Throwable e1)
		{
			manager.logError(e1, Manager.applyStringTable("On loading class file"), true);
		}
		
		connection = DriverManager.getConnection(url, id, pw);
		
		if(DataUtil.parseBoolean(Manager.getOption("prevent_timeout")))
		{
			String continuerGap = Manager.getOption("prevent_timeout_gap");
			if(DataUtil.isEmpty(continuerGap)) continuer = new ContinuousThread(this);
			else  continuer = new ContinuousThread(this, Integer.parseInt(continuerGap));
		}
	}
	
	/**
	 * <p>직접 드라이버 클래스를 불러와 접속을 시도합니다.</p>
	 * 
	 * @throws Exception 파일 액세스 문제, 클래스 이름 문제, 네트워크 문제, 인증 문제 등
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void connectDirectly() throws Exception
	{
		close();
		if(classPath != null)
		{
			List<File> jarFiles = ClassTool.localJdbcFiles(true);
			DynamicLoader urlClassLoader = null;
			for(File f : jarFiles)
			{
				manager.logNotLn(Manager.applyStringTable("Trying to connect directly with") + " : " + String.valueOf(f) + " --> ");
				try
				{
					URL url = f.toURI().toURL();
					URL[] urlArray = new URL[1];
					urlArray[0] = url;
					
					urlClassLoader = new DynamicLoader(urlArray, System.class.getClassLoader());
					Driver driver = ((Class<? extends Driver>) urlClassLoader.loadClass(classPath)).newInstance();
					
					Properties newProperty = new Properties();
					newProperty.setProperty("user", this.id);
					newProperty.setProperty("password", this.pw);
					connection = driver.connect(this.url, newProperty);
					
					manager.log(Manager.applyStringTable("SUCCESS"));
					if(DataUtil.parseBoolean(Manager.getOption("prevent_timeout")))
					{
						String continuerGap = Manager.getOption("prevent_timeout_gap");
						if(DataUtil.isEmpty(continuerGap)) continuer = new ContinuousThread(this);
						else  continuer = new ContinuousThread(this, Integer.parseInt(continuerGap));
					}
					break;
				}
				catch(SQLException e)
				{
					manager.logError(e, Manager.applyStringTable("Connection failed from data source"), true);
					break;
				}
				catch(ClassNotFoundException e)
				{
					manager.log(Manager.applyStringTable("FAIL"));
				}
				catch(Throwable e)
				{
					manager.log(Manager.applyStringTable("FAIL"));
					manager.logError(e, Manager.applyStringTable("On connect directly"), true);
				}
			}
		}
		else throw new NullPointerException(Manager.applyStringTable("Need to input classPath"));
	}
	
	@Override
	public void connectParallely(Runnable afterWorks)
	{
		connectThread  = new ConnectThread(this, afterWorks);
	}
	
	/**
	 * <p>접속을 해제합니다.</p>
	 * 
	 */
	public void disconnect()
	{
		close();
	}
	/**
	 * <p>접속된 DB의 이름을 반환합니다.</p>
	 * 
	 * @return DB 이름
	 */
	public String getConnecedDBType()
	{
		if(url == null) return "Not connected";
		
		for(int i=0; i<DataSourceToolUtil.toolList.size(); i++)
		{
			if(url.startsWith(DataSourceToolUtil.toolList.get(i).jdbcUrlPrefix()))
			{
				return DataSourceToolUtil.toolList.get(i).getDBName();
			}
		}
//		
//		if(url.startsWith("jdbc:oracle"))
//		{
//			return "Oracle";
//		}
//		else if(url.startsWith("jdbc:mariadb"))
//		{
//			return "MariaDB";
//		}
//		else if(url.startsWith("jdbc:h2"))
//		{
//			return "H2";
//		}
//		else if(url.startsWith("jdbc:postgresql"))
//		{
//			return "PostgresSQL";
//		}
//		else if(url.startsWith("jdbc:cubrid"))
//		{
//			return "Cubrid";
//		}
		return "Unknown";
	}
	
	/**
	 * <p>DB에 대한 툴 객체를 반환합니다. 이를 통해 해당 DB에 대한 유용한 정보를 사용할 수 있습니다.</p>
	 * 
	 * @param fakeJdbcUrl : 가짜 JDBC URL, 다른 타입의 DBMS 용 DB툴 객체를 만들 때 사용하므로 해당 DBMS의 JDBC URL 형식이어야 합니다.
	 * @return DB 툴 객체
	 */
	public DBTool getDBTool(String fakeJdbcUrl)
	{
		if(DataUtil.isEmpty(fakeJdbcUrl)) return getDBTool();
		for(int i=0; i<DataSourceToolUtil.toolList.size(); i++)
		{
			if(fakeJdbcUrl.startsWith(DataSourceToolUtil.toolList.get(i).jdbcUrlPrefix()))
			{
				return DataSourceToolUtil.toolList.get(i).newInstance(this);
			}
		}
		return null;
	}
	
	/**
	 * <p>DB에 대한 툴 객체를 반환합니다. 이를 통해 해당 DB에 대한 유용한 정보를 사용할 수 있습니다.</p>
	 * 
	 * @return DB 툴 객체
	 */
	public DBTool getDBTool()
	{
		if(url == null) return null;
		
		for(int i=0; i<DataSourceToolUtil.toolList.size(); i++)
		{
			if(url.startsWith(DataSourceToolUtil.toolList.get(i).jdbcUrlPrefix()))
			{
				return DataSourceToolUtil.toolList.get(i).newInstance(this);
			}
		}
		
//		if(url.startsWith("jdbc:oracle"))
//		{
//			if(tool != null) tool.close();
//			tool = new OTool(this);
//			return tool;
//		}
//		else if(url.startsWith("jdbc:mariadb"))
//		{
//			if(tool != null) tool.close();
//			tool = new MTool(this);
//			return tool;
//		}
//		else if(url.startsWith("jdbc:h2"))
//		{
//			if(tool != null) tool.close();
//			tool = new HTool(this);
//			return tool;
//		}
//		else if(url.startsWith("jdbc:postgresql"))
//		{
//			if(tool != null) tool.close();
//			tool = new PTool(this);
//			return tool;
//		}
//		else if(url.startsWith("jdbc:cubrid"))
//		{
//			if(tool != null) tool.close();
//			tool = new CTool(this);
//			return tool;
//		}
		return null;
	}
	
	/**
	 * <p>SQL 문에 DDL 형식 중 삭제 문장(DROP, ALTER, TRUNCATE)이 있는지 검사합니다.</p>
	 * 
	 * @param query : SQL 문장
	 * @return DDL 포함 여부
	 */
	public static boolean hasDDLStatements(String query)
	{
		return query.indexOf("DROP") >= 0 || query.indexOf("drop") >= 0
				|| query.indexOf("ALTER") >= 0 || query.indexOf("alter") >= 0
				|| query.indexOf("TRUNCATE") >= 0 || query.indexOf("truncate") >= 0;
	}
	
	@Override
	protected TableSet query(String sql, boolean noOut) throws Exception
	{
		return query(sql, noOut, defaultTableSetType);
	}
	
	@Override
	public TableSet query(String sql, Class<? extends TableSet> tableSetType) throws Exception
	{
		return query(sql, false, tableSetType);
	}
	
	/**
	 * <p>테이블 셋 설정에 따라 테이블 셋을 만들어 데이터를 삽입해 반환합니다.</p>
	 * 
	 * @param statement : 문장 객체
	 * @param resultSet : 쿼리 결과로 받은 ResultSet 객체
	 * @param tableSetType : 테이블 셋 타입 클래스
	 * @return 생성된 테이블 셋
	 * @throws Exception : 초기화 실패
	 */
	protected TableSet makeTableSet(PreparedStatement statement, ResultSet resultSet, Class<? extends TableSet> tableSetType) throws Exception
	{
		TableSet testInstance = null;
		if(tableSetType != null) testInstance = tableSetType.newInstance();
		else testInstance = defaultTableSetType.newInstance();
		try
		{
			testInstance.noMoreUse();
		}
		catch(Exception e)
		{
			
		}
		
		if(testInstance instanceof ColumnTableSet) return new DefaultTableSet(resultSet.getMetaData().getTableName(1), resultSet);
		else if(testInstance instanceof ResultSetTableSet) return new ResultSetTableSet(resultSet, statement, this);
		else 
		{
			testInstance.addData(resultSet);
			return testInstance;
		}
	}
	
	@Override
	protected TableSet query(String sql, boolean noOut, Class<? extends TableSet> tableSetType) throws Exception
	{		
		if(! isAlive())
		{
			close();
			throw new NotConnectedException(Manager.applyStringTable("There is no connection, or connection was closed before."));
		}
		
		String scripts = String.valueOf(sql).trim();
		scripts = DataUtil.remove65279(scripts);
		
		if((! noOut) || Main.MODE >= DebuggingUtil.DEBUG) manager.log(Manager.applyStringTable("Preparing the following SQL") + "...\n" + scripts, Manager.LOG_NOTICE);
		
		try
		{
			if(prepared != null)
			{
				prepared.close();
			}
		}
		catch(Throwable e)
		{
			
		}
		
		if((! noOut) || Main.MODE >= DebuggingUtil.DEBUG) manager.log(Manager.applyStringTable("Executing SQL") + "...\n", Manager.LOG_NOTICE);
		
		if(Main.MODE == DebuggingUtil.DEBUG_ONLY)
		{
			if(hasDDLStatements(scripts))
			{	
				manager.log(Manager.applyStringTable("Execution blocked because of DEBUG_ONLY option."));
				return null;
			}
		}
		
		try
		{
			prepared = connection.prepareStatement(scripts, ResultSet.CONCUR_UPDATABLE, Statement.RETURN_GENERATED_KEYS);
		}
		catch(Throwable t)
		{
			if(isFirstToMakePrepared)
			{
				manager.log(Manager.applyStringTable("Current JDBC PreparedStatement cannot make updatable and return-generated-keys ResultSet. See following error message..."));
				manager.logError(new IncludesException(t), Manager.applyStringTable("Cannot use ResultSetTableSet type."), true);
				manager.log(Manager.applyStringTable("Try to using default PreparedStatement..."));
				isFirstToMakePrepared = false;
			}
			
			prepared = connection.prepareStatement(scripts);
			if(defaultTableSetType != null)
			{
				TableSet sample = defaultTableSetType.newInstance();
				if(sample instanceof ResultSetTableSet)
				{
					manager.log(Manager.applyStringTable("Cannot use ResultSetTableSet type.") + "\n" + Manager.applyStringTable("Default table set type is changed to DefaultTableSet."));
					defaultTableSetType = DefaultTableSet.class;
				}
				sample.noMoreUse();
			}
		}
		prepared.execute();
		
		ResultSet results = prepared.getResultSet();
		
		if(results == null && Main.MODE == DebuggingUtil.DEBUG_ONLY)
		{
			connection.rollback();
			manager.log(Manager.applyStringTable("Rollbacked because of DEBUG_ONLY option."));
		}
		
		if(results == null) return null;
		
		return makeTableSet(prepared, results, tableSetType);
	}
	
	/**
	 * <p>DB에 SQL문을 넘겨 실행합니다. 결과가 없으면 null, 있으면 TableSet 형태로 반환합니다.</p>
	 * 
	 * @param sql : SQL 스크립트
	 * @param parameters : 데이터 리스트
	 * @param types : 데이터 타입 리스트
	 * @return 결과가 없으면 null, 있으면 TableSet 객체
	 * @throws Exception : DBMS에서의 오류, 혹은 네트워크 문제
	 */
	public TableSet query(String sql, List<String> parameters, List<Integer> types) throws Exception
	{		
		if(! isAlive())
		{
			close();
			throw new NotConnectedException(Manager.applyStringTable("There is no connection, or connection was closed before."));
		}
		
		
		String scripts = String.valueOf(sql).trim();
		scripts = DataUtil.remove65279(scripts);
		
		manager.log(Manager.applyStringTable("Preparing the following SQL") + "...\n" + scripts, Manager.LOG_NOTICE);
		
		if(Main.MODE == DebuggingUtil.DEBUG_ONLY)
		{
			if(hasDDLStatements(scripts))
			{	
				manager.log(Manager.applyStringTable("Execution blocked because of DEBUG_ONLY option."));
				return null;
			}
		}
		
		prepared = connection.prepareStatement(scripts);
		
		manager.log(Manager.applyStringTable("Executing SQL") + "...\n", Manager.LOG_NOTICE);
		
		for(int i=0; i<parameters.size(); i++)
		{
			if(Column.TYPE_STRING == types.get(i).intValue())
			{
				prepared.setString(i + 1, parameters.get(i));
			}
			else if(Column.TYPE_NUMERIC == types.get(i).intValue())
			{
				prepared.setDouble(i + 1, Double.parseDouble(parameters.get(i)));
			}
			else if(Column.TYPE_BOOLEAN == types.get(i).intValue())
			{
				prepared.setBoolean(i + 1, DataUtil.parseBoolean(parameters.get(i)));
			}
			else if(Column.TYPE_BLANK == types.get(i).intValue())
			{
				prepared.setNull(i + 1, Types.VARCHAR);
			}
			else
			{
				prepared.setString(i + 1, parameters.get(i));
			}
		}
				
		prepared.execute();
		ResultSet results = prepared.getResultSet();
		
		if(results == null && Main.MODE == DebuggingUtil.DEBUG_ONLY)
		{
			connection.rollback();
			manager.log(Manager.applyStringTable("Rollbacked because of DEBUG_ONLY option."));
		}
		
		if(results == null) return null;
		
		return makeTableSet(prepared, results, defaultTableSetType);
	}
	
	/**
	 * <p>DB에 SQL문을 넘겨 실행합니다. 결과가 없으면 null, 결과가 있으면 ResultSet 형태로 반환합니다.</p>
	 * 
	 * @param sql : SQL 스크립트
	 * @return 결과가 없으면 null, 있으면 ResultSet 객체
	 * @throws Exception : DBMS에서의 오류, 혹은 네트워크 문제
	 */
	public ResultSet rawQuery(String sql) throws Exception
	{
		if(! isAlive())
		{
			close();
			throw new NotConnectedException(Manager.applyStringTable("There is no connection, or connection was closed before."));
		}
		
		
		String scripts = String.valueOf(sql).trim();
		scripts = DataUtil.remove65279(scripts);
		
		manager.log(Manager.applyStringTable("Preparing the following SQL") + "...\n" + scripts, Manager.LOG_NOTICE);
		
		if(Main.MODE == DebuggingUtil.DEBUG_ONLY)
		{
			if(hasDDLStatements(scripts))
			{	
				manager.log(Manager.applyStringTable("Execution blocked because of DEBUG_ONLY option."));
				return null;
			}
		}
		
		prepared = connection.prepareStatement(scripts);
		manager.log(Manager.applyStringTable("Executing SQL") + "...\n", Manager.LOG_NOTICE);
		
		prepared.execute();
		ResultSet results = prepared.getResultSet();
		
		if(results == null && Main.MODE == DebuggingUtil.DEBUG_ONLY)
		{
			connection.rollback();
			manager.log(Manager.applyStringTable("Rollbacked because of DEBUG_ONLY option."));
		}
		
		return results;
	}
	
	/**
	 * <p>데이터를 DB로부터 가져옵니다.</p>
	 * 
	 * @param tableName : 조회할 테이블 이름
	 * @param additionalWheres : 조건문
	 * @param startsIndex : 시작 레코드 번호
	 * @param endIndex : 끝 레코드 번호
	 * @return 조회 결과 객체
	 * @throws Exception : DBMS에서의 오류, 혹은 네트워크 문제
	 */
	@Override
	public ResultStruct select(String tableName, String additionalWheres, int startsIndex, int endIndex) throws Exception
	{
		String selectQuery = "SELECT * FROM (";
		DBTool dbTool = getDBTool();
		
		String rownumKeyword = null;
		if(dbTool != null) rownumKeyword = dbTool.getRownumName();
		String rownumAlias = "ROWNUM_FOR_HG";
		
		if(DataUtil.isNotEmpty(rownumKeyword)) selectQuery = selectQuery + "SELECT A.*, " + rownumKeyword + " AS " + rownumAlias + " FROM " + tableName + " A";
		else  selectQuery = selectQuery + "SELECT A.* FROM " + tableName + " A";
		if(DataUtil.isNotEmpty(additionalWheres))
		{
			selectQuery = selectQuery + " WHERE " + additionalWheres;
		}
		
		selectQuery = selectQuery + ")";
		if(DataUtil.isNotEmpty(rownumKeyword))
		{
			selectQuery = selectQuery + " WHERE " + rownumAlias + " >= " + String.valueOf(startsIndex) + " AND " + rownumAlias + " < " + String.valueOf(endIndex);
		}
		
		TableSet tableSets = query(selectQuery);
		ResultStruct results = null;
		
		if(tableSets == null) results = new ResultStruct(tableSets, 0);
		else results = new ResultStruct(tableSets, tableSets.getRecordCount());
		
		return results;
	}
	
	/**
	 * <p>DB에 commit 명령을 날립니다.</p>
	 * 
	 * @throws SQLException DBMS 문제
	 */
	@Override
	public void commit() throws Exception
	{
		connection.commit();
	}
	/**
	 * <p>DB에 rollback 명령을 날립니다.</p>
	 * 
	 * @throws SQLException DBMS 문제
	 */
	@Override
	public void rollback() throws Exception
	{
		connection.rollback();
	}	
	
	/**
	 * <p>접속을 닫습니다. 닫기 전 롤백을 실행하므로, 중요한 작업 후 커밋을 했는지 먼저 확인하시기 바랍니다.</p>
	 */
	@Override
	public void close()
	{
		boolean beforeAlive = false;
		
		try
		{
			beforeAlive = isAlive();
		}
		catch(Exception e)
		{
			
		}
		
		try
		{
			if(continuer != null) continuer.noMoreUse();
		}
		catch(Exception e)
		{
			
		}
		
		continuer = null;
		
		try
		{
			if(connectThread != null) connectThread.noMoreUse();
		}
		catch(Exception e)
		{
			
		}
		
		connectThread = null;
		
		if(tool != null)
		{
			try
			{
				tool.close();
			}
			catch(Throwable e)
			{
				
			}
		}
		try
		{
			connection.rollback();
		}
		catch(Throwable e)
		{
			
		}
		try
		{
			prepared.close();
		}
		catch(Throwable e)
		{
			
		}
		prepared = null;
		try
		{
			if(connection != null) connection.close();
		}
		catch(Throwable e)
		{
			manager.logError(e, Manager.applyStringTable("On closing connection") + " : " + toString());
		}
		connection = null;
		
		if(beforeAlive)
		{
			manager.log(toString());
		}
	}
	
	/**
	 * <p>연결이 아직 유효한지 여부를 반환합니다.</p>
	 * 
	 * @return true 이면 연결이 아직 유효한 것
	 */
	@Override
	public boolean isAlive()
	{
		if(connection == null) return false;
		try
		{
			if(connection.isClosed()) 
			{
				return false;
			}
		}
		catch (Exception e)
		{
			manager.logError(e, Manager.applyStringTable("On checking connection is alive"));
			return false;
		}
		return true;
	}
	public Connection getConnection()
	{
		return connection;
	}
	public void setConnection(Connection connection)
	{
		this.connection = connection;
	}
	public String getId()
	{
		return id;
	}
	
	/**
	 * <p>ID 값을 미리 입력합니다. 이미 접속한 후에는 이 메소드를 사용해도 소용이 없습니다.</p>
	 * 
	 * @param id : DB ID
	 */
	public void setId(String id)
	{
		if(! isAlive()) this.id = id;
	}
	public String getPw()
	{
		return pw;
	}
	/**
	 * <p>암호 값을 미리 입력합니다. 이미 접속한 후에는 이 메소드를 사용해도 소용이 없습니다.</p>
	 * 
	 * @param pw : DB 암호
	 */
	public void setPw(String pw)
	{
		if(! isAlive()) this.pw = pw;
	}
	public String getUrl()
	{
		return url;
	}
	
	/**
	 * <p>URL 값을 미리 입력합니다. 이미 접속한 후에는 이 메소드를 사용해도 소용이 없습니다.</p>
	 * 
	 * @param url : JDBC URL
	 */
	public void setUrl(String url)
	{
		if(! isAlive()) this.url = url;
	}
	public String getClassPath()
	{
		return classPath;
	}
	
	/**
	 * <p>JDBC 클래스명을 미리 입력합니다. 이미 접속한 후에는 이 메소드를 사용해도 소용이 없습니다.</p>
	 * 
	 * @param classPath : JDBC 드라이버 클래스명
	 */
	public void setClassPath(String classPath)
	{
		if(! isAlive()) this.classPath = classPath;
		if(tool != null) tool.close();
		if(this.classPath.equalsIgnoreCase("Oracle"))
		{
			tool = new OTool(this);
			this.classPath = tool.getJdbcClassPath();
		}
		else if(this.classPath.equalsIgnoreCase("MariaDB") || this.classPath.equalsIgnoreCase("MySQL"))
		{
			tool = new MTool(this);
			this.classPath = tool.getJdbcClassPath();
		}
		else if(this.classPath.equalsIgnoreCase("H2"))
		{
			tool = new HTool(this);
			this.classPath = tool.getJdbcClassPath();
		}
		else if(this.classPath.equalsIgnoreCase("Cubrid"))
		{
			tool = new CTool(this);
			this.classPath = tool.getJdbcClassPath();
		}
	}
	public String getClassFile()
	{
		return classFile;
	}
	
	/**
	 * <p>JDBC 클래스 파일 혹은 디렉토리 정보를 미리 입력합니다. 이미 접속한 후에는 이 메소드를 사용해도 소용이 없습니다.</p>
	 * 
	 * @param classFile : JDBC 클래스 파일 혹은 디렉토리 정보
	 */
	public void setClassFile(String classFile)
	{
		if(! isAlive()) this.classFile = classFile;
	}
	@Override
	public void cancel() throws SQLException
	{
		if(prepared == null) throw new NullPointerException(Manager.applyStringTable("There is no requests to cancel."));
		prepared.cancel();
	}
	public PreparedStatement getPrepared()
	{
		return prepared;
	}
	public void setPrepared(PreparedStatement prepared)
	{
		this.prepared = prepared;
	}
	
	@Override
	public AccessInfo getAccessInfo()
	{
		AccessInfo newAccessInfo = new AccessInfo();
		
		if(classPath != null) newAccessInfo.setClassPath(new String(classPath));
		if(id != null) newAccessInfo.setId(new String(id));
		if(pw != null) newAccessInfo.setPw(new String(pw));
		if(url != null) newAccessInfo.setUrl(new String(url));
		
		return newAccessInfo;
	}
	
	@Override
	public void setAccessInfo(AccessInfo accessInfo)
	{
		classPath = accessInfo.getClassPath();
		id = accessInfo.getId();
		pw = accessInfo.getPw();
		url = accessInfo.getUrl();
	}
	
	@Override
	public String getAliasName()
	{
		return aliasName;
	}

	@Override
	public void setAliasName(String aliasName)
	{
		this.aliasName = aliasName;
	}
	@Override
	public JScriptRunner getRunner()
	{
		return runner;
	}
	@Override
	public void setRunner(JScriptRunner runner)
	{
		if((runner != null) || (this.runner == null)) this.runner = runner;
	}

	@Override
	public void finalize()
	{
		close();
	}
	
	/**
	 * <p>접속 정보들을 요약한 내용을 반환합니다. 이 결과를 출력하면 접속 정보를 알 수 있습니다.</p>
	 * 
	 */
	@Override
	public String toString()
	{
		StringBuffer results = new StringBuffer("");
		
		if(getAliasName() == null || getAliasName().trim().equals(""))
		{
			if(id != null)
			{
				results = results.append(Manager.applyStringTable("DB") + " : " + getConnecedDBType() + " \n");
				results = results.append(Manager.applyStringTable("URL") + " : " + url + " \n");
				results = results.append(Manager.applyStringTable("ID") + " : " + id + " \n");
			}
		}
		else
		{
			results = results.append(getAliasName() + " \n");
		}
		
		if(isAlive())
		{
			results = results.append(Manager.applyStringTable("Connected"));
		}
		else
		{
			results = results.append(Manager.applyStringTable("Not connected"));
		}
		
		return results.toString();
	}
	
	/**
	 * <p>다른 DAO과 접속 정보가 같은지를 비교합니다.</p>
	 * 
	 * @param others
	 * @return
	 */
	@Override
	public boolean equals(Object others)
	{
		if(others instanceof JdbcDao)
		{
			if(this.getAccessInfo().equals(equals(((JdbcDao) others).getAccessInfo())))
			{
				if(this.getAliasName() == null)
				{
					if(((JdbcDao) others).getAliasName() == null)
					{
						 return true;
					}
					else
					{
						 return false;
					}
				}
				else
				{
					if(((JdbcDao) others).getAliasName() == null)
					{
						 return false;
					}
					else
					{
						 return getAliasName().equalsIgnoreCase(((JdbcDao) others).getAliasName());
					}
				}
			}
			else return false;
		}
		else return false;
	}

	@Override
	public String getDataSourceType()
	{
		return getConnecedDBType();
	}

	@Override
	public DataSourceTool getDataSourceTool()
	{
		return getDBTool();
	}
	
	@Override
	public String help()
	{
		StringBuffer stringBuffer = new StringBuffer("");
		
		stringBuffer = stringBuffer.append(Manager.applyStringTable("This object can access to DB.") + "\n");
		stringBuffer = stringBuffer.append(Manager.applyStringTable("Set id, password, uri, and call connect() to connect to DB,") + "\n");
		stringBuffer = stringBuffer.append(Manager.applyStringTable("Then, use query(sql) to query.") + "\n");
		stringBuffer = stringBuffer.append(Manager.applyStringTable("") + "\n");
		stringBuffer = stringBuffer.append("setId(id) : " + Manager.applyStringTable("Set ID to login to DB") + "\n");
		stringBuffer = stringBuffer.append("setPw(pw) : " + Manager.applyStringTable("Set Password to login to DB") + "\n");
		stringBuffer = stringBuffer.append("setUrl(url) : " + Manager.applyStringTable("Set JDBC URL to access to DB") + "\n");
		stringBuffer = stringBuffer.append("setClassPath(cp) : " + Manager.applyStringTable("Set class fullname of JDBC Driver") + "\n");
		stringBuffer = stringBuffer.append("setClassFile(cf) : " + Manager.applyStringTable("Set JDBC Driver file path (Not always needed)") + "\n");
		stringBuffer = stringBuffer.append("connect() : " + Manager.applyStringTable("Try to connect into DB") + "\n");
		stringBuffer = stringBuffer.append("close() : " + Manager.applyStringTable("Try to disconnect into DB") + "\n");
		stringBuffer = stringBuffer.append("isAlive() : " + Manager.applyStringTable("Check connection.") + "\n");
		stringBuffer = stringBuffer.append("query(sql) : " + Manager.applyStringTable("Query, and return result if exists.") + "\n");
		
		return stringBuffer.toString();
	}
}

/**
 * <p>별도의 쓰레드에서 접속을 시도하기 위한 객체 생성에 관여합니다.</p>
 * 
 * @author HJOW
 *
 */
class ConnectThread implements Runnable, NeedtoEnd
{
	private JdbcDao dao;
	private boolean threadSwitch = false;
	private Runnable afterWork = null;
	private int tryCount = 0;
	
	/**
	 * <p>별도의 쓰레드를 통해 접속을 시도합니다.</p>
	 * 
	 * @param dao : 접속에 쓰일 DAO 객체
	 * @param afterWork : 접속 후 수행할 작업
	 */
	public ConnectThread(JdbcDao dao, Runnable afterWork)
	{
		this.dao = dao;
		this.afterWork = afterWork;
		threadSwitch = true;
		tryCount = 0;
		new HThread(this).start();
	}
	@Override
	public void run()
	{
		while(threadSwitch)
		{
			try
			{
				if(dao == null) break;
				if(Manager.getOption("safe_mode") != null && DataUtil.parseBoolean("safe_mode"))
				{
					dao.connect();
				}
				else dao.connectDirectly();
				
				if(afterWork != null) afterWork.run();
				noMoreUse();
			}
			catch(SQLException e)
			{
				if(dao != null)
				{
					dao.getManager().logError(e, Manager.applyStringTable("On connecting thread"), true);
					dao.close();
				}
				else
				{
					Main.logError(e, Manager.applyStringTable("On connecting thread"));
				}
				noMoreUse();
				break;
			}
			catch(Throwable e)
			{
				if(dao != null) 
			    {
					dao.getManager().logError(e, Manager.applyStringTable("On connecting thread"), true);
					dao.close();
			    }
				else Main.logError(e, Manager.applyStringTable("On connecting thread"));
			}
			tryCount++;
			if(tryCount >= 5) noMoreUse();
			if(threadSwitch)
			{
				try
				{
					Thread.sleep(2000);
				}
				catch(Throwable e)
				{
					
				}
			}
		}
		noMoreUse();
	}

	@Override
	public void noMoreUse()
	{
		threadSwitch = false;
		dao = null;
		
	}
	@Override
	public boolean isAlive()
	{
		return threadSwitch;
	}
}

/**
 * <p>접속을 유지하게 하는 쓰레드 생성에 관여합니다.</p>
 * 
 * @author HJOW
 *
 */
class ContinuousThread implements Runnable, NeedtoEnd
{
	protected JdbcDao dao;
	protected boolean threadSwitch = false;
	protected int gap = 30;
	protected int addRandoms = 5;
	protected transient int nowStatus = 0;
	protected transient int nowGap = 30;
	
	/**
	 * <p>접속 유지 쓰레드를 생성합니다.</p>
	 * 
	 * @param dao : 접속한 DAO 객체
	 */
	public ContinuousThread(JdbcDao dao)
	{
		this.dao = dao;
		nowGap = gap;
		threadSwitch = true;
		new HThread(this).start();
	}
	/**
	 * <p>접속 유지 쓰레드를 생성합니다.</p>
	 * 
	 * @param dao : 접속한 DAO 객체
	 * @param gap : 접속 유지 명령 수행 주기
	 */
	public ContinuousThread(JdbcDao dao, int gaps)
	{
		this.dao = dao;
		this.gap = gaps;
		nowGap = gaps;
		threadSwitch = true;
		new HThread(this).start();
	}
	@Override
	public void run()
	{
		while(threadSwitch)
		{
			if(! dao.isAlive()) noMoreUse();
			if(! threadSwitch)  noMoreUse();
			
			if(nowStatus >= nowGap)
			{
				if(dao.getDBTool() != null) 
				{
					try
					{
						dao.query(dao.getDBTool().getPreventTimeoutScript(), true);
					}
					catch (Exception e)
					{
						
					}
				}
				nowStatus = 0;
				nowGap = gap + (int) (Math.random() * addRandoms);
			}
			
			nowStatus++;
			
			try
			{
				Thread.sleep(1000);
			}
			catch(Throwable e)
			{
				
			}
		}
	}
	@Override
	public void noMoreUse()
	{
		threadSwitch = false;
		dao = null;
	}
	public JdbcDao getDao()
	{
		return dao;
	}
	public void setDao(JdbcDao dao)
	{
		this.dao = dao;
	}
	public boolean isThreadSwitch()
	{
		return threadSwitch;
	}
	public void setThreadSwitch(boolean threadSwitch)
	{
		this.threadSwitch = threadSwitch;
	}
	public int getGap()
	{
		return gap;
	}
	public void setGap(int gap)
	{
		this.gap = gap;
	}
	public int getAddRandoms()
	{
		return addRandoms;
	}
	public void setAddRandoms(int addRandoms)
	{
		this.addRandoms = addRandoms;
	}
	@Override
	public boolean isAlive()
	{
		return threadSwitch;
	}
}