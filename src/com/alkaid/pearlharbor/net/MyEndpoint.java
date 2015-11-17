package com.alkaid.pearlharbor.net;

import java.io.IOException;

import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;

public class MyEndpoint extends Endpoint{

	
	@Override
	public void onClose(Session session, CloseReason closeReason) {
		// TODO Auto-generated method stub
		super.onClose(session, closeReason);
		
	}

	@Override
	public void onError(Session session, Throwable throwable) {
		// TODO Auto-generated method stub
		super.onError(session, throwable);

	}

	@Override
	public void onOpen(Session arg0, EndpointConfig arg1) {
		// TODO Auto-generated method stub

		RemoteEndpoint.Basic remoteEndpointBasic = arg0.getBasicRemote();
		arg0.addMessageHandler(new EchoMessageHandler(remoteEndpointBasic));
	}
	
	private static class EchoMessageHandler implements MessageHandler.Whole<String>
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
