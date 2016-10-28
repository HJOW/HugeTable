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

import hjow.hgtable.Main;
import hjow.hgtable.Manager;
import hjow.hgtable.ui.NeedtoEnd;
import hjow.hgtable.util.DataUtil;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

/**
 * <p>이 클래스 객체는 환경 설정 편집 대화 상자를 구성합니다.</p>
 * 
 * @author HJOW
 *
 */
public class SwingPreferenceEditor extends JDialog implements NeedtoEnd, ActionListener, WindowListener, TableModelListener
{
	private static final long serialVersionUID = 558531299466010695L;
	protected Manager manager;
	protected JPanel mainPanel;
	protected JPanel centerPanel;
	protected JPanel downPanel;
	protected JTable centerTable;
	protected DefaultTableModel tableModel;
	protected JScrollPane centerScroll;
	protected JButton btOk;
	protected JButton btCancel;
	protected JPanel controlPanel;
	protected JPanel upPanel;
	protected JButton btAdd;
	protected JPanel needRestartPanel;
	protected JLabel needRestartLabel;
	private JButton btClean;
	
	/**
	 * <p>객체를 만들고 컴포넌트를 초기화합니다.</p>
	 * 
	 * @param frame : Frame 객체 (AWT 호환)
	 * @param manager : 매니저 객체
	 */
	public SwingPreferenceEditor(Frame frame, Manager manager)
	{
		super(frame, true);
		this.manager = manager;
		this.setSize(500, 350);
		
		this.setTitle(Manager.applyStringTable("Preference"));
		
		Dimension scSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((int)(scSize.getWidth()/2 - this.getWidth()/2), (int)(scSize.getHeight()/2 - this.getHeight()/2));
		
		this.setLayout(new BorderLayout());
		
		mainPanel = new JPanel();
		
		this.add(mainPanel, BorderLayout.CENTER);
		mainPanel.setLayout(new BorderLayout());
		
		upPanel = new JPanel();
		centerPanel = new JPanel();
		downPanel = new JPanel();
		
		mainPanel.add(upPanel, BorderLayout.NORTH);
		mainPanel.add(centerPanel, BorderLayout.CENTER);
		mainPanel.add(downPanel, BorderLayout.SOUTH);
		
		centerPanel.setLayout(new BorderLayout());
		
		tableModel = new DefaultTableModel();
		tableModel.addColumn(Manager.applyStringTable("KEY"));
		tableModel.addColumn(Manager.applyStringTable("VALUE"));
		tableModel.addTableModelListener(this);
		centerTable = new JTable(tableModel);
		centerScroll = new JScrollPane(centerTable);
		
		centerPanel.add(centerScroll);
		
		downPanel.setLayout(new BorderLayout());
		
		controlPanel = new JPanel();
		controlPanel.setLayout(new FlowLayout());
		
		needRestartPanel = new JPanel();
		needRestartPanel.setLayout(new FlowLayout());
		
		needRestartLabel = new JLabel(Manager.applyStringTable("Need restart to apply changes."));
		needRestartPanel.add(needRestartLabel);
		
		downPanel.add(controlPanel, BorderLayout.SOUTH);
		downPanel.add(needRestartPanel, BorderLayout.NORTH);
		
		btOk = new JButton(Manager.applyStringTable("OK"));
		btCancel = new JButton(Manager.applyStringTable("Cancel"));
		
		controlPanel.add(btOk);
		controlPanel.add(btCancel);
		
		upPanel.setLayout(new FlowLayout());
		
		btAdd = new JButton(Manager.applyStringTable("Add new option"));
		btClean = new JButton(Manager.applyStringTable("Clear Empty Row"));
		upPanel.add(btAdd);
		upPanel.add(btClean);
		
		this.addWindowListener(this);
		btOk.addActionListener(this);
		btCancel.addActionListener(this);
		btAdd.addActionListener(this);
		btClean.addActionListener(this);
	}
	
	/**
	 * <p>대화 상자를 엽니다.</p>
	 * 
	 */
	public void open()
	{
		setVisible(false);
		refresh();
		setVisible(true);
	}
	
	/**
	 * <p>대화 상자를 닫습니다.</p>
	 * 
	 */
	public void close()
	{
		setVisible(false);
	}
	
	/**
	 * <p>목록을 새로 고칩니다. 파일로부터 읽어오지는 않고, 현재 적용되어 있는 옵션만 가져옵니다.</p>
	 * 
	 */
	public void refresh()
	{
		Set<String> keys = manager.getParamMap(manager).keySet();
		
		if(tableModel != null)
		{
			tableModel.removeTableModelListener(this);
		}
		tableModel = new DefaultTableModel();
		
		tableModel.addColumn(Manager.applyStringTable("KEY"));
		tableModel.addColumn(Manager.applyStringTable("VALUE"));
		
		for(String s : keys)
		{
			if(s.equalsIgnoreCase("GUIMode")) continue;
			String[] rowData = new String[2];
			rowData[0] = s;
			rowData[1] = Manager.getOption(s);
			tableModel.addRow(rowData);
		}
		
		tableModel.addTableModelListener(this);
		centerTable.setModel(tableModel);
		System.gc();
	}
	
	/**
	 * <p>환경 설정을 적용하고 파일로 저장합니다. 일부 설정은 재시작 후에 적용됩니다.</p>
	 * 
	 */
	public void save()
	{
		for(int i=0; i<tableModel.getRowCount(); i++)
		{
			if(DataUtil.isNotEmpty(tableModel.getValueAt(i, 0)))
			{
				manager.getParamMap(manager).put(String.valueOf(tableModel.getValueAt(i, 0)), String.valueOf(tableModel.getValueAt(i, 1)));
			}
		}
		manager.reInsertDefaultParams();
		manager.saveConfig();
	}
	@Override
	public void tableChanged(TableModelEvent e)
	{
		
	}
	@Override
	public void actionPerformed(ActionEvent e)
	{
		Object ob = e.getSource();
		if(ob == btCancel)
		{
			close();
		}
		else if(ob == btOk)
		{
			save();
			close();
		}
		else if(ob == btAdd)
		{
			String[] newRow = new String[2];
			newRow[0] = "NEWOPTION";
			newRow[1] = "";
			tableModel.addRow(newRow);
		}
		else if(ob == btClean)
		{
			btClean();
		}
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
				while(i<tableModel.getRowCount())
				{
					boolean isAllEmpty = true;
					for(int j=0; j<2; j++)
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
					
					if(! (Main.checkInterrupt(this, "btClean")))
					{
						break;
					}
				}
			}
		});
	}
	
	@Override
	public void windowClosing(WindowEvent e)
	{
		Object ob = e.getSource();
		if(ob == this)
		{
			close();
		}
	}
	@Override
	public void noMoreUse()
	{
		this.manager = null;
	}
	@Override
	public boolean isAlive()
	{
		return manager != null;
	}
	
	@Override
	public void windowOpened(WindowEvent e)
	{
		
		
	}
	@Override
	public void windowClosed(WindowEvent e)
	{
		
		
	}
	@Override
	public void windowIconified(WindowEvent e)
	{
		
		
	}
	@Override
	public void windowDeiconified(WindowEvent e)
	{
		
		
	}
	@Override
	public void windowActivated(WindowEvent e)
	{
		
		
	}
	@Override
	public void windowDeactivated(WindowEvent e)
	{
		
		
	}
}
