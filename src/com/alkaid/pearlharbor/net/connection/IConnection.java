package com.alkaid.pearlharbor.net.connection;

import javax.websocket.RemoteEndpoint;

public interface IConnection {
	
	void setCid(String cid);
	
	String getCid();
	
	void setReal(Object arg0);
	
	Object getReal();
}

class WebSocketConnection implements IConnection
{
	private String mCid;
	private RemoteEndpoint mRemoteEndpoint;
	
	public WebSocketConnection()
	{
		mCid = "";
		mRemoteEndpoint = null;
	}

	@Override
	public void setCid(String cid)
	{
		mCid = cid;
	}
	
	@Override
	public String getCid()
	{
		return mCid;
	}
	
	@Override
	public void setReal(Object arg0) {
		// TODO Auto-generated method stub
		mRemoteEndpoint = (RemoteEndpoint)arg0;
	}

	@Override
	public Object getReal() {
		// TODO Auto-generated method stub
		return mRemoteEndpoint;
	}
	
}