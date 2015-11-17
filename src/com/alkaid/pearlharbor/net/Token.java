package com.alkaid.pearlharbor.net;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

import javax.websocket.RemoteEndpoint;
import javax.websocket.RemoteEndpoint.Basic;
import javax.websocket.SendHandler;
import javax.websocket.SendResult;

import com.alkaid.pearlharbor.logger.LoggerSystem;
import com.alkaid.pearlharbor.logger.LoggerSystem.LogType;
import com.alkaid.pearlharbor.playersystem.Player;
import com.alkaid.pearlharbor.util.LifeCycle;
import com.alkaid.pearlharbor.util.ServerConfig;

public class Token implements LifeCycle{
	
	private class AsyncSendHandler implements SendHandler
	{
		@Override
		public void onResult(SendResult arg0) {
			// TODO Auto-generated method stub
			Token.this.setCanSend(true);
			if (arg0.isOK())
			{
				Token.this.finishedSend();
			}
		}
	}
	
	private boolean bUsing = false;
	
	private RemoteEndpoint mRemoteEndpoint;
	
//	private Byte[] mReadBuffer;
//	private int mReadBufferOffset;
//	
//	private Byte[] mWriteBuffer;
//	private int mWriteBufferOffset;
	
	private Semaphore tempSemaphore = null;
	
	private Player mBindPlayer;
	
	private Queue<IPacket> mSendMessageQueue;
	private boolean bCanSend = false;
	private AsyncSendHandler mSendCallback;
	
	// function
	public Token()
	{
		bUsing = false;
		mRemoteEndpoint = null;
//		mReadBuffer = new Byte[ServerConfig.MAX_WEBSOCKET_BUFFER_SIZE * 2];
//		mReadBufferOffset = 0;
//		mWriteBuffer = new Byte[ServerConfig.MAX_WEBSOCKET_BUFFER_SIZE * 2];
//		mWriteBufferOffset = 0;
		
		tempSemaphore = new Semaphore(1);
		
		mBindPlayer = null;
		
		mSendMessageQueue = new LinkedList<IPacket>();
		mSendCallback = new AsyncSendHandler();
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
	public boolean destroy() {
		// TODO Auto-generated method stub
		
		
		return true;
	}
	
	public void reset()
	{
		bUsing = false;
		mRemoteEndpoint = null;
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
	
	public void setEndpoint(RemoteEndpoint ep)
	{
		this.mRemoteEndpoint = ep;
	}
	
	public RemoteEndpoint getRemoteEndpoint()
	{
		return this.mRemoteEndpoint;
	}
	
	public void sendPacket(IPacket packet)
	{
		/// send packet, this send message will be send into an queue
		String msg = packet.encode();
//		try {
//			((Basic) mRemoteEndpoint).sendText(msg);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		mSendMessageQueue.offer(packet);
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
	
	private void setCanSend(boolean arg)
	{
		bCanSend = arg;
	}
	
	private void finishedSend()
	{
		this.mSendMessageQueue.poll();
	}
	private void startSend(String message)
	{
		((RemoteEndpoint.Async)this.getRemoteEndpoint()).sendText(message, this.mSendCallback);
		this.setCanSend(false);// 这个保证了每次只能发送一个包，必须等到有返回才会发送第二个
	}
	
	public void checkAndSendPacket()
	{
		if (this.getCanSend())
		{
			if (!this.mSendMessageQueue.isEmpty())
			{
				IPacket packet = this.mSendMessageQueue.peek();
				String msg = packet.encode();
				System.out.println("Token checkAndSendPacket:" + msg);
				startSend(msg);
			}
		}
	}
	
}
