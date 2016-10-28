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

package hjow.hgtable.ui.module;

import java.awt.Component;

import hjow.hgtable.ui.GUIManager;

/**
 * <p>패널형 GUI 모듈입니다.</p>
 * 
 * @author HJOW
 *
 */
public class GUIPanelModule extends GUIModule
{
	private static final long serialVersionUID = 6951727337184618932L;

	public GUIPanelModule()
	{
		super();
	}
	
	public GUIPanelModule(GUIManager manager)
	{
		super(manager);
	}
	
	/**
	 * <p>이 메소드는 이 패널이 화면 상에서 숨겨질 때 호출됩니다. 별도의 대화 상자로 숨겨지는 경우에는 호출되지 않습니다.</p>
	 */
	@Override
	public void close()
	{
		
	}
	
	/**
	 * <p>이 메소드는 이 패널이 화면 상에서 보여질 때 호출됩니다. 별도의 대화 상자로 숨겨진 후 보여지는 경우에는 호출되지 않습니다.</p>
	 * 
	 */
	@Override
	public void open()
	{
		
	}
	
	/**
	 * <p>패널 객체를 반환합니다.</p>
	 * 
	 * @return 대화 상자 객체
	 */
	@Override
	public Component getComponent()
	{
		return null;
	}
}
