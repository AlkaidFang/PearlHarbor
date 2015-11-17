package com.alkaid.pearlharbor.net;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import com.alkaid.pearlharbor.logger.LoggerSystem;
import com.alkaid.pearlharbor.logger.LoggerSystem.LogType;
import com.alkaid.pearlharbor.net.packethandler.BasicPacketHandler;
import com.alkaid.pearlharbor.net.packethandler.LoginPacketHandler;
import com.alkaid.pearlharbor.util.LifeCycle;

public class PacketHandlerManager implements LifeCycle{
	private class HandlerObject
	{
		private Object mOwner;
		private Method mMethod;
		public HandlerObject(Object owner, Method method)
		{
			mOwner = owner;
			mMethod = method;
		}
	}
	
	private HashMap<Integer, HandlerObject> mHandlerMap = null;
		
	public PacketHandlerManager()
	{
		mHandlerMap = new HashMap<Integer, HandlerObject>();
		
	}
	
	@Override
	public boolean init() {
		// TODO Auto-generated method stub
		registerHandler(new LoginPacketHandler());
		
		BasicPacketHandler basic = new BasicPacketHandler();
		registerHandler(PacketType._Type_Hello_, basic, "handleHello");
		registerHandler(PacketType._Type_Logout_, basic, "handleLogout");
		
		return true;
	}

	@Override
	public void tick() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean destroy() {
		// TODO Auto-generated method stub
		
		mHandlerMap.clear();
		
		return true;
	}
	
	private void registerHandler(IPacketHandler handler)
	{
		//mHandlerMap.put(handler.getType(), handler);
		
		try {
			
			registerHandler(handler.getType(), new HandlerObject(handler, handler.getClass().getMethod("handle", Token.class, Object.class)));
			
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void registerHandler(int type, Object owner, String methodName)
	{
		try {
			
			registerHandler(type, new HandlerObject(owner, owner.getClass().getMethod(methodName, Token.class, Object.class)));
			
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void registerHandler(int type, HandlerObject handlerobject)
	{
		mHandlerMap.put(type,  handlerobject);
	}
	
	public boolean handle(Token token, int type, Object data)
	{
		HandlerObject methodObject = mHandlerMap.get(type);
		
		boolean ret = false;
		if (methodObject != null)
		{
			try {
				
				ret =  (boolean) methodObject.mMethod.invoke(methodObject.mOwner, token, data);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				LoggerSystem.error(LogType.DEFAULT, "PacketHandlerManager handle cause error, type:" + type + ", data:" + data.toString());
			}
		}
		
		return ret;
	}
	


}
