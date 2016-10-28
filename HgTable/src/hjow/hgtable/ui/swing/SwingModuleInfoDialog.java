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
/*
 Be careful !
 Oracle software has a license policy called OTN.
 If you want to use this source to use Oracle Database, you should agree OTN.
 Visit http://www.oracle.com to see details. 
 */

package hjow.hgtable.ui.swing;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import hjow.hgtable.Manager;
import hjow.hgtable.classload.ClassTool;
import hjow.hgtable.jscript.module.Module;
import hjow.hgtable.ui.GUIManager;
import hjow.hgtable.ui.module.GUIDialogModule;
import hjow.hgtable.util.DataUtil;
import hjow.hgtable.util.StreamUtil;


/**
 * <p>모듈에 대한 정보 표시 및 라이센스 동의서를 받습니다.</p>
 * 
 * @author HJOW
 *
 */
public class SwingModuleInfoDialog extends GUIDialogModule
{
	private static final long serialVersionUID = -8816267507867208182L;
	protected JDialog dialog;
	protected JPanel mainPanel;
	protected JPanel upPanel;
	protected JPanel centerPanel;
	protected JPanel downPanel;
	protected JEditorPane licenseArea;
	protected JScrollPane licenseScroll;
	protected JPanel controlPanel;
	protected JButton btAccept;
	protected JButton btDecline;
	protected boolean selected = false;
	protected JPanel infoPanel;
	protected JLabel nameLabel;
	protected JPanel[] infoPns;
	protected JLabel idLabel;
	protected JLabel isIdLabel;
	protected JSplitPane centerSplit;
	protected JTextArea privilegeArea;
	protected JScrollPane privilegeScroll;

	/**
	 * <p>기본 생성자입니다.</p>
	 */
	public SwingModuleInfoDialog()
	{
		super();
	}
	
	/**
	 * <p>매니저 객체를 받는 생성자입니다.</p>
	 * 
	 * @param manager
	 */
	public SwingModuleInfoDialog(GUIManager manager)
	{
		super(manager);
		init();
	}
	
	@Override
	public void initializeComponents()
	{
		if(manager != null && manager instanceof GUIManager)
		{
			dialog = (JDialog) ((GUIManager) manager).newDialog(true);
			dialog.setTitle(Manager.applyStringTable("License Agreements"));
			
			dialog.setSize(450, 350);
			dialog.addWindowListener(this);
			
			Dimension scSize = Toolkit.getDefaultToolkit().getScreenSize();
			dialog.setLocation((int)(scSize.getWidth()/2 - dialog.getWidth()/2), (int)(scSize.getHeight()/2 - dialog.getHeight()/2));
			
			dialog.setLayout(new BorderLayout());
			
			mainPanel = new JPanel();
			dialog.add(mainPanel, BorderLayout.CENTER);
			
			mainPanel.setLayout(new BorderLayout());
			
			upPanel = new JPanel();
			centerPanel = new JPanel();
			downPanel = new JPanel();
			
			mainPanel.add(upPanel, BorderLayout.NORTH);
			mainPanel.add(centerPanel, BorderLayout.CENTER);
			mainPanel.add(downPanel, BorderLayout.SOUTH);
			
			upPanel.setLayout(new BorderLayout());
			centerPanel.setLayout(new BorderLayout());
			downPanel.setLayout(new BorderLayout());
			
			centerSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
			
			privilegeArea = new JTextArea();
			privilegeArea.setEditable(false);
			privilegeScroll = new JScrollPane(privilegeArea);
			
			centerSplit.setTopComponent(privilegeScroll);
			
			licenseArea = new UIEditorPane();
			licenseArea.setEditable(false);
			licenseScroll = new JScrollPane(licenseArea);
			
			centerSplit.setBottomComponent(licenseScroll);
			
			centerPanel.add(centerSplit, BorderLayout.CENTER);
			
			controlPanel = new JPanel();
			downPanel.add(controlPanel, BorderLayout.CENTER);
			
			controlPanel.setLayout(new FlowLayout());
			
			btAccept = new JButton(Manager.applyStringTable("Accept"));
			btDecline = new JButton(Manager.applyStringTable("Decline"));
			
			btAccept.addActionListener(this);
			btDecline.addActionListener(this);
			
			controlPanel.add(btAccept);
			controlPanel.add(btDecline);
			
			infoPanel = new JPanel();
			upPanel.add(infoPanel, BorderLayout.CENTER);
			
			infoPns = new JPanel[2];
			infoPanel.setLayout(new GridLayout(infoPns.length, 1));
			
			for(int i=0; i<infoPns.length; i++)
			{
				infoPns[i] = new JPanel();
				infoPns[i].setLayout(new FlowLayout());
				infoPanel.add(infoPns[i]);
			}
			
			nameLabel = new JLabel();
			infoPns[0].add(nameLabel);
			
			isIdLabel = new JLabel(Manager.applyStringTable("ID") + " : ");
			idLabel = new JLabel();
			infoPns[1].add(isIdLabel);
			infoPns[1].add(idLabel);
		}
	}
	
	@Override
	public Dialog getDialog()
	{
		return dialog;
	}
	
	/**
	 * <p>라이센스 정책 대화 상자를 엽니다.</p>
	 * 
	 * @param module : 해당 모듈
	 */
	public void open(Module module)
	{
		selected = false;
		
		if(DataUtil.isNotEmpty(module.getLicense()) || DataUtil.isNotEmpty(module.getPriv_allowReadFilePath()) || DataUtil.isNotEmpty(module.getPriv_allowWriteFilePath()))
		{
			if(DataUtil.isNotEmpty(module.getLicense()))
			{
				String target = module.getLicense().trim();
				if(target.startsWith("url://") || target.startsWith("URL://") || target.startsWith("Url://"))
				{
					try
					{
						licenseArea.setPage(target.substring(new String("url://").length()));
					}
					catch (IOException e)
					{
						licenseArea.setText(Manager.applyStringTable("Cannot access URL") + " : " + target.substring(new String("url://").length()));
					}
				}
				else if(target.startsWith("file://") || target.startsWith("FILE://") || target.startsWith("File://"))
				{
					File targetFile = new File(target.substring(new String("file://").length()));
					if(! targetFile.exists())
					{
						targetFile = new File(ClassTool.currentDirectory() + target.substring(new String("file://").length()));
					}
					if(targetFile.exists())
					{
						licenseArea.setText(StreamUtil.readText(targetFile, "UTF-8"));
					}
					else
					{
						licenseArea.setText(Manager.applyStringTable("Cannot access File") + " : " + target.substring(new String("file://").length()));
					}
				}
				else
				{
					licenseArea.setText(target);
				}
			}
			else
			{
				licenseArea.setText(Manager.applyStringTable("There is no license policy for") + " " + module.getName());
			}
			
			String privText = "";
			if(DataUtil.isNotEmpty(module.getPriv_allowReadFilePath()))
			{
				privText = privText + module.getName() + " " + Manager.applyStringTable("can access following pathes to read contents.") + "\n";
				for(String s : module.getPriv_allowReadFilePath())
				{
					privText = privText + s + "\n";
				}
			}
			if(DataUtil.isNotEmpty(module.getPriv_allowWriteFilePath()))
			{
				privText = privText + module.getName() + " " + Manager.applyStringTable("can access following pathes to write contents.") + "\n";
				for(String s : module.getPriv_allowWriteFilePath())
				{
					privText = privText + s + "\n";
				}
			}
			if(DataUtil.isEmpty(privText))
			{
				privText = Manager.applyStringTable("There is no additional privilege to use") + " " + module.getName();
			}
			
			privilegeArea.setText(privText);
			
			nameLabel.setText(module.getName());
			idLabel.setText(String.valueOf(module.getModuleId()));
			
			super.open();
			
			SwingUtilities.invokeLater(new Runnable()
			{
				@Override
				public void run()
				{
					centerSplit.setDividerLocation(0.3);
				}
			});
		}
		else
		{
			selected = true;
			close();
		}
	}

	/**
	 * <p>사용자가 라이센스 정책에 동의했는지 여부를 반환합니다.</p>
	 * 
	 * @return 사용자의 동의 여부
	 */
	public boolean accepted()
	{
		return selected;
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		Object ob = e.getSource();
		if(ob == btDecline)
		{
			selected = false;
			close();
		}
		else if(ob == btAccept)
		{
			selected = true;
			close();
		}
	}
}
