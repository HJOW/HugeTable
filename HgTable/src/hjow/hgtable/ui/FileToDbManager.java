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
import hjow.hgtable.util.StreamUtil;
import hjow.hgtable.tableset.TableSet;
import hjow.hgtable.util.DirectIOUtil;
import hjow.hgtable.util.JSONUtil;
import hjow.hgtable.util.XLSXUtil;

import java.awt.Dialog;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * <p>파일로부터 데이터를 DB에 삽입하는 작업을 위한 마법사 클래스입니다.</p>
 * 
 * @author HJOW
 *
 */
public abstract class FileToDbManager implements ActionListener, WindowListener, Runnable, NeedtoEnd
{
	protected Manager manager;
	protected boolean threadSwitch = true;
	protected int threadGap = 30;
	protected boolean needWork = false;
	protected Map<String, String> functionData = new Hashtable<String, String>();
	protected Map<String, Integer> functionIndexData = new Hashtable<String, Integer>();
	protected Map<String, List<String>> functionOtherParams = new Hashtable<String, List<String>>();
	
	/**
	 * <p>기본 생성자입니다.</p> 
	 */
	public FileToDbManager(Manager manager)
	{
		this.manager = manager;
	}
	
	/**
	 * <p>더 이상 사용되지 않을 때 호출됩니다.</p>
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
	 * <p>마법사 창을 엽니다.</p>
	 * 
	 */
	public abstract void open();
	
	/**
	 * <p>대화 상자 객체를 반환합니다.</p>
	 * 
	 * @return 대화 상자 객체, AWT 호환
	 */
	public abstract Dialog getDialog();
	
	/**
	 * <p>진행 상태를 게이지 바 형태로 알 수 있는 경우 이 메소드를 오버라이드해 사용합니다.</p>
	 * 
	 * @param v : 진행 상태, 0 ~ 100
	 */
	public void setPercent(int v)
	{
		
	}
	
	/**
	 * <p>컴포넌트에서 변환 함수 정보를 받아옵니다.</p>
	 */
	protected abstract void setFunctionDataFromTable();
	
	/**
	 * <p>선택된 파일 경로와 이름을 반환합니다.</p>
	 * 
	 * @return 선택된 파일 정보
	 */
	public abstract String getSelectedFile();
	
	/**
	 * <p>사용자에 의해 입력된 테이블 이름을 반환합니다.</p>
	 * 
	 * @return 테이블 이름
	 */
	public abstract String getSelectedTable();
	
	/**
	 * <p>컬럼에 따라 필요한 변환 함수들 정보를 삽입합니다.</p>
	 * 
	 * @param functionData : 변환 함수 정보
	 */
	public void setFunctionData(Map<String, String> functionData)
	{
		this.functionData = functionData;
	}
	/**
	 * <p>컬럼에 따라 필요한 변환 함수들 정보를 삽입합니다.</p>
	 * 
	 * @param functionData : 변환 함수 정보
	 */
	public void setFunctionIndexData(Map<String, Integer> functionIndexData)
	{
		this.functionIndexData = functionIndexData;
	}
	/**
	 * <p>컬럼에 따라 필요한 변환 함수들 정보를 삽입합니다.</p>
	 * 
	 * @param functionData : 변환 함수 정보
	 */
	public void setFunctionOtherParams(Map<String, List<String>> functionOtherParams)
	{
		this.functionOtherParams = functionOtherParams;
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
				String fileName = getSelectedFile().trim();
				String tableName = getSelectedTable().trim();
				String charset = Manager.getOption("file_charset");
				if(charset == null) charset = "UTF-8";
				
				setFunctionDataFromTable();
				
				TableSet newTableSet;
				if(fileName.endsWith(".xlsx") || fileName.endsWith(".XLSX") || fileName.endsWith(".Xlsx"))
				{
					newTableSet = XLSXUtil.toTableSet(tableName, new File(fileName));
					newTableSet.setName(tableName);
					newTableSet.insertIntoDB(manager.getDao(), new Hashtable<String, String>(), functionData, functionIndexData, functionOtherParams);
				}
				else if(fileName.endsWith(".json") || fileName.endsWith(".JSON") || fileName.endsWith(".Json"))
				{
					newTableSet = JSONUtil.toTableSet(StreamUtil.readText(new File(fileName), charset));
					newTableSet.setName(tableName);
					newTableSet.insertIntoDB(manager.getDao(), new Hashtable<String, String>(), functionData, functionIndexData, functionOtherParams);
				}
				else if(fileName.endsWith(".hgf") || fileName.endsWith(".HGF") || fileName.endsWith(".Hgf"))
				{
					DirectIOUtil.hgfToDb(manager.getDao(), new File(fileName), functionData, functionIndexData, functionOtherParams);
				}
				else
				{
					DirectIOUtil.hgfToDb(manager.getDao(), new File(fileName), functionData, functionIndexData, functionOtherParams);
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
	/**
	 * <p>이 메소드는 작업이 실패했을 때 호출됩니다.</p>
	 * 
	 * @param reasons : 실패 사유 메시지
	 */
	public void onFail(String reasons)
	{
		
	}
	/**
	 * <p>이 메소드는 작업이 성공했을 때 호출됩니다.</p>
	 */
	public void onSuccess()
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
			
			if(! (Main.checkInterrupt(this, "File to DB Manager thread")))
			{
				break;
			}
		}
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
