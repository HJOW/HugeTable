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
package hjow.hgtable.util.net;

import java.util.List;
import java.util.Map;

import hjow.web.content.Page;
import hjow.web.session.Session;

/**
 * <p>페이지 클래스입니다. 요청을 받아, 사용자에게 보낼 텍스트를 만들어냅니다. 대부분은 hweb.jar 라이브러리의 것을 사용합니다.</p>
 * 
 * @author HJOW
 *
 */
public class DefaultPage implements Page
{

	@Override
	public String process(Map<String, List<String>> parameter, Map<String, String> meta, Session session)
	{
		return null;
	}

	@Override
	public void init()
	{
		
	}

	@Override
	public void close()
	{
		
	}

	@Override
	public String getOrder()
	{
		return null;
	}
}
