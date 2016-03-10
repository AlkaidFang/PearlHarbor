package com.alkaid.pearlharbor.dbsystem.redis;

import com.alkaid.pearlharbor.logger.LoggerSystem;
import com.alkaid.pearlharbor.logger.LoggerSystem.LogType;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisHelper{

	private JedisPool mJedisPool = null;
	
	private Jedis mTemp = null;

	public void init(String ip, int port)
	{
		if (mJedisPool == null)
		{
			JedisPoolConfig config = new JedisPoolConfig();
			config.setMaxTotal(128);	// the max jedis instance could be generated, when all is create, the pool is exhausted.
			config.setMaxIdle(5);		// the max jedis instance the pool remain.(others will recycle)
			config.setMaxWaitMillis(1000 * 60);		// when borrow a redis instance, the max wait time. longer the JedisConnectionException will throw.
			config.setTestOnBorrow(true);
			
			mJedisPool = new JedisPool(config, ip, port);
		}
	}
	
	public Jedis getDb()
	{
		mTemp = null;
		if (mJedisPool != null)
		{
			try
			{
				mTemp = mJedisPool.getResource();
			}
			catch(Exception e)
			{
				e.printStackTrace();
				LoggerSystem.info(LogType.DB, e.toString());
				mTemp.close();
				end();
			}
		}
		
		return mTemp;
	}
	
	public void release(Jedis db)
	{
		if (mTemp == db)
		{
			mTemp = null;
		}
		
		if (db != null)
		{
			db.close();
		}
	}

	public void end()
	{
		if (null != mTemp)
		{
			mTemp.close();
		}
		
		if (mJedisPool != null)
		{
			mJedisPool.destroy();
			mJedisPool = null;
		}
	}
}
