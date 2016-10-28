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

import hjow.hgtable.Main;
import hjow.hgtable.Manager;
import hjow.hgtable.ui.GUIManager;
import hjow.hgtable.ui.module.GUIDialogModule;
import hjow.hgtable.ui.swing.LineNumberTextArea;
import hjow.hgtable.ui.swing.UIComboBox;
import hjow.hgtable.util.DataUtil;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

/**
 * <p>테이블 생성 마법사 대화 상자입니다.</p>
 * 
 * @author HJOW
 *
 */
public class SwingCreateTable extends GUIDialogModule
{
	private static final long serialVersionUID = -7374203926681816716L;
	public static final transient long sid = serialVersionUID;
	protected JDialog dialog;
	protected JPanel mainPanel;
	protected DefaultTableModel tableModel;
	protected JTable columnList;
	protected JScrollPane columnScroll;
	protected JPanel upPanel;
	protected JPanel centerPanel;
	protected JPanel downPanel;
	protected JTextField tableNameField;
	protected JLabel tableNameLabel;
	protected JButton btClose;
	protected JPanel createColumnPanel;
	protected JTextField columnNameField;
	protected UIComboBox columnTypeCombo;
	protected JPanel[] createColumnPns;
	protected JLabel columnNameLabel;
	protected JSpinner mainSizeField;
	protected JSpinner floatSizeField;
	protected JPanel controlPanel;
	protected JButton btCreate;
	protected JButton btNewColumn;
	protected JButton btClean;
	protected JLabel sizeLabel;
	protected JCheckBox primaryKeyCheck;
	protected JCheckBox notNullCheck;
	protected CardLayout mainLayout;
	protected JPanel infoPanel;
	protected JPanel reviewPanel;
	protected JPanel reviewCenterPanel;
	protected JPanel reviewDownPanel;
	protected LineNumberTextArea reviewArea;
	protected JButton btNext;
	protected JButton btBefore;
	
	/**
	 * <p>생성자입니다. 컴포넌트들을 초기화합니다.</p>
	 * 
	 * @param frame : Frame 객체
	 * @param manager : 매니저 객체
	 */
	public SwingCreateTable(Frame frame, GUIManager manager)
	{
		super(manager);
		
		dialog = new JDialog(frame);
		dialog.setSize(400, 400);
		
		dialog.setTitle(Manager.applyStringTable("Create Table"));
		
		Dimension scSize = Toolkit.getDefaultToolkit().getScreenSize();
		dialog.setLocation((int)(scSize.getWidth()/2 - dialog.getWidth()/2), (int)(scSize.getHeight()/2 - dialog.getHeight()/2));
		
		dialog.setLayout(new BorderLayout());
		
		mainPanel = new JPanel();
		dialog.add(mainPanel, BorderLayout.CENTER);
		
		mainLayout = new CardLayout();
		mainPanel.setLayout(mainLayout);
		
		infoPanel = new JPanel();
		infoPanel.setLayout(new BorderLayout());
		
		mainPanel.add("INFO", infoPanel);
		
		reviewPanel = new JPanel();
		reviewPanel.setLayout(new BorderLayout());
		
		reviewCenterPanel = new JPanel();
		reviewDownPanel = new JPanel();
		
		reviewPanel.add(reviewCenterPanel, BorderLayout.CENTER);
		reviewPanel.add(reviewDownPanel, BorderLayout.SOUTH);
		
		reviewCenterPanel.setLayout(new BorderLayout());
		reviewArea = new LineNumberTextArea(dialog);
		
		reviewCenterPanel.add(reviewArea, BorderLayout.CENTER);
		
		reviewDownPanel.setLayout(new FlowLayout());
		
		btCreate = new JButton(Manager.applyStringTable("Create Table"));
		btBefore = new JButton(Manager.applyStringTable("Before"));
		
		reviewDownPanel.add(btBefore);
		reviewDownPanel.add(btCreate);
		
		mainPanel.add("REVIEW", reviewPanel);
		
		upPanel = new JPanel();
		centerPanel = new JPanel();
		downPanel = new JPanel();
		
		infoPanel.add(upPanel, BorderLayout.NORTH);
		infoPanel.add(centerPanel, BorderLayout.CENTER);
		infoPanel.add(downPanel, BorderLayout.SOUTH);
		
		tableModel = new DefaultTableModel();
		columnList = new JTable(tableModel);
		columnScroll = new JScrollPane(columnList);
		
		tableModel.addColumn("COLUMN");
		tableModel.addColumn("TYPE");
		tableModel.addColumn("CONSTRAINT");
		
		centerPanel.setLayout(new BorderLayout());
		centerPanel.add(columnScroll);
		
		upPanel.setLayout(new FlowLayout());
		downPanel.setLayout(new BorderLayout());
		
		tableNameLabel = new JLabel(Manager.applyStringTable("Table Name"));
		tableNameField = new JTextField(15);
		upPanel.add(tableNameLabel);
		upPanel.add(tableNameField);
		
		createColumnPanel = new JPanel();
		downPanel.add(createColumnPanel, BorderLayout.CENTER);
		
		controlPanel = new JPanel();
		downPanel.add(controlPanel, BorderLayout.SOUTH);
		
		createColumnPns = new JPanel[4];
		createColumnPanel.setLayout(new GridLayout(createColumnPns.length, 1));
		for(int i=0; i<createColumnPns.length; i++)
		{
			createColumnPns[i] = new JPanel();
			createColumnPns[i].setLayout(new FlowLayout());
			createColumnPanel.add(createColumnPns[i]);
		}
		
		columnNameLabel = new JLabel(Manager.applyStringTable("New Column"));
		columnNameField = new JTextField(10);
		createColumnPns[0].add(columnNameLabel);
		createColumnPns[0].add(columnNameField);
		
		Vector<String> columnType = new Vector<String>();
		columnType.add("VARCHAR");
		columnType.add("INTEGER");
		columnType.add("FLOAT");
		columnType.add("NUMERIC");
		columnType.add("DATE");
		
		columnTypeCombo = new UIComboBox(columnType);
		columnTypeCombo.setEditable(true);
		
		createColumnPns[0].add(columnTypeCombo);
		
		sizeLabel = new JLabel(Manager.applyStringTable("Size"));
		mainSizeField = new JSpinner(new SpinnerNumberModel(1, 1, 3000, 1));
		floatSizeField = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));
		floatSizeField.setEnabled(false);
		
		createColumnPns[1].add(sizeLabel);
		createColumnPns[1].add(mainSizeField);
		createColumnPns[1].add(floatSizeField);
		
		primaryKeyCheck = new JCheckBox(Manager.applyStringTable("PRIMARY KEY"));
		notNullCheck = new JCheckBox(Manager.applyStringTable("NOT NULL"));
		btNewColumn = new JButton(Manager.applyStringTable("New"));
		
		createColumnPns[2].add(primaryKeyCheck);
		createColumnPns[2].add(notNullCheck);
		createColumnPns[2].add(btNewColumn);
		
		btClean = new JButton(Manager.applyStringTable("Clear Empty Row"));
		
		createColumnPns[createColumnPns.length - 1].add(btClean);
		
		btNext = new JButton(Manager.applyStringTable("Next"));
		btClose = new JButton(Manager.applyStringTable("Close"));
		
		controlPanel.setLayout(new FlowLayout());
		controlPanel.add(btClose);
		controlPanel.add(btNext);
		
		dialog.addWindowListener(this);
		btClose.addActionListener(this);
		btCreate.addActionListener(this);
		btNewColumn.addActionListener(this);
		btClean.addActionListener(this);
		btNext.addActionListener(this);
		btBefore.addActionListener(this);
		columnTypeCombo.addItemListener(this);
		primaryKeyCheck.addItemListener(this);
	}
	
	/**
	 * <p>다음 버튼을 눌렀을 때 호출됩니다.</p>
	 * 
	 */
	protected void btNext()
	{
		SwingUtilities.invokeLater(new Runnable()
		{	
			@Override
			public void run()
			{
				StringBuffer statements = new StringBuffer("");
				statements = statements.append("CREATE TABLE " + tableNameField.getText() + "\n");
				statements = statements.append("(\n");
				for(int i=0; i<tableModel.getRowCount(); i++)
				{
					statements = statements.append(tableModel.getValueAt(i, 0) + " " + tableModel.getValueAt(i, 1)
							+ " " + tableModel.getValueAt(i, 2) + "\n");
					if(i < tableModel.getRowCount() - 1) statements = statements.append(", ");
				}
				statements = statements.append(")");
				reviewArea.setText(statements.toString());
				mainLayout.show(mainPanel, "REVIEW");
			}
		});
	}

	/**
	 * <p>이전 버튼을 눌렀을 때 호출됩니다.</p>
	 * 
	 */
	protected void btBefore()
	{
		SwingUtilities.invokeLater(new Runnable()
		{	
			@Override
			public void run()
			{
				mainLayout.show(mainPanel, "INFO");
			}
		});
	}
	
	/**
	 * <p>컬럼 리스트 테이블에서 비어 있는 레코드를 제거합니다.</p>
	 * 
	 */
	protected void cleanColumn()
	{
		SwingUtilities.invokeLater(new Runnable()
		{	
			@Override
			public void run()
			{
				int i = 0;
				while(i<tableModel.getRowCount())
				{
					boolean isAllEmpty = true;
					for(int j=0; j<3; j++)
					{
						if(DataUtil.isNotEmpty(tableModel.getValueAt(i, j)))
						{
							isAllEmpty = false;
							break;
						}
					}
					if(isAllEmpty)
					{
						tableModel.removeRow(i);
						i = 0;
					}
					else
					{
						i++;
					}
					
					if(! (Main.checkInterrupt(this, "clearColumn")))
					{
						break;
					}
				}
			}
		});
	}
	
	/**
	 * <p>사용자가 입력한 새 컬럼 데이터들을 Vector 로 반환합니다.</p>
	 * 
	 * @return 컬럼 데이터 Vector
	 */
	protected Vector<String> newColumnData()
	{
		Vector<String> rowData = new Vector<String>();
		rowData.add(columnNameField.getText());
		
		String types = String.valueOf(columnTypeCombo.getSelectedItem());
		
		if(! (types.equalsIgnoreCase("INTEGER") || types.equalsIgnoreCase("FLOAT")))
		{
			types = types + "(" + String.valueOf(mainSizeField.getValue());
			if(types.equalsIgnoreCase("NUMERIC") || types.equalsIgnoreCase("FLOAT"))
			{
				types = types + ", " + String.valueOf(floatSizeField.getValue());
			}
			types = types + ")";
		}
		
		rowData.add(types);
		
		String constraints = "";
		
		if(primaryKeyCheck.isSelected())
		{
			constraints = constraints + " PRIMARY KEY";
			constraints = constraints.trim();
		}
		else if(notNullCheck.isSelected())
		{
			constraints = constraints + " NOT NULL";
			constraints = constraints.trim();
		}
		
		rowData.add(constraints);
		
		return rowData;
	}

	/**
	 * <p>새로 만들 테이블의 새 컬럼을 추가합니다.</p>
	 * 
	 */
	protected void newColumn()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				tableModel.addRow(newColumnData());
			}
		});
		
	}
	
	/**
	 * <p>테이블 생성 작업을 시작합니다.</p>
	 * 
	 */
	protected void createTable()
	{
		try
		{
			manager.getDao().query(reviewArea.getText());
			manager.log(Manager.applyStringTable("Successfully created") + " : " + tableNameField.getText());
			JOptionPane.showMessageDialog(dialog, Manager.applyStringTable("Successfully created") + " : " + tableNameField.getText());
		}
		catch(SQLException e)
		{
			manager.logError(e, "On create table", true);
			JOptionPane.showMessageDialog(dialog, Manager.applyStringTable("Failed") + " : " + e.getMessage());
		}
		catch(Throwable e)
		{
			manager.logError(e, "On create table");
			JOptionPane.showMessageDialog(dialog, Manager.applyStringTable("Failed") + " : " + e.getMessage());
		}
		btBefore();
		close();
	}
	@Override
	public void open()
	{
		mainLayout.show(mainPanel, "INFO");
		super.open();
	}
	@Override
	public void itemStateChanged(ItemEvent e)
	{
		Object ob = e.getSource();
		if(ob == columnTypeCombo)
		{
			String gets = String.valueOf(columnTypeCombo.getSelectedItem());
			
			if(gets.equalsIgnoreCase("NUMERIC"))
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					@Override
					public void run()
					{
						floatSizeField.setEnabled(true);
					}
				});
			}
			else
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					@Override
					public void run()
					{
						floatSizeField.setEnabled(false);
					}
				});
			}
			
			if(gets.equalsIgnoreCase("INTEGER") || gets.equalsIgnoreCase("FLOAT"))
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					@Override
					public void run()
					{
						mainSizeField.setEnabled(false);
					}
				});
			}
			else
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					@Override
					public void run()
					{
						mainSizeField.setEnabled(true);
					}
				});
			}
		}
		else if(ob == primaryKeyCheck)
		{
			if(primaryKeyCheck.isSelected())
			{
				notNullCheck.setEnabled(false);
				notNullCheck.setSelected(false);
			}
			else
			{
				notNullCheck.setEnabled(true);
			}
		}
	}
	@Override
	public void actionPerformed(ActionEvent e)
	{
		Object ob = e.getSource();
		if(ob == btClose)
		{
			close();
		}
		else if(ob == btCreate)
		{
			createTable();
		}
		else if(ob == btNewColumn)
		{
			newColumn();
		}
		else if(ob == btClean)
		{
			cleanColumn();
		}
		else if(ob == btBefore)
		{
			btBefore();
		}
		else if(ob == btNext)
		{
			btNext();
		}
	}

	@Override
	public Dialog getDialog()
	{
		return dialog;
	}
}