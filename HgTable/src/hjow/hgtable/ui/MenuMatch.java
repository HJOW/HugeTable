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
package hjow.hgtable.ui;

import hjow.hgtable.Main;
import hjow.hgtable.Manager;
import hjow.hgtable.ui.module.GUIDialogModule;
import hjow.hgtable.ui.module.ModuleOnMenu;
import hjow.hgtable.util.ModuleUtil;
import hjow.hgtable.util.debug.DebuggingUtil;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;

import javax.swing.JMenuItem;

/**
 * <p>모듈과 메뉴 객체를 매칭시키기 위한 객체 생성에 관여합니다.</p>
 * 
 * @author HJOW
 *
 */
public class MenuMatch implements Serializable, ActionListener, NeedtoEnd
{
	private static final long serialVersionUID = -3268516461477893459L;
	protected JMenuItem menuItem;
	protected ModuleOnMenu module;
	protected Manager manager;
	
	public MenuMatch()
	{
		
	}
	public MenuMatch(JMenuItem menuItem, ModuleOnMenu module, Manager manager)
	{
		super();
		this.menuItem = menuItem;
		this.module = module;
		this.manager = manager;
	}
	@Override
	public void noMoreUse()
	{
		menuItem = null;
		module = null;
		manager = null;
	}
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == menuItem)
		{
			try
			{
				if(ModuleUtil.checkAccepted(module.getModule()))
				{
					module.open();
				}
				else
				{
					boolean gets = manager.askModuleAccept(module.getModule());
					if(gets)
					{
						String values = Manager.getOption("accepted_modules");
						if(values == null) values = "";
						else if(! values.endsWith(";")) values = values + ";";
						manager.setOption("accepted_modules", values + module.getModule().getModuleId(), manager);
						module.open();
					}
					else
					{
						manager.alert(Manager.applyStringTable("Module") + " " + module.getModule().getName() + " " + Manager.applyStringTable("is declined."));
					}
				}
			}
			catch(Exception e1)
			{
				manager.logError(e1, Manager.applyStringTable("On opening menu of") + " " + module.getModule().getName(), Main.MODE <= DebuggingUtil.RELEASE);
				manager.alert(Manager.applyStringTable("Cannot open module") + " " + module.getModule().getName() 
						+ " (" + Manager.applyStringTable("Reason") + " : " + e1.getMessage() + ")");
				if(Main.MODE >= DebuggingUtil.DEBUG) e1.printStackTrace();
			}
		}
	}
	public JMenuItem getMenuItem()
	{
		return menuItem;
	}
	public void setMenuItem(JMenuItem menuItem)
	{
		this.menuItem = menuItem;
	}
	public ModuleOnMenu getModule()
	{
		return module;
	}
	public void setModule(GUIDialogModule module)
	{
		this.module = module;
	}
	@Override
	public boolean isAlive()
	{
		return module != null;
	}
}
