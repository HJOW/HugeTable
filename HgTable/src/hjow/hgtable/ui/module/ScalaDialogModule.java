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
package hjow.hgtable.ui.module;

import hjow.hgtable.ui.swing.UIComboBox;
import hjow.hgtable.ui.swing.UIList;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;

/**
 * <p>스칼라 스크립트 기반으로 만들어진 대화 상자형 모듈은 이 클래스의 하위 클래스여야 합니다.</p>
 * 
 * @author HJOW
 *
 */
public abstract class ScalaDialogModule extends GUIDialogModule
{
	private static final long serialVersionUID = -2524374201603986073L;
	protected List<AbstractButton> buttons = new Vector<AbstractButton>();
	protected List<JTextField> fields = new Vector<JTextField>();
	protected List<Window> windows = new Vector<Window>();
	protected List<UIComboBox> comboBoxes = new Vector<UIComboBox>();
	protected List<JCheckBox> checks = new Vector<JCheckBox>();
	protected List<UIList> lists = new Vector<UIList>();
	protected List<Component> mouseComponents = new Vector<Component>();
	
	/**
	 * <p>버튼 객체를 이벤트 대상에 추가합니다.</p>
	 * 
	 * @param buttons : 버튼 객체
	 */
	public void addListener(AbstractButton ... buttons)
	{
		if(buttons != null && buttons.length >= 1)
		{
			for(AbstractButton b : buttons)
			{
				this.buttons.add(b);
			}
		}
	}
	
	/**
	 * <p>텍스트 필드를 이벤트 대상에 추가합니다.</p>
	 * 
	 * @param field : 텍스트 필드 객체
	 */
	public void addListener(JTextField field)
	{
		fields.add(field);
	}
	
	/**
	 * <p>대화 상자, 혹은 창을 이벤트 대상에 추가합니다.</p>
	 * 
	 * @param win : 대화 상자, 창 객체
	 */
	public void addListener(Window win)
	{
		windows.add(win);
	}
	
	/**
	 * <p>콤보박스를 이벤트 대상에 추가합니다.</p>
	 * 
	 * @param box : 콤보박스 객체
	 */
	public void addListener(UIComboBox box)
	{
		comboBoxes.add(box);
	}
	
	/**
	 * <p>체크박스를 이벤트 대상에 추가합니다.</p>
	 * 
	 * @param check : 체크박스 객체
	 */
	public void addListener(JCheckBox check)
	{
		checks.add(check);
	}
	
	/**
	 * <p>리스트를 이벤트 대상에 추가합니다.</p>
	 * 
	 * @param list : 리스트 객체
	 */
	public void addListener(UIList list)
	{
		lists.add(list);
	}
	
	/**
	 * <p>컴포넌트를 마우스 이벤트 대상에 추가합니다.</p>
	 * 
	 * @param c : 컴포넌트 객체
	 */
	public void addMouseListener(Component c)
	{
		mouseComponents.add(c);
	}
	
	
	/**
	 * <p>이벤트 리스너들을 제거합니다.</p>
	 * 
	 */
	public void removeListeners()
	{
		for(int i=0; i<buttons.size(); i++)
		{
			try
			{
				buttons.get(i).removeActionListener(this);
			}
			catch(Exception e)
			{
				
			}
		}
		for(int i=0; i<fields.size(); i++)
		{
			try
			{
				fields.get(i).removeActionListener(this);
			}
			catch(Exception e)
			{
				
			}
		}
		for(int i=0; i<windows.size(); i++)
		{
			try
			{
				windows.get(i).removeWindowListener(this);
			}
			catch(Exception e)
			{
				
			}
		}
		for(int i=0; i<comboBoxes.size(); i++)
		{
			try
			{
				comboBoxes.get(i).removeItemListener(this);
			}
			catch(Exception e)
			{
				
			}
		}
		for(int i=0; i<checks.size(); i++)
		{
			try
			{
				checks.get(i).removeItemListener(this);
			}
			catch(Exception e)
			{
				
			}
		}
		for(int i=0; i<lists.size(); i++)
		{
			try
			{
				lists.get(i).removeListSelectionListener(this);
			}
			catch(Exception e)
			{
				
			}
		}
		for(int i=0; i<mouseComponents.size(); i++)
		{
			try
			{
				mouseComponents.get(i).removeMouseListener(this);
			}
			catch(Exception e)
			{
				
			}
		}
	}
	
	/**
	 * <p>이벤트 리스너를 적용합니다.</p>
	 * 
	 */
	public void setListeners()
	{
		removeListeners();
		for(int i=0; i<buttons.size(); i++)
		{
			try
			{
				buttons.get(i).addActionListener(this);
			}
			catch(Exception e)
			{
				
			}
		}
		for(int i=0; i<fields.size(); i++)
		{
			try
			{
				fields.get(i).addActionListener(this);
			}
			catch(Exception e)
			{
				
			}
		}
		for(int i=0; i<windows.size(); i++)
		{
			try
			{
				windows.get(i).addWindowListener(this);
			}
			catch(Exception e)
			{
				
			}
		}
		for(int i=0; i<comboBoxes.size(); i++)
		{
			try
			{
				comboBoxes.get(i).addItemListener(this);
			}
			catch(Exception e)
			{
				
			}
		}
		for(int i=0; i<checks.size(); i++)
		{
			try
			{
				checks.get(i).addItemListener(this);
			}
			catch(Exception e)
			{
				
			}
		}
		for(int i=0; i<lists.size(); i++)
		{
			try
			{
				lists.get(i).addListSelectionListener(this);
			}
			catch(Exception e)
			{
				
			}
		}
		for(int i=0; i<mouseComponents.size(); i++)
		{
			try
			{
				mouseComponents.get(i).addMouseListener(this);
			}
			catch(Exception e)
			{
				
			}
		}
	}
	
	@Override
	public void noMoreUse()
	{
		removeListeners();
		buttons.clear();
		fields.clear();
		windows.clear();
		comboBoxes.clear();
		checks.clear();
		lists.clear();
		mouseComponents.clear();
		super.noMoreUse();
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		Object ob = e.getSource();
		
		boolean checked = false;
		for(int i=0; i<buttons.size(); i++)
		{
			if(buttons.get(i) == ob)
			{	
				actionPerformed(buttons.get(i));
				checked = true;
				break;
			}
		}
		for(int i=0; i<fields.size(); i++)
		{
			if(fields.get(i) == ob)
			{
				actionPerformed(fields.get(i));
				checked = true;
				break;
			}
		}
		if(! checked)
		{
			super.actionPerformed(e);
		}
	}
	
	@Override
	public void itemStateChanged(ItemEvent e)
	{
		Object ob = e.getSource();
		
		boolean checked = false;
		for(int i=0; i<this.checks.size(); i++)
		{
			if(checks.get(i) == ob)
			{
				itemStateChanged(checks.get(i));
				checked = true;
				break;
			}
		}
		for(int i=0; i<comboBoxes.size(); i++)
		{
			if(comboBoxes.get(i) == ob)
			{
				itemStateChanged(comboBoxes.get(i));
				checked = true;
				break;
			}
		}
		if(! checked)
		{
			super.itemStateChanged(e);
		}
	}
	
	@Override
	public void valueChanged(ListSelectionEvent e)
	{
		Object ob = e.getSource();
		
		boolean checked = false;
		for(int i=0; i<this.lists.size(); i++)
		{
			if(lists.get(i) == ob)
			{
				valueChanged(lists.get(i));
				checked = true;
				break;
			}
		}
		if(! checked)
		{
			super.valueChanged(e);
		}
	}
	
	/**
	 * <p>이 메소드는 리스트가 선택되었을 때 호출됩니다.</p>
	 * 
	 * @param button : 클릭된 리스트 객체
	 */
	protected void valueChanged(UIList list)
	{
		
	}
	
	/**
	 * <p>이 메소드는 버튼이 클릭되었을 때 호출됩니다.</p>
	 * 
	 * @param button : 클릭된 버튼 객체
	 */
	protected void actionPerformed(AbstractButton button)
	{
		
	}
	
	/**
	 * <p>이 메소드는 텍스트 필드 내에서 사용자가 엔터 키를 눌렀을 때 호출됩니다.</p>
	 * 
	 * @param button : 엔터 키가 눌린 텍스트 필드 객체
	 */
	protected void actionPerformed(JTextField button)
	{
		
	}
	
	/**
	 * <p>이 메소드는 체크박스의 체크 여부가 변경되었을 때 호출됩니다.</p>
	 * 
	 * @param c : 상태가 변경된 체크박스 객체
	 */
	protected void itemStateChanged(JCheckBox c)
	{
		
	}
	
	/**
	 * <p>이 메소드는 사용자가 해당 콤보박스의 다른 아이템을 선택했을 때 호출됩니다.</p>
	 * 
	 * @param c : 대상 콤보박스 객체
	 */
	protected void itemStateChanged(UIComboBox c)
	{
		
	}
	
	/**
	 * <p>이 메소드는 사용자가 해당 대화 상자 혹은 창을 닫았을 때 호출됩니다.</p>
	 * 
	 * @param w : 해당 대화 상자 혹은 창
	 */
	protected void windowClosing(Window w)
	{
		
	}
	
	/**
	 * <p>이 메소드는 사용자가 해당 대화 상자 혹은 창이 닫혔을 때 호출됩니다.</p>
	 * 
	 * @param w : 해당 대화 상자 혹은 창
	 */
	protected void windowClosed(Window w)
	{
		
	}
	
	/**
	 * <p>이 메소드는 사용자가 해당 대화 상자 혹은 창이 활성화되었을 때 호출됩니다.</p>
	 * 
	 * @param w : 해당 대화 상자 혹은 창
	 */
	protected void windowActivated(Window w)
	{
		
	}
	
	/**
	 * <p>이 메소드는 사용자가 해당 대화 상자 혹은 창이 비활성화되었을 때 호출됩니다.</p>
	 * 
	 * @param w : 해당 대화 상자 혹은 창
	 */
	protected void windowDeactivated(Window w)
	{
		
	}
	
	/**
	 * <p>이 메소드는 사용자가 해당 대화 상자 혹은 창의 최소화가 해제되었을 때 호출됩니다.</p>
	 * 
	 * @param w : 해당 대화 상자 혹은 창
	 */
	protected void windowDeiconified(Window w)
	{
		
	}
	
	/**
	 * <p>이 메소드는 사용자가 해당 대화 상자 혹은 창이 최소화되었을 때 호출됩니다.</p>
	 * 
	 * @param w : 해당 대화 상자 혹은 창
	 */
	protected void windowIconified(Window w)
	{
		
	}
	
	/**
	 * <p>이 메소드는 사용자가 해당 대화 상자 혹은 창이 열렸을 때 호출됩니다.</p>
	 * 
	 * @param w : 해당 대화 상자 혹은 창
	 */
	protected void windowOpened(Window w)
	{
		
	}
	
	/**
	 * <p>이 메소드는 사용자가 해당 컴포넌트를 클릭했을 때 호출됩니다.</p>
	 * 
	 * @param c : 클릭된 컴포넌트
	 */
	protected void mousePressed(Component c)
	{
		
	}
	
	@Override
	public void windowActivated(WindowEvent e)
	{
		Object ob = e.getSource();
		
		boolean checked = false;
		for(int i=0; i<windows.size(); i++)
		{
			if(windows.get(i) == ob)
			{
				windowActivated(windows.get(i));
				checked = true;
				break;
			}
		}
		if(! checked)
		{
			super.windowActivated(e);
		}
	}
	@Override
	public void windowClosed(WindowEvent e)
	{
		Object ob = e.getSource();
		
		boolean checked = false;
		for(int i=0; i<windows.size(); i++)
		{
			if(windows.get(i) == ob)
			{
				windowClosed(windows.get(i));
				checked = true;
				break;
			}
		}
		if(! checked)
		{
			super.windowClosed(e);
		}
	}
	@Override
	public void windowClosing(WindowEvent e)
	{
		Object ob = e.getSource();
		
		boolean checked = false;
		for(int i=0; i<windows.size(); i++)
		{
			if(windows.get(i) == ob)
			{
				windowClosing(windows.get(i));
				checked = true;
				break;
			}
		}
		if(! checked)
		{
			super.windowClosing(e);
		}
	}
	
	@Override
	public void windowDeactivated(WindowEvent e)
	{
		Object ob = e.getSource();
		
		boolean checked = false;
		for(int i=0; i<windows.size(); i++)
		{
			if(windows.get(i) == ob)
			{
				windowDeactivated(windows.get(i));
				checked = true;
				break;
			}
		}
		if(! checked)
		{
			super.windowDeactivated(e);
		}
	}
	@Override
	public void windowDeiconified(WindowEvent e)
	{
		Object ob = e.getSource();
		
		boolean checked = false;
		for(int i=0; i<windows.size(); i++)
		{
			if(windows.get(i) == ob)
			{
				windowDeiconified(windows.get(i));
				checked = true;
				break;
			}
		}
		if(! checked)
		{
			super.windowDeiconified(e);
		}
	}
	@Override
	public void windowIconified(WindowEvent e)
	{
		Object ob = e.getSource();
		
		boolean checked = false;
		for(int i=0; i<windows.size(); i++)
		{
			if(windows.get(i) == ob)
			{
				windowIconified(windows.get(i));
				checked = true;
				break;
			}
		}
		if(! checked)
		{
			super.windowIconified(e);
		}
	}
	@Override
	public void windowOpened(WindowEvent e)
	{
		Object ob = e.getSource();
		
		boolean checked = false;
		for(int i=0; i<windows.size(); i++)
		{
			if(windows.get(i) == ob)
			{
				windowOpened(windows.get(i));
				checked = true;
				break;
			}
		}
		if(! checked)
		{
			super.windowOpened(e);
		}
	}
	@Override
	public void mousePressed(MouseEvent e)
	{
		Object ob = e.getSource();
		
		boolean checked = false;
		for(int i=0; i<mouseComponents.size(); i++)
		{
			if(mouseComponents.get(i) == ob)
			{
				mousePressed(mouseComponents.get(i));
				checked = true;
				break;
			}
		}
		if(! checked)
		{
			super.mousePressed(e);
		}
	}
}
