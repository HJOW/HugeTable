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

import hjow.hgtable.ui.NeedtoEnd;

import javax.swing.JApplet;

/**
 * <p>애플릿 객체입니다.</p>
 * 
 * @author HJOW
 *
 */
public class ManagerApplet extends JApplet implements NeedtoEnd
{
	private static final long serialVersionUID = -1645787987955622616L;
	protected SwingManager manager;
	public ManagerApplet()
	{
		
	}
	public ManagerApplet(SwingManager manager)
	{
		this.manager = manager;
	}
	@Override
	public void init()
	{
		manager.initComponents();
	}
	@Override
	public void noMoreUse()
	{
		manager = null;
	}
	@Override
	public boolean isAlive()
	{
		return manager != null && manager.isAlive();
	}
}
