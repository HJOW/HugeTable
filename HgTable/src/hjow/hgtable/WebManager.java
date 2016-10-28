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

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.ServiceLoader;
import java.util.Vector;

import hjow.hgtable.util.net.WebServer;
import hjow.web.content.DefaultPageContent;
import hjow.web.content.Page;

public class WebManager extends Manager
{
	private static final long serialVersionUID = -182290948609607761L;
	protected WebServer server;
	protected DefaultPageContent pageContent;
	protected Manager superManager;
	protected List<Task> tasks = new Vector<Task>();
	protected TaskThread taskThread;
	
	public WebManager(Manager manager)
	{
		this.superManager = manager;
	}
	
	@Override
	public void manage(Map<String, String> args)
	{
		super.manage(args);
		
		try
		{
			init();
		}
		catch(Throwable t)
		{
			close();
		}
	}
	
	public void init() throws IOException
	{
		taskThread = new TaskThread();
		
		server = new WebServer();
		pageContent = new DefaultPageContent();
		
		ServiceLoader<Page> loader = ServiceLoader.load(Page.class);
		for(Page page : loader)
		{
			pageContent.getPages().add(page);
		}
		
		server.setPageContent(pageContent);
		
		taskThread.start();
		server.init(80);
	}

	@Override
	public String askInput(String msg, boolean isShort)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void logRaw(Object ob)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void logRawNotLn(Object ob)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void initModules()
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void close()
	{
		if(server != null) server.close();
		server = null;
		pageContent = null;
		super.close();
	}
	
	class TaskThread extends Thread
	{
		protected boolean threadSwitch = true;
		
		public TaskThread()
		{
			
		}
		public void close()
		{
			threadSwitch = false;
		}
		@Override
		public void run()
		{
			while(threadSwitch)
			{
				for(Task task : tasks)
				{
					try
					{
						
					}
					catch(Throwable t)
					{
						
					}
				}
				
				try
				{
					Thread.sleep(50);
				}
				catch(Throwable t)
				{
					
				}
			}
		}
	}
}
class Task implements Serializable
{
	private static final long serialVersionUID = 6736717125259846616L;
	protected Long id = new Long(new Random().nextLong());
	protected String received;
	
	public Task()
	{
		
	}
	public Task(Long id, String received)
	{
		super();
		this.id = id;
		this.received = received;
	}
	public Long getId()
	{
		return id;
	}
	public void setId(Long id)
	{
		this.id = id;
	}
	public String getReceived()
	{
		return received;
	}
	public void setReceived(String received)
	{
		this.received = received;
	}
}
