package com.alkaid.pearlharbor.net;

public interface IPacket {

    int getPacketType();

    byte[] getData();
}
