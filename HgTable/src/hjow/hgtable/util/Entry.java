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
package hjow.hgtable.util;

/**
 * <p>두 요소를 가지는 객체가 필요할 때 쓸 수 있는 클래스입니다.</p>
 * 
 * @author HJOW
 *
 */
public class Entry<T1, T2> implements java.util.Map.Entry<T1, T2>
{
	protected T1 key;
	protected T2 value;
	public Entry()
	{
		super();
	}
	public Entry(T1 key, T2 value)
	{
		super();
		this.key = key;
		this.value = value;
	}
	@Override
	public T1 getKey()
	{
		return key;
	}
	public void setKey(T1 key)
	{
		this.key = key;
	}
	@Override
	public T2 getValue()
	{
		return value;
	}
	@Override
	public T2 setValue(T2 value)
	{
		T2 oldOne = this.value;
		this.value = value;
		return oldOne;
	}
}
