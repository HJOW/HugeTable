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

import java.awt.Dialog;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 * <p>사용자로부터 여러 줄 텍스트 입력을 받는 대화 상자입니다. 모달 형태이므로 이 대화 상자가 닫힐 때까지 대부분의 작업이 잠시 중단됩니다.</p>
 * 
 * @author HJOW
 *
 */
public abstract class AskInputDialog implements ActionListener, WindowListener, NeedtoEnd
{
	public transient String results = "";
	
	/**
	 * <p>대화 상자를 엽니다. 다른 작업이 일시 중단됩니다.</p>
	 * 
	 */
	public abstract void open(String message);
	/**
	 * <p>더 이상 이 객체를 사용할 필요가 없을 때 호출합니다. 순환 참조를 제거합니다. 프로그램이 종료될 때 호출됩니다.</p>
	 * 
	 */
	@Override
	public void noMoreUse()
	{
		actionCancel();
	}
	@Override
	public boolean isAlive()
	{
		return true;
	}
	
	/**
	 * <p>사용자가 OK 버튼을 눌렀을 때 호출됩니다.</p>
	 * 
	 */
	public abstract void actionOk();
	
	/**
	 * <p>사용자가 취소 버튼을 눌렀을 때 호출됩니다.</p>
	 * 
	 */
	public abstract void actionCancel();
	@Override
	public void windowClosing(WindowEvent e)
	{
		Object ob = e.getSource();
		if(ob == this)
		{
			actionCancel();
		}
	}
	protected abstract void actionFile();
	public abstract Dialog getDialog();
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
