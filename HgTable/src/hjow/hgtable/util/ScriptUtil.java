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
package hjow.hgtable.util;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;

import hjow.hgtable.Main;
import hjow.hgtable.Manager;
import hjow.hgtable.jscript.JScriptRunner;
import hjow.hgtable.jscript.JavaScriptRunner;
import hjow.hgtable.jscript.OtherScriptRunner;
import hjow.hgtable.jscript.module.CommandRunner;

/**
 * <p>스크립트 엔진에 관련된 여러 정적 메소드들이 있습니다.</p>
 * 
 * @author HJOW
 *
 */
public class ScriptUtil
{
	private static transient Map<String, String> programArgs = new Hashtable<String, String>();
	private static boolean firstTime = true;
	private static List<JScriptRunner> runners = new Vector<JScriptRunner>();
	
	/**
	 * <p>스크립트 도구를 초기화합니다. 처음 실행 시에만 동작합니다.</p>
	 * 
	 * @param args : 프로그램 실행 시 입력된 매개 변수들
	 * @param manager : 매니저 객체
	 */
	public static void init(Map<String, String> args, Manager manager)
	{
		if(! firstTime) return;
		programArgs.putAll(args);
		
		List<List<String>> engines = ScriptUtil.getAvailableEngineGroups();
		for(List<String> e : engines)
		{
			String name = "";
			try
			{
				if(e.size() >= 1)
				{
					name = e.get(0);
					if(name.equalsIgnoreCase("js") || name.equalsIgnoreCase("JavaScript") 
							|| name.equalsIgnoreCase("nashorn") || name.equalsIgnoreCase("rhino")) continue;
					if(name.equalsIgnoreCase("Command")) continue;
					JScriptRunner newRunner = new OtherScriptRunner(manager, name);
					runners.add(newRunner);
				}
			}
			catch(NullPointerException e1)
			{
				manager.logError(e1, Manager.applyStringTable("On initializing script engine") + " " + name, true);
			}
			catch(Throwable e1)
			{
				manager.logError(e1, Manager.applyStringTable("On initializing script engine") + " " + name);
			}
		}
		runners.add(new CommandRunner(manager));
	}
	
	/**
	 * <p>J스크립트 엔진을 제외한 모든 스크립트 엔진들을 닫습니다.</p>
	 * 
	 */
	public static void noMoreUse()
	{
		programArgs.clear();
		
		for(JScriptRunner r : runners)
		{
			r.close();
		}
		runners.clear();
	}
	
	/**
	 * <p>사용 가능한 스크립트 엔진 이름들을 모두 반환합니다.</p>
	 * 
	 * @return 스크립트 엔진 이름 리스트
	 */
	public static List<String> getAvailableEngineNames()
	{
		List<String> availableNames = new Vector<String>();
		
		ScriptEngineManager manager = new ScriptEngineManager();
		List<ScriptEngineFactory> factories = manager.getEngineFactories();
		for (ScriptEngineFactory factory : factories)
		{
			availableNames.addAll(factory.getNames());
		}
		
		for(JScriptRunner engine : runners)
		{
			if(engine instanceof CommandRunner)
			{
				availableNames.add("Command");
				break;
			}
		}
		
		return availableNames;
	}
	
	/**
	 * <p>사용 가능한 스크립트 엔진 이름들을 모두 반환합니다. 같은 엔진을 의미하는 이름끼리 묶어 리스트를 만들고, 이 리스트들을 모두 반환합니다.</p>
	 * 
	 * @return 스크립트 엔진 이름 리스트의 리스트
	 */
	public static List<List<String>> getAvailableEngineGroups()
	{
		List<List<String>> availableNames = new Vector<List<String>>();
		
		ScriptEngineManager manager = new ScriptEngineManager();
		List<ScriptEngineFactory> factories = manager.getEngineFactories();
		for (ScriptEngineFactory factory : factories)
		{
			availableNames.add(factory.getNames());
		}
		
		return availableNames;
	}
	
	/**
	 * <p>기본 스크립트 엔진 키워드를 반환합니다. 이 값에 따라 엔진 동작이 확연히 달라집니다.</p>
	 * 
	 * @return 엔진 키워드
	 */
	public static String getDefaultEngineName()
	{
		if(DataUtil.parseBoolean(programArgs.get("use_nashorn")))
		{
			if(DataUtil.isNotEmpty(programArgs.get("java_version")) && Integer.parseInt(programArgs.get("java_version")) >= 8)
			{
				if(firstTime)
				{
					Main.println("Java version is " + programArgs.get("java_version"));
					Main.println("Nashorn is activated");
					firstTime = false;
				}
				return "nashorn";
			}
			else
			{
				Main.println("Cannot find java version.");
				Main.println("If this runtime is on java 8 or above, nashorn will be activated automatically.");
				return "JavaScript";
			}
		}
		
		return "JavaScript";
	}
	
	/**
	 * <p>기본 함수를 생성합니다.</p>
	 * 
	 * @param runner : 스크립트 엔진
	 * @throws Exception 스크립트 상 문제 (수정 필요)
	 */
	public static void createBasicFunctions(JScriptRunner runner) throws Throwable
	{
		String engineType = runner.getEngineType();
		if(engineType.equalsIgnoreCase("JavaScript") || engineType.equalsIgnoreCase("JS") || engineType.equalsIgnoreCase("nashorn"))
		{
			runner.execute("var log              = function(t) { manager.log(t);             };");
			runner.execute("var logNotLn         = function(t) { manager.logNotLn(t);        };");
			runner.execute("var alert            = function(t) { manager.alert(t);           };");
			runner.execute("var logMemory        = function()  { manager.logMemory();        };");
			runner.execute("var logMemoryDetails = function()  { manager.logMemoryDetails(); };");
			runner.execute("var gc               = function()  { jtool.gc();                 };");
			runner.execute("var query            = function(t) { return dao.query(t);        };");
			runner.execute("var menu             = function(t) { manager.runMenu('false');   };");
			runner.execute("var help             = function(t) { if(t != null) return t.help();"
					+ "else return \"" + Manager.applyStringTable("How about see jtool.help(), joc.help(), smb.help(), manager.help() or dao.help()?") + "\";"
			        + "};");
			runner.execute("var delVar      = function(t)                  {try { t.noMoreUse(); } catch(e) { }; t = null; }");
			runner.execute("var setInterval = function(scripts, intervals) { return jtool.setInterval(scripts, intervals); };");
			
			runner.execute("var eval             = function(t) { return jtool.eval(t);   };");
			runner.execute("var strings          = function(t) { return jtool.strings(t);   };");
			runner.execute("var integers         = function(t) { return jtool.integers(t);   };");
			runner.execute("var floats           = function(t) { return jtool.floats(t);   };");
			runner.execute("var booleans         = function(t) { return jtool.booleans(t);   };");
			runner.execute("var date             = function(t) { return jtool.date(t);   };");
			runner.execute("var newDao           = function(t) { return jtool.dao(t);   };");
			runner.execute("var selectedDaoIndex = function()  { return jtool.getSelectedDao();   };");
			runner.execute("var translate        = function(t) { return jtool.translate(t);   };");
			runner.execute("var stringTokenizer  = function(t, s) { return joc.newStringTokenizer(t, s);   };");
			runner.execute("var bigint           = function(t) { return joc.newBigInt(t);   };");
			runner.execute("var bigdec           = function(t) { return joc.newDecimal(t);   };");
			runner.execute("var map              = function()  { return joc.newMap();   };");
			runner.execute("var set              = function()  { return joc.newSet();   };");
			runner.execute("var list             = function()  { return joc.newList();   };");
			runner.execute("var bytes            = function(t) { return joc.newByteArray(t);   };");
			runner.execute("var clear            = function()  { smb.clearConsole(manager);   };");
			runner.execute("var isEmpty          = function(t) { return smb.isEmpty(t);   };");
			runner.execute("var isInt            = function(t) { return smb.isInteger(t);   };");
			runner.execute("var isFloat          = function(t) { return smb.isFloat(t);   };");
			runner.execute("var isNumber         = function(t) { return smb.isNumber(t);   };");
			runner.execute("var cast             = function(t, s) { return smb.castQuote(t, s);   };");
			runner.execute("var uncast           = function(t, s) { return smb.reCastQuote(t, s);   };");
			runner.execute("var castTotal        = function(t, s) { return smb.castTotal(t, s);   };");
			runner.execute("var reCastTotal      = function(t, s) { return smb.reCastTotal(t, s);   };");
			runner.execute("var encrypt          = function(t, s, v) { return smb.encrypt(t, s, v);   };");
			runner.execute("var decrypt          = function(t, s, v) { return smb.decrypt(t, s, v);   };");
			runner.execute("var hash             = function(t, s) { return smb.hash(t, s);   };");
		}
		else if(engineType.equalsIgnoreCase("ruby") || engineType.equalsIgnoreCase("jruby"))
		{
			runner.execute(" def log(t)               \n  $manager.log(t);             \n end");
			runner.execute(" def logNotLn(t)          \n  $manager.logNotLn(t);        \n end");
			runner.execute(" def alert(t)             \n  $manager.alert(t);           \n end");
			runner.execute(" def logMemory()          \n  $manager.logMemory();        \n end");
			runner.execute(" def logMemoryDetails()   \n  $manager.logMemoryDetails(); \n end");
			runner.execute(" def gc()                 \n  $jtool.gc();                 \n end");
			runner.execute(" def query(t)             \n  $dao.query(t);               \n end");
			runner.execute(" def menu(t)              \n  $manager.runMenu('false');   \n end");
			runner.execute(" def help(t)              \n  if t != null then \n    t.help(); \n"
					+ "  else \n   \"" + Manager.applyStringTable("How about see $jtool.help(), $joc.help(), $smb.help(), $manager.help() or $dao.help()?") + "\"; \n"
			        + "\n  end \n end");
			runner.execute(" def delVar(t)            \n begin \n t.noMoreUse(); \n rescue Exception => e \n \n end \n t = null; \n end");
			runner.execute(" def setInterval(scripts, intervals) \n  $jtool.setInterval(scripts, intervals); \n end");
			
			runner.execute(" def eval(t)                   \n  $jtool.eval(t);                \n end");
			runner.execute(" def strings(t)                \n  $jtool.strings(t);             \n end");
			runner.execute(" def integers(t)               \n  $jtool.integers(t);            \n end");
			runner.execute(" def floats(t)                 \n  $jtool.floats(t);              \n end");
			runner.execute(" def booleans(t)               \n  $jtool.booleans(t);            \n end");
			runner.execute(" def date(t)                   \n  $jtool.date(t);                \n end");
			runner.execute(" def newDao(t)                 \n  $jtool.dao(t);                 \n end");
			runner.execute(" def selectedDaoIndex()        \n  $jtool.getSelectedDao();       \n end");
			runner.execute(" def translate(t)              \n  $jtool.translate(t);           \n end");
			runner.execute(" def stringTokenizer(t, s)     \n  $joc.newStringTokenizer(t, s); \n end");
			runner.execute(" def bigint(t)                 \n  $joc.newBigInt(t);             \n end");
			runner.execute(" def bigdec(t)                 \n  $joc.newDecimal(t);            \n end");
			runner.execute(" def map()                     \n  $joc.newMap();                 \n end");
			runner.execute(" def set()                     \n  $joc.newSet();                 \n end");
			runner.execute(" def list()                    \n  $joc.newList();                \n end");
			runner.execute(" def bytes(t)                  \n  $joc.newByteArray(t);          \n end");
			runner.execute(" def clear()                   \n  $smb.clearConsole(manager);    \n end");
			runner.execute(" def isEmpty(t)                \n  $smb.isEmpty(t);               \n end");
			runner.execute(" def isInt(t)                  \n  $smb.isInteger(t);             \n end");
			runner.execute(" def isFloat(t)                \n  $smb.isFloat(t);               \n end");
			runner.execute(" def isNumber(t)               \n  $smb.isNumber(t);              \n end");
			runner.execute(" def cast(t, s)                \n  $smb.castQuote(t, s);          \n end");
			runner.execute(" def uncast(t, s)              \n  $smb.reCastQuote(t, s);        \n end");
			runner.execute(" def castTotal(t, s)           \n  $smb.castTotal(t, s);          \n end");
			runner.execute(" def reCastTotal(t, s)         \n  $smb.reCastTotal(t, s);        \n end");
			runner.execute(" def encrypt(t, s, v)          \n  $smb.encrypt(t, s, v);         \n end");
			runner.execute(" def decrypt(t, s, v)          \n  $smb.decrypt(t, s, v);         \n end");
			runner.execute(" def hash(t, s)                \n  $smb.hash(t, s);               \n end");
		}
		else if(engineType.equalsIgnoreCase("python") || engineType.equalsIgnoreCase("jython"))
		{
			runner.execute("def log(t):               \n manager.log(t);             \n\n");
			runner.execute("def logNotLn(t):          \n manager.logNotLn(t);        \n\n");
			runner.execute("def alert(t):             \n manager.alert(t);           \n\n");
			runner.execute("def logMemory():          \n manager.logMemory();        \n\n");
			runner.execute("def logMemoryDetails():   \n manager.logMemoryDetails(); \n\n");
			runner.execute("def gc():                 \n jtool.gc();                 \n\n");
			runner.execute("def query(t):             \n return dao.query(t);        \n\n");
			runner.execute("def menu(t):              \n manager.runMenu('false');   \n\n");
			runner.execute("def help(t):              \n if t != null: \n  return t.help(); \n"
					+ " else: \n  return \"" + Manager.applyStringTable("How about see jtool.help(), joc.help(), smb.help(), manager.help() or dao.help()?") + "\";"
			        + "\n \n\n");
			runner.execute("def delVar(t):            \n try: \n  t.noMoreUse(); \n except: \n  t = null;\n \n t = null; \n\n");
			runner.execute("def setInterval(scripts, intervals): \n return jtool.setInterval(scripts, intervals); \n\n");
			
			runner.execute("def eval(t):                \n return jtool.eval(t);                \n\n");
			runner.execute("def strings(t):             \n return jtool.strings(t);             \n\n");
			runner.execute("def integers(t):            \n return jtool.integers(t);            \n\n");
			runner.execute("def floats(t):              \n return jtool.floats(t);              \n\n");
			runner.execute("def booleans(t):            \n return jtool.booleans(t);            \n\n");
			runner.execute("def date(t):                \n return jtool.date(t);                \n\n");
			runner.execute("def newDao(t):              \n return jtool.dao(t);                 \n\n");
			runner.execute("def selectedDaoIndex ():    \n return jtool.getSelectedDao();       \n\n");
			runner.execute("def translate(t):           \n return jtool.translate(t);           \n\n");
			runner.execute("def stringTokenizer(t, s):  \n return joc.newStringTokenizer(t, s); \n\n");
			runner.execute("def bigint(t):              \n return joc.newBigInt(t);             \n\n");
			runner.execute("def bigdec(t):              \n return joc.newDecimal(t);            \n\n");
			runner.execute("def map():                  \n return joc.newMap();                 \n\n");
			runner.execute("def set():                  \n return joc.newSet();                 \n\n");
			runner.execute("def list():                 \n return joc.newList();                \n\n");
			runner.execute("def bytes(t):               \n return joc.newByteArray(t);          \n\n");
			runner.execute("def clear():                \n smb.clearConsole(manager);           \n\n");
			runner.execute("def isEmpty(t):             \n return smb.isEmpty(t);               \n\n");
			runner.execute("def isInt(t):               \n return smb.isInteger(t);             \n\n");
			runner.execute("def isFloat(t):             \n return smb.isFloat(t);               \n\n");
			runner.execute("def isNumber(t):            \n return smb.isNumber(t);              \n\n");
			runner.execute("def cast(t, s):             \n return smb.castQuote(t, s);          \n\n");
			runner.execute("def uncast(t, s):           \n return smb.reCastQuote(t, s);        \n\n");
			runner.execute("def castTotal(t, s):        \n return smb.castTotal(t, s);          \n\n");
			runner.execute("def reCastTotal(t, s):      \n return smb.reCastTotal(t, s);        \n\n");
			runner.execute("def encrypt(t, s, v):       \n return smb.encrypt(t, s, v);         \n\n");
			runner.execute("def decrypt(t, s, v):       \n return smb.decrypt(t, s, v);         \n\n");
			runner.execute("def hash(t, s):             \n return smb.hash(t, s);               \n\n");
		}
	}

	/**
	 * <p>이름에 해당하는 준비된 스크립트 엔진을 반환합니다. 단, J스크립트 엔진은 목록에 포함되지 않습니다.</p>
	 * 
	 * @param name 스크립트 이름
	 * @return 해당 스크립트 엔진
	 */
	public static JScriptRunner getRunner(String name)
	{
		for(JScriptRunner r : runners)
		{
			if(r.getEngineType().equals(name))
			{
				return r;
			}
		}
		return null;
	}
	
	/**
	 * <p>J스크립트 엔진을 제외한 준비된 스크립트 엔진 갯수를 반환합니다.</p>
	 * 
	 * @return 준비된 스크립트 엔진 갯수
	 */
	public static int getRunnerCount()
	{
		return runners.size();
	}

	/**
	 * <p>준비된 스크립트 이름들을 반환합니다. 단, J스크립트는 목록에 포함되지 않습니다.</p>
	 * 
	 * @return 준비된 스크립트 이름 리스트
	 */
	public static List<String> getPreparedScriptNames()
	{
		List<String> names = new Vector<String>();
		for(JScriptRunner r : runners)
		{
			names.add(r.getEngineType());
		}
		return names;
	}
	
	/**
	 * <p>준비된 스크립트 엔진들 모두(J스크립트 엔진 제외)에 객체를 삽입합니다.</p>
	 * 
	 * @param id : 사용할 변수 이름
	 * @param ob : 삽입할 객체
	 */
	public static void putObject(String id, Object ob)
	{
		for(JScriptRunner r : runners)
		{
			r.put(id, ob);
			r.getAttributes().put(id, ob);
		}
	}

	/**
	 * <p>새 스크립트 실행 모듈을 반환합니다.</p>
	 * 
	 * @param manager : 매니저 객체
	 * @param args : 매개 변수들
	 * @return 스크립트 실행 모듈
	 */
	public static JScriptRunner newScriptRunner(Manager manager, Map<String, String> args)
	{	
		if(args != null)
		{
			String mainScriptType = args.get("mainScriptType");
			if(mainScriptType != null)
			{
				mainScriptType = mainScriptType.trim();
				if(mainScriptType.equalsIgnoreCase("js") || mainScriptType.equalsIgnoreCase("JavaScript") || mainScriptType.equalsIgnoreCase("JScript")
						 || mainScriptType.equalsIgnoreCase("Nashorn") || mainScriptType.equalsIgnoreCase("Rhino"))
				{
					return new JavaScriptRunner(manager);
				}
				else return new OtherScriptRunner(manager, mainScriptType);
			}
			else return new JavaScriptRunner(manager);
		}
		else return new JavaScriptRunner(manager);
	}
}
