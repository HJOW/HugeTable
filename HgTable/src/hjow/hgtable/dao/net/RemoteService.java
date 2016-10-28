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

import java.io.IOException;

import hjow.web.core.Server;

/**
 * <p>원격 접속 DAO 서비스를 해 주는 서버입니다.</p>
 * 
 * @author HJOW
 *
 */
public class RemoteService
{
	protected Server server;
	public RemoteService()
	{
		server = new Server();
		server.setPageContent(new RemotePageContent());
	}
	
	public void init(int port) throws IOException
	{
		server.init(port);
	}
}
