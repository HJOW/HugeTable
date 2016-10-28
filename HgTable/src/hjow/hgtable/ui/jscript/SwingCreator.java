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

package hjow.hgtable.ui.jscript;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Window;
import java.util.List;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import hjow.hgtable.jscript.JScriptObject;
import hjow.hgtable.jscript.JScriptRunner;
import hjow.hgtable.ui.GUIManager;
import hjow.hgtable.ui.NeedtoEnd;
import hjow.hgtable.ui.swing.UIComboBox;
import hjow.hgtable.ui.swing.UIEditorPane;
import hjow.hgtable.ui.swing.UIList;
import hjow.hgtable.ui.swing.UISyntaxView;
import hjow.swing.jsonSwing.JSONCore;

/**
 * <p>Java Swing 컴포넌트 객체를 생성합니다.</p>
 * 
 * @author HJOW
 *
 */
public class SwingCreator implements JScriptObject, NeedtoEnd
{
	private static final long serialVersionUID = -1188534210689477989L;
	protected JScriptRunner runner;
	protected GUIManager manager;
	protected List<ListenerBroker> listeners = new Vector<ListenerBroker>();

	public SwingCreator()
	{
		
	}
	
	public SwingCreator(JScriptRunner runner, GUIManager manager)
	{
		this.runner = runner;
		this.manager = manager;
	}
	
	public JFrame newFrame()
	{
		return new JFrame();
	}
	
	public Window newDialog(Window frame, boolean isModal)
	{
		if(frame == null)
		{
			return manager.newDialog(isModal);
		}
		
		if(frame instanceof Frame) return new JDialog((Frame) frame, isModal);
		else return new JDialog(frame);
	}
	
	public SwingDialog newDialog(Window frame, String json) throws Exception
	{
		if(frame != null) return new SwingDialog(json, frame);
		else return new SwingDialog(json);
	}
	
	public JPanel newPanel()
	{
		return new JPanel();
	}
	
	public JLabel newLabel()
	{
		return new JLabel();
	}
	
	public JTextField newTextField(Integer i)
	{
		if(i == null) return new JTextField();
		else return new JTextField(i.intValue());
	}
	
	public JTextArea newTextArea()
	{
		return new JTextArea();
	}
	
	public JEditorPane newEditorPane()
	{
		return new UIEditorPane();
	}
	
	public JTextPane newTextPane()
	{
		return new JTextPane();
	}
	
	public JSplitPane newSplitPane()
	{
		return new JSplitPane();
	}
	
	public JTabbedPane newTabbedPane()
	{
		return new JTabbedPane();
	}
	
	public UIComboBox newComboBox(Object labelContent)
	{
		if(labelContent == null) return new UIComboBox();
		else
		{
			Vector<String> items = new Vector<String>();
			
			if(labelContent instanceof List<?>)
			{
				for(int i=0; i<((List<?>) labelContent).size(); i++)
				{
					items.add(String.valueOf(((List<?>) labelContent).get(i)));
				}
			}
			else if(labelContent instanceof Object[])
			{
				for(int i=0; i<((Object[]) labelContent).length; i++)
				{
					items.add(String.valueOf(((Object[]) labelContent)[i]));
				}
			}
			else if(labelContent instanceof String[])
			{
				for(int i=0; i<((Object[]) labelContent).length; i++)
				{
					items.add(String.valueOf(((String[]) labelContent)[i]));
				}
			}
			else
			{
				return new UIComboBox(String.valueOf(labelContent).split(","));
			}
			
			return new UIComboBox(items);
		}
	}
	
	public JCheckBox newCheckBox(Object labelContent)
	{
		if(labelContent == null) return new JCheckBox();
		else return new JCheckBox(String.valueOf(labelContent));
	}
	
	public JRadioButton newRadioButton(Object labelContent)
	{
		if(labelContent == null) return new JRadioButton();
		else return new JRadioButton(String.valueOf(labelContent));
	}
	
	public ButtonGroup newButtonGroup()
	{
		return new ButtonGroup();
	}
	
	public UIList newList(ListModel listModel)
	{
		if(listModel == null) return new UIList();
		else return new UIList(listModel);
	}
	
	public UISyntaxView newSyntaxView(Window window)
	{
		return new UISyntaxView(window);
	}
	
	public DefaultListModel newListModel()
	{
		return new DefaultListModel();
	}
	
	public JTable newTable(TableModel tableModel)
	{
		if(tableModel == null) return new JTable();
		else return new JTable(tableModel);
	}
	
	public TableModel newTableModel()
	{
		return new DefaultTableModel();
	}
	
	public JPasswordField newPasswordField()
	{
		return new JPasswordField();
	}
	
	public JScrollPane newScrollPane(Component comp)
	{
		return new JScrollPane(comp);
	}
	
	public JPopupMenu newPopupMenu(Object labelText)
	{
		if(labelText == null) return new JPopupMenu();
		else return new JPopupMenu(String.valueOf(labelText));
	}
	
	public JMenuBar newMenuBar()
	{
		return new JMenuBar();
	}
	
	public JMenu newMenu(Object labelText)
	{		
		if(labelText == null) return new JMenu();
		else return new JMenu(String.valueOf(labelText));
	}
	
	public JMenuItem newMenuItem(Object labelText)
	{
		if(labelText == null) return new JMenuItem();
		else return new JMenuItem(String.valueOf(labelText));
	}
	
	public JCheckBoxMenuItem newCheckMenuItem(Object labelText)
	{
		if(labelText == null) return new JCheckBoxMenuItem();
		else return new JCheckBoxMenuItem(String.valueOf(labelText));
	}
	
	public BorderLayout newBorderLayout()
	{
		return new BorderLayout();
	}
	
	public FlowLayout newFlowLayout()
	{
		return new FlowLayout();
	}
	
	public GridLayout newGridLayout(int rows, int cols)
	{
		return new GridLayout(rows, cols);
	}
	
	/**
	 * <p>이벤트 리스너 대행 객체를 생성합니다.</p>
	 * 
	 * @return 이벤트 리스너 대행 객체
	 */
	public ListenerBroker newListenerBroker()
	{
		ListenerBroker newOne = new ListenerBroker(runner);
		listeners.add(newOne);
		return newOne;
	}
	
	/**
	 * <p>JSON 텍스트를 이용해 컴포넌트 혹은 관련 객체를 생성합니다.</p>
	 * 
	 * @param json : JSON 형식 텍스트
	 * @return JSON 정의된 객체
	 * @throws Exception 문법 오류 등
	 */
	public Object createJSONSwingObject(String json) throws Exception
	{
		return JSONCore.createComponent(json);
	}
	
	/**
	 * <p>Font 객체를 생성합니다.</p>
	 * 
	 * @param name : 글꼴 이름
	 * @param style : 글꼴 스타일 (java.awt.Font 클래스의 상수값 사용)
	 * @param size : 글꼴 크기 (pixel)
	 * @return Font 객체
	 */
	public Font newFont(String name, int style, int size)
	{
		return new Font(name, style, size);
	}

	@Override
	public String help()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void noMoreUse()
	{
		if(listeners != null)
		{
			for(int i=0; i<listeners.size(); i++)
			{
				listeners.get(i).noMoreUse();
			}
		}
		runner = null;
		manager = null;
	}
	
	@Override
	public boolean isAlive()
	{
		return runner != null;
	}
}
