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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Serializable;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import hjow.hgtable.Manager;
import hjow.hgtable.ui.GUIManager;

/**
 * <p>사용자에게 Map<String, String> 입력을 받는 대화 상자입니다.</p>
 * 
 * @author HJOW
 *
 */
public class SwingArgumentDialog implements Serializable
{
	private static final long serialVersionUID = 7250052491710564424L;
	public Map<String, String> result = null;
	protected SwingArgumentView view;
	protected JDialog dialog;
	protected JTextArea textArea;
	
	/**
	 * <p>대화 상자 생성자입니다. 직접 호출하는 대신 ask() 메소드 활용을 권장합니다.</p>
	 * 
	 * @param manager : GUI 매니저 객체
	 */
	public SwingArgumentDialog(GUIManager manager)
	{
		dialog = new JDialog(manager.getFrame(), true);
		
		Dimension scSize = Toolkit.getDefaultToolkit().getScreenSize();
		dialog.setSize(450, 350);
		dialog.setLocation((int)(scSize.getWidth() / 2 - dialog.getWidth() / 2), (int)(scSize.getHeight() / 2 - dialog.getHeight() / 2));
		
		dialog.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				result = null;
				close();
			}
		});
		
		dialog.setLayout(new BorderLayout());
		
		JPanel mainPanel = new JPanel();
		dialog.add(mainPanel, BorderLayout.CENTER);
		
		mainPanel.setLayout(new BorderLayout());
		
		textArea = new JTextArea();
		textArea.setEditable(false);
		mainPanel.add(new JScrollPane(textArea), BorderLayout.NORTH);
		
		view = new SwingArgumentView();
		mainPanel.add(view, BorderLayout.CENTER);
		
		JPanel downPanel = new JPanel();
		mainPanel.add(downPanel, BorderLayout.SOUTH);
		
		JButton btOk     = new JButton(Manager.applyStringTable("OK"));
		JButton btCancel = new JButton(Manager.applyStringTable("Cancel"));
		
		btOk.addActionListener(new ActionListener()
		{	
			@Override
			public void actionPerformed(ActionEvent e)
			{
				result = view.getMap();
				close();
			}
		});
		
		btCancel.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				result = null;
				close();
			}
		});
		
		downPanel.setLayout(new FlowLayout());
		downPanel.add(btOk);
		downPanel.add(btCancel);
	}
	
	protected void open()
	{
		dialog.setVisible(true);
	}
	
	protected void close()
	{
		dialog.setVisible(false);
	}
	
	/**
	 * <p>대화 상자를 열고 사용자에게 맵 입력을 받습니다. 대화 상자가 열려 있는 동안 다른 GUI 활동이 일시 정지됩니다.</p>
	 * 
	 * @param manager : GUI 매니저 객체
	 * @param msg : 사용자에게 보일 메시지
	 * @return 맵 객체
	 */
	public static Map<String, String> ask(GUIManager manager, String msg, Map<String, String> befores)
	{
		SwingArgumentDialog dialog = new SwingArgumentDialog(manager);
		if(befores != null) dialog.view.setMap(befores);
		dialog.textArea.setText(msg);
		dialog.open();
		return dialog.result;
	}
}
