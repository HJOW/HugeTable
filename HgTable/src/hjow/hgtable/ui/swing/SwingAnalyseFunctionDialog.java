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
package hjow.hgtable.ui.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import hjow.hgtable.Manager;
import hjow.hgtable.analyze.AnalyzeFunction;
import hjow.hgtable.jscript.JScriptRunner;
import hjow.hgtable.tableset.Column;
import hjow.hgtable.tableset.ColumnWithTableInfo;
import hjow.hgtable.tableset.TableSet;
import hjow.hgtable.ui.GUIManager;
import hjow.hgtable.ui.NeedtoEnd;
import hjow.hgtable.util.AnalyzeUtil;
import hjow.hgtable.util.DataUtil;

/**
 * <p>분석 함수 사용을 위한 대화 상자입니다.</p>
 * 
 * @author HJOW
 *
 */
public class SwingAnalyseFunctionDialog implements NeedtoEnd
{
	protected JDialog dialog;
	protected JPanel mainPanel;
	protected JTabbedPane tabPanel;
	protected JPanel upPanel;
	protected JButton btClose;
	protected JPanel columnFunctionTab;
	protected JPanel tableFunctionTab;
	protected UIList selectedColumnList;
	protected JPanel columnFunctionCenterPanel;
	protected JPanel columnFunctionDownPanel;
	protected JScrollPane selectedColumnScroll;
	protected JSplitPane columnFunctionSplitPane;
	protected JPanel columnFunctionLeftPanel;
	protected JPanel[] columnPns;
	protected UIList selectTableListOnColumnFunction;
	protected JScrollPane selectTableScrollOnColumnFunction;
	protected UIList selectColumnListOnColumnFunction;
	protected JScrollPane selectColumnScrollOnColumnFunction;
	protected JButton btPutOnColumnTab;
	protected DefaultListModel selectedColumnModel;
	protected GUIManager manager;
	protected JButton btActOnColumnTab;
	protected JSplitPane mainSplit;
	protected SwingArgumentView argPanel;
	protected JSplitPane tableSplit;
	protected JPanel tableLeftPanel;
	protected UIList selectedTableList;
	protected JScrollPane selectedTableScroll;
	protected JPanel tableRightPanel;
	protected UIList selectedTableListOnTableFunction;
	protected JScrollPane selectedTableScrollOnTableFunction;
	protected DefaultListModel selectedTableModel;
	protected JPanel tablePanel;
	protected JButton btActOnTableTab;
	protected JButton btPutOnTableTab;
	protected JPanel tableDownPanel;
	protected JPanel columnFunctionUpPanel;
	protected DefaultComboBoxModel columnFunctionModel;
	protected UIComboBox cbColumnFunction;
	protected JPanel tableUpPanel;
	protected UIComboBox cbTableFunction;
	protected DefaultComboBoxModel tableFunctionModel;
	protected Vector<TableSet> addedTableSets = new Vector<TableSet>();
	private JPanel columnFunctionLeftMainPanel;
	private JPanel columnFunctionLeftDownPanel;
	
	/**
	 * <p>컴포넌트들을 초기화하고 객체를 생성합니다.</p>
	 * 
	 * @param manager : 매니저 객체
	 */
	public SwingAnalyseFunctionDialog(GUIManager manager)
	{
		this.manager = manager;
		dialog = (JDialog) manager.newDialog(false);
		
		Dimension scSize = Toolkit.getDefaultToolkit().getScreenSize();
		dialog.setSize((int)(600), (int)(450));
		dialog.setLocation((int)(scSize.getWidth()/2 - dialog.getWidth()/2), (int)(scSize.getHeight()/2 - dialog.getHeight()/2));
		
		dialog.setTitle(Manager.applyStringTable("Analyze Function"));
		
		dialog.setLayout(new BorderLayout());
		dialog.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				close();
			}
		});
		
		mainPanel = new JPanel();
		dialog.add(mainPanel, BorderLayout.CENTER);
		
		mainPanel.setLayout(new BorderLayout());
		
		upPanel = new JPanel();
		upPanel.setLayout(new FlowLayout());
		mainPanel.add(upPanel, BorderLayout.NORTH);
		
		mainSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		mainPanel.add(mainSplit, BorderLayout.CENTER);
		
		btClose = new JButton(Manager.applyStringTable("Close"));
		upPanel.add(btClose);
		btClose.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				close();
			}
		});
		
		tabPanel = new JTabbedPane();
		mainSplit.setTopComponent(tabPanel);
		
		argPanel = new SwingArgumentView();
		mainSplit.setBottomComponent(argPanel);
		
		columnFunctionTab = new JPanel();
		tabPanel.addTab(Manager.applyStringTable("Column"), columnFunctionTab);
		
		columnFunctionTab.setLayout(new BorderLayout());
		
		columnFunctionUpPanel     = new JPanel();
		columnFunctionCenterPanel = new JPanel();
		columnFunctionDownPanel   = new JPanel();
		
		columnFunctionSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
//		columnFunctionScroll = new JScrollPane(columnFunctionSplitPane);
		
		columnFunctionLeftPanel = new JPanel();
		
		selectedColumnModel = new DefaultListModel();
		selectedColumnList = new UIList(selectedColumnModel);
		selectedColumnList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		selectedColumnScroll = new JScrollPane(selectedColumnList);
		
		columnFunctionSplitPane.setRightComponent(selectedColumnScroll);
		columnFunctionSplitPane.setLeftComponent(columnFunctionLeftPanel);
		
		columnFunctionLeftPanel.setLayout(new BorderLayout());
		columnFunctionLeftMainPanel = new JPanel();
		columnFunctionLeftPanel.add(columnFunctionLeftMainPanel, BorderLayout.CENTER);
		
		columnPns = new JPanel[2];
		columnFunctionLeftMainPanel.setLayout(new GridLayout(columnPns.length, 1));
		
		for(int i=0; i<columnPns.length; i++)
		{
			columnPns[i] = new JPanel();
			columnPns[i].setLayout(new BorderLayout());
			columnFunctionLeftMainPanel.add(columnPns[i]);
		}
		
		selectTableListOnColumnFunction = new UIList();
		selectTableListOnColumnFunction.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		selectTableListOnColumnFunction.addListSelectionListener(new ListSelectionListener()
		{
			@Override
			public void valueChanged(ListSelectionEvent e)
			{
				refreshColumnList((TableSet) selectTableListOnColumnFunction.getSelectedValue());
			}
		});
		selectTableScrollOnColumnFunction = new JScrollPane(selectTableListOnColumnFunction);
		columnPns[0].add(selectTableScrollOnColumnFunction, BorderLayout.CENTER);
		
		selectColumnListOnColumnFunction = new UIList();
		selectColumnListOnColumnFunction.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		selectColumnScrollOnColumnFunction = new JScrollPane(selectColumnListOnColumnFunction);
		columnPns[1].add(selectColumnScrollOnColumnFunction, BorderLayout.CENTER);
		
		columnFunctionLeftDownPanel = new JPanel();
		columnFunctionLeftDownPanel.setLayout(new FlowLayout());
		
		btPutOnColumnTab = new JButton(Manager.applyStringTable("Add"));
		btPutOnColumnTab.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				putOnColumnTab();
			}
		});
		
		columnFunctionLeftDownPanel.add(btPutOnColumnTab);
		columnFunctionLeftPanel.add(columnFunctionLeftDownPanel, BorderLayout.SOUTH);
		
		columnFunctionCenterPanel.setLayout(new BorderLayout());
		columnFunctionCenterPanel.add(columnFunctionSplitPane, BorderLayout.CENTER);
		
		columnFunctionDownPanel.setLayout(new FlowLayout());
		
		btActOnColumnTab = new JButton(Manager.applyStringTable("Execute"));
		btActOnColumnTab.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				actOnColumnTab();
			}
		});
		columnFunctionDownPanel.add(btActOnColumnTab);
		
		columnFunctionTab.add(columnFunctionCenterPanel, BorderLayout.CENTER);
		columnFunctionTab.add(columnFunctionDownPanel, BorderLayout.SOUTH);
		columnFunctionTab.add(columnFunctionUpPanel, BorderLayout.NORTH);
		
		columnFunctionUpPanel.setLayout(new FlowLayout());
		
		columnFunctionModel = new DefaultComboBoxModel();
		cbColumnFunction = new UIComboBox(columnFunctionModel);
		columnFunctionUpPanel.add(cbColumnFunction);
		
		tableFunctionTab = new JPanel();
		tabPanel.addTab(Manager.applyStringTable("TableSet"), tableFunctionTab);
		
		tableFunctionTab.setLayout(new BorderLayout());
		
		tableUpPanel = new JPanel();
		tableFunctionTab.add(tableUpPanel, BorderLayout.NORTH);
		
		tableUpPanel.setLayout(new FlowLayout());
		
		tableFunctionModel = new DefaultComboBoxModel();
		cbTableFunction = new UIComboBox(tableFunctionModel);
		tableUpPanel.add(cbTableFunction);
		
		tableSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
//		tableScroll = new JScrollPane(tableSplit);
		tableFunctionTab.add(tableSplit, BorderLayout.CENTER);
		
		tableRightPanel = new JPanel();
		tableRightPanel.setLayout(new BorderLayout());
		
		tableLeftPanel = new JPanel();
		tableLeftPanel.setLayout(new BorderLayout());
		
		selectedTableModel = new DefaultListModel();
		selectedTableList = new UIList(selectedTableModel);
		selectedTableList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		selectedTableScroll = new JScrollPane(selectedTableList);
		tableRightPanel.add(selectedTableScroll, BorderLayout.CENTER);
		
		tableSplit.setRightComponent(tableRightPanel);
		
		selectedTableListOnTableFunction = new UIList();
		selectedTableListOnTableFunction.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		selectedTableScrollOnTableFunction = new JScrollPane(selectedTableListOnTableFunction);
		tableLeftPanel.add(selectedTableScrollOnTableFunction, BorderLayout.CENTER);
		
		tablePanel = new JPanel();
		tablePanel.setLayout(new FlowLayout());
		
		btPutOnTableTab = new JButton(Manager.applyStringTable("Add"));
		btPutOnTableTab.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				putOnTableTab();
			}
		});
		
		tablePanel.add(btPutOnTableTab);
				
		tableLeftPanel.add(tablePanel, BorderLayout.SOUTH);
		
		tableSplit.setLeftComponent(tableLeftPanel);
		
		tableDownPanel = new JPanel();
		tableDownPanel.setLayout(new FlowLayout());
		tableFunctionTab.add(tableDownPanel, BorderLayout.SOUTH);
		
		btActOnTableTab = new JButton(Manager.applyStringTable("Execute"));
		btActOnTableTab.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				actOnTableTab();
			}
		});
		
		tableDownPanel.add(btActOnTableTab);
	}
	
	/**
	 * <p>분석 함수 목록을 다시 불러옵니다.</p>
	 * 
	 */
	public void refreshFunctionList()
	{
		columnFunctionModel.removeAllElements();
		tableFunctionModel.removeAllElements();
		
		for(AnalyzeFunction f : AnalyzeUtil.getColumnAnalyzeFunctions())
		{
			columnFunctionModel.addElement(f);
		}
		
		for(AnalyzeFunction f : AnalyzeUtil.getTableSetAnalyzeFunctions())
		{
			tableFunctionModel.addElement(f);
		}
	}
	/**
	 * <p>테이블 셋 분석 함수를 실행합니다.</p>
	 * 
	 */
	protected void actOnTableTab()
	{
		Map<String, String> args = argPanel.getMap();
		String selectedFunction = ((AnalyzeFunction) cbTableFunction.getSelectedItem()).getName();
		TableSet[] tableSets = new TableSet[selectedTableModel.getSize()];
		for(int i=0; i<tableSets.length; i++)
		{
			tableSets[i] = (TableSet) selectedTableModel.get(i);
		}
		manager.logTable(AnalyzeUtil.analyze(selectedFunction, args, tableSets));
		close();
	}
	
	/**
	 * <p>컬럼 분석 함수를 실행합니다.</p>
	 * 
	 */
	protected void actOnColumnTab()
	{
		Map<String, String> args = argPanel.getMap();
		String selectedFunction = ((AnalyzeFunction) cbColumnFunction.getSelectedItem()).getName();
		Column[] columns = new Column[selectedColumnModel.getSize()];
		for(int i=0; i<columns.length; i++)
		{
			columns[i] = ((ColumnWithTableInfo) selectedColumnModel.get(i)).getColumn();
		}
		manager.logTable(AnalyzeUtil.analyze(selectedFunction, args, columns));
		close();
	}
	
	/**
	 * <p>분석 대상 테이블 셋 목록에 테이블 셋을 추가합니다.</p>
	 */
	protected void putOnTableTab()
	{
		selectedTableModel.addElement((TableSet) selectedTableListOnTableFunction.getSelectedValue());
	}
	
	/**
	 * <p>분석 대상 컬럼 목록에 컬럼을 추가합니다.</p>
	 * 
	 */
	protected void putOnColumnTab()
	{
		ColumnWithTableInfo newInfo = new ColumnWithTableInfo(((TableSet) selectTableListOnColumnFunction.getSelectedValue())
				, selectColumnListOnColumnFunction.getSelectedIndex());
		selectedColumnModel.addElement(newInfo);
	}
	
	/**
	 * <p>테이블 셋 목록을 다시 불러옵니다.</p>
	 * 
	 * @param scriptEngine : 스크립트 엔진 객체
	 */
	protected void refreshTableList(JScriptRunner scriptEngine)
	{
		Map<String, Object> attributes = scriptEngine.getAttributes();
		Set<String> keys = attributes.keySet();
		
		addedTableSets.clear();
		
		for(String s : keys)
		{
			if(attributes.get(s) instanceof TableSet)
			{
				addedTableSets.add((TableSet) attributes.get(s));
			}
		}
		refreshTableList();
	}
	/**
	 * <p>테이블 셋 목록을 다시 불러옵니다.</p>
	 * 
	 * @param scriptEngine : 스크립트 엔진 객체
	 * @param tableSets : 테이블 셋 리스트
	 */
	protected void refreshTableList(JScriptRunner scriptEngine, List<TableSet> tableSets)
	{
		addedTableSets.clear();
		
		if(scriptEngine != null)
		{
			Map<String, Object> attributes = scriptEngine.getAttributes();
			Set<String> keys = attributes.keySet();
			
			for(String s : keys)
			{
				if(attributes.get(s) instanceof TableSet)
				{
					addedTableSets.add((TableSet) attributes.get(s));
				}
			}
		}
		if(DataUtil.isNotEmpty(tableSets))
		{
			addedTableSets.addAll(tableSets);
		}
		refreshTableList();
	}
	/**
	 * <p>테이블 셋 목록을 다시 불러옵니다.</p>
	 * 
	 */
	protected void refreshTableList()
	{
		Vector<TableSet> tableSets = new Vector<TableSet>();
		tableSets.addAll(addedTableSets);
		selectTableListOnColumnFunction.setListData(addedTableSets);
		selectedTableListOnTableFunction.setListData(tableSets);
	}
	
	/**
	 * <p>컬럼 목록을 다시 불러옵니다.</p>
	 * 
	 * @param tableSet : 테이블 셋 객체
	 */
	protected void refreshColumnList(TableSet tableSet)
	{
		Vector<Column> columns = new Vector<Column>();
		if(tableSet != null) columns.addAll(tableSet.getColumns());
		selectColumnListOnColumnFunction.setListData(columns);
	}
	
	/**
	 * <p>대화 상자를 엽니다.</p>
	 * 
	 * @param scriptEngine : 스크립트 엔진 객체 (애트리뷰트로 삽입된 테이블 셋 객체들을 목록으로 불러오게 됨)
	 */
	public void open(JScriptRunner scriptEngine)
	{
		close();
		refreshFunctionList();
		refreshTableList(scriptEngine);
		dialog.setVisible(true);
		SwingUtilities.invokeLater(new Runnable()
		{	
			@Override
			public void run()
			{
				columnFunctionSplitPane.setDividerLocation(0.5);
				mainSplit.setDividerLocation(0.5);
			}
		});
	}
	
	/**
	 * <p>대화 상자를 엽니다.</p>
	 * 
	 * @param tableSets : 테이블 셋 리스트 (이 리스트에 있는 테이블 셋 객체들만 목록으로 불러오게 됨)
	 */
	public void open(List<TableSet> tableSets)
	{
		close();
		refreshFunctionList();
		addedTableSets.addAll(tableSets);
		refreshTableList();
		dialog.setVisible(true);
		SwingUtilities.invokeLater(new Runnable()
		{	
			@Override
			public void run()
			{
				columnFunctionSplitPane.setDividerLocation(0.5);
				mainSplit.setDividerLocation(0.5);
			}
		});
	}
	
	/**
	 * <p>대화 상자를 엽니다.</p>
	 * 
	 * @param scriptEngine : 스크립트 엔진 객체 (애트리뷰트로 삽입된 테이블 셋 객체들을 목록으로 불러오게 됨)
	 * @param tableSets    : 테이블 셋 리스트 (이 리스트에 있는 테이블 셋 객체들 또한 목록으로 불러오게 됨)
	 */
	public void open(JScriptRunner scriptEngine, List<TableSet> tableSets)
	{
		close();
		refreshFunctionList();
		refreshTableList(scriptEngine, tableSets);
		dialog.setVisible(true);
		SwingUtilities.invokeLater(new Runnable()
		{	
			@Override
			public void run()
			{
				columnFunctionSplitPane.setDividerLocation(0.5);
				mainSplit.setDividerLocation(0.5);
			}
		});
	}
	
	/**
	 * <p>대화 상자를 닫습니다.</p>
	 * 
	 */
	public void close()
	{
		dialog.setVisible(false);
		selectColumnListOnColumnFunction.setListData(new Vector<Column>());
		selectTableListOnColumnFunction.setListData(new Vector<TableSet>());
		selectedTableListOnTableFunction.setListData(new Vector<TableSet>());
		selectedColumnModel.clear();
		selectedTableModel.clear();
		addedTableSets.clear();
	}
	@Override
	public void noMoreUse()
	{
		close();
		this.manager = null;
	}
	@Override
	public boolean isAlive()
	{
		return (manager != null);
	}
}
