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

import javax.swing.JList;
import javax.swing.ListModel;

/**
 * 
 * <p>GUI 리스트 컴포넌트입니다. JList 를 대신합니다.</p>
 * 
 * @author HJOW
 *
 */
public class UIList extends JList
{
	private static final long serialVersionUID = -2892058647247584727L;
	
	/**
	 * <p>리스트 컴포넌트 객체를 만듭니다.</p>
	 * 
	 */
	public UIList()
	{
		super();
	}
	
	/**
	 * <p>리스트 모델을 사용해 리스트 컴포넌트 객체를 만듭니다.</p>
	 * 
	 * @param dataModel : Swing 호환 ListModel 객체
	 */
	public UIList(ListModel dataModel)
	{
		super(dataModel);
	}
	
	@Override
	public void setListData(@SuppressWarnings("rawtypes") Vector listData)
	{
		super.setListData(listData);
	}
}
