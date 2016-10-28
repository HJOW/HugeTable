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

import java.util.Map;

import hjow.hgtable.Main;
import hjow.hgtable.tableset.Column;
import hjow.hgtable.tableset.DefaultTableSet;

/**
 * <p>이 클래스에는 기본 옵션들을 지정하는 정적 메소드가 있습니다.</p>
 * 
 * @author HJOW
 *
 */
public class OptionUtil
{
	public static void setDefaultOptions(Map<String, String> params, boolean onlyDuty) throws Exception
	{
		/*
		  사용자 홈 경로를 지정합니다.
		  Windows 의 경우, C:\Users\[사용자이름]\
		  Unix 의 경우, /home/[사용자이름]/
		 */
		String defaultPath = System.getProperty("user.home");
		params.put("user_home", defaultPath);
		
		/*
		 시스템의 디렉토리 구분자를 지정합니다.
		 Windows 의 경우 \ 기호를,
		 Unix 의 경우 / 기호를 사용합니다.
		 */
		String fileSep = System.getProperty("file.separator");
		params.put("file_separator", fileSep);
		
		/*
		 언어 설정입니다. 
		 */
		String userLang = System.getProperty("user.language");
		if(! onlyDuty) params.put("user_language", userLang);
		
		/*
		 OS 이름입니다. 
		 */
		String osName = System.getProperty("os.name");
		params.put("os_name", osName);
					
		/*
		 프로그램 제목입니다. 환경설정 파일 경로에 영향을 미칩니다. 
		 */
		params.put("program_title", LicenseUtil.titles);
		
		/*
		 환경설정 파일이 저장되고 불러올 디렉토리 경로를 지정합니다.
		 저장할 권한이 있도록 사용자 홈 폴더 아래에 프로그램 제목을 이름으로 디렉토리를 사용합니다.
		 */
		params.put("config_path", defaultPath + fileSep + params.get("program_title").replace(" ", "") + fileSep);
		
		/*
		 모듈에 대한 환경설정 파일이 저장되고 불러올 디렉토리 경로를 지정합니다.
		 */
		params.put("module_config_path", params.get("config_path") + "module_configs" + fileSep);
		
		/*
		 자바 런타임 버전입니다. 
		 */
		params.put("java_version_full", JavaUtil.fullVer());
		
		/*
		 자바 런타임 버전입니다. 세대 번호만 포함합니다. 
		 */
		params.put("java_version", String.valueOf(JavaUtil.ver()));
		
		/*
		 파일을 읽거나 쓸 때 사용할 기본 캐릭터 셋을 지정합니다.
		 */
		if(! onlyDuty) params.put("file_charset", "UTF-8");
		
		/*
		 안전 모드로 실행할 지 여부를 지정합니다.
		 */
		if(! onlyDuty) params.put("safe_mode", "false");
		
		/*
		 시험 중인 기능을 사용할 지 여부를 지정합니다. 
		 */
		if(! onlyDuty) params.put("use_testing_functions", "false");
		
		/*
		 자주 선택하는 항목을 저장할 지 여부를 지정합니다. GUI 모드에서만 유효합니다.
		 */
		if(! onlyDuty) params.put("save_frequency", "true");
		
		/*
		 라이센스 내용에 동의하는지 여부를 동의 안 함으로 지정합니다.
		 동의 안 함으로 지정해야 이후 프로그램 실행 시 라이센스 창이 나타납니다. 
		 */
		if(! onlyDuty) params.put("agree_license", "false");
		
		/*
		 여러 탭 사용 여부를 지정합니다. GUI 모드에서만 유효합니다.
		 주의 : 아직 불안정한 기능입니다.
		 */
		if(! onlyDuty) params.put("use_multi_tab", "false");
		
		/*
		 결과로 발생한 테이블 셋을 별도의 탭에서 보는 기능을 사용할 지 여부를 지정합니다. GUI 모드에서만 유효합니다.
		 */
		if(! onlyDuty) params.put("use_result_tab", "true");
		
		/*
		 GUI 화면에서 타이머 기능을 사용합니다.
		 */
		if(! onlyDuty) params.put("use_gui_timer", "true");
		
		/*
		 타이머 기능 사용 시 타이머 정확도를 지정합니다. 지정한 숫자를 밀리세컨드 단위로 현재 시간을 측정하므로 값이 작을수록 정확합니다. 20 ~ 1000 사이의 값을 사용합니다.
		 */
		if(! onlyDuty) params.put("gui_timer_gap", "50");
		
		/*
		 결과로 발생한 테이블 셋을 별도의 창에서 보는 기능을 사용할 지 여부를 지정합니다. GUI 모드에서만 유효합니다.
		 */
		if(! onlyDuty) params.put("use_result_window", "false");
		
		/*
		 GUI 모드에서, 실행 시 중간 구분 막대의 처음 위치를 비율로 지정합니다. 
		 * 
		 */
		if(! onlyDuty) params.put("main_divider_ratio", "0.5");
		
		/*
		 종료 시 환경 설정을 저장할 지 여부를 선택합니다.
		 */
		if(! onlyDuty) params.put("save_config_after_close", "true");
		
		/*
		 종료 시 언어 설정을 저장할 지 여부를 선택합니다.
		 */
		if(! onlyDuty) params.put("save_stringtable_after_close", "false");
		
		/*
		 종료 시 언어 설정(스트링 테이블)에 없는 문구들 표시 여부를 선택합니다.
		 */
		if(! onlyDuty) params.put("see_stringtable_debug", "false");
		
		/*
		 TableSet 객체 출력 시 컬럼 사이의 구분자를 지정합니다. 
		 */
		if(! onlyDuty) params.put("default_column_delimiter", "auto");
		
		/*
		 처음 실행 시 JDBC 드라이버를 불러올 지 여부를 설정합니다.
		 */
		if(! onlyDuty) params.put("load_basic_jdbc", "true");
		
		/*
		 레코드를 다룰 때 컬럼명에 대해 대소문자를 구분할 지를 지정합니다.
		 */
		if(! onlyDuty) params.put("case_columnname", "false");
		
		/*
		 데이터 소스에 데이터 삽입 시 날짜 관련 타입 데이터는 생략할 지를 지정합니다. 
		 */
		if(! onlyDuty) params.put("skip_date_form", "true");
		
		/*
		 데이터 소스와의 연결이 오랜 시간 무사용으로 접속이 끊기는 것을 방지하는 기능을 가능하면 사용할 지를 지정합니다.
		 */
		if(! onlyDuty) params.put("prevent_timeout", "false");
		
		/*
		 데이터 소스 무사용 끊김 방지 기능 사용 시, 몇 초에 한 번씩 작업을 수행할 지를 지정합니다.
		 */
		if(! onlyDuty) params.put("prevent_timeout_gap", "200");
		
		/*
		 처음 실행 시 환경 설정 폴더에서 처음에 추가로 불러와야 할 클래스 풀네임들이 적혀 있는 파일을 지정합니다.
		 null 을 넣거나 빈 칸을 넣는다면 이 기능을 사용하지 않습니다.
		 */
		if(! onlyDuty) params.put("load_basic_jdbc_list_file", params.get("config_path") + "load_classes.txt");
		
		/*
		 GUI 실행 시 얼마나 로딩되었는지를 보여주는 창을 사용합니다. 
		 */
		if(! onlyDuty) params.put("use_state_view", "true");
		
		/*
		 GUI 실행 시 폰트 크기를 지정합니다.
		 reload_fonts 가 true 여야 이 옵션들이 효력이 있습니다.
		 */
		if(! onlyDuty) params.put("fontSize"      , String.valueOf(GUIUtil.default_fontSize));
		if(! onlyDuty) params.put("scriptFontSize", String.valueOf(GUIUtil.script_fontSize));
		
		/*
		 GUI 실행 시 기본 폰트를 지정합니다. UI 상에 적용될 폰트, 그리고 스크립트 란에 적용될 폰트를 따로 지정할 수 있습니다.
		 빈 칸으로 지정 시 우선 나눔고딕코딩 폰트를 찾고, 사용이 불가능할 경우 OS 기본 폰트를 사용합니다.
		 
		 reload_fonts 가 true 여야 이 옵션들이 효력이 있습니다.
		 */
		if(! onlyDuty) params.put("fontFamily"      , "");
		if(! onlyDuty) params.put("scriptFontFamily", "");
		
		/*
		 이 옵션을 true 로 하면 옵션들을 적용할 때 폰트를 다시 불러옵니다.
		 
		 이 옵션을 true 로 해야 위의 fontSize, scriptFontSize, fontFamily, scriptFontFamily 옵션들이 효력을 발휘합니다.
		 이 옵션을 false 로 하면, 위의 옵션을 사용 못하는 대신 프로그램이 더 빨리 실행됩니다.
		 */
		if(! onlyDuty) params.put("reload_fonts", "true");
		
		/*
		 로그 출력 옵션을 지정합니다.
		 */
		if(! onlyDuty) 
		{
			params.put("show_debug", "false");
			params.put("show_notice", "true");
			params.put("show_warn", "true");
			params.put("show_error", "true");
		}
		
		/*
		 GUI 모드에서, 문법 하이라이트 기능을 사용할 지를 지정합니다.
		 */
		if(! onlyDuty) params.put("use_highlight_syntax", "false");
		
		/*
		 기본으로 불러올 클래스 목록입니다.
		 */
		StringBuffer defaultClassList = new StringBuffer("");
		
		defaultClassList = defaultClassList.append("org.apache.poi.hssf.usermodel.HSSFWorkbook" + ";");
		defaultClassList = defaultClassList.append("org.apache.poi.ss.usermodel.Cell" + ";");
		defaultClassList = defaultClassList.append("org.apache.poi.ss.usermodel.DateUtil" + ";");
		defaultClassList = defaultClassList.append("org.apache.poi.ss.usermodel.FormulaEvaluator" + ";");
		defaultClassList = defaultClassList.append("org.apache.poi.ss.usermodel.Row" + ";");
		defaultClassList = defaultClassList.append("org.apache.poi.ss.usermodel.Sheet" + ";");
		defaultClassList = defaultClassList.append("org.apache.poi.ss.usermodel.Workbook" + ";");
		defaultClassList = defaultClassList.append("org.apache.poi.xssf.usermodel.XSSFWorkbook");
		
		if(! onlyDuty) params.put("default_class_list", defaultClassList.toString());
		
		/*
		 클래스를 불러온 후 인스턴스 생성을 시도할 지 여부를 선택합니다.
		 JNI 를 사용하는 라이브러리를 불러오려는 경우 문제가 생길 수 있습니다.
		 */
		if(! onlyDuty) params.put("try_create_instance_after_load", "false");
		
		/*
		 GUI 모드에서 보일 테마를 지정합니다. (system, nimbus, metal, 혹은 Swing 의 LookAndFeel 클래스명 사용 가능)
		 */
		if(! onlyDuty) params.put("guitheme", "system");
		
		/*
		 기본 날짜 형식을 지정합니다. 
		 */
		if(! onlyDuty) params.put("defaultDateFormat", Column.DEFAULT_DATE_FORMAT);
		
		/*
		 기본 테이블셋 클래스명을 지정합니다. 
		 */
		if(! onlyDuty) params.put("defaultTableSetClass", DefaultTableSet.class.getName());
		
		/*
		 기타 설정을 삽입합니다.
		 */
		if(! onlyDuty) 
		{
			params.put("script_mode", "false");      // 이 옵션이 true 이면 처음 실행 시 자동으로 J스크립트 모드로 바로 들어갑니다.
			params.put("script_file_mode", "false"); // 이 옵션이 true 이면 처음 실행 시 자동으로 script_file 에 지정한 파일 내용을 J스크립트로 실행합니다.
			params.put("script_file", "null");       // 이 옵션은 script_file_mode 사용 시 반드시 같이 사용해야 합니다. 실행할 파일 경로와 이름을 지정합니다.
			params.put("after_exit", "false");       // 이 옵션이 true 이면 처음 실행 시 자동 실행할 내용이 실행된 후 바로 종료됩니다.
		}
		
		/*
		 버전 정보를 삽입합니다.
		 */
		String versionText = "v";
		for(int i=0; i<Main.version.length; i++)
		{
			versionText = versionText + String.valueOf(Main.version[i]);
			if(i < Main.version.length - 1) versionText = versionText + ".";
		}
		params.put("version", versionText);
		
		params.put("copyright_owner", LicenseUtil.copyrightOwner);
		params.put("copyright_year", LicenseUtil.copyrightYear);
		params.put("copyright_email", LicenseUtil.copyrightEmail);
		params.put("made_by", "Made by " + params.get("copyright_owner") + " ( EMAIL : " + params.get("copyright_email") + " )");
	}
}
