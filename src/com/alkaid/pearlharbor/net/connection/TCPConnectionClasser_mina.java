package com.alkaid.pearlharbor.net.connection;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteOrder;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.ProtocolDecoderAdapter;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import com.alkaid.pearlharbor.net.NetSystem;
import com.alkaid.pearlharbor.util.ServerConfig;

public class TCPConnectionClasser_mina {

	public static class MinaConnection
	{
		private IoAcceptor mAcceptor = null;
		private IoHandlerAdapter mServerHandler = null;
		
		public void start()
		{
			IoAcceptor macceptor = new NioSocketAcceptor();
			macceptor.getFilterChain().addLast("logger", new LoggingFilter());
			macceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new MinaCodec.Encoder(), new MinaCodec.Decoder()));
			macceptor.getSessionConfig().setReadBufferSize(4096);
			// macceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
			mServerHandler = new MinaServerHandler();
			macceptor.setHandler(mServerHandler);
			
			try {
				macceptor.bind(new InetSocketAddress(ServerConfig.NET_TCP_IP, ServerConfig.NET_TCP_PORT));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	
	private static class MinaServerHandler extends IoHandlerAdapter
	{

		@Override
		public void exceptionCaught(IoSession session, Throwable cause)
				throws Exception {
			// TODO Auto-generated method stub
			NetSystem.getInstance().getConnectionManager().onError(session);
			cause.printStackTrace();
		}

		@Override
		public void messageReceived(IoSession session, Object message)
				throws Exception {
			// TODO Auto-generated method stub
			
			byte[] data = (byte[]) message;
			if (data != null)
			{
				// received message
				NetSystem.getInstance().getConnectionManager().onReceivedData(session, (byte[])message);
			}
		}

		@Override
		public void sessionClosed(IoSession session) throws Exception {
			// TODO Auto-generated method stub
			// offline
			NetSystem.getInstance().getConnectionManager().onEnd(session);
		}

		@Override
		public void sessionOpened(IoSession session) throws Exception {
			// TODO Auto-generated method stub
			
			// online
			NetSystem.getInstance().getConnectionManager().onBind(session);
		}
	}
	
	
	protected static class MinaCodec
	{
		public static class Encoder extends ProtocolEncoderAdapter
		{
	
			@Override
			public void encode(IoSession arg0, Object arg1,
					ProtocolEncoderOutput arg2) throws Exception {
				// TODO Auto-generated method stub
				if (arg1 instanceof byte[])
				{
					byte[] data = (byte[]) arg1;
					IoBuffer buffer = IoBuffer.allocate(data.length);
					buffer.put(data);
					buffer.flip();
					arg2.write(buffer);
				}
			}
		}
		
		public static class Decoder extends ProtocolDecoderAdapter
		{
			@Override
			public void decode(IoSession arg0, IoBuffer arg1,
					ProtocolDecoderOutput arg2) throws Exception {
				// TODO Auto-generated method stub
				IoBuffer buffer = arg1;
				buffer.order(ByteOrder.LITTLE_ENDIAN);
				int length = buffer.limit();
				byte[] data = new byte[length];
				buffer.get(data);
				arg2.write(data);
			}
		}
	}

}
