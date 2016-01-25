package com.alkaid.pearlharbor.logger;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.alkaid.pearlharbor.game.Game;
import com.alkaid.pearlharbor.util.LifeCycle;

public class LoggerSystem implements LifeCycle{
	
	public enum LogType
	{
		DEFAULT,
		LOGIN,
		CREATEROLE,
		_MAX_,
	}

	private volatile static LoggerSystem instance = null;
	
	private Logger[] mLoggerList = new Logger[LogType._MAX_.ordinal()];
	
	private LoggerSystem(){}
	public static LoggerSystem getInstance()
	{
		if (null == instance)
		{
			synchronized(LoggerSystem.class)
			{
				if (null == instance)
				{
					instance = new LoggerSystem();
				}
			}
		}
		
		return instance;
	}

	@Override
	public boolean init() {
		// TODO Auto-generated method stub

		PropertyConfigurator.configure(Game.getInstance().getLogPropertiesPath());

		mLoggerList[LogType.DEFAULT.ordinal()] = Logger.getLogger("DEFAULT");
		mLoggerList[LogType.DEFAULT.ordinal()].info("Default Log Begin");
		
		mLoggerList[LogType.LOGIN.ordinal()] = Logger.getLogger("LOGIN");
		mLoggerList[LogType.LOGIN.ordinal()].info("Login Log Begin");
		
		mLoggerList[LogType.CREATEROLE.ordinal()] = Logger.getLogger("CREATEROLE");
		mLoggerList[LogType.CREATEROLE.ordinal()].info("CreateRole Log Begin");
		
		return true;
	}

	@Override
	public void tick() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean destroy() {
		// TODO Auto-generated method stub
		return true;
	}
	
	public Logger getLogger(LogType type)
	{
		return mLoggerList[type.ordinal()];
	}

	public static void debug(LogType type, Object message)
	{
		LoggerSystem.getInstance().getLogger(type).debug(message);;
	}
	public static void info(LogType type, Object message)
	{
		LoggerSystem.getInstance().getLogger(type).info(message);
	}
	public static void warn(LogType type, Object message)
	{
		LoggerSystem.getInstance().getLogger(type).warn(message);
	}
	public static void error(LogType type, Object message)
	{
		LoggerSystem.getInstance().getLogger(type).error(message);
	}
	public static void fatal(LogType type, Object message)
	{
		LoggerSystem.getInstance().getLogger(type).fatal(message);
	}
	
}
