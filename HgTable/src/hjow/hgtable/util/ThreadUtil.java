package hjow.hgtable.util;

import java.util.Map;

import hjow.hgtable.HThread;
import hjow.hgtable.ParameterRunnable;

/**
 * 간단한 병렬 작업을 위한 정적 메소드들이 있습니다.
 * 
 * @author HJOW
 *
 */
public class ThreadUtil
{
	public static HThread run(Runnable runnable)
	{
		HThread newThread = new HThread(runnable);
		newThread.start();
		return newThread;
	}
	public static HThread run(final ParameterRunnable runnable, final Map<String, Object> parameters)
	{
		HThread newThread = new HThread(new Runnable()
		{
			@Override
			public void run()
			{
				runnable.run(parameters);
			}
		});
		newThread.start();
		return newThread;
	}
}
