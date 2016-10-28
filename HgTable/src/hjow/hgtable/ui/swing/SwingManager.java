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

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;

import hjow.hgtable.HThread;
import hjow.hgtable.Main;
import hjow.hgtable.Manager;
import hjow.hgtable.NotConnectedException;
import hjow.hgtable.ParameterRunnable;
import hjow.hgtable.classload.ClassTool;
import hjow.hgtable.dao.Dao;
import hjow.hgtable.favorites.AbstractFavorites;
import hjow.hgtable.jscript.SpecialOrder;
import hjow.hgtable.jscript.module.AbstractModule;
import hjow.hgtable.jscript.module.Module;
import hjow.hgtable.tableset.Record;
import hjow.hgtable.tableset.TableSet;
import hjow.hgtable.ui.AskInputDialog;
import hjow.hgtable.ui.DbToFileManager;
import hjow.hgtable.ui.FileToDbManager;
import hjow.hgtable.ui.GUIManager;
import hjow.hgtable.ui.MenuMatch;
import hjow.hgtable.ui.module.GUIConnectModule;
import hjow.hgtable.ui.module.GUIDialogModule;
import hjow.hgtable.ui.module.GUIModule;
import hjow.hgtable.ui.module.GUIPanelModule;
import hjow.hgtable.ui.module.GUIToolbarModule;
import hjow.hgtable.ui.module.ModuleOnMenu;
import hjow.hgtable.ui.module.ScriptModule;
import hjow.hgtable.ui.view.SwingCreateTable;
import hjow.hgtable.ui.view.SwingFunctionListView;
import hjow.hgtable.ui.view.SwingModuleListView;
import hjow.hgtable.ui.view.SwingProcedureListView;
import hjow.hgtable.ui.view.SwingTableListView;
import hjow.hgtable.ui.view.SwingViewListView;
import hjow.hgtable.util.ConnectUtil;
import hjow.hgtable.util.DataUtil;
import hjow.hgtable.util.GUIUtil;
import hjow.hgtable.util.InvalidInputException;
import hjow.hgtable.util.JSONUtil;
import hjow.hgtable.util.LicenseUtil;
import hjow.hgtable.util.ModuleUtil;
import hjow.hgtable.util.ScriptUtil;
import hjow.hgtable.util.SpecialOrderUtil;
import hjow.hgtable.util.StreamUtil;
import hjow.hgtable.util.XLSXUtil;
import hjow.state.FirstStateView;

/**
 * <p>GUI 환경의 매니저 객체입니다.</p>
 * 
 * @author HJOW
 *
 */
public class SwingManager extends GUIManager implements TableModelListener
{
	private static final long serialVersionUID = -7099620417986373647L;
	protected JFrame frame;
	protected ManagerApplet applets;
	protected Container masterContainer;
	protected JPanel mainPanel;
	protected JPanel upPanel;
	protected JPanel centerPanel;
	// protected JPanel leftPanel;
	protected JPanel downPanel;
	protected JPanel rightPanel;
	protected JPanel dbAccessStatusPanel;
	protected JPanel dbAccessControlPanel;
	protected JSplitPane scriptSplit;
	protected SwingScriptView scriptArea;
	protected JPanel resultPanel;
	protected JTable resultTable;
	protected JScrollPane resultScroll;
	protected DefaultTableModel resultTableModel;
	protected JPanel scriptPanel;
	protected JPanel scriptControlPanel;
	protected JButton btRunScript;
	protected JButton btCancelScript;
	protected JPanel processPanel;
	protected JProgressBar processBar;
	protected UIComboBox accessCombo;
	protected JButton btConnect;
	protected JPanel[] dbAccessControlPns;
	protected JLabel urlLabel;
	protected UIComboBox urlField;
	protected JLabel idLabel;
	protected UIComboBox idField;
	protected JLabel pwLabel;
	protected JPasswordField pwField;
	protected JPanel dbAccessControlGrid;
	protected JTextArea resultArea;
	protected JTabbedPane resultTab;
	protected AskInputDialog inputDialog;
	protected JScrollPane resultTableScroll;
	protected UIComboBox runCombo;
	protected UIComboBox runCombo2;
	protected Vector<String> runComboElements;
	protected JButton btDisconnect;
	protected JMenuBar menuBar;
	protected JButton btCommit;
	protected JButton btRollback;
	protected JMenu menuFile;
	protected JLabel classLabel;
	protected UIComboBox classField;
	protected JMenuItem menuFileExit;
	protected JMenuItem menuFileRestart;
	protected JMenu menuTool;
	protected JMenuItem menuToolDbToFile;
	protected JMenuItem menuToolFileToDb;
	protected DbToFileManager dbToFileManager;
	protected FileToDbManager fileToDbManager;
	protected JPanel resultTablePanel;
	protected JPanel resultTableControlPanel;
	protected JButton btSave;
	protected JFileChooser fileChooser;
	protected FileFilter xlsxFileFilter;
	protected FileFilter jsonFileFilter;
	protected FileFilter hgfFileFilter;
	protected JPanel resultAreaPanel;
	protected JPanel onelinePanel;
	protected JTextField onelineField;
	protected JButton btRunOneLine;
	protected JButton btLoad;
	protected JButton btToDb;
	protected JMenu menuHelp;
	protected JMenu menuFav;
	protected JMenuItem menuAbout;
	protected SwingAboutDialog aboutDialog;
	protected JFileChooser textFileChooser;
	protected FileFilter textFileFilter;
	protected JMenuItem menuFileSaveConsole;
	protected JMenuItem menuTransRun;
	protected JMenu menuTrans;
	protected JMenuItem menuTransCommit;
	protected JMenuItem menuTransRollback;
	protected SwingTableListView tableListView;
	protected JMenu menuView;
	protected JMenuItem menuViewTableList;
	protected JTextField statusField;
	protected JButton btHideOrShow;
	protected JPanel dbAccessButtonPanel;
	protected JMenuItem menuToolPreference;
	protected SwingPreferenceEditor preferenceView;
	protected JMenuItem menuFileLoadJar;
	protected JFileChooser jarFileChooser;
	protected FileFilter jarFileFilter;
	protected boolean runComboInitialized;
	protected boolean accessComboLocked;
	protected transient ModuleOnMenu tempModule;
	protected JMenuItem menuToolUndo;
	protected JMenuItem menuToolRedo;
	protected JMenuItem menuTransRunSelected;
	protected SwingViewListView viewListView;
	protected JMenuItem menuViewViewList;
	protected JMenuItem menuViewProcedureList;
	protected JMenuItem menuViewFunctionList;
	protected SwingProcedureListView procedureListView;
	protected SwingFunctionListView functionListView;
	protected int afterIndex;
	protected JMenuItem menuFileSave;
	protected JMenuItem menuFileLoad;
	protected JFileChooser loadFileChooser;
	protected transient Vector<String> newClassList = new Vector<String>();
	protected transient Vector<String> newUrlList = new Vector<String>();
	protected transient Vector<String> newIdList = new Vector<String>();
	protected boolean showScriptErrorSimply = true;
	protected JMenuItem menuFileNewTab;
	protected JMenuItem menuViewSearch;
	protected JMenuItem menuToolCreateTable;
	protected SwingCreateTable createTableView;
	protected List<JMenu> additionalMenu = new Vector<JMenu>();
	protected transient Dao newDao;
	protected transient List<MenuMatch> menuMatching = new Vector<MenuMatch>();
	protected SwingModuleInfoDialog licenseView;
	protected SwingModuleListView moduleListView;
	protected JButton btExit;
	protected List<SwingTableSetView> tableSetViews = new Vector<SwingTableSetView>();
	protected FileFilter sqlFileFilter;
	protected FileFilter jsFileFilter;
	protected JFileChooser saveFileChooser;
	protected SwingAnalyseFunctionDialog functionDialog;
	protected JMenuItem menuToolFunction;
	protected UITimerView timerView;
	protected JPanel scriptButtonPanel;
	protected JPanel timerPanel;
	protected JPanel scriptTransactionPanel;
	protected JPanel scriptDownPanel;
	protected JPanel toolbarPanel;
	protected JMenuItem menuToolModules;
	protected JTabbedPane dbAccessControlTab;
	protected JPanel dbAccessControlJdbcPanel;
	
	/**
	 * <p>기본 생성자입니다. 기존 매니저 객체의 초기화 과정을 거칩니다.</p>
	 * 
	 */
	public SwingManager()
	{
		super();
	}
	@Override
	protected void initComponents()
	{
		try
		{
			frameInit(false);
			
			FirstStateView.set(52);
			
			mainPanel = new JPanel();
			masterContainer.add(mainPanel, BorderLayout.CENTER);
			
			mainPanel.setLayout(new BorderLayout());
			
			upPanel = new JPanel();
			centerPanel = new JPanel();
			downPanel = new JPanel();
			// leftPanel = new JPanel();
			rightPanel = new JPanel();
			
			mainPanel.add(upPanel, BorderLayout.NORTH);
			mainPanel.add(centerPanel, BorderLayout.CENTER);
			mainPanel.add(downPanel, BorderLayout.SOUTH);
			// mainPanel.add(leftPanel, BorderLayout.WEST);
			mainPanel.add(rightPanel, BorderLayout.EAST);
			
			upPanel.setLayout(new BorderLayout());
			
			dbAccessStatusPanel = new JPanel();
			dbAccessControlPanel = new JPanel();
			
			upPanel.add(dbAccessStatusPanel, BorderLayout.NORTH);
			upPanel.add(dbAccessControlPanel, BorderLayout.CENTER);
			
			dbAccessStatusPanel.setLayout(new BorderLayout());	
			
			accessCombo = new UIComboBox();
			btDisconnect = new JButton(applyStringTable("Disconnect"));
			btHideOrShow = new JButton("▲");
			
			dbAccessStatusPanel.add(accessCombo, BorderLayout.CENTER);
			
			dbAccessButtonPanel = new JPanel();		
			dbAccessStatusPanel.add(dbAccessButtonPanel, BorderLayout.EAST);
			
			dbAccessButtonPanel.setLayout(new BorderLayout());
			dbAccessButtonPanel.add(btDisconnect, BorderLayout.CENTER);
			dbAccessButtonPanel.add(btHideOrShow, BorderLayout.EAST);
			
			FirstStateView.set(53);
			
			// prepareConnectPanel();
			
			centerPanel.setLayout(new BorderLayout());
			
			scriptSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
			centerPanel.add(scriptSplit, BorderLayout.CENTER);
			
			scriptPanel = new JPanel();
			scriptPanel.setLayout(new BorderLayout());
			
			FirstStateView.set(55);
			
			scriptArea = new SwingScriptView(this);
			
			FirstStateView.set(58);
			
			scriptPanel.add(scriptArea, BorderLayout.CENTER);
			
			scriptDownPanel = new JPanel();
			scriptDownPanel.setLayout(new BorderLayout());
			scriptPanel.add(scriptDownPanel, BorderLayout.NORTH);
			
			scriptControlPanel = new JPanel();
			scriptControlPanel.setLayout(new BorderLayout());
			scriptDownPanel.add(scriptControlPanel, BorderLayout.NORTH);
			
			toolbarPanel = new JPanel();
			toolbarPanel.setLayout(new BorderLayout());
			scriptDownPanel.add(toolbarPanel, BorderLayout.CENTER);
			
			scriptButtonPanel = new JPanel();
			scriptButtonPanel.setLayout(new FlowLayout());
			scriptControlPanel.add(scriptButtonPanel, BorderLayout.CENTER);
			
			scriptTransactionPanel = new JPanel();
			scriptTransactionPanel.setLayout(new FlowLayout());
			scriptControlPanel.add(scriptTransactionPanel, BorderLayout.WEST);
			
			FirstStateView.set(59);
			
			// TODO
			runComboElements = new Vector<String>();
			runComboElements.add("SQL");
			runComboElements.add("JScript");
			runComboElements.add("JSON");
			runComboElements.add("HGF");
			
			Vector<String> runComboContents = new Vector<String>();
			Vector<String> runComboContents2 = new Vector<String>();
			for(int i=0; i<runComboElements.size(); i++)
			{
				runComboContents.add(applyStringTable(runComboElements.get(i)));
				runComboContents2.add(applyStringTable(runComboElements.get(i)));
			}
			
			runCombo = new UIComboBox(runComboContents);
			runCombo2 = new UIComboBox(runComboContents2);
			
			setAdditionalScriptItem();
			
			scriptButtonPanel.add(runCombo);
			
			btRunScript = new JButton(applyStringTable("Run"));
			btCancelScript = new JButton(applyStringTable("Cancel"));
			
			scriptButtonPanel.add(btRunScript);
			
			btCommit = new JButton(applyStringTable("Commit"));
			btRollback = new JButton(applyStringTable("Rollback"));
			scriptTransactionPanel.add(btCommit);
			scriptTransactionPanel.add(btRollback);
			
			scriptSplit.setTopComponent(scriptPanel);
			
			resultPanel = new JPanel();
			resultPanel.setLayout(new BorderLayout());
			scriptSplit.setBottomComponent(resultPanel);
			
			resultTab = new JTabbedPane();
			
			resultAreaPanel = new JPanel();
			resultArea = new JTextArea();
			resultArea.setLineWrap(true);
			resultArea.setEditable(false);
			resultScroll = new JScrollPane(resultArea);
			resultAreaPanel.setLayout(new BorderLayout());
			resultAreaPanel.add(resultScroll, BorderLayout.CENTER);
			onelinePanel = new JPanel();
			onelinePanel.setLayout(new BorderLayout());
			resultAreaPanel.add(onelinePanel, BorderLayout.SOUTH);
			onelineField = new JTextField();
			btRunOneLine = new JButton(applyStringTable("Run single line"));
			onelinePanel.add(runCombo2, BorderLayout.WEST);
			onelinePanel.add(onelineField, BorderLayout.CENTER);
			onelinePanel.add(btRunOneLine, BorderLayout.EAST);
			resultTab.add(applyStringTable("Console"), resultAreaPanel);
			
			FirstStateView.set(60);
			
			resultTablePanel = new JPanel();		
			resultTableModel = new DefaultTableModel();
			resultTableModel.addTableModelListener(this);
			resultTable = new JTable(resultTableModel);
			// resultTable.setEnabled(false);
			resultTableScroll = new JScrollPane(resultTable);
			resultTablePanel.setLayout(new BorderLayout());
			resultTablePanel.add(resultTableScroll, BorderLayout.CENTER);
			resultTableControlPanel = new JPanel();
			resultTablePanel.add(resultTableControlPanel, BorderLayout.SOUTH);		
			resultTab.add(applyStringTable("TableSet"), resultTablePanel);
			
			resultTableControlPanel.setLayout(new FlowLayout());
			
			btSave = new JButton(applyStringTable("Save"));
			btLoad = new JButton(applyStringTable("Load"));
			btToDb = new JButton(applyStringTable("Insert this to DB"));
			resultTableControlPanel.add(btSave);
			resultTableControlPanel.add(btLoad);
			resultTableControlPanel.add(btToDb);
			
			FirstStateView.set(61);
			
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
					else if(pathname.isDirectory()) return true;
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
					else if(f.isDirectory()) return true;
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
					else if(f.isDirectory()) return true;
					return false;
				}
			};
			
			textFileChooser = new JFileChooser();
			textFileChooser.setMultiSelectionEnabled(false);
			textFileFilter = new FileFilter()
			{			
				@Override
				public String getDescription()
				{
					return Manager.applyStringTable("Text File (*.txt)");
				}
				
				@Override
				public boolean accept(File f)
				{
					return (f.getAbsolutePath().endsWith(".txt") || f.getAbsolutePath().endsWith(".TXT") || f.getAbsolutePath().endsWith(".Txt"));
				}
			};
			textFileChooser.setFileFilter(textFileFilter);
			
			loadFileChooser = new JFileChooser();
			loadFileChooser.setMultiSelectionEnabled(false);
			
			sqlFileFilter = new FileFilter()
			{			
				@Override
				public String getDescription()
				{
					return Manager.applyStringTable("SQL File (*.sql)");
				}
				
				@Override
				public boolean accept(File f)
				{
					return (f.getAbsolutePath().endsWith(".sql") || f.getAbsolutePath().endsWith(".SQL") || f.getAbsolutePath().endsWith(".Sql"));
				}
			};
			
			jsFileFilter = new FileFilter()
			{			
				@Override
				public String getDescription()
				{
					return Manager.applyStringTable("JScript File (*.js)");
				}
				
				@Override
				public boolean accept(File f)
				{
					return (f.getAbsolutePath().endsWith(".js") || f.getAbsolutePath().endsWith(".JS") || f.getAbsolutePath().endsWith(".Js"));
				}
			};
			
			loadFileChooser.addChoosableFileFilter(sqlFileFilter);
			loadFileChooser.addChoosableFileFilter(jsFileFilter);
			loadFileChooser.addChoosableFileFilter(hgfFileFilter);
			loadFileChooser.addChoosableFileFilter(xlsxFileFilter);
			loadFileChooser.addChoosableFileFilter(jsonFileFilter);
			loadFileChooser.setFileFilter(jsFileFilter);
			
			saveFileChooser = new JFileChooser();
			saveFileChooser.setMultiSelectionEnabled(false);
			
			saveFileChooser.addChoosableFileFilter(sqlFileFilter);
			saveFileChooser.addChoosableFileFilter(jsFileFilter);
			
			jarFileChooser = new JFileChooser();
			jarFileChooser.setMultiSelectionEnabled(false);
			
			jarFileFilter = new FileFilter()
			{			
				@Override
				public String getDescription()
				{
					return Manager.applyStringTable("Java Archive (*.jar)");
				}
				
				@Override
				public boolean accept(File f)
				{
					return (f.getAbsolutePath().endsWith(".jar") || f.getAbsolutePath().endsWith(".JAR") || f.getAbsolutePath().endsWith(".Jar"));
				}
			};
			jarFileChooser.setFileFilter(jarFileFilter);
			
			fileChooser.setFileFilter(xlsxFileFilter);
			fileChooser.addChoosableFileFilter(jsonFileFilter);
			fileChooser.addChoosableFileFilter(hgfFileFilter);
			
			FirstStateView.set(62);
			
			resultPanel.add(resultTab, BorderLayout.CENTER);
			
			rightPanel.setLayout(new BorderLayout());
			
			downPanel.setLayout(new BorderLayout());
			
			processPanel = new JPanel();
			processPanel.setLayout(new BorderLayout());
			processBar = new JProgressBar(JProgressBar.HORIZONTAL, 0, 100);
			processPanel.add(processBar, BorderLayout.CENTER);
			
			downPanel.add(processPanel, BorderLayout.WEST);
			
			statusField = new JTextField();
			statusField.setEditable(false);
			
			downPanel.add(statusField, BorderLayout.CENTER);
			
			btExit = new JButton(applyStringTable("Exit"));
			
			downPanel.add(btExit, BorderLayout.EAST);
			
			FirstStateView.set(63);
			
			menuBar = new JMenuBar(); 
			
			if(masterContainer instanceof JFrame)
			{
				frame.setJMenuBar(menuBar);
			}
			else if(masterContainer instanceof ManagerApplet)
			{
				applets.setJMenuBar(menuBar);
			}
				
			menuFile = new JMenu(applyStringTable("File"));
			menuBar.add(menuFile);
			
			menuFileNewTab = new JMenuItem(applyStringTable("New"));
			
			try
			{
				if(DataUtil.parseBoolean(getOption("use_multi_tab")))
				{
					menuFile.add(menuFileNewTab);
					menuFile.addSeparator();
				}
			}
			catch (InvalidInputException e)
			{
				
			}
			
			menuFileSave = new JMenuItem(applyStringTable("Save"));
			menuFileSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
			menuFile.add(menuFileSave);
			
			menuFileLoad = new JMenuItem(applyStringTable("Load"));
			menuFileLoad.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.CTRL_MASK));
			menuFile.add(menuFileLoad);
			
			menuFile.addSeparator();
			
			menuFileSaveConsole = new JMenuItem(applyStringTable("Save console contents"));
			menuFileSaveConsole.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, ActionEvent.ALT_MASK));
			menuFile.add(menuFileSaveConsole);
			
			menuFileLoadJar = new JMenuItem(applyStringTable("Add JAR entry"));
			menuFile.add(menuFileLoadJar);
			
			menuFile.addSeparator();
			
			menuFileRestart = new JMenuItem(applyStringTable("Restart"));
			menuFile.add(menuFileRestart);
			menuFileRestart.setVisible(DataUtil.parseBoolean(getOption("use_testing_functions")) || System.getProperty("os.name").toLowerCase().indexOf("windows") >= 0);
			
			menuFileExit = new JMenuItem(applyStringTable("Exit"));
			menuFileExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, ActionEvent.ALT_MASK));
			menuFile.add(menuFileExit);
			
			menuTool = new JMenu(applyStringTable("Tool"));
			menuBar.add(menuTool);
			
			menuToolUndo = new JMenuItem(applyStringTable("Undo"));
			menuToolUndo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
			menuTool.add(menuToolUndo);
			
			menuToolRedo = new JMenuItem(applyStringTable("Redo"));
			menuToolRedo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK));
			menuTool.add(menuToolRedo);
			
			menuTool.addSeparator();
			
			menuToolCreateTable = new JMenuItem(applyStringTable("Create Table"));
			menuTool.add(menuToolCreateTable);
			
			menuTool.addSeparator();
			
			menuToolDbToFile = new JMenuItem(applyStringTable("DB --> file"));
			menuToolDbToFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F7, ActionEvent.CTRL_MASK));
			menuTool.add(menuToolDbToFile);
			
			menuToolFileToDb = new JMenuItem(applyStringTable("file --> DB"));
			menuToolFileToDb.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F8, ActionEvent.CTRL_MASK));
			menuTool.add(menuToolFileToDb);
			
			menuTool.addSeparator();
			
			menuToolFunction = new JMenuItem(applyStringTable("Analyze Function"));
			menuTool.add(menuToolFunction);
			
			menuTool.addSeparator();
			
			menuToolPreference = new JMenuItem(applyStringTable("Preference"));
			menuToolPreference.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F12, ActionEvent.CTRL_MASK));
			menuTool.add(menuToolPreference);
			
			menuToolModules = new JMenuItem(applyStringTable("Module List"));
			menuTool.add(menuToolModules);
			
			menuTrans = new JMenu(applyStringTable("Transaction"));
			menuBar.add(menuTrans);
			
			menuTransRun = new JMenuItem(applyStringTable("Run"));
			menuTransRun.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
			menuTrans.add(menuTransRun);
			
			menuTransRunSelected = new JMenuItem(applyStringTable("Run Selected Script"));
			menuTransRunSelected.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, ActionEvent.SHIFT_MASK));
			menuTrans.add(menuTransRunSelected);
			
			menuTrans.addSeparator();
			
			menuTransCommit = new JMenuItem(applyStringTable("Commit"));
			menuTransCommit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F9, ActionEvent.CTRL_MASK));
			menuTrans.add(menuTransCommit);
			
			menuTransRollback = new JMenuItem(applyStringTable("Rollback"));
			menuTransRollback.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F10, ActionEvent.CTRL_MASK));
			menuTrans.add(menuTransRollback);
			
			menuView = new JMenu(applyStringTable("View"));
			menuBar.add(menuView);
			
			menuViewSearch = new JMenuItem(applyStringTable("Search"));
			menuViewSearch.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK));
			menuView.add(menuViewSearch);
			
			menuView.addSeparator();
			
			menuViewTableList = new JMenuItem(applyStringTable("Table List"));
			menuViewTableList.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F9, ActionEvent.ALT_MASK));
			menuView.add(menuViewTableList);
			
			menuViewViewList = new JMenuItem(applyStringTable("View List"));
			menuViewViewList.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F10, ActionEvent.ALT_MASK));
			menuView.add(menuViewViewList);
			
			menuViewProcedureList = new JMenuItem(applyStringTable("Procedure List"));
			menuViewProcedureList.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F11, ActionEvent.ALT_MASK));
			menuView.add(menuViewProcedureList);
			
			menuViewFunctionList = new JMenuItem(applyStringTable("Function List"));
			menuViewFunctionList.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F12, ActionEvent.ALT_MASK));
			menuView.add(menuViewFunctionList);
			
			menuHelp = new JMenu(applyStringTable("Help"));
			menuBar.add(menuHelp);
			
			menuAbout = new JMenuItem(applyStringTable("About..."));
			menuAbout.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, ActionEvent.CTRL_MASK));
			menuHelp.add(menuAbout);
			
			inputDialog = new SwingAskInputDialog(frame, this);      FirstStateView.set(64);
			dbToFileManager = new SwingDbToFileManager(this, frame); FirstStateView.set(65);
			fileToDbManager = new SwingFileToDbManager(this, frame); FirstStateView.set(66);
			
			aboutDialog = new SwingAboutDialog(frame, this);         FirstStateView.set(67);
			tableListView = new SwingTableListView(frame, this);     FirstStateView.set(68);
			viewListView = new SwingViewListView(frame, this);       FirstStateView.set(69);
			
			procedureListView = new SwingProcedureListView(frame, this); FirstStateView.set(70);
			functionListView = new SwingFunctionListView(frame, this);   FirstStateView.set(71);
			preferenceView = new SwingPreferenceEditor(frame, this);     FirstStateView.set(72);
			createTableView = new SwingCreateTable(frame, this);         FirstStateView.set(73);
			
			licenseView = new SwingModuleInfoDialog(this);
			functionDialog = new SwingAnalyseFunctionDialog(this);
			moduleListView = new SwingModuleListView(this);
			
			if(DataUtil.parseBoolean(getOption("use_gui_timer"))) timerView = new UITimerView(25);
			
			if(DataUtil.isNotEmpty(getOption("gui_timer_gap")) && timerView != null) timerView.setGap(getOption("gui_timer_gap"));
			if(DataUtil.isNotEmpty(getOption("gui_timer_acting_gap")) && timerView != null) timerView.setForcedGap(getOption("gui_timer_acting_gap"));
			
			timerPanel = new JPanel();
			timerPanel.setLayout(new FlowLayout());
			if(timerView != null) timerPanel.add(timerView);
			scriptControlPanel.add(timerPanel, BorderLayout.EAST);
			
			needToEnds.add(inputDialog);
			needToEnds.add(dbToFileManager);
			needToEnds.add(fileToDbManager);
			needToEnds.add(tableListView);
			needToEnds.add(viewListView);
			needToEnds.add(procedureListView);
			needToEnds.add(functionListView);
			needToEnds.add(preferenceView);
			needToEnds.add(createTableView);
			needToEnds.add(licenseView);
			needToEnds.add(moduleListView);
			
			FirstStateView.set(74);
					
			frame.addWindowListener(this);
			accessCombo.addItemListener(this);
			runCombo.addItemListener(this);
			btRunScript.addActionListener(this);
			btCancelScript.addActionListener(this);
			btDisconnect.addActionListener(this);
			btCommit.addActionListener(this);
			btRollback.addActionListener(this);
			btSave.addActionListener(this);
			btLoad.addActionListener(this);
			btToDb.addActionListener(this);
			btRunOneLine.addActionListener(this);
			btHideOrShow.addActionListener(this);
			btExit.addActionListener(this);
			onelineField.addActionListener(this);
			menuFileNewTab.addActionListener(this);
			menuFileSave.addActionListener(this);
			menuFileLoad.addActionListener(this);
			menuFileSaveConsole.addActionListener(this);
			menuFileLoadJar.addActionListener(this);
			menuFileRestart.addActionListener(this);
			menuFileExit.addActionListener(this);
			menuToolUndo.addActionListener(this);
			menuToolRedo.addActionListener(this);
			menuToolCreateTable.addActionListener(this);
			menuToolDbToFile.addActionListener(this);
			menuToolFileToDb.addActionListener(this);
			menuToolPreference.addActionListener(this);
			menuToolModules.addActionListener(this);
			menuToolFunction.addActionListener(this);
			menuTransRun.addActionListener(this);
			menuTransRunSelected.addActionListener(this);
			menuTransCommit.addActionListener(this);
			menuTransRollback.addActionListener(this);
			menuViewSearch.addActionListener(this);
			menuViewTableList.addActionListener(this);
			menuViewViewList.addActionListener(this);
			menuViewProcedureList.addActionListener(this);
			menuViewFunctionList.addActionListener(this);
			menuAbout.addActionListener(this);
			
			FirstStateView.set(75);
			
			refreshUndoing();
			
			FirstStateView.set(76);
			
			if(eventHandler != null) eventHandlerCleaner.start();
		}
		catch(Throwable e)
		{
			if(Main.checkInterrupt(this, "On initComponent")) logError(e, applyStringTable("On initComponent"));
			else close();
		}
	}
	
	/**
	 * 데이터 소스 접속에 사용되는 컴포넌트들을 준비합니다.
	 */
	protected void prepareConnectPanel()
	{
		dbAccessControlPanel.removeAll();
		dbAccessControlPanel.setLayout(new BorderLayout());
		
		if(dbAccessControlTab == null) dbAccessControlTab = new JTabbedPane();
		else dbAccessControlTab.removeAll();
		dbAccessControlTab.setMaximumSize(new Dimension(8192, 100));
		dbAccessControlTab.setMinimumSize(new Dimension(50, 10));
		dbAccessControlPanel.add(dbAccessControlTab);
		
		dbAccessControlJdbcPanel = new JPanel();
		dbAccessControlJdbcPanel.setLayout(new BorderLayout());
		
		dbAccessControlTab.add(applyStringTable("JDBC Direct"), dbAccessControlJdbcPanel);
		
		dbAccessControlGrid = new JPanel();
		dbAccessControlJdbcPanel.add(dbAccessControlGrid, BorderLayout.CENTER);
		
		dbAccessControlPns = new JPanel[4];
		dbAccessControlGrid.setLayout(new GridLayout(dbAccessControlPns.length, 1));
		
		for(int i=0; i<dbAccessControlPns.length; i++)
		{
			dbAccessControlPns[i] = new JPanel();
			dbAccessControlPns[i].setLayout(new BorderLayout());
			dbAccessControlGrid.add(dbAccessControlPns[i]);
		}
		
		Vector<String> basicSupportsClasses = new Vector<String>();
		basicSupportsClasses.add("oracle.jdbc.driver.OracleDriver");
		basicSupportsClasses.add("org.mariadb.jdbc.Driver");
		basicSupportsClasses.add("cubrid.jdbc.driver.CUBRIDDriver");
		basicSupportsClasses.add("org.h2.Driver");
		basicSupportsClasses.add("org.postgresql.Driver");
		
		classLabel = new JLabel(applyStringTable("Driver or Data Source name"));
		classField = new UIComboBox(basicSupportsClasses);
		classField.setEditable(true);
		
		String[] urlFieldAssists = new String[5];
		urlFieldAssists[0] = "jdbc:oracle:thin:@" + "[" + applyStringTable("IP") + "]" + ":" + "[" + applyStringTable("Port") + "]" + "/" + "[" + applyStringTable("SID") + "]";
		urlFieldAssists[1] = "jdbc:mariadb://" + "[" + applyStringTable("IP") + "]" + ":" + "[" + applyStringTable("Port") + "]" + "/" + "[" + applyStringTable("SID") + "]";
		urlFieldAssists[2] = "jdbc:cubrid:" + "[" + applyStringTable("IP") + "]" + ":" + "[" + applyStringTable("Port") + "]" + ":" + "[" + applyStringTable("SID") + "]" + ":::";
		urlFieldAssists[3] = "jdbc:h2:" + "[" + applyStringTable("IP") + "]" + ":" + "[" + applyStringTable("Port") + "]" + "/" + "[" + applyStringTable("SID") + "]";
		urlFieldAssists[4] = "jdbc:postgresql://" + "[" + applyStringTable("IP") + "]" + ":" + "[" + applyStringTable("Port") + "]" + "/" + "[" + applyStringTable("SID") + "]";
		
		urlLabel = new JLabel(applyStringTable("URL"));
		urlField = new UIComboBox(urlFieldAssists);
		urlField.setEditable(true);
		
		idLabel = new JLabel(applyStringTable("ID"));
		idField = new UIComboBox();
		idField.setEditable(true);
		
		pwLabel = new JLabel(applyStringTable("Password"));
		
		if(pwField != null) pwField.removeActionListener(this);
		pwField = new JPasswordField();
		
		pwField.addActionListener(this);
		
		dbAccessControlPns[0].add(classLabel, BorderLayout.WEST);
		dbAccessControlPns[0].add(classField, BorderLayout.CENTER);
		dbAccessControlPns[1].add(urlLabel, BorderLayout.WEST);
		dbAccessControlPns[1].add(urlField, BorderLayout.CENTER);
		dbAccessControlPns[2].add(idLabel, BorderLayout.WEST);
		dbAccessControlPns[2].add(idField, BorderLayout.CENTER);
		dbAccessControlPns[3].add(pwLabel, BorderLayout.WEST);
		dbAccessControlPns[3].add(pwField, BorderLayout.CENTER);
				
		if(btConnect != null) btConnect.removeActionListener(this);
		btConnect = new JButton(applyStringTable("Connect"));
		btConnect.addActionListener(this);
		
		dbAccessControlJdbcPanel.add(btConnect, BorderLayout.EAST);
		
		for(int i=0; i<modules.size(); i++)
		{
			if(modules.get(i) instanceof GUIConnectModule)
			{
				dbAccessControlTab.add(modules.get(i).getName(), ((GUIConnectModule) modules.get(i)).getComponent());
			}
		}
	}
	/**
	 * <p>추가로 사용 가능한 스크립트 엔진 이름을 실행 모드 콤보박스에 추가합니다.</p>
	 * 
	 */
	protected void setAdditionalScriptItem()
	{
		for(String e : ScriptUtil.getPreparedScriptNames())
		{
			runComboElements.add(e);
			runCombo.addItem(applyStringTable(e));
			runCombo2.addItem(applyStringTable(e));
		}
	}
	
	/**
	 * <p>창을 초기화합니다.</p>
	 * 
	 * @param isApplet : true 인 경우 애플릿으로 동작 (완전하지 않음)
	 */
	private void frameInit(boolean isApplet)
	{
		if(isApplet)
		{
			applets = new ManagerApplet();
		}
		else
		{
			frame = new JFrame();		
			frame.setTitle(params.get("program_title"));
			
			masterContainer = frame;
			
			Dimension scSize = Toolkit.getDefaultToolkit().getScreenSize();
			
			int wid, hei;
			
			wid = (int)(scSize.getWidth() - 200);
			hei = (int)(scSize.getHeight() - 200);
			
			if(wid < 600) wid = 600;
			if(hei < 400) hei = 400;
			
			frame.setSize(wid, hei);
			frame.setLocation((int)(scSize.getWidth()/2 - frame.getWidth()/2), (int)(scSize.getHeight()/2 - frame.getHeight()/2));
			
			frame.getContentPane().setLayout(new BorderLayout());
		}
	}
	@Override
	public void refreshDaos(boolean controlCompAfter, boolean setComponentSelectBefores)
	{	
		long beforeSelectedDaoId = -1;
		if(accessCombo.getSelectedIndex() >= 0)
		{
			beforeSelectedDaoId = daos.get(accessCombo.getSelectedIndex()).getDaoId();
		}
		
		cleanDaos();
		
		SwingUtilities.invokeLater(new Runnable()
		{			
			@Override
			public void run()
			{
				accessCombo.removeAllItems();
				for(int i=0; i<daos.size(); i++)
				{
					accessCombo.addItem(daos.get(i).toString());
				}				
			}
		});
		
		afterIndex = -1;
		
		if(daos.size() >= 1)
		{
			for(int i=0; i<daos.size(); i++)
			{
				if(daos.get(i).getDaoId() == beforeSelectedDaoId)
				{					
					afterIndex = i;
					break;
				}
			}
		}
		
		if(afterIndex >= 0 && controlCompAfter && setComponentSelectBefores)
		{
			SwingUtilities.invokeLater(new Runnable()
			{				
				@Override
				public void run()
				{
					accessCombo.setSelectedIndex(afterIndex);
				}
			});
		}
		else if(daos.size() >= 1 && controlCompAfter && (! setComponentSelectBefores))
		{
			SwingUtilities.invokeLater(new Runnable()
			{				
				@Override
				public void run()
				{
					accessCombo.setSelectedIndex(daos.size() - 1);
				}
			});			
		}
		
		try
		{
			Map<String, Object> additionalData = new Hashtable<String, Object>();
			additionalData.put("selected_dao", new Integer(selectedDao));
			
			tableListView.refresh(additionalData);
			viewListView.refresh(additionalData);
			procedureListView.refresh(additionalData);
			functionListView.refresh(additionalData);
		}
		catch(Throwable e1)
		{
			
		}
	}
	/**
	 * <p>사용자가 실행 버튼을 눌렀을 때 실행되는 메소드입니다.</p>
	 * 
	 */
	@Override
	protected void btRunScriptAction(boolean onlySelected)
	{
		if(timerView != null) timerView.forceTimer();
		Object results = runScriptAction(onlySelected, null);
		sendRefreshToModules("RunScriptEvent://", results);
		if(timerView != null) timerView.freeTimer();
	}
	
	protected Object runScriptAction(boolean onlySelected, ParameterRunnable afterAction)
	{
		Object resultObject = null;
		String scriptContents = null;
		boolean simplifyError = true;
		
		if(onlySelected)
		{
			scriptContents = scriptArea.getSelectedText();
		}
		else
		{
			scriptContents = scriptArea.getText();
		}
		
		int selectedIndex = runCombo.getSelectedIndex();
		String selectedItem = runComboElements.get(selectedIndex);
		
		if(runComboElements.get(selectedIndex).equalsIgnoreCase("JScript")) // JScript
		{
			try
			{
				resultObject = scriptRunner.execute(scriptContents);
				if(resultObject instanceof TableSet)
				{
					if(((TableSet) resultObject).getName() == null) ((TableSet) resultObject).setName("RESULT");
					logTable((TableSet) resultObject);					
				}
				else if(resultObject instanceof SpecialOrder)
				{
					SpecialOrderUtil.act(((SpecialOrder) resultObject).getOrder(), scriptRunner);
				}
				else log(resultObject);
			}
			catch(Throwable e)
			{
				logError(e, applyStringTable("On script"), scriptRunner.isDefaultErrorSimplicityOption());
			}
		}
		else if(runComboElements.get(selectedIndex).equalsIgnoreCase("JSON")) // JSON
		{
			try
			{
				resultObject = JSONUtil.toTableSet(scriptContents);
				logTable((TableSet) resultObject);
			}
			catch(Throwable e)
			{
				logError(e, applyStringTable("On script"), true);
			}
		}
		else if(runComboElements.get(selectedIndex).equalsIgnoreCase("HGF")) // HGF
		{
			try
			{
				resultObject = DataUtil.toTableSet(scriptContents);
				logTable((TableSet) resultObject);
			}
			catch(Throwable e)
			{
				logError(e, applyStringTable("On script"), true);
			}
		}
		else if(runComboElements.get(selectedIndex).equalsIgnoreCase("SQL"))// SQL
		{
			try
			{
				TableSet results = getDao().query(scriptContents);
				if(results != null)
				{
					if(results.getName() == null) results.setName("RESULT");
					logTable(results);
				}
				else log(Manager.applyStringTable("There is no results."));
			}
			catch(SQLException e)
			{
				logError(e, applyStringTable("On SQL"), false);
			}
			catch(NotConnectedException e)
			{
				logError(e, applyStringTable("On SQL"), false);
			}
			catch(Throwable e)
			{
				logError(e, applyStringTable("On SQL"), false);
			}
		}
		else
		{
			try
			{
				simplifyError = ScriptUtil.getRunner(selectedItem).isDefaultErrorSimplicityOption();
				resultObject = ScriptUtil.getRunner(selectedItem).execute(scriptContents);
				if(resultObject instanceof TableSet)
				{
					if(((TableSet) resultObject).getName() == null) ((TableSet) resultObject).setName("RESULT");
					logTable((TableSet) resultObject);					
				}
				else if(resultObject instanceof SpecialOrder)
				{
					SpecialOrderUtil.act(((SpecialOrder) resultObject).getOrder(), scriptRunner);
				}
				else log(resultObject);
			}
			catch(Throwable e)
			{
				logError(e, applyStringTable("On script"), simplifyError);
			}
		}
		
		Map<String, Object> afterActionParam = new Hashtable<String, Object>();
		if(resultObject != null) afterActionParam.put("result", resultObject);
		if(afterAction != null) afterAction.run(afterActionParam);
		return resultObject;
	}
	
	@Override
	protected void btCancelScript()
	{
		int selectedIndex = runCombo.getSelectedIndex();
		String selectedItem = runComboElements.get(selectedIndex);
		
		if(runComboElements.get(selectedIndex).equalsIgnoreCase("JScript")) // JScript
		{
			try
			{
				scriptRunner.cancelAll();
			}
			catch(Throwable e)
			{
				logError(e, applyStringTable("On script"), scriptRunner.isDefaultErrorSimplicityOption());
			}
		}
		else if(runComboElements.get(selectedIndex).equalsIgnoreCase("JSON")) // JSON
		{
			try
			{
				// TODO : 스크립트 취소 기능 구현
			}
			catch(Throwable e)
			{
				logError(e, applyStringTable("On script"), true);
			}
		}
		else if(runComboElements.get(selectedIndex).equalsIgnoreCase("HGF")) // HGF
		{
			try
			{
				// TODO : 스크립트 취소 기능 구현
			}
			catch(Throwable e)
			{
				logError(e, applyStringTable("On script"), true);
			}
		}
		else if(runComboElements.get(selectedIndex).equalsIgnoreCase("SQL"))// SQL
		{
			try
			{
				getDao().cancel();
			}
			catch(Throwable e)
			{
				logError(e, applyStringTable("On SQL"), false);
			}
		}
		else
		{
			try
			{
				ScriptUtil.getRunner(selectedItem).cancelAll();
			}
			catch(Throwable e)
			{
				logError(e, applyStringTable("On script"), true);
			}
		}
		sendRefreshToModules("CancelEvent://", null);
	}
	
	/**
	 * <p>사용자가 접속 버튼을 눌렀을 때 실행되는 메소드입니다.</p>
	 * 
	 */
	@Override
	protected void btConnect()
	{
		String selectedId = String.valueOf(idField.getSelectedItem());
		String selectedPw = new String(pwField.getPassword());
		String selectedUrl = String.valueOf(urlField.getSelectedItem());
		
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				btConnect.setEnabled(false);
			}
		});
		
		boolean alreadyConnected = false;
		if(daos != null)
		{
			for(int i=0; i<daos.size(); i++)
			{
				if(selectedId != null)
				{
					if(selectedId.equals(daos.get(i).getId()))
					{
						alreadyConnected = true;
					}
					else alreadyConnected = false;
				}
				else if(daos.get(i).getId() != null)
				{
					alreadyConnected = true;
				}
				else alreadyConnected = false;
				
				if(! alreadyConnected) continue;
				
				if(selectedPw != null)
				{
					if(selectedPw.equals(daos.get(i).getPw()))
					{
						alreadyConnected = true;
					}
					else alreadyConnected = false;
				}
				else if(daos.get(i).getPw() != null)
				{
					alreadyConnected = true;
				}
				else alreadyConnected = false;
				
				if(! alreadyConnected) continue;
				
				if(selectedUrl != null)
				{
					if(selectedUrl.equals(daos.get(i).getUrl()))
					{
						alreadyConnected = true;
					}
					else alreadyConnected = false;
				}
				else if(daos.get(i).getUrl() != null)
				{
					alreadyConnected = true;
				}
				else alreadyConnected = false;
				
				if(! alreadyConnected) continue;
				
				if(daos.get(i).isAlive())
				{
					alreadyConnected = true;
				}
				else alreadyConnected = false;
				
				if(alreadyConnected) break;
			}
		}
		
		if(alreadyConnected)
		{
			log(Manager.applyStringTable("Already connected"));
			return;
		}
		
		boolean classPathExist = false;
		for(int i=0; i<classField.getItemCount(); i++)
		{
			if(classField.getItemAt(i).equals(classField.getSelectedItem()))
			{
				classPathExist = true;
				break;
			}
		}
		if(! classPathExist)
		{
			classField.addItem(classField.getSelectedItem());
		}
		
		boolean urlExist = false;
		for(int i=0; i<urlField.getItemCount(); i++)
		{
			if(urlField.getItemAt(i).equals(urlField.getSelectedItem()))
			{
				urlExist = true;
				break;
			}
		}
		if(! urlExist)
		{
			urlField.addItem(urlField.getSelectedItem());
		}
		
		boolean idExist = false;
		for(int i=0; i<idField.getItemCount(); i++)
		{
			if(idField.getItemAt(i).equals(idField.getSelectedItem()))
			{
				idExist = true;
				break;
			}
		}
		if(! idExist)
		{
			idField.addItem(idField.getSelectedItem());
		}
		
		try
		{
			newDao = ConnectUtil.connectParallely(this, selectedId, selectedPw, selectedUrl, String.valueOf(classField.getSelectedItem()).trim(), new Runnable()
			{
				@Override
				public void run()
				{
					daos.add(newDao);
					
					refreshDaos(true, false);
					
					setSelectedDao(daos.size() - 1);
					
					log(newDao);
					
					SwingUtilities.invokeLater(new Runnable()
					{				
						@Override
						public void run()
						{
							try
							{
								accessCombo.setSelectedIndex(getSelectedDao());
							}
							catch(Throwable e)
							{
								logError(e, applyStringTable("On after connect successful"));
							}
						}
					});
					
					if(! runComboInitialized)
					{
						runComboInitialized = true;
						runCombo.setSelectedIndex(0);
					}
					
					newDao = null;
					
					SwingUtilities.invokeLater(new Runnable()
					{
						@Override
						public void run()
						{
							btConnect.setEnabled(true);
						}
					});
				}
			});
		}
		catch(Throwable e)
		{
			e.printStackTrace();
			logError(e, applyStringTable("On connect"));
			newDao = null;
			SwingUtilities.invokeLater(new Runnable()
			{
				@Override
				public void run()
				{
					btConnect.setEnabled(true);
				}
			});
		}
		
		scriptArea.requestFocus();
	}
	
	@Override
	protected void btDisconnect()
	{
		if(getDao().isAlive()) getDao().close();
		refreshDaos(true, true);
		
		SwingUtilities.invokeLater(new Runnable()
		{				
			@Override
			public void run()
			{
				try
				{
					accessCombo.setSelectedIndex(getSelectedDao());
				}
				catch(Throwable e)
				{
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * <p>GUI 화면을 엽니다. 열기 전에 수행해야 할 일들(폰트 불러오기, 포커스)을 수행합니다.</p>
	 * 
	 */
	@Override
	public void open()
	{
		if(! initialized)
		{
			FirstStateView.text("Applying themes");
			
			try
			{
				String themes = getOption("guitheme");
				if(DataUtil.isNotEmpty(themes))
				{
					if(themes.equalsIgnoreCase("nimbus")) UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
					else if(themes.equalsIgnoreCase("metal")) UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
					else if(themes.equalsIgnoreCase("system")) UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					else UIManager.setLookAndFeel(themes);
				}
				else
				{
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				}
			}
			catch(Throwable e)
			{
				logError(e, applyStringTable("On theme"), true);
			}
			
			FirstStateView.text("Initializing main UI components");
			FirstStateView.set(51);
			
			initComponents();
			
			FirstStateView.text("Applying fonts");
			FirstStateView.set(77);
			
			applyFont();
			
			FirstStateView.text("Loading modules");
			FirstStateView.set(78);
			
			loadModules();  FirstStateView.set(83);
			
			FirstStateView.text("Applying modules");
			
			applyModules(); FirstStateView.set(88);
			
			FirstStateView.text("Loading frequency lists");
			
			loadFrequency();
			
			FirstStateView.set(90);
			FirstStateView.text("Initializing UI components from licenses");
			
			LicenseUtil.additionalInitializingGUI(this);
			if(timerView != null) timerView.start();
			
			initialized = true;
			log(beforeInitializedMessage);
			beforeInitializedMessage = "";
			System.gc();
		}
		
		FirstStateView.text("Complete");
		FirstStateView.off();
		
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				frame.setVisible(true);
				scriptSplit.setDividerLocation(Double.parseDouble(getOption("main_divider_ratio")));
				refreshEnabled();
				refreshTableView();
				initSelection();
				statusField.setText("");
				refreshScriptAreaHighlightMode();
			}
		});
		
		try
		{
			if(! DataUtil.parseBoolean(getOption("agree_license")))
			{
				aboutDialog.open();
			}
		}
		catch(Exception e)
		{
			
		}
		
		try
		{
			for(int i=0; i<modules.size(); i++)
			{
				modules.get(i).doAfterInit();
			}
		}
		catch(Exception e)
		{
			
		}
		
		applyFavorites();
		if(getAliveDaos().size() >= 1) btHideOrShow();
	}
	
	/**
	 * <p>폰트를 다시 적용합니다.</p>
	 */
	protected void applyFont()
	{
		try
		{
			if(frame.isVisible())
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					@Override
					public void run()
					{
						setFontAll();
					}
				});
			}
			else setFontAll();
		}
		catch(Throwable e)
		{
			
		}
	}
	private void setFontAll()
	{
		GUIUtil.setFontRecursively(frame, GUIUtil.usingFont);
		GUIUtil.setFontRecursively(inputDialog.getDialog(), GUIUtil.usingFont);
		GUIUtil.setFontRecursively(fileToDbManager.getDialog(), GUIUtil.usingFont);
		GUIUtil.setFontRecursively(dbToFileManager.getDialog(), GUIUtil.usingFont);
		GUIUtil.setFontRecursively(aboutDialog, GUIUtil.usingFont);
		GUIUtil.setFontRecursively(preferenceView, GUIUtil.usingFont);
//		GUIUtil.setFontRecursively(scriptArea, GUIUtil.usingScriptFont);
		scriptArea.setScriptFont(GUIUtil.usingScriptFont);
	}
	@Override
	protected void applyModules()
	{
		try
		{
			if(getOption("safe_mode") != null && DataUtil.parseBoolean(getOption("safe_mode")))
			{
				return;
			}
		}
		catch (InvalidInputException e)
		{
			
		}
		
		boolean menuFileFirst = true;
		boolean menuToolFirst = true;
		boolean menuTransFirst = true;
		boolean menuViewFirst = true;
		boolean menuHelpFirst = true;
		
		prepareConnectPanel();
		
		for(int i=0; i<modules.size(); i++)
		{
			try
			{
				AbstractModule module = modules.get(i);
				module.init();
				
				if((module instanceof ModuleOnMenu))
				{
					JMenuItem newMenuItem = new JMenuItem(module.getName());
					tempModule = (ModuleOnMenu) module;
					MenuMatch newMenuMatch = new MenuMatch(newMenuItem, tempModule, this);
					newMenuItem.addActionListener(newMenuMatch);
					menuMatching.add(newMenuMatch);
					switch(tempModule.getMenuLocation())
					{
					case GUIDialogModule.ON_MENU_FILE:
						if(menuFileFirst)
						{
							menuFileFirst = false;
							menuFile.addSeparator();
						}
						menuFile.add(newMenuItem);
						break;
					case GUIDialogModule.ON_MENU_TOOL:
						if(menuToolFirst)
						{
							menuToolFirst = false;
							menuTool.addSeparator();
						}
						menuTool.add(newMenuItem);
						break;
					case GUIDialogModule.ON_MENU_TRANS:
						if(menuTransFirst)
						{
							menuTransFirst = false;
							menuTrans.addSeparator();
						}
						menuTrans.add(newMenuItem);
						break;
					case GUIDialogModule.ON_MENU_VIEW:
						if(menuViewFirst)
						{
							menuViewFirst = false;
							menuView.addSeparator();
						}
						menuView.add(newMenuItem);
						break;
					case GUIDialogModule.ON_MENU_HELP:
						if(menuHelpFirst)
						{
							menuHelpFirst = false;
							menuHelp.addSeparator();
						}
						menuHelp.add(newMenuItem);
						break;
					case GUIDialogModule.ON_MENU_NONE:
						break;
					default:
						boolean checkExist = false;
						for(int j=0; j<additionalMenu.size(); j++)
						{
							if(additionalMenu.get(j).getText().equals(applyStringTable(tempModule.getMenuLocationName())))
							{
								checkExist = true;
								additionalMenu.get(j).add(newMenuItem);
							}
						}
						if(! checkExist)
						{
							if(DataUtil.isNotEmpty(applyStringTable(tempModule.getMenuLocationName())))
							{
								JMenu addMenu = new JMenu(applyStringTable(tempModule.getMenuLocationName()));
								additionalMenu.add(addMenu);
								menuBar.add(addMenu);
								addMenu.add(newMenuItem);
							}
						}
					}
					
					if(((ModuleOnMenu) module).getShortcut() != null)
					{
						ModuleOnMenu moduleOnMenu = (ModuleOnMenu) module;
						
						Integer shortcuts = moduleOnMenu.getShortcut();
						Integer mask = moduleOnMenu.getMask();
						if(shortcuts != null)
						{
							if(mask != null)
							{
								newMenuItem.setAccelerator(KeyStroke.getKeyStroke(shortcuts.intValue(), mask.intValue()));
							}
							else
							{
								newMenuItem.setAccelerator(KeyStroke.getKeyStroke((char) (shortcuts.intValue() - KeyEvent.VK_0 + (int) '0')));
							}
						}
					}
				}
				
				if(module instanceof GUIPanelModule) resultTab.add(module.getName(), ((GUIPanelModule) module).getComponent());
				if(module instanceof GUIToolbarModule) toolbars.add((GUIToolbarModule) module);
				
				if(module instanceof GUIModule) GUIUtil.setFontRecursively(((GUIModule) module).getComponent(), GUIUtil.usingFont);
			}
			catch(Exception e)
			{
				logError(e, applyStringTable("On applying GUI modules"));
			}
		}
		
		if(toolbars != null)
		{
			JPanel beforePanel = null;
			JPanel beforeToolbarPanel = null;
			for(GUIToolbarModule t : toolbars)
			{
				if(t.getComponent() != null)
				{
					if(beforeToolbarPanel == null)
					{
						beforeToolbarPanel = new JPanel();
						beforeToolbarPanel.setLayout(new BorderLayout());
						beforeToolbarPanel.add(t.getComponent(), BorderLayout.CENTER);
						
						if(beforePanel == null) beforePanel = toolbarPanel;
						beforePanel.add(beforeToolbarPanel, BorderLayout.SOUTH);
						
						beforePanel = beforeToolbarPanel;
					}
					else
					{
						beforeToolbarPanel.add(t.getComponent(), BorderLayout.EAST);
						beforeToolbarPanel = null;
					}
				}
			}
		}
	}
	protected void initSelection()
	{
		runCombo.setSelectedIndex(1);
		runCombo2.setSelectedIndex(1);
		if(getDao().isAlive() && (! runComboInitialized))
		{
			runComboInitialized = true;
			runCombo.setSelectedIndex(0);
		}
	}
	@Override
	public void itemStateChanged(ItemEvent e)
	{
		Object ob = e.getSource();
		if(ob == accessCombo)
		{
			accessCombo();			
		}
		else if(ob == runCombo)
		{
			refreshScriptAreaHighlightMode();
		}
	}
	
	/**
	 * <p>메인 스크립트 영역의 문법 강조 모드를 다시 적용합니다.</p>
	 * 
	 */
	private void refreshScriptAreaHighlightMode()
	{
//		if(runCombo.getSelectedIndex() == 0)
		// TODO
		int selectedIndex = runCombo.getSelectedIndex();
		String selectedItem = runComboElements.get(selectedIndex);
		if(selectedItem.equalsIgnoreCase("SQL"))
		{
			scriptArea.setHighlightMode("SQL");
		}
		else if(selectedItem.equalsIgnoreCase("JScript")
				|| selectedItem.equalsIgnoreCase("JSON"))
		{
			scriptArea.setHighlightMode("JScript");
		}
		else if(selectedItem.equalsIgnoreCase("HGF"))
		{
			scriptArea.setHighlightMode("HGF");
		}
		else
		{
			scriptArea.setHighlightMode(selectedItem);
		}
	}
	/**
	 * <p>이 메소드는 사용자가 접속 콤보박스를 다른 것으로 선택했을 때 호출됩니다.</p>
	 * 
	 */
	protected void accessCombo()
	{
		if(accessCombo.getSelectedIndex() >= 0)
		{
			setSelectedDao(accessCombo.getSelectedIndex());
			idField.setSelectedItem(getDao().getId());
			pwField.setText("****");
			urlField.setSelectedItem(getDao().getUrl());
		}
		if(! accessComboLocked)
		{
			int befores = accessCombo.getSelectedIndex();
			if(accessCombo.getSelectedIndex() >= 0)
			{					
				if(befores >= 0)
				{
					accessComboLocked = true;
					accessCombo.setSelectedIndex(befores);
				}
				
				try
				{
					Map<String, Object> additionalData = new Hashtable<String, Object>();
					additionalData.put("selected_dao", new Integer(selectedDao));
					
					tableListView.refresh(additionalData);
					viewListView.refresh(additionalData);
					procedureListView.refresh(additionalData);
					functionListView.refresh(additionalData);
				}
				catch(Throwable e1)
				{
					
				}
			}
		}
		refreshEnabled();
		if(accessComboLocked)
		{
			accessComboLocked = false;
		}
	}
	@Override
	public void setPercent(int p)
	{
		super.setPercent(p);
		processBar.setValue(p);
		try
		{
			fileToDbManager.setPercent(p);
		}
		catch(Throwable e)
		{
			
		}
		try
		{
			dbToFileManager.setPercent(p);
		}
		catch(Throwable e)
		{
			
		}
	}
	@Override
	public void close()
	{
		try
		{
			if(timerView != null) timerView.noMoreUse();
		}
		catch(Exception e)
		{
			
		}
		
		try
		{
			saveFrequency();
		}
		catch(Exception e)
		{
			
		}
		
		try
		{
			scriptArea.noMoreUse();
		}
		catch(Exception e)
		{
			
		}
		
		try
		{
			frame.setVisible(false);
		}
		catch(Exception e)
		{
			
		}
		
		try
		{
			functionDialog.noMoreUse();
		}
		catch(Exception e)
		{
			
		}
		
		try
		{
			for(int i=0; i<tableSetViews.size(); i++)
			{
				tableSetViews.get(i).noMoreUse();
			}
			tableSetViews.clear();
		}
		catch(Exception e)
		{
			
		}
		
		try
		{
			for(int i=0; i<menuMatching.size(); i++)
			{
				menuMatching.get(i).noMoreUse();
			}
			menuMatching.clear();
			menuMatching = null;
		}
		catch(Exception e)
		{
			
		}
				
		if(additionalMenu != null) additionalMenu.clear();
		super.close();
		System.gc();
		
		try
		{
			Thread.sleep(500);
		}
		catch(Exception e)
		{
			
		}
		
		Main.processShutdown();
	}
	
	/**
	 * <p>드라이버 입력 콤보박스에 새 아이템을 추가합니다.</p>
	 * 
	 * @param contents : 추가할 내용
	 */
	public void addItemOnClassField(String contents)
	{
		classField.addItem(contents);
	}
	
	/**
	 * <p>URL 입력 콤보박스에 새 아이템을 추가합니다.</p>
	 * 
	 * @param contents : 추가할 내용
	 */
	public void addItemOnUrlField(String contents)
	{
		urlField.addItem(contents);
	}
	
	/**
	 * <p>ID 입력 콤보박스에 새 아이템을 추가합니다.</p>
	 * 
	 * @param contents : 추가할 내용
	 */
	public void addItemOnIdField(String contents)
	{
		idField.addItem(contents);
	}
	
	/**
	 * <p>자주 사용하는 콤보박스 값 리스트를 불러와 적용합니다.</p>
	 */
	protected void loadFrequency()
	{
		newClassList.clear();
		newUrlList.clear();
		newIdList.clear();
		
		try
		{
			File targets = new File(getOption("config_path") + "frequency.txt");
			if(! targets.exists()) return;
			String contents = StreamUtil.readText(targets, "UTF-8");
			
			StringTokenizer lineTokenizer = new StringTokenizer(contents, "\n");
			boolean classMode = false;
			boolean idMode = false;
			boolean urlMode = false;
			while(lineTokenizer.hasMoreTokens())
			{
				String lines = lineTokenizer.nextToken();
				lines = lines.trim();
				if(lines.startsWith("#")) continue;
				else if(lines.equalsIgnoreCase("@classpath"))
				{
					classMode = true;
					urlMode = false;
					idMode = false;
				}
				else if(lines.equalsIgnoreCase("@url"))
				{
					classMode = false;
					urlMode = true;
					idMode = false;
				}
				else if(lines.equalsIgnoreCase("@id"))
				{
					classMode = false;
					urlMode = false;
					idMode = true;
				}
				else if(classMode)
				{
					boolean classExist = false;
					for(int i=0; i<classField.getItemCount(); i++)
					{
						if(String.valueOf(classField.getItemAt(i)).equals(lines))
						{
							classExist = true;
						}
					}
					if(! classExist) newClassList.add(lines);
				}
				else if(urlMode)
				{
					boolean urlExist = false;
					for(int i=0; i<urlField.getItemCount(); i++)
					{
						if(String.valueOf(urlField.getItemAt(i)).equals(lines))
						{
							urlExist = true;
						}
					}
					if(! urlExist) newUrlList.add(lines);
				}
				else if(idMode)
				{
					boolean idExist = false;
					for(int i=0; i<idField.getItemCount(); i++)
					{
						if(String.valueOf(idField.getItemAt(i)).equals(lines))
						{
							idExist = true;
						}
					}
					if(! idExist) newIdList.add(lines);
				}
			}
		}
		catch(Exception e)
		{
			
		}
		
		for(int i=0; i<classField.getItemCount(); i++)
		{
			if(! (newClassList.contains(classField.getItemAt(i)))) newClassList.add(String.valueOf(classField.getItemAt(i)));
		}
		for(int i=0; i<urlField.getItemCount(); i++)
		{
			if(! (newUrlList.contains(urlField.getItemAt(i)))) newUrlList.add(String.valueOf(urlField.getItemAt(i)));
		}
		for(int i=0; i<idField.getItemCount(); i++)
		{
			if(! (newIdList.contains(idField.getItemAt(i)))) newIdList.add(String.valueOf(idField.getItemAt(i)));
		}
		
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				classField.removeAllItems();
				urlField.removeAllItems();
				idField.removeAllItems();
				
				for(int i=0; i<newClassList.size(); i++)
				{
					classField.addItem(newClassList.get(i));
				}
				for(int i=0; i<newUrlList.size(); i++)
				{
					urlField.addItem(newUrlList.get(i));
				}
				for(int i=0; i<newIdList.size(); i++)
				{
					idField.addItem(newIdList.get(i));
				}
			}
		});
	}
	
	/**
	 * <p>환경 설정에 따라 자주 사용하는 콤보박스 값들 저장을 시도합니다.</p>
	 * 
	 */
	protected void saveFrequency()
	{
		StringBuffer contents = new StringBuffer("");
		
		contents = contents.append("@classpath" + "\n");
		for(int i=0; i<classField.getItemCount(); i++)
		{
			contents = contents.append(classField.getItemAt(i) + "\n");
		}
		
		contents = contents.append("@url" + "\n");
		for(int i=0; i<urlField.getItemCount(); i++)
		{
			contents = contents.append(urlField.getItemAt(i) + "\n");
		}

		contents = contents.append("@id" + "\n");
		for(int i=0; i<idField.getItemCount(); i++)
		{
			contents = contents.append(idField.getItemAt(i) + "\n");
		}
		
		try
		{
			if(DataUtil.parseBoolean(getOption("save_frequency")))
			{
				StreamUtil.saveFile(new File(getOption("config_path") + "frequency.txt"), contents.toString(), "UTF-8");
			}
		}
		catch(Exception e)
		{
			
		}
	}
	protected boolean isRunScriptEvent(AWTEvent e)
	{
		return ((e.getSource() == btRunScript) || (e.getSource() == menuTransRun));
	}
	protected boolean isCancelScriptEvent(AWTEvent e)
	{
		return ((e.getSource() == btCancelScript));
	}
	protected boolean isTransactionRunEvent(AWTEvent e)
	{
		return (e.getSource() == menuTransRunSelected);
	}
	protected boolean isTransactionCommitEvent(AWTEvent e)
	{
		return (e.getSource() == btCommit || e.getSource() == menuTransCommit);
	}
	protected boolean isTransactionRollbackEvent(AWTEvent e)
	{
		return (e.getSource() == btRollback || e.getSource() == menuTransRollback);
	}
	protected boolean isTryConnectEvent(AWTEvent e)
	{
		return (e.getSource() == btConnect || e.getSource() == pwField);
	}
	protected boolean isTryDisconnectEvent(AWTEvent e)
	{
		return (e.getSource() == btDisconnect);
	}
	protected void afterEvent(String eventName, AWTEvent e)
	{
		if(eventName.equalsIgnoreCase("run"))
		{
			String[] scriptInfo = new String[4];
			scriptInfo[0] = getQueryAreaText();
			scriptInfo[1] = String.valueOf(runCombo.getSelectedIndex());
			scriptInfo[2] = String.valueOf(runComboElements.get(runCombo.getSelectedIndex()));
			scriptInfo[3] = String.valueOf(runCombo.getSelectedItem());
			sendRefreshToModules("RunEvent://", scriptArea.getText());
		}
		else sendRefreshToModules("ActionEvent://" + eventName, e);
	}
	@Override
	public void actionPerformed(ActionEvent e)
	{
		Object ob = e.getSource();
		String eventName = "";
				
		if(isRunScriptEvent(e))
		{
			eventName = "run";
			if(eventHandler != null) eventQueue.add(eventName);
			else btRunScriptAction(false);
		}
		else if(isTransactionRunEvent(e))
		{
			eventName = "run_selected";
			if(eventHandler != null) eventQueue.add(eventName);
			else btRunScriptAction(true);
		}
		else if(isTryConnectEvent(e))
		{
			eventName = "connect";
			if(eventHandler != null) eventQueue.add(eventName);
			else btConnect();
		}
		else if(isTryDisconnectEvent(e))
		{
			eventName = "disconnect";
			if(eventHandler != null) eventQueue.add(eventName);
			else btDisconnect();
		}
		else if(isTransactionCommitEvent(e))
		{
			eventName = "commit";
			if(eventHandler != null) eventQueue.add(eventName);
			else btCommit();
		}
		else if(isTransactionRollbackEvent(e))
		{
			eventName = "rollback";
			if(eventHandler != null) eventQueue.add(eventName);
			else btRollback();
		}
		else if(ob == btSave)
		{
			eventName = "save";
			if(eventHandler != null) eventQueue.add(eventName);
			else btSave();
		}
		else if(ob == btLoad)
		{
			eventName = "load";
			if(eventHandler != null) eventQueue.add(eventName);
			else btLoad();
		}
		else if(ob == btToDb)
		{
			eventName = "todb";
			if(eventHandler != null) eventQueue.add(eventName);
			else btToDb();
		}
		else if(ob == btRunOneLine || ob == onelineField)
		{
			eventName = "oneline";
			if(eventHandler != null) eventQueue.add(eventName);
			else btRunOneLine();
		}
		else if(ob == btHideOrShow)
		{
			eventName = "hideorshow";
			if(eventHandler != null) eventQueue.add(eventName);
			else btHideOrShow();
		}
		else if(ob == menuFileNewTab)
		{
			eventName = "newtab";
			if(eventHandler != null) eventQueue.add(eventName);
			else newTab();
		}
		else if(ob == menuFileSave)
		{
			eventName = "savefile";
			if(eventHandler != null) eventQueue.add(eventName);
			else menuFileSave();
		}
		else if(ob == menuFileLoad)
		{
			eventName = "loadfile";
			if(eventHandler != null) eventQueue.add(eventName);
			else menuFileLoad();
		}
		else if(ob == menuFileSaveConsole)
		{
			eventName = "saveconsole";
			if(eventHandler != null) eventQueue.add(eventName);
			else menuFileSaveConsole();
		}
		else if(ob == menuFileRestart)
		{
			eventName = "Restart";
			Main.restartAllProcess();
		}
		else if(ob == menuFileExit || ob == btExit)
		{
			eventName = "exit";
			close();
		}
		else if(ob == menuFileLoadJar)
		{
			eventName = "loadjar";
			if(eventHandler != null) eventQueue.add(eventName);
			else menuFileLoadJar();
		}
		else if(ob == menuToolCreateTable)
		{
			eventName = "createTableView";
			if(eventHandler != null) eventQueue.add(eventName);
			else menuToolCreateTool();
		}
		else if(ob == menuToolDbToFile)
		{
			eventName = "dbtofile";
			if(eventHandler != null) eventQueue.add(eventName);
			else menuToolDbToFile();
		}
		else if(ob == menuToolFileToDb)
		{
			eventName = "filetodb";
			if(eventHandler != null) eventQueue.add(eventName);
			else menuToolFileToDb();
		}
		else if(ob == menuToolPreference)
		{
			eventName = "preference";
			if(eventHandler != null) eventQueue.add(eventName);
			else menuToolPreference();
		}
		else if(ob == menuToolModules)
		{
			eventName = "moduleList";
			if(eventHandler != null) eventQueue.add(eventName);
			else menuToolModules();
		}
		else if(ob == menuToolFunction)
		{
			eventName = "analyzeFunction";
			if(eventHandler != null) eventQueue.add(eventName);
			else menuToolFunction();
		}
		else if(ob == menuViewSearch)
		{
			eventName = "search";
			if(eventHandler != null) eventQueue.add(eventName);
			else scriptArea.showSearchDialog();
		}
		else if(ob == menuViewTableList)
		{
			eventName = "viewtablelist";
			if(eventHandler != null) eventQueue.add(eventName);
			else tableListView.open();
		}
		else if(ob == menuViewViewList)
		{
			eventName = "viewviewlist";
			if(eventHandler != null) eventQueue.add(eventName);
			else viewListView.open();
		}
		else if(ob == menuViewProcedureList)
		{
			eventName = "viewProcedurelist";
			if(eventHandler != null) eventQueue.add(eventName);
			else procedureListView.open();
		}
		else if(ob == menuViewFunctionList)
		{
			eventName = "viewFunctionlist";
			if(eventHandler != null) eventQueue.add(eventName);
			else functionListView.open();
		}
		else if(ob == menuAbout)
		{
			eventName = "about";
			if(eventHandler != null) eventQueue.add(eventName);
			else aboutDialog.open();
		}
		else if(ob == menuToolUndo)
		{
			eventName = "undo";
			SwingUtilities.invokeLater(new Runnable()
			{				
				@Override
				public void run()
				{
					scriptArea.undo();
					refreshUndoing();
				}
			});			
		}
		else if(ob == menuToolRedo)
		{
			eventName = "redo";
			SwingUtilities.invokeLater(new Runnable()
			{				
				@Override
				public void run()
				{
					scriptArea.redo();
					refreshUndoing();
				}
			});
		}
		
		if(eventHandler != null)
		{
			activeEventHandler(eventName);
		}
		
		refreshEnabled();
		afterEvent(eventName, e);
	}
	
	/**
	 * 이벤트 핸들러를 하나 생성해 활성화합니다. 동작 하나만 실행하고 이 핸들러는 종료됩니다.
	 */
	protected void activeEventHandler(String description)
	{
		eventHandlerSwitch = true;
		HThread newEventHandler = new HThread(new Runnable()
		{
			@Override
			public void run()
			{
				while(eventHandlerSwitch)
				{
					try
					{
						if(eventQueue.size() >= 1)
						{
							String order = eventQueue.remove(0);
							activeOrder(order);
						}
					}
					catch(Exception e)
					{
						logError(e, applyStringTable("On event handling"));
					}
					try
					{
						Thread.sleep(100);
					}
					catch(InterruptedException e)
					{
						eventHandlerSwitch = false;
					}
					catch(Exception e)
					{
						
					}
					break;
				}
			}
		});
		newEventHandler.setName(applyStringTable("Event Handler At " + System.currentTimeMillis()));
		newEventHandler.setDescription(applyStringTable("This event handler run") + " : " + description);
		eventHandler.add(newEventHandler);
		newEventHandler.start();
	}
	
	@Override
	protected void activeOrder(String order) 
	{
		if(order.equals("run"))
		{
			btRunScriptAction(false);
		}
		else if(order.equals("run_selected"))
		{
			btRunScriptAction(true);
		}
		else if(order.equals("connect"))
		{
			btConnect();
		}
		else if(order.equals("disconnect"))
		{
			btDisconnect();
		}
		else if(order.equals("commit"))
		{
			btCommit();
		}
		else if(order.equals("rollback"))
		{
			btRollback();
		}
		else if(order.equals("save"))
		{
			btSave();
		}
		else if(order.equals("load"))
		{
			btLoad();
		}
		else if(order.equals("todb"))
		{
			btToDb();
		}
		else if(order.equals("oneline"))
		{
			btRunOneLine();
		}
		else if(order.equals("hideorshow"))
		{
			btHideOrShow();
		}
		else if(order.equals("newtab"))
		{
			newTab();
		}
		else if(order.equals("savefile"))
		{
			menuFileSave();
		}
		else if(order.equals("loadfile"))
		{
			menuFileLoad();
		}
		else if(order.equals("saveconsole"))
		{
			menuFileSaveConsole();
		}
		else if(order.equals("loadjar"))
		{
			menuFileLoadJar();
		}
		else if(order.equals("createTableView"))
		{
			menuToolCreateTool();
		}
		else if(order.equals("dbtofile"))
		{
			menuToolDbToFile();
		}
		else if(order.equals("filetodb"))
		{
			menuToolFileToDb();
		}
		else if(order.equals("preference"))
		{
			menuToolPreference();
		}
		else if(order.equals("moduleList"))
		{
			menuToolModules();
		}
		else if(order.equals("analyzeFunction"))
		{
			menuToolFunction();
		}
		else if(order.equals("search"))
		{
			scriptArea.showSearchDialog();
		}
		else if(order.equals("viewtablelist"))
		{
			tableListView.open();
		}
		else if(order.equals("viewviewlist"))
		{
			viewListView.open();
		}
		else if(order.equals("viewProcedurelist"))
		{
			procedureListView.open();
		}
		else if(order.equals("viewFunctionlist"))
		{
			functionListView.open();
		}
		else if(order.equals("about"))
		{
			aboutDialog.open();
		}
		
	}
	/**
	 * <p>테이블 생성 대화 상자를 엽니다.</p>
	 * 
	 */
	public void createTableView()
	{
		createTableView.open();
	}
	/**
	 * <p>새 탭을 추가합니다.</p>
	 * 
	 */
	protected void newTab()
	{
		scriptArea.newTab();
	}
	@Override
	protected void loadScript()
	{
		int selection = loadFileChooser.showOpenDialog(frame);
		if(selection == JFileChooser.APPROVE_OPTION)
		{
			File selectedFile = loadFileChooser.getSelectedFile();
			
			try
			{
				if(loadFileChooser.getFileFilter() == hgfFileFilter)
				{
					logTable(DataUtil.toTableSet(StreamUtil.readText(selectedFile, getOption("file_charset"))));
				}
				else if(loadFileChooser.getFileFilter() == jsonFileFilter)
				{
					logTable(JSONUtil.toTableSet(StreamUtil.readText(selectedFile, getOption("file_charset"))));
				}
				else if(loadFileChooser.getFileFilter() == xlsxFileFilter)
				{
					logTable(XLSXUtil.toTableSet(null, selectedFile));
				}
				else
				{
					scriptArea.setText(StreamUtil.readText(selectedFile, getOption("file_charset")));
				}
			}
			catch(Exception e)
			{
				logError(e, applyStringTable("On read file") + " : " + String.valueOf(selectedFile));
			}
		}
	}
	@Override
	protected void saveScript()
	{
		int selection = saveFileChooser.showSaveDialog(frame);
		if(selection == JFileChooser.APPROVE_OPTION)
		{
			File selectedFile = saveFileChooser.getSelectedFile();
			File selectedDir = saveFileChooser.getCurrentDirectory();
			
			if(! selectedDir.exists())
			{
				selectedDir.mkdir();
			}
			
			try
			{
				StreamUtil.saveFile(selectedFile, scriptArea.getText(), getOption("file_charset"));
			}
			catch(Exception e)
			{
				logError(e, applyStringTable("On save file") + " : " + String.valueOf(selectedFile));
			}
		}
	}
	protected void menuFileLoadJar()
	{
		int selection = jarFileChooser.showOpenDialog(frame);
		if(selection == JFileChooser.APPROVE_OPTION)
		{
			File selected = jarFileChooser.getSelectedFile();
			ClassTool.addJarFileList(selected);
			log(applyStringTable("Selected JAR file is accepted."));
		}
	}
	/**
	 * <p>이 메소드는 사용자가 환경 설정 버튼을 클릭했을 때 호출됩니다.</p>
	 * 
	 */
	protected void menuToolPreference()
	{
		preferenceView.open();
	}
	/**
	 * <p>이 메소드는 사용자가 모듈 목록 버튼을 클릭했을 때 호출됩니다.</p>
	 * 
	 */
	protected void menuToolModules()
	{
		moduleListView.setList(modules);
		moduleListView.open();
	}
	
	/**
	 * <p>이 메소드는 사용자가 접속 영역 숨기기 / 보이기 버튼을 클릭했을 때 호출됩니다.</p>
	 * 
	 */
	protected void btHideOrShow()
	{
		SwingUtilities.invokeLater(new Runnable()
		{			
			@Override
			public void run()
			{
				if(dbAccessControlPanel.isVisible())
				{
					dbAccessControlPanel.setVisible(false);
					btHideOrShow.setText("▼");
				}
				else
				{
					dbAccessControlPanel.setVisible(true);
					btHideOrShow.setText("▲");
				}
			}
		});
	}
	/**
	 * <p>이 메소드는 사용자가 콘솔 내역을 파일로 저장하기를 선택(메뉴에서 해당 항목을 클릭)했을 때 호출됩니다. 파일 저장 창을 띄우고, 선택한 위치와 이름으로 파일을 저장합니다.</p>
	 * 
	 */
	protected void onSaveConsole()
	{
		int selected = textFileChooser.showSaveDialog(frame);
		if(selected == JFileChooser.APPROVE_OPTION)
		{
			File file = textFileChooser.getSelectedFile();
			StreamUtil.saveFile(file, resultArea.getText(), getOption("file_charset"));
		}
		
	}
	
	/**
	 * <p>이 메소드는 사용자가 테이블 내용을 직접 수정했을 때 호출됩니다. 임시 저장된 테이블 셋에 변경 사항을 반영합니다.</p> 
	 * 
	 * @param e : 테이블 모델 이벤트 객체
	 */
	protected void onTableModified(TableModelEvent e)
	{		
		if(temporary != null && resultTableModel != null)
		{
			temporary.setData(e.getColumn(), e.getLastRow(), String.valueOf(resultTableModel.getValueAt(e.getLastRow(), e.getColumn())));
			logTable(temporary);
		}
	}
	
	/**
	 * <p>이 메소드는 사용자가 "DB에 삽입" 버튼을 클릭했을 때 호출됩니다. 삽입할 대상 테이블 이름 입력을 물어보고, 임시 저장된 테이블 셋을 접속한 DB에 삽입합니다.</p>
	 * 
	 */
	protected void btToDb()
	{
		if(temporary == null) return;
		String tableName = askInput(applyStringTable("Input the table name of DB."), true);
		if(tableName != null)
		{
			temporary.setName(tableName);
			temporary.insertIntoDB(getDao());
		}
	}
	
	/**
	 * <p>이 메소드는 사용자가 "불러오기" 버튼을 클릭했을 때 호출됩니다. 파일 불러오기 창을 열고, 사용자가 파일을 선택했을 때, 파일 불러오기 창에서 선택한 형식에 따라 파일을 불러옵니다.</p>
	 * <p>파일 형식을 "모든 파일"로 선택한 상태에서 파일을 선택한 경우, 파일 이름 끝의 확장자를 검사해 동작합니다.</p>
	 */
	protected void btLoad()
	{
		int selected = fileChooser.showOpenDialog(frame);
		if(selected == JFileChooser.APPROVE_OPTION)
		{
			File file = fileChooser.getSelectedFile();
			FileFilter selectedFilter = fileChooser.getFileFilter();
			
			TableSet oldOne = temporary;
			
			try
			{
				if(selectedFilter == xlsxFileFilter)
				{
					temporary = XLSXUtil.toTableSet(Manager.applyStringTable("INSERTS"), file);
				}
				else if(selectedFilter == jsonFileFilter)
				{
					temporary = JSONUtil.toTableSet(StreamUtil.readText(file, getOption("file_charset")));
				}
				else if(selectedFilter == hgfFileFilter)
				{
					temporary = DataUtil.toTableSet(StreamUtil.readText(file, getOption("file_charset")));
				}
				else
				{
					temporary = DataUtil.fromFile(file);
				}
				
				if(oldOne != null) oldOne.noMoreUse();
				
				logTable(temporary);
			}
			catch(Throwable e)
			{
				logError(e, applyStringTable("On load"));
			}
		}
	}
	
	/**
	 * <p>이 메소드는 사용자가 "단일 J스크립트 실행" 버튼을 눌렀거나, 단일 J스크립트 입력 란에서 엔터 키를 눌렀을 때 호출됩니다. 스크립트를 실행합니다.</p>
	 * 
	 */
	protected void btRunOneLine()
	{
		String getScript = onelineField.getText();
		log(">> " + getScript);
		onelineField.setText("");
		
		int selectedIndex = runCombo2.getSelectedIndex();
		String selectedItem = runComboElements.get(selectedIndex);
		try
		{
			if(selectedItem.equalsIgnoreCase("JScript"))
			{
				Object ob = scriptRunner.execute(getScript, showScriptErrorSimply);
				if(ob != null) log(ob);
				
				if(ob instanceof TableSet)
				{
					if(((TableSet) ob).getName() == null) ((TableSet) ob).setName("RESULT");
					logTable((TableSet) ob);				
				}
				else if(ob instanceof SpecialOrder)
				{
					String orders = ((SpecialOrder) ob).getOrder();
					if(orders.equalsIgnoreCase("exit"))
					{
						close();
					}
					else if(orders.equalsIgnoreCase("error_simple"))
					{
						showScriptErrorSimply = true;
					}
					else if(orders.equalsIgnoreCase("error_detail"))
					{
						showScriptErrorSimply = false;
					}
					else
					{
						ob = SpecialOrderUtil.act(orders, scriptRunner);
						if(ob != null) log(ob);
					}
				}
			}
			else if(selectedItem.equalsIgnoreCase("SQL"))
			{
				TableSet tableSet = getDao().query(getScript);
				if(tableSet != null) logTable(tableSet);
			}
			else
			{
				Object resultObject = ScriptUtil.getRunner(selectedItem).execute(getScript);
				if(resultObject instanceof TableSet)
				{
					if(((TableSet) resultObject).getName() == null) ((TableSet) resultObject).setName("RESULT");
					logTable((TableSet) resultObject);					
				}
				else if(resultObject instanceof SpecialOrder)
				{
					SpecialOrderUtil.act(((SpecialOrder) resultObject).getOrder(), ScriptUtil.getRunner(selectedItem));
				}
				else log(resultObject);
			}
		}
		catch(Throwable e)
		{
			logError(e, getScript, showScriptErrorSimply);
		}
		onelineField.requestFocus();
	}
	
	/**
	 * <p>이 메소드는 사용자가 "저장" 버튼을 눌렀을 때 호출됩니다. 파일 저장 창을 띄우고, 사용자가 선택한 형식에 따라 파일을 저장합니다.</p>
	 * 
	 */
	protected void btSave()
	{
		if(temporary == null) return;
		int selected = fileChooser.showSaveDialog(frame);
		if(selected == JFileChooser.APPROVE_OPTION)
		{
			File target = fileChooser.getSelectedFile();
			File dir = new File(StreamUtil.getDirectoryPathOfFile(target));
			
			if(! dir.exists()) dir.mkdir();
			String selectedFile = target.getAbsolutePath();
			
			if(fileChooser.getFileFilter() == xlsxFileFilter)
			{
				if(! (selectedFile.endsWith(".xlsx") || selectedFile.endsWith(".XLSX") || selectedFile.endsWith(".Xlsx")))
				{
					selectedFile = selectedFile + ".xlsx";
				}
				XLSXUtil.save(temporary, new File(selectedFile));
			}
			else if(fileChooser.getFileFilter() == jsonFileFilter)
			{
				if(! (selectedFile.endsWith(".json") || selectedFile.endsWith(".JSON") || selectedFile.endsWith(".Json")))
				{
					selectedFile = selectedFile + ".json";
				}
				StreamUtil.saveFile(new File(selectedFile), temporary.toJSON(false));
			}
			else if(fileChooser.getFileFilter() == hgfFileFilter)
			{
				if(! (selectedFile.endsWith(".hgf") || selectedFile.endsWith(".HGF") || selectedFile.endsWith(".Hgf")))
				{
					selectedFile = selectedFile + ".hgf";
				}
				StreamUtil.saveFile(new File(selectedFile), temporary.toHGF());
			}
			else
			{
				if(! (selectedFile.endsWith(".hgf") || selectedFile.endsWith(".HGF") || selectedFile.endsWith(".Hgf")))
				{
					selectedFile = selectedFile + ".hgf";				
				}
				StreamUtil.saveFile(new File(selectedFile), temporary.toHGF());
			}
		}
	}
	
	/**
	 * <p>이 메소드는 사용자가 메뉴에서 도구 - "file --> DB" 를 선택했을 때 호출됩니다. 파일 내용을 DB에 반영하는 마법사 창을 엽니다.</p>
	 * 
	 */
	protected void menuToolFileToDb()
	{
		fileToDbManager.open();
	}
	
	/**
	 * <p>이 메소드는 사용자가 메뉴에서 도구 - "DB --> file" 를 선택했을 때 호출됩니다. DB 쿼리 내용을 파일로 저장하는 마법사 창을 엽니다.</p>
	 * 
	 */
	protected void menuToolDbToFile()
	{
		dbToFileManager.open();
	}
	@Override
	protected void refreshEnabled()
	{
		boolean onOff = false;
		if(getDao() != null)
		{
			if(getDao().isAlive())
			{
				onOff = true;
			}
		}
		btDisconnect.setEnabled(onOff);
		if(runCombo.getSelectedIndex() <= 0 && (! onOff)) runCombo.setSelectedIndex(1);
		btCommit.setEnabled(onOff);
		btRollback.setEnabled(onOff);
		menuTransCommit.setEnabled(onOff);
		menuTransRollback.setEnabled(onOff);
		menuToolDbToFile.setEnabled(onOff);
		menuToolFileToDb.setEnabled(onOff);
		menuToolCreateTable.setEnabled(onOff);
		menuViewFunctionList.setEnabled(onOff);
		menuViewProcedureList.setEnabled(onOff);
		menuViewTableList.setEnabled(onOff);
		menuViewViewList.setEnabled(onOff);
		btToDb.setEnabled(onOff && (temporary != null));
	}
	/**
	 * <p>결과 란을 비웁니다.</p>
	 * 
	 */
	@Override
	public void clearResultArea()
	{
		resultArea.setText("");
	}
	@Override
	public void windowClosing(WindowEvent e)
	{
		Object ob = e.getSource();
		if(ob == frame)
		{
			close();
		}
	}

	@Override
	public void logTable(TableSet table, String spaces) 
	{
		super.logTable(table, spaces);
		
		detectTempChanged = false;
		
		TableSet oldOne = temporary;
		
		if(temporary != table) temporary = table;
		if(oldOne != null) oldOne.noMoreUse();
		
		if(resultTableModel != null)
		{
			try
			{
				resultTableModel.removeTableModelListener(this);
			}
			catch(Throwable e)
			{
				
			}
		}
		resultTableModel = new DefaultTableModel();
		resultTableModel.addTableModelListener(this);
		System.gc();
		
		for(int i=0; i<table.getColumns().size(); i++)
		{
			resultTableModel.addColumn(table.getColumn(i).getName() + "(" + table.getColumn(i).type() + ")");
		}
		for(int i=0; i<table.getRecordCount(); i++)
		{
			Record record = table.getRecord(i);
			String[] rowData = new String[record.columnCount()];
			for(int j=0; j<record.columnCount(); j++)
			{
				rowData[j] = String.valueOf(record.getDataOf(j));
			}
			resultTableModel.addRow(rowData);
		}
			
		resultTable.setModel(resultTableModel);
		refreshTableView();
		
		if(scriptRunner != null)
		{
			scriptRunner.put("shown_tableset", table);
			ScriptUtil.putObject("shown_tableset", table);
		}
		if(modules != null)
		{
			for(int i=0; i<modules.size(); i++)
			{
				if(modules.get(i) instanceof ScriptModule)
				{
					((ScriptModule) modules.get(i)).put("shown_tableset", table);
				}
			}
		}
		
		detectTempChanged = true;
		
		SwingTableSetView newView = null;
		if(DataUtil.parseBoolean(getOption("use_result_tab")))
		{	
			newView = new SwingTableSetView(this, table);
			tableSetViews.add(newView);
			resultTab.add(table.getName(), newView.getComponent());
			newView.refresh();
		}
		if(DataUtil.parseBoolean(getOption("use_result_window")))
		{	
			newView = new SwingTableSetViewDialog(this, table);
			tableSetViews.add(newView);
			newView.refresh();
			
			if(newView instanceof SwingTableSetViewDialog)
			{
				((SwingTableSetViewDialog) newView).getDialog().setVisible(true);
			}
		}
	}
	
	/**
	 * <p>이 메소드는 테이블 보기 화면을 불러왔을 때 호출됩니다. 데이터 존재 여부를 검사해 버튼을 활성화/비활성화합니다.</p>
	 * 
	 */
	protected void refreshTableView()
	{
		boolean temporaryExist = (temporary != null);
		
		btSave.setEnabled(temporaryExist);
		
		boolean onOff = false;
		if(getDao() != null)
		{
			if(getDao().isAlive())
			{
				onOff = true;
			}
		}
		
		btToDb.setEnabled(onOff && temporaryExist);
	}
	@Override
	public void logRaw(Object ob)
	{
		if(initialized)
		{
			// Main.println(ob);
			resultArea.append(String.valueOf(ob));
			resultArea.append("\n");
			resultArea.setCaretPosition(resultArea.getDocument().getLength() - 1);
			
			if(ob != null)
			{
				String[] splitLines = String.valueOf(ob).split("\n");
				if(splitLines.length >= 1)
				{
					statusField.setText(splitLines[splitLines.length - 1]);
				}
			}
			
			if(logBuffer != null)
			{
				try
				{
					logBuffer.write(String.valueOf(ob));
					logBuffer.newLine();
				}
				catch(Throwable e)
				{
					
				}
			}
		}
		else
		{
			Main.println(ob);
			beforeInitializedMessage = beforeInitializedMessage + ob + "\n";
		}
	}

	@Override
	public void logRawNotLn(Object ob)
	{
		if(initialized)
		{
			// System.out.print(ob);
			resultArea.append(String.valueOf(ob));
			resultArea.setCaretPosition(resultArea.getDocument().getLength() - 1);
			
			if(ob != null)
			{
				String[] splitLines = String.valueOf(ob).split("\n");
				if(splitLines.length >= 1)
				{
					statusField.setText(statusField.getText() + splitLines[splitLines.length - 1]);
				}
			}
			
			if(logBuffer != null)
			{
				try
				{
					logBuffer.write(String.valueOf(ob));
				}
				catch(Throwable e)
				{
					
				}
			}
		}
		else 
		{
			System.out.print(ob);
			beforeInitializedMessage = beforeInitializedMessage + ob;
		}
	}
	
	/**
	 * <p>되돌리기 / 다시 적용 메뉴 비활성화 필요 여부를 다시 검사해 적용합니다.</p>
	 * 
	 */
	protected void refreshUndoing()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				menuToolUndo.setEnabled(scriptArea.canUndo());
				menuToolRedo.setEnabled(scriptArea.canRedo());
			}
		});
	}

	@Override
	public boolean askYes(String msg)
	{
		return (JOptionPane.showConfirmDialog(frame, msg, msg, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION);
	}

	@Override
	public String askInput(String msg, boolean isShort)
	{
		if(isShort) return JOptionPane.showInputDialog(frame, msg);
		else
		{
			inputDialog.open(msg);
			return inputDialog.results;
		}
	}
	@Override
	public void keyPressed(KeyEvent e)
	{
		Object ob = e.getSource();
		if(ob == scriptArea.getTextArea())
		{
			refreshUndoing();
			scriptArea.keyPressed(e);
		}
	}
	@Override
	public void tableChanged(TableModelEvent e)
	{
		Object ob = e.getSource();
		if(ob == resultTableModel)
		{
			if(detectTempChanged) onTableModified(e);
		}
	}
	@Override
	public void setQueryAreaText(String str)
	{
		scriptArea.setText(str);
	}
	@Override
	public String getQueryAreaText()
	{
		return scriptArea.getText();
	}
	@Override
	public void mousePressed(MouseEvent e)
	{
		if(scriptArea.checkEventOccured(e))
		{
			scriptArea.mousePressed(e);
		}
	}
	@Override
	public Frame getFrame()
	{
		return frame;
	}
	
	@Override
	public Window newDialog(boolean needModal)
	{
		if(needModal)
		{
			return new JDialog(frame, true);
		}
		return new JDialog(frame);
	}
	
	@Override
	public boolean askModuleAccept(Module module)
	{
		licenseView.open(module);
		return licenseView.accepted();
	}
	
	/**
	 * <p>해당 고유 ID를 갖는 테이블 셋 뷰를 닫습니다.</p>
	 * 
	 * @param uniqueId : 고유 ID값
	 */
	public void closeTableSetView(long uniqueId)
	{
		int i = 0;
		while(i < tableSetViews.size())
		{
			SwingTableSetView target = tableSetViews.get(i);
			if(target.getUniqueId() == uniqueId)
			{
				resultTab.remove(target.getComponent());
				target.noMoreUse();
				tableSetViews.remove(i);
				
				i = 0;
				continue;
			}
			
			i++;
		}
		System.gc();
	}
	
	@Override
	protected void initModules()
	{
		int i=0;
		while(i < modules.size())
		{	
			if(! (modules.get(i) instanceof GUIModule))
			{
				modules.remove(i);
				i = 0;
			}
			else
			{
				try
				{
					modules.get(i).init();
				}
				catch(Throwable t)
				{
					logError(t, applyStringTable("On loading module") + " : " + modules.get(i).getName());
				}
				i++;
			}
		}
	}
	
	/**
	 * <p>분석 함수 대화 상자를 엽니다.</p>
	 * 
	 * @param tableSets : 분석 함수 대화 상자에 추가로 보일 테이블 셋 리스트
	 */
	public void openAnalyzeDialog(List<TableSet> tableSets)
	{
		functionDialog.open(scriptRunner, tableSets);
	}
	
	private void menuToolFunction()
	{
		openAnalyzeDialog(null);
	}
	private void menuToolCreateTool()
	{
		createTableView();
	}
	private void menuFileSaveConsole()
	{
		onSaveConsole();
	}
	private void menuFileLoad()
	{
		loadScript();
	}
	private void menuFileSave()
	{
		saveScript();
	}
	
	// 이 곳은 외부 모듈에서 호출할 수 있어야 하는 컴포넌트들을 메소드에 매칭하는 곳입니다.
	// TODO
	
	public void menuToolFunction(Module module, AWTEvent e)
	{
		if(! ModuleUtil.checkAccepted(module)) throw new SecurityException(applyStringTable("Module") + " " + module.getName() + "(" + module.getModuleId() + " "
				+ applyStringTable("is not authorized") + ".");
		openAnalyzeDialog(null);
		refreshEnabled();
		afterEvent("analyzeFunction", e);
	}
	public void menuToolCreateTool(Module module, AWTEvent e)
	{
		if(! ModuleUtil.checkAccepted(module)) throw new SecurityException(applyStringTable("Module") + " " + module.getName() + "(" + module.getModuleId() + " "
				+ applyStringTable("is not authorized") + ".");
		createTableView();
		refreshEnabled();
		afterEvent("createTableView", e);
	}
	public void menuFileSaveConsole(Module module, AWTEvent e)
	{
		if(! ModuleUtil.checkAccepted(module)) throw new SecurityException(applyStringTable("Module") + " " + module.getName() + "(" + module.getModuleId() + " "
				+ applyStringTable("is not authorized") + ".");
		onSaveConsole();
		refreshEnabled();
		afterEvent("saveconsole", e);
	}
	public void menuFileLoad(Module module, AWTEvent e)
	{
		if(! ModuleUtil.checkAccepted(module)) throw new SecurityException(applyStringTable("Module") + " " + module.getName() + "(" + module.getModuleId() + " "
				+ applyStringTable("is not authorized") + ".");
		loadScript();
		refreshEnabled();
		afterEvent("loadfile", e);
	}
	public void menuFileSave(Module module, AWTEvent e)
	{
		if(! ModuleUtil.checkAccepted(module)) throw new SecurityException(applyStringTable("Module") + " " + module.getName() + "(" + module.getModuleId() + " "
				+ applyStringTable("is not authorized") + ".");
		saveScript();
		refreshEnabled();
		afterEvent("savefile", e);
	}
	
	public void btCommit(Module module, AWTEvent e)
	{
		if(! ModuleUtil.checkAccepted(module)) throw new SecurityException(applyStringTable("Module") + " " + module.getName() + "(" + module.getModuleId() + " "
				+ applyStringTable("is not authorized") + ".");
		btCommit();
		refreshEnabled();
		afterEvent("commit", e);
	}
	
	public void btRollback(Module module, AWTEvent e)
	{
		if(! ModuleUtil.checkAccepted(module)) throw new SecurityException(applyStringTable("Module") + " " + module.getName() + "(" + module.getModuleId() + " "
				+ applyStringTable("is not authorized") + ".");
		btRollback();
		refreshEnabled();
		afterEvent("rollback", e);
	}
	
	public void btRunScript(Module module, AWTEvent e)
	{
		if(! ModuleUtil.checkAccepted(module)) throw new SecurityException(applyStringTable("Module") + " " + module.getName() + "(" + module.getModuleId() + " "
				+ applyStringTable("is not authorized") + ".");
		btRunScriptAction(false);
		refreshEnabled();
		afterEvent("run", e);
	}
	
	public void btCancelScript(Module module, AWTEvent e)
	{
		if(! ModuleUtil.checkAccepted(module)) throw new SecurityException(applyStringTable("Module") + " " + module.getName() + "(" + module.getModuleId() + " "
				+ applyStringTable("is not authorized") + ".");
		btCancelScript();
		refreshEnabled();
		afterEvent("cancel", e);
	}
	
	public void menuTransRun(Module module, AWTEvent e)
	{
		if(! ModuleUtil.checkAccepted(module)) throw new SecurityException(applyStringTable("Module") + " " + module.getName() + "(" + module.getModuleId() + " "
				+ applyStringTable("is not authorized") + ".");
		btRunScriptAction(false);
		refreshEnabled();
		afterEvent("run_selected", e);
	}
	
	// 이 곳은 외부 모듈에서 호출할 수 있어야 하는 컴포넌트에 대한 Getter 들을 위치하는 곳입니다.
	
	public JMenuItem getMenuToolDbToFile(Module module)
	{
		if(! ModuleUtil.checkAccepted(module)) throw new SecurityException(applyStringTable("Module") + " " + module.getName() + "(" + module.getModuleId() + " "
				+ applyStringTable("is not authorized") + ".");
		return menuToolDbToFile;
	}
	public JMenuItem getMenuToolFileToDb(Module module)
	{
		if(! ModuleUtil.checkAccepted(module)) throw new SecurityException(applyStringTable("Module") + " " + module.getName() + "(" + module.getModuleId() + " "
				+ applyStringTable("is not authorized") + ".");
		return menuToolFileToDb;
	}
	public JMenuItem getMenuFileSaveConsole(Module module)
	{
		if(! ModuleUtil.checkAccepted(module)) throw new SecurityException(applyStringTable("Module") + " " + module.getName() + "(" + module.getModuleId() + " "
				+ applyStringTable("is not authorized") + ".");
		return menuFileSaveConsole;
	}
	public JMenuItem getMenuTransRun(Module module)
	{
		if(! ModuleUtil.checkAccepted(module)) throw new SecurityException(applyStringTable("Module") + " " + module.getName() + "(" + module.getModuleId() + " "
				+ applyStringTable("is not authorized") + ".");
		return menuTransRun;
	}
	public JMenuItem getMenuTransCommit(Module module)
	{
		if(! ModuleUtil.checkAccepted(module)) throw new SecurityException(applyStringTable("Module") + " " + module.getName() + "(" + module.getModuleId() + " "
				+ applyStringTable("is not authorized") + ".");
		return menuTransCommit;
	}
	public JMenuItem getMenuTransRollback(Module module)
	{
		if(! ModuleUtil.checkAccepted(module)) throw new SecurityException(applyStringTable("Module") + " " + module.getName() + "(" + module.getModuleId() + " "
				+ applyStringTable("is not authorized") + ".");
		return menuTransRollback;
	}
	public JMenuItem getMenuViewTableList(Module module)
	{
		if(! ModuleUtil.checkAccepted(module)) throw new SecurityException(applyStringTable("Module") + " " + module.getName() + "(" + module.getModuleId() + " "
				+ applyStringTable("is not authorized") + ".");
		return menuViewTableList;
	}
	public JMenuItem getMenuToolPreference(Module module)
	{
		if(! ModuleUtil.checkAccepted(module)) throw new SecurityException(applyStringTable("Module") + " " + module.getName() + "(" + module.getModuleId() + " "
				+ applyStringTable("is not authorized") + ".");
		return menuToolPreference;
	}
	public JMenuItem getMenuFileLoadJar(Module module)
	{
		if(! ModuleUtil.checkAccepted(module)) throw new SecurityException(applyStringTable("Module") + " " + module.getName() + "(" + module.getModuleId() + " "
				+ applyStringTable("is not authorized") + ".");
		return menuFileLoadJar;
	}
	public JMenuItem getMenuToolUndo(Module module)
	{
		if(! ModuleUtil.checkAccepted(module)) throw new SecurityException(applyStringTable("Module") + " " + module.getName() + "(" + module.getModuleId() + " "
				+ applyStringTable("is not authorized") + ".");
		return menuToolUndo;
	}
	public JMenuItem getMenuToolRedo(Module module)
	{
		if(! ModuleUtil.checkAccepted(module)) throw new SecurityException(applyStringTable("Module") + " " + module.getName() + "(" + module.getModuleId() + " "
				+ applyStringTable("is not authorized") + ".");
		return menuToolRedo;
	}
	public JMenuItem getMenuTransRunSelected(Module module)
	{
		if(! ModuleUtil.checkAccepted(module)) throw new SecurityException(applyStringTable("Module") + " " + module.getName() + "(" + module.getModuleId() + " "
				+ applyStringTable("is not authorized") + ".");
		return menuTransRunSelected;
	}
	public JMenuItem getMenuViewViewList(Module module)
	{
		if(! ModuleUtil.checkAccepted(module)) throw new SecurityException(applyStringTable("Module") + " " + module.getName() + "(" + module.getModuleId() + " "
				+ applyStringTable("is not authorized") + ".");
		return menuViewViewList;
	}
	public JMenuItem getMenuViewProcedureList(Module module)
	{
		if(! ModuleUtil.checkAccepted(module)) throw new SecurityException(applyStringTable("Module") + " " + module.getName() + "(" + module.getModuleId() + " "
				+ applyStringTable("is not authorized") + ".");
		return menuViewProcedureList;
	}
	public JMenuItem getMenuViewFunctionList(Module module)
	{
		if(! ModuleUtil.checkAccepted(module)) throw new SecurityException(applyStringTable("Module") + " " + module.getName() + "(" + module.getModuleId() + " "
				+ applyStringTable("is not authorized") + ".");
		return menuViewFunctionList;
	}
	public JMenuItem getMenuFileSave(Module module)
	{
		if(! ModuleUtil.checkAccepted(module)) throw new SecurityException(applyStringTable("Module") + " " + module.getName() + "(" + module.getModuleId() + " "
				+ applyStringTable("is not authorized") + ".");
		return menuFileSave;
	}
	public JMenuItem getMenuFileLoad(Module module)
	{
		if(! ModuleUtil.checkAccepted(module)) throw new SecurityException(applyStringTable("Module") + " " + module.getName() + "(" + module.getModuleId() + " "
				+ applyStringTable("is not authorized") + ".");
		return menuFileLoad;
	}
	public JMenuItem getMenuViewSearch(Module module)
	{
		if(! ModuleUtil.checkAccepted(module)) throw new SecurityException(applyStringTable("Module") + " " + module.getName() + "(" + module.getModuleId() + " "
				+ applyStringTable("is not authorized") + ".");
		return menuViewSearch;
	}
	public JMenuItem getMenuToolFunction(Module module)
	{
		if(! ModuleUtil.checkAccepted(module)) throw new SecurityException(applyStringTable("Module") + " " + module.getName() + "(" + module.getModuleId() + " "
				+ applyStringTable("is not authorized") + ".");
		return menuToolFunction;
	}
	
	public JButton getBtRollback(Module module)
	{
		if(! ModuleUtil.checkAccepted(module)) throw new SecurityException(applyStringTable("Module") + " " + module.getName() + "(" + module.getModuleId() + " "
				+ applyStringTable("is not authorized") + ".");
		return btRollback;
	}
	
	public JButton getBtCommit(Module module)
	{
		if(! ModuleUtil.checkAccepted(module)) throw new SecurityException(applyStringTable("Module") + " " + module.getName() + "(" + module.getModuleId() + " "
				+ applyStringTable("is not authorized") + ".");
		return btCommit;
	}
	
	public JButton getBtRunScript(Module module)
	{
		if(! ModuleUtil.checkAccepted(module)) throw new SecurityException(applyStringTable("Module") + " " + module.getName() + "(" + module.getModuleId() + " "
				+ applyStringTable("is not authorized") + ".");
		return btRunScript;
	}
	public JButton getBtCancelScript(Module module)
	{
		if(! ModuleUtil.checkAccepted(module)) throw new SecurityException(applyStringTable("Module") + " " + module.getName() + "(" + module.getModuleId() + " "
				+ applyStringTable("is not authorized") + ".");
		return btCancelScript;
	}
	@Override
	public void alert(Object ob)
	{
		super.alert(ob);
		JOptionPane.showMessageDialog(frame, ob);
	}
	
	@Override
	public void applyFavorites()
	{
		if(favorites.size() <= 0)
		{
			if(menuFav != null)
			{
				menuBar.remove(menuFav);
				menuFav.removeAll();
				menuFav = null;
			}
		}
		else
		{
			if(menuFav == null)
			{
				menuFav = new JMenu(applyStringTable("Favorites"));
			}
			else menuFav.removeAll();
			for(AbstractFavorites f : favorites)
			{
				final AbstractFavorites fav = f;
				JMenuItem newMenuItem = new JMenuItem(fav.getName());
				newMenuItem.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						try
						{
							runFavorite(fav);
						}
						catch(Throwable t)
						{
							logError(t, applyStringTable("On execute favorites"));
						}
					}
				});
				menuFav.add(newMenuItem);
				Integer shortcuts = fav.getShortcut();
				Integer mask = fav.getMask();
				if(shortcuts != null)
				{
					if(mask != null)
					{
						newMenuItem.setAccelerator(KeyStroke.getKeyStroke(shortcuts.intValue(), mask.intValue()));
					}
					else
					{
						newMenuItem.setAccelerator(KeyStroke.getKeyStroke((char) (shortcuts.intValue() - KeyEvent.VK_0 + (int) '0')));
					}
				}
			}
			menuTool.add(menuFav);
		}
	}
	
	@Override
	public byte[] askFile(String msg) throws IOException
	{
		JFileChooser newFileChooser = new JFileChooser();
		newFileChooser.setDialogTitle(msg);
		int selection = newFileChooser.showOpenDialog(getFrame());
		if(selection == JFileChooser.APPROVE_OPTION)
		{
			File file = newFileChooser.getSelectedFile();
			return StreamUtil.readBytes(file);
		}
		return null;
	}
	
	@Override
	public void askSave(String msg, byte[] bytes) throws IOException
	{
		JFileChooser newFileChooser = new JFileChooser();
		newFileChooser.setDialogTitle(msg);
		int selection = newFileChooser.showSaveDialog(getFrame());
		if(selection == JFileChooser.APPROVE_OPTION)
		{
			File file = newFileChooser.getSelectedFile();
			StreamUtil.saveBytes(file, bytes);
		}
	}
}