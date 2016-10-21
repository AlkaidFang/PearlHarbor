package com.alkaid.pearlharbor.playersystem;

import java.util.HashMap;

import com.alkaid.pearlharbor.dbsystem.DBSystem;
import com.alkaid.pearlharbor.game.Game;
import com.alkaid.pearlharbor.util.LifeCycle;
import com.alkaid.pearlharbor.util.ServerConfig;


public class PlayerSystem implements LifeCycle{
	
	private PlayerPool mPlayerPool = null;
	private HashMap<String, Player> mActivePlayer = null;
	
	private volatile static PlayerSystem instance = null;
	
	public static PlayerSystem getInstance()
	{
		if (null == instance)
		{
			synchronized(PlayerSystem.class)
			{
				if (null == instance)
				{
					instance = new PlayerSystem();
				}
			}
		}
		
		return instance;
	}
	
	private PlayerSystem()
	{
		mActivePlayer = new HashMap<String, Player>();
		mPlayerPool = new PlayerPool(ServerConfig.MAX_PLAYER_ALLOCATE);
	}

	@Override
	public boolean init() {
		// TODO Auto-generated method stub
		
		
		return true;
	}

	@Override
	public void tick() {
		// TODO Auto-generated method stub
		
		synchronized(mActivePlayer)
		{
			for (Player p : mActivePlayer.values())
			{
				p.tick();
			}
		}
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
		
		
	}
	
	private Player getNewPlayer()
	{
		return mPlayerPool.retain();
	}
	
	private void releasePlayer(Player arg)
	{
		mPlayerPool.release(arg);
	}
	
	public void onPlayerLogin(Player arg)
	{
		synchronized(mActivePlayer)
		{
			if (arg != null)
			{
				mActivePlayer.put(arg.mPlayerData.mGuid, arg);
			}
		}
	}
	
	public void onPlayerLogout(Player arg)
	{
		synchronized(mActivePlayer)
		{
			if (arg != null)
			{
				mActivePlayer.remove(arg.mPlayerData.mGuid);
			}
		}
	}
	
	/**
	 * @author fangjun
	 * @description login home, and havenot declar the avatar message
	 * @date 2016年3月8日
	 */
	public Player onLoginHome(String account)
	{
		// check if online
		Player player = mActivePlayer.get(account);
		if (null != player)
		{
			return player;
		}
		
		// not online
		player = getNewPlayer();
		player.mPlayerData.mHomeData.mAccount = account;
		
		// load storage
		if (!DBSystem.getInstance().loadPlayerHomeData(player))
		{
			// the db donot contain this player data, so this is a new user.
			player.mPlayerData.mHomeData.newPlayer();
			// save this data
			DBSystem.getInstance().savePlayerHomeData(player);
		}
		
		// active this data
		player.Using();
		onPlayerLogin(player);
		
		return player;
	}
	
	/**
	 * 
	 * @author fangjun
	 * @description enter the game world, so load the avatar data
	 * @date 2016年3月8日
	 */
	public void onLoginGame(Player player)
	{
		if (null == player) return;
		
		String guid = player.mPlayerData.mAvatarData.mAvatarGuid;
		
		if (!DBSystem.getInstance().loadPlayerAvatarData(player))
		{
			// db donot contain this avatar, to make new one;
			int avatarindex = DBSystem.getInstance().getNewAvatarIndex();
			guid = String.format(AvatarData.S_AvatarGuidFormat, Game.getInstance().getId(), avatarindex);
			player.mPlayerData.mAvatarData.newPlayer();
			player.mPlayerData.mAvatarData.mAvatarGuid = guid;
			player.mPlayerData.mHomeData.mAvatarGuids.add(guid);
			
			DBSystem.getInstance().savePlayerAllInfo(player);
		}
	}
	
}
