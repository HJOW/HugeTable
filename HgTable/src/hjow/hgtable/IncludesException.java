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
 * <p>다른 예외를 포함하는 예외 객체입니다. 다른 라이브러리와의 호환성 때문에 RuntimeException 이 아닌 예외를 처리하기 위해 사용됩니다.</p>
 * 
 * @author HJOW
 *
 */
public class IncludesException extends RuntimeException
{
	private static final long serialVersionUID = -5692592727634264435L;
	protected Throwable origins;
	
	public IncludesException(Throwable origins)
	{
		this(origins.getMessage(), origins);
	}
	
	public IncludesException(String msg, Throwable origins)
	{
		super(msg);
		this.origins = origins;
	}
	
	@Override
	public StackTraceElement[] getStackTrace() 
	{
		return origins.getStackTrace();
	}
	
	@Override
	public String getMessage()
	{
		return super.getMessage() + "\n  Caused by " + origins.getMessage();
	}
}
