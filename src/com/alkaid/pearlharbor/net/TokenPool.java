package com.alkaid.pearlharbor.net;

import java.util.Stack;

import com.alkaid.pearlharbor.logger.LoggerSystem;
import com.alkaid.pearlharbor.logger.LoggerSystem.LogType;

public class TokenPool {

	private int mNowCount = 0;
	private int mMaxPoolSize = 0;
	private Stack<Token> mTokenStack = null;
	
	public TokenPool(int maxsize)
	{
		mTokenStack = new Stack<Token>();
		mMaxPoolSize = maxsize;
		mNowCount = mMaxPoolSize;
		
		Token t = null;
		for (int i = 0; i < mMaxPoolSize; ++i)
		{
			t = new Token();
			t.init();
			mTokenStack.push(t);
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
	
	public Token retain()
	{
		Token t = null;
		synchronized(mTokenStack)
		{
			if (!IsEmpty())
			{
				-- mNowCount;
				t = mTokenStack.pop();
			}
		}
		
		return t;
	}
	
	public void release(Token t)
	{
		synchronized(mTokenStack)
		{
			if (mNowCount < mMaxPoolSize)
			{
				t.reset();
				
				++ mNowCount;
				mTokenStack.push(t);
			}
		}
	}
	
	public void Dump()
	{
		// log now container
		LoggerSystem.debug(LogType.DEFAULT, "TokenPool Dump: now count is : " + mNowCount + " / " + mMaxPoolSize);
		
	}
}
