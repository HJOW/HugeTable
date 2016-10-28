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

import hjow.hgtable.Manager;
import hjow.hgtable.util.StreamUtil;
import hjow.hgtable.stringtable.InvalidPrefixException;
import hjow.hgtable.stringtable.StringTable;
import hjow.hgtable.ui.module.defaults.ArgumentData;
import hjow.hgtable.util.ArgumentUtil;
import hjow.hgtable.util.ConsoleUtil;
import hjow.hgtable.util.DataUtil;
import hjow.hgtable.util.DirectIOUtil;
import hjow.hgtable.util.InvalidInputException;
import hjow.hgtable.util.JSONUtil;
import hjow.hgtable.util.SecurityUtil;
import hjow.hgtable.util.StringTableUtil;
import hjow.hgtable.util.TextBlock;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * <p>이 클래스 객체는 Huge Table 에서 제공하는 정적 메소드들을 스크립트 내에서 사용할 수 있게 해 줍니다. smb 라는 이름으로 액세스할 수 있습니다.</p>
 * 
 * @author HJOW
 *
 */
public class StaticMethodBroker implements JScriptObject
{
	private static final long serialVersionUID = 5403774828235278913L;
	public final Map<String, Object> utils = new Hashtable<String, Object>();
	
	/**
	 * <p>기본 생성자입니다.</p>
	 * 
	 */
	public StaticMethodBroker()
	{
		utils.put("console", new ConsoleUtil());
		utils.put("data", new DataUtil());
		utils.put("dio", new DirectIOUtil());
		utils.put("lang", new StringTableUtil());
		try
		{
			utils.put("json", new JSONUtil());
		}
		catch(Throwable e)
		{
			
		}
	}
	
	/**
	 * <p>콘솔을 비웁니다. 대개 수많은 줄을 띄어 비어 있는 것처럼 보이게 됩니다.</p>
	 * 
	 * @param manager : 매니저 객체
	 */
	public void clearConsole(Manager manager)
	{
		ConsoleUtil.clearConsole(manager);
	}
	
	/**
	 * <p>어떤 객체가 null 인지 확인합니다. 문자열인 경우 빈 칸인지 여부도 검사합니다.</p>
	 * 
	 * @param ob : 검사할 대상 객체
	 * @return 비어 있는지 여부
	 */
	public boolean isEmpty(Object ob)
	{
		return DataUtil.isEmpty(ob);
	}
	
	/**
	 * <p>이 메소드는 isEmpty(ob) 메소드의 결과와 반대로 작동합니다.</p>
	 * 
	 * @param ob : 검사할 대상 객체
	 * @return 비어 있지 않은지 여부
	 */
	public boolean isNotEmpty(Object ob)
	{
		return DataUtil.isNotEmpty(ob);
	}
	
	/**
	 * <p>객체들이 모두 비어 있는지 여부를 반환합니다. 모두 빈 객체여야 true 가 반환됩니다.</p>
	 * 
	 * @param obs : 객체들
	 * @return 비어 있는지 여부
	 */
	public boolean isEmpties(Object ... obs)
	{
		return DataUtil.isEmpties(obs);
	}
	
	/**
	 * <p>객체들이 모두 비어 있지 않은지 여부를 반환합니다. 모두 비어 있지 않은 객체여야 true 가 반환됩니다.</p>
	 * 
	 * @param obs : 객체들
	 * @return 비어 있지 않은지 여부
	 */
	public boolean isNotEmpties(Object ... obs)
	{
		return DataUtil.isNotEmpties(obs);
	}
	
	/**
	 * <p>어떤 객체가 정수 값으로 변환이 가능한지 여부를 반환합니다.</p>
	 * 
	 * @param ob : 검사할 객체
	 * @return : 정수 변환 가능 여부
	 */
	public boolean isInteger(Object ob)
	{
		return DataUtil.isInteger(ob);
	}
	
	/**
	 * <p>어떤 객체가 실수 값으로 변환이 가능한지 여부를 반환합니다.</p>
	 * 
	 * @param ob : 검사할 객체
	 * @return : 실수 변환 가능 여부
	 */
	public boolean isFloat(Object ob)
	{
		return DataUtil.isFloat(ob);
	}
	
	/**
	 * <p>어떤 객체가 정수, 혹은 실수 값인지 여부를 반환합니다.</p>
	 * 
	 * @param ob : 검사할 객체
	 * @return 숫자 데이터 여부
	 */
	public boolean isNumber(Object ob)
	{
		return DataUtil.isNumber(ob);
	}
	
	/**
	 * <p>어떠한 객체를 논리값으로 변환합니다. 예를 들어, 문자열인 경우 내용이 true, yes, y, 1 중 하나이면 true 를 반환합니다. 변환할 수 없는 내용이면 예외를 발생합니다.</p>
	 * 
	 * @param ob : 변환할 대상
	 * @return 변환된 논리값
	 * @throws InvalidInputException 변환할 수 없는 객체인 경우
	 */
	public boolean parseBoolean(Object ob) throws InvalidInputException
	{
		return DataUtil.parseBoolean(ob);
	}
	
	/**
	 * <p>따옴표 앞에 \ 기호를 붙입니다.</p>
	 * 
	 * @param isDoubleQuote : 이 값이 true 이면 쌍따옴표 앞에만 \ 기호를 붙이고, 그 외에는 일반 따옴표 앞에만 \ 기호를 붙입니다.
	 * @param target : 대상 텍스트
	 * @return \ 처리된 텍스트
	 */
	public String castQuote(boolean isDoubleQuote, String target)
	{
		return DataUtil.castQuote(isDoubleQuote, target);
	}
	
	/**
	 * <p>\ 처리된 따옴표들을 찾아 원래의 따옴표로 돌려 놓습니다.</p>
	 * 
	 * @param isDoubleQuote : 이 값이 true 이면 \ 처리된 쌍따옴표들을 찾아 원래의 쌍따옴표로 바꾸고, false 이면 일반 따옴표들을 찾아 원래의 따옴표로 바꿉니다.</p>
	 * @param target : 대상 텍스트
	 * @return \ 가 제거된 텍스트
	 */
	public String reCastQuote(boolean isDoubleQuote, String target)
	{
		return DataUtil.reCastQuote(isDoubleQuote, target);
	}
	
	/**
	 * <p>줄 띄기, 탭, 따옴표 앞에 \를 붙입니다. 여러 줄 텍스트는 한 줄로 변환되고, 줄 띄는 자리에는 대신 \n이, 탭 자리에는 \t가 오게 됩니다.</p>
	 * 
	 * @param isDoubleQuote : 이 값이 true 이면 쌍따옴표 앞에만 \ 기호를 붙이고, 그 외에는 일반 따옴표 앞에만 \ 기호를 붙입니다.
	 * @param target : 대상 텍스트
	 * @return \ 처리된 텍스트
	 */
	public String castTotal(boolean isDoubleQuote, String target)
	{
		return DataUtil.castTotal(isDoubleQuote, target);
	}
	
	/**
	 * <p>\n, \t, \" 를 찾아 \ 기호를 제거합니다. \n은 줄 띄기 기호로 변환되고, \t는 탭 공백으로 변환됩니다.</p>
	 * 
	 * @param isDoubleQuote : 이 값이 true 이면 \ 처리된 쌍따옴표들을 찾아 원래의 쌍따옴표로 바꾸고, false 이면 일반 따옴표들을 찾아 원래의 따옴표로 바꿉니다.
	 * @param target : 대상 텍스트
	 * @return \ 가 제거된 텍스트
	 */
	public String reCastTotal(boolean isDoubleQuote, String target)
	{
		return DataUtil.reCastTotal(isDoubleQuote, target);
	}
	
	/**
	 * <p>스트링 테이블 객체에 기본 데이터를 넣습니다. 시스템 언어 설정에 따라 작동이 다릅니다.</p>
	 * 
	 * @param stringTable : 스트링 테이블 (언어 설정) 객체
	 */
	public void defaultData(StringTable stringTable)
	{
		StringTableUtil.defaultData(stringTable);
	}
	
	/**
	 * <p>텍스트로부터 스트링 테이블 데이터를 꺼내 스트링 테이블에 넣습니다.</p>
	 * 
	 * @param stringTable : 대상 스트링 테이블
	 * @param str : 대상 텍스트
	 * @throws InvalidPrefixException 정해진 규칙에 맞지 않는 텍스트인 경우 발생
	 */
	public void inputFromText(StringTable stringTable, String str) throws InvalidPrefixException
	{
		StringTableUtil.inputFromText(stringTable, str);
	}
	
	/**
	 * <p>텍스트를 암호화합니다.</p>
	 * 
	 * @param text : 대상이 되는 텍스트
	 * @param password : 암호화에 쓰일 비밀번호
	 * @param algorithm : 암호화 방법 (null 시 AES 사용)
	 * @return 암호화된 텍스트
	 */
	public String encrypt(String text, String password, String algorithm)
	{
		return SecurityUtil.encrypt(text, password, algorithm);
	}
	
	/**
	 * <p>암호화된 텍스트를 복호화해 원래의 텍스트를 구합니다.</p>
	 * 
	 * @param text : 암호화된 텍스트
	 * @param password : 암호화에 쓰인 비밀번호
	 * @param algorithm : 암호화에 쓰인 암호화 방법 (null 시 AES 로 간주)
	 * @return 원래의 텍스트
	 */
	public String decrypt(String text, String password, String algorithm)
	{
		return SecurityUtil.decrypt(text, password, algorithm);
	}
	
	/**
	 * <p>텍스트의 해시값을 구합니다.</p>
	 * 
	 * @param text : 원래의 텍스트
	 * @param algorithm : 해싱 알고리즘
	 * @return 해시값
	 */
	public String hash(String text, String algorithm)
	{
		return SecurityUtil.hash(text, algorithm);
	}
	
	/**
	 * <p>SQL문에 매개 변수를 삽입한 결과를 반환합니다.</p>
	 * 
	 * @param targetScript : 대상 스크립트
	 * @param args : 매개 변수 리스트
	 * @param forms : 형식, ?, :args, #args# 가능
	 * @return 매개 변수가 삽입된 스크립트
	 */
	public String applyArgs(String targetScript, List<Object> args, String forms)
	{
		List<ArgumentData> newArgList = new Vector<ArgumentData>();
		ArgumentData newOne;
		
		for(int i=0; i<args.size(); i++)
		{
			if(args.get(i) instanceof ArgumentData)
			{
				newArgList.add((ArgumentData) args.get(i));
			}
			else if(args.get(i) instanceof Object[])
			{
				newOne = new ArgumentData();
				newOne.setName(String.valueOf(((Object[]) args.get(i))[0]));
				newOne.setData(String.valueOf(((Object[]) args.get(i))[1]));
				newArgList.add(newOne);
			}
			else
			{
				newOne = new ArgumentData();
				newOne.setName(String.valueOf(i));
				newOne.setData(String.valueOf(args.get(i)));
				newArgList.add(newOne);
			}
		}
		
		return ArgumentUtil.applyArgs(targetScript, newArgList, forms);
	}
	
	/**
	 * <p>텍스트를 여러 블록으로 나눕니다. 구분자로 나누지만 따옴표 안의 구분자는 나누지 않습니다.</p>
	 * 
	 * @param delim : 구분자
	 * @param contents : 원래의 텍스트
	 * @return 나눈 블록 리스트
	 */
	public List<TextBlock> getBlocks(String delim, String contents)
	{
		if(isNotEmpty(delim))
		{
			return DataUtil.getBlocks(delim.charAt(0), contents);
		}
		else
		{
			return DataUtil.getBlocks(' ', contents);
		}
	}
	
	/**
	 * <p>텍스트로부터 매개 변수들을 받습니다.</p>
	 * 
	 * @param ignores : 무시할 매개 변수 목록
	 * @param parameter : 텍스트
	 * @return 매개 변수 리스트
	 */
	public Map<String, String> getArguments(String parameter, List<String> ignores)
	{
		return ArgumentUtil.getArguments(parameter, ignores);
	}
	
	/**
	 * <p>메모리 현황 정보 메시지를 텍스트로 반환합니다.</p>
	 * 
	 * @param basicData : true 시 기본 정보를 포함합니다.
	 * @param detailData : true 시 상세 정보를 포함합니다. 주의 ! 기본 정보가 상세 정보에 담겨 있지 않습니다.
	 * @return 메모리 현황 정보
	 * @throws InvalidInputException : 매개 변수에 true, false, yes, no 외에 다른 값이 들어온 경우
	 */
	public String memoryData(Object basicData, Object detailData) throws InvalidInputException
	{
		if(basicData == null && detailData == null) return ConsoleUtil.memoryData(true, false);
		if(basicData != null && detailData == null) return ConsoleUtil.memoryData(DataUtil.parseBoolean(basicData), DataUtil.parseBoolean(detailData));
		if(basicData == null && detailData != null) return ConsoleUtil.memoryData(true, false);
		return ConsoleUtil.memoryData(DataUtil.parseBoolean(basicData), DataUtil.parseBoolean(detailData));
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
	
	/**
	 * <p>두 날짜 객체에 대한 차이를 구합니다.</p>
	 * 
	 * @param one : 날짜 객체
	 * @param two : 다른 날짜 객체
	 * @return 날짜 뺄셈 결과
	 */
	public Date subtract(Date one, Date two)
	{
		return DataUtil.subtract(one, two);
	}
	
	/**
	 * <p>입력 스트림으로 받은 데이터를 출력 스트림으로 전달합니다.</p>
	 * 
	 * @param inputStream : 입력 스트림
	 * @param outputStream : 출력 스트림
	 * @throws IOException : 스트림 문제 (네트워크 등)
	 */
	public void pipe(InputStream inputStream, OutputStream outputStream) throws IOException
	{
		StreamUtil.pipe(inputStream, outputStream);
	}
	
	/**
	 * <p>바이트 배열 내용을 0으로 채웁니다.</p>
	 * 
	 * @param bytes : 바이트 배열
	 */
	public void emptyByteArray(byte[] bytes)
	{
		DataUtil.emptyByteArray(bytes);
	}
	
	@Override
	public String help()
	{
		StringBuffer results = new StringBuffer("");
		
		results = results.append(Manager.applyStringTable("This object has many useful methods which don't need any privileges.") + "\n");
		results = results.append("clearConsole(manager)" + " : " + Manager.applyStringTable("Clear manager's console.") + "\n");
		results = results.append("isEmpty(ob)" + " : " + Manager.applyStringTable("Return true if the ob is null or ob is empty.") + "\n");
		results = results.append("parseBoolean(ob)" + " : " + Manager.applyStringTable("Return true if the ob is not null and the ob is 1, true, or \"true\", \"1\", \"y\", \"yes\".") + "\n");
		results = results.append("hash(text, algorithm)" + " : " + Manager.applyStringTable("Return hash value of text, algorithm can be MD5, SHA-256, and SHA-512.") + "\n");
		results = results.append("encrypt(text, password, algorithm)" + " : " + Manager.applyStringTable("Return encrypted text. algorithm can be DES, and AES.") + "\n");
		results = results.append("decrypt(text, password, algorithm)" + " : " + Manager.applyStringTable("Return decrypted text. algorithm can be DES, and AES.") + "\n");
		results = results.append("applyArgs(targetScript, args, forms)" + " : " + Manager.applyStringTable("Return SQL statements with arguments. args should be List of arguments. "
				+ "forms can be ?, :args and #args#") + "\n");
		results = results.append("getBlocks(delim, contents)" + " : " + Manager.applyStringTable("Return contents divided by delim as List.") + "\n");
		results = results.append("getArguments(parameter, ignores)" + " : " + Manager.applyStringTable("Return arguments from text. "
				+ "ignores is List of arguments which should be ignored. ignores can be null.") + "\n");
		results = results.append("memoryData(basicData, detailData)" + " : " + Manager.applyStringTable("Return memory informations. "
				+ "If basicData is true, basic informations will be returned. If detailData is true, detail informations will be returned.") + "\n");
		
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
