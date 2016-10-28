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

package hjow.hgtable.jscript.daemon;

import hjow.hgtable.ui.AccessInfo;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;

@Deprecated
public class HgAccount implements Serializable
{
	private static final long serialVersionUID = -5306294787141218880L;
	
	protected String id;
	protected String pw;
	
	protected List<AccessInfo> priv_accessDBList = new Vector<AccessInfo>();
	protected List<String> priv_allowedWriteFilePath = new Vector<String>();
	protected List<String> priv_allowedReadFilePath = new Vector<String>();
	protected boolean priv_allowCommand = false;
	protected boolean priv_allowThread = false;
	protected boolean priv_allowLoadClass = false;
	
	public HgAccount()
	{
		
	}
	
	public String getId()
	{
		return id;
	}
	public void setId(String id)
	{
		this.id = id;
	}
	public String getPw()
	{
		return pw;
	}
	public void setPw(String pw)
	{
		this.pw = pw;
	}
	public List<AccessInfo> getPriv_accessDBList()
	{
		return priv_accessDBList;
	}
	public void setPriv_accessDBList(List<AccessInfo> priv_accessDBList)
	{
		this.priv_accessDBList = priv_accessDBList;
	}
	public List<String> getPriv_allowedWriteFilePath()
	{
		return priv_allowedWriteFilePath;
	}
	public void setPriv_allowedWriteFilePath(List<String> priv_allowedWriteFilePath)
	{
		this.priv_allowedWriteFilePath = priv_allowedWriteFilePath;
	}
	public List<String> getPriv_allowedReadFilePath()
	{
		return priv_allowedReadFilePath;
	}
	public void setPriv_allowedReadFilePath(List<String> priv_allowedReadFilePath)
	{
		this.priv_allowedReadFilePath = priv_allowedReadFilePath;
	}
	public boolean isPriv_allowCommand()
	{
		return priv_allowCommand;
	}
	public void setPriv_allowCommand(boolean priv_allowCommand)
	{
		this.priv_allowCommand = priv_allowCommand;
	}
	public boolean isPriv_allowThread()
	{
		return priv_allowThread;
	}
	public void setPriv_allowThread(boolean priv_allowThread)
	{
		this.priv_allowThread = priv_allowThread;
	}
	public boolean isPriv_allowLoadClass()
	{
		return priv_allowLoadClass;
	}
	public void setPriv_allowLoadClass(boolean priv_allowLoadClass)
	{
		this.priv_allowLoadClass = priv_allowLoadClass;
	}
}
