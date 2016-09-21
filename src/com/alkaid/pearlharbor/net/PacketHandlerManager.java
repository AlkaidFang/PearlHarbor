package com.alkaid.pearlharbor.net;

import java.util.HashMap;
import com.alkaid.pearlharbor.net.packethandler.HelloPacketHandler;
import com.alkaid.pearlharbor.net.packethandler.LoginPacketHandler;
import com.alkaid.pearlharbor.net.packethandler.PingPacketHandler;
import com.alkaid.pearlharbor.util.LifeCycle;

public class PacketHandlerManager implements LifeCycle{
	
	private HashMap<Integer, IPacketHandler> mHandlerMap = null;
		
	public PacketHandlerManager()
	{
		mHandlerMap = new HashMap<Integer, IPacketHandler>();
		
	}
	
	@Override
	public boolean init() {
		// TODO Auto-generated method stub
		registerHandler(new HelloPacketHandler());
		registerHandler(new LoginPacketHandler());
		registerHandler(new PingPacketHandler());
		
		
		return true;
	}

	@Override
	public void tick() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
		mHandlerMap.clear();
		
	}
	
	private void registerHandler(IPacketHandler handler)
	{
		mHandlerMap.put(handler.getType(), handler);
	}
	
	public boolean handle(Token token, int type, byte[] data)
	{
		IPacketHandler packetHandler = mHandlerMap.get(type);
		
		boolean ret = false;
		if (packetHandler != null)
		{
			ret = packetHandler.handle(token, data);
		}
		
		return ret;
	}
	


}
