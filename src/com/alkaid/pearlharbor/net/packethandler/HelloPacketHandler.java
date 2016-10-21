package com.alkaid.pearlharbor.net.packethandler;

import com.alkaid.pearlharbor.net.IPacketHandler;
import com.alkaid.pearlharbor.net.PacketType;
import com.alkaid.pearlharbor.net.SendPacket;
import com.alkaid.pearlharbor.net.Token;
import com.alkaid.pearlharbor.net.XMessage;
import com.google.protobuf.InvalidProtocolBufferException;

public class HelloPacketHandler implements IPacketHandler{

	@Override
	public int getType() {
		// TODO Auto-generated method stub
		return PacketType._CS_HelloWorld;
	}

	@Override
	public boolean handle(Token token, byte[] data) {
		// TODO Auto-generated method stub
		byte[] b = (byte[])data;
		XMessage.CS_HelloWorld hello = null;
		try {
			hello = XMessage.CS_HelloWorld.parseFrom(b);
		} catch (InvalidProtocolBufferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (hello == null) return false;
		
		XMessage.SC_HelloWorldResult proto = XMessage.SC_HelloWorldResult.newBuilder().setResultCode(888).build();
		SendPacket packet = new SendPacket(PacketType._CS_HelloWorld);
		packet.setProto(proto);
		token.sendPacket(packet);
		
		System.out.println("LoginPacketHandler ,  int:" + hello.getInt() + " long:" + hello.getLong());
		
		return true;
	}

}
