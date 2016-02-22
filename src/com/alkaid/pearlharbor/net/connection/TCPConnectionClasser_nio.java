package com.alkaid.pearlharbor.net.connection;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

import com.alkaid.pearlharbor.net.NetSystem;
import com.alkaid.pearlharbor.util.AsyncThread;
import com.alkaid.pearlharbor.util.AsyncThread.ThreadCallback;
import com.alkaid.pearlharbor.util.ServerConfig;

public class TCPConnectionClasser_nio {

	public static class NIOConnection
	{
		private class SelectThreadCallback implements ThreadCallback
		{
			private ByteBuffer buffer = ByteBuffer.allocate(ServerConfig.MAX_SOCKET_BUFFER_SIZE);
			private byte[] data = new byte[ServerConfig.MAX_SOCKET_BUFFER_SIZE];
			private int offset = 0;
			private int length = 0;
			
			@Override
			public void onThreadLogicContext(AsyncThread thread) {
				// TODO Auto-generated method stub
				
				try {
					while(mSelector.select() > 0) // This method performs a blocking selection operation. It returns only after at least one channel is selected
					{
						for (SelectionKey sk : mSelector.selectedKeys())
						{
							mSelector.selectedKeys().remove(sk);
							
							if (sk.isAcceptable())
							{
								SocketChannel channel = mChannel.accept();
								channel.configureBlocking(false);
								SelectionKey key = channel.register(mSelector, SelectionKey.OP_READ);
								//channel.register(mSelector, SelectionKey.OP_WRITE);
								// bind a connection
								NetSystem.getInstance().getConnectionManager().onBind(channel);
							}
							
							if (sk.isReadable())
							{
								SocketChannel channel = (SocketChannel) sk.channel();
								buffer.clear();
								offset = 0;
								try
								{
									while ((length = channel.read(buffer)) > 0)
									{
										if (offset + length > data.length)
										{
											byte[] temp = new byte[data.length * 2];
											System.arraycopy(data, 0, temp, 0, data.length);
											data = temp;
										}
										buffer.flip();
										System.arraycopy(buffer.array(), 0, data, offset, length);
										offset += length;
										
										buffer.clear();
									}
									
									NetSystem.getInstance().getConnectionManager().OnReceivedData(channel, data, offset);
									
									//sk.interestOps(SelectionKey.OP_READ);
								}
								catch (IOException e)
								{
									e.printStackTrace();
									// disconnect;
									NetSystem.getInstance().getConnectionManager().onError(channel);
									
									sk.cancel();
									if (sk.channel() != null)
									{
										sk.channel().close();
									}
									
									NetSystem.getInstance().getConnectionManager().onEnd(channel);
								}
							}
						}
					}
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			public void onThreadFinishedContext() {
				// TODO Auto-generated method stub
				
			}
			
		}
		
		private Selector mSelector = null;
		ServerSocketChannel mChannel = null;
		
		private Charset mCharset = Charset.forName("UTF-8");
		
		private AsyncThread mSelectThread = null;
		
		public void start()
		{
			try
			{
				mSelector = Selector.open();
				mChannel = ServerSocketChannel.open();
				InetSocketAddress isa = new InetSocketAddress(ServerConfig.NET_TCP_IP, ServerConfig.NET_TCP_PORT);
				
				mChannel.bind(isa);
				mChannel.configureBlocking(false);
				mChannel.register(mSelector, SelectionKey.OP_ACCEPT);
	
				mSelectThread = new AsyncThread(new SelectThreadCallback());
				mSelectThread.Start();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}

		
	}
	
}
