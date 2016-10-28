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
import java.awt.Dialog;
import java.awt.event.WindowEvent;

import javax.swing.JOptionPane;

import hjow.hgtable.Manager;
import hjow.hgtable.ui.GUIManager;

/**
 * <p>대화 상자형 GUI 모듈입니다.</p>
 * 
 * @author HJOW
 *
 */
public abstract class GUIDialogModule extends GUIModule implements ModuleOnMenu
{
	private static final long serialVersionUID = -119306042460687200L;
	
	protected Integer shortcut;
	protected Integer mask;
	
	public static final int ON_MENU_FILE = 0;
	public static final int ON_MENU_TOOL = 1;
	public static final int ON_MENU_VIEW = 2;
	public static final int ON_MENU_TRANS = 3;
	public static final int ON_MENU_HELP = 4;
	public static final int ON_MENU_OTHER = 5;
	public static final int ON_MENU_NONE = 6;
	
	/**
	 * <p>기본 생성자입니다.</p>
	 */
	public GUIDialogModule()
	{
		super();
	}
	
	/**
	 * <p>매니저 객체를 받는 생성자입니다.</p>
	 * 
	 * @param manager
	 */
	public GUIDialogModule(GUIManager manager)
	{
		super(manager);
	}
	
		
	/**
	 * <p>대화 상자를 닫습니다.</p>
	 */
	@Override
	public void close()
	{
		if(getDialog() != null) getDialog().setVisible(false);
	}
	
	/**
	 * <p>대화 상자를 엽니다.</p>
	 * 
	 */
	@Override
	public void open()
	{
		getDialog().setVisible(true);
	}
	
	/**
	 * <p>경고 메시지 대화 상자를 엽니다.</p>
	 * 
	 * @param msg : 보일 메시지
	 */
	public void alert(String msg)
	{
		JOptionPane.showMessageDialog(getDialog(), msg);
	}
	
	/**
	 * <p>예, 아니오를 입력받는 대화 상자를 엽니다.</p>
	 * 
	 * @param msg : 보일 메시지
	 * @return 사용자가 입력한 예/아니오
	 */
	public boolean confirm(String msg)
	{
		return JOptionPane.showConfirmDialog(getDialog(), msg, msg, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
	}
	
	/**
	 * <p>대화 상자 객체를 반환합니다.</p>
	 * 
	 * @return 대화 상자 객체
	 */
	public Dialog getDialog()
	{
		return null;
	}
	
	@Override
	public int getMenuLocation()
	{
		return ON_MENU_OTHER;
	}
	
	@Override
	public String getMenuLocationName()
	{
		return menuCodeToName(getMenuLocation());
	}
	
	@Override
	public Integer getShortcut()
	{
		return shortcut;
	}

	public void setShortcut(Integer shortcut)
	{
		this.shortcut = shortcut;
	}

	@Override
	public Integer getMask()
	{
		return mask;
	}

	public void setMask(Integer mask)
	{
		this.mask = mask;
	}

	/**
	 * <p>메뉴 코드를 이름으로 반환합니다.</p>
	 * 
	 * @param menuCode : 메뉴 코드
	 * @return 메뉴 이름
	 */
	public static String menuCodeToName(int menuCode)
	{
		switch(menuCode)
		{
		case ON_MENU_FILE:
			return Manager.applyStringTable("File");
		case ON_MENU_HELP:
			return Manager.applyStringTable("Help");
		case ON_MENU_TOOL:
			return Manager.applyStringTable("Tool");
		case ON_MENU_TRANS:
			return Manager.applyStringTable("Transaction");
		case ON_MENU_VIEW:
			return Manager.applyStringTable("View"); 
		}
		return null;
	}
	
	/**
	 * <p>대화 상자 객체를 반환합니다.</p>
	 * 
	 * @return 대화 상자 객체
	 */
	@Override
	public Component getComponent()
	{
		return getDialog();
	}
	
	@Override
	public void windowClosing(WindowEvent e)
	{
		Object ob = e.getSource();
		if(ob == getDialog())
		{
			close();
		}
		else super.windowClosing(e);
	}
}
