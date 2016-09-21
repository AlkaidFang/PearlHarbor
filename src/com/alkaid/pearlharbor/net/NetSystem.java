package com.alkaid.pearlharbor.net;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.alkaid.pearlharbor.logger.LoggerSystem;
import com.alkaid.pearlharbor.logger.LoggerSystem.LogType;
import com.alkaid.pearlharbor.net.connection.ConnectionType;
import com.alkaid.pearlharbor.net.connection.IConnection;
import com.alkaid.pearlharbor.net.connection.IConnectionManager;
import com.alkaid.pearlharbor.net.connection.TCPConnectionManager_mina;
import com.alkaid.pearlharbor.net.connection.TCPConnectionManager_nio;
import com.alkaid.pearlharbor.net.connection.WebSocketConnectionManager;
import com.alkaid.pearlharbor.util.LifeCycle;
import com.alkaid.pearlharbor.util.ServerConfig;

public class NetSystem implements LifeCycle{
	private TokenPool mTokenPool = null;
	private PacketHandlerManager mPacketHandlerManager = null;
	private HashMap<String, Token> mActiveTokenMap = null;
	
	private boolean mActiveTokenMapChanged = false;
	private List<String> mActiveTokenKeys = null;
	
	private IConnectionManager mConnectionManager = null;
	
	private volatile static NetSystem instance = null;
	public static NetSystem getInstance()
	{
		if (null == instance)
		{
			synchronized(NetSystem.class)
			{
				if (null == instance)
				{
					instance = new NetSystem();
				}
			}
		}
		
		return instance;
	}
	
	private NetSystem()
	{
		mTokenPool = new TokenPool(ServerConfig.MAX_TOKEN_ALLOCATE);
		mPacketHandlerManager = new PacketHandlerManager();
		mActiveTokenMap = new HashMap<String, Token>();
		mConnectionManager = null;
		
		mActiveTokenMapChanged = false;
		mActiveTokenKeys = new ArrayList<String>();
	}
	

	@Override
	public boolean init() {
		// TODO Auto-generated method stub
		
		mPacketHandlerManager.init();
		
		setConnectionType(ConnectionType.TCP_mina);
		mConnectionManager.init();
		
		return true;
	}

	@Override
	public void tick() {
		// TODO Auto-generated method stub
		
		// Token tick
		/*Iterator<Entry<String, Token>> it = mActiveTokenMap.entrySet().iterator();
		Map.Entry<String, Token> entry = null;
		while(it.hasNext())
		{
			entry = (Entry<String, Token>) it.next();
			((Token)entry.getValue()).tick();
		}*/
		synchronized(mActiveTokenMap)
		{
			for (Token t : mActiveTokenMap.values())
			{
				t.tick();
			}
		}
		
		// PacketHandlerManager tick
		mPacketHandlerManager.tick();
		
		// ConnectionManager tick
		mConnectionManager.tick();
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
		mPacketHandlerManager.destroy();

		mConnectionManager.destroy();
	}
	
	public void setConnectionType(ConnectionType type)
	{
		switch(type)
		{
			case TCP_nio: mConnectionManager = new TCPConnectionManager_nio(); break;
			case TCP_mina: mConnectionManager = new TCPConnectionManager_mina(); break;
			case WEBSOCKET: mConnectionManager = new WebSocketConnectionManager(); break;
			default: break;
		}
	}
	
	public TokenPool getTokenPool()
	{
		return mTokenPool;
	}
	
	public IConnectionManager getConnectionManager()
	{
		return mConnectionManager;
	}
	
	public synchronized List<String> getActiveTokenKeys()
	{
		if (mActiveTokenMapChanged)
		{
			mActiveTokenMapChanged = false;
			
			mActiveTokenKeys.clear();
			synchronized(mActiveTokenMap)
			{
				mActiveTokenKeys.addAll(mActiveTokenMap.keySet());
			}
		}
		
		return mActiveTokenKeys;
	}
	
	public synchronized void bindConnection(IConnection connection)
	{
		Token token = mTokenPool.retain();
		if (token != null)
		{
			token.setConnection(connection);
			token.using();
			synchronized(mActiveTokenMap)
			{
				mActiveTokenMap.put(connection.getCid(), token);
			}
			
			mActiveTokenMapChanged = true;
			LoggerSystem.info(LogType.DEFAULT, "NetSystem Bind Connection : " + connection.getCid());
		}
	}
	
	public synchronized void errorConnection(String guid)
	{
		// it seems to be done as this.
		LoggerSystem.error(LogType.DEFAULT, "NetSystem Error Connection : " + guid);
		
		//endConnection(guid);
	}
	
	public synchronized void endConnection(String guid)
	{
		Token token = mActiveTokenMap.get(guid);
		if (token != null)
		{

			synchronized(mActiveTokenMap)
			{
				mActiveTokenMap.remove(guid);
			}
			
			mTokenPool.release(token);

			mActiveTokenMapChanged = true;
		}

		LoggerSystem.info(LogType.DEFAULT, "NetSystem End Connection : " + guid);
	}
	
	public Token getTokenByConnection(String connectionGuid)
	{
		Token token = mActiveTokenMap.get(connectionGuid);
		return token;
	}
	
	public void dispatchHandler(Token token, int packetType, byte[] protoData)
	{
		this.mPacketHandlerManager.handle(token, packetType, protoData);
	}
	
	/*public void handlerMessage(Session connection, Object message)
	{
		// 
		Token token = this.mActiveTokenMap.get(connection.hashCode());
		if (token != null)
		{
			String msg = (String)message;
			String [] arg = msg.split(";");
			String protoData = null;
			if (arg.length == 2)
			{
				protoData = arg[1];
			}
			int type = Integer.parseInt(arg[0]);
			this.mPacketHandlerManager.handle(token, type, protoData);
		}

	}*/

	
}
