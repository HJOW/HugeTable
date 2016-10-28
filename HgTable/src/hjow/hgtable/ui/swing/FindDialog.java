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

import hjow.hgtable.Manager;
import hjow.hgtable.ui.NeedtoEnd;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;

/**
 * <p>검색 창입니다.</p>
 * 
 * @author HJOW
 *
 */
public class FindDialog extends JDialog implements NeedtoEnd, ActionListener, WindowListener
{
	private static final long serialVersionUID = 6755143659050867402L;
	protected CodeEditorPane superComp;
	protected JPanel mainPanel;
	protected JPanel upPanel;
	protected JPanel centerPanel;
	protected JPanel downPanel;
	protected JPanel[] centerPns;
	
	protected int selectedPosition = 0;
	protected JLabel searchTargetLabel;
	protected JTextArea searchTargetField;
	protected JLabel replaceTargetLabel;
	protected JTextArea replaceTargetField;
	protected JButton btClose;
	protected JButton btSearch;
	protected JButton btReplace;
	protected JButton btReplaceAll;
	protected JPanel[] downPns;
	protected JCheckBox caseSensitive;

	public FindDialog(CodeEditorPane superComp, Window window)
	{
		super(window);
		this.superComp = superComp;
		
		Dimension scSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setSize((int)(250), (int)(250));
		this.setLocation((int)(scSize.getWidth()/2 - this.getWidth()/2), (int)(scSize.getHeight()/2 - this.getHeight()/2));
		
		this.setTitle(Manager.applyStringTable("Search"));
		
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
		
		centerPns = new JPanel[5];
		centerPanel.setLayout(new GridLayout(centerPns.length, 1));
		
		for(int i=0; i<centerPns.length; i++)
		{
			centerPns[i] = new JPanel();
			if(i < 2) centerPns[i].setLayout(new BorderLayout());
			else centerPns[i].setLayout(new FlowLayout());
			centerPanel.add(centerPns[i]);
		}
		
		searchTargetLabel = new JLabel(Manager.applyStringTable("Keyword"));
		searchTargetField = new JTextArea();
		searchTargetField.setBorder(new EtchedBorder());
		
		centerPns[0].add(searchTargetLabel, BorderLayout.WEST);
		centerPns[0].add(searchTargetField, BorderLayout.CENTER);
		
		replaceTargetLabel = new JLabel(Manager.applyStringTable("Replaces"));
		replaceTargetField = new JTextArea();
		replaceTargetField.setBorder(new EtchedBorder());
		
		centerPns[1].add(replaceTargetLabel, BorderLayout.WEST);
		centerPns[1].add(replaceTargetField, BorderLayout.CENTER);
		
		caseSensitive = new JCheckBox(Manager.applyStringTable("Case Sensitive"));
		caseSensitive.setSelected(true);
		centerPns[2].add(caseSensitive);
		
		downPns = new JPanel[2];
		downPanel.setLayout(new GridLayout(downPns.length, 1));
		for(int i=0; i<downPns.length; i++)
		{
			downPns[i] = new JPanel();
			downPns[i].setLayout(new FlowLayout());
			downPanel.add(downPns[i]);
		}
		
		btSearch = new JButton(Manager.applyStringTable("Search"));
		btReplace = new JButton(Manager.applyStringTable("Replace"));
		btReplaceAll = new JButton(Manager.applyStringTable("Replace All"));
		btClose = new JButton(Manager.applyStringTable("Close"));
		
		downPns[0].add(btSearch);
		downPns[0].add(btReplace);
		downPns[1].add(btReplaceAll);
		downPns[1].add(btClose);
		
		btClose.addActionListener(this);
		btSearch.addActionListener(this);
		btReplace.addActionListener(this);
		btReplaceAll.addActionListener(this);
		
//		searchTargetField.addActionListener(this);
		this.addWindowListener(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		Object ob = e.getSource();
		if(ob == btSearch || ob == searchTargetField)
		{
			search();
		}
		else if(ob == btReplace)
		{
			replace();
		}
		else if(ob == btReplaceAll)
		{
			replaceAll();
		}
		else if(ob == btClose)
		{
			close();
		}
	}
	
	/**
	 * <p>대화 상자를 닫습니다.</p>
	 * 
	 */
	protected void close()
	{
		this.setVisible(false);
	}
	
	/**
	 * <p>대화 상자를 엽니다.</p>
	 */
	protected void open()
	{
		this.setVisible(true);
	}
	
	/**
	 * <p>모두 바꾸기 작업을 수행합니다.</p>
	 * 
	 */
	protected void replaceAll()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				superComp.setText(superComp.getText().replaceAll(searchTargetField.getText(), replaceTargetField.getText()));
			}
		});
	}

	/**
	 * <p>바꾸기 작업을 수행합니다.</p>
	 */
	protected void replace()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				if(searchTargetField.getText().equals("") || replaceTargetField.getText().equals("")) return;
				int position = superComp.getText().indexOf(replaceTargetField.getText());
				if(position >= 0)
				{
					superComp.getTextPanel().setCaretPosition(position);
					superComp.setText(superComp.getText().replaceFirst(searchTargetField.getText(), replaceTargetField.getText()));
					superComp.getTextPanel().select(position, position + replaceTargetField.getText().length());
				}
			}
		});
	}

	/**
	 * <p>찾기 작업을 수행합니다.</p>
	 * 
	 */
	protected void search()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				String originalText = superComp.getText();
				String targetText = originalText;
				int position = -1;
				
				if(originalText == null || originalText.equals("")) return;
				
				if(caseSensitive.isSelected())
				{
					position = targetText.indexOf(searchTargetField.getText());
				}
				else
				{
					targetText = targetText.toUpperCase();
					position = targetText.indexOf(searchTargetField.getText().toUpperCase());
				}
				if(position >= 0)
				{
					superComp.getTextPanel().setCaretPosition(position);
					superComp.getTextPanel().select(position, position + searchTargetField.getText().length());
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
		superComp = null;
	}
	
	@Override
	public boolean isAlive()
	{
		return superComp != null;
	}

	@Override
	public void windowActivated(WindowEvent e)
	{
		
	}

	@Override
	public void windowClosed(WindowEvent e)
	{
		
	}

	@Override
	public void windowDeactivated(WindowEvent e)
	{
		
	}

	@Override
	public void windowDeiconified(WindowEvent e)
	{
		
	}

	@Override
	public void windowIconified(WindowEvent e)
	{
		
	}

	@Override
	public void windowOpened(WindowEvent e)
	{
		
	}
}
