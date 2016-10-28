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

package hjow.hgtable;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.script.ScriptException;

import hjow.hgtable.classload.ClassTool;
import hjow.hgtable.dao.Dao;
import hjow.hgtable.dao.JdbcDao;
import hjow.hgtable.favorites.AbstractFavorites;
import hjow.hgtable.jscript.JScriptMode;
import hjow.hgtable.jscript.JScriptObject;
import hjow.hgtable.jscript.JScriptRunner;
import hjow.hgtable.jscript.module.AbstractConsoleModule;
import hjow.hgtable.jscript.module.AbstractModule;
import hjow.hgtable.jscript.module.Module;
import hjow.hgtable.streamchain.ChainInputStream;
import hjow.hgtable.streamchain.ChainOutputStream;
import hjow.hgtable.stringtable.DefaultStringTable;
import hjow.hgtable.stringtable.StringTable;
import hjow.hgtable.tableset.Record;
import hjow.hgtable.tableset.ResultStruct;
import hjow.hgtable.tableset.TableSet;
import hjow.hgtable.ui.AccessInfo;
import hjow.hgtable.ui.NeedtoEnd;
import hjow.hgtable.util.AnalyzeUtil;
import hjow.hgtable.util.ConnectUtil;
import hjow.hgtable.util.ConsoleUtil;
import hjow.hgtable.util.DataSourceToolUtil;
import hjow.hgtable.util.DataUtil;
import hjow.hgtable.util.FavoriteUtil;
import hjow.hgtable.util.GUIUtil;
import hjow.hgtable.util.InvalidInputException;
import hjow.hgtable.util.LicenseUtil;
import hjow.hgtable.util.ModuleUtil;
import hjow.hgtable.util.OptionUtil;
import hjow.hgtable.util.ScriptUtil;
import hjow.hgtable.util.SecurityUtil;
import hjow.hgtable.util.StreamUtil;
import hjow.hgtable.util.StringTableUtil;
import hjow.hgtable.util.XLSXUtil;
import hjow.hgtable.util.debug.DebuggingUtilCollection;
import hjow.state.FirstStateView;

/**
 * <p>사용자와의 의사 소통을 담당하는 클래스입니다.</p>
 * 
 * @author HJOW
 *
 */
public abstract class Manager extends HasManyServices implements AbstractManager
{
	private static final long serialVersionUID = -5711105810896660938L;
	protected transient List<Dao> daos = new Vector<Dao>();                        // DB에 액세스하는 DAO 객체들
	protected static StringTable stringTable = new DefaultStringTable();           // 프로그램의 언어 설정
	protected static Map<String, String> params = new Hashtable<String, String>(); // 환경 설정
	protected transient int selectedDao = 0;                                       // 선택된 DAO
	protected transient boolean threadSwitch = true;                               // 별도의 쓰레드를 사용하는 Manager 클래스를 위한 변수
	protected transient boolean runSwitch = true;                                  // 콘솔 기반 Manager 클래스를 위한 변수
	protected transient JScriptRunner scriptRunner = null;                         // JavaScript 형식의 명령문 실행기
	
	protected transient TableSet temporary;
	protected transient boolean detectTempChanged = true;
	
	public transient List<NeedtoEnd> needToEnds = new Vector<NeedtoEnd>();
	public transient List<AbstractModule> modules = new Vector<AbstractModule>();
	protected transient Map<Long, List<Throwable>> errorCollection = new Hashtable<Long, List<Throwable>>();
	public transient List<AbstractFavorites> favorites = new Vector<AbstractFavorites>();
	
	protected FileOutputStream logFileStream = null;
	protected ChainOutputStream logChainStream = null;
	protected OutputStreamWriter logWriter = null;
	protected BufferedWriter logBuffer = null;
		
	public static final int LOG_NOTICE = 1;
	public static final int LOG_DEBUG  = 2;
	public static final int LOG_WARN   = 3;
	public static final int LOG_ERROR  = 4;
	
	protected transient int connectMenuNumber = 1;
	protected transient int queryMenuNumber = 2;
	protected transient int seeFileNumber = 3;
	protected transient int dbToFileNumber = 4;
	protected transient int fileToDbNumber = 5;
	protected transient int preferenceNumber = 6;
	protected transient int jscriptNumber = 7;
	protected transient int moduleNumber = 8;
	protected transient int exitNumber = 9;
	
	protected transient int beforePercentValue = -1;
			
	/**
	 * <p>기본 생성자입니다.</p>
	 */
	public Manager()
	{
		addDao(new JdbcDao(this));
	}
	
	/**
	 * <p>프로그램이 실행되면 처음 호출되는 메소드입니다.</p>
	 * <p>하위 클래스에는 이 메소드가 재정의되어 있으나, super 키워드를 사용하여 최상위 Manager 클래스의 메소드 내용이 먼저 실행되어야 합니다.</p>
	 * 
	 * @param args : 매개 변수
	 */
	@Override
	public void manage(Map<String, String> args)
	{
		DebuggingUtilCollection.init();
		
		FirstStateView.text("Initializing Script Engines");
		
		try
		{
			ScriptUtil.init(args, this);
			scriptRunner = ScriptUtil.newScriptRunner(this, args);
		}
		catch(Throwable e)
		{
			logError(e, applyStringTable("On initializing script engine"));
		}
		
		FirstStateView.set(1);
		FirstStateView.text("Initializing licenses");
		
		String licenseUtilData = LicenseUtil.authorizeThis();
		if(DataUtil.isNotEmpty(LicenseUtil.edition())) licenseUtilData = licenseUtilData + LicenseUtil.edition();
		if(! SecurityUtil.hash(licenseUtilData.trim(), "SHA-512").equals("b8648e037f78cde68986ef49c77ec4a64888f61ee068ef9f136de104165901841f0afbf29b9e36764d2fde5dd3431594d6113298c9484e53b6bda77997ae488d"))
		{
			try { close(); } catch(Exception e) { }
			log(applyStringTable("Authorizing failed"), LOG_DEBUG);
			Main.exitAllProcess();
		}
		
		defaultParam();
		
		FirstStateView.set(2);
		FirstStateView.text("Initializing string tables");
		
		log(applyStringTable("Loading string table") + "...", LOG_DEBUG);
		loadStringTable();
		
		FirstStateView.set(12);
		FirstStateView.text("Loading configs and options");
		
		log(applyStringTable("Loading config") + "...", LOG_DEBUG);
		loadConfig();
		
		if(DataUtil.isNotEmpty(getOption("use_state_view"))
				&& (! DataUtil.parseBoolean(getOption("use_state_view")))) FirstStateView.off();
		
		FirstStateView.set(22);
		FirstStateView.text("Loading default classes");
		
		log(applyStringTable("Loading default classes") + "...", LOG_DEBUG);
		loadDefaultClasses();
		
		FirstStateView.set(32);
		FirstStateView.text("Loading options from arguments");
		
		log(applyStringTable("Loading options from arguments") + "...", LOG_DEBUG);
		getParamOnArgs(args);
		
		FirstStateView.text("Applying some options");
		
		log(applyStringTable("Applying some options") + "...", LOG_DEBUG);
		applySomeParams();
		
		FirstStateView.text("Loading favorites");
		
		log(applyStringTable("Loading favorites") + "...", LOG_DEBUG);
		loadFavorites();
		
		FirstStateView.set(40);
		FirstStateView.text("Initializing manager object from license utilities");
		
		LicenseUtil.additionalInitializingManager(this);
		
		FirstStateView.set(41);
		FirstStateView.text("Preparing data source tool samples");
		
		DataSourceToolUtil.prepareToolList();
		
		FirstStateView.set(43);
		FirstStateView.text("Initializing analyze functions");
		
		AnalyzeUtil.init();
		
		FirstStateView.set(44);
		FirstStateView.text("Applying auto-login option if exists");
		
		log(applyStringTable("Checking auto login option") + "...", LOG_DEBUG);
		autoLogin();
		
		FirstStateView.set(45);
		FirstStateView.text("Applying reserved actions");
		
		log(applyStringTable("Checking reserved actions") + "...", LOG_DEBUG);
		autoAction();
		
		FirstStateView.set(50);
		FirstStateView.text("Core is initialized");
		log(applyStringTable("Initializing complete"), LOG_NOTICE);
	}	

	/**
	 * <p>자주 사용하는 스크립트 목록을 불러옵니다.</p>
	 */
	protected void loadFavorites()
	{
		favorites.addAll(FavoriteUtil.readFavorites(this));
	}

	/**
	 * <p>언어 설정(스트링 테이블)을 파일로부터 불러옵니다.</p>
	 */
	protected void loadStringTable()
	{
		try
		{
			stringTable.putAll(DefaultStringTable.getFromFile());
		}
		catch(Throwable e)
		{
			logError(e, applyStringTable("On loading string table on file"), true);
		}
	}

	/**
	 * <p>환경 설정에서 자동 실행 옵션이 있는지 검사하여 분기합니다.</p>
	 */
	protected void autoAction()
	{
		String scriptFileModeOption = getOption("script_file_mode");
		String scriptFileOption = getOption("script_file");
		if(scriptFileModeOption != null)
		{
			try
			{
				if(scriptFileOption != null && DataUtil.parseBoolean(scriptFileModeOption))
				{
					try
					{
						if(scriptFileOption.startsWith("file://") || scriptFileOption.startsWith("FILE://") || scriptFileOption.startsWith("File://"))
						{
							scriptRunner.execute(scriptFileOption);
						}
						else scriptRunner.execute("file://" + scriptFileOption);
					}
					catch (Throwable e)
					{
						logError(e, "On script file mode");
					}
				}
			}
			catch (InvalidInputException e)
			{
				
			}
		}
		
		String scriptModeOption = getOption("script_mode");
		if(scriptModeOption != null)
		{
			try
			{
				if(DataUtil.parseBoolean(scriptModeOption))
				{
					JScriptMode.manage(this, scriptRunner);
					Main.processShutdown();
				}
			}
			catch (InvalidInputException e)
			{
				
			}
		}
		
		String afterExitOption = getOption("after_exit");
		if(afterExitOption != null)
		{
			try
			{
				if(DataUtil.parseBoolean(afterExitOption))
				{
					close();
					Main.processShutdown();
				}
			}
			catch (InvalidInputException e)
			{
				
			}
		}
	}
	
	/**
	 * <p>기본으로 불러와야 할 클래스들을 불러옵니다. 동적으로 불러와야 할 라이브러리의 경우에만 이 메소드를 통해 불러옵니다. 즉, 대개 이 메소드는 아무 역할도 하지 않습니다.</p>
	 * 
	 */
	protected void loadDefaultClasses()
	{
		
	}

	/**
	 * <p>환경 설정을 불러옵니다. 파일이 존재하지 않으면 적용하지 않습니다.</p>
	 * <p>기본 파일은 defaultParam() 메소드에서 지정한 config_path 환경설정값에 해당하는 경로에 있는 config.cfg 파일입니다. 텍스트 파일입니다.</p>
	 * <p>파일은 UTF-8 형식의 텍스트 파일이어야 합니다.</p>
	 * 
	 * <p>환경 설정이 있으면 자동 DB 접속 등을 설정할 수 있습니다. 아래와 같은 형태를 가집니다.</p>
	 * <p>설정대상=설정값</p>
	 * <p>이러한 내용들이 여러 줄로 기입될 수 있습니다. # 으로 시작하는 줄은 무시됩니다.</p>
	 */
	protected void loadConfig()
	{
		File file = new File(params.get("config_path") + "config.cfg");
		
		FileInputStream fileStream = null;
		ChainInputStream chainStream = null;
		InputStreamReader reader = null;
		BufferedReader bufferedReader = null;
		
		if(! file.exists())
		{
			Main.atFirst = true;
			return;
		}
		else Main.atFirst = false;
		
		try
		{
			fileStream = new FileInputStream(file);
			chainStream = new ChainInputStream(fileStream);
			StreamUtil.additionalSetting(chainStream);
			reader = new InputStreamReader(chainStream.getInputStream(), "UTF-8");
			bufferedReader = new BufferedReader(reader);
			
			String readLines;
			String[] contents;
			while(true)
			{
				readLines = bufferedReader.readLine();
				if(readLines == null) break;
				
				readLines = readLines.trim();
				if(readLines.equals("")) continue;
				if(readLines.startsWith("#")) continue;
				
				contents = readLines.split("=");
				if(contents.length >= 2)
				{
					params.put(contents[0], contents[1]);
				}
				else if(contents.length >= 1)
				{
					params.put(contents[0], "true");
				}
				
				if(! (Main.checkInterrupt(this, "loadConfig")))
				{
					break;
				}
			}
			
			try
			{
				bufferedReader.close();
			}
			catch(Throwable e)
			{
				
			}
			try
			{
				reader.close();
			}
			catch(Throwable e)
			{
				
			}
			try
			{
				chainStream.close();
			}
			catch(Throwable e)
			{
				
			}
			try
			{
				fileStream.close();
			}
			catch(Throwable e)
			{
				
			}
		}
		catch(Throwable e)
		{
			logError(e, stringTable.get("On loading config"));
		}
		finally
		{
			try
			{
				bufferedReader.close();
			}
			catch(Throwable e)
			{
				
			}
			try
			{
				chainStream.close();
			}
			catch(Throwable e)
			{
				
			}
			try
			{
				fileStream.close();
			}
			catch(Throwable e)
			{
				
			}
			
			reInsertDefaultParams();
		}
	}
	
	/**
	 * <p>환경설정을 파일로 저장합니다. 경로는 config_path 경로에 config.cfg 이름으로 UTF-8 텍스트 형식으로 저장됩니다.</p>
	 */
	public void saveConfig()
	{
		File file = new File(params.get("config_path") + "config.cfg");
		FileOutputStream fileStream = null;
		ChainOutputStream chainStream = null;
		OutputStreamWriter writer = null;
		BufferedWriter bufferedWriter = null;
		
		Set<String> keyParam = params.keySet();
		
		File dirs = new File(params.get("config_path"));
		try
		{
			if(! dirs.exists())
			{
				dirs.mkdir();
			}
		}
		catch(Throwable e)
		{
			logError(e, applyStringTable("On creating directory") + " : " + dirs, true);
		}
		
		try
		{
			fileStream = new FileOutputStream(file);
			chainStream = new ChainOutputStream(fileStream);
			StreamUtil.additionalSetting(chainStream);
			
			writer = new OutputStreamWriter(chainStream.getOutputStream(), "UTF-8");
			bufferedWriter = new BufferedWriter(writer);
			
			for(String s : keyParam)
			{
				bufferedWriter.write(s + "=" + params.get(s) + "\n");
				bufferedWriter.newLine();
			}
			
			try
			{
				bufferedWriter.close();
			}
			catch(Throwable e)
			{
				
			}
			try
			{
				writer.close();
			}
			catch(Throwable e)
			{
				
			}
			try
			{
				chainStream.close();
			}
			catch(Throwable e)
			{
				
			}
			try
			{
				fileStream.close();
			}
			catch(Throwable e)
			{
				
			}
		}
		catch(Throwable e)
		{
			logError(e, stringTable.get("On saving config"));
		}
		finally
		{
			try
			{
				bufferedWriter.close();
			}
			catch(Throwable e)
			{
				
			}
			try
			{
				writer.close();
			}
			catch(Throwable e)
			{
				
			}
			try
			{
				chainStream.close();
			}
			catch(Throwable e)
			{
				
			}
			try
			{
				fileStream.close();
			}
			catch(Throwable e)
			{
				
			}
		}
		
	}
	
	/**
	 * <p>몇몇 환경설정은 별도의 작업이 필요합니다. 그 작업을 하는 메소드입니다. 프로그램 실행 시 1회만 호출됩니다.</p>
	 */
	private void applySomeParams()
	{
		try
		{
			String seeStringTableDebugOption = getOption("see_stringtable_debug");
			boolean seeStringTableDebug = DataUtil.parseBoolean(seeStringTableDebugOption);
			
			stringTable.setCollectNoInString(seeStringTableDebug);
		}
		catch(Throwable e)
		{
			
		}
		try
		{
			if(getOption("save_log_file") != null)
			{
				File target = new File(getOption(getOption("save_log_file")));
				logFileStream = new FileOutputStream(target);
				logChainStream = new ChainOutputStream(logFileStream);
				StreamUtil.additionalSetting(logChainStream);
				
				String charsets = "UTF-8";
				if(getOption("file_charset") != null) charsets = getOption("file_charset");
				
				logWriter = new OutputStreamWriter(logChainStream.getOutputStream(), charsets);
				logBuffer = new BufferedWriter(logWriter);
			}
		}
		catch(Throwable e)
		{
			
		}
		
		// 폰트 다시 불러오기
		if(DataUtil.parseBoolean(getOption("reload_fonts"))) GUIUtil.prepareFont(params);
	}
	
	/**
	 * <p>기본 환경설정값을 적용합니다.</p>
	 */
	protected void defaultParam()
	{
		try
		{
			OptionUtil.setDefaultOptions(params, false);
			
			StringTableUtil.defaultData(stringTable);
		}
		catch(Throwable e)
		{
			
		}
	}
	
	/**
	 * <p>변하면 안 되는 환경 설정들을 다시 삽입합니다.</p>
	 * 
	 */
	public void reInsertDefaultParams()
	{
		try
		{
			OptionUtil.setDefaultOptions(params, true);
		}
		catch(Exception e)
		{
			
		}
	}
	/**
	 *  <p>DB 자동 접속 환경설정이 있는지 확인하고, 있으면 접속을 시도합니다.</p>
	 */
	protected void autoLogin()
	{
		String checkConnectOption = params.get("jdbcAutoLogin");
		if(checkConnectOption != null)
		{
			try
			{
				if(DataUtil.parseBoolean(checkConnectOption)) // 여기까지 통과하면, DB 자동 접속 환경설정이 켜져 있는 것입니다.
				{
					if(params.get("jdbcDriver") != null) // JDBC 드라이버 클래스명을 지정합니다.
					{
						getDao().setClassPath(params.get("jdbcDriver"));
					}
					if(params.get("jdbcDriverFile") != null) // 별도의 jar 파일 위치가 지정된 경우, 해당 위치를 입력하여 접속 시 불러오도록 합니다.
					{
						getDao().setClassFile(params.get("jdbcDriverFile"));
					}
					
					// 접속을 시도합니다.
					getDao().connect(params.get("jdbcId"), params.get("jdbcPw"), params.get("jdbcUrl"));
				}
			}
			catch(Throwable e)
			{
				logError(e, applyStringTable("On autologin on DB"));
			}
		}
	}
	/**
	 * <p>사용자가 입력한 매개 변수를 환경 설정에 적용합니다.</p>
	 * 
	 * @param args : 매개 변수들
	 */
	protected void getParamOnArgs(Map<String, String> args)
	{
		params.putAll(args);
	}
	
	/**
	 * <p>이 메소드는 매니저 객체로부터 호출됩니다. 호출된 사유는 additionalData 에 whyCalled 라는 원소에 텍스트로 삽입됩니다.</p>
	 * 
	 * @param additionalData : 매니저 객체로부터 받은 정보들
	 */
	@Override
	public void refresh(Map<String, Object> additionalData)
	{
		if(scriptRunner != null)
		{
			scriptRunner.put("dao", getDao());
			AbstractModule module = null;
			List<Throwable> errorsOnModule = null;
			for(int i=0; i<modules.size(); i++)
			{
				module = modules.get(i);
				errorsOnModule = errorCollection.get(new Long(module.getModuleId()));
				int limits = DataUtil.parseIntWithoutError(getOption("module_error_limit"), 5);
				if(errorsOnModule != null && errorsOnModule.size() >= limits && limits >= 0) continue;
				String name = applyStringTable("unknown");
				try
				{
					name = module.getName() + "(" + module.getModuleId() + ")";
					if(ModuleUtil.checkAccepted(module))
					{
						module.refresh(additionalData);
					}
				}
				catch(Throwable e)
				{
					logError(e, applyStringTable("On refreshing") + " " + name, true);
					if(errorsOnModule == null)
					{
						errorCollection.put(new Long(module.getModuleId()), new Vector<Throwable>());
						errorsOnModule = errorCollection.get(new Long(module.getModuleId()));
					}
					errorsOnModule.add(e);
				}
			}
		}
	}
	
	/**
	 * <p>쓰레드가 필요한 매니저에서 이 메소드는 100 밀리초당 1회 실행됩니다. 쓰레드 사용 시 이 메소드를 오버라이드해 사용합니다.</p>
	 */
	protected void onThread()
	{
		
	}
	
	/**
	 * <p>쓰레드가 필요한 매니저에서 필요한 메소드입니다. 실제로 이 메소드를 오버라이드하지는 않고 대신 onThread() 메소드를 오버라이드하도록 합니다.</p>
	 */
	public void run()
	{
		while(threadSwitch)
		{
			onThread();
			try
			{
				
			}
			catch(Throwable e)
			{
				try
				{
					Thread.sleep(100);
				}
				catch (InterruptedException e1)
				{
					
				}
			}
			
			if(! (Main.checkInterrupt(this, "Manager thread")))
			{
				break;
			}
		}
	}
	
	/**
	 * <p>이 프로그램에서 한 DB 접속을 전부 닫습니다.</p>
	 * 
	 */
	public void closeAllConnection()
	{
		for(int i=0; i<daos.size(); i++)
		{
			try
			{
				daos.get(i).close();
			}
			catch(Throwable e)
			{
				
			}
		}
	}
	
	/**
	 * <p>매니저와 연관된 객체들을 모두 닫고 종료를 준비합니다.</p>
	 * 
	 */
	public void close()
	{
		threadSwitch = false;
		closeAllServices();
		
		if(modules != null)
		{
			for(int i=0; i<modules.size(); i++)
			{
				try
				{
					modules.get(i).noMoreUse();
				}
				catch(Exception e)
				{
					
				}
			}
		}
		
		AnalyzeUtil.noMoreUse();
		
		try
		{
			scriptRunner.close();
		}
		catch(Exception e)
		{
			
		}
		
		ScriptUtil.noMoreUse();
		
		closeAllConnection();
		
		for(int i=0; i<daos.size(); i++)
		{
			try
			{
				daos.get(i).noMoreUse();
			}
			catch(Exception e)
			{
				
			}
		}
		
		if(needToEnds != null)
		{
			for(int i=0; i<needToEnds.size(); i++)
			{
				try
				{
					needToEnds.get(i).noMoreUse();
				}
				catch(Throwable e)
				{
					
				}
			}
		}
		
		try
		{
			logBuffer.close();
		}
		catch(Throwable e)
		{
			
		}
		try
		{
			logWriter.close();
		}
		catch(Throwable e)
		{
			
		}
		try
		{
			logChainStream.close();
		}
		catch(Throwable e)
		{
			
		}
		try
		{
			logFileStream.close();
		}
		catch(Throwable e)
		{
			
		}
		
		try
		{
			// save_config_after_close
			String saveConfigOption = getOption("save_config_after_close");
			boolean saveConfig = false;
			if(saveConfigOption != null)
			{
				saveConfig = DataUtil.parseBoolean(saveConfigOption);
			}
			
			if(saveConfig)
			{
				saveConfig();
			}
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
		
		try
		{
			// save_stringtable_after_close
			String saveStringTableOption = getOption("save_stringtable_after_close");
			boolean saveStringTable = false;
			if(saveStringTableOption != null)
			{
				saveStringTable = DataUtil.parseBoolean(saveStringTableOption);
			}
			if(saveStringTable)
			{
				stringTable.save(new File(getOption("config_path") + getOption("user_language") + ".lang"));
			}
		}
		catch(Throwable e)
		{
			
		}
		
		stringTable.finalize();
		Main.interrupt(true);
	}
	
	/**
	 * <p>로그 출력 (그리고 줄띄움) 메소드입니다. 이 메소드는 다른 형태의 Manager 클래스에서 log(ob) 메소드 대신 오버라이드되어야 합니다.</p>
	 * 
	 * @param ob : 출력할 대상 객체
	 */
	protected abstract void logRaw(Object ob);
	
	/**
	 * <p>로그 출력 메소드입니다. 이 메소드는 다른 형태의 Manager 클래스에서 logNotLn(ob) 메소드 대신 오버라이드되어야 합니다.</p>
	 * 
	 * @param ob : 출력할 대상 객체
	 */
	protected abstract void logRawNotLn(Object ob);
	
	/**
	 * <p>진행 상태를 게이지 바 형태로 알 수 있는 Manager 의 경우 이 메소드를 오버라이드해 사용합니다.</p>
	 * 
	 * @param percents : 현재 진행 상태 (0 ~ 100)
	 */
	public void setPercent(int percents)
	{
		if(percents == 0) beforePercentValue = -1;
		if(percents > beforePercentValue)
		{
			beforePercentValue = percents;
			log(String.valueOf(percents) + "%", LOG_NOTICE);
		}
		if(percents == 100) beforePercentValue = -1;
	}
	/**
	 * <p>로그를 출력하고 줄을 띄웁니다.</p>
	 * 
	 * @param ob : 출력할 객체
	 */
	@Override
	public void log(Object ob)
	{
		log(ob, LOG_NOTICE);
	}
	
	/**
	 * <p>로그를 출력합니다.</p>
	 * 
	 * @param ob : 출력할 객체
	 */
	@Override
	public void logNotLn(Object ob)
	{
		logNotLn(ob, LOG_NOTICE);
	}
	
	/**
	 * <p>콘솔 환경에서는 log(msg) 와 동일하게 동작합니다. GUI 환경에서는 별도의 경고 대화상자를 띄워 메시지를 표시합니다.</p>
	 * 
	 * @param msg : 보일 메시지
	 */
	@Override
	public void alert(Object msg)
	{
		log(msg, LOG_ERROR);
	}
	
	/**
	 * <p>로그를 출력하고 줄을 띄웁니다.</p>
	 * <p>현재 설정한 단계에 따라 출력 여부가 달라집니다.</p>
	 * 
	 * @param ob : 출력할 객체
	 * @param logLevel : 출력할 메시지의 의도에 해당하는 상수값
	 */
	@Override
	public void log(Object ob, int logLevel)
	{
		if(needPrintLog(logLevel))
		{
			if(ob instanceof TableSet) logTable((TableSet) ob);
			else if(ob instanceof ResultStruct) logTable(((ResultStruct) ob).getTableSet());
			else logRaw(ob);
		}
	}
	
	/**
	 * <p>로그를 출력합니다.</p>
	 * <p>현재 설정한 단계에 따라 출력 여부가 달라집니다.</p>
	 * 
	 * @param ob : 출력할 객체
	 * @param logLevel : 출력할 메시지의 의도에 해당하는 상수값
	 */
	@Override
	public void logNotLn(Object ob, int logLevel)
	{	
		if(needPrintLog(logLevel))
		{
			if(ob instanceof TableSet) logTable((TableSet) ob);
			else if(ob instanceof ResultStruct) logTable(((ResultStruct) ob).getTableSet());
			else logRawNotLn(ob);
		}
	}
	
	/**
	 * <p>메모리 현황을 로그로 출력합니다.</p>
	 * 
	 */
	public void logMemory()
	{
		log(ConsoleUtil.memoryData(true, false));
	}
	
	/**
	 * <p>상세한 메모리 현황을 로그로 출력합니다.</p>
	 * 
	 */
	public void logMemoryDetails()
	{
		log(ConsoleUtil.memoryData(true, true));
	}
	
	/**
	 * <p>환경 설정을 검사하여 현재 어떠한 로그가 출력해도 되는 설정인지를 확인합니다.</p>
	 * 
	 * @param logLevel : 로그 의도 상수값
	 * @return 로그 출력 여부
	 */
	private boolean needPrintLog(int logLevel)
	{
		String noticeOptionCheck = params.get("show_notice");
		String debugOptionCheck = params.get("show_debug");
		String warnOptionCheck = params.get("show_warn");
		String errorOptionCheck = params.get("show_error");
		
		boolean show = true;
		
		switch(logLevel)
		{
		case LOG_NOTICE:
			if(noticeOptionCheck == null) show = true;
			else
			{
				try
				{
					show = DataUtil.parseBoolean(noticeOptionCheck);
				}
				catch(Throwable e)
				{
					show = true;
				}
			}
			break;
		case LOG_DEBUG:
			if(debugOptionCheck == null) show = true;
			else
			{
				try
				{
					show = DataUtil.parseBoolean(debugOptionCheck);
				}
				catch(Throwable e)
				{
					show = true;
				}
			}
			break;
		case LOG_WARN:
			if(warnOptionCheck == null) show = true;
			else
			{
				try
				{
					show = DataUtil.parseBoolean(warnOptionCheck);
				}
				catch(Throwable e)
				{
					show = true;
				}
			}
			break;
		case LOG_ERROR:
			if(errorOptionCheck == null) show = true;
			else
			{
				try
				{
					show = DataUtil.parseBoolean(errorOptionCheck);
				}
				catch(Throwable e)
				{
					show = true;
				}
			}
			break;
		 default:
			 show = true;
		}
		return show;
	}
	
	/**
	 * <p>직선을 출력합니다.</p>
	 */
	@Override
	public void logDrawBar()
	{
		log("--------------------------------------------------");
	}
	
	/**
	 * <p>TableSet 객체를 로그로 출력합니다.</p>
	 * 
	 * @param table : TableSet 객체
	 */
	@Override
	public void logTable(TableSet table)
	{
		logTable(table, params.get("default_column_delimiter"));
	}
	
	/**
	 * <p>TableSet 객체를 로그로 출력합니다.</p>
	 * 
	 * @param table : TableSet 객체
	 * @param spaces : 각 컬럼 사이의 공백 혹은 구분자
	 */
	public void logTable(TableSet table, String spaces)
	{
		TableSet resultTable = table;
		
		if(resultTable == null) log(applyStringTable("There is no table"));
		
		List<String> spaceDelims = new Vector<String>();
		String standardSpaces = spaces;
		StringBuffer temps;
		
		boolean useAutoTab = false;
		List<Integer> maxLengths = new Vector<Integer>();
		if(spaces == null || spaces.equalsIgnoreCase("auto"))
		{
			standardSpaces = "\t";
			useAutoTab = true;
			
			for(int i=0; i<resultTable.getColumns().size(); i++)
			{
				maxLengths.add(resultTable.getColumns().get(i).getBiggestDataLength());
			}
			
		}
		else
		{
			for(int i=0; i<resultTable.getColumns().size(); i++)
			{
				spaceDelims.add(spaces);
			}
		}
		
		if(resultTable.getRecordCount() <= 0) log(applyStringTable("There is no data in table"));	
		else
		{			
			int nowLength = -1;
			log("");
			logNotLn(applyStringTable("NO") + standardSpaces);
			for(int i=0; i<resultTable.getColumns().size(); i++)
			{
				logNotLn(resultTable.getColumns().get(i).getName());
				if(i < resultTable.getColumns().size() - 1) 
				{
					if(useAutoTab)
					{
						nowLength = resultTable.getColumns().get(i).getName().length();
						temps = new StringBuffer("");
						/*
						if(i == 2)
						{
							Main.println();
							Main.println("In column part, nowLength : " + nowLength);
							Main.println("In column part, maxValue : " + maxLengths.get(i).intValue());
						}
						*/
						for(int l=nowLength; l<maxLengths.get(i).intValue(); l++)
						{
							temps = temps.append(" ");
						}
						temps = temps.append("\t");
						logNotLn(temps);
					}
					else logNotLn(spaceDelims.get(i));
				}
			}
			log("");
			logDrawBar();
			
			for(int i=0; i<resultTable.getRecordCount(); i++)
			{
				logNotLn(String.valueOf(i) + standardSpaces);
				for(int j=0; j<resultTable.getColumns().size(); j++)
				{
					logNotLn(resultTable.getColumns().get(j).getData().get(i));
					if(j < resultTable.getColumns().size() - 1)
					{
						if(useAutoTab)
						{
							if(resultTable.getColumns().get(j).getData().get(i) != null) nowLength = resultTable.getColumns().get(j).getData().get(i).length();
							else nowLength = 4;
							temps = new StringBuffer("");
							/*
							if(j == 2)
							{
								Main.println();
								Main.println("In row part, nowLength : " + nowLength);
								Main.println("In row part, maxValue : " + maxLengths.get(j).intValue());
								Main.println("In row part, now apply : " + (maxLengths.get(j).intValue() + 3 - nowLength));
							}
							*/
							for(int l=nowLength; l<maxLengths.get(j).intValue(); l++)
							{
								temps = temps.append(" ");
							}
							temps = temps.append("\t");
							logNotLn(temps);
						}
						else logNotLn(spaceDelims.get(j));
					}
				}
				log("");
			}
			log("");
		}
	}
	
	/**
	 * <p>예외 혹은 오류를 출력합니다.</p>
	 * 
	 * @param e : Exception 객체
	 * @param addMsg : 오류 발생 상황을 알 수 있게 해 주는 추가 메시지
	 */
	@Override
	public void logError(Throwable e, String addMsg)
	{
		logError(e, addMsg, LOG_ERROR);
	}
	
	/**
	 * <p>예외 혹은 오류를 출력합니다.</p>
	 * 
	 * @param e : Exception 객체
	 * @param addMsg : 오류 발생 상황을 알 수 있게 해 주는 추가 메시지
	 * @param simplify : true 시 자세한 스택 추적 내용을 출력하지 않습니다.
	 */
	@Override
	public void logError(Throwable e, String addMsg, boolean simplify)
	{
		logError(e, addMsg, simplify, LOG_ERROR);
	}
	
	/**
	 * <p>예외 혹은 오류를 출력합니다.</p>
	 * 
	 * @param e : Exception 객체
	 * @param addMsg : 오류 발생 상황을 알 수 있게 해 주는 추가 메시지
	 * @param warnLevel : 로그의 의도 상수 코드, 예상이 가능하고 처리가 가능한 오류인 경우 설정에 따라 출력 안해도 될 때 사용합니다.
	 */
	public void logError(Throwable e, String addMsg, int warnLevel)
	{
		String errorSimplifyOption = params.get("simplify_error");
		boolean errorSimplify = false;
		if(errorSimplifyOption != null)
		{
			if(errorSimplifyOption.trim().equalsIgnoreCase("true") || errorSimplifyOption.trim().equalsIgnoreCase("yes") || errorSimplifyOption.trim().equalsIgnoreCase("y"))
			{
				errorSimplify = true;
			}
		}
		
		logError(e, addMsg, errorSimplify, warnLevel);
	}
	
	/**
	 * <p>예외 혹은 오류를 출력합니다.</p>
	 * 
	 * @param e : Exception 객체
	 * @param addMsg : 오류 발생 상황을 알 수 있게 해 주는 추가 메시지
	 * @param simplify : true 시 자세한 스택 추적 내용을 출력하지 않습니다.
	 * @param warnLevel : 로그의 의도 상수 코드, 예상이 가능하고 처리가 가능한 오류인 경우 설정에 따라 출력 안해도 될 때 사용합니다.
	 */
	public void logError(Throwable e, String addMsg, boolean simplify, int warnLevel)
	{
		String errorLogPrintOption = params.get("show_error");
		boolean showError = true;
		
		if(errorLogPrintOption == null) showError = true;
		else if(errorLogPrintOption.trim().equalsIgnoreCase("true") || errorLogPrintOption.trim().equalsIgnoreCase("yes") || errorLogPrintOption.trim().equalsIgnoreCase("y"))
		{
			showError = true;
		}
		else showError = false;
		
		if(! showError) return;
		
		if(simplify)
		{
			// e.printStackTrace();
			logNotLn("\n" + applyStringTable("ERROR") + " : " + applyStringTable(addMsg) + " --> " + e.getClass().getName() + " : " + applyStringTable(e.getMessage()));
			
			String stackInfo = "";
			
			StackTraceElement[] elements = e.getStackTrace();
			if(elements != null && elements.length >= 1)
			{
				stackInfo = elements[0].toString();
			}
			
			log(" " + applyStringTable("from") + " " + stackInfo);
		}
		else
		{
			log(DataUtil.stackTrace(e));
			log("\n" + addMsg);
		}
	}
	
	/**
	 * <p>사용자에게 y 혹은 n 입력을 받습니다. 결과값은 y의 경우 true, n의 경우 false 로 반환합니다.</p>
	 * 
	 * @param msg : 입력 받을 때 보일 메시지
	 * @return 사용자의 입력 결과
	 */
	@Override
	public boolean askYes(String msg)
	{
		String results = askInput(msg + "(y/n)", true).trim();
		try
		{
			return DataUtil.parseBoolean(results);
		}
		catch (InvalidInputException e)
		{
			return askYes(msg);
		}		
	}
	
	@Override
	public byte[] askFile(String msg) throws IOException
	{
		String reads = askInput(msg, true);
		if(DataUtil.isEmpty(reads)) return null;
		File file = new File(reads);
		return StreamUtil.readBytes(file);
	}
	
	@Override
	public void askSave(String msg, byte[] bytes) throws IOException
	{
		String reads = askInput(msg, true);
		if(DataUtil.isEmpty(reads)) return;
		File file = new File(reads);
		StreamUtil.saveBytes(file, bytes);
	}
	
	/**
	 * <p>콘솔 모드에서 메뉴 입력에 사용된 메소드입니다.</p>
	 * 
	 * @param msg : 입력 받을 때 보일 메시지
	 * @return 사용자의 입력 결과
	 */
	public String askMenu(String msg)
	{
		return askInput(applyStringTable(msg), true);
	}
	
	/**
	 * <p>사용자에게 문장을 입력받습니다. 사용자가 [end] 를 입력할 때까지 입력받게 됩니다.</p>
	 * 
	 * @param msg : 입력 받을 때 보일 메시지
	 * @return 사용자의 입력 결과
	 */
	@Override
	public String askInput(String msg)
	{
		return askInput(msg, false);
	}	
	/**
	 * <p>SQL 문장을 입력받습니다.</p>
	 * 
	 * @param msg : 입력 받을 때 보일 메시지
	 * @return 사용자의 입력 결과
	 */
	@Override
	public String askQuery(String msg)
	{
		String getQuery = askInput(msg, false);
		getQuery = getQuery.trim();
		
		// log("Asking query...\n" + getQuery + "\n...end");
		
		if(getQuery.startsWith("file://") || getQuery.startsWith("FILE://") || getQuery.startsWith("File://")
				|| getQuery.startsWith("url://") || getQuery.startsWith("URL://") || getQuery.startsWith("Url://"))
		{			
			String filePathString = null;
			boolean isFile = false;
			
			if(getQuery.startsWith("file://") || getQuery.startsWith("FILE://") || getQuery.startsWith("File://")) isFile = true;
			else isFile = false;
			
			File targetFile = null;
			URL targetUrl = null;
			
			if(isFile)
			{
				filePathString = getQuery.substring(7);
				log(applyStringTable("Asking query from file") + " : " + filePathString);
				
				targetFile = new File(filePathString);
			}
			else
			{
				filePathString = getQuery.substring(6);
				log(applyStringTable("Asking query from URL") + " : " + filePathString);
				
				try
				{
					targetUrl = new URL(filePathString);
				}
				catch (Exception e)
				{
					logError(e, applyStringTable("On asking query from URL") + " : " + targetUrl);
					return null;
				}
			}
			
			InputStream stream = null;
			BufferedReader bufferedReader = null;
			InputStreamReader reader = null;
			ChainInputStream chainStream = null;
			
			String defaultCharset = "UTF-8";
			if(params.get("file_charset") != null) defaultCharset = params.get("file_charset");
			
			try
			{
				if(isFile) stream = new FileInputStream(targetFile);
				else stream = targetUrl.openStream();
				
				chainStream = new ChainInputStream(stream);
				StreamUtil.additionalSetting(chainStream);
				
				reader = new InputStreamReader(chainStream.getInputStream(), defaultCharset);
				bufferedReader = new BufferedReader(reader);
				
				StringBuffer reads = new StringBuffer("");
				String readLine = null;
				
				while(true)
				{
					readLine = bufferedReader.readLine();
					if(readLine == null) break;
					
					reads = reads.append(readLine + "\n");
					
					if(! (Main.checkInterrupt(this, "askQuery")))
					{
						break;
					}
				}				
				
				try
				{
					bufferedReader.close();
				}
				catch(Throwable e)
				{
					
				}
				try
				{
					reader.close();
				}
				catch(Throwable e)
				{
					
				}
				try
				{
					chainStream.close();
				}
				catch(Throwable e)
				{
					
				}
				try
				{
					stream.close();
				}
				catch(Throwable e)
				{
					
				}
				
				return reads.toString();
			}
			catch(Throwable e)
			{
				logError(e, applyStringTable("On loading query file") + " : " + getQuery);
				return "";
			}
			finally
			{
				try
				{
					bufferedReader.close();
				}
				catch(Throwable e)
				{
					
				}
				try
				{
					reader.close();
				}
				catch(Throwable e)
				{
					
				}
				try
				{
					chainStream.close();
				}
				catch(Throwable e)
				{
					
				}
				try
				{
					stream.close();
				}
				catch(Throwable e)
				{
					
				}
			}
		}
		else return getQuery;
	}
	
	/**
	 * <p>맵 객체를 입력받습니다.</p>
	 * 
	 * @param msg : 사용자에게 보일 메시지
	 * @return 맵 객체
	 */
	@Override
	public Map<String, String> askMap(String msg)
	{
		return askMap(msg, null);
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
		boolean switches = true;
		Map<String, String> results = new Hashtable<String, String>();
		
		if(befores != null) results.putAll(befores);
		
		while(switches)
		{
			logDrawBar();
			log(msg);
			logDrawBar();
			log(applyStringTable("There are properties in your map."));
			for(String s : results.keySet())
			{
				log(s + " : " + results.get(s));
			}
			if(askYes(applyStringTable("Do you want to change some properties? (y/n)")))
			{
				String gets = askInput(applyStringTable("Input the key you want to change."), true);
				if(DataUtil.isNotEmpty(gets))
				{
					results.put(gets, askInput(applyStringTable("Input new value you want to set of") + " : " + gets));
				}
			}
			else
			{
				if(askYes(applyStringTable("Do you want to submit this properties? (y/n)")))
				{
					break;
				}
			}
		}
		
		return results;
	}
	
	/**
	 * <p>DAO들 목록을 정리합니다. 닫힌 DAO은 확실히 닫고 목록에서 제거합니다.</p>
	 * 
	 */
	public void cleanDaos()
	{
		int i=0;
		while(true)
		{
			if(i >= daos.size()) break;
			
			try
			{
				if(! daos.get(i).isAlive())
				{
					daos.get(i).noMoreUse();
					daos.remove(i);
					i = 0;
				}
			}
			catch(Throwable e)
			{
				logError(e, applyStringTable("On cleaning DAO list"), true);
			}
			
			i++;
			
			if(! (Main.checkInterrupt(this, "cleanDaos")))
			{
				break;
			}
		}
		if(daos.size() <= 0) daos.add(new JdbcDao(this));
	}
	
	/**
	 * <p>선택된 DAO을 반환합니다. 콘솔 모드인 경우 DAO을 하나만 쓰므로 동일한 DAO을 반환하게 됩니다.</p>
	 * 
	 * @return DB 접속 DAO
	 */
	public Dao getDao()
	{
		if(daos.size() <= 0) daos.add(new JdbcDao(this));
		
		if(selectedDao >= daos.size())
		{
			selectedDao = daos.size() - 1;
			Map<String, Object> additionalData = new Hashtable<String, Object>();
			additionalData.put("selected_dao", new Integer(selectedDao));
			refresh(additionalData);
		}
		
		if(selectedDao < 0) return null;
		
		return daos.get(selectedDao);
	}
	
	/**
	 * <p>몇 번째 DAO을 반환합니다.</p>
	 * 
	 * @param index : 번호
	 * @return DB 접속 DAO
	 */
	public Dao getDao(int index)
	{
		return daos.get(index);
	}
	
	/**
	 * <p>접속 정보에 해당하는 DAO을 반환합니다.</p>
	 * 
	 * @param info : 접속 정보
	 * @return DB 접속 DAO
	 */
	public Dao getDao(AccessInfo info)
	{
		for(int i=0; i<daos.size(); i++)
		{
			if(daos.get(i).getAccessInfo().compareTo(info) == 0)
			{
				return daos.get(i);
			}
		}
		return null;
	}
	
	/**
	 * <p>DAO들을 모두 반환합니다.</p>
	 * 
	 * @return DAO 전부
	 */
	public List<Dao> getDaos()
	{
		return daos;
	}
	
	/**
	 * <p>아직 접속되어 있는 DAO들을 모두 반환됩니다.</p>
	 * 
	 * @return 접속되어 있는 DAO 전부
	 */
	public List<Dao> getAliveDaos()
	{
		List<Dao> alives = new Vector<Dao>();
		for(Dao dao : getDaos())
		{
			if(dao.isAlive()) alives.add(dao);
		}
		return alives;
	}
	
	protected void setDaos(List<Dao> dao)
	{
		if(this.daos != null) this.daos.clear();
		else this.daos = new Vector<Dao>();
		if(dao != null) this.daos.addAll(dao);
		if(this.daos.size() <= 0) addDao(new JdbcDao(this));
		if(selectedDao >= this.daos.size()) selectedDao = this.daos.size() - 1;
		
		Map<String, Object> additionalData = new Hashtable<String, Object>();
		additionalData.put("selected_dao", new Integer(selectedDao));
		refresh(additionalData);
	}
	
	public void addDaos(List<Dao> daos)
	{
		this.daos.addAll(daos);
		selectedDao = this.daos.size() - 1;
		
		Map<String, Object> additionalData = new Hashtable<String, Object>();
		additionalData.put("selected_dao", new Integer(selectedDao));
		refresh(additionalData);
	}
	
	public void addDao(Dao dao)
	{
		this.daos.add(dao);
		selectedDao = this.daos.size() - 1;
		
		Map<String, Object> additionalData = new Hashtable<String, Object>();
		additionalData.put("selected_dao", new Integer(selectedDao));
		refresh(additionalData);
	}

	public static StringTable getStringTable()
	{
		return stringTable;
	}

	public static void setStringTable(StringTable stringTable)
	{
		Manager.stringTable = stringTable;
	}

	public static String applyStringTable(String str)
	{
		if(stringTable != null) return stringTable.process(str);
		else return str;
	}
	
	/*
	public Map<String, String> getParams()
	{
		return params;
	}

	public void setParams(Map<String, String> params)
	{
		Manager.params = params;
	}
	*/
	
	/**
	 * <p>환경 설정값을 변경합니다.</p>
	 * 
	 * @param key : 대상 환경 설정 키
	 * @param value : 지정할 환경 설정 값
	 * @param manager : 자기자신 객체 (인증용)
	 */
	public void setOption(String key, String value, Manager manager)
	{
		if(this == manager) params.put(key, value);
	}
	
	/**
	 * <p>환경 설정 값을 반환합니다.</p>
	 * 
	 * @param key : 환경 설정 키
	 * @return 환경 설정 값
	 */
	public static String getOption(String key)
	{
		if(params == null) return null;
		return params.get(key);
	}
	
	/**
	 * <p>현재 적용되어 있는 환경 설정 키들을 반환합니다.</p>
	 * 
	 * @return 환경 설정 키들
	 */
	public static List<String> optionList()
	{
		List<String> optionLists = new Vector<String>();
		optionLists.addAll(params.keySet());
		return optionLists;
	}
	
	/**
	 * <p>콘솔 메뉴를 실행합니다.</p>
	 * 
	 * @param runIndependent 일반 콘솔모드로의 진입 여부, 현재의 메뉴가 종료되면 프로그램을 종료할 지의 여부입니다.
	 */
	public void runMenu(Object runIndependent)
	{
		boolean runIndependency = true;		
		
		if(runIndependent != null) 
		{
			try
			{
				runIndependency = DataUtil.parseBoolean(String.valueOf(runIndependent));
			}
			catch (InvalidInputException e1)
			{
				
			}
		}
		
		runSwitch = true;
		while(runSwitch)
		{
			try
			{
				menu(runIndependency);
			}
			catch (Exception e)
			{
				logError(e, "On menu");
			}
			
			if(! (Main.checkInterrupt(this, "runMenu")))
			{
				break;
			}
			Main.removeInterrupt();
		}
	}
	
	/**
	 * <p>메뉴 실행 내용입니다. runMenu(runIndependency) 를 실행하면 이 메소드가 반복 실행됩니다.</p>
	 * 
	 * @param runIndependency : 일반 콘솔모드로의 진입 여부, 현재의 메뉴가 종료되면 프로그램을 종료할 지의 여부입니다.
	 * @throws Exception : 입출력 과정에서 발생할 수 있는 예외 및 오류
	 */
	protected void menu(boolean runIndependency) throws Exception
	{	
		log("");
		log(params.get("program_title") + " " + params.get("version"));
		
		log("");
		
		logNotLn(applyStringTable("DB Connection") + " : ");
		if(! getDao().isAlive()) log(applyStringTable("not connected"));
		else log(getDao().getUrl() + " " + applyStringTable("as") + " " + getDao().getId());
		
		log("");
		
		log(applyStringTable("Main Menu"));
		
		if(! getDao().isAlive()) logRaw(String.valueOf(connectMenuNumber) + ". " + applyStringTable("Connect"));
		else logRaw(String.valueOf(connectMenuNumber) + ". " + applyStringTable("Disconnect"));
		
		log(String.valueOf(queryMenuNumber) + ". " + applyStringTable("Query"));
		log(String.valueOf(seeFileNumber) + ". " + applyStringTable("See file contents"));
		log(String.valueOf(dbToFileNumber) + ". " + applyStringTable("DB --> file"));
		log(String.valueOf(fileToDbNumber) + ". " + applyStringTable("file --> DB"));
		log(String.valueOf(preferenceNumber) + ". " + applyStringTable("Preference"));
		log(String.valueOf(jscriptNumber) + ". " + applyStringTable("JScript"));
		if(getConsoleModules().size() >= 0) log(String.valueOf(moduleNumber) + ". " + applyStringTable("Use Module"));
		if(runIndependency)
		{
			log(String.valueOf(exitNumber) + ". " + applyStringTable("Exit"));
		}
		else
		{
			log(String.valueOf(exitNumber) + ". " + applyStringTable("Exit menu"));
		}
				
		String gets = askMenu(applyStringTable("What do you want to do?"));
		
		gets = gets.trim();
		if(gets.equals("")) return;
		int menuSelection = 0;
		
		try // 입력된 값이 정수 숫자인 경우
		{
			menuSelection = Integer.parseInt(gets);
			
			if(menuSelection == connectMenuNumber) // Connect
			{
				menuConnect();
			}
			else if(menuSelection == queryMenuNumber) // Query
			{
				menuQuery(null);			
			}
			else if(menuSelection == seeFileNumber) // See xlsx contents
			{
				menuSeeFile();			
			}
			else if(menuSelection == dbToFileNumber) // DB --> xlsx
			{
				menuDbToFile();			
			}
			else if(menuSelection == fileToDbNumber) // xlsx --> DB
			{
				menuFileToDb();
			}
			else if(menuSelection == preferenceNumber) // Preference
			{
				menuPreference();
			}
			else if(menuSelection == jscriptNumber) // JScript
			{
				menuJscript();			
			}
			else if(menuSelection == moduleNumber) // Use Module
			{
				menuModule();
			}
			else if(menuSelection == exitNumber) // Exit
			{		
				menuExit(runIndependency);
			}
		}
		catch(NumberFormatException e) // 입력된 값이 정수 숫자가 아닌 경우
		{
			try
			{
				menuOthers(gets, runIndependency);
			}
			catch(NotConnectedException e1)
			{
				logError(e1, applyStringTable("On menu"), true);
			}
			catch(SQLException e1)
			{
				logError(e1, applyStringTable("On menu"), true);
			}
			catch(Throwable e1)
			{
				logError(e1, applyStringTable("On menu"));
			}
		}
		catch(NotConnectedException e)
		{
			logError(e, applyStringTable("On menu"), true);
		}
		catch(SQLException e)
		{
			logError(e, applyStringTable("On menu"), true);
		}
	}
	
	/**
	 * <p>사용자가 메뉴에서 숫자가 아닌 다른 값을 입력했을 때의 동작을 합니다.</p>
	 * 
	 * @param inputs : 입력한 텍스트
	 * @param runIndependency : 일반 콘솔모드로의 진입 여부, 현재의 메뉴가 종료되면 프로그램을 종료할 지의 여부입니다.
	 * @throws Exception 네트워크, 혹은 DBMS에서 발생한 문제
	 */
	protected void menuOthers(String inputs, boolean runIndependency) throws Throwable
	{
		if(inputs.equalsIgnoreCase("exit")) menuExit(runIndependency);
		else if(inputs.equalsIgnoreCase("connect") || inputs.equalsIgnoreCase("disconnect")) menuConnect();
		else if(inputs.equalsIgnoreCase("preference")) menuPreference();
		else if(inputs.equalsIgnoreCase("script")) menuJscript();
		else if(inputs.equalsIgnoreCase("gc"))
		{			
			log(applyStringTable("Before GC") + "...");
			logMemory();
			System.gc();
			log(applyStringTable("After GC") + "...");
			logMemory();
		}
		else if(inputs.startsWith("SCRIPT://") || inputs.startsWith("script://") || inputs.startsWith("Script://"))
		{
			String scripts = inputs.substring(new String("SCRIPT://").length());
			if(scriptRunner != null)
			{
				Object res = scriptRunner.execute(scripts);
				log(res);
				scriptRunner.getAttributes().put("result", res);
			}
		}
		else if(inputs.startsWith("FILE://") || inputs.startsWith("file://") || inputs.startsWith("File://"))
		{
			String filePath = inputs.substring(new String("FILE://").length());
			TableSet tableSet = DataUtil.fromFile(new File(filePath));
			logTable(tableSet);
			if(scriptRunner != null)
			{
				scriptRunner.getAttributes().put("result", tableSet);
			}
		}
		else
		{
			boolean exists = false;
			
			if(scriptRunner != null)
			{
				Set<String> keys = scriptRunner.getAttributes().keySet();
				
				for(String s : keys)
				{
					if(inputs.equals(s))
					{
						exists = true;
						
						Object attrValue = scriptRunner.getAttributes().get(s);
						if(attrValue instanceof TableSet)
						{
							TableSet attrSet = (TableSet) attrValue;
							logTable(attrSet);
							
							boolean saveInDb = askYes(applyStringTable("Do you want to insert this into DB?"));
							if(saveInDb)
							{
								log(getDao());
								saveInDb = askYes(applyStringTable("Are you sure?"));
								
								if(saveInDb) attrSet.insertIntoDB(getDao());
							}
						}
						else if(attrValue instanceof Record)
						{
							Record attrRec = (Record) attrValue;
							log(attrRec);
							
							boolean saveInDb = askYes(applyStringTable("Do you want to insert this into DB?"));
							if(saveInDb)
							{
								String tableName = askInput(applyStringTable("Input the table name of DB."));
								
								log(getDao());
								saveInDb = askYes(applyStringTable("Are you sure?"));
								
								if(saveInDb) attrRec.insertIntoDB(getDao(), tableName, "true");
							}
						}
						else
						{
							log(attrValue);
						}
						
						break;
					}
				}
			}
			
			if(! exists)
			{
				TableSet tableSet = getDao().query(inputs);
				logTable(tableSet);
				if(scriptRunner != null)
				{
					scriptRunner.getAttributes().put("result", tableSet);
					System.gc();
				}
			}
		}
	}
	
	/**
	 * <p>메뉴에서 사용자가 Connect 를 선택했을 때의 동작입니다. 접속 여부를 확인해 접속 안되어 있으면 접속 과정을 시작하고, 접속이 되어 있으면 접속을 닫습니다.</p>
	 */
	public void menuConnect()
	{
		if(! getDao().isAlive())
		{
			Dao dao = ConnectUtil.tryConnect(this);
			if(dao != null) addDao(dao);
		}
		else
		{
			getDao().close();
		}
	}
	/**
	 * <p>메뉴에서 사용자가 Query 를 선택했을 때의 동작입니다.</p>
	 * 
	 * @param query : DB에 보낼 쿼리문입니다. null 사용 시 사용자에게 쿼리문을 입력받습니다.
	 * @throws Exception : DBMS에서 발생한 문제, 네트워크 문제, 혹은 잘못된 쿼리문으로 인한 문제
	 */
	public void menuQuery(String query) throws Exception
	{
		String sql = query;
		if(query == null) sql = askQuery(applyStringTable("Input SQL scripts."));
		TableSet reads = getDao().query(sql);
		logTable(reads);
		
		if(scriptRunner != null)
		{
			boolean saveAsAttr = askYes(applyStringTable("Do you want to save this table set into the attribute store?"));
			if(saveAsAttr)
			{
				String attrName = askInput(applyStringTable("Input the attribute name you want."), true);
				scriptRunner.getAttributes().put(attrName, reads);
			}
		}
	}
	
	/**
	 * <p>메뉴에서 사용자가 See file 를 선택했을 때의 동작입니다.</p>
	 * 
	 * @throws Exception : 파일 액세스 관련 문제, 권한 문제
	 */
	public void menuSeeFile() throws Exception
	{
		String filePath = askInput(applyStringTable("Input file path."), true);
		TableSet reads = DataUtil.fromFile(new File(filePath));
		logTable(reads);
		
		if(scriptRunner != null)
		{
			boolean saveAsAttr = askYes(applyStringTable("Do you want to save this table set into the attribute store?"));
			if(saveAsAttr)
			{
				String attrName = askInput(applyStringTable("Input the attribute name you want."), true);
				scriptRunner.getAttributes().put(attrName, reads);
			}
		}
	}
	
	/**
	 * <p>메뉴에서 사용자가 DB --> file 를 선택했을 때의 동작입니다.</p>
	 * 
	 * @throws Exception : 파일 액세스 관련 문제, 권한 문제, DBMS에서 발생한 문제, 네트워크 문제, 혹은 잘못된 쿼리문으로 인한 문제
	 */
	public void menuDbToFile() throws Exception
	{
		String sql = askQuery(applyStringTable("Input SQL select scripts."));
		TableSet results = getDao().query(sql);
		if(results == null) logTable(results);
		else
		{
			String filePath = askInput(applyStringTable("Input where to save, with file name."), true);
			filePath = filePath.trim();
			if(filePath.endsWith(".xls") || filePath.endsWith(".xlsx")
					|| filePath.endsWith(".XLS") || filePath.endsWith(".XLSX")
					|| filePath.endsWith(".Xls") || filePath.endsWith(".Xlsx"))
			{
				XLSXUtil.save(results, new File(filePath));
			}
			else if(filePath.endsWith(".json") || filePath.endsWith(".JSON") || filePath.endsWith(".Json"))
			{
				StreamUtil.saveFile(new File(filePath), results.toJSON(false), getOption("file_charset"));
			}
			else if(filePath.endsWith(".hgf") || filePath.endsWith(".HGF") || filePath.endsWith(".Hgf"))
			{
				StreamUtil.saveFile(new File(filePath), results.toHGF(), getOption("file_charset"));
			}
			else
			{
				StreamUtil.saveFile(new File(filePath), results.toHGF(), getOption("file_charset"));
			}
			
			logRaw(applyStringTable("Successfully saved."));
		}
	}
	
	
	/**
	 * <p>메뉴에서 사용자가 file --> DB 를 선택했을 때의 동작입니다.</p>
	 * 
	 * @throws Exception : 파일 액세스 관련 문제, 권한 문제, DBMS에서 발생한 문제, 네트워크 문제, 혹은 잘못된 쿼리문으로 인한 문제
	 */
	public void menuFileToDb() throws Exception
	{
		File target = new File(askInput(applyStringTable("Input file name with path what to load."), true));
		TableSet results = null;
		
		if(! target.exists())
		{
			throw new FileNotFoundException(applyStringTable("On menuFileToDb, file is not exist") + " : " + target);
		}
		
		results = DataUtil.fromFile(target);
		
		log("");
		String tableName = askInput(applyStringTable("Input the table name."), true);
		results.setName(tableName);
		// sql = results.toInsertSQL();			
		log(applyStringTable("Trying to insert data into the DB..."));
		results.insertIntoDB(getDao());
		getDao().commit();
		log(applyStringTable("All data are successfully inserted."));
	}
	
	/**
	 * <p>메뉴에서 사용자가 Preference 를 선택했을 때의 동작입니다.</p>
	 */
	public void menuPreference()
	{
		Set<String> keySet = params.keySet();
		if(keySet.size() <= 0)
		{
			log(applyStringTable("There is no options applied."));
		}
		else
		{
			for(String s : keySet)
			{
				logRaw(s + " : " + params.get(s));
			}
		}
		if(askYes(applyStringTable("Do you want to input or change something?")))
		{
			String keys = askInput(applyStringTable("Please input key of option."), true);
			String values = askInput(applyStringTable("Please input") + " " + keys + " " + applyStringTable("'s value."), true);
			params.put(keys, values);
			logRaw(applyStringTable("Option is applied."));
		}
	}
	
	/**
	 * <p>불러온 콘솔용 모듈들을 반환합니다.</p>
	 * 
	 * @return 콘솔용 모듈 리스트
	 */
	protected List<AbstractConsoleModule> getConsoleModules()
	{
		List<AbstractConsoleModule> consoleModules = new Vector<AbstractConsoleModule>();
		
		for(int i=0; i<modules.size(); i++)
		{
			if(modules.get(i) instanceof AbstractConsoleModule) consoleModules.add((AbstractConsoleModule) modules.get(i));
		}
		
		return consoleModules;
	}
	
	/**
	 * <p>모듈들을 초기화합니다.</p>
	 */
	protected abstract void initModules();
	
	/**
	 * <p>파일로부터 모듈을 불러옵니다.</p>
	 */
	protected void loadModules()
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
		
		try
		{
			modules.addAll(ModuleUtil.loadDefaultClassModules(this));
		}
		catch(Throwable e)
		{
			logError(e, applyStringTable("On loading default module"));
		}
		
		try
		{
			modules.addAll(ModuleUtil.loadModules(this));
		}
		catch(Exception e)
		{
			logError(e, applyStringTable("On loading GUI modules"));
		}
		finally
		{
			
		}
		
		initModules();
	}
	
	/**
	 * <p>모듈을 적용합니다.</p>
	 * 
	 */
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
		
		for(int i=0; i<modules.size(); i++)
		{
			try
			{
				modules.get(i).init();
			}
			catch(Throwable e)
			{
				logError(e, applyStringTable("On loading module") + " : " + modules.get(i).getName(), true);
			}
		}
	}
	
	/**
	 * <p>사용자가 메뉴에서 모듈 사용 항목을 선택했을 때의 동작입니다.</p>
	 * 
	 */
	protected void menuModule()
	{
		List<AbstractConsoleModule> consoleModules = getConsoleModules();
		
		if(consoleModules.size() <= 0) return;
		for(int i=0; i<consoleModules.size(); i++)
		{
			consoleModules.get(i).setMenuNumber(i);
		}
		String inputs = "";
		while(true)
		{
			log("");
			for(int i=0; i<consoleModules.size(); i++)
			{
				log(String.valueOf(i) + ". " + applyStringTable(consoleModules.get(i).getName()));
			}
			int exitNumber = consoleModules.size();
			
			log(String.valueOf(exitNumber) + ". " + applyStringTable("Back"));
			inputs = askMenu(applyStringTable("What module do you want to use?"));
			
			if(DataUtil.isInteger(inputs))
			{
				int selection = Integer.parseInt(inputs);
				boolean selected = false;
				for(int i=0; i<consoleModules.size(); i++)
				{
					if(selection == consoleModules.get(i).getMenuNumber())
					{
						consoleModules.get(i).manage();
						selected = true;
					}
				}
				if(! selected)
				{
					if(selection == exitNumber)
					{
						break;
					}
				}
			}
		}
	}
	
	/**
	 * <p>메뉴에서 사용자가 JScript 를 선택했을 때의 동작입니다.</p>
	 */
	public void menuJscript()
	{
		JScriptMode.manage(this, scriptRunner);
	}
	
	/**
	 * <p>메뉴에서 사용자가 Exit 를 선택했을 때의 동작입니다.</p>
	 * 
	 * @param runIndependency : 일반 콘솔모드로의 진입 여부, 현재의 메뉴가 종료되면 프로그램을 종료할 지의 여부입니다.
	 */
	public void menuExit(boolean runIndependency)
	{
		if(runIndependency)
		{
			logRaw(applyStringTable("Preparing to exit..."));
			getDao().close();
		}
		runSwitch = false;
	}
	
	/**
	 * <p>현재의 매니저의 클래스 이름 풀네임을 반환합니다.</p>
	 */
	@Override
	public String toString()
	{
		return getClass().getName();
	}
	
	/**
	 * <p>선택된 DAO(DB 접속)의 번호를 반환합니다.</p>
	 * 
	 * @return 선택된 DAO 번호
	 */
	public int getSelectedDao()
	{
		if(daos == null) daos = new Vector<Dao>();
		if(daos.size() <= 0) addDao(new JdbcDao(this));
		
		if(selectedDao >= daos.size())
		{
			selectedDao = daos.size() - 1;
			
			Map<String, Object> additionalData = new Hashtable<String, Object>();
			additionalData.put("selected_dao", new Integer(selectedDao));
			refresh(additionalData);
		}
		return selectedDao;
	}

	/**
	 * <p>DAO을 선택합니다.</p>
	 * 
	 * @param selectedDao : 선택할 DAO 번호
	 */
	public void setSelectedDao(int selectedDao)
	{
		this.selectedDao = selectedDao;
		if(daos == null) daos = new Vector<Dao>();
		if(daos.size() <= 0) addDao(new JdbcDao(this));
		if(selectedDao >= daos.size()) selectedDao = daos.size() - 1;
		
		Map<String, Object> additionalData = new Hashtable<String, Object>();
		additionalData.put("selected_dao", new Integer(selectedDao));
		refresh(additionalData);
	}
	
	/**
	 * <p>환경 설정 맵을 반환합니다. 대상 매니저 객체를 가지고 있는 환경에서만 실행이 가능합니다.</p>
	 * 
	 * @param manager : 매니저 객체
	 * @return 환경 설정 맵
	 */
	public Map<String, String> getParamMap(Manager manager)
	{
		if(manager == this) return params;
		else return null;
	}
	
	/**
	 * <p>환경 설정 키 리스트를 반환합니다.</p>
	 * 
	 * @return 환경 설정 키들
	 */
	public Set<String> paramKeys()
	{
		return params.keySet();
	}

	@Override
	public String help()
	{
		StringBuffer stringBuffer = new StringBuffer("");
		
		stringBuffer = stringBuffer.append(Manager.applyStringTable("This object supply interfaces to you.") + "\n");
		stringBuffer = stringBuffer.append(Manager.applyStringTable("") + "\n");
		stringBuffer = stringBuffer.append("log(message) : " + Manager.applyStringTable("Show message.") + "\n");
		stringBuffer = stringBuffer.append("logNotLn(message) : " + Manager.applyStringTable("Show message without line jump.") + "\n");
		stringBuffer = stringBuffer.append("alert(message) : " + Manager.applyStringTable("Show alert dialog.") + "\n");
		stringBuffer = stringBuffer.append("askInput(message, isShort) : " + Manager.applyStringTable("Ask input texts to user. If isShort is false, user can input multiple lines, end with ; symbol.") + "\n");
		stringBuffer = stringBuffer.append("askYes(message) : " + Manager.applyStringTable("Ask y or n to user.") + "\n");
		stringBuffer = stringBuffer.append("askMap(message) : " + Manager.applyStringTable("Ask input map data.") + "\n");
		stringBuffer = stringBuffer.append("askFile(message) : " + Manager.applyStringTable("Ask to select file to load, and return contents as a byte array.") + "\n");
		stringBuffer = stringBuffer.append("askSave(message, byteArray) : " + Manager.applyStringTable("Ask to select file to save, and save contents.") + "\n");
		stringBuffer = stringBuffer.append("applyStringTable(message) : " + Manager.applyStringTable("Translate message.") + "\n");
		stringBuffer = stringBuffer.append("getOption(key) : " + Manager.applyStringTable("Get preference option value") + "\n");
		stringBuffer = stringBuffer.append("optionList() : " + Manager.applyStringTable("See applied preference keys") + "\n");
		
		return stringBuffer.toString();
	}
	
	@Override
	public void noMoreUse()
	{
		
	}
	
	@Override
	public boolean isAlive()
	{
		return true;
	}
	
	/**
	 * <p>DAO 정보들을 반환합니다.</p>
	 * 
	 * @return DAO 정보들
	 */
	public List<String> daoList()
	{
		List<String> daoLists = new Vector<String>();
		for(int i=0; i<getDaos().size(); i++)
		{
			daoLists.add(getDaos().get(i).toString());
		}
		return daoLists;
	}
	
	/**
	 * <p>해당 모듈 사용 여부를 묻고 그 결과를 반환합니다.</p>
	 * 
	 * @param module : 모듈
	 * @return 사용 여부
	 */
	public boolean askModuleAccept(Module module)
	{
		String msg = applyStringTable("Name") + " : " + module.getName();
		msg = msg + "\n" + applyStringTable("ID") + " : " + String.valueOf(module.getModuleId());
		msg = msg + "\n\n";
		
		String licenseInfos = module.getLicense(); 
		if(DataUtil.isNotEmpty(licenseInfos))
		{
			licenseInfos = licenseInfos.trim();
			if(licenseInfos.startsWith("url://") || licenseInfos.startsWith("URL://") || licenseInfos.startsWith("Url://"))
			{
				try
				{
					licenseInfos = StreamUtil.readText(new URL(licenseInfos.substring(new String("url://").length())), "UTF-8");
				}
				catch (IOException e)
				{
					licenseInfos = applyStringTable("Cannot access URL") + " : " + licenseInfos.substring(new String("url://").length());
				}
			}
			else if(licenseInfos.startsWith("file://") || licenseInfos.startsWith("FILE://") || licenseInfos.startsWith("File://"))
			{
				File targetFile = new File(licenseInfos.substring(new String("file://").length()));
				if(! targetFile.exists())
				{
					targetFile = new File(ClassTool.currentDirectory() + licenseInfos.substring(new String("file://").length()));
				}
				if(targetFile.exists())
				{
					licenseInfos = StreamUtil.readText(targetFile, "UTF-8");
				}
				else
				{
					licenseInfos = applyStringTable("Cannot access File") + " : " + licenseInfos.substring(new String("file://").length());
				}
			}
			
			msg = msg + licenseInfos;
		}
		
		msg = msg + "\n" + applyStringTable("Do you want to use") + " " + module.getName() + "?";
		
		return askYes(msg);
	}
	
	/**
	 * <p>스크립트 엔진에 객체를 삽입합니다. 해당 변수 이름을 이미 사용하고 있다면 예외가 발생합니다.</p>
	 * 
	 * @param id : 사용할 변수 이름
	 * @param obj : 삽입할 객체
	 * @throws Exception 스크립트 엔진이 초기화되지 않은 문제, 이미 해당 변수 이름을 쓰고 있는 문제 등
	 */
	public void putOnScriptEngine(String id, JScriptObject obj) throws Throwable
	{
		Object existChecks = null;
		
		try
		{
			existChecks = scriptRunner.execute(id, true, true);
			if(existChecks != null 
					&& (! (existChecks instanceof ScriptException))) throw new InvalidInputException(applyStringTable("Already exist") + " : " + id);
			
			scriptRunner.put(id, obj);
			scriptRunner.getAttributes().put(id, obj);
			ScriptUtil.putObject(id, obj);
		}
		catch(ScriptException e)
		{
			 throw new InvalidInputException(applyStringTable("Already exist") + " : " + id);
		}
	}
	
	/**
	 * <p>스크립트 엔진이 준비되어 있으면 그 스크립트 엔진을 반환합니다.</p>
	 * 
	 * @param module : 스크립트 엔진이 필요한 모듈 (사용자가 이 모듈 사용을 동의한 상태가 아니라면 null 반환)
	 * @return 스크립트 엔진
	 */
	public JScriptRunner getMainScriptEngine(Module module)
	{
		if(ModuleUtil.checkAccepted(module)) return scriptRunner;
		else return null;
	}
	
	/**
	 * <p>모듈 단독 실행 옵션이 있는지 검사합니다.</p>
	 * 
	 * @param args : 입력받은 매개 변수들
	 * @return 실행해야 할 모듈 ID
	 */
	protected long checkRunModuleMode(Map<String, String> args)
	{
		if(args == null) return 0;
		
		try
		{
			if(DataUtil.parseBoolean(args.get("runModuleIndependency")))
			{
				return Long.parseLong(args.get("runModuleIndependency"));
			}
		}
		catch(Throwable e)
		{
			logError(e, applyStringTable("On checking run-module-independency option"));
			log(applyStringTable("The program will be closed."));
			args.remove("runModuleIndependency");
			close();
			Main.processShutdown();
		}
		
		return 0;
	}
	
	/**
	 * <p>모듈 단독 실행 옵션을 검사해 해당 옵션이 있으면 동작시킵니다.</p>
	 * 
	 * @param args : 입력받은 매개 변수들
	 */
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
						m.manage(args);
					}
				}
			}
			if(runNow)
			{
				close();
				Main.processShutdown();
			}
		}
	}
	
	/**
	 * <p>자주 사용하는 스크립트 목록을 갱신합니다. 파일을 불러오지는 않으며, 이미 불러온 파일 목록을 토대로 화면만 갱신합니다.</p>
	 */
	protected void applyFavorites()
	{
		
	}
	
	/**
	 * <p>자주 사용하는 스크립트를 실행합니다.</p>
	 * 
	 * @param fav : 자주 사용하는 스크립트
	 * @throws Throwable
	 */
	protected void runFavorite(AbstractFavorites fav) throws Throwable
	{
		String lang = fav.getLanguage();
		Object result = null;
		Set<String> paramSet = fav.getParameterSet();
		Map<String, String> befores = new Hashtable<String, String>();
		for(String s : paramSet)
		{
			befores.put(s, "");
		}
		Map<String, String> params = null;
		if(paramSet.isEmpty()) params = new Hashtable<String, String>();
		else if(paramSet.size() == 1)
		{
			String onlyKey = paramSet.iterator().next();
			params = new Hashtable<String, String>();
			params.put(onlyKey, askInput(applyStringTable("Parameter") + " " + onlyKey, true));
		}
		else params = askMap(applyStringTable("Favorites") + " : " + fav.getName(), befores);
		if(params == null) return;
		if(lang.equalsIgnoreCase("SQL"))
		{
			result = getDao().query(fav.getScript(params));
		}
		else if(lang.equalsIgnoreCase("JScript"))
		{
			result = scriptRunner.execute(fav.getScript(params));
		}
		else
		{
			result = ScriptUtil.getRunner(lang).execute(fav.getScript(params));
		}
		if(result != null) log(result);
	}
	
	/**
	 * 매니저 객체가 지원하는 명령어를 실행합니다.
	 * 
	 * @param order : 명령
	 */
	protected void activeOrder(String order)
	{
		
	}
	
	/**
	 * 매니저 객체가 지원하는 명령어를 실행합니다.
	 * 
	 * @param order : 명령
	 * @param module : 명령을 실행하려는 모듈 객체 (보통 this 사용)
	 */
	public void activeOrder(String order, Module module)
	{
		if(! ModuleUtil.checkAuthorize(module)) throw new SecurityException(applyStringTable("Module") + " " + module.getName() + "(" + module.getModuleId() + " "
				+ applyStringTable("is not authorized") + ".");
		activeOrder(order);
	}
}
