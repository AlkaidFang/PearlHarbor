package com.alkaid.pearlharbor.net.packethandler;

import com.alkaid.pearlharbor.net.IPacketHandler;
import com.alkaid.pearlharbor.net.PacketType;
import com.alkaid.pearlharbor.net.Token;
import com.alkaid.pearlharbor.net.XMessage;
import com.alkaid.pearlharbor.playersystem.Player;
import com.alkaid.pearlharbor.playersystem.PlayerSystem;
import com.google.protobuf.InvalidProtocolBufferException;

public class LoginPacketHandler implements IPacketHandler{

	@Override
	public int getType() {
		// TODO Auto-generated method stub
		return PacketType._Type_Login_;
	}

	@Override
	public boolean handle(Token token, byte[] data) {
		// TODO Auto-generated method stub		
		XMessage.CS_Login login = null;
		try
		{
			login = XMessage.CS_Login.parseFrom(data);
		}
		catch (InvalidProtocolBufferException e)
		{
			e.printStackTrace();
		}
		
		if (login == null) return false;
		
		//Player player = PlayerSystem.getInstance().onPlayerLogin(arg);
		
		return true;
	}

}





