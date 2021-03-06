package com.alkaid.pearlharbor.dbsystem;

import com.alkaid.pearlharbor.dbsystem.DatabaseDefine.*;
import com.alkaid.pearlharbor.dbsystem.redis.RedisDatabaseHandler;
import com.alkaid.pearlharbor.playersystem.Player;
import com.alkaid.pearlharbor.util.LifeCycle;
import com.alkaid.pearlharbor.util.ServerConfig;

public class DBSystem implements LifeCycle{
	
	private IDatabaseHandler mDatabaseHandler = null;
	
	private boolean mInitResult = false;
	
	private static DBSystem instance = null;
	public static DBSystem getInstance()
	{
		if (null == instance)
		{
			synchronized(DBSystem.class)
			{
				if (null == instance)
				{
					instance = new DBSystem();
				}
			}
		}
		
		return instance;
	}

	private DBSystem()
	{
		mInitResult = false;
	}

	@Override
	public boolean init() {
		// TODO Auto-generated method stub
		
		if (ServerConfig.DB_SERVER_ACTIVE)
		{
			setDBType(DBType.REDIS);
			mDatabaseHandler.setConfig(ServerConfig.DB_SERVER_IP, ServerConfig.DB_SERVER_PORT);
			
			mInitResult = mDatabaseHandler.init();
			
		}
		else
		{
			mInitResult = true;
		}
		
		return mInitResult;
		
	}

	@Override
	public void tick() {
		// TODO Auto-generated method stub
		if (mInitResult && ServerConfig.DB_SERVER_ACTIVE)
			mDatabaseHandler.tick();
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
		if (mInitResult && ServerConfig.DB_SERVER_ACTIVE)
			mDatabaseHandler.destroy();
	}
	
	public void setDBType(DBType type)
	{
		switch(type)
		{
			case REDIS:mDatabaseHandler = new RedisDatabaseHandler();break;
			default:break;
		}
	}

	private boolean process(Player player, DatabaseFuncType functionType)
	{
		if (!mInitResult || !ServerConfig.DB_SERVER_ACTIVE)
			return false;
		
		DatabaseError nError = mDatabaseHandler.schedulerPlayerData(player, functionType);
		
		if (nError == DatabaseError._ERROR_OK_)
			return true;
		
		switch (nError)
		{
	        case _ERROR_UNKNOW_:
		        {
		
		        }break;
		    case _ERROR_NOT_CONNECT_DBSERVER_:
		        {
		
		        }break;
		    case _ERROR_UNDEFINE_FUNCTYPE_:
		        {
		
		        }break;
		    case _ERROR_ACCOUNT_NOT_FOUND_:
		        {
		
		        }break;
		    case _ERROR_WRITE_INNER_BREAK_:
		        {
		
		        }break;
		    case _ERROR_READ_INNER_BREAK_:
		        {
		
		        }break;
		    case _ERROR_DBSERVER_BUSY_:
		        {
		
		        }break;
		    default:
		        {// 未知错误
		
		        }break;
		}
		
		return false;
	}
	
	public boolean loadPlayerHomeData(Player player)
	{
		return process(player, DatabaseFuncType._DB_FUNC_LOAD_PLAYER_HOME_);
	}
	public boolean savePlayerHomeData(Player player)
	{
		return process(player, DatabaseFuncType._DB_FUNC_SAVE_PLAYER_HOME_);
	}
	

    public boolean loadPlayerAvatarData(Player player)
    {
        return process(player, DatabaseFuncType._DB_FUNC_LOAD_PLAYER_AVATAR_);
    }
    public boolean savePlayerAvatarData(Player player)
    {
        return process(player, DatabaseFuncType._DB_FUNC_SAVE_PLAYER_AVATAR_);
    }

    public boolean savePlayerAllInfo(Player player)
    {
        return process(player, DatabaseFuncType._DB_FUNC_SAVE_PLAYER_HOME_) && process(player, DatabaseFuncType._DB_FUNC_SAVE_PLAYER_AVATAR_);
    }
    
    public int getNewAvatarIndex()
    {
    	return mDatabaseHandler.getNewAvatarIndex();
    }
}
