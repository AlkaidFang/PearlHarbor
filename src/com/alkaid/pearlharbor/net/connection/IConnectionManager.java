package com.alkaid.pearlharbor.net.connection;

import com.alkaid.pearlharbor.util.LifeCycle;

public interface IConnectionManager extends LifeCycle {

	ConnectionType getConnectionType();
	
	void accept();
	
	void recieve();
	
	void send();
	
	int connectionNum();

	void onBind(Object connection);
	
	void onError(Object connection);
	
	void onEnd(Object connection);
	
	void onReceivedData(Object connection, byte[] data);
}
