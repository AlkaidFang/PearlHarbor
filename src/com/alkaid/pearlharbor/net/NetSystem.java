package com.alkaid.pearlharbor.net;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.websocket.Session;
import javax.websocket.RemoteEndpoint;

import com.alkaid.pearlharbor.game.Game;
import com.alkaid.pearlharbor.logger.LoggerSystem;
import com.alkaid.pearlharbor.logger.LoggerSystem.LogType;
import com.alkaid.pearlharbor.net.connection.ConnectionType;
import com.alkaid.pearlharbor.net.connection.IConnection;
import com.alkaid.pearlharbor.net.connection.IConnectionManager;
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
		
		setConnection(ConnectionType.WEBSOCKET);
		mConnectionManager.init();
		
		return true;
	}

	@Override
	public void tick() {
		// TODO Auto-generated method stub
		
		// Token tick
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
	
	public void setConnection(ConnectionType type)
	{
		switch(type)
		{
			case TCP: break;
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
	
	public List<String> getActiveTokenKeys()
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
	
	public void bindConnection(IConnection connection)
	{
		Token token = mTokenPool.retain();
		if (token != null)
		{
			token.setConnection(connection);
			token.using();
			mActiveTokenMap.put(connection.getCid(), token);
			
			mActiveTokenMapChanged = true;
			LoggerSystem.info(LogType.DEFAULT, "NetSystem Bind Connection : " + connection.getCid());
		}
	}
	
	public void errorConnection(String guid)
	{
		// it seems to be done as this.
		LoggerSystem.error(LogType.DEFAULT, "NetSystem Error Connection : " + guid);
		
		//endConnection(guid);
	}
	
	public void endConnection(String guid)
	{
		Token token = mActiveTokenMap.get(guid);
		if (token != null)
		{
			mActiveTokenMap.remove(guid);
			
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
