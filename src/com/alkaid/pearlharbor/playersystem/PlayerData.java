package com.alkaid.pearlharbor.playersystem;

public class PlayerData {

	public String mGuid;
	
    // 家园数据
    public HomeData mHomeData;

    // 精灵数据
    public AvatarData mAvatarData;


    public PlayerData()
    {
        mHomeData = new HomeData();

        mAvatarData = new AvatarData();
    }

    public void reset()
    {
        mHomeData.reset();

        mAvatarData.reset();
    }
}
