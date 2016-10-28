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
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;

import hjow.hgtable.Manager;
import hjow.hgtable.jscript.module.AbstractConsoleModule;
import hjow.hgtable.jscript.module.AbstractModule;
import hjow.hgtable.ui.GUIManager;
import hjow.hgtable.ui.module.GUIDialogModule;
import hjow.hgtable.ui.module.GUIPanelModule;
import hjow.hgtable.ui.swing.UIEditorPane;
import hjow.hgtable.ui.swing.UIList;
import hjow.hgtable.util.DataUtil;
import hjow.hgtable.util.ModuleUtil;

/**
 * <p>현재 불러온 모듈 리스트와 그 상태를 보여 줍니다.</p>
 * 
 * @author HJOW
 *
 */
public class SwingModuleListView extends GUIDialogModule
{
	private static final long serialVersionUID = 2401785305423424458L;
	public static final transient long sid = serialVersionUID;
	protected JDialog dialog;
	protected GUIManager manager;
	protected JPanel mainPanel;
	protected JPanel upPanel;
	protected JPanel centerPanel;
	protected JPanel downPanel;
	protected JSplitPane mainSplit;
	protected UIList lstModules;
	protected JScrollPane scLstModules;
	protected JEditorPane area;
	protected JScrollPane scArea;
	
	protected List<AbstractModule> modules;
	protected JButton btClose;
	
	protected transient Set<Long> acceptedList = new HashSet<Long>();
	protected JButton btAccept;
	
	/**
	 * <p>모듈 리스트 대화 상자와 컴포넌트들을 초기화합니다.</p>
	 * 
	 * @param manager : GUI 기반 매니저 객체
	 */
	public SwingModuleListView(GUIManager manager)
	{
		this.manager = manager;
		
		dialog = new JDialog(manager.getFrame());
		dialog.setSize(400, 400);
		
		dialog.setTitle(Manager.applyStringTable("Module List"));
		
		Dimension scSize = Toolkit.getDefaultToolkit().getScreenSize();
		dialog.setLocation((int)(scSize.getWidth()/2 - dialog.getWidth()/2), (int)(scSize.getHeight()/2 - dialog.getHeight()/2));
		
		dialog.setLayout(new BorderLayout());
		
		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		dialog.add(mainPanel, BorderLayout.CENTER);
		
		upPanel     = new JPanel();
		centerPanel = new JPanel();
		downPanel   = new JPanel();
		
		mainPanel.add(upPanel    , BorderLayout.NORTH);
		mainPanel.add(centerPanel, BorderLayout.CENTER);
		mainPanel.add(downPanel  , BorderLayout.SOUTH);
		
		centerPanel.setLayout(new BorderLayout());
		
		mainSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		centerPanel.add(mainSplit, BorderLayout.CENTER);
		
		lstModules = new UIList();
		lstModules.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lstModules.addListSelectionListener(this);
		scLstModules = new JScrollPane(lstModules);
		mainSplit.setTopComponent(scLstModules);
		
		area = new UIEditorPane();
		area.setEditable(false);
		scArea = new JScrollPane(area);
		mainSplit.setBottomComponent(scArea);
		
		downPanel.setLayout(new FlowLayout());
		
		btAccept = new JButton("");
		btAccept.setEnabled(false);
		btAccept.addActionListener(this);
		downPanel.add(btAccept);
		
		btClose = new JButton(Manager.applyStringTable("Close"));
		btClose.addActionListener(this);
		downPanel.add(btClose);
	}
	
	/**
	 * <p>이 메소드는 매니저 객체에 의해 호출됩니다.</p>
	 * 
	 * @param modules : 모듈 리스트
	 */
	public void setList(List<AbstractModule> modules)
	{
		this.modules = modules;
		Vector<String> moduleStr = new Vector<String>();
		for(AbstractModule m : modules)
		{
			moduleStr.add(m.getName() + "\t(" + m.getModuleId() + ")");
		}
		lstModules.setListData(moduleStr);
		area.setText("");
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		Object ob = e.getSource();
		if(ob == btClose)
		{
			close();
		}
		else if(ob == btAccept)
		{
			AbstractModule m = modules.get(lstModules.getSelectedIndex());
			if(ModuleUtil.checkAccepted(m))
			{
				for(Long l : acceptedList)
				{
					if(l.longValue() == m.getModuleId())
					{
						acceptedList.remove(l);
						break;
					}
				}
			}
			else if(! ModuleUtil.checkAccepted(m))
			{
				acceptedList.add(new Long(m.getModuleId()));
			}
			
			applyAcceptedOption();
			
			if(ModuleUtil.isDefaultModule(m))
			{
				btAccept.setText("");
				btAccept.setEnabled(false);
			}
			else if(ModuleUtil.checkAccepted(m))
			{
				btAccept.setText(Manager.applyStringTable("Disable"));
				btAccept.setEnabled(true);
			}
			else if(! ModuleUtil.checkAccepted(m))
			{
				btAccept.setText(Manager.applyStringTable("Accept to use"));
				btAccept.setEnabled(true);
			}
		}
	}
	
	@Override
	public void valueChanged(ListSelectionEvent e)
	{
		Object ob = e.getSource();
		if(ob == lstModules)
		{
			int selected = lstModules.getSelectedIndex();
			if(selected < 0)
			{
				btAccept.setText("");
				btAccept.setEnabled(false);
				area.setText("");
				return;
			}
			
			AbstractModule m = modules.get(selected);
			StringBuffer text = new StringBuffer(m.getName() + "\t" + "(" + m.getModuleId() + ")" + "\n\n");
			
			if(ModuleUtil.isDefaultModule(m))
			{
				text = text.append(Manager.applyStringTable("Default Module") + "\n\n\n");
			}
			else
			{
				text = text.append(Manager.applyStringTable("Authorized") + " : " + ModuleUtil.checkAuthorize(m) + "\n");
				text = text.append(Manager.applyStringTable("Accepted")   + " : " + ModuleUtil.checkAccepted(m)  + "\n\n\n");
			}
			
			if(ModuleUtil.isDefaultModule(m))
			{
				btAccept.setText("");
				btAccept.setEnabled(false);
			}
			else if(ModuleUtil.checkAccepted(m))
			{
				btAccept.setText(Manager.applyStringTable("Disable"));
				btAccept.setEnabled(true);
			}
			else if(! ModuleUtil.checkAccepted(m))
			{
				btAccept.setText(Manager.applyStringTable("Accept to use"));
				btAccept.setEnabled(true);
			}
				
			
			text = text.append(Manager.applyStringTable("Type")   + " : ");
			if(m instanceof AbstractConsoleModule)
			{
				text = text.append(Manager.applyStringTable("Console"));
				if(m instanceof GUIDialogModule)
				{
					text = text.append(", " + Manager.applyStringTable("Dialog"));
				}
				else if(m instanceof GUIPanelModule)
				{
					text = text.append(", " + Manager.applyStringTable("Panel"));
				}
			}
			else if(m instanceof GUIDialogModule)
			{
				text = text.append(Manager.applyStringTable("Dialog"));
			}
			else if(m instanceof GUIPanelModule)
			{
				text = text.append(Manager.applyStringTable("Panel"));
			}
			
			text = text.append(m.description() + "\n\n\n");
			text = text.append(m.getLicense() + "\n");
			area.setText(text.toString());
		}
	}
	
	/**
	 * <p>변경한 사용 동의 옵션을 실제 옵션에 반영합니다.</p>
	 * 
	 */
	protected void applyAcceptedOption()
	{
		String values = "";
		for(Long l : acceptedList)
		{
			values = values + ";" + String.valueOf(l);
		}
		values = values.substring(1).trim();
		manager.setOption("accepted_modules", values, manager);
	}
	
	@Override
	public void open()
	{
		acceptedList.clear();
		String nowOption = Manager.getOption("accepted_modules");
		if(DataUtil.isEmpty(nowOption)) nowOption = "";
		StringTokenizer commaTokenizer = new StringTokenizer(nowOption, ";");
		while(commaTokenizer.hasMoreTokens())
		{
			acceptedList.add(new Long(commaTokenizer.nextToken().trim()));
		}
		super.open();
	}
	
	@Override
	public void noMoreUse()
	{
		manager = null;
		acceptedList.clear();
		acceptedList = null;
	}

	@Override
	public boolean isAlive()
	{
		return manager != null;
	}
	
	@Override
	public Dialog getDialog()
	{
		return dialog;
	}
}
