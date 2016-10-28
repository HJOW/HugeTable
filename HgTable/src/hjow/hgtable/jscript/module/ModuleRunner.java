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

package hjow.hgtable.jscript.module;

import hjow.dbtool.cubrid.CTool;
import hjow.dbtool.mariadb.MTool;
import hjow.dbtool.oracle.OTool;
import hjow.hgtable.Manager;
import hjow.hgtable.jscript.JScriptTool;
import hjow.hgtable.jscript.JavaObjectCreator;
import hjow.hgtable.jscript.JavaScriptRunner;
import hjow.hgtable.jscript.StaticMethodBroker;
import hjow.hgtable.jscript.TypeCodeHelp;
import hjow.hgtable.tableset.Column;
import hjow.hgtable.tableset.DefaultTableSet;
import hjow.hgtable.tableset.TableSet;
import hjow.hgtable.ui.GUIManager;
import hjow.hgtable.ui.NeedtoEnd;
import hjow.hgtable.ui.jscript.AWTCreator;
import hjow.hgtable.ui.jscript.SwingCreator;
import hjow.hgtable.ui.module.GUIModule;
import hjow.hgtable.util.DataUtil;
import hjow.hgtable.util.ScriptUtil;
import hjow.hgtable.util.debug.DebuggingUtilCollection;

import java.util.Vector;

/**
 * <p>모듈 내 스크립트 실행용 엔진입니다.</p>
 * 
 * @author HJOW
 *
 */
public class ModuleRunner extends JavaScriptRunner implements NeedtoEnd
{
	protected ManagerBroker managerBroker;
	/**
	 * <p>생성자입니다. 기존 스크립트 엔진처럼 초기화한 후 권한을 최하로 설정합니다.</p>
	 * 
	 * @param manager : 매니저 객체
	 */
	public ModuleRunner(Manager manager)
	{
		super(manager);
		
		this.priv_allowCommand = false;
		this.priv_allowLoadClass = false;
		this.priv_allowThread = true;
		this.priv_allowedReadFilePath = new Vector<String>();
		this.priv_allowedWriteFilePath = new Vector<String>();
	}
	
	/**
	 * <p>생성자입니다. 기존 스크립트 엔진처럼 초기화한 후 권한을 최하로 설정합니다.</p>
	 * 
	 * @param manager : 매니저 객체
	 * @param scriptType : 스크립트 종류
	 */
	public ModuleRunner(Manager manager, String scriptType)
	{
		super(manager, scriptType);
		
		this.priv_allowCommand = false;
		this.priv_allowLoadClass = false;
		this.priv_allowThread = true;
		this.priv_allowedReadFilePath = new Vector<String>();
		this.priv_allowedWriteFilePath = new Vector<String>();
	}
	
	@Override
	protected void initEngine()
	{
		javax.script.ScriptEngineManager scriptEngine = new javax.script.ScriptEngineManager();
		if(DataUtil.isEmpty(engineType)) engineType = ScriptUtil.getDefaultEngineName();
		engine = scriptEngine.getEngineByName(engineType);
	}
	
	/**
	 * <p>이 메소드는 </p>
	 * 
	 * @param module
	 */
	public void init(GUIModule module)
	{
		this.priv_allowCommand = false;
		this.priv_allowLoadClass = false;
		this.priv_allowThread = true;
		if(this.priv_allowedReadFilePath == null) this.priv_allowedReadFilePath = new Vector<String>();
		if(this.priv_allowedWriteFilePath == null) this.priv_allowedWriteFilePath = new Vector<String>();
		this.priv_allowedReadFilePath.add(Manager.getOption("config_path") + module.getName() + Manager.getOption("file_separator"));
		this.priv_allowedWriteFilePath.add(Manager.getOption("config_path") + module.getName() + Manager.getOption("file_separator"));
	}
	
	/**
	 * <p>이 메소드는 스크립트를 최저 권한으로 실행합니다.</p>
	 * 
	 */
	@Override
	public Object execute(String script) throws Throwable
	{
		this.priv_allowCommand = false;
		this.priv_allowLoadClass = false;
		this.priv_allowThread = false;
		if(this.priv_allowedReadFilePath == null) this.priv_allowedReadFilePath = new Vector<String>();
		if(this.priv_allowedWriteFilePath == null) this.priv_allowedWriteFilePath = new Vector<String>();
		this.priv_allowedReadFilePath.clear();
		this.priv_allowedWriteFilePath.clear();
		return super.execute(script);
	}
	
	/**
	 * <p>이 메소드는 모듈의 스크립트를 실행하고 그 결과를 반환합니다. 실행하기 전 다시 권한 초기화 과정을 거칩니다.</p>
	 * 
	 * @param script : 실행할 스크립트
	 * @param module : 해당 모듈
	 * @return 실행 결과
	 * @throws Exception : 스크립트 문법/논리 오류
	 */
	public Object execute(String script, GUIModule module) throws Throwable
	{
		init(module);
		return super.execute(script);
	}
	@Override
	protected void addDefaults(Manager manager)
	{
		DebuggingUtilCollection.putAll(this);
		
		managerBroker = new ManagerBroker(manager);
		objectList.add(managerBroker);
		
		DaoBroker daoBroker = new DaoBroker(manager);
		objectList.add(daoBroker);
		
		engine.put("manager", managerBroker);
		engine.put("dao", daoBroker);
		
		otool = new OTool(manager.getDao());
		mtool = new MTool(manager.getDao());
		ctool = new CTool(manager.getDao());
		objectList.add(otool);
		objectList.add(mtool);
		objectList.add(ctool);
		
		jtool = new JScriptTool(this, manager);
		
		joc = new JavaObjectCreator();
		smb = new StaticMethodBroker();
		if(manager instanceof GUIManager) awtc = new AWTCreator(this, (GUIManager) manager);
		if(manager instanceof GUIManager) swingc = new SwingCreator(this, (GUIManager) manager);
		
		objectList.add(jtool);
		objectList.add(joc);
		objectList.add(smb);
		objectList.add(awtc);
		objectList.add(swingc);
		
		engine.put("otool", otool);
		engine.put("mtool", mtool);
		engine.put("ctool", ctool);
		
		engine.put("jtool", jtool);
		engine.put("joc", joc);
		engine.put("smb", smb);
		engine.put("awtc", awtc);
		
		engine.put("typehelp", new TypeCodeHelp());
		
		engine.put("attribute", getAttributes());
		
		TableSet emptyTableSet = new DefaultTableSet();
		emptyTableSet.getColumns().add(new Column());
		engine.put("shown_tableset", emptyTableSet);
	}
	
	@Override
	protected void makeBasicFunction()
	{
		try
		{
			ScriptUtil.createBasicFunctions(this);
		}
		catch(Throwable e)
		{
			
		}
	}
	@Override
	public void noMoreUse()
	{
		managerBroker.noMoreUse();
		if(awtc != null) awtc.noMoreUse();
		if(swingc != null) swingc.noMoreUse();
	}

	@Override
	public boolean isAlive()
	{
		return managerBroker != null && managerBroker.isAlive();
	}
}
