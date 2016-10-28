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

/*
This class source is based on JSyntaxView library which following Apache License 2.0. 
 */

package hjow.hgtable.ui.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.JEditorPane;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretListener;
import javax.swing.text.Document;

import hjow.hgtable.Main;
import hjow.hgtable.Manager;

/**
 * <p>JSyntaxPane 기반의 스크립트 입력 패널 컴포넌트입니다.</p>
 * 
 * @author HJOW
 *
 */
public class UISyntaxView extends JPanel implements CodeEditorPane
{
	private static final long serialVersionUID = -8972337694937176784L;
	protected JEditorPane scriptPane;
	protected JScrollPane scriptScroll;
	
	protected Window superWindow;
	protected FindDialog searchDialog;
	protected JMenuItem popupsSearch;
	protected JPopupMenu popups;
	protected JMenuItem popupsCopy;
	protected JMenuItem popupsCut;
	protected JMenuItem popupsPaste;
	
	protected Font font = null;
	
//	/**
//	 * <p>JSyntaxPane 사용을 위해, 객체 사용 전 준비 작업을 합니다.</p>
//	 * 
//	 */
//	static
//	{
//		DefaultSyntaxKit.initKit();
//	}
	
	/**
	 * <p>컴포넌트 객체를 생성합니다. 검색 대화 상자가 생성되지 않습니다.</p>
	 * 
	 */
	public UISyntaxView()
	{
		super();
		init();
	}
	
	/**
	 * <p>컴포넌트 객체와 함께 검색 대화 상자를 생성합니다.</p>
	 * 
	 * @param window : 윈도우 객체 (검색 대화 상자의 상위 컴포넌트가 됨)
	 */
	public UISyntaxView(Window window)
	{
		super();
		this.superWindow = window;
		init();
	}

	@Override
	public void init()
	{
		this.setLayout(new BorderLayout());
		
		scriptPane = new UIEditorPane();
		scriptScroll = new JScrollPane(scriptPane);
		this.add(scriptScroll, BorderLayout.CENTER);
//		scriptPane.setContentType("text/javascript");
		
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
		
		popupsCopy.addActionListener(this);
		popupsCut.addActionListener(this);
		popupsPaste.addActionListener(this);
		scriptPane.addMouseListener(this);
		
		if(popupsSearch != null) popupsSearch.addActionListener(this);
	}

	@Override
	public void noMoreUse()
	{
		if(searchDialog != null) searchDialog.noMoreUse();
		searchDialog = null;
		
		superWindow = null;
	}

	@Override
	public boolean isAlive()
	{
		return true;
	}

	@Override
	public void undo()
	{
		((de.sciss.syntaxpane.SyntaxDocument) scriptPane.getDocument()).doUndo();
	}

	@Override
	public void redo()
	{
		((de.sciss.syntaxpane.SyntaxDocument) scriptPane.getDocument()).doRedo();
	}

	@Override
	public boolean canUndo()
	{
		return ((de.sciss.syntaxpane.SyntaxDocument) scriptPane.getDocument()).canUndo();
	}

	@Override
	public boolean canRedo()
	{
		return ((de.sciss.syntaxpane.SyntaxDocument) scriptPane.getDocument()).canRedo();
	}

	@Override
	public String getSelectedText()
	{
		return scriptPane.getSelectedText();
	}

	@Override
	public void setText(String text)
	{
		scriptPane.setText(text);
	}

	@Override
	public String getText()
	{
		return scriptPane.getText();
	}

	@Override
	public void append(String text)
	{
		setText(getText() + text);
	}

	@Override
	public void setEditable(boolean editables)
	{
		scriptPane.setEditable(editables);
	}

	@Override
	public void addCaretListener(CaretListener listener)
	{
		scriptPane.addCaretListener(listener);
	}

	@Override
	public void removeFocusListener(FocusListener listener)
	{
		scriptPane.removeFocusListener(listener);
	}

	@Override
	public void removeCaretListener(CaretListener listener)
	{
		scriptPane.removeCaretListener(listener);
	}

	@Override
	public Document getDocument()
	{
		return scriptPane.getDocument();
	}

	@Override
	public void setDocument(Document doc)
	{
		scriptPane.setDocument(doc);
	}

	@Override
	public void setCaretPosition(int caretPos)
	{
		scriptPane.setCaretPosition(caretPos);
	}

	@Override
	public JScrollPane getScrollPane()
	{
		return scriptScroll;
	}

	@Override
	public JEditorPane getTextPanel()
	{
		return scriptPane;
	}

	@Override
	public JScrollPane getTextScroll()
	{
		return scriptScroll;
	}

	@Override
	public boolean isSearchDialogAvailable()
	{
		return false;
	}

	@Override
	public void showSearchDialog()
	{
		searchDialog.open();
	}

	@Override
	public void closeSearchDialog()
	{
		searchDialog.close();
	}

	@Override
	public Component getComponent()
	{
		return this;
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
	public void mousePressed(MouseEvent e)
	{
		if(SwingUtilities.isRightMouseButton(e))
		{
			popups.show(getTextPanel(), e.getX(), e.getY());
		}
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		
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
	public void setHighlightMode(String types)
	{
		if(types.equalsIgnoreCase("SQL") || types.equalsIgnoreCase("text/sql"))
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				@Override
				public void run()
				{
					try
					{
						String beforeContents = scriptPane.getText();
						int beforePosition = scriptPane.getCaretPosition();
						scriptPane.setText("");
						
						scriptPane.setContentType("text/sql");
						scriptPane.setText(beforeContents);
						scriptPane.setCaretPosition(beforePosition);
						if(font != null) scriptPane.setFont(font);
					}
					catch(Exception e)
					{
						Main.println(Manager.applyStringTable("There is some problem on changing syntax highlighting mode") + " : " + "text/sql" + ", " + e.getMessage());
					}
				}
			});
		}
		else if(types.equalsIgnoreCase("JavaScript") || types.equalsIgnoreCase("JScript") || types.equalsIgnoreCase("text/javascript")
				|| types.equalsIgnoreCase("nashorn") || types.equalsIgnoreCase("Rhino"))
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				@Override
				public void run()
				{
					try
					{
						String beforeContents = scriptPane.getText();
						int beforePosition = scriptPane.getCaretPosition();
						scriptPane.setText("");
						
						scriptPane.setContentType("text/javascript");
						scriptPane.setText(beforeContents);
						scriptPane.setCaretPosition(beforePosition);
						if(font != null) scriptPane.setFont(font);
					}
					catch(Exception e)
					{
						Main.println(Manager.applyStringTable("There is some problem on changing syntax highlighting mode") + " : " + "text/sql" + ", " + e.getMessage());
					}
				}
			});
		}
		else if(types.equalsIgnoreCase("Ruby") || types.equalsIgnoreCase("JRuby") || types.equalsIgnoreCase("text/ruby"))
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				@Override
				public void run()
				{
					try
					{
						String beforeContents = scriptPane.getText();
						int beforePosition = scriptPane.getCaretPosition();
						scriptPane.setText("");
						
						scriptPane.setContentType("text/ruby");
						scriptPane.setText(beforeContents);
						scriptPane.setCaretPosition(beforePosition);
						if(font != null) scriptPane.setFont(font);
					}
					catch(Exception e)
					{
						Main.println(Manager.applyStringTable("There is some problem on changing syntax highlighting mode") + " : " + "text/sql" + ", " + e.getMessage());
					}
				}
			});
		}
		else if(types.equalsIgnoreCase("Python") || types.equalsIgnoreCase("Jython") || types.equalsIgnoreCase("text/python"))
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				@Override
				public void run()
				{
					try
					{
						String beforeContents = scriptPane.getText();
						int beforePosition = scriptPane.getCaretPosition();
						scriptPane.setText("");
						
						scriptPane.setContentType("text/python");
						scriptPane.setText(beforeContents);
						scriptPane.setCaretPosition(beforePosition);
						if(font != null) scriptPane.setFont(font);
					}
					catch(Exception e)
					{
						Main.println(Manager.applyStringTable("There is some problem on changing syntax highlighting mode") + " : " + "text/sql" + ", " + e.getMessage());
					}
				}
			});
		}
		else
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				@Override
				public void run()
				{
					try
					{
						String beforeContents = scriptPane.getText();
						int beforePosition = scriptPane.getCaretPosition();
						scriptPane.setText("");
						
						scriptPane.setContentType("text");
						scriptPane.setText(beforeContents);
						scriptPane.setCaretPosition(beforePosition);
						if(font != null) scriptPane.setFont(font);
					}
					catch(Exception e)
					{
						Main.println(Manager.applyStringTable("There is some problem on changing syntax highlighting mode") + " : " + "text/sql" + ", " + e.getMessage());
					}
				}
			});
		}
	}
	@Override
	public void setFont(Font font)
	{
		this.font = font;
		if(scriptPane != null) scriptPane.setFont(font);
		if(scriptScroll != null) scriptScroll.setFont(font);
		super.setFont(font);
	}
}