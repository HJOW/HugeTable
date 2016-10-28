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
package hjow.hgtable.analyze;

import hjow.hgtable.jscript.module.Module;
import hjow.hgtable.tableset.Column;
import hjow.hgtable.tableset.TableSet;

import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * <p>분석 함수입니다.</p>
 * 
 * @author HJOW
 *
 */
public abstract class AnalyzeFunction extends Module
{
	private static final long serialVersionUID = -6156104357626456746L;
	
	/**
	 * <p>분석 함수를 초기화합니다.</p>
	 * 
	 */
	public void init()
	{
		
	}

	/**
	 * <p>테이블 셋을 분석합니다. 입력한 함수 이름에 맞게 해당 분석 함수를 실행해 줍니다.</p>
	 * 
	 * @param arguments : 매개 변수들
	 * @param tableSet : 분석할 대상 테이블 셋 혹은 대상 테이블 셋들
	 * @return 분석 결과 (테이블 셋 객체 형태)
	 */
	public abstract TableSet analyze(Map<String, String> arguments, TableSet ... tableSets);
	
	/**
	 * <p>컬럼을 분석합니다. 입력한 함수 이름에 맞게 해당 분석 함수를 실행해 줍니다.</p>
	 * 
	 * @param arguments : 매개 변수들
	 * @param column : 분석할 대상 컬럼 객체 혹은 컬럼 객체들
	 * @return 분석 결과 (테이블 셋 객체 형태)
	 */
	public abstract TableSet analyze(Map<String, String> arguments, Column ... columns);
	
	/**
	 * <p>필요한 매개 변수 키들을 반환합니다.</p>
	 * 
	 * @param forTableSet : true 시 테이블 셋 분석에 필요한 매개 변수 키들을 반환합니다. false 시 컬럼 분석에 필요한 매개 변수 키들을 반환합니다.
	 * @return 필요한 매개 변수 목록
	 */
	public List<String> getNeedArguments(boolean forTableSet)
	{
		return new Vector<String>();
	}
	
	/**
	 * <p>컬럼 분석이 가능한지 여부를 반환합니다.</p>
	 * 
	 * @return 컬럼 분석 가능 여부
	 */
	public boolean canAnalyzeColumn()
	{
		return false;
	}
	
	/**
	 * <p>테이블 셋 분석이 가능한지 여부를 반환합니다.</p>
	 * 
	 * @return 테이블 셋 분석 가능 여부
	 */
	public boolean canAnalyzeTableSet()
	{
		return false;
	}
	
	@Override
	public String toString()
	{
		return name;
	}
	
	@Override
	public void noMoreUse()
	{
		super.noMoreUse();
	}

	@Override
	public boolean isAlive()
	{
		return true;
	}

	@Override
	public String help()
	{
		return "";
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
}
