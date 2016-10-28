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
import hjow.hgtable.ui.AskInputDialog;
import hjow.hgtable.util.StreamUtil;

import java.awt.BorderLayout;
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
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * <p>사용자로부터 여러 줄 텍스트 입력을 받는 대화 상자입니다. 모달 형태이므로 이 대화 상자가 닫힐 때까지 대부분의 작업이 잠시 중단됩니다.</p>
 * 
 * @author HJOW
 *
 */
public class SwingAskInputDialog extends AskInputDialog
{
	protected JDialog dialog;
	protected JPanel mainPanel;
	protected JTextArea inputArea;
	protected JScrollPane inputScroll;
	protected JButton btOk;
	protected JButton btCancel;
	protected JPanel downPanel;
	protected JButton btFile;
	protected JFileChooser fileChooser;
	protected JPanel messagePanel;
	protected JLabel messageLabel;

	/**
	 * <p>대화 상자를 초기화합니다.</p>
	 * 
	 * @param frame : JFrame 객체
	 * @param manager : 매니저 객체
	 */
	public SwingAskInputDialog(Frame frame, Manager manager)
	{
		super();
		this.dialog = new JDialog(frame, true);
		this.dialog.setLayout(new BorderLayout());
		
		Dimension scSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.dialog.setSize((int)(400), (int)(300));
		this.dialog.setLocation((int)(scSize.getWidth()/2 - this.dialog.getWidth()/2), (int)(scSize.getHeight()/2 - this.dialog.getHeight()/2));
		
		init();
	}
	/**
	 * <p>대화 상자를 초기화합니다.</p>
	 * 
	 * @param frame : JDialog 객체
	 * @param manager : 매니저 객체
	 */
	public SwingAskInputDialog(Dialog frame, Manager manager)
	{
		super();
		dialog = new JDialog(frame, true);
		dialog.setLayout(new BorderLayout());
		
		Dimension scSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.dialog.setSize((int)(400), (int)(300));
		this.dialog.setLocation((int)(scSize.getWidth()/2 - this.dialog.getWidth()/2), (int)(scSize.getHeight()/2 - this.dialog.getHeight()/2));
		
		init();
	}
	
	/**
	 * <p>컴포넌트들을 초기화합니다.</p>
	 * 
	 */
	protected void init()
	{
		this.mainPanel = new JPanel();
		this.mainPanel.setLayout(new BorderLayout());
		
		this.downPanel = new JPanel();
		this.mainPanel.add(this.downPanel, BorderLayout.SOUTH);
		
		this.downPanel.setLayout(new FlowLayout());
		
		this.inputArea = new JTextArea();
		this.inputScroll = new JScrollPane(inputArea);
		
		this.mainPanel.add(inputScroll, BorderLayout.CENTER);
		
		this.messagePanel = new JPanel();
		this.messagePanel.setLayout(new FlowLayout());
		this.mainPanel.add(this.messagePanel, BorderLayout.NORTH);
		
		this.messageLabel = new JLabel();
		this.messagePanel.add(this.messageLabel);
		
		this.btFile = new JButton(Manager.applyStringTable("From file..."));
		this.btOk = new JButton(Manager.applyStringTable("OK"));
		this.btCancel = new JButton(Manager.applyStringTable("Cancel"));
		this.btFile.addActionListener(this);
		this.btOk.addActionListener(this);
		this.btCancel.addActionListener(this);
		
		this.downPanel.add(this.btFile);
		this.downPanel.add(this.btOk);
		this.downPanel.add(this.btCancel);
		
		dialog.addWindowListener(this);
		dialog.add(this.mainPanel, BorderLayout.CENTER);
	}
	/**
	 * <p>대화 상자를 엽니다. 다른 작업이 일시 중단됩니다.</p>
	 * 
	 */
	public void open(String message)
	{
		this.messageLabel.setText(message);
		dialog.setVisible(true);
		inputArea.requestFocus();
	}
	/**
	 * <p>더 이상 이 객체를 사용할 필요가 없을 때 호출합니다. 순환 참조를 제거합니다. 프로그램이 종료될 때 호출됩니다.</p>
	 * 
	 */
	public void noMoreUse()
	{
		actionCancel();
	}
	
	/**
	 * <p>사용자가 OK 버튼을 눌렀을 때 호출됩니다.</p>
	 * 
	 */
	@Override
	public void actionOk()
	{
		results = inputArea.getText();
		dialog.setVisible(false);
	}
	
	/**
	 * <p>사용자가 취소 버튼을 눌렀을 때 호출됩니다.</p>
	 * 
	 */
	@Override
	public void actionCancel()
	{
		results = "";
		dialog.setVisible(false);
	}
	@Override
	public void windowClosing(WindowEvent e)
	{
		Object ob = e.getSource();
		if(ob == this)
		{
			actionCancel();
		}
	}
	@Override
	public void actionPerformed(ActionEvent e)
	{
		Object ob = e.getSource();
		if(ob == btOk)
		{
			actionOk();
		}
		else if(ob == btCancel)
		{
			actionCancel();
		}
		else if(ob == btFile)
		{
			actionFile();
		}
		
	}
	@Override
	protected void actionFile()
	{
		if(fileChooser == null)
		{
			fileChooser = new JFileChooser();
			fileChooser.setMultiSelectionEnabled(false);
		}
		
		int selects = fileChooser.showOpenDialog(dialog);
		if(selects == JFileChooser.APPROVE_OPTION)
		{
			File targets = fileChooser.getSelectedFile();
			inputArea.setText(StreamUtil.readText(targets, "UTF-8"));
		}
	}
	@Override
	public Dialog getDialog()
	{
		return dialog;
	}
}
