package com.alkaid.pearlharbor.net.packethandler;

import com.alkaid.pearlharbor.logger.LoggerSystem;
import com.alkaid.pearlharbor.logger.LoggerSystem.LogType;
import com.alkaid.pearlharbor.net.IPacketHandler;
import com.alkaid.pearlharbor.net.PacketType;
import com.alkaid.pearlharbor.net.SendPacket;
import com.alkaid.pearlharbor.net.Token;
import com.alkaid.pearlharbor.net.XMessage;
import com.google.protobuf.InvalidProtocolBufferException;

public class PingPacketHandler implements IPacketHandler{

	@Override
	public int getType() {
		// TODO Auto-generated method stub
		return PacketType._CS_Ping_;
	}

	@Override
	public boolean handle(Token token, byte[] data) {
		// TODO Auto-generated method stub		
		XMessage.CS_Ping proto = null;
		try
		{
			proto = XMessage.CS_Ping.parseFrom(data);
		}
		catch (InvalidProtocolBufferException e)
		{
			e.printStackTrace();
		}
		
		if (proto == null) return false;

		XMessage.SC_PingResult proto1 = XMessage.SC_PingResult.newBuilder().setTimestamp(proto.getTimestamp()).build();
		SendPacket packet = new SendPacket(PacketType._CS_Ping_);
		packet.setProto(proto1);
		token.sendPacket(packet);
		
		LoggerSystem.debug(LogType.DEFAULT, "ping: token:" + token.getConnection().getCid() + "   TimeStamp:" + proto.getTimestamp());
		
		return true;
	}

}





