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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.EventObject;
import java.util.Hashtable;
import java.util.Map;

import hjow.hgtable.Main;
import hjow.hgtable.Manager;
import hjow.hgtable.jscript.JScriptObject;
import hjow.hgtable.jscript.JScriptRunner;
import hjow.hgtable.ui.NeedtoEnd;

/**
 * <p>이 클래스의 객체는 GUI 기반 모듈에서 이벤트 처리를 대행합니다.</p>
 * 
 * @author HJOW
 *
 */
public class ListenerBroker implements JScriptObject, ActionListener, ItemListener, WindowListener, MouseListener, MouseMotionListener, NeedtoEnd
{
	private static final long serialVersionUID = -2815556938048266592L;
	protected JScriptRunner runner;
	protected Map<String, String> eventScripts = new Hashtable<String, String>();

	public ListenerBroker()
	{
		
	}
	
	public ListenerBroker(JScriptRunner runner)
	{
		this.runner = runner;
	}

	@Override
	public String help()
	{
		// TODO Auto-generated method stub
		return null;
	}

	private void onEvent(EventObject eventObject)
	{
		runner.put("last_event", eventObject);
		String eventName = eventObject.getClass().getName();
		try
		{
			runner.execute(eventScripts.get(eventName));
		}
		catch (Throwable e1)
		{
			Main.logError(e1, Manager.applyStringTable("On script event") + " : " + eventName);
		}
	}
	@Override
	public void mouseClicked(MouseEvent e)
	{
		onEvent(e);
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
		onEvent(e);
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
		onEvent(e);
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		onEvent(e);
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		onEvent(e);
	}

	@Override
	public void windowActivated(WindowEvent e)
	{
		onEvent(e);
	}

	@Override
	public void windowClosed(WindowEvent e)
	{
		onEvent(e);
	}

	@Override
	public void windowClosing(WindowEvent e)
	{
		onEvent(e);
	}

	@Override
	public void windowDeactivated(WindowEvent e)
	{
		onEvent(e);
	}

	@Override
	public void windowDeiconified(WindowEvent e)
	{
		onEvent(e);
	}

	@Override
	public void windowIconified(WindowEvent e)
	{
		onEvent(e);
	}

	@Override
	public void windowOpened(WindowEvent e)
	{
		onEvent(e);
	}

	@Override
	public void itemStateChanged(ItemEvent e)
	{
		onEvent(e);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		onEvent(e);
	}
	
	@Override
	public void mouseDragged(MouseEvent e)
	{
		onEvent(e);
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{
		onEvent(e);
	}

	@Override
	public void noMoreUse()
	{
		this.runner = null;
	}
	
	@Override
	public boolean isAlive()
	{
		return runner != null;
	}

	public JScriptRunner getRunner()
	{
		return runner;
	}

	public void setRunner(JScriptRunner runner)
	{
		this.runner = runner;
	}

	public Map<String, String> getEventScripts()
	{
		return eventScripts;
	}

	public void setEventScripts(Map<String, String> eventScripts)
	{
		this.eventScripts = eventScripts;
	}
}
