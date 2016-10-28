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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import hjow.dbtool.common.DBTool;
import hjow.hgtable.Manager;
import hjow.hgtable.dao.Dao;
import hjow.hgtable.dao.JdbcDao;
import hjow.hgtable.tableset.TableSet;
import hjow.hgtable.ui.GUIManager;
import hjow.hgtable.ui.swing.UIList;
import hjow.hgtable.util.DataUtil;

/**
 * <p>테이블 리스트를 보는 대화 상자 객체입니다.</p>
 * 
 * @author HJOW
 *
 */
public class SwingTableListView extends ListView implements ListSelectionListener
{
	private static final long serialVersionUID = 5370406837398187826L;
	public static final transient long sid = serialVersionUID;
	protected JDialog dialog;
	protected JPanel mainPanel;
	protected JPanel upPanel;
	protected JPanel centerPanel;
	protected UIList objectList;
	protected JScrollPane listScroll;
	protected int selected = -1;
	protected boolean selectChanged = false;
	protected DefaultListModel listModel;
	protected JButton btClose;
	protected JPanel buttonPanel;
	protected JButton btRefresh;
	protected JTextField searchField;
	protected JPopupMenu popupMenu;
	protected JMenuItem menuTableInfo;
	protected JMenuItem menuTableSelects;
	
	/**
	 * <p>객체를 생성하고 컴포넌트를 초기화합니다.</p>
	 * 
	 * @param frame : 프레임 객체, AWT 호환
	 * @param manager : 매니저 객체
	 */
	public SwingTableListView(Frame frame, GUIManager manager)
	{
		super(manager);
		
		dialog = new JDialog(frame);
		dialog.setTitle(Manager.applyStringTable("Table List"));
		
		dialog.setSize(270, 400);
		
		Dimension scSize = Toolkit.getDefaultToolkit().getScreenSize();
		dialog.setLocation((int)(scSize.getWidth()/2 - dialog.getWidth()/2), (int)(scSize.getHeight()/2 - dialog.getHeight()/2));
		
		dialog.setLayout(new BorderLayout());
		
		mainPanel = new JPanel();
		dialog.add(mainPanel, BorderLayout.CENTER);
		
		mainPanel.setLayout(new BorderLayout());
		
		upPanel = new JPanel();
		centerPanel = new JPanel();
		
		mainPanel.add(upPanel, BorderLayout.NORTH);
		mainPanel.add(centerPanel, BorderLayout.CENTER);
		
		upPanel.setLayout(new BorderLayout());
		centerPanel.setLayout(new BorderLayout());
		
		listModel = new DefaultListModel();
		objectList = new UIList(listModel);
		objectList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listScroll = new JScrollPane(objectList);
		
		popupMenu = new JPopupMenu();
		
		menuTableInfo = new JMenuItem(Manager.applyStringTable("Information"));
		menuTableInfo.addActionListener(this);
		popupMenu.add(menuTableInfo);
		
		menuTableSelects = new JMenuItem(Manager.applyStringTable("Select"));
		menuTableSelects.addActionListener(this);
		popupMenu.add(menuTableSelects);
		
		objectList.setComponentPopupMenu(popupMenu);
		
		centerPanel.add(listScroll);
		
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		
		searchField = new JTextField(10);
		btRefresh = new JButton(Manager.applyStringTable("Search"));
		btClose = new JButton("X");
		buttonPanel.add(searchField);
		buttonPanel.add(btRefresh);
		buttonPanel.add(btClose);
		
		upPanel.add(buttonPanel, BorderLayout.EAST);
		
		btRefresh.addActionListener(this);
		btClose.addActionListener(this);
		dialog.addWindowListener(this);
		objectList.addMouseListener(this);
		objectList.addListSelectionListener(this);
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
							TableSet tableResult = tool.tableList(false, searchField.getText());
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
					else
					{
						listModel.addElement(Manager.applyStringTable("Not available"));
					}
				}
			}
		}
	}
	@Override
	public void clear()
	{
		listModel.removeAllElements();
	}
	@Override
	public void actionPerformed(ActionEvent e)
	{
		Object ob = e.getSource();
		int selectedIndex = objectList.getSelectedIndex();
		if(ob == btClose)
		{
			dialog.setVisible(false);
		}
		else if(ob == btRefresh)
		{
			Map<String, Object> refreshParam = new Hashtable<String, Object>();
			refreshParam.put("selected_dao", manager.getSelectedDao());
			refresh(refreshParam);
		}
		else if(ob == menuTableInfo)
		{
			if(selectedIndex < 0) return;
			tableSelected(selectedIndex, false);
			popupMenu.setVisible(false);
		}
		else if(ob == menuTableSelects)
		{
			if(selectedIndex < 0) return;
			simplySelectTable(selectedIndex, false);
		}
	}
	
	protected void tableSelected(int selected, boolean ignoreErr)
	{
		String tableName = String.valueOf(listModel.getElementAt(selected));
		if(manager.getDao() != null && manager.getDao().isAlive() && manager.getDao().getDataSourceTool() != null
				&& (manager.getDao().getDataSourceTool() instanceof DBTool) 
				&& ((DBTool) manager.getDao().getDataSourceTool()).getTableInfoQuery(tableName) != null) 
		{
			try
			{
				manager.logTable(((DBTool) manager.getDao().getDataSourceTool()).tableInfo(tableName));
			}
			catch (Exception e1)
			{
				if(ignoreErr)
				{
					manager.setQueryAreaText(manager.getQueryAreaText() + tableName);
				}
				else
				{
					manager.logError(e1, trans("Cannot read table information" + " : ") + tableName);
				}
			}
		}
		else manager.setQueryAreaText(manager.getQueryAreaText() + tableName);
	}
	protected void simplySelectTable(int selected, boolean ignoreErr)
	{
		String tableName = String.valueOf(listModel.getElementAt(selected));
		try
		{
			manager.logTable(((DBTool) manager.getDao().getDataSourceTool()).simpleSelect(tableName));
		}
		catch (Exception e1)
		{
			if(ignoreErr)
			{
				manager.setQueryAreaText(manager.getQueryAreaText() + tableName);
			}
			else
			{
				manager.logError(e1, trans("Cannot read table information" + " : ") + tableName);
			}
		}
	}
	
	@Override
	public void mousePressed(MouseEvent e)
	{
		Object ob = e.getSource();
		
		if(ob == objectList)
		{
			if(selectChanged && e.getClickCount() == 2)
			{				
				e.consume();
				int selectedIndex = objectList.getSelectedIndex();
				if(selectedIndex >= 0) tableSelected(selectedIndex, true);
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
		else
		{
			e.consume();
		}
	}
	@Override
	public void valueChanged(ListSelectionEvent e)
	{
		Object ob = e.getSource();
		if(ob == objectList)
		{			
			
		}
	}
	@Override
	public void windowClosing(WindowEvent e)
	{
		Object ob = e.getSource();
		if(ob == dialog)
		{
			dialog.setVisible(false);
		}
	}
	@Override
	public void open()
	{
		refresh(null);
		dialog.setVisible(true);
	}	
	
	@Override
	public void noMoreUse()
	{
		super.noMoreUse();
	}
}