package com.alkaid.pearlharbor.playersystem;

import java.util.Stack;

import com.alkaid.pearlharbor.logger.LoggerSystem;
import com.alkaid.pearlharbor.logger.LoggerSystem.LogType;

public class PlayerPool {

	private int mNowCount = 0;
	private int mMaxPoolSize = 0;
	private Stack<Player> mPlayerStack = null;
	
	public PlayerPool(int maxsize)
	{
		mPlayerStack = new Stack<Player>();
		mNowCount = 0;
		mMaxPoolSize = maxsize;
		
		Player t = null;
		for (int i = 0; i < mMaxPoolSize; ++i)
		{
			t = new Player();
			t.init();
			
		}
	}
	
	public boolean IsEmpty()
	{
		boolean ret = false;
		if (mNowCount == 0)
		{
			ret = true;
		}
		
		return ret;
	}
	
	public Player retain()
	{
		Player t = null;
		synchronized(mPlayerStack)
		{
			if (!IsEmpty())
			{
				-- mNowCount;
				t = mPlayerStack.pop();
			}
		}
		
		return t;
	}
	
	public void release(Player t)
	{
		synchronized(mPlayerStack)
		{
			if (mNowCount < mMaxPoolSize)
			{
				t.Reset();
				
				++ mNowCount;
				mPlayerStack.push(t);
			}
		}
	}
	
	public void Dump()
	{
		// log now container
		LoggerSystem.debug(LogType.DEFAULT, "PlayerPool Dump: now count is : " + mNowCount + " / " + mMaxPoolSize);
		
	}

}
