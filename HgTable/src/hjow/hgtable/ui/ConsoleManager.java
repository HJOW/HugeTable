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

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Map;

import hjow.hgtable.Main;
import hjow.hgtable.Manager;
import hjow.hgtable.jscript.module.AbstractConsoleModule;
import hjow.hgtable.tableset.TableSet;
import hjow.hgtable.util.DataUtil;
import hjow.hgtable.util.InvalidInputException;
import hjow.hgtable.util.StreamUtil;
import hjow.state.FirstStateView;

/**
 * <p>콘솔 모드에서 사용되는 매니저입니다.</p>
 * 
 * @author HJOW
 *
 */
public class ConsoleManager extends Manager
{
	private static final long serialVersionUID = -5504383476324335831L;
	private InputStreamReader reader;
	protected BufferedReader bufferedReader;
	
		
	public ConsoleManager()
	{
		super();
		reader = new InputStreamReader(System.in);
		bufferedReader = new BufferedReader(reader);
	}
	@Override
	public void close()
	{
		super.close();
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
		bufferedReader = null;
		reader = null;
	}
	@Override
	protected void onThread()
	{
		super.onThread();
	}
	@Override
	protected void logRaw(Object ob)
	{
		Main.println(ob);
		
		if(logBuffer != null)
		{
			try
			{
				logBuffer.write(String.valueOf(ob));
				logBuffer.newLine();
			}
			catch(Throwable e)
			{
				
			}
		}
	}
	@Override
	protected void logRawNotLn(Object ob)
	{
		Main.print(ob);
		
		if(logBuffer != null)
		{
			try
			{
				logBuffer.write(String.valueOf(ob));
			}
			catch(Throwable e)
			{
				
			}
		}
	}
	@Override
	public void logDrawBar()
	{
		logRaw("------------------------------------------");
	}	
	@Override
	public void logTable(TableSet table)
	{
		super.logTable(table);
//		TableSet resultTable = table;
//		if(resultTable == null) logRaw(applyStringTable("There is no table"));
//		else if(resultTable.getRecordCount() <= 0) logRaw(applyStringTable("There is no data in table"));
//		else
//		{
//			logRaw("");
//			logRawNotLn(applyStringTable("ROWNUM") + "\t");
//			for(int i=0; i<resultTable.getColumns().size(); i++)
//			{
//				logRawNotLn(resultTable.getColumns().get(i).getName());
//				if(i < resultTable.getColumns().size() - 1) logRawNotLn("\t");
//			}
//			logRaw("");
//			logDrawBar();
//			
//			for(int i=0; i<resultTable.getRecordCount(); i++)
//			{
//				logRawNotLn(String.valueOf(i) + "\t");
//				for(int j=0; j<resultTable.getColumns().size(); j++)
//				{
//					logRawNotLn(resultTable.getColumns().get(j).getData().get(i));
//					if(j < resultTable.getColumns().size() - 1) logRawNotLn("\t");
//				}
//				logRaw("");
//			}
//			logRaw("");
//		}
	}
	@Override
	public void logError(Throwable e, String addMsg)
	{
		e.printStackTrace();
		logRaw("\n" + addMsg);
	}
	@Override
	public String askInput(String msg)
	{
		return askInput(msg, false);
	}
	@Override
	public String askInput(String msg, boolean isShort)
	{
		return askInput(msg, "", isShort);
	}
	
	/**
	 * <p>사용자에게 문장을 입력받습니다. 여러 줄로 된 긴 문장을 입력받으려는 경우 사용자가 [end] 를 입력할 때까지 입력받게 됩니다.</p>
	 * 
	 * @param msg : 입력받기 전 보일 메시지
	 * @param prepends : 입력 란 앞부분에 보일 메시지
	 * @param isShort : 1줄 입력 여부
	 * @return 입력된 문자열
	 */
	public String askInput(String msg, String prepends, boolean isShort)
	{
		if(msg != null) logRaw(msg);
		if(! isShort) logRaw(applyStringTable("Input [end] to finish input."));
		try
		{
			String readLines = "";
			String accums = "";
			int singleQuotes = 0;
			int doubleQuotes = 0;
			int nullCount = 0;
			
			while(true)
			{
				logRawNotLn(prepends + ">> ");
				readLines = bufferedReader.readLine();
				
				if(readLines == null)
				{
					nullCount++;
					continue;
				}
				else nullCount = 0;
				
				if(nullCount >= 2) break;
				
				singleQuotes = singleQuotes + readLines.split("'").length - 1;
				doubleQuotes = doubleQuotes + readLines.split("\"").length - 1;
				
				if(isShort)
				{
					return accums + readLines;
				}
				else if((readLines.trim().equalsIgnoreCase("[END]")) 
						&& ((! (singleQuotes >= 1 && singleQuotes % 2 == 1))
								&& (! (doubleQuotes >= 1 && doubleQuotes % 2 == 1)))) 
				{
					break;
				}
				else if((readLines.trim().equalsIgnoreCase("[CANCEL]")) 
						&& ((! (singleQuotes >= 1 && singleQuotes % 2 == 1))
								&& (! (doubleQuotes >= 1 && doubleQuotes % 2 == 1))))
				{
					return "";
				}
				else
				{
					accums = accums + readLines;
				}
				
				if(! (Main.checkInterrupt(this, "askInput")))
				{
					break;
				}
			}
			return DataUtil.remove65279(accums);
		}
		catch(Throwable e)
		{
			logError(e, "");
			return "";
		}		
	}
	@Override
	public void manage(Map<String, String> args)
	{
		super.manage(args);
		
		FirstStateView.off();
		String scriptModeArgs = args.get("script_mode");
		String fileModeArgs = args.get("file_mode");
		boolean runMenu = true;
		boolean fileMode = false;
		long moduleMode = checkRunModuleMode(args);
		
		if(scriptModeArgs != null)
		{
			try
			{
				runMenu = (! DataUtil.parseBoolean(scriptModeArgs));
			}
			catch (InvalidInputException e)
			{
				e.printStackTrace();
			}
		}
		
		if(fileModeArgs != null)
		{
			try
			{
				fileMode = DataUtil.parseBoolean(fileModeArgs);
				runMenu = (! fileMode);
			}
			catch (InvalidInputException e)
			{
				e.printStackTrace();
			}
		}
		
		loadModules();
		
		if(fileMode)
		{
			try
			{
				String charsets = args.get("file_charset");
				if(DataUtil.isEmpty(charsets)) log(scriptRunner.execute(StreamUtil.readText(new File(args.get("file_target")), "UTF-8")));
				else log(scriptRunner.execute(StreamUtil.readText(new File(args.get("file_target")), charsets)));
			}
			catch(Throwable e)
			{
				logError(e, applyStringTable("On running file"));
			}
		}
		else if(moduleMode != 0)
		{
			applyRunModuleOption(args);
		}
		else runMenu(String.valueOf(runMenu));
		
		close();
		logRaw("Bye");
	}
	@Override
	public boolean isAlive()
	{
		return reader != null;
	}
	
	@Override
	protected void initModules()
	{
		int i=0;
		while(i < modules.size())
		{	
			if(modules.get(i) instanceof AbstractConsoleModule)
			{
				try
				{
					modules.get(i).init();
				}
				catch(Throwable t)
				{
					logError(t, applyStringTable("On loading module") + " : " + modules.get(i).getName());
				}
				i++;
			}
			else
			{
				modules.remove(i);
				i = 0;
			}
		}
	}
	@Override
	public void alert(Object ob)
	{
		log(ob);
	}
}
