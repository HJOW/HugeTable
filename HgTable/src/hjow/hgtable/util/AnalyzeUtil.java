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
import hjow.hgtable.analyze.AnalyzeFunction;
import hjow.hgtable.analyze.defaults.AnalyzeCounts;
import hjow.hgtable.tableset.Column;
import hjow.hgtable.tableset.TableSet;

import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * <p>테이블 셋 객체를 다루는 여러 정적 메소드를 가집니다.</p>
 * 
 * @author HJOW
 *
 */
public class AnalyzeUtil
{
	protected static final List<AnalyzeFunction> functions = new Vector<AnalyzeFunction>();
	
	/**
	 * <p>분석 함수들을 준비합니다.</p>
	 * 
	 */
	public static void init()
	{
		AnalyzeFunction funcs = null;
		
		funcs = new AnalyzeCounts();
		add(funcs);
	}
	
	/**
	 * <p>분석 함수들의 사용을 종료합니다.</p>
	 * 
	 */
	public static void noMoreUse()
	{
		for(AnalyzeFunction f : functions)
		{
			try
			{
				f.noMoreUse();
			}
			catch(Exception e)
			{
				
			}
		}
		try
		{
			functions.clear();
		}
		catch(Exception e)
		{
			
		}
	}
	
	/**
	 * <p>분석 함수를 리스트에 추가하고 초기화합니다.</p>
	 * 
	 * @param a : 분석 함수
	 */
	public static void add(AnalyzeFunction a)
	{
		if(get(a.getName()) == null)
		{
			a.init();
			functions.add(a);
		}
		else throw new InvalidInputException(Manager.applyStringTable("Analyze function name cannot be duplicated") + " : " + a.getName());
	}
	
	/**
	 * <p>해당 이름의 분석 함수를 반환합니다. 불러오지 않았거나 존재하지 않는 분석 함수이면 null 을 반환합니다.</p>
	 * 
	 * @param name : 분석 함수 이름
	 * @return 해당 분석 함수
	 */
	public static AnalyzeFunction get(String name)
	{
		for(AnalyzeFunction f : functions)
		{
			if(f.getName().equals(name))
			{
				return f;
			}
		}
		return null;
	}
	
	/**
	 * <p>사용 가능한 분석 함수들의 목록을 반환합니다.</p>
	 * 
	 * @param tableSetFunctions : true 시 테이블 셋 분석용 함수들의 목록을 반환합니다. false 시 컬럼 분석용 함수들의 목록을 반환합니다.
	 * @return 분석 함수 목록
	 */
	public static List<String> availables(boolean tableSetFunctions)
	{
		List<String> results = new Vector<String>();
		if(tableSetFunctions)
		{
			for(AnalyzeFunction f : functions)
			{
				if(f.canAnalyzeTableSet()) results.add(f.getName());
			}
		}
		else
		{
			for(AnalyzeFunction f : functions)
			{
				if(f.canAnalyzeColumn()) results.add(f.getName());
			}
		}
		return results;
	}
	
	/**
	 * <p>컬럼 분석이 가능한 분석 함수들을 반환합니다.</p>
	 * 
	 * @return 컬럼 분석 함수들
	 */
	public static List<AnalyzeFunction> getColumnAnalyzeFunctions()
	{
		List<AnalyzeFunction> results = new Vector<AnalyzeFunction>();
		
		for(AnalyzeFunction f : functions)
		{
			if(f.canAnalyzeColumn()) results.add(f);
		}
		
		return results;
	}
	
	/**
	 * <p>테이블 셋을 분석할 수 있는 분석 함수들을 반환합니다.</p>
	 * 
	 * @return 테이블 셋 분석 함수들
	 */
	public static List<AnalyzeFunction> getTableSetAnalyzeFunctions()
	{
		List<AnalyzeFunction> results = new Vector<AnalyzeFunction>();
		
		for(AnalyzeFunction f : functions)
		{
			if(f.canAnalyzeTableSet()) results.add(f);
		}
		
		return results;
	}
	
	/**
	 * <p>테이블 셋을 분석합니다. 입력한 함수 이름에 맞게 해당 분석 함수를 실행해 줍니다.</p>
	 * 
	 * @param analyzeFunction : 분석 함수 이름
	 * @param arguments : 매개 변수들
	 * @param tableSets : 분석할 대상 테이블 셋 혹은 대상 테이블 셋들
	 * @return 분석 결과 (테이블 셋 객체 형태)
	 */
	public static TableSet analyze(String analyzeFunction, Map<String, String> arguments, TableSet ... tableSets)
	{
		boolean processed = false;
		
		for(AnalyzeFunction f : getColumnAnalyzeFunctions())
		{
			if(f.getName().equals(analyzeFunction))
			{
				return f.analyze(arguments, tableSets);
			}
		}
		
		if(! processed) throw new InvalidInputException(Manager.applyStringTable("There is no analyze function for table set of") + " " + analyzeFunction);
		return null;
	}
	
	/**
	 * <p>컬럼을 분석합니다. 입력한 함수 이름에 맞게 해당 분석 함수를 실행해 줍니다.</p>
	 * 
	 * @param analyzeFunction : 분석 함수 이름
	 * @param arguments : 매개 변수들
	 * @param columns : 분석할 대상 컬럼 객체 혹은 컬럼 객체들
	 * @return 분석 결과 (테이블 셋 객체 형태)
	 */
	public static TableSet analyze(String analyzeFunction, Map<String, String> arguments, Column ... columns)
	{
		boolean processed = false;
		
		for(AnalyzeFunction f : getTableSetAnalyzeFunctions())
		{
			if(f.getName().equals(analyzeFunction))
			{
				return f.analyze(arguments, columns);
			}
		}
		
		if(! processed) throw new InvalidInputException(Manager.applyStringTable("There is no analyze function for column of") + " " + analyzeFunction);
		return null;
	}
}
