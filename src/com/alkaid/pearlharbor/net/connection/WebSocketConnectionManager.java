package com.alkaid.pearlharbor.net.connection;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.RemoteEndpoint;
import javax.websocket.SendHandler;
import javax.websocket.SendResult;
import javax.websocket.Session;

import com.alkaid.pearlharbor.logger.LoggerSystem;
import com.alkaid.pearlharbor.logger.LoggerSystem.LogType;
import com.alkaid.pearlharbor.net.NetSystem;
import com.alkaid.pearlharbor.net.PacketFormat;
import com.alkaid.pearlharbor.net.Token;
import com.alkaid.pearlharbor.util.AsyncThread;
import com.alkaid.pearlharbor.util.AsyncThread.ThreadCallback;
import com.alkaid.pearlharbor.util.NetStream;

public class WebSocketConnectionManager implements IConnectionManager, ThreadCallback{
	
	private class AsyncSendHandler implements SendHandler
	{
		private Token mToken;
		private int mLength;
		
		public AsyncSendHandler(Token t)
		{
			mToken = t;
		}
		
		public AsyncSendHandler setLength(int length)
		{
			mLength = length;
			return this;
		}
		
		@Override
		public void onResult(SendResult arg0) {
			// TODO Auto-generated method stub
			if (mToken.isUsing())
			{
				mToken.setCanSend(true);
				if (arg0.isOK())
				{
					mToken.completeSend(mLength);
				}
			}
			
		}
	}
	
	private int mConnectionNum = 0;
	private PacketFormat mPacketFormat = null;
	private AsyncThread mSendThread = null;
	private ConcurrentHashMap<String, AsyncSendHandler> mAsyncSendHandlerMap;

	private volatile static WebSocketConnectionManager instance = null;
	public static WebSocketConnectionManager getInstance()
	{
		if (null == instance)
		{
			synchronized(NetSystem.class)
			{
				if (null == instance)
				{
					instance = new WebSocketConnectionManager();
				}
			}
		}
		
		return instance;
	}

	@Override
	public boolean init() {
		// TODO Auto-generated method stub
		
		mPacketFormat = new PacketFormat();
		mSendThread = new AsyncThread(this);
		mAsyncSendHandlerMap = new ConcurrentHashMap<String, AsyncSendHandler>();
		
		return true;
	}

	@Override
	public void tick() {
		// TODO Auto-generated method stub
		
		processDecode();
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
		mSendThread.Stop();
	}

	@Override
	public ConnectionType getConnectionType() {
		// TODO Auto-generated method stub
		return ConnectionType.WEBSOCKET;
	}

	@Override
	public void accept() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void recieve() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void send() {
		// TODO Auto-generated method stub
		mSendThread.Start();
	}

	@Override
	public int connectionNum() {
		// TODO Auto-generated method stub
		return mConnectionNum;
	}

	@Override
	public void onBind(Object connection) {
		// TODO Auto-generated method stub
		Session session = (Session)connection;
		if (session != null)
		{
			IConnection ic = new WebSocketConnection();
			ic.setReal(session.getAsyncRemote());
			ic.setCid(session.getId());
			NetSystem.getInstance().bindConnection(ic);
			
			++ mConnectionNum;
		}
	}

	@Override
	public void onError(Object connection) {
		// TODO Auto-generated method stub
		Session session = (Session)connection;
		if (session != null)
		{
			NetSystem.getInstance().errorConnection(session.getId());
		}
	}
	
	@Override
	public void onEnd(Object connection) {
		// TODO Auto-generated method stub
		Session session = (Session)connection;
		if (session != null)
		{
			NetSystem.getInstance().endConnection(session.getId());
			-- mConnectionNum;
			removeAsyncSendHandler(session.getId());
		}
	}

	@Override
	public void onReceivedData(Object connection, byte[] data) {
		// TODO Auto-generated method stub
		Session session = (Session)connection;
		if (session != null)
		{
			Token token = NetSystem.getInstance().getTokenByConnection(session.getId());
			token.completeRead(data);
			LoggerSystem.info(LogType.DEFAULT, "received data : " + session.getId() + "   data:" + data);
		}
	}
	
	private void processDecode()
	{
		List<String> activeTokenKeys = NetSystem.getInstance().getActiveTokenKeys();
		Token token = null;
		for (String key : activeTokenKeys)
		{
			token = NetSystem.getInstance().getTokenByConnection(key);
			if (token == null || !token.isUsing())
			{
				continue;
			}
			
			NetStream netStream = token.getNetStream();
			
			// decode
			while(netStream.inStreamLength() > 0 && mPacketFormat.CheckHavePacket(netStream.inStream(), netStream.inStreamLength()))
			{
				// read
				PacketFormat.DecodeResult dr = mPacketFormat.DecodePacket(netStream.inStream());

				// handle
				NetSystem.getInstance().dispatchHandler(token, dr.mPacketType, dr.mProtoData);
				
				// offset
				netStream.PopInStream(dr.mPacketLength);
			}
			
		}
	}
	
	private AsyncSendHandler getAsyncSendHandler(Token token)
	{
		AsyncSendHandler ret = null;
		String cid = token.getConnection().getCid();
		synchronized(mAsyncSendHandlerMap)
		{
			if (mAsyncSendHandlerMap.containsKey(cid))
			{
				ret = mAsyncSendHandlerMap.get(cid);
			}
			else
			{
				ret = new AsyncSendHandler(token);
				mAsyncSendHandlerMap.put(cid, ret);
			}
		}
		
		return ret;
	}
	
	private void removeAsyncSendHandler(String guid)
	{
		synchronized(mAsyncSendHandlerMap)
		{
			if (mAsyncSendHandlerMap.containsKey(guid))
			{
				mAsyncSendHandlerMap.remove(guid);
			}
		}
	}
	
	private void processSend()
	{
		List<String> activeTokenKeys = NetSystem.getInstance().getActiveTokenKeys();
		Token token = null;
		for (String key : activeTokenKeys)
		{
			token = NetSystem.getInstance().getTokenByConnection(key);
			if (token == null || !token.isUsing())
			{
				continue;
			}
			
			// 
			token.formatSendPacket(mPacketFormat);
			
			int length = token.getNetStream().OutStreamLength();
			if (token.getCanSend() && token.getNetStream().asyncPipeInIdle() && length > 0)
			{
				RemoteEndpoint re = (RemoteEndpoint)token.getConnection().getReal();
				
				ByteBuffer bb = ByteBuffer.wrap(token.getNetStream().asyncPipeOut(), 0, length);
				((RemoteEndpoint.Async)re).sendBinary(bb, getAsyncSendHandler(token).setLength(length));
				token.setCanSend(false);
			}
		}
	}

	@Override
	public void onThreadLogicContext(AsyncThread thread) {
		// TODO Auto-generated method stub
		while (thread.IsWorking())
		{
			processSend();
			
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onThreadFinishedContext() {
		// TODO Auto-generated method stub
		
	}

}


