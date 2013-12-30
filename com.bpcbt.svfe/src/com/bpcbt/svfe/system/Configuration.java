package com.bpcbt.svfe.system;

import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;



public class Configuration {
	private	Preferences	prefs;
	private String 		configurationName;
	private String 		serverName;
	private String 		serverUsername;
	private String 		serverPassword;
	private String 		keyLocation;
	private String 		serverSrcLocation;
	private String		gzipFilesLocation;
	private boolean 	authByKey;
		
	
	public Configuration() 	{	}
	
	public boolean initFromPrefs ( Preferences prefs)
	{
		this.prefs = prefs;
		configurationName = prefs.get("configurationName", "default");
		serverName = prefs.get("serverName", "localhost");
		serverUsername = prefs.get("serverUsername", "svista");
		serverPassword = prefs.get("serverPassword", "svista");
		keyLocation = prefs.get("keyLocation", "c:\\id_dsa");
		serverSrcLocation = prefs.get("serverSrcLocation", "/home/svista/src");
		gzipFilesLocation = prefs.get("gzipFilesLocation", "C:\\");
		authByKey = prefs.getBoolean("authByKey", false);
		return true;
	}

	public void initialize(Preferences prefs, String configurationName, String serverName, String serverUsername, String serverPassword, String keyLocation, String serverSrcLocation, String gzipFilesLocation, boolean authByKey)
	{
		this.prefs = prefs;
		this.configurationName 	= configurationName;
		this.serverName 		= serverName;
		this.serverUsername 	= serverUsername;
		this.serverPassword 	= serverPassword;
		this.keyLocation 		= keyLocation;
		this.serverSrcLocation 	= serverSrcLocation;
		this.gzipFilesLocation 	= gzipFilesLocation;
		this.authByKey			= authByKey;		
		flushPrefs();
	}
	
	public String getConfigurationName() {
		return configurationName;
	}

	public void setConfigurationName(String configurationName) {
		this.configurationName = configurationName;
		flushPrefs();
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
		flushPrefs();
	}

	public String getServerUsername() {
		return serverUsername;
	}

	public void setServerUsername(String serverUsername) {
		this.serverUsername = serverUsername;
		flushPrefs();
	}

	public String getServerPassword() {
		return serverPassword;
	}

	public void setServerPassword(String serverPassword) {
		this.serverPassword = serverPassword;
		flushPrefs();
	}

	public String getKeyLocation() {
		return keyLocation;
	}

	public void setKeyLocation(String keyLocation) {
		this.keyLocation = keyLocation;
		flushPrefs();
	}

	public String getServerSrcLocation() {
		return serverSrcLocation;
	}

	public void setServerSrcLocation(String serverSrcLocation) {
		this.serverSrcLocation = serverSrcLocation;
		flushPrefs();
	}

	public String getGzipFilesLocation() {
		return gzipFilesLocation;
	}

	public void setGzipFilesLocation(String gzipFilesLocation) {
		this.gzipFilesLocation = gzipFilesLocation;
		flushPrefs();
	}

	public boolean isAuthByKey() {
		return authByKey;
	}

	public void setAuthByKey(boolean authByKey) {
		this.authByKey = authByKey;
		flushPrefs();
	}
	
	public void removePrefs()
	{
		try {
			prefs.removeNode();
		} catch (BackingStoreException e) {	e.printStackTrace();}
	}

	private void flushPrefs()
	{
		prefs.put("configurationName", configurationName);
		prefs.put("serverName", serverName);
		prefs.put("serverUsername", serverUsername);
		prefs.put("serverPassword", serverPassword);
		prefs.put("keyLocation", keyLocation);
		prefs.put("serverSrcLocation", serverSrcLocation);
		prefs.put("gzipFilesLocation", gzipFilesLocation);
		prefs.putBoolean("authByKey", authByKey);
		try{
			prefs.flush();
		} catch (BackingStoreException e){ 
			e.printStackTrace();
		}		
	}



}
