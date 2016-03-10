package com.alkaid.pearlharbor.playersystem;

/*
 * 说明：
 * 这是精灵在游戏中的数据
 * （每个玩家可能有多个精灵）
 **/

public class AvatarData {
	public static String S_AvatarGuidFormat = "{0}-{1}";

	public String mAvatarGuid;
	public String mName;
	public int mLevel;
	public int mGold;
	public int mJewel;
	public int mVip;
	
	public AvatarData()
	{
		mAvatarGuid = "";
		mName = "";
		mLevel = 0;
		mGold = 0;
		mJewel = 0;
		mVip = 0;
	}
	
	public void reset()
	{
		mAvatarGuid = "";
		mName = "";
		mLevel = 0;
		mGold = 0;
		mJewel = 0;
		mVip = 0;
	}
	
	public void newPlayer()
	{
		// when first generate an avatar, this function will be called to give this avatar some resource.
		mVip = 0;
        mLevel = 0;
        mGold = 1000;
        mJewel = 200;
	}
}
