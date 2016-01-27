package com.alkaid.pearlharbor.playersystem;

import com.alkaid.pearlharbor.util.LifeCycle;

public class Player  implements LifeCycle{

	private boolean bUsing = false;
	public PlayerData mPlayerData = null;
	
	public Player()
	{
		bUsing = false;
		mPlayerData = new PlayerData();
	}
	
	public void Reset()
	{
		if (bUsing)
		{
			// need to save this player.
			
		}
		
		mPlayerData.Reset();
		
		UnUsed();
	}
	
	@Override
	public boolean init() {
		// TODO Auto-generated method stub
		UnUsed();
		
		return true;
	}

	@Override
	public void tick() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub


	}

	public void Using()
	{
		bUsing = true;
	}
	
	public void UnUsed()
	{
		bUsing = false;
	}
	
}
