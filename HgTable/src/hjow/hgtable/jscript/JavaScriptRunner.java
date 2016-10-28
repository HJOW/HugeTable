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

package hjow.hgtable.jscript;

import java.io.File;
import java.net.URL;

import hjow.dbtool.cubrid.CTool;
import hjow.dbtool.mariadb.MTool;
import hjow.dbtool.oracle.OTool;
import hjow.hgtable.IncludesException;
import hjow.hgtable.Main;
import hjow.hgtable.Manager;
import hjow.hgtable.tableset.Column;
import hjow.hgtable.tableset.DefaultTableSet;
import hjow.hgtable.tableset.TableSet;
import hjow.hgtable.ui.GUIManager;
import hjow.hgtable.ui.jscript.AWTCreator;
import hjow.hgtable.ui.jscript.SwingCreator;
import hjow.hgtable.util.DataUtil;
import hjow.hgtable.util.ScriptUtil;
import hjow.hgtable.util.StreamUtil;
import hjow.hgtable.util.debug.DebuggingUtilCollection;

/**
 * <p>JDK에서 제공하는 자바스크립트 엔진을 사용한 스크립트 실행 모듈 생성 클래스입니다.</p>
 * 
 * @author HJOW
 *
 */
public class JavaScriptRunner extends JScriptRunner
{
	protected javax.script.ScriptEngine engine;
	protected OTool otool;
	protected MTool mtool;
	protected CTool ctool;
	protected JScriptTool jtool;
	protected JavaObjectCreator joc;
	protected StaticMethodBroker smb;
	protected AWTCreator awtc;
	protected SwingCreator swingc;
	
	/**
	 * <p>자바스크립트 엔진 생성자입니다.</p>
	 * 
	 * @param manager : 매니저 객체
	 */
	public JavaScriptRunner(Manager manager)
	{
		super();
		
		initEngine();
		addDefaults(manager);
		
		makeBasicFunction();
	}
	
	/**
	 * <p>스크립트 엔진 생성자입니다. 하위 클래스에서 쓰입니다.</p>
	 * 
	 * @param manager : 매니저 객체
	 * @param engineType : 스크립트 이름
	 */
	protected JavaScriptRunner(Manager manager, String engineType)
	{
		super();
		
		this.engineType = engineType;
		
		initEngine();
		addDefaults(manager);
		
		makeBasicFunction();
	}
	
	/**
	 * <p>엔진을 초기화합니다.</p>
	 * 
	 */
	protected void initEngine() throws NullPointerException
	{
		javax.script.ScriptEngineManager scriptEngine = new javax.script.ScriptEngineManager();
		if(DataUtil.isEmpty(engineType)) engineType = ScriptUtil.getDefaultEngineName();
		engine = scriptEngine.getEngineByName(engineType);
		if(engine == null) throw new NullPointerException(Manager.applyStringTable("On initializing script engine"));
	}
	
	/**
	 * <p>엔진에 기본 객체들을 삽입합니다.</p>
	 * 
	 * @param manager : 매니저 객체
	 */
	protected void addDefaults(Manager manager) throws NullPointerException
	{
		if(engine == null) throw new NullPointerException(Manager.applyStringTable("This script engine is not be initialized"));
		
		DebuggingUtilCollection.putAll(this);
		
		engine.put("manager", manager);
		engine.put("dao", manager.getDao());
		
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
		engine.put("swingc", swingc);
		
		engine.put("typehelp", new TypeCodeHelp());
		
		engine.put("attribute", getAttributes());
		
		TableSet emptyTableSet = new DefaultTableSet();
		emptyTableSet.getColumns().add(new Column());
		engine.put("shown_tableset", emptyTableSet);
	}
	
	/**
	 * <p>기본 함수를 생성합니다.</p>
	 * 
	 */
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
	public Object executeOnThread(final String script, boolean simplifyError, boolean notTraceHere) throws Throwable
	{
//		if(Main.MODE >= DebuggingUtil.DEBUG) Main.println(script);
		String runScripts = script;
		if(script.startsWith("file://") || script.startsWith("FILE://") || script.startsWith("File://"))
		{
			String filePath = script.substring(7);
			File target = new File(filePath);
			String readText = StreamUtil.readText(target, Manager.getOption("file_charset"));
			try
			{
				return execute(readText);
			}
			catch (Throwable e)
			{
				if(notTraceHere) throw new IncludesException(e);
				Main.logError(e, Manager.applyStringTable("On following script") + "...\n" + readText, simplifyError);
				
				return e;
			}
		}
		else if(script.startsWith("url://") || script.startsWith("URL://") || script.startsWith("Url://"))
		{
			String urlText = script.substring(6);
			URL target = new URL(urlText);
			String readText = StreamUtil.readText(target, Manager.getOption("file_charset"));
			try
			{
				return execute(readText);
			}
			catch (Throwable e)
			{
				if(notTraceHere) throw new IncludesException(e);
				Main.logError(e, Manager.applyStringTable("On following script") + "...\n" + readText, simplifyError);
				
				return e;
			}
		}
		else if(script.equalsIgnoreCase("exit"))
		{
			return new SpecialOrder("exit");
		}
		else if(script.equalsIgnoreCase("error_simple"))
		{
			return new SpecialOrder("error_simple");
		}
		else if(script.equalsIgnoreCase("error_detail"))
		{
			return new SpecialOrder("error_detail");
		}
		else if(script.startsWith("special://"))
		{
			return new SpecialOrder(script.substring(10));
		}
		
		try
		{
			Object results = engine.eval(script);
			futures.remove(this);
			return results;
		}
		catch(Throwable e)
		{
			if(notTraceHere) throw e;
			Main.logError(e, Manager.applyStringTable("On following script") + "...\n" + runScripts, simplifyError);
			
			return e;
		}
	}

	@Override
	public void put(String id, Object ob)
	{
		super.put(id, ob);
		engine.put(id, ob);
	}
	
	@Override
	public void noMoreUse()
	{
		super.noMoreUse();
		if(awtc != null) awtc.noMoreUse();
		if(swingc != null) swingc.noMoreUse();
	}
}