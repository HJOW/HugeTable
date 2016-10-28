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

import java.io.File;
import java.io.FileFilter;
import java.util.List;

import hjow.hgtable.ui.ProgressEvent;

/**
 * <p>파일을 불러와 테이블 셋으로 반환할 능력이 있는 클래스들이 구현하는 인터페이스입니다.</p>
 * 
 * @author HJOW
 *
 */
public interface TableSetBuilder extends TableSetTreator
{
	/**
	 * <p>파일을 불러와 테이블 셋 리스트로 반환합니다.</p>
	 * 
	 * @param file : 불러올 파일
	 * @param event : 현재 진행률을 보고하기 위한 이벤트 객체 (null 가능해야 함)
	 * @return 불러온 테이블 셋
	 */
	public List<TableSet> toTableSet(File file, ProgressEvent event);
	
	/**
	 * <p>불러올 수 있는 파일 종류를 구분짓는 데 사용하는 필터 객체를 반환합니다.</p>
	 * 
	 * @return 파일 필터
	 */
	public FileFilter availables();
}
