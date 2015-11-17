package com.alkaid.pearlharbor.dataprovidersystem;

import com.alkaid.pearlharbor.logger.LoggerSystem;
import com.alkaid.pearlharbor.util.LifeCycle;

import java.util.*;

public class DataProviderSystem implements LifeCycle{

	private volatile static DataProviderSystem instance = null;
	private ArrayList<IDataProvider> mDataProvider = new ArrayList<IDataProvider>();
	
	public static DataProviderSystem getInstance()
	{
		if (null == instance)
		{
			synchronized(DataProviderSystem.class)
			{
				if (null == instance)
				{
					instance = new DataProviderSystem();
				}
			}
		}
		
		return instance;
	}
	
	@Override
	public boolean init() {
		// TODO Auto-generated method stub
		// register all dataprovider
		registerDataProvider(new DictionaryDataProvider());
		
		
		// load all
		IDataProvider data = null;
		for (int i = 0; i < mDataProvider.size(); ++ i)
		{
			data = mDataProvider.get(i);
			
			data.load();
			if (!data.verify()) return false;
		}
		
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
	
	private void registerDataProvider(IDataProvider dataProvider)
	{
		mDataProvider.add(dataProvider);
	}

}
