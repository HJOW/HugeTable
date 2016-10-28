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

import hjow.hgtable.Main;
import hjow.hgtable.Manager;
import hjow.hgtable.tableset.Column;
import hjow.hgtable.tableset.DefaultTableSet;
import hjow.hgtable.tableset.Record;
import hjow.hgtable.tableset.TableSet;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.commons.codec.binary.Base64;

/**
 * <p>데이터 타입 변환에 관련된 여러 정적 메소드들이 있는 클래스입니다.</p>
 * 
 * @author HJOW
 *
 */
public class DataUtil
{
	/**
	 * <p>객체가 비었는지 여부를 반환합니다. null 이면 true 가 반환되고, 빈 텍스트인 경우도 true 가 반환됩니다.</p>
	 * 
	 * @param ob : 검사할 텍스트
	 * @return 비었는지의 여부
	 */
	public static boolean isEmpty(Object ob)
	{
		if(ob == null) return true;
		if(ob instanceof String)
		{
			if(((String) ob).trim().equals("")) return true;
			else if(((String) ob).trim().equals("null")) return true;
			else return false;
		}
		else if(ob instanceof List<?>)
		{
			return ((List<?>) ob).isEmpty();
		}
		else if(ob instanceof Map<?, ?>)
		{
			return ((Map<?, ?>) ob).isEmpty();
		}
		else if(String.valueOf(ob).trim().equals("null")) return true;
		else if(String.valueOf(ob).trim().equals("")) return true;
		else return isEmpty(String.valueOf(ob));
	}
	
	/**
	 * <p>객체들이 모두 비어 있는지 여부를 반환합니다. 모두 빈 객체여야 true 가 반환됩니다.</p>
	 * 
	 * @param obs : 객체들
	 * @return 비어 있는지 여부
	 */
	public static boolean isEmpties(Object ... obs)
	{
		for(Object o : obs)
		{
			if(isNotEmpty(o))
			{
				return false;
			}
		}
		return true;
	}
	
	/**
	 * <p>객체가 비었는지 여부를 반환합니다. null 이면 false 가 반환되고, 빈 텍스트인 경우도 false 가 반환됩니다.</p>
	 * 
	 * @param ob : 검사할 텍스트
	 * @return 비었는지의 여부
	 */
	public static boolean isNotEmpty(Object ob)
	{
		return ! isEmpty(ob);
	}
	
	/**
	 * <p>객체들이 모두 비어 있지 않은지 여부를 반환합니다. 모두 비어 있지 않은 객체여야 true 가 반환됩니다.</p>
	 * 
	 * @param obs : 객체들
	 * @return 비어 있지 않은지 여부
	 */
	public static boolean isNotEmpties(Object ... obs)
	{
		for(Object o : obs)
		{
			if(isEmpty(o))
			{
				return false;
			}
		}
		return true;
	}
	
	/**
	 * <p>어떤 객체가 정수 값으로 변환이 가능한지 여부를 반환합니다.</p>
	 * 
	 * @param ob : 검사할 객체
	 * @return : 정수 변환 가능 여부
	 */
	public static boolean isInteger(Object ob)
	{
		if(ob instanceof BigInteger) return true;
		if(ob instanceof Integer) return true;
		if(ob instanceof Long) return true;
		if(ob instanceof String)
		{
			try
			{
				Long.parseLong((String) ob);
				return true;
			}
			catch(NumberFormatException e)
			{
				return false;
			}
		}
		else return false;
	}
	
	/**
	 * <p>어떤 객체가 실수 값으로 변환이 가능한지 여부를 반환합니다.</p>
	 * 
	 * @param ob : 검사할 객체
	 * @return : 실수 변환 가능 여부
	 */
	public static boolean isFloat(Object ob)
	{
		if(ob instanceof BigDecimal) return true;
		if(ob instanceof Float) return true;
		if(ob instanceof Double) return true;
		if(ob instanceof String)
		{
			try
			{
				Double.parseDouble((String) ob);
				return true;
			}
			catch(NumberFormatException e)
			{
				return false;
			}
		}
		else return false;
	}
	
	/**
	 * <p>어떤 객체가 정수, 혹은 실수 값인지 여부를 반환합니다.</p>
	 * 
	 * @param ob : 검사할 객체
	 * @return 숫자 데이터 여부
	 */
	public static boolean isNumber(Object ob)
	{
		return isInteger(ob) || isFloat(ob);
	}
	
	
	/**
	 * <p>객체를 boolean 타입으로 반환합니다. 사용자가 y/n 입력을 한 경우 그에 해당하는 true, false 논리값으로 변환하는 데 사용됩니다.</p>
	 * 
	 * @param ob : 객체
	 * @return 논리값
	 * @throws InvalidInputException : 논리값과 전혀 상관이 없는 객체가 매개변수로 들어온 문제
	 */
	public static boolean parseBoolean(Object ob) throws InvalidInputException
	{
		if(ob == null) return false;
		if(isEmpty(ob)) return false;
		if(ob instanceof String)
		{
			String target = ((String) ob).trim();
			if(target.equalsIgnoreCase("t") || target.equalsIgnoreCase("true")
					|| target.equalsIgnoreCase("y") || target.equalsIgnoreCase("yes")) return true;
			else return false;
		}
		else if(ob instanceof Integer)
		{
			if(((Integer) ob).intValue() == 0) return false;
			else return true;
		}
		else if(ob instanceof BigInteger)
		{
			if(((Integer) ob).intValue() == 0) return false;
			else return true;
		}
		else if(ob instanceof Boolean)
		{
			return ((Boolean) ob).booleanValue();
		}
		throw new InvalidInputException(Manager.applyStringTable("Invalid inputs") + " : " + String.valueOf(ob));
	}
	
	/**
	 * <p>배열을 리스트 객체로 변환합니다.</p>
	 * 
	 * @param newList : 리스트 객체, 주로 새 객체를 만들어 넣습니다. 이전에 쓰던 객체를 넣으면 배열 내용이 객체에 추가됩니다.
	 * @return 리스트 객체
	 */
	public static <T> List<T> arrayToList(T[] obj, List<T> newList)
	{
		if(obj == null) return null;
		List<T> lists = newList;
		for(int i=0; i<obj.length; i++)
		{
			lists.add(obj[i]);
		}
		return lists;
	}
	
	/**
	 * <p>리스트 둘을 병합한 새 리스트를 반환합니다. 이 과정에서 둘 사이에 중복 원소가 있는지 검사합니다.</p>
	 * 
	 * @param one : 리스트
	 * @param two : 또 다른 리스트
	 * @return 병합된 리스트
	 */
	private static <T> List<T> mergeList(List<T> one, List<T> two)
	{
		if(one == null) return two;
		if(two == null) return one;
		if(one.size() <= 0) return two;
		if(two.size() <= 0) return one;
		
		List<T> newList = new Vector<T>();
		
		boolean isEquals = true;
		for(int i=0; i<one.size(); i++)
		{
			newList.add(one.get(i));
		}
		for(int i=0; i<two.size(); i++)
		{
			isEquals = true;
			for(int j=0; j<newList.size(); j++)
			{
				if(! (two.get(i).equals(newList.get(j))))
				{
					isEquals = false;
				}
			}
			if(! isEquals)
			{
				newList.add(two.get(i));
			}
		}
		return newList;
	}
	
	/**
	 * <p>여러 리스트들을 병합한 새 리스트를 반환합니다. 이 과정에서 둘 사이에 중복 원소가 있는지 검사합니다.</p>
	 * <p>비교 과정에서 equals(others) 메소드를 사용하기 때문에, 비교해야 할 객체들은 이 메소드를 가지고 있어야 합니다.</p>
	 * 
	 * @param lists : 리스트들
	 * @return 병합된 새 리스트
	 */
	public static <T> List<T> merge(List<T> ... lists)
	{
		List<List<T>> targets = new Vector<List<T>>();
		for(List<T> othersList : lists)
		{
			targets.add(othersList);
		}
		
		List<T> results = new Vector<T>();
		for(int i=0; i<targets.size(); i++)
		{
			results = mergeList(results, targets.get(i));
		}
		
		return results;
	}
	
	/**
	 * <p>따옴표 앞에 \ 기호를 붙입니다. (캐스팅)</p>
	 * 
	 * @param isDoubleQuote : 이 값이 true 이면 쌍따옴표 앞에만 \ 기호를 붙이고, 그 외에는 일반 따옴표 앞에만 \ 기호를 붙입니다.
	 * @param target : 대상 텍스트
	 * @return \ 처리된 텍스트
	 */
	public static String castQuote(boolean isDoubleQuote, String target)
	{
		if(isDoubleQuote)
		{
			return target.replace("\"", "\\" + "\"");
		}
		else
		{
			return target.replace("'", "\'");
		}
	}
	
	/**
	 * <p>\ 처리된 따옴표들을 찾아 원래의 따옴표로 돌려 놓습니다. (캐스팅 해제)</p>
	 * 
	 * @param isDoubleQuote : 이 값이 true 이면 \ 처리된 쌍따옴표들을 찾아 원래의 쌍따옴표로 바꾸고, false 이면 일반 따옴표들을 찾아 원래의 따옴표로 바꿉니다.
	 * @param target : 대상 텍스트
	 * @return \ 가 제거된 텍스트
	 */
	public static String reCastQuote(boolean isDoubleQuote, String target)
	{
		/*
		if(isDoubleQuote)
		{
			return target.replace("\\" + "\"", "\"");
		}
		else
		{
			return target.replace("\'", "'");
		}
		*/
		char[] chars = target.toCharArray();
		StringBuffer results = new StringBuffer("");
		boolean useDefault = true;
		for(int i=0; i<chars.length; i++)
		{
			useDefault = true;
			if(isDoubleQuote)
			{
				if(i >= 3)
				{
					useDefault = false;
					if((chars[i - 2] == '\\') && chars[i - 1] == '\\' && chars[i] == '"')
					{
						results = results.append(String.valueOf('"'));
					}
					else if((chars[i - 2] != '\\') && chars[i - 1] == '\\' && chars[i] == '"')
					{
						results = results.append(String.valueOf('"'));
					}
					else if(chars[i - 1] == '\\' && chars[i] == '\\')
					{
						results = results.append(String.valueOf('\\'));
					}
					else if(chars[i] == '\\')
					{
						continue;
					}
					else
					{
						useDefault = true;
					}
				}
				else if(i >= 2)
				{
					useDefault = false;
					if(chars[i - 1] == '\\' && chars[i] == '"')
					{
						results = results.append(String.valueOf('"'));
					}
					else if(chars[i - 1] == '\\' && chars[i] == '\\')
					{
						results = results.append(String.valueOf('\\'));
					}
					else if(chars[i] == '\\')
					{
						continue;
					}
					else useDefault = true;
				}
				else
				{
					useDefault = true;
				}
			}
			else
			{
				if(i >= 3)
				{
					useDefault = false;
					if((chars[i - 2] == '\\') && chars[i - 1] == '\\' && chars[i] == '\'')
					{
						results = results.append(String.valueOf('\''));
					}
					else if((chars[i - 2] != '\\') && chars[i - 1] == '\\' && chars[i] == '\'')
					{
						results = results.append(String.valueOf('\''));
					}
					else if(chars[i - 1] == '\\' && chars[i] == '\\')
					{
						results = results.append(String.valueOf('\\'));
					}
					else if(chars[i] == '\\')
					{
						continue;
					}
					else
					{
						useDefault = true;
					}
				}
				else if(i >= 2)
				{
					useDefault = false;
					if(chars[i - 1] == '\\' && chars[i] == '\'')
					{
						results = results.append(String.valueOf('\''));
					}
					else if(chars[i - 1] == '\\' && chars[i] == '\\')
					{
						results = results.append(String.valueOf('\\'));
					}
					else if(chars[i] == '\\')
					{
						continue;
					}
					else useDefault = true;
				}
				else
				{
					useDefault = true;
				}
			}
			
			if(useDefault)
			{
				results = results.append(chars[i]);
			}
		}
		return results.toString();
	}
	
	/**
	 * <p>텍스트 앞뒤에 따옴표를 붙입니다. 그 안의 내용은 캐스팅 처리합니다.</p>
	 * 
	 * @param contents : 대상 텍스트
	 * @param needDouble : true 시 쌍따옴표 사용
	 * @return 따옴표로 포장된 텍스트
	 */
	public static String putQuote(String contents, boolean needDouble)
	{
		if(needDouble) return "\"" + castQuote(true, contents) + "\"";
		else return "'" + castQuote(false, contents) + "'";
	}
	
	/**
	 * <p>텍스트 앞뒤에서 따옴표를 제거하고, 그 안의 내용을 캐스팅 해제합니다. 대상 텍스트가 따옴표로 시작하고 끝나야만 적용이 가능합니다.</p>
	 * 
	 * @param contents : 대상 텍스트
	 * @return 따옴표 제거된 원본 텍스트
	 */
	public static String removeQuote(String contents)
	{
		String target = contents.trim();
		if(target.startsWith("\"") && target.endsWith("\""))
		{
			target = target.substring(1, target.length() - 1);
			target = reCastQuote(true, target);
		}
		else if(target.startsWith("'") && target.endsWith("'"))
		{
			target = target.substring(1, target.length() - 1);
			target = reCastQuote(false, target);
		}
		return target;
	}
	
	/**
	 * <p>줄 띄기, 탭, 따옴표 앞에 \를 붙입니다. 여러 줄 텍스트는 한 줄로 변환되고, 줄 띄는 자리에는 대신 \n이, 탭 자리에는 \t가 오게 됩니다.</p>
	 * 
	 * @param isDoubleQuote : 이 값이 true 이면 쌍따옴표 앞에만 \ 기호를 붙이고, 그 외에는 일반 따옴표 앞에만 \ 기호를 붙입니다.
	 * @param target : 대상 텍스트
	 * @return \ 처리된 텍스트
	 */
	public static String castTotal(boolean isDoubleQuote, String target)
	{	
		return castQuote(isDoubleQuote, target.replace("\n", "\\n").replace("\t", "\\t"));
	}
	
	/**
	 * <p>\n, \t, \" 를 찾아 \ 기호를 제거합니다. \n은 줄 띄기 기호로 변환되고, \t는 탭 공백으로 변환됩니다.</p>
	 * 
	 * @param isDoubleQuote : 이 값이 true 이면 \ 처리된 쌍따옴표들을 찾아 원래의 쌍따옴표로 바꾸고, false 이면 일반 따옴표들을 찾아 원래의 따옴표로 바꿉니다.
	 * @param target : 대상 텍스트
	 * @return \ 가 제거된 텍스트
	 */
	public static String reCastTotal(boolean isDoubleQuote, String target)
	{
		// return reCastQuote(isDoubleQuote, target.replace("\\n", "\n").replace("\\t", "\t"));
		
		char[] chars = target.toCharArray();
		StringBuffer results = new StringBuffer("");
		boolean useDefault = true;
		for(int i=0; i<chars.length; i++)
		{
			useDefault = true;
			if(isDoubleQuote)
			{
				if(i >= 3)
				{
					useDefault = false;
					if((chars[i - 2] == '\\') && chars[i - 1] == '\\' && chars[i] == '"')
					{
						results = results.append(String.valueOf('"'));
					}
					else if((chars[i - 2] != '\\') && chars[i - 1] == '\\' && chars[i] == '"')
					{
						results = results.append(String.valueOf('"'));
					}
					else if((chars[i - 2] == '\\') && chars[i - 1] == '\\' && chars[i] == 'n')
					{
						results = results.append(String.valueOf('n'));
					}
					else if((chars[i - 2] != '\\') && chars[i - 1] == '\\' && chars[i] == 'n')
					{
						results = results.append(String.valueOf('\n'));
					}
					else if((chars[i - 2] == '\\') && chars[i - 1] == '\\' && chars[i] == 't')
					{
						results = results.append(String.valueOf('t'));
					}
					else if((chars[i - 2] != '\\') && chars[i - 1] == '\\' && chars[i] == 't')
					{
						results = results.append(String.valueOf('\t'));
					}
					else if(chars[i - 1] == '\\' && chars[i] == '\\')
					{
						results = results.append(String.valueOf('\\'));
					}
					else if(chars[i] == '\\')
					{
						continue;
					}
					else
					{
						useDefault = true;
					}
				}
				else if(i >= 2)
				{
					useDefault = false;
					if(chars[i - 1] == '\\' && chars[i] == '"')
					{
						results = results.append(String.valueOf('"'));
					}
					else if(chars[i - 1] == '\\' && chars[i] == '\\')
					{
						results = results.append(String.valueOf('\\'));
					}
					else if(chars[i - 1] == '\\' && chars[i] == 'n')
					{
						results = results.append(String.valueOf('\n'));
					}
					else if(chars[i - 1] == '\\' && chars[i] == 't')
					{
						results = results.append(String.valueOf('\t'));
					}
					else if(chars[i] == '\\')
					{
						continue;
					}
					else useDefault = true;
				}
				else
				{
					useDefault = true;
				}
			}
			else
			{
				if(i >= 3)
				{
					useDefault = false;
					if((chars[i - 2] == '\\') && chars[i - 1] == '\\' && chars[i] == '\'')
					{
						results = results.append(String.valueOf("\\'"));
					}
					else if((chars[i - 2] != '\\') && chars[i - 1] == '\\' && chars[i] == '\'')
					{
						results = results.append(String.valueOf('\''));
					}
					else if((chars[i - 2] == '\\') && chars[i - 1] == '\\' && chars[i] == 'n')
					{
						results = results.append(String.valueOf('n'));
					}
					else if((chars[i - 2] != '\\') && chars[i - 1] == '\\' && chars[i] == 'n')
					{
						results = results.append(String.valueOf('\n'));
					}
					else if((chars[i - 2] == '\\') && chars[i - 1] == '\\' && chars[i] == 't')
					{
						results = results.append(String.valueOf('t'));
					}
					else if((chars[i - 2] != '\\') && chars[i - 1] == '\\' && chars[i] == 't')
					{
						results = results.append(String.valueOf('\t'));
					}
					else if(chars[i - 1] == '\\' && chars[i] == '\\')
					{
						results = results.append(String.valueOf('\\'));
					}
					else if(chars[i] == '\\')
					{
						continue;
					}
					else
					{
						useDefault = true;
					}
				}
				else if(i >= 2)
				{
					useDefault = false;
					if(chars[i - 1] == '\\' && chars[i] == '\'')
					{
						results = results.append(String.valueOf('\''));
					}
					else if(chars[i - 1] == '\\' && chars[i] == '\\')
					{
						results = results.append(String.valueOf('\\'));
					}
					else if(chars[i - 1] == '\\' && chars[i] == 'n')
					{
						results = results.append(String.valueOf('\n'));
					}
					else if(chars[i - 1] == '\\' && chars[i] == 't')
					{
						results = results.append(String.valueOf('\t'));
					}
					else if(chars[i] == '\\')
					{
						continue;
					}
					else useDefault = true;
				}
				else
				{
					useDefault = true;
				}
			}
			
			if(useDefault)
			{
				results = results.append(chars[i]);
			}
		}
		return results.toString();
	}
	
	/**
	 * <p>HGF 형식의 텍스트를 테이블 셋으로 변환합니다.</p>
	 * 
	 * @param hgf : HGF 텍스트
	 * @return 테이블 셋 객체
	 */
	public static TableSet toTableSet(String hgf)
	{
		TableSet tableSet = new DefaultTableSet();
		
		StringTokenizer lineToken = new StringTokenizer(hgf, "\n");
		String lines;
		
		String data = "";
		String selectedColumn = null;
		char inQuote = ' ';
		Column newColumn;
		
		while(lineToken.hasMoreTokens())
		{
			lines = lineToken.nextToken().trim(); // 한 줄
			
			if(lines.startsWith("#")) continue; // # 기호는 주석 --> # 으로 시작하는 줄은 처리하지 않음
			if(lines.startsWith("@")) // @ 으로 시작하는 줄은 테이블 이름을 지정
			{
				String name = lines.substring(1);
				if(name.startsWith("\""))
				{
					name = DataUtil.reCastTotal(true, name.substring(1, name.length() - 1));
				}
				else if(name.startsWith("'"))
				{
					name = DataUtil.reCastTotal(false, name.substring(1, name.length() - 1));
				}
				
				tableSet.setName(name);
			}
			else if(lines.startsWith("%")) // % 으로 시작하는 줄은 컬럼 지정, 컬럼명:타입명;컬럼명:타입명 형태로 지정
			{
				String columns = lines.substring(1).trim();
				char[] columnsChar = columns.toCharArray();
				
				for(int i=0; i<columnsChar.length; i++)
				{					
					if(inQuote == '"') // 따옴표 안에 있는 경우
					{
						if(columnsChar[i] == '"') // 따옴표를 만나면
						{
							inQuote = ' '; // 따옴표 밖으로 벗어남
							if(i == columnsChar.length - 1) // 마지막 글자인 경우
							{
								if(selectedColumn != null) // 컬럼 이름이 선택된 경우 --> 마지막 글자이므로 새 컬럼 추가
								{
									newColumn = new Column();
									newColumn.setName(selectedColumn);
									newColumn.setType(DataUtil.reCastTotal(true, data));
									tableSet.getColumns().add(newColumn);
									data = "";
									selectedColumn = null;
								}
							}
						}
						else // 그 외의 글자를 만나면
						{
							data = data + String.valueOf(columnsChar[i]);
						}
					}
					else // 따옴표 바깥에 있는 경우
					{
						if(columnsChar[i] == ' ') continue; // 공백이면 넘어감
						else if(columnsChar[i] == '"') // 따옴표를 만난 경우
						{
							inQuote = '"'; // 따옴표 안으로 진입
							data = "";
						}
						else if(columnsChar[i] == ':') // : 기호를 만나면, 이전까지 만난 글자들을 컬럼 이름으로 선택
						{
							selectedColumn = DataUtil.reCastTotal(true, data);
							data = "";
						}
						else if(columnsChar[i] == ';') // ; 기호를 만나면, 선택된 컬럼 이름에 이전까지 만난 글자들을 데이터 타입으로 하여 새 컬럼으로 추가
						{
							if(selectedColumn != null)
							{
								newColumn = new Column();
								newColumn.setName(selectedColumn);
								newColumn.setType(DataUtil.reCastTotal(true, data));
								tableSet.getColumns().add(newColumn);								
								data = "";
								selectedColumn = null;
							}
						}
						else // 그 외의 글자를 만나면
						{
							data = data + String.valueOf(columnsChar[i]);
						}
					}
				}
			}
			else if(lines.startsWith("$")) // $ 으로 시작하는 줄은 레코드, 한 줄은 하나의 레코드, ; 기호로 구분
			{
				String recordData = lines.substring(1).trim();
				char[] recordChar = recordData.toCharArray();
				
				data = "";
				List<String> recordDatas = new Vector<String>();
				List<String> columnNames = new Vector<String>();
				List<Integer> typeDatas = new Vector<Integer>();
				
				for(int i=0; i<recordChar.length; i++)
				{
					if(inQuote == '"') // 따옴표 안에 있는 경우
					{
						if(recordChar[i] == '"')
						{
							inQuote = ' ';
							if(i == recordChar.length - 1) // 마지막 글자인 경우
							{
								recordDatas.add(DataUtil.reCastTotal(true, data));
								data = "";
							}
						}
						else
						{
							data = data + String.valueOf(recordChar[i]);
						}
					}
					else // 따옴표 바깥에 있는 경우
					{
						if(recordChar[i] == ' ') continue; // 공백이면 넘어감
						else if(recordChar[i] == '"')
						{
							inQuote = '"';
							data = "";
						}
						else if(recordChar[i] == ';')
						{
							recordDatas.add(DataUtil.reCastTotal(true, data));
							data = "";
						}
						else
						{
							data = data + String.valueOf(recordChar[i]);
						}
					}
				}
				
				Record newRecord = new Record();
								
				List<Object> newRecordData = new Vector<Object>();
				
				for(int i=0; i<recordDatas.size(); i++)
				{
					newRecordData.add(recordDatas.get(i));
				}
				
				newRecord.setDatas(newRecordData);
				
				for(int i=0; i<tableSet.getColumns().size(); i++)
				{
					columnNames.add(tableSet.getColumn(i).getName());
					typeDatas.add(new Integer(tableSet.getColumn(i).getType()));
				}
				
				newRecord.setColumnName(columnNames);
				newRecord.setTypes(typeDatas);
				
				try
				{
					tableSet.addData(newRecord);
				}
				catch (InvalidInputException e)
				{
					Main.logError(e, Manager.applyStringTable("On HGF to TableSet") + "...\n" + hgf);
					return null;
				}
			}
		}
		
		return tableSet;
	}
	
	/**
	 * <p>파일로부터 테이블 셋을 읽습니다.</p>
	 * 
	 * @param file : 파일 객체
	 * @return 테이블 셋 객체
	 * @throws Exception 파일 액세스 문제. 파일 내용의 형식 문제
	 */
	public static TableSet fromFile(File file) throws Exception
	{
		if(file == null) throw new FileNotFoundException(Manager.applyStringTable("On fromFile,") + " " 
				+ String.valueOf(file) + " " + Manager.applyStringTable("is null"));
		
		String allPath = file.getAbsolutePath();
		if(allPath.endsWith(".xls") || allPath.endsWith(".xlsx")
				|| allPath.endsWith(".XLS") || allPath.endsWith(".XLSX")
				|| allPath.endsWith(".Xls") || allPath.endsWith(".Xlsx"))
		{
			// return new DefaultTableSet(Manager.applyStringTable("NOW_READING"), file);
			return XLSXUtil.toTableSet(Manager.applyStringTable("NOW_READING"), file);
		}
		else if(allPath.endsWith(".json") 
				|| allPath.endsWith(".JSON")
				|| allPath.endsWith(".Json"))
		{
			return JSONUtil.toTableSet(StreamUtil.readText(file, Manager.getOption("file_charset")));
		}
		else if(allPath.endsWith(".hgf") 
				|| allPath.endsWith(".HGF")
				|| allPath.endsWith(".Hgf"))
		{
			return toTableSet(StreamUtil.readText(file, Manager.getOption("file_charset")));
		}
		else
		{
			return toTableSet(StreamUtil.readText(file, Manager.getOption("file_charset")));
		}
	}
	
	/**
	 * <p>예외, 혹은 오류 객체의 내용을 텍스트로 반환합니다. 자바의 스택 추적 형식을 따릅니다.</p>
	 * 
	 * @param t : 예외, 혹은 오류 객체
	 * @return 텍스트 내용
	 */
	public static String stackTrace(Throwable t)
	{
		StringBuffer results = new StringBuffer("");		
		results = results.append(t.getClass().getName() + ": " + t.getMessage() + "\n");		
		StackTraceElement[] traces = t.getStackTrace();
		for(StackTraceElement e : traces)
		{
			results = results.append("\tat " +  String.valueOf(e) + "\n");
		}		
		return results.toString();
	}

	/**
	 * <p>텍스트에서 유니코드 텍스트 파일임을 나타내는 특수 기호를 제거한 텍스트를 반환합니다.</p>
	 * 
	 * @param target : 원래의 텍스트
	 * @return 유니코드 특수 기호가 제거된 텍스트
	 */
	public static String remove65279(String target)
	{
		char[] targetChar = target.toCharArray();
		
		List<Character> resultChars = new Vector<Character>();
		for(int i=0; i<targetChar.length; i++)
		{
			if(((int) targetChar[i]) != 65279) resultChars.add(new Character(targetChar[i]));
		}
		
		char[] newChar = new char[resultChars.size()];
		for(int i=0; i<newChar.length; i++)
		{
			newChar[i] = resultChars.get(i);
		}
		return new String(newChar);
	}
	
	/**
	 * <p>텍스트를 여러 블록으로 나눕니다. 구분자로 나누지만 따옴표 안의 구분자는 나누지 않습니다.</p>
	 * 
	 * @param delim : 구분자
	 * @param contents : 원래의 텍스트
	 * @return 나눈 블록 리스트
	 */
	public static List<TextBlock> getBlocks(char delim, String contents)
	{
		return getBlocks(delim, contents, true);
	}
	
	/**
	 * <p>텍스트를 여러 블록으로 나눕니다. 구분자로 나누지만 따옴표 안의 구분자는 나누지 않습니다.</p>
	 * <p>검증 필요</p>
	 * 
	 * @param delim : 구분자
	 * @param contents : 원래의 텍스트
	 * @return 나눈 블록 리스트
	 */
	public static List<TextBlock> getBlocks(char delim, String contents, boolean alsoDelimWithNewLine)
	{
		List<TextBlock> blocks = new Vector<TextBlock>();
		
		// TODO : 검증 필요
		
		StringBuffer newBlock = new StringBuffer("");
		int newBlockStartPos = -1;
		char beforeChar = ' ';
		char quotes = ' ';
		boolean casted = false;
		boolean commented = false;
		for(int i=0; i<contents.length(); i++)
		{
			char ch = contents.charAt(i);
			
			if(commented)
			{
				if(beforeChar == '*' && ch != '/')
				{
					newBlock = newBlock.append("*");
					beforeChar = ' ';
				}				
				
				if(ch == '*')
				{
					beforeChar = ch;
				}
				else if(beforeChar == '*' && ch == '/')
				{
					beforeChar = ' ';
					commented = false;
					
					if(DataUtil.isNotEmpty(newBlock.toString()))
					{
						blocks.add(new TextBlock(newBlockStartPos, i + 1, "/*" + newBlock.toString() + "*/"));
						newBlock = new StringBuffer("");
						newBlockStartPos = i;
						casted = false;
					}
					continue;
				}
				else
				{
					newBlock = newBlock.append(String.valueOf(ch));
				}
			}
			else
			{
				if(quotes == ' ')
				{
					if(beforeChar == '/' && ch != '*')
					{
						if(DataUtil.isEmpty(newBlock.toString()))
						{
							newBlockStartPos = i;
						}
						newBlock = newBlock.append("/");
						beforeChar = ' ';
					}
					
					if(ch == '"')
					{
						if(casted)
						{
							if(DataUtil.isEmpty(newBlock.toString()))
							{
								newBlockStartPos = i;
							}
							newBlock = newBlock.append("\"");
							casted = false;
						}
						else
						{
							quotes = '"';
						}
					}
					else if(beforeChar == '/' && ch == '*')
					{						
						beforeChar = ' ';
						commented = true;
						
						if(DataUtil.isNotEmpty(newBlock.toString()))
						{
							blocks.add(new TextBlock(newBlockStartPos, (i - 1), newBlock.toString()));							
						}
						newBlock = new StringBuffer("");
						newBlockStartPos = (i - 1);
						casted = false;
						
						continue;
					}
					else if(ch == '\'')
					{
						if(casted)
						{
							if(DataUtil.isEmpty(newBlock.toString()))
							{
								newBlockStartPos = i;
							}
							newBlock = newBlock.append("'");
							casted = false;
						}
						else
						{
							quotes = '\'';
						}
					}
					else if(ch == '\\')
					{
						if(casted)
						{
							if(DataUtil.isEmpty(newBlock.toString()))
							{
								newBlockStartPos = i;
							}
							newBlock = newBlock.append(String.valueOf(ch));
							casted = false;
						}
						else
						{
							casted = true;
						}
					}
					else if(ch == delim || (alsoDelimWithNewLine && ch == '\n') || (delim == ' ' && ch == '\t'))
					{
						if(DataUtil.isNotEmpty(newBlock.toString()))
						{
							blocks.add(new TextBlock(newBlockStartPos, i, newBlock.toString()));
							newBlock = new StringBuffer("");
							newBlockStartPos = i;
							casted = false;
						}
					}
					else if(ch == '*' || ch == '/')
					{
						beforeChar = ch;
					}
					else
					{
						if((! commented) && DataUtil.isEmpty(newBlock.toString()))
						{
							newBlockStartPos = i;
						}
						newBlock = newBlock.append(String.valueOf(ch));
					}
				}
				else if(quotes == '"')
				{
					if(ch == '"')
					{
						if(casted)
						{
							if((! commented) && DataUtil.isEmpty(newBlock.toString()))
							{
								newBlockStartPos = i;
							}
							newBlock = newBlock.append("\"");
							casted = false;
						}
						else
						{
							quotes = ' ';
							if(DataUtil.isNotEmpty(newBlock.toString()))
							{
								blocks.add(new TextBlock(newBlockStartPos, i, reCastQuote(true, newBlock.toString())));
								newBlock = new StringBuffer("");
								newBlockStartPos = i;
								casted = false;
							}
						}
					}
					else if(ch == '\\')
					{
						if(casted)
						{
							if((! commented) && DataUtil.isEmpty(newBlock.toString()))
							{
								newBlockStartPos = i;
							}
							newBlock = newBlock.append(String.valueOf(ch));
							casted = false;
						}
						else
						{
							casted = true;
						}
					}
					else
					{
						if((! commented) && DataUtil.isEmpty(newBlock.toString()))
						{
							newBlockStartPos = i;
						}
						newBlock = newBlock.append(String.valueOf(ch));
					}
				}
				else if(quotes == '\'')
				{
					if(ch == '\'')
					{
						if(casted)
						{
							if((! commented) && DataUtil.isEmpty(newBlock.toString()))
							{
								newBlockStartPos = i;
							}
							newBlock = newBlock.append("'");
							casted = false;
						}
						else
						{
							quotes = ' ';
							if(DataUtil.isNotEmpty(newBlock.toString()))
							{
								blocks.add(new TextBlock(newBlockStartPos, i, reCastQuote(false, newBlock.toString())));
								newBlock = new StringBuffer("");
								newBlockStartPos = i;
								casted = false;
							}
						}
					}
					else if(ch == '\\')
					{
						if(casted)
						{
							if((! commented) && DataUtil.isEmpty(newBlock.toString()))
							{
								newBlockStartPos = i;
							}
							newBlock = newBlock.append(String.valueOf(ch));
							casted = false;
						}
						else
						{
							casted = true;
						}
					}
					else
					{
						if((! commented) && DataUtil.isEmpty(newBlock.toString()))
						{
							newBlockStartPos = i;
						}
						newBlock = newBlock.append(String.valueOf(ch));
					}
				}
			}
			
			beforeChar = ch;
		}
		
		if(DataUtil.isNotEmpty(newBlock.toString()))
		{
			blocks.add(new TextBlock(newBlockStartPos, contents.length() - 1, newBlock.toString()));
			newBlock = new StringBuffer("");
			casted = false;
		}
		
		return blocks;
	}
	
	/**
	 * <p>데이터 용량 단위를 텍스트로 변환합니다.</p>
	 * 
	 * @param values : 데이트 용량 값 (byte 단위)
	 * @return 단위 적용한 텍스트
	 */
	public static String toByteUnit(long values)
	{
		double calcs = 0.0;
		
		if(values == 0) return "0";
		if(values == 1) return "1 byte";
		if(values < 1024) return String.valueOf(values) + " bytes";
		
		calcs = values / 1024.0;
		if(calcs < 1024.0) return String.format("%.3f KB", calcs);
		
		calcs = calcs / 1024.0;
		if(calcs < 1024.0) return String.format("%.3f MB", calcs);
		
		calcs = calcs / 1024.0;
		if(calcs < 1024.0) return String.format("%.3f GB", calcs);
		
		calcs = calcs / 1024.0;
		if(calcs < 1024.0) return String.format("%.3f TB", calcs);
		
		calcs = calcs / 1024.0;
		if(calcs < 1024.0) return String.format("%.3f PB", calcs);
		
		calcs = calcs / 1024.0;
		if(calcs < 1024.0) return String.format("%.3f EB", calcs);
		
		calcs = calcs / 1024.0;
		if(calcs < 1024.0) return String.format("%.3f ZB", calcs);
		
		calcs = calcs / 1024.0;
		if(calcs < 1024.0) return String.format("%.3f YB", calcs);
		
		return String.format("%.3f YB", calcs);
	}
	
	/**
	 * <p>바이트 배열 내용을 0으로 채웁니다.</p>
	 * 
	 * @param bytes : 바이트 배열
	 */
	public static void emptyByteArray(byte[] bytes)
	{
		for(int i=0; i<bytes.length; i++)
		{
			bytes[i] = 0;
		}
	}
	
	/**
	 * <p>오늘 날짜에 대한 Date 객체를 반환합니다.</p>
	 * 
	 * @return 날짜 객체
	 */
	public static Date currentDate()
	{
		Date date = new Date(System.currentTimeMillis());
		return date;
	}
	
	/**
	 * <p>두 날짜 객체에 대한 차이를 구합니다.</p>
	 * 
	 * @param one : 날짜 객체
	 * @param two : 다른 날짜 객체
	 * @return 날짜 뺄셈 결과
	 */
	public static Date subtract(Date one, Date two)
	{		
		long oneLong = one.getTime();
		long twoLong = two.getTime();
		
		if(oneLong > twoLong) return new Date(oneLong - twoLong);
		else return new Date(twoLong - oneLong);
	}
	
	/**
	 * <p>어떤 텍스트에 특정 텍스트가 몇 번 포함되어 있는지를 반환합니다. finds 가 빈 칸이면 -1 을 반환합니다.</p>
	 * 
	 * @param text : 텍스트
	 * @param finds : 패턴
	 * @return 패턴 포함 횟수
	 */
	public static int has(String text, String finds)
	{
		int results = 0;
		String target = text;
		
		if(finds.equals("")) return -1;
		
		while(text.indexOf(finds) >= 0)
		{
			results = results + 1;
			target = target.replaceFirst(finds, "");
		}
		
		return results;
	}
	
	/**
	 * <p>2분의 1 제곱 값을 구합니다.</p>
	 * 
	 * @param original : 원래의 값
	 * @param scale : 소수 자리수
	 * @return 2분의 1 제곱
	 */
	public static BigDecimal sqrt(BigDecimal original, int scale)
	{
		BigDecimal temp = new BigDecimal(String.valueOf(original));
		
		BigDecimal results = new BigDecimal("1.0");
		results.setScale(scale + 2);
		
		int loops = 0;
		
		while(true)
		{
			if(loops >= 1)
			{
				temp = new BigDecimal(String.valueOf(results));
			}
			
			temp.setScale(scale + 2, BigDecimal.ROUND_FLOOR);
			results = original.divide(temp, scale + 2, BigDecimal.ROUND_FLOOR).add(temp).divide(new BigDecimal("2.0"), scale + 2, BigDecimal.ROUND_FLOOR);
			if(temp.equals(results)) break;
			
			loops++;
		}
		
		return results.setScale(scale, BigDecimal.ROUND_HALF_UP);
	}
	
	/**
	 * <p>문자열을 정수로 변환합니다. null일 경우 -1을, 그 외의 이유로 정수 변환이 불가능한 경우 두 번째 파라미터 값을 반환합니다.</p>
	 * 
	 * @param val : 문자열
	 * @param original : 기본값
	 * @return 정수 변환 결과
	 */
	public static int parseIntWithoutError(String val, int original)
	{
		if(val == null) return -1;
		try
		{
			return Integer.parseInt(val.trim());
		}
		catch(NumberFormatException e)
		{
			return original;
		}
	}
	
	/**
	 * <p>해당 문자열의 선택된 줄에 문자열을 끼워 넣습니다.</p>
	 * 
	 * @param original : 원래 문자열
	 * @param lines    : 선택된 줄 번호들
	 * @param startsAt : 시작 지점
	 * @param appendies : 끼워 넣을 문자열
	 * @return 결과물
	 */
	public static String appendLines(String original, int[] lines, int startsAt, String appendies)
	{
		String[] lineTexts = original.split("\n");
		StringBuffer results = new StringBuffer("");
		
		for(int l=0; l<lineTexts.length; l++)
		{
			char[] lineChars = lineTexts[l].toCharArray();
			List<Character> newChars = new Vector<Character>();
			boolean worked = false;
			
			boolean exists = false;
			for(int lineNum : lines)
			{
				if(lineNum == l)
				{
					exists = true;
					break;
				}
			}
			
			if(! exists) continue;
			
			int charNums = 0;
			for(int i=0; i<lineChars.length; i++)
			{
				if(charNums == startsAt)
				{
					char[] appendiesChars = appendies.toCharArray();
					for(int c=0; c<appendiesChars.length; c++)
					{
						newChars.add(appendiesChars[c]);
						charNums++;
					}
				}
				newChars.add(lineChars[i]);
				charNums++;
				worked = true;
			}
			
			while(! worked)
			{
				if(charNums == startsAt)
				{
					char[] appendiesChars = appendies.toCharArray();
					for(int c=0; c<appendiesChars.length; c++)
					{
						newChars.add(appendiesChars[c]);
					}
				}
				else newChars.add(' ');
				charNums++;
			}
			
			char[] newLineChars = new char[newChars.size()];
			for(int i=0; i<newChars.size(); i++)
			{
				newLineChars[i] = newChars.get(i);
			}
			
			results = results.append(new String(newLineChars)).append("\n");
		}
		
		return results.toString().trim();
	}
	
	public static String toString(Date date, String format)
	{
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		return formatter.format(date);
	}
	
	public static Date toDate(String dateVal, String format) throws ParseException
	{
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		return formatter.parse(dateVal);
	}
	
	/**
	 * <p>BASE64 형식으로 바이트 데이터를 인코딩합니다.</p>
	 * 
	 * @param bytes : 바이너리 데이터
	 * @return BASE64 인코딩된 문자열
	 */
	public static String base64(byte[] bytes)
	{
		return new String(Base64.encodeBase64(bytes));
	}
	
	/**
	 * <p>BASE64 형식으로 인코딩된 데이터를 바이트 배열로 변환합니다.</p>
	 * 
	 * @param data : 인코딩된 문자열
	 * @return : 바이트 데이터
	 */
	public static byte[] base64(String data)
	{
		return Base64.decodeBase64(data.getBytes());
	}
}
