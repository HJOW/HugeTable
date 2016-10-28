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
 * 
 * <p>이 예외는 사용자가 입력을 잘못 했을 때 발생합니다. 예를 들어, y/n 입력을 요구하는 화면에서 다른 문자를 입력한 경우 발생합니다.</p>
 * 
 * @author HJOW
 *
 */
public class InvalidInputException extends RuntimeException
{
	private static final long serialVersionUID = 1586890853546664279L;
	public InvalidInputException()
	{
		super();
	}
	public InvalidInputException(String msg)
	{
		super(msg);
	}
}
