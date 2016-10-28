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
import hjow.hgtable.ui.GUIManager;
import hjow.hgtable.util.GUIUtil;
import hjow.hgtable.util.LicenseUtil;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/**
 * <p>이 프로그램에 대한 정보를 보여 주는 대화 상자 객체 생성에 쓰입니다.</p>
 * 
 * @author HJOW
 *
 */
public class SwingAboutDialog extends JDialog implements WindowListener, ActionListener
{
	private static final long serialVersionUID = -53668890310162648L;
	protected JPanel mainPanel;
	protected JPanel upPanel;
	protected JPanel centerPanel;
	protected JPanel downPanel;
	protected JButton btClose;
	protected JTabbedPane tab;
	protected JPanel titlePanel;
	protected JLabel titleLabel;
	protected JPanel titleLabelPanel;
	protected JLabel versionLabel;
	protected JPanel apachePOILicensePanel;
	protected JPanel apacheCommonCodecLicensePanel;
	protected JPanel mariaLicensePanel;
	protected JTextArea apachePOILicenseArea;
	protected JScrollPane apachePOILicenseScroll;
	protected JTextArea apacheCommonCodecLicenseArea;
	protected JScrollPane apacheCommonCodecLicenseScroll;
	protected JTextArea mariaLicenseArea;
	protected JScrollPane mariaLicenseScroll;
	protected JPanel titleMessagePanel;
	protected JTextArea titleMessageArea;
	protected JScrollPane titleMessageScroll;
	protected JPanel cubridLicensePanel;
	protected JTextArea cubridLicenseArea;
	protected JScrollPane cubridLicenseScroll;
	protected JButton btExitProgram;
	protected JLabel subTitleLabel;
	protected GUIManager manager;
	protected JPanel scalaLicensePanel;
	protected JTextArea scalaLicenseArea;
	protected JScrollPane scalaLicenseScroll;
		
	/**
	 * <p>기본 생성자입니다. 대화 상자 내 컴포넌트들을 초기화합니다.</p>
	 * 
	 * @param frame : JFrame 객체
	 */
	public SwingAboutDialog(JFrame frame, GUIManager manager)
	{
		super(frame, true);
		this.manager = manager;
		init();	
	}
	
	/**
	 * <p>기본 생성자입니다. 대화 상자 내 컴포넌트들을 초기화합니다.</p>
	 * 
	 * @param applets : JApplet 객체
	 */
	public SwingAboutDialog(JApplet applets, GUIManager manager)
	{
		super(SwingUtilities.windowForComponent(applets));
		this.manager = manager;
		init();	
	}
	
	private void init()
	{
		Dimension scSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setSize((int)(600), (int)(450));
		this.setLocation((int)(scSize.getWidth()/2 - this.getWidth()/2), (int)(scSize.getHeight()/2 - this.getHeight()/2));
		
		this.setTitle(Manager.getOption("program_title"));
		
		this.setLayout(new BorderLayout());
		
		mainPanel = new JPanel();
		this.add(mainPanel, BorderLayout.CENTER);
		
		upPanel = new JPanel();
		centerPanel = new JPanel();
		downPanel = new JPanel();
		
		mainPanel.setLayout(new BorderLayout());
		
		mainPanel.add(upPanel, BorderLayout.NORTH);
		mainPanel.add(centerPanel, BorderLayout.CENTER);
		mainPanel.add(downPanel, BorderLayout.SOUTH);
		
		downPanel.setLayout(new FlowLayout());
		
		btClose = new JButton(Manager.applyStringTable("OK"));
		downPanel.add(btClose);
		
		btExitProgram = new JButton(Manager.applyStringTable("I don't agree"));
		downPanel.add(btExitProgram);
		
		centerPanel.setLayout(new BorderLayout());
		
		tab = new JTabbedPane();
		centerPanel.add(tab, BorderLayout.CENTER);
		
		titlePanel = new JPanel();
		tab.add(Manager.applyStringTable("About"), titlePanel);
		
		titlePanel.setLayout(new BorderLayout());
		
		titleLabelPanel = new JPanel();
		titleLabel = new JLabel(Manager.getOption("program_title"));
		if(LicenseUtil.edition() == null) subTitleLabel = new JLabel();
		else subTitleLabel = new JLabel(LicenseUtil.edition());
		versionLabel = new JLabel(Manager.getOption("version") + ", " + Manager.getOption("made_by"));
		
		titleLabelPanel.setLayout(new FlowLayout());
		titleLabelPanel.add(titleLabel);
		titleLabelPanel.add(subTitleLabel);
		
		titleMessagePanel = new JPanel();
		titleMessagePanel.setLayout(new BorderLayout());
		titleMessageArea = new JTextArea();
		titleMessageArea.setLineWrap(true);
		titleMessageArea.setEditable(false);
		titleMessageArea.setText(Manager.applyStringTable(LicenseUtil.apacheLicenseNotices(Manager.getOption("copyright_year"), Manager.getOption("copyright_owner")))
				+ "\n\n\n" + LicenseUtil.hugeTableTitleMessage());
		titleMessageScroll = new JScrollPane(titleMessageArea);
		titleMessagePanel.add(titleMessageScroll);
		
		titlePanel.add(titleLabelPanel, BorderLayout.NORTH);
		titlePanel.add(titleMessagePanel, BorderLayout.CENTER);
		titlePanel.add(versionLabel, BorderLayout.SOUTH);
		
		apachePOILicensePanel = new JPanel();
		apacheCommonCodecLicensePanel = new JPanel();
		// gsonLicensePanel = new JPanel();
		mariaLicensePanel = new JPanel();
		cubridLicensePanel = new JPanel();
		scalaLicensePanel = new JPanel();
		
		tab.add(Manager.applyStringTable("Apache POI, Apache PDFBox, Google GSON, Sciss SyntaxPane License"), apachePOILicensePanel);
		tab.add(Manager.applyStringTable("Apache Common Codec License"), apacheCommonCodecLicensePanel);
		// tab.add(Manager.applyStringTable("Google GSON"), gsonLicensePanel);
		tab.add(Manager.applyStringTable("MariaDB JDBC License"), mariaLicensePanel);
		tab.add(Manager.applyStringTable("Cubrid JDBC License"), cubridLicensePanel);
		tab.add(Manager.applyStringTable("Scala License"), scalaLicensePanel);
		
		apachePOILicensePanel.setLayout(new BorderLayout());
		apachePOILicenseArea = new JTextArea();
		// apachePOILicenseArea.setLineWrap(true);
		apachePOILicenseArea.setEditable(false);
		apachePOILicenseArea.setText(LicenseUtil.apacheLicense2());
		apachePOILicenseScroll = new JScrollPane(apachePOILicenseArea);
		apachePOILicensePanel.add(apachePOILicenseScroll, BorderLayout.CENTER);
		
		apacheCommonCodecLicensePanel.setLayout(new BorderLayout());
		apacheCommonCodecLicenseArea = new JTextArea();
		// apacheCommonCodecLicenseArea.setLineWrap(true);
		apacheCommonCodecLicenseArea.setEditable(false);
		apacheCommonCodecLicenseArea.setText(LicenseUtil.apacheLicense());
		apacheCommonCodecLicenseScroll = new JScrollPane(apacheCommonCodecLicenseArea);
		apacheCommonCodecLicensePanel.add(apacheCommonCodecLicenseScroll, BorderLayout.CENTER);
						
		cubridLicensePanel.setLayout(new BorderLayout());
		cubridLicenseArea = new JTextArea();
		// apachePOILicenseArea.setLineWrap(true);
		cubridLicenseArea.setEditable(false);
		cubridLicenseArea.setText(LicenseUtil.bsdLicense("2011", "CUBRID co.,LTD"));
		cubridLicenseScroll = new JScrollPane(cubridLicenseArea);
		cubridLicensePanel.add(cubridLicenseScroll, BorderLayout.CENTER);
		
		scalaLicensePanel.setLayout(new BorderLayout());
		scalaLicenseArea = new JTextArea();
		// apachePOILicenseArea.setLineWrap(true);
		scalaLicenseArea.setEditable(false);
		scalaLicenseArea.setText(LicenseUtil.scalaLicense());
		scalaLicenseScroll = new JScrollPane(scalaLicenseArea);
		scalaLicensePanel.add(scalaLicenseScroll, BorderLayout.CENTER);
		
		mariaLicensePanel.setLayout(new BorderLayout());
		mariaLicenseArea = new JTextArea();
		// mysqlLicenseArea.setLineWrap(true);
		mariaLicenseArea.setEditable(false);
		mariaLicenseArea.setText(LicenseUtil.lgplLicense());
		mariaLicenseScroll = new JScrollPane(mariaLicenseArea);
		mariaLicensePanel.add(mariaLicenseScroll, BorderLayout.CENTER);
		
		btClose.addActionListener(this);
		btExitProgram.addActionListener(this);
		this.addWindowListener(this);	
	}

	/**
	 * <p>대화 상자를 닫습니다.</p>
	 */
	public void close()
	{
		this.setVisible(false);
	}
	/**
	 * <p>대화 상자를 엽니다.</p>
	 * 
	 */
	public void open()
	{
		try
		{
			titleLabel.setFont(GUIUtil.usingFont2B);
		}
		catch(Throwable e)
		{
			
		}
		this.setVisible(true);
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
	public void actionPerformed(ActionEvent e)
	{
		Object ob = e.getSource();
		if(ob == btClose)
		{
			setAgree(true);
			close();
		}
		else if(ob == btExitProgram)
		{
			setAgree(false);
			exitProgram();
		}
	}

	private void setAgree(boolean isAgree)
	{
		manager.setOption("agree_license", String.valueOf(isAgree), manager);
	}

	/**
	 * <p>프로그램을 완전히 종료합니다.</p>
	 * 
	 */
	protected void exitProgram()
	{
		close();
		manager.close();
		Main.exitAllProcess();
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
