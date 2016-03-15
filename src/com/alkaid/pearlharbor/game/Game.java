package com.alkaid.pearlharbor.game;

import com.alkaid.pearlharbor.dataprovidersystem.DataProviderSystem;
import com.alkaid.pearlharbor.dbsystem.DBSystem;
import com.alkaid.pearlharbor.logger.LoggerSystem;
import com.alkaid.pearlharbor.logger.LoggerSystem.LogType;
import com.alkaid.pearlharbor.net.NetSystem;
import com.alkaid.pearlharbor.playersystem.PlayerSystem;
import com.alkaid.pearlharbor.util.LifeCycle;
import com.alkaid.pearlharbor.util.ServerConfig;

public class Game implements LifeCycle{
	private volatile static Game instance = null;
	
	private volatile boolean mRunning = false;
	private Thread mMainLoopThread = null;
	
	private String mLogPropertiesPath;
	private String mResPath;
	
	private Game()
	{
		mRunning = false;
	}
	
	public static Game getInstance()
	{
		if (null == instance)
		{
			synchronized(Game.class)
			{
				if (null == instance)
				{
					instance = new Game();
				}
			}
		}
		
		return instance;
	}
	
	public void startServer()
	{
		boolean ret = init();
		if (!ret)
		{
			LoggerSystem.getInstance().getLogger(LogType.DEFAULT).error("server init failed!");
			return;
		}
		LoggerSystem.info(LogType.DEFAULT, "server init succeed!");
		
		this.mRunning = true;
		
		mMainLoopThread = new Thread(new Runnable(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				while(Game.getInstance().isRunning())
				{
					Game.getInstance().tick();
					
					try
					{
						Thread.sleep(ServerConfig.SERVER_TICK_INTERVAL_MILLISECONDS);
					}
					catch(Exception e)
					{
						LoggerSystem.error(LogType.DEFAULT, e.getMessage());
					}
				}
				
				Game.getInstance().destroy();
			}
		}) ;
		mMainLoopThread.start();
		LoggerSystem.info(LogType.DEFAULT, "server start succeed!");
	}	
	
	public void stopServer()
	{
		mRunning = false;
	}
	
	public boolean isRunning()
	{
		return mRunning;
	}

	@Override
	public boolean init() {
		// TODO Auto-generated method stub
		
		// this will do all init function.
		do
		{			
			// logger
			if (LoggerSystem.getInstance().init() == false)
				break;
			
			// DataProvider
			if (DataProviderSystem.getInstance().init() == false)
				break;
				
			// playersystem
			if (PlayerSystem.getInstance().init() == false)
				break;
			
			// netsystem
			if (NetSystem.getInstance().init() == false)
				break;
			
			// dbsystem
			if (DBSystem.getInstance().init() == false)
				break;

			
			return true;
		}
		while(false);
		
		return false;
	}

	@Override
	public void tick() {
		// TODO Auto-generated method stub
		// do all system tick
		
		LoggerSystem.getInstance().tick();
		
		DataProviderSystem.getInstance().tick();
		
		PlayerSystem.getInstance().tick();
		
		NetSystem.getInstance().tick();
		
		DBSystem.getInstance().tick();
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		LoggerSystem.info(LogType.DEFAULT, "server destroy start!");
		
		LoggerSystem.getInstance().destroy();
		
		DataProviderSystem.getInstance().destroy();
		
		PlayerSystem.getInstance().destroy();
		
		NetSystem.getInstance().destroy();
		
		DBSystem.getInstance().destroy();

		LoggerSystem.info(LogType.DEFAULT, "server destroy end!");
	}

	public String getLogPropertiesPath() {
		return mLogPropertiesPath;
	}

	public void setLogPropertiesPath(String mLogPropertiesPath)
	{
		this.mLogPropertiesPath = mLogPropertiesPath;
	}
	
	public String getResPath()
	{
		return this.mResPath;
	}
	public void setResPath(String respath)
	{
		this.mResPath = respath;
	}
	
	public int getId()
	{
		return ServerConfig.SERVER_ID;
	}
}
