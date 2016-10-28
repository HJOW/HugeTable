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

import javax.swing.JEditorPane;

/**
 * <p>JEditorPane 에 기능을 추가하기 위한 클래스입니다.</p>
 * 
 * @author HJOW
 *
 */
public class UIEditorPane extends JEditorPane
{
	private static final long serialVersionUID = -2631888731968769486L;
	protected boolean isMultiLineEditMode = false;
	
	/**
	 * <p>Creates a new JEditorPane. The document model is set to null.</p>
	 */
	public UIEditorPane()
	{
		super();
	}
	
	public String[] getLines()
	{
		return getText().split("\n");
	}

	public boolean isMultiLineEditMode()
	{
		return isMultiLineEditMode;
	}

	public void setMultiLineEditMode(boolean isMultiLineEditMode)
	{
		this.isMultiLineEditMode = isMultiLineEditMode;
	}
}
