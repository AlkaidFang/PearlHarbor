package com.alkaid.pearlharbor.dataprovidersystem;

public interface IDataProvider {

	public String path();
	
	public void load();
	
	public boolean verify();
}
