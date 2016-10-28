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

import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;

/**
 * 
 * <p>GUI 콤보박스 컴포넌트입니다. JComboBox 를 대신합니다.</p>
 * 
 * @author HJOW
 *
 */
public class UIComboBox extends JComboBox
{
	private static final long serialVersionUID = 3187558691746390375L;
	
	/**
	 * <p>콤보박스 컴포넌트를 만듭니다.</p>
	 * 
	 */
	public UIComboBox()
	{
		super();
	}
	
	/**
	 * <p>콤보박스 컴포넌트를 만들고 바로 아이템을 삽입합니다.</p>
	 * 
	 * @param obj : 아이템 배열
	 */
	public UIComboBox(Object[] obj)
	{
		super(obj);
	}

	/**
	 * <p>콤보박스 컴포넌트를 만들고 바로 아이템을 삽입합니다.</p>
	 * 
	 * @param obj : 아이템 리스트
	 */
	public UIComboBox(Vector<?> obj)
	{
		super(obj);
	}
	
	/**
	 * <p>콤보박스 모델을 사용해 콤보박스 컴포넌트를 만듭니다.</p>
	 * 
	 * @param model : Swing 호환 콤보박스 모델 객체
	 */
	public UIComboBox(ComboBoxModel model)
	{
		super(model);
	}
	
	@Override
	public void addItem(Object obj)
	{
		super.addItem(obj);
	}
}
