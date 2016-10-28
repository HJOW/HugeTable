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

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import hjow.hgtable.HThread;
import hjow.hgtable.jscript.JScriptObject;

/**
 * <p>서버 역할을 하는 객체를 생성하는 클래스입니다.</p>
 * 
 * @author HJOW
 *
 */
public class AdvancedCommunicator implements Runnable, JScriptObject
{
	private static final long serialVersionUID = -468768845135420864L;
	protected AdvancedReceiveHandler eventObject;
	protected ServerSocket serverSocket;
	protected List<Socket> connected = new Vector<Socket>();
	protected List<AdvancedCommunicationUnit> units = new Vector<AdvancedCommunicationUnit>();
	protected boolean threadSwitch = true;
	protected Thread selfThread = null;
	protected Cleaner cleanerThread = null;
	
	/**
	 * <p>통신 객체를 만듭니다. 서버 역할을 시작하지는 않습니다.</p>
	 */
	public AdvancedCommunicator()
	{
		
	}
	
	/**
	 * <p>서버 역할을 시작합니다.</p>
	 * 
	 * @param port : 포트 번호
	 * @param eventHandler : 메시지 수신 시 호출될 이벤트 핸들러
	 * @throws IOException 통신 관련 문제 (포트 번호 충돌 등)
	 */
	public void init(int port, AdvancedReceiveHandler eventHandler) throws IOException
	{
		this.serverSocket = new ServerSocket(port);
		this.eventObject = eventHandler;
		selfThread = new HThread(this);
		cleanerThread = new Cleaner();
		selfThread.start();
		cleanerThread.start();
	}
	
	/**
	 * <p>연결된 통신을 닫습니다.</p>
	 */
	public void close()
	{
		threadSwitch = false;
		cleanerThread.threadSwitch = false;
		selfThread.interrupt();
		cleanerThread.interrupt();
		selfThread = null;
		cleanerThread = null;
		for(Socket s : connected)
		{
			try
			{
				s.close();
			}
			catch(Throwable t)
			{
				
			}
		}
		for(AdvancedCommunicationUnit units : units)
		{
			units.noMoreUse();
		}
		connected.clear();
		units.clear();
		try
		{
			serverSocket.close();
		}
		catch(Throwable t)
		{
			
		}
		serverSocket = null;
		eventObject.noMoreUse();
		eventObject = null;
	}
	
	/**
	 * <p>접속한 클라이언트 유닛 객체를 반환합니다. 이 객체는 접속한 클라이언트를 대표하며, 이 객체를 사용해 해당 클라이언트의 정보를 알아내거나 메시지를 보낼 수 있습니다.</p>
	 * 
	 * @param idx : 클라이언트 번호
	 * @return 클라이언트 유닛 객체
	 */
	public AdvancedCommunicationUnit getClient(int idx)
	{
		return units.get(idx);
	}
	
	@Override
	public void run()
	{
		while(threadSwitch)
		{
			try
			{
				if(serverSocket != null)
				{
					Socket connection = serverSocket.accept();
					AdvancedCommunicationUnit sub = new AdvancedCommunicationUnit(connection);
					connected.add(connection);
					units.add(sub);
				}
			}
			catch(Throwable t)
			{
				
			}
			try
			{
				Thread.sleep(20 + (int) (Math.random() * 10));
			}
			catch(Throwable t)
			{
				
			}
		}
	}
	
	@Override
	public String help()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void noMoreUse()
	{
		close();
	}

	@Override
	public boolean isAlive()
	{
		return (serverSocket != null) && (! serverSocket.isClosed());
	}
	
	/**
	 * <p>주기적으로 리스트를 검사해 닫힌 소켓들을 찾아 제거해 줍니다.</p>
	 * 
	 * @author HJOW
	 *
	 */
	class Cleaner extends Thread
	{
		protected boolean threadSwitch = true;
		@Override
		public void run()
		{
			while(threadSwitch)
			{
				try
				{
					int i = 0;
					while(i < connected.size())
					{
						Socket target = connected.get(i); 
						if(target.isClosed())
						{
							connected.remove(i);
							for(int j=0; j<units.size(); j++)
							{
								if(units.get(j).isThis(target))
								{
									units.get(j).noMoreUse();
									units.remove(j);
									break;
								}
							}
							
							i = 0;
							continue;
						}
						i++;
					}
				}
				catch(Throwable t)
				{
					
				}
				try
				{
					Thread.sleep(20 + (int) (Math.random() * 10));
				}
				catch(Throwable t)
				{
					
				}
			}
		}
	}
	
	/**
	 * <p>소켓 통신에서 개개인의 소켓의 역할을 대신합니다.</p>
	 * 
	 * @author HJOW
	 *
	 */
	class AdvancedCommunicationUnit extends Thread implements JScriptObject
	{
		private static final long serialVersionUID = 1585404190550269171L;
		protected Socket socket;
		protected boolean threadSwitch = true;
		protected InputStream inputStream;
		protected OutputStream outputStream;
		protected ObjectInputStream objectStream;
		protected ObjectOutputStream objectSender;
		protected AdvancedReceiveHandler independentEvent;
		public AdvancedCommunicationUnit(Socket socket) throws IOException
		{
			this.socket = socket;
			inputStream = socket.getInputStream();
			outputStream = socket.getOutputStream();
			objectStream = new ObjectInputStream(inputStream);
			objectSender = new ObjectOutputStream(outputStream);
			this.start();
		}
		/**
		 * <p>독자적인 이벤트를 지정합니다.</p>
		 * 
		 * @param handler : 이벤트 핸들러
		 */
		public void setEvent(AdvancedReceiveHandler handler)
		{
			this.independentEvent = handler;
		}
		/**
		 * <p>메시지를 보냅니다.</p>
		 * 
		 * @param msg : 보낼 메시지
		 * @throws IOException 통신 문제
		 */
		public void send(Object msg) throws IOException
		{
			objectSender.writeObject(msg);
		}
		/**
		 * <p>소켓 객체와 비교할 때 쓰이는 메소드입니다.</p>
		 * 
		 * @param socket : 비교할 소켓 객체
		 * @return 동일 소켓 여부
		 */
		public boolean isThis(Socket socket)
		{
			return this.socket == socket;
		}
		
		/**
		 * <p>소켓에 대한 정보들을 반환합니다.</p>
		 * 
		 * @return 소켓 관련 정보
		 * @throws SocketException
		 */
		public Map<String, Object> getSocketInfo() throws SocketException
		{
			Map<String, Object> anotherInfo = new Hashtable<String, Object>();
			anotherInfo.put("IP", socket.getInetAddress());
			anotherInfo.put("LOCAL", socket.getLocalAddress());
			anotherInfo.put("PORT", socket.getLocalPort());
			anotherInfo.put("RECEIVE_BUFFER", socket.getReceiveBufferSize());
			anotherInfo.put("SEND_BUFFER", socket.getSendBufferSize());
			anotherInfo.put("REMOTE_SOCKET", socket.getRemoteSocketAddress());
			anotherInfo.put("LOCAL_SOCKET", socket.getLocalSocketAddress());
			return anotherInfo;
		}
		@Override
		public void run()
		{
			while(threadSwitch)
			{
				try
				{
					if(socket == null || socket.isClosed())
					{
						noMoreUse();
						break;
					}
					Object receives = objectStream.readObject();
					if(independentEvent == null) eventObject.receive(receives, getSocketInfo());
					else independentEvent.receive(receives, getSocketInfo());
				}
				catch(Throwable t)
				{
					
				}
			}
		}
		@Override
		public void noMoreUse()
		{
			threadSwitch = false;
			try
			{
				objectStream.close();
			}
			catch (IOException e)
			{
				
			}
			try
			{
				objectSender.close();
			}
			catch (IOException e)
			{
				
			}
			try
			{
				socket.close();
			}
			catch (IOException e)
			{
				
			}
			inputStream = null;
			outputStream = null;
			socket = null;
			independentEvent = null;
		}
		@Override
		public String help()
		{
			// TODO Auto-generated method stub
			return null;
		}
	}
}
