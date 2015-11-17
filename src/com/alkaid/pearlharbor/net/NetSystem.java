package com.alkaid.pearlharbor.net;

import java.util.HashMap;

import javax.websocket.Session;
import javax.websocket.RemoteEndpoint;

import com.alkaid.pearlharbor.game.Game;
import com.alkaid.pearlharbor.logger.LoggerSystem;
import com.alkaid.pearlharbor.logger.LoggerSystem.LogType;
import com.alkaid.pearlharbor.util.LifeCycle;
import com.alkaid.pearlharbor.util.ServerConfig;

public class NetSystem implements LifeCycle{
	private TokenPool mTokenPool = null;
	private PacketHandlerManager mPacketHandlerManager = null;
	private HashMap<Integer, Token> mActiveTokenMap = null;
	
	private boolean bSendThreadRunning = false;
	
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
		mActiveTokenMap = new HashMap<Integer, Token>();
		
		
	}
	

	@Override
	public boolean init() {
		// TODO Auto-generated method stub
		
		mPacketHandlerManager.init();
		bSendThreadRunning = true;
		
		startSendThread();
		
		return true;
	}

	@Override
	public void tick() {
		// TODO Auto-generated method stub
		
		synchronized(mActiveTokenMap)
		{
			for (Token t : mActiveTokenMap.values())
			{
				t.tick();
			}
		}
		
		mPacketHandlerManager.tick();
	}

	@Override
	public boolean destroy() {
		// TODO Auto-generated method stub
		
		mPacketHandlerManager.destroy();
		
		bSendThreadRunning = false;
		
		return true;
	}
	
	private void startSendThread()
	{
		Thread sendThread = new Thread(new Runnable()
		{

			@Override
			public void run() {
				// TODO Auto-generated method stub
				while(bSendThreadRunning)
				{
					// check and send users packet
					checkAndSendPacket();
					
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		
		sendThread.start();
	}
	
	private void checkAndSendPacket()
	{
		for (Token t : mActiveTokenMap.values())
		{
			t.checkAndSendPacket();
		}
	}
	
	public void bindConnection(Session connection)
	{
		if (connection != null)
		{
			Token token = mTokenPool.retain();
			if (token != null)
			{
				token.using();
				token.setEndpoint(connection.getAsyncRemote());
				mActiveTokenMap.put(connection.hashCode(), token);
				
				LoggerSystem.info(LogType.DEFAULT, "Bind Connection : " + connection.hashCode());
				
			}
		}
	}
	
	public void endConnection(Session connection)
	{
		Token token = mActiveTokenMap.get(connection.hashCode());
		if (token != null)
		{
			mActiveTokenMap.remove(connection.hashCode());
			
			mTokenPool.release(token);
		}

		LoggerSystem.info(LogType.DEFAULT, "End Connection : " + connection.hashCode());
	}
	
	public void errorConnection(Session connection)
	{
		// it seems to be done as this.
		LoggerSystem.error(LogType.DEFAULT, "Error Connection : " + connection.hashCode());
		
		endConnection(connection);
	}
	
	public void handlerMessage(Session connection, Object message)
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

	}

	
}
