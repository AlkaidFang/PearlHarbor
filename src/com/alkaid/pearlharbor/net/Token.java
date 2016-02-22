package com.alkaid.pearlharbor.net;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

import javax.websocket.RemoteEndpoint;
import javax.websocket.SendHandler;
import javax.websocket.SendResult;

import com.alkaid.pearlharbor.logger.LoggerSystem;
import com.alkaid.pearlharbor.logger.LoggerSystem.LogType;
import com.alkaid.pearlharbor.net.connection.IConnection;
import com.alkaid.pearlharbor.playersystem.Player;
import com.alkaid.pearlharbor.util.LifeCycle;
import com.alkaid.pearlharbor.util.NetStream;
import com.alkaid.pearlharbor.util.ServerConfig;

public class Token implements LifeCycle{
	
	private boolean bUsing = false;
	
	private RemoteEndpoint mRemoteEndpoint;
	private IConnection mConnection;
	private NetStream mNetStream;
	private Queue<IPacket> mSendMessageQueue;
	
//	private Byte[] mReadBuffer;
//	private int mReadBufferOffset;
//	
//	private Byte[] mWriteBuffer;
//	private int mWriteBufferOffset;
	
	private Semaphore tempSemaphore = null;
	
	private Player mBindPlayer;
	
	private boolean bCanSend = false;
	
	// function
	public Token()
	{
		bUsing = false;
		mRemoteEndpoint = null;
		mConnection = null;
		mNetStream = new NetStream(ServerConfig.MAX_SOCKET_BUFFER_SIZE * 2);
//		mReadBuffer = new Byte[ServerConfig.MAX_WEBSOCKET_BUFFER_SIZE * 2];
//		mReadBufferOffset = 0;
//		mWriteBuffer = new Byte[ServerConfig.MAX_WEBSOCKET_BUFFER_SIZE * 2];
//		mWriteBufferOffset = 0;
		
		tempSemaphore = new Semaphore(1);
		
		mBindPlayer = null;
		
		mSendMessageQueue = new LinkedList<IPacket>();
	}


	@Override
	public boolean init() {
		// TODO Auto-generated method stub
		
		bCanSend = true;
		
		return true;
	}


	@Override
	public void tick() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
		
	}
	
	public void reset()
	{
		bUsing = false;
		mRemoteEndpoint = null;
		mConnection = null;
		mBindPlayer = null;
		
		if (tempSemaphore != null)
		{
			if (!tempSemaphore.tryAcquire())
			{
				tempSemaphore.release();
			}
		}
		
	}
	
	public void using()
	{
		bUsing = true;
	}
	
	public void unUsed()
	{
		bUsing = false;
	}
	
	public boolean isUsing()
	{
		return bUsing;
	}
	
	public void setEndpoint(RemoteEndpoint ep)
	{
		this.mRemoteEndpoint = ep;
	}
	
	public RemoteEndpoint getRemoteEndpoint()
	{
		return this.mRemoteEndpoint;
	}
	
	public void setConnection(IConnection ic)
	{
		this.mConnection = ic;
	}
	
	public IConnection getConnection()
	{
		return this.mConnection;
	}
	
	public NetStream getNetStream()
	{
		return mNetStream;
	}
	
	public void formatSendPacket(PacketFormat pf)
	{
		synchronized(mSendMessageQueue)
		{
			byte[] buffer = null;
			for (IPacket packet : mSendMessageQueue)
			{
				buffer = pf.GenerateBuffer(packet);
				
				mNetStream.PushOutStream(buffer);
			}
			
			mSendMessageQueue.clear();
		}
	}
	
	public void completeRead(byte[] data)
	{
		mNetStream.PushInStream(data);
	}
	
	public void completeRead(byte[] data, int length)
	{
		mNetStream.PushInStream(data, length);
	}
	
	public void completeSend(int length)
	{
		mNetStream.FinishedOut(length);
	}
	
	public void sendPacket(IPacket packet)
	{
		synchronized(mSendMessageQueue)
		{
			mSendMessageQueue.offer(packet);
		}
	}
	
	public void bindPlayer(Player player)
	{
		if (null == mBindPlayer)
		{
			mBindPlayer = player;
		}
		else
		{
			// cause error, this token has a player
			LoggerSystem.error(LogType.LOGIN, "This token already has a player!");
		}
	}
	
	public Player getPlayer()
	{
		return mBindPlayer;
	}
	
	public boolean getCanSend()
	{
		return bCanSend;
	}
	
	public void setCanSend(boolean arg)
	{
		bCanSend = arg;
	}
}
