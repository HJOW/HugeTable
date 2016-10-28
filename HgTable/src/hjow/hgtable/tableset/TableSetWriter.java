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

import hjow.hgtable.ui.ProgressEvent;

/**
 * <p>테이블 셋 객체를 파일로 저장할 수 있는 클래스들이 구현하는 인터페이스입니다.</p>
 * 
 * @author HJOW
 *
 */
public interface TableSetWriter extends TableSetTreator
{
	/**
	 * <p>테이블 셋을 파일로 저장합니다.</p>
	 * 
	 * @param file : 저장할 파일 객체
	 * @param tableSet : 테이블 셋
	 * @param event : 현재 진행률 보고를 위한 이벤트 (null 가능해야 함)
	 */
	public void save(File file, TableSet tableSet, ProgressEvent event);
}
