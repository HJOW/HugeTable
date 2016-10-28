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

import hjow.hgtable.jscript.module.AbstractModule;

/**
 * <p>Thread 클래스의 기능을 확장한 클래스입니다.</p>
 * 
 * @author HJOW
 *
 */
public class HThread extends Thread
{
	protected String description = "";
	
	/**
	 * Allocates a new Thread object. This constructor has the same effect as Thread(null, null, gname), where gname is a newly generated name. Automatically generated names are of the form "Thread-"+n, where n is an integer. 
	 */
	public HThread()
	{
		super();
	}
	/**
	 * Allocates a new Thread object. This constructor has the same effect as Thread(null, target, gname), where gname is a newly generated name. Automatically generated names are of the form "Thread-"+n, where n is an integer. 
	 * 
	 * @param runnable : the object whose run method is called.
	 */
	public HThread(Runnable runnable)
	{
		super(runnable);
		if(runnable instanceof AbstractModule) setName("Module - " + ((AbstractModule) runnable).getName());
	}
	
	@Override
	public void run()
	{
		super.run();
	}
	
	/**
	 * Forces the thread to stop executing. 
     * 
	 * If there is a security manager installed, its checkAccess method is called with this as its argument. This may result in a SecurityException being raised (in the current thread). 
	 * 	
	 * If this thread is different from the current thread (that is, the current thread is trying to stop a thread other than itself), the security manager's checkPermission method (with a RuntimePermission("stopThread") argument) is called in addition. Again, this may result in throwing a SecurityException (in the current thread). 
	 * 	
	 * The thread represented by this thread is forced to stop whatever it is doing abnormally and to throw a newly created ThreadDeath object as an exception. 
	 * 	
	 * It is permitted to stop a thread that has not yet been started. If the thread is eventually started, it immediately terminates. 
	 * 	
	 * An application should not normally try to catch ThreadDeath unless it must do some extraordinary cleanup operation (note that the throwing of ThreadDeath causes finally clauses of try statements to be executed before the thread officially dies). If a catch clause catches a ThreadDeath object, it is important to rethrow the object so that the thread actually dies. 
	 * 	
	 * The top-level error handler that reacts to otherwise uncaught exceptions does not print out a message or otherwise notify the application if the uncaught exception is an instance of ThreadDeath. 
	 * 
	 */
	@SuppressWarnings("deprecation")
	public void stopThread()
	{
		super.stop();
	}
	public String getDescription()
	{
		return description;
	}
	public void setDescription(String description)
	{
		this.description = description;
	}
}
