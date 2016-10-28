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

package hjow.hgtable.util;

import hjow.hgtable.Main;
import hjow.hgtable.Manager;
import hjow.hgtable.ui.GUIManager;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.util.List;

/**
 * <p>콘솔 모드에서 사용하는 여러 정적 메소드들이 있는 클래스입니다.</p>
 * 
 * @author HJOW
 *
 */
public class ConsoleUtil
{
	/**
	 * <p>콘솔 화면을 백지화합니다. 실제로 내용을 지우는 것은 불가능한 경우가 많으므로, 일반적으로 다수의 "빈 줄"을 출력하는 방식으로 동작합니다.</p>
	 * 
	 * @param manager : 매니저 객체
	 */
	public static void clearConsole(Manager manager)
	{
		String cleanOption = Manager.getOption("console_clean_method");
		int howMany = 100;
		
		if(manager instanceof GUIManager)
		{
			((GUIManager) manager).clearResultArea();
			return;
		}
		
		if(cleanOption == null)
		{
			for(int i=0; i<howMany; i++)
			{
				manager.log("");
			}
		}
		else
		{
			try
			{
				howMany = Integer.parseInt(cleanOption);
				for(int i=0; i<howMany; i++)
				{
					manager.log("");
				}
			}
			catch(NumberFormatException e)
			{
				try
				{
					Runtime.getRuntime().exec(cleanOption);
				}
				catch (IOException e1)
				{
					
				}
			}
		}
	}
	
	/**
	 * <p>메모리 현황 정보를 반환합니다.</p>
	 * 
	 * @param basicData : 기본 정보 반환 여부를 지정합니다.
	 * @param detailData : 추가 정보 반환 여부를 지정합니다.
	 * @return 메모리 현황 메시지 정보
	 */
	public static String memoryData(boolean basicData, boolean detailData)
	{
		StringBuffer results = new StringBuffer("");
		long total, max, free;
		total = Runtime.getRuntime().totalMemory();
		max = Runtime.getRuntime().maxMemory();
		free = Runtime.getRuntime().freeMemory();
		
		if(basicData)
		{
			results = results.append(Manager.applyStringTable("Total memory") + " : " + DataUtil.toByteUnit(total) + "\n");
			results = results.append(Manager.applyStringTable("Max memory") + " : " + DataUtil.toByteUnit(max) + "\n");
			results = results.append(Manager.applyStringTable("Free memory") + " : " + DataUtil.toByteUnit(free) + "\n");
			results = results.append(Manager.applyStringTable("Usage") + " : " + String.format("%.2f", (float) ((total - free) / ((double) total)) * 100.0) + " %" + "\n");
		}
		
		if(detailData)
		{
			if(basicData) results = results.append("\n");
			try
			{
				MemoryMXBean memoryMxBean = ManagementFactory.getMemoryMXBean();
				List<MemoryPoolMXBean> memoryPoolMxBeans = ManagementFactory.getMemoryPoolMXBeans();
				
				MemoryUsage heaps = memoryMxBean.getHeapMemoryUsage();
				MemoryUsage nonHeap = memoryMxBean.getNonHeapMemoryUsage();
				
				results = results.append(toMemoryInfo(heaps, Manager.applyStringTable("Heap Space")));
				results = results.append("\n");
				results = results.append(toMemoryInfo(nonHeap, Manager.applyStringTable("Non - Heap Space")));
				results = results.append("\n");
				
				for(int i=0; i<memoryPoolMxBeans.size(); i++)
				{
					try
					{
						MemoryPoolMXBean poolBean = memoryPoolMxBeans.get(i);
						
						if(poolBean.getUsage() != null)
						{
							results = results.append(toMemoryInfo(poolBean.getUsage(), Manager.applyStringTable(poolBean.getName()) + " : " + Manager.applyStringTable("Usage")));
							results = results.append("\n");
						}
						if(poolBean.getCollectionUsage() != null)
						{
							results = results.append(toMemoryInfo(poolBean.getCollectionUsage(), Manager.applyStringTable(poolBean.getName()) + " : " + Manager.applyStringTable("Collection")));
							results = results.append("\n");
						}
						if(poolBean.getPeakUsage() != null)
						{
							results = results.append(toMemoryInfo(poolBean.getPeakUsage(), Manager.applyStringTable(poolBean.getName()) + " : " + Manager.applyStringTable("Peak")));
						}
					}
					catch(Throwable e)
					{
						Main.logError(e, Manager.applyStringTable("On detailed memory"));
					}
					
					if(i < memoryPoolMxBeans.size() - 1)
					{
						results = results.append("\n");
					}
				}
			}
			catch(Throwable e)
			{
				Main.logError(e, Manager.applyStringTable("On detailed memory"), true);
			}
		}
		
		return results.toString();
	}
	
	/**
	 * <p>메모리 사용량 정보 객체로부터 메모리 사용량 메시지를 만들어 반환합니다.</p>
	 * 
	 * @param usages : 메모리 사용량 객체
	 * @param wheres : 메모리 영역 이름
	 * @return 메모리 사용량 정보 메시지
	 */
	public static String toMemoryInfo(MemoryUsage usages, String wheres)
	{
		StringBuffer results = new StringBuffer("");
		
		results = results.append(wheres + "\n");
		results = results.append(Manager.applyStringTable("Init") + " : " + DataUtil.toByteUnit(usages.getInit()) + "\n");
		results = results.append(Manager.applyStringTable("Used") + " : " + DataUtil.toByteUnit(usages.getUsed()) + "\n");
		results = results.append(Manager.applyStringTable("Committed") + " : " + DataUtil.toByteUnit(usages.getCommitted()) + "\n");
		results = results.append(Manager.applyStringTable("Max") + " : " + DataUtil.toByteUnit(usages.getMax()) + "\n");
		
		return results.toString();
	}
}
