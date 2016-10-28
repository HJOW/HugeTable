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
import java.awt.CardLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;

import hjow.hgtable.HThread;
import hjow.hgtable.Main;
import hjow.hgtable.Manager;
import hjow.hgtable.ui.FileToDbManager;
import hjow.hgtable.util.DataUtil;

/**
 * <p>파일로부터 데이터를 DB에 삽입하는 작업을 위한 마법사 클래스입니다.</p>
 * 
 * @author HJOW
 *
 */
public class SwingFileToDbManager extends FileToDbManager
{
	protected JDialog dialog;
	protected JFileChooser fileChooser;
	protected FileFilter xlsxFileFilter;
	protected FileFilter jsonFileFilter;
	protected FileFilter hgfFileFilter;
	protected JPanel mainPanel;
	protected CardLayout mainLayout;
	protected JPanel inputPanel;
	protected JPanel[] inputPns;
	protected JTextField fileField;
	protected JButton btFile;
	protected JLabel tableLabel;
	protected JTextField tableField;
	protected JButton btOk;
	protected JButton btCancel;
	protected JPanel progressPanel;
	protected JProgressBar progressBar;
	protected JPanel inputControlPanel;
	protected JPanel inputFunctionPanel;
	protected JTable functionTable;
	protected JScrollPane functionScroll;
	protected DefaultTableModel functionTableModel;
	protected JPanel inputFunctionControlPanel;
	protected JButton btNewFunctionRow;
	protected JButton btClearFunctionRow;
	protected JButton btClearFunctionAll;
	
	/**
	 * <p>기본 생성자입니다. 컴포넌트들을 초기화한 후, 작업용 쓰레드를 실행합니다.</p>
	 * 
	 * @param manager : 매니저 객체
	 * @param frame : Frame 객체 (AWT 호환)
	 */
	public SwingFileToDbManager(Manager manager, Frame frame)
	{
		super(manager);
		dialog = new JDialog(frame, true);
		dialog.setTitle(Manager.applyStringTable("file --> DB"));
		
		Dimension scSize = Toolkit.getDefaultToolkit().getScreenSize();
		dialog.setSize((int)(350), (int)(400));
		dialog.setLocation((int)(scSize.getWidth()/2 - dialog.getWidth()/2), (int)(scSize.getHeight()/2 - dialog.getHeight()/2));
		
		dialog.addWindowListener(this);
		
		dialog.setLayout(new BorderLayout());
		mainPanel = new JPanel();
		dialog.add(mainPanel, BorderLayout.CENTER);
		
		mainLayout = new CardLayout();
		mainPanel.setLayout(mainLayout);
		
		inputPanel = new JPanel();
		inputPanel.setLayout(new BorderLayout());
		
		inputControlPanel = new JPanel();
		inputFunctionPanel = new JPanel();
		
		inputPanel.add(inputControlPanel, BorderLayout.NORTH);
		inputPanel.add(inputFunctionPanel, BorderLayout.CENTER);
		
		mainPanel.add(inputPanel, "INPUT");
		
		inputPns = new JPanel[3];
		inputControlPanel.setLayout(new GridLayout(inputPns.length, 1));
		
		for(int i=0; i<inputPns.length; i++)
		{
			inputPns[i] = new JPanel();
			inputControlPanel.add(inputPns[i]);
			inputPns[i].setLayout(new FlowLayout());
		}
		
		fileField = new JTextField(20);
		btFile = new JButton(Manager.applyStringTable("..."));
		inputPns[0].add(fileField);
		inputPns[0].add(btFile);
		
		tableLabel = new JLabel(Manager.applyStringTable("Table name"));
		tableField = new JTextField(20);
		
		inputPns[1].add(tableLabel);
		inputPns[1].add(tableField);
		
		btOk = new JButton(Manager.applyStringTable("OK"));
		btCancel = new JButton(Manager.applyStringTable("Cancel"));
		
		inputPns[2].add(btOk);
		inputPns[2].add(btCancel);
		
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
				return false;
			}
		};
		
		fileChooser.setFileFilter(xlsxFileFilter);
		fileChooser.addChoosableFileFilter(jsonFileFilter);
		fileChooser.addChoosableFileFilter(hgfFileFilter);
		
		progressPanel = new JPanel();
		mainPanel.add(progressPanel, "PROGRESS");
		
		progressPanel.setLayout(new FlowLayout());
		progressBar = new JProgressBar(JProgressBar.HORIZONTAL, 0, 100);
		
		inputFunctionPanel.setLayout(new BorderLayout());
		
		inputFunctionControlPanel = new JPanel();
		inputFunctionControlPanel.setLayout(new FlowLayout());
		
		btNewFunctionRow = new JButton(Manager.applyStringTable("New"));
		btClearFunctionRow = new JButton(Manager.applyStringTable("Clear Empty Row"));
		btClearFunctionAll = new JButton(Manager.applyStringTable("Clear All"));
		
		inputFunctionControlPanel.add(btNewFunctionRow);
		inputFunctionControlPanel.add(btClearFunctionRow);
		inputFunctionControlPanel.add(btClearFunctionAll);
		
		functionTableModel = new DefaultTableModel();
		functionTableModel.addColumn("COLUMN");
		functionTableModel.addColumn("FUNCTION");
		functionTableModel.addColumn("INDEX");
		functionTableModel.addColumn("PARAMETERS");
		
		functionTable = new JTable(functionTableModel);
		functionScroll = new JScrollPane(functionTable);
		
		inputFunctionPanel.add(functionScroll, BorderLayout.CENTER);
		inputFunctionPanel.add(inputFunctionControlPanel, BorderLayout.SOUTH);
		
		btFile.addActionListener(this);
		btOk.addActionListener(this);
		btCancel.addActionListener(this);
		btNewFunctionRow.addActionListener(this);
		btClearFunctionRow.addActionListener(this);
		btClearFunctionAll.addActionListener(this);
		
		new HThread(this).start();
	}
	@Override
	public void onFail(String reasons)
	{
		btCancel();
		manager.log(Manager.applyStringTable("Work failed") + " : " + reasons);
	}
	@Override
	public void onSuccess()
	{
		btCancel();
		manager.log(Manager.applyStringTable("Work success."));
	}
	@Override
	public void setPercent(int v) 
	{
		progressBar.setValue(v);
	}
	@Override
	public void actionPerformed(ActionEvent e)
	{
		Object ob = e.getSource();
		if(ob == btFile)
		{
			btFile();
		}
		else if(ob == btOk)
		{
			btOk();
		}
		else if(ob == btCancel)
		{
			btCancel();
		}
		else if(ob == btNewFunctionRow)
		{
			btNewFunctionRow();
		}
		else if(ob == btClearFunctionRow)
		{
			btClearFunctionRow();
		}
		else if(ob == btClearFunctionAll)
		{
			btClearFunctionAll();
		}
	}
	
	
	/**
	 * <p>변환함수 테이블의 모든 내용을 제거합니다.</p>
	 * 
	 */
	protected void btClearFunctionAll()
	{
		functionTableModel = new DefaultTableModel();
		functionTableModel.addColumn("COLUMN");
		functionTableModel.addColumn("FUNCTION");
		functionTableModel.addColumn("INDEX");
		functionTableModel.addColumn("PARAMETERS");
		SwingUtilities.invokeLater(new Runnable()
		{	
			@Override
			public void run()
			{
				functionTable.setModel(functionTableModel);
			}
		});
	}
	
	/**
	 * <p>변환함수 테이블에서 비어 있는 행을 제거합니다.</p>
	 * 
	 */
	protected void btClearFunctionRow()
	{
		SwingUtilities.invokeLater(new Runnable()
		{	
			@Override
			public void run()
			{
				int i = 0;
				while(i<functionTableModel.getRowCount())
				{
					boolean isAllEmpty = true;
					for(int j=0; j<4; j++)
					{
						if(DataUtil.isNotEmpty(functionTableModel.getValueAt(i, j)))
						{
							isAllEmpty = false;
							break;
						}
					}
					if(isAllEmpty)
					{
						functionTableModel.removeRow(i);
						i = 0;
					}
					else
					{
						i++;
					}
					
					if(! (Main.checkInterrupt(this, "btClearFunctionRow")))
					{
						break;
					}
				}
			}
		});
	}
	
	/**
	 * <p>변환함수 테이블에서 변환 함수 정보를 받아옵니다.</p>
	 */
	@Override
	protected void setFunctionDataFromTable()
	{
		btClearFunctionRow();
		
		if(functionData == null) functionData = new Hashtable<String, String>();
		if(functionIndexData == null) functionIndexData = new Hashtable<String, Integer>();
		if(functionOtherParams == null) functionOtherParams = new Hashtable<String, List<String>>();
		
		functionData.clear();
		functionIndexData.clear();
		functionOtherParams.clear();
		
		List<String> paramData;
		for(int i=0; i<functionTableModel.getRowCount(); i++)
		{
			functionData.put(String.valueOf(functionTableModel.getValueAt(i, 0)), String.valueOf(functionTableModel.getValueAt(i, 1)));
			functionIndexData.put(String.valueOf(functionTableModel.getValueAt(i, 0)), new Integer(String.valueOf(functionTableModel.getValueAt(i, 2))));
			
			paramData = new Vector<String>();
			StringTokenizer tokenizer = new StringTokenizer(String.valueOf(functionTableModel.getValueAt(i, 3)), ",");
			while(tokenizer.hasMoreTokens())
			{
				paramData.add(tokenizer.nextToken().trim());
			}
			
			functionOtherParams.put(String.valueOf(functionTableModel.getValueAt(i, 0)), paramData);
		}
	}
	/**
	 * <p>변환함수 테이블에 새 함수 항목을 추가합니다.</p>
	 * 
	 */
	protected void btNewFunctionRow()
	{
		SwingUtilities.invokeLater(new Runnable()
		{	
			@Override
			public void run()
			{
				Vector<String> newRow = new Vector<String>();
				newRow.add("");
				newRow.add("");
				newRow.add("");
				functionTableModel.addRow(newRow);
				
			}
		});
	}
	/**
	 * <p>이 메소드는 사용자가 취소 버튼을 눌렀을 때 호출됩니다.</p>
	 * 
	 */
	protected void btCancel()
	{
		mainLayout.show(mainPanel, "INPUT");
		dialog.setVisible(false);
	}
	
	/**
	 * <p>이 메소드는 사용자가 확인 버튼을 눌렀을 때 호출됩니다.</p>
	 * 
	 */
	protected void btOk()
	{
		mainLayout.show(mainPanel, "PROGRESS");
		needWork = true;
	}
	
	/**
	 * <p>이 메소드는 사용자가 ... 버튼 (파일 선택 버튼)을 눌렀을 때 호출됩니다.</p>
	 * 
	 */
	protected void btFile()
	{
		int selected = fileChooser.showOpenDialog(dialog);
		if(selected == JFileChooser.APPROVE_OPTION)
		{
			fileField.setText(fileChooser.getSelectedFile().getAbsolutePath());
		}
	}
	@Override
	public void windowClosing(WindowEvent e)
	{
		Object ob = e.getSource();
		if(ob == dialog)
		{
			btCancel();
		}
	}

	@Override
	public Dialog getDialog()
	{
		return dialog;
	}
	@Override
	public void open()
	{
		mainLayout.show(mainPanel, "INPUT");
		dialog.setVisible(true);
	}
	@Override
	public String getSelectedFile()
	{
		return fileField.getText();
	}
	@Override
	public String getSelectedTable()
	{
		return tableField.getText();
	}
}
