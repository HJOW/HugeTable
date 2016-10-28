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

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.text.Highlighter;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import de.sciss.syntaxpane.DefaultSyntaxKit;
import hjow.hgtable.Manager;
import hjow.hgtable.tableset.Column;
import hjow.hgtable.tableset.DefaultTableSet;
import hjow.hgtable.tableset.TableSet;
import hjow.hgtable.ui.swing.LineNumberTextArea;

/**
 * <p>이 클래스에는 GUI에 관련된 여러 정적 메소드들이 있습니다.</p>
 * 
 * @author HJOW
 *
 */
public class GUIUtil
{
	/**
	 * <p>GUI 툴킷을 초기화합니다.</p>
	 * 
	 * @param args : 매개 변수들
	 */
	public static void init(Map<String, String> args)
	{
		DefaultSyntaxKit.initKit();
		prepareFont(args);
	}
	
	/**
	 * <p>환경 설정 값을 반환합니다.</p>
	 * 
	 * @param key : 키
	 * @param args : 매개 변수들
	 * @return 값
	 */
	public static String getOption(String key, Map<String, String> args)
	{
		String results = Manager.getOption(key);
		if(DataUtil.isNotEmpty(results)) return results;
		
		results = args.get(key);
		if(DataUtil.isNotEmpty(results)) return results;
		
		return null;
	}
	
	/**
	 * 
	 * <p>글꼴 객체들입니다.</p>
	 */
	public static Font usingFont, usingFont2, usingFontB, usingFont2B, usingFontP, usingScriptFont;
	
	/**
	 * 
	 * <p>기본 글꼴 크기입니다.</p>
	 */
	public static int default_fontSize = 12;
	
	/**
	 * 
	 * <p>기본 스크립트 글꼴 크기입니다.</p>
	 */
	public static int script_fontSize = 15;
	
	/**
	 * 
	 * <p>기본 글꼴 이름입니다.</p>
	 */
	public static String usingFontName = null;
	
	/**
	 * 
	 * <p>스크립트용 기본 글꼴 이름입니다.</p>
	 */
	public static String usingScriptFontName = null;
	
	/**
	 * 
	 * <p>글꼴을 불러옵니다.</p>
	 * 
	 * @param args : 매개 변수들
	 */
	public static void prepareFont(Map<String, String> args)
	{
		boolean font_loaded = false;
		String osName = System.getProperty("os.name");
		String locale = System.getProperty("user.language");
		int fontSize = default_fontSize;
		int scriptFontSize = script_fontSize;
		boolean notDefaultFont = false;
		
		if(DataUtil.isNotEmpty(getOption("os_name"      , args))) osName = getOption("os_name", args);
		if(DataUtil.isNotEmpty(getOption("user_language", args))) locale = getOption("user_language", args);
		
		StringTokenizer commaTokenizer;
		
		List<String> fontFamilies = new Vector<String>();
		if(DataUtil.isNotEmpty(getOption("fontFamily", args)))
		{
			commaTokenizer = new StringTokenizer(getOption("fontFamily", args).trim(), ",");
			while(commaTokenizer.hasMoreTokens())
			{
				fontFamilies.add(commaTokenizer.nextToken());
			}
		}
		
		List<String> scriptFontFamilies = new Vector<String>();
		if(DataUtil.isNotEmpty(getOption("scriptFontFamily", args)))
		{
			commaTokenizer = new StringTokenizer(getOption("scriptFontFamily", args).trim(), ",");
			while(commaTokenizer.hasMoreTokens())
			{
				scriptFontFamilies.add(commaTokenizer.nextToken());
			}
		}
		
		if(osName == null)
		{
			osName = System.getProperty("os.name");
		}
		if(locale == null)
		{
			locale = System.getProperty("user.language");
		}
		if(locale.equalsIgnoreCase("auto"))
		{
			locale = System.getProperty("user.language");
		}
		
		try
		{
			fontSize = Integer.parseInt(getOption("fontSize", args));
		}
		catch(Throwable e)
		{
			fontSize = default_fontSize;
		}
		
		try
		{
			scriptFontSize = Integer.parseInt(getOption("scriptFontSize", args));
		}
		catch(Throwable e)
		{
			scriptFontSize = script_fontSize;
		}
		
		FileInputStream finfs = null;
		ObjectInputStream objs = null;
		
		
		font_loaded = loadFontFiles(args, fontSize, scriptFontSize);
		
		String mainFont = "돋움";
		String scriptFont = "돋움";
		boolean mainFontDecided = false;
		boolean scriptFontDecided = false;
		try
		{
			GraphicsEnvironment gr = GraphicsEnvironment.getLocalGraphicsEnvironment();
			String[] fontList = gr.getAvailableFontFamilyNames();
			
			mainFontDecided = false;
			scriptFontDecided = false;
			
			for(int i=0; i<fontList.length; i++)
			{
				if((! mainFontDecided))
				{
					for(int j=0; j<fontFamilies.size(); j++)
					{
						if(fontList[i].equals(fontFamilies.get(j)))
						{
							mainFont = fontList[i];
							mainFontDecided = true;
							notDefaultFont = true;
							break;
						}
					}
				}
				
				if((! mainFontDecided) && (fontList[i].equals("나눔고딕코딩") || fontList[i].equalsIgnoreCase("NanumGothicCoding")))
				{
					mainFont = fontList[i];
					mainFontDecided = true;
					notDefaultFont = false;
				}
				
				if((! scriptFontDecided))
				{
					for(int j=0; j<scriptFontFamilies.size(); j++)
					{
						if(fontList[i].equals(scriptFontFamilies.get(j)))
						{
							scriptFont = fontList[i];
							scriptFontDecided = true;
							break;
						}
					}
				}
				
				if((! scriptFontDecided) && (fontList[i].equals("나눔고딕코딩") || fontList[i].equalsIgnoreCase("NanumGothicCoding")))
				{
					scriptFont = fontList[i];
					scriptFontDecided = true;
				}
				
				if(mainFontDecided && scriptFontDecided) break;
			}
		} 
		catch (Exception e1)
		{
			mainFont = "돋움";
			scriptFont = "돋움";
			notDefaultFont = false;
		}
		
		if(! font_loaded)
		{
			try
			{
				File fontObjectFile = new File(getOption("config_path", args) + "defaultFont.font");
				if(fontObjectFile.exists())
				{
					finfs = new FileInputStream(fontObjectFile);				
					objs = new ObjectInputStream(finfs);
					usingFont = (Font) objs.readObject();
					usingScriptFont = usingFont.deriveFont(Font.PLAIN, scriptFontSize);
					usingFont2 = usingFont.deriveFont(Font.PLAIN, usingFont.getSize() * 2);
					usingFontB = usingFont.deriveFont(Font.BOLD, usingFont.getSize());
					usingFont2B = usingFont.deriveFont(Font.BOLD, usingFont.getSize() * 2);
					usingFontP = usingFont.deriveFont(Font.BOLD, fontSize - 2);
				}
			} 
			catch (Exception e)
			{
				e.printStackTrace();
				font_loaded = false;
			}
			finally
			{
				try
				{
					objs.close();
				}
				catch(Throwable e)
				{
					
				}
				try
				{
					finfs.close();
				}
				catch(Throwable e)
				{
					
				}
			}
		}
		
		if(! font_loaded)
		{
			if(osName.equalsIgnoreCase("Windows Vista") || osName.equalsIgnoreCase("Windows 7")
					|| osName.equalsIgnoreCase("Windows 8")|| osName.equalsIgnoreCase("Windows 8.1"))
			{
				if(notDefaultFont)
				{
					usingFontName = mainFont;
					if(DataUtil.isNotEmpty(scriptFont)) usingScriptFontName = scriptFont;
					else usingScriptFontName = usingFontName;
				}
				else
				{
					if(locale.startsWith("ko") || locale.startsWith("KO") || locale.startsWith("kr") || locale.startsWith("KR") || locale.startsWith("kor") || locale.startsWith("KOR"))
					{
						usingFontName = mainFont;
						if(DataUtil.isNotEmpty(scriptFont)) usingScriptFontName = scriptFont;
						else usingScriptFontName = usingFontName;
					}
					else
					{
						usingFontName = "Arial";
						usingScriptFontName = usingFontName;
					}
				}
			}
			else if(osName.startsWith("Windows") || osName.startsWith("windows") || osName.startsWith("WINDOWS"))
			{
				if(notDefaultFont)
				{
					usingFontName = mainFont;
					if(DataUtil.isNotEmpty(scriptFont)) usingScriptFontName = scriptFont;
					else usingScriptFontName = usingFontName;
				}
				else
				{
					if(osName.endsWith("95") || osName.endsWith("98") || osName.endsWith("me") || osName.endsWith("ME") || osName.endsWith("Me") || osName.endsWith("2000"))
					{
						if(locale.startsWith("ko") || locale.startsWith("KO") || locale.startsWith("kr") || locale.startsWith("KR") || locale.startsWith("kor") || locale.startsWith("KOR"))
							usingFontName = "돋움";
						else
							usingFontName = "Dialog";
					}
					else
					{
						if(locale.startsWith("ko") || locale.startsWith("KO") || locale.startsWith("kr") || locale.startsWith("KR") || locale.startsWith("kor") || locale.startsWith("KOR"))
							usingFontName = mainFont;
						else
							usingFontName = "Arial";
					}
					if(DataUtil.isNotEmpty(scriptFont)) usingScriptFontName = scriptFont;
					else usingScriptFontName = usingFontName;
				}
				
			}
			try
			{
				usingFont = new Font(usingFontName, Font.PLAIN, fontSize);
				usingScriptFont = new Font(usingScriptFontName, Font.PLAIN, scriptFontSize);
				usingFontB = new Font(usingFontName, Font.BOLD, fontSize);
				usingFont2 = new Font(usingFontName, Font.PLAIN, fontSize * 2);
				usingFont2B = new Font(usingFontName, Font.BOLD, fontSize * 2);
				usingFontP = usingFont.deriveFont(Font.BOLD, fontSize - 2);
				font_loaded = true;
			} 
			catch (Exception e)
			{
				e.printStackTrace();
				font_loaded = false;
				usingFont = null;
			}
		}
		if(font_loaded)
		{
			try
			{
				UIManager.put("OptionPane.messageFont", new FontUIResource(usingFont));
				UIManager.put("OptionPane.font", new FontUIResource(usingFont));
				UIManager.put("OptionPane.buttonFont", new FontUIResource(usingFont));
				UIManager.put("JOptionPane.font", new FontUIResource(usingFont));
			}
			catch(Throwable e)
			{
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * <p>폰트 파일이 있는지 확인해 있으면 불러옵니다.</p>
	 * 
	 * @param args : 매개 변수들
	 * @param fontSize : 폰트 크기
	 * @param scriptFontSize : 스크립트 폰트 크기
	 * @return 폰트 불러오기 성공 여부
	 */
	protected static boolean loadFontFiles(Map<String, String> args, int fontSize, int scriptFontSize)
	{
		FileInputStream finfs = null;
		BufferedInputStream infs = null;
		boolean font_loaded = false;
		
		try
		{
			File fontFile = new File(getOption("config_path", args) + "basic_font.ttf");
			if(fontFile.exists())
			{
				finfs = new FileInputStream(fontFile);
				infs = new BufferedInputStream(finfs);
				usingFont = Font.createFont(Font.TRUETYPE_FONT, infs);
				usingFont = usingFont.deriveFont(Font.PLAIN, fontSize);
				usingScriptFont = usingFont.deriveFont(Font.PLAIN, scriptFontSize);
				usingFont2 = usingFont.deriveFont(Font.PLAIN, fontSize * 2);
				usingFontB = usingFont.deriveFont(Font.BOLD, fontSize);
				usingFont2B = usingFont.deriveFont(Font.BOLD, fontSize * 2);
				usingFontP = usingFont.deriveFont(Font.BOLD, fontSize - 2);
				font_loaded = true;
			}
			else font_loaded = false;
		}
		catch(Throwable e)
		{
			e.printStackTrace();
			font_loaded = false;
		}
		finally
		{
			try
			{
				infs.close();
			}
			catch(Throwable e)
			{
				
			}
			try
			{
				finfs.close();
			}
			catch(Throwable e)
			{
				
			}
		}
		
		try
		{
			File fontFile = new File(getOption("config_path", args) + "script_font.ttf");
			if(fontFile.exists())
			{
				finfs = new FileInputStream(fontFile);
				infs = new BufferedInputStream(finfs);
				usingScriptFont = Font.createFont(Font.TRUETYPE_FONT, infs);
			}
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				infs.close();
			}
			catch(Throwable e)
			{
				
			}
			try
			{
				finfs.close();
			}
			catch(Throwable e)
			{
				
			}
		}
		return font_loaded;
	}

	/**
	 * 
	 * <p>컴포넌트의 글꼴을 변경합니다. 해당 컴포넌트에 포함된 하위 컴포넌트 전체에 적용됩니다.</p>
	 * 
	 * @param comp : 컴포넌트
	 * @param font : 폰트 객체
	 */
	public static void setFontRecursively(Component comp, Font font)
	{
		setFontRecursively(comp, font, 1000);
	}
	
	/**
	 * 
	 * <p>컴포넌트의 글꼴을 변경합니다. 해당 컴포넌트에 포함된 하위 컴포넌트 전체에 적용됩니다.</p>
	 * 
	 * @param comp : 컴포넌트
	 * @param font : 폰트 객체
	 * @param prevent_infiniteLoop : 무한 반복 방지용 실행 횟수 제한
	 */
	public static void setFontRecursively(Component comp, Font font, int prevent_infiniteLoop)
	{
		try
		{
			if(font == null) return;
			try
			{
				comp.setFont(font);
			}
			catch(Throwable e)
			{
				
			}
			int max_limits = prevent_infiniteLoop;
			if(comp instanceof Container)
			{
				Container cont = (Container) comp;
				int ub = cont.getComponentCount();
				for(int  j=0; j<ub; j++)
				{
					ub = cont.getComponentCount();
					if(ub > max_limits) ub = max_limits;
					max_limits--;
					if(max_limits <= 0) break;
					setFontRecursively(cont.getComponent(j), font, max_limits);					
				}
			}
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * <p>강조해야 할 키워드를 지정합니다.</p>
	 * 
	 * @param keywordList : 키워드 리스트
	 * @param syntaxKind : 문법 종류
	 */
	public static void initKeywords(List<String> keywordList, String syntaxKind)
	{
		keywordList.clear();
		
		if(syntaxKind.equalsIgnoreCase("SQL") || syntaxKind.equalsIgnoreCase("text/sql"))
		{
			keywordList.add("SELECT");
			keywordList.add("FROM");
			keywordList.add("WHERE");
			keywordList.add("AND");
			keywordList.add("OR");
			keywordList.add("IS");
			keywordList.add("NULL");
			keywordList.add("BETWEEN");
			keywordList.add("CASE");
			keywordList.add("WHEN");
			keywordList.add("THEN");
			keywordList.add("ELSE");
			keywordList.add("END");
			keywordList.add("BY");
			keywordList.add("GROUP");
			keywordList.add("ORDER");
			keywordList.add("INSERT");
			keywordList.add("INTO");
			keywordList.add("UPDATE");
			keywordList.add("DELETE");
			keywordList.add("CREATE");
			keywordList.add("ALTER");
			keywordList.add("GRANT");
			keywordList.add("REVOKE");
			keywordList.add("TABLE");
			keywordList.add("VIEW");
		}
		else if(syntaxKind.equalsIgnoreCase("JScript") || syntaxKind.equalsIgnoreCase("text/javascript"))
		{
			keywordList.add("var");
			keywordList.add("if");
			keywordList.add("else");
			keywordList.add("for");
			keywordList.add("while");
			keywordList.add("try");
			keywordList.add("catch");
		}
	}
	
	/**
	 * <p>문법 강조를 위해 스타일을 준비합니다.</p>
	 * 
	 * @param textPanel : 스타일을 적용할 텍스트 영역
	 */
	public static void initStyle(LineNumberTextArea textPanel)
	{
		Style greenStyle = textPanel.getTextPane().addStyle("green", null);
		StyleConstants.setForeground(greenStyle, Color.GREEN);
		Style orangeStyle = textPanel.getTextPane().addStyle("orange", null);
		StyleConstants.setForeground(orangeStyle, Color.ORANGE);
		Style blackStyle = textPanel.getTextPane().addStyle("default", null);
		StyleConstants.setForeground(blackStyle, textPanel.getTextPanel().getForeground());
		
		Style lineStyle = textPanel.getLineNumberPanel().addStyle("default", null);
		StyleConstants.setForeground(lineStyle, textPanel.getLineNumberPanel().getForeground());
		
		StyleConstants.setFontFamily(greenStyle, usingFontName);
		StyleConstants.setFontFamily(orangeStyle, usingFontName);
		StyleConstants.setFontFamily(blackStyle, usingFontName);
		StyleConstants.setFontFamily(lineStyle, usingFontName);
	}
	
	/**
	 * <p>텍스트 영역 내용이 바뀔 때 적용할 텍스트 강조 작업을 합니다.</p>
	 * 
	 * @param textPanel : 해당 텍스트 영역
	 * @param documents : 텍스트 영역의 문서 객체
	 * @param keywords : 키워드 리스트
	 * @throws Exception
	 */
	public static void treatTextChanged(LineNumberTextArea textPanel, StyledDocument documents, List<String> keywords) throws Exception
	{
		Highlighter highlighter = textPanel.getTextPanel().getHighlighter();
		
		List<TextBlock> blocks = DataUtil.getBlocks(' ', textPanel.getText(), true);
		String nowBlockText;
		
		highlighter.removeAllHighlights();
		documents.setCharacterAttributes(0, documents.getLength(), textPanel.getTextPane().getStyle("default"), true);
		
		int startPos, endPos;
		for(int i=0; i<blocks.size(); i++)
		{
			nowBlockText = blocks.get(i).getContents().trim();
			startPos = blocks.get(i).getStartPosition();
			endPos = blocks.get(i).getEndPosition();
			
			if(nowBlockText.startsWith("/*") && nowBlockText.endsWith("*/"))
			{
//				Main.println("StartPos : " + startPos);
//				Main.println("EndPos : " + endPos);
//				Main.println("Contents : " + nowBlockText);
				try
				{
					documents.setCharacterAttributes(startPos, (endPos - startPos), textPanel.getTextPane().getStyle("green"), true);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			else if(keywords.contains(nowBlockText))
			{
				try
				{
					documents.setCharacterAttributes(startPos, (endPos - startPos), textPanel.getTextPane().getStyle("orange"), true);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * <p>테이블 셋 객체를 Swing 호환 테이블 모델 객체로 변환합니다.</p>
	 * 
	 * @param tables : 테이블 셋 객체
	 * @return 테이블 모델 객체
	 */
	public static DefaultTableModel toTableModel(TableSet tables)
	{
		DefaultTableModel tableModel = new DefaultTableModel();
		
		for(int i=0; i<tables.getColumns().size(); i++)
		{
			tableModel.addColumn(tables.getColumn(i).getName());
		}
		
		Vector<String> records;
		for(int i=0; i<tables.getRecordCount(); i++)
		{
			records = new Vector<String>();
			for(Object d : tables.getRecord(i).getDatas())
			{
				records.add(String.valueOf(d));
			}
			tableModel.addRow(records);
		}
		
		return tableModel;
	}
	
	/**
	 * <p>Swing 의 테이블 모델 객체를 테이블 셋 객체로 변환합니다. 타입은 텍스트(String) 로 통일됩니다.</p>
	 * 
	 * @param model : 테이블 모델 객체
	 * @return 테이블 셋 객체
	 */
	public static TableSet toTableSet(TableModel model)
	{
		TableSet newTableSet = new DefaultTableSet();
		newTableSet.setColumns(new Vector<Column>());
		
		Column newColumn;
		for(int i=0; i<model.getColumnCount(); i++)
		{
			newColumn = new Column(model.getColumnName(i), Column.TYPE_STRING);
			newTableSet.getColumns().add(newColumn);
		}
		
		for(int i=0; i<model.getRowCount(); i++)
		{
			for(int j=0; j<model.getColumnCount(); i++)
			{
				newTableSet.getColumn(j).getData().add(String.valueOf(model.getValueAt(i, j)));
			}
		}
		
		return newTableSet;
	}
}
