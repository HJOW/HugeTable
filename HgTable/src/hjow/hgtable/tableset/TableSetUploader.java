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

import hjow.hgtable.dao.Dao;
import hjow.hgtable.ui.ProgressEvent;

/**
 * <p>테이블 셋을 파일에서 불러와 바로 데이터 소스에 삽입하는 도구입니다.</p>
 * 
 * @author HJOW
 *
 */
public interface TableSetUploader extends TableSetTreator
{
	/**
	 * <p>파일을 불러와 데이터 소스에 삽입합니다.</p>
	 * 
	 * @param file : 불러올 파일
	 * @param dao : 데이터 소스 접속 DAO
	 * @param tableName : 삽입할 테이블 이름
	 * @param event : 현재 진행률을 보고하기 위한 이벤트 객체 (null 가능해야 함)
	 */
	public void upload(File file, Dao dao, String tableName, ProgressEvent event);
	
	/**
	 * <p>불러올 수 있는 파일 종류를 구분짓는 데 사용하는 필터 객체를 반환합니다.</p>
	 * 
	 * @return 파일 필터
	 */
	public FileFilter availables();
}
