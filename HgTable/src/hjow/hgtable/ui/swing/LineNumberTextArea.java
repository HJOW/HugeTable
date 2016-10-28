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

package hjow.hgtable.ui.swing;

import hjow.hgtable.Manager;
import hjow.hgtable.util.DataUtil;
import hjow.hgtable.util.GUIUtil;
import hjow.hgtable.util.InvalidInputException;
import hjow.swing.jsonSwing.JSONCore;
import hjow.swing.jsonSwing.JSONSwingObject;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.PopupMenu;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.Vector;

import javax.swing.JEditorPane;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.event.AncestorListener;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.StyleContext;
import javax.swing.undo.UndoManager;

/**
 * 
 * <p>이 Swing 컴포넌트는 여러 줄 텍스트와 줄 번호까지 보여줍니다.</p>
 * 
 * @author HJOW
 *
 */
public class LineNumberTextArea extends JPanel implements CodeEditorPane, JSONSwingObject
{
	private static final long serialVersionUID = 2879465797975417227L;
	public static final String JSON_KEYWORD = "LINE_NUMBER_AREA";
	
	protected List<String> keywords = new Vector<String>();
	protected JTextPane textPanel;
	protected JScrollPane textScroll;
	protected JTextPane lineNumberPanel;
	protected UndoManager undomanager;
	protected boolean hideNumberFieldWhenEmpty = true;
	protected boolean numberFieldIsHidden = true;
	
	protected DefaultStyledDocument documents;
	protected StyleContext styles;
	protected JPopupMenu popups;
	protected JMenuItem popupsCopy;
	protected JMenuItem popupsCut;
	protected JMenuItem popupsPaste;
	
	protected Window superWindow;
	protected FindDialog searchDialog;
	protected JMenuItem popupsSearch;
	
	protected boolean useHighlighting = false;
	
	static
	{
		JSONCore.addSample(new LineNumberTextArea());
	}
	
	@Override
	public String getJsonKeyword()
	{
		return JSON_KEYWORD;
	}
	
	/**
	 * <p>기본 생성자입니다. 컴포넌트들을 초기화합니다.</p>
	 * 
	 */
	public LineNumberTextArea()
	{
		init();
	}
	
	/**
	 * <p>검색 기능을 사용할 경우 이 생성자를 사용해야 합니다.</p>
	 * 
	 * @param window : Frame 혹은 JFrame 객체
	 */
	public LineNumberTextArea(Window window)
	{
		this.superWindow = window;
		init();
	}
	
	/**
	 * <p>기본 생성자입니다. 컴포넌트들을 초기화합니다.</p>
	 * 
	 */
	public void init()
	{
		setLayout(new BorderLayout());
		
		try
		{
			useHighlighting = DataUtil.parseBoolean(Manager.getOption("use_highlight_syntax"));
		}
		catch (InvalidInputException e1)
		{
			useHighlighting = false;
		}
		
		textScroll = new JScrollPane();		
		lineNumberPanel = new JTextPane();
		
		if(useHighlighting)
		{
			styles = new StyleContext();
			documents = new DefaultStyledDocument(styles);
			textPanel = new JTextPane(documents);
			
			GUIUtil.initStyle(this);
			GUIUtil.initKeywords(keywords, "SQL");
		}
		else
		{
			textPanel = new JTextPane();
		}
		
		textPanel.setEditable(true);
		
		if(superWindow != null)
		{
			searchDialog = new FindDialog(this, superWindow);
		}
		else searchDialog = null;
		
		popups = new JPopupMenu();
		
		popupsCopy = new JMenuItem(Manager.applyStringTable("Copy"));
		popups.add(popupsCopy);
		
		popupsCut = new JMenuItem(Manager.applyStringTable("Cut"));
		popups.add(popupsCut);
		
		popupsPaste = new JMenuItem(Manager.applyStringTable("Paste"));
		popups.add(popupsPaste);
		
		if(searchDialog != null)
		{
			popupsSearch = new JMenuItem(Manager.applyStringTable("Search"));
//			popupsSearch.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK));
			popups.addSeparator();
			popups.add(popupsSearch);
		}
		else popupsSearch = null;
		
		lineNumberPanel.setEditable(false);
		
		textPanel.getDocument().addDocumentListener(new DocumentListener()
		{			
			private String getText()
			{
				int caretPos = textPanel.getDocument().getLength();
				Element el = textPanel.getDocument().getDefaultRootElement();
				
				StringBuffer text = new StringBuffer("1\n");
				for(int i=2; i<el.getElementIndex(caretPos) + 2; i++)
				{
					text = text.append(String.valueOf(i));
					text = text.append("\n");
				}
				SwingUtilities.invokeLater(new Runnable()
				{					
					@Override
					public void run()
					{
						refreshLineNumberArea();
					}
				});
				return text.toString();
			}
			@Override
			public void removeUpdate(DocumentEvent e)
			{
				if(lineNumberPanel != null) lineNumberPanel.setText(getText());				
			}
			
			@Override
			public void insertUpdate(DocumentEvent e)
			{
				if(lineNumberPanel != null) lineNumberPanel.setText(getText());				
			}
			
			@Override
			public void changedUpdate(DocumentEvent e)
			{
				if(lineNumberPanel != null) lineNumberPanel.setText(getText());				
			}
		});
		
		textScroll.getViewport().add(textPanel);
		textScroll.setRowHeaderView(lineNumberPanel);
		textScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		undomanager = new UndoManager();
		
		textPanel.getDocument().addUndoableEditListener(undomanager);
		
		popupsCopy.addActionListener(this);
		popupsCut.addActionListener(this);
		popupsPaste.addActionListener(this);
		textPanel.addMouseListener(this);
		
		if(popupsSearch != null) popupsSearch.addActionListener(this);
		
		this.add(textScroll, BorderLayout.CENTER);
		refreshLineNumberArea();
		
		GUIUtil.setFontRecursively(this, GUIUtil.usingFont);
	}
	
	/**
	 * <p>이 메소드는 내용이 사용자에 의해 변경되었을 때 호출됩니다.</p>
	 * 
	 */
	protected void changed()
	{		
		try
		{
			if(useHighlighting) GUIUtil.treatTextChanged(this, documents, keywords);
		}
		catch (Exception e)
		{
			
		}
	}
	
	/**
	 * <p>편집한 내용을 한 번 되돌립니다.</p>
	 * 
	 */
	public void undo()
	{
		if(canUndo()) undomanager.undo();
	}
	
	/**
	 * <p>한 번 되돌린 내용을 원래대로 다시 적용합니다.</p>
	 * 
	 */
	public void redo()
	{
		if(canRedo()) undomanager.redo();
	}
	
	/**
	 * <p>되돌릴 내용이 있는지 여부를 반환합니다.</p>
	 * 
	 * @return 되돌릴 내용이 있다면 true, 그 외에는 false
	 */
	public boolean canUndo()
	{
		return undomanager.canUndo();
	}
	
	/**
	 * <p>되돌린 것들 중 원래대로 다시 적용할 사항이 있는지 여부를 반환합니다.</p>
	 * 
	 * @return 다시 적용할 수 있는 내용이 있다면 true, 그 외에는 false
	 */
	public boolean canRedo()
	{
		return undomanager.canRedo();
	}
	
	/**
	 * <p>선택된(블럭된) 텍스트를 반환합니다.</p>
	 * 
	 * @return 선택된 텍스트
	 */
	public String getSelectedText()
	{
		return textPanel.getSelectedText();
	}
	
	/**
	 * <p>텍스트 내용을 넣습니다.</p>
	 * 
	 * @param text : 텍스트
	 */
	public void setText(String text)
	{
		textPanel.setText(text);
		changed();
	}
	
	/**
	 * <p>텍스트 내용을 반환합니다.</p>
	 * 
	 * @return 내용
	 */
	public String getText()
	{
		return textPanel.getText();
	}
	
	/**
	 * <p>텍스트를 현재 내용 뒷부분에 추가합니다.</p>
	 * 
	 * @param text : 추가할 텍스트
	 */
	public void append(String text)
	{		
		textPanel.setText(textPanel.getText() + text);
		changed();
	}
	
	/**
	 * <p>컴포넌트를 숨기거나 보입니다.</p>
	 * 
	 * @param visible : true 시 보이고, false 시 숨김
	 */
	public void setVisible(boolean visible)
	{
		textPanel.setVisible(visible);
	}
	
	/**
	 * <p>컴포넌트가 현재 숨겨진 상태인지 여부를 반환합니다. 숨겨지지 않았으면 true 를 반환합니다.</p>
	 * 
	 */
	public boolean isVisible()
	{
		return textPanel.isVisible();
	}
	
	/**
	 * <p>편집 가능 여부를 지정합니다. false 시 편집 불가능하게 됩니다.</p>
	 * 
	 * @param editables : 편집 가능 여부
	 */
	public void setEditable(boolean editables)
	{
		textPanel.setEditable(editables);
	}
	
//	/**
//	 * <p>가로 길이 내용을 넘어서면 자동으로 다음 행에 텍스트를 표시하는 옵션을 지정합니다.</p>
//	 * 
//	 * @param lw : true 시 줄 길이가 넘어서는 텍스트는 다음 줄로 넘깁니다.
//	 */
//	public void setLineWrap(boolean lw)
//	{
//		// textPanel.setLineWrap(lw);
//	}
	
	/**
	 * <p>폰트를 지정합니다.</p>
	 * 
	 * @param font : 폰트 객체
	 */
	public void setFont(Font font)
	{
		try
		{
			textPanel.setFont(font);
			lineNumberPanel.setFont(font);
		}
		catch(NullPointerException e)
		{
			
		}
	}
	
	/**
	 * <p>컴포넌트를 활성화하거나 비활성화합니다.</p>
	 * 
	 * @param enabled : true 시 활성화됩니다.
	 */
	public void setEnabled(boolean enabled)
	{
		textPanel.setEnabled(enabled);
	}
	public void addCaretListener(CaretListener listener)
	{
		textPanel.addCaretListener(listener);
	}
	public void addFocusListener(FocusListener listener)
	{
		textPanel.addFocusListener(listener);
	}
	public void addKeyListener(KeyListener listener)
	{
		textPanel.addKeyListener(listener);
	}
	public void addMouseListener(MouseListener listener)
	{
		textPanel.addMouseListener(listener);
	}
	public void addAncestorListener(AncestorListener listener)
	{
		textPanel.addAncestorListener(listener);
	}
	public void removeKeyListener(KeyListener listener)
	{
		textPanel.removeKeyListener(listener);
	}
	public void removeFocusListener(FocusListener listener)
	{
		textPanel.removeFocusListener(listener);
	}
	public void removeCaretListener(CaretListener listener)
	{
		textPanel.removeCaretListener(listener);
	}
	public void removeMouseListener(MouseListener listener)
	{
		textPanel.removeMouseListener(listener);
	}
	public void removeAncestorListener(AncestorListener listener)
	{
		textPanel.removeAncestorListener(listener);
	}
	@Override
	public Document getDocument()
	{
		return textPanel.getDocument();
	}
	@Override
	public void setDocument(Document doc)
	{
		textPanel.setDocument(doc);
	}
	@Override
	public void setCaretPosition(int caretPos)
	{
		textPanel.setCaretPosition(caretPos);
	}
	@Override
	public void setBackground(Color background)
	{
		try
		{
			textPanel.setBackground(background);
		}
		catch(NullPointerException e)
		{
			
		}
	}
	@Override
	public void setForeground(Color foreground)
	{
		try
		{
			textPanel.setForeground(foreground);
		}
		catch(NullPointerException e)
		{
			
		}
	}
	public JTextPane getLineNumberArea()
	{
		return lineNumberPanel;
	}
	public JScrollPane getScrollPane()
	{
		return textScroll;
	}
	public void setLineNumberVisible(boolean v)
	{
		lineNumberPanel.setVisible(v);
		refreshLineNumberArea();
	}
	public boolean isLineNumberVisible()
	{
		return lineNumberPanel.isVisible();
	}
	@Override
	public Component add(Component comp)
	{
		return textPanel.add(comp);
	}
	public void add(PopupMenu comp)
	{
		textPanel.add(comp);
	}
	protected void refreshLineNumberArea()
	{
		if(hideNumberFieldWhenEmpty)
		{
			if(getText() == null || getText().equals(""))
			{
				lineNumberPanel.setVisible(false);
				turnOffLineNumber();
			}
			else
			{
				lineNumberPanel.setVisible(true);
				turnOnLineNumber();
			}				
		}
	}
	
	/**
	 * <p>행 번호 기능을 끕니다.</p>
	 * 
	 */
	private void turnOffLineNumber()
	{
		if(numberFieldIsHidden)
		{
			lineNumberPanel.setBackground(textPanel.getBackground());
			lineNumberPanel.setForeground(textPanel.getForeground());
			
			numberFieldIsHidden = false;
		}
	}
	
	/**
	 * <p>행 번호 기능을 켭니다.</p>
	 * 
	 */
	private void turnOnLineNumber()
	{
		if(! numberFieldIsHidden)
		{
			Color textPanelColor = textPanel.getBackground();
			int maxColorValue = 0;
			double ratio = 1.2;
			if(maxColorValue < textPanelColor.getRed()) maxColorValue = textPanelColor.getRed();
			if(maxColorValue < textPanelColor.getGreen()) maxColorValue = textPanelColor.getGreen();
			if(maxColorValue < textPanelColor.getBlue()) maxColorValue = textPanelColor.getBlue();
			if(((double) maxColorValue) > (255.0 / 2.0))
			{
				lineNumberPanel.setBackground(new Color((int)(textPanelColor.getRed()/ratio)
						, (int)(textPanelColor.getGreen()/ratio), (int)(textPanelColor.getBlue()/ratio)));
			}
			else
			{
				lineNumberPanel.setBackground(new Color((int)(textPanelColor.getRed()*ratio)
						, (int)(textPanelColor.getGreen()*ratio), (int)(textPanelColor.getBlue()*ratio)));
			}
			
			textPanelColor = textPanel.getForeground();
			maxColorValue = 0;
			ratio = 1.2;
			if(maxColorValue < textPanelColor.getRed()) maxColorValue = textPanelColor.getRed();
			if(maxColorValue < textPanelColor.getGreen()) maxColorValue = textPanelColor.getGreen();
			if(maxColorValue < textPanelColor.getBlue()) maxColorValue = textPanelColor.getBlue();
			if(((double) maxColorValue) > (255.0 / 2.0))
			{
				lineNumberPanel.setForeground(new Color((int)(textPanelColor.getRed()/ratio)
						, (int)(textPanelColor.getGreen()/ratio), (int)(textPanelColor.getBlue()/ratio)));
			}
			else
			{
				lineNumberPanel.setForeground(new Color((int)(textPanelColor.getRed()*ratio)
						, (int)(textPanelColor.getGreen()*ratio), (int)(textPanelColor.getBlue()*ratio)));
			}
			numberFieldIsHidden = true;
		}		
	}
	
	/**
	 * <p>텍스트 영역 컴포넌트 객체를 반환합니다. JTextPane 형식으로 반환됩니다.</p>
	 * 
	 * @return 텍스트 영역 컴포넌트 객체
	 */
	public JTextPane getTextPane()
	{
		return textPanel;
	}
	
	@Override
	public JEditorPane getTextPanel()
	{
		return textPanel;
	}
	@Override
	public JScrollPane getTextScroll()
	{
		return textScroll;
	}
	
	public JTextPane getLineNumberPanel()
	{
		return lineNumberPanel;
	}

	public boolean isHideNumberFieldWhenEmpty()
	{
		return hideNumberFieldWhenEmpty;
	}

	public void setHideNumberFieldWhenEmpty(boolean hideNumberFieldWhenEmpty)
	{
		this.hideNumberFieldWhenEmpty = hideNumberFieldWhenEmpty;
	}
	
	/**
	 * <p>키 이벤트 전달 역할을 하는 메소드입니다.</p>
	 * 
	 * @param e : 키 이벤트
	 */
	@Override
	public void keyPressed(KeyEvent e)
	{
		Object ob = e.getSource();
		if(ob == textPanel)
		{
			changed();
		}
	}
	
	/**
	 * <p>마우스 이벤트 전달 역할을 하는 메소드입니다.</p>
	 * 
	 * @param e : 마우스 이벤트
	 */
	@Override
	public void mousePressed(MouseEvent e)
	{
		if(SwingUtilities.isRightMouseButton(e))
		{
			popups.show(getTextPanel(), e.getX(), e.getY());
		}
	}
	
	/**
	 * <p>강조 키워드 종류를 변경합니다. 이전 키워드들은 초기화됩니다.</p>
	 * 
	 * @param types : 키워드 종류
	 */
	public void setHighlightMode(String types)
	{
		GUIUtil.initKeywords(keywords, types);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		Object ob = e.getSource();
		if(ob == popupsCopy)
		{
			copyText();
		}
		else if(ob == popupsCut)
		{
			cutText();
		}
		else if(ob == popupsPaste)
		{
			pasteText();
		}
		else if(ob == popupsSearch)
		{
			if(searchDialog != null) searchDialog.open();
		}
	}
	/**
	 * <p>사용자가 마우스 오른쪽 버튼 클릭 시 팝업 메뉴에서 잘라내기 항목을 클릭했을 때 호출되는 메소드입니다.</p>
	 * 
	 */
	protected void cutText()
	{
		getTextPanel().cut();
	}
	/**
	 * <p>사용자가 마우스 오른쪽 버튼 클릭 시 팝업 메뉴에서 붙여넣기 항목을 클릭했을 때 호출되는 메소드입니다.</p>
	 * 
	 */
	protected void pasteText()
	{
		getTextPanel().paste();
	}
	/**
	 * <p>사용자가 마우스 오른쪽 버튼 클릭 시 팝업 메뉴에서 복사 항목을 클릭했을 때 호출되는 메소드입니다.</p>
	 * 
	 */
	protected void copyText()
	{
		getTextPanel().copy();
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
		
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
		
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
		
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		
	}

	@Override
	public void noMoreUse()
	{
		try
		{
			textPanel.removeMouseListener(this);
		}
		catch(Exception e)
		{
			
		}
		
		superWindow = null;
	}
	
	@Override
	public boolean isAlive()
	{
		return superWindow != null;
	}
	
	/**
	 * <p>검색 기능을 사용할 수 있는지 여부를 반환합니다.</p>
	 * 
	 * @return 검색 기능 사용 여부
	 */
	public boolean isSearchDialogAvailable()
	{
		return (searchDialog != null);
	}
	
	/**
	 * <p>검색 대화 상자를 엽니다. 검색 기능 사용이 불가능하면 아무 동작을 하지 않습니다.</p>
	 */
	public void showSearchDialog()
	{
		if(searchDialog != null) searchDialog.open();
	}
	
	/**
	 * <p>검색 대화 상자를 닫습니다. 검색 기능 사용이 불가능하면 아무 동작을 하지 않습니다.</p>
	 */
	public void closeSearchDialog()
	{
		if(searchDialog != null) searchDialog.close();
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		
	}

	@Override
	public void keyTyped(KeyEvent e)
	{
		
	}

	@Override
	public Component getComponent()
	{
		return this;
	}
}