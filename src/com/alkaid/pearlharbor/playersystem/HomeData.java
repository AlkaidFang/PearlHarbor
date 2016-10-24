package com.alkaid.pearlharbor.playersystem;

import java.util.ArrayList;
import java.util.List;

/*
 * 启动家园数据
 * 说明：
 *      玩家登录后获取的阿凡达家园展示数据，包含所有用户拥有的精灵数据
 * 
 * */

public class HomeData {

	public String mAccount;		// user guid, the user's login account
	public int mAvatarCount;	// his game avatar count
	public List<String> mAvatarGuids; // avatar guid list
	
//	-----以下属性为新增，未做存储
	public List<String> mAvatarNames; 
	public List<Integer> mAvatarLevels;
	
	public HomeData()
	{
		mAccount = "";
		mAvatarCount = 0;
		mAvatarGuids = new ArrayList<String>();
		mAvatarNames = new ArrayList<String>();
		mAvatarLevels = new ArrayList<Integer>();
	}
	
	public void reset()
	{
		mAccount = "";
		mAvatarCount = 0;
		mAvatarGuids.clear();
		mAvatarNames.clear();
		mAvatarLevels.clear();
	}
	
	public void newPlayer()
	{
		// when first generate a player, this function will give default data.
		mAvatarCount = 0;
        mAvatarGuids.clear();
		mAvatarNames.clear();
		mAvatarLevels.clear();
	}
	
	public void addAvatar(String avatarGuid)
	{
		mAvatarCount++;
		mAvatarGuids.add(avatarGuid);
	}
}
