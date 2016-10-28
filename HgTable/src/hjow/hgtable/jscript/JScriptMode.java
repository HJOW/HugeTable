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

import hjow.hgtable.Main;
import hjow.hgtable.Manager;
import hjow.hgtable.dao.Dao;
import hjow.hgtable.ui.ConsoleManager;
import hjow.hgtable.util.ConnectUtil;
import hjow.hgtable.util.SpecialOrderUtil;

/**
 * <p>JScript 모드 실행 코드가 담긴 클래스입니다. 스크립트에서 자주 쓰이는 정적인 메소드만 선언되어 있습니다.</p>
 * 
 * @author HJOW
 *
 */
public class JScriptMode
{
	/**
	 * <p>스크립트 모드를 실행합니다.</p>
	 * 
	 * @param runner : 스크립트 엔진
	 */
	public static void manage(Manager manager, JScriptRunner runner)
	{
		log(Manager.getOption("program_title") + " " + Manager.getOption("version"));
		log(applyStringTable("JScript mode is started."));
		log(applyStringTable("If you want to exit JScript mode, just input \"exit\"."));
		String orders;
		Object results;
		Dao dao = null;
		String prepends = "";
		while(true)
		{
			if(dao != null) prepends = "DAO";
			if(manager instanceof ConsoleManager) orders = ((ConsoleManager) manager).askInput("", prepends, true); 
			else orders = askInput("");
			try
			{				
				if(orders == null || orders.trim().equals("")) continue;
				if(orders.equalsIgnoreCase("connect"))
				{
					dao = ConnectUtil.tryConnect(manager);
					if(dao != null && dao.isAlive()) log(applyStringTable("If you want to exit DAO mode, just input \"exit\"."));
					else
					{
						log(applyStringTable("Cannot go into the DAO mode."));
						dao = null;
					}
					continue;
				}
				if(dao == null) results = runner.execute(orders);
				else
				{
					if(orders.equalsIgnoreCase("exit") || orders.equalsIgnoreCase("exit;"))
					{
						dao.close();
						dao = null;
						continue;
					}
					results = dao.query(orders);
			    }
				if(results != null) log(results);
				
				if(results instanceof SpecialOrder)
				{
					if(String.valueOf(results).substring(10).equalsIgnoreCase("exit"))
					{
						break;
					}
					else
					{
						results = SpecialOrderUtil.act(String.valueOf(results).substring(10), runner);
						if(results != null) log(results);
					}
				}
			}
			catch(Throwable e)
			{
				logError(e, applyStringTable("On running following script") + "...\n" + orders + "\n", runner.isDefaultErrorSimplicityOption());
			}
			
			if(! (Main.checkInterrupt(JScriptMode.class, "manage")))
			{
				break;
			}
		}
	}
	private static String applyStringTable(String bef)
	{
		return Manager.applyStringTable(bef);
	}
	private static void log(Object ob)
	{
		Main.log(ob);
	}
	/*
	private static void logNotLn(Object ob)
	{
		Main.manager.logNotLn(ob);
	}
	private static void logDrawBar()
	{
		Main.manager.logDrawBar();
	}
	private static boolean askYes(String msg)
	{
		return Main.manager.askYes(msg);
	}
	*/
	private static String askInput(String msg)
	{
		return Main.askInput(msg, true);
	}
	private static void logError(Throwable e, String addMsg, boolean simplify)
	{
		Main.logError(e, addMsg, simplify);
	}
}
