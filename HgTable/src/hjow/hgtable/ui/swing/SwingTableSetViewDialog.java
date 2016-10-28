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
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;

import hjow.hgtable.tableset.TableSet;

/**
 * <p>테이블 셋 조회용 대화 상자입니다.</p>
 */
public class SwingTableSetViewDialog extends SwingTableSetView
{
	private static final long serialVersionUID = -530758793716019172L;
	private JDialog dialog;
	
	/**
	 * <p>이 생성자를 실제로 사용할 경우, 매니저와 테이블 셋 객체를 삽입한 후 initComponent() 메소드를 호출해 컴포넌트들을 초기화해야 합니다.</p>
	 * 
	 */
	public SwingTableSetViewDialog()
	{
		super();
	}
	/**
	 * <p>매니저 객체를 삽입하고 컴포넌트를 초기화합니다.</p>
	 * 
	 * @param manager : 매니저 객체
	 * @param tableSet : 테이블 셋 객체
	 */
	public SwingTableSetViewDialog(SwingManager manager, TableSet tableSet)
	{
		super(manager, tableSet);
	}
	
	/**
	 * <p>컴포넌트들을 초기화합니다.</p>
	 * 
	 */
	@Override
	public void initComponent()
	{
		dialog = new JDialog(manager.getFrame());
		super.initComponent();
		
		Dimension scSize = Toolkit.getDefaultToolkit().getScreenSize();
		dialog.setSize((int)(600), (int)(450));
		dialog.setLocation((int)(scSize.getWidth()/2 - dialog.getWidth()/2), (int)(scSize.getHeight()/2 - dialog.getHeight()/2));
		
		dialog.setLayout(new BorderLayout());
		dialog.add(mainPanel);
		
		dialog.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				dialog.setVisible(false);
				noMoreUse();
			}
		});
	}
	
	/**
	 * <p>대화 상자 객체를 반환합니다.</p>
	 * 
	 * @return 대화 상자
	 */
	public Window getDialog()
	{
		return dialog;
	}
	
	@Override
	protected void close()
	{
		dialog.setVisible(false);
		super.close();
	}
}
