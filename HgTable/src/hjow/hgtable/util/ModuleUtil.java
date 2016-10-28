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

import java.io.File;
import java.io.FileFilter;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Random;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import hjow.hgtable.Main;
import hjow.hgtable.Manager;
import hjow.hgtable.analyze.AnalyzeFunction;
import hjow.hgtable.classload.ClassTool;
import hjow.hgtable.classload.DynamicLoader;
import hjow.hgtable.jscript.module.AbstractModule;
import hjow.hgtable.jscript.module.Module;
import hjow.hgtable.jscript.module.ScriptConsoleModule;
import hjow.hgtable.ui.GUIManager;
import hjow.hgtable.ui.module.ModuleDataPack;
import hjow.hgtable.ui.module.ScriptDialogModule;
import hjow.hgtable.ui.module.ScriptPanelModule;
import hjow.hgtable.ui.module.defaults.ArgumentInjector;
import hjow.hgtable.ui.module.defaults.CreateModuleTool;
import hjow.hgtable.ui.module.defaults.DefaultToolbar;
import hjow.hgtable.ui.module.defaults.FavoriteConnector;
import hjow.hgtable.ui.view.SwingCreateTable;
import hjow.hgtable.ui.view.SwingFunctionListView;
import hjow.hgtable.ui.view.SwingModuleListView;
import hjow.hgtable.ui.view.SwingProcedureListView;
import hjow.hgtable.ui.view.SwingTableListView;
import hjow.hgtable.ui.view.SwingViewListView;

/**
 * <p>모듈을 불러오는 데 사용하는 여러 정적 메소드들이 있습니다.</p>
 * 
 * @author HJOW
 *
 */
public class ModuleUtil
{
	protected static List<Long> authorizedModuleIds = new Vector<Long>();
	
	/**
	 * <p>기본 제공 클래스 컴파일형 모듈 클래스 이름들을 반환합니다.</p>
	 * 
	 * @return 기본 제공 클래스 모듈의 클래스 이름 리스트
	 */
	protected static List<String> defaultClassModuleClassNames()
	{
		List<String> classes = new Vector<String>();
		
		classes.add("externalModules.MemoryView");
		classes.add("externalModules.HttpTool");
		classes.add("externalModules.PLSQLView");
		classes.add("externalModules.MassiveFiles");
		classes.add("externalModules.oracle.PLSQLRecompiler");
        classes.add("externalModules.FavoriteEditor");
        classes.add("externalModules.MemoryToolbar");
        classes.add("externalModules.StackTraceView");
		classes.add("externalModules.h2.H2Runner");
		classes.add("externalModules.MassiveSave");
		classes.addAll(LicenseUtil.defaultClassModuleClassNames());
		if(DataUtil.parseBoolean(Manager.getOption("use_testing_functions"))) classes.add("externalModules.oracle.SessionView");
		
		return classes;
	}
	
	/**
	 * <p>기본 디렉토리의 파일들로부터 모듈들을 불러옵니다.</p>
	 * 
	 * @return 불러온 모듈들
	 * @exception 파일 액세스 문제
	 */
	public static List<Module> loadModules(Manager manager) throws Exception
	{
		List<Module> modules = new Vector<Module>();
		modules.addAll(LicenseUtil.modules());
		
		File configPath = new File(Manager.getOption("config_path"));	
		FileFilter moduleFilter = new FileFilter()
		{
			@Override
			public boolean accept(File pathname)
			{
				return (pathname.getAbsolutePath().endsWith(".hgm") || pathname.getAbsolutePath().endsWith(".HGM")
						|| pathname.getAbsolutePath().endsWith(".Hgm")
						|| pathname.getAbsolutePath().endsWith(".hgmz")
						|| pathname.getAbsolutePath().endsWith(".HGMZ")
						|| pathname.getAbsolutePath().endsWith(".Hgmz")
						|| pathname.getAbsolutePath().endsWith(".hgmb")
						|| pathname.getAbsolutePath().endsWith(".HGMB")
						|| pathname.getAbsolutePath().endsWith(".Hgmb"));
			}
		};
		
		if(configPath.exists() && configPath.isDirectory())
		{
			File modulePath = new File(Manager.getOption("config_path") + "modules" + Manager.getOption("file_separator"));
			if(modulePath.exists() && modulePath.isDirectory())
			{
				File[] moduleFiles = modulePath.listFiles(moduleFilter);
				
				for(File f : moduleFiles)
				{
					try
					{
						ModuleDataPack packs = null;
						
						if(! f.exists()) continue;
						if(f.getAbsolutePath().endsWith(".hgmz") || f.getAbsolutePath().endsWith(".HGMZ") || f.getAbsolutePath().endsWith(".Hgmz"))
						{
							packs = inputData(StreamUtil.readText(f, "UTF-8", true), manager);
						}
						else if(f.getAbsolutePath().endsWith(".hgmb") || f.getAbsolutePath().endsWith(".HGMB") || f.getAbsolutePath().endsWith(".Hgmb"))
						{
							packs = (ModuleDataPack) StreamUtil.readObject(f, true);
						}
						else packs = inputData(StreamUtil.readText(f, "UTF-8"), manager);
						
						if(packs.getModuleType().equalsIgnoreCase("Dialog"))
						{
							if(manager instanceof GUIManager)
							{
								ScriptDialogModule newDialogModule = new ScriptDialogModule((GUIManager) manager);
								newDialogModule.input(packs);
								modules.add(newDialogModule);
							}
						}
						else if(packs.getModuleType().equalsIgnoreCase("Panel"))
						{
							if(manager instanceof GUIManager)
							{
								ScriptPanelModule newDialogModule = new ScriptPanelModule((GUIManager) manager);
								newDialogModule.input(packs);
								modules.add(newDialogModule);
							}
						}
						else if(packs.getModuleType().equalsIgnoreCase("Console"))
						{
							ScriptConsoleModule newConsoleModule = new ScriptConsoleModule(manager);
							newConsoleModule.input(packs);
							modules.add(newConsoleModule);
						}
					}
					catch(Exception e)
					{
						Main.logError(e, Manager.applyStringTable("On loading module from") + " : " + String.valueOf(f));
					}
				}
			}
		}
		
		try
		{
			File selfPath = new File(ClassTool.currentDirectory());
			if(selfPath.exists() && selfPath.isDirectory())
			{
				File modulePath = new File(ClassTool.currentDirectory() + "modules" + Manager.getOption("file_separator"));
				if(modulePath.exists() && modulePath.isDirectory())
				{
					
					File[] moduleFiles = modulePath.listFiles(moduleFilter);
					
					for(File f : moduleFiles)
					{
						try
						{
							ModuleDataPack packs = null;
							if(f.getAbsolutePath().endsWith(".hgmz") || f.getAbsolutePath().endsWith(".HGMZ") || f.getAbsolutePath().endsWith(".Hgmz"))
							{
								packs = inputData(StreamUtil.readText(f, "UTF-8", true), manager);
							}
							else if(f.getAbsolutePath().endsWith(".hgmb") || f.getAbsolutePath().endsWith(".HGMB") || f.getAbsolutePath().endsWith(".Hgmb"))
							{
								packs = (ModuleDataPack) StreamUtil.readObject(f, true);
							}
							else packs = inputData(StreamUtil.readText(f, "UTF-8"), manager);
							
							if(packs.getModuleType().equalsIgnoreCase("Dialog"))
							{
								if(manager instanceof GUIManager)
								{
									ScriptDialogModule newDialogModule = new ScriptDialogModule((GUIManager) manager);
									newDialogModule.input(packs);
									modules.add(newDialogModule);
								}
							}
							else if(packs.getModuleType().equalsIgnoreCase("Panel"))
							{
								if(manager instanceof GUIManager)
								{
									ScriptPanelModule newPanelModule = new ScriptPanelModule((GUIManager) manager);
									newPanelModule.input(packs);
									modules.add(newPanelModule);
								}
							}
							else if(packs.getModuleType().equalsIgnoreCase("Console"))
							{
								ScriptConsoleModule newConsoleModule = new ScriptConsoleModule(manager);
								newConsoleModule.input(packs);
								modules.add(newConsoleModule);
							}
						}
						catch(Exception e)
						{
							Main.logError(e, Manager.applyStringTable("On loading module from") + " : " + String.valueOf(f));
						}
					}
				}
			}
		}
		catch(Exception e)
		{
			
		}
		
		for(ModuleDataPack p : defaultModules())
		{
			if(p.getModuleType().equalsIgnoreCase("Dialog"))
			{
				if(manager instanceof GUIManager)
				{
					ScriptDialogModule newDialogModule = new ScriptDialogModule((GUIManager) manager);
					newDialogModule.input(p);
					modules.add(newDialogModule);
				}
			}
			else if(p.getModuleType().equalsIgnoreCase("Panel"))
			{
				if(manager instanceof GUIManager)
				{
					ScriptPanelModule newPanelModule = new ScriptPanelModule((GUIManager) manager);
					newPanelModule.input(p);
					modules.add(newPanelModule);
				}
			}
			else if(p.getModuleType().equalsIgnoreCase("Console"))
			{
				ScriptConsoleModule newConsoleModule = new ScriptConsoleModule(manager);
				newConsoleModule.input(p);
				modules.add(newConsoleModule);
			}
		}
		
		return modules;
	}
	
	/**
	 * <p>모듈 정의 형식 텍스트를 모듈 정보 단위로 변환합니다.</p>
	 * 
	 * @param contents : 형식 텍스트
	 * @param manager : 매니저 객체
	 * @return 모듈 정보
	 */
	public static ModuleDataPack inputData(String contents, Manager manager)
	{
		StringTokenizer lineTokenizer = new StringTokenizer(contents, "\n");
		
		ModuleDataPack newModuleDataPack = new ModuleDataPack();
		
		String moduleType = "";
		boolean initScriptMode = false;
		boolean afterInitScriptMode = false;
		boolean refreshScriptMode = false;
		boolean finalizeMode = false;
		boolean showMenuMode = false;
		boolean actMenuMode = false;
		boolean descriptionMode = false;
		boolean options = false;
		
		String name = "";
		String initScripts = "";
		String afterInitScripts = "";
		String refreshScripts = "";
		String finalizeScripts = "";
		String showMenuScripts = "";
		String actMenuScripts = "";
		String scriptType = "";
		String descriptions = "";
		long moduleId = new Random().nextLong();
		
		while(lineTokenizer.hasMoreTokens())
		{
			String lines = lineTokenizer.nextToken().trim();
			
			if(lines.startsWith("#")) continue;
//			else if(lines.startsWith("//")) continue;
			else if(DataUtil.isEmpty(lines)) continue;
			else if(lines.startsWith("type://") || lines.startsWith("TYPE://") || lines.startsWith("Type://"))
			{
				moduleType = lines.substring(new String("type://").length()).trim();
			}
			else if(lines.startsWith("name://") || lines.startsWith("NAME://") || lines.startsWith("Name://"))
			{
				name = lines.substring(new String("name://").length()).trim();
			}
			else if(lines.startsWith("id://") || lines.startsWith("ID://") || lines.startsWith("Id://"))
			{
				moduleId = Long.parseLong(lines.substring(new String("id://").length()).trim());
			}
			else if(lines.startsWith("syntax://") || lines.startsWith("SYNTAX://") || lines.startsWith("Syntax://"))
			{
				scriptType = lines.substring(new String("syntax://").length()).trim();
			}
			else if(lines.startsWith("init://") || lines.startsWith("INIT://") || lines.startsWith("Init://"))
			{
				initScriptMode = true;
				afterInitScriptMode = false;
				refreshScriptMode = false;
				finalizeMode = false;
				showMenuMode = false;
				actMenuMode = false;
				descriptionMode = false;
				options = false;
				
				String subs = lines.substring(new String("init://").length());
				if(DataUtil.isNotEmpty(subs))
				{
					initScripts = initScripts + subs;
				}
			}
			else if(lines.startsWith("afterinit://") || lines.startsWith("AFTERINIT://") || lines.startsWith("Afterinit://") || lines.startsWith("AfterInit://"))
			{
				initScriptMode = false;
				afterInitScriptMode = true;
				refreshScriptMode = false;
				finalizeMode = false;
				showMenuMode = false;
				actMenuMode = false;
				descriptionMode = false;
				options = false;
				
				String subs = lines.substring(new String("afterinit://").length());
				if(DataUtil.isNotEmpty(subs))
				{
					afterInitScripts = afterInitScripts + subs;
				}
			}
			else if(lines.startsWith("refresh://") || lines.startsWith("REFRESH://") || lines.startsWith("Refresh://"))
			{
				initScriptMode = false;
				afterInitScriptMode = false;
				refreshScriptMode = true;
				finalizeMode = false;
				showMenuMode = false;
				actMenuMode = false;
				descriptionMode = false;
				options = false;
				
				String subs = lines.substring(new String("refresh://").length());
				if(DataUtil.isNotEmpty(subs))
				{
					refreshScripts = refreshScripts + subs;
				}
			}
			else if(lines.startsWith("finalize://") || lines.startsWith("FINALIZE://") || lines.startsWith("Finalize://"))
			{
				initScriptMode = false;
				afterInitScriptMode = false;
				refreshScriptMode = false;
				finalizeMode = true;
				showMenuMode = false;
				actMenuMode = false;
				descriptionMode = false;
				options = false;
				
				String subs = lines.substring(new String("finalize://").length());
				if(DataUtil.isNotEmpty(subs))
				{
					finalizeScripts = finalizeScripts + subs;
				}
			}
			else if(lines.startsWith("showmenu://") || lines.startsWith("SHOWMENU://") || lines.startsWith("Showmenu://") || lines.startsWith("ShowMenu://"))
			{
				initScriptMode = false;
				afterInitScriptMode = false;
				refreshScriptMode = false;
				finalizeMode = false;
				showMenuMode = true;
				actMenuMode = false;
				descriptionMode = false;
				options = false;
				
				String subs = lines.substring(new String("showmenu://").length());
				if(DataUtil.isNotEmpty(subs))
				{
					showMenuScripts = showMenuScripts + subs;
				}
			}
			else if(lines.startsWith("actmenu://") || lines.startsWith("ACTMENU://") || lines.startsWith("Actmenu://") || lines.startsWith("ActMenu://"))
			{
				initScriptMode = false;
				afterInitScriptMode = false;
				refreshScriptMode = false;
				finalizeMode = false;
				showMenuMode = false;
				actMenuMode = true;
				descriptionMode = false;
				options = false;
				
				String subs = lines.substring(new String("actmenu://").length());
				if(DataUtil.isNotEmpty(subs))
				{
					actMenuScripts = actMenuScripts + subs;
				}
			}
			else if(lines.startsWith("description://") || lines.startsWith("DESCRIPTION://") || lines.startsWith("Description://"))
			{
				initScriptMode = false;
				afterInitScriptMode = false;
				refreshScriptMode = false;
				finalizeMode = false;
				showMenuMode = false;
				actMenuMode = false;
				descriptionMode = true;
				options = false;
				
				String subs = lines.substring(new String("description://").length());
				if(DataUtil.isNotEmpty(subs))
				{
					descriptions = descriptions + subs;
				}
			}
			else if(lines.startsWith("option://") || lines.startsWith("OPTION://") || lines.startsWith("Option://"))
			{
				initScriptMode = false;
				afterInitScriptMode = false;
				refreshScriptMode = false;
				finalizeMode = false;
				showMenuMode = false;
				actMenuMode = false;
				descriptionMode = false;
				options = true;
				
				String subs = lines.substring(new String("option://").length());
				if(DataUtil.isNotEmpty(subs))
				{
					String[] optionSplit = subs.split("=");
					if(optionSplit.length <= 1)
					{
						newModuleDataPack.getMoreOptions().put(optionSplit[0], "true");
					}
					else
					{
						newModuleDataPack.getMoreOptions().put(optionSplit[0], optionSplit[1]);
					}
				}
			}
			else if(lines.startsWith("file://") || lines.startsWith("FILE://") || lines.startsWith("File://"))
			{
				String filename = lines.substring(new String("file://").length());
				ModuleDataPack getPack = inputData(StreamUtil.readText(new File(filename), "UTF-8"), manager);
				
				if(DataUtil.isNotEmpty(getPack.getName()))
				{
					name = getPack.getName();
				}
				if(DataUtil.isNotEmpty(getPack.getModuleType()))
				{
					moduleType = getPack.getModuleType();
				}
				if(DataUtil.isNotEmpty(initScripts))
				{
					initScripts = initScripts + "\n" + getPack.getInitScripts();
				}
				else
				{
					initScripts = getPack.getInitScripts();
				}
				if(DataUtil.isNotEmpty(refreshScripts))
				{
					refreshScripts = refreshScripts + "\n" + getPack.getRefreshScripts();
				}
				else
				{
					refreshScripts = getPack.getRefreshScripts();
				}
				if(DataUtil.isNotEmpty(finalizeScripts))
				{
					finalizeScripts = finalizeScripts + "\n" + getPack.getFinalizeScripts();
				}
				else
				{
					finalizeScripts = getPack.getFinalizeScripts();
				}
			}
			else if(lines.startsWith("url://") || lines.startsWith("URL://") || lines.startsWith("Url://"))
			{
				String url = lines.substring(new String("url://").length());
				try
				{
					ModuleDataPack getPack = inputData(StreamUtil.readText(new URL(url), "UTF-8"), manager);
					
					if(DataUtil.isNotEmpty(getPack.getName()))
					{
						name = getPack.getName();
					}
					if(DataUtil.isNotEmpty(getPack.getModuleType()))
					{
						moduleType = getPack.getModuleType();
					}
					if(DataUtil.isNotEmpty(initScripts))
					{
						initScripts = initScripts + "\n" + getPack.getInitScripts();
					}
					else
					{
						initScripts = getPack.getInitScripts();
					}
					if(DataUtil.isNotEmpty(refreshScripts))
					{
						refreshScripts = refreshScripts + "\n" + getPack.getRefreshScripts();
					}
					else
					{
						refreshScripts = getPack.getRefreshScripts();
					}
					if(DataUtil.isNotEmpty(finalizeScripts))
					{
						finalizeScripts = finalizeScripts + "\n" + getPack.getFinalizeScripts();
					}
					else
					{
						finalizeScripts = getPack.getFinalizeScripts();
					}
				}
				catch (Exception e)
				{
					Main.logError(e, Manager.applyStringTable("On accessing URL") + " : " + url);
				}
			}
			else if(initScriptMode)
			{
				if(! (initScripts.trim().equals(""))) initScripts = initScripts + "\n";
				initScripts = initScripts + lines;
			}
			else if(afterInitScriptMode)
			{
				if(! (afterInitScripts.trim().equals(""))) afterInitScripts = afterInitScripts + "\n";
				afterInitScripts = afterInitScripts + lines;
			}
			else if(refreshScriptMode)
			{
				if(! (refreshScripts.trim().equals(""))) refreshScripts = refreshScripts + "\n";
				refreshScripts = refreshScripts + lines;
			}
			else if(finalizeMode)
			{
				if(! (finalizeScripts.trim().equals(""))) finalizeScripts = finalizeScripts + "\n";
				finalizeScripts = finalizeScripts + lines;
			}
			else if(showMenuMode)
			{
				if(! (showMenuScripts.trim().equals(""))) showMenuScripts = showMenuScripts + "\n";
				showMenuScripts = showMenuScripts + lines;
			}
			else if(actMenuMode)
			{
				if(! (actMenuScripts.trim().equals(""))) actMenuScripts = actMenuScripts + "\n";
				actMenuScripts = actMenuScripts + lines;
			}
			else if(descriptionMode)
			{
				if(! (descriptions.trim().equals(""))) descriptions = descriptions + "\n";
				descriptions = descriptions + lines;
			}
			else if(options)
			{
				String[] optionSplit = lines.split("=");
				if(optionSplit.length <= 1)
				{
					newModuleDataPack.getMoreOptions().put(optionSplit[0], "true");
				}
				else
				{
					newModuleDataPack.getMoreOptions().put(optionSplit[0], optionSplit[1]);
				}
			}
		}
		
		newModuleDataPack.setModuleType(moduleType);
		newModuleDataPack.setName(name);
		newModuleDataPack.setModuleId(new Long(moduleId));
		newModuleDataPack.setInitScripts(initScripts);
		newModuleDataPack.setAfterInitScripts(afterInitScripts);
		newModuleDataPack.setRefreshScripts(refreshScripts);
		newModuleDataPack.setFinalizeScripts(finalizeScripts);
		newModuleDataPack.setActMenuScripts(actMenuScripts);
		newModuleDataPack.setShowMenuScripts(showMenuScripts);
		newModuleDataPack.setScriptType(scriptType);
		newModuleDataPack.setDescription(descriptions);
		
		return newModuleDataPack;
	}
	
	/**
	 * <p>기본 제공 모듈들에 대한 정보를 반환합니다.</p>
	 * 
	 * @return 모듈 정보 객체 리스트
	 */
	public static List<ModuleDataPack> defaultModules()
	{
		List<ModuleDataPack> defaultModules = new Vector<ModuleDataPack>();
		return defaultModules;
	}
	
	/**
	 * <p>기본으로 제공되는 클래스 컴파일형 모듈 정보를 반환합니다. 대부분 스칼라 언어로 만들어지며, externalModules 패키지에 속합니다.</p>
	 * 
	 * @return 기본 제공 클래스 모듈 정보
	 */
	protected static List<ClassNameWithPath> defaultClassModuleLists()
	{
		List<ClassNameWithPath> classModuleList = new Vector<ClassNameWithPath>();
		
		String separator = Manager.getOption("file_separator");
		
		String defaultClassModulePath = ClassTool.currentDirectory() + "externalModules" + separator;
		
		for(String s : defaultClassModuleClassNames())
		{
			ClassNameWithPath defaults = new ClassNameWithPath();
			defaults.setName(s);
			defaults.setPath("FILE://" + defaultClassModulePath);
			classModuleList.add(defaults);
		}
		
		return classModuleList;
	}
	
	/**
	 * <p>클래스로 컴파일되거나 JAR 파일로 클래스패스에 등록된 모듈을 불러옵니다.</p>
	 * 
	 * @return 클래스 모듈 정보
	 */
	public static List<ClassNameWithPath> defaultClassModules()
	{
		List<ClassNameWithPath> classModuleList = new Vector<ClassNameWithPath>();
		classModuleList.addAll(defaultClassModuleLists());
		
		String configPath = Manager.getOption("config_path");
		String tokenDelim = "=";
		
		StringTokenizer colonTokenizer = null;
		try
		{
			File scalaModuleListFile = new File(configPath + "class_modules.cfg");
			if(scalaModuleListFile.exists())
			{
				StringTokenizer lineTokenizer = new StringTokenizer(StreamUtil.readText(scalaModuleListFile, "UTF-8"), "\n");
				while(lineTokenizer.hasMoreTokens())
				{
					String lines = lineTokenizer.nextToken().trim();
					ClassNameWithPath lineEntry = new ClassNameWithPath();
					if(DataUtil.isEmpty(lines)) continue;
					if(lines.startsWith("#")) continue;
					
					colonTokenizer = new StringTokenizer(lines, tokenDelim);
					lineEntry.setName(colonTokenizer.nextToken().trim());
					if(colonTokenizer.hasMoreTokens()) lineEntry.setPath(colonTokenizer.nextToken().trim());
					while(colonTokenizer.hasMoreTokens())
					{
						lineEntry.add(colonTokenizer.nextToken());
					}
					
					boolean alreadyExist = false;
					for(ClassNameWithPath c : classModuleList)
					{
						if(c.getName().equals(lineEntry.getName()))
						{
							alreadyExist = true;
							break;
						}
					}
					
					if(! alreadyExist) classModuleList.add(lineEntry);
				}
			}
			else
			{
				StringBuffer newContents = new StringBuffer("# " + Manager.applyStringTable("Input class names and its path to use Class-based modules.") + "\n");
				String defaultPath = "File://" + ClassTool.currentDirectory() + "externalModules" + Manager.getOption("file_separator");
				
				for(String s : defaultClassModuleClassNames())
				{
					newContents = newContents.append(s + tokenDelim + defaultPath + "\n");
				}
				
				StreamUtil.saveFile(scalaModuleListFile, newContents.toString(), "UTF-8");
			}
			
			String classOptions = Manager.getOption("class_modules");
			if(DataUtil.isNotEmpty(classOptions))
			{
				StringTokenizer commaTokenizer = new StringTokenizer(classOptions.trim(), ",");
				while(commaTokenizer.hasMoreTokens())
				{
					String elements = commaTokenizer.nextToken().trim();
					ClassNameWithPath elementEntry = new ClassNameWithPath();
					if(DataUtil.isEmpty(elements)) continue;
					
					colonTokenizer = new StringTokenizer(elements, tokenDelim);
					elementEntry.setName(colonTokenizer.nextToken().trim());
					if(colonTokenizer.hasMoreTokens()) elementEntry.setPath(colonTokenizer.nextToken().trim());
					while(colonTokenizer.hasMoreTokens())
					{
						elementEntry.add(colonTokenizer.nextToken());
					}
					
					boolean alreadyExist = false;
					for(ClassNameWithPath c : classModuleList)
					{
						if(c.getName().equals(elementEntry.getName()))
						{
							alreadyExist = true;
							break;
						}
					}
					
					if(! alreadyExist) classModuleList.add(elementEntry);
				}
			}
		}
		catch(Exception e)
		{
			Main.logError(e, Manager.applyStringTable("On reading scalamodule config"));
		}
		
		return classModuleList;
	}
	
	/**
	 * <p>자바로 구현된 기본 내장 모듈들의 ID를 반환합니다.</p>
	 * 
	 * @return 내장 모듈들 ID
	 */
	public static List<Long> defaultJavaModulesIds()
	{
		List<Long> ids = new Vector<Long>();
		
		ids.add(new Long(ArgumentInjector.sid));
		ids.add(new Long(CreateModuleTool.sid));
		ids.add(new Long(DefaultToolbar.sid));
		ids.add(new Long(SwingModuleListView.sid));
		ids.add(new Long(SwingCreateTable.sid));
		ids.add(new Long(SwingTableListView.sid));
		ids.add(new Long(SwingViewListView.sid));
		ids.add(new Long(SwingProcedureListView.sid));
		ids.add(new Long(SwingFunctionListView.sid));
		ids.add(new Long(FavoriteConnector.sid));
		
		return ids;
	}
	
	/**
	 * <p>자바로 구현된 기본 내장 모듈들을 반환합니다.</p>
	 * 
	 * @param manager : 매니저 객체
	 * @return 내장 모듈들
	 */
	public static List<Module> defaultJavaModules(Manager manager)
	{
		List<Module> modules = new Vector<Module>();
		if(manager instanceof GUIManager)
		{
			modules.add(new ArgumentInjector((GUIManager) manager));
			modules.add(new CreateModuleTool((GUIManager) manager));
			modules.add(new DefaultToolbar((GUIManager) manager));
			modules.add(new FavoriteConnector((GUIManager) manager));
		}
		return modules;
	}
	
	/**
	 * <p>클래스 파일로 컴파일된 모듈과 분석 함수를 불러옵니다.</p>
	 * 
	 * @param manager : 매니저 객체
	 * @return 모듈 리스트
	 */
	public static List<AbstractModule> loadDefaultClassModules(Manager manager)
	{
		List<AbstractModule> modules = new Vector<AbstractModule>();
		modules.addAll(defaultJavaModules(manager));
		
		URL[] classLoaderUrl = null;
		ClassLoader classModuleLoader = null;
		
		for(ClassNameWithPath s : defaultClassModules())
		{
			try
			{
				Object loads = null;
				Module newModule = null;
				
				classLoaderUrl = new URL[1];
				if(s.getPath().startsWith("FILE://") || s.getPath().startsWith("file://") || s.getPath().startsWith("File://"))
				{
					classLoaderUrl[0] = new File(s.getPath().substring(new String("FILE://").length())).toURI().toURL();
				}
				else if(s.getPath().startsWith("URL://") || s.getPath().startsWith("url://") || s.getPath().startsWith("Url://"))
				{
					classLoaderUrl[0] = new URL(s.getPath().substring(new String("URL://").length()));
				}
				else if(s.getPath().startsWith("CURRENT://") || s.getPath().startsWith("current://") || s.getPath().startsWith("Current://"))
				{
					classLoaderUrl[0] = new File(ClassTool.currentDirectory() + s.getPath().substring(new String("CURRENT://").length())).toURI().toURL();
				}
				else
				{
					classLoaderUrl[0] = new File(s.getPath()).toURI().toURL();
				}
				
				classModuleLoader = new URLClassLoader(classLoaderUrl, ClassLoader.getSystemClassLoader());
				loads = classModuleLoader.loadClass(s.getName()).newInstance();
				
				if(loads instanceof Module)
				{
					newModule = (Module) loads;
					newModule.setManager(manager);
					modules.add(newModule);
				}
				else if(loads instanceof AnalyzeFunction)
				{
					AnalyzeUtil.add((AnalyzeFunction) loads);
				}
			}
			catch(Throwable e)
			{
				manager.logError(e, Manager.applyStringTable("On loading default module" + " : " + s), true);
			}
		}
		
		modules = addLoadedAllModules(modules);
		
		return modules;
	}
	
	/**
	 * <p>현재 JVM에 불러온 모든 모듈 클래스들을 탐색해 리스트에 추가하지 않은 모듈들을 추가합니다.</p>
	 * 
	 * @param alreadyLoaded : 이미 불러온 모듈 리스트
	 * @return 모듈 리스트 (매개변수로 삽입된 리스트에 탐색한 모듈들을 추가해 그대로 반환)
	 */
	protected static List<AbstractModule> addLoadedAllModules(List<AbstractModule> alreadyLoaded)
	{
		List<File> modulePathes = ClassTool.modulePathes();
		URL[] urls = new URL[modulePathes.size()];
		for(int i=0; i<urls.length; i++)
		{
			try
			{
				urls[i] = modulePathes.get(i).toURI().toURL();
			}
			catch (MalformedURLException e)
			{
				
			}
		}
		
		List<AbstractModule> allModules = new Vector<AbstractModule>();
		
		try
		{
			Set<Class<? extends Object>> externalModulePackagedClasses = ClassTool.getClassesOn("externalModules");
			for(Class<? extends Object> mayBeModule : externalModulePackagedClasses)
			{
				try
				{
					Object tryTo = mayBeModule.newInstance();
					if(tryTo instanceof AbstractModule) allModules.add((AbstractModule) tryTo);
				}
				catch(Throwable t)
				{
					t.printStackTrace();
				}
			}
		}
		catch(NoClassDefFoundError e)
		{
			System.out.println(Manager.applyStringTable("Cannot use class autoloader because this feature needs Google Guava library.\n"
					+ "If you want to use it, download this library jar file into the lib directory."));
		}
		catch(Throwable t)
		{
			t.printStackTrace();
		}
		
		ClassLoader loader = new DynamicLoader(urls);
		ServiceLoader<AbstractModule> allModuleses = ServiceLoader.load(AbstractModule.class, loader);
		
		for(AbstractModule m : allModuleses)
		{
			for(AbstractModule m2 : allModules)
			{
				if(m2.getModuleId() == m.getModuleId()) continue;
			}
			allModules.add(m);
		}
		
		for(AbstractModule m : allModules)
		{
			boolean exists = false;
			for(int i=0; i<alreadyLoaded.size(); i++)
			{
				if(alreadyLoaded.get(i).getClass().getName().equals(m.getClass().getName()))
				{
					exists = true;
					break;
				}
			}
			if(! exists)
			{
				alreadyLoaded.add(m);
			}
		}
		
		return alreadyLoaded;
	}
	
	/**
	 * <p>모듈의 인증 여부를 검사합니다.</p>
	 * 
	 * @param module : 대상 모듈
	 * @return 모듈 인증 여부
	 */
	public static boolean checkAuthorize(AbstractModule module)
	{
		for(String s : defaultClassModuleClassNames())
		{
			if(module.getClass().getName().equals(s))
			{
				return true;
			}
		}
		
		if(isDefaultModule(module))return true;
		
		for(int i=0; i<authorizedModuleIds.size(); i++)
		{
			if(authorizedModuleIds.get(i).longValue() == module.getModuleId())
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * <p>해당 모듈 사용을 사용자가 동의했는지 여부를 반환합니다.</p>
	 * 
	 * @param module : 해당 모듈
	 * @return 동의 여부
	 */
	public static boolean checkAccepted(AbstractModule module)
	{
		String options = Manager.getOption("accepted_modules");
		if(DataUtil.isEmpty(options)) options = "";
		StringTokenizer semicolonTokenizer = new StringTokenizer(options, ";");
		while(semicolonTokenizer.hasMoreTokens())
		{
			String tokens = semicolonTokenizer.nextToken().trim();
			if(tokens.equals(String.valueOf(module.getModuleId())))
			{
				return true;
			}
		}
		
		if(isDefaultModule(module)) return true;
		
		return false;
	}
	
	
	/**
	 * <p>해당 모듈이 기본 제공 모듈인지 여부를 반환합니다.</p>
	 * 
	 * @param module : 대상 모듈 객체
	 * @return 기본 모듈 여부
	 */
	public static boolean isDefaultModule(AbstractModule module)
	{
		for(Long l : defaultJavaModulesIds())
		{
			if(module.getModuleId() == l.longValue()) return true;
		}
		
		for(String s : defaultClassModuleClassNames())
		{
			if(module.getClass().getName().equals(s)) return true;
		}
		
		return false;
	}
}
class ClassNameWithPath implements Serializable
{
	private static final long serialVersionUID = 3622295607491414728L;
	private String name;
	private String path;
	private List<String> additionalOptions = new Vector<String>();
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public String getPath()
	{
		return path;
	}
	public void setPath(String path)
	{
		this.path = path;
	}
	public void add(String element)
	{
		additionalOptions.add(element);
	}
	public String get(int index)
	{
		return additionalOptions.get(index);
	}
	public List<String> getAdditionalOptions()
	{
		return additionalOptions;
	}
	public void setAdditionalOptions(List<String> additionalOptions)
	{
		this.additionalOptions = additionalOptions;
	}
	public int size()
	{
		return additionalOptions.size();
	}
	public String toString()
	{
		return "[" + getName() + " in " + getPath() + "] " + additionalOptions;
	}
}