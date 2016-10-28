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
package hjow.hgtable;

/**
 * <p>이 예외는 접속하지 않은 DAO로 데이터 소스에 액세스 시도했을 때 발생합니다.</p>
 * 
 * @author HJOW
 *
 */
public class NotConnectedException extends Exception
{
	private static final long serialVersionUID = 8028551581751460317L;
	public NotConnectedException()
	{
		super();
	}
	public NotConnectedException(String msg)
	{
		super(msg);
	}
}
