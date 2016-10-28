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

package hjow.hgtable.ui.view;

import hjow.hgtable.ui.GUIManager;
import hjow.hgtable.ui.module.GUIDialogModule;

/**
 * <p>목록을 보여주는 대화 상자 객체가 됩니다.</p>
 * 
 * @author HJOW
 *
 */
public abstract class ListView extends GUIDialogModule
{
	private static final long serialVersionUID = -7514083479759647873L;
	protected GUIManager manager;
	
	/**
	 * <p>객체를 생성하면서, 매니저 객체를 등록시켜 연동합니다.</p>
	 * 
	 * @param manager : GUI 매니저 객체
	 */
	public ListView(GUIManager manager)
	{
		this.manager = manager;
	}
	
	/**
	 * <p>목록을 비웁니다.</p>
	 */
	public abstract void clear();
	
	/**
	 * 
	 * <p>대화 상자를 엽니다.</p>
	 */
	@Override
	public abstract void open();
	
	@Override
	public void noMoreUse()
	{
		this.manager = null;
	}
}
