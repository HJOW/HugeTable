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

import hjow.hgtable.ui.NeedtoEnd;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.PopupMenu;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.event.AncestorListener;
import javax.swing.event.CaretListener;
import javax.swing.text.Document;

/**
 * <p>스크립트 입력 에디터 객체는 이 인터페이스를 구현합니다.</p>
 * 
 * @author HJOW
 *
 */
public interface CodeEditorPane extends ActionListener, MouseListener, KeyListener, NeedtoEnd
{	
	/**
	 * <p>기본 생성자입니다. 컴포넌트들을 초기화합니다.</p>
	 * 
	 */
	public void init();
	
	/**
	 * <p>편집한 내용을 한 번 되돌립니다.</p>
	 * 
	 */
	public void undo();
	
	/**
	 * <p>한 번 되돌린 내용을 원래대로 다시 적용합니다.</p>
	 * 
	 */
	public void redo();
	
	/**
	 * <p>되돌릴 내용이 있는지 여부를 반환합니다.</p>
	 * 
	 * @return 되돌릴 내용이 있다면 true, 그 외에는 false
	 */
	public boolean canUndo();
	
	/**
	 * <p>되돌린 것들 중 원래대로 다시 적용할 사항이 있는지 여부를 반환합니다.</p>
	 * 
	 * @return 다시 적용할 수 있는 내용이 있다면 true, 그 외에는 false
	 */
	public boolean canRedo();
	
	/**
	 * <p>선택된(블럭된) 텍스트를 반환합니다.</p>
	 * 
	 * @return 선택된 텍스트
	 */
	public String getSelectedText();
	
	/**
	 * <p>텍스트 내용을 넣습니다.</p>
	 * 
	 * @param text : 텍스트
	 */
	public void setText(String text);
	
	/**
	 * <p>텍스트 내용을 반환합니다.</p>
	 * 
	 * @return 내용
	 */
	public String getText();
	
	/**
	 * <p>텍스트를 현재 내용 뒷부분에 추가합니다.</p>
	 * 
	 * @param text : 추가할 텍스트
	 */
	public void append(String text);
	
	/**
	 * <p>컴포넌트를 숨기거나 보입니다.</p>
	 * 
	 * @param visible : true 시 보이고, false 시 숨김
	 */
	public void setVisible(boolean visible);
	
	/**
	 * <p>컴포넌트가 현재 숨겨진 상태인지 여부를 반환합니다. 숨겨지지 않았으면 true 를 반환합니다.</p>
	 * 
	 */
	public boolean isVisible();
	
	/**
	 * <p>편집 가능 여부를 지정합니다. false 시 편집 불가능하게 됩니다.</p>
	 * 
	 * @param editables : 편집 가능 여부
	 */
	public void setEditable(boolean editables);
	
	/**
	 * <p>폰트를 지정합니다.</p>
	 * 
	 * @param font : 폰트 객체
	 */
	public void setFont(Font font);
	
	/**
	 * <p>컴포넌트를 활성화하거나 비활성화합니다.</p>
	 * 
	 * @param enabled : true 시 활성화됩니다.
	 */
	public void setEnabled(boolean enabled);
	public void addCaretListener(CaretListener listener);
	public void addFocusListener(FocusListener listener);
	public void addKeyListener(KeyListener listener);
	public void addMouseListener(MouseListener listener);
	public void addAncestorListener(AncestorListener listener);
	public void removeKeyListener(KeyListener listener);
	public void removeFocusListener(FocusListener listener);
	public void removeCaretListener(CaretListener listener);
	public void removeMouseListener(MouseListener listener);
	public void removeAncestorListener(AncestorListener listener);
	public Document getDocument();
	public void setDocument(Document doc);
	public void setCaretPosition(int caretPos);
	public void setBackground(Color background);
	public void setForeground(Color foreground);
	public JScrollPane getScrollPane();
	public void add(PopupMenu comp);
	public JEditorPane getTextPanel();
	public JScrollPane getTextScroll();
	
	/**
	 * <p>검색 기능을 사용할 수 있는지 여부를 반환합니다.</p>
	 * 
	 * @return 검색 기능 사용 여부
	 */
	public boolean isSearchDialogAvailable();
	
	/**
	 * <p>검색 대화 상자를 엽니다. 검색 기능 사용이 불가능하면 아무 동작을 하지 않습니다.</p>
	 */
	public void showSearchDialog();
	
	/**
	 * <p>검색 대화 상자를 닫습니다. 검색 기능 사용이 불가능하면 아무 동작을 하지 않습니다.</p>
	 */
	public void closeSearchDialog();
	
	/**
	 * <p>내부 에디터 컴포넌트들을 모두 포함한 컴포넌트 객체를 반환합니다.</p>
	 * 
	 * @return 컴포넌트 객체
	 */
	public Component getComponent();
	
	/**
	 * <p>강조 키워드 종류를 변경합니다. 이전 키워드들은 초기화됩니다.</p>
	 * 
	 * @param types : 키워드 종류
	 */
	public void setHighlightMode(String types);
}
