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

package hjow.hgtable.jscript.module;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import hjow.hgtable.HThread;
import hjow.hgtable.Main;
import hjow.hgtable.Manager;
import hjow.hgtable.stringtable.DefaultStringTable;
import hjow.hgtable.stringtable.StringTable;
import hjow.hgtable.ui.module.ModuleDataPack;
import hjow.hgtable.util.DataUtil;
import hjow.hgtable.util.StreamUtil;

/**
 * <p>모듈 객체입니다.</p>
 * 
 * @author HJOW
 *
 */
public abstract class Module implements AbstractModule
{
	private static final long serialVersionUID = -6926833683674434030L;
	protected long moduleId = new Random().nextLong();
	protected String name = "";
	protected long threadGap = 100;
	protected Map<String, String> options = new Hashtable<String, String>();
	protected StringTable standaloneStringTable = new DefaultStringTable();
	protected String license = "";
	protected transient boolean threadSwitch = false;
	protected transient boolean isInitialized = false;
	protected transient boolean isAfterInitDid = false;
	
	protected transient Manager manager = null;
	
	/**
	 * <p>매니저 객체를 지정합니다.</p>
	 * 
	 * @param manager : 매니저 객체
	 */
	public void setManager(Manager manager)
	{
		this.manager = manager;
	}
	
	@Override
	public void input(ModuleDataPack packs)
	{
		
	}
	
	@Override
	public void refresh(Map<String, Object> additionalData)
	{
		
	}
	
	@Override
	public String getName()
	{
		return name;
	}

	/**
	 * <p>모듈 이름을 지정합니다.</p>
	 * 
	 * @param name : 새 모듈 이름
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
	@Override
	public boolean isPriv_allowCommand()
	{
		return false;
	}
	
	@Override
	public boolean isPriv_allowLoadClass()
	{
		return false;
	}
	
	@Override
	public boolean isPriv_allowThread()
	{
		return false;
	}
	
	@Override
	public List<String> getPriv_allowReadFilePath()
	{
		return new Vector<String>();
	}
	
	@Override
	public List<String> getPriv_allowWriteFilePath()
	{
		return new Vector<String>();
	}
	
	@Override
	public void noMoreUse()
	{
		threadSwitch = false;
		saveOptions();
		manager = null;
	}
	
	/**
	 * <p>옵션들을 파일로 저장합니다. 파일 이름은 modulecfg_<i>모듈ID</i>.cfgmap 이며, xml 형식을 가집니다. 옵션값이 아무것도 없으면 저장 동작을 생략합니다.</p>
	 */
	protected void saveOptions()
	{
		if(getOptions() == null || getOptions().isEmpty()) return;
		try
		{
			File cfg = new File(Manager.getOption("module_config_path") + "modulecfg_" + getModuleId() + ".cfgmap");
			StreamUtil.saveMap(cfg, getOptions(), false);
		}
		catch(Throwable t)
		{
			onFailToSaveConfig(t);
		}
	}
	
	/**
	 * <p>옵션들을 파일로부터 불러옵니다. 파일 이름은 modulecfg_<i>모듈ID</i>.cfgmap 이며, xml 형식을 가집니다.</p>
	 */
	protected void readOptions()
	{
		try
		{
			File cfg = new File(Manager.getOption("module_config_path") + "modulecfg_" + getModuleId() + ".cfgmap");
			if(cfg.exists())
			{
				Map<String, ?> reads = StreamUtil.readMap(cfg, false);
				if(reads != null)
				{
					Set<String> keys = reads.keySet();
					for(String k : keys)
					{
						getOptions().put(k, String.valueOf(reads.get(k)));
					}
				}
			}
			else throw new FileNotFoundException(trans("Cannot find module config file"));
		}
		catch(Throwable t)
		{
			onFailToReadConfig(t);
		}
	}
	
	/**
	 * <p>모듈 설정 저장에 실패했을 때 호출됩니다.</p>
	 * 
	 * @param t : 예외 객체
	 */
	protected void onFailToSaveConfig(Throwable t)
	{
		
	}
	
	/**
	 * <p>모듈 설정 불러오기에 실패했을 때 호출됩니다. 파일이 존재하지 않을 때에도 호출될 수 있습니다.</p>
	 * 
	 * @param t : 예외 객체
	 */
	protected void onFailToReadConfig(Throwable t)
	{
		
	}
	
	@Override
	public boolean needThread()
	{
		return false;
	}
	
	@Override
	public void startThread()
	{
		threadSwitch = true;
		if(getThreadGap() == 0) setThreadGap(100);
		new HThread(this).start();
	}
	
	/**
	 * <p>이 메소드는 쓰레드 내에서 실행됩니다.</p>
	 * 
	 * @exception Exception 이 예외 처리는 쓰레드 동작을 멈추지 않기 위한 것입니다. try - catch 문을 사용하는 것을 권장합니다.
	 */
	protected void onThread() throws Exception
	{
		
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
			catch(Exception e)
			{
				
			}
			
			try
			{
				if(! Main.checkInterrupt(this, "On module thread")) break;
				Thread.sleep(threadGap);
			}
			catch(Exception e)
			{
				
			}
		}
	}

	/**
	 * <p>쓰레드가 동작 중인지의 여부를 반환합니다.</p>
	 * 
	 * @return 쓰레드 동작 여부
	 */
	public boolean isThreadSwitch()
	{
		return threadSwitch;
	}

	/**
	 * <p>쓰레드 동작 주기를 반환합니다. 밀리초 단위입니다.</p>
	 * 
	 * @return 쓰레드 동작 주기
	 */
	public long getThreadGap()
	{
		return threadGap;
	}

	/**
	 * <p>쓰레드 동작 주기를 지정합니다. 10 이상이어야 합니다.</p>
	 * 
	 * @param threadGap : 쓰레드 동작 주기
	 */
	public void setThreadGap(long threadGap)
	{
		this.threadGap = threadGap;
		if(threadGap < 10) this.threadGap = 10;
	}
	
	@Override
	public String description()
	{
		return "";
	}
	
	@Override
	public synchronized void init()
	{
		if(isInitialized) return;
		readOptions();
		initializeComponents();
		isInitialized = true;
	}
	
	@Override
	public void doAfterInit()
	{
		if(isAfterInitDid) return;
		doAfterInitialize();
		isAfterInitDid = true;
		
		if(needThread())
		{
			startThread();
		}
	}
	
	@Override
	public void initializeComponents()
	{
		
	}
	
	@Override
	public void doAfterInitialize()
	{
		
	}

	@Override
	public Map<String, String> getOptions()
	{
		return options;
	}

	@Override
	public void setOptions(Map<String, String> options)
	{
		this.options = options;
	}

	@Override
	public long getModuleId()
	{
		return moduleId;
	}

	public void setModuleId(long moduleId)
	{
		this.moduleId = moduleId;
	}

	@Override
	public String getLicense()
	{
		return license;
	}

	public void setLicense(String license)
	{
		this.license = license;
	}

	public StringTable getStandaloneStringTable()
	{
		return standaloneStringTable;
	}

	public void setStandaloneStringTable(StringTable standaloneStringTable)
	{
		this.standaloneStringTable = standaloneStringTable;
	}

	@Override
	public boolean isAlive()
	{
		return true;
	}
	
	@Override
	public String trans(String text)
	{
		String results = standaloneStringTable.get(text);
		if(DataUtil.isEmpty(results)) return Manager.applyStringTable(text);
		return results;
	}
	
	@Override
	public final void manage()
	{
		manage(null);
	}
	
	/**
	 * <p>사용자가 모듈을 직접 실행할 경우 호출되는 메소드입니다.</p>
	 * 
	 */
	@Override
	public void manage(Map<String, String> args)
	{
		
	}
}
