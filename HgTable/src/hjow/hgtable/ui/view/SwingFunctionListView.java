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

package hjow.hgtable.ui.view;

import hjow.dbtool.common.DBTool;
import hjow.hgtable.Manager;
import hjow.hgtable.dao.Dao;
import hjow.hgtable.dao.JdbcDao;
import hjow.hgtable.tableset.TableSet;
import hjow.hgtable.ui.GUIManager;
import hjow.hgtable.util.DataUtil;

import java.awt.Frame;
import java.awt.event.MouseEvent;
import java.util.Map;

/**
 * <p>함수 리스트를 보는 대화 상자 객체입니다.</p>
 * 
 * @author HJOW
 *
 */
public class SwingFunctionListView extends SwingProcedureListView
{
	private static final long serialVersionUID = -6000582178350529084L;
	public static final transient long sid = serialVersionUID;
	public SwingFunctionListView(Frame frame, GUIManager manager)
	{
		super(frame, manager);
		dialog.setTitle(Manager.applyStringTable("Function List"));
	}
	@Override
	public void refresh(Map<String, Object> additionalData)
	{		
		if(! dialog.isVisible()) return;
		if(additionalData != null && DataUtil.isNotEmpty(additionalData.get("selected_dao")))
		{
			Dao dao = manager.getDao();
			
			clear();
			
			if(dao != null && dao.isAlive())
			{
				if(dao instanceof JdbcDao)
				{
					DBTool tool = ((JdbcDao) dao).getDBTool();
					if(tool != null)
					{
						try
						{
							TableSet tableResult = tool.functionList(false, false, searchField.getText());
							if(tableResult != null)
							{
								for(int i=0; i<tableResult.getRecordCount(); i++)
								{
									listModel.addElement(tableResult.getRecord(i).getDataOf(0));
								}
							}
						}
						catch(Throwable e)
						{
							manager.logError(e, Manager.applyStringTable("On refresh table list"), true);
						}
					}
				}
			}
		}
	}
	@Override
	protected void objectListClicked(MouseEvent e)
	{
		if(selectChanged && e.getClickCount() == 2)
		{				
			e.consume();
			
			boolean defaultAct = true;
			
			Dao dao = manager.getDao();
			if(dao != null && dao.isAlive())
			{
				if(dao instanceof JdbcDao)
				{
					DBTool tool = ((JdbcDao) dao).getDBTool();
					if(tool != null)
					{
						if(tool.getFunctionScriptQuery(String.valueOf(objectList.getSelectedValue())) != null)
						{
							try
							{
								String scripts = tool.getFunctionScript(dao, String.valueOf(objectList.getSelectedValue()));
								if(scripts != null)
								{
									manager.setQueryAreaText(manager.getQueryAreaText() + "\n" + String.valueOf(scripts));
									
									defaultAct = false;
								}
							}
							catch(Throwable e1)
							{
								manager.logError(e1, Manager.applyStringTable("On getting function scripts"));
								defaultAct = true;
							}
						}
					}
				}
			}
			
			if(defaultAct) manager.setQueryAreaText(manager.getQueryAreaText() + String.valueOf(objectList.getSelectedValue()));
		}
		else
		{
			if(selected != objectList.getSelectedIndex())
			{
				selected = objectList.getSelectedIndex();
				selectChanged = false;
			}
			else selectChanged = true;
		}
	}
}
