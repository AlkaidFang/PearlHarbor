package com.alkaid.pearlharbor.net;

import java.nio.ByteBuffer;

public class BasicPacket implements IPacket{
	
	private int mType;
	private byte[] mProtoData = null;

	public BasicPacket(int type)
	{
		mType = type;
	}
	
	public void SetProto(byte[] protodata)
	{
		mProtoData = protodata;
	}
	
	@Override
	public int getType() {
		// TODO Auto-generated method stub
		return mType;
	}
	

	@Override
	public String encode() {
		// TODO Auto-generated method stub
		String data = "" + mType + ";";
		if (mProtoData != null)
		{
			data += new String(mProtoData);
		}
		
		return data;
	}
	

}
