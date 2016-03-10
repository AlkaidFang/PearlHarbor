package com.alkaid.pearlharbor.dbsystem;

import com.alkaid.pearlharbor.dbsystem.DatabaseDefine.DatabaseError;
import com.alkaid.pearlharbor.dbsystem.DatabaseDefine.DatabaseFuncType;
import com.alkaid.pearlharbor.playersystem.Player;
import com.alkaid.pearlharbor.util.LifeCycle;

public interface IDatabaseHandler extends LifeCycle{
	
	// set the db server ip and port.
	public void setConfig(String dbIp, int dbPort);
	
	// perform the user db instruction
	public DatabaseError schedulerPlayerData(Player player, DatabaseFuncType funcType);
	
	// get a new avatar index, and the index is add by self
	public int getNewAvatarIndex();
}
