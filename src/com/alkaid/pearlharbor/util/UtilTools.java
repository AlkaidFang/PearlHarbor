package com.alkaid.pearlharbor.util;

public class UtilTools {
	
	public static byte[] short2byte(short data)
	{
		byte[] targets = new byte[2];

		targets[0] = (byte) (data & 0xff);
		targets[1] = (byte) ((data >> 8) & 0xff);
		return targets;
	}
	
	public static byte[] int2byte(int data)
	{
		byte[] targets = new byte[4];

		targets[0] = (byte) (data & 0xff);
		targets[1] = (byte) ((data >> 8) & 0xff);
		targets[2] = (byte) ((data >> 16) & 0xff);
		targets[3] = (byte) (data >>> 24);
		return targets;
	}
	
	public static int byte2int(byte[] data)
	{
		return byte2int(data, 0);
	}
	
	public static int byte2int(byte[] data, int startIndex)
	{
		if (data.length <= startIndex + 3)
			return -1;
		int target = (data[startIndex + 0] & 0xff) | ((data[startIndex + 1] << 8) & 0xff00) | ((data[startIndex + 2] << 24) >>> 8) | (data[startIndex + 3] << 24);
		return target;
	}
}
