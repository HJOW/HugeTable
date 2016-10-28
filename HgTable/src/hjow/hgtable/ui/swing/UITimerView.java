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
package hjow.hgtable.ui.swing;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import hjow.hgtable.HThread;
import hjow.hgtable.Main;
import hjow.hgtable.ui.NeedtoEnd;

/**
 * <p>시간을 보여주는 컴포넌트입니다.</p>
 * 
 * @author HJOW
 *
 */
public class UITimerView extends JPanel implements NeedtoEnd, Runnable
{
	private static final long serialVersionUID = -6980107937710543942L;
	
	public static final int CLOCK = 0;
	public static final int TIMER = 1;
	
	protected boolean threadSwitch = true;
	protected int size = 25;
	protected volatile int mode = CLOCK;
	protected long gap = 50;
	protected long forcedGap = 25;
	protected double randomGap = 1.0;
	protected JTextField timeField;
	protected volatile boolean paused = false;
	protected volatile boolean timerStarted = false;
	protected volatile boolean forced = false;
	protected JButton btChangeMode;
	protected JButton btAct;
	protected JPanel controlPanel;
	protected transient Calendar cal = Calendar.getInstance();
	protected transient Calendar beforeCal;
	protected transient int year, month, date, hour, minute, second, millisecond;
	
	/**
	 * <p>컴포넌트를 초기화합니다.</p>
	 */
	public UITimerView()
	{
		initComponent();
//		new HThread(this).start();
	}
	
	/**
	 * <p>컴포넌트를 초기화합니다.</p>
	 * 
	 * @param size : 컴포넌트 크기
	 */
	public UITimerView(int size)
	{
		this.size = size;
		initComponent();
//		new HThread(this).start();
	}
	
	/**
	 * <p>컴포넌트를 초기화합니다.</p>
	 * 
	 */
	protected void initComponent()
	{
		this.setLayout(new BorderLayout());
		
		timeField = new JTextField(size);
		timeField.setEditable(false);
		this.add(timeField, BorderLayout.CENTER);
		
		controlPanel = new JPanel();
		controlPanel.setLayout(new GridLayout(1, 2));
		
		btChangeMode = new JButton("/");
		btAct = new JButton("■");
		
		btAct.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				act();
			}
		});
		btChangeMode.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if(mode == TIMER)
				{
					mode = CLOCK;
					pauseBreak();
				}
				else
				{
					mode = TIMER;
					timerStarted = false;
				}
				
				refresh();
			}
		});
		
		this.add(controlPanel, BorderLayout.EAST);
		controlPanel.add(btAct);
		controlPanel.add(btChangeMode);
	}
	
	/**
	 * <p>작동을 시작합니다.</p>
	 */
	public void start()
	{
		new HThread(this).start();
	}
	
	/**
	 * <p>일시정지를 걸거나 풉니다.</p>
	 * 
	 */
	public void act()
	{
		if(mode == TIMER)
		{
			if(timerStarted)
			{
				if(paused)
				{
					timerStarted = false;
				}
				else paused = true;
			}
			else
			{
				paused = false;
				beforeCal = Calendar.getInstance();
				timerStarted = true;
			}
		}
		else
		{
			paused = ! paused;
		}
		refresh();
	}
	
	/**
	 * <p>일시 정지합니다.</p>
	 */
	public void pause()
	{
		paused = true;
	}
	
	/**
	 * <p>일시 정지를 풉니다.</p>
	 */
	public void pauseBreak()
	{
		paused = false;
	}
	
	/**
	 * <p>화면 상에 보일 내용을 다시 작성합니다.</p>
	 * 
	 */
	protected void refresh()
	{
		cal = Calendar.getInstance();
		StringBuffer fieldText = new StringBuffer("");
		switch(mode)
		{
		case TIMER:
			fieldText = fieldText.append("T ");
			if(timerStarted)
			{
				year = cal.get(Calendar.YEAR) - beforeCal.get(Calendar.YEAR);
				month = cal.get(Calendar.MONTH) - beforeCal.get(Calendar.MONTH);
				date = cal.get(Calendar.DAY_OF_MONTH) - beforeCal.get(Calendar.DAY_OF_MONTH);
				hour = cal.get(Calendar.HOUR_OF_DAY) - beforeCal.get(Calendar.HOUR_OF_DAY);
				minute = cal.get(Calendar.MINUTE) - beforeCal.get(Calendar.MINUTE);
				second = cal.get(Calendar.SECOND) - beforeCal.get(Calendar.SECOND);
				millisecond = cal.get(Calendar.MILLISECOND) - beforeCal.get(Calendar.MILLISECOND);
				
				while(millisecond < 0)
				{
					second--;
					millisecond = millisecond + 1000;
				}
				
				while(second < 0)
				{
					minute--;
					second = second + 60;
				}
				
				while(minute < 0)
				{
					hour--;
					minute = minute + 60;
				}
				
				while(hour < 0)
				{
					date--;
					hour = hour + 24;
				}
				
				while(date < 0)
				{
					month--;
					date = date + 30;
				}
				
				while(month < 0)
				{
					year--;
					month = month + 12;
				}
				
				fieldText = fieldText.append(getTimeText(year, month, date, hour, minute, second, millisecond, false));
			}
			else
			{
				fieldText = fieldText.append(getTimeText(0, 0, 0, 0, 0, 0, 0, false));
			}
			break;
		case CLOCK:
			fieldText = fieldText.append("C ");
			fieldText = fieldText.append(getTimeText(cal.get(Calendar.YEAR)
					, cal.get(Calendar.MONTH)
					, cal.get(Calendar.DAY_OF_MONTH) + 1
					, cal.get(Calendar.HOUR_OF_DAY)
					, cal.get(Calendar.MINUTE)
					, cal.get(Calendar.SECOND)
					, cal.get(Calendar.MILLISECOND)
					, true));
			break;
		}
		timeField.setText(fieldText.toString());
	}
	
	/**
	 * <p>날짜, 시간을 텍스트로 변환합니다.</p>
	 * 
	 * @param year : 년
	 * @param month : 월 (0부터 시작)
	 * @param date : 일
	 * @param hour : 시
	 * @param minute : 분
	 * @param second : 초
	 * @param millisecond : 밀리초
	 * @param monthAdd : true 시 월에 1 추가
	 * @return 텍스트 형식
	 */
	public static String getTimeText(int year, int month, int date, int hour, int minute, int second, int millisecond, boolean monthAdd)
	{
		StringBuffer fieldText = new StringBuffer("");
		fieldText = fieldText.append(String.valueOf(year));
		fieldText = fieldText.append(".");
	    if(monthAdd) fieldText = fieldText.append(String.format("%02d", month + 1));
	    else fieldText = fieldText.append(String.format("%02d", month));
		fieldText = fieldText.append(".");
		fieldText = fieldText.append(String.format("%02d", date));
		fieldText = fieldText.append("/");
		fieldText = fieldText.append(String.format("%02d", hour));
		fieldText = fieldText.append(":");
		fieldText = fieldText.append(String.format("%02d", minute));
		fieldText = fieldText.append(":");
		fieldText = fieldText.append(String.format("%02d", second));
		fieldText = fieldText.append(":");
		fieldText = fieldText.append(String.format("%03d", millisecond));
		return fieldText.toString();
	}

	/**
	 * <p>이 메소드는 쓰레드에서 실행됩니다.</p>
	 * 
	 */
	protected void onThread()
	{
		if(paused) return;
		refresh();
	}
	
	/**
	 * <p>강제로 타이머를 동작합니다. 버튼이 비활성화됩니다.</p>
	 * 
	 */
	public void forceTimer()
	{
		btAct.setEnabled(false);
		btChangeMode.setEnabled(false);
		
		beforeCal = Calendar.getInstance();
		mode = TIMER;
		timerStarted = true;
		paused = false;
		forced = true;
		
		refresh();
	}
	
	/**
	 * <p>버튼이 모두 활성화되고, 타이머가 일시 정지됩니다.</p>
	 */
	public void freeTimer()
	{
		paused = true;
		forced = false;
		btAct.setEnabled(true);
		btChangeMode.setEnabled(true);
	}
	
	@Override
	public void noMoreUse()
	{
		threadSwitch = false;
	}

	@Override
	public boolean isAlive()
	{
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
				if(! Main.checkInterrupt(this, "timer")) noMoreUse();
				if(forced) Thread.sleep(forcedGap + (long) Math.round(Math.random() * randomGap));
				else Thread.sleep(gap + (long) Math.round(Math.random() * randomGap));
			}
			catch(Exception e)
			{
				System.out.print(e.getMessage());
				if(e.getStackTrace() != null && e.getStackTrace().length >= 1)
				{
					Main.println(" - " + e.getStackTrace()[0]);
				}
				else
				{
					Main.println();
				}
			}
		}
	}

	public int getMode()
	{
		return mode;
	}

	public void setMode(int mode)
	{
		this.mode = mode;
	}

	public long getGap()
	{
		return gap;
	}

	public void setGap(long gap)
	{
		this.gap = gap;
	}
	
	public void setGap(String gap)
	{
		try
		{
			if(gap == null) return;
			else if(gap.trim().equals("")) return;
			else if(gap.trim().equalsIgnoreCase("very slow")) this.gap = 200;
			else if(gap.trim().equalsIgnoreCase("slow")) this.gap = 100;
			else if(gap.trim().equalsIgnoreCase("normal")) this.gap = 50;
			else if(gap.trim().equalsIgnoreCase("fast")) this.gap = 30;
			else this.gap = Long.parseLong(gap);
		}
		catch(NumberFormatException e)
		{
			Main.println("Cannot parse " + gap + " into the number.");
		}
	}

	public long getForcedGap()
	{
		return forcedGap;
	}

	public void setForcedGap(long forcedGap)
	{
		this.forcedGap = forcedGap;
	}
	public void setForcedGap(String forcedGap)
	{
		try
		{
			if(forcedGap == null) return;
			else if(forcedGap.trim().equals("")) return;
			else if(forcedGap.trim().equalsIgnoreCase("very slow")) this.forcedGap = 200;
			else if(forcedGap.trim().equalsIgnoreCase("slow")) this.forcedGap = 100;
			else if(forcedGap.trim().equalsIgnoreCase("normal")) this.forcedGap = 50;
			else if(forcedGap.trim().equalsIgnoreCase("fast")) this.forcedGap = 30;
			else this.forcedGap = Long.parseLong(forcedGap);
		}
		catch(NumberFormatException e)
		{
			Main.println("Cannot parse " + gap + " into the number.");
		}
	}

	public double getRandomGap()
	{
		return randomGap;
	}

	public void setRandomGap(double randomGap)
	{
		this.randomGap = randomGap;
	}

	public boolean isPaused()
	{
		return paused;
	}

	public void setPaused(boolean paused)
	{
		this.paused = paused;
	}
}
