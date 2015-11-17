package com.alkaid.pearlharbor.net.packethandler;

import java.util.Date;

import com.alkaid.pearlharbor.net.BasicPacket;
import com.alkaid.pearlharbor.net.PacketProto;
import com.alkaid.pearlharbor.net.PacketType;
import com.alkaid.pearlharbor.net.Token;
import com.google.protobuf.InvalidProtocolBufferException;

public class BasicPacketHandler {

	public boolean handleLogout(Token token, Object data)
	{
		byte[] b = ((String)data).getBytes();
		PacketProto.Logout logout = null;
		try {
			logout = PacketProto.Logout.parseFrom(b);
		} catch (InvalidProtocolBufferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (logout == null) return false;
		
		System.out.println("BasicPacketHandler-----handleLogout ,  name:" + logout.getName() + ", time:" + logout.getTime());
		
		return true;
	}
	
	public boolean handleHello(Token token, Object data)
	{
		byte[] b = ((String)data).getBytes();
		PacketProto.hello hello = null;
//		try {
//			hello = PacketProto.hello.parseFrom(b);
//		} catch (InvalidProtocolBufferException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		System.out.println("handleHello -- -- -- -- --");
		
		BasicPacket packet = new BasicPacket(PacketType._Type_Hello_);
		PacketProto.hello h = PacketProto.hello.newBuilder().setGuid(999).setDelta(0).setTime(new Date().toString()).build();
		packet.SetProto(h.toByteArray());
		
		token.sendPacket(packet);
		
		if (hello == null) return false;

		System.out.println("BasicPacketHandler-----handleHello ,  " + hello.getGuid() + "," + hello.getDelta() + "," + hello.getTime());
		
		return true;
	}
	
}
