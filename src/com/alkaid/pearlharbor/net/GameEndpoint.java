package com.alkaid.pearlharbor.net;

import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;

public class GameEndpoint extends Endpoint{

	@Override
	public void onClose(Session session, CloseReason closeReason) {
		// TODO Auto-generated method stub
		super.onClose(session, closeReason);
		
		NetSystem.getInstance().endConnection(session);
	}

	@Override
	public void onError(Session session, Throwable throwable) {
		// TODO Auto-generated method stub
		super.onError(session, throwable);
		
		NetSystem.getInstance().errorConnection(session);
	}

	@Override
	public void onOpen(Session arg0, EndpointConfig arg1) {
		// TODO Auto-generated method stub
		
		// bind a connection
		NetSystem.getInstance().bindConnection(arg0);
		// add message handler
		arg0.addMessageHandler(new GameMessageHandler(arg0));
	}

	private class GameMessageHandler implements MessageHandler.Whole<String>
	{
		private Session mConnectSession = null;
		private GameMessageHandler(Session session)
		{
			mConnectSession = session;
		}

		@Override
		public void onMessage(String arg0) 
		{
			// TODO Auto-generated method stub
			NetSystem.getInstance().handlerMessage(mConnectSession, arg0);
		}
		
	}
}
