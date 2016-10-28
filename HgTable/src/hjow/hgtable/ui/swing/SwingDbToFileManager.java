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
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import hjow.hgtable.HThread;
import hjow.hgtable.Manager;
import hjow.hgtable.ui.DbToFileManager;
import hjow.hgtable.ui.GUIManager;

/**
 * <p>이 클래스 객체는 GUI 환경에서 DB의 데이터를 가져와 파일로 저장하는 데 사용됩니다.</p>
 * 
 * @author HJOW
 *
 */
public class SwingDbToFileManager extends DbToFileManager
{
	protected JDialog dialog;
	protected JFileChooser fileChooser;
	protected FileFilter xlsxFileFilter;
	protected FileFilter jsonFileFilter;
	protected FileFilter hgfFileFilter;
	protected JPanel mainPanel;
	protected JPanel upPanel;
	protected JPanel centerPanel;
	protected JPanel downPanel;
	protected JPanel inputPanel;
	protected CardLayout mainLayout;
	protected JTextField fileField;
	protected JButton btFile;
	protected CodeEditorPane queryArea;
	protected JButton btOk;
	protected JButton btCancel;
	protected JPanel progressPanel;
	protected JProgressBar progressBar;
	protected JLabel tableNameLabel;
	protected JTextField tableNameField;
	protected JPanel tableNamePanel;
	
	/**
	 * <p>기본 생성자입니다. 컴포넌트들을 초기화한 후, 작업용 쓰레드를 실행합니다.</p>
	 * 
	 * @param manager : GUI 매니저 객체
	 * @param frame : Frame 객체 (AWT 호환)
	 */
	public SwingDbToFileManager(GUIManager manager, Frame frame)
	{
		super(manager);
		dialog = new JDialog(frame, true);
		
		Dimension scSize = Toolkit.getDefaultToolkit().getScreenSize();
		dialog.setSize((int)(420), (int)(250));
		dialog.setLocation((int)(scSize.getWidth()/2 - dialog.getWidth()/2), (int)(scSize.getHeight()/2 - dialog.getHeight()/2));
		
		dialog.setTitle(Manager.applyStringTable("DB --> file"));
		
		mainPanel = new JPanel();
		dialog.setLayout(new BorderLayout());
		dialog.add(mainPanel, BorderLayout.CENTER);
		
		upPanel = new JPanel();
		centerPanel = new JPanel();
		downPanel = new JPanel();
		
		mainLayout = new CardLayout();
		mainPanel.setLayout(mainLayout);
		
		inputPanel = new JPanel();
		mainPanel.add(inputPanel, "INPUT");
		
		inputPanel.setLayout(new BorderLayout());
		
		inputPanel.add(upPanel, BorderLayout.NORTH);
		inputPanel.add(centerPanel, BorderLayout.CENTER);
		inputPanel.add(downPanel, BorderLayout.SOUTH);
		
		upPanel.setLayout(new FlowLayout());
		fileField = new JTextField(20);
		btFile = new JButton(Manager.applyStringTable("..."));
		upPanel.add(fileField);
		upPanel.add(btFile);
		
		centerPanel.setLayout(new BorderLayout());
//		queryArea = new LineNumberTextArea();
		queryArea = new UISyntaxView();
		queryArea.setText("/*" + Manager.applyStringTable("Type SELECT SQL statement here") + "*/\n");
		queryArea.setHighlightMode("SQL");
		// queryScroll = new JScrollPane(queryArea);
		
		centerPanel.add(queryArea.getComponent(), BorderLayout.CENTER);
		
		tableNamePanel = new JPanel();
		tableNameLabel = new JLabel(Manager.applyStringTable("Table name"));
		tableNameField = new JTextField(20);
		tableNamePanel.setLayout(new FlowLayout());
		tableNamePanel.add(tableNameLabel);
		tableNamePanel.add(tableNameField);
		centerPanel.add(tableNamePanel, BorderLayout.SOUTH);
		
		downPanel.setLayout(new FlowLayout());
		
		btOk = new JButton(Manager.applyStringTable("OK"));
		btCancel = new JButton(Manager.applyStringTable("Cancel"));
		downPanel.add(btOk);
		downPanel.add(btCancel);
		
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
		progressPanel.add(progressBar);
		
		dialog.addWindowListener(this);
		btFile.addActionListener(this);
		btOk.addActionListener(this);
		btCancel.addActionListener(this);
		
		new HThread(this).start();
	}
	@Override
	public String getQuery()
	{
		return queryArea.getText();
	}
	@Override
	public String getSelectedFile()
	{
		return fileField.getText();
	}
	@Override
	public String getSelectedTable()
	{
		return tableNameField.getText();
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
		int selected = fileChooser.showSaveDialog(dialog);
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
}
