package com.alkaid.pearlharbor.net;

import com.alkaid.pearlharbor.util.UtilTools;

public class PacketFormat
{
	public class DecodeResult
	{
		public boolean mStatus;
		public int mPacketLength;
		public int mPacketType;
		public byte[] mProtoData;
	}
	
    public static byte[] PACKET_HEAD = { 99, 99 }; //{'c', 'c'};

    public int GetLength(int dataLength)
    {
        return 2 + 4 + 4 + dataLength;
    }

    // generate this packet to buffer
    public void GenerateBuffer(byte[] dest, IPacket packet)
    {
        byte[] data = packet.getData();
        int iLength = GetLength(data.length);

        dest = new byte[iLength];

        // head
        System.arraycopy(PACKET_HEAD, 0, dest, 0, 2);

        // length
        byte[] bLength = UtilTools.int2byte(iLength);
        System.arraycopy(bLength, 0, dest, 2, 4);

        // type
        int iType = packet.getPacketType();
        byte[] bType = UtilTools.int2byte(iType);
        System.arraycopy(bType, 0, dest, 6, 4);

        // data
        System.arraycopy(data, 0, dest, 10, data.length);
    }

    //  check if have a full packet
    public boolean CheckHavePacket(byte[] buffer, int offset)
    {
        if (buffer[0] == PACKET_HEAD[0] && buffer[1] == PACKET_HEAD[1]) // 首两位为包头
        {
            int length = UtilTools.byte2int(buffer, 2);
            if (length <= offset)
            {
                return true;
            }
        }

        return false;
    }

    // decode this data
    public DecodeResult DecodePacket(byte[] buffer)
    {
        DecodeResult dr = new DecodeResult();
        do
        {
            dr.mPacketLength = UtilTools.byte2int(buffer, 2);
            if (dr.mPacketLength < 0)
                break;

            dr.mPacketType = UtilTools.byte2int(buffer, 6);
            if (dr.mPacketType < 0)
                break;

            dr.mProtoData = new byte[dr.mPacketLength - 10];
            if (null == dr.mProtoData)
                break;
            System.arraycopy(buffer, 10, dr.mProtoData, 0, dr.mPacketLength - 10);
            
            dr.mStatus = true;
        }
        while (false);

        dr.mStatus = false;
        
        return dr;
    }
}