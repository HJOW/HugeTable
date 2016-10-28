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

package hjow.hgtable.ui;

import hjow.hgtable.Main;
import hjow.hgtable.Manager;
import hjow.hgtable.tableset.DefaultTableSet;
import hjow.hgtable.tableset.TableSet;
import hjow.hgtable.util.DirectIOUtil;
import hjow.hgtable.util.StreamUtil;
import hjow.hgtable.util.XLSXUtil;

import java.awt.Dialog;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;

/**
 * <p>이 클래스 객체는 GUI 환경에서 DB의 데이터를 가져와 파일로 저장하는 데 사용됩니다.</p>
 * 
 * @author HJOW
 *
 */
public abstract class DbToFileManager implements ActionListener, WindowListener, Runnable, NeedtoEnd
{
	protected Manager manager;
	protected boolean threadSwitch = true;
	protected int threadGap = 100;
	protected boolean needWork = false;
		
	/**
	 * <p>기본 생성자입니다.</p>
	 * 
	 * @param manager : 매니저 객체
	 */
	public DbToFileManager(Manager manager)
	{
		this.manager = manager;
	}
	
	/**
	 * <p>이 메소드는 프로그램이 종료되거나, UI 매니저 사용이 중단될 때 호출되어 순환 참조를 제거합니다. 이 메소드가 호출되어야 메모리에서 이 객체가 삭제될 수 있습니다.</p>
	 * 
	 */
	@Override
	public void noMoreUse()
	{
		threadSwitch = false;
		manager = null;
	}
	
	@Override
	public boolean isAlive()
	{
		return threadSwitch;
	}
	
	/**
	 * <p>DB to File 매니저 창을 엽니다.</p>
	 * 
	 */
	public abstract void open();
	
	/**
	 * <p>Dialog 객체를 반환합니다. AWT 라이브러리와 호환됩니다.</p>
	 * 
	 * @return 대화 상자 객체
	 */
	public abstract Dialog getDialog();
	
	/**
	 * <p>사용자가 입력한 SQL문을 반환합니다.</p>
	 * 
	 * @return 사용자가 입력한 SQL문
	 */
	public abstract String getQuery();
	
	/**
	 * <p>사용자가 선택한 파일 경로와 이름을 반환합니다.</p>
	 * 
	 * @return 사용자가 선택한 파일 경로와 이름
	 */
	public abstract String getSelectedFile();
	
	/**
	 * <p>사용자가 입력한 테이블 이름을 반환합니다.</p>
	 * 
	 * @return 테이블 이름
	 */
	public abstract String getSelectedTable();
	
	/**
	 * <p>현재 진행 상황을 표시할 수단이 있는 경우 이 메소드를 오버라이드해 사용합니다. 0 ~ 100 사이 값을 지원해야 합니다.</p>
	 * 
	 * @param v : 현재 진행 상황 (%)
	 */
	public void setPercent(int v)
	{
		
	}
	
	/**
	 * <p>이 메소드는 별도의 쓰레드에서 병행 실행됩니다.</p>
	 * 
	 */
	public void onThread()
	{
		if(needWork)
		{
			needWork = false;
			try
			{
				String selected = getSelectedFile();
				String query = getQuery();
				
				selected = selected.trim();
				File file = new File(selected);
				File dir = new File(StreamUtil.getDirectoryPathOfFile(file));
				
				if(! dir.exists()) dir.mkdir();
				
				if(selected.endsWith(".json") || selected.endsWith(".JSON") || selected.endsWith(".Json"))
				{
					DirectIOUtil.dbToJson(getSelectedTable(), manager.getDao(), file, query, null, true);
				}
				else if(selected.endsWith(".xlsx") || selected.endsWith(".XLSX") || selected.endsWith(".Xlsx"))
				{
					TableSet tableSet = new DefaultTableSet(getSelectedTable(), manager.getDao(), query, null);
					XLSXUtil.save(tableSet, file);
				}
				else if(selected.endsWith(".hgf") || selected.endsWith(".HGF") || selected.endsWith(".Hgf"))
				{
					DirectIOUtil.dbToHgf(getSelectedTable(), manager.getDao(), file, query, null);
				}
				else
				{
					DirectIOUtil.dbToHgf(getSelectedTable(), manager.getDao(), file, query, null);
				}
				
				onSuccess();
			}
			catch(Throwable e)
			{
				manager.logError(e, Manager.applyStringTable("On file to DB"));
				onFail(e.getMessage());
			}			
		}
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
				
			}
			try
			{
				Thread.sleep(threadGap);
			}
			catch(Throwable e)
			{
				
			}
			
			if(! (Main.checkInterrupt(this, "DB to File Manager thread")))
			{
				break;
			}
		}
	}
	
	/**
	 * <p>이 메소드는 작업이 실패했을 때 호출됩니다.</p>
	 * 
	 * @param reasons : 실패 사유 메시지
	 */
	public void onFail(String reasons)
	{
		
	}
	
	/**
	 * <p>이 메소드는 작업을 성공했을 때 호출됩니다.</p>
	 * 
	 */
	public void onSuccess()
	{
		
	}

	@Override
	public void windowActivated(WindowEvent e)
	{
		
		
	}

	@Override
	public void windowClosed(WindowEvent e)
	{
		
		
	}


	@Override
	public void windowDeactivated(WindowEvent e)
	{
		
		
	}

	@Override
	public void windowDeiconified(WindowEvent e)
	{
		
		
	}

	@Override
	public void windowIconified(WindowEvent e)
	{
		
		
	}

	@Override
	public void windowOpened(WindowEvent e)
	{
		
		
	}

}
