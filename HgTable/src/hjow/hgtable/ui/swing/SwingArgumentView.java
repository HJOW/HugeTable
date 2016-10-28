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
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableModel;

import hjow.hgtable.Main;
import hjow.hgtable.Manager;
import hjow.hgtable.util.DataUtil;

/**
 * <p>사용자에게 Map<String, String> 입력을 받는 컴포넌트입니다.</p>
 * 
 * @author HJOW
 *
 */
public class SwingArgumentView extends JPanel implements ActionListener
{
	private static final long serialVersionUID = -3406627348232406263L;
	protected JPanel upPanel;
	protected JPanel centerPanel;
	protected JPanel downPanel;
	protected JTable table;
	protected DefaultTableModel model;
	protected JScrollPane scroll;
	protected JButton btClearEmpties;
	protected JButton btAdd;
	
	public SwingArgumentView()
	{
		this.setLayout(new BorderLayout());
		this.setBorder(new EtchedBorder());
		
		upPanel = new JPanel();
		centerPanel = new JPanel();
		downPanel = new JPanel();
		
		this.add(upPanel, BorderLayout.NORTH);
		this.add(centerPanel, BorderLayout.CENTER);
		this.add(downPanel, BorderLayout.SOUTH);
		
		centerPanel.setLayout(new BorderLayout());
		
		model = new DefaultTableModel();
		model.addColumn(t("Key"));
		model.addColumn(t("Value"));
		table = new JTable(model);
		scroll = new JScrollPane(table);
		
		centerPanel.add(scroll, BorderLayout.CENTER);
		
		upPanel.setLayout(new FlowLayout());
		
		btAdd   = new JButton(t("Add"));
		btClearEmpties = new JButton(t("Clear Empty Row"));
		
		upPanel.add(btAdd);
		upPanel.add(btClearEmpties);
		
		btAdd.addActionListener(this);
		btClearEmpties.addActionListener(this);
	}
	
	/**
	 * <p>사용자가 입력한 맵을 반환합니다.</p>
	 * <p>주의 : 입력 중인 내용은 반영되지 않습니다. 입력이 완전히 완료되어 커서가 밖으로 나간 셀 내용만 반영됩니다.</p>
	 * 
	 * @return 입력한 맵
	 */
	public Map<String, String> getMap()
	{
		table.requestFocus();
		Map<String, String> resultMap = new Hashtable<String, String>();
		
		for(int i=0; i<model.getRowCount(); i++)
		{
			resultMap.put(String.valueOf(model.getValueAt(i, 0)), String.valueOf(model.getValueAt(i, 1)));
		}
		
		return resultMap;
	}
	
	/**
	 * <p>맵 내용을 입력합니다. 기존에 입력된 내용은 삭제됩니다.</p>
	 * 
	 * @param mapData : 입력할 맵
	 */
	public void setMap(Map<String, String> mapData)
	{
		clear();
		
		for(String s : mapData.keySet())
		{
			Vector<String> newRow = new Vector<String>();
			
			newRow.add(s);
			newRow.add(mapData.get(s));
			
			model.addRow(newRow);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		Object ob = e.getSource();
		if(ob == btAdd)
		{
			addRow();
		}
		else if(ob == btClearEmpties)
		{
			clearEmpties();
		}
	}
	
	/**
	 * <p>테이블 내용을 모두 비웁니다.</p>
	 */
	public void clear()
	{
		model = new DefaultTableModel();
		model.addColumn(t("Key"));
		model.addColumn(t("Value"));
		table.setModel(model);
	}

	/**
	 * <p>빈 행을 하나 추가합니다.</p>
	 * 
	 */
	public void addRow()
	{
		Vector<String> newRow = new Vector<String>();
		newRow.add("");
		newRow.add("");
		model.addRow(newRow);
	}

	/**
	 * <p>빈 행들을 모두 제거합니다.</p>
	 */
	public void clearEmpties()
	{
		SwingUtilities.invokeLater(new Runnable()
		{	
			@Override
			public void run()
			{
				int i = 0;
				while(i<model.getRowCount())
				{
					boolean isAllEmpty = true;
					for(int j=0; j<model.getColumnCount(); j++)
					{
						if(DataUtil.isNotEmpty(model.getValueAt(i, j)))
						{
							isAllEmpty = false;
							break;
						}
					}
					if(isAllEmpty)
					{
						model.removeRow(i);
						i = 0;
					}
					else
					{
						i++;
					}
					
					if(! (c(this, "clearEmpties")))
					{
						break;
					}
				}
			}
		});
	}
	
	private boolean c(Object obj, String msg)
	{
		try
		{
			return Main.checkInterrupt(obj, msg);
		}
		catch(Throwable e)
		{
			return true;
		}
	}
	private String t(String str)
	{
		try
		{
			return Manager.applyStringTable(str);
		}
		catch(Throwable e)
		{
			return str;
		}
	}
}
