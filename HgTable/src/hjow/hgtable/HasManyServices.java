package hjow.hgtable;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import hjow.hgtable.ui.NeedtoEnd;

/**
 * 다수의 쓰레드 작업을 할 때 이 클래스를 확장해 사용합니다.
 * 
 * @author HJOW
 *
 */
public abstract class HasManyServices implements NeedtoEnd
{
	protected static int THREAD_POOL_SIZE = 1;
	protected transient boolean cleanThreadSwitch = true;
	protected Map<String, Object> attributes = new Hashtable<String, Object>();
	protected transient ExecutorService service = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
	protected transient List<Future<Object>> futures = new Vector<Future<Object>>();
	protected transient Runnable cleaner = new Runnable()
	{
		@Override
		public void run()
		{
			while(cleanThreadSwitch)
			{
				cleanThreadPool();
				try
				{
					Thread.sleep((long) (Math.random() * 1000));
				}
				catch(Exception e)
				{
					
				}
			}
		}
	};
	
	/**
	 * 쓰레드 풀 크기를 지정합니다. 서비스 청소기를 초기화하기 전에 호출해야 합니다.
	 * 
	 * @param size : 지정할 크기
	 * @throws IllegalAccessException : 이미 서비스 청소기가 작동 중인 경우
	 */
	public void setThreadPoolSize(int size) throws IllegalAccessException
	{
		if(isAlive()) throw new IllegalAccessException(Manager.applyStringTable("Service cleaner is alive. Cannot set thread pool size."));
		THREAD_POOL_SIZE = size;
	}
	
	/**
	 * 서비스 청소기를 초기화하고 작업을 시작합니다.
	 */
	protected void initServiceCleaner() 
	{
		HThread clearThread = new HThread(cleaner);
		cleanThreadSwitch = true;
		clearThread.start();
	}
	
	/**
	 * 쓰레드 풀을 정리합니다.
	 */
	public void cleanThreadPool()
	{
		int i=0;
		while(i < futures.size())
		{
			if(futures.get(i).isDone() || futures.get(i).isCancelled())
			{
				futures.remove(i);
				i = 0;
				continue;
			}
			i++;
		}
		if(service.isTerminated())
		{
			service = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
		}
	}
	
	public Map<String, Object> getAttributes()
	{
		return attributes;
	}

	public void setAttributes(Map<String, Object> attributes)
	{
		this.attributes = attributes;
	}
	
	/**
	 * 서비스 청소기를 끕니다.
	 */
	protected void closeServiceCleaner() 
	{
		cleanThreadSwitch = false;
	}
	
	/**
	 * 서비스 청소기를 끄고, 작업 중인 서비스들 모두 작업을 취소합니다.
	 */
	protected void closeAllServices() 
	{
		closeServiceCleaner();
		if(futures == null) return;
		for(int i=0; i<futures.size(); i++)
		{
			try
			{
				futures.get(i).cancel(true);
			}
			catch(Throwable t)
			{
				
			}
		}
		futures.clear();
	}

	@Override
	public void noMoreUse()
	{
		closeAllServices();
	}

	@Override
	public boolean isAlive()
	{
		return cleanThreadSwitch;
	}
}
