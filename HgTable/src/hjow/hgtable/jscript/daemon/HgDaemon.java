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

package hjow.hgtable.jscript.daemon;

import java.net.ServerSocket;
import java.util.List;
import java.util.Vector;

import hjow.hgtable.HThread;
import hjow.hgtable.Main;
import hjow.hgtable.Manager;
import hjow.hgtable.jscript.JScriptRunner;
import hjow.hgtable.jscript.NeedAuthorize;

/**
 * <p>데몬 인스턴스 하나에 해당하는 객체에 대한 클래스입니다.</p>
 * 
 * @author HJOW
 *
 */
@Deprecated
public class HgDaemon implements Runnable, NeedAuthorize
{
	// TODO : 미완성
	private boolean threadSwitch = false;
	private int threadGap = 100;
	
	protected ServerSocket serverSocket = null;
	protected List<HgUser> users = new Vector<HgUser>();
	
	protected int port = 8089;
	protected static List<HgDaemon> daemonList = new Vector<HgDaemon>();
	protected List<HgAccount> accounts = new Vector<HgAccount>();
	
	/**
	 * <p>기본 생성자입니다.</p>
	 */
	public HgDaemon()
	{
		
	}
	/**
	 * <p>이 프로세스를 통해 실행된 데몬을 모두 종료합니다.</p>
	 */
	public static void closeAll()
	{
		for(int i=0; i<daemonList.size(); i++)
		{
			try
			{
				daemonList.get(i).close();
			}
			catch(Throwable e)
			{
				
			}
		}
	}
	/**
	 * <p>데몬이 활성화되어 있는지 여부를 확인합니다.</p>
	 * 
	 * @return 데몬 작동 여부
	 */
	public boolean isAlive()
	{
		return threadSwitch;
	}
	/**
	 * <p>데몬을 끕니다.</p>
	 */
	public void close()
	{
		threadSwitch = false;
		for(int i=0; i<users.size(); i++)
		{
			try
			{
				users.get(i).close();
			}
			catch(Throwable e)
			{
				
			}
		}
		try
		{
			serverSocket.close();
		}
		catch(Throwable e)
		{
			
		}
	}
	/**
	 * <p>데몬을 실행합니다.</p>
	 * 
	 * @exception Exception 네트워크 문제
	 */
	public void start() throws Exception
	{
		threadSwitch = true;
		serverSocket = new ServerSocket(port);
		new HThread(this).start();
	}
	public int getPort()
	{
		return port;
	}
	public void setPort(int port)
	{
		this.port = port;
	}
	public static void manage(String[] args) throws Exception
	{
		HgDaemon daemon = new HgDaemon();
		daemonList.add(daemon);
		daemon.start();
	}
	public void onThread() throws Exception
	{
		HgUser newUser = null;		
		newUser = new HgUser(serverSocket.accept());
		defaultSetting(newUser);
		users.add(newUser);
	}
	public void defaultSetting(HgUser user)
	{
		user.getScriptRunner().setPriv_allowCommand(this, false);
		user.getScriptRunner().setPriv_allowLoadClass(this, false);
		user.getScriptRunner().setPriv_allowThread(this, false);
		user.getScriptRunner().initPriv_accessDBList(this);
		user.getScriptRunner().initPriv_allowedReadFilePath(this);
		user.getScriptRunner().initPriv_allowedWriteFilePath(this);
		
		user.getScriptRunner().getPriv_allowedReadFilePath(this).add(System.getProperty("user.home"));
		user.getScriptRunner().getPriv_allowedWriteFilePath(this).add(System.getProperty("user.home"));
	}
	public boolean isAuthorize(JScriptRunner runner)
	{
		if(runner != null)
		{
			return true;
		}
		else return false;
	}
	public boolean isAuthorize(String id, String pw)
	{
		for(int i=0; i<accounts.size(); i++)
		{
			if(accounts.get(i).getId().equals(id))
			{
				if(accounts.get(i).getPw().equals(pw))
				{
					return true;
				}
			}
		}
		return false;
	}
	@Override
	public void run()
	{
		while(threadSwitch)
		{
			try
			{
				onThread();
			}
			catch(Throwable e)
			{
				Main.logError(e, Manager.applyStringTable("On daemon on thread"));
			}
			try
			{
				Thread.sleep(threadGap);
			}
			catch(Throwable e)
			{
				
			}
		}
	}
	public boolean isThreadSwitch()
	{
		return threadSwitch;
	}
	public void setThreadSwitch(boolean threadSwitch)
	{
		this.threadSwitch = threadSwitch;
	}
	public int getThreadGap()
	{
		return threadGap;
	}
	public void setThreadGap(int threadGap)
	{
		this.threadGap = threadGap;
	}
	public ServerSocket getServerSocket()
	{
		return serverSocket;
	}
	public void setServerSocket(ServerSocket serverSocket)
	{
		this.serverSocket = serverSocket;
	}
	public List<HgUser> getUsers()
	{
		return users;
	}
	public void setUsers(List<HgUser> users)
	{
		this.users = users;
	}
	public static List<HgDaemon> getDaemonList()
	{
		return daemonList;
	}
	public static void setDaemonList(List<HgDaemon> daemonList)
	{
		HgDaemon.daemonList = daemonList;
	}
}
