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

import java.awt.Window;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

/**
 * <p>GUI 환경에서 파일 액세스를 보다 원활하게 하기 위한 여러 정적 메소드들이 있습니다.</p>
 * 
 * @author HJOW
 *
 */
public class GUIStreamUtil
{
	protected static JFileChooser fileChooser = new JFileChooser();
	
	/**
	 * <p>파일 대화 상자를 띄워 파일 내용을 텍스트로 읽습니다.</p>
	 * 
	 * @param parent : 부모 창 객체
	 * @param fileFilter : 파일 필터
	 * @param defaultPath : 대화 상자 띄울 때 처음 보일 디렉토리 (null 가능)
	 * @return 파일 내용 혹은 null (불러오지 않고 창을 닫았을 때)
	 */
	public static String readTextFrom(Window parent, FileFilter fileFilter, String defaultPath)
	{
		return readTextFrom(parent, fileFilter, new File(defaultPath));
	}
	
	/**
	 * <p>파일 대화 상자를 띄워 파일 내용을 텍스트로 읽습니다.</p>
	 * 
	 * @param parent : 부모 창 객체
	 * @param fileFilter : 파일 필터
	 * @param defaultPath : 대화 상자 띄울 때 처음 보일 디렉토리 (null 가능)
	 * @return 파일 내용 혹은 null (불러오지 않고 창을 닫았을 때)
	 */
	public static String readTextFrom(Window parent, FileFilter fileFilter, File defaultPath)
	{
		String result = null;
		if(DataUtil.isNotEmpty(defaultPath)) fileChooser.setCurrentDirectory(defaultPath);
		if(fileFilter != null) fileChooser.setFileFilter(fileFilter);
		int selects = fileChooser.showOpenDialog(parent);
		if(selects == JFileChooser.APPROVE_OPTION)
		{
			File file = fileChooser.getSelectedFile();
			result = StreamUtil.readText(file, "UTF-8");
		}
		return result;
	}
	
	/**
	 * <p>파일 대화 상자를 띄워 파일 내용을 텍스트로 저장합니다.</p>
	 * 
	 * @param parent : 부모 창 객체
	 * @param fileFilter : 파일 필터
	 * @param defaultPath : 대화 상자 띄울 때 처음 보일 디렉토리 (null 가능)
	 * @return 저장 여부
	 */
	public static boolean saveText(Window parent, String contents, FileFilter fileFilter, String defaultPath)
	{
		return saveText(parent, contents, fileFilter, new File(defaultPath));
	}
	
	/**
	 * <p>파일 대화 상자를 띄워 파일 내용을 텍스트로 저장합니다.</p>
	 * 
	 * @param parent : 부모 창 객체
	 * @param fileFilter : 파일 필터
	 * @param defaultPath : 대화 상자 띄울 때 처음 보일 디렉토리 (null 가능)
	 * @return 저장 여부
	 */
	public static boolean saveText(Window parent, String contents, FileFilter fileFilter, File defaultPath)
	{
		if(DataUtil.isNotEmpty(defaultPath)) fileChooser.setCurrentDirectory(defaultPath);
		if(fileFilter != null) fileChooser.setFileFilter(fileFilter);
		int selects = fileChooser.showSaveDialog(parent);
		if(selects == JFileChooser.APPROVE_OPTION)
		{
			File file = fileChooser.getSelectedFile();
			StreamUtil.saveFile(file, contents);
			return true;
		}
		return false;
	}
}
