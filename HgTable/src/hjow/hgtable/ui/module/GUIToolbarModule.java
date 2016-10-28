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

import javax.swing.JToolBar;

import hjow.hgtable.ui.GUIManager;

/**
 * <p>툴바형 GUI 모듈입니다.</p>
 * 
 * @author HJOW
 *
 */
public abstract class GUIToolbarModule extends GUIModule implements ModuleOnMenu
{
	private static final long serialVersionUID = -119306042460687200L;
	
	protected transient JToolBar mainPanel = new JToolBar();
	protected Integer areaSize = new Integer(4);
	protected Integer shortcut;
	protected Integer mask;
	
	/**
	 * <p>기본 생성자입니다.</p>
	 */
	public GUIToolbarModule()
	{
		super();
		mainPanel.setFloatable(false);
	}
	
	/**
	 * <p>매니저 객체를 받는 생성자입니다.</p>
	 * 
	 * @param manager
	 */
	public GUIToolbarModule(GUIManager manager)
	{
		super(manager);
		mainPanel.setFloatable(false);
	}
	
	/**
	 * <p>대화 상자 객체를 반환합니다.</p>
	 * 
	 * @return 대화 상자 객체
	 */
	@Override
	public Component getComponent()
	{
		return mainPanel;
	}
	
	@Override
	public void open()
	{
		mainPanel.setVisible(true);
	}
	
	@Override
	public void close()
	{
		mainPanel.setVisible(false);
	}
	
	@Override
	public int getMenuLocation()
	{
		return GUIDialogModule.ON_MENU_NONE;
	}
	@Override
	public String getMenuLocationName()
	{
		return GUIDialogModule.menuCodeToName(getMenuLocation());
	}

	public Integer getAreaSize()
	{
		return areaSize;
	}

	public void setAreaSize(Integer area)
	{
		this.areaSize = area;
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
}
