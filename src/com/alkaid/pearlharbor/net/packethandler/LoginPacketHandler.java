package com.alkaid.pearlharbor.net.packethandler;

import com.alkaid.pearlharbor.net.IPacketHandler;
import com.alkaid.pearlharbor.net.PacketProto;
import com.alkaid.pearlharbor.net.PacketType;
import com.alkaid.pearlharbor.net.Token;
import com.google.protobuf.InvalidProtocolBufferException;

public class LoginPacketHandler implements IPacketHandler{

	@Override
	public int getType() {
		// TODO Auto-generated method stub
		return PacketType._Type_Login_;
	}

	@Override
	public boolean handle(Token token, Object data) {
		// TODO Auto-generated method stub
		byte[] b = ((String)data).getBytes();
		PacketProto.Login login = null;
		try {
			login = PacketProto.Login.parseFrom(b);
		} catch (InvalidProtocolBufferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (login == null) return false;
		
		System.out.println("LoginPacketHandler ,  name:" + login.getName() + ", pwd:" + login.getPassword());
		
		return true;
	}

}
