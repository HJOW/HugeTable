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

package hjow.hgtable.jscript;

import java.io.Serializable;

/**
 * <p>스크립트에서, 특수한 명령어를 입력한 경우 반환되는 구분용 객체를 만들 때 쓰는 클래스입니다.</p>
 * 
 * @author HJOW
 *
 */
public class SpecialOrder implements Serializable
{
	private static final long serialVersionUID = 7275222984704184123L;
	private String order;
	
	public SpecialOrder()
	{
		
	}
	public SpecialOrder(String order)
	{
		this.order = order;
	}
	public String getOrder()
	{
		return order;
	}
	public void setOrder(String order)
	{
		this.order = order;
	}
	@Override
	public String toString()
	{
		return "special://" + order;
	}
}
