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

import hjow.hgtable.Main;
import hjow.hgtable.Manager;
import hjow.hgtable.ui.GUIManager;
import hjow.hgtable.ui.module.GUIDialogModule;
import hjow.hgtable.ui.module.ModuleDataPack;
import hjow.hgtable.ui.swing.CodeEditorPane;
import hjow.hgtable.ui.swing.UIComboBox;
import hjow.hgtable.ui.swing.UISyntaxView;
import hjow.hgtable.util.DataUtil;
import hjow.hgtable.util.ModuleUtil;
import hjow.hgtable.util.StreamUtil;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.io.File;
import java.util.Hashtable;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;

/**
 * <p>모듈 개발 툴입니다.</p>
 * 
 * @author HJOW
 *
 */
public class CreateModuleTool extends GUIDialogModule
{
	private static final long serialVersionUID = -5770047314413562998L;
	public static final transient long sid = serialVersionUID;
	protected JDialog dialog;
	protected JPanel mainPanel;
	protected CardLayout mainLayout;
	protected JPanel inputPanel;
	protected JPanel reviewPanel;
	protected JPanel upPanel;
	protected JPanel centerPanel;
	protected JPanel downPanel;
	protected CodeEditorPane scriptArea;
	protected JPanel reviewControlPanel;
	protected JTabbedPane tabPanel;
	protected JPanel mainInfoPanel;
	protected JPanel initInfoPanel;
	protected JPanel afterInfoPanel;
	protected JPanel refreshInfoPanel;
	protected JPanel finalizeInfoPanel;
	protected JPanel showMenuInfoPanel;
	protected JPanel actMenuInfoPanel;
	protected CodeEditorPane initInfoArea;
	protected CodeEditorPane afterInfoArea;
	protected CodeEditorPane refreshInfoArea;
	protected CodeEditorPane finalizeInfoArea;
	protected CodeEditorPane showMenuInfoArea;
	protected CodeEditorPane actMenutInfoArea;
	protected JPanel[] pns;
	protected JLabel[] lbs;
	protected JTextField nameField;
	protected UIComboBox typeField;
	protected JButton btClose;
	protected JButton btNext;
	protected JButton btClose2;
	protected JButton btBefore;
	protected JButton btSave;
	protected ModuleDataPack target;
	protected JSpinner idField;
	protected JPanel mainInfoUpPanel;
	protected CodeEditorPane descriptionArea;
	protected JPanel optionPanel;
	protected DefaultTableModel optionModel;
	protected JTable optionTable;
	protected JScrollPane optionScroll;
	protected JPanel optionControlPanel;
	protected JButton btNewColumn;
	protected JButton btCleanOption;
	protected JFileChooser fileDialog;
	protected FileFilter hgmFilter;
	protected FileFilter hgmzFilter;
	protected JButton btLoad;
	protected FileFilter hgmbFilter;

	/**
	 * <p>기본 생성자입니다. 실제 사용을 권장하지는 않습니다.</p>
	 * 
	 */
	public CreateModuleTool()
	{
		setModuleId(serialVersionUID);
	}
	
	/**
	 * <p>모듈 생성 툴 모듈을 생성합니다. GUIManager 객체가 필요합니다.</p>
	 * 
	 * @param manager : 매니저 객체 (GUIManager 객체가 아니면 초기화 중 문제가 발생함)
	 */
	public CreateModuleTool(GUIManager manager)
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
		
		tabPanel = new JTabbedPane();
		centerPanel.add(tabPanel, BorderLayout.CENTER);
		
		mainInfoPanel = new JPanel();
		tabPanel.add(trans("Basic Information"), mainInfoPanel);
		
		mainInfoPanel.setLayout(new BorderLayout());
		
		mainInfoUpPanel = new JPanel();
//		descriptionArea = new LineNumberTextArea(dialog);
		descriptionArea = new UISyntaxView(dialog);
		
		pns = new JPanel[3];
		lbs = new JLabel[pns.length];
		mainInfoUpPanel.setLayout(new GridLayout(pns.length, 1));
		for(int i=0; i<pns.length; i++)
		{
			pns[i] = new JPanel();
			lbs[i] = new JLabel();
			pns[i].setLayout(new FlowLayout());
			pns[i].add(lbs[i]);
			
			mainInfoUpPanel.add(pns[i]);
		}
		
		nameField = new JTextField(20);
		lbs[0].setText(trans("Name"));
		pns[0].add(nameField);
		
		Vector<String> moduleType = new Vector<String>();
		moduleType.add("Dialog");
		moduleType.add("Panel");
		moduleType.add("Console");
		
		typeField = new UIComboBox(moduleType);
		typeField.setEditable(true);
		lbs[1].setText(trans("Type"));
		pns[1].add(typeField);
		
		idField = new JSpinner(new SpinnerNumberModel(new Long(new Random().nextLong()), new Long(Long.MIN_VALUE + 1), new Long(Long.MAX_VALUE - 1), new Long(1)));
		lbs[2].setText(trans("Unique ID"));
		pns[2].add(idField);
		
		mainInfoPanel.add(mainInfoUpPanel, BorderLayout.NORTH);
		mainInfoPanel.add(descriptionArea.getComponent(), BorderLayout.CENTER);
		
		initInfoPanel = new JPanel();
		tabPanel.add(trans("Init"), initInfoPanel);
		initInfoPanel.setLayout(new BorderLayout());
		initInfoArea = new UISyntaxView(dialog);
		initInfoArea.setText(initAreaDefaults());
		initInfoPanel.add(initInfoArea.getComponent(), BorderLayout.CENTER);
		
		afterInfoPanel = new JPanel();
		tabPanel.add(trans("After inited"), afterInfoPanel);
		afterInfoPanel.setLayout(new BorderLayout());
		afterInfoArea = new UISyntaxView(dialog);
		afterInfoArea.setText(afterInitDefaults());
		afterInfoPanel.add(afterInfoArea.getComponent(), BorderLayout.CENTER);
		
		refreshInfoPanel = new JPanel();
		tabPanel.add(trans("Refresh"), refreshInfoPanel);
		refreshInfoPanel.setLayout(new BorderLayout());
		refreshInfoArea = new UISyntaxView(dialog);
		refreshInfoArea.setText(refreshDefaults());
		refreshInfoPanel.add(refreshInfoArea.getComponent(), BorderLayout.CENTER);
		
		finalizeInfoPanel = new JPanel();
		tabPanel.add(trans("Finalize"), finalizeInfoPanel);
		finalizeInfoPanel.setLayout(new BorderLayout());
		finalizeInfoArea = new UISyntaxView(dialog);
		finalizeInfoArea.setText(finalizeDefaults());
		finalizeInfoPanel.add(finalizeInfoArea.getComponent(), BorderLayout.CENTER);
		
		showMenuInfoPanel = new JPanel();
		tabPanel.add(trans("Show Menu"), showMenuInfoPanel);
		showMenuInfoPanel.setLayout(new BorderLayout());
		showMenuInfoArea = new UISyntaxView(dialog);
		showMenuInfoArea.setText(showMenuDefaults());
		showMenuInfoPanel.add(showMenuInfoArea.getComponent(), BorderLayout.CENTER);
		
		actMenuInfoPanel = new JPanel();
		tabPanel.add(trans("Act Menu"), actMenuInfoPanel);
		actMenuInfoPanel.setLayout(new BorderLayout());
		actMenutInfoArea = new UISyntaxView(dialog);
		actMenutInfoArea.setText(actMenuDefaults());
		actMenuInfoPanel.add(actMenutInfoArea.getComponent(), BorderLayout.CENTER);
		
		optionPanel = new JPanel();
		tabPanel.add(trans("Options"), optionPanel);
		optionPanel.setLayout(new BorderLayout());
		optionModel = new DefaultTableModel();
		optionModel.addColumn(trans("KEY"));
		optionModel.addColumn(trans("VALUE"));
		optionTable = new JTable(optionModel);
		optionScroll = new JScrollPane(optionTable);
		optionPanel.add(optionScroll, BorderLayout.CENTER);
		optionControlPanel = new JPanel();
		optionControlPanel.setLayout(new FlowLayout());
		optionPanel.add(optionControlPanel, BorderLayout.SOUTH);
		
		btNewColumn = new JButton(trans("Add new option"));
		btCleanOption = new JButton(trans("Clean"));
		
		optionControlPanel.add(btNewColumn);
		optionControlPanel.add(btCleanOption);
		
		downPanel.setLayout(new FlowLayout());
		
		btClose = new JButton(trans("Close"));
		btLoad = new JButton(trans("Load"));
		btNext = new JButton(trans("Next"));
		
		downPanel.add(btClose);
		downPanel.add(btLoad);
		downPanel.add(btNext);
		
		reviewControlPanel = new JPanel();
		
		scriptArea = new UISyntaxView(dialog);
//		scriptArea = new LineNumberTextArea(dialog);
		reviewPanel.add(scriptArea.getComponent(), BorderLayout.CENTER);
		reviewPanel.add(reviewControlPanel, BorderLayout.SOUTH);
		
		reviewControlPanel.setLayout(new FlowLayout());
		
		btBefore = new JButton(trans("Before"));
		btClose2 = new JButton(trans("Close"));
		btSave = new JButton(trans("Save"));
		
		reviewControlPanel.add(btBefore);
		reviewControlPanel.add(btClose2);
		reviewControlPanel.add(btSave);
		
		if(DataUtil.isNotEmpty(Manager.getOption("config_path"))
				&& new File(Manager.getOption("config_path")).exists())
		{
			fileDialog = new JFileChooser(new File(Manager.getOption("config_path")));
		}
		else fileDialog = new JFileChooser();
		
		hgmFilter = new FileFilter()
		{
			@Override
			public String getDescription()
			{
				return trans("Module Scripts (*.hgm)");
			}
			
			@Override
			public boolean accept(File f)
			{
				return f.getAbsolutePath().endsWith(".hgm") || f.getAbsolutePath().endsWith(".HGM") || f.getAbsolutePath().endsWith(".Hgm");
			}
		};
		
		hgmzFilter = new FileFilter()
		{
			@Override
			public String getDescription()
			{
				return trans("Compressed Module Scripts (*.hgmz)");
			}
			
			@Override
			public boolean accept(File f)
			{
				return f.getAbsolutePath().endsWith(".hgmz") || f.getAbsolutePath().endsWith(".HGMZ") || f.getAbsolutePath().endsWith(".Hgmz");
			}
		};
		
		hgmbFilter = new FileFilter()
		{
			@Override
			public String getDescription()
			{
				return trans("Binary Module Scripts (*.hgmb)");
			}
			
			@Override
			public boolean accept(File f)
			{
				return f.getAbsolutePath().endsWith(".hgmz") || f.getAbsolutePath().endsWith(".HGMZ") || f.getAbsolutePath().endsWith(".Hgmz");
			}
		};
		
		fileDialog.setFileFilter(hgmFilter);
		fileDialog.addChoosableFileFilter(hgmzFilter);
		fileDialog.addChoosableFileFilter(hgmbFilter);
		
		dialog.addWindowListener(this);
		btNext.addActionListener(this);
		btLoad.addActionListener(this);
		btClose.addActionListener(this);
		btClose2.addActionListener(this);
		btBefore.addActionListener(this);
		btSave.addActionListener(this);
		btNewColumn.addActionListener(this);
		btCleanOption.addActionListener(this);
		typeField.addItemListener(this);
	}
	
	/**
	 * <p>모듈 종류에 따라 탭을 활성화하거나 잠급니다.</p>
	 * 
	 */
	protected void typeFieldSelected()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				String selectedType = String.valueOf(typeField.getSelectedItem());
				
				if(selectedType.equalsIgnoreCase("Dialog") || selectedType.equalsIgnoreCase("Panel"))
				{
					for(int i=0; i<tabPanel.getTabCount(); i++)
					{
						if(tabPanel.getTitleAt(i).equalsIgnoreCase(trans("Show Menu"))
								|| tabPanel.getTitleAt(i).equalsIgnoreCase(trans("Act Menu")))
						{
							tabPanel.setEnabledAt(i, false);
						}
						else
						{
							tabPanel.setEnabledAt(i, true);
						}
					}
				}
				else if(selectedType.equalsIgnoreCase("Console"))
				{
					for(int i=0; i<tabPanel.getTabCount(); i++)
					{
						if(tabPanel.getTitleAt(i).equalsIgnoreCase(trans("Refresh")))
						{
							tabPanel.setEnabledAt(i, false);
						}
						else
						{
							tabPanel.setEnabledAt(i, true);
						}
					}
				}
				else
				{
					for(int i=0; i<tabPanel.getTabCount(); i++)
					{
						tabPanel.setEnabledAt(i, true);
					}
				}
			}
		});
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		Object ob = e.getSource();
		if(ob == btClose || ob == btClose2)
		{
			close();
		}
		else if(ob == btNext)
		{
			btNext();
		}
		else if(ob == btBefore)
		{
			btBefore();
		}
		else if(ob == btNewColumn)
		{
			btNewColumn();
		}
		else if(ob == btCleanOption)
		{
			btCleanOption();
		}
		else if(ob == btSave)
		{
			btSave();
		}
		else if(ob == btLoad)
		{
			btLoad();
		}
		else super.actionPerformed(e);
	}

	@Override
	public void itemStateChanged(ItemEvent e)
	{
		Object ob = e.getSource();
		if(ob == typeField)
		{
			typeFieldSelected();
		}
	}

	/**
	 * <p>모듈 데이터 팩 객체에서 데이터를 가져와 컴포넌트들에 입력합니다.</p>
	 * 
	 * @param pack : 모듈 데이터 팩
	 */
	protected void setData(ModuleDataPack pack)
	{
		target = pack;
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				nameField.setText(target.getName());
				typeField.setSelectedItem(target.getModuleType());
				if(DataUtil.isNotEmpty(target.getModuleId())) idField.setValue(target.getModuleId());
				descriptionArea.setText(target.getDescription());
				initInfoArea.setText(target.getInitScripts());
				afterInfoArea.setText(target.getAfterInitScripts());
				refreshInfoArea.setText(target.getRefreshScripts());
				finalizeInfoArea.setText(target.getFinalizeScripts());
				showMenuInfoArea.setText(target.getShowMenuScripts());
				actMenutInfoArea.setText(target.getActMenuScripts());
				
				optionModel = new DefaultTableModel();
				optionModel.addColumn(trans("KEY"));
				optionModel.addColumn(trans("VALUE"));
				optionTable.setModel(optionModel);
				
				if(DataUtil.isNotEmpty(target.getMoreOptions()))
				{
					Set<String> keys = target.getMoreOptions().keySet();
					Vector<String> rows = null;
					for(String s : keys)
					{
						rows = new Vector<String>();
						rows.add(s);
						rows.add(target.getMoreOptions().get(s));
						optionModel.addRow(rows);
					}
				}
			}
		});
	}

	/**
	 * <p>사용자가 돌아가기 버튼을 눌렀을 때 호출됩니다.</p>
	 * 
	 */
	protected void btBefore()
	{
		target = ModuleUtil.inputData(scriptArea.getText(), manager);
		setData(target);
		
		mainLayout.show(mainPanel, "INPUT");
	}

	/**
	 * <p>사용자가 불러오기 버튼을 눌렀을 때 호출됩니다.</p>
	 * 
	 */
	protected void btLoad()
	{
		int selection = fileDialog.showOpenDialog(dialog);
		if(selection == JFileChooser.APPROVE_OPTION)
		{
			File reads = fileDialog.getSelectedFile();
			String gets = "";
			
			if(fileDialog.getFileFilter() == hgmFilter)
			{
				gets = StreamUtil.readText(reads, "UTF-8");
			}
			else if(fileDialog.getFileFilter() == hgmzFilter)
			{
				gets = StreamUtil.readText(reads, "UTF-8", true);
			}
			
			target = ModuleUtil.inputData(gets, manager);
			setData(target);
		}
	}
	
	
	/**
	 * <p>사용자가 저장 버튼을 눌렀을 때 호출됩니다.</p>
	 * 
	 */
	protected void btSave()
	{
		File selectedFile = null;
		
		int selection = fileDialog.showSaveDialog(dialog);
		if(selection == JFileChooser.APPROVE_OPTION)
		{
			selectedFile = fileDialog.getSelectedFile();
			boolean useHgmz = false;
			boolean useBinary = false;
			
			if(fileDialog.getFileFilter() == hgmFilter)
			{
				useHgmz = false;
			}
			else if(fileDialog.getFileFilter() == hgmzFilter)
			{
				useHgmz = true;
			}
			else if(fileDialog.getFileFilter() == hgmbFilter)
			{
				useBinary = true;
			}
			
			if(useBinary) StreamUtil.saveFile(selectedFile, ModuleUtil.inputData(scriptArea.getText(), manager), true);
			else if(useHgmz) StreamUtil.saveFile(selectedFile, scriptArea.getText(), "UTF-8", true);
			else StreamUtil.saveFile(selectedFile, scriptArea.getText(), "UTF-8");
			
			close();
		}
	}
	
	/**
	 * <p>사용자가 새 옵션 추가 버튼을 눌렀을 때 호출됩니다.</p>
	 * 
	 */
	protected void btCleanOption()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				int i = 0;
				while(i<optionModel.getRowCount())
				{
					boolean isAllEmpty = true;
					for(int j=0; j<2; j++)
					{
						if(DataUtil.isNotEmpty(optionModel.getValueAt(i, j)))
						{
							isAllEmpty = false;
							break;
						}
					}
					if(isAllEmpty)
					{
						optionModel.removeRow(i);
						i = 0;
					}
					else
					{
						i++;
					}
					
					if(! (Main.checkInterrupt(this, "btCleanOption")))
					{
						break;
					}
				}
			}
		});
	}

	/**
	 * <p>사용자가 옵션 패널에서 추가 버튼을 눌렀을 때 호출됩니다.</p>
	 * 
	 */
	protected void btNewColumn()
	{
		Vector<String> newRowData = new Vector<String>();
		newRowData.add("");
		newRowData.add("");
		optionModel.addRow(newRowData);
	}

	/**
	 * <p>사용자가 다음 버튼을 눌렀을 때 호출됩니다.</p>
	 * 
	 */
	protected void btNext()
	{
		target = new ModuleDataPack();
		
		target.setName(nameField.getText());
		target.setModuleType(String.valueOf(typeField.getSelectedItem()));
		target.setModuleId(new Long((long) Double.parseDouble(String.valueOf(idField.getValue()))));
		target.setInitScripts(initInfoArea.getText());
		target.setAfterInitScripts(afterInfoArea.getText());
		target.setRefreshScripts(refreshInfoArea.getText());
		target.setFinalizeScripts(finalizeInfoArea.getText());
		target.setShowMenuScripts(showMenuInfoArea.getText());
		target.setActMenuScripts(actMenutInfoArea.getText());
		target.setDescription(descriptionArea.getText());
		
		Map<String, String> moreOptions = new Hashtable<String, String>();
		
		for(int i=0; i<optionModel.getRowCount(); i++)
		{
			moreOptions.put(String.valueOf(optionModel.getValueAt(i, 0)), String.valueOf(optionModel.getValueAt(i, 1)));
		}
		
		target.setMoreOptions(moreOptions);
		
		scriptArea.setText(target.toString());
		mainLayout.show(mainPanel, "REVIEW");
	}

	@Override
	public void doAfterInitialize()
	{
		
	}
	
	@Override
	public void refresh(Map<String, Object> additionalData)
	{
		
	}
		
	@Override
	public String getName()
	{
		return trans("Module Developer");
	}
	
	@Override
	public Dialog getDialog()
	{
		return dialog;
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
		typeFieldSelected();
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{	
				mainLayout.show(mainPanel, "INPUT");
			}
		});
	}
	
	@Override
	public void noMoreUse()
	{
		scriptArea.noMoreUse();
		initInfoArea.noMoreUse();
		afterInfoArea.noMoreUse();
		refreshInfoArea.noMoreUse();
		finalizeInfoArea.noMoreUse();
		showMenuInfoArea.noMoreUse();
		actMenutInfoArea.noMoreUse();
		descriptionArea.noMoreUse();
		
		super.noMoreUse();
	}
	
	protected static String initAreaDefaults()
	{
		return "// " + Manager.applyStringTable("Input the scripts which will be run to initialize the module.") + "\n";
	}
	protected static String afterInitDefaults()
	{
		return "// " + Manager.applyStringTable("Input the scripts which will be run after the module is initialized.") + "\n";
	}
	protected static String finalizeDefaults()
	{
		return "// " + Manager.applyStringTable("Input the scripts which will be run at the program is closing.") + "\n";
	}
	protected static String refreshDefaults()
	{
		return "// " + Manager.applyStringTable("Input the scripts which will be run at the user control something.") + "\n"
				+ "// " + Manager.applyStringTable("You can use the constant \'refreshMap\' to access what the user control.") + "\n";
	}
	protected static String showMenuDefaults()
	{
		return "// " + Manager.applyStringTable("Input the scripts which will be run to show menu.") + "\n";
	}
	protected static String actMenuDefaults()
	{
		return "// " + Manager.applyStringTable("Input the scripts which will be run to process the user's inputs on the menu.") + "\n"
				+ "// " + Manager.applyStringTable("You can use the constant \'inputs\' to access what the user inputs.") + "\n";
	}
}
