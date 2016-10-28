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
package hjow.dbtool.h2;

import java.sql.SQLException;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.h2.tools.Server;

import hjow.dbtool.common.DBTool;
import hjow.hgtable.Manager;
import hjow.hgtable.dao.Dao;
import hjow.hgtable.dao.JdbcDao;

/**
 * <p>이 클래스 객체는 H2 Database 에 대한 주요 정보 및 자주 쓰는 쿼리를 메소드로 가집니다.</p>
 * 
 * @author HJOW
 *
 */
public class HTool extends DBTool 
{
	private static final long serialVersionUID = -6285000798831125316L;
	
	/**
	 * <p>생성자입니다.</p>
	 */
	public HTool()
	{
		
	}
	/**
	 * <p>생성자입니다. DB에 액세스할 수 있는 DAO 객체가 필요합니다.</p>
	 * 
	 * @param dao : DAO 객체
	 */
	public HTool(Dao dao)
	{
		super(dao);
	}

	@Override
	public String getJdbcClassPath()
	{
		return "org.h2.Driver";
	}

	@Override
	public String getTableListQuery(boolean allData)
	{
		if(allData) return "SELECT * FROM INFORMATION_SCHEMA";
		else return "SELECT TABLE_NAME FROM INFORMATION_SCHEMA";
	}

	@Override
	public String getTableListKeyColumnName()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDBName()
	{
		return "H2";
	}

	@Override
	public String getRownumName()
	{
		return "ROWNUM()";
	}

	@Override
	public String getUserListQuery(boolean isDBA, boolean allData)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUserListKeyColumnName()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTablespaceListQuery(boolean isDBA, boolean allData)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTablespaceListKeyColumnName()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getViewListQuery(boolean isDBA, boolean allData)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getViewListKeyColumnName()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDbLinkListQuery(boolean isDBA, boolean allData)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDbLinkListKeyColumnName()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getObjectListQuery(boolean isDBA, boolean allData)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getObjectListKeyColumnName()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDataFileListQuery(boolean allData)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDataFileListKeyColumnName()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getProcedureListQuery(boolean isDBA, boolean allData)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getProcedureListKeyColumnName()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFunctionListQuery(boolean isDBA, boolean allData)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFunctionListKeyColumnName()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getProcedureScriptQuery(String procedureName)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFunctionScriptQuery(String functionName)
	{
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public DBTool newInstance(Dao dao)
	{
		return new HTool(dao);
	}
	@Override
	public String jdbcUrlPrefix()
	{
		return "jdbc:h2";
	}
	
	/**
	 * <p>PostgresSQL 클라이언트로 접속할 수 있는 서버를 실행하고, 이를 컨트롤하는 데 사용할 수 있는 객체를 반환합니다.</p>
	 * <p>args 에 대한 자세한 내용은 <a href="http://www.h2database.com/javadoc/org/h2/tools/Server.html#main_String...">http://www.h2database.com/javadoc/org/h2/tools/Server.html#main_String...</a> 를 참고하세요.</p>
	 * 
	 * @param args : 매개 변수들
	 * @return 서버 객체
	 * @throws SQLException 서버 동작 실패 등
	 */
	public Server runPGServer(String ... args) throws SQLException
	{
		return createPGServer(args);
	}
	
	/**
	 * <p>JDBC 로 접속할 수 있는 TCP 서버를 실행하고, 이를 컨트롤하는 데 사용할 수 있는 객체를 반환합니다.</p>
	 * <p>args 에 대한 자세한 내용은 <a href="http://www.h2database.com/javadoc/org/h2/tools/Server.html#main_String...">http://www.h2database.com/javadoc/org/h2/tools/Server.html#main_String...</a> 를 참고하세요.</p>
	 * 
	 * @param args : 매개 변수들
	 * @return 서버 객체
	 * @throws SQLException 서버 동작 실패 등
	 */
	public Server runTCPServer(String ... args) throws SQLException
	{
		return createTCPServer(args);
	}
	
	/**
	 * <p>H2 콘솔을 사용한 웹 서버를 실행하고, 이를 컨트롤하는 데 사용할 수 있는 객체를 반환합니다.</p>
	 * <p>args 에 대한 자세한 내용은 <a href="http://www.h2database.com/javadoc/org/h2/tools/Server.html#main_String...">http://www.h2database.com/javadoc/org/h2/tools/Server.html#main_String...</a> 를 참고하세요.</p>
	 * 
	 * @param args : 매개 변수들
	 * @return 서버 객체
	 * @throws SQLException 서버 동작 실패 등
	 */
	public Server runWEBServer(String ... args) throws SQLException
	{
		return createWEBServer(args);
	}
	
	/**
	 * <p>PostgresSQL 클라이언트로 접속할 수 있는 서버를 실행하고, 이를 컨트롤하는 데 사용할 수 있는 객체를 반환합니다.</p>
	 * <p>args 에 대한 자세한 내용은 <a href="http://www.h2database.com/javadoc/org/h2/tools/Server.html#main_String...">http://www.h2database.com/javadoc/org/h2/tools/Server.html#main_String...</a> 를 참고하세요.</p>
	 * 
	 * @param args : 매개 변수들
	 * @return 서버 객체
	 * @throws SQLException 서버 동작 실패 등
	 */
	public static Server createPGServer(String ... args) throws SQLException
	{
		return Server.createPgServer(args).start();
	}
	
	/**
	 * <p>JDBC 로 접속할 수 있는 TCP 서버를 실행하고, 이를 컨트롤하는 데 사용할 수 있는 객체를 반환합니다.</p>
	 * <p>args 에 대한 자세한 내용은 <a href="http://www.h2database.com/javadoc/org/h2/tools/Server.html#main_String...">http://www.h2database.com/javadoc/org/h2/tools/Server.html#main_String...</a> 를 참고하세요.</p>
	 * 
	 * @param args : 매개 변수들
	 * @return 서버 객체
	 * @throws SQLException 서버 동작 실패 등
	 */
	public static Server createTCPServer(String ... args) throws SQLException
	{
		return Server.createTcpServer(args).start();
	}
	
	/**
	 * <p>H2 콘솔을 사용한 웹 서버를 실행하고, 이를 컨트롤하는 데 사용할 수 있는 객체를 반환합니다.</p>
	 * <p>args 에 대한 자세한 내용은 <a href="http://www.h2database.com/javadoc/org/h2/tools/Server.html#main_String...">http://www.h2database.com/javadoc/org/h2/tools/Server.html#main_String...</a> 를 참고하세요.</p>
	 * 
	 * @param args : 매개 변수들
	 * @return 서버 객체
	 * @throws SQLException 서버 동작 실패 등
	 */
	public static Server createWEBServer(String ... args) throws SQLException
	{
		return Server.createWebServer(args).start();
	}
	
	/**
	 * <p>PostgresSQL 클라이언트로 접속할 수 있는 서버를 실행하고, 이를 컨트롤하는 데 사용할 수 있는 객체를 반환합니다.</p>
	 * <p>args 에 대한 자세한 내용은 <a href="http://www.h2database.com/javadoc/org/h2/tools/Server.html#main_String...">http://www.h2database.com/javadoc/org/h2/tools/Server.html#main_String...</a> 를 참고하세요.</p>
	 * 
	 * @param args : 매개 변수들
	 * @return 서버 객체
	 * @throws SQLException 서버 동작 실패 등
	 */
	public static Server createPGServer(List<String> args) throws SQLException
	{
		String[] argArray = new String[args.size()];
		for(int i=0; i<argArray.length; i++)
		{
			argArray[i] = args.get(i);
		}
		return Server.createPgServer(argArray).start();
	}
	
	/**
	 * <p>JDBC 로 접속할 수 있는 TCP 서버를 실행하고, 이를 컨트롤하는 데 사용할 수 있는 객체를 반환합니다.</p>
	 * <p>args 에 대한 자세한 내용은 <a href="http://www.h2database.com/javadoc/org/h2/tools/Server.html#main_String...">http://www.h2database.com/javadoc/org/h2/tools/Server.html#main_String...</a> 를 참고하세요.</p>
	 * 
	 * @param args : 매개 변수들
	 * @return 서버 객체
	 * @throws SQLException 서버 동작 실패 등
	 */
	public static Server createTCPServer(List<String> args) throws SQLException
	{
		String[] argArray = new String[args.size()];
		for(int i=0; i<argArray.length; i++)
		{
			argArray[i] = args.get(i);
		}
		return Server.createTcpServer(argArray).start();
	}
	
	/**
	 * <p>H2 콘솔을 사용한 웹 서버를 실행하고, 이를 컨트롤하는 데 사용할 수 있는 객체를 반환합니다.</p>
	 * <p>args 에 대한 자세한 내용은 <a href="http://www.h2database.com/javadoc/org/h2/tools/Server.html#main_String...">http://www.h2database.com/javadoc/org/h2/tools/Server.html#main_String...</a> 를 참고하세요.</p>
	 * 
	 * @param args : 매개 변수들
	 * @return 서버 객체
	 * @throws SQLException 서버 동작 실패 등
	 */
	public static Server createWEBServer(List<String> args) throws SQLException
	{
		String[] argArray = new String[args.size()];
		for(int i=0; i<argArray.length; i++)
		{
			argArray[i] = args.get(i);
		}
		return Server.createWebServer(argArray).start();
	}
	
	/**
	 * <p>서버를 실행하고 바로 접속하고, 그 DAO 객체를 담은 툴 객체와 서버 객체를 반환합니다.</p>
	 * <p>Map 객체 안에, Server 라는 키로 서버 객체를, Tool 라는 키로 툴 객체를 담아 반환합니다.</p>
	 * <p>createServerArgs 에 대한 자세한 내용은 <a href="http://www.h2database.com/javadoc/org/h2/tools/Server.html#main_String...">http://www.h2database.com/javadoc/org/h2/tools/Server.html#main_String...</a> 를 참고하세요.</p>
	 * 
	 * @param manager : 매니저 객체
	 * @param id      : 접속할 사용자 ID
	 * @param pw      : 접속할 사용자 암호
	 * @param createServerArgs : 서버 실행에 필요한 매개 변수들
	 * @return 맵 객체
	 * @throws Exception 서버 동작 실패 등
	 */
	public static Map<String, Object> newH2Tool(Manager manager, String id, String pw, String ... createServerArgs) throws Exception
	{
		Server  server = null;
		JdbcDao dao    = null;
		try
		{
			Map<String, Object> result = new Hashtable<String, Object>();
			server = createTCPServer(createServerArgs);
			dao = new JdbcDao(manager);
			dao.setId(id);
			dao.setPw(pw);
			dao.setUrl("org.h2.Driver");
			dao.connectDirectly();
			HTool tool = new HTool(dao);
			result.put("Server", server);
			result.put("Tool"  , tool  );
			return result;
		}
		catch(Exception e)
		{
			throw e;
		}
		finally
		{
			try
			{
				dao.close();
			}
			catch(Exception e1)
			{
				
			}
			try
			{
				server.shutdown();
			}
			catch(Exception e1)
			{
				
			}
		}
	}
	@Override
	public String getTableInfoQuery(String tableName) {
		// TODO Auto-generated method stub
		return null;
	}
}
