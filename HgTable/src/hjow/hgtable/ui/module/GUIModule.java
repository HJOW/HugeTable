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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import hjow.hgtable.jscript.Refreshable;
import hjow.hgtable.jscript.module.Module;
import hjow.hgtable.ui.GUIManager;
import hjow.swing.jsonSwing.JSONSwingObject;

/**
 * <p>GUI 기반 모듈 객체입니다.</p>
 * 
 * @author HJOW
 *
 */
public abstract class GUIModule extends Module implements ActionListener, ItemListener, WindowListener, MouseListener, MouseMotionListener, ListSelectionListener, ChangeListener, Refreshable, JSONSwingObject
{
	private static final long serialVersionUID = 28227398845814222L;
	protected static transient Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	
	/**
	 * <p>기본 생성자입니다. 직렬화를 위해 있는 생성자이며, 런타임 중 호출되는 일은 없을 것입니다.</p>
	 * 
	 */
	public GUIModule()
	{
		
	}
	
	/**
	 * <p>생성자입니다. 매니저 객체로부터 이 생성자가 호출되어 모듈이 활성화됩니다.</p>
	 * 
	 * @param manager
	 */
	public GUIModule(GUIManager manager)
	{
		setManager(manager);
	}
	
	@Override
	public String getJsonKeyword()
	{
		return "HUGE_TABLE_MODULE";
	}
	
	/**
	 * <p>이 메소드는 이 모듈이 화면에 보여질 때 호출됩니다.</p>
	 */
	public void close()
	{
		
	}
	
	/**
	 * <p>이 메소드는 이 모듈이 열릴 때 호출됩니다.</p>
	 * 
	 */
	public void open()
	{
		
	}
	
	/**
	 * <p>컴포넌트 객체를 반환합니다.</p>
	 * 
	 * @return 컴포넌트 객체
	 */
	public Component getComponent()
	{
		return null;
	}
	
	/**
	 * <p>프로그램이 종료되거나, 매니저 객체가 교체될 때 때 호출됩니다. 순환 참조를 끊어야 합니다.</p>
	 */
	@Override
	public void noMoreUse()
	{
		super.noMoreUse();
		close();
		manager = null;
	}
	
	/**
	 * <p>스크립트 상에서 이 객체를 호출할 수 있게 하려는 경우, 이 메소드를 재정의하여 이 모듈에 대한 도움말 내용을 반환하도록 하십시오.</p>
	 * 
	 */
	@Override
	public String help()
	{
		return null;
	}
	@Override
	public void windowActivated(WindowEvent e)
	{
		
		
	}
	@Override
	public void windowClosed(WindowEvent e)
	{
		
		
	}
	@Override
	public void windowClosing(WindowEvent e)
	{
		
		
	}
	@Override
	public void windowDeactivated(WindowEvent e)
	{
		
		
	}
	@Override
	public void windowDeiconified(WindowEvent e)
	{
		
		
	}
	@Override
	public void windowIconified(WindowEvent e)
	{
		
		
	}
	@Override
	public void windowOpened(WindowEvent e)
	{
		
		
	}
	@Override
	public void actionPerformed(ActionEvent e)
	{
		
		
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
		
		
	}
	@Override
	public void mouseReleased(MouseEvent e)
	{
		
		
	}
	@Override
	public void itemStateChanged(ItemEvent e)
	{
		
		
	}
	@Override
	public void valueChanged(ListSelectionEvent e)
	{
		
	}
	@Override
	public void stateChanged(ChangeEvent e)
	{
		
	}
	@Override
	public void mouseDragged(MouseEvent e)
	{
		
	}
	@Override
	public void mouseMoved(MouseEvent e)
	{
		
	}
	
	/**
	 * <p>자기 자신을 반환합니다.</p>
	 * 
	 * @return 자기 자신
	 */
	public GUIModule getModule()
	{
		return this;
	}
	
	/**
	 * <p>등록된 매니저 객체를 반환합니다. 모듈 내부에서만 사용합니다.</p>
	 * 
	 * @return GUI 매니저 객체
	 */
	protected GUIManager getManager()
	{
		return (GUIManager) manager;
	}
}
