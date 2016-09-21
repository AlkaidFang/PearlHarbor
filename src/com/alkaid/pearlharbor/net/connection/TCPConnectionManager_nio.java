package com.alkaid.pearlharbor.net.connection;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;

import com.alkaid.pearlharbor.logger.LoggerSystem;
import com.alkaid.pearlharbor.logger.LoggerSystem.LogType;
import com.alkaid.pearlharbor.net.NetSystem;
import com.alkaid.pearlharbor.net.PacketFormat;
import com.alkaid.pearlharbor.net.Token;
import com.alkaid.pearlharbor.net.connection.TCPConnectionClasser_nio.NIOConnection;
import com.alkaid.pearlharbor.util.AsyncThread;
import com.alkaid.pearlharbor.util.NetStream;
import com.alkaid.pearlharbor.util.AsyncThread.ThreadCallback;
import com.alkaid.pearlharbor.util.ServerConfig;

public class TCPConnectionManager_nio implements IConnectionManager, ThreadCallback{
	
	private int mConnectionNum = 0;
	private PacketFormat mPacketFormat = null;
	private AsyncThread mSendThread = null;
	
	private NIOConnection nioc = null;

	@Override
	public boolean init() {
		// TODO Auto-generated method stub
		mPacketFormat = new PacketFormat();
		
		mSendThread = new AsyncThread(this);
		mSendThread.Start();
		
		nioc = new NIOConnection();
		nioc.start();
		
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
		
	}

	@Override
	public ConnectionType getConnectionType() {
		// TODO Auto-generated method stub
		return ConnectionType.TCP_nio;
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
		
	}

	@Override
	public int connectionNum() {
		// TODO Auto-generated method stub
		return mConnectionNum;
	}

	@Override
	public void onBind(Object connection) {
		// TODO Auto-generated method stub
		SocketChannel channel = (SocketChannel) connection;
		if (channel != null)
		{
			IConnection ic = new IConnection();
			ic.setReal(channel);
			ic.setCid(channel.hashCode() + "");
			NetSystem.getInstance().bindConnection(ic);
			
			++ mConnectionNum;
		}
	}

	@Override
	public void onError(Object connection) {
		// TODO Auto-generated method stub
		SocketChannel channel = (SocketChannel) connection;
		if (channel != null)
		{
			NetSystem.getInstance().errorConnection(channel.hashCode() + "");
		}
	}

	@Override
	public void onEnd(Object connection) {
		// TODO Auto-generated method stub
		SocketChannel channel = (SocketChannel) connection;
		if (channel != null)
		{
			NetSystem.getInstance().endConnection(channel.hashCode() + "");
			-- mConnectionNum;
		}
	}

	@Override
	public void onReceivedData(Object connection, byte[] data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnReceivedData(Object connection, byte[] data, int length) {
		// TODO Auto-generated method stub
		SocketChannel channel = (SocketChannel) connection;
		if (channel != null)
		{
			Token token = NetSystem.getInstance().getTokenByConnection(channel.hashCode() + "");
			token.completeRead(data, length);
			LoggerSystem.info(LogType.DEFAULT, "received data : " + channel.hashCode() + "   data:" + data + " length:" + length);
		}
	}
	
	private void processDecode()
	{
		List<String> activeTokenKeys = NetSystem.getInstance().getActiveTokenKeys();
		Token token = null;
		String key = null;
		for (int i = 0; i < activeTokenKeys.size(); ++i)
		{
			key = activeTokenKeys.get(i);
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
	
	private void processSend()
	{
		List<String> activeTokenKeys = NetSystem.getInstance().getActiveTokenKeys();
		Token token = null;
		String key = null;
		for (int i = 0; i < activeTokenKeys.size(); ++i)
		{
			key = activeTokenKeys.get(i);
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
				SocketChannel channel = (SocketChannel)token.getConnection().getReal();
				ByteBuffer bb = ByteBuffer.wrap(token.getNetStream().asyncPipeOut(), 0, length);
				if (channel.isConnected())
				{
					try {
						int count = 100;
						while (bb.hasRemaining() && count-- > 0)
						{
							int len = channel.write(bb);
							if (len < 0)
							{
								throw new EOFException();
							}
						}
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					token.completeSend(length);
				}
			
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
