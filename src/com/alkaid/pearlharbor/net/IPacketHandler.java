package com.alkaid.pearlharbor.net;

public interface IPacketHandler {
	
	int getType();
	
	boolean handle(Token token, byte[] data);
	
}
