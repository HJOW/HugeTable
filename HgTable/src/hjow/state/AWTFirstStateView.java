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
package hjow.state;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JFrame;

import hjow.hgtable.Main;
import hjow.hgtable.Manager;
import hjow.hgtable.ui.AWTProgressBar;
import hjow.hgtable.util.DataUtil;
import hjow.hgtable.util.GUIUtil;
import hjow.hgtable.util.LicenseUtil;

/**
 * <p>실행 시 프로세스 생존 여부를 사용자에게 알려주는 역할을 합니다. GUI로 쓰인 Swing 컴포넌트들과의 충돌 방지를 위해 AWT 기반으로 개발되었습니다.</p>
 * 
 * @author HJOW
 *
 */
public class AWTFirstStateView extends FirstStateView implements ActionListener
{
	protected Window dialog;
	protected Panel mainPanel;
	protected Panel centerPanel;
	protected Panel downPanel;
	protected AWTProgressBar progress;
	protected Button btExit;
	protected Panel progressPanel;
	protected Label titleLabel;
	protected int divisions = 3;
	protected Panel messagePanel;
	protected Panel titlePanel;
	protected TextField messageField;
	protected boolean showMsg = true;
	
	@Override
	protected void init()
	{
		dialog = new Frame();
		if(dialog instanceof JDialog) ((JDialog) dialog).setUndecorated(true);
		else if(dialog instanceof JFrame) ((JFrame) dialog).setUndecorated(true);
		
		Dimension scSize = Toolkit.getDefaultToolkit().getScreenSize();
//		dialog.setSize((int)(200), (int)(60));
		dialog.setSize((int)(200), (int)(120));
		dialog.setLocation((int)(scSize.getWidth()/2 - dialog.getWidth()/2), (int)(scSize.getHeight()/2 - dialog.getHeight()/2));
		
		dialog.setLayout(new BorderLayout());
		
		mainPanel = new Panel();
		dialog.add(mainPanel, BorderLayout.CENTER);
		
		mainPanel.setLayout(new BorderLayout());
		
		centerPanel = new Panel();
		downPanel = new Panel();
		
		mainPanel.add(centerPanel, BorderLayout.CENTER);
		mainPanel.add(downPanel, BorderLayout.SOUTH);
		
		centerPanel.setLayout(new BorderLayout());
		downPanel.setLayout(new BorderLayout());
		
		titlePanel = new Panel();
		titlePanel.setLayout(new FlowLayout());
		centerPanel.add(titlePanel, BorderLayout.CENTER);
		
		titleLabel = new Label(LicenseUtil.titles);
		titlePanel.add(titleLabel);
		
		progressPanel = new Panel();
		progress = new AWTProgressBar();
		progress.setEditable(false);
		
		progressPanel.setLayout(new BorderLayout());
		progressPanel.add(progress);
		
		downPanel.add(progressPanel, BorderLayout.CENTER);
		
		btExit = new Button("X");
		downPanel.add(btExit, BorderLayout.EAST);
		
		btExit.addActionListener(this);
		
		messagePanel = new Panel();
		messagePanel.setLayout(new BorderLayout());
		centerPanel.add(messagePanel, BorderLayout.SOUTH);
		
		messageField = new TextField();
		messageField.setEditable(false);
		messageField.setVisible(false);
		messagePanel.add(messageField);
		
		try
		{
		    if(DataUtil.isNotEmpty(Manager.getOption("loadingMessage"))) showMsg = DataUtil.parseBoolean(Manager.getOption("loadingMessage"));
		}
		catch(Exception e)
		{
			
		}
		
		if(GUIUtil.usingFont != null) GUIUtil.setFontRecursively(dialog, GUIUtil.usingFont, 100);
		if(GUIUtil.usingFont2B != null) GUIUtil.setFontRecursively(titleLabel, GUIUtil.usingFont2B, 10);
		messageField.setFont(new Font(null, Font.PLAIN, 8));
		dialog.setVisible(true);
	}
	
	public boolean isShowMsg()
	{
		return showMsg;
	}

	public void setShowMsg(boolean showMsg)
	{
		this.showMsg = showMsg;
		messageField.setVisible(showMsg);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		Object ob = e.getSource();
		if(ob == btExit)
		{
			Main.interrupt(true);
			off();
			Main.exitAllProcess();
		}
	}
	
	@Override
	protected void refresh()
	{
		if(dialog instanceof Frame)
		{
			progress.setValue(numbers);
			progress.setShowMsg(showMsg);
			progress.setText(msg);
			if(showMsg) messageField.setText(msg);
			((Frame) dialog).setTitle(String.valueOf(numbers) + " %");
		}
		else if(dialog instanceof Dialog)
		{
			progress.setValue(numbers);
			progress.setShowMsg(showMsg);
			progress.setText(msg);
			if(showMsg) messageField.setText(msg);
			((Dialog) dialog).setTitle(String.valueOf(numbers) + " %");
		}
	}
	
	@Override
	public void noMoreUse()
	{
		dialog.setVisible(false);
		dialog.removeAll();
		dialog = null;
	}
}
