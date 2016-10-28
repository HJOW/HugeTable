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

package hjow.hgtable.stringtable;

import java.io.File;
import java.util.Map;

/**
 * <p>언어 설정(스트링 테이블) 규격입니다.</p>
 * 
 * @author HJOW
 *
 */
public interface StringTable extends Map<String, String>
{
	/**
	 * <p>텍스트를 변환합니다. 스트링 테이블에 해당 내용이 없다면 그대로, 있다면 있는 내용을 대신 반환합니다.</p>
	 * 
	 * @param str : 대상 텍스트
	 * @return 변환된 내용
	 */
	public String process(String str);
	
	/**
	 * <p>스트링 테이블을 파일로부터 읽어 들입니다. # 기호로 시작하는 줄은 무시되며, 찾는 텍스트와 변환할 텍스트를 각 줄에 번갈아 가며 기입한 규격을 따릅니다.</p>
	 * 
	 * @param file : 대상 파일
	 */
	public void read(File file);
	
	/**
	 * <p>스트링 테이블을 파일에 저장합니다.</p>
	 * 
	 * @param file : 대상 파일
	 */
	public void save(File file);
	
	/**
	 * <p>스트링 테이블에 없는 내용 검색 기능이 켜져 있는지 여부를 반환합니다.</p>
	 * 
	 * @return 없는 내용 검색 기능이 켜져 있는지 여부
	 */
	boolean isCollectNoInString();
	
	/**
	 * <p>스트링 테이블에 없는 내용 검색 기능을 켜거나 끕니다.</p>
	 * 
	 * @param collectNoInString : true 입력 시 켜기, false 입력 시 끄기
	 */
	void setCollectNoInString(boolean collectNoInString);
	
	/**
	 * <p>스트링 테이블 객체가 메모리 상에서 제거될 때 해야 할 내용입니다.</p>
	 * 
	 */
	public void finalize();
}
