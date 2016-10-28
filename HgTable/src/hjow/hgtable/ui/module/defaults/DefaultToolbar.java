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
package hjow.hgtable.ui.module.defaults;

import java.awt.event.ActionEvent;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.UIManager;

import hjow.hgtable.ui.GUIManager;
import hjow.hgtable.ui.module.GUIToolbarModule;

public class DefaultToolbar extends GUIToolbarModule
{
	private static final long serialVersionUID = -147856875128275930L;
	public static final transient long sid = serialVersionUID;
	protected JButton btSave;
	protected JButton btLoad;
	protected JButton btRun;
	protected JButton btCancel;
	
	/**
	 * <p>기본 툴바를 생성합니다. GUI 매니저 객체 별도 삽입이 필요합니다.</p>
	 */
	public DefaultToolbar()
	{
		super();
		setModuleId(serialVersionUID);
	}
	
	/**
	 * <p>기본 툴바를 생성합니다. GUI 매니저 객체가 필요합니다.</p>
	 * 
	 * @param manager : 매니저 객체
	 */
	public DefaultToolbar(GUIManager manager)
	{
		super(manager);
		setModuleId(serialVersionUID);
	}
	
	@Override
	public void initializeComponents()
	{
		Icon icon = UIManager.getIcon("FileView.floppyDriveIcon");
		if(icon != null)
		{
			btSave = new JButton(icon);
		}
		else
		{
			btSave = new JButton(trans("Save"));
		}
		btSave.setToolTipText(trans("Save"));
		
		icon = UIManager.getIcon("Tree.openIcon");
		if(icon != null)
		{
			btLoad = new JButton(icon);
		}
		else
		{
			btLoad = new JButton(trans("Load"));
		}
		btLoad.setToolTipText(trans("Load"));
		
		icon = UIManager.getIcon("FileView.computerIcon");
		if(icon != null)
		{
			btRun = new JButton(icon);
		}
		else
		{
			btRun = new JButton(trans("Run"));
		}
		btRun.setToolTipText(trans("Run"));
		
		icon = UIManager.getIcon("Tree.collapsedIcon");
		if(icon != null)
		{
			btCancel = new JButton(icon);
		}
		else
		{
			btCancel = new JButton(trans("Cancel"));
		}
		btCancel.setToolTipText(trans("Cancel"));
		
		mainPanel.add(btSave);
		mainPanel.add(btLoad);
		mainPanel.add(btRun);
		mainPanel.add(btCancel);
		
		btSave.addActionListener(this);
		btLoad.addActionListener(this);
		btRun.addActionListener(this);
		btCancel.addActionListener(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		Object ob = e.getSource();
		if(ob == btSave)
		{
			try
			{
				getManager().actComponent("menuFileSave", this, e);
			}
			catch (Throwable e1)
			{
				e1.printStackTrace();
			}
		}
		if(ob == btLoad)
		{
			try
			{
				getManager().actComponent("menuFileLoad", this, e);
			}
			catch (Throwable e1)
			{
				e1.printStackTrace();
			}
		}
		if(ob == btRun)
		{
			try
			{
				getManager().actComponent("btRunScript", this, e);
			}
			catch (Throwable e1)
			{
				e1.printStackTrace();
			}
		}
		if(ob == btCancel)
		{
			try
			{
				getManager().actComponent("btCancelScript", this, e);
			}
			catch (Throwable e1)
			{
				e1.printStackTrace();
			}
		}
	}
}
