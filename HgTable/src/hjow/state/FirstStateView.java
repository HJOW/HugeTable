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
package hjow.state;

import java.util.Date;
import java.util.List;
import java.util.Vector;

import hjow.hgtable.HThread;
import hjow.hgtable.ui.NeedtoEnd;
import hjow.hgtable.util.Entry;

/**
 * <p>실행 시 프로세스 생존 여부를 사용자에게 알려주는 역할을 합니다.</p>
 * 
 * @author HJOW
 *
 */
public class FirstStateView implements Runnable, NeedtoEnd
{
	private static FirstStateView viewObj = null;
	protected boolean threadSwitch = true;
	protected int numbers = 0;
	protected boolean increase = false;
	protected char symbols = ':';
	protected String msg = "";
	protected List<Entry<Date, String>> records = new Vector<Entry<Date,String>>();
	
	/**
	 * <p>생성자입니다. 외부에 노출되지 않습니다.</p>
	 */
	protected FirstStateView()
	{
		init();
		new HThread(this).start();
	}
	
	/**
	 * <p>객체를 초기화합니다.</p>
	 */
	protected void init()
	{
		
	}
	
	/**
	 * <p>상태를 표시합니다.</p>
	 */
	protected void refresh()
	{
		System.out.print("\r");
		for(int i=0; i<(numbers); i++)
		{
			System.out.print(String.valueOf(symbols));
		}
	}
	
	@Override
	public void noMoreUse()
	{
		
	}

	@Override
	public void run()
	{
		while(threadSwitch)
		{
			try
			{
				if(! increase) break;
				
				refresh();
				
				numbers++;
				if(numbers > 100) numbers = 0;
			}
			catch(Exception e)
			{
				
			}
			try
			{
				Thread.sleep(200);
			}
			catch(Exception e)
			{
				
			}
		}
		
		if(increase)
		{
			System.gc();
			noMoreUse();
		}
	}
	
	/**
	 * <p>이 메소드는 프로그램이 처음 실행될 때 호출됩니다.</p>
	 */
	public static void on(boolean gui)
	{
		if(gui) viewObj = new AWTFirstStateView();
		else viewObj = new FirstStateView();
		viewObj.records.add(new Entry<Date, String>(new Date(), "Start"));
	}
	
	/**
	 * <p>이 메소드는 프로그램이 완전히 준비되었을 때 호출됩니다.</p>
	 */
	public static void off()
	{
		if(viewObj == null) return;
		viewObj.threadSwitch = false;
		
		if(! viewObj.increase)
		{
			viewObj.noMoreUse();
			System.gc();
		}
	}
	
	/**
	 * <p>현재 얼마나 초기화가 진행되었는지를 지정합니다.</p>
	 * 
	 * @param v : 숫자
	 */
	public static void set(int v)
	{
		if(viewObj == null) return;
		if(! viewObj.threadSwitch) return;
		if(! viewObj.increase)
		{
			viewObj.numbers = v;
			if(viewObj.numbers > 100) viewObj.numbers = 100;
			viewObj.refresh();
		}
	}
	
	/**
	 * <p>상태뷰가 텍스트 출력을 지원하는 경우, 보일 텍스트를 변경합니다.</p>
	 * 
	 * @param txt : 화면에 보일 텍스트
	 */
	public static synchronized void text(String txt)
	{
		if(viewObj == null) return;
		viewObj.msg = txt;
		viewObj.records.add(new Entry<Date, String>(new Date(), txt));
		viewObj.refresh();
	}

	@Override
	public boolean isAlive()
	{
		return threadSwitch;
	}
	
	/**
	 * <p>각 로딩 내역이 얼마나 시간이 걸렸는지 내역을 리스트로 반환합니다. 걸린 시간에 기반합니다.</p>
	 * 
	 * @return 로딩 내역
	 */
	protected List<Entry<String, Long>> getReportList()
	{
		List<Entry<String, Long>> report = new Vector<Entry<String,Long>>();
		long beforeTime = -1;
		String beforeMsg = null;
		for(Entry<Date, String> reportElement : this.records)
		{
			if(beforeTime >= 0)
			{
				report.add(new Entry<String, Long>(beforeMsg, new Long(reportElement.getKey().getTime() - beforeTime)));
			}
			beforeTime = reportElement.getKey().getTime();
			beforeMsg = reportElement.getValue();
		}
		if(beforeMsg != null)
		{
			report.add(new Entry<String, Long>(beforeMsg, new Long(0)));
		}
		return report;
	}
	
	/**
	 * <p>각 로딩 내역이 얼마나 시간이 걸렸는지 내역을 메시지로 반환합니다.</p>
	 * 
	 * @return 로딩 내역
	 */
	protected String getReport()
	{
		StringBuffer result = new StringBuffer("");
		
		List<Entry<String, Long>> report = getReportList();
		for(Entry<String, Long> element : report)
		{
			result = result.append("\n").append(String.format("%.3f", element.getValue() / 1000.0) + " seconds consumed : " + element.getKey());
		}
		
		return result.toString().trim();
	}
	
	/**
	 * <p>각 로딩 내역이 얼마나 시간이 걸렸는지 내역을 메시지로 반환합니다.</p>
	 * 
	 * @return 로딩 내역
	 */
	public static String report()
	{
		return viewObj.getReport();
	}
	
	/**
	 * <p>리포트를 정리합니다.</p>
	 */
	protected void clean()
	{
		records.clear();
	}
	
	/**
	 * <p>리포트를 정리합니다.</p>
	 */
	public static void cleanReports()
	{
		viewObj.clean();
	}
}
