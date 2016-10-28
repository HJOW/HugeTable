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

package hjow.hgtable.ui.module.defaults;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import hjow.hgtable.Main;
import hjow.hgtable.ui.GUIManager;
import hjow.hgtable.ui.module.GUIDialogModule;
import hjow.hgtable.ui.swing.LineNumberTextArea;
import hjow.hgtable.ui.swing.UIComboBox;
import hjow.hgtable.util.ArgumentUtil;
import hjow.hgtable.util.DataUtil;

/**
 * <p>SQL 문장의 매개 변수에 대응하는 것을 도와주는 모듈입니다.</p>
 * 
 * @author HJOW
 *
 */
public class ArgumentInjector extends GUIDialogModule
{
	private static final long serialVersionUID = -355939243422709022L;
	public static final transient long sid = serialVersionUID;
	protected JDialog dialog;
	protected JPanel mainPanel;
	protected JPanel upPanel;
	protected JPanel centerPanel;
	protected JPanel downPanel;
	protected JSplitPane splitPane;
	protected JPanel scriptPanel;
	protected LineNumberTextArea scriptArea;
	protected JPanel argumentPanel;
	protected JTable argTable;
	protected DefaultTableModel argModel;
	protected JButton btClose;
	protected JButton btRun;
	protected JScrollPane argScroll;
	protected UIComboBox argCombo;
	protected JPanel argumentControlPanel;
	protected JButton btNew;
	protected JButton btClean;
	protected CardLayout mainLayout;
	protected JPanel inputPanel;
	protected JPanel reviewPanel;
	protected JButton btReview;
	protected JPanel reviewCenterPanel;
	protected JPanel reviewDownPanel;
	protected LineNumberTextArea reviewArea;
	protected JButton btRunOnReview;
	protected JButton btCloseOnReview;
	protected JButton btBefore;
	
	public ArgumentInjector()
	{
		setModuleId(serialVersionUID);
	}
	
	/**
	 * <p>Argument Injector 모듈을 생성합니다. GUIManager 객체가 필요합니다.</p>
	 * 
	 * @param manager : 매니저 객체
	 */
	public ArgumentInjector(GUIManager manager)
	{
		super(manager);
		setModuleId(serialVersionUID);
	}
	
	@Override
	public void initializeComponents()
	{
		dialog = (JDialog) ((GUIManager) manager).newDialog(false);
		
		Dimension scSize = Toolkit.getDefaultToolkit().getScreenSize();
		dialog.setSize((int)(600), (int)(450));
		dialog.setLocation((int)(scSize.getWidth()/2 - dialog.getWidth()/2), (int)(scSize.getHeight()/2 - dialog.getHeight()/2));
		
		dialog.setLayout(new BorderLayout());
		dialog.setTitle(getName());
		
		mainPanel = new JPanel();
		dialog.add(mainPanel, BorderLayout.CENTER);
		
		mainLayout = new CardLayout();
		mainPanel.setLayout(mainLayout);
		
		inputPanel = new JPanel();
		reviewPanel = new JPanel();
		
		mainPanel.add("INPUT", inputPanel);
		mainPanel.add("REVIEW", reviewPanel);
		
		inputPanel.setLayout(new BorderLayout());
		reviewPanel.setLayout(new BorderLayout());
		
		upPanel = new JPanel();
		centerPanel = new JPanel();
		downPanel = new JPanel();
		
		inputPanel.add(upPanel, BorderLayout.NORTH);
		inputPanel.add(centerPanel, BorderLayout.CENTER);
		inputPanel.add(downPanel, BorderLayout.SOUTH);
		
		centerPanel.setLayout(new BorderLayout());
		
		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		centerPanel.add(splitPane, BorderLayout.CENTER);
		
		scriptPanel = new JPanel();
		scriptPanel.setLayout(new BorderLayout());
		
		scriptArea = new LineNumberTextArea();
		scriptPanel.add(scriptArea, BorderLayout.CENTER);
		
		splitPane.setTopComponent(scriptPanel);
		
		argumentPanel = new JPanel();
		argumentPanel.setLayout(new BorderLayout());
		
		argModel = new DefaultTableModel();
		argModel.addColumn(trans("KEY"));
		argModel.addColumn(trans("VALUE"));
		argTable = new JTable(argModel);
		argScroll = new JScrollPane(argTable);
		
		argumentPanel.add(argScroll, BorderLayout.CENTER);
		
		argumentControlPanel = new JPanel();
		argumentControlPanel.setLayout(new FlowLayout());
		
		btNew = new JButton(trans("New"));
		btClean = new JButton(trans("Clear Empty Row"));
		
		argumentControlPanel.add(btNew);
		argumentControlPanel.add(btClean);
		
		argumentPanel.add(argumentControlPanel, BorderLayout.NORTH);
		
		splitPane.setBottomComponent(argumentPanel);
		
		upPanel.setLayout(new FlowLayout());
		downPanel.setLayout(new FlowLayout());
		
		String[] argList = new String[3];
		argList[0] = "?";
		argList[1] = ":args";
		argList[2] = "#args#";
		
		argCombo = new UIComboBox(argList);
		
		btClose = new JButton(trans("Close"));
		btRun = new JButton(trans("Run"));
		btReview = new JButton(trans("Review"));
		
		upPanel.add(btClose);
		downPanel.add(argCombo);
		downPanel.add(btRun);
		downPanel.add(btReview);
		
		reviewCenterPanel = new JPanel();
		reviewDownPanel = new JPanel();
		
		reviewPanel.add(reviewCenterPanel, BorderLayout.CENTER);
		reviewPanel.add(reviewDownPanel, BorderLayout.SOUTH);
		
		reviewCenterPanel.setLayout(new BorderLayout());
		reviewDownPanel.setLayout(new FlowLayout());
		
		reviewArea = new LineNumberTextArea();
		reviewCenterPanel.add(reviewArea, BorderLayout.CENTER);
		
		btBefore = new JButton(trans("Before"));
		btRunOnReview = new JButton(trans("Run"));
		btCloseOnReview = new JButton(trans("Close"));
		
		reviewDownPanel.add(btBefore);
		reviewDownPanel.add(btRunOnReview);
		reviewDownPanel.add(btCloseOnReview);
		
		dialog.addWindowListener(this);
		btClose.addActionListener(this);
		btRun.addActionListener(this);
		btNew.addActionListener(this);
		btClean.addActionListener(this);
		btReview.addActionListener(this);
		btBefore.addActionListener(this);
		btRunOnReview.addActionListener(this);
		btCloseOnReview.addActionListener(this);
	}
	
	@Override
	public void refresh(Map<String, Object> additionalData)
	{
		
	}
	
	@Override
	public String getName()
	{
		return trans("Argument Injector for SQL");
	}
	
	@Override
	public Dialog getDialog()
	{
		return dialog;
	}
	
	@Override
	public void doAfterInitialize()
	{
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		Object ob = e.getSource();
		if(ob == btClose || ob == btCloseOnReview)
		{
			close();
		}
		else if(ob == btRun)
		{
			runScript(false);
		}
		else if(ob == btNew)
		{
			btNew();
		}
		else if(ob == btClean)
		{
			btClean();
		}
		else if(ob == btReview)
		{
			btReview();
		}
		else if(ob == btRunOnReview)
		{
			runScript(true);
		}
		else if(ob == btBefore)
		{
			btBefore();
		}
	}
	
	/**
	 * <p>입력 화면으로 돌아갑니다.</p>
	 */
	protected void btBefore()
	{
		mainLayout.show(mainPanel, "INPUT");
	}

	/**
	 * <p>미리 보기 화면으로 전환합니다.</p>
	 */
	protected void btReview()
	{
		String originals = scriptArea.getText();
		List<ArgumentData> args = new Vector<ArgumentData>();
		
		for(int i=0; i<argModel.getRowCount(); i++)
		{
			ArgumentData newData = new ArgumentData();
			
			newData.setName(String.valueOf(argModel.getValueAt(i, 0)));
			newData.setData(String.valueOf(argModel.getValueAt(i, 1)));
			
			args.add(newData);
		}
		
		String results = ArgumentUtil.applyArgs(originals, args, String.valueOf(argCombo.getSelectedItem()));
		reviewArea.setText(results);
		mainLayout.show(mainPanel, "REVIEW");
	}

	/**
	 * <p>매개 변수 테이블에서 빈 행을 제거합니다.</p>
	 * 
	 */
	protected void btClean()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				int i = 0;
				while(i<argModel.getRowCount())
				{
					boolean isAllEmpty = true;
					for(int j=0; j<2; j++)
					{
						if(DataUtil.isNotEmpty(argModel.getValueAt(i, j)))
						{
							isAllEmpty = false;
							break;
						}
					}
					if(isAllEmpty)
					{
						argModel.removeRow(i);
						i = 0;
					}
					else
					{
						i++;
					}
					
					if(! (Main.checkInterrupt(this, "btClean")))
					{
						break;
					}
				}
			}
		});
	}
	
	/**
	 * <p>매개 변수 테이블에 새 행을 추가합니다.</p>
	 */
	protected void btNew()
	{
		Object[] emptyObjs = new Object[2];
		emptyObjs[0] = "";
		emptyObjs[1] = "";
		argModel.addRow(emptyObjs);
	}

	/**
	 * <p>스크립트에 매개 변수들을 반영해 실행합니다.</p>
	 * 
	 * @param useReviews : 미리 보기 기능으로부터 실행되었는지 여부
	 */
	protected void runScript(boolean useReviews)
	{
		try
		{
			if(useReviews)
			{
				manager.logTable(manager.getDao().query(reviewArea.getText()));
			}
			else
			{
				String originals = scriptArea.getText();
				List<ArgumentData> args = new Vector<ArgumentData>();
				
				for(int i=0; i<argModel.getRowCount(); i++)
				{
					ArgumentData newData = new ArgumentData();
					
					newData.setName(String.valueOf(argModel.getValueAt(i, 0)));
					newData.setData(String.valueOf(argModel.getValueAt(i, 1)));
					
					args.add(newData);
				}
				
				String results = ArgumentUtil.applyArgs(originals, args, String.valueOf(argCombo.getSelectedItem()));
				reviewArea.setText(results);
				manager.logTable(manager.getDao().query(results));
			}
		}
		catch(Exception e)
		{
			manager.logError(e, trans("On run script with args"));
		}
	}
	
	@Override
	public int getMenuLocation()
	{
		return ON_MENU_TOOL;
	}

	@Override
	public void open()
	{
		super.open();
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{	
				mainLayout.show(mainPanel, "INPUT");
				splitPane.setDividerLocation(0.5);
			}
		});
	}
	
	@Override
	public void noMoreUse()
	{
		super.noMoreUse();
	}
}
