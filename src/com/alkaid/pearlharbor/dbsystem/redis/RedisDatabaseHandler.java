package com.alkaid.pearlharbor.dbsystem.redis;

import java.util.HashMap;
import java.util.Map;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import com.alkaid.pearlharbor.dbsystem.DatabaseDefine.DatabaseError;
import com.alkaid.pearlharbor.dbsystem.DatabaseDefine.DatabaseFuncType;
import com.alkaid.pearlharbor.dbsystem.IDatabaseHandler;
import com.alkaid.pearlharbor.game.Game;
import com.alkaid.pearlharbor.logger.LoggerSystem;
import com.alkaid.pearlharbor.logger.LoggerSystem.LogType;
import com.alkaid.pearlharbor.playersystem.Player;

public class RedisDatabaseHandler implements IDatabaseHandler{

	private String mIp = "";
	private int mPort = 0;
	
	private final static String sHomeDataKey = "key_home_{0}_{1}";	// serverid, user account
	private final static String sAvatarDataKey = "key_avatar_{0}";	// avatar guid
	
	private RedisHelper mRedisHelper = null;
	
	public RedisDatabaseHandler()
	{
		mRedisHelper = new RedisHelper();
	}
	
	@Override
	public boolean init() {
		// TODO Auto-generated method stub
		
		boolean ret = true;
		mRedisHelper.init(mIp, mPort);
		
		Jedis db = mRedisHelper.getDb();
		
		try
		{
			if (!db.exists("GLOBAL:GUID_BEGIN_INDEX") && !db.exists("GLOBAL:GUID_END_INDEX"))
			{
				Transaction tran = db.multi();
				tran.set("GLOBAL:GUID_BEGIN_INDEX", 10000 + "");
				tran.set("GLOBAL:GUID_END_INDEX", Integer.MAX_VALUE + "");
				tran.exec();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			LoggerSystem.error(LogType.DB, "RedisDatabaseHandler init failed!" + e.toString());
			mRedisHelper.release(db);
			mRedisHelper.end();
			
			ret = false;
		}
		finally
		{
			mRedisHelper.release(db);
		}
		
		return ret;
	}

	@Override
	public void tick() {
		// TODO Auto-generated method stub
		
		
		
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		mRedisHelper.end();
	}

	@Override
	public void setConfig(String dbIp, int dbPort) {
		// TODO Auto-generated method stub
		mIp = dbIp;
		mPort = dbPort;
	}

	@Override
	public DatabaseError schedulerPlayerData(Player player,
			DatabaseFuncType funcType) {
		// TODO Auto-generated method stub
		DatabaseError nRet = DatabaseError._ERROR_UNKNOW_;
		try
		{
	        switch(funcType)
	        {
	            case _DB_FUNC_UNKNOW_:
	                {
	                    nRet = DatabaseError._ERROR_UNDEFINE_FUNCTYPE_;
	                }break;
	            case _DB_FUNC_LOAD_PLAYER_HOME_:
	                {
	                    // 加载玩家家园信息
	                    nRet = loadPlayerHome(player);
	                }break;
	            case _DB_FUNC_SAVE_PLAYER_HOME_:
	                {
	                    // 存储玩家家园信息
	                    nRet = savePlayerHome(player);
	                } break;
	            case _DB_FUNC_LOAD_PLAYER_AVATAR_:
	                {
	                    // 加载玩家精灵信息
	                    nRet = loadPlayerAvatar(player);
	                } break;
	            case _DB_FUNC_SAVE_PLAYER_AVATAR_:
	                {
	                    nRet = savePlayerAvatar(player);
	                } break;
	            default:
	                {
	                    nRet = DatabaseError._ERROR_UNKNOW_;
	                }break;
	        }
		}
		catch (Exception e)
		{
			mRedisHelper.end();
		}


        return nRet;
	}

	@Override
	public int getNewAvatarIndex() {
		// TODO Auto-generated method stub
		
		int ret = 0;
		Jedis db = mRedisHelper.getDb();
		ret = db.incr("GLOBAL:GUID_BEGIN_INDEX").intValue();
		mRedisHelper.release(db);
		
		return ret;
	}
	
	private DatabaseError savePlayerHome(Player player)
	{
		DatabaseError nError = DatabaseError._ERROR_OK_;
		
		do
		{
			Jedis db = mRedisHelper.getDb();
			if (null == db)
			{
				nError = DatabaseError._ERROR_NO_DATABASE_;
				break;
			}
			
			String key = String.format(sHomeDataKey, Game.getInstance().getId(), player.mPlayerData.mHomeData.mAccount);
			
			Map<String, String> data = new HashMap<String, String>();
			data.put("mAccount", player.mPlayerData.mHomeData.mAccount);
			data.put("mAvatarCount", player.mPlayerData.mHomeData.mAvatarCount + "");
			int i = 0;
			for (String guid : player.mPlayerData.mHomeData.mAvatarGuids)
			{
				data.put("mAvatarGuids" + i, guid);
			}
			
			db.hmset(key, data);
			mRedisHelper.release(db);
		}
		while(false);
		
		return nError;
	}
	
	private DatabaseError loadPlayerHome(Player player)
	{
		DatabaseError nError = DatabaseError._ERROR_OK_;
		
		
		do
		{
			Jedis db = mRedisHelper.getDb();
			if (null == db)
			{
				nError = DatabaseError._ERROR_NO_DATABASE_;
				break;
			}
			
			String key = String.format(sHomeDataKey, Game.getInstance().getId(), player.mPlayerData.mHomeData.mAccount);
			Map<String, String> data = null;
			data = db.hgetAll(key);
			mRedisHelper.release(db);
			if (data.size() < 1)
			{
				nError = DatabaseError._ERROR_ACCOUNT_NOT_FOUND_;
				break;
			}
			
			for(String name : data.keySet())
			{
				if (name == "mAccount")
				{
					player.mPlayerData.mHomeData.mAccount = data.get(name);
				}
				else if (name == "mAvatarCount")
				{
					player.mPlayerData.mHomeData.mAvatarCount = Integer.parseInt(data.get(name));
				}
				else if (name.startsWith("mAvatarGuids"))
				{
					player.mPlayerData.mHomeData.mAvatarGuids.add(data.get(name));
				}
			}
			
		}
		while(false);

		return nError;
	}
	
	private DatabaseError savePlayerAvatar(Player player)
    {
        DatabaseError nError = DatabaseError._ERROR_OK_;

        do
        {
        	Jedis db = mRedisHelper.getDb();
            if (null == db)
            {
                nError = DatabaseError._ERROR_NO_DATABASE_;
                break;
            }

            String key = String.format(sAvatarDataKey, player.mPlayerData.mAvatarData.mAvatarGuid);
            Map<String, String> data = new HashMap<String, String>();
            data.put("mAvatarGuid", player.mPlayerData.mAvatarData.mAvatarGuid);
            data.put("mName", player.mPlayerData.mAvatarData.mName);
            data.put("mVip", player.mPlayerData.mAvatarData.mVip + "");
            data.put("mLevel", player.mPlayerData.mAvatarData.mLevel + "");
            data.put("mGold", player.mPlayerData.mAvatarData.mGold + "");
            data.put("mJewel", player.mPlayerData.mAvatarData.mJewel + "");
            
        	db.hmset(key, data);
			mRedisHelper.release(db);
            
        }
        while (false);

        return nError;
    }
	
    private DatabaseError loadPlayerAvatar(Player player)
    {
        DatabaseError nError = DatabaseError._ERROR_OK_;

        do
        {
        	Jedis db = mRedisHelper.getDb();
			if (null == db)
			{
				nError = DatabaseError._ERROR_NO_DATABASE_;
				break;
			}
			
			String key = String.format(sAvatarDataKey, Game.getInstance().getId(), player.mPlayerData.mAvatarData.mAvatarGuid);
			Map<String, String> data = null;
		
			data = db.hgetAll(key);
			mRedisHelper.release(db);
			
			if (data.size() < 1)
			{
				nError = DatabaseError._ERROR_AVATAR_NOT_FOUND_;
				break;
			}
			
			String v;
			for(String name : data.keySet())
			{
				v = data.get(name);
				if (name == "mAvatarGuid")
				{
					player.mPlayerData.mAvatarData.mAvatarGuid = v;
				}
				else if (name == "mName")
				{
					player.mPlayerData.mAvatarData.mName = v;
				}
				else if (name == "mLevel")
				{
					player.mPlayerData.mAvatarData.mLevel = Integer.parseInt(v);
				}
				else if (name == "mGold")
				{
					player.mPlayerData.mAvatarData.mGold = Integer.parseInt(v);
				}
				else if (name == "mJewel")
				{
					player.mPlayerData.mAvatarData.mJewel = Integer.parseInt(v);
				}
				else if (name == "mVip")
				{
					player.mPlayerData.mAvatarData.mVip = Integer.parseInt(v);
				}
			}
        	
        }
        while (false);

        return nError;
    }

	
}
