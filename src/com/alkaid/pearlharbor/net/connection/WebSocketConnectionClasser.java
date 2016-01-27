package com.alkaid.pearlharbor.net.connection;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.MessageHandler;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import javax.websocket.server.ServerApplicationConfig;
import javax.websocket.server.ServerEndpointConfig;

import com.alkaid.pearlharbor.net.NetSystem;

/**
 * @author fangjun
 * @description TODO
 * @date 2016年1月25日
 */
public class WebSocketConnectionClasser {
	
	
	
	public static class MyEndpointConfig implements ServerApplicationConfig {
		
	    @Override
	    public Set<ServerEndpointConfig> getEndpointConfigs(
	            Set<Class<? extends Endpoint>> scanned) {

	        Set<ServerEndpointConfig> result = new HashSet<>();

	        // Endpoint subclass config

	        if (scanned.contains(MyEndpoint.class)) {
	            result.add(ServerEndpointConfig.Builder.create(
	            		MyEndpoint.class,
	                    "/MyEndpoint").build());
	        }
	        
	        if (scanned.contains(GameEndpoint.class)) {
	            result.add(ServerEndpointConfig.Builder.create(
	            		GameEndpoint.class,
	                    "/Game").build());
	        }

	        return result;
	    }


	    @Override
	    public Set<Class<?>> getAnnotatedEndpointClasses(Set<Class<?>> scanned) {
	        // Deploy all WebSocket endpoints defined by annotations in the examples
	        // web application. Filter out all others to avoid issues when running
	        // tests on Gump
	    	

	        // Annotated config
	    	
	        Set<Class<?>> results = new HashSet<>();
	        for (Class<?> clazz : scanned) {
	            if (clazz.getPackage().getName().startsWith("com。alkaid.pearlharbor.")) {
	                results.add(clazz);
	            }
	        }
	        return results;
	    }
	}
	
	public static class MyEndpoint extends Endpoint{
		
		@Override
		public void onClose(Session session, CloseReason closeReason) {
			// TODO Auto-generated method stub
			super.onClose(session, closeReason);

			System.out.println("MyEndpoint OnClose");
		}

		@Override
		public void onError(Session session, Throwable throwable) {
			// TODO Auto-generated method stub
			super.onError(session, throwable);
			System.out.println("MyEndpoint OnError");
		}

		@Override
		public void onOpen(Session arg0, javax.websocket.EndpointConfig arg1) {
			// TODO Auto-generated method stub

			RemoteEndpoint.Basic remoteEndpointBasic = arg0.getBasicRemote();
			arg0.addMessageHandler(new EchoMessageHandler(remoteEndpointBasic));
			System.out.println("MyEndpoint OpOpen");
		}
		
		private class EchoMessageHandler implements MessageHandler.Whole<String>
		{
			private RemoteEndpoint.Basic m_remote = null;
			
			private EchoMessageHandler(RemoteEndpoint.Basic remote)
			{
				m_remote = remote;
			}
			
			@Override
			public void onMessage(String arg0) {
				// TODO Auto-generated method stub
				if (m_remote != null)
				{
					System.out.println("==============server revieve:" + arg0);
					try {
						m_remote.sendText("server:" + arg0);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
		}

	}
	
	public static class GameEndpoint extends Endpoint {

		@Override
		public void onClose(Session session, CloseReason closeReason) {
			// TODO Auto-generated method stub
			
			NetSystem.getInstance().getConnectionManager().onEnd(session);
			super.onClose(session, closeReason);
			
		}

		@Override
		public void onError(Session session, Throwable throwable) {
			// TODO Auto-generated method stub
			NetSystem.getInstance().getConnectionManager().onError(session);
			super.onError(session, throwable);
		}

		@Override
		public void onOpen(Session arg0, javax.websocket.EndpointConfig arg1) {
			// TODO Auto-generated method stub
			
			// bind a connection
			NetSystem.getInstance().getConnectionManager().onBind(arg0);
			// add message handler
			arg0.addMessageHandler(new GameMessageHandlerString(arg0));
			arg0.addMessageHandler(new GameMessageHandlerData(arg0));
		}
		
		private class GameMessageHandlerString implements MessageHandler.Whole<String>
		{
			private Session mConnectSession = null;
			private GameMessageHandlerString(Session session)
			{
				mConnectSession = session;
			}

			@Override
			public void onMessage(String arg0) 
			{
				// TODO Auto-generated method stub
				//NetSystem.getInstance().handlerMessage(mConnectSession, arg0);
				System.out.println("GameMessageHandlerString recieved:" + arg0);
			}
		}
		
		private class GameMessageHandlerData implements MessageHandler.Whole<byte[]>
		{
			private Session mConnectSession = null;
			private GameMessageHandlerData(Session session)
			{
				mConnectSession = session;
			}

			@Override
			public void onMessage(byte[] arg0) {
				// TODO Auto-generated method stub

				System.out.println("GameMessageHandlerData recieved:" + arg0);
				NetSystem.getInstance().getConnectionManager().onReceivedData(mConnectSession, arg0);
			}
			
		}

	}

}
	



