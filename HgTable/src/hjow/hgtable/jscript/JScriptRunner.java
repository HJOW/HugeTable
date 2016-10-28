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

import java.util.List;
import java.util.Vector;
import java.util.concurrent.Executors;

import hjow.hgtable.HasManyServices;
import hjow.hgtable.IncludesException;
import hjow.hgtable.ui.AccessInfo;
import hjow.hgtable.ui.NeedtoEnd;

/**
 * <p>스크립트 생성 모듈 클래스입니다.</p>
 * 
 * @author HJOW
 *
 */
public abstract class JScriptRunner extends HasManyServices implements NeedtoEnd
{
	protected List<AccessInfo> priv_accessDBList = null;
	protected List<String> priv_allowedWriteFilePath = null;
	protected List<String> priv_allowedReadFilePath = null;
	protected boolean priv_allowCommand = true;
	protected boolean priv_allowThread = true;
	protected boolean priv_allowLoadClass = true;
	protected boolean priv_allowNetwork = true;
	protected boolean defaultErrorSimplicityOption = true;
	protected List<JScriptObject> objectList = new Vector<JScriptObject>();
	protected String engineType = "";
	
	/**
	 * 재정의를 위한 생성자입니다. 쓰레드풀 정리 쓰레드 실행에 필요합니다.
	 */
	public JScriptRunner()
	{
		initServiceCleaner();
	}
	
	/**
	 * <p>스크립트를 실행한 결과를 반환합니다.</p>
	 * 
	 * @param script : 실행할 스크립트
	 * @param simplifyError : 오류 메시지 간소화 여부
	 * @return 실행 결과 반환된 객체
	 * @throws Throwable : 스크립트 상 오류
	 */
	public Object execute(String script, boolean simplifyError) throws Throwable
	{
		return execute(script, simplifyError, false);
	}
	
	/**
	 * <p>스크립트를 실행한 결과를 반환합니다.</p>
	 * 
	 * @param script : 실행할 스크립트
	 * @return 실행 결과 반환된 객체
	 * @throws Exception : 스크립트 상 오류
	 */
	public Object execute(String script) throws Throwable
	{
		return execute(script, false);
	}
	
	/**
	 * <p>스크립트를 실행한 결과를 반환합니다.</p>
	 * 
	 * @param script : 실행할 스크립트
	 * @param simplifyError : 오류 메시지 간소화 여부
	 * @param notTraceHere : 오류, 예외 발생 시 로그를 출력하지 않고 상위 메소드로 던질 지 여부
	 * @return 실행 결과 반환된 객체
	 * @throws Throwable : 스크립트 상 오류
	 */
	public Object execute(final String script, final boolean simplifyError, final boolean notTraceHere) throws Throwable
	{
		try
		{
			Object results = executeOnThread(script, simplifyError, notTraceHere);
			futures.remove(this);
			return results;
		}
		catch(Throwable t)
		{
			throw new IncludesException(t);
		}
	}
	
	/**
	 * execute 메소드를 호출하면 이 메소드가 별도의 쓰레드에서 실행됩니다.
	 * 
	 * @param script : 실행할 스크립트
	 * @param simplifyError : 오류 메시지 간소화 여부
	 * @param notTraceHere : 오류, 예외 발생 시 로그를 출력하지 않고 상위 메소드로 던질 지 여부
	 * @return 실행 결과 반환된 객체
	 * @throws Throwable : 스크립트 상 오류
	 */
	public abstract Object executeOnThread(String script, boolean simplifyError, boolean notTraceHere) throws Throwable;
	
	/**
	 * <p>스크립트 엔진 내에 객체를 삽입합니다. 삽입된 객체는 id값을 변수처럼 사용해 객체에 액세스할 수 있습니다.</p>
	 * 
	 * @param id : 스크립트 내에서 쓸 변수 이름
	 * @param ob : 객체
	 */
	public void put(String id, Object ob)
	{
		if(ob instanceof JScriptObject) objectList.add((JScriptObject) ob);
	}
	
	/**
	 * <p>이 메소드는 프로그램이 종료될 때 실행됩니다. 순환 참조가 제거됩니다.</p>
	 */
	public void close()
	{
		closeServiceCleaner();
		if(objectList != null)
		{
			for(int i=0; i<objectList.size(); i++)
			{
				try
				{
					objectList.get(i).noMoreUse();
				}
				catch(Exception e)
				{
					
				}
			}
		}
		try
		{
			if(service != null) service.shutdown();
		}
		catch(Exception e)
		{
			
		}
		try
		{
			if(futures != null) futures.clear();
		}
		catch(Exception e)
		{
			
		}
		
		service = null;
		futures = null;
	}
	
	/**
	 * 이 스크립트 엔진에서 실행 중인 쓰레드풀 작업을 모두 종료합니다.
	 */
	public void stopOnPool()
	{
		service.shutdown();
		service = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
	}
	
	/**
	 * 이 스크립트 엔진에서 실행 중인 요청을 모두 취소합니다.
	 */
	public void cancelAll()
	{
		for(int i=0; i<futures.size(); i++)
		{
			futures.get(i).cancel(true);
		}
	}
	
	@Override
	public boolean isAlive()
	{
		return cleanThreadSwitch;
	}
	
	@Override
	public void noMoreUse()
	{
		close();
	}
	
	public List<AccessInfo> getPriv_accessDBList()
	{
		List<AccessInfo> newList = new Vector<AccessInfo>();
		for(int i=0; i<priv_accessDBList.size(); i++)
		{
			newList.add(priv_accessDBList.get(i));
		}
		return newList;
	}

	public boolean isPriv_allowCommand()
	{
		return priv_allowCommand;
	}
	public List<AccessInfo> getPriv_accessDBList(NeedAuthorize daemon)
	{
		if(daemon.isAuthorize(this))
		{
			return priv_accessDBList;
		}
		return null;
	}
	public void initPriv_accessDBList(NeedAuthorize daemon)
	{
		if(daemon.isAuthorize(this))
		{
			priv_accessDBList = new Vector<AccessInfo>();
		}
	}
	public void initPriv_allowedReadFilePath(NeedAuthorize daemon)
	{
		if(daemon.isAuthorize(this))
		{
			priv_allowedReadFilePath = new Vector<String>();
		}
	}
	public void initPriv_allowedWriteFilePath(NeedAuthorize daemon)
	{
		if(daemon.isAuthorize(this))
		{
			priv_allowedWriteFilePath = new Vector<String>();
		}
	}
	public void allowAllPriv_accessDBList(NeedAuthorize daemon)
	{
		if(daemon.isAuthorize(this))
		{
			priv_accessDBList = null;
		}
	}
	public void allowAllPriv_allowedReadFilePath(NeedAuthorize daemon)
	{
		if(daemon.isAuthorize(this))
		{
			priv_allowedReadFilePath = null;
		}
	}
	public void allowAllPriv_allowedWriteFilePath(NeedAuthorize daemon)
	{
		if(daemon.isAuthorize(this))
		{
			priv_allowedWriteFilePath = null;
		}
	}

	public List<String> getPriv_allowedReadFilePath(NeedAuthorize daemon)
	{
		if(daemon.isAuthorize(this))
		{
			return priv_allowedReadFilePath;
		}
		return null;
	}
	public List<String> getPriv_allowedWriteFilePath(NeedAuthorize daemon)
	{
		if(daemon.isAuthorize(this))
		{
			return priv_allowedWriteFilePath;
		}
		return null;
	}
	public List<String> getPriv_allowedWriteFilePath()
	{
		List<String> newList = new Vector<String>();
		for(int i=0; i<priv_allowedWriteFilePath.size(); i++)
		{
			newList.add(new String(priv_allowedWriteFilePath.get(i)));
		}
		return newList;
	}

	public List<String> getPriv_allowedReadFilePath()
	{
		List<String> newList = new Vector<String>();
		for(int i=0; i<priv_allowedReadFilePath.size(); i++)
		{
			newList.add(new String(priv_allowedReadFilePath.get(i)));
		}
		return newList;
	}

	public boolean isPriv_allowThread()
	{
		return priv_allowThread;
	}
	public void setPriv_allowThread(NeedAuthorize daemon, boolean value)
	{
		if(daemon.isAuthorize(this))
		{
			priv_allowThread = value;
		}
	}
	public void setPriv_allowLoadClass(NeedAuthorize daemon, boolean value)
	{
		if(daemon.isAuthorize(this))
		{
			priv_allowLoadClass = value;
		}
	}
	public void setPriv_allowCommand(NeedAuthorize daemon, boolean value)
	{
		if(daemon.isAuthorize(this))
		{
			priv_allowCommand = value;
		}
	}
	public void setPriv_allowNetwork(NeedAuthorize daemon, boolean value)
	{
		if(daemon.isAuthorize(this))
		{
			priv_allowNetwork = value;
		}
	}

	public boolean isPriv_allowLoadClass()
	{
		return priv_allowLoadClass;
	}
	
	public boolean isPriv_allowNetwork()
	{
		return priv_allowNetwork;
	}
	
	/**
	 * <p>엔진 생성 시 사용된 이름을 반환합니다.</p>
	 * 
	 * @return 스크립트 엔진 종류
	 */
	public String getEngineType()
	{
		return engineType;
	}
	
	/**
	 * 특정 작업을 취소합니다.
	 * 
	 * @param workHash : 작업 코드
	 */
	public void cancel(int workHash)
	{
		for(int i=0; i<futures.size(); i++)
		{
			if(workHash == futures.get(i).hashCode()) futures.get(i).cancel(true);
		}
	}
	
	/**
	 * 실행 중인 작업들을 지칭하는 작업 코드들을 반환합니다. 이 작업 코드를 통해 특정 작업을 컨트롤할 수 있습니다.
	 * 
	 * @return 작업 코드들
	 */
	public int[] getWorks()
	{
		int[] results = new int[futures.size()];
		for(int i=0; i<futures.size(); i++)
		{
			results[i] = futures.get(i).hashCode();
		}
		return results;
	}

	public boolean isDefaultErrorSimplicityOption()
	{
		return defaultErrorSimplicityOption;
	}

	public void setDefaultErrorSimplicityOption(boolean defaultErrorSimplicityOption)
	{
		this.defaultErrorSimplicityOption = defaultErrorSimplicityOption;
	}
}