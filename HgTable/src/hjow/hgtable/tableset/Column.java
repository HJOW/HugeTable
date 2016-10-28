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

import hjow.hgtable.Main;
import hjow.hgtable.Manager;
import hjow.hgtable.jscript.JScriptObject;
import hjow.hgtable.jscript.JScriptRunner;
import hjow.hgtable.util.ConstantUtil;
import hjow.hgtable.util.DataUtil;
import hjow.hgtable.util.InvalidInputException;
import hjow.hgtable.util.AnalyzeUtil;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * <p>컬럼 정보를 담은 클래스입니다. 컬럼 이름과 타입, 그리고 데이터들을 가지고 있습니다.</p>
 * 
 * @author HJOW
 *
 */
public class Column implements JScriptObject
{
	private static final long serialVersionUID = -4243912987054662208L;
	public static final int TYPE_BLANK = ConstantUtil.TYPE_BLANK();
	public static final int TYPE_BOOLEAN = ConstantUtil.TYPE_BOOLEAN();
	public static final int TYPE_ERROR = ConstantUtil.TYPE_ERROR();
	public static final int TYPE_FORMULA = ConstantUtil.TYPE_FORMULA();
	public static final int TYPE_NUMERIC = ConstantUtil.TYPE_NUMERIC();
	public static final int TYPE_INTEGER = ConstantUtil.TYPE_INTEGER();
	public static final int TYPE_FLOAT = ConstantUtil.TYPE_FLOAT();
	public static final int TYPE_STRING = ConstantUtil.TYPE_STRING();
	public static final int TYPE_DATE = ConstantUtil.TYPE_DATE();
	public static final int TYPE_OBJECT = ConstantUtil.TYPE_OBJECT();
	public static final int TYPE_BLOB = ConstantUtil.TYPE_BLOB();
	public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm ss SSS";
	
	protected String name;
	protected int type;
	protected List<String> data = new Vector<String>();
	
	/**
	 * <p>기본 생성자입니다.</p>
	 * 
	 */
	public Column()
	{
		
	}
	
	/**
	 * <p>이름과 타입을 지정하여 빈 컬럼 객체를 만듭니다.</p>
	 * 
	 * @param name : 컬럼 이름
	 * @param type : 타입 코드 (Apache POI 의 Cell 형식을 따름)
	 */
	public Column(String name, int type)
	{
		this.name = name;
		this.type = type;
	}
	
	/**
	 * <p>이름과 타입, 데이터를 컬럼 객체를 만듭니다.</p>
	 * 
	 * @param name : 컬럼 이름
	 * @param type : 타입 코드 (Apache POI 의 Cell 형식을 따름)
	 * @param data : 데이터 리스트
	 */
	public Column(String name, int type, List<String> data)
	{
		super();
		this.name = name;
		this.type = type;
		this.data = data;
	}
	
	/**
	 * <p>스크립트를 통해 자동으로 데이터들을 생성합니다.</p>
	 * <p>생성할 데이터 갯수만큼 스크립트를 실행합니다.</p>
	 * <p>매 회 스크립트 실행 전 startValue 라는 변수 이름으로 시작 값을, nowProgress 라는 변수 이름으로 현재 몇 번째 데이터를 생성해야 하는지를 스크립트 엔진에 삽입합니다.</p>
	 * 
	 * @param name : 컬럼 이름
	 * @param size : 생성할 데이터 갯수
	 * @param startValue : 시작 값
	 * @param scripts : 실행할 스크립트 (최종 반환 값은 정수, 혹은 실수 형태의 숫자여야 합니다.)
	 * @param runner : 스크립트 엔진
	 * @throws Throwable : 스크립트 오류
	 */
	public Column(String name, int size, double startValue, String scripts, JScriptRunner runner) throws Throwable
	{
		super();
		this.name = name;
		this.type = TYPE_FLOAT;
		this.data = new Vector<String>();
		
		setColumnSequencial(size, startValue, scripts, runner);
	}
	
	/**
	 * <p>수열로 데이터들을 생성합니다. 기존 데이터는 사라집니다.</p>
	 * 
	 * @param startValue : 시작 값
	 * @param scripts    : 실행할 스크립트 (최종 반환 값은 정수, 혹은 실수 형태의 숫자여야 합니다.)
	 * @param runner     : 스크립트 엔진
	 * @throws Throwable : 스크립트 오류
	 */
	public void setColumnSequencial(double startValue, String scripts, JScriptRunner runner) throws Throwable
	{
		setColumnSequencial(this.size(), startValue, scripts, runner);
	}
	
	/**
	 * <p>수열로 데이터들을 생성합니다. 기존 데이터는 사라집니다.</p>
	 * 
	 * @param size : 생성할 데이터 갯수
	 * @param startValue : 시작 값
	 * @param scripts    : 실행할 스크립트 (최종 반환 값은 정수, 혹은 실수 형태의 숫자여야 합니다.)
	 * @param runner     : 스크립트 엔진
	 * @throws Throwable : 스크립트 오류
	 */
	public void setColumnSequencial(int size, double startValue, String scripts, JScriptRunner runner) throws Throwable
	{
		this.data.clear();
		this.type = TYPE_FLOAT;
		for(int i=0; i<size; i++)
		{
			runner.put("startValue", new Double(startValue));
			runner.put("nowProgress", new Integer(i));
			this.data.add(String.valueOf(runner.execute(scripts)));
		}
	}
	
	/**
	 * <p>정수 컬럼으로 변환합니다. 컬럼 타입이 NUMERIC, FLOAT 혹은 STRING 이어야 이 메소드를 사용할 수 있습니다.</p>
	 * 
	 * @exception InvalidInputException : 정수로 변환할 수 없는 타입의 컬럼인 경우
	 */
	public void toIntegerColumn() throws RuntimeException
	{
		if(type == TYPE_NUMERIC || type == TYPE_FLOAT || type == TYPE_INTEGER || type == TYPE_STRING)
		{
			type = TYPE_INTEGER;
			
			List<String> beforeData = new Vector<String>();
			beforeData.addAll(this.data);
			try
			{
				this.data = new Vector<String>();
				for(int i=0; i<beforeData.size(); i++)
				{
					this.data.add(String.valueOf(Double.parseDouble(beforeData.get(i).trim())));
				}
			}
			catch(RuntimeException e)
			{
				this.data = new Vector<String>();
				this.data.addAll(beforeData);
				throw e;
			}
		}
		else throw new InvalidInputException(Manager.applyStringTable("Cannot convert") + " " + type() + " " + Manager.applyStringTable("into the integer") + ".");
	}
	
	/**
	 * <p>데이터들 모두 양쪽 공백을 제거합니다.</p>
	 * 
	 */
	public void trimAll()
	{
		for(int i=0; i<getData().size(); i++)
		{
			getData(i).trim();
		}
	}
	
	/**
	 * <p>Apache POI 의 Cell 형식 타입 코드를 JDBC의 타입 코드로 변환합니다.</p>
	 */
	public void switchTypeCodeToDB()
	{
		type = typeCodeToDB(type);
	}
	
	/**
	 * <p>Apache POI 의 Cell 형식 타입 코드를 JDBC의 타입 코드로 변환합니다.</p>
	 * 
	 * @param type : 변환할 POI 형식 타입 코드
	 * @return JDBC 형식 타입 코드
	 */
	public static int typeCodeToDB(int type)
	{
		int results = -1;
		
		if(type == TYPE_STRING)
		{
			results = Types.VARCHAR;
		}
		else if(type == TYPE_NUMERIC)
		{
			results = Types.NUMERIC;
		}
		else if(type == TYPE_FLOAT)
		{
			results = Types.DOUBLE;
		}
		else if(type == TYPE_INTEGER)
		{
			results = Types.INTEGER;
		}
		else if(type == TYPE_BOOLEAN)
		{
			results = Types.BOOLEAN;
		}
		else if(type == TYPE_DATE)
		{
			results = Types.DATE;
		}
		else if(type == TYPE_BLOB)
		{
			results = Types.BLOB;
		}
		else if(type == TYPE_OBJECT)
		{
			results = Types.JAVA_OBJECT;
		}
		else if(type == TYPE_BLANK)
		{
			results = Types.NULL;
		}
		else
		{
			results = Types.VARCHAR;
		}
		
		return results;
	}
	
	public static int typeCodeToSheet(int type)
	{
		int results = -1;
		switch(type)
		{
		case Types.VARCHAR:
			results = TYPE_STRING;
			break;
		case Types.CHAR:
			results = TYPE_STRING;
			break;
		case Types.CLOB:
			results = TYPE_STRING;
			break;
		case Types.NUMERIC:
			results = TYPE_NUMERIC;
			break;
		case Types.BIGINT:
			// results = Column.TYPE_NUMERIC;
			results = TYPE_INTEGER;
			break;
		case Types.DOUBLE:
			// results = Column.TYPE_NUMERIC;
			results = TYPE_FLOAT;
			break;
		case Types.FLOAT:
			results = TYPE_NUMERIC;
			break;
		case Types.INTEGER:
			results = TYPE_NUMERIC;
			break;
		case Types.DECIMAL:
			results = TYPE_NUMERIC;
			break;
		case Types.DATE:
			results = TYPE_DATE;
			break;
		case Types.BOOLEAN:
			results = TYPE_BOOLEAN;
			break;
		case Types.JAVA_OBJECT:
			results = TYPE_OBJECT;
			break;
		case Types.BLOB:
			results = TYPE_BLOB;
				break;
		case Types.NULL:
			results = TYPE_BLANK;
			break;
		default:
			results = TYPE_STRING;
			break;
		}
		return results;
	}
	
	/**
	 * <p>JDBC의 타입 코드를 Apache POI 의 Cell 형식 타입 코드로 변환합니다.</p>
	 * 
	 */
	public void switchTypeCodeToSheet()
	{
		type = typeCodeToSheet(type);
	}
	/**
	 * <p>컬럼 이름을 반환합니다.</p>
	 * 
	 * @return 컬럼 이름
	 */
	public String getName()
	{
		return name;
	}
	/**
	 * <p>컬럼 이름을 지정합니다.</p>
	 * 
	 * @param name : 새 컬럼 이름
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	/**
	 * <p>데이터의 수를 반환합니다.</p>
	 * 
	 * @return 데이터 수
	 */
	public int size()
	{
		return data.size();
	}
	public int getType()
	{
		return type;
	}
	public void setType(int type)
	{
		this.type = type;
	}
	public List<String> getData()
	{
		return data;
	}
	public String getData(int index)
	{
		try
		{
			return data.get(index);
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			StringBuffer err = new StringBuffer("");
			for(StackTraceElement errEl : e.getStackTrace())
			{
				err = err.append("\t " + errEl + "\n");
			}
			
			throw new ArrayIndexOutOfBoundsException(Manager.applyStringTable("Array index out of range") + ", " 
					+ Manager.applyStringTable("Column name") + " : " + getName() + ", "
					+ Manager.applyStringTable("request") + " : " + index + "/" + data.size()
					+ "\n " + Manager.applyStringTable("<-\n") + err
					+ "\n " + Manager.applyStringTable("Original Message") + "..."
					+ e.getMessage() + "\n" + Manager.applyStringTable("End"));
		}
	}
	public void setData(int index, String data)
	{
		this.data.set(index, data);
	}
	public void setData(List<String> data)
	{
		this.data = data;
	}
	/**
	 * <p>비어 있는 컬럼인지 여부를 반환합니다.</p>
	 * 
	 * @return 비어 있는지 여부
	 */
	public boolean isEmpty()
	{
		boolean isNotEmpty = false;
		
		if((name != null) && (! name.trim().equals(""))) return false;
		for(int i=0; i<data.size(); i++)
		{
			if(data.get(i) != null)
			{
				if(! (data.get(i).trim().equals("")))
				{
					isNotEmpty = true;
					break;
				}
			}
		}
		
		return (! isNotEmpty);
	}
	
	/**
	 * <p>0을 반환합니다. 단, 타입이 NUMERIC 이거나 FLOAT 이면 소수점 아래 자리 크기를 반환합니다.</p>
	 * 
	 * @return 소수점 아래 크기
	 */
	public int getUnderPointMaxSize()
	{
		int results = 0;
		int allSize = 0;
		
		for(int i=0; i<data.size(); i++)
		{
			if(allSize < data.get(i).length())
			{
				allSize = data.get(i).length();
			}
		}
		
		results = allSize - getTextMaxSize();
		if(results < 0) return 0;
		
		return results;
	}
	
	/**
	 * <p>안에 있는 데이터들 중 최대 길이(자리수)를 반환합니다. NUMERIC 이거나 FLOAT 인 경우 정수부 자리수만을 반환합니다.</p>
	 * 
	 * @return 최대 길이
	 */
	public int getTextMaxSize()
	{
		int results = 1;
		
		if(type == TYPE_NUMERIC || type == TYPE_FLOAT)
		{
			for(int i=0; i<data.size(); i++)
			{
				if(results < String.valueOf((int) Math.floor(Double.parseDouble(data.get(i)))).length())
				{
					results = String.valueOf((int) Math.floor(Double.parseDouble(data.get(i)))).length();
				}
			}
		}
		else
		{
			try
			{
				for(int i=0; i<data.size(); i++)
				{
					if(results < data.get(i).getBytes("UTF-8").length)
					{
						results = data.get(i).getBytes("UTF-8").length;
					}
				}
			}
			catch(Exception e)
			{
				Main.logError(e, "On getTextMaxSize");
			}
		}
		
		return results;
	}
	
	/**
	 * <p>컬럼의 SQL 방식 타입 이름을 반환합니다.</p>
	 * 
	 * @return SQL 방식의 타입 이름
	 */
	public String SQLType(boolean withSize, boolean useVarchar2)
	{
		String results;
		if(type == TYPE_BLANK)
		{
			if(useVarchar2) results = "VARCHAR2";
			else results = "VARCHAR";
			if(withSize) results = results + "(" + String.valueOf(getTextMaxSize()) + ")";
		}
		else if(type == TYPE_BOOLEAN)
		{
			results = "BOOLEAN";
			if(withSize) results = results + "(" + String.valueOf(getTextMaxSize()) + ")";
		}
		else if(type == TYPE_DATE)
		{
			results = "DATE";
		}
		else if(type == TYPE_ERROR)
		{
			if(useVarchar2) results = "VARCHAR2";
			else results = "VARCHAR";
			if(withSize) results = results + "(" + String.valueOf(getTextMaxSize()) + ")";
		}
		else if(type == TYPE_FORMULA)
		{
			results = "NUMERIC";
			if(withSize) results = results + "(" + String.valueOf(getTextMaxSize()) + ")";
		}
		else if(type == TYPE_NUMERIC)
		{
			results = "NUMERIC";
			if(withSize) results = results + "(" + String.valueOf(getTextMaxSize() + ", " + getUnderPointMaxSize()) + ")";
		}
		else if(type == TYPE_INTEGER)
		{
			results = "INTEGER";
			if(withSize) results = results + "(" + String.valueOf(getTextMaxSize()) + ")";
		}
		else if(type == TYPE_FLOAT)
		{
			results = "NUMERIC";
			if(withSize) results = results + "(" + String.valueOf(getTextMaxSize() + ", " + getUnderPointMaxSize()) + ")";
		}
		else if(type == TYPE_STRING)
		{
			if(useVarchar2) results = "VARCHAR2";
			else results = "VARCHAR";
			if(withSize) results = results + "(" + String.valueOf(getTextMaxSize()) + ")";
		}
		else if(type == TYPE_OBJECT)
		{
			results = "OBJECT";
		}
		else if(type == TYPE_BLOB)
		{
			results = "BLOB";
			if(withSize) results = results + "(" + String.valueOf(getTextMaxSize() + ", " + getUnderPointMaxSize()) + ")";
		}
		else
		{
			if(useVarchar2) results = "VARCHAR2";
			else results = "VARCHAR";
			if(withSize) results = results + "(" + String.valueOf(getTextMaxSize()) + ")";
		}
		return results;
	}
	
	/**
	 * <p>해당 타입 코드에 대한 이름을 반환합니다.</p> 
	 * 
	 * @param typeCode : 타입 코드 (Apache POI 기반 코드)
	 * @return 타입 이름
	 */
	public static String typeName(int typeCode)
	{
		if(typeCode == TYPE_BLANK)
		{
			return "BLANK";
		}
		else if(typeCode == TYPE_BOOLEAN)
		{
			return "BOOLEAN";
		}
		else if(typeCode == TYPE_DATE)
		{
			return "DATE";
		}
		else if(typeCode == TYPE_ERROR)
		{
			return "ERROR";
		}
		else if(typeCode == TYPE_FORMULA)
		{
			return "FORMULA";
		}
		else if(typeCode == TYPE_NUMERIC)
		{
			return "NUMERIC";
		}
		else if(typeCode == TYPE_INTEGER)
		{
			return "INTEGER";
		}
		else if(typeCode == TYPE_FLOAT)
		{
			return "FLOAT";
		}
		else if(typeCode == TYPE_STRING)
		{
			return "STRING";
		}
		else if(typeCode == TYPE_BLOB)
		{
			return "BLOB";
		}
		else if(typeCode == TYPE_OBJECT)
		{
			return "OBJECT";
		}
		else
		{
			return "BLANK";
		}
	}
	
	/**
	 * <p>컬럼의 타입 이름을 반환합니다.</p>
	 * 
	 * @return 타입 이름
	 */
	public String type()
	{
		return typeName(type);
	}
	/**
	 * <p>컬럼 타입을 지정합니다. 데이터가 바뀌지는 않습니다.</p>
	 * 
	 * @param name : 타입 이름
	 */
	public void setType(String name)
	{
		try
		{
			int values = Integer.parseInt(name.trim());
			setType(values);
			return;
		}
		catch(Throwable e)
		{
			
		}
		if(name.equalsIgnoreCase("BLANK"))
		{
			this.type = TYPE_BLANK;
		}
		else if(name.equalsIgnoreCase("BOOLEAN"))
		{
			this.type = TYPE_BOOLEAN;
		}
		else if(name.equalsIgnoreCase("DATE"))
		{
			this.type = TYPE_DATE;
		}
		else if(name.equalsIgnoreCase("ERROR"))
		{
			this.type = TYPE_ERROR;
		}
		else if(name.equalsIgnoreCase("FORMULA"))
		{
			this.type = TYPE_FORMULA;
		}
		else if(name.equalsIgnoreCase("NUMERIC"))
		{
			this.type = TYPE_NUMERIC;
		}
		else if(name.equalsIgnoreCase("INTEGER"))
		{
			this.type = TYPE_INTEGER;
		}
		else if(name.equalsIgnoreCase("FLOAT"))
		{
			this.type = TYPE_FLOAT;
		}
		else if(name.equalsIgnoreCase("OBJECT"))
		{
			this.type = TYPE_OBJECT;
		}
		else if(name.equalsIgnoreCase("BLOB"))
		{
			this.type = TYPE_BLOB;
		}
		else if(name.equalsIgnoreCase("STRING"))
		{
			this.type = TYPE_STRING;
		}
	}
	/**
	 * <p>컬럼의 데이터들 중 크기가 가장 큰 데이터의 크기를 반환합니다.</p>
	 * 
	 * @return 가장 큰 데이터의 크기
	 */
	public int getBiggestDataLength()
	{
		int results = 0;
		if(getName() != null)
		{
			results = getName().length();
		}
		for(int i=0; i<data.size(); i++)
		{
			if(data.get(i) != null)
			{
				if(results < data.get(i).length()) results = data.get(i).length();
			}
			else
			{
				if(results < 4) results = 4;
			}
		}
		return results;
	}
	/**
	 * <p>컬럼의 데이터들 중 크기가 가장 큰 데이터 값을 반환합니다.</p>
	 * 
	 * @return 가장 큰 데이터
	 */
	public String getBiggestData()
	{
		int indexOfBiggest = -1;
		int sizes = 0;
		for(int i=0; i<data.size(); i++)
		{
			if(data.get(i) != null)
			{
				if(sizes < data.get(i).length())
				{
					sizes = data.get(i).length();
					indexOfBiggest = i;
				}
			}
		}
		if(indexOfBiggest >= 0) return data.get(indexOfBiggest);
		else return null;
	}
	@Override
	public String toString()
	{
		StringBuffer results = new StringBuffer("");
		
		results = results.append("{");
		results = results.append("   name:\"" + DataUtil.castQuote(true, getName()) + "\"");
		results = results.append("   type:\"" + getType() + "\"");
		results = results.append("}");
		
		return results.toString();
	}
	@Override
	public String help()
	{
		StringBuffer results = new StringBuffer("");
		results = results.append(Manager.applyStringTable("This is column object."));
		results = results.append("getData(i)" + " : " + Manager.applyStringTable("Return i'th data of this column."));
		results = results.append("setData(i, text)" + " : " + Manager.applyStringTable("Replace i'th data to the other text."));
		results = results.append("type()" + " : " + Manager.applyStringTable("Return this column's type name."));
		results = results.append("setType(typename)" + " : " + Manager.applyStringTable("Modify type of this column at force."));
		return results.toString();
	}
	
	/**
	 * <p>어떤 값이 데이터에 포함되어 있는지 여부를 반환합니다.</p>
	 * 
	 * @param value : 값
	 * @return 포함 여부
	 */
	public boolean contains(String value)
	{
		return getData().contains(value);
	}
	
	/**
	 * <p>전체 데이터들 안에 특정 내용이 얼마나 포함되어 있는지를 반환합니다. 찾을 내용이 빈 칸이면 -1을 반환합니다.</p>
	 * 
	 * @param finds : 찾을 내용
	 * @return 포함 횟수
	 */
	public int has(String finds)
	{
		int results = 0;
		int values = 0;
		
		if(finds.equals("")) return -1;
		
		for(String s : getData())
		{
			values = DataUtil.has(s, finds);
			results = results + values;
		}
		return results;
	}
	
	/**
	 * <p>중복을 제거한 새 컬럼을 반환합니다.</p> 
	 * 
	 * @return 중복 제거된 컬럼
	 */
	public Column distincts()
	{
		Column results = new Column();
		results.setName("distinct of " + getName());
		results.setType(getType());
		
		for(String s : getData())
		{
			if(! (results.contains(s)))
			{
				results.getData().add(s);
			}
		}
		
		return results;
	}
	
	/**
	 * <p>상관계수를 구합니다. 두 컬럼의 데이터 수가 다르면 둘 중 데이터 수가 적은 컬럼 갯수를 사용하므로 데이터 수가 더 많은 컬럼의 데이터 일부를 사용하지 않고 계산합니다.</p>
	 * <p>정수, 혹은 실수 관련 데이터가 아니라면 오류가 발생합니다.</p>
	 * <p>데이터 수가 2 이상이어야 제대로 된 값이 반환됩니다. 1 이하이면 null 을 반환합니다.</p>
	 * 
	 * @param another : 다른 컬럼
	 * @param scale : 소수 자리수
	 * @return 상관 계수값
	 */
	public BigDecimal correlation(Column another, int scale)
	{
		int minimals = getData().size();
		if(minimals > another.getData().size()) minimals = another.getData().size();
		
		if(minimals <= 1) return null;
		
		BigDecimal upValue, upX, upY, downX, downY, avX, avY;
		
		upValue = new BigDecimal("0.0");
		upX = new BigDecimal("0.0");
		upY = new BigDecimal("0.0");
		downX = new BigDecimal("0.0");
		downY = new BigDecimal("0.0");
		avX = average(scale);
		avY = another.average(scale);
		
		for(int i=0; i<minimals; i++)
		{
			upX = new BigDecimal(getData().get(i)).subtract(avX);
			upY = new BigDecimal(another.getData().get(i)).subtract(avY);
			upValue = upValue.add(upX.multiply(upY));
			
			downX = downX.add(new BigDecimal(getData().get(i)).subtract(avX).pow(2));
			downY = downY.add(new BigDecimal(another.getData().get(i)).subtract(avY).pow(2));
		}
		
		return upValue.divide(DataUtil.sqrt(downX.multiply(downY), scale), scale, BigDecimal.ROUND_HALF_UP);
	}
	
	/**
	 * <p>공분산을 구합니다. 두 컬럼의 데이터 수가 다르면 둘 중 데이터 수가 적은 컬럼 갯수를 사용하므로 데이터 수가 더 많은 컬럼의 데이터 일부를 사용하지 않고 계산합니다.</p>
	 * <p>정수, 혹은 실수 관련 데이터가 아니라면 오류가 발생합니다.</p>
	 * <p>데이터 수가 2 이상이어야 제대로 된 값이 반환됩니다. 1 이하이면 null 을 반환합니다.</p>
	 * 
	 * @param another : 다른 컬럼
	 * @param scale : 소수 자리수
	 * @return 공분산 값
	 */
	public BigDecimal covariance(Column another, int scale)
	{
		int minimals = getData().size();
		if(minimals > another.getData().size()) minimals = another.getData().size();
		
		if(minimals <= 1) return null;
		
		BigDecimal x = new BigDecimal("0.0");
		BigDecimal y = new BigDecimal("0.0");
		
		BigDecimal sums = new BigDecimal("0.0");
		
		BigDecimal averageOfX = average(scale);
		BigDecimal averageOfY = another.average(scale);
		
		for(int i=0; i<minimals; i++)
		{
			x = new BigDecimal(getData().get(i)).subtract(averageOfX);
			y = new BigDecimal(another.getData().get(i)).subtract(averageOfY);
			
			sums = sums.add(x.multiply(y));
		}
		
		return sums.divide(new BigDecimal((double) minimals), scale, BigDecimal.ROUND_HALF_UP);
	}
	
	/**
	 * <p>표준 편차를 구합니다. 정수, 혹은 실수 관련 데이터가 아니라면 오류가 발생합니다.</p>
	 * 
	 * @param scale : 소수 자리수
	 * @return 표준 편차
	 */
	public BigDecimal stdev(int scale)
	{
		return DataUtil.sqrt(variance(scale), scale);
	}
	
	/**
	 * <p>분산을 구합니다. 정수, 혹은 실수 관련 데이터가 아니라면 오류가 발생합니다.</p>
	 * <p>데이터 수가 2 이상이어야 유효한 값이 나옵니다.</p>
	 * 
	 * @param scale : 소수 자리수
	 * @return 분산 값
	 */
	public BigDecimal variance(int scale)
	{
		BigDecimal av = average(scale);
		BigDecimal x = new BigDecimal("0.0");
		
		if(getData().size() == 0) return null;
		if(getData().size() == 1) return new BigDecimal(String.valueOf(0.0));
		
		for(int i=0; i<getData().size(); i++)
		{
			x = x.add((new BigDecimal(getData().get(i)).subtract(av)).pow(2));
		}
		return x.divide(new BigDecimal(String.valueOf((double) (getData().size() - 1))));
	}
	
	/**
	 * <p>데이터들의 산술 평균을 구합니다. 정수, 혹은 실수 관련 데이터가 아니라면 오류가 발생합니다.</p>
	 * 
	 * @param scale : 소수 자리수
	 * @return 산술 평균값
	 */
	public BigDecimal average(int scale)
	{
		return (sum().divide(new BigDecimal(String.valueOf((double) getData().size())), scale, BigDecimal.ROUND_HALF_UP));
	}
	
	/**
	 * <p>데이터들의 합계를 구해 반환합니다. 정수, 혹은 실수 관련 데이터가 아니라면 오류가 발생합니다.</p>
	 * 
	 * @return 데이터들의 합계
	 */
	public BigDecimal sum()
	{
		BigDecimal values = new BigDecimal("0.0");
		for(String s : getData())
		{
			values = values.add(new BigDecimal(s));
		}
		return values;
	}
	
	/**
	 * <p>상관계수를 구합니다. 두 컬럼의 데이터 수가 다르면 둘 중 데이터 수가 적은 컬럼 갯수를 사용하므로 데이터 수가 더 많은 컬럼의 데이터 일부를 사용하지 않고 계산합니다.</p>
	 * <p>정수, 혹은 실수 관련 데이터가 아니라면 오류가 발생합니다.</p>
	 * <p>데이터 수가 2 이상이어야 제대로 된 값이 반환됩니다. 1 이하이면 0 을 반환합니다.</p>
	 * <p>Java 의 기본 자료형 double 을 사용하므로, 빠르지만 최대 크기와 정확도 제한이 있습니다.</p>
	 * 
	 * @param another : 다른 컬럼
	 * @return 상관계수 값
	 */
	public double lmCorrelation(Column another)
	{
		int minimals = getData().size();
		if(minimals > another.getData().size()) minimals = another.getData().size();
		
		if(minimals <= 1) return 0.0;
		
		double upValue, downX, downY, avX, avY;
		
		avX = lmAverage();
		avY = another.lmAverage();
		upValue = 0.0;
		downX = 0.0;
		downY = 0.0;
		
		for(int i=0; i<minimals; i++)
		{
			upValue = upValue + ((Double.parseDouble(getData().get(i)) - avX) * (Double.parseDouble(another.getData().get(i)) - avY));
			downX = downX + Math.pow(Double.parseDouble(getData().get(i)) - avX, 2.0);
			downY = downY + Math.pow(Double.parseDouble(another.getData().get(i)) - avY, 2.0);
		}
		
		return (upValue / Math.sqrt(downX * downY));
	}
	
	/**
	 * <p>공분산을 구합니다. 두 컬럼의 데이터 수가 다르면 둘 중 데이터 수가 적은 컬럼 갯수를 사용하므로 데이터 수가 더 많은 컬럼의 데이터 일부를 사용하지 않고 계산합니다.</p>
	 * <p>정수, 혹은 실수 관련 데이터가 아니라면 오류가 발생합니다.</p>
	 * <p>데이터 수가 2 이상이어야 제대로 된 값이 반환됩니다. 1 이하이면 0 을 반환합니다.</p>
	 * <p>Java 의 기본 자료형 double 을 사용하므로, 빠르지만 최대 크기와 정확도 제한이 있습니다.</p>
	 * 
	 * @param another : 다른 컬럼
	 * @return 공분산 값
	 */
	public double lmCovariance(Column another)
	{
		int minimals = getData().size();
		if(minimals > another.getData().size()) minimals = another.getData().size();
		
		if(minimals <= 1) return 0.0;
		
		double x = 0.0;
		double y = 0.0;
		
		double sums = 0.0;
		
		double averageOfX = lmAverage();
		double averageOfY = another.lmAverage();
		
		for(int i=0; i<minimals; i++)
		{	
			x = Double.parseDouble(getData().get(i)) - averageOfX;
			y = Double.parseDouble(another.getData().get(i)) - averageOfY;
			
			sums = sums + (x * y);
		}
		
		return sums / minimals;
	}
	
	/**
	 * <p>표준 편차를 구합니다. 정수, 혹은 실수 관련 데이터가 아니라면 오류가 발생합니다.</p>
	 * <p>Java 의 기본 자료형 double 을 사용하므로, 빠르지만 최대 크기와 정확도 제한이 있습니다.</p>
	 * 
	 * @param scale : 소수 자리수
	 * @return 표준 편차
	 */
	public double lmStdev(int scale)
	{
		return Math.sqrt(lmVariance());
	}
	
	/**
	 * <p>분산을 구합니다. 정수, 혹은 실수 관련 데이터가 아니라면 오류가 발생합니다.</p>
	 * <p>데이터 수가 2 이상이어야 유효한 값이 나옵니다.</p>
	 * <p>Java 의 기본 자료형 double 을 사용하므로, 빠르지만 최대 크기와 정확도 제한이 있습니다.</p>
	 * 
	 * @return 분산 값
	 */
	public double lmVariance()
	{
		double values = 0.0;
		double av = lmAverage();
		
		if(getData().size() == 0) return -1.0;
		if(getData().size() == 1) return 0.0;
		
		for(int i=0; i<getData().size(); i++)
		{
			values = values + Math.pow(Double.parseDouble(getData().get(i)) - av, 2.0);
		}
		return values / (getData().size() - 1);
	}
	
	/**
	 * <p>데이터들의 산술 평균을 구합니다. 정수, 혹은 실수 관련 데이터가 아니라면 오류가 발생합니다.</p>
	 * <p>Java 의 기본 자료형 double 을 사용하므로, 빠르지만 최대 크기와 정확도 제한이 있습니다.</p>
	 * 
	 * @return 산술 평균값
	 */
	public double lmAverage()
	{
		return lmSum() / ((double) getData().size());
	}
	
	/**
	 * <p>데이터들의 합계를 구해 반환합니다. 정수, 혹은 실수 관련 데이터가 아니라면 오류가 발생합니다.</p>
	 * <p>Java 의 기본 자료형 double 을 사용하므로, 빠르지만 최대 크기와 정확도 제한이 있습니다.</p>
	 * 
	 * @return 데이터들의 합계
	 */
	public double lmSum()
	{
		double values = 0;
		for(String s : getData())
		{
			values = values + Double.parseDouble(s);
		}
		return values;
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
	/**
	 * <p>컬럼을 분석합니다. 입력한 함수 이름에 맞게 해당 분석 함수를 실행해 줍니다.</p>
	 * 
	 * @param analyzeFunction : 분석 함수 이름
	 * @param arguments : 매개 변수들
	 * @return 분석 결과 (테이블 셋 객체 형태)
	 */
	public TableSet analyze(String analyzeFunction, Map<String, String> arguments)
	{
		return AnalyzeUtil.analyze(analyzeFunction, arguments, this);
	}
	/**
	 * <p>컬럼을 분석합니다. 입력한 함수 이름에 맞게 해당 분석 함수를 실행해 줍니다.</p>
	 * 
	 * @param analyzeFunction : 분석 함수 이름
	 * @param arguments : 매개 변수들
	 * @param otherColumns : 다른 컬럼들 (필요 시)
	 * @return 분석 결과 (테이블 셋 객체 형태)
	 */
	public TableSet analyze(String analyzeFunction, Map<String, String> arguments, Column ... otherColumns)
	{
		List<Column> otherColumnList = new Vector<Column>();
		otherColumnList.add(this);
		if(otherColumns != null)
		{
			for(Column c : otherColumns)
			{
				otherColumnList.add(c);
			}
		}
		return AnalyzeUtil.analyze(analyzeFunction, arguments, otherColumnList.toArray(new Column[otherColumnList.size()]));
	}
	
	/**
	 * <p>데이터들을 정렬합니다. 역순 정렬은 지원되지 않습니다.</p>
	 * 
	 */
	public void sort()
	{
		Collections.sort(data);
	}
}
