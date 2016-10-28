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
/*
 Be careful !
 Oracle software has a license policy called OTN.
 If you want to use this source to use Oracle Database, you should agree OTN.
 Visit http://www.oracle.com to see details. 
 */

package hjow.dbtool.oracle;

import java.io.File;

import hjow.dbtool.common.DBTool;
import hjow.hgtable.dao.Dao;
import hjow.hgtable.tableset.TableSet;
import hjow.hgtable.util.DataUtil;

/**
 * <p>이 클래스 객체는 Oracle Database 에 대한 주요 정보 및 자주 쓰는 쿼리를 메소드로 가집니다.</p>
 * 
 * @author HJOW
 *
 */
public class OTool extends DBTool
{
	private static final long serialVersionUID = -7037136791901440512L;
	/**
	 * <p>생성자입니다.</p>
	 */
	public OTool()
	{
		prepareTnsPath();
	}
	/**
	 * <p>생성자입니다. DB에 액세스할 수 있는 DAO 객체가 필요합니다.</p>
	 * 
	 * @param dao : DAO 객체
	 */
	public OTool(Dao dao)
	{
		super(dao);
		prepareTnsPath();
	}
	
	/**
	 * <p>오라클 JDBC 드라이버의 클래스 이름을 풀네임으로 반환합니다.</p>
	 * 
	 * @return JDBC 드라이버 클래스명
	 */
	@Override
	public String getJdbcClassPath()
	{
		return "oracle.jdbc.driver.OracleDriver";
	}
	
//	@Override
//	public TableSet dataFileList(boolean allData, String addWheres) throws Exception
//	{
//		if(allData)
//		{
//			return dao.query("SELECT * FROM DBA_DATA_FILES");
//		}
//		else
//		{
//			return dao.query("SELECT FILE_NAME FROM DBA_DATA_FILES");
//		}		
//	}
	@Override
	public String getTableListQuery(boolean allData)
	{
		if(allData) return "SELECT * FROM TAB";
		else return "SELECT TNAME FROM TAB";
	}
	@Override
	public String getUserListQuery(boolean isDBA, boolean allData)
	{
		if(allData)
		{
			if(isDBA) return "SELECT * FROM DBA_USERS";
			else return "SELECT * FROM USER_USERS";
		}
		else
		{
			if(isDBA) return "SELECT USERNAME FROM DBA_USERS";
			else return "SELECT USERNAME FROM USER_USERS";
		}		
	}
	@Override
	public String getTablespaceListQuery(boolean isDBA, boolean allData)
	{
		if(allData)
		{
			if(isDBA) return "SELECT * FROM DBA_TABLESPACES";
			else return "SELECT * FROM USER_TABLESPACES";
		}
		else
		{
			if(isDBA) return "SELECT TABLESPACE_NAME FROM DBA_TABLESPACES";
			else return "SELECT TABLESPACE_NAME FROM USER_TABLESPACES";
		}
	}
	@Override
	public String getViewListQuery(boolean isDBA, boolean allData)
	{
		if(allData)
		{
			if(isDBA) return "SELECT * FROM DBA_VIEWS";
			else return "SELECT * FROM USER_VIEWS";
		}
		else
		{
			if(isDBA) return "SELECT VIEW_NAME FROM DBA_VIEWS";
			else return "SELECT VIEW_NAME FROM USER_VIEWS";
		}	
	}
	@Override
	public String getDbLinkListQuery(boolean isDBA, boolean allData)
	{
		if(allData)
		{
			if(isDBA) return "SELECT * FROM DBA_DB_LINKS";
			else return "SELECT * FROM USER_DB_LINKS";
		}
		else
		{
			if(isDBA) return "SELECT DB_LINK FROM DBA_DB_LINKS";
			else return "SELECT DB_LINK FROM USER_DB_LINKS";
		}	
	}
	@Override
	public String getObjectListQuery(boolean isDBA, boolean allData)
	{
		if(allData)
		{
			if(isDBA) return "SELECT * FROM DBA_OBJECTS";
			else return "SELECT * FROM USER_OBJECTS";
		}
		else
		{
			if(isDBA) return "SELECT OBJECT_NAME FROM DBA_OBJECTS";
			else return "SELECT OBJECT_NAME FROM USER_OBJECTS";
		}
	}
	@Override
	public String getDataFileListQuery(boolean allData)
	{
		if(allData)
		{
			return "SELECT * FROM DBA_DATA_FILES";
		}
		else
		{
			return "SELECT FILE_NAME FROM DBA_DATA_FILES";
		}
	}
	@Override
	public String getProcedureListQuery(boolean isDBA, boolean allData)
	{
		if(isDBA)
		{
			if(allData)
			{
				return "SELECT * FROM ALL_SOURCE WHERE TYPE = 'PROCEDURE'";
			}
			else
			{
				return "SELECT DISTINCT NAME FROM ALL_SOURCE WHERE TYPE = 'PROCEDURE'";
			}
		}
		else
		{
			if(allData)
			{
				return "SELECT * FROM USER_SOURCE WHERE TYPE = 'PROCEDURE'";
			}
			else
			{
				return "SELECT DISTINCT NAME FROM USER_SOURCE WHERE TYPE = 'PROCEDURE'";
			}
		}
	}
	@Override
	public String getFunctionListQuery(boolean isDBA, boolean allData)
	{
		if(isDBA)
		{
			if(allData)
			{
				return "SELECT * FROM ALL_SOURCE WHERE TYPE = 'FUNCTION'";
			}
			else
			{
				return "SELECT DISTINCT NAME FROM ALL_SOURCE WHERE TYPE = 'FUNCTION'";
			}
		}
		else
		{
			if(allData)
			{
				return "SELECT * FROM USER_SOURCE WHERE TYPE = 'FUNCTION'";
			}
			else
			{
				return "SELECT DISTINCT NAME FROM USER_SOURCE WHERE TYPE = 'FUNCTION'";
			}
		}		
	}
	@Override
	public String getProcedureScriptQuery(String procedureName)
	{
		return "SELECT TEXT FROM USER_SOURCE WHERE TYPE = 'PROCEDURE' AND NAME = '" + procedureName + "'";
	}
	@Override
	public String getFunctionScriptQuery(String functionName)
	{
		return "SELECT TEXT FROM USER_SOURCE WHERE TYPE = 'FUNCTION' AND NAME = '" + functionName + "'";
	}
	/**
	 * <p>프로시저, 혹은 함수의 스크립트 내용을 반환합니다. 이러한 기능을 지원하지 않으면 null 을 반환합니다.</p>
	 * 
	 * @param objectName : 프로시저, 혹은 함수 이름
	 * @return 해당 프로시저, 혹은 함수의 스크립트 내용
	 * @throws Exception 네트워크 문제, 데이터 소스 스크립트 문법 오류, 데이터 소스 문제
	 */
	@Override
	public String getScriptOf(String objectName) throws Exception
	{
		TableSet contents = dao.query("SELECT TEXT FROM USER_SOURCE WHERE NAME = '" + objectName + "'");
		StringBuffer results = new StringBuffer("CREATE OR REPLACE ");
		
		if(DataUtil.isEmpty(contents.getColumn("TEXT").getData()))
		{
			return "";
		}
		for(String s : contents.getColumn("TEXT").getData())
		{
			results = results.append(s);
			results = results.append("\n");
		}
		
		return results.toString();
	}
	
	@Override
	public TableSet getProcedureWithStatus(String wheres) throws Exception
	{
		String statements = "SELECT OBJECT_NAME AS NAME, CASE STATUS WHEN 'VALID' THEN 'O' ELSE 'X' END AS STATUS FROM ALL_OBJECTS WHERE OBJECT_TYPE = 'PROCEDURE'";
		if(DataUtil.isNotEmpty(wheres))
		{
			statements = statements + " AND " + wheres;
		}
		
		return dao.query(statements);
	}
	
	@Override
	public TableSet getFunctionWithStatus(String wheres) throws Exception
	{
		String statements = "SELECT OBJECT_NAME AS NAME, CASE STATUS WHEN 'VALID' THEN 'O' ELSE 'X' END AS STATUS FROM ALL_OBJECTS WHERE OBJECT_TYPE = 'FUNCTION'";
		if(DataUtil.isNotEmpty(wheres))
		{
			statements = statements + " AND " + wheres;
		}
		
		return dao.query(statements);
	}
	
	@Override
	public TableSet getViewWithStatus(String wheres) throws Exception
	{
		String statements = "SELECT OBJECT_NAME AS NAME, CASE STATUS WHEN 'VALID' THEN 'O' ELSE 'X' END AS STATUS FROM ALL_OBJECTS WHERE OBJECT_TYPE = 'VIEW'";
		if(DataUtil.isNotEmpty(wheres))
		{
			statements = statements + " AND " + wheres;
		}
		
		return dao.query(statements);
	}
	
	@Override
	public TableSet getProcedureWithStatus(String keyword, String wheres) throws Exception
	{
		String statements = "SELECT OBJECT_NAME AS NAME, CASE STATUS WHEN 'VALID' THEN 'O' ELSE 'X' END AS STATUS FROM ALL_OBJECTS WHERE OBJECT_TYPE = 'PROCEDURE'";
		if(DataUtil.isNotEmpty(keyword))
		{
			statements = statements + " AND OBJECT_NAME LIKE \'%" + keyword + "%\'";
		}
		if(DataUtil.isNotEmpty(wheres))
		{
			statements = statements + " AND " + wheres;
		}
		
		return dao.query(statements);
	}
	
	@Override
	public TableSet getFunctionWithStatus(String keyword, String wheres) throws Exception
	{
		String statements = "SELECT OBJECT_NAME AS NAME, CASE STATUS WHEN 'VALID' THEN 'O' ELSE 'X' END AS STATUS FROM ALL_OBJECTS WHERE OBJECT_TYPE = 'FUNCTION'";
		if(DataUtil.isNotEmpty(keyword))
		{
			statements = statements + " AND OBJECT_NAME LIKE \'%" + keyword + "%\'";
		}
		if(DataUtil.isNotEmpty(wheres))
		{
			statements = statements + " AND " + wheres;
		}
		
		return dao.query(statements);
	}
	
	@Override
	public TableSet getViewWithStatus(String keyword, String wheres) throws Exception
	{
		String statements = "SELECT OBJECT_NAME AS NAME, CASE STATUS WHEN 'VALID' THEN 'O' ELSE 'X' END AS STATUS FROM ALL_OBJECTS WHERE OBJECT_TYPE = 'VIEW'";
		if(DataUtil.isNotEmpty(keyword))
		{
			statements = statements + " AND OBJECT_NAME LIKE \'%" + keyword + "%\'";
		}
		if(DataUtil.isNotEmpty(wheres))
		{
			statements = statements + " AND " + wheres;
		}
		
		return dao.query(statements);
	}
	
	@Override
	public String getPreventTimeoutScript()
	{
		return "SELECT * FROM TAB WHERE ROWNUM <= 1";
	}
	@Override
	public String getDBName()
	{
		return "Oracle";
	}
	@Override
	public String getRownumName()
	{
		return "ROWNUM";
	}
	@Override
	public String getTableListKeyColumnName()
	{
		return "TNAME";
	}
	@Override
	public String getUserListKeyColumnName()
	{
		return "USERNAME";
	}
	@Override
	public String getTablespaceListKeyColumnName()
	{
		return "TABLESPACE_NAME";
	}
	@Override
	public String getViewListKeyColumnName()
	{
		return "VIEW_NAME";
	}
	@Override
	public String getDbLinkListKeyColumnName()
	{
		return "DB_LINK";
	}
	@Override
	public String getObjectListKeyColumnName()
	{
		return "OBJECT_NAME";
	}
	@Override
	public String getDataFileListKeyColumnName()
	{
		return "FILE_NAME";
	}
	@Override
	public String getProcedureListKeyColumnName()
	{
		return "NAME";
	}
	@Override
	public String getFunctionListKeyColumnName()
	{
		return "NAME";
	}
	@Override
	public DBTool newInstance(Dao dao)
	{
		return new OTool(dao);
	}
	@Override
	public String jdbcUrlPrefix()
	{
		return "jdbc:oracle";
	}
	
	/**
	 * <p>TNS 홈 경로를 반환합니다.</p>
	 * <p>사전에 TNS_ADMIN 또는 ORACLE_HOME 환경 변수가 지정되어 있어야 사용할 수 있습니다.</p>
	 * 
	 * @return TNS 경로 (존재하지 않을 경우 null)
	 */
	public static File getTnsFilePath()
	{
		String tnsAdmin = System.getenv("TNS_ADMIN");
	    if (tnsAdmin == null) {
	        String oracleHome = System.getenv("ORACLE_HOME");
	        if (oracleHome == null) {
	            return null; //failed to find any useful env variables
	        }
	        tnsAdmin = oracleHome + File.separatorChar + "network" + File.separatorChar + "admin";
	    }
	    if(! (tnsAdmin.endsWith(String.valueOf(File.separatorChar))))
	    {
	    	tnsAdmin = tnsAdmin + String.valueOf(File.separatorChar);
	    }
	    File tnsPath = new File(tnsAdmin);
	    if(! tnsPath.exists()) return null;
	    return tnsPath;
	}
	
	/**
	 * <p>TNS를 통한 접속을 준비합니다. 사용할 수 없으면 false 를 반환합니다.</p>
	 * 
	 * @return 사용 가능 여부
	 */
	public static boolean prepareTnsPath()
	{
		File tnsPath = getTnsFilePath();
		if(tnsPath == null) return false;
		System.setProperty("oracle.net.tns_admin", tnsPath.getAbsolutePath());
		return true;
	}
	
	/**
	 * <p>TNS 홈 경로를 반환합니다.</p>
	 * <p>사전에 TNS_ADMIN 또는 ORACLE_HOME 환경 변수가 지정되어 있어야 사용할 수 있습니다.</p>
	 * <p>Nashorn 환경에서도 호출할 수 있습니다.</p>
	 * 
	 * @return TNS 경로 (존재하지 않을 경우 null)
	 */
	public File getTnsPath()
	{
		return getTnsFilePath();
	}
	
	/**
	 * <p>TNS를 통한 접속을 준비합니다. 사용할 수 없으면 false 를 반환합니다. Nashorn 환경에서도 호출할 수 있습니다.</p>
	 * 
	 * @return 사용 가능 여부
	 */
	public boolean prepareTns()
	{
		return prepareTnsPath();
	}
	
	@Override
	public String getTableInfoQuery(String tableName) {
		StringBuffer result = new StringBuffer("");
		result = result.append("SELECT TBL.TABLE_NAME  AS TABLE_NAME");
		result = result.append("     , TBL.COLUMN_NAME AS COLUMN_ID");
		result = result.append("     , COL.COMMENTS    AS COLUMN_NAME");
		result = result.append("     , CASE WHEN TBL.DATA_TYPE LIKE 'TIMESTAMP' THEN 'TIMESTAMP'");
		result = result.append("            ELSE TBL.DATA_TYPE END AS DATA_TYPE_NAME");
		result = result.append("     , CASE WHEN TBL.DATA_TYPE LIKE 'VARCHAR' THEN TBL.DATA_LENGTH");
		result = result.append("            WHEN TBL.DATA_TYPE LIKE 'NUMBER'  THEN TBL.DATA_PRECISION");
		result = result.append("            ELSE NULL END AS DATA_LENGTH");
		result = result.append("     , TBL.NULLABLE     AS CAN_BE_NULL");
		result = result.append("     , TBL.DATA_DEFAULT AS DEFAULT_VALUE");
		result = result.append("  FROM USER_TAB_COLUMNS TBL");
		result = result.append("  JOIN USER_COL_COMMENTS COL");
		result = result.append("    ON COL.TABLE_NAME  = TBL.TABLE_NAME");
		result = result.append("   AND COL.COLUMN_NAME = TBL.COLUMN_NAME");
		result = result.append(" WHERE TBL.TABLE_NAME = ");
		result = result.append("'" + tableName + "'");
		result = result.append(" ORDER BY TBL.TABLE_NAME, TBL.COLUMN_NAME");
		return result.toString();
	}
}
