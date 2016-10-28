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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.StringTokenizer;

import hjow.hgtable.HThread;
import hjow.hgtable.Main;
import hjow.hgtable.Manager;
import hjow.hgtable.jscript.JScriptRunner;
import hjow.hgtable.jscript.JavaScriptRunner;
import hjow.hgtable.jscript.NotEnoughPrivilegeException;
import hjow.hgtable.streamchain.ChainInputStream;
import hjow.hgtable.streamchain.ChainOutputStream;
import hjow.hgtable.tableset.TableSet;
import hjow.hgtable.util.DataUtil;

@Deprecated
public class HgUser implements Runnable
{
	protected Socket socket;
	protected InputStream inputStream;
	protected ChainInputStream chainInputStream;
	protected InputStreamReader charsetReader;
	protected BufferedReader reader;
	protected OutputStream outputStream;
	protected ChainOutputStream chainOutputStream;
	protected OutputStreamWriter charsetWriter;
	protected BufferedWriter writer;
	protected JScriptRunner scriptRunner;
	protected HgDaemon daemon;
	protected Manager manager;
	protected boolean accepted = false;
	
	protected boolean threadSwitch = false;
	protected int threadGap = 100;
	
	public HgUser()
	{
		
	}
	public HgUser(Socket socket) throws Exception
	{
		this.socket = socket;
		inputStream = socket.getInputStream();
		chainInputStream = new ChainInputStream(inputStream);
		charsetReader = new InputStreamReader(chainInputStream.getInputStream(), "UTF-8");
		reader = new BufferedReader(charsetReader);
		outputStream = socket.getOutputStream();
		chainOutputStream = new ChainOutputStream(outputStream);
		charsetWriter = new OutputStreamWriter(chainOutputStream.getOutputStream(), "UTF-8");
		writer = new BufferedWriter(charsetWriter);
		
		// TODO 새 매니저 객체 할당 필요
		scriptRunner = new JavaScriptRunner(null);
		
		threadSwitch = true;
		new HThread(this).start();
	}
	public void onThread() throws Throwable
	{
		String gets = receiveRaw();
		if(gets == null) return;
		gets = gets.trim();
		
		if(gets.equalsIgnoreCase("exit")) close();
		if(gets.startsWith("exit://") || gets.startsWith("EXIT://") || gets.startsWith("Exit://")) close();
		else
		{
			String reCasted = DataUtil.reCastTotal(true, gets).trim();
			
			if(reCasted.startsWith("CONNECT://") || reCasted.startsWith("connect://") || reCasted.startsWith("Connect://"))
			{
				String connectInfos = reCasted.substring(new String("CONNECT://").length());
				StringTokenizer equalTokenizer = new StringTokenizer(connectInfos, "=");
				String id = equalTokenizer.nextToken();
				String pw = "";
				if(equalTokenizer.hasMoreTokens()) pw = equalTokenizer.nextToken();
				if(daemon.isAuthorize(id, pw))
				{
					accepted = true;
					send("CONNECTION_SUCCESS");
					// TODO
				}
				else
				{
					accepted = false;
					send(new NotEnoughPrivilegeException(Manager.getOption("Invalid ID or Password")));
				}
			}
			else if(accepted)
			{
				if(reCasted.startsWith("SCRIPT://") || reCasted.startsWith("script://") || reCasted.startsWith("Script://"))
				{
					Object results = scriptRunner.execute(reCasted.substring(new String("SCRIPT://").length()));
					scriptRunner.getAttributes().put("result", results);
					send(String.valueOf(results));
					// TODO
				}
			}
		}
	}
	public String receiveRaw() throws Exception
	{
		return reader.readLine();
	}
	public void send(String str) throws Exception
	{
		sendRaw("RETURN://" + str);
	}
	public void send(TableSet tableSet) throws Exception
	{
		sendRaw("RETURN://" + DataUtil.castTotal(true, tableSet.toHGF()));
	}
	public void send(Exception e) throws Exception
	{
		sendRaw("EXCEPTION://" + e.getClass().getName() + ":" + e.getMessage());
	}
	public void sendRaw(String contents) throws Exception
	{
		String processed = DataUtil.castTotal(true, contents);
		StringTokenizer lineTokenizer = new StringTokenizer(processed, "\n");
		while(lineTokenizer.hasMoreTokens())
		{
			writer.write(lineTokenizer.nextToken());
		}
	}
	public void close()
	{
		try
		{
			sendRaw("exit");
		}
		catch(Throwable e)
		{
			
		}
		
		daemon = null;
		manager = null;
		threadSwitch = false;
		
		try
		{
			writer.close();
		}
		catch(Throwable e)
		{
			
		}
		try
		{
			reader.close();
		}
		catch(Throwable e)
		{
			
		}
		try
		{
			charsetReader.close();
		}
		catch(Throwable e)
		{
			
		}
		try
		{
			charsetWriter.close();
		}
		catch(Throwable e)
		{
			
		}
		try
		{
			chainInputStream.close();
		}
		catch(Throwable e)
		{
			
		}
		try
		{
			chainOutputStream.close();
		}
		catch(Throwable e)
		{
			
		}
		try
		{
			inputStream.close();
		}
		catch(Throwable e)
		{
			
		}
		try
		{
			outputStream.close();
		}
		catch(Throwable e)
		{
			
		}
		try
		{
			socket.close();
		}
		catch(Throwable e)
		{
			
		}
		
		System.gc();
	}
	public Socket getSocket()
	{
		return socket;
	}
	public void setSocket(Socket socket)
	{
		this.socket = socket;
	}
	public InputStream getInputStream()
	{
		return inputStream;
	}
	public void setInputStream(InputStream inputStream)
	{
		this.inputStream = inputStream;
	}
	public ChainInputStream getChainInputStream()
	{
		return chainInputStream;
	}
	public void setChainInputStream(ChainInputStream chainInputStream)
	{
		this.chainInputStream = chainInputStream;
	}
	public InputStreamReader getCharsetReader()
	{
		return charsetReader;
	}
	public void setCharsetReader(InputStreamReader charsetReader)
	{
		this.charsetReader = charsetReader;
	}
	public BufferedReader getReader()
	{
		return reader;
	}
	public void setReader(BufferedReader reader)
	{
		this.reader = reader;
	}
	public OutputStream getOutputStream()
	{
		return outputStream;
	}
	public void setOutputStream(OutputStream outputStream)
	{
		this.outputStream = outputStream;
	}
	public ChainOutputStream getChainOutputStream()
	{
		return chainOutputStream;
	}
	public void setChainOutputStream(ChainOutputStream chainOutputStream)
	{
		this.chainOutputStream = chainOutputStream;
	}
	public OutputStreamWriter getCharsetWriter()
	{
		return charsetWriter;
	}
	public void setCharsetWriter(OutputStreamWriter charsetWriter)
	{
		this.charsetWriter = charsetWriter;
	}
	public BufferedWriter getWriter()
	{
		return writer;
	}
	public void setWriter(BufferedWriter writer)
	{
		this.writer = writer;
	}
	public JScriptRunner getScriptRunner()
	{
		return scriptRunner;
	}
	public void setScriptRunner(JScriptRunner scriptRunner)
	{
		this.scriptRunner = scriptRunner;
	}	
	@Override
	public void run()
	{
		while(threadSwitch)
		{
			try
			{
				onThread();
			}
			catch(Throwable e)
			{
				Main.logError(e, Manager.applyStringTable("On daemon on thread"));
			}
			try
			{
				Thread.sleep(threadGap);
			}
			catch(Throwable e)
			{
				
			}
		}
	}
}
