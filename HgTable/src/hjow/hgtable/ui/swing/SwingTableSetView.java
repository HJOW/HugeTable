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
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;

import hjow.hgtable.Manager;
import hjow.hgtable.dao.Dao;
import hjow.hgtable.dao.JdbcDao;
import hjow.hgtable.tableset.ResultSetTableSet;
import hjow.hgtable.tableset.TableSet;
import hjow.hgtable.ui.NeedtoEnd;
import hjow.hgtable.util.DataUtil;
import hjow.hgtable.util.GUIUtil;
import hjow.hgtable.util.StreamUtil;
import hjow.hgtable.util.XLSXUtil;

/**
 * <p>테이블 셋 조회용 탭 컴포넌트입니다.</p>
 */
public class SwingTableSetView implements NeedtoEnd, ActionListener, Serializable, TableModelListener
{
	private static final long serialVersionUID = 8977926720912097335L;
	protected long uniqueId = new Random().nextLong();
	protected transient SwingManager manager;
	protected transient TableSet tableSet;
	protected transient Dao dao;
	protected JPanel mainPanel;
	protected JPanel upPanel;
	protected JPanel centerPanel;
	protected JPanel downPanel;
	protected DefaultTableModel model;
	protected JTable table;
	protected JScrollPane scroll;
	protected JFileChooser fileChooser;
	protected FileFilter xlsxFileFilter;
	protected FileFilter jsonFileFilter;
	protected FileFilter hgfFileFilter;
	protected JButton btSave;
	protected JButton btClose;
	protected JLabel lbCount;
	protected JTextField tfCount;
	protected JButton btToDb;
	protected JTabbedPane tabPanel;
	protected LineNumberTextArea hgfArea;
	protected JScrollPane hgfScroll;
	protected UISyntaxView jsonArea;
	protected JScrollPane jsonScroll;
	protected JButton btPut;
	protected JButton btFunction;
	
	/**
	 * <p>이 생성자를 실제로 사용할 경우, 매니저와 테이블 셋 객체를 삽입한 후 initComponent() 메소드를 호출해 컴포넌트들을 초기화해야 합니다.</p>
	 * 
	 */
	public SwingTableSetView()
	{
		
	}
	/**
	 * <p>매니저 객체를 삽입하고 컴포넌트를 초기화합니다.</p>
	 * 
	 * @param manager : 매니저 객체
	 * @param tableSet : 테이블 셋 객체
	 */
	public SwingTableSetView(SwingManager manager, TableSet tableSet)
	{
		this.manager = manager;
		this.tableSet = tableSet;
		initComponent();
	}
	
	/**
	 * <p>매니저 객체를 삽입하고 컴포넌트를 초기화합니다.</p>
	 * 
	 * @param manager : 매니저 객체
	 * @param tableSet : 테이블 셋 객체
	 * @param dao : 테이블 셋을 조회할 때 사용한 DAO 객체
	 */
	public SwingTableSetView(SwingManager manager, TableSet tableSet, Dao dao)
	{
		this.manager = manager;
		this.tableSet = tableSet;
		this.dao = dao;
		initComponent();
	}
	
	/**
	 * <p>컴포넌트들을 초기화합니다.</p>
	 * 
	 */
	public void initComponent()
	{
		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		
		upPanel = new JPanel();
		centerPanel = new JPanel();
		downPanel = new JPanel();
		
		mainPanel.add(upPanel, BorderLayout.NORTH);
		mainPanel.add(centerPanel, BorderLayout.CENTER);
		mainPanel.add(downPanel, BorderLayout.SOUTH);
		
		centerPanel.setLayout(new BorderLayout());
		
		model = GUIUtil.toTableModel(tableSet);
		table = new JTable(model);
		model.addTableModelListener(this);
		if(dao != null && (dao instanceof JdbcDao)) 
		{
			
		}
		scroll = new JScrollPane(table);
		
		if(DataUtil.parseBoolean(Manager.getOption("use_testing_functions")))
		{
			tabPanel = new JTabbedPane();
			centerPanel.add(tabPanel, BorderLayout.CENTER);
			
			tabPanel.addTab(Manager.applyStringTable("TableSet"), scroll);
			
			hgfArea = new LineNumberTextArea();
			hgfScroll = new JScrollPane(hgfArea);
			
			tabPanel.addTab(Manager.applyStringTable("HGF"), hgfScroll);
			
			jsonArea = new UISyntaxView();
			jsonScroll = new JScrollPane(jsonArea);
			
			tabPanel.addTab(Manager.applyStringTable("JSON"), jsonScroll);
		}
		else centerPanel.add(scroll, BorderLayout.CENTER);
		
		upPanel.setLayout(new FlowLayout());
		
		lbCount = new JLabel(Manager.applyStringTable("Count"));
		tfCount = new JTextField(10);
		tfCount.setEditable(false);
		btPut = new JButton(Manager.applyStringTable("Put"));
		btClose = new JButton(Manager.applyStringTable("Close"));
		
		upPanel.add(lbCount);
		upPanel.add(tfCount);
		upPanel.add(btPut);
		upPanel.add(btClose);
		
		downPanel.setLayout(new FlowLayout());
		
		btSave = new JButton(Manager.applyStringTable("Save"));
		btToDb = new JButton(Manager.applyStringTable("Insert this to DB"));
		btFunction = new JButton(Manager.applyStringTable("Analize Function"));
		
		downPanel.add(btSave);
		downPanel.add(btToDb);
		downPanel.add(btFunction);
		
		fileChooser = new JFileChooser();
		
		xlsxFileFilter = new FileFilter()
		{			
			@Override
			public boolean accept(File pathname)
			{
				if(pathname.getAbsolutePath().endsWith(".xlsx") || pathname.getAbsolutePath().endsWith(".XLSX"))
				{
					return true;
				}
				else if(pathname.isDirectory()) return true;
				return false;
			}

			@Override
			public String getDescription()
			{
				return Manager.applyStringTable("OOXML spreadsheet (*.xlsx)");
			}
		};
		jsonFileFilter = new FileFilter()
		{			
			@Override
			public String getDescription()
			{
				return Manager.applyStringTable("JavaScript Standard Object Notation (*.json)");
			}
			
			@Override
			public boolean accept(File f)
			{
				if(f.getAbsolutePath().endsWith(".json") || f.getAbsolutePath().endsWith(".JSON"))
				{
					return true;
				}
				else if(f.isDirectory()) return true;
				return false;
			}
		};
		hgfFileFilter = new FileFilter()
		{
			@Override
			public String getDescription()
			{
				return Manager.applyStringTable("Huge Table formed text (*.hgf)");
			}
			
			@Override
			public boolean accept(File f)
			{
				if(f.getAbsolutePath().endsWith(".hgf") || f.getAbsolutePath().endsWith(".HGF"))
				{
					return true;
				}
				else if(f.isDirectory()) return true;
				return false;
			}
		};
		
		fileChooser.setFileFilter(xlsxFileFilter);
		fileChooser.addChoosableFileFilter(jsonFileFilter);
		fileChooser.addChoosableFileFilter(hgfFileFilter);
		
		btPut.addActionListener(this);
		btSave.addActionListener(this);
		btClose.addActionListener(this);
		btToDb.addActionListener(this);
		btFunction.addActionListener(this);
	}
	
	/**
	 * <p>바뀐 내용을 화면에 적용합니다.</p>
	 * 
	 */
	public void refresh()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				if(tableSet != null)
				{
					table.setModel(GUIUtil.toTableModel(tableSet));
					tfCount.setText(String.valueOf(tableSet.getRecordCount()));
					
					if(hgfArea != null)
					{
						String hgfs = tableSet.toHGF();
						hgfs = hgfs.replaceAll("  ", " ");
						hgfs = hgfs.replaceAll("\t", " ");
						hgfs = hgfs.replaceAll(";", "\t;");
						hgfArea.setText(hgfs);
					}
					if(jsonArea != null) jsonArea.setText(tableSet.toJSON(false));
					
					btSave.setEnabled(true);
					btToDb.setEnabled(DataUtil.isNotEmpty(manager.getDao()) && manager.getDao().isAlive());
				}
				else
				{
					table.setModel(new DefaultTableModel());
					tfCount.setText(String.valueOf(0));
					
					if(hgfArea != null) hgfArea.setText("");
					if(jsonArea != null) jsonArea.setText("");
					
					btSave.setEnabled(false);
					btToDb.setEnabled(false);
				}
			}
		});
	}
	
	/**
	 * <p>매니저 객체를 삽입합니다. 이미 다른 매니저 객체가 삽입된 경우 이 메소드는 아무 일도 하지 않습니다.</p>
	 * 
	 * @param manager : 매니저 객체
	 */
	public void setManager(SwingManager manager)
	{
		if(this.manager == null) this.manager = manager;
	}
	/**
	 * <p>테이블 셋 객체를 삽입합니다.</p>
	 * 
	 * @param tableSet : 테이블 셋 객체
	 */
	public void setTableSet(TableSet tableSet)
	{
		this.tableSet = tableSet;
		refresh();
	}
	/**
	 * <p>입력된 테이블 셋 객체를 반환합니다.</p>
	 * 
	 * @return 테이블 셋 객체
	 */
	public TableSet getTableSet()
	{
		return tableSet;
	}
	/**
	 * <p>고유한 ID값을 반환합니다.</p>
	 * 
	 * @return ID 값
	 */
	public long getUniqueId()
	{
		return uniqueId;
	}
	@Override
	public void noMoreUse()
	{
		manager = null;
		dao = null;
		model.removeTableModelListener(this);
		model = null;
		if(hgfArea != null) hgfArea.noMoreUse();
		if(jsonArea != null) jsonArea.noMoreUse();
	}
	@Override
	public boolean isAlive()
	{
		return (manager != null);
	}
	
	/**
	 * <p>이 메소드는 사용자가 저장 버튼을 눌렀을 때 호출됩니다.</p>
	 * 
	 */
	protected void save()
	{
		int selection = fileChooser.showSaveDialog(manager.getFrame());
		if(selection == JFileChooser.APPROVE_OPTION)
		{
			File target = fileChooser.getSelectedFile();
			String selectedFile = target.getAbsolutePath();
			
			if(fileChooser.getFileFilter() == xlsxFileFilter)
			{
				if(! (selectedFile.endsWith(".xlsx") || selectedFile.endsWith(".XLSX") || selectedFile.endsWith(".Xlsx")))
				{
					selectedFile = selectedFile + ".xlsx";
				}
				XLSXUtil.save(tableSet, new File(selectedFile));
			}
			else if(fileChooser.getFileFilter() == jsonFileFilter)
			{
				if(! (selectedFile.endsWith(".json") || selectedFile.endsWith(".JSON") || selectedFile.endsWith(".Json")))
				{
					selectedFile = selectedFile + ".json";
				}
				StreamUtil.saveFile(new File(selectedFile), tableSet.toJSON(false));
			}
			else if(fileChooser.getFileFilter() == hgfFileFilter)
			{
				if(! (selectedFile.endsWith(".hgf") || selectedFile.endsWith(".HGF") || selectedFile.endsWith(".Hgf")))
				{
					selectedFile = selectedFile + ".hgf";
				}
				StreamUtil.saveFile(new File(selectedFile), tableSet.toHGF());
			}
			else
			{
				if(! (selectedFile.endsWith(".hgf") || selectedFile.endsWith(".HGF") || selectedFile.endsWith(".Hgf")))
				{
					selectedFile = selectedFile + ".hgf";				
				}
				StreamUtil.saveFile(new File(selectedFile), tableSet.toHGF());
			}
		}
	}
	/**
	 * <p>테이블 셋을 DB에 삽입합니다.</p>
	 */
	protected void toDb()
	{
		if(tableSet == null) return;
		String tableName = manager.askInput(Manager.applyStringTable("Input the table name of DB."), true);
		if(tableName != null)
		{
			tableSet.setName(tableName);
			tableSet.insertIntoDB(manager.getDao());
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		Object ob = e.getSource();
		if(ob == btSave)
		{
			save();
		}
		else if(ob == btClose)
		{
			close();
		}
		else if(ob == btToDb)
		{
			toDb();
		}
		else if(ob == btPut)
		{
			putTableSet();
		}
		else if(ob == btFunction)
		{
			actFunction();
		}
	}
	
	/**
	 * <p>분석 함수 대화 상자를 엽니다.</p>
	 * 
	 */
	protected void actFunction()
	{
		List<TableSet> tables = new Vector<TableSet>();
		tableSet.setAlias("this");
		tables.add(tableSet);
		manager.openAnalyzeDialog(tables);
	}
	/**
	 * <p>탭을 닫습니다.</p>
	 */
	protected void close()
	{
		manager.closeTableSetView(uniqueId);
		noMoreUse();
	}
	
	/**
	 * <p>스크립트 엔진에 테이블 셋 객체를 삽입합니다.</p>
	 */
	protected void putTableSet()
	{
		try
		{
			String idName = manager.askInput(Manager.applyStringTable("Input the identifier name which identify this table set."), true);
			if(DataUtil.isNotEmpty(idName))
			{
				tableSet.setAlias(idName);
				manager.putOnScriptEngine(idName, tableSet);
				manager.alert(Manager.applyStringTable("This table set is in script engine named") + " " + idName);
			}
		}
		catch (Throwable e)
		{
			manager.logError(e, Manager.applyStringTable("On put the table set into the script engine"));
		}
	}
	
	
	/**
	 * <p>최상위 컴포넌트 객체를 반환합니다.</p>
	 * 
	 * @return 컴포넌트
	 */
	public Component getComponent()
	{
		return mainPanel;
	}
	public Dao getDao() {
		return dao;
	}
	public void setDao(Dao dao) {
		this.dao = dao;
	}
	@Override
	public void tableChanged(TableModelEvent e)
	{
		int col = e.getColumn();
		int rowStart = e.getFirstRow();
		int rowEnd = e.getLastRow();
		int action = e.getType();
		
		if(this.tableSet instanceof ResultSetTableSet)
		{
			ResultSetTableSet tableSet = (ResultSetTableSet) this.tableSet;
			if(action == TableModelEvent.UPDATE)
			{
				for(int i=rowStart; i<=rowEnd; i++)
				{
					tableSet.setData(col, i, String.valueOf(model.getValueAt(i, col)));
				}
			}
			else if(action == TableModelEvent.DELETE)
			{
				for(int i=rowStart; i<=rowEnd; i++)
				{
					tableSet.remove(rowStart);
				}
			}
			else if(action == TableModelEvent.INSERT)
			{
				
			}
		}
	}
}
