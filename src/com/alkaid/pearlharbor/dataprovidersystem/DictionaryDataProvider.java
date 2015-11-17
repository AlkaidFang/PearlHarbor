package com.alkaid.pearlharbor.dataprovidersystem;

import java.util.ArrayList;
import java.util.List;

import com.alkaid.pearlharbor.game.Game;
import com.alkaid.pearlharbor.util.FileReaderHelper;

public class DictionaryDataProvider implements IDataProvider{
	
	public class DictionaryDataItem
	{
		public int mID = -1;
		public String mData = "";
	}
	
	private List<DictionaryDataItem> mDataList = new ArrayList<DictionaryDataItem>();

	@Override
	public String path() {
		// TODO Auto-generated method stub
		return "data/dictionary.txt";
	}

	@Override
	public void load() {
		// TODO Auto-generated method stub
		
		FileReaderHelper.Load(Game.getInstance().getResPath() + path());
		
		DictionaryDataItem item = null;
		while(!FileReaderHelper.IsEnd())
		{
			FileReaderHelper.ReadLine();
			item = new DictionaryDataItem();
			item.mID = FileReaderHelper.ReadInt();
			item.mData = FileReaderHelper.ReadString();
			
			this.mDataList.add(item);
		}
		
	}

	@Override
	public boolean verify() {
		// TODO Auto-generated method stub
		
		for(DictionaryDataItem i : mDataList)
		{
			System.out.println("Dic   " + i.mID + "  " + i.mData);
		}
		return true;
	}

}
