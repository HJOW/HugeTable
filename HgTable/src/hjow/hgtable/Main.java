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

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import hjow.hgtable.classload.ClassTool;
import hjow.hgtable.tableset.TableSet;
import hjow.hgtable.ui.ConsoleManager;
import hjow.hgtable.ui.swing.SwingManager;
import hjow.hgtable.util.ArgumentUtil;
import hjow.hgtable.util.DataUtil;
import hjow.hgtable.util.GUIUtil;
import hjow.hgtable.util.InvalidInputException;
import hjow.hgtable.util.JavaUtil;
import hjow.hgtable.util.LicenseUtil;
import hjow.hgtable.util.debug.DebuggingUtil;
import hjow.state.FirstStateView;

/**
 * 
 * <p>처음 jar 파일이 실행될 때 실행되는 main 메소드가 있는 클래스입니다.</p>
 * 
 * @author HJOW
 *
 */
public class Main
{
	public static final int[] version = {0, 0, 0, 21};
	protected static Manager manager;
	public static boolean atFirst = true;
	
	public static final int INTERRUPT_NORMAL = -1;
	public static final int INTERRUPT_EXIT_ALL = 0;
	public static final int INTERRUPT_EXIT_TO_MAIN = 1;
	
	public static final int EXIT_RESULT_ERROR = -1;
	public static final int EXIT_RESULT_NORMAL = 0;
	public static final int EXIT_RESULT_RESTART = 1;
	
	private static int interrupts = INTERRUPT_NORMAL;
	private static int exitResultFlag = EXIT_RESULT_NORMAL;
	
	/**
	 * <p>디버깅, 혹은 배포용으로 실행 모드를 지정합니다.</p>
	 */
	public static int MODE = DebuggingUtil.RELEASE;
	
	/**
	 * <p>프로그램이 처음 실행될 때 입력된 매개 변수들을 임시 저장합니다.</p>
	 */
	private static transient String[] startArgs;
	
	/**
	 * <p>종료 명령에 사용되는 변수입니다. 이 값이 true 이면 종료 시 프로그램이 완전히 종료되지 않고 재시작됩니다.</p>
	 */
	private static transient boolean needRestart = false;
	
	/**
	 * <p>종료 명령 수행 중 종료 명령이 또 실행되는 것을 방지하기 위한 변수입니다.</p>
	 */
	private static transient boolean ignoreShutdownOrder = false;
	
	/**
	 * <p>처음 jar 파일이 실행될 때 실행되는 main 메소드입니다.</p>
	 * 
	 * <p>매개 변수는 다음과 같은 형태를 사용합니다.</p>
	 * <p>--GUIMode true</p>
	 * <p>설정할 대상은 앞에 뺄셈 기호(-) 두 개를 붙이고, 설정 값은 그대로 넣습니다.</p>
	 * 
	 * @param args : 실행 시 입력한 매개 변수들
	 */
	public static void main(String[] args)
	{
		startArgs = args;
		
		// GUI 모드 여부 확인
		boolean guiMode = false;
		
//		StringBuffer argText = new StringBuffer("");
		List<String> argList = new Vector<String>();
		List<String> ignoreList = new Vector<String>();
		Map<String, String> argMap = new Hashtable<String, String>();
		if(args != null)
		{
			for(int i=0; i<args.length; i++)
			{
				if(args[i].startsWith("-XX")) continue;
				if(args[i].startsWith("-Xm")) continue;
				if(args[i].startsWith("-jar")) continue;
				if(args[i].startsWith("-classpath")) continue;
				if(args[i].startsWith("-cp")) continue;
				
//				if(args[i].indexOf(" ") >= 0 && (! (args[i].trim().startsWith("\"") || args[i].trim().startsWith("'"))))
//				{
//					argText = argText.append("\"" + args[i] + "\"");
//				}
//				else argText = argText.append(args[i]);
//				if(i < args.length - 1) argText = argText.append(" ");
				argList.add(args[i]);
			}
			
			argMap.putAll(ArgumentUtil.getArguments(argList, ignoreList));
			
			if(argMap.get("GUIMode") != null)
			{
				try
				{
					guiMode = DataUtil.parseBoolean(argMap.get("GUIMode"));
				}
				catch (InvalidInputException e)
				{
					System.out.println("GUIMode setting is invalid : " + argMap.get("GUIMode"));
				}
			}
		}
		
		argMap.put("java_version_full", JavaUtil.fullVer());
		argMap.put("java_version", String.valueOf(JavaUtil.ver()));
		argMap.put("config_path", System.getProperty("user.home") 
				+ System.getProperty("file.separator") 
				+ LicenseUtil.titles.replace(" ", "") 
				+ System.getProperty("file.separator"));
		
		boolean useState = true;
		
		if(DataUtil.isNotEmpty(argMap.get("use_state_view")))
		{
			useState = DataUtil.parseBoolean(argMap.get("use_state_view"));
		}
		
		String managerTypeArgs = argMap.get("manager_class");
			
		if(DataUtil.isEmpty(managerTypeArgs))
		{
			// GUI 모드 여부에 따라 Manager 객체를 다르게 생성
			if(guiMode)
			{
				GUIUtil.init(argMap);
				if(useState) FirstStateView.on(true);
				
				manager = new SwingManager();
			}
			else
			{
				manager = new ConsoleManager();
			}
		}
		else
		{
			try 
			{
				manager = (Manager) ClassTool.createInstance(managerTypeArgs);
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				return;
			}
		}
		
		// 프로그램 실행
		manager.manage(argMap);
	}
	
	/**
	 * <p>이 메소드는 무한 반복 잠재성이 있는 곳에서 호출됩니다.</p>
	 * 
	 * @param targetClass : 대상 클래스, 정적 메소드인 경우 사용
	 * @param msg : 반복문 위치를 짐작할 수 있는 메시지
	 * @return : false 이면 반복을 끝내야 하는 것 
	 */
	public static boolean checkInterrupt(Class<?> targetClass, String msg)
	{
		switch(interrupts)
		{
		case INTERRUPT_EXIT_ALL:
			return false;
		case INTERRUPT_EXIT_TO_MAIN:
			return ((targetClass.getName().endsWith("Manager") || targetClass.getName().endsWith("ConsoleManager")) && msg.equals("runMenu"));
		}
		return true;
	}
	
	/**
	 * <p>이 메소드는 무한 반복 잠재성이 있는 곳에서 호출됩니다.</p>
	 * 
	 * @param targetObject : 대상 객체, 일반 메소드인 경우 사용
	 * @param msg : 반복문 위치를 짐작할 수 있는 메시지
	 * @return : false 이면 반복을 끝내야 하는 것 
	 */
	public static boolean checkInterrupt(Object targetObject, String msg)
	{
		switch(interrupts)
		{
		case INTERRUPT_EXIT_ALL:
			return false;
		case INTERRUPT_EXIT_TO_MAIN:
			return ((targetObject instanceof Manager) && msg.equals("runMenu"));
		}
		return true;
	}
	
	/**
	 * <p>실행 중인 반복문들, 쓰레드 실행을 중단하는 옵션을 지정합니다.</p>
	 * 
	 * @param exitAll : true 시 모든 반복문이 종료됩니다. false 시 콘솔 메뉴까지만 남기고 나머지 반복문이 종료되며 중단 옵션이 원래대로 돌아옵니다.
	 */
	public static void interrupt(boolean exitAll)
	{
		if(exitAll)
		{
			interrupts = INTERRUPT_EXIT_ALL;
		}
		else
		{
			interrupts = INTERRUPT_EXIT_TO_MAIN;
		}
	}
	
	/**
	 * <p>중단 옵션을 원래대로 되돌립니다.</p>
	 */
	public static void removeInterrupt()
	{
		interrupts = INTERRUPT_NORMAL;
	}
	
	/**
	 * <p>표준 출력을 대신합니다.</p>
	 * 
	 */
	public static void println()
	{
		System.out.println();
	}
	
	/**
	 * <p>표준 출력을 대신합니다.</p>
	 * 
	 * @param ob : 출력할 객체
	 */
	public static void println(Object ob)
	{
		System.out.println(ob);
	}
	
	/**
	 * <p>표준 출력을 대신합니다.</p>
	 * 
	 * @param ob : 출력할 객체
	 */
	public static void print(Object ob)
	{
		System.out.print(ob);
	}

	/**
	 * <p>진행 상태를 게이지 바 형태로 알 수 있는 Manager 의 경우 이 메소드를 오버라이드해 사용합니다.</p>
	 * 
	 * @param percents : 현재 진행 상태 (0 ~ 100)
	 */
	public static void setPercent(int percents)
	{
		manager.setPercent(percents);
	}
	
	/**
	 * <p>프로그램 종료를 준비합니다.</p>
	 */
	protected static void prepareToShutdown()
	{
		ignoreShutdownOrder = true;
		try
		{
			if(manager != null)
			{
				manager.close();
			}
		}
		catch(Throwable t)
		{
			
		}
		System.gc();
		manager = null;
		ignoreShutdownOrder = false;
	}
	
	/**
	 * <p>이 프로그램을 종료합니다. Manager 객체의 close 중에 사용됩니다.</p>
	 */
	public static void exitAllProcess()
	{
		if(ignoreShutdownOrder) return;
		prepareToShutdown();
		System.exit(exitResultFlag);
	}
	
	/**
	 * <p>프로그램을 완전히 재시작합니다. Windows 에서만 동작하며, Unix 계열에서도 동작하게 하려면 쉘 파일을 완성해야 합니다.</p>
	 */
	public static void restartAllProcess()
	{
		exitResultFlag = EXIT_RESULT_RESTART;
		exitAllProcess();
	}
	
	/**
	 * <p>프로그램 실행 시 입력한 매개변수를 그대로 가지고 main 동작을 다시 실행합니다. 내부적으로 대부분의 쓰레드들을 종료시키지만, 되도록이면 다른 동작이 완전히 완료된 이후 호출해야 합니다.</p>
	 */
	public static void restartMain()
	{
		if(ignoreShutdownOrder) return;
		prepareToShutdown();
		main(startArgs);
	}
	
	/**
	 * <p>프로그램 종료 명령을 시행합니다. 재시작 플래그가 있었다면 재시작 동작을 합니다.</p>
	 */
	public static void processShutdown()
	{
		if(needRestart) 
		{
			needRestart = false;
			restartAllProcess();
		}
		else exitAllProcess();
	}

	public static void log(Object str)
	{
		manager.logRaw(str);
	}

	public static void log(Object str, int level)
	{
		manager.log(str, level);
	}

	public static void logError(Throwable e, String additionalMsg)
	{
		manager.logError(e, additionalMsg);
	}

	public static void logError(Throwable e, String additionalMsg, boolean simplify)
	{
		manager.logError(e, additionalMsg, simplify);
	}

	public static void logNotLn(Object ob)
	{
		manager.logRawNotLn(ob);
	}

	public static void logNotLn(Object ob, int level)
	{
		manager.logNotLn(ob, level);
	}

	public static void logDrawBar()
	{
		manager.logDrawBar();
	}

	public static void logTable(TableSet table)
	{
		manager.logTable(table);
	}
	
	public static boolean askYes(String msg)
	{
		return manager.askYes(msg);
	}
	
	public static String askInput(String msg, boolean isShort)
	{
		return manager.askInput(msg, isShort);
	}
	
	public static List<String> daoList()
	{
		return manager.daoList();
	}
	
	public static String getSelectedDao()
	{
		return manager.getDao().toString();
	}
}