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

package hjow.hgtable.streamchain;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Vector;

public class ChainReader extends ChainObject
{
	private List<Reader> chains = new Vector<Reader>();
	
	public ChainReader(Reader firstStream)
	{
		chains.add(firstStream);
	}
	
	public void put(Class<? extends Reader> inputStreamClass) throws Exception
	{
		if(locked) throw new IOException("Cannot put the input stream on the chain stream which is already locked.");
		Reader stream = inputStreamClass.getConstructor(Reader.class).newInstance(getReader(false));
		chains.add(stream);
	}
	public Reader getReader()
	{
		return getReader(true);
	}
	private Reader getReader(boolean lock)
	{
		locked = lock;
		return chains.get(chains.size() - 1);
	}
	
	@Override
	public void close() throws IOException
	{
		for(int i=0; i<chains.size(); i++)
		{
			try
			{
				chains.get(i).close();
			}
			catch(Throwable e)
			{
				
			}
		}
		locked = true;
	}

	@Override
	public void put(String streamName) throws Exception
	{
		
	}
}
