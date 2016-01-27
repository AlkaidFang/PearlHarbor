package com.alkaid.pearlharbor.playersystem;

import java.util.HashMap;

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
	
	public Player getNewPlayer()
	{
		return mPlayerPool.retain();
	}
	
	public void releasePlayer(Player arg)
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
				mActivePlayer.put(arg.mPlayerData.mGuid, arg);
			}
		}
	}
}
