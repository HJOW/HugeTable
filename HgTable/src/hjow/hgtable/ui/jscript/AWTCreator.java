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

import hjow.hgtable.jscript.JScriptObject;
import hjow.hgtable.jscript.JScriptRunner;
import hjow.hgtable.ui.GUIManager;
import hjow.hgtable.ui.NeedtoEnd;

import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Choice;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Panel;
import java.awt.PopupMenu;
import java.awt.ScrollPane;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.Window;
import java.util.List;
import java.util.Vector;

/**
 * <p>Java AWT 컴포넌트 객체를 생성합니다.</p>
 * 
 * @author HJOW
 *
 */
public class AWTCreator implements JScriptObject, NeedtoEnd
{
	private static final long serialVersionUID = -1188534210689477989L;
	protected JScriptRunner runner;
	protected GUIManager manager;
	protected List<ListenerBroker> listeners = new Vector<ListenerBroker>();

	public AWTCreator()
	{
		
	}
	
	public AWTCreator(JScriptRunner runner, GUIManager manager)
	{
		this.runner = runner;
		this.manager = manager;
	}
	
	public Frame newFrame()
	{
		return new Frame();
	}
	
	public Window newDialog(Window frame, boolean isModal)
	{
		if(frame == null)
		{
			return manager.newDialog(isModal);
		}
		
		if(frame instanceof Frame) return new Dialog((Frame) frame, isModal);
		else return new Dialog(frame);
	}
	
	public Panel newPanel()
	{
		return new Panel();
	}
	
	public Label newLabel()
	{
		return new Label();
	}
	
	public TextField newTextField(Integer i)
	{
		if(i == null) return new TextField();
		else return new TextField(i.intValue());
	}
	
	public TextArea newTextArea()
	{
		return new TextArea();
	}
	
	public Choice newChoice()
	{
		return new Choice();
	}
	
	public Checkbox newCheckBox(String labelContent, CheckboxGroup group, boolean firstState)
	{
		return new Checkbox(labelContent, group, firstState);
	}
	
	public CheckboxGroup newCheckBoxGroup()
	{
		return new CheckboxGroup();
	}
	
	public PopupMenu newPopupMenu()
	{
		return new PopupMenu();
	}
	
	public ScrollPane newScrollPane()
	{
		return new ScrollPane();
	}
	
	public MenuBar newMenuBar()
	{
		return new MenuBar();
	}
	
	public Menu newMenu()
	{
		return new Menu();
	}
	
	public MenuItem newMenuItem()
	{
		return new MenuItem();
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
	
	public ListenerBroker newListenerBroker()
	{
		ListenerBroker newOne = new ListenerBroker(runner);
		listeners.add(newOne);
		return newOne;
	}
	
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
