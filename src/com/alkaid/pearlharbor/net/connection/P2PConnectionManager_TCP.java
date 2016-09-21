package com.alkaid.pearlharbor.net.connection;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import org.apache.mina.core.session.IoSession;

import com.alkaid.pearlharbor.logger.LoggerSystem;
import com.alkaid.pearlharbor.logger.LoggerSystem.LogType;
import com.alkaid.pearlharbor.net.NetSystem;
import com.alkaid.pearlharbor.net.PacketFormat;
import com.alkaid.pearlharbor.net.Token;
import com.alkaid.pearlharbor.util.AsyncThread;
import com.alkaid.pearlharbor.util.AsyncThread.ThreadCallback;
import com.alkaid.pearlharbor.util.NetStream;
import com.alkaid.pearlharbor.util.ServerConfig;

public class P2PConnectionManager_TCP implements IConnectionManager, ThreadCallback{

	private int mConnectionNum = 0;
	private PacketFormat mPacketFormat = null;

	private AsyncThread mBindThread = null;
	
	
	@Override
	public boolean init() {
		// TODO Auto-generated method stub
		
		mPacketFormat = new PacketFormat();
		mBindThread = new AsyncThread(this);
		mBindThread.Start();
		
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

	@Override
	public void onThreadLogicContext(AsyncThread thread) {
		// TODO Auto-generated method stub
	    /*Socket socket;  
	    BufferedReader in;  
	    PrintWriter out;
		ServerSocket holeMasker = new ServerSocket(ServerConfig.NET_TCP_PORT_P2P, 100, InetAddress.getByName(ServerConfig.NET_TCP_IP));
		holeMasker.setReuseAddress(true);
		while (thread.IsWorking())
		{
            socket = holeMasker.accept();  
              
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));  
            out = new PrintWriter(socket.getOutputStream(), true);  
            String line = in.readLine();  
              
            System.out.println("you input is : " + line);  
              
            //out.println("you input is :" + line);  
              
            out.close();  
            in.close();  
            socket.close();  
              
            if(line.equalsIgnoreCase("quit") || line.equalsIgnoreCase("exit"))  
                break;
              
            //ss.close();  
		}
		
		*/
	}

	@Override
	public void onThreadFinishedContext() {
		// TODO Auto-generated method stub
		
	}
}

