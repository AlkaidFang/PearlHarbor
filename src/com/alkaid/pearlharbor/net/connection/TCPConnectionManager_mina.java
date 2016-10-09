package com.alkaid.pearlharbor.net.connection;

import java.io.EOFException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.List;

import org.apache.mina.core.session.IoSession;

import com.alkaid.pearlharbor.logger.LoggerSystem;
import com.alkaid.pearlharbor.logger.LoggerSystem.LogType;
import com.alkaid.pearlharbor.net.NetSystem;
import com.alkaid.pearlharbor.net.PacketFormat;
import com.alkaid.pearlharbor.net.Token;
import com.alkaid.pearlharbor.net.connection.TCPConnectionClasser_mina.MinaConnection;
import com.alkaid.pearlharbor.util.AsyncThread;
import com.alkaid.pearlharbor.util.NetStream;
import com.alkaid.pearlharbor.util.AsyncThread.ThreadCallback;

public class TCPConnectionManager_mina implements IConnectionManager, ThreadCallback{

	private int mConnectionNum = 0;
	private PacketFormat mPacketFormat = null;
	private AsyncThread mSendThread = null;
	
	private MinaConnection mina = null;
	
	@Override
	public boolean init() {
		// TODO Auto-generated method stub
		
		mPacketFormat = new PacketFormat();
		
		mSendThread = new AsyncThread(this);
		mSendThread.Start();
		
		mina = new MinaConnection();
		mina.start();
		
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
		mina.stop();
	}


	@Override
	public ConnectionType getConnectionType() {
		// TODO Auto-generated method stub
		return ConnectionType.TCP_mina;
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
		IoSession session = (IoSession) connection;
		if (session != null)
		{
			IConnection ic = new IConnection();
			ic.setCid(session.getId() + "");
			ic.setReal(connection);
			// 获取客户端的网络地址
			String clientIp = ((InetSocketAddress)session.getRemoteAddress()).getAddress().getHostAddress();
			int clientPort = ((InetSocketAddress)session.getRemoteAddress()).getPort();
			ic.setRemoteIpPort(clientIp, clientPort);
			NetSystem.getInstance().bindConnection(ic);
			++ mConnectionNum;
		}
	}

	@Override
	public void onError(Object connection) {
		// TODO Auto-generated method stub
		IoSession session = (IoSession) connection;
		if (session != null)
		{
			NetSystem.getInstance().errorConnection(session.getId() + "");
		}
	}

	@Override
	public void onEnd(Object connection) {
		// TODO Auto-generated method stub
		IoSession session = (IoSession) connection;
		if (session != null)
		{
			NetSystem.getInstance().endConnection(session.getId() + "");
			-- mConnectionNum;
		}
	}

	@Override
	public void onReceivedData(Object connection, byte[] data) {
		// TODO Auto-generated method stub
		IoSession session = (IoSession) connection;
		if (session != null)
		{
			Token token = NetSystem.getInstance().getTokenByConnection(session.getId() + "");
			token.completeRead(data);
			LoggerSystem.info(LogType.DEFAULT, "received data : " + session.getId() + "   data:" + data);
		}
	}

	@Override
	public void OnReceivedData(Object connection, byte[] data, int length) {
		// TODO Auto-generated method stub
		
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
				IoSession session = (IoSession)token.getConnection().getReal();
				byte[] data = new byte[length];
				System.arraycopy(token.getNetStream().asyncPipeOut(), 0, data, 0, length);
				
				if (session.isConnected())
				{
					try {
						session.write(data);
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
