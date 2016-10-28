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

import java.io.Serializable;

/**
 * <p>텍스트 블록에 대한 정보를 담는 객체에 관여하는 클래스입니다.</p>
 * 
 * @author HJOW
 *
 */
public class TextBlock implements Serializable
{
	private static final long serialVersionUID = 6107780833445306941L;
	protected int startPosition;
	protected int endPosition;
	protected String contents;
	
	/**
	 * <p>기본 생성자입니다.</p>
	 */
	public TextBlock()
	{
		
	}
	
	/**
	 * <p>필드에 값을 입력하는 생성자입니다.</p>
	 * 
	 * @param startPosition : 블록의 시작 위치
	 * @param endPosition : 블록의 끝 위치
	 * @param contents : 내용
	 */
	public TextBlock(int startPosition, int endPosition, String contents)
	{
		super();
		this.startPosition = startPosition;
		this.endPosition = endPosition;
		this.contents = contents;
	}
	
	@Override
	public String toString()
	{
		return contents;
	}
	public int getStartPosition()
	{
		return startPosition;
	}
	public void setStartPosition(int startPosition)
	{
		this.startPosition = startPosition;
	}
	public int getEndPosition()
	{
		return endPosition;
	}
	public void setEndPosition(int endPosition)
	{
		this.endPosition = endPosition;
	}
	public String getContents()
	{
		return contents;
	}
	public void setContents(String contents)
	{
		this.contents = contents;
	}
}
