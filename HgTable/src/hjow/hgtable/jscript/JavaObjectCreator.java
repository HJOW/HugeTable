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

import hjow.dbtool.common.DBTool;
import hjow.dbtool.cubrid.CTool;
import hjow.dbtool.h2.HTool;
import hjow.dbtool.mariadb.MTool;
import hjow.dbtool.oracle.OTool;
import hjow.dbtool.postgresql.PTool;
import hjow.hgtable.Manager;
import hjow.hgtable.dao.Dao;
import hjow.hgtable.ui.module.defaults.ArgumentData;
import hjow.hgtable.util.TextBlock;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * <p>이 객체는 J스크립트 내에서 joc 이름으로 액세스할 수 있습니다. 스크립트 상에서 사용할 수 있는 자주 사용하는 자바 객체들을 대신 생성해 반환합니다.</p>
 * 
 * @author HJOW
 *
 */
public class JavaObjectCreator implements JScriptObject
{
	private static final long serialVersionUID = 1279834596242133737L;
	
	/**
	 * <p>기본 생성자입니다.</p>
	 * 
	 */
	public JavaObjectCreator()
	{
		
	}
	public StringTokenizer newStringTokenizer(String str, String delim)
	{
		return new StringTokenizer(str, delim);
	}
	public BigInteger newBigInt(Object param)
	{
		return new BigInteger(String.valueOf(param));
	}
	public BigDecimal newDecimal(Object param)
	{
		return new BigDecimal(String.valueOf(param));
	}
	public Map<String, Object> newMap()
	{
		return new Hashtable<String, Object>();
	}
	public Set<Object> newSet()
	{
		return new HashSet<Object>();
	}
	public List<Object> newList()
	{
		return new Vector<Object>();
	}
	public byte[] newByteArray(int size)
	{
		return new byte[size];
	}
	public String string(Object ob)
	{
		if(ob == null) return new String("");
		return String.valueOf(ob);
	}
	public ArgumentData argumentData(String name, String data)
	{
		ArgumentData newOne = new ArgumentData();
		newOne.setName(name);
		newOne.setData(data);
		return newOne;
	}
	public TextBlock textBlock(int startPos, int endPos, String contents)
	{
		return new TextBlock(startPos, endPos, contents);
	}
	public DBTool newTool(String name, Dao dao)
	{
		if(name.equalsIgnoreCase("Oracle")) return new OTool(dao);
		else if(name.equalsIgnoreCase("CUBRID")) return new CTool(dao);
		else if(name.equalsIgnoreCase("MariaDB")) return new MTool(dao);
		else if(name.equalsIgnoreCase("PostgresSQL")) return new PTool(dao);
		else if(name.equalsIgnoreCase("H2")) return new HTool(dao);
		else return null;
	}
	public ByteArrayInputStream newByteArrayInputStream(byte[] buf)
	{
		return new ByteArrayInputStream(buf);
	}
	public ByteArrayOutputStream newByteArrayOutputStream()
	{
		return new ByteArrayOutputStream();
	}
	/**
	 * <p>날짜 객체를 반환합니다. 기본값으로 오늘 현재의 날짜와 시간 데이터가 삽입된 상태로 반환됩니다.</p>
	 * 
	 * @return 날짜 객체
	 */
	public Date newDate()
	{
		return new Date(System.currentTimeMillis());
	}
	@Override
	public String help()
	{
		StringBuffer results = new StringBuffer("");
		
		results = results.append(Manager.applyStringTable("This object has methods to create Java basic objects.") + "\n");
		results = results.append("newBigInt(p)" + " : " + Manager.applyStringTable("Create java.math.BigInteger object. This object treats infinite-size integer number.") + "\n");
		results = results.append("newDecimal(p)" + " : " + Manager.applyStringTable("Create java.math.BigDecimal object. This object treats infinite-size floating number.") + "\n");
		results = results.append("newMap()" + " : " + Manager.applyStringTable("Create java.util.Hashtable<String, Object> object.") + "\n");
		results = results.append("newSet()" + " : " + Manager.applyStringTable("Create java.util.HashSet<Object> object.") + "\n");
		results = results.append("newList()" + " : " + Manager.applyStringTable("Create java.util.Vector<Object> object.") + "\n");
		results = results.append("newByteArray(size)" + " : " + Manager.applyStringTable("Create new byte array.") + "\n");
		results = results.append("string(ob)" + " : " + Manager.applyStringTable("Create java.lang.String object.") + "\n");
		results = results.append("argumentData(name, data)" + " : " + Manager.applyStringTable("Create argument data bean. This has two fields to to match arguments name and its value.") + "\n");
		results = results.append("newTool(dbName, dao)" + " : " + Manager.applyStringTable("Create data source tool object which has useful methods to control data source.") + "\n");
		results = results.append("newByteArrayInputStream(byteArray)" + " : " + Manager.applyStringTable("Create java.io.ByteArrayInputStream object. This object can be used like a input stream.") + "\n");
		results = results.append("newByteArrayOutputStream()" + " : " + Manager.applyStringTable("Create java.io.ByteArrayOutputStream object. This object can be used like a output stream and collect bytes.") + "\n");
		
		return results.toString();
	}
	@Override
	public void noMoreUse()
	{
		
	}
	@Override
	public boolean isAlive()
	{
		return true;
	}
}
