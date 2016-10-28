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

import hjow.hgtable.dao.Dao;
import hjow.hgtable.jscript.JScriptObject;
import hjow.hgtable.jscript.JScriptRunner;
import hjow.hgtable.ui.NeedtoEnd;
import hjow.hgtable.util.InvalidInputException;

import java.io.Serializable;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;


/**
 * <p>테이블 객체이며 데이터들을 포함합니다.</p>
 * 
 * @author HJOW
 *
 */
public interface TableSet extends Serializable, JScriptObject, NeedtoEnd, List<Record>
{
		
	/**
	 * <p>테이블 이름을 반환합니다. DB에 삽입 시 이 이름이 테이블명으로 사용됩니다.</p>
	 * 
	 * @return 테이블 이름
	 */
	public String getName();
	
	/**
	 * <p>테이블 이름을 지정합니다. DB에 삽입 시 이 이름이 테이블명으로 사용됩니다.</p>
	 * 
	 * @param name : 테이블 이름
	 */
	public void setName(String name);
	
	/**
	 * <p>컬럼들을 모두 반환합니다.</p>
	 * 
	 * @return 컬럼 리스트
	 */
	public List<Column> getColumns();
	
	/**
	 * <p>해당 번호의 컬럼의 이름을 반환합니다.</p>
	 * 
	 * @param index : 컬럼 번호
	 * @return 해당 컬럼의 이름
	 */
	public String getColumnName(int index);
	
	/**
	 * <p>해당 번호의 컬럼의 데이터 타입을 반환합니다.</p>
	 * 
	 * @param index : 컬럼 번호
	 * @return 해당 컬럼의 데이터 타입
	 */
	public int getColumnType(int index);
	
	/**
	 * <p>해당 번호의 컬럼의 데이터 타입 이름을 반환합니다.</p>
	 * 
	 * @param index : 컬럼 번호
	 * @return 해당 컬럼의 데이터 타입 이름
	 */
	public String getColumnTypeName(int index);
	
	/**
	 * <p>컬럼 갯수를 반환합니다.</p>
	 * 
	 * @return 컬럼 갯수
	 */
	public int getColumnCount();
	
	/**
	 * <p>컬럼 리스트를 한 번에 교체합니다. 각 컬럼들끼리 레코드 갯수가 모두 같아야 합니다. 기존 데이터들을 모두 대체합니다.</p>
	 * 
	 * @param columns 컬럼 리스트
	 */
	public void setColumns(List<Column> columns);
	
	/**
	 * <p>row 번째 레코드의 col 번째 컬럼 값을 수정합니다.</p>
	 * 
	 * @param col : 컬럼 번호
	 * @param row : 레코드 번호
	 * @param data : 변경할 데이터
	 */
	public void setData(int col, int row, String data);
	
	/**
	 * <p>row 번째 레코드의 col 번째 컬럼 값을 반환합니다.</p>
	 * 
	 * @param col : 컬럼 번호
	 * @param row : 레코드 번호
	 * @return 값
	 */
	public String getData(int col, int row);
	
	/**
	 * <p>레코드 갯수를 반환합니다. 컬럼이 하나도 없으면 예외가 발생합니다.</p>
	 * 
	 * @return 레코드 갯수
	 */
	public int getRecordCount();
	
	/**
	 * <p>데이터 소스에 테이블 혹은 그에 맞는 단위를 생성합니다.</p>
	 * 
	 * @param dao : 연결된 DAO 객체
	 * @param insertData
	 */
	public void createTable(Dao dao, Map<String, String> options, boolean useVarchar2, boolean insertData) throws Exception;
	
	/**
	 * <p>테이블 데이터를 실제 데이터 소스에 넣습니다.</p>
	 * 
	 * <p>컬럼과 레코드를 반복을 돌리며 꺼내 삽입 구문을 만듭니다. 컬럼 이름을 함수 필요 여부 맵에서 찾아, 있으면 함수를 덮고, 아니면 그대로 데이터를 붙입니다.</p>
	 * <p>레코드 하나에 대한 삽입 구문이 완성되면 데이터 소스에 전송해 실행합니다. 모두 실행되면 COMMIT 합니다.</p>
	 * 
	 * @param dao : 연결된 DAO 객체
	 */
	public void insertIntoDB(Dao dao);
	
	/**
	 * <p>테이블 데이터를 실제 데이터 소스에 넣습니다.</p>
	 * 
	 * <p>컬럼과 레코드를 반복을 돌리며 꺼내 삽입 구문을 만듭니다. 컬럼 이름을 함수 필요 여부 맵에서 찾아, 있으면 함수를 덮고, 아니면 그대로 데이터를 붙입니다.</p>
	 * <p>레코드 하나에 대한 삽입 구문이 완성되면 데이터 소스에 전송해 실행합니다. 모두 실행되면 COMMIT 합니다.</p>
	 * 
	 * @param dao : 연결된 DAO 객체
	 * @param params : 매개 변수들
	 */
	public void insertIntoDB(Dao dao, Map<String, String> params);
	
	/**
	 * <p>테이블 데이터를 실제 DB에 넣습니다.</p>
	 * 
	 * <p>컬럼과 레코드를 반복을 돌리며 꺼내 INSERT INTO 구문을 만듭니다. 컬럼 이름을 함수 필요 여부 맵에서 찾아, 있으면 함수를 덮고, 아니면 그대로 데이터를 붙입니다.</p>
	 * <p>레코드 하나에 대한 INSERT INTO 구문이 완성되면 DB에 전송해 실행합니다. 모두 실행되면 COMMIT 합니다.</p>
	 * 
	 * @param dao : DB에 연결된 DAO 객체
	 * @param params : 매개 변수들
	 * @param columnSurroundFunctions : 매개변수의 함수 필요 여부가 담긴 Map
	 * @param columnSurroundFunctionParamIndex : 매개변수의 함수 필요한 경우 몇 번째 파라미터에 데이터가 들어가야 하는지가 담긴 Map
	 * @param columnSurroundFunctionParams : 매개변수의 함수 필요한 경우 다른 파라미터들
	 */
	public void insertIntoDB(Dao dao, Map<String, String> params, Map<String, String> columnSurroundFunctions, Map<String, Integer> columnSurroundFunctionParamIndex, Map<String, List<String>> columnSurroundFunctionParams);
	
	/**
	 * <p>데이터들을 모두 DB에 넣기 위한 INSERT INTO 구문을 반환합니다.</p>
	 */
	public String toInsertSQL();
	
	/**
	 * <p>데이터들을 복사해 새 테이블 셋 객체를 만듭니다.</p>
	 * 
	 * @return 새로운 테이블 셋 객체
	 */
	public TableSet clone();
	
	/**
	 * <p>범위를 지정해 몇 번째부터 몇 번째까지의 레코드들을 복사해 새 테이블 셋 객체로 반환합니다.</p>
	 * 
	 * @param startRecordIndex : 시작 지점 (0부터 시작)
	 * @param endRecordIndex : 끝 지점
	 * @return 복사된 테이블 셋 객체
	 */
	public TableSet subTable(int startRecordIndex, int endRecordIndex);
	
	/**
	 * <p>범위를 지정해 몇 번째부터 몇 번째까지의 레코드들을 복사해 새 테이블 셋 객체로 반환합니다.</p>
	 * 
	 * @param startColumnIndex : 시작 컬럼 지점 (0부터 시작)
	 * @param endColumnIndex : 끝 컬럼 지점
	 * @param startRecordIndex : 시작 레코드 지점 (0부터 시작)
	 * @param endRecordIndex : 끝 레코드 지점
	 * @return 복사된 테이블 셋 객체
	 */
	public TableSet subTable(int startColumnIndex, int endColumnIndex, int startRecordIndex, int endRecordIndex);
	
	/**
	 * <p>몇 번째 컬럼을 반환합니다.</p>
	 * 
	 * @param index : 컬럼 번호
	 * @return 컬럼
	 */
	public Column getColumn(int index);
	
	/**
	 * <p>해당 이름의 컬럼을 반환합니다.</p>
	 * 
	 * @param name : 컬럼 이름
	 * @return 컬럼
	 */
	public Column getColumn(String name);
	
	/**
	 * <p>컬럼을 추가합니다. 기존 컬럼이 존재할 경우 갯수가 맞지 않으면 예외를 대신 발생합니다.</p>
	 * 
	 * @param newColumn : 추가할 새 컬럼
	 */
	public void addColumn(Column newColumn);
	
	/**
	 * <p>스크립트를 실행해 그 결과를 반환합니다. 항상 텍스트 형태로 변환되어 반환됩니다.</p>
	 * 
	 * <p>주의 : target 이라는 변수에 현재의 테이블 셋 객체가 삽입되며, 이전에 target 변수에 있던 값이나 참조가 사라집니다.</p>
	 * <p>스크립트에 의해 테이블 셋의 데이터가 변경될 수 있습니다.</p>
	 * 
	 * @param script : 스크립트
	 * @return 실행 결과
	 */
	public String applyScript(String script, JScriptRunner runner) throws Throwable;
	
	/**
	 * <p>DB로부터 가져온 ResultSet 로부터 데이터를 가져와 넣습니다.</p>
	 * 
	 * @param data : ResultSet 객체 (JDBC 라이브러리)
	 * @throws Exception 데이터 타입 문제이거나 null 문제
	 */
	public void addData(ResultSet data) throws Exception;

	/**
	 * <p>DB로부터 데이터를 가져와 넣습니다. 쿼리 결과 나온 테이블 이름으로 테이블 셋의 이름이 바뀝니다.</p>
	 * 
	 * @param name : 테이블 이름 (쿼리에 사용됨)
	 * @param dao : DAO (DB 접속)
	 * @param additionalWheres : 추가 조건문
	 * @throws Exception : DB 접속 과정에서 발생하는 문제들
	 */
	public void addData(String name, Dao dao, String additionalWheres) throws Exception;

	/**
	 * <p>다른 테이블 셋에 있는 데이터를 복사해 넣습니다. 컬럼 갯수와 이름, 그리고 데이터 타입이 맞아야 합니다.</p>
	 * 
	 * @param tableSet : 다른 테이블 셋
	 * @throws Exception : 데이터 타입 및 컬럼 존재 여부, 이름 문제
	 */
	public void addData(TableSet tableSet) throws Exception;

	/**
	 * <p>해당 배열을 레코드로 취급해 테이블 셋에 삽입합니다.</p>
	 * 
	 * @param dataArray : 데이터 배열
	 */
	public void addData(String[] dataArray);
	
	/**
	 * <p>해당 리스트를 레코드로 취급해 테이블 셋에 삽입합니다.</p>
	 * 
	 * @param dataList : 데이터 리스트
	 */
	public void addData(List<String> dataArray);
	
	/**
	 * <p>해당 레코드를 테이블 셋에 삽입합니다.</p>
	 * 
	 * @param record : 레코드
	 * @exception InvalidInputException : 타입이 일치하지 않을 경우
	 */
	public void addData(Record record) throws InvalidInputException;
	
	/**
	 * <p>해당 레코드를 반환합니다.</p>
	 * 
	 * @param index : 레코드 번호
	 * @return 해당 번째의 레코드
	 */
	public Record getRecord(int index);
	
	/**
	 * <p>레코드들을 담은 리스트를 반환합니다.</p>
	 * 
	 * @return 레코드 리스트
	 */
	public List<Record> toRecordList();
	
	/**
	 * <p>데이터들을 모두 포함하는 JSON 형식의 텍스트를 반환합니다.</p>
	 * 
	 * @param basedRecord : 이 값이 true 이면 레코드 기준으로 JSON 형식을 배치합니다. false 이면 컬럼 기준으로 배치합니다.
	 * @return JSON 형식의 텍스트
	 */
	public String toJSON(boolean basedRecord);

	/**
	 * <p>데이터들을 HGF 형식 텍스트로 반환합니다.</p>
	 * 
	 * @return HGF 형식 텍스트
	 */
	public String toHGF();
	
	/**
	 * <p>모두 비어 있는 컬럼을 제거합니다.</p>
	 * 
	 * @param exceptWhenNameExists : 이름이 존재하는 컬럼 제거 안 함
	 */
	public void removeEmptyColumn(boolean exceptWhenNameExists);
	
	/**
	 * <p>모두 비어 있는 레코드들을 제거합니다.</p>
	 * 
	 */
	public void removeEmptyRow();
	
	/**
	 * <p>해당 컬럼을 제거합니다. 관련 데이터가 모두 제거됩니다.</p>
	 * 
	 * @param columnName : 제거할 컬럼 이름
	 */
	public void removeColumn(String columnName);
	
	/**
	 * <p>데이터 내용이 다른 사항들을 담은 새 테이블 셋을 반환합니다. 내용이 같은 데이터는 포함되지 않습니다.</p> 
	 * <p>주의 ! 테이블 셋 내용이 모두 같으면 null 을 반환합니다. 컬럼 수 혹은 레코드 수가 다르면 예외가 발생합니다.</p>
	 * 
	 * @param others : 다른 테이블 셋
	 * @return 양측의 데이터들의 다른 점을 병합한 테이블 셋
	 */
	public TableSet difference(TableSet others) throws InvalidInputException;
	
	/**
	 * <p>CREATE TABLE 문을 반환합니다.</p>
	 * 
	 * @param useVarchar2 : VARCHAR 대신 VARCHAR2 사용 여부
	 * @param options : 해당 컬럼에 삽입할 제약조건 등을 담은 객체
	 * @return CREATE TABLE SQL문
	 */
	public String  getCreateTableSQL(boolean useVarchar2, Map<String, String> options);
	
	/**
	 * <p>테이블 셋을 분석합니다. 입력한 함수 이름에 맞게 해당 분석 함수를 실행해 줍니다.</p>
	 * 
	 * @param analyzeFunction : 분석 함수 이름
	 * @param arguments : 매개 변수들
	 * @return 분석 결과 (테이블 셋 객체 형태)
	 */
	public TableSet analyze(String analyzeFunction, Map<String, String> arguments);
	
	/**
	 * <p>테이블 셋을 분석합니다. 입력한 함수 이름에 맞게 해당 분석 함수를 실행해 줍니다.</p>
	 * 
	 * @param analyzeFunction : 분석 함수 이름
	 * @param arguments : 매개 변수들
	 * @param otherTableSets : 다른 테이블 셋들
	 * @return 분석 결과 (테이블 셋 객체 형태)
	 */
	public TableSet analyze(String analyzeFunction, Map<String, String> arguments, TableSet ... otherTableSets);
	
	/**
	 * <p>테이블 셋의 데이터 타입, 컬럼 정보를 테이블 셋 형태로 반환합니다. 반환되는 테이블 셋은 다음과 같은 컬럼으로 구성됩니다.</p>
	 * <ul>
	 * 	  <li>TABLE_NAME : 테이블 이름</li>
	 *    <li>COLUMN_ID : 컬럼 ID (지원하지 않는 경우 null)</li>
	 *    <li>COLUMN_NAME : 컬럼 이름</li>
	 *    <li>DATA_TYPE_NAME : 데이터 타입 이름</li>
	 *    <li>DATA_LENGTH : 데이터 지원 최대 크기</li>
	 *    <li>CAN_BE_NULL : 널 가능 여부</li>
	 *    <li>DEFAULT_VALUE : 기본값</li>
	 * </ul>
	 * 
	 * @return 컬럼 및 데이터 타입 정보
	 */
	public TableSet typeInfo();

	/**
	 * <p>별명을 지정합니다. 실제 값에는 영향을 끼치지 않습니다.</p>
	 * 
	 * @param alias : 별명
	 */
	public void setAlias(String alias);

	/**
	 * <p>별명 값을 반환합니다.</p>
	 * 
	 * @return 별명
	 */
	public String getAlias();

	/**
	 * <p>고유 값을 반환합니다. 디버깅할 때 주로 사용합니다.</p>
	 * 
	 * @return 고유 값
	 */
	public Long getUniqueId();
	
	
	/**
	 * <p>테이블 셋 사용을 중단합니다. 대개 아무것도 하지 않습니다.</p>
	 * 
	 */
	@Override
	public void noMoreUse();
}