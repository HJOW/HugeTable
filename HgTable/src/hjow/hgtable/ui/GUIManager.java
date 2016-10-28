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

package hjow.hgtable.ui;

import java.awt.AWTEvent;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.AbstractButton;

import hjow.hgtable.HThread;
import hjow.hgtable.Main;
import hjow.hgtable.Manager;
import hjow.hgtable.jscript.module.AbstractModule;
import hjow.hgtable.jscript.module.Module;
import hjow.hgtable.tableset.TableSet;
import hjow.hgtable.ui.module.GUIDialogModule;
import hjow.hgtable.ui.module.GUIModule;
import hjow.hgtable.ui.module.GUIPanelModule;
import hjow.hgtable.ui.module.GUIToolbarModule;
import hjow.hgtable.ui.swing.SwingArgumentDialog;
import hjow.hgtable.util.DataUtil;
import hjow.hgtable.util.InvalidInputException;
import hjow.hgtable.util.ModuleUtil;

/**
 * <p>이 클래스 객체는 GUI 인터페이스를 제공하는 객체 생성에 관여합니다.</p>
 * 
 * @author HJOW
 *
 */
public abstract class GUIManager extends Manager implements ActionListener, WindowListener, ItemListener, KeyListener, MouseListener
{
	private static final long serialVersionUID = 8331957625823725490L;
	protected boolean initialized = false;
	protected String beforeInitializedMessage = "";
	protected List<GUIToolbarModule> toolbars = new Vector<GUIToolbarModule>();
	protected boolean eventHandlerSwitch = true;
	protected Vector<HThread> eventHandler = new Vector<HThread>();
	protected Vector<String> eventQueue = new Vector<String>();
	protected HThread eventHandlerCleaner = new HThread(new Runnable()
	{
		@Override
		public void run()
		{
			while(eventHandlerSwitch)
			{
				try
				{
					if(Main.checkInterrupt(this, applyStringTable("Event Handler Cleaner"))) break;
					if(eventHandler == null) break;
					int i=0;
					while(i < eventHandler.size())
					{
						if(Main.checkInterrupt(this, applyStringTable("Event Handler Cleaner"))) break;
						HThread currentHandler = eventHandler.get(i);
						if(currentHandler == null || (! currentHandler.isAlive()))
						{
							eventHandler.remove(i);
							i = 0;
						}
						else
						{
							i++;
						}
					}
				}
				catch(Exception e)
				{
					logError(e, applyStringTable("On Event Handler Clearing"));
				}
				try
				{
					
				}
				catch(Exception e)
				{
					logError(e, applyStringTable("On Event Handler Working"));
				}
				try
				{
					Thread.sleep(100 + (int)(Math.random() * 100));
				}
				catch(InterruptedException e)
				{
					break;
				}
				catch(Exception e)
				{
					
				}
			}
		}
	});
		
	/**
	 * <p>기본 생성자입니다. 기존 매니저 객체의 초기화 과정을 거칩니다.</p>
	 * 
	 */
	public GUIManager()
	{
		super();
	}
	/**
	 * <p>GUI 컴포넌트들을 초기화합니다.</p>
	 * 
	 */
	protected abstract void initComponents();
	/**
	 * <p>닫혀 있는 DAO들을 목록에서 제거하고, 콤보박스 내용을 새로 고칩니다.</p>
	 * 
	 * @param controlCompAfter : true 시 작업 이후 관련 컴포넌트를 새로 고칩니다.
	 * @param setComponentSelectBefores : true 시 해당 컴포넌트가 가능하면 이전 DAO를 선택하게 합니다. controlCompAfter 가 false 이면 의미가 없습니다.
	 */
	public abstract void refreshDaos(boolean controlCompAfter, boolean setComponentSelectBefores);
	/**
	 * <p>사용자가 실행 버튼을 눌렀을 때 실행되는 메소드입니다.</p>
	 * 
	 * @param onlySelected : true 시 선택된 텍스트만 실행합니다.
	 */
	protected abstract void btRunScriptAction(boolean onlySelected);
	/**
	 * <p>사용자가 취소 버튼을 눌렀을 때 실행되는 메소드입니다.</p>
	 * 
	 */
	protected abstract void btCancelScript();
	/**
	 * <p>사용자가 접속 버튼을 눌렀을 때 실행되는 메소드입니다.</p>
	 * 
	 */
	protected abstract void btConnect();
	/**
	 * <p>GUI 화면을 엽니다. 열기 전에 수행해야 할 일들(폰트 불러오기, 포커스)을 수행합니다.</p>
	 * 
	 */
	public abstract void open();
	
	@Override
	public void manage(Map<String, String> args)
	{
		super.manage(args);
		open();
		
		applyRunModuleOption(args);
	}
	/**
	 * <p>이 메소드는 사용자가 롤백 버튼을 눌렀을 때 실행됩니다.</p>
	 * 
	 */
	protected void btRollback()
	{
		try
		{
			getDao().rollback();
			log(applyStringTable("Rollbacked."));
		}
		catch (Exception e)
		{
			logError(e, Manager.applyStringTable("On rollback"));
		}
	}
	/**
	 * <p>이 메소드는 사용자가 커밋 버튼을 눌렀을 때 실행됩니다.</p>
	 */
	protected void btCommit()
	{
		try
		{
			getDao().commit();
			log(applyStringTable("Committed."));
		}
		catch (Exception e)
		{
			logError(e, Manager.applyStringTable("On commit"));
		}
	}
	
	/**
	 * 
	 */
	protected abstract void refreshEnabled();
	
	public abstract void setQueryAreaText(String str);
	public abstract String getQueryAreaText();
	
	/**
	 * <p>결과 란을 비웁니다.</p>
	 * 
	 */
	public abstract void clearResultArea();
	
	/**
	 * <p>사용자가 접속 해제 버튼을 눌렀을 때 실행되는 메소드입니다.</p>
	 */
	protected void btDisconnect()
	{
		if(getDao().isAlive()) getDao().close();
		refreshDaos(true, true);
	}
			
	/**
	 * <p>모듈들에게 정보를 보냅니다.</p>
	 * 
	 * @param message : 사건에 대한 메시지
	 * @param ob : 사건 정보가 담긴 객체
	 */
	protected void sendRefreshToModules(String message, Object ob)
	{
		if(modules != null && modules.size() >= 1)
		{
			Map<String, Object> actionInfo = new Hashtable<String, Object>();
			if(ob == null) actionInfo.put(message, "null");
			else actionInfo.put(message, ob);
			actionInfo.put("selected_connection", getDao().getAccessInfo());
			for(int i=0; i<modules.size(); i++)
			{
				try
				{
					if(ModuleUtil.checkAccepted(modules.get(i))) modules.get(i).refresh(actionInfo);
				}
				catch(Throwable e)
				{
					logError(e, applyStringTable("On refreshing module") + " " 
							+ modules.get(i).getName() + "(" + String.valueOf(modules.get(i).getModuleId()) + ")"
							+ " : " + String.valueOf(actionInfo));
				}
			}
		}
	}
	
	/**
	 * <p>스크립트 파일을 불러와 스크립트 입력 란에 채우기 위해 파일 불러오기 창을 띄웁니다.</p>
	 */
	protected abstract void loadScript();
	
	/**
	 * <p>스크립트 입력 란의 내용을 파일로 저장하기 위해 파일 저장 창을 띄웁니다.</p>
	 */
	protected abstract void saveScript();
	
	/**
	 * <p>메인 창이 되는 객체를 반환합니다.</p>
	 * 
	 * @return 창 객체
	 */
	public abstract Frame getFrame();
	
	/**
	 * <p>대화 상자를 대신 생성해 줍니다.</p>
	 * 
	 * @param needModal : true 시 모달 대화 상자가 되어, 열렸을 때 대화 상자 외 다른 작업이 일시 정지됩니다.
	 * @return 새 대화 상자 객체
	 */
	public abstract Window newDialog(boolean needModal);
		
	@Override
	public void close()
	{
		try
		{
			toolbars.clear();
		}
		catch(Exception e)
		{
			
		}
		
		try
		{
			eventHandlerSwitch = false;
			eventHandlerCleaner = null;
			
			if(eventHandler != null)
			{
				for(int i=0; i<eventHandler.size(); i++)
				{
				    try
				    {
				    	eventHandler.get(i).stopThread();
				    }
				    catch(Exception e)
				    {
				    	
				    }
				}
			}
			eventHandler.clear();
			eventHandler = null;
			eventQueue.clear();
			eventQueue = null;
		}
		catch(Exception e)
		{
			
		}
		
		super.close();
	}
	
	@Override
	public void logTable(TableSet table, String spaces) 
	{
		super.logTable(table, spaces);
	}
	
	/**
	 * <p>불러온 GUI 모듈 이름들을 반환합니다.</p>
	 * 
	 * @return 모듈 이름 리스트
	 */
	public List<String> getGUIModuleNames()
	{
		List<String> results = new Vector<String>();
		
		for(AbstractModule m : modules)
		{
			if(m instanceof GUIModule) results.add(m.getName());
		}
		
		return results;
	}
	
	/**
	 * <p>불러온 GUI 모듈 ID들을 반환합니다.</p>
	 * 
	 * @return 모듈 ID 리스트
	 */
	public List<Long> getGUIModuleIDs()
	{
		List<Long> results = new Vector<Long>();
		
		for(AbstractModule m : modules)
		{
			if(m instanceof GUIModule) results.add(new Long(m.getModuleId()));
		}
		
		return results;
	}
	
	/**
	 * <p>불러온 패널 모듈 이름들을 반환합니다.</p>
	 * 
	 * @return 모듈 이름 리스트
	 */
	public List<String> getPanelModuleNames()
	{
		List<String> results = new Vector<String>();
		
		for(AbstractModule m : modules)
		{
			if(m instanceof GUIPanelModule) results.add(m.getName());
		}
		
		return results;
	}
	
	/**
	 * <p>불러온 패널 모듈 ID들을 반환합니다.</p>
	 * 
	 * @return 모듈 ID 리스트
	 */
	public List<Long> getPanelModuleIDs()
	{
		List<Long> results = new Vector<Long>();
		
		for(AbstractModule m : modules)
		{
			if(m instanceof GUIPanelModule) results.add(new Long(m.getModuleId()));
		}
		
		return results;
	}
	
	/**
	 * <p>불러온 대화 상자형 모듈 이름들을 반환합니다.</p>
	 * 
	 * @return 모듈 이름 리스트
	 */
	public List<String> getDialogModuleNames()
	{
		List<String> results = new Vector<String>();
		
		for(AbstractModule m : modules)
		{
			if(m instanceof GUIDialogModule) results.add(m.getName());
		}
		
		return results;
	}
	
	/**
	 * <p>불러온 대화 상자형 모듈 ID들을 반환합니다.</p>
	 * 
	 * @return 모듈 ID 리스트
	 */
	public List<Long> getDialogModuleIDs()
	{
		List<Long> results = new Vector<Long>();
		
		for(AbstractModule m : modules)
		{
			if(m instanceof GUIDialogModule) results.add(new Long(m.getModuleId()));
		}
		
		return results;
	}
	
	/**
	 * <p>불러온 툴바형 모듈 이름들을 반환합니다.</p>
	 * 
	 * @return 모듈 이름 리스트
	 */
	public List<String> getToolbarModuleNames()
	{
		List<String> results = new Vector<String>();
		
		for(AbstractModule m : modules)
		{
			if(m instanceof GUIToolbarModule) results.add(m.getName());
		}
		
		return results;
	}
	
	/**
	 * <p>불러온 툴바형 모듈 ID들을 반환합니다.</p>
	 * 
	 * @return 모듈 ID 리스트
	 */
	public List<Long> getToolbarModuleIDs()
	{
		List<Long> results = new Vector<Long>();
		
		for(AbstractModule m : modules)
		{
			if(m instanceof GUIToolbarModule) results.add(new Long(m.getModuleId()));
		}
		
		return results;
	}
	
	/**
	 * <p>ID에 해당하는 모듈을 엽니다.</p>
	 * 
	 * @param moduleId : 모듈 ID
	 * @return 성공 여부
	 */
	public boolean openModule(long moduleId)
	{
		for(AbstractModule m : modules)
		{
			if(m instanceof GUIModule)
			{
				if(m.getModuleId() == moduleId)
				{
					((GUIModule) m).open();
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * <p>매니저 객체 안의 컴포넌트들을 동작시킵니다. 인증된 모듈에서만 이 메소드를 사용할 수 있습니다.</p>
	 * 
	 * @param name : 동작시킬 컴포넌트 이름
	 * @param module : 호출 주체가 되는 인증된 모듈
	 * @param e : GUI 이벤트 객체 (있는 경우에만 사용)
	 * @throws Throwable : 인증된 모듈이 아니거나, 해당 이름에 대한 컴포넌트가 없는 경우
	 */
	public void actComponent(String name, Module module, AWTEvent e) throws Throwable
	{
		if(! ModuleUtil.checkAuthorize(module)) throw new SecurityException(applyStringTable("Module") + " " + module.getName() + "(" + module.getModuleId() + " "
				+ applyStringTable("is not authorized") + ".");
		
		Field[] fields = this.getClass().getFields();
		Method[] methods = this.getClass().getMethods();
		boolean fieldExisted = false;
		boolean called = false;
		
		Object fieldValue = null;
		
		for(int i=0; i<fields.length; i++)
		{
			if(name.equals(fields[i].getName()))
			{
				fieldValue = fields[i].get(this);
			}
		}
		
		if(fieldValue == null)
		{
			String getterMethod = "get" + String.valueOf(name.charAt(0)).toUpperCase() + new String(name).substring(1);
			for(int i=0; i<methods.length; i++)
			{
				if(methods[i].getName().equals(getterMethod))
				{
					fieldValue = methods[i].invoke(this, module);
				}
			}
		}
		
		if(fieldValue != null)
		{
			if(DataUtil.isNotEmpty(fieldValue))
			{
				fieldExisted = true;
				if(! (fieldValue instanceof AbstractButton)) 
					throw new InvalidInputException(applyStringTable("A component named") + " " + name + " " + applyStringTable("found but it cannot be acted") + ".");
				for(int j=0; j<methods.length; j++)
				{
					if(name.equals(methods[j].getName()))
					{
						methods[j].invoke(this, module, e);
						called = true;
					}
				}
			}
		}
		
		if(! fieldExisted) throw new InvalidInputException(applyStringTable("There is no component named") + " " + name);
		if(! called) throw new Exception(applyStringTable("A component named") + " " + name + " " + applyStringTable("found but there is no method linked") + ".");
	}
	
	/**
	 * <p>맵 객체를 입력받습니다.</p>
	 * 
	 * @param msg : 사용자에게 보일 메시지
	 * @param befores : 이전에 입력한 데이터 (null 가능)
	 * @return 맵 객체
	 */
	@Override
	public Map<String, String> askMap(String msg, Map<String, String> befores)
	{
		return SwingArgumentDialog.ask(this, msg, befores);
	}
	
	@Override
	public void windowOpened(WindowEvent e)
	{
		
		
	}
	@Override
	public void windowClosed(WindowEvent e)
	{
		
		
	}
	@Override
	public void windowIconified(WindowEvent e)
	{
		
		
	}
	@Override
	public void windowDeiconified(WindowEvent e)
	{
		
		
	}
	@Override
	public void windowActivated(WindowEvent e)
	{
		
		
	}
	@Override
	public void windowDeactivated(WindowEvent e)
	{
		
		
	}
	
	@Override
	public void keyPressed(KeyEvent e)
	{
		
	}
	@Override
	public void keyReleased(KeyEvent e)
	{
		
	}
	@Override
	public void keyTyped(KeyEvent e)
	{
		
	}
	
	@Override
	public void mouseClicked(MouseEvent e)
	{
		
	}
	@Override
	public void mouseEntered(MouseEvent e)
	{
		
	}
	@Override
	public void mouseExited(MouseEvent e)
	{
		
	}
	@Override
	public void mousePressed(MouseEvent e)
	{
		
	}
	@Override
	public void mouseReleased(MouseEvent e)
	{
		
	}
	
	@Override
	protected void applyRunModuleOption(Map<String, String> args)
	{
		long checkModuleRunOption = checkRunModuleMode(args);
		if(checkModuleRunOption != 0)
		{
			boolean runNow = false;
			for(int i=0; i<modules.size(); i++)
			{
				AbstractModule m = modules.get(i);
				if(m.getModuleId() == checkModuleRunOption)
				{
					if(ModuleUtil.checkAuthorize(m) || ModuleUtil.checkAccepted(m))
					{
						runNow = true;
					}
					else
					{
						runNow = askYes(applyStringTable("Do you want to run") + " " + m.getName() + "(" + m.getModuleId() + ") ?");
					}
					
					if(runNow)
					{
						getFrame().setVisible(false);
						m.manage(args);
					}
				}
			}
		}
	}
}
