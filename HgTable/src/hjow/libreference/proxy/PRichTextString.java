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

package hjow.libreference.proxy;

import org.apache.poi.ss.usermodel.RichTextString;

import hjow.libreference.HgRichTextString;

/**
 * <p>Rich 텍스트 형식을 대신합니다.</p>
 * 
 * @author HJOW
 *
 */
public class PRichTextString implements HgRichTextString
{
	private static final long serialVersionUID = 7966518288339972707L;
	protected Object contents;
	
	public PRichTextString()
	{
		
	}
	
	public PRichTextString(Object contents)
	{
		setValue(contents);
	}
	
	@Override
	public String help()
	{
		return null;
	}

	@Override
	public String getString()
	{
		return ((RichTextString) contents).getString();
	}
	public void setValue(Object value)
	{
		this.contents = value;
	}

	public Object getContents()
	{
		return contents;
	}

	public void setContents(Object contents)
	{
		this.contents = contents;
	}
	@Override
	public void noMoreUse()
	{
		
	}
	@Override
	public boolean isAlive()
	{
		return true;
	}
}
