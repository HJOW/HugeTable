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
package hjow.hgtable.dao.net;

import hjow.hgtable.dao.net.page.ConnectPage;
import hjow.web.content.DefaultPageContent;
import hjow.web.content.Page;

public class RemotePageContent extends DefaultPageContent
{
	public RemotePageContent()
	{
		pages.add(new ConnectPage());
	}
	@Override
	public void close()
	{
		for(Page page : pages)
		{
			page.close();
		}
		pages.clear();
		super.close();
	}
}
