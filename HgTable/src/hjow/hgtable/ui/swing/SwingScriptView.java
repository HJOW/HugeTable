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
import hjow.hgtable.ui.GUIManager;
import hjow.hgtable.ui.NeedtoEnd;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Vector;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.text.Document;

/**
 * <p>이 클래스 객체는 편집 창 여럿을 탭으로 가지게 되는 패널입니다.</p>
 * 
 * @author HJOW
 *
 */
public class SwingScriptView extends JPanel implements NeedtoEnd
{
	private static final long serialVersionUID = 3527505268397080185L;
	protected List<CodeEditorPane> tabs = new Vector<CodeEditorPane>();
	protected JTabbedPane tabPanel;
	protected GUIManager manager;
	protected Font scriptFont;
	
	/**
	 * <p>생성자입니다. GUI 타입의 매니저 객체가 필요합니다.</p>
	 * 
	 * @param manager : GUI 매니저 객체
	 */
	public SwingScriptView(GUIManager manager)
	{
		super();
		this.manager = manager;
		this.setLayout(new BorderLayout());
		
		tabPanel = new JTabbedPane();
		this.add(tabPanel, BorderLayout.CENTER);
		
//		tabs.add(new LineNumberTextArea(manager.getFrame()));
		UISyntaxView view = new UISyntaxView(manager.getFrame());
		view.setHighlightMode("text/javascript");
		tabs.add(view);
		tabPanel.add("1", tabs.get(0).getComponent());
		setListener();
	}
	
	/**
	 * <p>새 탭을 추가합니다.</p>
	 * 
	 */
	public void newTab()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					CodeEditorPane newTab = null;
					newTab = new UISyntaxView(manager.getFrame());
					//newTab = new LineNumberTextArea(manager.getFrame());
					tabs.add(newTab);
					setListener();
					tabPanel.add(String.valueOf(tabs.size()), newTab.getComponent());
					if(tabs.size() >= 1) selectTab(tabs.size() - 1);
					if(scriptFont != null) newTab.setFont(scriptFont);
				}
				catch(Exception e)
				{
					manager.logError(e, Manager.applyStringTable("On new tab"), true);
				}
			}
		});
	}
	
	/**
	 * <p>이벤트를 다시 지정합니다.</p>
	 * 
	 */
	public void setListener()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					tabPanel.removeMouseListener(manager);
				}
				catch(Exception e)
				{
					
				}
				try
				{
					tabPanel.addMouseListener(manager);
				}
				catch(Exception e)
				{
					
				}
			}
		});
	}
	
	/**
	 * <p>선택된 탭 번호를 반환합니다.</p>
	 * 
	 * @return 탭 번호
	 */
	public int getSelectedTabIndex()
	{
		return tabPanel.getSelectedIndex();
	}
	
	/**
	 * <p>탭을 선택합니다.</p>
	 * 
	 * @param index : 탭 번호
	 */
	public void selectTab(int index)
	{
		tabPanel.setSelectedIndex(index);
	}
	
	/**
	 * <p>선택된 탭에서 입력 커서 위치를 변경합니다.</p>
	 * 
	 * @param pos : 위치
	 */
	public void setCaretPosition(int pos)
	{
		if(tabPanel.getSelectedIndex() < 0) return;
		tabs.get(getSelectedTabIndex()).setCaretPosition(pos);
	}
	
	/**
	 * <p>선택된 탭에 입력된 문서의 길이를 반환합니다.</p>
	 * 
	 * @return 문서의 길이
	 */
	public int getDocumentLength()
	{
		return tabs.get(getSelectedTabIndex()).getDocument().getLength();
	}
	
	/**
	 * <p>선택된 탭에 입력된 문서를 반환합니다.</p>
	 * 
	 * @return 문서 객체
	 */
	public Document getDocument()
	{
		return tabs.get(getSelectedTabIndex()).getDocument();
	}
	
	/**
	 * <p>선택된 탭에 입력된 텍스트를 반환합니다.</p>
	 * 
	 * @return 텍스트 내용
	 */
	public String getText()
	{
		if(tabPanel.getSelectedIndex() < 0) return null;
		return tabs.get(getSelectedTabIndex()).getText();
	}
	
	/**
	 * <p>선택된 탭에 입력된 선택된 텍스트를 반환합니다.</p>
	 * 
	 * @return 선택된 텍스트
	 */
	public String getSelectedText()
	{
		if(tabPanel.getSelectedIndex() < 0) return null;
		return tabs.get(getSelectedTabIndex()).getSelectedText();
	}
	
	/**
	 * <p>선택된 탭에 텍스트 내용을 지정합니다. 이전 내용을 대체합니다.</p>
	 * 
	 * @param str : 입력할 텍스트
	 */
	public void setText(String str)
	{
		if(tabPanel.getSelectedIndex() < 0) return;
		tabs.get(getSelectedTabIndex()).setText(str);
	}
	
	/**
	 * <p>되돌릴 내용이 있는지 여부를 반환합니다.</p>
	 * 
	 * @return 되돌릴 내용이 있다면 true, 그 외에는 false
	 */
	public boolean canUndo()
	{
		if(tabPanel.getSelectedIndex() < 0) return false;
		return tabs.get(getSelectedTabIndex()).canUndo();
	}
	
	/**
	 * <p>되돌린 것들 중 원래대로 다시 적용할 사항이 있는지 여부를 반환합니다.</p>
	 * 
	 * @return 다시 적용할 수 있는 내용이 있다면 true, 그 외에는 false
	 */
	public boolean canRedo()
	{
		if(tabPanel.getSelectedIndex() < 0) return false;
		return tabs.get(getSelectedTabIndex()).canRedo();
	}
	
	/**
	 * <p>편집한 내용을 한 번 되돌립니다.</p>
	 * 
	 */
	public void undo()
	{
		if(tabPanel.getSelectedIndex() < 0) return;
		tabs.get(getSelectedTabIndex()).undo();
	}
	
	/**
	 * <p>한 번 되돌린 내용을 원래대로 다시 적용합니다.</p>
	 * 
	 */
	public void redo()
	{
		if(tabPanel.getSelectedIndex() < 0) return;
		tabs.get(getSelectedTabIndex()).redo();
	}
	
	/**
	 * <p>편집 영역의 폰트를 변경합니다.</p>
	 * 
	 * @param font : 폰트 객체
	 */
	public void setScriptFont(Font font)
	{
		scriptFont = font;
		for(int i=0; i<tabs.size(); i++)
		{
			tabs.get(i).setFont(scriptFont);
		}
	}
	
	/**
	 * <p>현재 선택된 탭의 텍스트 입력 영역 컴포넌트를 반환합니다.</p>
	 * 
	 * @return 텍스트 입력 영역 컴포넌트
	 */
	public JEditorPane getTextArea()
	{
		if(tabPanel.getSelectedIndex() < 0) return null;
		return tabs.get(getSelectedTabIndex()).getTextPanel();
	}
	@Override
	public void noMoreUse()
	{
		for(int i=0; i<tabs.size(); i++)
		{
			try
			{
				tabs.get(i).getTextPanel().removeKeyListener(manager);
			}
			catch(Exception e)
			{
				
			}
			tabs.get(i).noMoreUse();
		}
		manager = null;
	}
	
	@Override
	public boolean isAlive()
	{
		return manager != null;
	}
	
	/**
	 * <p>해당 이벤트가 이 컴포넌트에서 발생했는지 검사합니다.</p>
	 * 
	 * @param e : 대상 이벤트
	 * @return 해당 이벤트 발생 지점이 이 컴포넌트가 맞는지 여부
	 */
	public boolean checkEventOccured(ComponentEvent e)
	{
		if(e.getSource() == getTextArea()) return true;
		for(int i=0; i<tabs.size(); i++)
		{
			if(e.getSource() == tabs.get(i).getTextPanel())
			{
				return true;
			}
		}
		if(e.getSource() == tabPanel)
		{
			return true;
		}
		if(e.getSource() == this) return true;
		return false;
	}
	
	@Override
	public void requestFocus()
	{
		getTextArea().requestFocus();
	}
	
	/**
	 * <p>강조 키워드 종류를 변경합니다. 이전 키워드들은 초기화됩니다.</p>
	 * 
	 * @param types : 키워드 종류
	 */
	public void setHighlightMode(String types)
	{
		for(int i=0; i<tabs.size(); i++)
		{
			if(tabs.get(i) instanceof CodeEditorPane)
			{
				((CodeEditorPane) tabs.get(i)).setHighlightMode(types);
				if(scriptFont != null) ((CodeEditorPane) tabs.get(i)).setFont(scriptFont);
			}
		}
	}
	
	/**
	 * <p>키 이벤트 전달 역할을 하는 메소드입니다.</p>
	 * 
	 * @param e : 키 이벤트
	 */
	public void keyPressed(KeyEvent e)
	{
		Object ob = e.getSource();
		for(int i=0; i<tabs.size(); i++)
		{
			if(tabs.get(i).getTextPanel() == ob)
			{
				tabs.get(i).keyPressed(e);
				break;
			}
		}
	}
	
	/**
	 * <p>마우스 이벤트 전달 역할을 하는 메소드입니다.</p>
	 * 
	 * @param e : 마우스 이벤트
	 */
	public void mousePressed(MouseEvent e)
	{
		Object ob = e.getSource();
		if(ob == tabPanel)
		{
			closeSearchDialog();
		}
		else
		{
			for(int i=0; i<tabs.size(); i++)
			{
				if(tabs.get(i).getTextPanel() == ob)
				{
					tabs.get(i).mousePressed(e);
					break;
				}
			}
		}
	}
	
	/**
	 * <p>검색 대화 상자를 닫습니다. 검색 기능 사용이 불가능하면 아무 동작을 하지 않습니다.</p>
	 */
	public void closeSearchDialog()
	{
		for(int i=0; i<tabs.size(); i++)
		{
			tabs.get(i).closeSearchDialog();
		}
	}
	
	/**
	 * <p>검색 대화 상자를 엽니다. 검색 기능 사용이 불가능하면 아무 동작을 하지 않습니다.</p>
	 */
	public void showSearchDialog()
	{
		if(tabPanel.getSelectedIndex() >= 0) tabs.get(tabPanel.getSelectedIndex()).showSearchDialog();
	}
	
	/**
	 * <p>검색 기능을 사용할 수 있는지 여부를 반환합니다.</p>
	 * 
	 * @return 검색 기능 사용 여부
	 */
	public boolean isSearchDialogAvailable()
	{
		if(tabPanel.getSelectedIndex() >= 0) return tabs.get(tabPanel.getSelectedIndex()).isSearchDialogAvailable();
		return false;
	}
}
