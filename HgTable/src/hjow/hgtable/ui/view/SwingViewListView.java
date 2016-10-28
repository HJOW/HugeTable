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

import java.awt.Frame;
import java.util.Map;

import hjow.dbtool.common.DBTool;
import hjow.hgtable.Manager;
import hjow.hgtable.dao.Dao;
import hjow.hgtable.dao.JdbcDao;
import hjow.hgtable.tableset.TableSet;
import hjow.hgtable.ui.GUIManager;
import hjow.hgtable.util.DataUtil;

/**
 * <p>뷰 리스트를 보는 대화 상자 객체입니다.</p>
 * 
 * @author HJOW
 *
 */
public class SwingViewListView extends SwingTableListView
{
	private static final long serialVersionUID = -7157874646084934803L;
	public static final transient long sid = serialVersionUID;
	public SwingViewListView(Frame frame, GUIManager manager)
	{
		super(frame, manager);
		dialog.setTitle(Manager.applyStringTable("View List"));
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
							TableSet tableResult = tool.viewList(false, false, searchField.getText());
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
	protected void tableSelected(int selected, boolean ignoreErr)
	{
		String viewName = String.valueOf(listModel.getElementAt(selected));
		if(manager.getDao() != null && manager.getDao().isAlive() && manager.getDao().getDataSourceTool() != null
				&& (manager.getDao().getDataSourceTool() instanceof DBTool) 
				&& ((DBTool) manager.getDao().getDataSourceTool()).getTableInfoQuery(viewName) != null) 
		{
			try
			{
				TableSet results = ((DBTool) manager.getDao().getDataSourceTool()).simpleSelect(viewName);
				if(results == null || results.getRecordCount() <= 0) 
				{
					alert(trans("Cannot read information because there is no data."));
					return;
				}
				
				manager.logTable(results.typeInfo());
			}
			catch (Exception e1)
			{
				if(ignoreErr)
				{
					manager.setQueryAreaText(manager.getQueryAreaText() + viewName);
				}
				else
				{
					manager.logError(e1, trans("Cannot read table information" + " : ") + viewName);
				}
			}
		}
		else manager.setQueryAreaText(manager.getQueryAreaText() + viewName);
	}
}
