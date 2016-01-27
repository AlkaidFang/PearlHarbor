package com.alkaid.pearlharbor.net.packethandler;

import com.alkaid.pearlharbor.net.IPacketHandler;
import com.alkaid.pearlharbor.net.PacketProto;
import com.alkaid.pearlharbor.net.PacketType;
import com.alkaid.pearlharbor.net.Token;
import com.google.protobuf.InvalidProtocolBufferException;

public class HelloPacketHandler implements IPacketHandler{

	@Override
	public int getType() {
		// TODO Auto-generated method stub
		return PacketType._Type_HelloWorld;
	}

	@Override
	public boolean handle(Token token, byte[] data) {
		// TODO Auto-generated method stub
		byte[] b = (byte[])data;
		PacketProto.CS_HelloWorld hello = null;
		try {
			hello = PacketProto.CS_HelloWorld.parseFrom(b);
		} catch (InvalidProtocolBufferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (hello == null) return false;
		
		System.out.println("LoginPacketHandler ,  int:" + hello.getInt() + " long:" + hello.getLong());
		
		return true;
	}

}
