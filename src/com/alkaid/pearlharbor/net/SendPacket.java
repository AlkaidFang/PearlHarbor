package com.alkaid.pearlharbor.net;

public class SendPacket implements IPacket{
	
	private int mType;
    private byte[] mProtoBytes;
    private Object mProto;

    public void setProto(Object proto)
    {
        mProto = proto;
        encodeProto();
    }

    public SendPacket(int type)
    {
        mType = type;
        mProtoBytes = null;
        mProto = null;
    }

    private void encodeProto()
    {
        if (mProto != null)
        {
        	com.google.protobuf.GeneratedMessage msg = (com.google.protobuf.GeneratedMessage)mProto;
        	mProtoBytes = msg.toByteArray();
        }
    }

	@Override
	public int getPacketType() {
		// TODO Auto-generated method stub

        return mType;
	}

	@Override
	public byte[] getData() {
		// TODO Auto-generated method stub

        return mProtoBytes;
	}

}
