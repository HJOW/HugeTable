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

import hjow.hgtable.Manager;
import hjow.hgtable.dao.Dao;
import hjow.hgtable.dao.FakeDao;
import hjow.hgtable.dao.JdbcDao;

/**
 * <p>데이터 소스와의 접속에 관련된 여러 정적 메소드가 있습니다.</p>
 * 
 * @author HJOW
 *
 */
public class ConnectUtil
{
	public static final int RDBMS    = 0;
	public static final int NOSQL    = 1;
	public static final int FAKE     = -1;
	public static final int UNKNOWN  = -2;
	
	/**
	 * <p>직접 접속을 시도합니다.</p>
	 * 
	 * @param manager : 매니저 객체
	 * @param id : ID
	 * @param pw : 암호
	 * @param url : 접속할 URL
	 * @param classPath : 드라이버 클래스 이름 (풀네임)
	 * @return DAO 객체
	 */
	public static Dao connect(Manager manager, String id, String pw, String url, String classPath)
	{
		String getId = ""
			 , getPw = ""
			 , getUrl = ""
			 , getClassPath = "";
		
		if(DataUtil.isNotEmpty(id)) getId = id.trim();
		if(DataUtil.isNotEmpty(pw)) getPw = pw.trim();
		if(DataUtil.isNotEmpty(url)) getUrl = url.trim();
		if(DataUtil.isNotEmpty(classPath)) getClassPath = classPath.trim();
		
		Dao dao = null;
		
		int type = RDBMS;
		if(getClassPath.equals("FAKE"))
		{
			type = FAKE;
		}
		
		try
		{
		
			switch(type)
			{
			case RDBMS:
				dao = new JdbcDao(manager);
				if(DataUtil.isNotEmpty(getId)) dao.setId(getId);
				if(DataUtil.isNotEmpty(getPw)) dao.setPw(getPw);
				if(DataUtil.isNotEmpty(getUrl)) dao.setUrl(getUrl);
				if(DataUtil.isNotEmpty(getClassPath)) dao.setClassPath(getClassPath);
				
				dao.connect();
				return dao;
			case NOSQL:
				break;
			case FAKE:
				dao = new FakeDao(manager);
				if(DataUtil.isNotEmpty(getId)) dao.setId(getId);
				if(DataUtil.isNotEmpty(getPw)) dao.setPw(getPw);
				if(DataUtil.isNotEmpty(getUrl)) dao.setUrl(getUrl);
				if(DataUtil.isNotEmpty(getClassPath)) dao.setClassPath(getClassPath);
				
				dao.connect();
				return dao;
			default:
				dao = new JdbcDao(manager);
				if(DataUtil.isNotEmpty(getId)) dao.setId(getId);
				if(DataUtil.isNotEmpty(getPw)) dao.setPw(getPw);
				if(DataUtil.isNotEmpty(getUrl)) dao.setUrl(getUrl);
				if(DataUtil.isNotEmpty(getClassPath)) dao.setClassPath(getClassPath);
				
				dao.connect();
				return dao;
			}
		}
		catch(Throwable e)
		{
			manager.logError(e, Manager.applyStringTable("On connecting parallely"));
			try
			{
				dao.close();
			}
			catch(Throwable e1)
			{
				
			}
		}
		
		return dao;
	}
	
	/**
	 * <p>별도의 쓰레드에서 접속을 시도합니다. 동시에 다른 일을 수행할 수 있습니다.</p>
	 * 
	 * @param manager : 매니저 객체
	 * @param id : ID
	 * @param pw : 암호
	 * @param url : 접속할 URL
	 * @param classPath : 드라이버 클래스 이름 (풀네임)
	 * @param callback : 접속 성공 시 수행할 작업을 run() 메소드로 가지고 있는 객체
	 * @return DAO 객체
	 */
	public static Dao connectParallely(Manager manager, String id, String pw, String url, String classPath, Runnable callback)
	{
		String getId = ""
			 , getPw = ""
			 , getUrl = ""
			 , getClassPath = "";
		
		if(DataUtil.isNotEmpty(id)) getId = id.trim();
		if(DataUtil.isNotEmpty(pw)) getPw = pw.trim();
		if(DataUtil.isNotEmpty(url)) getUrl = url.trim();
		if(DataUtil.isNotEmpty(classPath)) getClassPath = classPath.trim();
		
		Dao dao = null;
		
		int type = RDBMS;
		if(getClassPath.equals("FAKE"))
		{
			type = FAKE;
		}
		
		try
		{
		
			switch(type)
			{
			case RDBMS:
				dao = new JdbcDao(manager);
				if(DataUtil.isNotEmpty(getId)) dao.setId(getId);
				if(DataUtil.isNotEmpty(getPw)) dao.setPw(getPw);
				if(DataUtil.isNotEmpty(getUrl)) dao.setUrl(getUrl);
				if(DataUtil.isNotEmpty(getClassPath)) dao.setClassPath(getClassPath);
				
				dao.connectParallely(callback);
				return dao;
			case NOSQL:
				break;
			case FAKE:
				dao = new FakeDao(manager);
				if(DataUtil.isNotEmpty(getId)) dao.setId(getId);
				if(DataUtil.isNotEmpty(getPw)) dao.setPw(getPw);
				if(DataUtil.isNotEmpty(getUrl)) dao.setUrl(getUrl);
				if(DataUtil.isNotEmpty(getClassPath)) dao.setClassPath(getClassPath);
				
				dao.connectParallely(callback);
				return dao;
			default:
				dao = new JdbcDao(manager);
				if(DataUtil.isNotEmpty(getId)) dao.setId(getId);
				if(DataUtil.isNotEmpty(getPw)) dao.setPw(getPw);
				if(DataUtil.isNotEmpty(getUrl)) dao.setUrl(getUrl);
				if(DataUtil.isNotEmpty(getClassPath)) dao.setClassPath(getClassPath);
				
				dao.connectParallely(callback);
				return dao;
			}
		}
		catch(Throwable e)
		{
			manager.logError(e, Manager.applyStringTable("On connecting parallely"));
			try
			{
				dao.close();
			}
			catch(Throwable e1)
			{
				
			}
		}
		
		return dao;
	}
	
	/**
	 * <p>접속을 시도합니다. 필요한 정보를 사용자에게 물어봅니다.</p>
	 * <p>사용자에게 정보를 묻는 과정에서, GUI 환경에서는 하나하나 입력 창을 따로 띄우게 되므로, 주로 이 메소드는 콘솔 환경에서 사용합니다.</p>
	 * 
	 * @param manager : 매니저 객체
	 */
	public static Dao tryConnect(Manager manager)
	{
		String classPath, url, id, pw;
		String dbName = null;
		
		int type = RDBMS;
		
		dbName = manager.askInput(Manager.applyStringTable("Input Data source name, or just input space."), true);
		
		if(dbName != null && dbName.equalsIgnoreCase("Oracle"))
		{
			classPath = "oracle.jdbc.driver.OracleDriver";
			String ip = manager.askInput(Manager.applyStringTable("Input IP address of Oracle server."), true);
			String sid = manager.askInput(Manager.applyStringTable("Input SID of Oracle server."), true);
			String port = manager.askInput(Manager.applyStringTable("Input port number of Oracle server.") + "("
			+ Manager.applyStringTable("Default") + " : 1521)", true);
			if(port == null || port.trim().equals("")) port = "1521";
			// url = "jdbc:oracle:thin:@" + ip + ":" + port + ":" + sid;
			url = "jdbc:oracle:thin:@" + ip + ":" + port + "/" + sid;
			type = RDBMS;
		}
		else if(dbName != null && dbName.equalsIgnoreCase("MariaDB"))
		{
			classPath = "org.mariadb.jdbc.Driver";
			String ip = manager.askInput(Manager.applyStringTable("Input IP address of DB server."), true);
			String sid = manager.askInput(Manager.applyStringTable("Input DB name of MariaDB."), true);
			String port = manager.askInput(Manager.applyStringTable("Input port number of DB server.") + "("
					+ Manager.applyStringTable("Default") + " : 3306)", true);
			if(port == null || port.trim().equals("")) port = "3306";
			url = "jdbc:mariadb://" + ip + ":" + port + "/" + sid;
			type = RDBMS;
		}
		else if(dbName != null && dbName.equalsIgnoreCase("H2"))
		{
			classPath = "org.h2.Driver";
			String ip = manager.askInput(Manager.applyStringTable("Input IP address of DB server."), true);
			String sid = manager.askInput(Manager.applyStringTable("Input DB name of MariaDB."), true);
			String port = manager.askInput(Manager.applyStringTable("Input port number of DB server.") + "("
					+ Manager.applyStringTable("Default") + " : 8082)", true);
			if(port == null || port.trim().equals("")) port = "8082";
			if(port.equals("8082")) url = "jdbc:h2:" + ip + "/" + sid;
			else url = "jdbc:h2:" + ip + ":" + port + "/" + sid;
			type = RDBMS;
		}
		else if(dbName != null && dbName.equalsIgnoreCase("PostgresSQL"))
		{
			classPath = "org.postgresql.Driver";
			String ip = manager.askInput(Manager.applyStringTable("Input IP address of DB server."), true);
			String sid = manager.askInput(Manager.applyStringTable("Input DB name of MariaDB."), true);
			String port = manager.askInput(Manager.applyStringTable("Input port number of DB server.") + "("
					+ Manager.applyStringTable("Default") + " : 5432)", true);
			if(port == null || port.trim().equals("")) port = "5432";
			if(port.equals("8082")) url = "jdbc:postgresql://" + ip + "/" + sid;
			else url = "jdbc:postgresql://" + ip + ":" + port + "/" + sid;
			type = RDBMS;
		}
		else if(dbName != null && (dbName.equalsIgnoreCase("Cubrid")))
		{
			classPath = "cubrid.jdbc.driver.CUBRIDDriver";
			String ip = manager.askInput(Manager.applyStringTable("Input IP address of DB server."), true);
			String sid = manager.askInput(Manager.applyStringTable("Input SID of Cubrid server."), true);
			String port = manager.askInput(Manager.applyStringTable("Input port number of Cubrid server.") + "("
					+ Manager.applyStringTable("Default") + " : 30000)", true);
			if(port == null || port.trim().equals("")) port = "30000";
			url = "jdbc:cubrid:" + ip + ":" + port + ":" + sid + ":::";
			type = RDBMS;
		}
		else
		{
			classPath = manager.askInput(Manager.applyStringTable("Please input the classPath of driver"), true);
			if(classPath == null || classPath.trim().equals(""))
			{
				type = FAKE;
				manager.alert(Manager.applyStringTable("Fake DAO connection will be created."));
			}
			url = manager.askInput(Manager.applyStringTable("Please input the data source URL"), true);
			if(url == null || url.trim().equals(""))
			{
				type = FAKE;
				manager.alert(Manager.applyStringTable("Fake DAO connection will be created."));
			}
			type = UNKNOWN;
		}
		id = manager.askInput(Manager.applyStringTable("Please input data source account ID"), true);
		if(id == null || id.trim().equals("")) throw new NullPointerException(Manager.applyStringTable("ID for data source is needed."));
		
		pw = manager.askInput(Manager.applyStringTable("Please input data source account PW"), true);
		
		ConsoleUtil.clearConsole(manager);
		
		Dao newDao = null;
		
		if(DataUtil.isNotEmpty(classPath))
		{
			if(classPath.trim().equalsIgnoreCase("FAKE")) type = FAKE;
		}
		
		switch(type)
		{
		case RDBMS:
			newDao = new JdbcDao(manager);
			break;
		case FAKE:
			newDao = new FakeDao(manager);
			break;
		default:
			newDao = new JdbcDao(manager);
			break;
		}
		
		manager.log(Manager.applyStringTable("Trying to connect at") + " " + url + " " + Manager.applyStringTable("as") + " " + id + ".");
		
		newDao.setClassPath(classPath);
		try
		{
			newDao.setId(id);
			newDao.setPw(pw);
			newDao.setUrl(url);
			
			if(Manager.getOption("safe_mode") != null && DataUtil.parseBoolean("safe_mode"))
			{
				newDao.connect();
			}
			else newDao.connectDirectly();			
			
			manager.log(Manager.applyStringTable("Connected at") + " " + url + " " + Manager.applyStringTable("as")
					+ " " + id + ".");
			
			return newDao;
		}
		catch (Throwable e)
		{
			manager.logError(e, Manager.applyStringTable("On trying to connect to the DB"));
			return null;
		}
		finally
		{
			try
			{
				newDao.close();
			}
			catch(Throwable e)
			{
				
			}
		}
	}
}
